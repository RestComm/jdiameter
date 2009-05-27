/**
 * Start time:12:10:24 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.jdiameter.common.impl.validation;

import org.jdiameter.common.api.DiameterRuntimeException;

/**
 * Start time:12:10:24 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpNotAllowedException extends DiameterRuntimeException {

	/**
	 * 	
	 */
	public AvpNotAllowedException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param message
	 */
	public AvpNotAllowedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param cause
	 */
	public AvpNotAllowedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public AvpNotAllowedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
