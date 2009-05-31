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
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;

/**
 * <pre>
 *  &lt;b&gt;8.34. Final-Unit-Indication AVP&lt;/b&gt;
 * 
 * 
 *   The Final-Unit-Indication AVP (AVP Code 430) is of type Grouped and
 *   indicates that the Granted-Service-Unit AVP in the Credit-Control-
 *   Answer, or in the AA answer, contains the final units for the
 *   service.  After these units have expired, the Diameter credit-control
 *   client is responsible for executing the action indicated in the
 *   Final-Unit-Action AVP (see section 5.6).
 * 
 *   If more than one unit type is received in the Credit-Control-Answer,
 *   the unit type that first expired SHOULD cause the credit-control
 *   client to execute the specified action.
 * 
 *   In the first interrogation, the Final-Unit-Indication AVP with
 *   Final-Unit-Action REDIRECT or RESTRICT_ACCESS can also be present
 *   with no Granted-Service-Unit AVP in the Credit-Control-Answer or in
 *   the AA answer.  This indicates to the Diameter credit-control client
 *   to execute the specified action immediately.  If the home service
 *   provider policy is to terminate the service, naturally, the server
 *   SHOULD return the appropriate transient failure (see section 9.1) in
 *   order to implement the policy-defined action.
 * 
 *   The Final-Unit-Action AVP defines the behavior of the service element
 *   when the user's account cannot cover the cost of the service and MUST
 *   always be present if the Final-Unit-Indication AVP is included in a
 *   command.
 * 
 *   If the Final-Unit-Action AVP is set to TERMINATE, no other AVPs MUST
 *   be present.
 * 
 *   If the Final-Unit-Action AVP is set to REDIRECT at least the
 *   Redirect-Server AVP MUST be present.  The Restriction-Filter-Rule AVP
 *   or the Filter-Id AVP MAY be present in the Credit-Control-Answer
 *   message if the user is also allowed to access other services that are
 *   not accessible through the address given in the Redirect-Server AVP.
 * 
 *   If the Final-Unit-Action AVP is set to RESTRICT_ACCESS, either the
 *   Restriction-Filter-Rule AVP or the Filter-Id AVP SHOULD be present.
 *   The Filter-Id AVP is defined in [NASREQ].  The Filter-Id AVP can be
 *   used to reference an IP filter list installed in the access device by
 *   means other than the Diameter credit-control application, e.g.,
 *   locally configured or configured by another entity.
 * 
 *   The Final-Unit-Indication AVP is defined as follows (per the
 *   grouped-avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Final-Unit-Indication ::= &lt; AVP Header: 430 &gt;
 *                                { Final-Unit-Action }
 *                               *[ Restriction-Filter-Rule ]
 *                               *[ Filter-Id ]
 *                                [ Redirect-Server ]
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface FinalUnitIndicationAvp extends GroupedAvp {

  /**
   * Returns the set of Filter-Id AVPs. The returned array contains the AVPs
   * in the order they appear in the message. A return value of null implies
   * that no Filter-Id AVPs have been set. The elements in the given array are
   * String objects (Filter-Id is of type UTF8String).
   * 
   * @return
   */
  java.lang.String[] getFilterIds();

  /**
   * Returns the value of the Final-Unit-Action AVP, of type Enumerated.
   * Return value of null indicates that this avp has not been set. See:
   * {@link FinalUnitActionType}
   * 
   * @return
   */
  FinalUnitActionType getFinalUnitAction();

  /**
   * Returns the value of the Redirect-Server AVP, of type Grouped. Return
   * value of null indicates that this avp has not been set. See:
   * {@link RedirectServerAvp}
   * 
   * @return
   */
  RedirectServerAvp getRedirectServer();

  /**
   * Returns the set of Restriction-Filter-Rule AVPs. Null value implies that
   * value has not been set. See: {@link IPFilterRule}
   * 
   * @return
   */
  IPFilterRule[] getRestrictionFilterRules();

  /**
   * Returns true if the Final-Unit-Action AVP is present in the message.
   * 
   * @return
   */
  boolean hasFinalUnitAction();

  /**
   * Returns true if the Redirect-Server AVP is present in the message.
   * 
   * @return
   */
  boolean hasRedirectServer();

  /**
   * Sets a single Filter-Id AVP in the message, of type UTF8String.
   * 
   * @param filterId
   */
  void setFilterId(java.lang.String filterId);

  /**
   * Sets the set of Filter-Id AVPs, with all the values in the given array.
   * 
   * @param filterIds
   */
  void setFilterIds(java.lang.String[] filterIds);

  /**
   * Sets the value of the Final-Unit-Action AVP, of type Enumerated. See:
   * {@link FinalUnitActionType}
   * 
   * @param finalUnitAction
   */
  void setFinalUnitAction(FinalUnitActionType finalUnitAction);

  /**
   * Sets the value of the Redirect-Server AVP, of type Grouped. See:
   * {@link RedirectServerAvp}
   * 
   * @param redirectServer
   */
  void setRedirectServer(RedirectServerAvp redirectServer);

  /**
   * Sets the set of Restriction-Filter-Rule AVPs, with all the values in the
   * given array. The AVPs will be added to message in the order in which they
   * appear in the array. Note: the array must not be altered by the caller
   * following this call, and getRestrictionFilterRules() is not guaranteed to
   * return the same array instance, e.g. an "==" check would fail. See:
   * {@link IPFilterRule}
   * 
   * @param restrictionFilterRule
   */
  void setRestrictionFilterRule(IPFilterRule restrictionFilterRule);

  /**
   * Sets the set of Restriction-Filter-Rule AVPs, with all the values in the
   * given array. See: {@link IPFilterRule}
   * 
   * @param restrictionFilterRules
   */
  void setRestrictionFilterRules(IPFilterRule[] restrictionFilterRules);

}
