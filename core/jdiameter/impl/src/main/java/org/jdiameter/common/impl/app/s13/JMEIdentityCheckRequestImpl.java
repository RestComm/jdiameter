package org.jdiameter.common.impl.app.s13;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMEIdentityCheckRequestImpl extends AppRequestEventImpl implements JMEIdentityCheckRequest {
	private static final long serialVersionUID = 1L;

	protected final static Logger logger = LoggerFactory.getLogger(JMEIdentityCheckRequestImpl.class);

	public JMEIdentityCheckRequestImpl(Message message) {
		super(message);
		message.setRequest(true);
	}

	@Override
	public Avp getTerminalInformationAvp() {
		return super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);
	}

	@Override
	public boolean hasIMEI() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				return terminalInfoAvp.getGrouped().getAvp(Avp.TGPP_IMEI) != null;
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) IMEI AVP value", ex);
			}
		}
		return false;
	}

	@Override
	public String getIMEI() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				Avp imei = terminalInfoAvp.getGrouped().getAvp(Avp.TGPP_IMEI);
				if(imei != null){
					return imei.getUTF8String();
				}
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) IMEI AVP value", ex);
			}
		}
		return null;
	}

	@Override
	public boolean hasTgpp2MEID() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				return terminalInfoAvp.getGrouped().getAvp(Avp.TGPP2_MEID) != null;
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) MEID AVP value", ex);
			}
		}
		return false;
	}

	@Override
	public byte[] getTgpp2MEID() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				Avp meid = terminalInfoAvp.getGrouped().getAvp(Avp.TGPP2_MEID);
				if(meid != null){
					return meid.getOctetString();
				}
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) MEID AVP value", ex);
			}
		}
		return null;
	}

	@Override
	public boolean hasSoftwareVersion() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				return terminalInfoAvp.getGrouped().getAvp(Avp.SOFTWARE_VERSION) != null;
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) Software-Version AVP value", ex);
			}
		}
		return false;
	}

	@Override
	public String getSoftwareVersion() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(Avp.TERMINAL_INFORMATION);

		if(terminalInfoAvp != null) {
			try {
				Avp softwareVersion = terminalInfoAvp.getGrouped().getAvp(Avp.SOFTWARE_VERSION);
				if(softwareVersion != null){
					return softwareVersion.getUTF8String();
				}
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) Software-Version AVP value", ex);
			}
		}
		return null;
	}

	@Override
	public boolean isUserNameAVPPresent() {
		return super.message.getAvps().getAvp(Avp.USER_NAME) != null;
	}

	@Override
	public String getUserName() {
		Avp userNameAvp = super.message.getAvps().getAvp(Avp.USER_NAME);

		if(userNameAvp != null){
			try{
				return userNameAvp.getUTF8String();
			}catch (AvpDataException e) {
				logger.debug("Failure trying to obtain User-Name AVP value", e);
			}
		}
		return null;
	}
}
