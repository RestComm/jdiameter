/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.api.cxdx.events;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;

/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JServerAssignmentAnswer extends AppAnswerEvent {

	public static final String _SHORT_NAME = "SAA";
	public static final String _LONG_NAME = "Server-Assignment-Answer";
	public static final int code = 301;
}
