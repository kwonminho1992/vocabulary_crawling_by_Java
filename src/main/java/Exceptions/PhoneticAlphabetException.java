package Exceptions;

/**
 * exception when failed to add phonetic_alphabet field
 * @author Kwon Minho
 *
 */
public class PhoneticAlphabetException extends Exception{
	
	/**
	 * default constructor
	 */
	public PhoneticAlphabetException() {
		super();
	}
	/**
	 * You can set exception message into '()' when occurs Exception
	 * @param message
	 */
	public PhoneticAlphabetException(String message) {
		super(message);
	}	
}
