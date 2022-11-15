package Exceptions;

/**
 * exception when failed to add phonetic_alphabet field
 * 
 * @author Kwon Minho
 *
 */
public class PhoneticAlphabetOrHanjaException extends Exception {

  /**
   * default constructor
   */
  public PhoneticAlphabetOrHanjaException() {
    super();
  }

  /**
   * You can set exception message into '()' when occurs Exception
   * 
   * @param message
   */
  public PhoneticAlphabetOrHanjaException(String message) {
    super(message);
  }
}
