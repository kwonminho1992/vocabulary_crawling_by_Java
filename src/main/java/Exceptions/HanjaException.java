package Exceptions;

/**
 * exception when failed to add hanja field (Vietnamese class)
 * @author Kwon Minho
 *
 */
public class HanjaException extends Exception{
	
	/**
	 * default constructor
	 */
	public HanjaException() {
		super();
	}
	/**
	 * You can set exception message into '()' when occurs Exception
	 * @param message
	 */
	public HanjaException(String message) {
		super(message);
	}	
}

