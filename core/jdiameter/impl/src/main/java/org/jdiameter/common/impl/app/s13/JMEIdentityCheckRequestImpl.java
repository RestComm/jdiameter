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
	
	protected Logger logger = LoggerFactory.getLogger(JMEIdentityCheckRequestImpl.class);
	
	private static final int TERMINAL_INFO_GROUP_AVP_CODE = 1401;
	private static final int IMEI_AVP_CODE = 1402;

	public JMEIdentityCheckRequestImpl(Message message) {
		super(message);
		message.setRequest(true);
	}

	@Override
	public String getIMEI() {
		Avp terminalInfoAvp = super.message.getAvps().getAvp(TERMINAL_INFO_GROUP_AVP_CODE);
		
		if(terminalInfoAvp !=null) {
			try {
				Avp imei = terminalInfoAvp.getGrouped().getAvp(IMEI_AVP_CODE);
				if(imei != null){
					return imei.getUTF8String();
				}
			}catch(AvpDataException ex){
				 logger.debug("Failure trying to obtain (Terminal-Infonation) IMEI AVP value", ex);
			}
		}
		return null;
	}
}
