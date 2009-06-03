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

import net.java.slee.resource.diameter.ro.events.avp.ImsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MbmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.PocInformation;
import net.java.slee.resource.diameter.ro.events.avp.PsInformation;
import net.java.slee.resource.diameter.ro.events.avp.ServiceInformation;
import net.java.slee.resource.diameter.ro.events.avp.WlanInformation;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * ServiceInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>3:17:26 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ServiceInformationImpl extends GroupedAvpImpl implements ServiceInformation {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ServiceInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getImsInformation()
   */
  public ImsInformation getImsInformation() {
    return (ImsInformation) getAvpAsCustom(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, ImsInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getLcsInformation()
   */
  public LcsInformation getLcsInformation() {
    return (LcsInformation) getAvpAsCustom(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, LcsInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getMbmsInformation()
   */
  public MbmsInformation getMbmsInformation() {
    return (MbmsInformation) getAvpAsCustom(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, MbmsInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getMmsInformation()
   */
  public MmsInformation getMmsInformation() {
    return (MmsInformation) getAvpAsCustom(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, MmsInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getPocInformation()
   */
  public PocInformation getPocInformation() {
    return (PocInformation) getAvpAsCustom(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, PocInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getPsInformation()
   */
  public PsInformation getPsInformation() {
    return (PsInformation) getAvpAsCustom(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, PsInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#getWlanInformation()
   */
  public WlanInformation getWlanInformation() {
    return (WlanInformation) getAvpAsCustom(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, WlanInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasImsInformation()
   */
  public boolean hasImsInformation() {
    return hasAvp( DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasLcsInformation()
   */
  public boolean hasLcsInformation() {
    return hasAvp( DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasMbmsInformation()
   */
  public boolean hasMbmsInformation() {
    return hasAvp( DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasMmsInformation()
   */
  public boolean hasMmsInformation() {
    return hasAvp( DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasPocInformation()
   */
  public boolean hasPocInformation() {
    return hasAvp( DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasPsInformation()
   */
  public boolean hasPsInformation() {
    return hasAvp( DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#hasWlanInformation()
   */
  public boolean hasWlanInformation() {
    return hasAvp( DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setImsInformation(net.java.slee.resource.diameter.ro.events.avp.ImsInformation)
   */
  public void setImsInformation( ImsInformation imsInformation ) {
    addAvp(DiameterRoAvpCodes.IMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, imsInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setLcsInformation(net.java.slee.resource.diameter.ro.events.avp.LcsInformation)
   */
  public void setLcsInformation( LcsInformation lcsInformation ) {
    addAvp(DiameterRoAvpCodes.LCS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setMbmsInformation(net.java.slee.resource.diameter.ro.events.avp.MbmsInformation)
   */
  public void setMbmsInformation( MbmsInformation mbmsInformation ) {
    addAvp(DiameterRoAvpCodes.MBMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, mbmsInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setMmsInformation(net.java.slee.resource.diameter.ro.events.avp.MmsInformation)
   */
  public void setMmsInformation( MmsInformation mmsInformation ) {
    addAvp(DiameterRoAvpCodes.MMS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, mmsInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setPocInformation(net.java.slee.resource.diameter.ro.events.avp.PocInformation)
   */
  public void setPocInformation( PocInformation mmsInformation ) {
    addAvp(DiameterRoAvpCodes.POC_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, mmsInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setPsInformation(net.java.slee.resource.diameter.ro.events.avp.PsInformation)
   */
  public void setPsInformation( PsInformation psInformation ) {
    addAvp(DiameterRoAvpCodes.PS_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, psInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ServiceInformation#setWlanInformation(net.java.slee.resource.diameter.ro.events.avp.WlanInformation)
   */
  public void setWlanInformation( WlanInformation wlanInformation ) {
    addAvp(DiameterRoAvpCodes.WLAN_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID, wlanInformation.byteArrayValue());
  }

}
