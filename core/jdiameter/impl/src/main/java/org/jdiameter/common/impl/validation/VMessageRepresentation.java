package org.jdiameter.common.impl.validation;

import java.util.HashMap;
import java.util.Map;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Start time:10:40:36 2009-05-26<br>
 * Project: diameter-parent<br>
 * This class represents message/command in validation framework. It contains
 * basic info about command along with avp list - their multiplicity and
 * allowance.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.189
 */
public class VMessageRepresentation implements Comparable<VMessageRepresentation>, Cloneable {

	private static final transient Logger log = LoggerFactory.getLogger(VMessageRepresentation.class);
	private int commandCode = -1;
	private long applicationId = 0;

	private boolean isRequest = false;
	protected Map<VAvpRepresentation, VAvpRepresentation> messageAvps = new HashMap<VAvpRepresentation, VAvpRepresentation>();
	private String name = null;

	public VMessageRepresentation(int commandCode, long applicationId, boolean isRequest) {
		super();
		this.commandCode = commandCode;
		this.applicationId = applicationId;
		this.isRequest = isRequest;
	}

	public VMessageRepresentation(int commandCode, long applicationId, boolean isRequest, String name) {
		super();
		this.commandCode = commandCode;
		this.applicationId = applicationId;
		this.isRequest = isRequest;
		this.name = name;
	}

	public VMessageRepresentation(VMessageRepresentation clone) {
		super();
		this.applicationId = clone.applicationId;
		this.commandCode = clone.commandCode;
		this.isRequest = clone.isRequest;
		this.name = clone.name;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (applicationId ^ (applicationId >>> 32));
		result = prime * result + commandCode;
		result = prime * result + (isRequest ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VMessageRepresentation other = (VMessageRepresentation) obj;
		if (applicationId != other.applicationId)
			return false;
		if (commandCode != other.commandCode)
			return false;
		if (isRequest != other.isRequest)
			return false;
		return true;
	}

	public Map<VAvpRepresentation, VAvpRepresentation> getMessageAvps() {
		return messageAvps;
	}

	public void setMessageAvps(Map<VAvpRepresentation, VAvpRepresentation> messageAvps) {
		this.messageAvps = messageAvps;
	}

	public int getCommandCode() {
		return commandCode;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public boolean isRequest() {
		return isRequest;
	}

	public String getName() {
		return name;
	}

	/**
	 * Valdiates messages in terms of contained avps. It checks against
	 * deinftion if available if all requried avps are set, not allowed avps are
	 * not present, and number of avps is correct.
	 * 
	 * @param msg
	 * @throws JAvpNotAllowedException
	 */
	public void validate(Message msg) throws JAvpNotAllowedException {
		for (VAvpRepresentation ap : this.messageAvps.values()) {

			AvpSet innerSet = msg.getAvps().getAvps(ap.getCode(), ap.getVendorId());
			int count = 0;
			if (innerSet != null) {
				count = innerSet.size();
			}

			if (!ap.isCountValidForMultiplicity(count)) {
				throw new JAvpNotAllowedException("AVP: " + ap + " has wrong count in message - " + (count), ap.getCode(), ap.getVendorId());
			}

			if (count != 0 && ap.isGrouped()) {
				// we are grouped
				validateGrouped(ap, innerSet);
			}
		}
	}

	/**
	 * @param ap
	 * @param innerSet
	 */
	private void validateGrouped(VAvpRepresentation ap, AvpSet innerSet) {

		// we have set of grouped avps, and ap is grouped, lets validate
		// NOTE this methods can be called multiple time, until we dont have

		for (int index = 0; index < innerSet.size(); index++) {

			Avp presumablyGrouped = innerSet.getAvpByIndex(index);
			AvpSet groupedPart = null;
			try {
				groupedPart = presumablyGrouped.getGrouped();
			} catch (AvpDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (groupedPart == null) {
				log.error("Avp should be grouped, but its not, skipping validation: " + ap);

				continue;
			} else {
				validateGroupedChildren(ap, groupedPart);
			}

		}

	}

	/**
	 * @param ap
	 * @param presumablyGrouped
	 */
	private void validateGroupedChildren(VAvpRepresentation ap, AvpSet groupedAvp) {
		// we have grouped avp, and its representation, we should validate
		// children.
		for (VAvpRepresentation childrenVAvp : ap.getChildren()) {
			AvpSet childSset = groupedAvp.getAvps(childrenVAvp.getCode(), childrenVAvp.getVendorId());
			int count = childSset.size();

			if (!childrenVAvp.isCountValidForMultiplicity(count)) {
				throw new JAvpNotAllowedException("AVP: " + childrenVAvp + " has wrong count ,in grouped parent avp - " + (count)+", allowed: "+childrenVAvp.getMultiplicityIndicator(), ap.getCode(), ap.getVendorId());
			}
			if (childrenVAvp.isGrouped()) {

				validateGrouped(childrenVAvp, childSset);
			}
			// else we are good ?
		}

	}

	public void validate(AvpSet destination, Avp avp) {
		VAvpRepresentation avpRep = new VAvpRepresentation(avp.getCode(), avp.getVendorId());
		avpRep = this.messageAvps.get(avpRep);
		if (avpRep == null)
			return;

		if (!avpRep.isAllowed()) {
			throw new JAvpNotAllowedException("AVP: " + avpRep + " is not allowed.", avp.getCode(), avp.getVendorId());
		}
		// For avp beeing added :)
		int count = 1;
		AvpSet innerSet = destination.getAvps(avpRep.getCode(), avpRep.getVendorId());
		if (innerSet != null) {
			count += innerSet.size();
		}

		if (!avpRep.isCountValidForMultiplicity(count)) {
			throw new JAvpNotAllowedException("AVP: " + avpRep + " is not allowed, to many avps present already - " + (count - 1)+", allowed: "+avpRep.getMultiplicityIndicator(), avp.getCode(), avp.getVendorId());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(VMessageRepresentation o) {
		if (o == this)
			return 0;
		if (o == null)
			return 1;
		return this.hashCode() - o.hashCode();
	}

	/**
	 * @param code
	 * @param vendorId
	 * @param count
	 * @return
	 */
	public boolean isCountValidForMultiplicity(int code, long vendorId, int count) {
		VAvpRepresentation avpRep = new VAvpRepresentation(code, vendorId);
		avpRep = this.messageAvps.get(avpRep);
		if (avpRep == null)
			return true;
		return avpRep.isCountValidForMultiplicity(count);
	}

	/**
	 * @param code
	 * @param vendorId
	 * @return
	 */
	public boolean isAllowed(int code, long vendorId) {
		VAvpRepresentation avpRep = new VAvpRepresentation(code, vendorId);
		avpRep = this.messageAvps.get(avpRep);
		if (avpRep == null)
			return true;
		return avpRep.isAllowed();
	}

	/**
	 * @param avpCode
	 * @param avpVendor
	 * @return
	 */
	public boolean hasRepresentation(int avpCode, long avpVendor) {
		VAvpRepresentation avpRep = new VAvpRepresentation(avpCode, avpVendor);
		avpRep = this.messageAvps.get(avpRep);
		if (avpRep == null) {

			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		VMessageRepresentation clone = (VMessageRepresentation) super.clone();
		clone.applicationId = this.applicationId;
		clone.commandCode = this.commandCode;
		clone.isRequest = this.isRequest;
		clone.name = this.name;
		clone.messageAvps = new HashMap<VAvpRepresentation, VAvpRepresentation>();
		for (VAvpRepresentation key : this.messageAvps.keySet()) {
			clone.messageAvps.put((VAvpRepresentation) key.clone(), (VAvpRepresentation) this.messageAvps.get(key).clone());
		}
		return clone;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(isRequest ? "Request" : "Answer").append(" code: ").append(this.commandCode).append(" applicationId: ").append(this.applicationId).append(" name: ").append(this.name);
		for (VAvpRepresentation childAvp : this.getMessageAvps().values()) {
			sb.append("\n").append(childAvp.toString());
		}

		return sb.toString();
	}

}
