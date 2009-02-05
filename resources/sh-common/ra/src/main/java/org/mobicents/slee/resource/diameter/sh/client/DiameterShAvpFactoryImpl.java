package org.mobicents.slee.resource.diameter.sh.client;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.MessageFactory;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.parser.MessageParser;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class DiameterShAvpFactoryImpl implements DiameterShAvpFactory {

  protected DiameterAvpFactory baseAvpFactory = null;

  protected MessageParser parser = new MessageParser(null);

  protected Stack stack=null;

  public DiameterShAvpFactoryImpl(DiameterAvpFactory baseAvpFactory, Stack stack)
  {
    super();
    this.baseAvpFactory = baseAvpFactory;
    this.stack = stack;
  }

  public DiameterShAvpFactoryImpl(Stack stack)
  {
    super();
    this.stack = stack;
    this.baseAvpFactory = new DiameterAvpFactoryImpl();
  }

  public SupportedApplicationsAvp createSupportedApplications(long authApplicationId, long acctApplicationId, VendorSpecificApplicationIdAvp vendorSpecificApplicationId)
  {
    SupportedApplicationsAvp saAvp = this.createSupportedApplications();

    saAvp.setAcctApplicationId(acctApplicationId);
    saAvp.setAuthApplicationId(authApplicationId);

    return saAvp;
  }

  public SupportedApplicationsAvp createSupportedApplications()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.SUPPORTED_APPLICATIONS, MessageFactory._SH_VENDOR_ID);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    SupportedApplicationsAvpImpl saAvp = new SupportedApplicationsAvpImpl(DiameterShAvpCodes.SUPPORTED_APPLICATIONS, MessageFactory._SH_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});

    return saAvp;
  }

  public SupportedFeaturesAvp createSupportedFeatures(long vendorId, long featureListId, long featureList)
  {
    SupportedFeaturesAvp sfAvp = this.createSupportedFeatures();

    sfAvp.setVendorId(vendorId);
    sfAvp.setFeatureList(featureList);
    sfAvp.setFeatureListId(featureListId);

    return sfAvp;
  }

  public SupportedFeaturesAvp createSupportedFeatures()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.SUPPORTED_FEATURES, MessageFactory._SH_VENDOR_ID);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    SupportedFeaturesAvpImpl sfAvp = new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES, MessageFactory._SH_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});

    return sfAvp;
  }

  public UserIdentityAvp createUserIdentity()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.USER_IDENTITY, MessageFactory._SH_VENDOR_ID);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    UserIdentityAvpImpl uiAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, MessageFactory._SH_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});

    return uiAvp;
  }

  public DiameterAvpFactory getBaseFactory()
  {
    return this.baseAvpFactory;
  }

}
