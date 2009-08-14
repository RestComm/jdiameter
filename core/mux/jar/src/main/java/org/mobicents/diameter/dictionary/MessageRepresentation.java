package org.mobicents.diameter.dictionary;

import org.jdiameter.common.impl.validation.VAvpRepresentation;
import org.jdiameter.common.impl.validation.VMessageRepresentation;

/**
 * Start time:11:34:51 2009-08-11<br>
 * Project: diameter-parent<br>
 * Simple override class to provide more functionality if required.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski</a>
 */
public class MessageRepresentation extends VMessageRepresentation implements Cloneable {

	public MessageRepresentation(int commandCode, long applicationId, boolean isRequest, String name) {
		super(commandCode, applicationId, isRequest, name);
		// TODO Auto-generated constructor stub
	}

	public MessageRepresentation(int commandCode, long applicationId, boolean isRequest) {
		super(commandCode, applicationId, isRequest);
		// TODO Auto-generated constructor stub
	}

	public MessageRepresentation(VMessageRepresentation clone) {
		super(clone);

		for (VAvpRepresentation key : clone.getMessageAvps().keySet()) {
			this.messageAvps.put(new AvpRepresentation((VAvpRepresentation) key), new AvpRepresentation((VAvpRepresentation) clone.getMessageAvps().get(key)));
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
