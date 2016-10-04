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
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
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

  boolean isSLgLocationTypeAVPPresent();
  int getSLgLocationType();

  boolean isUserNameAVPPresent(); // Mapped IE: IMSI
  String getUserName(); // Mapped IE: IMSI

  boolean isMSISDNAVPPresent();
  byte[] getMSISDN();

  boolean isIMEIAVPPresent();
  String getIMEI();

  boolean isLCSEPSClientNameAVPPresent();
  String getLSCNameString();
  int getLCSFormatIndicator();

  boolean isLCSCLientTypeAVPPresent();
  int getLCSClientType();

  boolean isLCSRequestorNamePresent();
  String getLCSRequestorIdString();

  boolean isLCSPriorityPresent();
  long getLCSPriority();

  boolean isLCSQoSAVPPresent();
  int getLCSQoSClass();
  long getHorizontalAccuracy();
  long getVerticalAccuracy();
  int getVerticalRequested();
  int getResponseTime();

  boolean isVelocityRequestedAVPPresent();
  byte[] getVelocityRequested();

  boolean isSupportedGADShapesAVPPresent();
  long getSupportedGADSahpes();

  boolean isLSCServiceTypeIdAVPPresent();
  long getLSCServiceTypeId();

  boolean isLCSCodewordAVPPresent();
  String getLCSCodeword();

  boolean isServiceSelectionAVPPresent();
  String getServiceSelection(); // IE: APN

  boolean isLCSPrivacyCheckSessionAVPPresent();
  int getLCSPrivacyCheckSession(); // IE: Session-Related Privacy Check

  boolean isLCSPrivacyCheckNonSessionAVPPresent();
  int getLCSPrivacyCheckNonSession(); // IE: LCS-Privacy-Check-Non-Session

  boolean isDeferredLocationTypeAVPPresent();
  long getDeferredLocationType();

  boolean isLCSReferenceNumberAVPPresent();
  byte[] getLCSReferenceNumber();

  boolean isAreaEventInfoAVPPresent();
  long getAreaType();
  byte[] getAreaIdentification();

  boolean isGMLCAddressAVPPresent();
  java.net.InetAddress getGMLCAddress();

  boolean isPLRFlagsAVPPresent();
  long getPLRFLags();

  boolean isPeriodicLDRInformationAVPPresent();
  long getReportingAmount();
  long getReportingInterval();

}
