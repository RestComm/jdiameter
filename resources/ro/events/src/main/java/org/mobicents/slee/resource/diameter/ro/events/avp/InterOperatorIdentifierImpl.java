package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * InterOperatorIdentifierImpl.java
 *
 * <br>Project:  mobicents
 * <br>8:03:55 PM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class InterOperatorIdentifierImpl extends GroupedAvpImpl implements InterOperatorIdentifier {

  private static final Logger logger = Logger.getLogger( InterOperatorIdentifierImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public InterOperatorIdentifierImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#getOriginatingIoi()
   */
  public String getOriginatingIoi()
  {
    if(hasOriginatingIoi())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ORIGINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ORIGINATING_IOI);
        logger.error( "Failure while trying to obtain Originating-IOI AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#getTerminatingIoi()
   */
  public String getTerminatingIoi()
  {
    if(hasTerminatingIoi())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.TERMINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.TERMINATING_IOI);
        logger.error( "Failure while trying to obtain Terminating-IOI AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#hasOriginatingIoi()
   */
  public boolean hasOriginatingIoi()
  {
    return hasAvp(DiameterRoAvpCodes.ORIGINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#hasTerminatingIoi()
   */
  public boolean hasTerminatingIoi()
  {
    return hasAvp(DiameterRoAvpCodes.TERMINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#setOriginatingIoi(java.lang.String)
   */
  public void setOriginatingIoi( String originatingIoi )
  {
    if(hasOriginatingIoi())
    {
      throw new IllegalStateException("AVP Originating-IOI is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ORIGINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.ORIGINATING_IOI);
      super.avpSet.addAvp(DiameterRoAvpCodes.ORIGINATING_IOI, originatingIoi, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.InterOperatorIdentifier#setTerminatingIoi(java.lang.String)
   */
  public void setTerminatingIoi( String terminatingIoi )
  {
    if(hasTerminatingIoi())
    {
      throw new IllegalStateException("AVP Terminating-IOI is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.TERMINATING_IOI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.TERMINATING_IOI);
      super.avpSet.addAvp(DiameterRoAvpCodes.TERMINATING_IOI, terminatingIoi, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
