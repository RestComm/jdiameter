package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * SCSCFRestorationInfoImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SCSCFRestorationInfoImpl extends GroupedAvpImpl implements SCSCFRestorationInfo {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public SCSCFRestorationInfoImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#getRestorationInfos()
   */
  public RestorationInfo[] getRestorationInfos() {
    return (RestorationInfo[]) getAvpsAsCustom(RESTORATION_INFO, CXDX_VENDOR_ID, RestorationInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#getUserName()
   */
  public String getUserName() {
    return getAvpAsUTF8String(DiameterAvpCodes.USER_NAME);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#hasUserName()
   */
  public boolean hasUserName() {
    return hasAvp(DiameterAvpCodes.USER_NAME);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#setRestorationInfo(net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo)
   */
  public void setRestorationInfo(RestorationInfo restorationInfo) throws IllegalStateException {
    addAvp(RESTORATION_INFO, CXDX_VENDOR_ID, restorationInfo.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#setRestorationInfos(net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo[])
   */
  public void setRestorationInfos(RestorationInfo[] restorationInfos) throws IllegalStateException {
    for(RestorationInfo restorationInfo : restorationInfos) {
      setRestorationInfo(restorationInfo);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo#setUserName(java.lang.String)
   */
  public void setUserName(String userName) throws IllegalStateException {
    addAvp(DiameterAvpCodes.USER_NAME, userName);
  }

}
