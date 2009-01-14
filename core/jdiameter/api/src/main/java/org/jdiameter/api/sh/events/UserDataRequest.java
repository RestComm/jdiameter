package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface UserDataRequest extends AppRequestEvent {
	public static final String _SHORT_NAME="UDR";
	public static final String _LONG_NAME="User-Data-Request";
	public static final int code = 306;
}
