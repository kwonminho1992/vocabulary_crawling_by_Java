package crawling;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * This class provides useful methods to organize crawling file of each language. You can apply
 * methods of Frame class to class files of each languae
 * 
 * @author Kwon Minho
 *
 */
public class Tool {

  /**
   * Tool class has only default constructor
   */
  public Tool() {}

  /**
   * set Chrome options for selenium running and create ChromeDriver object chromediver.exe's
   * version : 101 (chromedriver download address : https://chromedriver.chromium.org/downloads)
   * 
   * @return Chromedriver driver
   */
  public ChromeDriver getDriver() throws InterruptedException {
    // get path of the chromedriver
    String currentDir = System.getProperty("user.dir");
    Path path = Paths.get(currentDir + "\\resource\\chromedriver.exe");

    // set webdriver's path
    System.setProperty("webdriver.chrome.driver", path.toString());
    // set webdriver's option
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless", "--disable-gpu", "--blink-setting=imagesEnable=false",
        "--window-size=1920,1200", "--ignore-certificate-errors", "--disable-extensions",
        "--no-sandbox", "--disable-dev-shm-usage", "--disable-default-apps",
        "--disable-popup-blcking", "--blink-setting", "--remote-allow-origins=*");
    ChromeDriver driver = new ChromeDriver(options);
    return driver;
  }

  /**
   * after create ChromeDriver object, open chrome tab
   * 
   * @param ChromeDriver driver
   */
  public void openTab(ChromeDriver driver) throws InterruptedException {
    // create blank tab
    driver.executeScript("window.open('about:blank','_blank');");
    List<String> tabs = new ArrayList<String>(driver.getWindowHandles());
    // convert to first tab
    driver.switchTo().window(tabs.get(0));
  }


  /**
   * return the directory of resource folder
   * 
   * @return String resourceDir
   */
  public String getResourceDir() {
    String resourceDir = System.getProperty("user.dir");
    resourceDir += "\\resource\\";
    return resourceDir;
  }

  /**
   * if you input url and set file name, you can download the file into resource directory
   * 
   * @param String url, String fileName
   * @throws MalformedURLException
   * @throws IOException
   */
  public void fileDownload(String url, String fileName) throws MalformedURLException, IOException {
    String resourceDir = this.getResourceDir();
    File f = new File(resourceDir + fileName);
    FileUtils.copyURLToFile(new URL(url), f);
  }
}
