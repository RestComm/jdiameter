package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * SIPDigestAuthenticateImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class SIPDigestAuthenticateImpl extends GroupedAvpImpl implements SIPDigestAuthenticate {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public SIPDigestAuthenticateImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#getDigestAlgorithm()
   */
  public String getDigestAlgorithm() {
    return getAvpAsUTF8String(509, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#getDigestHA1()
   */
  public String getDigestHA1() {
    return getAvpAsUTF8String(511, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#getDigestQoP()
   */
  public String getDigestQoP() {
    return getAvpAsUTF8String(510, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#getDigestRealm()
   */
  public String getDigestRealm() {
    return getAvpAsUTF8String(504, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#hasDigestAlgorithm()
   */
  public boolean hasDigestAlgorithm() {
    return hasAvp(509, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#hasDigestHA1()
   */
  public boolean hasDigestHA1() {
    return hasAvp(511, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#hasDigestQoP()
   */
  public boolean hasDigestQoP() {
    return hasAvp(510, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#hasDigestRealm()
   */
  public boolean hasDigestRealm() {
    return hasAvp(504, 13019L);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#setDigestAlgorithm(java.lang.String)
   */
  public void setDigestAlgorithm(String digestAlgorithm) {
    addAvp(509, 13019L, digestAlgorithm);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#setDigestHA1(java.lang.String)
   */
  public void setDigestHA1(String digestHA1) {
    addAvp(511, 13019L, digestHA1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#setDigestQoP(java.lang.String)
   */
  public void setDigestQoP(String digestQoP) {
    addAvp(510, 13019L, digestQoP);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate#setDigestRealm(java.lang.String)
   */
  public void setDigestRealm(String digestRealm) {
    addAvp(504, 13019L, digestRealm);
  }

}
