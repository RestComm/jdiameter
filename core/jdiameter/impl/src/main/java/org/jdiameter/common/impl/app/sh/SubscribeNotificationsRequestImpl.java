package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class SubscribeNotificationsRequestImpl extends AppRequestEventImpl implements SubscribeNotificationsRequest {

  private static final long serialVersionUID = 1L;

  public SubscribeNotificationsRequestImpl(Request request) {
    super(request);
  }

}
