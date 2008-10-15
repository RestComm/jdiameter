package org.mobicents.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.jdiameter.api.AvpDataException;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;


public class UserIdentityAvpImpl extends GroupedAvpImpl implements UserIdentityAvp {

	public UserIdentityAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	public byte[] getMsisdn() {
		if(hasMsisdn())
		{
			try {
				return super.avpSet.getAvp(DiameterShAvpCodes.MSISDN,10415).getRaw();
			} catch (AvpDataException e) {
				
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getPublicIdentity() {
		if(hasPublicIdentity())
		{
			try {
				return super.avpSet.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY,10415).getUTF8String();
			} catch (AvpDataException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasMsisdn() {
		
		return super.avpSet.getAvp(DiameterShAvpCodes.MSISDN)!=null;
	}

	public boolean hasPublicIdentity() {
		return super.avpSet.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY)!=null;
	}

	public void setMsisdn(byte[] msisdn) {
		super.avpSet.removeAvp(DiameterShAvpCodes.MSISDN);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.MSISDN,10415);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.avpSet.addAvp(DiameterShAvpCodes.MSISDN, msisdn, mandatoryAvp==1, protectedAvp==1);

	}

	public void setPublicIdentity(String publicIdentity) {
		super.avpSet.removeAvp(DiameterShAvpCodes.PUBLIC_IDENTITY);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.PUBLIC_IDENTITY,10415);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		super.avpSet.addAvp(DiameterShAvpCodes.PUBLIC_IDENTITY, publicIdentity, mandatoryAvp==1);
	}

}
