package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface PushNotificationRequest extends AppRequestEvent {
	public static final String _SHORT_NAME="PNR";
	public static final String _LONG_NAME="Push-Notification-Request";
	public static final int code = 309;
}
