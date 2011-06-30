/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.mobicents.slee.resources.diameter.tests.base.avp;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.gx.events.GxCreditControlAnswer;
import net.java.slee.resource.diameter.gx.events.GxCreditControlRequest;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpRepresentation;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.impl.StackImpl;
import org.jdiameter.common.impl.validation.DictionaryImpl;
import org.junit.Test;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.gx.GxMessageFactoryImpl;
import org.mobicents.slee.resources.diameter.tests.factories.CCAFactoriesTest.MyConfiguration;
import org.mobicents.slee.resources.diameter.tests.factories.GxFactoriesTest;

/**
 * Specific tests for Issue #2697: Inconsistent treatment of AVP mandatory field
 * http://code.google.com/p/mobicents/issues/detail?id=2697
 * 
 * TODO: Generalize to run in all messages/avps
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AvpFlags {

  private final static long TGPP_VENDOR_ID = 10415L;

  private static StackImpl stack;
  
  private static DiameterAvpFactoryImpl baseAvpFactory;
  private static DiameterMessageFactoryImpl baseMessageFactory;

  private static GxMessageFactoryImpl gxMessageFactory;
  
  private static Dictionary validator = DictionaryImpl.INSTANCE;
  
  static {
    stack = new org.jdiameter.client.impl.StackImpl();
    try {
      stack.init(new MyConfiguration());
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to initialize the stack.");
    }

    baseAvpFactory = new DiameterAvpFactoryImpl();
    baseMessageFactory = new DiameterMessageFactoryImpl(stack);

    try {
      gxMessageFactory = new GxMessageFactoryImpl(baseMessageFactory, stack.getSessionFactory().getNewSession().getSessionId(), stack);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      DictionaryImpl.INSTANCE.configure(GxFactoriesTest.class.getClassLoader().getResourceAsStream("dictionary.xml"));
    }
    catch (Exception e) {
      throw new RuntimeException("Failed to parse dictionary file.");
    }
  }

  @Test
  public void testCorrectFlagsSupportedFeatures() {
    List<DiameterAvp> avps = new ArrayList<DiameterAvp>();
    DiameterAvp avpFeatureListID = AvpUtilities.createAvp(Avp.FEATURE_LIST_ID, TGPP_VENDOR_ID, 1);
    avps.add(AvpUtilities.createAvp(Avp.SUPPORTED_FEATURES, TGPP_VENDOR_ID, new DiameterAvp[]{avpFeatureListID}));
    
    GxCreditControlRequest ccr = gxMessageFactory.createGxCreditControlRequest();
    GxCreditControlAnswer cca = gxMessageFactory.createGxCreditControlAnswer(ccr);
    cca.setExtensionAvps(avps.toArray(avps.toArray(new DiameterAvp[avps.size()])));

    Message msg = ((DiameterMessageImpl)cca).getGenericData();

    //AvpUtilities.addAvp(msg, Avp.SUPPORTED_FEATURES, TGPP_VENDOR_ID, msg.getAvps(), new DiameterAvp[]{avpFeatureListID});
    
    // Attribute Name    AVP Code  Section Value Type  Must  May Should not  Must not  May Encr.
    // Supported-Features  628      6.3.29  Grouped    V M    -     -            -        No
    
    ArrayList<String> failures = checkAvpFlags(msg.getAvps());
    
    if(failures.size() > 0) {
      System.err.println("The following AVPs flags have failed to check against dictionary:");
      for(String failure : failures) {
        System.err.println(failure);
      }
      Assert.fail(failures.toString());
    }
  }

  @Test
  public void testCorrectFlagsBaseExample() throws NoSuchAvpException {
    List<DiameterAvp> avps = new ArrayList<DiameterAvp>();

    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.SESSION_ID, "accesspoint7.acme.com;1876543210;523;mobile@200.1.1.88".getBytes()));

    DiameterAvp avpVendorId = baseAvpFactory.createAvp(DiameterAvpCodes.VENDOR_ID, 193);
    DiameterAvp avpAcctApplicationId = baseAvpFactory.createAvp(DiameterAvpCodes.ACCT_APPLICATION_ID, 19302);

    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, new DiameterAvp[] { avpVendorId, avpAcctApplicationId }));

    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.ORIGIN_HOST, "127.0.0.1".getBytes()));
    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.ORIGIN_REALM, "mobicents.org".getBytes()));

    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.DESTINATION_HOST, ("127.0.0.1" + ":" + "3868").getBytes()));
    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.DESTINATION_REALM, "mobicents.org".getBytes()));

    // Subscription ID
    DiameterAvp subscriptionIdType = baseAvpFactory.createAvp(193, 555, 0);
    DiameterAvp subscriptionIdData = baseAvpFactory.createAvp(193, 554, "00001000");
    avps.add(baseAvpFactory.createAvp(193, 553, new DiameterAvp[] { subscriptionIdType, subscriptionIdData }));

    // Requested Service Unit
    DiameterAvp unitType = baseAvpFactory.createAvp(193, 611, 2);
    DiameterAvp valueDigits = baseAvpFactory.createAvp(193, 617, 10L);
    DiameterAvp unitValue = baseAvpFactory.createAvp(193, 612, new DiameterAvp[] { valueDigits });
    avps.add(baseAvpFactory.createAvp(193, 606, new DiameterAvp[] { unitType, unitValue }));

    // Record Number and Type
    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.ACCOUNTING_RECORD_NUMBER, 0));
    avps.add(baseAvpFactory.createAvp(DiameterAvpCodes.ACCOUNTING_RECORD_TYPE, 1));

    // Requested action
    avps.add(baseAvpFactory.createAvp(193, 615, 0));

    // Service Parameter Type
    DiameterAvp serviceParameterType = baseAvpFactory.createAvp(193, 608, 0);
    DiameterAvp serviceParameterValue = baseAvpFactory.createAvp(193, 609, "510");
    avps.add(baseAvpFactory.createAvp(193, 607, new DiameterAvp[] { serviceParameterType, serviceParameterValue }));

    // Service Parameter Type
    DiameterAvp serviceParameterType2 = baseAvpFactory.createAvp(193, 608, 14);
    DiameterAvp serviceParameterValue2 = baseAvpFactory.createAvp(193, 609, "20");
    avps.add(baseAvpFactory.createAvp(193, 607, new DiameterAvp[] { serviceParameterType2, serviceParameterValue2 }));

    DiameterAvp[] avpArray = new DiameterAvp[avps.size()];
    avpArray = avps.toArray(avpArray);

    AccountingRequest acr = gxMessageFactory.getBaseMessageFactory().createAccountingRequest();
    acr.setExtensionAvps(avpArray);

    ArrayList<String> failures = checkAvpFlags(((DiameterMessageImpl)acr).getGenericData().getAvps());
    
    if(failures.size() > 0) {
      System.err.println("The following AVPs flags have failed to check against dictionary:");
      for(String failure : failures) {
        System.err.println(failure);
      }
      Assert.fail(failures.toString());
    }
    
    // Test going through other way for adding avp's
    acr = gxMessageFactory.getBaseMessageFactory().createAccountingRequest(avpArray);

    failures = checkAvpFlags(((DiameterMessageImpl)acr).getGenericData().getAvps());
    
    if(failures.size() > 0) {
      System.err.println("The following AVPs flags have failed to check against dictionary:");
      for(String failure : failures) {
        System.err.println(failure);
      }
      Assert.fail(failures.toString());
    }
  }

  /**
   * 
   * @param set the set to check
   * @return an array of offending AVPs
   */
  private ArrayList<String> checkAvpFlags(AvpSet set) {
    ArrayList<String> failedAvps = new ArrayList<String>();

    for(Avp avp : set) {
      //System.out.println(avp.getVendorId() + ":" + avp.getCode() + " V[" + avp.isVendorId() + "] M[" + avp.isMandatory() + "] P[" + avp.isEncrypted() + "]");
      AvpRepresentation avpRep = validator.getAvp(avp.getCode(), avp.getVendorId());
      //System.out.println(avpRep.getVendorId() + ":" + avpRep.getCode() + " V[" + avpRep.getRuleVendorBit() + "] M[" + avpRep.getRuleMandatory() + "] P[" + avpRep.getRuleProtected() + "]");
      
      // Mandatory must not be set if rule is MUST NOT or SHOULD NOT
      if(avp.isMandatory() && (avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot"))) {
        failedAvps.add("- Code[" + avp.getCode() + "], Vendor-Id[" + avp.getVendorId() + "], Flag[M / '" + avp.isMandatory() + "' vs '" + avpRep.getRuleMandatory() + "']");
      }
      
      // Protected must not be set if rule is MUST or MAY
      if(avp.isEncrypted() && !(avpRep.getRuleProtected().equals("must") || avpRep.getRuleProtected().equals("may"))) {
        failedAvps.add("- Code[" + avp.getCode() + "], Vendor-Id[" + avp.getVendorId() + "], Flag[P / '" + avp.isEncrypted() + "' vs '" + avpRep.getRuleProtected() + "']");
      }

      // Vendor must be set if rule is MUST or MAY
      if(avp.isEncrypted() && !(avpRep.getRuleProtected().equals("must") || avpRep.getRuleProtected().equals("may"))) {
        failedAvps.add("- Code[" + avp.getCode() + "], Vendor-Id[" + avp.getVendorId() + "], Flag[P / '" + avp.isEncrypted() + "' vs '" + avpRep.getRuleProtected() + "']");
      }

      AvpSet subAvps = null;
      try {
        subAvps = avp.getGrouped();
      }
      catch (Exception e) {
      }
      
      if(subAvps != null) {
        failedAvps.addAll(checkAvpFlags(subAvps));
      }
    }
    
    return failedAvps;
  }
}
