package org.jdiameter.common.impl.app.s13;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.s13.S13SessionState;

public class S13LocalSessionDataImpl extends AppSessionDataLocalImpl implements IS13SessionData {

	protected S13SessionState state = S13SessionState.IDLE;
	protected Request buffer;
	protected Serializable tsTimerId;

	public void setS13SessionState(S13SessionState state) {
		this.state = state;
	}

	public S13SessionState getS13SessionState() {
		return this.state;
	}

	public Serializable getTsTimerId() {
		return this.tsTimerId;
	}

	public void setTsTimerId(Serializable tid) {
		this.tsTimerId = tid;
	}

	public void setBuffer(Request buffer) {
		this.buffer = buffer;
	}

	public Request getBuffer() {
		return this.buffer;
	}
}
