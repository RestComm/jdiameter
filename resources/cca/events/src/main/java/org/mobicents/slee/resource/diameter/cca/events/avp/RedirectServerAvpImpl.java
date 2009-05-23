package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:17:27:03 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link RedirectServerAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RedirectServerAvpImpl extends GroupedAvpImpl implements RedirectServerAvp {

	private static transient Logger logger = Logger.getLogger(RedirectServerAvpImpl.class);

	public RedirectServerAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * getRedirectAddressType()
	 */
	public RedirectAddressType getRedirectAddressType() {
		if (hasRedirectAddressType()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Address_Type);
			try {
				return RedirectAddressType.IPv4_Address.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Redirect_Address_Type);
				logger.error("Failure while trying to obtain Redirect-Address-Type AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * getRedirectServerAddress()
	 */
	public String getRedirectServerAddress() {
		if (hasRedirectServerAddress()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Server_Address);
			try {
				return rawAvp.getUTF8String();
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Redirect_Server_Address);
				logger.error("Failure while trying to obtain Redirect-Server-Address AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * hasRedirectAddressType()
	 */
	public boolean hasRedirectAddressType() {
		return super.hasAvp(CreditControlAVPCodes.Redirect_Address_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * hasRedirectServerAddress()
	 */
	public boolean hasRedirectServerAddress() {
		return super.hasAvp(CreditControlAVPCodes.Redirect_Server_Address);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * setRedirectAddressType
	 * (net.java.slee.resource.diameter.cca.events.avp.RedirectAddressType)
	 */
	public void setRedirectAddressType(RedirectAddressType redirectAddressType) {
		if (hasAvp(CreditControlAVPCodes.Redirect_Address_Type)) {
			throw new IllegalStateException("AVP Redirect-Address-Type is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Redirect_Address_Type, redirectAddressType.getValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp#
	 * setRedirectServerAddress(java.lang.String)
	 */
	public void setRedirectServerAddress(String redirectServerAddress) {
		if (hasAvp(CreditControlAVPCodes.Redirect_Server_Address)) {
			throw new IllegalStateException("AVP Redirect-Server-Address is already present in message and cannot be overwritten.");
		}

		super.setAvpAsString(CreditControlAVPCodes.Redirect_Server_Address, redirectServerAddress, false, true);
	}

}
