package org.mobicents.slee.resource.diameter.sh.client;

import org.apache.log4j.Logger;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.parser.MessageParser;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

public class DiameterShAvpFactoryImpl implements DiameterShAvpFactory {

	protected DiameterAvpFactory baseAvpFactory = null;
	private static transient Logger logger = Logger.getLogger(DiameterShAvpFactoryImpl.class);

	protected MessageParser parser = new MessageParser(null);

	protected Stack stack=null;
	
	
	
	public DiameterShAvpFactoryImpl(DiameterAvpFactory baseAvpFactory,
			Stack stack) {
		super();
		this.baseAvpFactory = baseAvpFactory;
		this.stack = stack;
	}

	public DiameterShAvpFactoryImpl(Stack stack) {
		super();
		this.stack = stack;
		this.baseAvpFactory=new DiameterAvpFactoryImpl();
	}

	public SupportedApplicationsAvp createSupportedApplications(long authApplicationId, long acctApplicationId, VendorSpecificApplicationIdAvp vendorSpecificApplicationId) {
		SupportedApplicationsAvp n=this.createSupportedApplications();
		n.setAcctApplicationId(acctApplicationId);
		n.setAuthApplicationId(authApplicationId);
		return n;
	}

	public SupportedApplicationsAvp createSupportedApplications() {
		 AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.SUPPORTED_APPLICATIONS,ShClientMessageFactoryImpl._SH_VENDOR_ID);

		 int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		 int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		SupportedApplicationsAvpImpl n=new SupportedApplicationsAvpImpl(DiameterShAvpCodes.SUPPORTED_APPLICATIONS,ShClientMessageFactoryImpl._SH_VENDOR_ID,mandatoryAvp,protectedAvp,new byte[]{});
		return n;
	}

	public SupportedFeaturesAvp createSupportedFeatures(long vendorId, long featureListId, long featureList) {
		SupportedFeaturesAvp sfai=this.createSupportedFeatures();
		sfai.setVendorId(vendorId);
		sfai.setFeatureList(featureList);
		sfai.setFeatureListId(featureListId);
		return sfai;
	}

	public SupportedFeaturesAvp createSupportedFeatures() {
		 AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.SUPPORTED_FEATURES,ShClientMessageFactoryImpl._SH_VENDOR_ID);

		 int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		 int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		SupportedFeaturesAvpImpl sfai=new SupportedFeaturesAvpImpl(DiameterShAvpCodes.SUPPORTED_FEATURES,ShClientMessageFactoryImpl._SH_VENDOR_ID,mandatoryAvp,protectedAvp,new byte[]{});
		return sfai;
	}

	public UserIdentityAvp createUserIdentity() {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.USER_IDENTITY,ShClientMessageFactoryImpl._SH_VENDOR_ID);


		 int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		 int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		UserIdentityAvpImpl ui=new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY,ShClientMessageFactoryImpl._SH_VENDOR_ID,mandatoryAvp,protectedAvp,new byte[]{});
		
		return ui;
	}

	public DiameterAvpFactory getBaseFactory() {
		return this.baseAvpFactory;
	}

}
