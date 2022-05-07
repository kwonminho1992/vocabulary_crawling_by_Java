package crawling;

import java.util.ArrayList;

import org.openqa.selenium.chrome.ChromeDriver;

import Exceptions.ExampleException;
import Exceptions.HanjaException;
import Exceptions.MP3DownloadException;
import Exceptions.MeaningException;
import Exceptions.PartException;
import Exceptions.PhoneticAlphabetException;

/**
 * Executive class
 * @author kwon minho
 *
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		//choose language to run crawling program
		String[] choice = new String[2];
		choice[0] = "english";
		choice[1] = "vietnamese";
		
		
		runCrawling(choice[1]); // run crawling program (get language as argument)
	}
	
	/**
	 * Proceed crawling
	 * If you want to run English, input "english" into paramete. (in case Vietnamese, input "vietnamese")
	 * @param String language
	 * @throws InterruptedException
	 */
	public static void runCrawling(String language) throws InterruptedException {
		Frame frame; // declare frame object (superclass of each languages such as English, Vietnamese...)
		if (language.equals("english")) { // if user choose english, frame object will be English type and run English crawling 
			frame = new English();
		} else { // if user choose vietnamese, frame object will be Vietnamese type and run Vietnamese crawling 
			frame = new Vietnamese();
		}
		
		Tool tool = new Tool();
		ChromeDriver driver = tool.getDriver(); // create driver object
		tool.openTab(driver); // open chrome tab
		ArrayList<ArrayList<String>> words = frame.sortWords(language); // arraylist for the words' information (part, meaning etc...)
		int size = words.size();
		for (int i = 0; i < size; i++) {
			//get information(meaning, example etc..) from web
			try { // download mp3 file
				frame.downloadMp3File(words.get(i).get(0), driver); 
			} catch (MP3DownloadException e) {
				e.printStackTrace();
			}
			if (language.equals("english")) { //setPartForms() method is only for English type (Other languages don't use)
				frame.setPartForms(words.get(i).get(0), driver);
			}
			String picture = frame.addPicture(words.get(i).get(0)); //add picture field
			words.get(i).add(picture); 
			try { //add part field
				String part = frame.addPart(words.get(i).get(0), driver);
				words.get(i).add(part);
			} catch (PartException e) {
				words.get(i).add(".");
				e.printStackTrace();
			}
			try { //add meaning field
				String meaning = frame.addMeaning(words.get(i).get(0), driver);
				words.get(i).add(meaning);
			} catch (MeaningException e) {
				words.get(i).add(".");
				e.printStackTrace();
			}
			try { //add example field
				String example = frame.addExample(words.get(i).get(0), driver);
				words.get(i).add(example);
			} catch (ExampleException e) {
				words.get(i).add("{{c1::" + words.get(i).get(0) + "}}");
				e.printStackTrace();
			}
			if (language.equals("english")) { //phonetic_alphabet field is only for English type
				try { //add phonetic_alphabet field
					String phoneticAlphabet = frame.addPhoneticAlphabet(words.get(i).get(0), driver);
					words.get(i).add(phoneticAlphabet);
				} catch (PhoneticAlphabetException e) {
					words.get(i).add(".");
					e.printStackTrace();
				}
			} else {//hanja field is only for Vietnamese type
				try { //add hanja field
					String hanja = frame.addHanja(words.get(i).get(0), driver);
					words.get(i).add(hanja);
				} catch (HanjaException e) {
					words.get(i).add(".");
					e.printStackTrace();
				}
			}
			String pronunciation = frame.addPronunciation(words.get(i).get(0)); // add pronunciation field
			words.get(i).add(pronunciation);
			String link = frame.addLink(words.get(i).get(0)); // add link field
			words.get(i).add(link);
			String tag = frame.addTag(words.get(i).get(0)); // add tag to the card
			words.get(i).add(tag);
			//write information to csv file
			frame.fillCsvFile(words.get(i), language); 
			System.out.println("Current progress : " + (i + 1) + " / " + words.size());
		}	
		frame.quitChromeDriver(driver);
		System.out.println("Finish program");
	}
}
