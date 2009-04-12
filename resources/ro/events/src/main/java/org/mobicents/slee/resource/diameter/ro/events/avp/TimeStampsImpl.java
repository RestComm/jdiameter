package org.mobicents.slee.resource.diameter.ro.events.avp;

import java.util.Date;

import net.java.slee.resource.diameter.ro.events.avp.TimeStamps;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * TimeStampsImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:10:40 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TimeStampsImpl extends GroupedAvpImpl implements TimeStamps {

  private static final Logger logger = Logger.getLogger( ApplicationServerInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public TimeStampsImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#getSipRequestTimestamp()
   */
  public Date getSipRequestTimestamp()
  {
    if(hasSipRequestTimestamp())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getTime();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP);
        logger.error( "Failure while trying to obtain SIP-Request-Timestamp AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#getSipResponseTimestamp()
   */
  public Date getSipResponseTimestamp()
  {
    if(hasSipResponseTimestamp())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getTime();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP);
        logger.error( "Failure while trying to obtain SIP-Response-Timestamp AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#hasSipRequestTimestamp()
   */
  public boolean hasSipRequestTimestamp()
  {
    return hasAvp( DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#hasSipResponseTimestamp()
   */
  public boolean hasSipResponseTimestamp()
  {
    return hasAvp( DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#setSipRequestTimestamp(java.util.Date)
   */
  public void setSipRequestTimestamp( Date sipRequestTimestamp )
  {
    if(hasSipRequestTimestamp())
    {
      throw new IllegalStateException("AVP SIP-Request-Timestamp is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP);
      super.avpSet.addAvp(DiameterRoAvpCodes.SIP_REQUEST_TIMESTAMP, sipRequestTimestamp, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TimeStamps#setSipResponseTimestamp(java.util.Date)
   */
  public void setSipResponseTimestamp( Date sipResponseTimestamp )
  {
    if(hasSipResponseTimestamp())
    {
      throw new IllegalStateException("AVP SIP-Response-Timestamp is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP);
      super.avpSet.addAvp(DiameterRoAvpCodes.SIP_RESPONSE_TIMESTAMP, sipResponseTimestamp, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

}
