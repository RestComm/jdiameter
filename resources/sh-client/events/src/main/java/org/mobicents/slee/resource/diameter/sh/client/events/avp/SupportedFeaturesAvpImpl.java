package org.mobicents.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;


public class SupportedFeaturesAvpImpl extends GroupedAvpImpl implements SupportedFeaturesAvp {

	public SupportedFeaturesAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);

	}

	public long getFeatureList() {
		return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST);
	}

	public long getFeatureListId() {
		return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID);
	}

	public boolean hasFeatureList() {
		Avp rawAvp = super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST);
		return rawAvp != null;
	}

	public boolean hasFeatureListId() {

		Avp rawAvp = super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST_ID);
		return rawAvp != null;
	}

	public void setFeatureList(long featureList) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.FEATURE_LIST,10415);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		
		super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST, featureList, mandatoryAvp==1, true);
	}

	public void setFeatureListId(long featureListId) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.FEATURE_LIST_ID,10415);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID, featureListId, mandatoryAvp==1, true);
	}

	public boolean hasVendorId() {
		Avp rawAvp = super.avpSet.getAvp(Avp.VENDOR_ID);
		return rawAvp != null;
	}

	public void setVendorId(long vendorId) {
		if (hasVendorId())
			throw new IllegalStateException("Cant set vendor Id again!!!!");
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(Avp.VENDOR_ID);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		super.setAvpAsUInt32(Avp.VENDOR_ID, vendorId, mandatoryAvp==1, true);

	}
}
