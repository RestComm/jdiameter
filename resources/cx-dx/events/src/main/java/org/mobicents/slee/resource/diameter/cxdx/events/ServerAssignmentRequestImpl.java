package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.avp.MultipleRegistrationIndication;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.ServerAssignmentType;
import net.java.slee.resource.diameter.cxdx.events.avp.UserDataAlreadyAvailable;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfoImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;


/**
 *
 * ServerAssignmentRequestImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerAssignmentRequestImpl extends DiameterMessageImpl implements ServerAssignmentRequest {

  /**
   * @param message
   */
  public ServerAssignmentRequestImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "Server-Assignment-Request";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "SAR";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getMultipleRegistrationIndication()
   */
  public MultipleRegistrationIndication getMultipleRegistrationIndication() {
    return (MultipleRegistrationIndication) getAvpAsEnumerated(MULTIPLE_REGISTRATION_INDICATION, CXDX_VENDOR_ID, MultipleRegistrationIndication.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getPublicIdentities()
   */
  public String[] getPublicIdentities() {
    return getAvpsAsUTF8String(PUBLIC_IDENTITY, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getSCSCFRestorationInfo()
   */
  public SCSCFRestorationInfo getSCSCFRestorationInfo() {
    return (SCSCFRestorationInfo) getAvpAsCustom(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID, SCSCFRestorationInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getServerAssignmentType()
   */
  public ServerAssignmentType getServerAssignmentType() {
    return (ServerAssignmentType) getAvpAsEnumerated(SERVER_ASSIGNMENT_TYPE, ServerAssignmentType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getServerName()
   */
  public String getServerName() {
    return getAvpAsUTF8String(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getUserDataAlreadyAvailable()
   */
  public UserDataAlreadyAvailable getUserDataAlreadyAvailable() {
    return (UserDataAlreadyAvailable) getAvpAsEnumerated(USER_DATA_ALREADY_AVAILABLE, UserDataAlreadyAvailable.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getWildcardedIMPU()
   */
  public String getWildcardedIMPU() {
    return getAvpAsUTF8String(WILDCARDED_IMPU, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#getWildcardedPSI()
   */
  public String getWildcardedPSI() {
    return getAvpAsUTF8String(WILDCARDED_PSI, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasMultipleRegistrationIndication()
   */
  public boolean hasMultipleRegistrationIndication() {
    return hasAvp(MULTIPLE_REGISTRATION_INDICATION, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasSCSCFRestorationInfo()
   */
  public boolean hasSCSCFRestorationInfo() {
    return hasAvp(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasServerAssignmentType()
   */
  public boolean hasServerAssignmentType() {
    return hasAvp(SERVER_ASSIGNMENT_TYPE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasServerName()
   */
  public boolean hasServerName() {
    return hasAvp(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasUserDataAlreadyAvailable()
   */
  public boolean hasUserDataAlreadyAvailable() {
    return hasAvp(USER_DATA_ALREADY_AVAILABLE, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasWildcardedIMPU()
   */
  public boolean hasWildcardedIMPU() {
    return hasAvp(WILDCARDED_IMPU, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#hasWildcardedPSI()
   */
  public boolean hasWildcardedPSI() {
    return hasAvp(WILDCARDED_PSI, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setMultipleRegistrationIndication(net.java.slee.resource.diameter.cxdx.events.avp.MultipleRegistrationIndication)
   */
  public void setMultipleRegistrationIndication(MultipleRegistrationIndication multipleRegistrationIndication) {
    addAvp(MULTIPLE_REGISTRATION_INDICATION, CXDX_VENDOR_ID, (long)multipleRegistrationIndication.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setPublicIdentities(java.lang.String[])
   */
  public void setPublicIdentities(String[] publicIdentities) {
    for(String publicIdentity : publicIdentities) {
      setPublicIdentity(publicIdentity);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setPublicIdentity(java.lang.String)
   */
  public void setPublicIdentity(String publicIdentity) {
    addAvp(PUBLIC_IDENTITY, CXDX_VENDOR_ID, publicIdentity);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setSCSCFRestorationInfo(net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo)
   */
  public void setSCSCFRestorationInfo(SCSCFRestorationInfo scscfRestorationInfo) {
    addAvp(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID, scscfRestorationInfo.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setServerAssignmentType(net.java.slee.resource.diameter.cxdx.events.avp.ServerAssignmentType)
   */
  public void setServerAssignmentType(ServerAssignmentType serverAssignmentType) {
    addAvp(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID, (long)serverAssignmentType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setServerName(java.lang.String)
   */
  public void setServerName(String serverName) {
    addAvp(SERVER_NAME, CXDX_VENDOR_ID, serverName);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setUserDataAlreadyAvailable(net.java.slee.resource.diameter.cxdx.events.avp.UserDataAlreadyAvailable)
   */
  public void setUserDataAlreadyAvailable(UserDataAlreadyAvailable userDataAlreadyAvailable) {
    addAvp(USER_DATA_ALREADY_AVAILABLE, CXDX_VENDOR_ID, (long)userDataAlreadyAvailable.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setWildcardedIMPU(java.lang.String)
   */
  public void setWildcardedIMPU(String wildcardedIMPU) {
    addAvp(WILDCARDED_IMPU, CXDX_VENDOR_ID, wildcardedIMPU);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest#setWildcardedPSI(java.lang.String)
   */
  public void setWildcardedPSI(String wildcardedPSI) {
    addAvp(WILDCARDED_PSI, CXDX_VENDOR_ID, wildcardedPSI);
  }

}
