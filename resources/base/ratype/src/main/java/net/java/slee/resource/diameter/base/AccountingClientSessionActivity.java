package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.AccountingRequest;

public interface AccountingClientSessionActivity extends
		AccountingSessionActivity {

	/**
	 * Send Account Request to Server
	 * @param request
	 * @throws IOException 
	 */
	 void 	sendAccountRequest(AccountingRequest request) throws IOException;
     
	
}
