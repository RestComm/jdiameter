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
 * <pre>
 *  &lt;b&gt;8.49. User-Equipment-Info AVP&lt;/b&gt;
 * 
 * 
 *   The User-Equipment-Info AVP (AVP Code 458) is of type Grouped and
 *   allows the credit-control client to indicate the identity and
 *   capability of the terminal the subscriber is using for the connection
 *   to network.
 * 
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 * 
 *      User-Equipment-Info ::= &lt; AVP Header: 458 &gt;
 *                              { User-Equipment-Info-Type }
 *                              { User-Equipment-Info-Value }
 *                              
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface UserEquipmentInfoAvp extends GroupedAvp {

  /**
   * Sets the value of the User-Equipment-Info-Type AVP, of type Enumerated.
   * <br>
   * See: {@link UserEquipmentInfoType}
   * 
   * @param type
   */
  public void setUserEquipmentInfoType(UserEquipmentInfoType type);

  /**
   * Returns the value of the User-Equipment-Info-Type AVP, of type
   * Enumerated. A return value of null implies that the AVP has not been set.
   * <br>
   * See: {@link UserEquipmentInfoType}
   * 
   * @return
   */
  public UserEquipmentInfoType getUserEquipmentInfoType();

  /**
   * Returns true if the User-Equipment-Info-Type AVP is present in the
   * message. <br>
   * See: {@link UserEquipmentInfoType}
   * 
   * @return
   */
  public boolean hasUserEquipmentInfoType();

  /**
   * Sets the value of the User-Equipment-Info-Value AVP, of type OctetString.
   * 
   * @param value
   */
  public void setUserEquipmentInfoValue(byte[] value);

  /**
   * Returns the value of the User-Equipment-Info-Value AVP, of type
   * OctetString. A return value of null implies that the AVP has not been
   * set.
   * 
   * @return
   */
  public byte[] getUserEquipmentInfoValue();

  /**
   * Returns true if the User-Equipment-Info-Value AVP is present in the
   * message.
   * 
   * @return
   */
  public boolean hasUserEquipmentInfoValue();

}
