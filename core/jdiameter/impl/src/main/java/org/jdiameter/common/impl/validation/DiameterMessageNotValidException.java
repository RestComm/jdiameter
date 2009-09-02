package org.jdiameter.common.impl.validation;

import org.jdiameter.common.api.DiameterRuntimeException;

/**
 * Start time:11:31:29 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.189
 */
public class DiameterMessageNotValidException extends DiameterRuntimeException {

  private static final long serialVersionUID = 1L;

  /**
	 * 	
	 */
	public DiameterMessageNotValidException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param message
	 */
	public DiameterMessageNotValidException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param cause
	 */
	public DiameterMessageNotValidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public DiameterMessageNotValidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
