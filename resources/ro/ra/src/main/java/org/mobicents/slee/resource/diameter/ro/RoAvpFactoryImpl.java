package org.mobicents.slee.resource.diameter.ro;

import org.jdiameter.api.Stack;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.CcMoneyAvpImpl;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
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
import net.java.slee.resource.diameter.ro.events.avp.RoleOfNode;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer;

/**
 * 
 * RoAvpFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:04:10 PM Apr 10, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoAvpFactoryImpl implements RoAvpFactory {

  private DiameterAvpFactory baseAvpFactory;
  private Stack stack;

  private AvpDictionary avpDictionary = AvpDictionary.INSTANCE;

  public RoAvpFactoryImpl(DiameterAvpFactory baseAvpFactory, Stack stack)
  {
    super();

    this.baseAvpFactory = baseAvpFactory;
    this.stack = stack;
  }
  
  public AdditionalContentInformation createAdditionalContentInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AdditionalContentInformation createAdditionalContentInformation( String additionalTypeInformation )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AddressDomain createAddressDomain()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public AddressDomain createAddressDomain( String domainName, byte[] tgppImsiMccMnc )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ApplicationServerInformation createApplicationServerInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ApplicationServerInformation createApplicationServerInformation( String applicationServer, String[] applicationProvidedCalledPartyAddress )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public EventType createEventType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public EventType createEventType( String sipMethod, String event, long expires )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ImsInformation createImsInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ImsInformation createImsInformation( ApplicationServerInformation[] applicationServerInformations, TimeStamps timestamps, EventType eventType, String imsChargingIdentifier, InterOperatorIdentifier[] interOperatiorIdentifiers, MessageBody[] messageBodies, NodeFunctionality nodeFunc, RoleOfNode role, SdpMediaComponent[] mediaComponents, String[] sdpSessionDescriptors, AddressAvp serverPartyIPAddress, TrunkGroupId trunkGroupId, String serviceSpecificData, String serviceID, String[] calledPartyAddresses, String[] callingParrtyAddresses, String serSessionId, byte[] bearerService, byte[] serverCapabilities, int causeCode )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ImsInformation createImsInformation( NodeFunctionality nodeFunctionality )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public InterOperatorIdentifier createInterOperatorIdentifier()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public InterOperatorIdentifier createInterOperatorIdentifier( String originatingIoi )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public InterOperatorIdentifier createInterOperatorIdentifier( String originatingIoi, String terminatingToi )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public LcsClientId createLcsClientId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public LcsClientName createLcsClientName()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public LcsInformation createLcsInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public LcsRequestorId createLcsRequestorId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public LocationType createLocationType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MbmsInformation createMbmsInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MbmsInformation createMbmsInformation( byte[] tmgi, byte[] mbmsServiceType, MbmsUserServiceType mbmsUserServiceType )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MessageBody createMessageBody()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MessageClass createMessageClass()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MmContentType createMmContentType()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public MmsInformation createMmsInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public OriginatorAddress createOriginatorAddress()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public PocInformation createPocInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public PsFurnishChargingInformation createPsFurnishChargingInformation( byte[] tgppChargingId, byte[] psFreeFormatData )
  {
    // TODO Auto-generated method stub
    return null;
  }

  public PsInformation createPsInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public RecipientAddress createRecipientAddress()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public SdpMediaComponent createSdpMediaComponent()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ServiceInformation createServiceInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public TalkBurstExchange createTalkBurstExchange()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public TimeStamps createTimeStamps()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public TrunkGroupId createTrunkGroupId()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public WlanInformation createWlanInformation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public WlanRadioContainer createWlanRadioContainer()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public CreditControlAVPFactory getCreditControlAVPFactory()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
