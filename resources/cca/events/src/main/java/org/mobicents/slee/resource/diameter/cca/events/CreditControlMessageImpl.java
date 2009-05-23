package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;

/**
 * CCA Credit-Control-Request/Answer common message implementation.<br>
 * <br>
 *  
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class CreditControlMessageImpl extends DiameterMessageImpl implements CreditControlMessage {

  private static transient Logger logger = Logger.getLogger(CreditControlMessageImpl.class);

  /**
   * Constructor.
   * @param message the Message object to be wrapped
   */
  public CreditControlMessageImpl(Message message)
  {
    super(message);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getAcctMultiSessionId()
   */
  public String getAcctMultiSessionId()
  {
    if(hasAcctMultiSessionId())
    {
      Avp raw = super.message.getAvps().getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
      try
      {
        return raw.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
        logger.error( "Failure while trying to obtain Acct-Multi-Session-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestNumber()
   */
  public long getCcRequestNumber()
  {
    if(hasCcRequestNumber())
    {
      Avp raw = super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Request_Number);
      try
      {
        return raw.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Request_Number);
        logger.error( "Failure while trying to obtain CC-Request-Number AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestType()
   */
  public CcRequestType getCcRequestType()
  {
    if(hasCcRequestType())
    {
      Avp raw = super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Request_Type);
      try
      {
        return CcRequestType.EVENT_REQUEST.fromInt(raw.getInteger32());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Request_Type);
        logger.error( "Failure while trying to obtain CC-Request-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcSubSessionId()
   */
  public long getCcSubSessionId()
  {
    if(hasCcSubSessionId())
    {
      Avp raw = super.message.getAvps().getAvp(CreditControlAVPCodes.CC_Sub_Session_Id);
      try
      {
        return raw.getUnsigned64();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Sub_Session_Id);
        logger.error( "Failure while trying to obtain CC-Sub-Session-Id AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getMultipleServicesCreditControls()
   */
  public MultipleServicesCreditControlAvp[] getMultipleServicesCreditControls()
  {
    if(hasMultipleServicesCreditControl())
    {
      AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCodes.Multiple_Services_Credit_Control);
      MultipleServicesCreditControlAvp[] avps = new MultipleServicesCreditControlAvp[set.size()];
      for(int index=0;index<set.size();index++)
      {
        Avp avp = set.getAvpByIndex(index);
        try
        {
          avps[index] = new MultipleServicesCreditControlAvpImpl(avp.getCode(), avp.getVendorId(), avp.isMandatory() ? 1 : 0, avp.isEncrypted() ? 1 : 0, avp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError("Failed on index: " + index + ", " + e.getMessage(), CreditControlAVPCodes.Multiple_Services_Credit_Control);
          logger.error( "Failure while trying to obtain Multiple-Services-Credit-Control AVP.", e );
        }
      }
      return avps;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasMultipleServicesCreditControl()
   */
  public boolean hasMultipleServicesCreditControl()
  {
    return super.hasAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasAcctMultiSessionId()
   */
  public boolean hasAcctMultiSessionId()
  {
    return super.hasAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestNumber()
   */
  public boolean hasCcRequestNumber()
  {
    return super.hasAvp(CreditControlAVPCodes.CC_Request_Number);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestType()
   */
  public boolean hasCcRequestType()
  {
    return super.hasAvp(CreditControlAVPCodes.CC_Request_Type);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcSubSessionId()
   */
  public boolean hasCcSubSessionId()
  {
    return super.hasAvp(CreditControlAVPCodes.CC_Sub_Session_Id);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setAcctMultiSessionId(java.lang.String)
   */
  public void setAcctMultiSessionId(String acctMultiSessionId) throws IllegalStateException
  {
    if(hasAcctMultiSessionId())
    {
      throw new IllegalStateException("AVP Acct-Multi-Session-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      super.message.getAvps().removeAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
      super.message.getAvps().addAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID, acctMultiSessionId, protectedAvp == 1, mandatoryAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestNumber(long)
   */
  public void setCcRequestNumber(long ccRequestNumber) throws IllegalStateException
  {
    if(hasCcRequestNumber())
    {
      throw new IllegalStateException("AVP CC-Request-Number is already present in message and cannot be overwritten.");
    }
    else
    {
      super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Request_Number);
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Request_Number);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
      super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Request_Number, ccRequestNumber, protectedAvp == 1, mandatoryAvp == 1, true);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestType(net.java.slee.resource.diameter.cca.events.avp.CcRequestType)
   */
  public void setCcRequestType(CcRequestType ccRequestType) throws IllegalStateException
  {
    if(hasCcRequestType())
    {
      throw new IllegalStateException("AVP CC-Request-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Request_Type);
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Request_Type);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
      super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Request_Type, ccRequestType.getValue(), protectedAvp == 1, mandatoryAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcSubSessionId(long)
   */
  public void setCcSubSessionId(long ccSubSessionId) throws IllegalStateException
  {
    if(hasCcSubSessionId())
    {
      throw new IllegalStateException("AVP CC-Sub-Session-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      super.message.getAvps().removeAvp(CreditControlAVPCodes.CC_Sub_Session_Id);
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Sub_Session_Id);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
      super.message.getAvps().addAvp(CreditControlAVPCodes.CC_Request_Number, ccSubSessionId, protectedAvp == 1, mandatoryAvp == 1, true);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControl(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp)
   */
  public void setMultipleServicesCreditControl(MultipleServicesCreditControlAvp multipleServicesCreditControl) throws IllegalStateException
  {
    this.setMultipleServicesCreditControls(new MultipleServicesCreditControlAvp[]{multipleServicesCreditControl});
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControls(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp[])
   */
  public void setMultipleServicesCreditControls(MultipleServicesCreditControlAvp[] multipleServicesCreditControls) throws IllegalStateException
  {
    if(hasMultipleServicesCreditControl())
    {
      throw new IllegalStateException("AVP Multiple-Services-Credit-Control is already present in message and cannot be overwritten.");
    }
    else
    {
      super.message.getAvps().removeAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      for(MultipleServicesCreditControlAvp multipleServicesCreditControlAvp: multipleServicesCreditControls)
      {
        super.message.getAvps().addAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control, multipleServicesCreditControlAvp.byteArrayValue(), protectedAvp == 1, mandatoryAvp == 1);
      }
    }
  }

}
