/**
 * Start time:14:44:47 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package net.java.slee.resource.diameter.base.events.avp;

/**
 * Start time:14:44:47 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * This exception is thrown when wrong fetch type is used on avp/message, it indicates that underlying avp is of wrong type or it has no value set yet.
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class IllegalAvpValueException extends RuntimeException {


	public IllegalAvpValueException() {
		
	}


	public IllegalAvpValueException(String message) {
		super(message);
		
	}


	public IllegalAvpValueException(Throwable cause) {
		super(cause);
		
	}


	public IllegalAvpValueException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
