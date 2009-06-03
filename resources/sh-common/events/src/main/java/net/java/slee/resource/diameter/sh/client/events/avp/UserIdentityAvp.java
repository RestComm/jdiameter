/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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
package net.java.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the User-Identity grouped AVP type.
 *
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0) specification:
 * <pre>
 * 6.3.1        User-Identity AVP
 * 
 * The User-Identity AVP is of type Grouped. This AVP contains either a Public-Identity AVP
 * or an MSISDN AVP.
 * 
 * AVP format
 * User-Identity ::=   &lt;AVP Header: 700 10415&gt;
 *                     [Public-Identity]
 *                     [MSISDN]
 *                     *[AVP]
 * </pre>
 */
public interface UserIdentityAvp extends GroupedAvp {

  /**
   * Returns true if the Public-Identity AVP is present in the message.
   */
  public boolean hasPublicIdentity();

  /**
   * Returns the value of the Public-Identity AVP, of type UTF8String.
   * A return value of null implies that the AVP has not been set.
   */
  public String getPublicIdentity();

  /**
   * Sets the value of the Public-Identity AVP, of type UTF8String.
   * @throws IllegalStateException if setPublicIdentity has already been called
   */
  public void setPublicIdentity(String publicIdentity);

  /**
   * Returns true if the MSISDN AVP is present in the message.
   */
  public boolean hasMsisdn();

  /**
   * Returns the value of the MSISDN AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set.
   */
  public String getMsisdn();

  /**
   * Sets the value of the MSISDN AVP, of type OctetString.
   * @throws IllegalStateException if setMsisdn has already been called
   */
  public void setMsisdn(String msisdn);

}
