/**
 * Start time:19:45:16 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package net.java.slee.resource.diameter.cca.events.avp;

/**
 * Start time:19:45:16 2008-12-08<br>
 * Project: mobicents-diameter-parent<br>
 * Contains definition of avp codes and values used by Diameter CCA - rfc 4006
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public final class CreditControlAVPCodes {

	private CreditControlAVPCodes() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <b>8.1. CC-Correlation-Id AVP</b><br>
	 * 
	 * 
	 * The CC-Correlation-Id AVP (AVP Code 411) is of type OctetString and
	 * contains information to correlate credit-control requests generated for
	 * different components of the service; e.g., transport and service level.
	 * The one who allocates the Service-Context-Id (i.e., unique identifier of
	 * a service specific document) is also responsible for defining the content
	 * and encoding of the CC-Correlation-Id AVP.
	 */
	public static final int CC_Correlation_Id = 411;

	/**
	 * <b>8.2. CC-Request-Number AVP</b></br>
	 * 
	 * 
	 * The CC-Request-Number AVP (AVP Code 415) is of type Unsigned32 and
	 * identifies this request within one session. As Session-Id AVPs are
	 * globally unique, the combination of Session-Id and CC-Request-Number AVPs
	 * is also globally unique and can be used in matching credit- control
	 * messages with confirmations. An easy way to produce unique numbers is to
	 * set the value to 0 for a credit-control request of type INITIAL_REQUEST
	 * and EVENT_REQUEST and to set the value to 1 for the first UPDATE_REQUEST,
	 * to 2 for the second, and so on until the value for TERMINATION_REQUEST is
	 * one more than for the last UPDATE_REQUEST.
	 */
	public static final int CC_Request_Number = 415;

	/**
	 * <pre>
	 * 8.3. CC-Request-Type AVP
	 * 
	 * 
	 * 	   The CC-Request-Type AVP (AVP Code 416) is of type Enumerated and
	 * 	   contains the reason for sending the credit-control request message.
	 * 	   It MUST be present in all Credit-Control-Request messages.  The
	 * 	   following values are defined for the CC-Request-Type AVP:
	 * 
	 * 	   INITIAL_REQUEST                 1
	 * 	      An Initial request is used to initiate a credit-control session,
	 * 	      and contains credit control information that is relevant to the
	 * 	      initiation.
	 * 
	 * 	   UPDATE_REQUEST                  2
	 * 	      An Update request contains credit-control information for an
	 * 	      existing credit-control session.  Update credit-control requests
	 * 	      SHOULD be sent every time a credit-control re-authorization is
	 * 	      needed at the expiry of the allocated quota or validity time.
	 * 	      Further, additional service-specific events MAY trigger a
	 * 	      spontaneous Update request.
	 * 
	 * 	   TERMINATION_REQUEST             3
	 * 	      A Termination request is sent to terminate a credit-control
	 * 	      session and contains credit-control information relevant to the
	 * 	      existing session.
	 * 
	 * 	   EVENT_REQUEST                   4
	 * 	      An Event request is used when there is no need to maintain any
	 * 	      credit-control session state in the credit-control server.  This
	 * 	      request contains all information relevant to the service, and is
	 * 	      the only request of the service.  The reason for the Event request
	 * 	      is further detailed in the Requested-Action AVP.  The Requested-
	 * 	      Action AVP MUST be included in the Credit-Control-Request message
	 * 	      when CC-Request-Type is set to EVENT_REQUEST.
	 * </pre>
	 */
	public static final int CC_Request_Type = 416;
	/**
	 * <pre>
	 * 8.4. CC-Session-Failover AVP
	 * 
	 * 
	 * 	   The CC-Session-Failover AVP (AVP Code 418) is type of Enumerated and
	 * 	   contains information as to whether moving the credit-control message
	 * 	   stream to a backup server during an ongoing credit-control session is
	 * 	   supported.  In communication failures, the credit-control message
	 * 	   streams can be moved to an alternative destination if the credit-
	 * 	   control server supports failover to an alternative server.  The
	 * 	   secondary credit-control server name, if received from the home
	 * 	   Diameter AAA server, can be used as an address of the backup server.
	 * 	   An implementation is not required to support moving a credit-control
	 * 	   message stream to an alternative server, as this also requires moving
	 * 	   information related to the credit-control session to backup server.
	 * 
	 * 	   The following values are defined for the CC-Session-Failover AVP:
	 * 
	 * 	   FAILOVER_NOT_SUPPORTED          0
	 * 	      When the CC-Session-Failover AVP is set to FAILOVER_NOT_SUPPORTED,
	 * 	      the credit-control message stream MUST NOT to be moved to an
	 * 	      alternative destination in the case of communication failure.
	 * 
	 * 	      This is the default behavior if the AVP isn't included in the
	 * 	      reply from the authorization or credit-control server.
	 * 
	 * 	   FAILOVER_SUPPORTED              1
	 * 	      When the CC-Session-Failover AVP is set to FAILOVER_SUPPORTED, the
	 * 	      credit-control message stream SHOULD be moved to an alternative
	 * 	      destination in the case of communication failure.  Moving the
	 * 	      credit-control message stream to a backup server MAY require that
	 * 	      information related to the credit-control session should also be
	 * 	      forwarded to alternative server.
	 * </pre>
	 */
	public static final int CC_Session_Failover = 418;
	/**
	 *<pre>
	 * 8.5. CC-Sub-Session-Id AVP
	 * 
	 * 
	 * 	   The CC-Sub-Session-Id AVP (AVP Code 419) is of type Unsigned64 and
	 * 	   contains the credit-control sub-session identifier.  The combination
	 * 	   of the Session-Id and this AVP MUST be unique per sub-session, and
	 * 	   the value of this AVP MUST be monotonically increased by one for all
	 * 	   new sub-sessions.  The absence of this AVP implies that no sub-
	 * 	   sessions are in use.
	 * </pre>
	 */
	public static final int CC_Sub_Session_Id = 419;

	/**
	 * <pre>
	 * 8.6. Check-Balance-Result AVP
	 * 
	 * 
	 * 	   The Check Balance Result AVP (AVP Code 422) is of type Enumerated and
	 * 	   contains the result of the balance check.  This AVP is applicable
	 * 	   only when the Requested-Action AVP indicates CHECK_BALANCE in the
	 * 	   Credit-Control-Request command.
	 * 
	 * 	   The following values are defined for the Check-Balance-Result AVP.
	 * 
	 * 	   ENOUGH_CREDIT                   0
	 * 	      There is enough credit in the account to cover the requested
	 * 	      service.
	 * 
	 * 	   NO_CREDIT                       1
	 * 	      There isn't enough credit in the account to cover the requested
	 * 	      service.
	 * </pre>
	 */
	public static final int Check_Balance_Result = 422;
	/**
	 * <pre>
	 * 8.7. Cost-Information AVP
	 * 
	 * 
	 * 	   The Cost-Information AVP (AVP Code 423) is of type Grouped, and it is
	 * 	   used to return the cost information of a service, which the credit-
	 * 	   control client can transfer transparently to the end user.  The
	 * 	   included Unit-Value AVP contains the cost estimate (always type of
	 * 	   money) of the service, in the case of price enquiry, or the
	 * 	   accumulated cost estimation, in the case of credit-control session.
	 * 
	 * 	   The Currency-Code specifies in which currency the cost was given.
	 * 	   The Cost-Unit specifies the unit when the service cost is a cost per
	 * 	   unit (e.g., cost for the service is $1 per minute).
	 * 
	 * 	   When the Requested-Action AVP with value PRICE_ENQUIRY is included in
	 * 	   the Credit-Control-Request command, the Cost-Information AVP sent in
	 * 	   the succeeding Credit-Control-Answer command contains the cost
	 * 	   estimation of the requested service, without any reservation being
	 * 	   made.
	 * 
	 * 	   The Cost-Information AVP included in the Credit-Control-Answer
	 * 	   command with the CC-Request-Type set to UPDATE_REQUEST contains the
	 * 	   accumulated cost estimation for the session, without taking any
	 * 	   credit reservation into account.
	 * 
	 * 	   The Cost-Information AVP included in the Credit-Control-Answer
	 * 	   command with the CC-Request-Type set to EVENT_REQUEST or
	 * 	   TERMINATION_REQUEST contains the estimated total cost for the
	 * 	   requested service.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of
	 * 	   RFC 3588 [DIAMBASE]):
	 * 
	 * 	                Cost-Information ::= &lt; AVP Header: 423 &gt;
	 * 	                                     { Unit-Value }
	 * 	                                     { Currency-Code }
	 * 	                                     [ Cost-Unit ]
	 * </pre>
	 */
	public static final int Cost_Information = 423;

	/**
	 *<pre>
	 * 8.8. Unit-Value AVP
	 * 
	 * 
	 * 	   Unit-Value AVP is of type Grouped (AVP Code 445) and specifies the
	 * 	   units as decimal value.  The Unit-Value is a value with an exponent;
	 * 	   i.e., Unit-Value = Value-Digits AVP * 10&circ;Exponent.  This
	 * 	   representation avoids unwanted rounding off.  For example, the value
	 * 	   of 2,3 is represented as Value-Digits = 23 and Exponent = -1.  The
	 * 	   absence of the exponent part MUST be interpreted as an exponent equal
	 * 	   to zero.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of
	 * 	   RFC 3588 [DIAMBASE]):
	 * 
	 * 	                    Unit-Value ::= &lt; AVP Header: 445 &gt;
	 * 	                                   { Value-Digits }
	 * 	                                   [ Exponent ]
	 * </pre>
	 */
	public static final int Unit_Value = 445;
	/**
	 * <pre>
	 * 8.9. Exponent AVP
	 * 
	 * 
	 * 	   Exponent AVP is of type Integer32 (AVP Code 429) and contains the
	 * 	   exponent value to be applied for the Value-Digit AVP within the
	 * 	   Unit-Value AVP.
	 * </pre>
	 */
	public static final int Exponent = 429;
	/**
	 * <pre>
	 * 8.10. Value-Digits AVP
	 * 
	 * 
	 * 	   The Value-Digits AVP is of type Integer64 (AVP Code 447) and contains
	 * 	   the significant digits of the number.  If decimal values are needed
	 * 	   to present the units, the scaling MUST be indicated with the related
	 * 	   Exponent AVP.  For example, for the monetary amount $ 0.05 the value
	 * 	   of Value-Digits AVP MUST be set to 5, and the scaling MUST be
	 * 	   indicated with the Exponent AVP set to -2.
	 * </pre>
	 */
	public static final int Value_Digits = 447;
	/**
	 * <pre>
	 * 8.11. Currency-Code AVP
	 * 
	 * 
	 * 	   The Currency-Code AVP (AVP Code 425) is of type Unsigned32 and
	 * 	   contains a currency code that specifies in which currency the values
	 * 	   of AVPs containing monetary units were given.  It is specified by
	 * 	   using the numeric values defined in the ISO 4217 standard [ISO4217].
	 * </pre>
	 */
	public static final int Currency_Code = 425;
	/**
	 * <pre>
	 * 8.12. Cost-Unit AVP
	 * 
	 * 
	 * 	   The Cost-Unit AVP (AVP Code 424) is of type UTF8String, and it is
	 * 	   used to display a human readable string to the end user.  It
	 * 	   specifies the applicable unit to the Cost-Information when the
	 * 	   service cost is a cost per unit (e.g., cost of the service is $1 per
	 * 	   minute).  The Cost-Unit can be minutes, hours, days, kilobytes,
	 * 	   megabytes, etc.
	 * </pre>
	 */
	public static final int Cost_Unit = 424;
	/**
	 * <pre>
	 * 8.13. Credit-Control AVP
	 * 
	 * 
	 * 	   The Credit-Control AVP (AVP Code 426) is of type Enumerated and MUST
	 * 	   be included in AA requests when the service element has credit-
	 * 	   control capabilities.
	 * 
	 * 	   CREDIT_AUTHORIZATION            0
	 * 	      If the home Diameter AAA server determines that the user has
	 * 	      prepaid subscription, this value indicates that the credit-control
	 * 	      server MUST be contacted to perform the first interrogation.  The
	 * 	      value of the Credit-Control AVP MUST always be set to 0 in an AA
	 * 	      request sent to perform the first interrogation and to initiate a
	 * 	      new credit-control session.
	 * 
	 * 	   RE_AUTHORIZATION                1
	 * 	      This value indicates to the Diameter AAA server that a credit-
	 * 	      control session is ongoing for the subscriber and that the
	 * 	      credit-control server MUST not be contacted.  The Credit-Control
	 * 	      AVP set to the value of 1 is to be used only when the first
	 * 	      interrogation has been successfully performed and the credit-
	 * 	      control session is ongoing (i.e., re-authorization triggered by
	 * 	      Authorization-Lifetime).  This value MUST NOT be used in an AA
	 * 	      request sent to perform the first interrogation.
	 * </pre>
	 */
	public static final int Credit_Control = 426;
	/**
	 * <pre>
	 * 8.14. Credit-Control-Failure-Handling AVP
	 * 
	 * 
	 * 	   The Credit-Control-Failure-Handling AVP (AVP Code 427) is of type
	 * 	   Enumerated.  The credit-control client uses information in this AVP
	 * 	   to decide what to do if sending credit-control messages to the
	 * 	   credit-control server has been, for instance, temporarily prevented
	 * 	   due to a network problem.  Depending on the service logic, the
	 * 	   credit-control server can order the client to terminate the service
	 * 	   immediately when there is a reason to believe that the service cannot
	 * 	   be charged, or to try failover to an alternative server, if possible.
	 * 	   Then the server could either terminate or grant the service, should
	 * 	   the alternative connection also fail.
	 * 
	 * 	   TERMINATE                       0
	 * 	      When the Credit-Control-Failure-Handling AVP is set to TERMINATE,
	 * 	      the service MUST only be granted for as long as there is a
	 * 	      connection to the credit-control server.  If the credit-control
	 * 	      client does not receive any Credit-Control-Answer message within
	 * 	      the Tx timer (as defined in section 13), the credit-control
	 * 	      request is regarded as failed, and the end user's service session
	 * 	      is terminated.
	 * 
	 * 	      This is the default behavior if the AVP isn't included in the
	 * 	      reply from the authorization or credit-control server.
	 * 
	 * 	   CONTINUE                       1
	 * 	      When the Credit-Control-Failure-Handling AVP is set to CONTINUE,
	 * 	      the credit-control client SHOULD re-send the request to an
	 * 	      alternative server in the case of transport or temporary failures,
	 * 	      provided that a failover procedure is supported in the credit-
	 * 	      control server and the credit-control client, and that an
	 * 	      alternative server is available.  Otherwise, the service SHOULD be
	 * 	      granted, even if credit-control messages can't be delivered.
	 * 
	 * 	   RETRY_AND_TERMINATE            2
	 * 	      When the Credit-Control-Failure-Handling AVP is set to
	 * 	      RETRY_AND_TERMINATE, the credit-control client SHOULD re-send the
	 * 	      request to an alternative server in the case of transport or
	 * 	      temporary failures, provided that a failover procedure is
	 * 	      supported in the credit-control server and the credit-control
	 * 	      client, and that an alternative server is available.  Otherwise,
	 * 	      the service SHOULD not be granted when the credit-control messages
	 * 	      can't be delivered.
	 * </pre>
	 */
	public static final int Credit_Control_Failure_Handling = 427;
	/**
	 * <pre>
	 * 8.15. Direct-Debiting-Failure-Handling AVP
	 * 
	 * 
	 * 	   The Direct-Debiting-Failure-Handling AVP (AVP Code 428) is of type
	 * 	   Enumerated.  The credit-control client uses information in this AVP
	 * 	   to decide what to do if sending credit-control messages (Requested-
	 * 	   Action AVP set to DIRECT_DEBITING) to the credit-control server has
	 * 	   been, for instance, temporarily prevented due to a network problem.
	 * 
	 * 	   TERMINATE_OR_BUFFER             0
	 * 	      When the Direct-Debiting-Failure-Handling AVP is set to
	 * 	      TERMINATE_OR_BUFFER, the service MUST be granted for as long as
	 * 	      there is a connection to the credit-control server.  If the
	 * 	      credit-control client does not receive any Credit-Control-Answer
	 * 	      message within the Tx timer (as defined in section 13) the
	 * 	      credit-control request is regarded as failed.  The client SHOULD
	 * 	      terminate the service if it can determine from the failed answer
	 * 	      that units have not been debited.  Otherwise the credit-control
	 * 	      client SHOULD grant the service, store the request in application
	 * 	      level non-volatile storage, and try to re-send the request.  These
	 * 	      requests MUST be marked as possible duplicates by setting the T-
	 * 	      flag in the command header as described in [DIAMBASE] section 3.
	 * 
	 * 	      This is the default behavior if the AVP isn't included in the
	 * 	      reply from the authorization server.
	 * 
	 * 	   CONTINUE                                              1
	 * 	      When the Direct-Debiting-Failure-Handling AVP is set to CONTINUE,
	 * 	      the service SHOULD be granted, even if credit-control messages
	 * 	      can't be delivered, and the request should be deleted.
	 * </pre>
	 */
	public static final int Direct_Debiting_Failure_Handling = 428;
	/**
	 * <pre>
	 * 8.16. Multiple-Services-Credit-Control AVP
	 * 
	 * 
	 * 	   Multiple-Services-Credit-Control AVP (AVP Code 456) is of type
	 * 	   Grouped and contains the AVPs related to the independent credit-
	 * 	   control of multiple services feature.  Note that each instance of
	 * 	   this AVP carries units related to one or more services or related to
	 * 	   a single rating group.
	 * 
	 * 	   The Service-Identifier and the Rating-Group AVPs are used to
	 * 	   associate the granted units to a given service or rating group.  If
	 * 	   both the Service-Identifier and the Rating-Group AVPs are included,
	 * 	   the target of the service units is always the service(s) indicated by
	 * 	   the value of the Service-Identifier AVP(s).  If only the Rating-
	 * 	   Group-Id AVP is present, the Multiple-Services-Credit-Control AVP
	 * 	   relates to all the services that belong to the specified rating
	 * 	   group.
	 * 
	 * 	   The G-S-U-Pool-Reference AVP allows the server to specify a G-S-U-
	 * 	   Pool-Identifier identifying a credit pool within which the units of
	 * 	   the specified type are considered pooled.  If a G-S-U-Pool-Reference
	 * 	   AVP is present, then actual service units of the specified type MUST
	 * 	   also be present.  For example, if the G-S-U-Pool-Reference AVP
	 * 	   specifies Unit-Type TIME, then the CC-Time AVP MUST be present.
	 * 
	 * 	   The Requested-Service-Unit AVP MAY contain the amount of requested
	 * 	   service units or the requested monetary value.  It MUST be present in
	 * 	   the initial interrogation and within the intermediate interrogations
	 * 	   in which new quota is requested.  If the credit-control client does
	 * 	   not include the Requested-Service-Unit AVP in a request command,
	 * 	   because for instance, it has determined that the end-user terminated
	 * 	   the service, the server MUST debit the used amount from the user's
	 * 	   account but MUST NOT return a new quota in the corresponding answer.
	 * 	   The Validity-Time, Result-Code, and Final-Unit-Indication AVPs MAY be
	 * 	   present in an answer command as defined in sections 5.1.2 and 5.6 for
	 * 	   the graceful service termination.
	 * 
	 * 	   When both the Tariff-Time-Change and Tariff-Change-Usage AVPs are
	 * 	   present, the server MUST include two separate instances of the
	 * 	   Multiple-Services-Credit-Control AVP with the Granted-Service-Unit
	 * 	   AVP associated to the same service-identifier and/or rating-group.
	 * 	   Where the two quotas are associated to the same pool or to different
	 * 	   pools, the credit pooling mechanism defined in section 5.1.2 applies.
	 * 	   The Tariff-Change-Usage AVP MUST NOT be included in request commands
	 * 	   to report used units before, and after tariff time change the Used-
	 * 	   Service-Unit AVP MUST be used.
	 * 
	 * 	   A server not implementing the independent credit-control of multiple
	 * 	   services functionality MUST treat the Multiple-Services-Credit-
	 * 	   Control AVP as an invalid AVP.
	 * 
	 * 	   The Multiple-Services-Control AVP is defined as follows (per the
	 * 	   grouped-avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      Multiple-Services-Credit-Control ::= &lt; AVP Header: 456 &gt;
	 * 	                                           [ Granted-Service-Unit ]
	 * 	                                           [ Requested-Service-Unit ]
	 *  Used-Service-Unit ]
	 * 	                                           [ Tariff-Change-Usage ]
	 *  Service-Identifier ]
	 * 	                                           [ Rating-Group ]
	 *  G-S-U-Pool-Reference ]
	 * 	                                           [ Validity-Time ]
	 * 	                                           [ Result-Code ]
	 * 	                                           [ Final-Unit-Indication ]
	 *  AVP ]
	 * </pre>
	 */
	public static final int Multiple_Services_Credit_Control = 456;

	/**
	 * <pre>
	 * 8.17. Granted-Service-Unit AVP
	 * 
	 * 
	 * 	   Granted-Service-Unit AVP (AVP Code 431) is of type Grouped and
	 * 	   contains the amount of units that the Diameter credit-control client
	 * 	   can provide to the end user until the service must be released or the
	 * 	   new Credit-Control-Request must be sent.  A client is not required to
	 * 	   implement all the unit types, and it must treat unknown or
	 * 	   unsupported unit types in the answer message as an incorrect CCA
	 * 	   answer.  In this case, the client MUST terminate the credit-control
	 * 	   session and indicate in the Termination-Cause AVP reason
	 * 	   DIAMETER_BAD_ANSWER.
	 * 
	 * 
	 * 	   The Granted-Service-Unit AVP is defined as follows (per the grouped-
	 * 	   avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      Granted-Service-Unit ::= &lt; AVP Header: 431 &gt;
	 * 	                                 [ Tariff-Time-Change ]
	 * 	                                 [ CC-Time ]
	 * 	                                 [ CC-Money ]
	 * 	                                 [ CC-Total-Octets ]
	 * 	                                 [ CC-Input-Octets ]
	 * 	                                 [ CC-Output-Octets ]
	 * 	                                 [ CC-Service-Specific-Units ]
	 *  AVP ]
	 * </pre>
	 */
	public static final int Granted_Service_Unit = 431;
	/**
	 * <pre>
	 * 8.18. Requested-Service-Unit AVP
	 * 
	 * 
	 * 	   The Requested-Service-Unit AVP (AVP Code 437) is of type Grouped and
	 * 	   contains the amount of requested units specified by the Diameter
	 * 	   credit-control client.  A server is not required to implement all the
	 * 	   unit types, and it must treat unknown or unsupported unit types as
	 * 	   invalid AVPs.
	 * 
	 * 	   The Requested-Service-Unit AVP is defined as follows (per the
	 * 	   grouped-avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      Requested-Service-Unit ::= &lt; AVP Header: 437 &gt;
	 * 	                                 [ CC-Time ]
	 * 	                                 [ CC-Money ]
	 * 	                                 [ CC-Total-Octets ]
	 * 	                                 [ CC-Input-Octets ]
	 * 	                                 [ CC-Output-Octets ]
	 * 	                                 [ CC-Service-Specific-Units ]
	 *  AVP ]
	 * </pre>
	 */
	public static final int Requested_Service_Unit = 437;

	/**
	 * <pre>
	 * 8.19. Used-Service-Unit AVP
	 * 
	 * 
	 * 	   The Used-Service-Unit AVP is of type Grouped (AVP Code 446) and
	 * 	   contains the amount of used units measured from the point when the
	 * 	   service became active or, if interim interrogations are used during
	 * 	   the session, from the point when the previous measurement ended.
	 * 
	 * 	   The Used-Service-Unit AVP is defined as follows (per the grouped-
	 * 	   avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      Used-Service-Unit ::= &lt; AVP Header: 446 &gt;
	 * 	                            [ Tariff-Change-Usage ]
	 * 	                            [ CC-Time ]
	 * 	                            [ CC-Money ]
	 * 	                            [ CC-Total-Octets ]
	 * 	                            [ CC-Input-Octets ]
	 * 	                            [ CC-Output-Octets ]
	 * 	                            [ CC-Service-Specific-Units ]
	 *  AVP ]
	 * </pre>
	 */
	public static final int Used_Service_Unit = 446;
	/**
	 * <pre>
	 * 8.20. Tariff-Time-Change AVP
	 * 
	 * 
	 * 	   The Tariff-Time-Change AVP (AVP Code 451) is of type Time.  It is
	 * 	   sent from the server to the client and includes the time in seconds
	 * 	   since January 1, 1900, 00:00 UTC, when the tariff of the service will
	 * 	   be changed.
	 * 
	 * 	   The tariff change mechanism is optional for the client and server,
	 * 	   and it is not used for time-based services defined in section 5.  If
	 * 	   a client does not support the tariff time change mechanism, it MUST
	 * 	   treat Tariff-Time-Change AVP in the answer message as an incorrect
	 * 	   CCA answer.  In this case, the client terminates the credit-control
	 * 	   session and indicates in the Termination-Cause AVP reason
	 * 	   DIAMETER_BAD_ANSWER.
	 * 
	 * 	   Omission of this AVP means that no tariff change is to be reported.
	 * </pre>
	 */
	public static final int Tariff_Time_Change = 451;

	/**
	 * <pre>
	 * 8.21. CC-Time AVP
	 * 
	 * 
	 * 	   The CC-Time AVP (AVP Code 420) is of type Unsigned32 and indicates
	 * 	   the length of the requested, granted, or used time in seconds.
	 * </pre>
	 */
	public static final int CC_Time = 420;
	/**
	 * <pre>
	 * 8.22. CC-Money AVP
	 * 
	 * 
	 * 	   The CC-Money AVP (AVP Code 413) is of type Grouped and specifies the
	 * 	   monetary amount in the given currency.  The Currency-Code AVP SHOULD
	 * 	   be included.  It is defined as follows (per the grouped-avp-def of
	 * 	   RFC 3588 [DIAMBASE]):
	 * 
	 * 	      CC-Money ::= &lt; AVP Header: 413 &gt;
	 * 	                   { Unit-Value }
	 * 	                   [ Currency-Code ]
	 * </pre>
	 */
	public static final int CC_Money = 413;

	/**
	 * <pre>
	 * 8.23. CC-Total-Octets AVP
	 * 
	 * 
	 * 	   The CC-Total-Octets AVP (AVP Code 421) is of type Unsigned64 and
	 * 	   contains the total number of requested, granted, or used octets
	 * 	   regardless of the direction (sent or received).
	 * </pre>
	 */
	public static final int CC_Total_Octets = 421;
	/**
	 * <pre>
	 * 8.24. CC-Input-Octets AVP
	 * 
	 * 
	 * 	   The CC-Input-Octets AVP (AVP Code 412) is of type Unsigned64 and
	 * 	   contains the number of requested, granted, or used octets that can
	 * 	   be/have been received from the end user.
	 * </pre>
	 */
	public static final int CC_Input_Octets = 412;

	/**
	 * <pre>
	 * 8.25. CC-Output-Octets AVP
	 * 
	 * 
	 * 	   The CC-Output-Octets AVP (AVP Code 414) is of type Unsigned64 and
	 * 	   contains the number of requested, granted, or used octets that can
	 * 	   be/have been sent to the end user.
	 * </pre>
	 */
	public static final int CC_Output_Octets = 414;
	/**
	 * <pre>
	 * 8.26. CC-Service-Specific-Units AVP
	 * 
	 * 
	 * 	   The CC-Service-Specific-Units AVP (AVP Code 417) is of type
	 * 	   Unsigned64 and specifies the number of service-specific units (e.g.,
	 * 	   number of events, points) given in a selected service.  The service-
	 * 	   specific units always refer to the service identified in the
	 * 	   Service-Identifier AVP (or Rating-Group AVP when the Multiple-
	 * 	   Services-Credit-Control AVP is used).
	 * </pre>
	 */
	public static final int CC_Service_Specific_Units = 417;
	/**
	 * <pre>
	 * 8.27. Tariff-Change-Usage AVP
	 * 
	 * 
	 * 	   The Tariff-Change-Usage AVP (AVP Code 452) is of type Enumerated and
	 * 	   defines whether units are used before or after a tariff change, or
	 * 	   whether the units straddled a tariff change during the reporting
	 * 	   period.  Omission of this AVP means that no tariff change has
	 * 	   occurred.
	 * 
	 * 	   In addition, when present in answer messages as part of the
	 * 	   Multiple-Services-Credit-Control AVP, this AVP defines whether units
	 * 	   are allocated to be used before or after a tariff change event.
	 * 
	 * 	   When the Tariff-Time-Change AVP is present, omission of this AVP in
	 * 	   answer messages means that the single quota mechanism applies.
	 * 
	 * 	   Tariff-Change-Usage can be one of the following:
	 * 
	 * 	   UNIT_BEFORE_TARIFF_CHANGE       0
	 * 	      When present in the Multiple-Services-Credit-Control AVP, this
	 * 	      value indicates the amount of the units allocated for use before a
	 * 	      tariff change occurs.
	 * 
	 * 	      When present in the Used-Service-Unit AVP, this value indicates
	 * 	      the amount of resource units used before a tariff change had
	 * 	      occurred.
	 * 
	 * 	   UNIT_AFTER_TARIFF_CHANGE        1
	 * 	      When present in the Multiple-Services-Credit-Control AVP, this
	 * 	      value indicates the amount of the units allocated for use after a
	 * 	      tariff change occurs.
	 * 
	 * 	      When present in the Used-Service-Unit AVP, this value indicates
	 * 	      the amount of resource units used after tariff change had
	 * 	      occurred.
	 * 
	 * 	   UNIT_INDETERMINATE              2
	 * 	      The used unit contains the amount of units that straddle the
	 * 	      tariff change (e.g., the metering process reports to the credit-
	 * 	      control client in blocks of n octets, and one block straddled the
	 * 	      tariff change).  This value is to be used only in the Used-
	 * 	      Service-Unit AVP.
	 * </pre>
	 */
	public static final int Tariff_Change_Usage = 452;

	/**
	 * <pre>
	 * 8.28. Service-Identifier AVP
	 * 
	 * 
	 * 	   The Service-Identifier AVP is of type Unsigned32 (AVP Code 439) and
	 * 	   contains the identifier of a service.  The specific service the
	 * 	   request relates to is uniquely identified by the combination of
	 * 	   Service-Context-Id and Service-Identifier AVPs.
	 * 
	 * 	   A usage example of this AVP is illustrated in Appendix A (Flow IX).
	 * </pre>
	 */
	public static final int Service_Identifier = 439;

	/**
	 * <pre>
	 * 8.29. Rating-Group AVP
	 * 
	 * 
	 * 	   The Rating-Group AVP is of type Unsigned32 (AVP Code 432) and
	 * 	   contains the identifier of a rating group.  All the services subject
	 * 	   to the same rating type are part of the same rating group.  The
	 * 	   specific rating group the request relates to is uniquely identified
	 * 	   by the combination of Service-Context-Id and Rating-Group AVPs.
	 * 
	 * 	   A usage example of this AVP is illustrated in Appendix A (Flow IX).
	 * </pre>
	 */
	public static final int Rating_Group = 432;

	/**
	 * <pre>
	 * 8.30. G-S-U-Pool-Reference AVP
	 * 
	 * 
	 * 	   The G-S-U-Pool-Reference AVP (AVP Code 457) is of type Grouped.  It
	 * 	   is used in the Credit-Control-Answer message, and associates the
	 * 	   Granted-Service-Unit AVP within which it appears with a credit pool
	 * 	   within the session.
	 * 
	 * 	   The G-S-U-Pool-Identifier AVP specifies the credit pool from which
	 * 	   credit is drawn for this unit type.
	 * 
	 * 
	 * 	   The CC-Unit-Type AVP specifies the type of units for which credit is
	 * 	   pooled.
	 * 
	 * 	   The Unit-Value AVP specifies the multiplier, which converts between
	 * 	   service units of type CC-Unit-Type and abstract service units within
	 * 	   the credit pool (and thus to service units of any other service or
	 * 	   rating group associated with the same pool).
	 * 
	 * 	   The G-S-U-Pool-Reference AVP is defined as follows (per the grouped-
	 * 	   avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      G-S-U-Pool-Reference    ::= &lt; AVP Header: 457 &gt;
	 * 	                                  { G-S-U-Pool-Identifier }
	 * 	                                  { CC-Unit-Type }
	 * 	                                  { Unit-Value }
	 * </pre>
	 */
	public static final int G_S_U_Pool_Reference = 457;

	/**
	 * <pre>
	 * 8.31. G-S-U-Pool-Identifier AVP
	 * 
	 * 
	 * 	   The G-S-U-Pool-Identifier AVP (AVP Code 453) is of type Unsigned32
	 * 	   and identifies a credit pool within the session.
	 * </pre>
	 */
	public static final int G_S_U_Pool_Identifier = 453;

	/**
	 * <pre>
	 * 8.32. CC-Unit-Type AVP
	 * 
	 * 
	 * 	   The CC-Unit-Type AVP (AVP Code 454) is of type Enumerated and
	 * 	   specifies the type of units considered to be pooled into a credit
	 * 	   pool.
	 * 
	 * 	   The following values are defined for the CC-Unit-Type AVP:
	 * 
	 * 	      TIME                         0
	 * 	      MONEY                        1
	 * 	      TOTAL-OCTETS                 2
	 * 	      INPUT-OCTETS                 3
	 * 	      OUTPUT-OCTETS                4
	 * 	      SERVICE-SPECIFIC-UNITS       5
	 * </pre>
	 */
	public static final int CC_Unit_Type = 454;

	/**
	 * <pre>
	 * 8.33. Validity-Time AVP
	 * 
	 * 
	 * 	   The Validity-Time AVP is of type Unsigned32 (AVP Code 448).  It is
	 * 	   sent from the credit-control server to the credit-control client.
	 * 	   The AVP contains the validity time of the granted service units.  The
	 * 	   measurement of the Validity-Time is started upon receipt of the
	 * 	   Credit-Control-Answer Message containing this AVP.  If the granted
	 * 	   service units have not been consumed within the validity time
	 * 	   specified in this AVP, the credit-control client MUST send a Credit-
	 * 	   Control-Request message to the server, with CC-Request-Type set to
	 * 	   UPDATE_REQUEST.  The value field of the Validity-Time AVP is given in
	 * 	   seconds.
	 * 
	 * 	   The Validity-Time AVP is also used for the graceful service
	 * 	   termination (see section 5.6) to indicate to the credit-control
	 * 	   client how long the subscriber is allowed to use network resources
	 * 	   after the specified action (i.e., REDIRECT or RESTRICT_ACCESS)
	 * 	   started.  When the Validity-Time elapses, a new intermediate
	 * 	   interrogation is sent to the server.
	 * </pre>
	 */
	public static final int Validity_Time = 448;

	/**
	 * <pre>
	 * 8.34. Final-Unit-Indication AVP
	 * 
	 * 
	 * 	   The Final-Unit-Indication AVP (AVP Code 430) is of type Grouped and
	 * 	   indicates that the Granted-Service-Unit AVP in the Credit-Control-
	 * 	   Answer, or in the AA answer, contains the final units for the
	 * 	   service.  After these units have expired, the Diameter credit-control
	 * 	   client is responsible for executing the action indicated in the
	 * 	   Final-Unit-Action AVP (see section 5.6).
	 * 
	 * 	   If more than one unit type is received in the Credit-Control-Answer,
	 * 	   the unit type that first expired SHOULD cause the credit-control
	 * 	   client to execute the specified action.
	 * 
	 * 	   In the first interrogation, the Final-Unit-Indication AVP with
	 * 	   Final-Unit-Action REDIRECT or RESTRICT_ACCESS can also be present
	 * 	   with no Granted-Service-Unit AVP in the Credit-Control-Answer or in
	 * 	   the AA answer.  This indicates to the Diameter credit-control client
	 * 	   to execute the specified action immediately.  If the home service
	 * 	   provider policy is to terminate the service, naturally, the server
	 * 	   SHOULD return the appropriate transient failure (see section 9.1) in
	 * 	   order to implement the policy-defined action.
	 * 
	 * 	   The Final-Unit-Action AVP defines the behavior of the service element
	 * 	   when the user's account cannot cover the cost of the service and MUST
	 * 	   always be present if the Final-Unit-Indication AVP is included in a
	 * 	   command.
	 * 
	 * 	   If the Final-Unit-Action AVP is set to TERMINATE, no other AVPs MUST
	 * 	   be present.
	 * 
	 * 	   If the Final-Unit-Action AVP is set to REDIRECT at least the
	 * 	   Redirect-Server AVP MUST be present.  The Restriction-Filter-Rule AVP
	 * 	   or the Filter-Id AVP MAY be present in the Credit-Control-Answer
	 * 	   message if the user is also allowed to access other services that are
	 * 	   not accessible through the address given in the Redirect-Server AVP.
	 * 
	 * 	   If the Final-Unit-Action AVP is set to RESTRICT_ACCESS, either the
	 * 	   Restriction-Filter-Rule AVP or the Filter-Id AVP SHOULD be present.
	 * 
	 * 	   The Filter-Id AVP is defined in [NASREQ].  The Filter-Id AVP can be
	 * 	   used to reference an IP filter list installed in the access device by
	 * 	   means other than the Diameter credit-control application, e.g.,
	 * 	   locally configured or configured by another entity.
	 * 
	 * 	   The Final-Unit-Indication AVP is defined as follows (per the
	 * 	   grouped-avp-def of RFC 3588 [DIAMBASE]):
	 * 
	 * 	      Final-Unit-Indication ::= &lt; AVP Header: 430 &gt;
	 * 	                                { Final-Unit-Action }
	 *  Restriction-Filter-Rule ]
	 *  Filter-Id ]
	 * 	                                [ Redirect-Server ]
	 * </pre>
	 */
	public static final int Final_Unit_Indication = 430;

	/**
	 * <pre>
	 * 8.35. Final-Unit-Action AVP
	 * 
	 * 
	 * 	   The Final-Unit-Action AVP (AVP Code 449) is of type Enumerated and
	 * 	   indicates to the credit-control client the action to be taken when
	 * 	   the user's account cannot cover the service cost.
	 * 
	 * 	   The Final-Unit-Action can be one of the following:
	 * 
	 * 	   TERMINATE                       0
	 * 	      The credit-control client MUST terminate the service session.
	 * 	      This is the default handling, applicable whenever the credit-
	 * 	      control client receives an unsupported Final-Unit-Action value,
	 * 	      and it MUST be supported by all the Diameter credit-control client
	 * 	      implementations conforming to this specification.
	 * 
	 * 	   REDIRECT                        1
	 * 	      The service element MUST redirect the user to the address
	 * 	      specified in the Redirect-Server-Address AVP.  The redirect action
	 * 	      is defined in section 5.6.2.
	 * 
	 * 	   RESTRICT_ACCESS                 2
	 * 	      The access device MUST restrict the user access according to the
	 * 	      IP packet filters defined in the Restriction-Filter-Rule AVP or
	 * 	      according to the IP packet filters identified by the Filter-Id
	 * 	      AVP.  All the packets not matching the filters MUST be dropped
	 * 	      (see section 5.6.3).
	 * </pre>
	 */
	public static final int Final_Unit_Action = 449;

	/**
	 * <pre>
	 * 8.36. Restriction-Filter-Rule AVP
	 * 
	 * 
	 * 	   The Restriction-Filter-Rule AVP (AVP Code 438) is of type
	 * 	   IPFilterRule and provides filter rules corresponding to services that
	 * 	   are to remain accessible even if there are no more service units
	 * 	   granted.  The access device has to configure the specified filter
	 * 	   rules for the subscriber and MUST drop all the packets not matching
	 * 	   these filters.  Zero, one, or more such AVPs MAY be present in a
	 * 	   Credit-Control-Answer message or in an AA answer message.
	 * </pre>
	 */
	public static final int Restriction_Filter_Rule = 438;

	/**
	 * <pre>
	 * 8.37. Redirect-Server AVP
	 * 
	 * 
	 * 	   The Redirect-Server AVP (AVP Code 434) is of type Grouped and
	 * 	   contains the address information of the redirect server (e.g., HTTP
	 * 	   redirect server, SIP Server) with which the end user is to be
	 * 	   connected when the account cannot cover the service cost.  It MUST be
	 * 	   present when the Final-Unit-Action AVP is set to REDIRECT.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of RFC 3588
	 * 	   [DIAMBASE]):
	 * 
	 * 	      Redirect-Server ::= &lt; AVP Header: 434 &gt;
	 * 	                          { Redirect-Address-Type }
	 * 	                          { Redirect-Server-Address }
	 * </pre>
	 */
	public static final int Redirect_Server = 434;

	/**
	 * <pre>
	 * 8.38. Redirect-Address-Type AVP
	 * 
	 * 
	 * 	   The Redirect-Address-Type AVP (AVP Code 433) is of type Enumerated
	 * 	   and defines the address type of the address given in the Redirect-
	 * 	   Server-Address AVP.
	 * 
	 * 	   The address type can be one of the following:
	 * 
	 * 	   IPv4 Address                    0
	 * 	      The address type is in the form of &quot;dotted-decimal&quot; IPv4 address,
	 * 	      as defined in [IPv4].
	 * 
	 * 	   IPv6 Address                    1
	 * 	      The address type is in the form of IPv6 address, as defined in
	 * 	      [IPv6Addr].  The address is a text representation of the address
	 * 	      in either the preferred or alternate text form [IPv6Addr].
	 * 	      Conformant implementations MUST support the preferred form and
	 * 	      SHOULD support the alternate text form for IPv6 addresses.
	 * 
	 * 	   URL                             2
	 * 	      The address type is in the form of Uniform Resource Locator, as
	 * 	      defined in [URL].
	 * 
	 * 	   SIP URI                         3
	 * 	      The address type is in the form of SIP Uniform Resource
	 * 	      Identifier, as defined in [SIP].
	 * </pre>
	 */
	public static final int Redirect_Address_Type = 433;

	/**
	 * <pre>
	 * 8.39. Redirect-Server-Address AVP
	 * 
	 * 
	 * 	   The Redirect-Server-Address AVP (AVP Code 435) is of type UTF8String
	 * 	   and defines the address of the redirect server (e.g., HTTP redirect
	 * 	   server, SIP Server) with which the end user is to be connected when
	 * 	   the account cannot cover the service cost.
	 * </pre>
	 */
	public static final int Redirect_Server_Address = 435;

	/**
	 * <pre>
	 * 8.40. Multiple-Services-Indicator AVP
	 * 
	 * 
	 * 	   The Multiple-Services-Indicator AVP (AVP Code 455) is of type
	 * 	   Enumerated and indicates whether the Diameter credit-control client
	 * 	   is capable of handling multiple services independently within a
	 * 	   (sub-) session.  The absence of this AVP means that independent
	 * 	   credit-control of multiple services is not supported.
	 * 
	 * 	   A server not implementing the independent credit-control of multiple
	 * 	   services MUST treat the Multiple-Services-Indicator AVP as an invalid
	 * 	   AVP.
	 * 
	 * 	   The following values are defined for the Multiple-Services-Indicator
	 * 	   AVP:
	 * 
	 * 	   MULTIPLE_SERVICES_NOT_SUPPORTED 0
	 * 	      Client does not support independent credit-control of multiple
	 * 	      services within a (sub-)session.
	 * 
	 * 	   MULTIPLE_SERVICES_SUPPORTED     1
	 * 	      Client supports independent credit-control of multiple services
	 * 	      within a (sub-)session.
	 * </pre>
	 */
	public static final int Multiple_Services_Indicator = 455;

	/**
	 * <pre>
	 * 8.41. Requested-Action AVP
	 * 
	 * 
	 * 	   The Requested-Action AVP (AVP Code 436) is of type Enumerated and
	 * 	   contains the requested action being sent by Credit-Control-Request
	 * 	   command where the CC-Request-Type is set to EVENT_REQUEST.  The
	 * 	   following values are defined for the Requested-Action AVP:
	 * 
	 * 	   DIRECT_DEBITING                 0
	 * 	      This indicates a request to decrease the end user's account
	 * 	      according to information specified in the Requested-Service-Unit
	 * 	      AVP and/or Service-Identifier AVP (additional rating information
	 * 	      may be included in service-specific AVPs or in the Service-
	 * 	      Parameter-Info AVP).  The Granted-Service-Unit AVP in the Credit-
	 * 	      Control-Answer command contains the debited units.
	 * 
	 * 	   REFUND_ACCOUNT                  1
	 * 	      This indicates a request to increase the end user's account
	 * 	      according to information specified in the Requested-Service-Unit
	 * 	      AVP and/or Service-Identifier AVP (additional rating information
	 * 	      may be included in service-specific AVPs or in the Service-
	 * 	      Parameter-Info AVP).  The Granted-Service-Unit AVP in the Credit-
	 * 	      Control-Answer command contains the refunded units.
	 * 
	 * 	   CHECK_BALANCE                   2
	 * 	      This indicates a balance check request.  In this case, the
	 * 	      checking of the account balance is done without any credit
	 * 	      reservation from the account.  The Check-Balance-Result AVP in the
	 * 	      Credit-Control-Answer command contains the result of the balance
	 * 	      check.
	 * 
	 * 	   PRICE_ENQUIRY                   3
	 * 	      This indicates a price enquiry request.  In this case, neither
	 * 	      checking of the account balance nor reservation from the account
	 * 	      will be done; only the price of the service will be returned in
	 * 	      the Cost-Information AVP in the Credit-Control-Answer Command.
	 * </pre>
	 */
	public static final int Requested_Action = 436;

	/**
	 * <pre>
	 * 8.42. Service-Context-Id AVP
	 * 
	 * 
	 * 	   The Service-Context-Id AVP is of type UTF8String (AVP Code 461) and
	 * 	   contains a unique identifier of the Diameter credit-control service
	 * 	   specific document that applies to the request (as defined in section
	 * 	   4.1.2).  This is an identifier allocated by the service provider, by
	 * 	   the service element manufacturer, or by a standardization body, and
	 * 	   MUST uniquely identify a given Diameter credit-control service
	 * 	   specific document.  The format of the Service-Context-Id is:
	 * 
	 * 	   &quot;service-context&quot; &quot;@&quot; &quot;domain&quot;
	 * 
	 * 	   service-context = Token
	 * 
	 * 	   The Token is an arbitrary string of characters and digits.
	 * 
	 * 	   'domain' represents the entity that allocated the Service-Context-Id.
	 * 	   It can be ietf.org, 3gpp.org, etc., if the identifier is allocated by
	 * 	   a standardization body, or it can be the FQDN of the service provider
	 * 	   (e.g., provider.example.com) or of the vendor (e.g.,
	 * 	   vendor.example.com) if the identifier is allocated by a private
	 * 	   entity.
	 * 
	 * 	   This AVP SHOULD be placed as close to the Diameter header as
	 * 	   possible.
	 * 
	 * 	   Service-specific documents that are for private use only (i.e., to
	 * 	   one provider's own use, where no interoperability is deemed useful)
	 * 	   may define private identifiers without need of coordination.
	 * 	   However, when interoperability is wanted, coordination of the
	 * 	   identifiers via, for example, publication of an informational RFC is
	 * 	   RECOMMENDED in order to make Service-Context-Id globally available.
	 * </pre>
	 */
	public static final int Service_Context_Id = 461;

	/**
	 * <pre>
	 * 8.43. Service-Parameter-Info AVP
	 * 
	 * 
	 * 	   The Service-Parameter-Info AVP (AVP Code 440) is of type Grouped and
	 * 	   contains service-specific information used for price calculation or
	 * 	   rating.  The Service-Parameter-Type AVP defines the service parameter
	 * 	   type, and the Service-Parameter-Value AVP contains the parameter
	 * 	   value.  The actual contents of these AVPs are not within the scope of
	 * 	   this document and SHOULD be defined in another Diameter application,
	 * 	   in standards written by other standardization bodies, or in service-
	 * 	   specific documentation.
	 * 
	 * 	   In the case of an unknown service request (e.g., unknown Service-
	 * 	   Parameter-Type), the corresponding answer message MUST contain the
	 * 	   error code DIAMETER_RATING_FAILED.  A Credit-Control-Answer message
	 * 	   with this error MUST contain one or more Failed-AVP AVPs containing
	 * 	   the Service-Parameter-Info AVPs that caused the failure.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of RFC 3588
	 * 	   [DIAMBASE]):
	 * 
	 * 	      Service-Parameter-Info ::= &lt; AVP Header: 440 &gt;
	 * 	                                 { Service-Parameter-Type }
	 * 	                                 { Service-Parameter-Value }
	 * </pre>
	 */
	public static final int Service_Parameter_Info = 440;

	/**
	 * <pre>
	 * 8.44. Service-Parameter-Type AVP
	 * 
	 * 
	 * 	   The Service-Parameter-Type AVP is of type Unsigned32 (AVP Code 441)
	 * 	   and defines the type of the service event specific parameter (e.g.,
	 * 	   it can be the end-user location or service name).  The different
	 * 	   parameters and their types are service specific, and the meanings of
	 * 	   these parameters are not defined in this document.  Whoever allocates
	 * 	   the Service-Context-Id (i.e., unique identifier of a service-specific
	 * 	   document) is also responsible for assigning Service-Parameter-Type
	 * 	   values for the service and ensuring their uniqueness within the given
	 * 	   service.  The Service-Parameter-Value AVP contains the value
	 * 	   associated with the service parameter type.
	 * </pre>
	 */
	public static final int Service_Parameter_Type = 441;

	/**
	 * <pre>
	 * 8.45. Service-Parameter-Value AVP
	 * 
	 * 
	 * 	   The Service-Parameter-Value AVP is of type OctetString (AVP Code 442)
	 * 	   and contains the value of the service parameter type.
	 * </pre>
	 */
	public static final int Service_Parameter_Value = 442;

	/**
	 * <pre>
	 * 8.46. Subscription-Id AVP
	 * 
	 * 
	 * 	   The Subscription-Id AVP (AVP Code 443) is used to identify the end
	 * 	   user's subscription and is of type Grouped.  The Subscription-Id AVP
	 * 	   includes a Subscription-Id-Data AVP that holds the identifier and a
	 * 	   Subscription-Id-Type AVP that defines the identifier type.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of RFC 3588
	 * 	   [DIAMBASE]):
	 * 
	 * 	      Subscription-Id ::= &lt; AVP Header: 443 &gt;
	 * 	                          { Subscription-Id-Type }
	 * 	                          { Subscription-Id-Data }
	 * </pre>
	 */
	public static final int Subscription_Id = 443;

	/**
	 * <pre>
	 * 8.47. Subscription-Id-Type AVP
	 * 
	 * 
	 * 	   The Subscription-Id-Type AVP (AVP Code 450) is of type Enumerated,
	 * 	   and it is used to determine which type of identifier is carried by
	 * 	   the Subscription-Id AVP.
	 * 
	 * 	   This specification defines the following subscription identifiers.
	 * 	   However, new Subscription-Id-Type values can be assigned by an IANA
	 * 	   designated expert, as defined in section 12.  A server MUST implement
	 * 	   all the Subscription-Id-Types required to perform credit
	 * 	   authorization for the services it supports, including possible future
	 * 	   values.  Unknown or unsupported Subscription-Id-Types MUST be treated
	 * 	   according to the 'M' flag rule, as defined in [DIAMBASE].
	 * 
	 * 	   END_USER_E164                   0
	 * 	      The identifier is in international E.164 format (e.g., MSISDN),
	 * 	      according to the ITU-T E.164 numbering plan defined in [E164] and
	 * 	      [CE164].
	 * 
	 * 	   END_USER_IMSI                   1
	 * 	      The identifier is in international IMSI format, according to the
	 * 	      ITU-T E.212 numbering plan as defined in [E212] and [CE212].
	 * 
	 * 	   END_USER_SIP_URI                2
	 * 	      The identifier is in the form of a SIP URI, as defined in [SIP].
	 * 
	 * 	   END_USER_NAI                    3
	 * 	      The identifier is in the form of a Network Access Identifier, as
	 * 	      defined in [NAI].
	 * 
	 * 	   END_USER_PRIVATE                4
	 * 	      The Identifier is a credit-control server private identifier.
	 * </pre>
	 */
	public static final int Subscription_Id_Type = 450;

	/**
	 * <pre>
	 * 8.48. Subscription-Id-Data AVP
	 * 
	 * 
	 * 	   The Subscription-Id-Data AVP (AVP Code 444) is used to identify the
	 * 	   end user and is of type UTF8String.  The Subscription-Id-Type AVP
	 * 	   defines which type of identifier is used.
	 * </pre>
	 */
	public static final int Subscription_Id_Data = 444;

	/**
	 * <pre>
	 * 8.49. User-Equipment-Info AVP
	 * 
	 * 
	 * 	   The User-Equipment-Info AVP (AVP Code 458) is of type Grouped and
	 * 	   allows the credit-control client to indicate the identity and
	 * 	   capability of the terminal the subscriber is using for the connection
	 * 	   to network.
	 * 
	 * 	   It is defined as follows (per the grouped-avp-def of RFC 3588
	 * 	   [DIAMBASE]):
	 * 
	 * 	      User-Equipment-Info ::= &lt; AVP Header: 458 &gt;
	 * 	                              { User-Equipment-Info-Type }
	 * 	                              { User-Equipment-Info-Value }
	 * </pre>
	 */
	public static final int User_Equipment_Info = 458;

	/**
	 * <pre>
	 * 8.50. User-Equipment-Info-Type AVP
	 * 
	 * 
	 * 	   The User-Equipment-Info-Type AVP is of type Enumerated  (AVP Code
	 * 	   459) and defines the type of user equipment information contained in
	 * 	   the User-Equipment-Info-Value AVP.
	 * 
	 * 	   This specification defines the following user equipment types.
	 * 	   However, new User-Equipment-Info-Type values can be assigned by an
	 * 	   IANA designated expert, as defined in section 12.
	 * 
	 * 	   IMEISV                          0
	 * 	      The identifier contains the International Mobile Equipment
	 * 	      Identifier and Software Version in the international IMEISV format
	 * 	      according to 3GPP TS 23.003 [3GPPIMEI].
	 * 
	 * 	   MAC                             1
	 * 	      The 48-bit MAC address is formatted as described in [RAD802.1X].
	 * 
	 * 	   EUI64                           2
	 * 	      The 64-bit identifier used to identify hardware instance of the
	 * 	      product, as defined in [EUI64].
	 * 
	 * 	   MODIFIED_EUI64                  3
	 * 	      There are a number of types of terminals that have identifiers
	 * 	      other than IMEI, IEEE 802 MACs, or EUI-64.  These identifiers can
	 * 	      be converted to modified EUI-64 format as described in [IPv6Addr]
	 * 	      or by using some other methods referred to in the service-specific
	 * 	      documentation.
	 * </pre>
	 */
	public static final int User_Equipment_Info_Type = 459;

	/**
	 * <pre>
	 * 8.51. User-Equipment-Info-Value AVP
	 * 
	 * 
	 * 	   The User-Equipment-Info-Value AVP (AVP Code 460) is of type
	 * 	   OctetString.  The User-Equipment-Info-Type AVP defines which type of
	 * 	   identifier is used.
	 * </pre>
	 */
	public static final int User_Equipment_Info_Value = 460;

}
