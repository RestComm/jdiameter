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

import java.util.Date;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 *  &lt;b&gt;8.17. Granted-Service-Unit AVP&lt;/b&gt;
 * 
 * 
 *   Granted-Service-Unit AVP (AVP Code 431) is of type Grouped and
 *   contains the amount of units that the Diameter credit-control client
 *   can provide to the end user until the service must be released or the
 *   new Credit-Control-Request must be sent.  A client is not required to
 *   implement all the unit types, and it must treat unknown or
 *   unsupported unit types in the answer message as an incorrect CCA
 *   answer.  In this case, the client MUST terminate the credit-control
 *   session and indicate in the Termination-Cause AVP reason
 *   DIAMETER_BAD_ANSWER.
 * 
 *   The Granted-Service-Unit AVP is defined as follows (per the grouped-
 *   avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Granted-Service-Unit ::= &lt; AVP Header: 431 &gt;
 *                                 [ Tariff-Time-Change ]
 *                                 [ CC-Time ]
 *                                 [ CC-Money ]
 *                                 [ CC-Total-Octets ]
 *                                 [ CC-Input-Octets ]
 *                                 [ CC-Output-Octets ]
 *                                 [ CC-Service-Specific-Units ]
 *                                *[ AVP ]
 *                                
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface GrantedServiceUnitAvp extends GroupedAvp {

  /**
   * <pre>
   * The Tariff-Time-Change AVP (AVP Code 451) is of type Time.  It is
   *   sent from the server to the client and includes the time in seconds
   *   since January 1, 1900, 00:00 UTC, when the tariff of the service will
   *   be changed.
   * 
   *   The tariff change mechanism is optional for the client and server,
   *   and it is not used for time-based services defined in section 5.  If
   *   a client does not support the tariff time change mechanism, it MUST
   *   treat Tariff-Time-Change AVP in the answer message as an incorrect
   *   CCA answer.  In this case, the client terminates the credit-control
   *   session and indicates in the Termination-Cause AVP reason
   *   DIAMETER_BAD_ANSWER.
   * 
   *   Omission of this AVP means that no tariff change is to be reported.
   * </pre>
   * 
   * @param ttc
   */
  public void setTariffTimeChange(Date ttc);

  /**
   * Return value of Tariff-Time-Change avp of type time. Null return value
   * indicates that this avp has not been set.
   * 
   * @return
   */
  public Date getTariffTimeChange();

  /**
   * Return true if avp is present
   * 
   * @return
   */
  public boolean hasTariffTimeChange();

  /**
   * Set value of CC-Money avp of type Grouped.<br>
   * See: {@link CcMoneyAvp}
   * 
   * @param ccm
   */
  public void setCreditControlMoneyAvp(CcMoneyAvp ccm);

  /**
   * Returns value of CC-Money avp of type grouped. Return value of null
   * indicates that this avp has not been set. <br>
   * See: {@link CcMoneyAvp}
   * 
   * @return
   */
  public CcMoneyAvp getCreditControlMoneyAvp();

  /**
   * Return true if CC-Money avp is present
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
   * Returns value of the CC-Total-Octets AVP, of type Unsigned64. If not
   * present value should be <0
   * 
   * @return
   */
  public long getCreditControlTotalOctets();

  /**
   * Returns true if CC-Total-Octets avp is present
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
   * Returns value of the CC-Input-Octets AVP, of type Unsigned64.
   * 
   * @return
   */
  public long getCreditControlInputOctets();

  /**
   * Returns true if CC-Input-Octets AVP is present
   * 
   * @return
   */
  public boolean hasCreditControlInputOctets();

  /**
   * Returns the value of the CC-Output-Octets AVP, of type Unsigned64.
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
   * Returns true if CC-Output-Octets AVP is present
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
   * Returns true if CC-Time AVP is present.
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
   * Returns true if CC-Servce-Specific-Units AVP is present.
   * 
   * @return
   */
  public boolean hasCreditControlServiceSpecificUnits();



}
