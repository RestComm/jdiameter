package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItemImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;


/**
 *
 * MultimediaAuthenticationAnswerImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MultimediaAuthenticationAnswerImpl extends DiameterMessageImpl implements MultimediaAuthenticationAnswer {

  /**
   * @param message
   */
  public MultimediaAuthenticationAnswerImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "Multimedia-Authentication-Answer";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "MAA";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getExperimentalResult()
   */
  public ExperimentalResultAvp getExperimentalResult() {
    return (ExperimentalResultAvp) getAvpAsCustom(DiameterAvpCodes.EXPERIMENTAL_RESULT, ExperimentalResultAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getPublicIdentity()
   */
  public String getPublicIdentity() {
    return getAvpAsUTF8String(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getSIPAuthDataItems()
   */
  public SIPAuthDataItem[] getSIPAuthDataItems() {
    return (SIPAuthDataItem[]) getAvpsAsCustom(SIP_AUTH_DATA_ITEM, CXDX_VENDOR_ID, SIPAuthDataItemImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getSIPNumberAuthItems()
   */
  public long getSIPNumberAuthItems() {
    return getAvpAsUnsigned32(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#getWildcardedIMPU()
   */
  public String getWildcardedIMPU() {
    return getAvpAsUTF8String(WILDCARDED_IMPU, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#hasExperimentalResult()
   */
  public boolean hasExperimentalResult() {
    return hasAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#hasPublicIdentity()
   */
  public boolean hasPublicIdentity() {
    return hasAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#hasSIPNumberAuthItems()
   */
  public boolean hasSIPNumberAuthItems() {
    return hasAvp(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#hasWildcardedIMPU()
   */
  public boolean hasWildcardedIMPU() {
    return hasAvp(WILDCARDED_IMPU, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setExperimentalResult(net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp)
   */
  public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
    addAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT, experimentalResult.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setPublicIdentity(java.lang.String)
   */
  public void setPublicIdentity(String publicIdentity) {
    addAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID, publicIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setSIPAuthDataItem(net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem)
   */
  public void setSIPAuthDataItem(SIPAuthDataItem sipAuthDataItem) {
    addAvp(SIP_AUTH_DATA_ITEM, CXDX_VENDOR_ID, sipAuthDataItem.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setSIPAuthDataItems(net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem[])
   */
  public void setSIPAuthDataItems(SIPAuthDataItem[] sipAuthDataItems) {
    for(SIPAuthDataItem sipAuthDataItem : sipAuthDataItems) {
      setSIPAuthDataItem(sipAuthDataItem);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setSIPNumberAuthItems(long)
   */
  public void setSIPNumberAuthItems(long sipNumberAuthItems) {
    addAvp(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID, sipNumberAuthItems);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer#setWildcardedIMPU(java.lang.String)
   */
  public void setWildcardedIMPU(String wildcardedIMPU) {
    addAvp(WILDCARDED_IMPU, CXDX_VENDOR_ID, wildcardedIMPU);
  }

}
