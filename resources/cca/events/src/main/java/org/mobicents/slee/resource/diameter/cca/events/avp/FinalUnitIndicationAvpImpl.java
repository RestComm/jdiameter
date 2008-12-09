/**
 * Start time:13:51:00 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodesNotSupported;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
		if(hasAvp(CreditControlAVPCode.FINAL_UNIT_ACTION))
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
			try {
				return FinalUnitActionType.REDIRECT.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError("due to: "+e, CreditControlAVPCode.FINAL_UNIT_ACTION);
				e.printStackTrace();
			} 
			
		}
		//FIXME: this is not correct, it has to have it.
		return null;
		
	}

	public RedirectServerAvp getRedirectServer() {
		if(hasRedirectServer())
		{
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
			try {
				
				RedirectServerAvp result=new RedirectServerAvpImpl(CreditControlAVPCode.REDIRECT_SERVER,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.REDIRECT_SERVER);
				e.printStackTrace();
			}
		}
		return null;
	}

	public IPFilterRuleAvp[] getRestrictionFilterRules() {
		if(hasAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE))
		{
			AvpSet set=super.avpSet.getAvps(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
			IPFilterRuleAvp[] result=new IPFilterRuleAvp[set.size()];
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(int index=0;index<set.size();index++)
			{
				Avp rawAvp=set.getAvpByIndex(index);
				try {
					result[index]=new IPFilterRuleAvpImpl(rawAvp.getOctetString(),rawAvp.getVendorId(),rawAvp.isMandatory(),rawAvp.isEncrypted());
				} catch (AvpDataException e) {
					super.reportAvpFetchError("index="+index, CreditControlAVPCode.RESTRICTION_FILTER_RULE);
					e.printStackTrace();
				}
			}
			return result;
			
		}
		
		return null;
		
	}

	public boolean hasFinalUnitAction() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
	}

	public boolean hasRedirectServer() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
	}

	public void setFilterId(String filterId) {
		this.setFilterIds(new String[]{filterId});
		
	}

	public void setFilterIds(String[] filterIds) {
		super.avpSet.removeAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(String avp: filterIds)
		{
			super.setAvpAsString(protectedAvp, avp, false, mandatoryAvp==1,protectedAvp==1, false);
		}
	}

	public void setFinalUnitAction(FinalUnitActionType finalUnitAction) {
		super.avpSet.removeAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.FINAL_UNIT_ACTION, finalUnitAction.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		
	}

	public void setRedirectServer(RedirectServerAvp redirectServer) {
		super.avpSet.removeAvp(CreditControlAVPCode.REDIRECT_SERVER);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REDIRECT_SERVER, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REDIRECT_SERVER, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)redirectServer).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.REDIRECT_SERVER,redirectServer.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.REDIRECT_SERVER,redirectServer.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	public void setRestrictionFilterRule(IPFilterRuleAvp restrictionFilterRule) {
		this.setRestrictionFilterRules(new IPFilterRuleAvp[]{restrictionFilterRule});
		
	}

	public void setRestrictionFilterRules(
			IPFilterRuleAvp[] restrictionFilterRules) {
		super.avpSet.removeAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(IPFilterRuleAvpImpl avp: (IPFilterRuleAvpImpl[])restrictionFilterRules)
		{
			
			//if(avpRep.getVendorId()!=null)
			//	super.setAvpAsString(protectedAvp, avp.getRuleString(), Long.getLong(avpRep.getVendorId()),true, mandatoryAvp==1,protectedAvp==1, false);
			//else
				super.setAvpAsString(protectedAvp, avp.getRuleString(), true, mandatoryAvp==1,protectedAvp==1, false);
			
		}
		
	}

	

}
/**
 * Start time:13:51:00 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodesNotSupported;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
		if(hasAvp(CreditControlAVPCode.FINAL_UNIT_ACTION))
		{
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
			try {
				return FinalUnitActionType.REDIRECT.fromInt(rawAvp.getInteger32());
			} catch (Exception e) {
				super.reportAvpFetchError("due to: "+e, CreditControlAVPCode.FINAL_UNIT_ACTION);
				e.printStackTrace();
			} 
			
		}
		//FIXME: this is not correct, it has to have it.
		return null;
		
	}

	public RedirectServerAvp getRedirectServer() {
		if(hasRedirectServer())
		{
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			Avp rawAvp=super.avpSet.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
			try {
				
				RedirectServerAvp result=new RedirectServerAvpImpl(CreditControlAVPCode.REDIRECT_SERVER,rawAvp.getVendorId(),rawAvp.isMandatory()?1:0,rawAvp.isEncrypted()?1:0,rawAvp.getRaw());
				return result;
			} catch (AvpDataException e) {
				super.reportAvpFetchError(""+e, CreditControlAVPCode.REDIRECT_SERVER);
				e.printStackTrace();
			}
		}
		return null;
	}

	public IPFilterRuleAvp[] getRestrictionFilterRules() {
		if(hasAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE))
		{
			AvpSet set=super.avpSet.getAvps(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
			IPFilterRuleAvp[] result=new IPFilterRuleAvp[set.size()];
			//AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
			//int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			//int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			for(int index=0;index<set.size();index++)
			{
				Avp rawAvp=set.getAvpByIndex(index);
				try {
					result[index]=new IPFilterRuleAvpImpl(rawAvp.getOctetString(),rawAvp.getVendorId(),rawAvp.isMandatory(),rawAvp.isEncrypted());
				} catch (AvpDataException e) {
					super.reportAvpFetchError("index="+index, CreditControlAVPCode.RESTRICTION_FILTER_RULE);
					e.printStackTrace();
				}
			}
			return result;
			
		}
		
		return null;
		
	}

	public boolean hasFinalUnitAction() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
	}

	public boolean hasRedirectServer() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
	}

	public void setFilterId(String filterId) {
		this.setFilterIds(new String[]{filterId});
		
	}

	public void setFilterIds(String[] filterIds) {
		super.avpSet.removeAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodesNotSupported.FILTER_ID);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(String avp: filterIds)
		{
			super.setAvpAsString(protectedAvp, avp, false, mandatoryAvp==1,protectedAvp==1, false);
		}
	}

	public void setFinalUnitAction(FinalUnitActionType finalUnitAction) {
		super.avpSet.removeAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_ACTION);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		super.setAvpAsUInt32(CreditControlAVPCode.FINAL_UNIT_ACTION, finalUnitAction.getValue(), mandatoryAvp==1, protectedAvp==1, true);
		
	}

	public void setRedirectServer(RedirectServerAvp redirectServer) {
		super.avpSet.removeAvp(CreditControlAVPCode.REDIRECT_SERVER);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.REDIRECT_SERVER);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
		//AvpSet inserted=null;
		//if(avpRep.getVendorId()!=null)
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REDIRECT_SERVER, Long.getLong(avpRep.getVendorId()), mandatoryAvp==1, protectedAvp==1);
		//else
		//	inserted=super.avpSet.addGroupedAvp(CreditControlAVPCode.REDIRECT_SERVER, mandatoryAvp==1, protectedAvp==1);
		//inserted.addAvp(((GroupedAvpImpl)redirectServer).getRaw());
		//if(avpRep.getVendorId()!=null)
		//	super.avpSet.addAvp(CreditControlAVPCode.REDIRECT_SERVER,redirectServer.byteArrayValue(),Long.getLong(avpRep.getVendorId()),mandatoryAvp==1, protectedAvp==1);
		//else
			super.avpSet.addAvp(CreditControlAVPCode.REDIRECT_SERVER,redirectServer.byteArrayValue(),mandatoryAvp==1, protectedAvp==1);
		
	}

	public void setRestrictionFilterRule(IPFilterRuleAvp restrictionFilterRule) {
		this.setRestrictionFilterRules(new IPFilterRuleAvp[]{restrictionFilterRule});
		
	}

	public void setRestrictionFilterRules(
			IPFilterRuleAvp[] restrictionFilterRules) {
		super.avpSet.removeAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.RESTRICTION_FILTER_RULE);
		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
		int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

		for(IPFilterRuleAvpImpl avp: (IPFilterRuleAvpImpl[])restrictionFilterRules)
		{
			
			//if(avpRep.getVendorId()!=null)
			//	super.setAvpAsString(protectedAvp, avp.getRuleString(), Long.getLong(avpRep.getVendorId()),true, mandatoryAvp==1,protectedAvp==1, false);
			//else
				super.setAvpAsString(protectedAvp, avp.getRuleString(), true, mandatoryAvp==1,protectedAvp==1, false);
			
		}
		
	}

	

}
