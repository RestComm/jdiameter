package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface UserDataAnswer extends AppAnswerEvent {
	public static final String _SHORT_NAME="UDA";
	public static final String _LONG_NAME="User-Data-Answer";
	public static final int code = 306;
}
