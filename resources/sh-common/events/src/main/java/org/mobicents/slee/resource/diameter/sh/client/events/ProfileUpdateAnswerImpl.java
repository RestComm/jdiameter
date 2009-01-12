package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;

public class ProfileUpdateAnswerImpl extends DiameterShMessageImpl implements ProfileUpdateAnswer {

	public ProfileUpdateAnswerImpl(Message msg) {
		super(msg);
		msg.setRequest(false);
		super.longMessageName="Profile-Update-Answer";
		super.shortMessageName="PUA";
	}

	public ExperimentalResultAvp getExperimentalResult() {


		if(!hasExperimentalResult())
			return null;
		
		Avp rawAvp=super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
		
		try {
			return new ExperimentalResultAvpImpl(rawAvp.getCode(),rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
		} catch (AvpDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean hasExperimentalResult() {
		return super.message.getAvps().getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT)!=null;
	}

	public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
		//FIXME: Baranowb ???
		super.setAvpAsGroup(experimentalResult.getCode(), new ExperimentalResultAvp[]{experimentalResult}, experimentalResult.getMandatoryRule()==1, true);

	}

}
