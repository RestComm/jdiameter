package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface SubscribeNotificationsRequest extends AppRequestEvent {
	public static final String _SHORT_NAME="SNR";
	public static final String _LONG_NAME="Subscribe-Notifications-Request";
	public static final int code = 308;
}
