/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.TerminationCauseType;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedActionType;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp;
import net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.SubscriptionIdAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvpImpl;

/**
 * CCA Credit-Control-Request message implementation.<br>
 * <br>
 * 
 * Super project: mobicents <br>
 * 12:25:46 2008-11-10 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlRequestImpl extends CreditControlMessageImpl implements CreditControlRequest{

  /**
   * Constructor.
   * @param message the message to construct the Request
   */
  public CreditControlRequestImpl(Message message) {
    super(message);
  }

  @Override
  public String getLongName() {
    return "Credit-Control-Request";
  }

  @Override
  public String getShortName() {
    return "CCR";
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getCcCorrelationId()
   */
  public byte[] getCcCorrelationId()
  {
    return getAvpAsRaw(CreditControlAVPCodes.CC_Correlation_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getMultipleServicesIndicator()
   */
  public MultipleServicesIndicatorType getMultipleServicesIndicator()
  {
    return (MultipleServicesIndicatorType) getAvpAsEnumerated(CreditControlAVPCodes.Multiple_Services_Indicator, MultipleServicesIndicatorType.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getRequestedAction()
   */
  public RequestedActionType getRequestedAction()
  {
    return (RequestedActionType) getAvpAsEnumerated(CreditControlAVPCodes.Requested_Action, RequestedActionType.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getRequestedServiceUnit()
   */
  public RequestedServiceUnitAvp getRequestedServiceUnit()
  {
    return (RequestedServiceUnitAvp) getAvpAsCustom(CreditControlAVPCodes.Requested_Service_Unit, RequestedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceContextId()
   */
  public String getServiceContextId()
  {
    return getAvpAsUTF8String(CreditControlAVPCodes.Service_Context_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceIdentifier()
   */
  public long getServiceIdentifier()
  {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Service_Identifier);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceParameterInfos()
   */
  public ServiceParameterInfoAvp[] getServiceParameterInfos()
  {
    return (ServiceParameterInfoAvp[]) getAvpsAsCustom(CreditControlAVPCodes.Service_Parameter_Info, ServiceParameterInfoAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getSubscriptionIds()
   */
  public SubscriptionIdAvp[] getSubscriptionIds()
  {
    return (SubscriptionIdAvp[]) getAvpsAsCustom(CreditControlAVPCodes.Subscription_Id, SubscriptionIdAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getTerminationCause()
   */
  public TerminationCauseType getTerminationCause()
  {
    return (TerminationCauseType) getAvpAsEnumerated(DiameterAvpCodes.TERMINATION_CAUSE, TerminationCauseType.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getUsedServiceUnits()
   */
  public UsedServiceUnitAvp[] getUsedServiceUnits()
  {
    return (UsedServiceUnitAvp[]) getAvpsAsCustom(CreditControlAVPCodes.Used_Service_Unit, UsedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getUserEquipmentInfo()
   */
  public UserEquipmentInfoAvp getUserEquipmentInfo()
  {
    return (UserEquipmentInfoAvp) getAvpAsCustom(CreditControlAVPCodes.User_Equipment_Info, UserEquipmentInfoAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasCcCorrelationId()
   */
  public boolean hasCcCorrelationId()
  {
    return hasAvp(CreditControlAVPCodes.CC_Correlation_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasMultipleServicesIndicator()
   */
  public boolean hasMultipleServicesIndicator()
  {
    return hasAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasRequestedAction()
   */
  public boolean hasRequestedAction()
  {
    return hasAvp(CreditControlAVPCodes.Requested_Action);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasRequestedServiceUnit()
   */
  public boolean hasRequestedServiceUnit()
  {
    return hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasServiceContextId()
   */
  public boolean hasServiceContextId()
  {
    return hasAvp(CreditControlAVPCodes.Service_Context_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasServiceIdentifier()
   */
  public boolean hasServiceIdentifier()
  {
    return hasAvp(CreditControlAVPCodes.Service_Identifier);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasTerminationCause()
   */
  public boolean hasTerminationCause()
  {
    return hasAvp(DiameterAvpCodes.TERMINATION_CAUSE);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasUserEquipmentInfo()
   */
  public boolean hasUserEquipmentInfo()
  {
    return hasAvp(CreditControlAVPCodes.User_Equipment_Info);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setCcCorrelationId(byte[])
   */
  public void setCcCorrelationId(byte[] ccCorrelationId) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.CC_Correlation_Id, ccCorrelationId);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setMultipleServicesIndicator(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType)
   */
  public void setMultipleServicesIndicator(MultipleServicesIndicatorType multipleServicesIndicator) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Multiple_Services_Indicator, (long)multipleServicesIndicator.getValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setRequestedAction(net.java.slee.resource.diameter.cca.events.avp.RequestedActionType)
   */
  public void setRequestedAction(RequestedActionType requestedAction) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Requested_Action, (long)requestedAction.getValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setRequestedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp)
   */
  public void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Requested_Service_Unit, requestedServiceUnit.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceContextId(java.lang.String)
   */
  public void setServiceContextId(String serviceContextId) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Service_Context_Id, serviceContextId);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceIdentifier(long)
   */
  public void setServiceIdentifier(long serviceIdentifier) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Service_Identifier, serviceIdentifier);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceParameterInfo(net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp)
   */
  public void setServiceParameterInfo(ServiceParameterInfoAvp serviceParameterInfo) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Service_Parameter_Info, serviceParameterInfo.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceParameterInfos(net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp[])
   */
  public void setServiceParameterInfos(ServiceParameterInfoAvp[] serviceParameterInfos) throws IllegalStateException
  {
    for(ServiceParameterInfoAvp serviceParameterInfo: serviceParameterInfos) {
      setServiceParameterInfo(serviceParameterInfo);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setSubscriptionId(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp)
   */
  public void setSubscriptionId(SubscriptionIdAvp subscriptionId) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Subscription_Id, subscriptionId.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setSubscriptionIds(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp[])
   */
  public void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds) throws IllegalStateException
  {
    for(SubscriptionIdAvp subscriptionId : subscriptionIds) {
      setSubscriptionId(subscriptionId);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setTerminationCause(net.java.slee.resource.diameter.base.events.avp.TerminationCauseType)
   */
  public void setTerminationCause(TerminationCauseType terminationCause) throws IllegalStateException
  {
    addAvp(DiameterAvpCodes.TERMINATION_CAUSE, (long)terminationCause.getValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUsedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp)
   */
  public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Used_Service_Unit, usedServiceUnit.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUsedServiceUnits(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp[])
   */
  public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) throws IllegalStateException
  {
    for(UsedServiceUnitAvp usedServiceUnit : usedServiceUnits) {
      setUsedServiceUnit(usedServiceUnit);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUserEquipmentInfo(net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp)
   */
  public void setUserEquipmentInfo(UserEquipmentInfoAvp userEquipmentInfo) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.User_Equipment_Info, userEquipmentInfo.byteArrayValue());
  }

}
