package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class SubscribeNotificationsRequestImpl extends AppRequestEventImpl
		implements SubscribeNotificationsRequest {

	public SubscribeNotificationsRequestImpl(Request request) {
		super(request);

	}

}
