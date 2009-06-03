/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
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
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation;
import net.java.slee.resource.diameter.ro.events.avp.EventType;
import net.java.slee.resource.diameter.ro.events.avp.ImsInformation;
import net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier;
import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality;
import net.java.slee.resource.diameter.ro.events.avp.RoleOfNode;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;
import net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities;
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * ImsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:34:57 AM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */ 
public class ImsInformationImpl extends GroupedAvpImpl implements ImsInformation {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ImsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getApplicationServerInformations()
   */
  public ApplicationServerInformation[] getApplicationServerInformations() {
    return (ApplicationServerInformation[]) getAvpsAsCustom(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, ApplicationServerInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getBearerService()
   */
  public String getBearerService() {
    return getAvpAsOctetString(DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCalledPartyAddress()
   */
  public String getCalledPartyAddress() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCallingPartyAddress()
   */
  public String getCallingPartyAddress() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCauseCode()
   */
  public int getCauseCode() {
    return getAvpAsInteger32(DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getEventType()
   */
  public EventType getEventType() {
    return (EventType) getAvpAsCustom(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, EventTypeImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getImsChargingIdentifier()
   */
  public String getImsChargingIdentifier() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getInterOperatorIdentifiers()
   */
  public InterOperatorIdentifier[] getInterOperatorIdentifiers() {
    return (InterOperatorIdentifier[]) getAvpsAsCustom(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, InterOperatorIdentifierImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getMessageBodys()
   */
  public MessageBody[] getMessageBodys() {
    return (MessageBody[]) getAvpsAsCustom(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, MessageBodyImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getNodeFunctionality()
   */
  public NodeFunctionality getNodeFunctionality() {
    return (NodeFunctionality) getAvpAsEnumerated(DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID, NodeFunctionality.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getRoleOfNode()
   */
  public RoleOfNode getRoleOfNode() {
    return (RoleOfNode) getAvpAsEnumerated(DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID, RoleOfNode.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getSdpMediaComponents()
   */
  public SdpMediaComponent[] getSdpMediaComponents() {
    return (SdpMediaComponent[]) getAvpsAsCustom(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, SdpMediaComponentImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getSdpSessionDescriptions()
   */
  public String[] getSdpSessionDescriptions() {
    return getAvpsAsUTF8String(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServedPartyIpAddress()
   */
  public Address getServedPartyIpAddress() {
    return getAvpAsAddress(DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServerCapabilities()
   */
  public ServerCapabilities getServerCapabilities() {
    return (ServerCapabilities) getAvpAsCustom(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, ServerCapabilitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServiceId()
   */
  public String getServiceId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServiceSpecificData()
   */
  public String getServiceSpecificData() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getTimeStamps()
   */
  public TimeStamps getTimeStamps() {
    return (TimeStamps) getAvpAsCustom(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, TimeStampsImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getTrunkGroupId()
   */
  public TrunkGroupId getTrunkGroupId() {
    return (TrunkGroupId) getAvpAsCustom(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, TrunkGroupIdImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getUserSessionId()
   */
  public String getUserSessionId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasBearerService()
   */
  public boolean hasBearerService() {
    return hasAvp( DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCalledPartyAddress()
   */
  public boolean hasCalledPartyAddress() {
    return hasAvp( DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCallingPartyAddress()
   */
  public boolean hasCallingPartyAddress() {
    return hasAvp( DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCauseCode()
   */
  public boolean hasCauseCode() {
    return hasAvp( DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasEventType()
   */
  public boolean hasEventType() {
    return hasAvp( DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasImsChargingIdentifier()
   */
  public boolean hasImsChargingIdentifier() {
    return hasAvp( DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasNodeFunctionality()
   */
  public boolean hasNodeFunctionality() {
    return hasAvp( DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasRoleOfNode()
   */
  public boolean hasRoleOfNode() {
    return hasAvp( DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServedPartyIpAddress()
   */
  public boolean hasServedPartyIpAddress() {
    return hasAvp( DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServerCapabilities()
   */
  public boolean hasServerCapabilities() {
    return hasAvp( DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServiceId()
   */
  public boolean hasServiceId() {
    return hasAvp( DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServiceSpecificData()
   */
  public boolean hasServiceSpecificData() {
    return hasAvp( DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasTimeStamps()
   */
  public boolean hasTimeStamps() {
    return hasAvp( DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasTrunkGroupId()
   */
  public boolean hasTrunkGroupId() {
    return hasAvp( DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasUserSessionId()
   */
  public boolean hasUserSessionId() {
    return hasAvp( DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setApplicationServerInformation(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation)
   */
  public void setApplicationServerInformation( ApplicationServerInformation applicationServerInformation ){
    addAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, applicationServerInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setApplicationServerInformations(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation[])
   */
  public void setApplicationServerInformations( ApplicationServerInformation[] applicationServerInformations ){
    for(ApplicationServerInformation applicationServerInformation : applicationServerInformations) {
      setApplicationServerInformation(applicationServerInformation);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setBearerService(String)
   */
  public void setBearerService( String bearerService ) {
    addAvp(DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID, bearerService);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCalledPartyAddress(java.lang.String)
   */
  public void setCalledPartyAddress( String calledPartyAddress ) {
    addAvp(DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, calledPartyAddress);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCallingPartyAddress(java.lang.String)
   */
  public void setCallingPartyAddress( String callingPartyAddress ) {
    addAvp(DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, callingPartyAddress);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCauseCode(int)
   */
  public void setCauseCode( int causeCode ) {
    addAvp(DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID, causeCode);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setEventType(net.java.slee.resource.diameter.ro.events.avp.EventType)
   */
  public void setEventType( EventType eventType ) {
    addAvp(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, eventType.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setImsChargingIdentifier(java.lang.String)
   */
  public void setImsChargingIdentifier( String imsChargingIdentifier ) {
    addAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, imsChargingIdentifier);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setInterOperatorIdentifier(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier)
   */
  public void setInterOperatorIdentifier( InterOperatorIdentifier interOperatorIdentifier ) {
    addAvp(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, interOperatorIdentifier.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setInterOperatorIdentifiers(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier[])
   */
  public void setInterOperatorIdentifiers( InterOperatorIdentifier[] interOperatorIdentifiers ) {
    for(InterOperatorIdentifier interOperatorIdentifier : interOperatorIdentifiers) {
      setInterOperatorIdentifier(interOperatorIdentifier);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setMessageBody(net.java.slee.resource.diameter.ro.events.avp.MessageBody)
   */
  public void setMessageBody( MessageBody messageBody ) {
    addAvp(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, messageBody.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setMessageBodys(net.java.slee.resource.diameter.ro.events.avp.MessageBody[])
   */
  public void setMessageBodys( MessageBody[] messageBodys ) {
    for(MessageBody messageBody : messageBodys) {
      setMessageBody(messageBody);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setNodeFunctionality(net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality)
   */
  public void setNodeFunctionality( NodeFunctionality nodeFunctionality ) {
    addAvp(DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID, (long)nodeFunctionality.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setRoleOfNode(net.java.slee.resource.diameter.ro.events.avp.RoleOfNode)
   */
  public void setRoleOfNode( RoleOfNode roleOfNode ) {
    addAvp(DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID, roleOfNode.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpMediaComponent(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent)
   */
  public void setSdpMediaComponent( SdpMediaComponent sdpMediaComponent ) {
    addAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, sdpMediaComponent.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpMediaComponents(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent[])
   */
  public void setSdpMediaComponents( SdpMediaComponent[] sdpMediaComponents ) {
    for(SdpMediaComponent sdpMediaComponent : sdpMediaComponents) {
      setSdpMediaComponent(sdpMediaComponent);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpSessionDescription(java.lang.String)
   */
  public void setSdpSessionDescription( String sdpSessionDescription )
  {
    addAvp(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID, sdpSessionDescription);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpSessionDescriptions(java.lang.String[])
   */
  public void setSdpSessionDescriptions( String[] sdpSessionDescriptions ) {
    for(String sdpSessionDescription : sdpSessionDescriptions) {
      setSdpSessionDescription(sdpSessionDescription);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServedPartyIpAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setServedPartyIpAddress( Address servedPartyIpAddress ) {
    addAvp(DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, servedPartyIpAddress.encode());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServerCapabilities(net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities)
   */
  public void setServerCapabilities( ServerCapabilities serverCapabilities ) {
    addAvp(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, serverCapabilities.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServiceId(java.lang.String)
   */
  public void setServiceId( String serviceId ) {
    addAvp(DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, serviceId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServiceSpecificData(java.lang.String)
   */
  public void setServiceSpecificData( String serviceSpecificData ) {
    addAvp(DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID, serviceSpecificData);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setTimeStamps(net.java.slee.resource.diameter.ro.events.avp.TimeStamps)
   */
  public void setTimeStamps( TimeStamps timeStamps ) {
    addAvp(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, timeStamps.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setTrunkGroupId(net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId)
   */
  public void setTrunkGroupId( TrunkGroupId trunkGroupId ) {
    addAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, trunkGroupId.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setUserSessionId(java.lang.String)
   */
  public void setUserSessionId( String userSessionId ) {
    addAvp(DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, userSessionId);
  }

}
