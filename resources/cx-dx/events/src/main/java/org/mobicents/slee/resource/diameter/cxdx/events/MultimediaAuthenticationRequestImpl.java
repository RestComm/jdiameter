package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItemImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;

/**
 *
 * MultimediaAuthenticationRequestImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MultimediaAuthenticationRequestImpl extends DiameterMessageImpl implements MultimediaAuthenticationRequest {

  /**
   * @param message
   */
  public MultimediaAuthenticationRequestImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "Multimedia-Authentication-Request";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "MAR";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getPublicIdentity()
   */
  public String getPublicIdentity() {
    return getAvpAsUTF8String(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getSIPAuthDataItem()
   */
  public SIPAuthDataItem getSIPAuthDataItem() {
    return (SIPAuthDataItem) getAvpAsCustom(SIP_AUTH_DATA_ITEM, CXDX_VENDOR_ID, SIPAuthDataItemImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getSIPNumberAuthItems()
   */
  public long getSIPNumberAuthItems() {
    return getAvpAsUnsigned32(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getServerName()
   */
  public String getServerName() {
    return getAvpAsUTF8String(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#hasPublicIdentity()
   */
  public boolean hasPublicIdentity() {
    return hasAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#hasSIPAuthDataItem()
   */
  public boolean hasSIPAuthDataItem() {
    return hasAvp(SIP_AUTH_DATA_ITEM, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#hasSIPNumberAuthItems()
   */
  public boolean hasSIPNumberAuthItems() {
    return hasAvp(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#hasServerName()
   */
  public boolean hasServerName() {
    return hasAvp(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setPublicIdentity(java.lang.String)
   */
  public void setPublicIdentity(String publicIdentity) {
    addAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID, publicIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setSIPAuthDataItem(net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem)
   */
  public void setSIPAuthDataItem(SIPAuthDataItem sipAuthDataItem) {
    addAvp(SIP_AUTH_DATA_ITEM, CXDX_VENDOR_ID, sipAuthDataItem.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setSIPNumberAuthItems(long)
   */
  public void setSIPNumberAuthItems(long sipNumberAuthItems) {
    addAvp(SIP_NUMBER_AUTH_ITEMS, CXDX_VENDOR_ID, sipNumberAuthItems);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setServerName(java.lang.String)
   */
  public void setServerName(String serverName) {
    addAvp(SERVER_NAME, CXDX_VENDOR_ID, serverName);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

}
