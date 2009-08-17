/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.common.impl.app.cxdx;

import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/**
 * Start time:13:45:50 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class JMultimediaAuthRequestImpl extends AppRequestEventImpl implements  JMultimediaAuthRequest{

	/**
	 * 	
	 * @param message
	 */
	public JMultimediaAuthRequestImpl(Message message) {
		super(message);
		message.setRequest(true);
	}

}
