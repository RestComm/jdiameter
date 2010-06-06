package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class PushNotificationRequestImpl extends AppRequestEventImpl implements PushNotificationRequest {

  private static final long serialVersionUID = 1L;

  public PushNotificationRequestImpl(Request request) {
    super(request);
  }
}