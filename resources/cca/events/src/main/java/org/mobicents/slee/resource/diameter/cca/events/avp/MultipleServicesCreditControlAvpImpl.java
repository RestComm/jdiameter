package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:29:27 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link MultipleServicesCreditControlAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MultipleServicesCreditControlAvpImpl extends GroupedAvpImpl implements MultipleServicesCreditControlAvp {

	private static transient Logger logger = Logger.getLogger(MultipleServicesCreditControlAvpImpl.class);

	public MultipleServicesCreditControlAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getFinalUnitIndication()
	 */
	public FinalUnitIndicationAvp getFinalUnitIndication() {
		if (hasFinalUnitIndication()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Final_Unit_Indication);

			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

			try {
				return new FinalUnitIndicationAvpImpl(CreditControlAVPCodes.Final_Unit_Indication, Long.valueOf(avpRep.getVendorId()), mandatoryAvp, protectedAvp, rawAvp.getRaw());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Final_Unit_Indication);
				logger.error("Failure while trying to obtain Final-Unit-Indication AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getGrantedServiceUnit()
	 */
	public GrantedServiceUnitAvp getGrantedServiceUnit() {
		if (hasGrantedServiceUnit()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Granted_Service_Unit);

			try {
				return new GrantedServiceUnitAvpImpl(CreditControlAVPCodes.Granted_Service_Unit, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Granted_Service_Unit);
				logger.error("Failure while trying to obtain Granted-Service-Unit AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getGsuPoolReferences()
	 */
	public GSUPoolReferenceAvp[] getGsuPoolReferences() {
		if (super.hasAvp(CreditControlAVPCodes.G_S_U_Pool_Reference)) {
			AvpSet rawAvps = super.avpSet.getAvps(CreditControlAVPCodes.G_S_U_Pool_Reference);

			GSUPoolReferenceAvp[] result = new GSUPoolReferenceAvp[rawAvps.size()];

			for (int index = 0; index < rawAvps.size(); index++) {
				Avp rawAvp = rawAvps.getAvpByIndex(index);
				try {
					result[index] = new GSUPoolReferenceAvpImpl(CreditControlAVPCodes.G_S_U_Pool_Reference, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp
							.getRaw());
				} catch (Exception e) {
					reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), CreditControlAVPCodes.G_S_U_Pool_Reference);
					logger.error("Failure while trying to obtain G-S-U-Pool-Reference AVP.", e);
				}
			}

			return result;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getRatingGroup()
	 */
	public long getRatingGroup() {
		if (hasRatingGroup()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Rating_Group);

			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Rating_Group);
				logger.error("Failure while trying to obtain Rating-Group AVP.", e);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getRequestedServiceUnit()
	 */
	public RequestedServiceUnitAvp getRequestedServiceUnit() {
		if (hasRequestedServiceUnit()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Requested_Service_Unit);

			try {
				return new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
			} catch (AvpDataException e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Requested_Service_Unit);
				logger.error("Failure while trying to obtain Requested-Service-Unit AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getResultCode()
	 */
	public long getResultCode() {
		if (hasResultCode()) {
			Avp rawAvp = super.avpSet.getAvp(DiameterAvpCodes.RESULT_CODE);
			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), DiameterAvpCodes.RESULT_CODE);
				logger.error("Failure while trying to obtain Result-Code AVP.", e);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getServiceIdentifiers()
	 */
	public long[] getServiceIdentifiers() {
		return super.getAllAvpAsUInt32(CreditControlAVPCodes.Service_Identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getTariffChangeUsage()
	 */
	public TariffChangeUsageType getTariffChangeUsage() {
		if (hasTariffChangeUsage()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Change_Usage);

			try {
				return TariffChangeUsageType.UNIT_AFTER_TARIFF_CHANGE.fromInt((int) rawAvp.getUnsigned32());
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Tariff_Change_Usage);
				logger.error("Failure while trying to obtain Tariff-Change-Usage AVP.", e);
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getUsedServiceUnits()
	 */
	public UsedServiceUnitAvp[] getUsedServiceUnits() {
		if (super.hasAvp(CreditControlAVPCodes.Used_Service_Unit)) {
			AvpSet set = super.avpSet.getAvps(CreditControlAVPCodes.Used_Service_Unit);

			UsedServiceUnitAvp[] avps = new UsedServiceUnitAvp[set.size()];

			for (int index = 0; index < set.size(); index++) {
				try {
					Avp rawAvp = set.getAvpByIndex(index);
					UsedServiceUnitAvp avp = new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0,
							rawAvp.getRaw());
					avps[index] = avp;
				} catch (Exception e) {
					reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Used_Service_Unit);
					logger.error("Failure while trying to obtain Used-Service-Unit AVP.", e);
				}
			}

			return avps;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#getValidityTime()
	 */
	public long getValidityTime() {
		if (hasValidityTime()) {
			Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Validity_Time);

			try {
				return rawAvp.getUnsigned32();
			} catch (Exception e) {
				reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Validity_Time);
				logger.error("Failure while trying to obtain Validity-Time AVP.", e);
			}
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Granted_Service_Unit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasRatingGroup()
	 */
	public boolean hasRatingGroup() {
		return super.hasAvp(CreditControlAVPCodes.Rating_Group);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasRequestedServiceUnit()
	 */
	public boolean hasRequestedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasResultCode()
	 */
	public boolean hasResultCode() {
		return super.hasAvp(DiameterAvpCodes.RESULT_CODE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasTariffChangeUsage()
	 */
	public boolean hasTariffChangeUsage() {
		return super.hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCodes.Validity_Time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setFinalUnitIndication(net.java.slee.resource
	 * .diameter.cca.events.avp.FinalUnitIndicationAvp)
	 */
	public void setFinalUnitIndication(FinalUnitIndicationAvp finalUnitIndication) {
		if (hasAvp(CreditControlAVPCodes.Final_Unit_Indication)) {
			throw new IllegalStateException("AVP Final-Unit-Indication is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Final_Unit_Indication, finalUnitIndication.longValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setGrantedServiceUnit(net.java.slee.resource
	 * .diameter.cca.events.avp.GrantedServiceUnitAvp)
	 */
	public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) {
		if (hasAvp(CreditControlAVPCodes.Granted_Service_Unit)) {
			throw new IllegalStateException("AVP Granted-Service-Unit is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Granted_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Granted_Service_Unit);
		super.avpSet.addAvp(CreditControlAVPCodes.Granted_Service_Unit, grantedServiceUnit.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setGsuPoolReference(net.java.slee.resource
	 * .diameter.cca.events.avp.GSUPoolReferenceAvp)
	 */
	public void setGsuPoolReference(GSUPoolReferenceAvp gsuPoolReference) {
		this.setGsuPoolReferences(new GSUPoolReferenceAvp[] { gsuPoolReference });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setGsuPoolReferences(net.java.slee.resource
	 * .diameter.cca.events.avp.GSUPoolReferenceAvp[])
	 */
	public void setGsuPoolReferences(GSUPoolReferenceAvp[] gsuPoolReferences) {
		if (hasAvp(CreditControlAVPCodes.G_S_U_Pool_Reference)) {
			throw new IllegalStateException("AVP G-S-U-Pool-Reference is already present in message and cannot be overwritten.");
		}

		// super.avpSet.removeAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.G_S_U_Pool_Reference);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for (GSUPoolReferenceAvpImpl gsuPoolReference : (GSUPoolReferenceAvpImpl[]) gsuPoolReferences) {
			super.avpSet.addAvp(CreditControlAVPCodes.G_S_U_Pool_Reference, gsuPoolReference.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#setRatingGroup(long)
	 */
	public void setRatingGroup(long ratingGroup) {
		if (hasAvp(CreditControlAVPCodes.Rating_Group)) {
			throw new IllegalStateException("AVP Rating-Group is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Rating_Group, ratingGroup, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setRequestedServiceUnit(net.java.slee.resource
	 * .diameter.cca.events.avp.RequestedServiceUnitAvp)
	 */
	public void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit) {
		if (hasAvp(CreditControlAVPCodes.Requested_Service_Unit)) {
			throw new IllegalStateException("AVP Requested-Service-Unit is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Requested_Service_Unit);
		super.avpSet.addAvp(CreditControlAVPCodes.Requested_Service_Unit, requestedServiceUnit.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#setResultCode(long)
	 */
	public void setResultCode(long resultCode) {
		if (hasAvp(DiameterAvpCodes.RESULT_CODE)) {
			throw new IllegalStateException("AVP Result-Code is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(DiameterAvpCodes.RESULT_CODE, resultCode, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#setServiceIdentifier(long)
	 */
	public void setServiceIdentifier(long serviceIdentifier) {
		this.setServiceIdentifiers(new long[] { serviceIdentifier });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#setServiceIdentifiers(long[])
	 */
	public void setServiceIdentifiers(long[] serviceIdentifiers) {
		if (hasAvp(CreditControlAVPCodes.Service_Identifier)) {
			throw new IllegalStateException("AVP Service-Identifier is already present in message and cannot be overwritten.");
		}

		// super.avpSet.removeAvp(CreditControlAVPCodes.Service_Identifier);
		for (long serviceIdentifier : serviceIdentifiers) {
			super.setAvpAsUInt32(CreditControlAVPCodes.Service_Identifier, serviceIdentifier, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setTariffChangeUsage(net.java.slee.resource
	 * .diameter.cca.events.avp.TariffChangeUsageType)
	 */
	public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
		if (hasAvp(CreditControlAVPCodes.Tariff_Change_Usage)) {
			throw new IllegalStateException("AVP Tariff-Change-Usage is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Tariff_Change_Usage, tariffChangeUsage.getValue(), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setUsedServiceUnit(net.java.slee.resource
	 * .diameter.cca.events.avp.UsedServiceUnitAvp)
	 */
	public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) {
		this.setUsedServiceUnits(new UsedServiceUnitAvp[] { usedServiceUnit });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp
	 * #setUsedServiceUnits(net.java.slee.resource
	 * .diameter.cca.events.avp.UsedServiceUnitAvp[])
	 */
	public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) {
		if (hasAvp(CreditControlAVPCodes.Used_Service_Unit)) {
			throw new IllegalStateException("AVP Used-Service-Unit is already present in message and cannot be overwritten.");
		}

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		// super.avpSet.removeAvp(CreditControlAVPCodes.Used_Service_Unit);

		for (UsedServiceUnitAvp usedServiceUnit : usedServiceUnits) {
			super.avpSet.addAvp(CreditControlAVPCodes.Used_Service_Unit, usedServiceUnit.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.events.avp.
	 * MultipleServicesCreditControlAvp#setValidityTime(long)
	 */
	public void setValidityTime(long validityTime) {
		if (hasAvp(CreditControlAVPCodes.Validity_Time)) {
			throw new IllegalStateException("AVP Validity-Time is already present in message and cannot be overwritten.");
		}

		super.setAvpAsUInt32(CreditControlAVPCodes.Validity_Time, validityTime, true);
	}

}
