package net.java.slee.resource.diameter.base;

import java.io.IOException;

import net.java.slee.resource.diameter.base.events.AccountingAnswer;

public interface AccountingServerSessionActivity extends
		AccountingSessionActivity {

	/**
	 * Sends generated answer back to client
	 * @param answer
	 * @throws IOException
	 */
	void sendAccountAnswer(AccountingAnswer answer) throws IOException;

	
}
