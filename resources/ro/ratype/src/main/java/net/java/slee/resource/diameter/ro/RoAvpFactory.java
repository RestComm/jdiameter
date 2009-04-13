package net.java.slee.resource.diameter.ro;

import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
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

public interface RoAvpFactory extends CreditControlAVPFactory {

	public CreditControlAVPFactory getCreditControlAVPFactory();

	/**
	 * Create an empty AdditionalContentInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	AdditionalContentInformation createAdditionalContentInformation();

	/**
	 * Create an empty AddressDomain (Grouped AVP) instance.
	 * 
	 * @return
	 */
	AddressDomain createAddressDomain();

	/**
	 * Create an empty ApplicationServerInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	ApplicationServerInformation createApplicationServerInformation();

	/**
	 * Create an empty EventType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	EventType createEventType();

	/**
	 * Create an empty ImsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	ImsInformation createImsInformation();

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
	MbmsInformation createMbmsInformation(byte[] tmgi, byte[] mbmsServiceType, MbmsUserServiceType mbmsUserServiceType);

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
	PsFurnishChargingInformation createPsFurnishChargingInformation(byte[] tgppChargingId, byte[] psFreeFormatData);

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

}
