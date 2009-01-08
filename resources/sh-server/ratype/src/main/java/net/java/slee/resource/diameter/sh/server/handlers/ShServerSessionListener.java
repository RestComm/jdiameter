package net.java.slee.resource.diameter.sh.server.handlers;

import javax.slee.resource.ActivityHandle;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.ServerShSession;

public interface ShServerSessionListener {

  public void sessionDestroyed(String sessionId,ServerShSession session);

  /**
	 * Listener for Server Session creation.
	 */
	public void sessionCreated(ServerShSession session, boolean isSubscription);

  /**
   * Helper method to fire events to SLEE.
   * @param sessionId the id of the session for this event
   * @param name the event name (without suffix Request/Answer)
   * @param request the request object (if it is a request)
   * @param answer the answer object (if it is a answer)
   */
  public void fireEvent(String sessionId, String name, Request request, Answer answer);

  /**
   * Helper method to fire events to SLEE.
   * @param handle the activity handle where to fire it
   * @param name the event name (without suffix Request/Answer)
   * @param request the request object (if it is a request)
   * @param answer the answer object (if it is a answer)
   */
  public void fireEvent(ActivityHandle handle, String name, Request request, Answer answer);
}
