package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PocServerRole;
import net.java.slee.resource.diameter.ro.events.avp.PocSessionType;
import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * PocInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:14:33 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PocInformationImpl extends GroupedAvpImpl implements PocInformation {

  private static final Logger logger = Logger.getLogger( MmsInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public PocInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getNumberOfParticipants()
   */
  public int getNumberOfParticipants()
  {
    if(hasNumberOfParticipants())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.NUMBER_OF_PARTICIPANTS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getInteger32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.NUMBER_OF_PARTICIPANTS);
        logger.error( "Failure while trying to obtain Number-of-Participants AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getParticipantsInvolveds()
   */
  public String[] getParticipantsInvolveds()
  {
    String[] participantsInvolveds = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.PARTICIPANTS_INVOLVED, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      participantsInvolveds = new String[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          participantsInvolveds[i] = rawAvps.getAvp(i).getUTF8String();
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PARTICIPANTS_INVOLVED);
          logger.error( "Failure while trying to obtain Participants-Involveds AVP (index:" + i + ").", e );
        }
      }
    }

    return participantsInvolveds;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getPocControllingAddress()
   */
  public String getPocControllingAddress()
  {
    if(hasPocControllingAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_CONTROLLING_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_CONTROLLING_ADDRESS);
        logger.error( "Failure while trying to obtain PoC-Controlling-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getPocGroupName()
   */
  public String getPocGroupName()
  {
    if(hasPocControllingAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_GROUP_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_GROUP_NAME);
        logger.error( "Failure while trying to obtain PoC-Group-Name AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getPocServerRole()
   */
  public PocServerRole getPocServerRole()
  {
    if(hasPocServerRole())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_SERVER_ROLE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return PocServerRole.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_SERVER_ROLE);
        logger.error( "Failure while trying to obtain PoC-Server-Role AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getPocSessionId()
   */
  public String getPocSessionId()
  {
    if(hasPocSessionId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_SESSION_ID);
        logger.error( "Failure while trying to obtain PoC-Session-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getPocSessionType()
   */
  public PocSessionType getPocSessionType()
  {
    if(hasPocSessionType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.POC_SESSION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return PocSessionType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.POC_SESSION_TYPE);
        logger.error( "Failure while trying to obtain PoC-Session-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#getTalkBurstExchanges()
   */
  public TalkBurstExchange[] getTalkBurstExchanges()
  {
    TalkBurstExchange[] talkBurstExchanges = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      talkBurstExchanges = new TalkBurstExchange[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          talkBurstExchanges[i] = new TalkBurstExchangeImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TALK_BURST_EXCHANGE);
          logger.error( "Failure while trying to obtain Talk-Burst-Exchange AVP (index:" + i + ").", e );
        }
      }
    }

    return talkBurstExchanges;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasNumberOfParticipants()
   */
  public boolean hasNumberOfParticipants()
  {
    return hasAvp( DiameterRoAvpCodes.NUMBER_OF_PARTICIPANTS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasPocControllingAddress()
   */
  public boolean hasPocControllingAddress()
  {
    return hasAvp( DiameterRoAvpCodes.POC_CONTROLLING_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasPocGroupName()
   */
  public boolean hasPocGroupName()
  {
    return hasAvp( DiameterRoAvpCodes.POC_GROUP_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasPocServerRole()
   */
  public boolean hasPocServerRole()
  {
    return hasAvp( DiameterRoAvpCodes.POC_SERVER_ROLE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasPocSessionId()
   */
  public boolean hasPocSessionId()
  {
    return hasAvp( DiameterRoAvpCodes.POC_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#hasPocSessionType()
   */
  public boolean hasPocSessionType()
  {
    return hasAvp( DiameterRoAvpCodes.POC_SESSION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setNumberOfParticipants(int)
   */
  public void setNumberOfParticipants( int numberOfParticipants )
  {
    if(hasNumberOfParticipants())
    {
      throw new IllegalStateException("AVP Number-of-Participants is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.NUMBER_OF_PARTICIPANTS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.NUMBER_OF_PARTICIPANTS, numberOfParticipants, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setParticipantsInvolved(java.lang.String)
   */
  public void setParticipantsInvolved( String participantsInvolved )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PARTICIPANTS_INVOLVED, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.PARTICIPANTS_INVOLVED, participantsInvolved, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setParticipantsInvolveds(java.lang.String[])
   */
  public void setParticipantsInvolveds( String[] participantsInvolveds )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PARTICIPANTS_INVOLVED, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(String participantsInvolved : participantsInvolveds)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.PARTICIPANTS_INVOLVED, participantsInvolved, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setPocControllingAddress(java.lang.String)
   */
  public void setPocControllingAddress( String pocControllingAddress )
  {
    if(hasPocControllingAddress())
    {
      throw new IllegalStateException("AVP PoC-Controlling-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_CONTROLLING_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_CONTROLLING_ADDRESS, pocControllingAddress, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setPocGroupName(java.lang.String)
   */
  public void setPocGroupName( String pocGroupName )
  {
    if(hasPocGroupName())
    {
      throw new IllegalStateException("AVP PoC-Group-Name is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_GROUP_NAME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_GROUP_NAME, pocGroupName, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setPocServerRole(net.java.slee.resource.diameter.ro.events.avp.PocServerRole)
   */
  public void setPocServerRole( PocServerRole pocServerRole )
  {
    if(hasPocServerRole())
    {
      throw new IllegalStateException("AVP PoC-Server-Role is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_SERVER_ROLE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_SERVER_ROLE, pocServerRole.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setPocSessionId(java.lang.String)
   */
  public void setPocSessionId( String pocSessionId )
  {
    if(hasPocSessionId())
    {
      throw new IllegalStateException("AVP PoC-Session-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_SESSION_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_SESSION_ID, pocSessionId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setPocSessionType(net.java.slee.resource.diameter.ro.events.avp.PocSessionType)
   */
  public void setPocSessionType( PocSessionType pocSessionType )
  {
    if(hasPocSessionType())
    {
      throw new IllegalStateException("AVP PoC-Session-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.POC_SESSION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.POC_SESSION_TYPE, pocSessionType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setTalkBurstExchange(net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange)
   */
  public void setTalkBurstExchange( TalkBurstExchange talkBurstExchange )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, talkBurstExchange.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.PocInformation#setTalkBurstExchanges(net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange[])
   */
  public void setTalkBurstExchanges( TalkBurstExchange[] talkBurstExchanges )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(TalkBurstExchange talkBurstExchange : talkBurstExchanges)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.TALK_BURST_EXCHANGE, talkBurstExchange.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
