package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * TrunkGroupIdImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:52:51 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TrunkGroupIdImpl extends GroupedAvpImpl implements TrunkGroupId {

  private static final Logger logger = Logger.getLogger( TrunkGroupIdImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public TrunkGroupIdImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#getIncomingTrunkGroupId()
   */
  public String getIncomingTrunkGroupId()
  {
    if(hasIncomingTrunkGroupId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID);
        logger.error( "Failure while trying to obtain Incoming-Trunk-Group-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#getOutgoingTrunkGroupId()
   */
  public String getOutgoingTrunkGroupId()
  {
    if(hasOutgoingTrunkGroupId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID);
        logger.error( "Failure while trying to obtain Outgoing-Trunk-Group-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#hasIncomingTrunkGroupId()
   */
  public boolean hasIncomingTrunkGroupId()
  {
    return hasAvp( DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#hasOutgoingTrunkGroupId()
   */
  public boolean hasOutgoingTrunkGroupId()
  {
    return hasAvp( DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#setIncomingTrunkGroupId(java.lang.String)
   */
  public void setIncomingTrunkGroupId( String incomingTrunkGroupId )
  {
    if(hasIncomingTrunkGroupId())
    {
      throw new IllegalStateException("AVP Incoming-Trunk-Group-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID);
      super.avpSet.addAvp(DiameterRoAvpCodes.INCOMING_TRUNK_GROUP_ID, incomingTrunkGroupId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.TrunkGroupId#setOutgoingTrunkGroupId(java.lang.String)
   */
  public void setOutgoingTrunkGroupId( String outgoingTrunkGroupId )
  {
    if(hasOutgoingTrunkGroupId())
    {
      throw new IllegalStateException("AVP Outgoing-Trunk-Group-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID);
      super.avpSet.addAvp(DiameterRoAvpCodes.OUTGOING_TRUNK_GROUP_ID, outgoingTrunkGroupId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
