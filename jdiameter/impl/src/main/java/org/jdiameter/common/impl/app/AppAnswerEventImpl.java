package org.jdiameter.common.impl.app;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppAnswerEvent;


public class AppAnswerEventImpl extends AppEventImpl implements AppAnswerEvent {

    public AppAnswerEventImpl(Message message) {
        super(message);
    }

    public Avp getResultCodeAvp() throws AvpDataException {
        Avp rs1 = message.getAvps().getAvp(Avp.RESULT_CODE);
        Avp rs2 = message.getAvps().getAvp(Avp.EXPERIMENTAL_RESULT_CODE);
        return rs1 != null ? rs1 : rs2;
    }
}
