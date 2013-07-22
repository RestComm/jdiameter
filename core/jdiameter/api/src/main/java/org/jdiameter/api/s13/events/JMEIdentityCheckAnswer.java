package org.jdiameter.api.s13.events;

import org.jdiameter.api.app.AppAnswerEvent;

public interface JMEIdentityCheckAnswer extends AppAnswerEvent {
	
	public static final String _SHORT_NAME = "ECA";
	public static final String _LONG_NAME = "ME-Identity-Check-Answer";
	
	public static final int code = 324;
	
	public int getEquipmentStatus();
}
