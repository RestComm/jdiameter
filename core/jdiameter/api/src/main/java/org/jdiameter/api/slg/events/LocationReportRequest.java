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
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.172 v13.0.0, Subscriber Location Report operation is used by an MME or SGSN to provide the location of a
 * target UE to a GMLC, when a request for location has been implicitly issued or when a Delayed Location Reporting is triggered
 * after receipt of a request for location for a UE transiently not reachable.
 *
 * The Location-Report-Request (LRR) command, indicated by the Command-Code field set to 8388621 and the "R" bit set in the
 * Command Flags field, is sent by the MME or SGSN in order to provide subscriber location data to the GMLC (Subscriber Location
 * Report operation request)
 */

public interface LocationReportRequest extends AppRequestEvent {

  String _SHORT_NAME = "LRR";
  String _LONG_NAME = "Location-Report-Request";

  int code = 8388621;

  boolean isLocationEventAvpPresent();
  int getLocationEvent();

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

  boolean isLocationEstimateAvpPresent();
  byte[] getLocationEstimate();

  boolean isAccuracyFulfilmentIndicatorAvpPresent();
  int getAccuracyFulfilmentIndicator();

  boolean isAgeOfLocationEstimateAvpPresent();
  long getAgeOfLocationEstimate();

  boolean isVelocityEstimateAvpPresent();
  byte[] getVelocityEstimate();

  boolean isEUTRANPositioningDataAvpPresent();
  byte[] getEUTRANPositioningData();

  boolean isECGIAvpPresent();
  byte[] getECGI();

  boolean isGERANPositioningInfoAvpPresent();
  // GERAN-Positioning-Info AVP of type grouped, includes:
  // GERAN-Positioning-Data, GERAN-GANSS-Positioning-Data
  boolean isGERANPositioningDataAvpPresent();
  byte[] getGERANPositioningData();
  boolean isGERANGANSSPositioningDataAvpPresent();
  byte[] getGERANGANSSPositioningData();

  boolean isCellGlobalIdentityAvpPresent();
  byte[] getCellGlobalIdentity();

  boolean isUTRANPositioningInfoAvpPresent();
  // UTRAN-Positioning-Info AVP of type grouped, includes:
  // UTRAN-Positioning-Data, UTRAN-GANSS-Positioning-Data
  boolean isUTRANPositioningDataAvpPresent();
  byte[] getUTRANPositioningData();
  boolean isUTRANGANSSPositioningDataAvpPresent();
  byte[] getUTRANGANSSPositioningData();

  boolean isServiceAreaIdentityAvpPresent();
  byte[] getServiceAreaIdentity();

  boolean isLCSServiceTypeIDAvpPresent();
  long getLCSServiceTypeID();

  boolean isPseudonymIndicatorAvpPresent();
  int getPseudonymIndicator();

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

  boolean isServingNodeAvpPresent();
  // [ Serving-Node ] IE: Target Serving Node Identity
  // Serving-Node AVP of type grouped, includes:
  // SGSN-Number, SGSN-Name, SGSN-Realm.
  // MME-Name, MME-Realm
  // MSC-Number
  // 3GPP-AAA-Server-Name, LCS-Capabilities-Sets, GMLC-Address
  boolean isSGSNNumberAvpPresent();
  byte[] getSGSNNumber();
  boolean isSGSNNameAvpPresent();
  String getSGSNName();
  boolean isSGSNRealmAvpPresent();
  String getSGSNRealm();
  boolean isMMENameAvpPresent();
  String getMMEName();
  boolean isMMERealmAvpPresent();
  String getMMERealm();
  boolean isMSCNumberAvpPresent();
  byte[] getMSCNumber();
  boolean is3GPPAAAServerNameAvpPresent();
  String get3GPPAAAServerName();
  boolean isLCSCapabilitiesSetsAvpPresent();
  long getLCSCapabilitiesSets();
  boolean isGMLCAddressAvpPresent();
  java.net.InetAddress getGMLCAddress();

  boolean isLRRFlagsAvpPresent();
  long getLRRFLags();

  boolean isLCSReferenceNumberAvpPresent();
  byte[] getLCSReferenceNumber();

  boolean isDeferredMTLRDataAvpPresent();
  // Deferred-MT-LR-Data AVP of type grouped, includes:
  // Deferred-Location-Type, Termination-Cause.
  boolean isDeferredLocationTypeAvpPresent();
  long getDeferredLocationType();
  boolean isTerminationCauseAvpPresent();
  long getTerminationCause();

  boolean isHGMLCAddressAvpPresent(); // IE: H-GMLC Address mapped to GMLC-Address AVP
  java.net.InetAddress getHGMLCAddress();

  boolean isPeriodicLDRInfoAvpPresent();
  // Periodic-LDR-Info AVP of type grouped, includes:
  // Reporting-Amount, Reporting-Interval
  boolean isReportingAmountAvpPresent();
  long getReportingAmount();
  boolean isReportingIntervalAvpPresent();
  long getReportingInterval();

  boolean isESMLCCellInfoAvpPresent();
  long getCellPortionId();

  boolean is1xRTTRCIDAvpPresent();
  byte[] get1xRTTRCID();

  boolean isCivicAddressAvpPresent();
  String getCivicAddress();

  boolean isBarometricPressureAvpPresent();
  long getBarometricPressure();

}