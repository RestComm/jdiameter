/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.cxdx.events.avp;


public class DiameterCxDxAvpCodes {

  private DiameterCxDxAvpCodes() {
  }
  
  public static final long CXDX_VENDOR_ID = 10415L; 
  
  /**
   * <pre>
   * Name......: Visited-Network-Identifier
   * Code......: 600
   * Section...: 6.3.1
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int VISITED_NETWORK_IDENTIFIER = 600;

  /**
   * <pre>
   * Name......: Public-Identity
   * Code......: 601
   * Section...: 6.3.2
   * Type......: UTF8String
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int PUBLIC_IDENTITY = 601;

  /**
   * <pre>
   * Name......: Server-Name
   * Code......: 602
   * Section...: 6.3.3
   * Type......: UTF8String
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SERVER_NAME = 602;

  /**
   * <pre>
   * Name......: Server-Capabilities
   * Code......: 603
   * Section...: 6.3.4
   * Type......: Grouped
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SERVER_CAPABILITIES = 603;

  /**
   * <pre>
   * Name......: Mandatory-Capability
   * Code......: 604
   * Section...: 6.3.5
   * Type......: Unsigned32
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int MANDATORY_CAPABILITY = 604;

  /**
   * <pre>
   * Name......: Optional-Capability
   * Code......: 605
   * Section...: 6.3.6
   * Type......: Unsigned32
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int OPTIONAL_CAPABILITY = 605;

  /**
   * <pre>
   * Name......: User-Data
   * Code......: 606
   * Section...: 6.3.7
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int USER_DATA = 606;

  /**
   * <pre>
   * Name......: SIP-Number-Auth-Items
   * Code......: 607
   * Section...: 6.3.8
   * Type......: Unsigned32
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_NUMBER_AUTH_ITEMS = 607;

  /**
   * <pre>
   * Name......: SIP-Authentication-Scheme
   * Code......: 608
   * Section...: 6.3.9
   * Type......: UTF8String
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_AUTHENTICATION_SCHEME = 608;

  /**
   * <pre>
   * Name......: SIP-Authenticate
   * Code......: 609
   * Section...: 6.3.10
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_AUTHENTICATE = 609;

  /**
   * <pre>
   * Name......: SIP-Authorization
   * Code......: 610
   * Section...: 6.3.11
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_AUTHORIZATION = 610;

  /**
   * <pre>
   * Name......: SIP-Authentication-Context
   * Code......: 611
   * Section...: 6.3.12
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_AUTHENTICATION_CONTEXT = 611;

  /**
   * <pre>
   * Name......: SIP-Auth-Data-Item
   * Code......: 612
   * Section...: 6.3.13
   * Type......: Grouped
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_AUTH_DATA_ITEM = 612;

  /**
   * <pre>
   * Name......: SIP-Item-Number
   * Code......: 613
   * Section...: 6.3.14
   * Type......: Unsigned32
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_ITEM_NUMBER = 613;

  /**
   * <pre>
   * Name......: Server-Assignment-Type
   * Code......: 614
   * Section...: 6.3.15
   * Type......: Enumerated
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SERVER_ASSIGNMENT_TYPE = 614;

  /**
   * <pre>
   * Name......: Deregistration-Reason
   * Code......: 615
   * Section...: 6.3.16
   * Type......: Grouped
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int DEREGISTRATION_REASON = 615;

  /**
   * <pre>
   * Name......: Reason-Code
   * Code......: 616
   * Section...: 6.3.17
   * Type......: Enumerated
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int REASON_CODE = 616;

  /**
   * <pre>
   * Name......: Reason-Info
   * Code......: 617
   * Section...: 6.3.18
   * Type......: UTF8String
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int REASON_INFO = 617;

  /**
   * <pre>
   * Name......: Charging-Information
   * Code......: 618
   * Section...: 6.3.19
   * Type......: Grouped
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int CHARGING_INFORMATION = 618;

  /**
   * <pre>
   * Name......: Primary-Event-Charging-Function-Name
   * Code......: 619
   * Section...: 6.3.20
   * Type......: DiameterURI
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int PRIMARY_EVENT_CHARGING_FUNCTION_NAME = 619;

  /**
   * <pre>
   * Name......: Secondary-Event-Charging-Function-Name
   * Code......: 620
   * Section...: 6.3.21
   * Type......: DiameterURI
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SECONDARY_EVENT_CHARGING_FUNCTION_NAME = 620;

  /**
   * <pre>
   * Name......: Primary-Charging-Collection-Function-Name
   * Code......: 621
   * Section...: 6.3.22
   * Type......: DiameterURI
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME = 621;

  /**
   * <pre>
   * Name......: Secondary-Charging-Collection-Function-Name
   * Code......: 622
   * Section...: 6.3.23
   * Type......: DiameterURI
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME = 622;

  /**
   * <pre>
   * Name......: User-Authorization-Type
   * Code......: 623
   * Section...: 6.3.24
   * Type......: Enumerated
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int USER_AUTHORIZATION_TYPE = 623;

  /**
   * <pre>
   * Name......: User-Data-Already-Available
   * Code......: 624
   * Section...: 6.3.26
   * Type......: Enumerated
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int USER_DATA_ALREADY_AVAILABLE = 624;

  /**
   * <pre>
   * Name......: Confidentiality-Key
   * Code......: 625
   * Section...: 6.3.27
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int CONFIDENTIALITY_KEY = 625;

  /**
   * <pre>
   * Name......: Integrity-Key
   * Code......: 626
   * Section...: 6.3.28
   * Type......: OctetString
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int INTEGRITY_KEY = 626;

  /**
   * <pre>
   * Name......: Supported-Features
   * Code......: 628
   * Section...: 6.3.29
   * Type......: Grouped
   * Must......: V
   * May.......: M
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int SUPPORTED_FEATURES = 628;

  /**
   * <pre>
   * Name......: Feature-List-ID
   * Code......: 629
   * Section...: 6.3.30
   * Type......: Unsigned32
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int FEATURE_LIST_ID = 629;

  /**
   * <pre>
   * Name......: Feature-List
   * Code......: 630
   * Section...: 6.3.31
   * Type......: Unsigned32
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int FEATURE_LIST = 630;

  /**
   * <pre>
   * Name......: Supported-Applications
   * Code......: 631
   * Section...: 6.3.32
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int SUPPORTED_APPLICATIONS = 631;

  /**
   * <pre>
   * Name......: Associated-Identities
   * Code......: 632
   * Section...: 6.3.33
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int ASSOCIATED_IDENTITIES = 632;

  /**
   * <pre>
   * Name......: Originating-Request
   * Code......: 633
   * Section...: 6.3.34
   * Type......: Enumerated
   * Must......: M,V
   * May.......: -
   * Should....: -
   * MustNot...: -
   * May Encr..: No
   * </pre>
   */
  public static final int ORIGINATING_REQUEST = 633;

  /**
   * <pre>
   * Name......: Wildcarded-PSI
   * Code......: 634
   * Section...: 6.3.35
   * Type......: UTF8String
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int WILDCARDED_PSI = 634;

  /**
   * <pre>
   * Name......: SIP-Digest-Authenticate
   * Code......: 635
   * Section...: 6.3.36
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int SIP_DIGEST_AUTHENTICATE = 635;

  /**
   * <pre>
   * Name......: Wildcarded-IMPU
   * Code......: 636
   * Section...: 6.3.43
   * Type......: UTF8String
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int WILDCARDED_IMPU = 636;

  /**
   * <pre>
   * Name......: UAR-Flags
   * Code......: 637
   * Section...: 6.3.44
   * Type......: Unsigned32
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int UAR_FLAGS = 637;

  /**
   * <pre>
   * Name......: Loose-Route-Indication
   * Code......: 638
   * Section...: 6.3.45
   * Type......: Enumerated
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int LOOSE_ROUTE_INDICATION = 638;

  /**
   * <pre>
   * Name......: SCSCF-Restoration-Info
   * Code......: 639
   * Section...: 6.3.46
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int SCSCF_RESTORATION_INFO = 639;

  /**
   * <pre>
   * Name......: Path
   * Code......: 640
   * Section...: 6.3.47
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int PATH = 640;

  /**
   * <pre>
   * Name......: Contact
   * Code......: 641
   * Section...: 6.3.48
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int CONTACT = 641;

  /**
   * <pre>
   * Name......: Subscription-Info
   * Code......: 642
   * Section...: 6.3.49
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int SUBSCRIPTION_INFO = 642;

  /**
   * <pre>
   * Name......: Call-ID-SIP-Header
   * Code......: 643
   * Section...: 6.3.49.1
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int CALL_ID_SIP_HEADER = 643;

  /**
   * <pre>
   * Name......: From-SIP-Header
   * Code......: 644
   * Section...: 6.3.49.2
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int FROM_SIP_HEADER = 644;

  /**
   * <pre>
   * Name......: To-SIP-Header
   * Code......: 645
   * Section...: 6.3.49.3
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int TO_SIP_HEADER = 645;

  /**
   * <pre>
   * Name......: Record-Route
   * Code......: 646
   * Section...: 6.3.49.4
   * Type......: OctetString
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int RECORD_ROUTE = 646;

  /**
   * <pre>
   * Name......: Associated-Registered-Identities
   * Code......: 647
   * Section...: 6.3.50
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int ASSOCIATED_REGISTERED_IDENTITIES = 647;

  /**
   * <pre>
   * Name......: Multiple-Registration-Indication
   * Code......: 648
   * Section...: 6.3.51
   * Type......: Enumerated
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int MULTIPLE_REGISTRATION_INDICATION = 648;

  /**
   * <pre>
   * Name......: Restoration-Info
   * Code......: 649
   * Section...: 6.3.52
   * Type......: Grouped
   * Must......: V
   * May.......: -
   * Should....: -
   * MustNot...: M
   * May Encr..: No
   * </pre>
   */
  public static final int RESTORATION_INFO = 649;

}
