package Exceptions;

/**
 * exception when failed MP3 file download
 * 
 * @author Kwon Minho
 *
 */
public class MP3DownloadException extends Exception {

  /**
   * default constructor
   */
  public MP3DownloadException() {
    super();
  }

  /**
   * You can set exception message into '()' when occurs Exception
   * 
   * @param message
   */
  public MP3DownloadException(String message) {
    super(message);
  }
}
