package crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import Exceptions.ExampleException;
import Exceptions.HanjaException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetException;


/**
 * Vietnamese Class
 * @author Kwon Minho
 *
 */
public class Vietnamese extends Language {
	/**
	 * Vietnamese class has only default constructor
	 */
	public Vietnamese () {
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
			driver.get("http://tratu.coviet.vn/hoc-tieng-anh/tu-dien/lac-viet/V-V/" + word + ".html"); //access dictionary
			Thread.sleep(1000);
			int size = driver.findElements(By.cssSelector("div.ub")).size();
			for (int i = 0; i < size; i++) {
				part += driver.findElements(By.cssSelector("div.ub")).get(i).getText() + " / ";
			}
			if (part.equals("/ ")) {
				throw new PartException("There is no part information of <" + word + "> in dictionary site.");
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
			int size = driver.findElements(By.cssSelector("div.m")).size();
			for (int i = 0; i < size; i++) {
				meaning += driver.findElements(By.cssSelector("div.m")).get(i).getText() + " /<br>";
			}
			if (meaning.equals("/ ")) {
				throw new MeaningException("There is no meaning information of <" + word + "> in dictionary site.");
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
			int size = driver.findElements(By.cssSelector("div.e")).size();
			for (int i = 0; i < size; i++) {
				String str = driver.findElements(By.cssSelector("div.e")).get(i).getText();
				if (str.contains(word)) {
					example += " /<br>" + str.replace(word, "{{c1::" + word + "}}");
				}
			}
			if (example.equals("{{c1::" + word + "}}")) {
				throw new ExampleException("There is no example information of <" + word + "> in dictionary site.");
			} else {
				System.out.println(example);
			}
		} catch (Exception e) {
			throw new ExampleException("Failed to add <" + word + "> into example field");
		}
		return example;
	}
	/**
	 * This method is for Vietnamese class only. Other languages don't use this method
	 * hanja of card (Vietnamese)
	 * @param String word, ChromeDriver driver
	 * @return String hanja
	 * @throws HanjaException
	 */
	public String addHanja(String word, ChromeDriver driver) throws HanjaException {
		driver.get("https://hvdic.thivien.net/hv/" + word); //access từ điển Hán Nôm
		String hanja = "/ ";
		try {
			int size = driver.findElements(By.cssSelector("div.hvres-word.han")).size();
			for (int i = 0; i < size; i++) {
				hanja += driver.findElements(By.cssSelector("div.hvres-word.han")).get(i).getText() + " / ";
				
			}	
			if (hanja.equals("/ ")) {
				throw new HanjaException("There is no hanja information of <" + word + "> in từ điển Hán Nôm site.");
			} else {
				System.out.println(hanja);
			}
		} catch (Exception e) {
			throw new HanjaException("Failed to add <" + word + "> into hanja field");
		}
		return hanja;
	}
	/**
	 * pronunciation of card
	 * @param String word
	 * @return String pronunciation
	 */
	@Override
	public String addPronunciation(String word) {
		String pronunciation = "[sound:pronunciation_vn_" + word + ".mp3]";
		return pronunciation;
	}
	/**
	 * link of card
	 * @param String word
	 * @return String link
	 */
	@Override
	public String addLink(String word) {
		String link = "<a href=\"https://www.google.co.kr/search?q=" + word + "&tbm=isch\">image link</a><br><br>"
		        + "<a href=\"http://tratu.coviet.vn/hoc-tieng-anh/tu-dien/lac-viet/V-V/" + word + ".html\">"
				+ "http://tratu.coviet.vn/hoc-tieng-anh/tu-dien/lac-viet/V-V/" + word + ".html" + "</a><br><br><a "
				+ "href=\"http://tratu.soha.vn/dict/vn_vn/" + word + "\">"
				+ "http://tratu.soha.vn/dict/vn_vn/" + word + "</a><br><br><a "
				+ "href=\"https://vtudien.com/viet-viet/dictionary/nghia-cua-tu-" + word + "\">"
				+ "https://vtudien.com/viet-viet/dictionary/nghia-cua-tu-" + word + "</a><br><br><a "
				+ "href=\"https://dict.naver.com/vikodict/#/search?query=" + word + "\">"
				+ "https://dict.naver.com/vikodict/#/search?query=" + word + "</a>";
		return link;
	}
	/**
	 * tag of card
	 * @param String word
	 * @return String tag
	 */
	@Override
	public String addTag(String word) {
		String tag = "VN";
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
			driver.get("https://dict.naver.com/vikodict/#/search?query=" + word); //access to the site
			Thread.sleep(1000);
			driver.findElements(By.className("highlight")).get(0).click(); // get into the word page
			Thread.sleep(1000);
			String Mp3Address = driver.findElements(By.cssSelector("button.btn_listen.mp3")).get(0).getAttribute("purl"); //get MP3 file's URL		
			Tool tool = new Tool();
			tool.fileDownload(Mp3Address, "pronunciation_vn_" + word + ".mp3");
			System.out.println("successfully download [pronunciation_vn_" + word + ".mp3] !!");
		} catch (Exception e) {
			throw new MP3DownloadException("Failed to download MP3 file of <" + word + ">.");
		}	
	}
}
