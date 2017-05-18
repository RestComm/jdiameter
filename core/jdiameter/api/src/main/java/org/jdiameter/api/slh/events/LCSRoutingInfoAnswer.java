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

package org.jdiameter.api.slh.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * @author <a href="mailto:fernando.mendioroz@telestax.com"> Fernando Mendioroz </a>
 *
 */

/*
 * As for 3GPP TS 29.173 v13.0.0, the LCS-Routing-Info-Answer (RIA) command, indicated by the Command-Code field set to 8388622
 * and the "R" bit cleared in the Command Flags field, is sent from HSS to GMLC. The procedure invoked by the GMLC is used for
 * retrieving routing information for LCS (Location Services) for a specified user from the HSS via a LCS-Routing-Info-Request
 * (RIR) command.
 *
 */
public interface LCSRoutingInfoAnswer extends AppAnswerEvent {

  String _SHORT_NAME = "RIA";
  String _LONG_NAME = "LCS-Routing-Info-Answer";

  int code = 8388622;

  boolean isUserNameAVPPresent(); // Mapped IE: IMSI
  String getUserName(); // Mapped IE: IMSI

  boolean isMSISDNAVPPresent();
  byte[] getMSISDN();

  boolean isLMSIAVPPresent();
  byte[] getLMSI();

  boolean isServingNodeAVPPresent();
  // Serving-Node AVP of type grouped, includes:
  // SGSN-Number, SGSN-Name, SGSN-Realm
  // MME-Name, MME-Realm
  // MSC-Number
  // 3GPP-AAA-Server-Name
  // LCS-Capabilities-Sets
  // GMLC-Address

  boolean isSGSNNumberAVPPresent();
  byte[] getSGSNNumber();

  boolean isSGSNNameAVPPresent();
  String getSGSNName();

  boolean isSGSNRealmAVPPresent();
  String getSGSNRealm();

  boolean isMMENameAVPPresent();
  String getMMEName();

  boolean isMMERealmAVPPresent();
  String getMMERealm();

  boolean isMSCNumberAVPPresent();
  byte[] getMSCNumber();

  boolean is3GPPAAAServerNameAVPPresent();
  String get3GPPAAAServerName();

  boolean isLCSCapabilitiesSetsAVPPresent();
  long getLCSCapabilitiesSets();

  boolean isGMLCAddressAVPPresent();
  java.net.InetAddress getGMLCAddress();

  boolean isAdditionalServingNodeAVPPresent();
  // Serving-Node AVP of type grouped, includes:
  // SGSN-Number, SGSN-Name, SGSN-Realm
  // MME-Name, MME-Realm
  // MSC-Number
  // 3GPP-AAA-Server-Name
  // LCS-Capabilities-Sets
  // GMLC-Address

  boolean isAdditionalSGSNNumberAVPPresent();
  byte[] getAdditionalSGSNNumber();

  boolean isAdditionalSGSNNameAVPPresent();
  String getAdditionalSGSNName();

  boolean isAdditionalSGSNRealmAVPPresent();
  String getAdditionalSGSNRealm();

  boolean isAdditionalMMENameAVPPresent();
  String getAdditionalMMEName();

  boolean isAdditionalMMERealmAVPPresent();
  String getAdditionalMMERealm();

  boolean isAdditionalMSCNumberAVPPresent();
  byte[] getAdditionalMSCNumber();

  boolean isAdditional3GPPAAAServerNameAVPPresent();
  String getAdditional3GPPAAAServerName();

  boolean isAdditionalLCSCapabilitiesSetsAVPPresent();
  long getAdditionalLCSCapabilitiesSets();

  boolean isAdditionalGMLCAddressAVPPresent();
  java.net.InetAddress getAdditionalGMLCAddress();

  boolean isPPRAddressAVPPresent();
  java.net.InetAddress getPPRAddress();

  boolean isRIAFlagsAVPPresent();
  long getRIAFLags();

}
