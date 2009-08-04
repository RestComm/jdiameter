package org.jdiameter.common.impl.app.auth;

import static org.jdiameter.api.Avp.AUTH_APPLICATION_ID;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class AbortSessionRequestImpl extends AppRequestEventImpl implements AbortSessionRequest {

  private static final long serialVersionUID = 1L;

  public AbortSessionRequestImpl(AppSession session, String destRealm, String destHost) {
    super(session.getSessions().get(0).createRequest(code, session.getSessionAppId(), destRealm, destHost));
  }

  public AbortSessionRequestImpl(Message message) {
    super(message);
  }

  public long getAuthApplicationId() throws AvpDataException {
    if (message.getAvps().getAvp(AUTH_APPLICATION_ID) != null) {
      return message.getAvps().getAvp(AUTH_APPLICATION_ID).getUnsigned32();
    }
    else {
      throw new AvpDataException("Avp AUTH_APPLICATION_ID not found");
    }
  }
}
