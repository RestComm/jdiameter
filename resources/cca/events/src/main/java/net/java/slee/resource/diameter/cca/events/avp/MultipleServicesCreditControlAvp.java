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
 * Defines an interface representing the Multiple-Services-Credit-Control
 * grouped AVP type. From the Diameter Credit-Control Application (rfc4006.txt)
 * specification:
 * 
 * <pre>
 *  8.16.  Multiple-Services-Credit-Control AVP
 *  
 *     Multiple-Services-Credit-Control AVP (AVP Code 456) is of type
 *     Grouped and contains the AVPs related to the independent credit-
 *     control of multiple services feature.  Note that each instance of
 *     this AVP carries units related to one or more services or related to
 *     a single rating group.
 *  
 *     The Service-Identifier and the Rating-Group AVPs are used to
 *     associate the granted units to a given service or rating group.  If
 *     both the Service-Identifier and the Rating-Group AVPs are included,
 *     the target of the service units is always the service(s) indicated by
 *     the value of the Service-Identifier AVP(s).  If only the Rating-
 *     Group-Id AVP is present, the Multiple-Services-Credit-Control AVP
 *     relates to all the services that belong to the specified rating
 *     group.
 *  
 *     The G-S-U-Pool-Reference AVP allows the server to specify a G-S-U-
 *     Pool-Identifier identifying a credit pool within which the units of
 *     the specified type are considered pooled.  If a G-S-U-Pool-Reference
 *     AVP is present, then actual service units of the specified type MUST
 *     also be present.  For example, if the G-S-U-Pool-Reference AVP
 *     specifies Unit-Type TIME, then the CC-Time AVP MUST be present.
 *  
 *     The Requested-Service-Unit AVP MAY contain the amount of requested
 *     service units or the requested monetary value.  It MUST be present in
 *     the initial interrogation and within the intermediate interrogations
 *     in which new quota is requested.  If the credit-control client does
 *     not include the Requested-Service-Unit AVP in a request command,
 *     because for instance, it has determined that the end-user terminated
 *     the service, the server MUST debit the used amount from the user's
 *     account but MUST NOT return a new quota in the corresponding answer.
 *     The Validity-Time, Result-Code, and Final-Unit-Indication AVPs MAY be
 *     present in an answer command as defined in sections 5.1.2 and 5.6 for
 *     the graceful service termination.
 *  
 *     When both the Tariff-Time-Change and Tariff-Change-Usage AVPs are
 *     present, the server MUST include two separate instances of the
 *     Multiple-Services-Credit-Control AVP with the Granted-Service-Unit
 *     AVP associated to the same service-identifier and/or rating-group.
 *     Where the two quotas are associated to the same pool or to different
 *     pools, the credit pooling mechanism defined in section 5.1.2 applies.
 *     The Tariff-Change-Usage AVP MUST NOT be included in request commands
 *     to report used units before, and after tariff time change the Used-
 *     Service-Unit AVP MUST be used.
 *  
 *     A server not implementing the independent credit-control of multiple
 *     services functionality MUST treat the Multiple-Services-Credit-
 *     Control AVP as an invalid AVP.
 *  
 *     The Multiple-Services-Control AVP is defined as follows (per the
 *     grouped-avp-def of RFC 3588 [DIAMBASE]):
 *  
 *        Multiple-Services-Credit-Control ::= &lt; AVP Header: 456 &gt;
 *                                             [ Granted-Service-Unit ]
 *                                             [ Requested-Service-Unit ]
 * 											  *[ Used-Service-Unit ]
 *                                             [ Tariff-Change-Usage ]
 * 											  *[ Service-Identifier ]
 *                                             [ Rating-Group ]
 * 											  *[ G-S-U-Pool-Reference ]
 *                                             [ Validity-Time ]
 *                                             [ Result-Code ]
 *                                             [ Final-Unit-Indication ]
 * 										      *[ AVP ]
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface MultipleServicesCreditControlAvp extends GroupedAvp {


  /**
   * Returns the value of the Final-Unit-Indication AVP, of type Grouped.
   * Return value of null indicates that AVP has not been set. <br>
   * See: {@link FinalUnitIndicationAvp}
   * 
   * @return
   */
  FinalUnitIndicationAvp getFinalUnitIndication();

  /**
   * Returns the value of the Granted-Service-Unit AVP, of type Grouped.Return
   * value of null indicates that AVP has not been set. <br>
   * See: {@link GrantedServiceUnitAvp}
   * 
   * @return
   */
  GrantedServiceUnitAvp getGrantedServiceUnit();

  /**
   * Returns the set of G-S-U-Pool-Reference AVPs.Return value of null
   * indicates that AVP has not been set. <br>
   * See: {@link GSUPoolReferenceAvp}
   * 
   * @return
   */
  GSUPoolReferenceAvp[] getGsuPoolReferences();

  /**
   * Returns the value of the Rating-Group AVP, of type Unsigned32.
   * 
   * @return
   */
  long getRatingGroup();

  /**
   * Returns the value of the Requested-Service-Unit AVP, of type
   * Grouped.Return value of null indicates that AVP has not been set. <br>
   * See: {@link RequestedServiceUnitAvp}
   * 
   * @return
   */
  RequestedServiceUnitAvp getRequestedServiceUnit();

  /**
   * Returns the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @return
   */
  long getResultCode();

  /**
   * Returns the set of Service-Identifier AVPs. The returned array contains
   * the AVPs in the order they appear in the message. A return value of null
   * implies that no Service-Identifier AVPs have been set. The elements in
   * the given array are long objects.
   * 
   * @return
   */
  long[] getServiceIdentifiers();

  /**
   * Returns the value of the Tariff-Change-Usage AVP, of type Enumerated. A
   * return value of null implies that the AVP has not been set. <br>
   * See: {@link TariffChangeUsageType}.
   * 
   * @return
   */
  TariffChangeUsageType getTariffChangeUsage();

  /**
   * Returns the set of Used-Service-Unit AVPs. The returned array contains
   * the AVPs in the order they appear in the message. A return value of null
   * implies that no Used-Service-Unit AVPs have been set. <br>
   * See: {@link UsedServiceUnitAvp}.
   * 
   * @return
   */
  UsedServiceUnitAvp[] getUsedServiceUnits();

  /**
   * Returns the value of the Validity-Time AVP, of type Unsigned32.
   * 
   * @return
   */
  long getValidityTime();

  /**
   * Returns the value of the Validity-Time AVP, of type Unsigned32.
   * 
   * @return
   */
  boolean hasFinalUnitIndication();

  /**
   * Returns true if the Granted-Service-Unit AVP is present in the message.
   * 
   * @return
   */
  boolean hasGrantedServiceUnit();

  boolean hasRatingGroup();

  /**
   * Returns true if the Rating-Group AVP is present in the message.
   * 
   * @return
   */
  boolean hasRequestedServiceUnit();

  /**
   * Returns true if the Requested-Service-Unit AVP is present in the message.
   * 
   * @return
   */
  boolean hasResultCode();

  /**
   * Returns true if the Result-Code AVP is present in the message.
   * 
   * @return
   */
  boolean hasTariffChangeUsage();

  /**
   * Returns true if the Validity-Time AVP is present in the message.
   * 
   * @return
   */
  boolean hasValidityTime();


  /**
   * Sets the value of the Final-Unit-Indication AVP, of type Grouped. <br>
   * See: {@link FinalUnitIndicationAvp}
   */
  void setFinalUnitIndication(FinalUnitIndicationAvp finalUnitIndication);

  /**
   * Sets the value of the Granted-Service-Unit AVP, of type Grouped. <br>
   * See: {@link GrantedServiceUnitAvp}
   * 
   * @param grantedServiceUnit
   */
  void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit);

  /**
   * Sets a single G-S-U-Pool-Reference AVP in the message, of type Grouped.
   * <br>
   * See: {@link GSUPoolReferenceAvp}
   * 
   * @param gsuPoolReference
   */
  void setGsuPoolReference(GSUPoolReferenceAvp gsuPoolReference);

  /**
   * Sets the set of G-S-U-Pool-Reference AVPs, with all the values in the
   * given array. <br>
   * See: {@link GSUPoolReferenceAvp}
   * 
   * @param gsuPoolReferences
   */
  void setGsuPoolReferences(GSUPoolReferenceAvp[] gsuPoolReferences);

  /**
   * Sets the value of the Rating-Group AVP, of type Unsigned32.
   * 
   * @param ratingGroup
   */
  void setRatingGroup(long ratingGroup);

  /**
   * Sets the value of the Requested-Service-Unit AVP, of type Grouped.
   * 
   * @param requestedServiceUnit
   */
  void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit);

  /**
   * Sets the value of the Result-Code AVP, of type Unsigned32.
   * 
   * @param resultCode
   */
  void setResultCode(long resultCode);

  /**
   * Sets a single Service-Identifier AVP in the message, of type Unsigned32.
   * 
   * @param serviceIdentifier
   */
  void setServiceIdentifier(long serviceIdentifier);

  /**
   * Sets the set of Service-Identifier AVPs, with all the values in the given
   * array.
   * 
   * @param serviceIdentifiers
   */
  void setServiceIdentifiers(long[] serviceIdentifiers);

  /**
   * Sets the value of the Tariff-Change-Usage AVP, of type Enumerated.
   * 
   * @param tariffChangeUsage
   */
  void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage);

  /**
   * Sets a single Used-Service-Unit AVP in the message, of type Grouped. <br>
   * See: {@link TariffChangeUsageType}
   * 
   * @param usedServiceUnit
   */
  void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit);

  /**
   * Sets the set of Used-Service-Unit AVPs, with all the values in the given
   * array. <br>
   * See: {@link UsedServiceUnitAvp}
   * 
   * @param usedServiceUnits
   */
  void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits);

  /**
   * Sets the value of the Validity-Time AVP, of type Unsigned32.
   * 
   * @param validityTime
   */
  void setValidityTime(long validityTime);

}
