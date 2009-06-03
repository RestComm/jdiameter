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

import net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * MessageClassImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:10:15 PM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageClassImpl extends GroupedAvpImpl implements MessageClass {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MessageClassImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#getClassIdentifier()
   */
  public ClassIdentifier getClassIdentifier() {
    return (ClassIdentifier) getAvpAsEnumerated(DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, ClassIdentifier.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#getTokenText()
   */
  public String getTokenText() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#hasClassIdentifier()
   */
  public boolean hasClassIdentifier() {
    return hasAvp( DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#hasTokenText()
   */
  public boolean hasTokenText() {
    return hasAvp( DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#setClassIdentifier(net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier)
   */
  public void setClassIdentifier( ClassIdentifier classIdentifier ) {
    addAvp(DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, classIdentifier.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#setTokenText(java.lang.String)
   */
  public void setTokenText( String tokenText ) {
    addAvp(DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID, tokenText);
  }

}
