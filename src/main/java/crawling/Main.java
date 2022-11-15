package crawling;

import java.util.ArrayList;
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
    // choose language to run crawling program
    String[] choice = new String[3];
    choice[0] = "english";
    choice[1] = "vietnamese";
    choice[2] = "korean";


    runCrawling(choice[0]); // run crawling program (get language as argument)
  }

  /**
   * Proceed crawling If you want to run English, input "english" into parameter. (in case
   * Vietnamese, input "vietnamese")
   * 
   * @param String choice
   * @throws InterruptedException
   */
  public static void runCrawling(String choice) throws InterruptedException {
    Language language; // declare frame object (superclass of each languages such as English,
                       // Vietnamese...)
    if (choice.equals("english")) { // if user choose english, frame object will be English type and
                                    // run English crawling
      language = new English();
    } else if (choice.contentEquals("vietnamese")) { // if user choose vietnamese, frame object will
                                                     // be Vietnamese type and run Vietnamese
      // crawling
      language = new Vietnamese();
    } else {
      language = new Korean();
    }

    Tool tool = new Tool();
    ChromeDriver driver = tool.getDriver(); // create driver object
    tool.openTab(driver); // open chrome tab
    ArrayList<ArrayList<String>> words = language.sortWords(choice); // arraylist for the words'
                                                                     // information (part, meaning
                                                                     // etc...)
    int size = words.size();
    for (int i = 0; i < size; i++) {
      // get information(meaning, example etc..) from web
      try { // download mp3 file
        language.downloadMp3File(words.get(i).get(0), driver);
      } catch (MP3DownloadException e) {
        e.printStackTrace();
      }
      if (choice.equals("english")) { // setPartForms() method is only for English type (Other
                                      // languages don't use)
        language.setPartForms(words.get(i).get(0), driver);
      }
      String picture = language.addPicture(words.get(i).get(0)); // add picture field
      words.get(i).add(picture);
      try { // add part field
        String part = language.addPart(words.get(i).get(0), driver);
        words.get(i).add(part);
      } catch (PartException e) {
        words.get(i).add(".");
        e.printStackTrace();
      }
      try { // add meaning field
        String meaning = language.addMeaning(words.get(i).get(0), driver);
        words.get(i).add(meaning);
      } catch (MeaningException e) {
        words.get(i).add(".");
        e.printStackTrace();
      }
      try { // add example field
        String example = language.addExample(words.get(i).get(0), driver);
        words.get(i).add(example);
      } catch (ExampleException e) {
        words.get(i).add("{{c1::" + words.get(i).get(0) + "}}");
        e.printStackTrace();
      }
      try { // add phonetic_alphabet or hanja field
        String phoneticAlphabet = language.addPhoneticAlphabetOrHanja(words.get(i).get(0), driver);
        words.get(i).add(phoneticAlphabet);
      } catch (PhoneticAlphabetOrHanjaException e) {
        words.get(i).add(".");
        e.printStackTrace();
      }
      String pronunciation = language.addPronunciation(words.get(i).get(0)); // add pronunciation //
                                                                             // field
      words.get(i).add(pronunciation);
      String link = language.addLink(words.get(i).get(0)); // add link field
      words.get(i).add(link);
      String tag = language.addTag(words.get(i).get(0)); // add tag to the card
      words.get(i).add(tag);
      // write information to csv file
      language.fillCsvFile(words.get(i), choice);
      System.out.println("Current progress : " + (i + 1) + " / " + words.size());
    }
    language.quitChromeDriver(driver);
    System.out.println("Finish program");
  }
}
