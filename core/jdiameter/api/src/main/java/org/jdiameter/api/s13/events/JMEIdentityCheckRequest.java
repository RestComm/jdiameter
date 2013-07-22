package org.jdiameter.api.s13.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface JMEIdentityCheckRequest extends AppRequestEvent {
	public static final String _SHORT_NAME = "ECR";
	public static final String _LONG_NAME = "ME-Identity-Check-Request";

	public static final int code = 324;
	
	public abstract String getIMEI();
}
