/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceParameterInfoAvpImpl extends GroupedAvpImpl implements
		ServiceParameterInfoAvp {

	public ServiceParameterInfoAvpImpl(int code, long vendorId, int mnd,
			int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterType()
	 */
	public long getServiceParameterType() {
		if(hasServiceParameterType())
			return super.getAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
		else
			return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterValue()
	 */
	public byte[] getServiceParameterValue() {
		if(hasServiceParameterValue())
			return super.getAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
		else 
			return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterType()
	 */
	public boolean hasServiceParameterType() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterValue()
	 */
	public boolean hasServiceParameterValue() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterType(long)
	 */
	public void setServiceParameterType(long serviceParameterType) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE, Long.valueOf(avpRep.getVendorId()).longValue(), serviceParameterType, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE, serviceParameterType, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterValue(byte[])
	 */
	public void setServiceParameterValue(byte[] serviceParameterValue) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE, Long.valueOf(avpRep.getVendorId()).longValue(), serviceParameterValue, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE, serviceParameterValue, mandatoryAvp==1, protectedAvp==1, true);

	}

}
/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;

import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:14:01 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceParameterInfoAvpImpl extends GroupedAvpImpl implements
		ServiceParameterInfoAvp {

	public ServiceParameterInfoAvpImpl(int code, long vendorId, int mnd,
			int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterType()
	 */
	public long getServiceParameterType() {
		if(hasServiceParameterType())
			return super.getAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
		else
			return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#getServiceParameterValue()
	 */
	public byte[] getServiceParameterValue() {
		if(hasServiceParameterValue())
			return super.getAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
		else 
			return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterType()
	 */
	public boolean hasServiceParameterType() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#hasServiceParameterValue()
	 */
	public boolean hasServiceParameterValue() {
		return super.hasAvp(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterType(long)
	 */
	public void setServiceParameterType(long serviceParameterType) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_TYPE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE, Long.valueOf(avpRep.getVendorId()).longValue(), serviceParameterType, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsUInt32(CreditControlAVPCode.SERVICE_PARAMETER_TYPE, serviceParameterType, mandatoryAvp==1, protectedAvp==1, true);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp#setServiceParameterValue(byte[])
	 */
	public void setServiceParameterValue(byte[] serviceParameterValue) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.SERVICE_PARAMETER_VALUE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		
		//super.setAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE, Long.valueOf(avpRep.getVendorId()).longValue(), serviceParameterValue, mandatoryAvp==1, protectedAvp==1, true);
		super.setAvpAsByteArray(CreditControlAVPCode.SERVICE_PARAMETER_VALUE, serviceParameterValue, mandatoryAvp==1, protectedAvp==1, true);

	}

}
