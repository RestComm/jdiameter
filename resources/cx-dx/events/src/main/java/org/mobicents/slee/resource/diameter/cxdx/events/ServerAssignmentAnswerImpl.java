package org.mobicents.slee.resource.diameter.cxdx.events;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.LooseRouteIndication;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.ChargingInformationImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfoImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;

/**
 *
 * ServerAssignmentAnswerImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerAssignmentAnswerImpl extends DiameterMessageImpl implements ServerAssignmentAnswer {

  /**
   * @param message
   */
  public ServerAssignmentAnswerImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getLongName()
   */
  @Override
  public String getLongName() {
    return "Server-Assignment-Answer";
  }

  /* (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl#getShortName()
   */
  @Override
  public String getShortName() {
    return "SAA";
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getAssociatedIdentities()
   */
  public AssociatedIdentities getAssociatedIdentities() {
    return (AssociatedIdentities) getAvpAsCustom(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID, AssociatedIdentitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getAssociatedRegisteredIdentities()
   */
  public AssociatedRegisteredIdentities getAssociatedRegisteredIdentities() {
    return (AssociatedRegisteredIdentities) getAvpAsCustom(ASSOCIATED_REGISTERED_IDENTITIES, CXDX_VENDOR_ID, AssociatedRegisteredIdentitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getAuthSessionState()
   */
  public AuthSessionStateType getAuthSessionState() {
    return (AuthSessionStateType) getAvpAsEnumerated(DiameterAvpCodes.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getChargingInformation()
   */
  public ChargingInformation getChargingInformation() {
    return (ChargingInformation) getAvpAsCustom(CHARGING_INFORMATION, CXDX_VENDOR_ID, ChargingInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getExperimentalResult()
   */
  public ExperimentalResultAvp getExperimentalResult() {
    return (ExperimentalResultAvp) getAvpAsCustom(DiameterAvpCodes.EXPERIMENTAL_RESULT, ExperimentalResultAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getLooseRouteIndication()
   */
  public LooseRouteIndication getLooseRouteIndication() {
    return (LooseRouteIndication) getAvpAsEnumerated(LOOSE_ROUTE_INDICATION, CXDX_VENDOR_ID, LooseRouteIndication.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getSCSCFRestorationInfos()
   */
  public SCSCFRestorationInfo[] getSCSCFRestorationInfos() {
    return (SCSCFRestorationInfo[]) getAvpsAsCustom(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID, SCSCFRestorationInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getServerName()
   */
  public String getServerName() {
    return getAvpAsUTF8String(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getSupportedFeatureses()
   */
  public SupportedFeaturesAvp[] getSupportedFeatureses() {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(SUPPORTED_FEATURES, CXDX_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#getUserData()
   */
  public String getUserData() {
    return getAvpAsOctetString(USER_DATA, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasAssociatedIdentities()
   */
  public boolean hasAssociatedIdentities() {
    return hasAvp(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasAssociatedRegisteredIdentities()
   */
  public boolean hasAssociatedRegisteredIdentities() {
    return hasAvp(ASSOCIATED_REGISTERED_IDENTITIES, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasAuthSessionState()
   */
  public boolean hasAuthSessionState() {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasChargingInformation()
   */
  public boolean hasChargingInformation() {
    return hasAvp(CHARGING_INFORMATION, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasExperimentalResult()
   */
  public boolean hasExperimentalResult() {
    return hasAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasLooseRouteIndication()
   */
  public boolean hasLooseRouteIndication() {
    return hasAvp(LOOSE_ROUTE_INDICATION, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasServerName()
   */
  public boolean hasServerName() {
    return hasAvp(SERVER_NAME, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#hasUserData()
   */
  public boolean hasUserData() {
    return hasAvp(USER_DATA, CXDX_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setAssociatedIdentities(net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities)
   */
  public void setAssociatedIdentities(AssociatedIdentities associatedIdentities) {
    addAvp(ASSOCIATED_IDENTITIES, CXDX_VENDOR_ID, associatedIdentities.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setAssociatedRegisteredIdentities(net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities)
   */
  public void setAssociatedRegisteredIdentities(AssociatedRegisteredIdentities associatedRegisteredIdentities) {
    addAvp(ASSOCIATED_REGISTERED_IDENTITIES, CXDX_VENDOR_ID, associatedRegisteredIdentities.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setAuthSessionState(net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType)
   */
  public void setAuthSessionState(AuthSessionStateType authSessionState) {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setChargingInformation(net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation)
   */
  public void setChargingInformation(ChargingInformation chargingInformation) {
    addAvp(CHARGING_INFORMATION, CXDX_VENDOR_ID, chargingInformation.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setExperimentalResult(net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp)
   */
  public void setExperimentalResult(ExperimentalResultAvp experimentalResult) {
    addAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT, experimentalResult.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setLooseRouteIndication(net.java.slee.resource.diameter.cxdx.events.avp.LooseRouteIndication)
   */
  public void setLooseRouteIndication(LooseRouteIndication looseRouteIndication) {
    addAvp(LOOSE_ROUTE_INDICATION, CXDX_VENDOR_ID, (long)looseRouteIndication.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setSCSCFRestorationInfo(net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo)
   */
  public void setSCSCFRestorationInfo(SCSCFRestorationInfo scscfRestorationInfo) {
    addAvp(SCSCF_RESTORATION_INFO, CXDX_VENDOR_ID, scscfRestorationInfo.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setSCSCFRestorationInfos(net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo[])
   */
  public void setSCSCFRestorationInfos(SCSCFRestorationInfo[] scscfRestorationInfos) {
    for(SCSCFRestorationInfo scscfRestorationInfo : scscfRestorationInfos) {
      setSCSCFRestorationInfo(scscfRestorationInfo);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setServerName(java.lang.String)
   */
  public void setServerName(String serverName) {
    addAvp(SERVER_NAME, CXDX_VENDOR_ID, serverName);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setSupportedFeatures(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp)
   */
  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures) {
    addAvp(SUPPORTED_FEATURES, CXDX_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setSupportedFeatureses(net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp[])
   */
  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses) {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer#setUserData(java.lang.String)
   */
  public void setUserData(String userData) {
    addAvp(USER_DATA, CXDX_VENDOR_ID, userData);
  }

}
