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
package net.java.slee.resource.diameter.ro.events.avp;

/**
 * Defines an interface representing the PS-Information grouped AVP type. 
 * 
 * From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification:
 * 
 *  7.2.87 PS-Information AVP The PS-Information AVP (AVP code 874) is of type Grouped. 
 *  Its purpose is to allow the transmission of additional PS service specific information elements. 
 *  
 *  It has the following ABNF grammar:
 *    PS-Information ::= AVP Header: 874 
 *      [ TGPP-Charging-Id ] 
 *      [ TGPP-PDP-Type ] 
 *      [ PDP-Address ] 
 *      [ TGPP-GPRS-Negotiated-QoS-Profile ] 
 *      [ SGSN-Address ] 
 *      [ GGSN-Address ] 
 *      [ CG-Address ] 
 *      [ TGPP-IMSI-MCC-MNC ] 
 *      [ TGPP-GGSN-MCC-MNC ] 
 *      [ TGPP-NSAPI ] 
 *      [ Called-Station-Id ] 
 * #exclude [ TGPP-Session-Stop-Indicator ] 
 *      [ TGPP-Selection-Mode ] 
 *      [ TGPP-Charging-Characteristics ] 
 *      [ TGPP-SGSN-MCC-MNC ] 
 *      [ TGPP-MS-TimeZone ] 
 *      [ TGPP-CAMEL-Charging-Info ] 
 *      [ Charging-Rule-Base-Name ] 
 *      [ TGPP-User-Location-Info ] 
 *      [ TGPP-RAT-Type ] 
 *      [ PS-Furnish-Charging-Information ]
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface PsInformation extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
  /**
   * Returns the value of the CG-Address AVP, of type Address. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.base.events.avp.Address getCgAddress();

  /**
   * Returns the value of the Charging-Rule-Base-Name AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getChargingRuleBaseName();

  /**
   * Returns the value of the GGSN-Address AVP, of type Address. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.base.events.avp.Address getGgsnAddress();

  /**
   * Returns the value of the PDP-Address AVP, of type Address. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.base.events.avp.Address getPdpAddress();

  /**
   * Returns the value of the PS-Furnish-Charging-Information AVP, of type Grouped. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation getPsFurnishChargingInformation();

  /**
   * Returns the value of the SGSN-Address AVP, of type Address. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.base.events.avp.Address getSgsnAddress();

  /**
   * Returns the value of the TGPP-CAMEL-Charging-Info AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppCamelChargingInfo();

  /**
   * Returns the value of the TGPP-Charging-Characteristics AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppChargingCharacteristics();

  /**
   * Returns the value of the TGPP-Charging-Id AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppChargingId();

  /**
   * Returns the value of the TGPP-GGSN-MCC-MNC AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppGgsnMccMnc();

  /**
   * Returns the value of the TGPP-GPRS-Negotiated-QoS-Profile AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppGprsNegotiatedQosProfile();

  /**
   * Returns the value of the TGPP-IMSI-MCC-MNC AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppImsiMccMnc();

  /**
   * Returns the value of the TGPP-MS-TimeZone AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppMsTimezone();

  /**
   * Returns the value of the TGPP-NSAPI AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppNsapi();

  /**
   * Returns the value of the TGPP-PDP-Type AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppPdpType();

  /**
   * Returns the value of the TGPP-RAT-Type AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppRatType();

  /**
   * Returns the value of the TGPP-Selection-Mode AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppSelectionMode();

  /**
   * Returns the value of the TGPP-Session-Stop-Indicator AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppSessionStopIndicator();

  /**
   * Returns the value of the TGPP-SGSN-MCC-MNC AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppSgsnMccMnc();

  /**
   * Returns the value of the TGPP-User-Location-Info AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppUserLocationInfo();

  /**
   * Returns true if the CG-Address AVP is present in the message.
   */
  abstract boolean hasCgAddress();

  /**
   * Returns true if the Charging-Rule-Base-Name AVP is present in the message.
   */
  abstract boolean hasChargingRuleBaseName();

  /**
   * Returns true if the GGSN-Address AVP is present in the message.
   */
  abstract boolean hasGgsnAddress();

  /**
   * Returns true if the PDP-Address AVP is present in the message.
   */
  abstract boolean hasPdpAddress();

  /**
   * Returns true if the PS-Furnish-Charging-Information AVP is present in the message.
   */
  abstract boolean hasPsFurnishChargingInformation();

  /**
   * Returns true if the SGSN-Address AVP is present in the message.
   */
  abstract boolean hasSgsnAddress();

  /**
   * Returns true if the TGPP-CAMEL-Charging-Info AVP is present in the message.
   */
  abstract boolean hasTgppCamelChargingInfo();

  /**
   * Returns true if the TGPP-Charging-Characteristics AVP is present in the message.
   */
  abstract boolean hasTgppChargingCharacteristics();

  /**
   * Returns true if the TGPP-Charging-Id AVP is present in the message.
   */
  abstract boolean hasTgppChargingId();

  /**
   * Returns true if the TGPP-GGSN-MCC-MNC AVP is present in the message.
   */
  abstract boolean hasTgppGgsnMccMnc();

  /**
   * Returns true if the TGPP-GPRS-Negotiated-QoS-Profile AVP is present in the message.
   */
  abstract boolean hasTgppGprsNegotiatedQosProfile();

  /**
   * Returns true if the TGPP-IMSI-MCC-MNC AVP is present in the message.
   */
  abstract boolean hasTgppImsiMccMnc();

  /**
   * Returns true if the TGPP-MS-TimeZone AVP is present in the message.
   */
  abstract boolean hasTgppMsTimezone();

  /**
   * Returns true if the TGPP-NSAPI AVP is present in the message.
   */
  abstract boolean hasTgppNsapi();

  /**
   * Returns true if the TGPP-PDP-Type AVP is present in the message.
   */
  abstract boolean hasTgppPdpType();

  /**
   * Returns true if the TGPP-RAT-Type AVP is present in the message.
   */
  abstract boolean hasTgppRatType();

  /**
   * Returns true if the TGPP-Selection-Mode AVP is present in the message.
   */
  abstract boolean hasTgppSelectionMode();

  /**
   * Returns true if the TGPP-Session-Stop-Indicator AVP is present in the message.
   */
  abstract boolean hasTgppSessionStopIndicator();

  /**
   * Returns true if the TGPP-SGSN-MCC-MNC AVP is present in the message.
   */
  abstract boolean hasTgppSgsnMccMnc();

  /**
   * Returns true if the TGPP-User-Location-Info AVP is present in the message.
   */
  abstract boolean hasTgppUserLocationInfo();

  /**
   * Sets the value of the CG-Address AVP, of type Address.
   */
  abstract void setCgAddress(net.java.slee.resource.diameter.base.events.avp.Address cgAddress);

  /**
   * Sets the value of the Charging-Rule-Base-Name AVP, of type OctetString.
   */
  abstract void setChargingRuleBaseName(String chargingRuleBaseName);

  /**
   * Sets the value of the GGSN-Address AVP, of type Address.
   */
  abstract void setGgsnAddress(net.java.slee.resource.diameter.base.events.avp.Address ggsnAddress);

  /**
   * Sets the value of the PDP-Address AVP, of type Address.
   */
  abstract void setPdpAddress(net.java.slee.resource.diameter.base.events.avp.Address pdpAddress);

  /**
   * Sets the value of the PS-Furnish-Charging-Information AVP, of type Grouped.
   */
  abstract void setPsFurnishChargingInformation(net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation psFurnishChargingInformation);

  /**
   * Sets the value of the SGSN-Address AVP, of type Address.
   */
  abstract void setSgsnAddress(net.java.slee.resource.diameter.base.events.avp.Address sgsnAddress);

  /**
   * Sets the value of the TGPP-CAMEL-Charging-Info AVP, of type OctetString.
   */
  abstract void setTgppCamelChargingInfo(String tgppCamelChargingInfo);

  /**
   * Sets the value of the TGPP-Charging-Characteristics AVP, of type OctetString.
   */
  abstract void setTgppChargingCharacteristics(String tgppChargingCharacteristics);

  /**
   * Sets the value of the TGPP-Charging-Id AVP, of type OctetString.
   */
  abstract void setTgppChargingId(String tgppChargingId);

  /**
   * Sets the value of the TGPP-GGSN-MCC-MNC AVP, of type OctetString.
   */
  abstract void setTgppGgsnMccMnc(String tgppGgsnMccMnc);

  /**
   * Sets the value of the TGPP-GPRS-Negotiated-QoS-Profile AVP, of type OctetString.
   */
  abstract void setTgppGprsNegotiatedQosProfile(String tgppGprsNegotiatedQosProfile);

  /**
   * Sets the value of the TGPP-IMSI-MCC-MNC AVP, of type OctetString.
   */
  abstract void setTgppImsiMccMnc(String tgppImsiMccMnc);

  /**
   * Sets the value of the TGPP-MS-TimeZone AVP, of type OctetString.
   */
  abstract void setTgppMsTimezone(String tgppMsTimezone);

  /**
   * Sets the value of the TGPP-NSAPI AVP, of type OctetString.
   */
  abstract void setTgppNsapi(String tgppNsapi);

  /**
   * Sets the value of the TGPP-PDP-Type AVP, of type OctetString.
   */
  abstract void setTgppPdpType(String tgppPdpType);

  /**
   * Sets the value of the TGPP-RAT-Type AVP, of type OctetString.
   */
  abstract void setTgppRatType(String tgppRatType);

  /**
   * Sets the value of the TGPP-Selection-Mode AVP, of type OctetString.
   */
  abstract void setTgppSelectionMode(String tgppSelectionMode);

  /**
   * Sets the value of the TGPP-Session-Stop-Indicator AVP, of type OctetString.
   */
  abstract void setTgppSessionStopIndicator(String tgppSessionStopIndicator);

  /**
   * Sets the value of the TGPP-SGSN-MCC-MNC AVP, of type OctetString.
   */
  abstract void setTgppSgsnMccMnc(String tgppSgsnMccMnc);

  /**
   * Sets the value of the TGPP-User-Location-Info AVP, of type OctetString.
   */
  abstract void setTgppUserLocationInfo(String tgppUserLocationInfo);

}
