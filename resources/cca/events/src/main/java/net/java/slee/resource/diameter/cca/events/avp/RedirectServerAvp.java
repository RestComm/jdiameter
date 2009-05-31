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
package net.java.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 *<pre> <b>8.37. Redirect-Server AVP</b>
 *
 *
 *   The Redirect-Server AVP (AVP Code 434) is of type Grouped and
 *   contains the address information of the redirect server (e.g., HTTP
 *   redirect server, SIP Server) with which the end user is to be
 *   connected when the account cannot cover the service cost.  It MUST be
 *   present when the Final-Unit-Action AVP is set to REDIRECT.
 *
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 *
 *      Redirect-Server ::= < AVP Header: 434 >
 *                          { Redirect-Address-Type }
 *                          { Redirect-Server-Address }
 *	</pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface RedirectServerAvp extends GroupedAvp {

  /**
   * Returns the value of the Redirect-Address-Type AVP, of type Enumerated.
   * <br>See: {@link RedirectAddressType}
   * @return
   */
  RedirectAddressType getRedirectAddressType();

  /**
   * Returns the value of the Redirect-Server-Address AVP, of type UTF8String. If return value is null it implies that value has not been set.
   * 
   * @return
   */
  java.lang.String getRedirectServerAddress();

  /**
   * Returns true if the Redirect-Address-Type AVP is present in the message.
   * 
   * @return
   */
  boolean hasRedirectAddressType();

  /**
   * Returns true if the Redirect-Server-Address AVP is present in the
   * message.
   * 
   * @return
   */
  boolean hasRedirectServerAddress();

  /**
   * Sets the value of the Redirect-Address-Type AVP, of type Enumerated.
   * <br>See: {@link RedirectAddressType}
   * @param redirectAddressType
   */
  void setRedirectAddressType(RedirectAddressType redirectAddressType);

  /**
   * Sets the value of the Redirect-Server-Address AVP, of type UTF8String.
   * 
   * @param redirectServerAddress
   */
  void setRedirectServerAddress(java.lang.String redirectServerAddress);

}
