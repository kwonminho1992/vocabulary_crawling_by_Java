package Exceptions;

/**
 * exception when failed to add part field
 * @author Kwon Minho
 *
 */
public class PartException extends Exception{
	
	/**
	 * default constructor
	 */
	public PartException() {
		super();
	}
	/**
	 * You can set exception message into '()' when occurs Exception
	 * @param message
	 */
	public PartException(String message) {
		super(message);
	}	
}