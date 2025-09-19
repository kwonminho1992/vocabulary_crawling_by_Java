package crawling;

import java.util.ArrayList;
import java.util.Locale;
import org.openqa.selenium.chrome.ChromeDriver;
import Exceptions.ExampleException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetOrHanjaException;

/**
 * Executive class
 * 
 * @author kwon minho
 *
 */
public class Main {

  public static void main(String[] args) throws InterruptedException {
    String choice = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "english";
    runCrawling(choice);
  }

  /**
   * Proceed crawling If you want to run English, input "english" into parameter. (in case
   * Vietnamese, input "vietnamese")
   * 
   * @param String choice
   * @throws InterruptedException
   */
  public static void runCrawling(String choice) throws InterruptedException {
    String normalizedChoice = choice.toLowerCase(Locale.ROOT);
    Language language = selectLanguage(normalizedChoice);

    Tool tool = new Tool();
    ChromeDriver driver = tool.getDriver(); // create driver object
    tool.openTab(driver); // open chrome tab
    ArrayList<ArrayList<String>> words =
        language.sortWords(normalizedChoice); // arraylist for the words'
                                                                     // information (part, meaning
                                                                     // etc...)
    int size = words.size();
    for (int i = 0; i < size; i++) {
      ArrayList<String> wordInfo = words.get(i);
      String word = wordInfo.get(0);
      // get information(meaning, example etc..) from web
      try { // download mp3 file
        language.downloadMp3File(word, driver);
      } catch (MP3DownloadException e) {
        e.printStackTrace();
      }
      language.setPartForms(word, driver);
      String picture = language.addPicture(word); // add picture field
      wordInfo.add(picture);
      try { // add part field
        String part = language.addPart(word, driver);
        wordInfo.add(part);
      } catch (PartException e) {
        wordInfo.add(".");
        e.printStackTrace();
      }
      try { // add meaning field
        String meaning = language.addMeaning(word, driver);
        wordInfo.add(meaning);
      } catch (MeaningException e) {
        wordInfo.add(".");
        e.printStackTrace();
      }
      try { // add example field
        String example = language.addExample(word, driver);
        wordInfo.add(example);
      } catch (ExampleException e) {
        wordInfo.add("{{c1::" + word + "}}");
        e.printStackTrace();
      }
      try { // add phonetic_alphabet or hanja field
        String phoneticAlphabet = language.addPhoneticAlphabetOrHanja(word, driver);
        wordInfo.add(phoneticAlphabet);
      } catch (PhoneticAlphabetOrHanjaException e) {
        wordInfo.add(".");
        e.printStackTrace();
      }
      String pronunciation = language.addPronunciation(word); // add pronunciation field
      wordInfo.add(pronunciation);
      String link = language.addLink(word); // add link field
      wordInfo.add(link);
      String tag = language.addTag(word); // add tag to the card
      wordInfo.add(tag);
      // write information to csv file
      language.fillCsvFile(wordInfo, normalizedChoice);
      System.out.println("Current progress : " + (i + 1) + " / " + words.size());
    }
    language.quitChromeDriver(driver);
    System.out.println("Finish program");
  }

  private static Language selectLanguage(String choice) {
    if ("english".equals(choice)) {
      return new English();
    }
    if ("vietnamese".equals(choice)) {
      return new Vietnamese();
    }
    throw new IllegalArgumentException("Unsupported language: " + choice);
  }
}
