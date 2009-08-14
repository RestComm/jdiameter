package org.jdiameter.common.impl.validation;

import org.jdiameter.common.api.DiameterRuntimeException;

/**
 * Start time:12:10:24 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.189
 */
public class JAvpNotAllowedException extends DiameterRuntimeException {

  private static final long serialVersionUID = 1L;
  
  private int avpCode = -1;
	private long vendorId = -1l;

	/**
	 * 	
	 */
	public JAvpNotAllowedException(int code, long vendor) {
		this.avpCode = code;
		this.vendorId = vendor;
	}

	/**
	 * 
	 * @param message
	 */
	public JAvpNotAllowedException(String message, int code, long vendor) {
		super(message);
		this.avpCode = code;
		this.vendorId = vendor;
	}

	/**
	 * 
	 * @param cause
	 */
	public JAvpNotAllowedException(Throwable cause, int code, long vendor) {
		super(cause);
		this.avpCode = code;
		this.vendorId = vendor;
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public JAvpNotAllowedException(String message, Throwable cause, int code, long vendor) {
		super(message, cause);
		this.avpCode = code;
		this.vendorId = vendor;
	}

	public int getAvpCode() {
		return avpCode;
	}

	public long getVendorId() {
		return vendorId;
	}

}
