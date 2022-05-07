package Exceptions;

/**
 * exception when failed to add example field
 * @author Kwon Minho
 *
 */
public class ExampleException extends Exception{
	
	/**
	 * default constructor
	 */
	public ExampleException() {
		super();
	}
	/**
	 * You can set exception message into '()' when occurs Exception
	 * @param message
	 */
	public ExampleException(String message) {
		super(message);
	}	
}
