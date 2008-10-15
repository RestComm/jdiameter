package net.java.slee.resource.diameter.cca;

/**
 * Superinterface for Credit Control activities.
 * 
 * @author Alexandre Mendonça
 *
 */
public interface CreditControlSession {

	/**
	 * Provides session state information. CC session must conform to CC FSM as
	 * described in <a href="link http://rfc.net/rfc4006.html#s7">section 7 of rfc4006</a>
	 * 
	 * @return instance of {@link CreditControlSessionState}
	 */
	public CreditControlSessionState getState();

	/**
	 * Returns the session ID of the credit control session, which uniquely
	 * identifies the session.
	 * 
	 * @return 
	 */
	public String getSessionId();
}
