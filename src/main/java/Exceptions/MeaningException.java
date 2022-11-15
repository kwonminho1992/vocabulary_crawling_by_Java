package Exceptions;

/**
 * exception when failed to add meaning field
 * 
 * @author Kwon Minho
 *
 */
public class MeaningException extends Exception {

  /**
   * default constructor
   */
  public MeaningException() {
    super();
  }

  /**
   * You can set exception message into '()' when occurs Exception
   * 
   * @param message
   */
  public MeaningException(String message) {
    super(message);
  }
}
