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

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.cca.events.avp.CostInformationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvpImpl;

/**
 * CCA Credit-Control-Answer message implementation.<br>
 * <br>
 * 
 * Start time:15:49:22 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlAnswerImpl extends CreditControlMessageImpl implements CreditControlAnswer {

  /**
   * Constructor.
   * @param message the message to construct the Answer
   */
  public CreditControlAnswerImpl(Message message) {
    super(message);
  }

  @Override
  public String getLongName() {
    return "Credit-Control-Answer";
  }

  @Override
  public String getShortName() {
    return "CCA";
  }  

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCcSessionFailover()
   */
  public CcSessionFailoverType getCcSessionFailover()
  {
    return (CcSessionFailoverType) getAvpAsEnumerated(CreditControlAVPCodes.CC_Session_Failover, CcSessionFailoverType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCheckBalanceResult()
   */
  public CheckBalanceResultType getCheckBalanceResult()
  {
    return (CheckBalanceResultType) getAvpAsEnumerated(CreditControlAVPCodes.Check_Balance_Result, CheckBalanceResultType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCostInformation()
   */
  public CostInformationAvp getCostInformation()
  {
    return (CostInformationAvp) getAvpAsCustom(CreditControlAVPCodes.Cost_Information, CostInformationAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getCreditControlFailureHandling()
   */
  public CreditControlFailureHandlingType getCreditControlFailureHandling()
  {
    return (CreditControlFailureHandlingType) getAvpAsEnumerated(CreditControlAVPCodes.Credit_Control_Failure_Handling, CreditControlFailureHandlingType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getDirectDebitingFailureHandling()
   */
  public DirectDebitingFailureHandlingType getDirectDebitingFailureHandling()
  {
    return (DirectDebitingFailureHandlingType) getAvpAsEnumerated(CreditControlAVPCodes.Direct_Debiting_Failure_Handling, DirectDebitingFailureHandlingType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getFinalUnitIndication()
   */
  public FinalUnitIndicationAvp getFinalUnitIndication()
  {
    return (FinalUnitIndicationAvp) getAvpAsCustom(CreditControlAVPCodes.Final_Unit_Indication, FinalUnitIndicationAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getGrantedServiceUnit()
   */
  public GrantedServiceUnitAvp getGrantedServiceUnit()
  {
    return (GrantedServiceUnitAvp) getAvpAsCustom(CreditControlAVPCodes.Granted_Service_Unit, GrantedServiceUnitAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#getValidityTime()
   */
  public long getValidityTime()
  {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Validity_Time);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCcSessionFailover()
   */
  public boolean hasCcSessionFailover()
  {
    return hasAvp(CreditControlAVPCodes.CC_Session_Failover);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCheckBalanceResult()
   */
  public boolean hasCheckBalanceResult()
  {
    return hasAvp(CreditControlAVPCodes.Check_Balance_Result);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCostInformation()
   */
  public boolean hasCostInformation()
  {
    return hasAvp(CreditControlAVPCodes.Cost_Information);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasCreditControlFailureHandling()
   */
  public boolean hasCreditControlFailureHandling()
  {
    return hasAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasDirectDebitingFailureHandling()
   */
  public boolean hasDirectDebitingFailureHandling()
  {
    return hasAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasFinalUnitIndication()
   */
  public boolean hasFinalUnitIndication()
  {
    return hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasGrantedServiceUnit()
   */
  public boolean hasGrantedServiceUnit()
  {
    return hasAvp(CreditControlAVPCodes.Granted_Service_Unit);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#hasValidityTime()
   */
  public boolean hasValidityTime()
  {
    return hasAvp(CreditControlAVPCodes.Validity_Time);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCcSessionFailover(net.java.slee.resource.diameter.cca.events.avp.CcSessionFailoverType)
   */
  public void setCcSessionFailover(CcSessionFailoverType ccSessionFailover) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.CC_Session_Failover, (long)ccSessionFailover.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCheckBalanceResult(net.java.slee.resource.diameter.cca.events.avp.CheckBalanceResultType)
   */
  public void setCheckBalanceResult(CheckBalanceResultType checkBalanceResult) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Check_Balance_Result, (long)checkBalanceResult.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCostInformation(net.java.slee.resource.diameter.cca.events.avp.CostInformationAvp)
   */
  public void setCostInformation(CostInformationAvp costInformation) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Cost_Information, costInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setCreditControlFailureHandling(net.java.slee.resource.diameter.cca.events.avp.CreditControlFailureHandlingType)
   */
  public void setCreditControlFailureHandling(CreditControlFailureHandlingType creditControlFailureHandling) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Credit_Control_Failure_Handling, (long)creditControlFailureHandling.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setDirectDebitingFailureHandling(net.java.slee.resource.diameter.cca.events.avp.DirectDebitingFailureHandlingType)
   */
  public void setDirectDebitingFailureHandling(DirectDebitingFailureHandlingType directDebitingFailureHandling) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Direct_Debiting_Failure_Handling, (long)directDebitingFailureHandling.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp)
   */
  public void setFinalUnitIndication(FinalUnitIndicationAvp finalUnitIndication) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Final_Unit_Indication, finalUnitIndication.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setGrantedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp)
   */
  public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Granted_Service_Unit, grantedServiceUnit.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlAnswer#setValidityTime(long)
   */
  public void setValidityTime(long validityTime) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Validity_Time, validityTime);
  }

}
