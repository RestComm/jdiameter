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

import static org.jdiameter.api.Avp.ACC_RECORD_NUMBER;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class AccountRequestImpl extends AppRequestEventImpl implements AccountRequest {

  private static final long serialVersionUID = 1L;

  public AccountRequestImpl(AppSession session, int accountRecordType, int accReqNumber, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
    try {
      getMessage().getAvps().addAvp(Avp.ACC_RECORD_TYPE, accountRecordType);
      getMessage().getAvps().addAvp(Avp.ACC_RECORD_NUMBER, accReqNumber);
    }
    catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public AccountRequestImpl(Request request) {
    super(request);
  }

  public int getAccountingRecordType() throws AvpDataException {
    if ( message.getAvps().getAvp(Avp.ACC_RECORD_TYPE) != null ) {
      return message.getAvps().getAvp(Avp.ACC_RECORD_TYPE).getInteger32();
    }
    else {
      throw new AvpDataException("Avp ACC_RECORD_NUMBER not found");
    }
  }

  public long getAccountingRecordNumber() throws AvpDataException {
    if ( message.getAvps().getAvp(ACC_RECORD_NUMBER) != null ) {
      return message.getAvps().getAvp(ACC_RECORD_NUMBER).getUnsigned32();
    }
    else {
      throw new AvpDataException("Avp ACC_RECORD_NUMBER not found");
    }
  }
}
