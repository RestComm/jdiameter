package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppRequestEvent;

public interface ProfileUpdateRequest extends AppRequestEvent {
	public static final String _SHORT_NAME="PUR";
	public static final String _LONG_NAME="Profile-Update-Request";
	public static final int code=307;
}
