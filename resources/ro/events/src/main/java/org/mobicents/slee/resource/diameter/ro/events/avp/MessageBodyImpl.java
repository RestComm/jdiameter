/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.Originator;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * MessageBodyImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:08:04 PM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageBodyImpl extends GroupedAvpImpl implements MessageBody {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MessageBodyImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentDisposition()
   */
  public String getContentDisposition() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentLength()
   */
  public long getContentLength() {
    return getAvpAsUnsigned32(DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentType()
   */
  public String getContentType() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getOriginator()
   */
  public Originator getOriginator() {
    return (Originator) getAvpAsEnumerated(DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID, Originator.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentDisposition()
   */
  public boolean hasContentDisposition() {
    return hasAvp( DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentLength()
   */
  public boolean hasContentLength() {
    return hasAvp( DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentType()
   */
  public boolean hasContentType() {
    return hasAvp( DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasOriginator()
   */
  public boolean hasOriginator() {
    return hasAvp( DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentDisposition(java.lang.String)
   */
  public void setContentDisposition( String contentDisposition ) {
    addAvp(DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID, contentDisposition);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentLength(long)
   */
  public void setContentLength( long contentLength ) {
    addAvp(DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID, contentLength);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentType(java.lang.String)
   */
  public void setContentType( String contentType ) {
    addAvp(DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, contentType);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setOriginator(net.java.slee.resource.diameter.ro.events.avp.Originator)
   */
  public void setOriginator( Originator originator ) {
    addAvp(DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID, originator.getValue());
  }

}
