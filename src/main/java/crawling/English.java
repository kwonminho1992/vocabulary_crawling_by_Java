package crawling;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
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
  private ArrayList<String> partForms = new ArrayList<String>(); // all part forms of the word

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
    String part = "/ ";
    try {
      driver.get("https://dictionary.cambridge.org/dictionary/english/" + word); // access cambridge
                                                                                 // dictionary
      Thread.sleep(1000);
      int size = driver.findElements(By.cssSelector(".pos.dpos")).size();
      for (int i = 0; i < size; i++) {
        part += driver.findElements(By.cssSelector(".pos.dpos")).get(i).getText() + " / ";
      }
      if (part.equals("/ ")) {
        throw new PartException(
            "There is no part information of <" + word + "> in Cambridge dictionary site.");
      } else {
        System.out.println(part);
      }
    } catch (Exception e) {
      throw new PartException("Failed to add <" + word + "> into part field");
    }
    return part;
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
    String meaning = "/ ";
    try {
      int size = driver.findElements(By.cssSelector(".def.ddef_d.db")).size();
      for (int i = 0; i < size; i++) {
        meaning +=
            driver.findElements(By.cssSelector(".def.ddef_d.db")).get(i).getText() + " /<br>";
      }
      if (meaning.equals("/ ")) {
        throw new MeaningException(
            "There is no meaning information of <" + word + "> in Cambridge dictionary site.");
      } else {
        System.out.println(meaning);
      }
    } catch (Exception e) {
      throw new MeaningException("Failed to add <" + word + "> into meaning field");
    }
    return meaning;
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
    String example = "{{c1::" + word + "}}";
    try {
      Pattern pattern = createSearchPattern(word);
      ArrayList<String> sentences = fetchExampleSentences(word, pattern);
      if (sentences.isEmpty()) {
        throw new ExampleException(
            "There is no example information of <" + word + "> in recent news articles.");
      }
      for (String sentence : sentences) {
        example += " /<br>" + sentence;
      }
      System.out.println(example);
    } catch (IOException | RuntimeException e) {
      throw new ExampleException("Failed to add <" + word + "> into example field");
    }
    return example;
  }

  private Pattern createSearchPattern(String word) {
    ArrayList<String> forms = new ArrayList<String>(this.partForms);
    if (!forms.contains(word)) {
      forms.add(word);
    }
    StringBuilder builder = new StringBuilder();
    for (String form : forms) {
      if (form == null) {
        continue;
      }
      String trimmed = form.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      if (builder.length() > 0) {
        builder.append("|");
      }
      builder.append(Pattern.quote(trimmed));
    }
    if (builder.length() == 0) {
      builder.append(Pattern.quote(word));
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
    String phoneticAlphabet = "/ ";
    try {
      phoneticAlphabet += driver.findElements(By.cssSelector(".pron.dpron")).get(0).getText();
      if (phoneticAlphabet.equals("/ ")) {
        throw new PhoneticAlphabetOrHanjaException("There is no phonetic alphabet information of <"
            + word + "> in Cambridge dictionary site.");
      } else {
        System.out.println(phoneticAlphabet);
      }
    } catch (Exception e) {
      throw new PhoneticAlphabetOrHanjaException(
          "Failed to add <" + word + "> into phonetic_alphabet field");
    }
    return phoneticAlphabet;
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
      driver.findElements(By.className("highlight")).get(0).click(); // get into the word page
      Thread.sleep(1000);
      driver.findElements(By.cssSelector(".listen_global_item.us._listen_global_item")).get(0)
          .click(); // click American Accent
      Thread.sleep(700);
      String Mp3Address =
          driver.findElements(By.cssSelector(".btn_listen_global.mp3._btn_play_single")).get(0)
              .getAttribute("data-playobj"); // get MP3 file's URL
      Tool tool = new Tool();
      tool.fileDownload(Mp3Address, "pronunciation_en_" + word + ".mp3");
      System.out.println("successfully download [pronunciation_en_" + word + ".mp3] !!");
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
    int size = driver.findElements(By.cssSelector("span.word_inner.is-bold")).size();
    ArrayList<String> partForms = new ArrayList<String>();
    String[] partFormsForTest = new String[size + 1];
    partFormsForTest[0] = word; // add basic form into array
    for (int i = 0; i < size; i++) { // add all part forms into array (before duplicate test)
      String form = driver.findElements(By.cssSelector("span.word_inner.is-bold")).get(i).getText();
      partFormsForTest[i + 1] = form;

    }
    for (String part : partFormsForTest) { // duplicate test
      if (!partForms.contains(part)) {
        partForms.add(part);
      }
    }
    this.partForms = partForms;
  }
}
