package org.jdiameter.server.impl.app.cca;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.ServerCCASessionListener;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.common.api.app.IAppSessionState;
import org.jdiameter.common.api.app.cca.ICCAMessageFactory;
import org.jdiameter.common.api.app.cca.IServerCCASessionContext;
import org.jdiameter.common.api.app.cca.ServerCCASessionState;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.AppCCASessionImpl;

public class ServerCCASessionImpl extends AppCCASessionImpl implements ServerCCASession, NetworkReqListener, EventListener<Request, Answer> {

  private static final long serialVersionUID = 1L;

  // Session State Handling ---------------------------------------------------
  protected boolean stateless = true;
  protected ServerCCASessionState state = ServerCCASessionState.IDLE;
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected ICCAMessageFactory factory = null;
  protected IServerCCASessionContext context = null;
  protected ServerCCASessionListener listener = null;

  //  Tcc timer (supervises an ongoing credit-control
  //             session in the credit-control server) ------------------------
  protected ScheduledFuture tccFuture = null;

  protected long[] authAppIds = new long[]{4};
  protected String originHost, originRealm;

  public ServerCCASessionImpl(ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst) {
    this(null, fct, sf, lst);
  }

  public ServerCCASessionImpl(String sessionId, ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst) {
    super(sf);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationIds() == null) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    if(lst instanceof IServerCCASessionContext) {
      context = (IServerCCASessionContext)lst;
    }
    authAppIds = fct.getApplicationIds();
    listener = lst;
    factory = fct;
    try {
      session = sessionId == null ? sf.getNewSession() : sf.getNewSession(sessionId);
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public void sendCreditControlAnswer(JCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    handleEvent(new Event(false, null, answer));
  }

  public void sendReAuthRequest(ReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SENT_RAR, request, null);
  }

  public boolean isStateless() {
    return stateless;
  }

  public <E> E getState(Class<E> stateType) {
    return stateType == ServerCCASessionState.class ? (E) state : null;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    ServerCCASessionState newState = null;

    try {
      sendAndStateLock.lock();

      // Can be null if there is no state transition, transition to IDLE state should terminate this app session
      Event localEvent = (Event) event;

      //Its kind of akward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      Event.Type eventType = (Event.Type) localEvent.getType();
      switch(state)
      {
      case IDLE:
        switch(eventType)
        {
        case RECEIVED_INITIAL:
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;

        case RECEIVED_EVENT:
          // Current State: IDLE
          // Event: CC event request received and successfully processed
          // Action: Send CC event answer
          // New State: IDLE
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;

        case SENT_EVENT_RESPONSE:
          // Current State: IDLE
          // Event: CC event request received and successfully processed
          // Action: Send CC event answer
          // New State: IDLE

          // Current State: IDLE
          // Event: CC event request received but not successfully processed
          // Action: Send CC event answer with Result-Code != SUCCESS
          // New State: IDLE
          newState = ServerCCASessionState.IDLE;
          dispatchEvent(localEvent.getAnswer());
          break;

        case SENT_INITIAL_RESPONSE:
          JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
          try {
            long resultCode = answer.getResultCodeAvp().getUnsigned32();
            // Current State: IDLE
            // Event: CC initial request received and successfully processed
            // Action: Send CC initial answer, reserve units, start Tcc
            // New State: OPEN
            if(isSuccess(resultCode)) {
              startTcc(answer.getValidityTimeAvp());
              newState = ServerCCASessionState.OPEN;
            }
            // Current State: IDLE
            // Event: CC initial request received but not successfully processed
            // Action: Send CC initial answer with Result-Code != SUCCESS
            // New State: IDLE
            else {
              newState = ServerCCASessionState.IDLE;
            }
            dispatchEvent(localEvent.getAnswer());
          }
          catch (AvpDataException e) {
            throw new InternalException(e);
          }
          break;
        default:
          throw new InternalException("Wrong state: " + ServerCCASessionState.IDLE + " one event: " + eventType + " " + localEvent.getRequest() + " " + localEvent.getAnswer());
        }

      case OPEN:
        switch(eventType)
        {
        case RECEIVED_UPDATE:
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;

        case SENT_UPDATE_RESPONSE:
          JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
          try {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
              // Current State: OPEN
              // Event: CC update request received and successfully processed
              // Action: Send CC update answer, debit used units, reserve new units, restart Tcc
              // New State: OPEN
              startTcc(answer.getValidityTimeAvp());
            }
            else {
              // Current State: OPEN
              // Event: CC update request received but not successfully processed
              // Action: Send CC update answer with Result-Code != SUCCESS, debit used units
              // New State: IDLE

              // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
            }
          }
          catch (AvpDataException e) {
            throw new InternalException(e);
          }
          dispatchEvent(localEvent.getAnswer());
          break;
        case RECEIVED_TERMINATE:
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;
        case SENT_TERMINATE_RESPONSE:
          answer = (JCreditControlAnswer) localEvent.getAnswer();
          try {
            // Current State: OPEN
            // Event: CC termination request received and successfully processed
            // Action: Send CC termination answer, Stop Tcc, debit used units
            // New State: IDLE
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
              stopTcc(false);
            }
            else {
              // Current State: OPEN
              // Event: CC termination request received but not successfully processed
              // Action: Send CC termination answer with Result-Code != SUCCESS, debit used units
              // New State: IDLE

              // It's a failure, we wait for Tcc to fire -- FIXME: Alexandre: Should we?
            }
          }
          catch (AvpDataException e) {
            throw new InternalException(e);
          }
          finally {
            newState = ServerCCASessionState.IDLE;
          }
          dispatchEvent(localEvent.getAnswer());
          break;

        case RECEIVED_RAA:
          listener.doReAuthAnswer(this, new ReAuthRequestImpl(localEvent.getRequest().getMessage()), new ReAuthAnswerImpl(localEvent.getAnswer().getMessage()));
          break;
        case SENT_RAR:
          dispatchEvent(localEvent.getAnswer());
          break;
        }
      }
      return true;
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      if(newState != null) {
        setState(newState);
      }
      sendAndStateLock.unlock();
    }
  }

  private class TccScheduledTask implements Runnable {
    ServerCCASession session = null;

    private TccScheduledTask(ServerCCASession session) {
      super();
      this.session = session;
    }

    public void run() {
      // Current State: OPEN
      // Event: Session supervision timer Tcc expired
      // Action: Release reserved units
      // New State: IDLE
      context.sessionSupervisionTimerExpired(session);
      try {
        sendAndStateLock.lock();
        tccFuture = null;
        setState(ServerCCASessionState.IDLE);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }


  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  public void timeoutExpired(Request request) {
    context.timeoutExpired(request);
    //FIXME: Should we release ?
  }

  private void startTcc(Avp validityAvp) {
    long defaultValue = 2 * context.getDefaultValidityTime();

    if(validityAvp != null) {
      try {
        defaultValue = 2 * validityAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        logger.debug("Unable to retrieve Validity-Time AVP value, using default.", e);
      }
    }
    if(tccFuture != null) {
      stopTcc(true);
      tccFuture = super.scheduler.schedule(new TccScheduledTask(this), defaultValue, TimeUnit.SECONDS);
      context.sessionSupervisionTimerReStarted(this, tccFuture);
    }
    else {
      tccFuture = super.scheduler.schedule(new TccScheduledTask(this), defaultValue, TimeUnit.SECONDS);
    }
  }

  private void stopTcc(boolean willRestart) {
    if(tccFuture != null) {
      tccFuture.cancel(false);
      ScheduledFuture f = tccFuture;
      tccFuture = null;
      if(!willRestart) {
        context.sessionSupervisionTimerStopped(this, f);
      }
    }
  }

  protected  boolean isProvisional(long resultCode) {
    return resultCode >= 1000 && resultCode < 2000;
  }

  protected boolean isSuccess(long resultCode) {
    return resultCode >= 2000 && resultCode < 3000;
  }

  protected void setState(ServerCCASessionState newState) {
    setState(newState, true);
  }

  protected void setState(ServerCCASessionState newState, boolean release) {
    IAppSessionState oldState = state;
    state = newState;
    for (StateChangeListener i : stateListeners) {
      i.stateChanged((Enum) oldState, (Enum) newState);
    }
    if (newState == ServerCCASessionState.IDLE) {
      if (release) {
        this.release();
      }
      stopTcc(false);
    }
  }

  @Override
  public void release() {
    this.stopTcc(false);

    if(super.isValid()) {
      super.release();
    }

    if(super.session != null) {
      super.session.setRequestListener(null);
    }

    this.session = null;

    if(listener != null) {
      this.removeStateChangeNotification((StateChangeListener) listener);
      this.listener = null;
    }

    this.factory = null;
  }

  protected void send(Event.Type type, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException {
    try{
      session.send(event.getMessage(), this);
      // Store last destination information
      // FIXME: add differentiation on server/client request
      originRealm = event.getMessage().getAvps().getAvp(Avp.ORIGIN_REALM).getOctetString();
      originHost = event.getMessage().getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString();
    }
    catch(Exception e) {
      //throw new InternalException(e);
      logger.debug("Failure trying to dispatch event", e);
    }
  }

  private class RequestDelivery implements Runnable {
    ServerCCASession session;
    Request request;

    public void run() {
      try {
        switch (request.getCommandCode()) {
        case JCreditControlAnswer.code:
          handleEvent(new Event(true, factory.createCreditControlRequest(request), null));
          break;

        default:
          listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
          break;
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ServerCCASession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        // FIXME: baranowb: add message validation here!!!
        // We handle CCR, STR, ACR, ASR other go into extension
        switch (request.getCommandCode()) {
        case ReAuthRequest.code:
          handleEvent(new Event(Event.Type.RECEIVED_RAA, factory.createReAuthRequest(request), factory.createReAuthAnswer(answer)));
          break;
        default:
          listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
          break;
        }

      }
      catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
    }
  }

}
