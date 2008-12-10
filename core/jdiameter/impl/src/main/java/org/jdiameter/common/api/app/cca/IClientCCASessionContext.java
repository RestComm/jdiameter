package org.jdiameter.common.api.app.cca;

import org.jdiameter.api.Message;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.client.impl.app.cca.ClientCCASessionImpl;

public interface IClientCCASessionContext {

	long getDefaultTxTimerValue();

	void txTimerExpired(ClientCCASession session);
	
	int getDefaultCCFHValue();
	int getDefaultDDFHValue();

	void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl,Message request);

	void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl,Message request);

	void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl);

	void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl);

	void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl);

	void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl);

	void indicateServiceError(ClientCCASession clientCCASessionImpl);

}
