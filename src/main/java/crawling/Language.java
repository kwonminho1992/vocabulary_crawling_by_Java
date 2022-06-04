package crawling;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.openqa.selenium.chrome.ChromeDriver;

import com.opencsv.CSVWriter;

import Exceptions.ExampleException;
import Exceptions.HanjaException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetException;
/**
 * This class provides frame toward each language's class 
 * @author Kwon Minho
 *
 */
public abstract class Language{
		//field
		private Tool tool = new Tool();	
		private String resourceDir = tool.getResourceDir(); // resource directory's address 
		
		/**
		 * Frame class has only default constructor
		 */
		public Language () {
		}
		
		/**
		 * Read csv file and get all words and input to the nested arraylist. And then return it.
		 * Input language name by lowercase into parameter (ex. english, vietnamese) 
		 * Datatype : ArrayList<ArrayList<String>> 
		 * @param String language 
		 * @return ArrayList<ArrayList<String>> words
		 */
		public ArrayList<ArrayList<String>> sortWords(String language) {
			ArrayList<ArrayList<String>> words = new ArrayList<ArrayList<String>>(); // nested list stored information of each word
			BufferedReader readCsv = null;
			try {
				//read csv file
				String csvFile = (resourceDir + language + "_input.csv"); // create object
//				BufferedReader readCsv = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), Charset.forName("UTF-8"))); // create object
				readCsv = new BufferedReader(new FileReader((csvFile))); // create object
				String line;
				readCsv.readLine(); // skip header
				for (int i = 0; (line = readCsv.readLine()) != null; i++){ // get words from the csv file and store into arraylist
					ArrayList<String> contents = new ArrayList<String>(); // arraylist to store information of the word
					contents.add(line);
					words.add(contents);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					readCsv.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return words;
		}
		
		/**
		 * picture of card
		 * @param String word
		 * @return String picture
		 */
		abstract String addPicture(String word);
		/**
		 * part of card
		 * @param String word, ChromeDriver driver
		 * @return String part
		 * @throws PartException
		 */
		abstract String addPart(String word, ChromeDriver driver) throws PartException;
		/**
		 * meaning of card
		 * @param String word, ChromeDriver driver
		 * @return String meaning
		 * @throws MeaningException
		 */
		abstract String addMeaning(String word, ChromeDriver driver) throws MeaningException;
		/**
		 * example of card
		 * @param String word, ChromeDriver driver
		 * @return String example
		 * @throws ExampleException
		 */
		abstract String addExample(String word, ChromeDriver driver) throws ExampleException;
		/**
		 * pronunciation of card
		 * @param String word
		 * @return String pronunciation
		 */
		
		/**
		 * This method is for English class only. Other languages don't use this method
		 * phonetic_alphabet of card
		 * @param String word, ChromeDriver driver
		 * @return String phoneticAlphabet
		 * @throws PhoneticAlphabetException
		 */
		public String addPhoneticAlphabet(String word, ChromeDriver driver) throws PhoneticAlphabetException{
			return word;
		}
		/**
		 * This method is for Vietnamese class only. Other languages don't use this method
		 * hanja of card (Vietnamese)
		 * @param String word, ChromeDriver driver
		 * @return String hanja
		 * @throws HanjaException
		 */
		public String addHanja(String word, ChromeDriver driver) throws HanjaException {
			return word;
		}
		
		abstract String addPronunciation(String word);
		/**
		 * link of card
		 * @param String word
		 * @return String link
		 */
		abstract String addLink(String word);
		/**
		 * tag of card
		 * @param String word
		 * @return String tag
		 */
		abstract String addTag(String word);
		/**
		 * download mp3 files of the word into resource directory
		 * @param String word, ChromeDriver driver
		 * @throws MP3DownloadException
		 */
		abstract void downloadMp3File(String word, ChromeDriver driver) throws MP3DownloadException;
		
		/**
		 * write csv file with stored words' information (part, meaning, example etc..)
		 * Input language name by lowercase into parameter (ex. english, vietnamese) 
		 * @param ArrayList<String> infos, String language
		 */
		public void fillCsvFile (ArrayList<String> infos, String language) {
			CSVWriter writeCsv = null;
			try {
			//create csv file
			writeCsv = new CSVWriter(new FileWriter(resourceDir + language + "_ouput.csv", true));	
			Charset.forName("utf-8"); //encoding

			int size = infos.size();
			String[] contents = new String[size];
			for (int j = 0; j < size; j++) {
				contents[j] = infos.get(j);
			}

			writeCsv.writeNext(contents);

			System.out.println(infos.get(0) + " is added into " + language + "_output.csv");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writeCsv.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * quit ChromeDriver
		 * @param ChromeDriver driver
		 */
		public void quitChromeDriver(ChromeDriver driver) {
			driver.quit();
		}
		
		/**
		 * This method is for English class only. Other languages don't use this method 
		 * get all part forms of the word from Naver dictionary
		 * Example : listen => listens, listened, listening
		 * This method doesn't get url of Naver dictionary, so You'd better use method after DownloadMP3file() method.
		 * DownloadMP3file() method downloads MP3 file from Naver dictionary.
		 * @param String word, ChromeDriver driver
		 */
		public void setPartForms(String word, ChromeDriver driver) {			
		}
		
		
}