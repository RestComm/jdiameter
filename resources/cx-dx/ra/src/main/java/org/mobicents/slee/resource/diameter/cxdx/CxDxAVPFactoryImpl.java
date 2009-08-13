package org.mobicents.slee.resource.diameter.cxdx;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;
import net.java.slee.resource.diameter.cxdx.events.avp.DeregistrationReason;
import net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode;
import net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfo;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItem;
import net.java.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticate;
import net.java.slee.resource.diameter.cxdx.events.avp.SubscriptionInfo;

import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentitiesImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.ChargingInformationImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.DeregistrationReasonImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.RestorationInfoImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SCSCFRestorationInfoImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPAuthDataItemImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SIPDigestAuthenticateImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.avp.SubscriptionInfoImpl;

/**
 *
 * CxDxAVPFactoryImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxAVPFactoryImpl extends DiameterAvpFactoryImpl implements CxDxAVPFactory {

  /**
   * 
   */
  public CxDxAVPFactoryImpl() {
    super();
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createAssociatedIdentities()
   */
  public AssociatedIdentities createAssociatedIdentities() {
    return (AssociatedIdentities) AvpUtilities.createAvp(ASSOCIATED_IDENTITIES, null, AssociatedIdentitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createAssociatedRegisteredIdentities()
   */
  public AssociatedRegisteredIdentities createAssociatedRegisteredIdentities() {
    return (AssociatedRegisteredIdentities) AvpUtilities.createAvp(ASSOCIATED_REGISTERED_IDENTITIES, null, AssociatedRegisteredIdentitiesImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createChargingInformation()
   */
  public ChargingInformation createChargingInformation() {
    return (ChargingInformation) AvpUtilities.createAvp(CHARGING_INFORMATION, null, ChargingInformationImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createDeregistrationReason()
   */
  public DeregistrationReason createDeregistrationReason() {
    return (DeregistrationReason) AvpUtilities.createAvp(DEREGISTRATION_REASON, null, DeregistrationReasonImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createDeregistrationReason(net.java.slee.resource.diameter.cxdx.events.avp.ReasonCode)
   */
  public DeregistrationReason createDeregistrationReason(ReasonCode reasonCode) {
    // Create the empty AVP
    DeregistrationReason avp = createDeregistrationReason();

    // Set the provided AVP values
    avp.setReasonCode(reasonCode);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createRestorationInfo()
   */
  public RestorationInfo createRestorationInfo() {
    return (RestorationInfo) AvpUtilities.createAvp(RESTORATION_INFO, null, RestorationInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createRestorationInfo(java.lang.String, java.lang.String)
   */
  public RestorationInfo createRestorationInfo(String path, String contact) {
    // Create the empty AVP
    RestorationInfo avp = createRestorationInfo();

    // Set the provided AVP values
    avp.setPath(path);
    avp.setContact(contact);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSCSCFRestorationInfo()
   */
  public SCSCFRestorationInfo createSCSCFRestorationInfo() {
    return (SCSCFRestorationInfo) AvpUtilities.createAvp(SCSCF_RESTORATION_INFO, null, SCSCFRestorationInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSCSCFRestorationInfo(java.lang.String, net.java.slee.resource.diameter.cxdx.events.avp.RestorationInfo[])
   */
  public SCSCFRestorationInfo createSCSCFRestorationInfo(String userName, RestorationInfo[] restorationInfos) {
    // Create the empty AVP
    SCSCFRestorationInfo avp = createSCSCFRestorationInfo();

    // Set the provided AVP values
    avp.setUserName(userName);
    avp.setRestorationInfos(restorationInfos);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSIPAuthDataItem()
   */
  public SIPAuthDataItem createSIPAuthDataItem() {
    return (SIPAuthDataItem) AvpUtilities.createAvp(SIP_AUTH_DATA_ITEM, null, SIPAuthDataItemImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSIPDigestAuthenticate()
   */
  public SIPDigestAuthenticate createSIPDigestAuthenticate() {
    return (SIPDigestAuthenticate) AvpUtilities.createAvp(SIP_DIGEST_AUTHENTICATE, null, SIPDigestAuthenticateImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSIPDigestAuthenticate(java.lang.String, java.lang.String, java.lang.String)
   */
  public SIPDigestAuthenticate createSIPDigestAuthenticate(String digestRealm, String digestQoP, String digestHA1) {
    // Create the empty AVP
    SIPDigestAuthenticate avp = createSIPDigestAuthenticate();

    // Set the provided AVP values
    avp.setDigestRealm(digestRealm);
    avp.setDigestQoP(digestQoP);
    avp.setDigestHA1(digestHA1);

    return avp;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSubscriptionInfo()
   */
  public SubscriptionInfo createSubscriptionInfo() {
    return (SubscriptionInfo) AvpUtilities.createAvp(SUBSCRIPTION_INFO, null, SubscriptionInfoImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxAVPFactory#createSubscriptionInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  public SubscriptionInfo createSubscriptionInfo(String callIDSIPHeader, String fromSIPHeader, String toSIPHeader, String recordRoute, String contact) {
    // Create the empty AVP
    SubscriptionInfo avp = createSubscriptionInfo();

    // Set the provided AVP values
    avp.setCallIDSIPHeader(callIDSIPHeader);
    avp.setFromSIPHeader(fromSIPHeader);
    avp.setToSIPHeader(toSIPHeader);
    avp.setRecordRoute(recordRoute);
    avp.setContact(contact);

    return avp;
  }

}
