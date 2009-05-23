package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:18:25:13 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link SubscriptionIdAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscriptionIdAvpImpl extends GroupedAvpImpl implements SubscriptionIdAvp {

	private static transient Logger logger = Logger.getLogger(SubscriptionIdAvpImpl.class);

	public SubscriptionIdAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * getSubscriptionIdData()
	 */
	public String getSubscriptionIdData() {
		if (hasSubscriptionIdData()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Subscription_Id_Data);
			try {
				return rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Subscription_Id_Data);
				logger.error("Failure while trying to obtain Subscription-Id-Data AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * getSubscriptionIdType()
	 */
	public SubscriptionIdType getSubscriptionIdType() {
		if (hasSubscriptionIdType()) {
			int v = (int) super.getAvpAsUInt32(CreditControlAVPCodes.Subscription_Id_Type);
			return SubscriptionIdType.END_USER_E164.fromInt(v);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * hasSubscriptionIdData()
	 */
	public boolean hasSubscriptionIdData() {
		return super.hasAvp(CreditControlAVPCodes.Subscription_Id_Data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * hasSubscriptionIdType()
	 */
	public boolean hasSubscriptionIdType() {
		return super.hasAvp(CreditControlAVPCodes.Subscription_Id_Type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * setSubscriptionIdData(java.lang.String)
	 */
	public void setSubscriptionIdData(String data) {
		if (hasAvp(CreditControlAVPCodes.Subscription_Id_Data)) {
			throw new IllegalStateException("AVP Subscription-Id-Data is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Subscription_Id_Data);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsString(CreditControlAVPCodes.Subscription_Id_Data, data, mandatoryAvp == 1, protectedAvp == 1, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp#
	 * setSubscriptionIdType
	 * (net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdType)
	 */
	public void setSubscriptionIdType(SubscriptionIdType type) {
		if (hasAvp(CreditControlAVPCodes.Subscription_Id_Type)) {
			throw new IllegalStateException("AVP Subscription-Id-Type is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Subscription_Id_Type, type.getValue(), true);
	}

}
