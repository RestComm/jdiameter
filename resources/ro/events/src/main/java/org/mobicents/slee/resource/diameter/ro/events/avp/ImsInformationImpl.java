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
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
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

  private static final Logger logger = Logger.getLogger( ImsInformationImpl.class );

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ImsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getApplicationServerInformations()
   */
  public ApplicationServerInformation[] getApplicationServerInformations()
  {
    ApplicationServerInformation[] applicationServerInformations = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      applicationServerInformations = new ApplicationServerInformation[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          applicationServerInformations[i] = new ApplicationServerInformationImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION);
          logger.error( "Failure while trying to obtain Application-Server-Information AVP (index:" + i + ").", e );
        }
      }
    }

    return applicationServerInformations;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getBearerService()
   */
  public byte[] getBearerService()
  {
    if(hasBearerService())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.BEARER_SERVICE);
        logger.error( "Failure while trying to obtain Bearer-Service AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCalledPartyAddress()
   */
  public String getCalledPartyAddress()
  {
    if(hasCalledPartyAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CALLED_PARTY_ADDRESS);
        logger.error( "Failure while trying to obtain Called-Party-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCallingPartyAddress()
   */
  public String getCallingPartyAddress()
  {
    if(hasCallingPartyAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CALLING_PARTY_ADDRESS);
        logger.error( "Failure while trying to obtain Calling-Party-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getCauseCode()
   */
  public int getCauseCode()
  {
    if(hasCauseCode())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getInteger32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CAUSE_CODE);
        logger.error( "Failure while trying to obtain Cause-Code AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getEventType()
   */
  public EventType getEventType()
  {
    if(hasEventType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new EventTypeImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.EVENT_TYPE);
        logger.error( "Failure while trying to obtain Event-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getImsChargingIdentifier()
   */
  public String getImsChargingIdentifier()
  {
    if(hasImsChargingIdentifier())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER);
        logger.error( "Failure while trying to obtain IMS-Charging-Identifier AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getInterOperatorIdentifiers()
   */
  public InterOperatorIdentifier[] getInterOperatorIdentifiers()
  {
    InterOperatorIdentifier[] interOperatorIdentifiers = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      interOperatorIdentifiers = new InterOperatorIdentifier[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          interOperatorIdentifiers[i] = new InterOperatorIdentifierImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER);
          logger.error( "Failure while trying to obtain Inter-Operator-Identifier AVP (index:" + i + ").", e );
        }
      }
    }

    return interOperatorIdentifiers;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getMessageBodys()
   */
  public MessageBody[] getMessageBodys()
  {
    MessageBody[] messageBodies = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      messageBodies = new MessageBody[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          messageBodies[i] = new MessageBodyImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MESSAGE_BODY);
          logger.error( "Failure while trying to obtain Message-Body AVP (index:" + i + ").", e );
        }
      }
    }

    return messageBodies;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getNodeFunctionality()
   */
  public NodeFunctionality getNodeFunctionality()
  {
    if(hasNodeFunctionality())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return NodeFunctionality.fromInt(rawAvp.getInteger32());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.NODE_FUNCTIONALITY);
        logger.error( "Failure while trying to obtain Node-Functionality AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getRoleOfNode()
   */
  public RoleOfNode getRoleOfNode()
  {
    if(hasNodeFunctionality())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return RoleOfNode.fromInt(rawAvp.getInteger32());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ROLE_OF_NODE);
        logger.error( "Failure while trying to obtain Role-of-Node AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getSdpMediaComponents()
   */
  public SdpMediaComponent[] getSdpMediaComponents()
  {
    SdpMediaComponent[] sdpMediaComponents = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      sdpMediaComponents = new SdpMediaComponent[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          sdpMediaComponents[i] = new SdpMediaComponentImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SDP_MEDIA_COMPONENT);
          logger.error( "Failure while trying to obtain SDP-Media-Component AVP (index:" + i + ").", e );
        }
      }
    }

    return sdpMediaComponents;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getSdpSessionDescriptions()
   */
  public String[] getSdpSessionDescriptions()
  {
    String[] sdpSessionDescriptions = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      sdpSessionDescriptions = new String[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          sdpSessionDescriptions[i] = rawAvps.getAvp(i).getUTF8String();
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION);
          logger.error( "Failure while trying to obtain SDP-Session-Description AVP (index:" + i + ").", e );
        }
      }
    }

    return sdpSessionDescriptions;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServedPartyIpAddress()
   */
  public Address getServedPartyIpAddress()
  {
    if(hasServedPartyIpAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Address.decode(rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS);
        logger.error( "Failure while trying to obtain Served-Party-IP-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServerCapabilities()
   */
  public byte[] getServerCapabilities()
  {
    if(hasServerCapabilities())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SERVER_CAPABILITIES);
        logger.error( "Failure while trying to obtain Server-Capabilities AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServiceId()
   */
  public String getServiceId()
  {
    if(hasServiceId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SERVICE_ID);
        logger.error( "Failure while trying to obtain Service-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getServiceSpecificData()
   */
  public String getServiceSpecificData()
  {
    if(hasServiceSpecificData())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA);
        logger.error( "Failure while trying to obtain Service-Specific-Data AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getTimeStamps()
   */
  public TimeStamps getTimeStamps()
  {
    if(hasTimeStamps())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new TimeStampsImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TIME_STAMPS);
        logger.error( "Failure while trying to obtain Time-Stamps AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getTrunkGroupId()
   */
  public TrunkGroupId getTrunkGroupId()
  {
    if(hasTrunkGroupId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new TrunkGroupIdImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TRUNK_GROUP_ID);
        logger.error( "Failure while trying to obtain Trunk-Group-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#getUserSessionId()
   */
  public String getUserSessionId()
  {
    if(hasUserSessionId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.USER_SESSION_ID);
        logger.error( "Failure while trying to obtain User-Session-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasBearerService()
   */
  public boolean hasBearerService()
  {
    return hasAvp( DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCalledPartyAddress()
   */
  public boolean hasCalledPartyAddress()
  {
    return hasAvp( DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCallingPartyAddress()
   */
  public boolean hasCallingPartyAddress()
  {
    return hasAvp( DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasCauseCode()
   */
  public boolean hasCauseCode()
  {
    return hasAvp( DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasEventType()
   */
  public boolean hasEventType()
  {
    return hasAvp( DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasImsChargingIdentifier()
   */
  public boolean hasImsChargingIdentifier()
  {
    return hasAvp( DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasNodeFunctionality()
   */
  public boolean hasNodeFunctionality()
  {
    return hasAvp( DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasRoleOfNode()
   */
  public boolean hasRoleOfNode()
  {
    return hasAvp( DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServedPartyIpAddress()
   */
  public boolean hasServedPartyIpAddress()
  {
    return hasAvp( DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServerCapabilities()
   */
  public boolean hasServerCapabilities()
  {
    return hasAvp( DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServiceId()
   */
  public boolean hasServiceId()
  {
    return hasAvp( DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasServiceSpecificData()
   */
  public boolean hasServiceSpecificData()
  {
    return hasAvp( DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasTimeStamps()
   */
  public boolean hasTimeStamps()
  {
    return hasAvp( DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasTrunkGroupId()
   */
  public boolean hasTrunkGroupId()
  {
    return hasAvp( DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#hasUserSessionId()
   */
  public boolean hasUserSessionId()
  {
    return hasAvp( DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setApplicationServerInformation(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation)
   */
  public void setApplicationServerInformation( ApplicationServerInformation applicationServerInformation )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, applicationServerInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setApplicationServerInformations(net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation[])
   */
  public void setApplicationServerInformations( ApplicationServerInformation[] applicationServerInformations )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(ApplicationServerInformation applicationServerInformation : applicationServerInformations)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, applicationServerInformation.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setBearerService(byte[])
   */
  public void setBearerService( byte[] bearerService )
  {
    if(hasBearerService())
    {
      throw new IllegalStateException("AVP Bearer-Service is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.BEARER_SERVICE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.BEARER_SERVICE, bearerService, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCalledPartyAddress(java.lang.String)
   */
  public void setCalledPartyAddress( String calledPartyAddress )
  {
    if(hasCalledPartyAddress())
    {
      throw new IllegalStateException("AVP Called-Party-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CALLED_PARTY_ADDRESS, calledPartyAddress, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCallingPartyAddress(java.lang.String)
   */
  public void setCallingPartyAddress( String callingPartyAddress )
  {
    if(hasCallingPartyAddress())
    {
      throw new IllegalStateException("AVP Calling-Party-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CALLING_PARTY_ADDRESS, callingPartyAddress, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setCauseCode(int)
   */
  public void setCauseCode( int causeCode )
  {
    if(hasCauseCode())
    {
      throw new IllegalStateException("AVP Cause-Code is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CAUSE_CODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CAUSE_CODE, causeCode, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setEventType(net.java.slee.resource.diameter.ro.events.avp.EventType)
   */
  public void setEventType( EventType eventType )
  {
    if(hasEventType())
    {
      throw new IllegalStateException("AVP Event-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.EVENT_TYPE, eventType.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setImsChargingIdentifier(java.lang.String)
   */
  public void setImsChargingIdentifier( String imsChargingIdentifier )
  {
    if(hasImsChargingIdentifier())
    {
      throw new IllegalStateException("AVP IMS-Charging-Identifier is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, imsChargingIdentifier, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setInterOperatorIdentifier(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier)
   */
  public void setInterOperatorIdentifier( InterOperatorIdentifier interOperatorIdentifier )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, interOperatorIdentifier.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setInterOperatorIdentifiers(net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier[])
   */
  public void setInterOperatorIdentifiers( InterOperatorIdentifier[] interOperatorIdentifiers )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(InterOperatorIdentifier interOperatorIdentifier : interOperatorIdentifiers)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.IMS_CHARGING_IDENTIFIER, interOperatorIdentifier.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setMessageBody(net.java.slee.resource.diameter.ro.events.avp.MessageBody)
   */
  public void setMessageBody( MessageBody messageBody )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_BODY, messageBody.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setMessageBodys(net.java.slee.resource.diameter.ro.events.avp.MessageBody[])
   */
  public void setMessageBodys( MessageBody[] messageBodys )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(MessageBody messageBody : messageBodys)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_BODY, messageBody.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setNodeFunctionality(net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality)
   */
  public void setNodeFunctionality( NodeFunctionality nodeFunctionality )
  {
    if(hasNodeFunctionality())
    {
      throw new IllegalStateException("AVP Node-Functionality is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.NODE_FUNCTIONALITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.NODE_FUNCTIONALITY, nodeFunctionality.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setRoleOfNode(net.java.slee.resource.diameter.ro.events.avp.RoleOfNode)
   */
  public void setRoleOfNode( RoleOfNode roleOfNode )
  {
    if(hasRoleOfNode())
    {
      throw new IllegalStateException("AVP Role-of-Node is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ROLE_OF_NODE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ROLE_OF_NODE, roleOfNode.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpMediaComponent(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent)
   */
  public void setSdpMediaComponent( SdpMediaComponent sdpMediaComponent )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, sdpMediaComponent.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpMediaComponents(net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent[])
   */
  public void setSdpMediaComponents( SdpMediaComponent[] sdpMediaComponents )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(SdpMediaComponent sdpMediaComponent : sdpMediaComponents)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, sdpMediaComponent.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpSessionDescription(java.lang.String)
   */
  public void setSdpSessionDescription( String sdpSessionDescription )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, sdpSessionDescription, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setSdpSessionDescriptions(java.lang.String[])
   */
  public void setSdpSessionDescriptions( String[] sdpSessionDescriptions )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(String sdpSessionDescription : sdpSessionDescriptions)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.SDP_SESSION_DESCRIPTION, sdpSessionDescription, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServedPartyIpAddress(net.java.slee.resource.diameter.base.events.avp.Address)
   */
  public void setServedPartyIpAddress( Address servedPartyIpAddress )
  {
    if(hasServedPartyIpAddress())
    {
      throw new IllegalStateException("AVP Served-Party-IP-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SERVED_PARTY_IP_ADDRESS, servedPartyIpAddress.encode(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServerCapabilities(byte[])
   */
  public void setServerCapabilities( byte[] serverCapabilities )
  {
    if(hasServerCapabilities())
    {
      throw new IllegalStateException("AVP Server-Capabilities is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SERVER_CAPABILITIES, serverCapabilities, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServiceId(java.lang.String)
   */
  public void setServiceId( String serviceId )
  {
    if(hasServiceId())
    {
      throw new IllegalStateException("AVP Service-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SERVICE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SERVICE_ID, serviceId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setServiceSpecificData(java.lang.String)
   */
  public void setServiceSpecificData( String serviceSpecificData )
  {
    if(hasServiceSpecificData())
    {
      throw new IllegalStateException("AVP Service-Specific-Data is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SERVICE_SPECIFIC_DATA, serviceSpecificData, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setTimeStamps(net.java.slee.resource.diameter.ro.events.avp.TimeStamps)
   */
  public void setTimeStamps( TimeStamps timeStamps )
  {
    if(hasTimeStamps())
    {
      throw new IllegalStateException("AVP Time-Stamps is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TIME_STAMPS, timeStamps.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setTrunkGroupId(net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId)
   */
  public void setTrunkGroupId( TrunkGroupId trunkGroupId )
  {
    if(hasTrunkGroupId())
    {
      throw new IllegalStateException("AVP Trunk-Group-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, trunkGroupId.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ImsInformation#setUserSessionId(java.lang.String)
   */
  public void setUserSessionId( String userSessionId )
  {
    if(hasUserSessionId())
    {
      throw new IllegalStateException("AVP User-Session-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.USER_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.USER_SESSION_ID, userSessionId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
