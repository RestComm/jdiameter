package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.DeregistrationReasonImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;

/**
 *
 * RegistrationTerminationRequestImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RegistrationTerminationRequestImpl extends DiameterMessageImpl implements RegistrationTerminationRequest {

  /**
   * @param message
   */
  public RegistrationTerminationRequestImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "Registration-Termination-Request";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "RTR";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#getAssociatedIdentities()
   */
  public AssociatedIdentities getAssociatedIdentities() {
    return (AssociatedIdentities) getAvpAsCustom(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID, AssociatedIdentitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#getDeregistrationReason()
   */
  public DeregistrationReason getDeregistrationReason() {
    return (DeregistrationReason) getAvpAsCustom(DEREGISTRATION_REASON, CXDX_VENDOR_ID, DeregistrationReasonImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#getPublicIdentities()
   */
  public String[] getPublicIdentities() {
    return getAvpsAsUTF8String(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#hasAssociatedIdentities()
   */
  public boolean hasAssociatedIdentities() {
    return hasAvp(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#hasDeregistrationReason()
   */
  public boolean hasDeregistrationReason() {
    return hasAvp(DEREGISTRATION_REASON, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setAssociatedIdentities(net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities)
   */
  public void setAssociatedIdentities(AssociatedIdentities associatedIdentities) {
    addAvp(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID, associatedIdentities.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setDeregistrationReason(net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason)
   */
  public void setDeregistrationReason(DeregistrationReason publicIdentity) {
    addAvp(DEREGISTRATION_REASON, CXDX_VENDOR_ID, publicIdentity.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setPublicIdentities(java.lang.String[])
   */
  public void setPublicIdentities(String[] publicIdentities) {
    for(String publicIdentity : publicIdentities) {
      setPublicIdentity(publicIdentity);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setPublicIdentity(java.lang.String)
   */
  public void setPublicIdentity(String publicIdentity) {
    addAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID, publicIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

}
