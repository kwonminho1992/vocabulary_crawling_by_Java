package crawling;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import Exceptions.ExampleException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetOrHanjaException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * English Class
 * 
 * @author Kwon Minho
 *
 */
public class English extends Language {
  // field
  private static final String GUARDIAN_API_TEMPLATE =
      "https://content.guardianapis.com/search?q=%s&page-size=5&order-by=newest&show-fields=bodyText&api-key=test";
  private static final int MAX_EXAMPLE_SENTENCES = 3;
  private final List<String> partForms = new ArrayList<String>(); // all part forms of the word

  /**
   * English class has only default constructor
   */
  public English() {}

  /**
   * picture of card
   * 
   * @param String word
   * @return String picture
   */
  @Override
  public String addPicture(String word) {
    String pictureAddress =
        "<a href=\"https://www.google.co.kr/search?q=" + word + "&tbm=isch\">link</a>";
    return pictureAddress;
  }

  /**
   * part of card
   * 
   * @param String word, ChromeDriver driver
   * @return String part
   * @throws PartException
   */
  @Override
  public String addPart(String word, ChromeDriver driver) throws PartException {
    StringBuilder partBuilder = new StringBuilder("/ ");
    try {
      driver.get("https://dictionary.cambridge.org/dictionary/english/" + word); // access cambridge
                                                                                 // dictionary
      Thread.sleep(1000);
      List<WebElement> partElements = driver.findElements(By.cssSelector(".pos.dpos"));
      for (WebElement element : partElements) {
        String text = element.getText().trim();
        if (!text.isEmpty()) {
          partBuilder.append(text).append(" / ");
        }
      }
      if (partBuilder.length() == 2) {
        throw new PartException(
            "There is no part information of <" + word + "> in Cambridge dictionary site.");
      } else {
        System.out.println(partBuilder.toString());
      }
    } catch (Exception e) {
      throw new PartException("Failed to add <" + word + "> into part field");
    }
    return partBuilder.toString();
  }

  /**
   * meaning of card
   * 
   * @param String word, ChromeDriver driver
   * @return String meaning
   * @throws MeaningException
   */
  @Override
  public String addMeaning(String word, ChromeDriver driver) throws MeaningException {
    StringBuilder meaningBuilder = new StringBuilder("/ ");
    try {
      List<WebElement> meaningElements = driver.findElements(By.cssSelector(".def.ddef_d.db"));
      for (WebElement element : meaningElements) {
        String text = element.getText().trim();
        if (!text.isEmpty()) {
          meaningBuilder.append(text).append(" /<br>");
        }
      }
      if (meaningBuilder.length() == 2) {
        throw new MeaningException(
            "There is no meaning information of <" + word + "> in Cambridge dictionary site.");
      } else {
        System.out.println(meaningBuilder.toString());
      }
    } catch (Exception e) {
      throw new MeaningException("Failed to add <" + word + "> into meaning field");
    }
    return meaningBuilder.toString();
  }

  /**
   * example of card
   * 
   * @param String word, ChromeDriver driver
   * @return String example
   * @throws ExampleException
   */
  @Override
  public String addExample(String word, ChromeDriver driver) throws ExampleException {
    StringBuilder exampleBuilder = new StringBuilder("{{c1::").append(word).append("}}");
    try {
      Pattern pattern = createSearchPattern(word);
      ArrayList<String> sentences = fetchExampleSentences(word, pattern);
      if (sentences.isEmpty()) {
        throw new ExampleException(
            "There is no example information of <" + word + "> in recent news articles.");
      }
      for (String sentence : sentences) {
        exampleBuilder.append(" /<br>").append(sentence);
      }
      System.out.println(exampleBuilder.toString());
    } catch (IOException | RuntimeException e) {
      throw new ExampleException("Failed to add <" + word + "> into example field");
    }
    return exampleBuilder.toString();
  }

  private Pattern createSearchPattern(String word) {
    Set<String> forms = new LinkedHashSet<String>(this.partForms);
    forms.add(word);
    StringJoiner builder = new StringJoiner("|");
    for (String form : forms) {
      if (form == null) {
        continue;
      }
      String trimmed = form.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      builder.add(Pattern.quote(trimmed));
    }
    if (builder.length() == 0) {
      builder.add(Pattern.quote(word));
    }
    return Pattern.compile("\\b(" + builder.toString() + ")\\b", Pattern.CASE_INSENSITIVE);
  }

  private ArrayList<String> fetchExampleSentences(String word, Pattern pattern)
      throws IOException {
    ArrayList<String> sentences = new ArrayList<String>();
    HttpURLConnection connection = null;
    try {
      String encodedWord = URLEncoder.encode(word, "UTF-8");
      String requestUrl = String.format(GUARDIAN_API_TEMPLATE, encodedWord);
      URL url = new URL(requestUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      connection.setRequestProperty("Accept", "application/json");
      try (InputStreamReader reader =
          new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        JsonObject response = root.getAsJsonObject("response");
        if (response == null || !response.has("results")) {
          return sentences;
        }
        JsonArray results = response.getAsJsonArray("results");
        for (int i = 0; i < results.size() && sentences.size() < MAX_EXAMPLE_SENTENCES; i++) {
          JsonObject resultObj = results.get(i).getAsJsonObject();
          JsonObject fields = resultObj.getAsJsonObject("fields");
          if (fields == null || !fields.has("bodyText")) {
            continue;
          }
          String bodyText = fields.get("bodyText").getAsString();
          String[] splitted = bodyText.split("(?<=[.!?])\\s+");
          for (int j = 0; j < splitted.length && sentences.size() < MAX_EXAMPLE_SENTENCES; j++) {
            String normalized = splitted[j].replaceAll("\\s+", " ").trim();
            if (normalized.isEmpty()) {
              continue;
            }
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
              sentences.add(toClozeSentence(normalized, pattern));
              break;
            }
          }
        }
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return sentences;
  }

  private String toClozeSentence(String sentence, Pattern pattern) {
    Matcher matcher = pattern.matcher(sentence);
    if (matcher.find()) {
      StringBuffer builder = new StringBuffer();
      matcher.appendReplacement(builder, "{{c1::" + matcher.group() + "}}");
      matcher.appendTail(builder);
      return builder.toString().trim();
    }
    return sentence.trim();
  }

  /**
   * phonetic_alphabet of card
   * 
   * @param String word, ChromeDriver driver
   * @return String phoneticAlphabet
   * @throws PhoneticAlphabetOrHanjaException
   */
  @Override
  public String addPhoneticAlphabetOrHanja(String word, ChromeDriver driver)
      throws PhoneticAlphabetOrHanjaException {
    try {
      List<WebElement> phoneticElements = driver.findElements(By.cssSelector(".pron.dpron"));
      if (phoneticElements.isEmpty()) {
        throw new PhoneticAlphabetOrHanjaException(
            "There is no phonetic alphabet information of <" + word + "> in Cambridge dictionary site.");
      }
      String text = phoneticElements.get(0).getText().trim();
      if (text.isEmpty()) {
        throw new PhoneticAlphabetOrHanjaException(
            "There is no phonetic alphabet information of <" + word + "> in Cambridge dictionary site.");
      }
      String phoneticAlphabet = "/ " + text;
      System.out.println(phoneticAlphabet);
      return phoneticAlphabet;
    } catch (PhoneticAlphabetOrHanjaException e) {
      throw e;
    } catch (Exception e) {
      throw new PhoneticAlphabetOrHanjaException(
          "Failed to add <" + word + "> into phonetic_alphabet field");
    }
  }

  /**
   * pronunciation of card
   * 
   * @param String word
   * @return String pronunciation
   */
  @Override
  public String addPronunciation(String word) {
    String pronunciation = "[sound:pronunciation_en_" + word + ".mp3]";
    return pronunciation;
  }

  /**
   * link of card
   * 
   * @param String word
   * @return String link
   */
  @Override
  public String addLink(String word) {
    String link =
        "<a href=\"https://www.google.co.kr/search?q=" + word + "&tbm=isch\">image link</a><br><br>"
            + "<a href=\"https://dictionary.cambridge.org/dictionary/english/" + word + "\">"
            + "https://dictionary.cambridge.org/dictionary/english/" + word + "</a><br><br><a "
            + "href=\"https://en.dict.naver.com/#/search?range=all&query=" + word + "\">https://"
            + "en.dict.naver.com/#/search?range=all&query=" + word + "</a><br><br>";
    return link;
  }

  /**
   * tag of card
   * 
   * @param String word
   * @return String tag
   */
  @Override
  public String addTag(String word) {
    String tag = "EN";
    return tag;
  }

  /**
   * download mp3 files of the word into resource directory
   * 
   * @param String word, ChromeDriver driver
   * @throws MP3DownloadException
   */
  @Override
  public void downloadMp3File(String word, ChromeDriver driver) throws MP3DownloadException {
    try {
      // access to Naver dictionary
      driver.get("https://en.dict.naver.com/#/search?query=" + word); // access to the site
      Thread.sleep(1000);
      List<WebElement> highlights = driver.findElements(By.className("highlight"));
      if (highlights.isEmpty()) {
        throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
      }
      highlights.get(0).click(); // get into the word page
      Thread.sleep(1000);
      List<WebElement> accentButtons =
          driver.findElements(By.cssSelector(".listen_global_item.us._listen_global_item"));
      if (accentButtons.isEmpty()) {
        throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
      }
      accentButtons.get(0).click(); // click American Accent
      Thread.sleep(700);
      List<WebElement> mp3Buttons =
          driver.findElements(By.cssSelector(".btn_listen_global.mp3._btn_play_single"));
      if (mp3Buttons.isEmpty()) {
        throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
      }
      String Mp3Address = mp3Buttons.get(0).getAttribute("data-playobj"); // get MP3 file's URL
      Tool tool = new Tool();
      tool.fileDownload(Mp3Address, "pronunciation_en_" + word + ".mp3");
      System.out.println("successfully download [pronunciation_en_" + word + ".mp3] !!");
    } catch (MP3DownloadException e) {
      throw e;
    } catch (Exception e) {
      throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
    }
  }

  /**
   * This method is for English class only. Other languages don't use this method get all part forms
   * of the word from Naver dictionary Example : listen => listens, listened, listening This method
   * doesn't get url of Naver dictionary, so You'd better use method after DownloadMP3file() method.
   * DownloadMP3file() method downloads MP3 file from Naver dictionary.
   * 
   * @param String word, ChromeDriver driver
   */
  @Override
  public void setPartForms(String word, ChromeDriver driver) {
    LinkedHashSet<String> forms = new LinkedHashSet<String>();
    forms.add(word);
    List<WebElement> partElements = driver.findElements(By.cssSelector("span.word_inner.is-bold"));
    for (WebElement element : partElements) {
      String text = element.getText().trim();
      if (!text.isEmpty()) {
        forms.add(text);
      }
    }
    this.partForms.clear();
    this.partForms.addAll(forms);
  }
}
