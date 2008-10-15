package net.java.slee.resource.diameter.base;



public interface AuthSessionActivity extends DiameterActivity{
	
	/**
	 * Return current auth session state - it can have values as follows: Idle,Pending,Open,Disconnected.<br>
	 * Disconnected value implies that activity is ending
	 * @return
	 */
	AuthSessionState getSessionState();

}
