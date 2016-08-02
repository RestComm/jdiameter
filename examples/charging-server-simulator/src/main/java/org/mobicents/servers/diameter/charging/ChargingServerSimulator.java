package org.mobicents.servers.diameter.charging;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Peer;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlAnswerImpl;
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.servers.diameter.utils.DiameterUtilities;
import org.mobicents.servers.diameter.utils.StackCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mobicents Diameter Charging Server Simulator.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ChargingServerSimulator extends CCASessionFactoryImpl implements NetworkReqListener, EventListener<Request, Answer> {

  private static final Logger logger = LoggerFactory.getLogger(ChargingServerSimulator.class);

  private static final Object[] EMPTY_ARRAY = new Object[]{};

  private ApplicationId roAppId = ApplicationId.createByAuthAppId(10415L, 4L);

  private HashMap<String, Long> accounts = new HashMap<String, Long>();
  private HashMap<String, Long> reserved = new HashMap<String, Long>();

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    new ChargingServerSimulator();
  }

  StackCreator stackCreator = null;

  public ChargingServerSimulator() throws Exception {
    super();

    AvpDictionary.INSTANCE.parseDictionary(this.getClass().getClassLoader().getResourceAsStream("dictionary.xml"));

    try {
      String config = readFile(this.getClass().getClassLoader().getResourceAsStream("config-server.xml"));
      this.stackCreator = new StackCreator(config, this, this, "Server", true);

      Network network = this.stackCreator.unwrap(Network.class);
      network.addNetworkReqListener(this, roAppId);
      network.addNetworkReqListener(this, ApplicationId.createByAuthAppId(0, 4));

      this.stackCreator.start(Mode.ALL_PEERS, 30000, TimeUnit.MILLISECONDS);

      printLogo();

      sessionFactory = (ISessionFactory) stackCreator.getSessionFactory();
      init(sessionFactory); // damn.. this doesn't looks good

      sessionFactory.registerAppFacory(ServerCCASession.class, this);
      sessionFactory.registerAppFacory(ClientCCASession.class, this);

      // Read users from properties file
      Properties properties = new Properties();
      try {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("accounts.properties");
        if (is == null) {
          throw new IOException("InputStream is null");
        }
        properties.load(is);
        for (Object property : properties.keySet()) {
          String accountName = (String) property;
          String balance = properties.getProperty(accountName,  "0");
          if (logger.isInfoEnabled()) {
            logger.info("Provisioned user '" + accountName + "' with [" + balance + "] units.");
          }
          accounts.put(accountName, Long.valueOf(balance));
        }
      }
      catch (IOException e) {
        System.err.println("Failed to read 'accounts.properties' file. Aborting.");
        System.exit(-1);
      }
    }
    catch (Exception e) {
      logger.error("Failure initializing Mobicents Diameter Ro/Rf Server Simulator", e);
    }
  }

  private void printLogo() {
    if (logger.isInfoEnabled()) {
      Properties sysProps = System.getProperties();

      String osLine = sysProps.getProperty("os.name") + "/" + sysProps.getProperty("os.arch");
      String javaLine = sysProps.getProperty("java.vm.vendor") + " " + sysProps.getProperty("java.vm.name") + " " + sysProps.getProperty("java.vm.version");

      Peer localPeer = stackCreator.getMetaData().getLocalPeer();

      String diameterLine = localPeer.getProductName() + " (" +  localPeer.getUri() + " @ " + localPeer.getRealmName() + ")";

      logger.info("===============================================================================");
      logger.info("");
      logger.info("== Mobicents Diameter Ro/Rf Server Simulator (" + osLine + ")" );
      logger.info("");
      logger.info("== " + javaLine);
      logger.info("");
      logger.info("== " + diameterLine);
      logger.info("");
      logger.info("===============================================================================");
    }
  }

  @Override
  public Answer processRequest(Request request) {
    if (logger.isInfoEnabled()) {
      logger.info("<< Received Request [" + request + "]");
    }
    try {
      ServerCCASessionImpl session =
          (sessionFactory).getNewAppSession(request.getSessionId(), ApplicationId.createByAuthAppId(0, 4), ServerCCASession.class, EMPTY_ARRAY);
      session.processRequest(request);
    }
    catch (InternalException e) {
      logger.error(">< Failure handling received request.", e);
    }

    return null;
  }

  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    if (logger.isInfoEnabled()) {
      logger.info("<< Received Success Message for Request [" + request + "] and Answer [" + answer + "]");
    }
  }

  @Override
  public void timeoutExpired(Request request) {
    if (logger.isInfoEnabled()) {
      logger.info("<< Received Timeout for Request [" + request + "]");
    }
  }

  @Override
  public void doCreditControlAnswer(ClientCCASession session, JCreditControlRequest request, JCreditControlAnswer answer) throws InternalException {
    // Do nothing.
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    // Do nothing.
  }

  @Override
  public void doReAuthRequest(ClientCCASession session, ReAuthRequest request) throws InternalException {
    // Do nothing.
  }

  @Override
  public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request) throws InternalException {
    AvpSet ccrAvps = request.getMessage().getAvps();

    switch (request.getRequestTypeAVPValue()) {
      // INITIAL_REQUEST                 1
      case 1:
        // UPDATE_REQUEST                  2
      case 2:
        if (logger.isInfoEnabled()) {
          logger.info("<< Received Credit-Control-Request [" + (request.getRequestTypeAVPValue() == 1 ? "INITIAL" : "UPDATE") + "]");
        }
        JCreditControlAnswer cca = null;
        try {
          long requestedUnits = ccrAvps.getAvp(437).getGrouped().getAvp(420).getInteger32();
          String subscriptionId = ccrAvps.getAvp(443).getGrouped().getAvp(444).getUTF8String();
          String serviceContextId = ccrAvps.getAvp(461).getUTF8String();

          if (logger.isInfoEnabled()) {
            logger.info(">> '" + subscriptionId + "' requested " + requestedUnits + " units for '" + serviceContextId + "'.");
          }

          Long balance = accounts.get(subscriptionId);
          if (balance != null) {
            if (balance <= 0) {
              //    DIAMETER_CREDIT_LIMIT_REACHED              4012
              // The credit-control server denies the service request because the
              // end user's account could not cover the requested service.  If the
              // CCR contained used-service-units they are deducted, if possible.
              cca = createCCA(session, request, -1, 4012);
              if (logger.isInfoEnabled()) {
                logger.info("<> '" + subscriptionId + "' has insufficient credit units. Rejecting.");
              }
            }
            else {
              // Check if not first request, should have Used-Service-Unit AVP
              if (ccrAvps.getAvp(415) != null && ccrAvps.getAvp(415).getUnsigned32() >= 1) {
                Avp usedServiceUnit = ccrAvps.getAvp(446);
                if (usedServiceUnit != null) {
                  Long wereReserved = reserved.remove(subscriptionId + "_" + serviceContextId);
                  wereReserved = wereReserved == null ? 0 : wereReserved;
                  long wereUsed = usedServiceUnit.getGrouped().getAvp(420).getUnsigned32();
                  long remaining = wereReserved - wereUsed;

                  if (logger.isInfoEnabled()) {
                    logger.info(">> '" + subscriptionId + "' had " + wereReserved + " reserved units, " + wereUsed + " units were used."
                        + " (rem: " + remaining + ").");
                  }
                  balance += remaining;
                }
              }

              long grantedUnits = Math.min(requestedUnits, balance);
              cca = createCCA(session, request, grantedUnits, 2001);

              reserved.put(subscriptionId + "_" + serviceContextId, grantedUnits);
              balance -= grantedUnits;
              if (logger.isInfoEnabled()) {
                logger.info(">> '" + subscriptionId + "' Balance: " + (balance + grantedUnits) +
                    " // Available(" + balance + ")  Reserved(" + grantedUnits + ")");
              }
              accounts.put(subscriptionId, balance);

              // Check if the user has no more credit
              if (balance <= 0) {
                // 8.34.  Final-Unit-Indication AVP
                //
                // The Final-Unit-Indication AVP (AVP Code 430) is of type Grouped and
                // indicates that the Granted-Service-Unit AVP in the Credit-Control-
                // Answer, or in the AA answer, contains the final units for the
                // service.  After these units have expired, the Diameter credit-control
                // client is responsible for executing the action indicated in the
                // Final-Unit-Action AVP (see section 5.6).
                //
                // If more than one unit type is received in the Credit-Control-Answer,
                // the unit type that first expired SHOULD cause the credit-control
                // client to execute the specified action.
                //
                // In the first interrogation, the Final-Unit-Indication AVP with
                // Final-Unit-Action REDIRECT or RESTRICT_ACCESS can also be present
                // with no Granted-Service-Unit AVP in the Credit-Control-Answer or in
                // the AA answer.  This indicates to the Diameter credit-control client
                // to execute the specified action immediately.  If the home service
                // provider policy is to terminate the service, naturally, the server
                // SHOULD return the appropriate transient failure (see section 9.1) in
                // order to implement the policy-defined action.
                //
                // The Final-Unit-Action AVP defines the behavior of the service element
                // when the user's account cannot cover the cost of the service and MUST
                // always be present if the Final-Unit-Indication AVP is included in a
                // command.
                //
                // If the Final-Unit-Action AVP is set to TERMINATE, no other AVPs MUST
                // be present.
                //
                // If the Final-Unit-Action AVP is set to REDIRECT at least the
                // Redirect-Server AVP MUST be present.  The Restriction-Filter-Rule AVP
                // or the Filter-Id AVP MAY be present in the Credit-Control-Answer
                // message if the user is also allowed to access other services that are
                // not accessible through the address given in the Redirect-Server AVP.
                //
                // If the Final-Unit-Action AVP is set to RESTRICT_ACCESS, either the
                // Restriction-Filter-Rule AVP or the Filter-Id AVP SHOULD be present.
                //
                // The Filter-Id AVP is defined in [NASREQ].  The Filter-Id AVP can be
                // used to reference an IP filter list installed in the access device by
                // means other than the Diameter credit-control application, e.g.,
                // locally configured or configured by another entity.
                //
                // The Final-Unit-Indication AVP is defined as follows (per the
                // grouped-avp-def of RFC 3588 [DIAMBASE]):
                //
                // Final-Unit-Indication ::= < AVP Header: 430 >
                //                           { Final-Unit-Action }
                //                          *[ Restriction-Filter-Rule ]
                //                          *[ Filter-Id ]
                //                           [ Redirect-Server ]
                AvpSet finalUnitIndicationAvp = cca.getMessage().getAvps().addGroupedAvp(430);

                // 8.35.  Final-Unit-Action AVP
                //
                // The Final-Unit-Action AVP (AVP Code 449) is of type Enumerated and
                // indicates to the credit-control client the action to be taken when
                // the user's account cannot cover the service cost.
                //
                // The Final-Unit-Action can be one of the following:
                //
                // TERMINATE                       0
                //   The credit-control client MUST terminate the service session.
                //   This is the default handling, applicable whenever the credit-
                //   control client receives an unsupported Final-Unit-Action value,
                //   and it MUST be supported by all the Diameter credit-control client
                //   implementations conforming to this specification.
                //
                // REDIRECT                        1
                //   The service element MUST redirect the user to the address
                //   specified in the Redirect-Server-Address AVP.  The redirect action
                //   is defined in section 5.6.2.
                //
                // RESTRICT_ACCESS                 2
                //   The access device MUST restrict the user access according to the
                //   IP packet filters defined in the Restriction-Filter-Rule AVP or
                //   according to the IP packet filters identified by the Filter-Id
                //   AVP.  All the packets not matching the filters MUST be dropped
                //   (see section 5.6.3).
                finalUnitIndicationAvp.addAvp(449, 0);
              }
            }
          }
          else {
            //    DIAMETER_USER_UNKNOWN                      5030
            // The specified end user is unknown in the credit-control server.
            cca = createCCA(session, request, -1, 5030);
            cca.getMessage().setError(true);
            if (logger.isInfoEnabled()) {
              logger.info("<> '" + subscriptionId + "' is not provisioned in this server. Rejecting.");
            }
          }

          //cca.getMessage().getAvps().addAvp(461, serviceContextId, false);
          session.sendCreditControlAnswer(cca);
        }
        catch (Exception e) {
          logger.error(">< Failure processing Credit-Control-Request [" + (request.getRequestTypeAVPValue() == 1 ? "INITIAL" : "UPDATE") + "]", e);
        }
        break;
        // TERMINATION_REQUEST             3
      case 3:
        if (logger.isInfoEnabled()) {
          logger.info("<< Received Credit-Control-Request [TERMINATION]");
        }
        try {
          String subscriptionId = ccrAvps.getAvp(443).getGrouped().getAvp(444).getUTF8String();
          String serviceContextId = ccrAvps.getAvp(461).getUTF8String();

          if (logger.isInfoEnabled()) {
            logger.info(">> '" + subscriptionId + "' requested service termination for '" + serviceContextId + "'.");
          }

          Long balance = accounts.get(subscriptionId);

          if (ccrAvps.getAvp(415) != null && ccrAvps.getAvp(415).getUnsigned32() >= 1) {
            Avp usedServiceUnit = ccrAvps.getAvp(446);
            if (usedServiceUnit != null) {
              long wereReserved = reserved.remove(subscriptionId + "_" + serviceContextId);
              long wereUsed = usedServiceUnit.getGrouped().getAvp(420).getUnsigned32();
              long remaining = wereReserved - wereUsed;

              if (logger.isInfoEnabled()) {
                logger.info(">> '" + subscriptionId + "' had " + wereReserved + " reserved units, " + wereUsed + " units were used."
                    + " (non-used: " + remaining + ").");
              }
              balance += remaining;
            }
          }

          if (logger.isInfoEnabled()) {
            logger.info(">> '" + subscriptionId + "' Balance: " + balance + " // Available(" + balance + ")  Reserved(0)");
          }
          accounts.put(subscriptionId, balance);

          cca = createCCA(session, request, -1, 2001);
          // 8.7.  Cost-Information AVP
          //
          // The Cost-Information AVP (AVP Code 423) is of type Grouped, and it is
          // used to return the cost information of a service, which the credit-
          // control client can transfer transparently to the end user.  The
          // included Unit-Value AVP contains the cost estimate (always type of
          // money) of the service, in the case of price enquiry, or the
          // accumulated cost estimation, in the case of credit-control session.
          //
          // The Currency-Code specifies in which currency the cost was given.
          // The Cost-Unit specifies the unit when the service cost is a cost per
          // unit (e.g., cost for the service is $1 per minute).
          //
          // When the Requested-Action AVP with value PRICE_ENQUIRY is included in
          // the Credit-Control-Request command, the Cost-Information AVP sent in
          // the succeeding Credit-Control-Answer command contains the cost
          // estimation of the requested service, without any reservation being
          // made.
          //
          // The Cost-Information AVP included in the Credit-Control-Answer
          // command with the CC-Request-Type set to UPDATE_REQUEST contains the
          // accumulated cost estimation for the session, without taking any
          // credit reservation into account.
          //
          // The Cost-Information AVP included in the Credit-Control-Answer
          // command with the CC-Request-Type set to EVENT_REQUEST or
          // TERMINATION_REQUEST contains the estimated total cost for the
          // requested service.
          //
          // It is defined as follows (per the grouped-avp-def of
          // RFC 3588 [DIAMBASE]):
          //
          //           Cost-Information ::= < AVP Header: 423 >
          //                                { Unit-Value }
          //                                { Currency-Code }
          //                                [ Cost-Unit ]

          // 7.2.133 Remaining-Balance AVP
          //
          // The Remaining-Balance AVP (AVPcode 2021) is of type Grouped and
          // provides information about the remaining account balance of the
          // subscriber.
          //
          // It has the following ABNF grammar:
          //      Remaining-Balance :: =  < AVP Header: 2021 >
          //                              { Unit-Value }
          //                              { Currency-Code }

          // We use no money notion ... maybe later.
          // AvpSet costInformation = ccaAvps.addGroupedAvp(423);

          session.sendCreditControlAnswer(cca);
        }
        catch (Exception e) {
          logger.error(">< Failure processing Credit-Control-Request [TERMINATION]", e);
        }
        break;
        // EVENT_REQUEST                   4
      case 4:
        if (logger.isInfoEnabled()) {
          logger.info("<< Received Credit-Control-Request [EVENT]");
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void doReAuthAnswer(ServerCCASession session, ReAuthRequest request, ReAuthAnswer answer) throws InternalException {
    // Do Nothing.
  }

  @Override
  public void sessionSupervisionTimerExpired(ServerCCASession session) {
    // Do Nothing.
  }

  @Override
  public void denyAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    // Do Nothing.
  }

  @Override
  public void txTimerExpired(ClientCCASession session) {
    // Do Nothing.
  }

  private JCreditControlAnswer createCCA(ServerCCASession session, JCreditControlRequest request, long grantedUnits, long resultCode)
      throws InternalException, AvpDataException {
    JCreditControlAnswerImpl answer = new JCreditControlAnswerImpl((Request) request.getMessage(), resultCode);

    AvpSet ccrAvps = request.getMessage().getAvps();
    AvpSet ccaAvps = answer.getMessage().getAvps();

    // <Credit-Control-Answer> ::= < Diameter Header: 272, PXY >
    //  < Session-Id >
    //  { Result-Code }
    //  { Origin-Host }
    //  { Origin-Realm }
    //  { Auth-Application-Id }

    //  { CC-Request-Type }
    // Using the same as the one present in request
    ccaAvps.addAvp(ccrAvps.getAvp(416));

    //  { CC-Request-Number }
    // Using the same as the one present in request
    ccaAvps.addAvp(ccrAvps.getAvp(415));

    //  [ User-Name ]
    //  [ CC-Session-Failover ]
    //  [ CC-Sub-Session-Id ]
    //  [ Acct-Multi-Session-Id ]
    //  [ Origin-State-Id ]
    //  [ Event-Timestamp ]

    //  [ Granted-Service-Unit ]
    // 8.17.  Granted-Service-Unit AVP
    //
    // Granted-Service-Unit AVP (AVP Code 431) is of type Grouped and
    // contains the amount of units that the Diameter credit-control client
    // can provide to the end user until the service must be released or the
    // new Credit-Control-Request must be sent.  A client is not required to
    // implement all the unit types, and it must treat unknown or
    // unsupported unit types in the answer message as an incorrect CCA
    // answer.  In this case, the client MUST terminate the credit-control
    // session and indicate in the Termination-Cause AVP reason
    // DIAMETER_BAD_ANSWER.
    //
    // The Granted-Service-Unit AVP is defined as follows (per the grouped-
    // avp-def of RFC 3588 [DIAMBASE]):
    //
    // Granted-Service-Unit ::= < AVP Header: 431 >
    //                          [ Tariff-Time-Change ]
    //                          [ CC-Time ]
    //                          [ CC-Money ]
    //                          [ CC-Total-Octets ]
    //                          [ CC-Input-Octets ]
    //                          [ CC-Output-Octets ]
    //                          [ CC-Service-Specific-Units ]
    //                         *[ AVP ]
    if (grantedUnits >= 0) {
      AvpSet gsuAvp = ccaAvps.addGroupedAvp(431);
      // Fetch AVP/Value from Request
      // gsuAvp.addAvp(ccrAvps.getAvp(437).getGrouped().getAvp(420));
      gsuAvp.addAvp(420, grantedUnits, true);
    }

    // *[ Multiple-Services-Credit-Control ]
    //  [ Cost-Information]
    //  [ Final-Unit-Indication ]
    //  [ Check-Balance-Result ]
    //  [ Credit-Control-Failure-Handling ]
    //  [ Direct-Debiting-Failure-Handling ]
    //  [ Validity-Time]
    // *[ Redirect-Host]
    //  [ Redirect-Host-Usage ]
    //  [ Redirect-Max-Cache-Time ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]
    // *[ Failed-AVP ]
    // *[ AVP ]

    if (logger.isInfoEnabled()) {
      logger.info(">> Created Credit-Control-Answer.");
      DiameterUtilities.printMessage(answer.getMessage());
    }

    return answer;
  }

  private static String readFile(InputStream is) throws IOException {
    /*FileInputStream stream = new FileInputStream(is);
    try {
      FileChannel fc = stream.getChannel();
      MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
      // Instead of using default, pass in a decoder.
      return Charset.defaultCharset().decode(bb).toString();
    }
    finally {
      stream.close();
    }*/
    BufferedInputStream bin = new BufferedInputStream(is);

    byte[] contents = new byte[1024];

    int bytesRead = 0;
    String strFileContents;
    StringBuilder sb = new StringBuilder();

    while ( (bytesRead = bin.read(contents)) != -1) {
      strFileContents = new String(contents, 0, bytesRead);
      sb.append(strFileContents);
    }

    return sb.toString();
  }
}
