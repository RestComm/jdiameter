package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Request;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class UserDataRequestImpl extends AppRequestEventImpl implements
		UserDataRequest {

	public UserDataRequestImpl(Request request) {
		super(request);

	}
}
