package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterHeader;

/**
 * 
 * DiameterHeaderImpl.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 3:05:20 PM Jun 20, 2008 <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 */
public class DiameterHeaderImpl implements DiameterHeader {

	// private long applicationId;
	// private long hopByHopId;
	// private long endToEndId;
	// private int messageLength;
	// private int commandCode;
	// private boolean request;
	// private boolean proxiable;
	// private boolean error;
	// private boolean potentiallyRetransmitted;

	// public DiameterHeaderImpl(long applicationId, long hopByHopId,
	// long endToEndId, int messageLength, int commandCode,
	// boolean request, boolean proxiable, boolean error,
	// boolean potentiallyRetransmitted) {
	// this.applicationId = applicationId;
	// this.hopByHopId = hopByHopId;
	// this.endToEndId = endToEndId;
	// this.messageLength = messageLength;
	// this.commandCode = commandCode;
	// this.request = request;
	// this.proxiable = proxiable;
	// this.error = error;
	// this.potentiallyRetransmitted = potentiallyRetransmitted;
	// }
	//
	// public long getApplicationId() {
	// return applicationId;
	// }
	//
	// public long getHopByHopId() {
	// return hopByHopId;
	// }
	//
	// public long getEndToEndId() {
	// return endToEndId;
	// }
	//
	// public int getMessageLength() {
	// return messageLength;
	// }
	//
	// public int getCommandCode() {
	// return commandCode;
	// }
	//
	// public boolean isRequest() {
	// return request;
	// }
	//
	// public boolean isProxiable() {
	// return proxiable;
	// }
	//
	// public boolean isError() {
	// return error;
	// }
	//
	// public boolean isPotentiallyRetransmitted() {
	// return potentiallyRetransmitted;
	// }
	//
	// public short getVersion() {
	// return 1;
	// }
	//
	// public Object clone() {
	// return new DiameterHeaderImpl(applicationId, hopByHopId, endToEndId,
	// messageLength, commandCode, request, proxiable, error,
	// potentiallyRetransmitted);
	// }
	//
	// public void setApplicationId(long appId) {
	// this.applicationId = appId;
	//
	// }
	//
	// public void setEndToEndId(long etd) {
	// this.endToEndId=etd;
	//
	// }
	//
	// public void setHopByHopId(long hbh) {
	// this.hopByHopId=hbh;
	// }

	private Message msg = null;

	public DiameterHeaderImpl(Message msg) {
		super();
		this.msg = msg;
	}

	public long getApplicationId() {
		return this.msg.getApplicationId();
	}

	public int getCommandCode() {
		return this.msg.getCommandCode();
	}

	public long getEndToEndId() {
		return this.msg.getEndToEndIdentifier();
	}

	public long getHopByHopId() {
		return this.msg.getHopByHopIdentifier();
	}

	public int getMessageLength() {
		return 0;
	}

	public short getVersion() {
		return this.msg.getVersion();
	}

	public boolean isError() {
		return this.msg.isError();
	}

	public boolean isPotentiallyRetransmitted() {
		return this.msg.isReTransmitted();
	}

	public boolean isProxiable() {
		return this.msg.isProxiable();
	}

	public boolean isRequest() {
		return this.msg.isRequest();
	}


	public void setEndToEndId(long etd) {
		((org.jdiameter.client.impl.parser.MessageImpl)this.msg).setEndToEndIdentifier(etd);
		
	}

	public void setHopByHopId(long hbh) {
		((org.jdiameter.client.impl.parser.MessageImpl)this.msg).setHopByHopIdentifier(hbh);
		
	}

	@Override
	public Object clone() {
		return new DiameterHeaderImpl(this.msg);
	}

}
