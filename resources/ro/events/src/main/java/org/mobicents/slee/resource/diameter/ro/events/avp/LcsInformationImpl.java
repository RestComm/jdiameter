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

import net.java.slee.resource.diameter.ro.events.avp.LcsClientId;
import net.java.slee.resource.diameter.ro.events.avp.LcsInformation;
import net.java.slee.resource.diameter.ro.events.avp.LocationType;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * LcsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:49:03 AM Apr 12, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LcsInformationImpl extends GroupedAvpImpl implements LcsInformation {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public LcsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLcsClientId()
   */
  public LcsClientId getLcsClientId() {
    return (LcsClientId) getAvpAsCustom(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, LcsClientIdImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLocationEstimate()
   */
  public String getLocationEstimate() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#getLocationType()
   */
  public LocationType getLocationType() {
    return (LocationType) getAvpAsCustom(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, LocationTypeImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLcsClientId()
   */
  public boolean hasLcsClientId() {
    return hasAvp( DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLocationEstimate()
   */
  public boolean hasLocationEstimate() {
    return hasAvp( DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#hasLocationType()
   */
  public boolean hasLocationType() {
    return hasAvp( DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLcsClientId(net.java.slee.resource.diameter.ro.events.avp.LcsClientId)
   */
  public void setLcsClientId( LcsClientId lcsClientId ) {
    addAvp(DiameterRoAvpCodes.LCS_CLIENT_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, lcsClientId.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLocationEstimate(java.lang.String)
   */
  public void setLocationEstimate( String locationEstimate ) {
    addAvp(DiameterRoAvpCodes.LOCATION_ESTIMATE, DiameterRoAvpCodes.TGPP_VENDOR_ID, locationEstimate);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.LcsInformation#setLocationType(net.java.slee.resource.diameter.ro.events.avp.LocationType)
   */
  public void setLocationType( LocationType locationType ) {
    addAvp(DiameterRoAvpCodes.LOCATION_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, locationType.byteArrayValue());
  }

}
