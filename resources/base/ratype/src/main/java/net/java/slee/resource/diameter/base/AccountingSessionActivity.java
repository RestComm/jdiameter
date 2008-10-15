package net.java.slee.resource.diameter.base;

public interface AccountingSessionActivity extends DiameterActivity{

	/**
	 * Returns accounting session state of underlying session. Valid values are: Idle,PendingS,PendingE,PendingB,Open,PendingI,PendingL
	 * {@link AccountingSessionState}
	 * @return
	 */
	AccountingSessionState getAccountingSessionState();
	
}
