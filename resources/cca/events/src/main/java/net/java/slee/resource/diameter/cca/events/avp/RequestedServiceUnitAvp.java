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
 *  &lt;b&gt;8.18. Requested-Service-Unit AVP&lt;/b&gt;
 * 
 * 
 *   The Requested-Service-Unit AVP (AVP Code 437) is of type Grouped and
 *   contains the amount of requested units specified by the Diameter
 *   credit-control client.  A server is not required to implement all the
 *   unit types, and it must treat unknown or unsupported unit types as
 *   invalid AVPs.
 * 
 *   The Requested-Service-Unit AVP is defined as follows (per the
 *   grouped-avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Requested-Service-Unit ::= &lt; AVP Header: 437 &gt;
 *                                 [ CC-Time ]
 *                                 [ CC-Money ]
 *                                 [ CC-Total-Octets ]
 *                                 [ CC-Input-Octets ]
 *                                 [ CC-Output-Octets ]
 *                                 [ CC-Service-Specific-Units ]
 *                                *[ AVP ]
 * </pre>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface RequestedServiceUnitAvp extends GroupedAvp {

  /**
   * Sets the value of the CC-Money AVP, of type Grouped. <br>
   * See: {@link CcMoneyAvp}
   * 
   * @param ccm
   */
  public void setCreditControlMoneyAvp(CcMoneyAvp ccm);

  /**
   * Returns the value of the CC-Money AVP, of type Grouped. A return value of
   * null implies that the AVP has not been set. <br>
   * See: {@link CcMoneyAvp}
   * 
   * @return
   */
  public CcMoneyAvp getCreditControlMoneyAvp();

  /**
   * Returns true if the CC-Money AVP is present in the message.
   * 
   * @return
   */
  public boolean hasCreditControlMoneyAvp();

  /**
   * Sets the value of the CC-Total-Octets AVP, of type Unsigned64.
   * 
   * @param ccto
   */
  public void setCreditControlTotalOctets(long ccto);

  /**
   * Returns the value of the CC-Total-Octets AVP, of type Unsigned64.
   * 
   * @return
   */
  public long getCreditControlTotalOctets();

  /**
   * Returns true if the CC-Total-Octets AVP is present in the message.
   * 
   * @return
   */
  public boolean hasCreditControlTotalOctets();

  /**
   * Sets the value of the CC-Input-Octets AVP, of type Unsigned64.
   * 
   * @param ttc
   */
  public void setCreditControlInputOctets(long ttc);

  /**
   * Returns the value of the CC-Input-Octets AVP, of type Unsigned64.
   * 
   * @return
   */
  public long getCreditControlInputOctets();

  /**
   * Returns true if the CC-Input-Octets AVP is present in the message.
   * 
   * @return
   */
  public boolean hasCreditControlInputOctets();

  /**
   * Sets the value of the CC-Output-Octets AVP, of type Unsigned64.
   * 
   * @param ccoo
   */
  public void setCreditControlOutputOctets(long ccoo);

  /**
   * Returns the value of the CC-Output-Octets AVP, of type Unsigned64.
   * 
   * @return
   */
  public long getCreditControlOutputOctets();

  /**
   * Returns true if the CC-Output-Octets AVP is present in the message.
   * 
   * @return
   */
  public boolean hasCreditControlOutputOctets();

  /**
   * Sets the value of the CC-Time AVP, of type Unsigned32.
   * 
   * @param cct
   */
  public void setCreditControlTime(long cct);

  /**
   * Returns the value of the CC-Time AVP, of type Unsigned32.
   * 
   * @return
   */
  public long getCreditControlTime();

  /**
   * Returns true if the CC-Time AVP is present in the message.
   * 
   * @return
   */
  public boolean hasCreditControlTime();

  /**
   * Sets the value of the CC-Service-Specific-Units AVP, of type Unsigned64.
   * 
   * @param ccssu
   */
  public void setCreditControlServiceSpecificUnits(long ccssu);

  /**
   * Returns the value of the CC-Service-Specific-Units AVP, of type
   * Unsigned64.
   * 
   * @return
   */
  public long getCreditControlServiceSpecificUnits();

  /**
   * Returns true if the CC-Service-Specific-Units AVP is present in the
   * message.
   * 
   * @return
   */
  public boolean hasCreditControlServiceSpecificUnits();

  // public void setExtensionsAvps(DiameterAvp[] extensions);
  // public DiameterAvp[] getExtensionsAvps();
  // public boolean hasExtensionsAvps();

}
