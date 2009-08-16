package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * DeregistrationReasonImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DeregistrationReasonImpl extends GroupedAvpImpl implements DeregistrationReason {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public DeregistrationReasonImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#getReasonCode()
   */
  public ReasonCode getReasonCode() {
    return (ReasonCode) getAvpAsEnumerated(REASON_CODE, CXDX_VENDOR_ID, ReasonCode.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#getReasonInfo()
   */
  public String getReasonInfo() {
    return getAvpAsUTF8String(REASON_INFO, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#hasReasonCode()
   */
  public boolean hasReasonCode() {
    return hasAvp(REASON_CODE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#hasReasonInfo()
   */
  public boolean hasReasonInfo() {
    return hasAvp(REASON_INFO, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#setReasonCode(net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode)
   */
  public void setReasonCode(ReasonCode reasonCode) {
    addAvp(REASON_CODE, CXDX_VENDOR_ID, (long)reasonCode.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason#setReasonInfo(java.lang.String)
   */
  public void setReasonInfo(String reasonInfo) {
    addAvp(REASON_INFO, CXDX_VENDOR_ID, reasonInfo);
  }

}
