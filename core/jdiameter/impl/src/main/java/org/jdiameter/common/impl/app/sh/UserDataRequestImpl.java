package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class UserDataRequestImpl extends AppRequestEventImpl implements UserDataRequest {

  private static final long serialVersionUID = 1L;

  public UserDataRequestImpl(Request request) {
    super(request);
  }

}