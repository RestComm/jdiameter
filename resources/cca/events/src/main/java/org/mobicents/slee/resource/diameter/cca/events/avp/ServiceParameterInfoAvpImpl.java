package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link ServiceParameterInfoAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceParameterInfoAvpImpl extends GroupedAvpImpl implements ServiceParameterInfoAvp {

	public ServiceParameterInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #getServiceParameterType()
	 */
	public long getServiceParameterType() {
		if (hasServiceParameterType()) {
			return super.getAvpAsUInt32(CreditControlAVPCodes.Service_Parameter_Type);
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #getServiceParameterValue()
	 */
	public byte[] getServiceParameterValue() {
		if (hasServiceParameterValue()) {
			return super.getAvpAsByteArray(CreditControlAVPCodes.Service_Parameter_Value);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #hasServiceParameterType()
	 */
	public boolean hasServiceParameterType() {
		return super.hasAvp(CreditControlAVPCodes.Service_Parameter_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #hasServiceParameterValue()
	 */
	public boolean hasServiceParameterValue() {
		return super.hasAvp(CreditControlAVPCodes.Service_Parameter_Value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #setServiceParameterType(long)
	 */
	public void setServiceParameterType(long serviceParameterType) {
		if (hasAvp(CreditControlAVPCodes.Service_Parameter_Type)) {
			throw new IllegalStateException("AVP Service-Parameter-Type is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Service_Parameter_Type, serviceParameterType, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp
	 * #setServiceParameterValue(byte[])
	 */
	public void setServiceParameterValue(byte[] serviceParameterValue) {
		if (hasAvp(CreditControlAVPCodes.Service_Parameter_Value)) {
			throw new IllegalStateException("AVP Service-Parameter-Value is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Parameter_Value);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsByteArray(CreditControlAVPCodes.Service_Parameter_Value, serviceParameterValue, mandatoryAvp == 1, protectedAvp == 1, true);
	}

}
