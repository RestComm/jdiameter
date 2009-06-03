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

/**
 * 
 * Used by applications to create Diameter Ro request messages.
 * Ro answer messages can be created using the RoServerSessionActivity.createRoCreditControlAnswer() method. 
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoAvpFactory extends CreditControlAVPFactory {

	public CreditControlAVPFactory getCreditControlAVPFactory();

	/**
	 * Create an empty AdditionalContentInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public AdditionalContentInformation createAdditionalContentInformation();

	/**
	 * Create an empty AddressDomain (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public AddressDomain createAddressDomain();

	/**
	 * Create an empty ApplicationServerInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public ApplicationServerInformation createApplicationServerInformation();

	/**
	 * Create an empty EventType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public EventType createEventType();

	/**
	 * Create an empty ImsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public ImsInformation createImsInformation();

	/**
	 * Create a ImsInformation (Grouped AVP) instance using required AVP values.
	 * 
	 * @param nodeFunctionality
	 * @return
	 */
	public ImsInformation createImsInformation(NodeFunctionality nodeFunctionality);

	/**
	 * Create an empty InterOperatorIdentifier (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public InterOperatorIdentifier createInterOperatorIdentifier();

	/**
	 * Create an empty LcsClientId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public LcsClientId createLcsClientId();

	/**
	 * Create an empty LcsClientName (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public LcsClientName createLcsClientName();

	/**
	 * Create an empty LcsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public LcsInformation createLcsInformation();

	/**
	 * Create an empty LcsRequestorId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public LcsRequestorId createLcsRequestorId();

	/**
	 * Create an empty LocationType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public LocationType createLocationType();

	/**
	 * Create an empty MbmsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public MbmsInformation createMbmsInformation();

	/**
	 * Create a MbmsInformation (Grouped AVP) instance using required AVP
	 * values.
	 * 
	 * @param tmgi
	 * @param mbmsServiceType
	 * @param mbmsUserServiceType
	 * @return
	 */
	public MbmsInformation createMbmsInformation(String tmgi, MbmsServiceType mbmsServiceType, MbmsUserServiceType mbmsUserServiceType);

	/**
	 * Create an empty MessageBody (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public MessageBody createMessageBody();

	/**
	 * Create an empty MessageClass (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public MessageClass createMessageClass();

	/**
	 * Create an empty MmContentType (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public MmContentType createMmContentType();

	/**
	 * Create an empty MmsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public MmsInformation createMmsInformation();

	/**
	 * Create an empty OriginatorAddress (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public OriginatorAddress createOriginatorAddress();

	/**
	 * Create an empty PocInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public PocInformation createPocInformation();

	/**
	 * Create an empty PsFurnishChargingInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public PsFurnishChargingInformation createPsFurnishChargingInformation();

	/**
	 * Create a PsFurnishChargingInformation (Grouped AVP) instance using
	 * required AVP values.
	 * 
	 * @param tgppChargingId
	 * @param psFreeFormatData
	 * @return
	 */
	public PsFurnishChargingInformation createPsFurnishChargingInformation(String tgppChargingId, String psFreeFormatData);

	/**
	 * Create an empty PsInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public PsInformation createPsInformation();

	/**
	 * Create an empty RecipientAddress (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public RecipientAddress createRecipientAddress();

	/**
	 * Create an empty SdpMediaComponent (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public SdpMediaComponent createSdpMediaComponent();

	/**
	 * Create an empty ServiceInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public ServiceInformation createServiceInformation();

	/**
	 * Create an empty TalkBurstExchange (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public TalkBurstExchange createTalkBurstExchange();

	/**
	 * Create an empty TimeStamps (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public TimeStamps createTimeStamps();

	/**
	 * Create an empty TrunkGroupId (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public TrunkGroupId createTrunkGroupId();

	/**
	 * Create an empty WlanInformation (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public WlanInformation createWlanInformation();

	/**
	 * Create an empty WlanRadioContainer (Grouped AVP) instance.
	 * 
	 * @return
	 */
	public WlanRadioContainer createWlanRadioContainer();

}
