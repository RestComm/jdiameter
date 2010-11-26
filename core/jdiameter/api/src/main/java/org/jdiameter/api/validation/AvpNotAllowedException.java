/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.api.validation;

/**
 * Class to indicate error in AVP add operation.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpNotAllowedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private int avpCode = -1;
  private long vendorId = -1L;

  /**
   * 	
   */
  public AvpNotAllowedException(int code, long vendor) {
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param message
   */
  public AvpNotAllowedException(String message, int code, long vendor) {
    super(message);
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param cause
   */
  public AvpNotAllowedException(Throwable cause, int code, long vendor) {
    super(cause);
    this.avpCode = code;
    this.vendorId = vendor;
  }

  /**
   * 
   * @param message
   * @param cause
   */
  public AvpNotAllowedException(String message, Throwable cause, int code, long vendor) {
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

  @Override
  public String toString() {
    return "AvpNotAllowedExcption [avpCode=" + avpCode + ", vendorId=" + vendorId + ", toString()=" + super.toString() + "]";
  }

}
