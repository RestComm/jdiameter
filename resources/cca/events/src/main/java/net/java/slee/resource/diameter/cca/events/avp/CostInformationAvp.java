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
 *<pre> <b>8.7. Cost-Information AVP</b>
 *
 *	
 *   The Cost-Information AVP (AVP Code 423) is of type Grouped, and it is
 *   used to return the cost information of a service, which the credit-
 *   control client can transfer transparently to the end user.  The
 *   included Unit-Value AVP contains the cost estimate (always type of
 *   money) of the service, in the case of price enquiry, or the
 *   accumulated cost estimation, in the case of credit-control session.
 *
 *   The Currency-Code specifies in which currency the cost was given.
 *   The Cost-Unit specifies the unit when the service cost is a cost per
 *   unit (e.g., cost for the service is $1 per minute).
 *
 *   When the Requested-Action AVP with value PRICE_ENQUIRY is included in
 *   the Credit-Control-Request command, the Cost-Information AVP sent in
 *   the succeeding Credit-Control-Answer command contains the cost
 *   estimation of the requested service, without any reservation being
 *   made.
 *
 *   The Cost-Information AVP included in the Credit-Control-Answer
 *   command with the CC-Request-Type set to UPDATE_REQUEST contains the
 *   accumulated cost estimation for the session, without taking any
 *   credit reservation into account.
 *   The Cost-Information AVP included in the Credit-Control-Answer
 *   command with the CC-Request-Type set to EVENT_REQUEST or
 *   TERMINATION_REQUEST contains the estimated total cost for the
 *   requested service.
 *
 *   It is defined as follows (per the grouped-avp-def of
 *   RFC 3588 [DIAMBASE]):
 *
 *                Cost-Information ::= < AVP Header: 423 >
 *                                     { Unit-Value }
 *                                     { Currency-Code }
 *                                     [ Cost-Unit ]
 *	</pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface CostInformationAvp extends GroupedAvp {

  /**
   * Returns the value of the Cost-Unit AVP, of type UTF8String. Return value of null indicates that its not present.
   * 
   * @return
   */
  java.lang.String getCostUnit();

  /**
   * Returns the value of the Currency-Code AVP, of type Unsigned32. Return value of null indicates that its not present.
   * See:  ISO 4217 standard [ISO4217] for defined values;
   * @return
   */
  long getCurrencyCode();

  /**
   * Returns the value of the Unit-Value AVP, of type Grouped.  Return value of null indicates that its not present 
   * See: {@link UnitValueAvp} .
   * @return
   */
  UnitValueAvp getUnitValue();

  /**
   * Returns true if the Cost-Unit AVP is present in the message. 
   * 
   * @return
   */
  boolean hasCostUnit();

  /**
   * Returns true if the Currency-Code AVP is present in the message.
   * 
   * @return
   */
  boolean hasCurrencyCode();

  /**
   * Returns true if the Unit-Value AVP is present in the message.
   * 
   * @return
   */
  boolean hasUnitValue();

  /**
   * Sets the value of the Cost-Unit AVP, of type UTF8String.
   * 
   * @param costUnit
   */
  void setCostUnit(java.lang.String costUnit);

  /**
   * Sets the value of the Currency-Code AVP, of type Unsigned32.
   * 
   * @param currencyCode
   */
  void setCurrencyCode(long currencyCode);

  /**
   * Sets the value of the Unit-Value AVP, of type Grouped.
   * See: {@link UnitValueAvp} .
   * @param unitValue
   */
  void setUnitValue(UnitValueAvp unitValue);

}
