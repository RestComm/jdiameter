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

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the IMS-Information grouped AVP type. 
 * From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 
 *  7.2.37 IMS-Information AVP The IMS-Information AVP (AVP code 876) is of type Grouped. 
 *  Its purpose is to allow the transmission of additional IMS service specific information elements. 
 *  
 *  It has the following ABNF grammar: 
 *    IMS-Information ::= AVP Header: 876 
 *      [ Event-Type ] 
 *      [ Role-Of-Node ] 
 *      { Node-Functionality } 
 *      [ User-Session-ID ] 
 *      [ Calling-Party-Address ] 
 *      [ Called-Party-Address ] 
 *      [ Time-Stamps ]
 *    * [ Application-Server-Information ]
 *    * [ Inter-Operator-Identifier ]
 *      [ IMS-Charging-Identifier ]
 *    * [ SDP-Session-Description ]
 *    * [ SDP-Media-Component ]
 *      [ Served-Party-IP-Address ]
 *      [ Server-Capabilities ]
 *      [ Trunk-Group-ID ]
 *      [ Bearer-Service ]
 *      [ Service-Id ]
 *      [ Service-Specific-Data ]
 *    * [ Message-Body ] 
 *      [ Cause-Code ]
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface ImsInformation extends GroupedAvp{
  /**
   * Returns the set of Application-Server-Information AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Application-Server-Information AVPs have been set. The elements in the given array are ApplicationServerInformation objects.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation[] getApplicationServerInformations();

  /**
   * Returns the value of the Bearer-Service AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getBearerService();

  /**
   * Returns the value of the Called-Party-Address AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getCalledPartyAddress();

  /**
   * Returns the value of the Calling-Party-Address AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getCallingPartyAddress();

  /**
   * Returns the value of the Cause-Code AVP, of type Integer32. A return value 0< implies that the AVP has not been set.
   */
  abstract int getCauseCode();

  /**
   * Returns the value of the Event-Type AVP, of type Grouped. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.EventType getEventType();

  /**
   * Returns the value of the IMS-Charging-Identifier AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getImsChargingIdentifier();

  /**
   * Returns the set of Inter-Operator-Identifier AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Inter-Operator-Identifier AVPs have been set. The elements in the given array are InterOperatorIdentifier objects.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier[] getInterOperatorIdentifiers();

  /**
   * Returns the set of Message-Body AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Message-Body AVPs have been set. The elements in the given array are MessageBody objects.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.MessageBody[] getMessageBodys();

  /**
   * Returns the value of the Node-Functionality AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality getNodeFunctionality();

  /**
   * Returns the value of the Role-Of-Node AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.RoleOfNode getRoleOfNode();

  /**
   * Returns the set of SDP-Media-Component AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no SDP-Media-Component AVPs have been set. The elements in the given array are SdpMediaComponent objects.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent[] getSdpMediaComponents();

  /**
   * Returns the set of SDP-Session-Description AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no SDP-Session-Description AVPs have been set. The elements in the given array are String objects.
   */
  abstract java.lang.String[] getSdpSessionDescriptions();

  /**
   * Returns the value of the Served-Party-IP-Address AVP, of type Address. A return value of null implies that the AVP has not been set.
   */
  abstract Address getServedPartyIpAddress();

  /**
   * Returns the value of the Server-Capabilities AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities getServerCapabilities();

  /**
   * Returns the value of the Service-Id AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getServiceId();

  /**
   * Returns the value of the Service-Specific-Data AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getServiceSpecificData();

  /**
   * Returns the value of the Time-Stamps AVP, of type Grouped. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.TimeStamps getTimeStamps();

  /**
   * Returns the value of the Trunk-Group-ID AVP, of type Grouped. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId getTrunkGroupId();

  /**
   * Returns the value of the User-Session-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getUserSessionId();

  /**
   * Returns true if the Bearer-Service AVP is present in the message.
   */
  abstract boolean hasBearerService();

  /**
   * Returns true if the Called-Party-Address AVP is present in the message.
   */
  abstract boolean hasCalledPartyAddress();

  /**
   * Returns true if the Calling-Party-Address AVP is present in the message.
   */
  abstract boolean hasCallingPartyAddress();

  /**
   * Returns true if the Cause-Code AVP is present in the message.
   */
  abstract boolean hasCauseCode();

  /**
   * Returns true if the Event-Type AVP is present in the message.
   */
  abstract boolean hasEventType();

  /**
   * Returns true if the IMS-Charging-Identifier AVP is present in the message.
   */
  abstract boolean hasImsChargingIdentifier();

  /**
   * Returns true if the Node-Functionality AVP is present in the message.
   */
  abstract boolean hasNodeFunctionality();

  /**
   * Returns true if the Role-Of-Node AVP is present in the message.
   */
  abstract boolean hasRoleOfNode();

  /**
   * Returns true if the Served-Party-IP-Address AVP is present in the message.
   */
  abstract boolean hasServedPartyIpAddress();

  /**
   * Returns true if the Server-Capabilities AVP is present in the message.
   */
  abstract boolean hasServerCapabilities();

  /**
   * Returns true if the Service-Id AVP is present in the message.
   */
  abstract boolean hasServiceId();

  /**
   * Returns true if the Service-Specific-Data AVP is present in the message.
   */
  abstract boolean hasServiceSpecificData();

  /**
   * Returns true if the Time-Stamps AVP is present in the message.
   */
  abstract boolean hasTimeStamps();

  /**
   * Returns true if the Trunk-Group-ID AVP is present in the message.
   */
  abstract boolean hasTrunkGroupId();

  /**
   * Returns true if the User-Session-ID AVP is present in the message.
   */
  abstract boolean hasUserSessionId();

  /**
   * Sets a single Application-Server-Information AVP in the message, of type Grouped.
   */
  abstract void setApplicationServerInformation(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation applicationServerInformation);

  /**
   * Sets the set of Application-Server-Information AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getApplicationServerInformations() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setApplicationServerInformations(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation[] applicationServerInformations);

  /**
   * Sets the value of the Bearer-Service AVP, of type OctetString.
   */
  abstract void setBearerService(java.lang.String bearerService);

  /**
   * Sets the value of the Called-Party-Address AVP, of type UTF8String.
   */
  abstract void setCalledPartyAddress(java.lang.String calledPartyAddress);

  /**
   * Sets the value of the Calling-Party-Address AVP, of type UTF8String.
   */
  abstract void setCallingPartyAddress(java.lang.String callingPartyAddress);

  /**
   * Sets the value of the Cause-Code AVP, of type Integer32.
   */
  abstract void setCauseCode(int causeCode);

  /**
   * Sets the value of the Event-Type AVP, of type Grouped.
   */
  abstract void setEventType(net.java.slee.resource.diameter.ro.events.avp.EventType eventType);

  /**
   * Sets the value of the IMS-Charging-Identifier AVP, of type UTF8String.
   */
  abstract void setImsChargingIdentifier(java.lang.String imsChargingIdentifier);

  /**
   * Sets a single Inter-Operator-Identifier AVP in the message, of type Grouped.
   */
  abstract void setInterOperatorIdentifier(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier interOperatorIdentifier);

  /**
   * Sets the set of Inter-Operator-Identifier AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getInterOperatorIdentifiers() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setInterOperatorIdentifiers(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier[] interOperatorIdentifiers);

  /**
   * Sets a single Message-Body AVP in the message, of type Grouped.
   */
  abstract void setMessageBody(net.java.slee.resource.diameter.ro.events.avp.MessageBody messageBody);

  /**
   * Sets the set of Message-Body AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getMessageBodys() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setMessageBodys(net.java.slee.resource.diameter.ro.events.avp.MessageBody[] messageBodys);

  /**
   * Sets the value of the Node-Functionality AVP, of type Enumerated.
   */
  abstract void setNodeFunctionality(net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality nodeFunctionality);

  /**
   * Sets the value of the Role-Of-Node AVP, of type Enumerated.
   */
  abstract void setRoleOfNode(net.java.slee.resource.diameter.ro.events.avp.RoleOfNode roleOfNode);

  /**
   * Sets a single SDP-Media-Component AVP in the message, of type Grouped.
   */
  abstract void setSdpMediaComponent(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent sdpMediaComponent);

  /**
   * Sets the set of SDP-Media-Component AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getSdpMediaComponents() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setSdpMediaComponents(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent[] sdpMediaComponents);

  /**
   * Sets a single SDP-Session-Description AVP in the message, of type UTF8String.
   */
  abstract void setSdpSessionDescription(java.lang.String sdpSessionDescription);

  /**
   * Sets the set of SDP-Session-Description AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getSdpSessionDescriptions() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setSdpSessionDescriptions(java.lang.String[] sdpSessionDescriptions);

  /**
   * Sets the value of the Served-Party-IP-Address AVP, of type Address.
   */
  abstract void setServedPartyIpAddress(Address servedPartyIpAddress);

  /**
   * Sets the value of the Server-Capabilities AVP, of type OctetString.
   */
  abstract void setServerCapabilities(net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities serverCapabilities);

  /**
   * Sets the value of the Service-Id AVP, of type UTF8String.
   */
  abstract void setServiceId(java.lang.String serviceId);

  /**
   * Sets the value of the Service-Specific-Data AVP, of type UTF8String.
   */
  abstract void setServiceSpecificData(java.lang.String serviceSpecificData);

  /**
   * Sets the value of the Time-Stamps AVP, of type Grouped.
   */
  abstract void setTimeStamps(net.java.slee.resource.diameter.ro.events.avp.TimeStamps timeStamps);

  /**
   * Sets the value of the Trunk-Group-ID AVP, of type Grouped.
   */
  abstract void setTrunkGroupId(net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId trunkGroupId);

  /**
   * Sets the value of the User-Session-ID AVP, of type UTF8String.
   */
  abstract void setUserSessionId(java.lang.String userSessionId);

}
