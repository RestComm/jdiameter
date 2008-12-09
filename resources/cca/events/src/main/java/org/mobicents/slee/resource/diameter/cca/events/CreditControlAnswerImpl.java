/**
 * Start time:15:49:22 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType;
import net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			try {
				return CcSessionFailoverType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_SESSION_FAILOVER);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			try {
				return CheckBalanceResultType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CHECK_BALANCE_RESULT);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.COST_INFORMATION);
			try {
				CostInformationAvp  costInformationAvp=new CostInformationAvpImpl(CreditControlAVPCode.COST_INFORMATION,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return costInformationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.COST_INFORMATION);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			try {
				return CreditControlFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			try {
				return DirectDebitingFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			try {
				FinalUnitIndicationAvp  finalUnitIndicationAvp=new FinalUnitIndicationAvpImpl(CreditControlAVPCode.FINAL_UNIT_INDICATION,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return finalUnitIndicationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.FINAL_UNIT_INDICATION);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			try {
				GrantedServiceUnitAvp  grantedServiceUnitAvp=new GrantedServiceUnitAvpImpl(CreditControlAVPCode.GRANTED_SERVICE_UNIT,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return grantedServiceUnitAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.GRANTED_SERVICE_UNIT);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.VALIDITY_TIME);
			try {
				
				return avp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.VALIDITY_TIME);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCcSessionFailover()
	 */
	public boolean hasCcSessionFailover() {
		return super.hasAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCheckBalanceResult()
	 */
	public boolean hasCheckBalanceResult() {
		return super.hasAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCostInformation()
	 */
	public boolean hasCostInformation() {
		return super.hasAvp(CreditControlAVPCode.COST_INFORMATION);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCreditControlFailureHandling()
	 */
	public boolean hasCreditControlFailureHandling() {
		return super.hasAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasDirectDebitingFailureHandling()
	 */
	public boolean hasDirectDebitingFailureHandling() {
		return super.hasAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCode.VALIDITY_TIME);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_SESSION_FAILOVER,ccSessionFailover.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			super.message.getAvps().addAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT,checkBalanceResult.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.COST_INFORMATION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.COST_INFORMATION);
			super.message.getAvps().addAvp(CreditControlAVPCode.COST_INFORMATION,costInformation.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			super.message.getAvps().addAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING,creditControlFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			super.message.getAvps().addAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING,directDebitingFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			super.message.getAvps().addAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION,finalUnitIndication.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			super.message.getAvps().addAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT,grantedServiceUnit.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.VALIDITY_TIME);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.VALIDITY_TIME);
			super.message.getAvps().addAvp(CreditControlAVPCode.VALIDITY_TIME,validityTime,mandatoryAvp==1,protectedAvp==1,true);
		}

	}

}
/**
 * Start time:15:49:22 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType;
import net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType;
import net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			try {
				return CcSessionFailoverType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_SESSION_FAILOVER);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			try {
				return CheckBalanceResultType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CHECK_BALANCE_RESULT);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.COST_INFORMATION);
			try {
				CostInformationAvp  costInformationAvp=new CostInformationAvpImpl(CreditControlAVPCode.COST_INFORMATION,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return costInformationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.COST_INFORMATION);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			try {
				return CreditControlFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			try {
				return DirectDebitingFailureHandlingType.fromInt(avp.getInteger32());
			} catch (Exception e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			try {
				FinalUnitIndicationAvp  finalUnitIndicationAvp=new FinalUnitIndicationAvpImpl(CreditControlAVPCode.FINAL_UNIT_INDICATION,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return finalUnitIndicationAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.FINAL_UNIT_INDICATION);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			try {
				GrantedServiceUnitAvp  grantedServiceUnitAvp=new GrantedServiceUnitAvpImpl(CreditControlAVPCode.GRANTED_SERVICE_UNIT,avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				return grantedServiceUnitAvp;
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.GRANTED_SERVICE_UNIT);
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
			Avp avp=super.message.getAvps().getAvp(CreditControlAVPCode.VALIDITY_TIME);
			try {
				
				return avp.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.VALIDITY_TIME);
				e.printStackTrace();
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCcSessionFailover()
	 */
	public boolean hasCcSessionFailover() {
		return super.hasAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCheckBalanceResult()
	 */
	public boolean hasCheckBalanceResult() {
		return super.hasAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCostInformation()
	 */
	public boolean hasCostInformation() {
		return super.hasAvp(CreditControlAVPCode.COST_INFORMATION);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCreditControlFailureHandling()
	 */
	public boolean hasCreditControlFailureHandling() {
		return super.hasAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasDirectDebitingFailureHandling()
	 */
	public boolean hasDirectDebitingFailureHandling() {
		return super.hasAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasFinalUnitIndication()
	 */
	public boolean hasFinalUnitIndication() {
		return super.hasAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasGrantedServiceUnit()
	 */
	public boolean hasGrantedServiceUnit() {
		return super.hasAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasValidityTime()
	 */
	public boolean hasValidityTime() {
		return super.hasAvp(CreditControlAVPCode.VALIDITY_TIME);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_SESSION_FAILOVER);
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_SESSION_FAILOVER,ccSessionFailover.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT);
			super.message.getAvps().addAvp(CreditControlAVPCode.CHECK_BALANCE_RESULT,checkBalanceResult.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.COST_INFORMATION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.COST_INFORMATION);
			super.message.getAvps().addAvp(CreditControlAVPCode.COST_INFORMATION,costInformation.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING);
			super.message.getAvps().addAvp(CreditControlAVPCode.CREDIT_CONTROL_FAILURE_HANDLING,creditControlFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING);
			super.message.getAvps().addAvp(CreditControlAVPCode.DIRECT_DEBITING_FAILURE_HANDLING,directDebitingFailureHandling.getValue(),mandatoryAvp==1,protectedAvp==1,false);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION);
			super.message.getAvps().addAvp(CreditControlAVPCode.FINAL_UNIT_INDICATION,finalUnitIndication.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT);
			super.message.getAvps().addAvp(CreditControlAVPCode.GRANTED_SERVICE_UNIT,grantedServiceUnit.byteArrayValue(),mandatoryAvp==1,protectedAvp==1);
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
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.VALIDITY_TIME);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().removeAvp(CreditControlAVPCode.VALIDITY_TIME);
			super.message.getAvps().addAvp(CreditControlAVPCode.VALIDITY_TIME,validityTime,mandatoryAvp==1,protectedAvp==1,true);
		}

	}

}
