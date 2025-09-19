package crawling;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.chrome.ChromeDriver;
import com.opencsv.CSVWriter;
import Exceptions.ExampleException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetOrHanjaException;

/**
 * This class provides frame toward each language's class
 * 
 * @author Kwon Minho
 * 
 */
public abstract class Language {
  // field
  private Tool tool = new Tool();
  private String resourceDir = tool.getResourceDir(); // resource directory's address

  /**
   * Frame class has only default constructor
   */
  public Language() {}

  /**
   * Read csv file and get all words and input to the nested arraylist. And then return it. Input
   * language name by lowercase into parameter (ex. english, vietnamese) Datatype :
   * ArrayList<ArrayList<String>>
   * 
   * @param String language
   * @return ArrayList<ArrayList<String>> words
   */
  public ArrayList<ArrayList<String>> sortWords(String language) {
    ArrayList<ArrayList<String>> words = new ArrayList<ArrayList<String>>(); // nested list stored
                                                                             // information of each
                                                                             // word
    Path csvPath = Paths.get(resourceDir + language + "_input.csv");
    try (BufferedReader readCsv = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
      String line;
      readCsv.readLine(); // skip header
      while ((line = readCsv.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }
        ArrayList<String> contents = new ArrayList<String>();
        contents.add(line);
        words.add(contents);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  /**
   * picture of card
   * 
   * @param String word
   * @return String picture
   */
  abstract String addPicture(String word);

  /**
   * part of card
   * 
   * @param String word, ChromeDriver driver
   * @return String part
   * @throws PartException
   */
  abstract String addPart(String word, ChromeDriver driver) throws PartException;

  /**
   * meaning of card
   * 
   * @param String word, ChromeDriver driver
   * @return String meaning
   * @throws MeaningException
   */
  abstract String addMeaning(String word, ChromeDriver driver) throws MeaningException;

  /**
   * example of card
   * 
   * @param String word, ChromeDriver driver
   * @return String example
   * @throws ExampleException
   */
  abstract String addExample(String word, ChromeDriver driver) throws ExampleException;


  /**
   * Phonetic alphabet or Hanja for East Asian languages
   * 
   * @param String word, ChromeDriver driver
   * @return String phoneticAlphabetOrHanja
   * @throws PhoneticAlphabetOrHanjaException
   */
  abstract String addPhoneticAlphabetOrHanja(String word, ChromeDriver driver)
      throws PhoneticAlphabetOrHanjaException;

  /**
   * pronunciation of card
   * 
   * @param String word
   * @return String pronunciation
   */

  abstract String addPronunciation(String word);

  /**
   * link of card
   * 
   * @param String word
   * @return String link
   */
  abstract String addLink(String word);

  /**
   * tag of card
   * 
   * @param String word
   * @return String tag
   */
  abstract String addTag(String word);

  /**
   * download mp3 files of the word into resource directory
   * 
   * @param String word, ChromeDriver driver
   * @throws MP3DownloadException
   */
  abstract void downloadMp3File(String word, ChromeDriver driver) throws MP3DownloadException;

  /**
   * write csv file with stored words' information (part, meaning, example etc..) Input language
   * name by lowercase into parameter (ex. english, vietnamese)
   * 
   * @param ArrayList<String> infos, String language
   */
  public void fillCsvFile(ArrayList<String> infos, String language) {
    String outputFile = resourceDir + language + "_output.csv";
    try (CSVWriter writeCsv = new CSVWriter(new FileWriter(outputFile, true))) {
      writeCsv.writeNext(infos.toArray(new String[0]));
      System.out.println(infos.get(0) + " is added into " + language + "_output.csv");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * quit ChromeDriver
   * 
   * @param ChromeDriver driver
   */
  public void quitChromeDriver(ChromeDriver driver) {
    driver.quit();
  }

  /**
   * This method is for English class only. Other languages don't use this method get all part forms
   * of the word from Naver dictionary Example : listen => listens, listened, listening This method
   * doesn't get url of Naver dictionary, so You'd better use method after DownloadMP3file() method.
   * DownloadMP3file() method downloads MP3 file from Naver dictionary.
   * 
   * @param String word, ChromeDriver driver
   */
  public void setPartForms(String word, ChromeDriver driver) {}


}
