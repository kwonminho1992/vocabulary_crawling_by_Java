package crawling;


import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import Exceptions.ExampleException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetOrHanjaException;


/**
 * Chinese-Korean Class
 * 
 * @author Kwon Minho
 *
 */
public class ChineseKorean extends Language {
  /**
   * Chinese-Korean class has only default constructor
   */
  public ChineseKorean() {}

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
      int size = driver.findElements(By.cssSelector("div.cont em")).size();
      for (int i = 0; i < size; i++) {
        part += (i + 1) + ". " + driver.findElements(By.cssSelector("div.cont em")).get(i).getText()
            + " / ";
      }
      if (part.equals("/ ")) {
        throw new PartException(
            "There is no part information of <" + word + "> in Naver dictionary site.");
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
      int size = driver.findElements(By.cssSelector("span.mean")).size();
      for (int i = 0; i < size; i++) {
        meaning += (i + 1) + ". "
            + driver.findElements(By.cssSelector("span.mean")).get(i).getText() + " /<br>";
      }
      if (meaning.equals("/ ")) {
        throw new MeaningException(
            "There is no meaning information of <" + word + "> in Naver dictionary site.");
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
      int size = driver.findElements(By.cssSelector("span[lang='zh_CN']")).size();
      for (int i = 0; i < size; i++) { // example 1
        String str = driver.findElements(By.cssSelector("span[lang='zh_CN']")).get(i).getText();
        if (str.contains(word) && !str.equals(word)) {
          example += " /<br>" + str.replace(word, "{{c1::" + word + "}}");
        }
      }
      if (example.equals("{{c1::" + word + "}}")) {
        throw new ExampleException(
            "There is no example information of <" + word + "> in Naver dictionary site.");
      } else {
        System.out.println(example);
      }
    } catch (Exception e) {
      throw new ExampleException("Failed to add <" + word + "> into example field");
    }
    return example;
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
      phoneticAlphabet = driver.findElements(By.cssSelector("span.pronounce")).get(0).getText();
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
    String pronunciation = "[sound:pronunciation_cn_kr_" + word + ".mp3]";
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
            + "<a href=\"https://zh.dict.naver.com/#/search?query=" + word + "\">https://"
            + "https://zh.dict.naver.com/#/search?query=" + word + "</a><br><br>";
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
    String tag = "CN-KR";
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
      driver.get("https://zh.dict.naver.com/#/search?query=" + word); // access to the site
      Thread.sleep(1000);
      driver.findElements(By.className("highlight")).get(0).click(); // get into the word page
      Thread.sleep(1000);
      driver.findElements(By.cssSelector("._listen_global_item.btn_listen.all")).get(0).click(); // click
                                                                                                 // pronunciation
      Thread.sleep(700);
      String Mp3Address =
          driver.findElements(By.cssSelector(".btn_listen_global.mp3._btn_play_single")).get(0)
              .getAttribute("data-playobj"); // get MP3 file's URL
      Tool tool = new Tool();
      tool.fileDownload(Mp3Address, "pronunciation_cn_kr_" + word + ".mp3");
      System.out.println("successfully download [pronunciation_cn_kr_" + word + ".mp3] !!");
    } catch (Exception e) {
      throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
    }
  }
}
