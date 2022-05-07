package crawling;


import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import Exceptions.ExampleException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetException;


/**
 * English Class
 * @author Kwon Minho
 *
 */
public class English extends Frame{
	//field
	private Tool tool = new Tool();
	private ArrayList<String> partForms = new ArrayList<String>(); // all part forms of the word

	/**
	 * English class has only default constructor
	 */
	public English () {
	}
	
	/**
	 * picture of card
	 * @param String word
	 * @return String picture
	 */
	@Override
	public String addPicture(String word) {
		String pictureAddress = "<a href=\"https://www.google.co.kr/search?q=" + word + "&tbm=isch\">link</a>";
		return pictureAddress;
	}
	/**
	 * part of card
	 * @param String word, ChromeDriver driver
	 * @return String part
	 * @throws PartException
	 */
	@Override
	public String addPart(String word, ChromeDriver driver) throws PartException{
		String part = "/ ";
		try {
			driver.get("https://dictionary.cambridge.org/dictionary/english/" + word); //access cambridge dictionary
			Thread.sleep(1000);
			int size = driver.findElements(By.cssSelector(".pos.dpos")).size();
			for (int i = 0; i < size; i++) {
				part += driver.findElements(By.cssSelector(".pos.dpos")).get(i).getText() + " / ";
			}
			if (part.equals("/ ")) {
				throw new PartException("There is no part information of <" + word + "> in Cambridge dictionary site.");
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
	 * @param String word, ChromeDriver driver
	 * @return String meaning
	 * @throws MeaningException
	 */
	@Override
	public String addMeaning(String word, ChromeDriver driver) throws MeaningException{
		String meaning = "/ ";
		try {
			int size = driver.findElements(By.cssSelector(".def.ddef_d.db")).size();
			for (int i = 0; i < size; i++) {
				meaning += driver.findElements(By.cssSelector(".def.ddef_d.db")).get(i).getText() + " /<br>";
			}
			if (meaning.equals("/ ")) {
				throw new MeaningException("There is no meaning information of <" + word + "> in Cambridge dictionary site.");
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
	 * @param String word, ChromeDriver driver
	 * @return String example
	 * @throws ExampleException
	 */
	@Override
	public String addExample(String word, ChromeDriver driver) throws ExampleException{
		String example = "{{c1::" + word + "}}";
		try {
			int size = driver.findElements(By.cssSelector(".eg.deg")).size();
			for (int i = 0; i < size; i++) { //example 1
				String str = driver.findElements(By.cssSelector(".eg.deg")).get(i).getText();
				for (int j = this.partForms.size() - 1; j >= 0; j--) { // convert example into cloze type
					if (str.contains(this.partForms.get(j))) {
						example += " /<br>" + str.replace(this.partForms.get(j), "{{c1::" + this.partForms.get(j) + "}}");
						break;
					}
				}
			}
			size = driver.findElements(By.cssSelector("span.deg")).size();
			for (int i = 0; i < size; i++) { //example 2
				String str = driver.findElements(By.cssSelector("span.deg")).get(i).getText();
				for (int j = this.partForms.size() - 1; j >= 0; j--) { // convert example into cloze type
					if (str.contains(this.partForms.get(j))) {
						example += " /<br>" + str.replace(this.partForms.get(j), "{{c1::" + this.partForms.get(j) + "}}");
						break;
					}
				}
			}
			if (example.equals("{{c1::" + word + "}}")) {
				throw new ExampleException("There is no example information of <" + word + "> in Cambridge dictionary site.");
			} else {
				System.out.println(example);
			}
		} catch (Exception e) {
			throw new ExampleException("Failed to add <" + word + "> into example field");
		}
		return example;
	}
	/**
	 * This method is for English class only. Other languages don't use this method
	 * phonetic_alphabet of card
	 * @param String word, ChromeDriver driver
	 * @return String phoneticAlphabet
	 * @throws PhoneticAlphabetException
	 */
	@Override
	public String addPhoneticAlphabet(String word, ChromeDriver driver) throws PhoneticAlphabetException{
		String phoneticAlphabet = "/ ";
		try {
			phoneticAlphabet += driver.findElements(By.cssSelector(".pron.dpron")).get(0).getText();	
			if (phoneticAlphabet.equals("/ ")) {
				throw new PhoneticAlphabetException("There is no phonetic alphabet information of <" + word + "> in Cambridge dictionary site.");
			} else {
				System.out.println(phoneticAlphabet);
			}
		} catch (Exception e) {
			throw new PhoneticAlphabetException("Failed to add <" + word + "> into phonetic_alphabet field");
		}
		return phoneticAlphabet;
	}
	/**
	 * pronunciation of card
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
	 * @param String word
	 * @return String link
	 */
	@Override
	public String addLink(String word) {
		String link = "<a href=\"https://dictionary.cambridge.org/dictionary/english/" + word + "\">"
				+ "https://dictionary.cambridge.org/dictionary/english/" + word + "</a><br><br><a "
				+ "href=\"https://en.dict.naver.com/#/search?range=all&query=" + word + "\">https://"
				+ "en.dict.naver.com/#/search?range=all&query=" + word + "</a><br><br>";
		return link;
	}
	/**
	 * tag of card
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
	 * @param String word, ChromeDriver driver
	 * @throws MP3DownloadException
	 */
	@Override
	public void downloadMp3File(String word, ChromeDriver driver) throws MP3DownloadException{
		try {
			//access to Naver dictionary 
			driver.get("https://en.dict.naver.com/#/search?query=" + word); //access to the site
			Thread.sleep(1000);
			driver.findElements(By.className("highlight")).get(0).click(); // get into the word page
			Thread.sleep(1000);
			driver.findElements(By.cssSelector(".listen_global_item.us._listen_global_item")).get(0).click(); // click American Accent
			Thread.sleep(700);
			String Mp3Address = driver.findElements(By.cssSelector(".btn_listen_global.mp3._btn_play_single")).get(0).getAttribute("data-playobj"); //get MP3 file's URL		
			tool.fileDownload(Mp3Address, "pronunciation_en_" + word + ".mp3");
			System.out.println("successfully download [pronunciation_en_" + word + ".mp3] !!");
		} catch (Exception e) {
			throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
		}	
	}
	
	/**
	 * This method is for English class only. Other languages don't use this method
	 * get all part forms of the word from Naver dictionary
	 * Example : listen => listens, listened, listening
	 * This method doesn't get url of Naver dictionary, so You'd better use method after DownloadMP3file() method.
	 * DownloadMP3file() method downloads MP3 file from Naver dictionary.
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
		for (String part : partFormsForTest) { //duplicate test 
			if (!partForms.contains(part)) {
				partForms.add(part);
			}
		}
		this.partForms = partForms;
	}
}
