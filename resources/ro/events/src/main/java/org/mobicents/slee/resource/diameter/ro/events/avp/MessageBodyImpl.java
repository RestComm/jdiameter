package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.MessageBody;
import net.java.slee.resource.diameter.ro.events.avp.Originator;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * MessageBodyImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:08:04 PM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MessageBodyImpl extends GroupedAvpImpl implements MessageBody {

  private static final Logger logger = Logger.getLogger( MessageBodyImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MessageBodyImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentDisposition()
   */
  public String getContentDisposition()
  {
    if(hasContentDisposition())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CONTENT_DISPOSITION);
        logger.error( "Failure while trying to obtain Content-Disposition AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentLength()
   */
  public long getContentLength()
  {
    if(hasContentType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CONTENT_LENGTH);
        logger.error( "Failure while trying to obtain Content-Length AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getContentType()
   */
  public String getContentType()
  {
    if(hasContentType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CONTENT_TYPE);
        logger.error( "Failure while trying to obtain Content-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#getOriginator()
   */
  public Originator getOriginator()
  {
    if(hasOriginator())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Originator.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ORIGINATOR);
        logger.error( "Failure while trying to obtain Originator AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentDisposition()
   */
  public boolean hasContentDisposition()
  {
    return hasAvp( DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentLength()
   */
  public boolean hasContentLength()
  {
    return hasAvp( DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasContentType()
   */
  public boolean hasContentType()
  {
    return hasAvp( DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#hasOriginator()
   */
  public boolean hasOriginator()
  {
    return hasAvp( DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentDisposition(java.lang.String)
   */
  public void setContentDisposition( String contentDisposition )
  {
    if(hasContentDisposition())
    {
      throw new IllegalStateException("AVP Content-Disposition is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CONTENT_DISPOSITION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.CONTENT_DISPOSITION);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_DISPOSITION, contentDisposition, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentLength(long)
   */
  public void setContentLength( long contentLength )
  {
    if(hasContentDisposition())
    {
      throw new IllegalStateException("AVP Content-Length is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CONTENT_LENGTH, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.CONTENT_LENGTH);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_LENGTH, contentLength, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setContentType(java.lang.String)
   */
  public void setContentType( String contentType )
  {
    if(hasContentDisposition())
    {
      throw new IllegalStateException("AVP Content-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.CONTENT_TYPE);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_TYPE, contentType, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MessageBody#setOriginator(net.java.slee.resource.diameter.ro.events.avp.Originator)
   */
  public void setOriginator( Originator originator )
  {
    if(hasOriginator())
    {
      throw new IllegalStateException("AVP Originator is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ORIGINATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.ORIGINATOR);
      super.avpSet.addAvp(DiameterRoAvpCodes.ORIGINATOR, originator.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
