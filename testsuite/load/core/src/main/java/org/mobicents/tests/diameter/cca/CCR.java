package org.mobicents.tests.diameter.cca;

import java.io.InputStream;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Level;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.CCASessionFactoryImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlAnswerImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl;
import org.mobicents.tests.diameter.AbstractStackRunner;

public class CCR extends AbstractStackRunner implements NetworkReqListener, EventListener<Request, Answer>,
    ServerCCASessionListener, IServerCCASessionContext, StateChangeListener<AppSession> {

  protected boolean isEventBased = true;
  protected ApplicationId authApplicationId = ApplicationId.createByAuthAppId(0, 4);
  // its miliseconds
  protected long messageTimeout = 5000;

  protected int defaultDirectDebitingFailureHandling = 0;
  protected int defaultCreditControlFailureHandling = 0;

  // its seconds
  protected long defaultValidityTime = 30;
  protected long defaultTxTimerValue = 10;

  protected CCASessionFactoryImpl ccaSessionFactory;

  public CCR() {
    super();

  }

  @Override
  public void configure(InputStream f) throws Exception {
    // TODO Auto-generated method stub
    super.configure(f);
    this.ccaSessionFactory = new CCASessionFactoryImpl(super.factory);
    this.ccaSessionFactory.setServerSessionListener(this);
    this.ccaSessionFactory.setServerContextListener(this);
    Network network = stack.unwrap(Network.class);
    network.addNetworkReqListener(this, authApplicationId);
    ((ISessionFactory) super.factory).registerAppFacory(ServerCCASession.class, ccaSessionFactory);
  }

  public void doCreditControlRequest(ServerCCASession session, JCreditControlRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    if (log.isInfoEnabled()) {
      log.info("Received Request: " + ((Request) request.getMessage()).getCommandCode() + "\nE2E:"
          + ((Request) request.getMessage()).getEndToEndIdentifier() + "\nHBH:"
          + ((Request) request.getMessage()).getHopByHopIdentifier() + "\nAppID:"
          + ((Request) request.getMessage()).getApplicationId());
      log.info("Request AVPS: \n");
      try {
        printAvps(((Request) request.getMessage()).getAvps());
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    try {
      // INITIAL_REQUEST 1, UPDATE_REQUEST 2, TERMINATION_REQUEST 3,
      // EVENT_REQUEST 4
      JCreditControlAnswer answer = null;
      switch (request.getRequestTypeAVPValue()) {
        case 1:

          JCreditControlAnswerImpl local = new JCreditControlAnswerImpl((Request) request.getMessage(), 2000);
          answer = local;
          AvpSet reqSet = request.getMessage().getAvps();

          AvpSet set = local.getMessage().getAvps();
          int c = 0;
          set.removeAvp(293);
          set.removeAvp(283);
          // set.removeAvp(296);
          // set.removeAvp(264);
          // set.addAvp(296,"mobicents.org", true);
          // set.addAvp(264,"aaa://192.168.1.103:3868", true);
          set.addAvp(reqSet.getAvp(416), reqSet.getAvp(415), reqSet.getAvp(258));

          if (answer != null) {

            if (log.isInfoEnabled()) {

              log.info("Created answer: " + answer.getCommandCode() + "\nE2E:"
                  + answer.getMessage().getEndToEndIdentifier() + "\nHBH:" + answer.getMessage().getHopByHopIdentifier()
                  + "\nAppID:" + answer.getMessage().getApplicationId());
              log.info("Answer AVPS: \n");
              try {
                printAvps(answer.getMessage().getAvps());
              }
              catch (AvpDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

            }

            session.sendCreditControlAnswer(answer);

          }

          break;
        case 2:

          local = new JCreditControlAnswerImpl((Request) request.getMessage(), 2000);
          answer = local;
          reqSet = request.getMessage().getAvps();

          set = local.getMessage().getAvps();
          c = 0;
          set.removeAvp(293);
          set.removeAvp(283);
          // set.removeAvp(296);
          // set.removeAvp(264);
          // set.addAvp(296,"mobicents.org", true);
          // set.addAvp(264,"aaa://192.168.1.103:3868", true);
          set.addAvp(reqSet.getAvp(416), reqSet.getAvp(415), reqSet.getAvp(258));

          if (answer != null) {
            if (log.isInfoEnabled()) {

              log.info("Created answer: " + answer.getCommandCode() + "\nE2E:"
                  + answer.getMessage().getEndToEndIdentifier() + "\nHBH:" + answer.getMessage().getHopByHopIdentifier()
                  + "\nAppID:" + answer.getMessage().getApplicationId());
              log.info("Answer AVPS: \n");
              try {
                printAvps(answer.getMessage().getAvps());
              }
              catch (AvpDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

            }
            session.sendCreditControlAnswer(answer);

          }

          break;
        case 3:

          local = new JCreditControlAnswerImpl((Request) request.getMessage(), 2000);
          answer = local;
          reqSet = request.getMessage().getAvps();

          set = local.getMessage().getAvps();
          c = 0;
          set.removeAvp(293);
          set.removeAvp(283);
          // set.removeAvp(296);
          // set.removeAvp(264);
          // set.addAvp(296,"mobicents.org", true);
          // set.addAvp(264,"aaa://192.168.1.103:3868", true);
          set.addAvp(reqSet.getAvp(416), reqSet.getAvp(415), reqSet.getAvp(258));

          if (answer != null) {
            if (log.isInfoEnabled()) {

              log.info("Created answer: " + answer.getCommandCode() + "\nE2E:"
                  + answer.getMessage().getEndToEndIdentifier() + "\nHBH:" + answer.getMessage().getHopByHopIdentifier()
                  + "\nAppID:" + answer.getMessage().getApplicationId());
              log.info("Answer AVPS: \n");
              try {
                printAvps(answer.getMessage().getAvps());
              }
              catch (AvpDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

            }
            session.sendCreditControlAnswer(answer);

          }

          break;
        case 4:

          local = new JCreditControlAnswerImpl((Request) request.getMessage(), 2000);
          answer = local;
          reqSet = request.getMessage().getAvps();

          set = local.getMessage().getAvps();
          c = 0;
          set.removeAvp(293);
          set.removeAvp(283);
          // set.removeAvp(296);
          // set.removeAvp(264);
          // set.addAvp(296,"mobicents.org", true);
          // set.addAvp(264,"aaa://192.168.1.103:3868", true);
          set.addAvp(reqSet.getAvp(416), reqSet.getAvp(415), reqSet.getAvp(258));

          if (answer != null) {
            if (log.isInfoEnabled()) {

              log.info("Created answer: " + answer.getCommandCode() + "\nE2E:"
                  + answer.getMessage().getEndToEndIdentifier() + "\nHBH:" + answer.getMessage().getHopByHopIdentifier()
                  + "\nAppID:" + answer.getMessage().getApplicationId());
              log.info("Answer AVPS: \n");
              try {
                printAvps(answer.getMessage().getAvps());
              }
              catch (AvpDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }

            }
            session.sendCreditControlAnswer(answer);
          }
          break;

        default:
          if (log.isEnabledFor(Level.ERROR)) {

            log.error("No REQ type present?: " + request.getRequestTypeAVPValue());

          }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  /*
   * (non-Javadoc)
   * @see
   * org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Object,
   * java.lang.Enum, java.lang.Enum)
   */
  public void stateChanged(AppSession source, Enum oldState, Enum newState) {
    this.stateChanged(oldState, newState);

  }

  public void stateChanged(Enum oldState, Enum newState) {
    if (log.isInfoEnabled()) {
      log.info("Diameter CCA SessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
    }

  }

  // ////////////////////
  // GENERIC HANDLERS //
  // ////////////////////

  public Answer processRequest(Request request) {

    if (request.getCommandCode() != 272) {
      if (super.log.isEnabledFor(Level.ERROR)) {
        // super.log.error("Received non CCR message, discarding: " +
        // toString(request));
        super.dumpMessage(request, false);
      }
      return null;
    }

    if (log.isInfoEnabled()) {
      log.info("===Received=== Request: " + request.getCommandCode() + "\nE2E:" + request.getEndToEndIdentifier()
          + "\nHBH:" + request.getHopByHopIdentifier() + "\nAppID:" + request.getApplicationId());
      log.info("Request AVPS: \n");
      try {
        printAvps(request.getAvps());
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
    try {
      ServerCCASessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
          ApplicationId.createByAuthAppId(0, 4), ServerCCASession.class, null);
      // session.
      session.addStateChangeNotification(this);
      session.processRequest(request);
    }
    catch (InternalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  public long[] getApplicationIds() {
    // FIXME: ???
    return new long[] { 4 };
  }

  public long getDefaultValidityTime() {
    return this.defaultValidityTime;
  }

  public JCreditControlAnswer createCreditControlAnswer(Answer answer) {
    return new JCreditControlAnswerImpl(answer);
  }

  public JCreditControlRequest createCreditControlRequest(Request req) {
    return new JCreditControlRequestImpl(req);
  }

  public ReAuthAnswer createReAuthAnswer(Answer answer) {

    return new ReAuthAnswerImpl(answer);
  }

  public ReAuthRequest createReAuthRequest(Request req) {

    return new ReAuthRequestImpl(req);
  }

  // /////////////////////
  // // CONTEXT METHODS // < - we dont care about them
  // /////////////////////

  public void sessionSupervisionTimerReStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Auto-generated method stub

  }

  public void sessionSupervisionTimerStarted(ServerCCASession session, ScheduledFuture future) {
    // TODO Auto-generated method stub

  }

  public void sessionSupervisionTimerStopped(ServerCCASession session, ScheduledFuture future) {
    // TODO Auto-generated method stub

  }

  public void timeoutExpired(Request request) {
    // FIXME ???

  }

  public void denyAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Auto-generated method stub

  }

  public void denyAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub

  }

  public int getDefaultCCFHValue() {

    return defaultCreditControlFailureHandling;
  }

  public int getDefaultDDFHValue() {

    return defaultDirectDebitingFailureHandling;
  }

  public long getDefaultTxTimerValue() {

    return defaultTxTimerValue;
  }

  public void grantAccessOnDeliverFailure(ClientCCASession clientCCASessionImpl, Message request) {
    // TODO Auto-generated method stub

  }

  public void grantAccessOnFailureMessage(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub

  }

  public void grantAccessOnTxExpire(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub

  }

  public void indicateServiceError(ClientCCASession clientCCASessionImpl) {
    // TODO Auto-generated method stub

  }

  public void receivedSuccessMessage(Request request, Answer answer) {

  }

  public void doCreditControlAnswer(ClientCCASession arg0, JCreditControlRequest arg1, JCreditControlAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doOtherEvent(AppSession arg0, AppRequestEvent arg1, AppAnswerEvent arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doReAuthRequest(ClientCCASession arg0, ReAuthRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doReAuthAnswer(ServerCCASession arg0, ReAuthRequest arg1, ReAuthAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void sessionSupervisionTimerExpired(ServerCCASession arg0) {
    // TODO Auto-generated method stub

  }

  public void denyAccessOnTxExpire(ClientCCASession arg0) {
    // TODO Auto-generated method stub

  }

  public void txTimerExpired(ClientCCASession arg0) {
    // TODO Auto-generated method stub

  }
}
