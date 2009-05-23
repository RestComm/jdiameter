package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodesNotSupported;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.IPFilterRuleAvpImpl;

/**
 * Start time:13:51:00 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link FinalUnitIndicationAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class FinalUnitIndicationAvpImpl extends GroupedAvpImpl implements FinalUnitIndicationAvp {

	private static transient Logger logger = Logger.getLogger(FinalUnitIndicationAvpImpl.class);

	public FinalUnitIndicationAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #getFilterIds()
	 */
	public String[] getFilterIds() {
		if (!super.hasAvp(DiameterAvpCodesNotSupported.FILTER_ID)) {
			return null;
		}

		AvpSet set = super.avpSet.getAvps(DiameterAvpCodesNotSupported.FILTER_ID);
		String[] result = new String[set.size()];

		for (int index = 0; index < set.size(); index++) {
			Avp rawAvp = set.getAvpByIndex(index);
			try {
				result[index] = rawAvp.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), DiameterAvpCodesNotSupported.FILTER_ID);
				logger.error("Failure while trying to obtain Filter-Id AVP.", e);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #getFinalUnitAction()
	 */
	public FinalUnitActionType getFinalUnitAction() {
		if (hasAvp(CreditControlAVPCodes.Final_Unit_Action)) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Final_Unit_Action);
			try {
				return FinalUnitActionType.REDIRECT.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Final_Unit_Action);
				logger.error("Failure while trying to obtain Final-Unit-Action AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #getRedirectServer()
	 */
	public RedirectServerAvp getRedirectServer() {
		if (hasRedirectServer()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Server);
			try {

				return new RedirectServerAvpImpl(CreditControlAVPCodes.Redirect_Server, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Redirect_Server);
				logger.error("Failure while trying to obtain Redirect-Server AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #getRestrictionFilterRules()
	 */
	public IPFilterRuleAvp[] getRestrictionFilterRules() {
		if (hasAvp(CreditControlAVPCodes.Restriction_Filter_Rule)) {
			AvpSet set = super.avpSet.getAvps(CreditControlAVPCodes.Restriction_Filter_Rule);
			IPFilterRuleAvp[] result = new IPFilterRuleAvp[set.size()];

			for (int index = 0; index < set.size(); index++) {
				Avp rawAvp = set.getAvpByIndex(index);
				try {
					result[index] = new IPFilterRuleAvpImpl(rawAvp.getOctetString(), rawAvp.getVendorId(), rawAvp.isMandatory(), rawAvp.isEncrypted());
				} catch (AvpDataException e) {
					reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Restriction_Filter_Rule);
					logger.error("Failure while trying to obtain Restriction-Filter-Rule AVP.", e);
				}
			}
			return result;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #hasFinalUnitAction()
	 */
	public boolean hasFinalUnitAction() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #hasRedirectServer()
	 */
	public boolean hasRedirectServer() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setFilterId(java.lang.String)
	 */
	public void setFilterId(String filterId) {
		this.setFilterIds(new String[] { filterId });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setFilterIds(java.lang.String[])
	 */
	public void setFilterIds(String[] filterIds) {
		if (hasAvp(DiameterAvpCodesNotSupported.FILTER_ID)) {
			throw new IllegalStateException("AVP Filter-Id is already present in message and cannot be overwritten.");
		}
		// super.avpSet.removeAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		for (String avp : filterIds) {
			super.setAvpAsString(DiameterAvpCodesNotSupported.FILTER_ID, avp, false, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setFinalUnitAction
	 * (net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType)
	 */
	public void setFinalUnitAction(FinalUnitActionType finalUnitAction) {
		if (hasAvp(CreditControlAVPCodes.Final_Unit_Action)) {
			throw new IllegalStateException("AVP Final-Unit-Action is already present in message and cannot be overwritten.");
		}

		// super.avpSet.removeAvp(CreditControlAVPCodes.Final_Unit_Action);
		super.setAvpAsUInt32(CreditControlAVPCodes.Final_Unit_Action, finalUnitAction.getValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setRedirectServer
	 * (net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp)
	 */
	public void setRedirectServer(RedirectServerAvp redirectServer) {
		if (hasAvp(CreditControlAVPCodes.Redirect_Server)) {
			throw new IllegalStateException("AVP Redirect-Server is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Server);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Redirect_Server);
		super.avpSet.addAvp(CreditControlAVPCodes.Redirect_Server, redirectServer.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setRestrictionFilterRule
	 * (net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp)
	 */
	public void setRestrictionFilterRule(IPFilterRuleAvp restrictionFilterRule) {
		this.setRestrictionFilterRules(new IPFilterRuleAvp[] { restrictionFilterRule });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp
	 * #setRestrictionFilterRules
	 * (net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp[])
	 */
	public void setRestrictionFilterRules(IPFilterRuleAvp[] restrictionFilterRules) {
		if (hasAvp(CreditControlAVPCodes.Restriction_Filter_Rule)) {
			throw new IllegalStateException("AVP Restriction-Filter-Rule is already present in message and cannot be overwritten.");
		}

		// super.avpSet.removeAvp(CreditControlAVPCodes.Restriction_Filter_Rule);
		for (IPFilterRuleAvpImpl avp : (IPFilterRuleAvpImpl[]) restrictionFilterRules) {
			super.setAvpAsString(CreditControlAVPCodes.Restriction_Filter_Rule, avp.getRuleString(), true, false);
		}
	}

}
