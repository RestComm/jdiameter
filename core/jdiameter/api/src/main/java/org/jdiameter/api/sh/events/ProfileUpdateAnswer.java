package org.jdiameter.api.sh.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface ProfileUpdateAnswer extends AppAnswerEvent {
	public static final String _SHORT_NAME="PUA";
	public static final String _LONG_NAME="Profile-Update-Answer";
	public static final int code=307;
}
