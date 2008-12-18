package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType;
import net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlFailureHandlingType;
import net.java.slee.resource.diameter.cca.events.avp.DirectDebitingFailureHandlingType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.cca.events.avp.CostInformationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvpImpl;

/**
 * Start time:15:49:22 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlAnswerImpl extends CreditControlMessageImpl implements
		CreditControlAnswer {

	/**
	 * tttttt	
	 * @param message
	 */
	public CreditControlAnswerImpl(Message message) {
		super(message);

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCcSessionFailover()
	 */
	public CcSessionFailoverType getCcSessionFailover() {
		if(hasCcSessionFailover())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Session_Failover);
			try {
				return CcSessionFailoverType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.CC_Session_Failover);
				e.printStackTrace();
			} 
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCheckBalanceResult()
	 */
	public CheckBalanceResultType getCheckBalanceResult() {
		if(hasCheckBalanceResult())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Check_Balance_Result);
			try {
				return CheckBalanceResultType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Check_Balance_Result);
				e.printStackTrace();
			} 
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCostInformation()
	 */
	public CostInformationAvp getCostInformation() {
		if(hasCostInformation())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Cost_Information);
			try {
				CostInformationAvp  costInformationAvp=new CostInformationAvpImpl(CreditControlAVPCodes.Cost_Information,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return costInformationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Cost_Information);
				e.printStackTrace();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCreditControlFailureHandling()
	 */
	public CreditControlFailureHandlingType getCreditControlFailureHandling() {
		if(hasCreditControlFailureHandling())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling);
			try {
				return CreditControlFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Credit_Control_Failure_Handling);
				e.printStackTrace();
			} 
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getDirectDebitingFailureHandling()
	 */
	public DirectDebitingFailureHandlingType getDirectDebitingFailureHandling() {
		if(hasDirectDebitingFailureHandling())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
			try {
				return DirectDebitingFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
				e.printStackTrace();
			} 
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getFinalUnitIndication()
	 */
	public FinalUnitIndicationAvp getFinalUnitIndication() {
		if(hasFinalUnitIndication())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Final_Unit_Indication);
			try {
				FinalUnitIndicationAvp  finalUnitIndicationAvp=new FinalUnitIndicationAvpImpl(CreditControlAVPCodes.Final_Unit_Indication,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return finalUnitIndicationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Final_Unit_Indication);
				e.printStackTrace();
			}
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getGrantedServiceUnit()
	 */
	public GrantedServiceUnitAvp getGrantedServiceUnit() {
		if(hasGrantedServiceUnit())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Granted_Service_Unit);
			try {
				GrantedServiceUnitAvp  grantedServiceUnitAvp=new GrantedServiceUnitAvpImpl(CreditControlAVPCodes.Granted_Service_Unit,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return grantedServiceUnitAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Granted_Service_Unit);
				e.printStackTrace();
			}
		}
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getValidityTime()
	 */
	public long getValidityTime() {
		if(hasValidityTime())
		{
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCodes.Validity_Time);
			try {
				
				return avp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCodes.Validity_Time);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCcSessionFailover()
	 */
	public boolean hasCcSessionFailover() {
		return super.hasAvp(CreditControlAVPCodes.CC_Session_Failover);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCheckBalanceResult()
	 */
	public boolean hasCheckBalanceResult() {
		return super.hasAvp(CreditControlAVPCodes.Check_Balance_Result);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCostInformation()
	 */
	public boolean hasCostInformation() {
		return super.hasAvp(CreditControlAVPCodes.Cost_Information);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCreditControlFailureHandling()
	 */
	public boolean hasCreditControlFailureHandling() {
		return super.hasAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasDirectDebitingFailureHandling()
	 */
	public boolean hasDirectDebitingFailureHandling() {
		return super.hasAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCodes.Granted_Service_Unit);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCodes.Validity_Time);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCcSessionFailover(net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType)
	 */
	public void setCcSessionFailover(CcSessionFailoverType ccSessionFailover)
			throws IllegalStateException {
		if(hasCcSessionFailover())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Session_Failover);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Session_Failover);
			super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Session_Failover,ccSessionFailover.getValue(),mandatoryAvp==1,protectedAvp==1,false);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCheckBalanceResult(net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType)
	 */
	public void setCheckBalanceResult(CheckBalanceResultType checkBalanceResult)
			throws IllegalStateException {
		if(hasCheckBalanceResult())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Check_Balance_Result);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Check_Balance_Result);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Check_Balance_Result,checkBalanceResult.getValue(),mandatoryAvp==1,protectedAvp==1,false);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCostInformation(net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp)
	 */
	public void setCostInformation(CostInformationAvp costInformation)
			throws IllegalStateException {
		if(hasCostInformation())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Cost_Information);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Cost_Information);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Cost_Information,costInformation.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCreditControlFailureHandling(net.java.slee.resource.diameter.cca.events.avp.CreditControlFailureHandlingType)
	 */
	public void setCreditControlFailureHandling(
			CreditControlFailureHandlingType creditControlFailureHandling)
			throws IllegalStateException {
		if(hasCreditControlFailureHandling())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling,creditControlFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setDirectDebitingFailureHandling(net.java.slee.resource.diameter.cca.events.avp.DirectDebitingFailureHandlingType)
	 */
	public void setDirectDebitingFailureHandling(
			DirectDebitingFailureHandlingType directDebitingFailureHandling)
			throws IllegalStateException {
		if(hasDirectDebitingFailureHandling())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling,directDebitingFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp)
	 */
	public void setFinalUnitIndication(
			FinalUnitIndicationAvp finalUnitIndication)
			throws IllegalStateException {
		if(hasCostInformation())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Final_Unit_Indication);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Final_Unit_Indication);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Final_Unit_Indication,finalUnitIndication.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
		}
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setGrantedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp)
	 */
	public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit)
			throws IllegalStateException {
		if(hasGrantedServiceUnit())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Granted_Service_Unit);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Granted_Service_Unit);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Granted_Service_Unit,grantedServiceUnit.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setValidityTime(long)
	 */
	public void setValidityTime(long validityTime) throws IllegalStateException {
		if(hasCcSessionFailover())
		{
			throw new IllegalStateException("It's been already set!?");
		}else
		{
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Validity_Time);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCodes.Validity_Time);
			super.message.getAvps().addAvp(CreditControlAVPCodes.Validity_Time,validityTime,mandatoryAvp==1,protectedAvp==1,true);
		}

	}

}
