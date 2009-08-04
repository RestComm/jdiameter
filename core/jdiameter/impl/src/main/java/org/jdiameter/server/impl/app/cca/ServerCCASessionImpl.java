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
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
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
import org.jdiameter.common.impl.app.acc.AccountAnswerImpl;
import org.jdiameter.common.impl.app.acc.AccountRequestImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionAnswerImpl;
import org.jdiameter.common.impl.app.auth.AbortSessionRequestImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.auth.SessionTermAnswerImpl;
import org.jdiameter.common.impl.app.auth.SessionTermRequestImpl;
import org.jdiameter.common.impl.app.cca.AppCCASessionImpl;

public class ServerCCASessionImpl extends AppCCASessionImpl implements ServerCCASession, NetworkReqListener,EventListener<Request, Answer> {

  private static final long serialVersionUID = 1L;

  //Is there any state?
  protected boolean stateless = true;
  protected ServerCCASessionState state = ServerCCASessionState.IDLE;
  protected ICCAMessageFactory factory = null;
  protected String originHost, originRealm;
  protected Lock sendAndStateLock = new ReentrantLock();
  protected long[] authAppIds = new long[]{4};
  protected ServerCCASessionListener listener = null;
  protected IServerCCASessionContext context = null;
  protected ScheduledFuture tccFuture = null;

  public ServerCCASessionImpl(ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst) {
    this(null,fct,sf,lst);
  }

  public ServerCCASessionImpl(String sessionId, ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst) {
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
      if (sessionId == null) {
        session = sf.getNewSession();
      }
      else {
        session = sf.getNewSession(sessionId);
      }
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public void sendCreditControlAnswer(JCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    //could be send but
    handleEvent(new Event(false,null,answer));
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

      //Can be null if there is no state transition, transition to IDLE state should terminate this app session
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
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;
          //Do nothing, only deliver

        case SENT_EVENT_RESPONSE:
          //We dont care about code, its always IDLE == terminate
          newState = ServerCCASessionState.IDLE;
          dispatchEvent(localEvent.getAnswer());
          break;
        case SENT_INITIAL_RESPONSE:
          JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
          try {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
              startTcc(answer.getValidityTimeAvp());
              newState = ServerCCASessionState.OPEN;
            }
            else if(isProvisional(answer.getResultCodeAvp().getUnsigned32())) {
              //
            }
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
          throw new InternalException("Wrong state: "+ServerCCASessionState.IDLE+" one event: "+eventType+" "+localEvent.getRequest()+" "+localEvent.getAnswer());
        }

      case OPEN:
        switch(eventType)
        {
        case RECEIVED_UPDATE:
          listener.doCreditControlRequest(this, (JCreditControlRequest)localEvent.getRequest());
          break;
          //Do nothing, deliver
        case SENT_UPDATE_RESPONSE:
          JCreditControlAnswer answer = (JCreditControlAnswer) localEvent.getAnswer();
          try {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
              startTcc(answer.getValidityTimeAvp());
            }
            else {
              //its a failure, we wait for Tcc to fire
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
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32())) {
              stopTcc(false);
            }
            else {
              //its a failure, we wait for Tcc to fire
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
          listener.doReAuthAnswer(this, new ReAuthRequestImpl(localEvent.getRequest().getMessage()),new ReAuthAnswerImpl(localEvent.getAnswer().getMessage()));
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

  public Answer processRequest(Request request) {

    try{
      //FIXME: baranowb: add message validation here!!!
      //We handle CCR,STR,ACR,ASR other go into extension
      switch(request.getCommandCode())
      {
      case JCreditControlAnswer.code:
        this.handleEvent(new Event(true,factory.createCreditControlRequest(request),null));
        break;

        //All other go straight to listner, they dont change state machine
        //Suprisingly there is no factory.... ech
        //FIXME: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
      case SessionTermAnswer.code:
        listener.doSessionTerminationRequest(this, new SessionTermRequestImpl(request));
        break;
      case AbortSessionAnswer.code:
        listener.doAbortSessionRequest(this, new AbortSessionRequestImpl(request));
        break;
      case AccountAnswer.code:
        listener.doAccountingRequest(this, new AccountRequestImpl(request));
        break;

      default:
        listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
      break;
      }
    }
    catch(Exception e) {
      logger.debug("Failed to process request message", e);
    }

    return null;
  }


  public void receivedSuccessMessage(Request request, Answer answer) {
    try{
      //FIXME: baranowb: add message validation here!!!
      //We handle CCR,STR,ACR,ASR other go into extension
      switch(request.getCommandCode())
      {
      case ReAuthRequest.code:
        handleEvent(new Event(Event.Type.RECEIVED_RAA,factory.createReAuthRequest(request ),factory.createReAuthAnswer(answer)));
        break;

        //All other go straight to listner, they dont change state machine
        //Suprisingly there is no factory.... ech
        //FIXME: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
      case SessionTermAnswer.code:
        listener.doSessionTerminationAnswer(this,null ,new SessionTermAnswerImpl(answer));
        break;
      case AbortSessionAnswer.code:
        listener.doAbortSessionAnswer(this,null, new AbortSessionAnswerImpl(answer));
        break;
      case AccountAnswer.code:
        listener.doAccountingAnswer(this,null, new AccountAnswerImpl(answer));
        break;

      default:
        listener.doOtherEvent(this, null, new AppAnswerEventImpl(answer));
      break;
      }

    }
    catch(Exception e) {
      logger.debug("Failed to process success message", e);
    }
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
      tccFuture = scheduler.schedule(new TccScheduledTask(this),defaultValue,TimeUnit.SECONDS);
      context.sessionSupervisionTimerReStarted(this, tccFuture);
    }
    else {
      tccFuture = scheduler.schedule(new TccScheduledTask(this),defaultValue,TimeUnit.SECONDS);
    }
  }

  private void stopTcc(boolean willRestart)
  {
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
    }
    this.listener = null;
    this.factory = null;
  }

  private class TccScheduledTask implements Runnable
  {
    ServerCCASession session = null;

    private TccScheduledTask(ServerCCASession session) {
      super();
      this.session = session;
    }

    public void run() {
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

  protected void send(Event.Type type, AppRequestEvent request, AppAnswerEvent answer) throws InternalException {
    try {
      //FIXME: isnt this bad? Shouldnt send be before state change?
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

  protected void dispatchEvent(AppEvent event) throws InternalException
  {
    try{
      session.send(event.getMessage(), this);
      // Store last destinmation information
      //FIXME: add differentation on server/client request
      originRealm = event.getMessage().getAvps().getAvp(Avp.ORIGIN_REALM).getOctetString();
      originHost = event.getMessage().getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString();
    }
    catch(Exception e) {
      //throw new InternalException(e);
      logger.debug("Failure trying to dispatch event", e);
    }
  }
}
