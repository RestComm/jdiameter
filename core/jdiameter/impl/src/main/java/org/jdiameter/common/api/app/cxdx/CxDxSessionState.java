/**
 * Start time:14:43:10 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.common.api.app.cxdx;

/**
 * Start time:14:43:10 2009-08-17<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public enum CxDxSessionState {

	//FIXME: should we distinguish types of messages?
	IDLE,MESSAGE_SENT_RECEIVED,TERMINATED, TIMEDOUT;
	
}
