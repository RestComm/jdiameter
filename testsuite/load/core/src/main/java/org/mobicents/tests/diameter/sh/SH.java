package org.mobicents.tests.diameter.sh;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
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
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.ServerShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.ShSessionFactoryImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;
import org.mobicents.tests.diameter.AbstractStackRunner;

public class SH extends AbstractStackRunner
    implements ServerShSessionListener, ClientShSessionListener, StateChangeListener<AppSession>, IShMessageFactory {

  private ApplicationId shAuthApplicationId = ApplicationId.createByAuthAppId(10415, 16777217);
  private ShSessionFactoryImpl shSessionFactory;

  public SH() {
    super();
    // TODO Auto-generated constructor stub
  }

  @Override
  public void configure(InputStream f) throws Exception {
    // TODO Auto-generated method stub
    super.configure(f);

    this.shSessionFactory = new ShSessionFactoryImpl(super.factory);
    this.shSessionFactory.setClientShSessionListener(this);
    this.shSessionFactory.setServerShSessionListener(this);

    Network network = stack.unwrap(Network.class);
    network.addNetworkReqListener(this, shAuthApplicationId);
    ((ISessionFactory) super.factory).registerAppFacory(ServerShSession.class, this.shSessionFactory);
    ((ISessionFactory) super.factory).registerAppFacory(ClientShSession.class, this.shSessionFactory);
  }

  public Answer processRequest(Request request) {

    int commandCode = request.getCommandCode();
    if (commandCode != 308 && commandCode != 306 && commandCode != 309) {
      if (log.isEnabledFor(Level.ERROR)) {
        log.error("Received command with wrong code: " + commandCode);
        super.dumpMessage(request, false);
      }
      return null;
    }

    if (commandCode == 308 || commandCode == 306) {
      try {
        ShServerSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
            shAuthApplicationId, ServerShSession.class, null);
        // session.
        session.addStateChangeNotification(this);
        session.processRequest(request);
      }
      catch (InternalException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    else {
      try {
        ShClientSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
            shAuthApplicationId, ClientShSession.class, null);
        // session.
        session.processRequest(request);
        session.addStateChangeNotification(this);
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
      new Exception().printStackTrace();
    }

  }

  public void timeoutExpired(Request arg0) {
    if (super.log.isInfoEnabled()) {
      super.log.info("Timeout expired");
      dumpMessage(arg0, true);
    }

  }

  public void doOtherEvent(AppSession arg0, AppRequestEvent arg1, AppAnswerEvent arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doProfileUpdateRequestEvent(ServerShSession arg0, ProfileUpdateRequest arg1)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doPushNotificationAnswerEvent(ServerShSession arg0, PushNotificationRequest arg1,
      PushNotificationAnswer arg2)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {

    if (log.isEnabledFor(Level.DEBUG)) {
      log.error("Received PNA");
      super.dumpMessage(arg1.getMessage(), false);
    }
  }

  public void doSubscribeNotificationsRequestEvent(ServerShSession appSession, SubscribeNotificationsRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {

      // create answer, we will do that always
      Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.shAuthApplicationId);
      AvpSet set = answer.getAvps();

      // Auth-Session-State
      set.addAvp(277, 0);

      if (log.isDebugEnabled()) {
        log.info("Recievend SNR in App Session.");
        super.dumpMessage(request.getMessage(), false);
        log.info("Sending SNA in App Session.");
        super.dumpMessage(answer, true);
      }

      appSession.sendSubscribeNotificationsAnswer(
          (SubscribeNotificationsAnswer) this.createSubscribeNotificationsAnswer(answer));

      // if we have subscribe, we need to send PNR
      set = request.getMessage().getAvps();
      Avp a = set.getAvp(705, 10415L);
      if (a == null) {
        if (log.isEnabledFor(Level.ERROR)) {
          log.error("No subs req type!!");
        }
        return;
      }
      int v = -1;
      try {
        v = a.getInteger32();
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (v == 0) {
        // other side waits for PNR
        try {
          Request pnrRaw = appSession.getSessions().get(0).createRequest(309, this.shAuthApplicationId,
              request.getOriginRealm(), request.getOriginHost());
          set = pnrRaw.getAvps();

          // Auth-Session-State
          set.addAvp(277, 0);
          // User-Identity
          set.addAvp(request.getMessage().getAvps().getAvp(700, 10415L));

          // User-Data
          set.addAvp(702, "XXXXXXXX", 10415L, true, false, true);
          appSession.sendPushNotificationRequest((PushNotificationRequest) this.createPushNotificationRequest(pnrRaw));
        }
        catch (AvpDataException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doUserDataRequestEvent(ServerShSession appSession, UserDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {

      // create answer, we will do that always
      Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.shAuthApplicationId);
      AvpSet set = answer.getAvps();

      // Auth-Session-State
      set.addAvp(277, 0);

      if (log.isDebugEnabled()) {
        log.info("Recievend UDR in App Session.");
        super.dumpMessage(request.getMessage(), false);
        log.info("Sending UDA in App Session.");
        super.dumpMessage(answer, true);
      }

      appSession.sendUserDataAnswer((UserDataAnswer) this.createUserDataAnswer(answer));

    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }

  // //////////////////
  // Client methods //
  // //////////////////
  public void doProfileUpdateAnswerEvent(ClientShSession arg0, ProfileUpdateRequest arg1, ProfileUpdateAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doPushNotificationRequestEvent(ClientShSession appSession, PushNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {

      // create answer, we will do that always
      Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.shAuthApplicationId);
      AvpSet set = answer.getAvps();

      // Auth-Session-State
      set.addAvp(277, 0);

      if (log.isDebugEnabled()) {
        log.info("Recievend PNR in App Session.");
        super.dumpMessage(request.getMessage(), false);
        log.info("Sending PNA in App Session.");
        super.dumpMessage(answer, true);
      }

      appSession.sendPushNotificationAnswer((PushNotificationAnswer) this.createPushNotificationAnswer(answer));

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doSubscribeNotificationsAnswerEvent(ClientShSession arg0, SubscribeNotificationsRequest arg1,
      SubscribeNotificationsAnswer arg2)
          throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void doUserDataAnswerEvent(ClientShSession arg0, UserDataRequest arg1, UserDataAnswer arg2)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    // TODO Auto-generated method stub

  }

  public void stateChanged(Enum arg0, Enum arg1) {
    if (log.isDebugEnabled()) {
      log.debug("State changed from[" + arg0 + "] to[" + arg1 + "]");

    }

  }

  public void stateChanged(AppSession source, Enum arg0, Enum arg1) {
    this.stateChanged(arg0, arg1);
  }

  public AppAnswerEvent createProfileUpdateAnswer(Answer answer) {
    return new ProfileUpdateAnswerImpl(answer);
  }

  public AppRequestEvent createProfileUpdateRequest(Request request) {
    return new ProfileUpdateRequestImpl(request);
  }

  public AppAnswerEvent createPushNotificationAnswer(Answer answer) {
    return new PushNotificationAnswerImpl(answer);
  }

  public AppRequestEvent createPushNotificationRequest(Request request) {
    return new PushNotificationRequestImpl(request);
  }

  public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer) {
    return new SubscribeNotificationsAnswerImpl(answer);
  }

  public AppRequestEvent createSubscribeNotificationsRequest(Request request) {
    return new SubscribeNotificationsRequestImpl(request);
  }

  public AppAnswerEvent createUserDataAnswer(Answer answer) {
    return new UserDataAnswerImpl(answer);
  }

  public AppRequestEvent createUserDataRequest(Request request) {
    return new UserDataRequestImpl(request);
  }

  public long getApplicationId() {
    return this.shAuthApplicationId.getAuthAppId();
  }

  public long getMessageTimeout() {
    // TODO Auto-generated method stub
    return 5000;
  }

}
