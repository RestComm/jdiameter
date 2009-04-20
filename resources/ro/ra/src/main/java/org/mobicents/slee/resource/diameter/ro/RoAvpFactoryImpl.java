package org.mobicents.slee.resource.diameter.ro;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
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

import org.jdiameter.api.Stack;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
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

  private AvpDictionary avpDictionary = AvpDictionary.INSTANCE;

  public RoAvpFactoryImpl(DiameterAvpFactory baseAvpFactory, Stack stack)
  {
    super(baseAvpFactory, stack);

    this.baseAvpFactory = baseAvpFactory;
    this.stack = stack;
  }
  
  public AdditionalContentInformation createAdditionalContentInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    AdditionalContentInformation avp = new AdditionalContentInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  public AddressDomain createAddressDomain()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    AddressDomain avp = new AddressDomainImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  public ApplicationServerInformation createApplicationServerInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    ApplicationServerInformation avp = new ApplicationServerInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});

    return avp;
  }

  public EventType createEventType()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    EventType avp = new EventTypeImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public ImsInformation createImsInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    ImsInformation avp = new ImsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public ImsInformation createImsInformation( NodeFunctionality nodeFunctionality )
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    ImsInformation avp = new ImsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    // Add the provided data
    avp.setNodeFunctionality( nodeFunctionality );
    
    return avp;
  }

  public InterOperatorIdentifier createInterOperatorIdentifier()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    InterOperatorIdentifier avp = new InterOperatorIdentifierImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public LcsClientId createLcsClientId()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    LcsClientId avp = new LcsClientIdImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public LcsClientName createLcsClientName()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    LcsClientName avp = new LcsClientNameImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public LcsInformation createLcsInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    LcsInformation avp = new LcsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public LcsRequestorId createLcsRequestorId()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    LcsRequestorId avp = new LcsRequestorIdImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public LocationType createLocationType()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    LocationType avp = new LocationTypeImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public MbmsInformation createMbmsInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MbmsInformation avp = new MbmsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public MbmsInformation createMbmsInformation( byte[] tmgi, byte[] mbmsServiceType, MbmsUserServiceType mbmsUserServiceType )
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MbmsInformation avp = new MbmsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    // Add the provided data
    avp.setTmgi( tmgi );
    avp.setMbmsServiceType( mbmsServiceType );
    avp.setMbmsUserServiceType( mbmsUserServiceType );
    
    return avp;
  }

  public MessageBody createMessageBody()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MessageBody avp = new MessageBodyImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public MessageClass createMessageClass()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MessageClass avp = new MessageClassImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public MmContentType createMmContentType()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MmContentType avp = new MmContentTypeImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public MmsInformation createMmsInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    MmsInformation avp = new MmsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public OriginatorAddress createOriginatorAddress()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    OriginatorAddress avp = new OriginatorAddressImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public PocInformation createPocInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    PocInformation avp = new PocInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    PsFurnishChargingInformation avp = new PsFurnishChargingInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation( byte[] tgppChargingId, byte[] psFreeFormatData )
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    PsFurnishChargingInformation avp = new PsFurnishChargingInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    // Add the provided data
    avp.setTgppChargingId( tgppChargingId );
    avp.setPsFreeFormatData( psFreeFormatData );

    return avp;
  }

  public PsInformation createPsInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    PsInformation avp = new PsInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public RecipientAddress createRecipientAddress()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    RecipientAddress avp = new RecipientAddressImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public SdpMediaComponent createSdpMediaComponent()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    SdpMediaComponent avp = new SdpMediaComponentImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public ServiceInformation createServiceInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    ServiceInformation avp = new ServiceInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public TalkBurstExchange createTalkBurstExchange()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    TalkBurstExchange avp = new TalkBurstExchangeImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public TimeStamps createTimeStamps()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    TimeStamps avp = new TimeStampsImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public TrunkGroupId createTrunkGroupId()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    TrunkGroupId avp = new TrunkGroupIdImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public WlanInformation createWlanInformation()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    WlanInformation avp = new WlanInformationImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public WlanRadioContainer createWlanRadioContainer()
  {
    // Get the dictionary representation for the AVP
    AvpRepresentation avpRep = avpDictionary.getAvp(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    // Create the AVP with no data
    WlanRadioContainer avp = new WlanRadioContainerImpl(avpRep.getCode(), avpRep.getVendorId(), avpRep.isMandatory() ? 1 : 0, avpRep.isProtected() ? 1 : 0, new byte[]{});
    
    return avp;
  }

  public CreditControlAVPFactory getCreditControlAVPFactory()
  {
    return this;
  }

}
