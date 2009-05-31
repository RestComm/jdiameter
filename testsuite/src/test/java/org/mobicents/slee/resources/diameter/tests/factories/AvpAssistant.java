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
package org.mobicents.slee.resources.diameter.tests.factories;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.AddressType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.jdiameter.api.ApplicationId;
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
import org.mobicents.slee.resource.diameter.cca.events.avp.CostInformationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 *
 * Aux Class for assisting in testing AVP setters/getters
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpAssistant {

  private static HashMap<Class, Object> typeValues = new HashMap<Class, Object>(); 

  public static Collection methodsToIgnore = new ArrayList<String>();

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
    methodsToIgnore.add("getHeader");

    Stack stack = new org.jdiameter.client.impl.StackImpl();
    stack.init( new MyConfiguration() );
    Message createMessage = stack.getSessionFactory().getNewRawSession().createMessage( 0, ApplicationId.createByAccAppId( 0 ));
    AvpSet rawAvp = createMessage.getAvps();
    rawAvp.addGroupedAvp(0).addAvp( 666, "pwning_more", true );
    byte[] dummyAvpBytes = rawAvp.getAvp(0).getRawData();

    typeValues.put( String.class, "alexandre_and_bartosz_pwn_diameter" );
    typeValues.put( String.class, "alexandre_and_bartosz_pwn_diameter" );

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
    typeValues.put( byte[].class, new byte[]{(byte)'m'} );

    typeValues.put( byte[][].class, new byte[][]{{(byte)'m'},{(byte)'m'}} );

    typeValues.put( Byte.class, Byte.valueOf((byte)'M') );
    typeValues.put( Byte[].class, new Byte[]{Byte.valueOf((byte)'M')} );

    typeValues.put( UserIdentityAvp.class, new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( UserIdentityAvp[].class, new UserIdentityAvpImpl[]{new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes)});

    typeValues.put( SupportedFeaturesAvp.class, new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes) );
    typeValues.put( SupportedFeaturesAvp[].class, new SupportedFeaturesAvpImpl[]{new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, 0, 1, dummyAvpBytes)});

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

    typeValues.put( DiameterAvp.class, new DiameterAvpImpl(0, 0, 0, 1, dummyAvpBytes, null) );
    typeValues.put( DiameterAvp[].class, new DiameterAvpImpl[]{new DiameterAvpImpl(0, 0, 0, 1, dummyAvpBytes, null)});

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
      //System.out.println("-------> " + m.getName());

      clearAVPsInMessage(message);

      if(AvpAssistant.methodsToIgnore.contains( m.getName() )) {
        continue;
      }

      else if(m.getName().startsWith( "get" ))
      {
        Class avpType = m.getReturnType();

        Object toGo = AvpAssistant.getValueFromEnumerated(avpType);

        if(toGo == null)
          toGo = AvpAssistant.getValueFromClass(avpType);

        if(toGo != null)
        {
          Method hasser = null;

          try {
            hasser = interfaze.getMethod( getSingularMethodName(m.getName().replaceFirst("get", "has")) );

            Object hasAvpBeforeSet = hasser.invoke(message);

            Assert.assertFalse( "Message already has value before setting for " + m.getName().replaceAll("get","") + "... aborting", (Boolean)hasAvpBeforeSet);
          }
          catch (NoSuchMethodException e) {
            // skip it...
          }

          Method setter = interfaze.getMethod( m.getName().replaceFirst("g", "s"), avpType );

          //System.out.println("Setting value " + setter.getName() +"(" + toGo.toString() +")");

          setter.invoke( message, toGo );

          if(hasser != null) {
            Object hasAvpAfterSet = hasser.invoke(message);

            Assert.assertTrue( "Message does not has value after setting for " + m.getName().replaceAll("get","") + "... aborting", (Boolean)hasAvpAfterSet);
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
          System.out.println("[" + (passed ? "PASSED" : "FAILED") + "] " + m.getName().replace("get", "") + " with param of type '"+ avpType.getName() + "' " + (hasser != null ? " WITH has" : " WITHOUT has"));
        }
        else {
          System.out.println("[??????] Unable to test " + m.getName().replace("get", "") + " with param of type '"+ avpType.getName() + "'.");
          Assert.fail("Missing AVP Implementation class to test " + m.getName());
        }
      }
    }

    return nFailures;
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
          getInstance().
          add(RealmEntry, "dummy" + ":" + "dummy" + "," + "dummy")
      );
    }
  }

}
