package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.DiameterShMessage;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;
public class DiameterShMessageImpl extends DiameterMessageImpl implements DiameterShMessage {

	
	protected String longMessageName=null;
	protected String shortMessageName=null;
	
	public DiameterShMessageImpl(Message msg) {
		super(msg);
		
	}

	@Override
	public String getLongName() {

		return longMessageName;
	}

	@Override
	public String getShortName() {

		return shortMessageName;
	}

	public AuthSessionStateType getAuthSessionState() {
		
		AvpSet avpSet=super.message.getAvps();
		Avp rawAvp=avpSet.getAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
		if(rawAvp==null)
		{
			return null;
		}else
		{
			int value=-1;
			try {
				value = rawAvp.getInteger32();
			} catch (AvpDataException e) {
				
				e.printStackTrace();
				return null;
			}
			return AuthSessionStateType.fromInt(value);
		}
		
	}

	

	public SupportedFeaturesAvp[] getSupportedFeatureses() {
		
		
		AvpSet set=super.message.getAvps().getAvps(DiameterShAvpCodes.SUPPORTED_FEATURES);
		SupportedFeaturesAvp[] returnValue=new SupportedFeaturesAvp[set.size()];
		int counter=0;

		for(Avp rawAvp:set)
		{
			try {
				returnValue[counter++]=new SupportedFeaturesAvpImpl(rawAvp.getCode(),rawAvp.getVendorId(),(rawAvp.isMandatory()?1:0),(rawAvp.isEncrypted()?1:0),rawAvp.getRaw());
			} catch (AvpDataException e) {
		
				e.printStackTrace();
				return null;
			}
		}
		
		return returnValue;
		
	}

	public boolean hasAuthSessionState() {
		Avp rawAvp = super.message.getAvps().getAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
		return rawAvp != null;
	}

	public void setAuthSessionState(AuthSessionStateType authSessionState) {
		super.setAvpAsUInt32(DiameterAvpCodes.AUTH_SESSION_STATE, authSessionState.getValue(), true, true);

	}

	public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
		
		super.setAvpAsGroup(supportedFeatures.getCode(), new SupportedFeaturesAvp[]{supportedFeatures}, true, true);

	}

	public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
		super.setAvpAsGroup(DiameterShAvpCodes.SUPPORTED_FEATURES, supportedFeatureses, true, true);

	}

	public SupportedFeaturesAvp getSupportedFeatures() {
		Avp rawAvp=super.message.getAvps().getAvp(DiameterShAvpCodes.SUPPORTED_FEATURES);
		
		if(rawAvp!=null)
			try {
				return new SupportedFeaturesAvpImpl(rawAvp.getCode(),rawAvp.getVendorId(),(rawAvp.isMandatory()?1:0),(rawAvp.isEncrypted()?1:0),rawAvp.getRaw());
			} catch (AvpDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			
		return null;
	}

}
