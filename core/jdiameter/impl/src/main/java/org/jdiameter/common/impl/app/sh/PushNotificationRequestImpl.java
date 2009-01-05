package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class PushNotificationRequestImpl extends AppRequestEventImpl implements PushNotificationRequest {
	



	public PushNotificationRequestImpl(Request request) {
		super(request);

	}
}