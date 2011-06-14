/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.resources.diameter.tests.factories;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.OwnVendorID;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeRequest;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerRequest;
import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.AddressType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate;
import net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo;
import net.java.slee.resource.diameter.gq.events.avp.BindingInformation;
import net.java.slee.resource.diameter.gq.events.avp.BindingInputList;
import net.java.slee.resource.diameter.gq.events.avp.BindingOutputList;
import net.java.slee.resource.diameter.gq.events.avp.FlowGrouping;
import net.java.slee.resource.diameter.gq.events.avp.Flows;
import net.java.slee.resource.diameter.gq.events.avp.GloballyUniqueAddress;
import net.java.slee.resource.diameter.gq.events.avp.MediaComponentDescription;
import net.java.slee.resource.diameter.gq.events.avp.MediaSubComponent;
import net.java.slee.resource.diameter.gq.events.avp.V4TransportAddress;
import net.java.slee.resource.diameter.gq.events.avp.V6TransportAddress;
import net.java.slee.resource.diameter.gx.events.avp.ChargingRuleInstall;
import net.java.slee.resource.diameter.gx.events.avp.ChargingRuleRemove;
import net.java.slee.resource.diameter.gx.events.avp.TFTPacketFilterInformation;
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
import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress;
import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsFurnishChargingInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;
import net.java.slee.resource.diameter.ro.events.avp.SdpMediaComponent;
import net.java.slee.resource.diameter.ro.events.avp.ServerCapabilities;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;
import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;
import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanRadioContainer;
import net.java.slee.resource.diameter.sh.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.events.avp.userdata.ShData;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.junit.Assert;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.FailedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ProxyInfoAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.CcMoneyAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.CostInformationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RedirectServerAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UnitValueAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.ChargingInformationImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.DeregistrationReasonImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.RestorationInfoImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfoImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItemImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticateImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SubscriptionInfoImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.BindingInformationImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.BindingInputListImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.BindingOutputListImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.DiameterGqAvpCodes;
import org.mobicents.slee.resource.diameter.gq.events.avp.FlowGroupingImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.FlowsImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.GloballyUniqueAddressImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.MediaComponentDescriptionImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.MediaSubComponentImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.V4TransportAddressImpl;
import org.mobicents.slee.resource.diameter.gq.events.avp.V6TransportAddressImpl;
import org.mobicents.slee.resource.diameter.gx.events.avp.ChargingRuleInstallImpl;
import org.mobicents.slee.resource.diameter.gx.events.avp.ChargingRuleRemoveImpl;
import org.mobicents.slee.resource.diameter.gx.events.avp.DiameterGxAvpCodes;
import org.mobicents.slee.resource.diameter.gx.events.avp.TFTPacketFilterInformationImpl;
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
import org.mobicents.slee.resource.diameter.ro.events.avp.ServerCapabilitiesImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.ServiceInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TalkBurstExchangeImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TimeStampsImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.TrunkGroupIdImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.WlanInformationImpl;
import org.mobicents.slee.resource.diameter.ro.events.avp.WlanRadioContainerImpl;
import org.mobicents.slee.resource.diameter.sh.events.avp.SupportedFeaturesAvpImpl;
import org.mobicents.slee.resource.diameter.sh.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.events.avp.userdata.ObjectFactory;
import org.mobicents.slee.resource.diameter.sh.events.avp.userdata.UserDataObjectFactoryImpl;


/**
 *
 * Aux Class for assisting in testing AVP setters/getters
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpAssistant {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;

  private static String serverHost = "localhost";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  private static String realmName = "mobicents.org";

  private static final HashMap<Class, Object> typeValues = new HashMap<Class, Object>(); 

  public static final Collection<String> methodsToIgnore = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInRequest = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInAnswer = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInCEX = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInDPX = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInDWX = new ArrayList<String>();

  public static final Collection<String> methodsToIgnoreInRfACR = new ArrayList<String>();


  public static AvpAssistant INSTANCE;

  static {
    try {
      INSTANCE = new AvpAssistant();
    }
    catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  private AvpAssistant() throws Exception
  {
    methodsToIgnore.add("getAvps");
    methodsToIgnore.add("getCommand");
    methodsToIgnore.add("getExtensionAvps");
    methodsToIgnore.add("setExtensionAvps");
    methodsToIgnore.add("getGenericData");
    
    methodsToIgnore.add("getHeader");
    methodsToIgnore.add("getClass");
    methodsToIgnore.add("getCode");
    methodsToIgnore.add("getVendorId");
    methodsToIgnore.add("getMandatoryRule");
    methodsToIgnore.add("getProtectedRule");
    methodsToIgnore.add("getName");
    methodsToIgnore.add("getType");

    methodsToIgnore.add("hasExtensionAvps");
    methodsToIgnore.add("hashCode");
    
    methodsToIgnoreInRequest.add("getResultCode");

    methodsToIgnoreInAnswer.add("getDestinationHost");
    methodsToIgnoreInAnswer.add("getDestinationRealm");

    methodsToIgnoreInCEX.add("getDestinationHost");
    methodsToIgnoreInCEX.add("getDestinationRealm");
    methodsToIgnoreInCEX.add("getSessionId");
    
    methodsToIgnoreInDPX.add("getDestinationHost");
    methodsToIgnoreInDPX.add("getDestinationRealm");
    methodsToIgnoreInDPX.add("getSessionId");
    
    methodsToIgnoreInDWX.add("getDestinationHost");
    methodsToIgnoreInDWX.add("getDestinationRealm");
    methodsToIgnoreInDWX.add("getSessionId");
    
    methodsToIgnoreInRfACR.add("getLocationType");
    methodsToIgnoreInRfACR.add("setLocationType");
    methodsToIgnoreInRfACR.add("hasLocationType");
    
    Stack stack = new org.jdiameter.client.impl.StackImpl();
    stack.init( new MyConfiguration() );
    Message createMessage = stack.getSessionFactory().getNewRawSession().createMessage( 0, org.jdiameter.api.ApplicationId.createByAccAppId( 0L ));
    AvpSet rawAvp = createMessage.getAvps();
    rawAvp.addGroupedAvp(0).addAvp( 666, "pwning_more", true );
    byte[] dummyAvpBytes = rawAvp.getAvp(0).getRawData();

    //DiameterAvpFactory baseAvpFactory = new DiameterAvpFactoryImpl();
    //CreditControlAVPFactory ccaAvpFactory = new CreditControlAVPFactoryImpl(baseAvpFactory, stack);

    typeValues.put( String.class, "alexandre_and_bartosz_pwn_diameter" );
    typeValues.put( String[].class, new String[]{"alexandre_and_bartosz_pwn_diameter"} );

    typeValues.put( int.class, 2805 );
    typeValues.put( int[].class, new int[]{2805} );

    typeValues.put( Integer.class, 2805 );
    typeValues.put( Integer[].class, new Integer[]{2805} );

    typeValues.put( long.class, 28052009L );
    typeValues.put( long[].class, new long[]{28052009L} );

    typeValues.put( Long.class, 28052009L );
    typeValues.put( Long[].class, new Long[]{28052009L} );

    typeValues.put( short.class, 28 );
    typeValues.put( short[].class, new short[]{28} );

    typeValues.put( Short.class, 28 );
    typeValues.put( Short[].class, new Short[]{28} );

    typeValues.put( boolean.class, true );
    typeValues.put( boolean[].class, new boolean[]{true} );

    typeValues.put( Boolean.class, true );
    typeValues.put( Boolean[].class, new Boolean[]{true} );

    typeValues.put( Date.class, new Date(1243500000000L) );
    typeValues.put( Date[].class, new Date[]{new Date(1243500000000L)} );

    typeValues.put( DiameterIdentity.class, new DiameterIdentity("diameter.mobicents.org") );
    typeValues.put( DiameterIdentity[].class, new DiameterIdentity[]{new DiameterIdentity("diameter.mobicents.org")} );

    typeValues.put( DiameterURI.class, new DiameterURI("aaa://diameter.mobicents.org") );
    typeValues.put( DiameterURI[].class, new DiameterURI[]{new DiameterURI("aaa://diameter.mobicents.org")} );

    typeValues.put( Address.class, new Address(AddressType.ADDRESS_IP, "127.0.0.2".getBytes()) );
    typeValues.put( Address[].class, new Address[]{new Address(AddressType.ADDRESS_IP, "127.0.0.2".getBytes())} );

    typeValues.put( byte.class, (byte)'m');
    typeValues.put( byte[].class, "mobicents".getBytes() );

    typeValues.put( byte[][].class, new byte[][]{{(byte)'m'},{(byte)'m'}} );

    typeValues.put( Byte.class, Byte.valueOf((byte)'M') );
    typeValues.put( Byte[].class, new Byte[]{Byte.valueOf((byte)'M')} );

    typeValues.put( UserIdentityAvp.class, new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( UserIdentityAvp[].class, new UserIdentityAvpImpl[]{new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ShData.class, new UserDataObjectFactoryImpl(new ObjectFactory()).createShData() );
    typeValues.put( ShData[].class, new ShData[]{new UserDataObjectFactoryImpl(new ObjectFactory()).createShData()});

    typeValues.put( SupportedFeaturesAvp.class, new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SupportedFeaturesAvp[].class, new SupportedFeaturesAvpImpl[]{new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.cxdx.events.avp.SupportedFeaturesAvp.class, new org.mobicents.slee.resource.diameter.cxdx.events.avp.SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.cxdx.events.avp.SupportedFeaturesAvp[].class, new org.mobicents.slee.resource.diameter.cxdx.events.avp.SupportedFeaturesAvpImpl[]{new org.mobicents.slee.resource.diameter.cxdx.events.avp.SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ProxyInfoAvp.class, new ProxyInfoAvpImpl(Avp.PROXY_INFO, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( ProxyInfoAvp[].class, new ProxyInfoAvpImpl[]{new ProxyInfoAvpImpl(Avp.PROXY_INFO, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( VendorSpecificApplicationIdAvp.class, new VendorSpecificApplicationIdAvpImpl(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( VendorSpecificApplicationIdAvp[].class, new VendorSpecificApplicationIdAvpImpl[]{new VendorSpecificApplicationIdAvpImpl(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( ExperimentalResultAvp.class, new ExperimentalResultAvpImpl(Avp.EXPERIMENTAL_RESULT, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( ExperimentalResultAvp[].class, new ExperimentalResultAvpImpl[]{new ExperimentalResultAvpImpl(Avp.EXPERIMENTAL_RESULT, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( FailedAvp.class, new FailedAvpImpl(Avp.FAILED_AVP, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( FailedAvp[].class, new FailedAvpImpl[]{new FailedAvpImpl(Avp.FAILED_AVP, 0L, 0, 1, dummyAvpBytes)});

    // CCA RA

    typeValues.put( CostInformationAvp.class, new CostInformationAvpImpl(CreditControlAVPCodes.Cost_Information, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( CostInformationAvp[].class, new CostInformationAvpImpl[]{new CostInformationAvpImpl(CreditControlAVPCodes.Cost_Information, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( FinalUnitIndicationAvp.class, new FinalUnitIndicationAvpImpl(CreditControlAVPCodes.Final_Unit_Indication, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( FinalUnitIndicationAvp[].class, new FinalUnitIndicationAvpImpl[]{new FinalUnitIndicationAvpImpl(CreditControlAVPCodes.Final_Unit_Indication, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( GrantedServiceUnitAvp.class, new GrantedServiceUnitAvpImpl(CreditControlAVPCodes.Granted_Service_Unit, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( GrantedServiceUnitAvp[].class, new GrantedServiceUnitAvpImpl[]{new GrantedServiceUnitAvpImpl(CreditControlAVPCodes.Granted_Service_Unit, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( MultipleServicesCreditControlAvp.class, new MultipleServicesCreditControlAvpImpl(CreditControlAVPCodes.Multiple_Services_Credit_Control, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( MultipleServicesCreditControlAvp[].class, new MultipleServicesCreditControlAvpImpl[]{new MultipleServicesCreditControlAvpImpl(CreditControlAVPCodes.Multiple_Services_Credit_Control, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( RequestedServiceUnitAvp.class, new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( RequestedServiceUnitAvp[].class, new RequestedServiceUnitAvpImpl[]{new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( ServiceParameterInfoAvp.class, new ServiceParameterInfoAvpImpl(CreditControlAVPCodes.Service_Parameter_Info, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( ServiceParameterInfoAvp[].class, new ServiceParameterInfoAvpImpl[]{new ServiceParameterInfoAvpImpl(CreditControlAVPCodes.Service_Parameter_Info, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( SubscriptionIdAvp.class, new SubscriptionIdAvpImpl(CreditControlAVPCodes.Subscription_Id, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( SubscriptionIdAvp[].class, new SubscriptionIdAvpImpl[]{new SubscriptionIdAvpImpl(CreditControlAVPCodes.Subscription_Id, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( UserEquipmentInfoAvp.class, new UserEquipmentInfoAvpImpl(CreditControlAVPCodes.User_Equipment_Info, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( UserEquipmentInfoAvp[].class, new UserEquipmentInfoAvpImpl[]{new UserEquipmentInfoAvpImpl(CreditControlAVPCodes.User_Equipment_Info, 0L, 0, 1, dummyAvpBytes)});

    typeValues.put( UsedServiceUnitAvp.class, new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( UsedServiceUnitAvp[].class, new UsedServiceUnitAvpImpl[]{new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit, 0L, 0, 1, dummyAvpBytes)});

    // CCA AVP Factory
    
    typeValues.put( CcMoneyAvp.class, new CcMoneyAvpImpl(CreditControlAVPCodes.CC_Money, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( CcMoneyAvp[].class, new CcMoneyAvpImpl[]{new CcMoneyAvpImpl(CreditControlAVPCodes.CC_Money, 0L, 0, 1, dummyAvpBytes)});
    
    typeValues.put( GSUPoolReferenceAvp.class, new GSUPoolReferenceAvpImpl(CreditControlAVPCodes.G_S_U_Pool_Reference, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( GSUPoolReferenceAvp[].class, new GSUPoolReferenceAvpImpl[]{new GSUPoolReferenceAvpImpl(CreditControlAVPCodes.G_S_U_Pool_Reference, 0L, 0, 1, dummyAvpBytes)});
    
    typeValues.put( IPFilterRule.class, new IPFilterRule("permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established") );
    typeValues.put( IPFilterRule[].class, new IPFilterRule[]{new IPFilterRule("permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established")});
    
    typeValues.put( RedirectServerAvp.class, new RedirectServerAvpImpl(CreditControlAVPCodes.Redirect_Server, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( RedirectServerAvp[].class, new RedirectServerAvpImpl[]{new RedirectServerAvpImpl(CreditControlAVPCodes.Redirect_Server, 0L, 0, 1, dummyAvpBytes)});
    
    typeValues.put( UnitValueAvp.class, new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value, 0L, 0, 1, dummyAvpBytes) );
    typeValues.put( UnitValueAvp[].class, new UnitValueAvpImpl[]{new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value, 0L, 0, 1, dummyAvpBytes)});
    
    // Ro AVP Factory
    
    typeValues.put( ApplicationServerInformation.class, new ApplicationServerInformationImpl(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ApplicationServerInformation[].class, new ApplicationServerInformationImpl[]{new ApplicationServerInformationImpl(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( EventType.class, new EventTypeImpl(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( EventType[].class, new EventTypeImpl[]{new EventTypeImpl(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( InterOperatorIdentifier.class, new InterOperatorIdentifierImpl(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( InterOperatorIdentifier[].class, new InterOperatorIdentifierImpl[]{new InterOperatorIdentifierImpl(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( MessageBody.class, new MessageBodyImpl(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MessageBody[].class, new MessageBodyImpl[]{new MessageBodyImpl(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( SdpMediaComponent.class, new SdpMediaComponentImpl(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SdpMediaComponent[].class, new SdpMediaComponentImpl[]{new SdpMediaComponentImpl(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( TimeStamps.class, new TimeStampsImpl(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( TimeStamps[].class, new TimeStampsImpl[]{new TimeStampsImpl(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( TrunkGroupId.class, new TrunkGroupIdImpl(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( TrunkGroupId[].class, new TrunkGroupIdImpl[]{new TrunkGroupIdImpl(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( LcsClientName.class, new LcsClientNameImpl(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( LcsClientName[].class, new LcsClientNameImpl[]{new LcsClientNameImpl(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( LcsRequestorId.class, new LcsRequestorIdImpl(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( LcsRequestorId[].class, new LcsRequestorIdImpl[]{new LcsRequestorIdImpl(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( LcsClientId.class, new LcsClientIdImpl(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( LcsClientId[].class, new LcsClientIdImpl[]{new LcsClientIdImpl(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( LocationType.class, new LocationTypeImpl(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( LocationType[].class, new LocationTypeImpl[]{new LocationTypeImpl(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( AdditionalContentInformation.class, new AdditionalContentInformationImpl(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( AdditionalContentInformation[].class, new AdditionalContentInformationImpl[]{new AdditionalContentInformationImpl(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( MessageClass.class, new MessageClassImpl(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MessageClass[].class, new MessageClassImpl[]{new MessageClassImpl(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( MmContentType.class, new MmContentTypeImpl(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MmContentType[].class, new MmContentTypeImpl[]{new MmContentTypeImpl(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( OriginatorAddress.class, new OriginatorAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( OriginatorAddress[].class, new OriginatorAddressImpl[]{new OriginatorAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( RecipientAddress.class, new RecipientAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( RecipientAddress[].class, new RecipientAddressImpl[]{new RecipientAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( AddressDomain.class, new AddressDomainImpl(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( AddressDomain[].class, new AddressDomainImpl[]{new AddressDomainImpl(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( TalkBurstExchange.class, new TalkBurstExchangeImpl(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( TalkBurstExchange[].class, new TalkBurstExchangeImpl[]{new TalkBurstExchangeImpl(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( PsFurnishChargingInformation.class, new PsFurnishChargingInformationImpl(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( PsFurnishChargingInformation[].class, new PsFurnishChargingInformationImpl[]{new PsFurnishChargingInformationImpl(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( ImsInformation.class, new ImsInformationImpl(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ImsInformation[].class, new ImsInformationImpl[]{new ImsInformationImpl(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( LcsInformation.class, new LcsInformationImpl(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( LcsInformation[].class, new LcsInformationImpl[]{new LcsInformationImpl(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( MbmsInformation.class, new MbmsInformationImpl(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MbmsInformation[].class, new MbmsInformationImpl[]{new MbmsInformationImpl(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( MmsInformation.class, new MmsInformationImpl(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MmsInformation[].class, new MmsInformationImpl[]{new MmsInformationImpl(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( PocInformation.class, new PocInformationImpl(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( PocInformation[].class, new PocInformationImpl[]{new PocInformationImpl(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( PsInformation.class, new PsInformationImpl(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( PsInformation[].class, new PsInformationImpl[]{new PsInformationImpl(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( WlanInformation.class, new WlanInformationImpl(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( WlanInformation[].class, new WlanInformationImpl[]{new WlanInformationImpl(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( WlanRadioContainer.class, new WlanRadioContainerImpl(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( WlanRadioContainer[].class, new WlanRadioContainerImpl[]{new WlanRadioContainerImpl(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ServerCapabilities.class, new ServerCapabilitiesImpl(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ServerCapabilities[].class, new ServerCapabilitiesImpl[]{new ServerCapabilitiesImpl(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ServiceInformation.class, new ServiceInformationImpl(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ServiceInformation[].class, new ServiceInformationImpl[]{new ServiceInformationImpl(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    // Rf AVP Factory

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ApplicationServerInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.ApplicationServerInformationImpl(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ApplicationServerInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.ApplicationServerInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.ApplicationServerInformationImpl(DiameterRoAvpCodes.APPLICATION_SERVER_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.EventType.class, new org.mobicents.slee.resource.diameter.rf.events.avp.EventTypeImpl(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.EventType[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.EventTypeImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.EventTypeImpl(DiameterRoAvpCodes.EVENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.InterOperatorIdentifier.class, new org.mobicents.slee.resource.diameter.rf.events.avp.InterOperatorIdentifierImpl(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.InterOperatorIdentifier[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.InterOperatorIdentifierImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.InterOperatorIdentifierImpl(DiameterRoAvpCodes.INTER_OPERATOR_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MessageBody.class, new org.mobicents.slee.resource.diameter.rf.events.avp.MessageBodyImpl(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MessageBody[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.MessageBodyImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.MessageBodyImpl(DiameterRoAvpCodes.MESSAGE_BODY, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.SdpMediaComponent.class, new org.mobicents.slee.resource.diameter.rf.events.avp.SdpMediaComponentImpl(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.SdpMediaComponent[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.SdpMediaComponentImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.SdpMediaComponentImpl(DiameterRoAvpCodes.SDP_MEDIA_COMPONENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TimeStamps.class, new org.mobicents.slee.resource.diameter.rf.events.avp.TimeStampsImpl(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TimeStamps[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.TimeStampsImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.TimeStampsImpl(DiameterRoAvpCodes.TIME_STAMPS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TrunkGroupId.class, new org.mobicents.slee.resource.diameter.rf.events.avp.TrunkGroupIdImpl(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TrunkGroupId[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.TrunkGroupIdImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.TrunkGroupIdImpl(DiameterRoAvpCodes.TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsClientName.class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientNameImpl(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsClientName[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientNameImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientNameImpl(DiameterRoAvpCodes.LCS_CLIENT_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsRequestorId.class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsRequestorIdImpl(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsRequestorId[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsRequestorIdImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.LcsRequestorIdImpl(DiameterRoAvpCodes.LCS_REQUESTOR_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsClientId.class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientIdImpl(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsClientId[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientIdImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.LcsClientIdImpl(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LocationType.class, new org.mobicents.slee.resource.diameter.rf.events.avp.LocationTypeImpl(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LocationType[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.LocationTypeImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.LocationTypeImpl(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.AdditionalContentInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.AdditionalContentInformationImpl(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.AdditionalContentInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.AdditionalContentInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.AdditionalContentInformationImpl(DiameterRoAvpCodes.ADDITIONAL_CONTENT_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MessageClass.class, new org.mobicents.slee.resource.diameter.rf.events.avp.MessageClassImpl(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MessageClass[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.MessageClassImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.MessageClassImpl(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MmContentType.class, new org.mobicents.slee.resource.diameter.rf.events.avp.MmContentTypeImpl(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MmContentType[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.MmContentTypeImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.MmContentTypeImpl(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.OriginatorAddress.class, new org.mobicents.slee.resource.diameter.rf.events.avp.OriginatorAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.OriginatorAddress[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.OriginatorAddressImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.OriginatorAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.RecipientAddress.class, new org.mobicents.slee.resource.diameter.rf.events.avp.RecipientAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.RecipientAddress[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.RecipientAddressImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.RecipientAddressImpl(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.AddressDomain.class, new org.mobicents.slee.resource.diameter.rf.events.avp.AddressDomainImpl(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.AddressDomain[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.AddressDomainImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.AddressDomainImpl(DiameterRoAvpCodes.ADDRESS_DOMAIN, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TalkBurstExchange.class, new org.mobicents.slee.resource.diameter.rf.events.avp.TalkBurstExchangeImpl(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.TalkBurstExchange[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.TalkBurstExchangeImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.TalkBurstExchangeImpl(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PsFurnishChargingInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.PsFurnishChargingInformationImpl(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PsFurnishChargingInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.PsFurnishChargingInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.PsFurnishChargingInformationImpl(DiameterRoAvpCodes.PS_FURNISH_CHARGING_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ImsInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.ImsInformationImpl(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ImsInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.ImsInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.ImsInformationImpl(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsInformationImpl(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.LcsInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.LcsInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.LcsInformationImpl(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MbmsInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.MbmsInformationImpl(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MbmsInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.MbmsInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.MbmsInformationImpl(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MmsInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.MmsInformationImpl(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.MmsInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.MmsInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.MmsInformationImpl(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PocInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.PocInformationImpl(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PocInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.PocInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.PocInformationImpl(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PsInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.PsInformationImpl(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.PsInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.PsInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.PsInformationImpl(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});
    
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.WlanInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.WlanInformationImpl(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.WlanInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.WlanInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.WlanInformationImpl(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.WlanRadioContainer.class, new org.mobicents.slee.resource.diameter.rf.events.avp.WlanRadioContainerImpl(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.WlanRadioContainer[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.WlanRadioContainerImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.WlanRadioContainerImpl(DiameterRoAvpCodes.WLAN_RADIO_CONTAINER, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ServerCapabilities.class, new org.mobicents.slee.resource.diameter.rf.events.avp.ServerCapabilitiesImpl(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ServerCapabilities[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.ServerCapabilitiesImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.ServerCapabilitiesImpl(DiameterRoAvpCodes.SERVER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ServiceInformation.class, new org.mobicents.slee.resource.diameter.rf.events.avp.ServiceInformationImpl(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.rf.events.avp.ServiceInformation[].class, new org.mobicents.slee.resource.diameter.rf.events.avp.ServiceInformationImpl[]{new org.mobicents.slee.resource.diameter.rf.events.avp.ServiceInformationImpl(DiameterRoAvpCodes.SERVICE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    // Cx/Dx AVP Factory
    
    typeValues.put( SIPAuthDataItem.class, new SIPAuthDataItemImpl(DiameterCxDxAvpCodes.SIP_AUTH_DATA_ITEM, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SIPAuthDataItem[].class, new SIPAuthDataItemImpl[]{new SIPAuthDataItemImpl(DiameterCxDxAvpCodes.SIP_AUTH_DATA_ITEM, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ChargingInformation.class, new ChargingInformationImpl(DiameterCxDxAvpCodes.CHARGING_INFORMATION, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ChargingInformation[].class, new ChargingInformationImpl[]{new ChargingInformationImpl(DiameterCxDxAvpCodes.CHARGING_INFORMATION, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( AssociatedIdentities.class, new AssociatedIdentitiesImpl(DiameterCxDxAvpCodes.ASSOCIATED_IDENTITIES, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( AssociatedIdentities[].class, new AssociatedIdentitiesImpl[]{new AssociatedIdentitiesImpl(DiameterCxDxAvpCodes.ASSOCIATED_IDENTITIES, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( AssociatedRegisteredIdentities.class, new AssociatedRegisteredIdentitiesImpl(DiameterCxDxAvpCodes.ASSOCIATED_REGISTERED_IDENTITIES, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( AssociatedRegisteredIdentities[].class, new AssociatedRegisteredIdentitiesImpl[]{new AssociatedRegisteredIdentitiesImpl(DiameterCxDxAvpCodes.ASSOCIATED_REGISTERED_IDENTITIES, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( DeregistrationReason.class, new DeregistrationReasonImpl(DiameterCxDxAvpCodes.DEREGISTRATION_REASON, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( DeregistrationReason[].class, new DeregistrationReasonImpl[]{new DeregistrationReasonImpl(DiameterCxDxAvpCodes.DEREGISTRATION_REASON, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( SCSCFRestorationInfo.class, new SCSCFRestorationInfoImpl(DiameterCxDxAvpCodes.SCSCF_RESTORATION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SCSCFRestorationInfo[].class, new SCSCFRestorationInfoImpl[]{new SCSCFRestorationInfoImpl(DiameterCxDxAvpCodes.SCSCF_RESTORATION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( SubscriptionInfo.class, new SubscriptionInfoImpl(DiameterCxDxAvpCodes.SUBSCRIPTION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SubscriptionInfo[].class, new SubscriptionInfoImpl[]{new SubscriptionInfoImpl(DiameterCxDxAvpCodes.SUBSCRIPTION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( SIPDigestAuthenticate.class, new SIPDigestAuthenticateImpl(DiameterCxDxAvpCodes.SIP_DIGEST_AUTHENTICATE, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SIPDigestAuthenticate[].class, new SIPDigestAuthenticateImpl[]{new SIPDigestAuthenticateImpl(DiameterCxDxAvpCodes.SIP_DIGEST_AUTHENTICATE, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( RestorationInfo.class, new RestorationInfoImpl(DiameterCxDxAvpCodes.RESTORATION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( RestorationInfo[].class, new RestorationInfoImpl[]{new RestorationInfoImpl(DiameterCxDxAvpCodes.RESTORATION_INFO, DiameterCxDxAvpCodes.CXDX_VENDOR_ID, 0, 1, dummyAvpBytes)});

    // Gx AVP Factory
    
    typeValues.put( TFTPacketFilterInformation.class, new TFTPacketFilterInformationImpl(DiameterGxAvpCodes.TFT_PACKET_FILTER_INFORMATION, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( TFTPacketFilterInformation[].class, new TFTPacketFilterInformationImpl[]{new TFTPacketFilterInformationImpl(DiameterGxAvpCodes.TFT_PACKET_FILTER_INFORMATION, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ChargingRuleRemove.class, new ChargingRuleRemoveImpl(DiameterGxAvpCodes.CHARGING_RULE_REMOVE, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ChargingRuleRemove[].class, new ChargingRuleRemoveImpl[]{new ChargingRuleRemoveImpl(DiameterGxAvpCodes.CHARGING_RULE_REMOVE, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( ChargingRuleInstall.class, new ChargingRuleInstallImpl(DiameterGxAvpCodes.CHARGING_RULE_INSTALL, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( ChargingRuleInstall[].class, new ChargingRuleInstallImpl[]{new ChargingRuleInstallImpl(DiameterGxAvpCodes.CHARGING_RULE_INSTALL, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( net.java.slee.resource.diameter.gx.events.avp.ChargingInformation.class, new org.mobicents.slee.resource.diameter.gx.events.avp.ChargingInformationImpl(DiameterGxAvpCodes.CHARGING_INFORMATION, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( net.java.slee.resource.diameter.gx.events.avp.ChargingInformation[].class, new org.mobicents.slee.resource.diameter.gx.events.avp.ChargingInformationImpl[]{new org.mobicents.slee.resource.diameter.gx.events.avp.ChargingInformationImpl(DiameterGxAvpCodes.CHARGING_INFORMATION, DiameterGxAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( DiameterAvp.class, new DiameterAvpImpl(0, 0, 0, 1, dummyAvpBytes, null) );
    typeValues.put( DiameterAvp[].class, new DiameterAvpImpl[]{new DiameterAvpImpl(0, 0, 0, 1, dummyAvpBytes, null)});

    // Gq AVP Factory

    typeValues.put( BindingInformation.class, new BindingInformationImpl(DiameterGqAvpCodes.ETSI_BINDING_INFORMATION, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( BindingInformation[].class, new BindingInformationImpl[]{new BindingInformationImpl(DiameterGqAvpCodes.ETSI_BINDING_INFORMATION, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( BindingInputList.class, new BindingInputListImpl(DiameterGqAvpCodes.ETSI_BINDING_INPUT_LIST, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( BindingInputList[].class, new BindingInputListImpl[]{new BindingInputListImpl(DiameterGqAvpCodes.ETSI_BINDING_INPUT_LIST, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( BindingOutputList.class, new BindingOutputListImpl(DiameterGqAvpCodes.ETSI_BINDING_OUTPUT_LIST, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( BindingOutputList[].class, new BindingOutputListImpl[]{new BindingOutputListImpl(DiameterGqAvpCodes.ETSI_BINDING_OUTPUT_LIST, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( FlowGrouping.class, new FlowGroupingImpl(DiameterGqAvpCodes.TGPP_FLOW_GROUPING, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( FlowGrouping[].class, new FlowGroupingImpl[]{new FlowGroupingImpl(DiameterGqAvpCodes.TGPP_FLOW_GROUPING, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( Flows.class, new FlowsImpl(DiameterGqAvpCodes.TGPP_FLOWS, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( Flows[].class, new FlowsImpl[]{new FlowsImpl(DiameterGqAvpCodes.TGPP_FLOWS, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( GloballyUniqueAddress.class, new GloballyUniqueAddressImpl(DiameterGqAvpCodes.ETSI_GLOBALLY_UNIQUE_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( GloballyUniqueAddress[].class, new GloballyUniqueAddressImpl[]{new GloballyUniqueAddressImpl(DiameterGqAvpCodes.ETSI_GLOBALLY_UNIQUE_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( MediaComponentDescription.class, new MediaComponentDescriptionImpl(DiameterGqAvpCodes.TGPP_MEDIA_COMPONENT_DESCRIPTION, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MediaComponentDescription[].class, new MediaComponentDescriptionImpl[]{new MediaComponentDescriptionImpl(DiameterGqAvpCodes.TGPP_MEDIA_COMPONENT_DESCRIPTION, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( MediaSubComponent.class, new MediaSubComponentImpl(DiameterGqAvpCodes.TGPP_MEDIA_SUB_COMPONENT, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( MediaSubComponent[].class, new MediaSubComponentImpl[]{new MediaSubComponentImpl(DiameterGqAvpCodes.TGPP_MEDIA_SUB_COMPONENT, DiameterGqAvpCodes.TGPP_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( V4TransportAddress.class, new V4TransportAddressImpl(DiameterGqAvpCodes.ETSI_V4_TRANSPORT_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( V4TransportAddress[].class, new V4TransportAddressImpl[]{new V4TransportAddressImpl(DiameterGqAvpCodes.ETSI_V4_TRANSPORT_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( V6TransportAddress.class, new V6TransportAddressImpl(DiameterGqAvpCodes.ETSI_V6_TRANSPORT_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( V6TransportAddress[].class, new V6TransportAddressImpl[]{new V6TransportAddressImpl(DiameterGqAvpCodes.ETSI_V6_TRANSPORT_ADDRESS, DiameterGqAvpCodes.ETSI_VENDOR_ID, 0, 1, dummyAvpBytes)});

  }

  public static Object getValueFromEnumerated(Class clazz) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    Class realClazz = clazz.isArray() ? clazz.getComponentType() : clazz;

    for(Class interfaze : realClazz.getInterfaces())
    {
      if(interfaze == Enumerated.class)
      {
        Object object = null;

        for(Field f : realClazz.getFields()) {
          if(f.getType() == realClazz) {
            object = f.get(null);
            break;
          }
        }

        if(object != null && clazz.isArray())
        {
          Object array = Array.newInstance( realClazz, 1 );
          Array.set( array, 0, object);
          return array;
        }
        return object;
      }
    }

    return null;
  }

  public static Object getValueFromClass(Class clazz) {
    return typeValues.get(clazz);
  }

  private static void clearAVPsInMessage(DiameterMessage message) {
    // Clear all AVP's...
    AvpSet set = ((DiameterMessageImpl)message).getGenericData().getAvps();
    while(set.size() > 0)
      set.removeAvpByIndex(0);    
  }

  public static int testMethods(DiameterMessage message, Class interfaze) throws Exception
  {
    System.out.println(":::::::: Testing accessors for Class " + message.getClass().getSimpleName() + " ::::::::");

    int nFailures = 0;

    for(Method m : interfaze.getMethods())
    {
      clearAVPsInMessage(message);

      int commandCode = message.getCommand().getCode();

      String methodName = m.getName();
      
      if(AvpAssistant.methodsToIgnore.contains( methodName )) {
        continue;
      }
      else if(message.getHeader().isRequest() && AvpAssistant.methodsToIgnoreInRequest.contains( methodName )) {
        continue;
      }
      else if(!message.getHeader().isRequest() && AvpAssistant.methodsToIgnoreInAnswer.contains( methodName )) {
        continue;
      }
      else if(commandCode == CapabilitiesExchangeRequest.commandCode && AvpAssistant.methodsToIgnoreInCEX.contains( methodName )) {
        continue;
      }
      else if(commandCode == DeviceWatchdogRequest.commandCode && AvpAssistant.methodsToIgnoreInDWX.contains( methodName )) {
        continue;
      }
      else if(commandCode == DisconnectPeerRequest.commandCode && AvpAssistant.methodsToIgnoreInDPX.contains( methodName )) {
        continue;
      }
      else if(commandCode == AccountingRequest.commandCode && AvpAssistant.methodsToIgnoreInRfACR.contains( methodName )) {
        continue;
      }
      else if(methodName.startsWith( "get" ))
      {
        Class avpType = m.getReturnType();

        Object toGo = AvpAssistant.getValueFromEnumerated(avpType);

        if(toGo == null)
          toGo = AvpAssistant.getValueFromClass(avpType);

        if(toGo != null)
        {
          Method hasser = null;

          try {
            hasser = interfaze.getMethod( getSingularMethodName(methodName.replaceFirst("get", "has")) );

            Object hasAvpBeforeSet = hasser.invoke(message);

            Assert.assertFalse( "Message already has value before setting for " + methodName.replaceAll("get","") + "... aborting", (Boolean)hasAvpBeforeSet);
          }
          catch (NoSuchMethodException e) {
            // skip it...
          }

          Method setter = interfaze.getMethod( methodName.replaceFirst("g", "s"), avpType );

          //System.out.println("Setting value " + setter.getName() +"(" + toGo.toString() +")");

          setter.invoke( message, toGo );

          if(hasser != null) {
            Object hasAvpAfterSet = hasser.invoke(message);

            Assert.assertTrue( "Message does not has value after setting for " + methodName.replaceAll("get","") + "... aborting", (Boolean)hasAvpAfterSet);
          }

          //System.out.println("Current message: \r\n" + snr);

          Object obtained = m.invoke( message );
          //System.out.println("Got value " + obtained.toString());
          boolean passed = false;
          try {
            if(avpType == byte[].class) {
              passed = Arrays.equals( (byte[])toGo, (byte[])obtained );
            }            
            else if(avpType == long[].class) {
              passed = Arrays.equals( (long[])toGo, (long[])obtained );
            }
            else {
              passed = (avpType.isArray() ? Arrays.equals( (Object[])toGo, (Object[])obtained ) : obtained.equals(toGo));
            }
          }
          catch (Exception e) {
            e.printStackTrace();
            // ignore... we fail!
          }

          nFailures = passed ? nFailures : nFailures+1;
          System.out.println("[" + (passed ? "PASSED" : "FAILED") + "] " + methodName.replace("get", "") + " with param of type '"+ avpType.getName() + "' " + (hasser != null ? " WITH has" : " WITHOUT has"));
        }
        else {
          System.out.println("[??????] Unable to test " + methodName.replace("get", "") + " with param of type '"+ avpType.getName() + "'.");
          Assert.fail("Missing AVP Implementation class to test " + methodName);
        }
      }
    }

    return nFailures;
  }

  public static void testSetters(Object object) throws Exception
  {
    for(Method m : object.getClass().getMethods())
    {
      if(!methodsToIgnore.contains(m.getName()) && m.getName().startsWith("set"))
      {
        //System.out.println("==> " + m.getName() + " <==");
        Class[] pTypes = m.getParameterTypes();

        Object[] params = new Object[pTypes.length];

        for(int i = 0; i < params.length; i++)
        {
          params[i] = AvpAssistant.getValueFromEnumerated(pTypes[i]);

          if(params[i] == null) {
            params[i] = getValueFromClass(pTypes[i]);
          }

          if(params[i] == null) {
            System.out.println("Could not find value for " + pTypes[i]);
            throw new NullPointerException("Could not find value for " + pTypes[i]);
          }
        }

        m.invoke(object, params);
      }
    }
  }

  public static void testGetters(Object object) throws Exception
  {
    for(Method m : object.getClass().getMethods())
    {
      if(!methodsToIgnore.contains(m.getName()) && m.getName().startsWith("get"))
      {
        //System.out.println("==> " + m.getName() + " <==");
        Class rType = m.getReturnType();

        if(rType.isArray() && rType != byte[].class) {
          continue;
        }
        
        Object expected = AvpAssistant.getValueFromEnumerated(rType);

        if(expected == null) {
          expected = getValueFromClass(rType);
        }

        if(expected == null) {
          System.out.println("Could not find value for " + rType + " in method " + m.getName());
          throw new NullPointerException("Could not find value for " + rType + " in method " + m.getName());
        }

        Object obtained = m.invoke(object);
        
        boolean passed;
        if(rType == byte[].class) {
          passed = Arrays.equals( (byte[])expected, (byte[])obtained );
        }            
        else if(rType == long[].class) {
          
          passed = Arrays.equals( (long[])expected, (long[])obtained );
        }
        else if(rType.isArray()){
          passed =  Arrays.equals( (Object[])expected, (Object[])obtained );
        }
        else {
          passed = obtained.equals( expected );
        }
        
        if(!passed)
          Assert.fail("Getter " + m.getName() + " did not return expected value.");
      }
    }
  }

  
  /*
  private static Object[] getSingleObjectAndArrauObject(Method m) throws Exception
  {
    Class rType = m.getReturnType();

    Object returnObject = AvpAssistant.getValueFromEnumerated(rType);

    if(returnObject == null) {
      returnObject = getValueFromClass(rType);
    }

    if(returnObject == null) {
      System.out.println("Could not find value for " + rType);
      throw new NullPointerException("Could not find value for " + rType);
    }
    
    
    Object singleReturnObject = AvpAssistant.getValueFromEnumerated(rType.getComponentType());

    if(singleReturnObject == null) {
      singleReturnObject = getValueFromClass(rType);
    }

    if(singleReturnObject == null) {
      System.out.println("Could not find value for " + rType);
      throw new NullPointerException("Could not find value for " + rType);
    }

    Object newArray = Array.newInstance(rType, ((Object[])returnObject).length+1);
    
    int i = 0;
    for(Object arrayObject : ((Object[])returnObject)) {
      Array.set(newArray, i++, arrayObject );
    }
    
    Array.set(newArray, ((Object[])returnObject).length, singleReturnObject );
    
    return (Object[]) newArray;
  }
  */
  
  
  public static void testHassers(Object object, boolean expected) throws Exception
  {
    for(Method m : object.getClass().getMethods())
    {
      if(!methodsToIgnore.contains(m.getName()) && m.getName().startsWith("has"))
      {
        //System.out.println("==> " + m.getName() + " <==");

        Object obtained = m.invoke(object);
        
        Assert.assertEquals("Hasser " + m.getName() + " did not return expected value.", expected, obtained);
      }
    }
  }

  private static String getSingularMethodName(String pluralMethodName) {
    if(pluralMethodName.endsWith("eses"))
      return pluralMethodName.substring(0, pluralMethodName.length()-2);
    else if(pluralMethodName.endsWith("s"))
      return pluralMethodName.substring(0, pluralMethodName.length()-1);
    else
      return pluralMethodName;
  }

  public static class MyConfiguration extends EmptyConfiguration 
  {
    public MyConfiguration() 
    {
      super();

      add(Assembler, Assembler.defValue());
      add(OwnDiameterURI, "aaa://localhost");
      add(OwnRealm, "dummy");
      add(OwnVendorID, 193L);
      // Set Ericsson SDK feature
      //add(UseUriAsFqdn, true);
      // Set Common Applications
      add(org.jdiameter.client.impl.helpers.Parameters.ApplicationId,
          // AppId 1
          getInstance().
          add(VendorId,   193L).
          add(AuthApplId, 0L).
          add(AcctApplId, 19302L)
      );
      // Set peer table
      add(PeerTable,
          // Peer 1
          getInstance().
          add(PeerRating, 1).
          add(PeerName, "aaa://localhost"));
      // Set realm table
      add(RealmTable, 
          // Realm 1
          getInstance().add(RealmEntry, getInstance().
              add(RealmName, realmName).
              add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).
              add(RealmHosts, clientHost + ", " + serverHost).
              add(RealmLocalAction, "LOCAL").
              add(RealmEntryIsDynamic, false).
              add(RealmEntryExpTime, 1000L)));
    }
  }

}
