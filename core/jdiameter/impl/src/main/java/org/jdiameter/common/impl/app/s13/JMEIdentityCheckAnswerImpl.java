package org.jdiameter.common.impl.app.s13;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMEIdentityCheckAnswerImpl extends AppAnswerEventImpl implements JMEIdentityCheckAnswer {
	private static final long serialVersionUID = 1L;
	
	protected Logger logger = LoggerFactory.getLogger(JMEIdentityCheckAnswerImpl.class);
	
	private static final int EQUIPMENT_STATUS_AVP_CODE = 1445;

	public JMEIdentityCheckAnswerImpl(Answer answer) {
		super(answer);
	}

	public JMEIdentityCheckAnswerImpl(Request request, long resultCode) {
		super(request.createAnswer(resultCode));
	}
	
	public int getEquipmentStatus() {

		Avp equipmentStatusAvp = super.message.getAvps().getAvp(EQUIPMENT_STATUS_AVP_CODE);
		if (equipmentStatusAvp != null) {
			try {
				return equipmentStatusAvp.getInteger32();
			} catch (AvpDataException e) {
				logger.debug("Failure trying to obtain Equipment-Status AVP value", e);
			}
		}
		return -1;
	}
}
