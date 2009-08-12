package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * SubscriptionInfoImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SubscriptionInfoImpl extends GroupedAvpImpl implements SubscriptionInfo {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public SubscriptionInfoImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#getCallIDSIPHeader()
   */
  public String getCallIDSIPHeader() {
    return getAvpAsOctetString(CALL_ID_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#getContact()
   */
  public String getContact() {
    return getAvpAsOctetString(CONTACT, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#getFromSIPHeader()
   */
  public String getFromSIPHeader() {
    return getAvpAsOctetString(FROM_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#getRecordRoute()
   */
  public String getRecordRoute() {
    return getAvpAsOctetString(RECORD_ROUTE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#getToSIPHeader()
   */
  public String getToSIPHeader() {
    return getAvpAsOctetString(TO_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#hasCallIDSIPHeader()
   */
  public boolean hasCallIDSIPHeader() {
    return hasAvp(CALL_ID_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#hasContact()
   */
  public boolean hasContact() {
    return hasAvp(CONTACT, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#hasFromSIPHeader()
   */
  public boolean hasFromSIPHeader() {
    return hasAvp(FROM_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#hasRecordRoute()
   */
  public boolean hasRecordRoute() {
    return hasAvp(RECORD_ROUTE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#hasToSIPHeader()
   */
  public boolean hasToSIPHeader() {
    return hasAvp(TO_SIP_HEADER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#setCallIDSIPHeader(java.lang.String)
   */
  public void setCallIDSIPHeader(String callIDSIPHeader) {
    addAvp(CALL_ID_SIP_HEADER, CXDX_VENDOR_ID, callIDSIPHeader);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#setContact(java.lang.String)
   */
  public void setContact(String contact) {
    addAvp(CONTACT, CXDX_VENDOR_ID, contact);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#setFromSIPHeader(java.lang.String)
   */
  public void setFromSIPHeader(String fromSIPHeader) {
    addAvp(FROM_SIP_HEADER, CXDX_VENDOR_ID, fromSIPHeader);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#setRecordRoute(java.lang.String)
   */
  public void setRecordRoute(String recordRoute) {
    addAvp(RECORD_ROUTE, CXDX_VENDOR_ID, recordRoute);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo#setToSIPHeader(java.lang.String)
   */
  public void setToSIPHeader(String toSIPHeader) {
    addAvp(TO_SIP_HEADER, CXDX_VENDOR_ID, toSIPHeader);
  }

}
