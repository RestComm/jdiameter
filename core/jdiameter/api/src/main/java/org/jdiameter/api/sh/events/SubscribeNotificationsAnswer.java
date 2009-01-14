package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppAnswerEvent;


public interface SubscribeNotificationsAnswer extends AppAnswerEvent {
	public static final String _SHORT_NAME="SNA";
	public static final String _LONG_NAME="Subscribe-Notifications-Answer";
	public static final int code = 308;
}
