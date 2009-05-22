/**
 * Start time:19:08:53 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.base.events;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.SessionTerminationMessage;

/**
 * Start time:19:08:53 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class SessionTerminationMessageImpl extends DiameterMessageImpl implements SessionTerminationMessage {
	private Logger logger = Logger.getLogger(SessionTerminationMessageImpl.class);

	/**
	 * 
	 * @param message
	 */
	public SessionTerminationMessageImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#
	 * getLongName()
	 */

	public byte[][] getClassAvps() {
		if (hasClassAvp()) {
			AvpSet s = message.getAvps().getAvps(Avp.CLASS);

			byte[][] rc = new byte[s.size()][];

			for (int i = 0; i < s.size(); i++) {
				try {
					rc[i] = s.getAvpByIndex(i).getRaw();
				} catch (Exception e) {
					logger.error("Unable to obtain/decode AVP (code:" + Avp.CLASS + ")", e);
				}
			}

			return rc;
		} else {
			return null;
		}
	}

	public void setClassAvp(byte[] classAvp) {
		message.getAvps().addAvp(25, classAvp, true, false);
	}

	public void setClassAvps(byte[][] classAvps) {
		for (byte[] i : classAvps) {
			setClassAvp(i);
		}
	}

	public byte[] getClassAvp() {
		if (hasClassAvp()) {
			Avp s = message.getAvps().getAvp(Avp.CLASS);

			try {
				return s.getRaw();
			} catch (AvpDataException e) {
				logger.error("Unable to obtain/decode AVP (code:" + Avp.CLASS + ")", e);
				return null;
			}
		} else {
			return null;
		}
	}

	public boolean hasClassAvp() {
		return super.hasAvp(Avp.CLASS);
	}

}
