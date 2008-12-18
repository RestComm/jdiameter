package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodesNotSupported;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

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
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class FinalUnitIndicationAvpImpl extends GroupedAvpImpl implements FinalUnitIndicationAvp {

	public FinalUnitIndicationAvpImpl(int code, long vendorId, int mnd,
			int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
		
	}

	public String[] getFilterIds() {
		if(!super.hasAvp(DiameterAvpCodesNotSupported.FILTER_ID))
			return null;
		{
			AvpSet set=super.avpSet.getAvps(DiameterAvpCodesNotSupported.FILTER_ID);
			String[] result=new String[set.size()];
			
			for(int index=0;index<set.size();index++)
			{
				Avp rawAvp=set.getAvpByIndex(index);
				try {
					result[index]=rawAvp.getUTF8String();
				} catch (AvpDataException e) {
					super.reportAvpFetchError("index="+index, DiameterAvpCodesNotSupported.FILTER_ID);
					e.printStackTrace();
				}
			}
			
			return result;
		}
	}

	public FinalUnitActionType getFinalUnitAction() {
		if(hasAvp(CreditControlAVPCodes.Final_Unit_Action))
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Final_Unit_Action);
			try {
				return FinalUnitActionType.REDIRECT.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError("due to: "+e, CreditControlAVPCodes.Final_Unit_Action);
				e.printStackTrace();
			} 
			
		}
		//FIXME: this is not correct, it has to have it.
		return null;
		
	}

	public RedirectServerAvp getRedirectServer() {
		if(hasRedirectServer())
		{
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Server);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Server);
			try {
				
				RedirectServerAvp result=new RedirectServerAvpImpl(CreditControlAVPCodes.Redirect_Server,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCodes.Redirect_Server);
				e.printStackTrace();
			}
		}
		return null;
	}

	public IPFilterRuleAvp[] getRestrictionFilterRules() {
		if(hasAvp(CreditControlAVPCodes.Restriction_Filter_Rule))
		{
			AvpSet set=super.avpSet.getAvps(CreditControlAVPCodes.Restriction_Filter_Rule);
			IPFilterRuleAvp[] result=new IPFilterRuleAvp[set.size()];
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Restriction_Filter_Rule);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(int index=0;index<set.size();index++)
			{
				Avp rawAvp=set.getAvpByIndex(index);
				try {
					result[index]=new IPFilterRuleAvpImpl(rawAvp.getOctetString(),rawAvp.getVendorId(),rawAvp.isMandatory(),rawAvp.isEncrypted());
				} catch (AvpDataException e) {
					super.reportAvpFetchError("index="+index, CreditControlAVPCodes.Restriction_Filter_Rule);
					e.printStackTrace();
				}
			}
			return result;
			
		}
		
		return null;
		
	}

	public boolean hasFinalUnitAction() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Action);
	}

	public boolean hasRedirectServer() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
	}

	public void setFilterId(String filterId) {
		this.setFilterIds(new String[]{filterId});
		
	}

	public void setFilterIds(String[] filterIds) {
		super.avpSet.removeAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(String avp: filterIds)
		{
			super.setAvpAsString(DiameterAvpCodesNotSupported.FILTER_ID, avp, false, false);
		}
	}

	public void setFinalUnitAction(FinalUnitActionType finalUnitAction) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Final_Unit_Action);
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Action);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCodes.Final_Unit_Action, finalUnitAction.getValue(),  true);
		
	}

	public void setRedirectServer(RedirectServerAvp redirectServer) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Redirect_Server);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Redirect_Server);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Redirect_Server, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCodes.Redirect_Server, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)redirectServer).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCodes.Redirect_Server,redirectServer.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCodes.Redirect_Server,redirectServer.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	public void setRestrictionFilterRule(IPFilterRuleAvp restrictionFilterRule) {
		this.setRestrictionFilterRules(new IPFilterRuleAvp[]{restrictionFilterRule});
		
	}

	public void setRestrictionFilterRules(
			IPFilterRuleAvp[] restrictionFilterRules) {
		super.avpSet.removeAvp(CreditControlAVPCodes.Restriction_Filter_Rule);
		//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Restriction_Filter_Rule);
		//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(IPFilterRuleAvpImpl avp: (IPFilterRuleAvpImpl[])restrictionFilterRules)
		{
			
			//if(avpRep.getVendorId()!=null)
			//	super.setAvpAsString(protectedAvp, avp.getRuleString(), Long.getLong(avpRep.getVendorId()),true, mandatoryAvp==1,protectedAvp==1, false);
			//else
				super.setAvpAsString(CreditControlAVPCodes.Restriction_Filter_Rule, avp.getRuleString(), true, false);
			
		}
		
	}

	

}
