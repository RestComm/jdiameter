/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.impl.app.acc;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import static org.jdiameter.api.Avp.ACC_RECORD_NUMBER;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;


public class AccountAnswerImpl extends AppAnswerEventImpl implements AccountAnswer {


    public AccountAnswerImpl(Request request, int accountRecordType, int accReqNumber, long resultCode) {
        super(request.createAnswer(resultCode));
        try {
            getMessage().getAvps().addAvp(Avp.ACC_RECORD_TYPE, accountRecordType);
            getMessage().getAvps().addAvp(Avp.ACC_RECORD_NUMBER, accReqNumber);
        } catch (Exception exc) {
            throw new IllegalArgumentException(exc);
        }
    }

    public AccountAnswerImpl(Answer answer) {
        super(answer);
    }

    public int getAccountingRecordType() throws AvpDataException {
        if ( message.getAvps().getAvp(Avp.ACC_RECORD_NUMBER) != null )
            return message.getAvps().getAvp(ACC_RECORD_NUMBER).getInteger32();
        else
            throw new AvpDataException("Avp ACC_RECORD_NUMBER not found");
    }

    public long getAccountingRecordNumber() throws AvpDataException {
        if ( message.getAvps().getAvp(ACC_RECORD_NUMBER) != null )
            return message.getAvps().getAvp(ACC_RECORD_NUMBER).getUnsigned32();
        else
            throw new AvpDataException("Avp ACC_RECORD_NUMBER not found");
    }
}
