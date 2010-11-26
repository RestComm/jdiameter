package org.mobicents.diameter.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdiameter.common.impl.validation.AvpRepresentationImpl;

/**
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class AvpRepresentation {

	/**
	 * <pre>
	 * Represents multiplicity of AVP:
	 * 0     The AVP MUST NOT be present in the message.
	 * </pre>
	 */
	public final static String _MP_NOT_ALLOWED = "0";
	/**
	 * <pre>
	 * Represents multiplicity of AVP:
	 * 0+    Zero or more instances of the AVP MAY be present in the
	 *       message.
	 * </pre>
	 */
	public final static String _MP_ZERO_OR_MORE = "0+";
	/**
	 * <pre>
	 * Represents multiplicity of AVP:
	 * 0-1   Zero or one instance of the AVP MAY be present in the
	 *       message.  It is considered an error if there are more than
	 *       one instance of the AVP.
	 * </pre>
	 */
	public final static String _MP_ZERO_OR_ONE = "0-1";
	/**
	 * <pre>
	 * Represents multiplicity of AVP:
	 * 1     One instance of the AVP MUST be present in the message.
	 *       message.
	 * </pre>
	 */
	public final static String _MP_ONE = "1";
	/**
	 * <pre>
	 * Represents multiplicity of AVP:
	 * 1+    At least one instance of the AVP MUST be present in the
	 *       message.
	 * </pre>
	 */
	public final static String _MP_ONE_AND_MORE = "1+";
	public final static String _DEFAULT_MANDATORY = "may";
	public final static String _DEFAULT_PROTECTED = "may";
	public final static String _DEFAULT_VENDOR = "mustnot";
	public final static int _FIX_POSITION_INDEX = -1;

	public enum Rule {
		must, may, mustnot, shouldnot
	};

	public enum Type {
		OctetString, Integer32, Integer64, Unsigned32, Unsigned64, Float32, Float64, Grouped, Address, Time, UTF8String, DiameterIdentity, DiameterURI, Enumerated, IPFilterRule, QoSFilterRule
	};

	protected String description;
	protected boolean mayEncrypt;
	protected boolean _protected;
	protected boolean _mandatory;
	protected String ruleMandatory;
	protected String ruleProtected;
	protected String ruleVendorBit;
	protected String type; // String, in case user defines his own type
	protected boolean constrained;

	// ususally this will be -1, as only SessionId has fixed position
	private int positionIndex = _FIX_POSITION_INDEX;
	protected int code = -1;
	protected long vendor = 0;
	protected boolean allowed = true;
	protected String multiplicityIndicator = "0";
	protected String name = "Some-AVP";
	protected boolean grouped = false;
	protected List<AvpRepresentation> children = new ArrayList<AvpRepresentation>();
	protected boolean weak = false;

	/**
	 * @param code
	 * @param vendor
	 */
	public AvpRepresentation(int code, long vendor) {
		super();
		this.code = code;
		this.vendor = vendor;
	}

	AvpRepresentation(AvpRepresentationImpl clone) {

		this(-1, clone.getCode(), clone.getVendorId(), clone.getMultiplicityIndicator(), clone.getName());

		this.allowed = clone.isAllowed();
		this.grouped = clone.isGrouped();
		this.positionIndex = clone.getPositionIndex();

		this.weak = clone.isWeak();

		this._mandatory = clone.isMandatory();
		this._protected = clone.isProtected();
		this.constrained = clone.isConstrained();
		this.description = clone.getDescription();
		this.mayEncrypt = clone.isMayEncrypt();
		this.ruleMandatory = clone.getRuleMandatory();
		this.ruleProtected = clone.getRuleProtected();
		this.ruleVendorBit = clone.getRuleVendorBit();
		this.type = clone.getType();
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;

		// copy others.
		if (isGrouped()) {
			for (Object o : clone.getChildren()) {
				AvpRepresentationImpl avpImpl = (AvpRepresentationImpl) o;
				this.children.add(new AvpRepresentation(avpImpl));
			}

			this.children = Collections.unmodifiableList(this.children);
		}
	}

	AvpRepresentation(int positionIndex, int code, long vendor, String multiplicityIndicator, String name) {
		super();
		this.positionIndex = positionIndex;

		this.code = code;
		this.vendor = vendor;
		this.multiplicityIndicator = multiplicityIndicator;
		this.name = name;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public boolean isPositionFixed() {
		return this.positionIndex == _FIX_POSITION_INDEX;
	}

	public void markFixPosition(int index) {
		this.positionIndex = index;
	}

	public static int get_FIX_POSITION_INDEX() {
		return _FIX_POSITION_INDEX;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public int getCode() {
		return code;
	}

	public long getVendorId() {
		return vendor;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public boolean isAllowed(int avpCode, long vendorId) {
		if (this.isGrouped()) {
			// make better get ?
			for (AvpRepresentation rep : this.children) {
				if (rep.getCode() == avpCode && rep.getVendorId() == vendorId) {
					return rep.isAllowed();
				} else {
					continue;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean isAllowed(int avpCode) {
		return this.isAllowed(avpCode, 0L);
	}

	public String getMultiplicityIndicator() {
		return multiplicityIndicator;
	}

	public String getName() {
		return name;
	}

	public boolean isGrouped() {
		return grouped;
	}

 	public List<AvpRepresentation> getChildren() {
		return children;
	}

	public boolean isWeak() {
		return weak;
	}

	public String getDescription() {
		return description;
	}

	public boolean isMayEncrypt() {
		return mayEncrypt;
	}

	public String getRuleMandatory() {
		return ruleMandatory;
	}

	public int getRuleMandatoryAsInt() {
		return Rule.valueOf(ruleMandatory).ordinal();
	}

	public String getRuleProtected() {
		return ruleProtected;
	}

	public int getRuleProtectedAsInt() {
		return Rule.valueOf(ruleProtected).ordinal();
	}

	public String getRuleVendorBit() {
		return ruleVendorBit;
	}

	public int getRuleVendorBitAsInt() {
		return Rule.valueOf(ruleVendorBit).ordinal();
	}

	public boolean isConstrained() {
		return constrained;
	}

	public String getType() {
		return type;
	}

	public boolean isProtected() {
		return _protected;
	}

	public boolean isMandatory() {
		return _mandatory;
	}

	public boolean isCountValidForMultiplicity(int avpCount) {

		// This covers not_allowed
		if (!allowed) {
			if (avpCount == 0) {
				return true;
			}
		} else {
			if (this.multiplicityIndicator.equals(_MP_ZERO_OR_MORE)) {
				if (avpCount >= 0)
					return true;
			} else if (this.multiplicityIndicator.equals(_MP_ZERO_OR_ONE)) {
				if ((avpCount == 0) || (avpCount == 1))
					return true;
			} else if (this.multiplicityIndicator.equals(_MP_ONE)) {
				if (avpCount == 1) {
					return true;
				}
			} else if (this.multiplicityIndicator.equals(_MP_ONE_AND_MORE)) {
				if (avpCount >= 1) {
					return true;
				}
			}

		}

		// if we did not return, we are screwed.
		return false;
	}
	
	// public String toString() {
	// return this.getName() + "@" + hashCode() + " Name[" + getName() +
	// "] Code[" + getCode() + "] Vendor[" + getVendorId() + "] MLP[" +
	// getMultiplicityIndicator() + "] Allowed[" + isAllowed() + "] ";
	// }

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("name: ").append(this.getName()).append(", code: ").append(this.getCode()).append(", vendor: ").append(this.getVendorId()).append(", weak: ")
				.append(this.isWeak()).append(", grouped: ").append(this.isGrouped()).append(", type: ").append(this.getType()).append(", multiplicity: ")
				.append(this.getMultiplicityIndicator());
		if (this.isGrouped()) {
			for (AvpRepresentation child : this.getChildren()) {
				String childStr = child.toString().replace("\n", "\n---");
				sb.append("\n---" + childStr);
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		// code+vendor is enough by AVP def
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + (int) (vendor ^ (vendor >>> 32));
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
		// code+vendor is enough by AVP def
		AvpRepresentation other = (AvpRepresentation) obj;
		if (code != other.code)
			return false;
		if (vendor != other.vendor)
			return false;
		return true;
	}

}
