package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.EventType;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * EventTypeImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:20:17 AM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class EventTypeImpl extends GroupedAvpImpl implements EventType {

  private static final Logger logger = Logger.getLogger( EventTypeImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public EventTypeImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#getEvent()
   */
  public String getEvent()
  {
    if(hasEvent())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.EVENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.EVENT);
        logger.error( "Failure while trying to obtain Event AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#getExpires()
   */
  public long getExpires()
  {
    if(hasExpires())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.EXPIRES, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.EXPIRES);
        logger.error( "Failure while trying to obtain Expires AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#getSipMethod()
   */
  public String getSipMethod()
  {
    if(hasSipMethod())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SIP_METHOD, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SIP_METHOD);
        logger.error( "Failure while trying to obtain SIP-Method AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#hasEvent()
   */
  public boolean hasEvent()
  {
    return hasAvp(DiameterRoAvpCodes.EVENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#hasExpires()
   */
  public boolean hasExpires()
  {
    return hasAvp(DiameterRoAvpCodes.EXPIRES, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#hasSipMethod()
   */
  public boolean hasSipMethod()
  {
    return hasAvp(DiameterRoAvpCodes.SIP_METHOD, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#setEvent(java.lang.String)
   */
  public void setEvent( String event )
  {
    if(hasEvent())
    {
      throw new IllegalStateException("AVP Event is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.EVENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.EVENT, event, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#setExpires(long)
   */
  public void setExpires( long expires )
  {
    if(hasExpires())
    {
      throw new IllegalStateException("AVP Expires is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.EXPIRES, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.EXPIRES, expires, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.EventType#setSipMethod(java.lang.String)
   */
  public void setSipMethod( String sipMethod )
  {
    if(hasSipMethod())
    {
      throw new IllegalStateException("AVP SIP-Method is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SIP_METHOD, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.SIP_METHOD, sipMethod, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
