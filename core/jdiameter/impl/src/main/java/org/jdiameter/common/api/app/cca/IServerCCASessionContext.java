package org.jdiameter.common.api.app.cca;

import java.util.concurrent.ScheduledFuture;

import org.jdiameter.api.Request;
import org.jdiameter.api.cca.ServerCCASession;

/**
 * 
 * IServerCCASessionContext.java
 *
 * <br>Super project:  mobicents
 * <br>4:22:54 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface IServerCCASessionContext {

	public void sessionSupervisionTimerExpired(ServerCCASession session);
	
	/**
	 * This is called always when Tcc starts
	 * @param session
	 * @param future
	 */
	public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future);

	public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future);
	
	public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future);

	/**
	 * Returns seconds value representing default validity time, App session uses 2x for Tcc timer
	 * @return
	 */
	public long getDefaultValidityTime();
	
	public void timeoutExpired(Request request);
	
}
