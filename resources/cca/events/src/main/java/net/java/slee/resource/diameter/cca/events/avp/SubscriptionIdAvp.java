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
 *  &lt;b&gt;8.46. Subscription-Id AVP&lt;/b&gt;
 * 
 * 
 *   The Subscription-Id AVP (AVP Code 443) is used to identify the end
 *   user's subscription and is of type Grouped.  The Subscription-Id AVP
 *   includes a Subscription-Id-Data AVP that holds the identifier and a
 *   Subscription-Id-Type AVP that defines the identifier type.
 * 
 *   It is defined as follows (per the grouped-avp-def of RFC 3588
 *   [DIAMBASE]):
 * 
 *      Subscription-Id ::= &lt; AVP Header: 443 &gt;
 *                          { Subscription-Id-Type }
 *                          { Subscription-Id-Data }
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface SubscriptionIdAvp extends GroupedAvp {

  /**
   * Sets the value of the Subscription-Id-Type AVP, of type Enumerated. <br>
   * See:{@link SubscriptionIdType}.
   * 
   * @param type
   */
  public void setSubscriptionIdType(SubscriptionIdType type);

  /**
   * Returns the value of the Subscription-Id-Type AVP, of type Enumerated. A
   * return value of null implies that the AVP has not been set.
   * 
   * @return
   */
  public SubscriptionIdType getSubscriptionIdType();

  public boolean hasSubscriptionIdType();

  /**
   * Sets the value of the Subscription-Id-Data AVP, of type UTF8String.
   * 
   * @param data
   */
  public void setSubscriptionIdData(String data);

  /**
   * Returns the value of the Subscription-Id-Data AVP, of type UTF8String. A
   * return value of null implies that the AVP has not been set.
   * 
   * @return
   */
  public String getSubscriptionIdData();

  /**
   * Returns true if the Subscription-Id-Data AVP is present in the message.
   * 
   * @return
   */
  public boolean hasSubscriptionIdData();
}
