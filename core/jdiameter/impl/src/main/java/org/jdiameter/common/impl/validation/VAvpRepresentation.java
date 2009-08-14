package org.jdiameter.common.impl.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Start time:10:50:39 2009-05-26<br>
 * Project: diameter-parent<br>
 * Represents command avp, it stores info about presence, multiplicity, avp
 * code, vendor.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.189
 */
public class VAvpRepresentation implements Cloneable {

	/**
	 * <pre>
	 * 0     The AVP MUST NOT be present in the message.
	 * 0+    Zero or more instances of the AVP MAY be present in the
	 *       message.
	 * 0-1   Zero or one instance of the AVP MAY be present in the
	 *       message.  It is considered an error if there are more than
	 *       one instance of the AVP.
	 * 1     One instance of the AVP MUST be present in the message.
	 * 1+    At least one instance of the AVP MUST be present in the
	 *       message.
	 * </pre>
	 */
	public final static String _MP_NOT_ALLOWED = "0";
	public final static String _MP_ZERO_OR_MORE = "0+";
	public final static String _MP_ZERO_OR_ONE = "0-1";
	public final static String _MP_ONE = "1";
	public final static String _MP_ONE_AND_MORE = "1+";

	private final transient static int _FIX_POSITION_INDEX = -1;

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
	protected String type;
	protected boolean constrained;

	public final static String _DEFAULT_MANDATORY = "may";
	public final static String _DEFAULT_PROTECTED = "may";
	public final static String _DEFAULT_VENDOR = "mustnot";

	// ususally this will be -1, as only SessionId has fixed position
	private int positionIndex = _FIX_POSITION_INDEX;
	protected int code = -1;
	protected long vendor = 0;
	protected boolean allowed = true;
	protected String multiplicityIndicator = "0";
	protected String name = "Some-AVP";
	protected boolean grouped = false;
	protected List<VAvpRepresentation> children = new ArrayList<VAvpRepresentation>();
	protected boolean weak = false;

	public VAvpRepresentation(VAvpRepresentation clone) {

		this(-1, clone.code, clone.getVendorId(), clone.getMultiplicityIndicator(), clone.getName());
		
		this.allowed = clone.allowed;
		this.code = clone.code;
		this.grouped = clone.grouped;
		this.multiplicityIndicator = clone.multiplicityIndicator;
		this.name = clone.name;
		this.positionIndex = clone.positionIndex;
		this.vendor = clone.vendor;
		this.weak = clone.weak;

		this._mandatory = clone._mandatory;
		this._protected = clone._protected;
		this.constrained = clone.constrained;
		this.description = clone.description;
		this.mayEncrypt = clone.mayEncrypt;
		this.ruleMandatory = clone.ruleMandatory;
		this.ruleProtected = clone.ruleProtected;
		this.ruleVendorBit = clone.ruleVendorBit;
		this.type = clone.type;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	
	}

	public VAvpRepresentation() {
		super();
		this.weak = true;
	}

	/**
	 * Constructor used to represent weak children. - weak means its only
	 * defined by name in some other avp. After configuration parse procedure is
	 * complete weak children are resolved. Weak children should not be stored
	 * in Set or any other has structure, its due to nature of hashing, which is
	 * done on vendor and code, which for weak children is always different than
	 * fully defined avp rep. <br>
	 * This constructor should be generaly used by extending classes, as well as
	 * no arg constructor.
	 * 
	 * @param name
	 * @param vendor
	 */
	public VAvpRepresentation(String name, long vendor) {
		super();
		this.name = name;
		this.vendor = vendor;
		this.weak = true;
	}

	/**
	 * This constructor is used my validator to lookup correct representation.
	 * Its hash and equals methods will match to fully populated avp
	 * representation in any data structure
	 * 
	 * @param code
	 * @param vendor
	 */
	public VAvpRepresentation(int code, long vendor) {
		super();
		this.code = code;
		this.vendor = vendor;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public VAvpRepresentation(int positionIndex, int code, long vendor, String multiplicityIndicator, String name) {
		super();
		this.positionIndex = positionIndex;
		this.code = code;
		this.vendor = vendor;
		this.multiplicityIndicator = multiplicityIndicator;
		this.name = name;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public VAvpRepresentation(int code, long vendor, String name) {
		super();
		this.positionIndex = -1;
		this.code = code;
		this.vendor = vendor;
		this.multiplicityIndicator = _MP_ZERO_OR_MORE;
		this.name = name;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public VAvpRepresentation(String name, String description, int code, boolean mayEncrypt, String ruleMandatory, String ruleProtected, String ruleVendorBit, long vendorId, boolean constrained,
			String type) {

		// zero and more, since its definition.
		this(-1, code, vendorId, _MP_ZERO_OR_MORE, name);
		if (code == -1) {
			throw new IllegalStateException("xxx");
		}
		this.description = description;

		this.mayEncrypt = mayEncrypt;
		this.ruleMandatory = ruleMandatory;
		this.ruleProtected = ruleProtected;
		this.ruleVendorBit = ruleVendorBit;

		if (this.ruleMandatory == null || this.ruleMandatory.equals(""))
			this.ruleMandatory = _DEFAULT_MANDATORY;
		if (this.ruleProtected == null || this.ruleProtected.equals(""))
			this.ruleProtected = _DEFAULT_PROTECTED;
		if (this.ruleVendorBit == null || this.ruleVendorBit.equals(""))
			this.ruleVendorBit = _DEFAULT_VENDOR;

		this.constrained = constrained;
		this.type = type;
		this._mandatory = this.ruleMandatory.equals("must");
		this._protected = this.ruleProtected.equals("must");
		if (type.equals(Type.Grouped.toString())) {
			this.setGrouped(true);
		}
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public boolean isPositionFixed() {
		return this.positionIndex == _FIX_POSITION_INDEX;
	}

	public void markFixPosition(int index) {
		this.positionIndex = index;
	}

	public boolean isCountValidForMultiplicity(int avpCount) {

		// This covver nto allowed
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

	public String getMultiplicityIndicator() {
		return multiplicityIndicator;
	}

	public String getName() {
		return name;
	}

	public boolean isGrouped() {
		return grouped;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	public List<VAvpRepresentation> getChildren() {
		return children;
	}

	public void setChildren(List<VAvpRepresentation> children) {
		this.children = children;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setVendorId(long vendor) {
		this.vendor = vendor;
	}

	public void setMultiplicityIndicator(String multiplicityIndicator) {
		this.multiplicityIndicator = multiplicityIndicator;
		if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isWeak() {
		return weak;
	}

	public void markWeak(boolean isWeak) {
		this.weak = isWeak;
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

	// public String toString() {
	// return this.getName() + "@" + hashCode() + " Name[" + getName() +
	// "] Code[" + getCode() + "] Vendor[" + getVendorId() + "] MLP[" +
	// getMultiplicityIndicator() + "] Allowed[" + isAllowed() + "] ";
	// }

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("name: ").append(this.getName()).append(", code: ").append(this.getCode()).append(", vendor: ").append(this.getVendorId()).append(", weak: ").append(this.isWeak()).append(
				", grouped: ").append(this.isGrouped()).append(", type: ").append(this.getType()).append(", multiplicity: ").append(this.getMultiplicityIndicator());
		if (this.isGrouped()) {
			for (VAvpRepresentation child : this.getChildren()) {
				String childStr = child.toString().replace("\n", "\n---");
				sb.append("\n---" + childStr);
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
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
		VAvpRepresentation other = (VAvpRepresentation) obj;
		if (code != other.code)
			return false;
		if (vendor != other.vendor)
			return false;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		VAvpRepresentation clone = (VAvpRepresentation) super.clone();
		clone.allowed = this.allowed;
		clone.code = this.code;
		clone.grouped = this.grouped;
		clone.multiplicityIndicator = this.multiplicityIndicator;
		clone.name = this.name;
		clone.positionIndex = this.positionIndex;
		clone.vendor = this.vendor;
		clone.weak = this.weak;

		clone._mandatory = this._mandatory;
		clone._protected = this._protected;
		clone.constrained = this.constrained;
		clone.description = this.description;
		clone.mayEncrypt = this.mayEncrypt;
		clone.ruleMandatory = this.ruleMandatory;
		clone.ruleProtected = this.ruleProtected;
		clone.ruleVendorBit = this.ruleVendorBit;
		clone.type = this.type;
		
		List<VAvpRepresentation> cloneChildren = new ArrayList<VAvpRepresentation>();
		clone.children = cloneChildren;
		for (VAvpRepresentation c : this.children) {
			cloneChildren.add((VAvpRepresentation) c.clone());
		}
		return clone;
	}

}
