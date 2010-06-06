package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class ProfileUpdateRequestImpl extends AppRequestEventImpl implements ProfileUpdateRequest {

  private static final long serialVersionUID = 1L;

  public ProfileUpdateRequestImpl(Request request) {
    super(request);
  }
}
