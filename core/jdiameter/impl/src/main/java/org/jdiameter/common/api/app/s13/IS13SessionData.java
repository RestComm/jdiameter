package org.jdiameter.common.api.app.s13;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.IAppSessionData;

public interface IS13SessionData extends IAppSessionData {

	  public void setS13SessionState(S13SessionState state);
	  public S13SessionState getS13SessionState();

	  public Serializable getTsTimerId();
	  public void setTsTimerId(Serializable tid);

	  public void setBuffer(Request buffer);
	  public Request getBuffer();
}
