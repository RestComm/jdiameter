package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * SIPAuthDataItemImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SIPAuthDataItemImpl extends GroupedAvpImpl implements SIPAuthDataItem {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public SIPAuthDataItemImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getConfidentialityKey()
   */
  public String getConfidentialityKey() {
    return getAvpAsOctetString(CONFIDENTIALITY_KEY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getFramedIPAddress()
   */
  public String getFramedIPAddress() {
    // 6.11.1. Framed-IP-Address AVP
    // The Framed-IP-Address AVP (AVP Code 8) [RADIUS] is of type OctetString
    return getAvpAsOctetString(8);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getFramedIPv6Prefix()
   */
  public String getFramedIPv6Prefix() {
    // 6.11.6. Framed-IPv6-Prefix AVP
    // The Framed-IPv6-Prefix AVP (AVP Code 97) is of type OctetString
    return getAvpAsOctetString(97);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getFramedInterfaceId()
   */
  public long getFramedInterfaceId() {
    // 6.11.5. Framed-Interface-Id AVP
    // The Framed-Interface-Id AVP (AVP Code 96) is of type Unsigned64
    return getAvpAsUnsigned64(96);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getIntegrityKey()
   */
  public String getIntegrityKey() {
    return getAvpAsOctetString(INTEGRITY_KEY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getLineIdentifiers()
   */
  public String getLineIdentifiers() {
    // 6.3.42  Line-Identifier AVP
    // The Line-Identifier AVP is of type OctetString. This AVP has Vendor Id ETSI (13019) and AVP code 500.
    return getAvpAsOctetString(500, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPAuthenticate()
   */
  public String getSIPAuthenticate() {
    return getAvpAsOctetString(SIP_AUTHENTICATE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPAuthenticationContext()
   */
  public String getSIPAuthenticationContext() {
    return getAvpAsOctetString(SIP_AUTHENTICATION_CONTEXT, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPAuthenticationScheme()
   */
  public String getSIPAuthenticationScheme() {
    return getAvpAsUTF8String(SIP_AUTHENTICATION_SCHEME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPAuthorization()
   */
  public String getSIPAuthorization() {
    return getAvpAsOctetString(SIP_AUTHORIZATION, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPDigestAuthenticate()
   */
  public SIPDigestAuthenticate getSIPDigestAuthenticate() {
    return (SIPDigestAuthenticate) getAvpAsCustom(SIP_DIGEST_AUTHENTICATE, CXDX_VENDOR_ID, SIPDigestAuthenticateImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#getSIPItemNumber()
   */
  public long getSIPItemNumber() {
    return getAvpAsUnsigned32(SIP_ITEM_NUMBER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasConfidentialityKey()
   */
  public boolean hasConfidentialityKey() {
    return hasAvp(CONFIDENTIALITY_KEY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasFramedIPAddress()
   */
  public boolean hasFramedIPAddress() {
    return hasAvp(8);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasFramedIPv6Prefix()
   */
  public boolean hasFramedIPv6Prefix() {
    return hasAvp(97);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasFramedInterfaceId()
   */
  public boolean hasFramedInterfaceId() {
    return hasAvp(96);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasIntegrityKey()
   */
  public boolean hasIntegrityKey() {
    return hasAvp(INTEGRITY_KEY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPAuthenticate()
   */
  public boolean hasSIPAuthenticate() {
    return hasAvp(SIP_AUTHENTICATE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPAuthenticationContext()
   */
  public boolean hasSIPAuthenticationContext() {
    return hasAvp(SIP_AUTHENTICATION_CONTEXT, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPAuthenticationScheme()
   */
  public boolean hasSIPAuthenticationScheme() {
    return hasAvp(SIP_AUTHENTICATION_SCHEME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPAuthorization()
   */
  public boolean hasSIPAuthorization() {
    return hasAvp(SIP_AUTHORIZATION, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPDigestAuthenticate()
   */
  public boolean hasSIPDigestAuthenticate() {
    return hasAvp(SIP_DIGEST_AUTHENTICATE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#hasSIPItemNumber()
   */
  public boolean hasSIPItemNumber() {
    return hasAvp(SIP_ITEM_NUMBER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setConfidentialityKey(java.lang.String)
   */
  public void setConfidentialityKey(String confidentialityKey) {
    addAvp(CONFIDENTIALITY_KEY, CXDX_VENDOR_ID, confidentialityKey);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setFramedIPAddress(java.lang.String)
   */
  public void setFramedIPAddress(String framedIPAddress) {
    addAvp(8, framedIPAddress);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setFramedIPv6Prefix(java.lang.String)
   */
  public void setFramedIPv6Prefix(String framedIPv6Prefix) {
    addAvp(97, framedIPv6Prefix);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setFramedInterfaceId(long)
   */
  public void setFramedInterfaceId(long framedInterfaceId) {
    addAvp(96, framedInterfaceId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setIntegrityKey(java.lang.String)
   */
  public void setIntegrityKey(String integrityKey) {
    addAvp(INTEGRITY_KEY, CXDX_VENDOR_ID, integrityKey);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setLineIdentifier(java.lang.String)
   */
  public void setLineIdentifier(String lineIdentifier) {
    addAvp(500, 13019L, lineIdentifier);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setLineIdentifiers(java.lang.String[])
   */
  public void setLineIdentifiers(String[] lineIdentifiers) {
    for(String lineIdentifier : lineIdentifiers) {
      setLineIdentifier(lineIdentifier);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPAuthenticate(java.lang.String)
   */
  public void setSIPAuthenticate(String sipAuthenticate) {
    addAvp(SIP_AUTHENTICATE, CXDX_VENDOR_ID, sipAuthenticate);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPAuthenticationContext(java.lang.String)
   */
  public void setSIPAuthenticationContext(String sipAuthenticationContext) {
    addAvp(SIP_AUTHENTICATION_CONTEXT, CXDX_VENDOR_ID, sipAuthenticationContext);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPAuthenticationScheme(java.lang.String)
   */
  public void setSIPAuthenticationScheme(String sipAuthenticationScheme) {
    addAvp(SIP_AUTHENTICATION_SCHEME, CXDX_VENDOR_ID, sipAuthenticationScheme);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPAuthorization(java.lang.String)
   */
  public void setSIPAuthorization(String sipAuthorization) {
    addAvp(SIP_AUTHORIZATION, CXDX_VENDOR_ID, sipAuthorization);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPDigestAuthenticate(net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate)
   */
  public void setSIPDigestAuthenticate(SIPDigestAuthenticate sipDigestAuthenticate) {
    addAvp(SIP_DIGEST_AUTHENTICATE, CXDX_VENDOR_ID, sipDigestAuthenticate.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem#setSIPItemNumber(long)
   */
  public void setSIPItemNumber(long sipItemNumber) {
    addAvp(SIP_ITEM_NUMBER, CXDX_VENDOR_ID, sipItemNumber);
  }

}
