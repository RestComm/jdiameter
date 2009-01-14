package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface PushNotificationAnswer extends AppAnswerEvent {

	public static final String _SHORT_NAME="PNA";
	public static final String _LONG_NAME="Push-Notification-Answer";
	public static final int code = 309;
	
}
