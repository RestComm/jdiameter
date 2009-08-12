package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.UserAuthorizationType;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;


/**
 *
 * UserAuthorizationRequestImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UserAuthorizationRequestImpl extends DiameterMessageImpl implements UserAuthorizationRequest {

  /**
   * @param message
   */
  public UserAuthorizationRequestImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "User-Authorization-Request";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "UAR";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getPublicIdentity()
   */
  public String getPublicIdentity() {
    return getAvpAsUTF8String(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getUARFlags()
   */
  public long getUARFlags() {
    return getAvpAsUnsigned32(UAR_FLAGS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getUserAuthorizationType()
   */
  public UserAuthorizationType getUserAuthorizationType() {
    return (UserAuthorizationType) getAvpAsEnumerated(USER_AUTHORIZATION_TYPE, UserAuthorizationType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#getVisitedNetworkIdentifier()
   */
  public String getVisitedNetworkIdentifier() {
    return getAvpAsOctetString(VISITED_NETWORK_IDENTIFIER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#hasPublicIdentity()
   */
  public boolean hasPublicIdentity() {
    return hasAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#hasUARFlags()
   */
  public boolean hasUARFlags() {
    return hasAvp(UAR_FLAGS, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#hasUserAuthorizationType()
   */
  public boolean hasUserAuthorizationType() {
    return hasAvp(USER_AUTHORIZATION_TYPE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#hasVisitedNetworkIdentifier()
   */
  public boolean hasVisitedNetworkIdentifier() {
    return hasAvp(VISITED_NETWORK_IDENTIFIER, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setPublicIdentity(java.lang.String)
   */
  public void setPublicIdentity(String publicIdentity) {
    addAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID, publicIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setUARFlags(long)
   */
  public void setUARFlags(long uarFlags) {
    addAvp(UAR_FLAGS, CXDX_VENDOR_ID, uarFlags);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setUserAuthorizationType(net.java.slee.resource.diameter.cxdx.events.avp.UserAuthorizationType)
   */
  public void setUserAuthorizationType(UserAuthorizationType userAuthorizationType) {
    addAvp(USER_AUTHORIZATION_TYPE, CXDX_VENDOR_ID, (long)userAuthorizationType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest#setVisitedNetworkIdentifier(java.lang.String)
   */
  public void setVisitedNetworkIdentifier(String visitedNetworkIdentifier) {
    addAvp(VISITED_NETWORK_IDENTIFIER, CXDX_VENDOR_ID, visitedNetworkIdentifier);
  }

}
