package org.mobicents.diameter.dictionary;

import org.jdiameter.common.impl.validation.VAvpRepresentation;

/**
 * Start time:11:37:43 2009-08-11<br>
 * Project: diameter-parent<br>
 * Simple class which allows custom definition in validator and dictionary.
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class AvpRepresentation extends VAvpRepresentation implements Cloneable{

	public AvpRepresentation() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(int positionIndex, int code, long vendor, String multiplicityIndicator, String name) {
		super(positionIndex, code, vendor, multiplicityIndicator, name);
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(int code, long vendor, String name) {
		super(code, vendor, name);
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(int code, long vendor) {
		super(code, vendor);
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(String name, long vendor) {
		super(name, vendor);
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(String name, String description, int code, boolean mayEncrypt, String ruleMandatory, String ruleProtected, String ruleVendorBit, long vendorId, boolean constrained,
			String type) {
		super(name, description, code, mayEncrypt, ruleMandatory, ruleProtected, ruleVendorBit, vendorId, constrained, type);
		// TODO Auto-generated constructor stub
	}

	public AvpRepresentation(VAvpRepresentation clone) {
		super(clone);
		if(this.isGrouped())
		{
			for (VAvpRepresentation c : clone.getChildren()) {
				this.children.add(new AvpRepresentation((VAvpRepresentation) c));
			}
		}
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
