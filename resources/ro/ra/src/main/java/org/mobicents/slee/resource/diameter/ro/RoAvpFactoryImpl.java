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
package org.mobicents.slee.resource.diameter.ro;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.ro.RoAvpFactory;
import net.java.slee.resource.diameter.ro.events.avp.AdditionalContentInformation;
import net.java.slee.resource.diameter.ro.events.avp.AddressDomain;
import net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation;
import net.java.slee.resource.diameter.ro.events.avp.EventType;
import net.java.slee.resource.diameter.ro.events.avp.ImsInformation;
import net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsClientName;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;
import net.java.slee.resource.diameter.ro.events.avp.LocationType;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsServiceType;
import net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType;
import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.NodeFunctionality;
import net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress;
import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer;

import org.mobicents.slee.resource.diameter.cca.CreditControlAVPFactoryImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.AdditionalContentInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.AddressDomainImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.ApplicationServerInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.DiameterRoAvpCodes;
import org.mobicents.slee.resource.diameter.ro.events.avp.EventTypeImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.ImsInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.InterOperatorIdentifierImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.LcsClientIdImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.LcsClientNameImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.LcsInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.LcsRequestorIdImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.LocationTypeImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.MbmsInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.MessageBodyImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.MessageClassImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.MmContentTypeImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.MmsInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.OriginatorAddressImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.PocInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.PsInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.RecipientAddressImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.SdpMediaComponentImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.ServiceInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TalkBurstExchangeImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TimeStampsImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TrunkGroupIdImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.WlanInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.WlanRadioContainerImpl;

/**
 * 
 * RoAvpFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:04:10 PM Apr 10, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoAvpFactoryImpl extends CreditControlAVPFactoryImpl implements RoAvpFactory {

  public RoAvpFactoryImpl(DiameterAvpFactory baseAvpFactory)
  {
    super(baseAvpFactory);

    this.baseAvpFactory = baseAvpFactory;
  }
  
  public AdditionalContentInformation createAdditionalContentInformation()
  {
    return (AdditionalContentInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, AdditionalContentInformationImpl.class);
  }

  public AddressDomain createAddressDomain()
  {
    return (AddressDomain) AvpUtilities.createAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, AddressDomainImpl.class);
  }

  public ApplicationServerInformation createApplicationServerInformation()
  {
    return (ApplicationServerInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, ApplicationServerInformationImpl.class);
  }

  public EventType createEventType()
  {
    return (EventType) AvpUtilities.createAvp(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, EventTypeImpl.class);
  }

  public ImsInformation createImsInformation()
  {
    return (ImsInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, ImsInformationImpl.class);
  }

  public ImsInformation createImsInformation( NodeFunctionality nodeFunctionality )
  {
    // Create the empty AVP
    ImsInformation imsInformationAvp = createImsInformation();
    
    // Set the provided AVP values
    imsInformationAvp.setNodeFunctionality(nodeFunctionality);
    
    return imsInformationAvp;
  }

  public InterOperatorIdentifier createInterOperatorIdentifier()
  {
    return (InterOperatorIdentifier) AvpUtilities.createAvp(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, InterOperatorIdentifierImpl.class);
  }

  public LcsClientId createLcsClientId()
  {
    return (LcsClientId) AvpUtilities.createAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, LcsClientIdImpl.class);
  }

  public LcsClientName createLcsClientName()
  {
    return (LcsClientName) AvpUtilities.createAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, LcsClientNameImpl.class);
  }

  public LcsInformation createLcsInformation()
  {
    return (LcsInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, LcsInformationImpl.class);
  }

  public LcsRequestorId createLcsRequestorId()
  {
    return (LcsRequestorId) AvpUtilities.createAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, LcsRequestorIdImpl.class);
  }

  public LocationType createLocationType()
  {
    return (LocationType) AvpUtilities.createAvp(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, LocationTypeImpl.class);
  }

  public MbmsInformation createMbmsInformation()
  {
    return (MbmsInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, MbmsInformationImpl.class);
  }

  public MbmsInformation createMbmsInformation( String tmgi, MbmsServiceType mbmsServiceType, MbmsUserServiceType mbmsUserServiceType )
  {
    // Create the empty AVP
    MbmsInformation mbmsInformationAvp = createMbmsInformation();
    
    // Set the provided AVP values
    mbmsInformationAvp.setTmgi(tmgi);
    mbmsInformationAvp.setMbmsServiceType( mbmsServiceType );
    mbmsInformationAvp.setMbmsUserServiceType( mbmsUserServiceType );
    
    return mbmsInformationAvp;
  }

  public MessageBody createMessageBody()
  {
    return (MessageBody) AvpUtilities.createAvp(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, MessageBodyImpl.class);
  }

  public MessageClass createMessageClass()
  {
    return (MessageClass) AvpUtilities.createAvp(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, MessageClassImpl.class);
  }

  public MmContentType createMmContentType()
  {
    return (MmContentType) AvpUtilities.createAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, MmContentTypeImpl.class);
  }

  public MmsInformation createMmsInformation()
  {
    return (MmsInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, MmsInformationImpl.class);
  }

  public OriginatorAddress createOriginatorAddress()
  {
    return (OriginatorAddress) AvpUtilities.createAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, OriginatorAddressImpl.class);
  }

  public PocInformation createPocInformation()
  {
    return (PocInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, PocInformationImpl.class);
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation()
  {
    return (PsFurnishChargingInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, PsFurnishChargingInformationImpl.class);
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation( String tgppChargingId, String psFreeFormatData )
  {
    // Create the empty AVP
    PsFurnishChargingInformation psFurnishChargingInformationAvp = createPsFurnishChargingInformation();
    
    // Set the provided AVP values
    psFurnishChargingInformationAvp.setTgppChargingId(tgppChargingId);
    psFurnishChargingInformationAvp.setPsFreeFormatData(psFreeFormatData);
    
    return psFurnishChargingInformationAvp;
  }

  public PsInformation createPsInformation()
  {
    return (PsInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, PsInformationImpl.class);
  }

  public RecipientAddress createRecipientAddress()
  {
    return (RecipientAddress) AvpUtilities.createAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, RecipientAddressImpl.class);
  }

  public SdpMediaComponent createSdpMediaComponent()
  {
    return (SdpMediaComponent) AvpUtilities.createAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, SdpMediaComponentImpl.class);
  }

  public ServiceInformation createServiceInformation()
  {
    return (ServiceInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, ServiceInformationImpl.class);
  }

  public TalkBurstExchange createTalkBurstExchange()
  {
    return (TalkBurstExchange) AvpUtilities.createAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, TalkBurstExchangeImpl.class);
  }

  public TimeStamps createTimeStamps()
  {
    return (TimeStamps) AvpUtilities.createAvp(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, TimeStampsImpl.class);
  }

  public TrunkGroupId createTrunkGroupId()
  {
    return (TrunkGroupId) AvpUtilities.createAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, TrunkGroupIdImpl.class);
  }

  public WlanInformation createWlanInformation()
  {
    return (WlanInformation) AvpUtilities.createAvp(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, WlanInformationImpl.class);
  }

  public WlanRadioContainer createWlanRadioContainer()
  {
    return (WlanRadioContainer) AvpUtilities.createAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, null, WlanRadioContainerImpl.class);
  }

  public CreditControlAVPFactory getCreditControlAVPFactory()
  {
    return this;
  }

}
