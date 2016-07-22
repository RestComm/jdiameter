package org.mobicents.tests.diameter.cxdx;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Network;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ClientCxDxSessionListener;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSessionListener;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.cxdx.CxDxClientSessionImpl;
import org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory;
import org.jdiameter.common.impl.app.cxdx.CxDxSessionFactoryImpl;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JLocationInfoRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JMultimediaAuthAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JMultimediaAuthRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JPushProfileRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JRegistrationTerminationRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentRequestImpl;
import org.jdiameter.common.impl.app.cxdx.JUserAuthorizationAnswerImpl;
import org.jdiameter.common.impl.app.cxdx.JUserAuthorizationRequestImpl;
import org.jdiameter.server.impl.app.cxdx.CxDxServerSessionImpl;
import org.mobicents.tests.diameter.AbstractStackRunner;

public class CXDX extends AbstractStackRunner implements ServerCxDxSessionListener, ClientCxDxSessionListener,
    StateChangeListener<AppSession>, ICxDxMessageFactory {

  private ApplicationId cxdxAuthApplicationId = ApplicationId.createByAuthAppId(10415, 16777216);
  private CxDxSessionFactoryImpl cxdxSessionFactory;

  public CXDX() {
    super();
    // TODO Auto-generated constructor stub
  }

  @Override
  public void configure(InputStream f) throws Exception {
    // TODO Auto-generated method stub
    super.configure(f);

    this.cxdxSessionFactory = new CxDxSessionFactoryImpl(super.factory);
    this.cxdxSessionFactory.setClientSessionListener(this);
    this.cxdxSessionFactory.setServerSessionListener(this);

    Network network = stack.unwrap(Network.class);
    network.addNetworkReqListener(this, cxdxAuthApplicationId);
    ((ISessionFactory) super.factory).registerAppFacory(ClientCxDxSession.class, cxdxSessionFactory);
    ((ISessionFactory) super.factory).registerAppFacory(ServerCxDxSession.class, cxdxSessionFactory);

  }

  public Answer processRequest(Request request) {

    // if we act as server, we will get UserAuthorizationRequest == 300
    int commandCode = request.getCommandCode();
    if (commandCode == 300) {
      // we act as server
      try {
        CxDxServerSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
            cxdxAuthApplicationId, ServerCxDxSession.class, null);
        session.addStateChangeNotification(this);
        session.processRequest(request);
        return null;
      }
      catch (InternalException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // Client RTR
    }
    else if (commandCode == 304) {
      try {
        CxDxClientSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
            cxdxAuthApplicationId, ClientCxDxSession.class, null);
        session.addStateChangeNotification(this);
        session.processRequest(request);
        return null;
      }
      catch (InternalException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return null;
  }

  public void receivedSuccessMessage(Request arg0, Answer arg1) {
    // we should not do that
    if (super.log.isEnabledFor(Level.ERROR)) {
      super.log.error("Received answer");
      dumpMessage(arg1, false);
    }

  }

  public void timeoutExpired(Request arg0) {
    if (super.log.isInfoEnabled()) {
      super.log.info("Timeout expired");
      dumpMessage(arg0, true);
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

  public void doLocationInformationRequest(ServerCxDxSession arg0, JLocationInfoRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doMultimediaAuthRequest(ServerCxDxSession arg0, JMultimediaAuthRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doOtherEvent(AppSession arg0, AppRequestEvent arg1, AppAnswerEvent arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doPushProfileAnswer(ServerCxDxSession arg0, JPushProfileRequest arg1, JPushProfileAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doRegistrationTerminationAnswer(ServerCxDxSession arg0, JRegistrationTerminationRequest arg1,
      JRegistrationTerminationAnswer arg2)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doServerAssignmentRequest(ServerCxDxSession arg0, JServerAssignmentRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doUserAuthorizationRequest(ServerCxDxSession appSession, JUserAuthorizationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    // we simply answer
    try {

      // create answer, we will do that always
      Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.cxdxAuthApplicationId);
      AvpSet set = answer.getAvps();

      // Auth-Session-State
      set.addAvp(277, 0);

      if (log.isDebugEnabled()) {
        log.info("Recievend UAR in App Session.");
        super.dumpMessage(request.getMessage(), false);
        log.info("Sending UAA in App Session.");
        super.dumpMessage(answer, true);
      }

      appSession.sendUserAuthorizationAnswer((JUserAuthorizationAnswer) this.createUserAuthorizationAnswer(answer));

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public long getApplicationId() {
    return this.cxdxAuthApplicationId.getAuthAppId();
  }

  public void doLocationInformationAnswer(ClientCxDxSession arg0, JLocationInfoRequest arg1, JLocationInfoAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doMultimediaAuthAnswer(ClientCxDxSession arg0, JMultimediaAuthRequest arg1, JMultimediaAuthAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doPushProfileRequest(ClientCxDxSession arg0, JPushProfileRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doRegistrationTerminationRequest(ClientCxDxSession appSession, JRegistrationTerminationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // we simply answer
    try {

      // create answer, we will do that always
      Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.cxdxAuthApplicationId);
      AvpSet set = answer.getAvps();

      // Auth-Session-State
      set.addAvp(277, 0);

      if (log.isDebugEnabled()) {
        log.info("Recievend RTR in App Session.");
        super.dumpMessage(request.getMessage(), false);
        log.info("Sending RTA in App Session.");
        super.dumpMessage(answer, true);
      }

      appSession.sendRegistrationTerminationAnswer(
          (JRegistrationTerminationAnswer) this.createRegistrationTerminationAnswer(answer));

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void doServerAssignmentAnswer(ClientCxDxSession arg0, JServerAssignmentRequest arg1,
      JServerAssignmentAnswer arg2)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doUserAuthorizationAnswer(ClientCxDxSession arg0, JUserAuthorizationRequest arg1,
      JUserAuthorizationAnswer arg2)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createLocationInfoAnswer(org.jdiameter.api.Answer)
   */
  public JLocationInfoAnswer createLocationInfoAnswer(Answer answer) {
    return new JLocationInfoAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createLocationInfoRequest(org.jdiameter.api.Request)
   */
  public JLocationInfoRequest createLocationInfoRequest(Request request) {
    return new JLocationInfoRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createMultimediaAuthAnswer(org.jdiameter.api.Answer)
   */
  public JMultimediaAuthAnswer createMultimediaAuthAnswer(Answer answer) {
    return new JMultimediaAuthAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createMultimediaAuthRequest(org.jdiameter.api.Request)
   */
  public JMultimediaAuthRequest createMultimediaAuthRequest(Request request) {
    return new JMultimediaAuthRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createPushProfileAnswer(org.jdiameter.api.Answer)
   */
  public JPushProfileAnswer createPushProfileAnswer(Answer answer) {
    return new JPushProfileAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createPushProfileRequest(org.jdiameter.api.Request)
   */
  public JPushProfileRequest createPushProfileRequest(Request request) {
    // TODO Auto-generated method stub
    return new JPushProfileRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createRegistrationTerminationAnswer(org.jdiameter.api.Answer)
   */
  public JRegistrationTerminationAnswer createRegistrationTerminationAnswer(Answer answer) {
    return new JRegistrationTerminationAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createRegistrationTerminationRequest(org.jdiameter.api.Request)
   */
  public JRegistrationTerminationRequest createRegistrationTerminationRequest(Request request) {
    return new JRegistrationTerminationRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createServerAssignmentAnswer(org.jdiameter.api.Answer)
   */
  public JServerAssignmentAnswer createServerAssignmentAnswer(Answer answer) {
    return new JServerAssignmentAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createServerAssignmentRequest(org.jdiameter.api.Request)
   */
  public JServerAssignmentRequest createServerAssignmentRequest(Request request) {
    return new JServerAssignmentRequestImpl(request);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createUserAuthorizationAnswer(org.jdiameter.api.Answer)
   */
  public JUserAuthorizationAnswer createUserAuthorizationAnswer(Answer answer) {
    return new JUserAuthorizationAnswerImpl(answer);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.cxdx.ICxDxMessageFactory#
   * createUserAuthorizationRequest(org.jdiameter.api.Request)
   */
  public JUserAuthorizationRequest createUserAuthorizationRequest(Request request) {
    return new JUserAuthorizationRequestImpl(request);
  }

}
