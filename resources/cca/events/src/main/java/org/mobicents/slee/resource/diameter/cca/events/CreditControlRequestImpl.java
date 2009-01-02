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

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
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

  private static transient Logger logger = Logger.getLogger(CreditControlRequestImpl.class);

  /**
   * Constructor.
   * @param message the message to construct the Request
   */
  public CreditControlRequestImpl(Message message)
  {
    super(message);
  }

  @Override
  public String getLongName()
  {
    return "Credit-Control-Request";
  }

  @Override
  public String getShortName()
  {
    return "CCR";
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getCcCorrelationId()
   */
  public byte[] getCcCorrelationId()
  {
    if(hasCcCorrelationId())
    {
      try
      {
        return super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Correlation_Id).getRaw();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Correlation_Id);
        logger.error( "Failure while trying to obtain CC-Correlation-Id AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getMultipleServicesIndicator()
   */
  public MultipleServicesIndicatorType getMultipleServicesIndicator()
  {
    if(hasMultipleServicesIndicator())
    {
      try
      {
        return MultipleServicesIndicatorType.MULTIPLE_SERVICES_NOT_SUPPORTED.fromInt(super.message.getAvps().getAvp(CreditControlAVPCodes.Multiple_Services_Indicator).getInteger32());
      }
      catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Multiple_Services_Indicator);
        logger.error( "Failure while trying to obtain Multiple-Services-Indicator AVP.", e );
      } 
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getRequestedAction()
   */
  public RequestedActionType getRequestedAction()
  {
    if(hasRequestedAction())
    {
      try
      {
        return RequestedActionType.CHECK_BALANCE.fromInt(super.message.getAvps().getAvp(CreditControlAVPCodes.Requested_Action).getInteger32());
      }
      catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Requested_Action);
        logger.error( "Failure while trying to obtain Requested-Action AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getRequestedServiceUnit()
   */
  public RequestedServiceUnitAvp getRequestedServiceUnit()
  {
    if(hasRequestedServiceUnit())
    {
      Avp rawAvp = super.message.getAvps().getAvp(CreditControlAVPCodes.Requested_Service_Unit);
      try
      {
        return new RequestedServiceUnitAvpImpl(CreditControlAVPCodes.Requested_Service_Unit, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Requested_Service_Unit);
        logger.error( "Failure while trying to obtain Requested-Service-Unit AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceContextId()
   */
  public String getServiceContextId()
  {
    if(hasServiceContextId())
    {
      try
      {
        return super.message.getAvps().getAvp(CreditControlAVPCodes.Service_Context_Id).getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Service_Context_Id);
        logger.error( "Failure while trying to obtain Service-Context-Id AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceIdentifier()
   */
  public long getServiceIdentifier()
  {
    if(hasServiceIdentifier())
    {
      try
      {
        return super.message.getAvps().getAvp(CreditControlAVPCodes.Service_Identifier).getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Service_Identifier);
        logger.error( "Failure while trying to obtain Service-Identifier AVP.", e );
      }
    }

    return -1;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getServiceParameterInfos()
   */
  public ServiceParameterInfoAvp[] getServiceParameterInfos()
  {
    if(super.hasAvp(CreditControlAVPCodes.Service_Parameter_Info))
    {
      AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCodes.Service_Parameter_Info);
      ServiceParameterInfoAvp[] avps = new ServiceParameterInfoAvp[set.size()];

      for(int index = 0;index < set.size(); index++)
      {
        try
        {
          Avp rawAvp = set.getAvpByIndex(index);
          ServiceParameterInfoAvp avp = new ServiceParameterInfoAvpImpl(CreditControlAVPCodes.Service_Parameter_Info, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
          avps[index] = avp;
        }
        catch (AvpDataException e) {
          reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), CreditControlAVPCodes.Service_Parameter_Info);
          logger.error( "Failure while trying to obtain Service-Parameter-Info AVP.", e );
        }
        return avps;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getSubscriptionIds()
   */
  public SubscriptionIdAvp[] getSubscriptionIds()
  {
    if(super.hasAvp(CreditControlAVPCodes.Subscription_Id))
    {
      AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCodes.Subscription_Id);
      SubscriptionIdAvp[] avps = new SubscriptionIdAvp[set.size()];

      for(int index = 0;index < set.size(); index++)
      {
        try
        {
          Avp rawAvp = set.getAvpByIndex(index);
          SubscriptionIdAvp avp = new SubscriptionIdAvpImpl(CreditControlAVPCodes.Subscription_Id, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
          avps[index] = avp;
        }
        catch (AvpDataException e) {
          reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), CreditControlAVPCodes.Subscription_Id);
          logger.error( "Failure while trying to obtain Subscription-Id AVP.", e );
        }
        return avps;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getTerminationCause()
   */
  public TerminationCauseType getTerminationCause()
  {
    if(hasTerminationCause())
    {
      try
      {
        return TerminationCauseType.fromInt(super.message.getAvps().getAvp(DiameterAvpCodes.TERMINATION_CAUSE).getInteger32());
      }
      catch (Exception e) {
        reportAvpFetchError(e.getMessage(), DiameterAvpCodes.TERMINATION_CAUSE);
        logger.error( "Failure while trying to obtain Termination-Cause AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getUsedServiceUnits()
   */
  public UsedServiceUnitAvp[] getUsedServiceUnits()
  {
    if(super.hasAvp(CreditControlAVPCodes.Used_Service_Unit))
    {
      AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCodes.Used_Service_Unit);
      UsedServiceUnitAvp[] avps = new UsedServiceUnitAvp[set.size()];

      for(int index = 0;index < set.size(); index++)
      {
        try
        {
          Avp rawAvp = set.getAvpByIndex(index);
          UsedServiceUnitAvp avp = new UsedServiceUnitAvpImpl(CreditControlAVPCodes.Used_Service_Unit, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
          avps[index] = avp;
        }
        catch (AvpDataException e) {
          reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), CreditControlAVPCodes.Used_Service_Unit);
          logger.error( "Failure while trying to obtain Used-Service-Unit AVP.", e );
        }
        return avps;
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#getUserEquipmentInfo()
   */
  public UserEquipmentInfoAvp getUserEquipmentInfo()
  {
    if(hasUserEquipmentInfo())
    {
      Avp rawAvp = super.message.getAvps().getAvp(CreditControlAVPCodes.User_Equipment_Info);
      try
      {
        return new UserEquipmentInfoAvpImpl(CreditControlAVPCodes.User_Equipment_Info, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.User_Equipment_Info);
        logger.error( "Failure while trying to obtain User-Equipment-Info AVP.", e );
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasCcCorrelationId()
   */
  public boolean hasCcCorrelationId()
  {
    return super.hasAvp(CreditControlAVPCodes.CC_Correlation_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasMultipleServicesIndicator()
   */
  public boolean hasMultipleServicesIndicator()
  {
    return super.hasAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasRequestedAction()
   */
  public boolean hasRequestedAction()
  {
    return super.hasAvp(CreditControlAVPCodes.Requested_Action);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasRequestedServiceUnit()
   */
  public boolean hasRequestedServiceUnit()
  {
    return super.hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasServiceContextId()
   */
  public boolean hasServiceContextId()
  {
    return super.hasAvp(CreditControlAVPCodes.Service_Context_Id);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasServiceIdentifier()
   */
  public boolean hasServiceIdentifier()
  {
    return super.hasAvp(CreditControlAVPCodes.Service_Identifier);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasTerminationCause()
   */
  public boolean hasTerminationCause()
  {
    return super.hasAvp(DiameterAvpCodes.TERMINATION_CAUSE);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#hasUserEquipmentInfo()
   */
  public boolean hasUserEquipmentInfo()
  {
    return super.hasAvp(CreditControlAVPCodes.User_Equipment_Info);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setCcCorrelationId(byte[])
   */
  public void setCcCorrelationId(byte[] ccCorrelationId) throws IllegalStateException
  {
    if(hasCcCorrelationId())
    {
      throw new IllegalStateException("AVP CC-Correlation-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Correlation_Id);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Correlation_Id);
      super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Correlation_Id, ccCorrelationId, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setMultipleServicesIndicator(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesIndicatorType)
   */
  public void setMultipleServicesIndicator(MultipleServicesIndicatorType multipleServicesIndicator) throws IllegalStateException
  {
    if(hasMultipleServicesIndicator())
    {
      throw new IllegalStateException("AVP Multiple-Services-Indicator is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Multiple_Services_Indicator);
      super.message.getAvps().addAvp(CreditControlAVPCodes.Multiple_Services_Indicator, multipleServicesIndicator.getValue(), mandatoryAvp == 1, protectedAvp == 1);
    }

  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setRequestedAction(net.java.slee.resource.diameter.cca.events.avp.RequestedActionType)
   */
  public void setRequestedAction(RequestedActionType requestedAction) throws IllegalStateException
  {
    if(hasRequestedAction())
    {
      throw new IllegalStateException("AVP Requested-Action is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Action);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Requested_Action);
      super.message.getAvps().addAvp(CreditControlAVPCodes.Requested_Action, requestedAction.getValue(), mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setRequestedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp)
   */
  public void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit) throws IllegalStateException
  {
    if(hasRequestedServiceUnit())
    {
      throw new IllegalStateException("AVP Requested-Service-Unit is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Requested_Service_Unit);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Requested_Service_Unit);
      super.message.getAvps().addAvp(CreditControlAVPCodes.Requested_Service_Unit, requestedServiceUnit.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceContextId(java.lang.String)
   */
  public void setServiceContextId(String serviceContextId) throws IllegalStateException
  {
    if(hasServiceContextId())
    {
      throw new IllegalStateException("AVP Service-Context-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Context_Id);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Context_Id);
      super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Context_Id, serviceContextId, false, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceIdentifier(long)
   */
  public void setServiceIdentifier(long serviceIdentifier) throws IllegalStateException
  {
    if(hasServiceIdentifier())
    {
      throw new IllegalStateException("AVP Service-Identifier is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Identifier);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
      super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Identifier);
      super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Identifier, serviceIdentifier, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceParameterInfo(net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp)
   */
  public void setServiceParameterInfo(ServiceParameterInfoAvp serviceParameterInfo) throws IllegalStateException
  {
    this.setServiceParameterInfos(new ServiceParameterInfoAvp[]{serviceParameterInfo});
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setServiceParameterInfos(net.java.slee.resource.diameter.cca.events.avp.ServiceParameterInfoAvp[])
   */
  public void setServiceParameterInfos(ServiceParameterInfoAvp[] serviceParameterInfos) throws IllegalStateException
  {
    if(hasAvp(CreditControlAVPCodes.Service_Parameter_Info))
    {
      throw new IllegalStateException("AVP Service-Parameter-Info is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Service_Parameter_Info);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Service_Parameter_Info);
      for(ServiceParameterInfoAvp serviceParameterInfo: serviceParameterInfos)
      {
        super.message.getAvps().addAvp(CreditControlAVPCodes.Service_Parameter_Info, serviceParameterInfo.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setSubscriptionId(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp)
   */
  public void setSubscriptionId(SubscriptionIdAvp subscriptionId) throws IllegalStateException
  {
    this.setSubscriptionIds(new SubscriptionIdAvp[]{subscriptionId});
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setSubscriptionIds(net.java.slee.resource.diameter.cca.events.avp.SubscriptionIdAvp[])
   */
  public void setSubscriptionIds(SubscriptionIdAvp[] subscriptionIds) throws IllegalStateException
  {
    if(hasAvp(CreditControlAVPCodes.Subscription_Id))
    {
      throw new IllegalStateException("AVP Subscription-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Subscription_Id);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      super.message.getAvps().removeAvp(CreditControlAVPCodes.Subscription_Id);
      for(SubscriptionIdAvp subscriptionId: subscriptionIds)
      {
        super.message.getAvps().addAvp(CreditControlAVPCodes.Subscription_Id, subscriptionId.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setTerminationCause(net.java.slee.resource.diameter.base.events.avp.TerminationCauseType)
   */
  public void setTerminationCause(TerminationCauseType terminationCause) throws IllegalStateException
  {
    if(hasTerminationCause())
    {
      throw new IllegalStateException("AVP Termination-Cause is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.TERMINATION_CAUSE);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(DiameterAvpCodes.TERMINATION_CAUSE);
      super.message.getAvps().addAvp(DiameterAvpCodes.TERMINATION_CAUSE, terminationCause.getValue(), avpRep.getVendorId(), mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUsedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp)
   */
  public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) throws IllegalStateException
  {
    this.setUsedServiceUnits(new UsedServiceUnitAvp[]{usedServiceUnit});
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUsedServiceUnits(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp[])
   */
  public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) throws IllegalStateException
  {
    if(hasAvp(CreditControlAVPCodes.Used_Service_Unit))
    {
      throw new IllegalStateException("AVP Used-Service-Unit is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Used_Service_Unit);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.Used_Service_Unit);
      for(UsedServiceUnitAvp usedServiceUnit: usedServiceUnits)
      {
        super.message.getAvps().addAvp(CreditControlAVPCodes.Used_Service_Unit, usedServiceUnit.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlRequest#setUserEquipmentInfo(net.java.slee.resource.diameter.cca.events.avp.UserEquipmentInfoAvp)
   */
  public void setUserEquipmentInfo(UserEquipmentInfoAvp userEquipmentInfo) throws IllegalStateException
  {
    if(hasUserEquipmentInfo())
    {
      throw new IllegalStateException("AVP User-Equipment-Info is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.User_Equipment_Info);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.message.getAvps().removeAvp(CreditControlAVPCodes.User_Equipment_Info);
      super.message.getAvps().addAvp(CreditControlAVPCodes.User_Equipment_Info, userEquipmentInfo.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
