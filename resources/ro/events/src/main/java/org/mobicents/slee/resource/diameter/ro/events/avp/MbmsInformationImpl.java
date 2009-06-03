/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported;
import net.java.slee.resource.diameter.ro.events.avp.Mbms2g3gIndicator;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsServiceType;
import net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * MbmsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:22:57 PM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */ 
public class MbmsInformationImpl extends GroupedAvpImpl implements MbmsInformation {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MbmsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getFileRepairSupported()
   */
  public FileRepairSupported getFileRepairSupported() {
    return (FileRepairSupported) getAvpAsEnumerated(DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, FileRepairSupported.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbms2g3gIndicator()
   */
  public Mbms2g3gIndicator getMbms2g3gIndicator() {
    return (Mbms2g3gIndicator) getAvpAsEnumerated(DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID, Mbms2g3gIndicator.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsServiceAreas()
   */
  public String[] getMbmsServiceAreas() {
    return getAvpsAsOctetString(DiameterRoAvpCodes.MBMS_SERVICE_AREA, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsServiceType()
   */
  public MbmsServiceType getMbmsServiceType() {
    return (MbmsServiceType) getAvpAsEnumerated(DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, MbmsServiceType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsSessionIdentity()
   */
  public String getMbmsSessionIdentity() {
    return getAvpAsOctetString(DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getMbmsUserServiceType()
   */
  public MbmsUserServiceType getMbmsUserServiceType() {
    return (MbmsUserServiceType) getAvpAsEnumerated(DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, MbmsUserServiceType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getRai()
   */
  public String getRai() {
    return getAvpAsOctetString(DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getRequiredMbmsBearerCapabilities()
   */
  public String getRequiredMbmsBearerCapabilities() {
    return getAvpAsOctetString(DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#getTmgi()
   */
  public String getTmgi() {
    return getAvpAsOctetString(DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasFileRepairSupported()
   */
  public boolean hasFileRepairSupported() {
    return hasAvp( DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbms2g3gIndicator()
   */
  public boolean hasMbms2g3gIndicator() {
    return hasAvp( DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsServiceType()
   */
  public boolean hasMbmsServiceType() {
    return hasAvp( DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsSessionIdentity()
   */
  public boolean hasMbmsSessionIdentity() {
    return hasAvp( DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasMbmsUserServiceType()
   */
  public boolean hasMbmsUserServiceType() {
    return hasAvp( DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasRai()
   */
  public boolean hasRai() {
    return hasAvp( DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasRequiredMbmsBearerCapabilities()
   */
  public boolean hasRequiredMbmsBearerCapabilities() {
    return hasAvp( DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#hasTmgi()
   */
  public boolean hasTmgi() {
    return hasAvp( DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setFileRepairSupported(net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported)
   */
  public void setFileRepairSupported( FileRepairSupported fileRepairSupported ) {
    addAvp(DiameterRoAvpCodes.FILE_REPAIR_SUPPORTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, fileRepairSupported.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbms2g3gIndicator(java.lang.String)
   */
  public void setMbms2g3gIndicator( Mbms2g3gIndicator mbms2g3gIndicator ) {
    addAvp(DiameterRoAvpCodes.MBMS_2G_3G_INDICATOR, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbms2g3gIndicator.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceArea(java.lang.String)
   */
  public void setMbmsServiceArea( String mbmsServiceArea ) {
    addAvp(DiameterRoAvpCodes.MBMS_SERVICE_AREA, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbmsServiceArea);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceAreas(String[])
   */
  public void setMbmsServiceAreas( String[] mbmsServiceAreas ) {
    for(String mbmsServiceArea : mbmsServiceAreas) {
      setMbmsServiceArea(mbmsServiceArea);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsServiceType(java.lang.String)
   */
  public void setMbmsServiceType( MbmsServiceType mbmsServiceType ) {
    addAvp(DiameterRoAvpCodes.MBMS_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbmsServiceType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsSessionIdentity(java.lang.String)
   */
  public void setMbmsSessionIdentity( String mbmsSessionIdentity ) {
    addAvp(DiameterRoAvpCodes.MBMS_SESSION_IDENTITY, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbmsSessionIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setMbmsUserServiceType(net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType)
   */
  public void setMbmsUserServiceType( MbmsUserServiceType mbmsUserServiceType ) {
    addAvp(DiameterRoAvpCodes.MBMS_USER_SERVICE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbmsUserServiceType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setRai(java.lang.String)
   */
  public void setRai( String rai ) {
    addAvp(DiameterRoAvpCodes.RAI, DiameterRoAvpCodes.TGPP_VENDOR_ID, rai);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setRequiredMbmsBearerCapabilities(java.lang.String)
   */
  public void setRequiredMbmsBearerCapabilities( String requiredMbmsBearerCapabilities ) {
    addAvp(DiameterRoAvpCodes.REQUIRED_MBMS_BEARER_CAPABILITIES, DiameterRoAvpCodes.TGPP_VENDOR_ID, requiredMbmsBearerCapabilities);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MbmsInformation#setTmgi(java.lang.String)
   */
  public void setTmgi( String tmgi ) {
    addAvp(DiameterRoAvpCodes.TMGI, DiameterRoAvpCodes.TGPP_VENDOR_ID, tmgi);
  }

}
