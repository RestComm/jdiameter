package net.java.slee.resource.diameter.ro;

import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.ro.events.avp.*;

public interface RoAvpFactory {

	public CreditControlAVPFactory getCreditControlAVPFactory();

	/**
	 * Create an empty AdditionalContentInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	AdditionalContentInformation createAdditionalContentInformation();

	/**
	 * Creates an empty AiidtionContentInformation (Grouped AVP) instance with
	 * prefilled value of the Additional-Type-Information.
	 * 
	 * @param additionalTypeInformation
	 * @return
	 */
	AdditionalContentInformation createAdditionalContentInformation(
			String additionalTypeInformation);

	/**
	 * Create an empty AddressDomain (Grouped AVP) instance.
	 * 
	 * @return
	 */
	AddressDomain createAddressDomain();

	/**
	 * Create AddressDomain (Grouped AVP) instance. It prefills Domain-Name and
	 * TGPP-IMSI-MCC-MNC AVPs
	 * 
	 * @param domainName
	 * @param tgppImsiMccMnc
	 * @return
	 */
	AddressDomain createAddressDomain(String domainName, byte[] tgppImsiMccMnc);

	/**
	 * Create an empty ApplicationServerInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	ApplicationServerInformation createApplicationServerInformation();

	/**
	 * Create ApplicationServerInformation (Grouped AVP) instance. Prefills
	 * Application-Provided-Called-Party-Address and Application-Server AVPs.
	 * 
	 * @param applicationServer
	 * @param applicationProvidedCalledPartyAddress
	 * @return
	 */
	ApplicationServerInformation createApplicationServerInformation(
			String applicationServer,
			String[] applicationProvidedCalledPartyAddress);

	/**
	 * Create an empty EventType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	EventType createEventType();

	/**
	 * Creates EventType AVP (Grouped AVP) with prefiled values of
	 * Event,SIP-Method and Expires AVPs
	 * 
	 * @param sipMethod
	 * @param event
	 * @param expires
	 * @return
	 */
	EventType createEventType(String sipMethod, String event, long expires);

	/**
	 * Create an empty ImsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	ImsInformation createImsInformation();

	/**
	 * Create ImsInformation (Grouped AVP) instance. Fills proper AVPs with
	 * passed values, see {@link ImsInformation} for details on populated AVPs.
	 * 
	 * @param applicationServerInformations
	 * @param timestamps
	 * @param eventType
	 * @param imsChargingIdentifier
	 * @param interOperatiorIdentifiers
	 * @param messageBodies
	 * @param nodeFunc
	 * @param role
	 * @param mediaComponents
	 * @param sdpSessionDescriptors
	 * @param serverPartyIPAddress
	 * @param trunkGroupId
	 * @param serviceSpecificData
	 * @param serviceID
	 * @param calledPartyAddresses
	 * @param callingParrtyAddresses
	 * @param serSessionId
	 * @param bearerService
	 * @param serverCapabilities
	 * @param causeCode
	 * @return
	 */
	ImsInformation createImsInformation(
			ApplicationServerInformation[] applicationServerInformations,
			TimeStamps timestamps, EventType eventType,
			String imsChargingIdentifier,
			InterOperatorIdentifier[] interOperatiorIdentifiers,
			MessageBody[] messageBodies, NodeFunctionality nodeFunc,
			RoleOfNode role, SdpMediaComponent[] mediaComponents,
			String[] sdpSessionDescriptors, AddressAvp serverPartyIPAddress,
			TrunkGroupId trunkGroupId, String serviceSpecificData,
			String serviceID, String[] calledPartyAddresses,
			String[] callingParrtyAddresses, String serSessionId,
			byte[] bearerService, byte[] serverCapabilities, int causeCode);

	/**
	 * Create a ImsInformation (Grouped AVP) instance using required AVP values.
	 * 
	 * @param nodeFunctionality
	 * @return
	 */
	ImsInformation createImsInformation(NodeFunctionality nodeFunctionality);

	/**
	 * Create an empty InterOperatorIdentifier (Grouped AVP) instance.
	 * 
	 * @return
	 */
	InterOperatorIdentifier createInterOperatorIdentifier();

	InterOperatorIdentifier createInterOperatorIdentifier(String originatingIoi);

	InterOperatorIdentifier createInterOperatorIdentifier(
			String originatingIoi, String terminatingToi);

	
	/**
	 * Create an empty LcsClientId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	LcsClientId createLcsClientId();

	/**
	 * Create an empty LcsClientName (Grouped AVP) instance.
	 * 
	 * @return
	 */
	LcsClientName createLcsClientName();

	/**
	 * Create an empty LcsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	LcsInformation createLcsInformation();

	/**
	 * Create an empty LcsRequestorId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	LcsRequestorId createLcsRequestorId();

	/**
	 * Create an empty LocationType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	LocationType createLocationType();

	/**
	 * Create an empty MbmsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	MbmsInformation createMbmsInformation();

	/**
	 * Create a MbmsInformation (Grouped AVP) instance using required AVP
	 * values.
	 * 
	 * @param tmgi
	 * @param mbmsServiceType
	 * @param mbmsUserServiceType
	 * @return
	 */
	MbmsInformation createMbmsInformation(byte[] tmgi, byte[] mbmsServiceType,
			MbmsUserServiceType mbmsUserServiceType);

	/**
	 * Create an empty MessageBody (Grouped AVP) instance.
	 * 
	 * @return
	 */
	MessageBody createMessageBody();

	/**
	 * Create an empty MessageClass (Grouped AVP) instance.
	 * 
	 * @return
	 */
	MessageClass createMessageClass();

	/**
	 * Create an empty MmContentType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	MmContentType createMmContentType();

	/**
	 * Create an empty MmsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	MmsInformation createMmsInformation();

	/**
	 * Create an empty OriginatorAddress (Grouped AVP) instance.
	 * 
	 * @return
	 */
	OriginatorAddress createOriginatorAddress();

	/**
	 * Create an empty PocInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	PocInformation createPocInformation();

	/**
	 * Create an empty PsFurnishChargingInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	PsFurnishChargingInformation createPsFurnishChargingInformation();

	/**
	 * Create a PsFurnishChargingInformation (Grouped AVP) instance using
	 * required AVP values.
	 * 
	 * @param tgppChargingId
	 * @param psFreeFormatData
	 * @return
	 */
	PsFurnishChargingInformation createPsFurnishChargingInformation(
			byte[] tgppChargingId, byte[] psFreeFormatData);

	/**
	 * Create an empty PsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	PsInformation createPsInformation();

	/**
	 * Create an empty RecipientAddress (Grouped AVP) instance.
	 * 
	 * @return
	 */
	RecipientAddress createRecipientAddress();

	/**
	 * Create an empty SdpMediaComponent (Grouped AVP) instance.
	 * 
	 * @return
	 */
	SdpMediaComponent createSdpMediaComponent();

	/**
	 * Create an empty ServiceInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	ServiceInformation createServiceInformation();

	/**
	 * Create an empty TalkBurstExchange (Grouped AVP) instance.
	 * 
	 * @return
	 */
	TalkBurstExchange createTalkBurstExchange();

	/**
	 * Create an empty TimeStamps (Grouped AVP) instance.
	 * 
	 * @return
	 */
	TimeStamps createTimeStamps();

	/**
	 * Create an empty TrunkGroupId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	TrunkGroupId createTrunkGroupId();

	/**
	 * Create an empty WlanInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	WlanInformation createWlanInformation();

	/**
	 * Create an empty WlanRadioContainer (Grouped AVP) instance.
	 * 
	 * @return
	 */
	WlanRadioContainer createWlanRadioContainer();

	//FIXME: Bartek -> I have restrained myself from createing more methods - like in case of createImsInformation(...) cause Im not sure of exact case when those methods can be invoked and with what
}
