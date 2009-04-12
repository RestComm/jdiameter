package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * MessageClassImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:10:15 PM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageClassImpl extends GroupedAvpImpl implements MessageClass {

  private static final Logger logger = Logger.getLogger( MessageClassImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MessageClassImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#getClassIdentifier()
   */
  public ClassIdentifier getClassIdentifier()
  {
    if(hasClassIdentifier())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return ClassIdentifier.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CLASS_IDENTIFIER);
        logger.error( "Failure while trying to obtain Class-Identifier AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#getTokenText()
   */
  public String getTokenText()
  {
    if(hasTokenText())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TOKEN_TEXT);
        logger.error( "Failure while trying to obtain Token-Text AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#hasClassIdentifier()
   */
  public boolean hasClassIdentifier()
  {
    return hasAvp( DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#hasTokenText()
   */
  public boolean hasTokenText()
  {
    return hasAvp( DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#setClassIdentifier(net.java.slee.resource.diameter.ro.events.avp.ClassIdentifier)
   */
  public void setClassIdentifier( ClassIdentifier classIdentifier )
  {
    if(hasClassIdentifier())
    {
      throw new IllegalStateException("AVP Class-Identifier is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CLASS_IDENTIFIER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CLASS_IDENTIFIER, classIdentifier.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageClass#setTokenText(java.lang.String)
   */
  public void setTokenText( String tokenText )
  {
    if(hasTokenText())
    {
      throw new IllegalStateException("AVP Token-Text is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TOKEN_TEXT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.TOKEN_TEXT, tokenText, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
