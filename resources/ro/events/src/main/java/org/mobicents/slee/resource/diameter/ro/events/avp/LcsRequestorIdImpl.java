package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * LcsRequestorIdImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:41:44 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LcsRequestorIdImpl extends GroupedAvpImpl implements LcsRequestorId {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsRequestorIdImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#getLcsDataCodingScheme()
   */
  public String getLcsDataCodingScheme() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#getLcsRequestorIdString()
   */
  public String getLcsRequestorIdString() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#hasLcsDataCodingScheme()
   */
  public boolean hasLcsDataCodingScheme()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#hasLcsRequestorIdString()
   */
  public boolean hasLcsRequestorIdString()
  {
    return hasAvp( DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#setLcsDataCodingScheme(java.lang.String)
   */
  public void setLcsDataCodingScheme( String lcsDataCodingScheme ) {
    addAvp(DiameterRoAvpCodes.LCS_DATA_CODING_SCHEME, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsDataCodingScheme);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsRequestorId#setLcsRequestorIdString(java.lang.String)
   */
  public void setLcsRequestorIdString( String lcsRequestorIdString ) {
    addAvp(DiameterRoAvpCodes.LCS_REQUESTOR_ID_STRING, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsRequestorIdString);
  }

}
