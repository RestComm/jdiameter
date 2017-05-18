/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.api.slg.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.172 v13.0.0, the Provide Subscriber Location operation is used by a GMLC to request the location of a
 * target UE from the MME or SGSN at any time, as part of EPC-MT-LR (Evolved Packet Core Mobile Terminated Location Request) or
 * PS-MT-LR (Packet Switched Mobile Terminated Location Request) positioning procedures. The response contains a location
 * estimate of the target UE and other additional information. This operation is also used by a GMLC to request the location of
 * the target UE from the SGSN at any time, as part of deferred MT-LR procedure. The response contains the acknowledgment of the
 * receipt of the request and other additional information.
 *
 * The Provide-Location-Request (PLR) command, indicated by the Command-Code field set to 8388620 and the "R" bit set in the
 * Command Flags field, is sent by the GMLC in order to request subscriber location to the MME or SGSN (Provide Subscriber
 * Location operation request)
 */

public interface ProvideLocationRequest extends AppRequestEvent {

  String _SHORT_NAME = "PLR";
  String _LONG_NAME = "Provide-Location-Request";

  int code = 8388620;

  boolean isSLgLocationTypeAvpPresent();
  int getSLgLocationType();

  boolean isUserNameAvpPresent(); // Mapped IE: IMSI
  String getUserName(); // Mapped IE: IMSI

  boolean isMSISDNAvpPresent();
  byte[] getMSISDN();

  boolean isIMEIAvpPresent();
  String getIMEI();

  boolean isLCSEPSClientNameAvpPresent();
  // LCS-EPS-Client-Name AVP of type grouped, includes:
  // LCS-Name-String, LCS-Format-Indicator
  boolean isLSCNameStringAvpPresent();
  String getLSCNameString();
  boolean isLCSFormatIndicatorAvpPresent();
  int getLCSFormatIndicator();

  boolean isLCSCLientTypeAvpPresent();
  int getLCSClientType();

  boolean isLCSRequestorNamePresent();
  // LCS-Requestor-NAme AVP of type grouped, includes:
  // LCS-Requestor-Id-String, LCS-Format-Indicator
  boolean isLCSRequestorIdStringAvpPresent();
  String getLCSRequestorIdString();
  boolean isReqLCSFormatIndicatorAvpPresent();
  int getReqLCSFormatIndicator();

  boolean isLCSPriorityPresent();
  long getLCSPriority();

  boolean isLCSQoSAvpPresent();
  // LCS-QoS AVP of type grouped, includes:
  // LCS-QoS-Class, Horizontal-Accuracy, Vertical-Accuracy
  // Vertical-Requested, Response-Time
  boolean isLCSQoSClassAvpPresent();
  int getLCSQoSClass();
  boolean isHorizontalAccuracyAvpPresent();
  long getHorizontalAccuracy();
  boolean isVerticalAccuracyAvpPresent();
  long getVerticalAccuracy();
  boolean isVerticalRequestedAvpPresent();
  int getVerticalRequested();
  boolean isResponseTimeAvpPresent();
  int getResponseTime();

  boolean isVelocityRequestedAvpPresent();
  int getVelocityRequested();

  boolean isSupportedGADShapesAvpPresent();
  long getSupportedGADSahpes();

  boolean isLSCServiceTypeIdAvpPresent();
  long getLSCServiceTypeId();

  boolean isLCSCodewordAvpPresent();
  String getLCSCodeword();

  boolean isServiceSelectionAvpPresent();
  String getServiceSelection(); // IE: APN

  boolean isLCSPrivacyCheckSessionAvpPresent(); // IE: Session-Related Privacy Check
  // LCS-Privacy-Check-Session of type grouped, includes
  // LCS-Privacy-Check
  boolean isLCSPrivacyCheckAvpPresent();
  int getLCSPrivacyCheck();

  boolean isLCSPrivacyCheckNonSessionAvpPresent(); // IE: Non-Session-Related Privacy Check
  // LCS-Privacy-Check-Non-Session of type grouped, includes
  // LCS-Privacy-Check
  boolean isLCSPrivacyCheckNSAvpPresent();
  int getLCSPrivacyCheckNS();

  boolean isDeferredLocationTypeAvpPresent();
  long getDeferredLocationType();

  boolean isLCSReferenceNumberAvpPresent();
  byte[] getLCSReferenceNumber();

  boolean isAreaEventInfoAvpPresent();
  // Area-Event-Info AVP of type grouped, includes:
  // Area-Definition, Occurrence-Info, Interval-Time
  boolean isOccurrenceInfoAvpPresent();
  int getOccurrenceInfo();
  boolean isIntervalTimeAvpPresent();
  long getIntervalTime();
  boolean isAreaDefinitionAvpPresent();
  // Area-Definition AVP of type grouped, includes:
  // Area-Type, Area-Identification
  boolean isAreaTypeAvpPresent();
  long getAreaType();
  boolean isAreaIdentificationAvpPresent();
  byte[] getAreaIdentification();

  boolean isGMLCAddressAvpPresent();
  java.net.InetAddress getGMLCAddress();

  boolean isPLRFlagsAvpPresent();
  long getPLRFLags();

  boolean isPeriodicLDRInfoAvpPresent();
  // Periodic-LDR-Info AVP of type grouped, includes:
  // Reporting-Amount, Reporting-Interval
  boolean isReportingAmountAvpPresent();
  long getReportingAmount();
  boolean isReportingIntervalAvpPresent();
  long getReportingInterval();

  boolean isReportingPLMNListAvpPresent();
  // Reporting-PLMN-List AVP of type grouped, includes:
  // PLMN-ID-List, Prioritized-List-Indicator
  boolean isPrioritizedListIndicatorAvpPresent();
  int getPrioritizedListIndicator();
  boolean isPLMNIDListAvpPresent();
  // PLMN-ID-List AVP of type grouped, includes:
  // Visited-PLMN-Id, Periodic-Location-Support-Indicator
  boolean isVisitedPLMNIdAvpPresent();
  byte[] getVisitedPLMNId();
  boolean isPeriodicLocationSupportIndicatorAvpPresent();
  int getPeriodicLocationSupportIndicator();
}
