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
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.auth.ReAuthAnswerImpl;
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl;
import org.jdiameter.common.impl.app.cca.AppCCASessionImpl;
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl;

public class ServerCCASessionImpl extends AppCCASessionImpl implements ServerCCASession, NetworkReqListener,EventListener<Request, Answer> {

  //Is there ant state?
  protected boolean stateless=true;
  protected ServerCCASessionState state=ServerCCASessionState.IDLE;
  protected ICCAMessageFactory factory=null;
  protected String destHost, destRealm;
  protected Lock sendAndStateLock = new ReentrantLock();
  protected long[] authAppIds = new long[]{4};
  protected ServerCCASessionListener listener=null;
  protected IServerCCASessionContext context=null;
  protected ScheduledFuture tccFuture=null;

  public ServerCCASessionImpl(ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst)
  {
    this(null,fct,sf,lst);
  }

  public ServerCCASessionImpl(String sessionId, ICCAMessageFactory fct, SessionFactory sf, ServerCCASessionListener lst)
  {
    if (lst == null)
    {
      throw new IllegalArgumentException("Listener can not be null");
    }
    
    if (fct.getApplicationIds() == null)
    {
      throw new IllegalArgumentException("ApplicationId can not be less than zer0");
    }
    if(lst instanceof IServerCCASessionContext)
    {
      context = (IServerCCASessionContext)lst;
    }
    
    authAppIds = fct.getApplicationIds();
    listener = lst;
    factory = fct;
    
    try
    {
      if (sessionId == null)
      {
        session = sf.getNewSession();
      }
      else
      {
        session = sf.getNewSession(sessionId);
      }
      
      session.setRequestListener(this);
    }
    catch (InternalException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public void sendCreditControlAnswer(JCreditControlAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    //could be send but
    handleEvent(new Event(false,null,answer));
  }

  public void sendReAuthRequest(ReAuthRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    send(EventType.SENT_RAR, request, null);
  }

  public boolean isStateless()
  {
    return stateless;
  }

  public <E> E getState(Class<E> stateType)
  {
    return stateType == ServerCCASessionState.class ? (E) state : null;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException
  {
    ServerCCASessionState newState = null;
    try
    {
      sendAndStateLock.lock();
      //Can be null if there is no state transition, transition to IDLE state should terminate this app session

      Event localEvent = (Event) event;

      //Its kind of akward, but with two state on server side its easier to go through event types?
      //but for sake of FSM readability
      EventType eventType=(EventType) localEvent.getType();
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
          //We dont care about code, its always IDLE== terminate
          newState = ServerCCASessionState.IDLE;
          dispatchEvent(localEvent.getAnswer());
          break;
        case SENT_INITIAL_RESPONSE:
          JCreditControlAnswer answer=(JCreditControlAnswer) localEvent.getAnswer();
          try {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32()))
            {
              startTcc(answer.getValidityTimeAvp());
              newState = ServerCCASessionState.OPEN;
            }
            else
            {
              newState = ServerCCASessionState.IDLE;
            }
            dispatchEvent(localEvent.getAnswer());
          }
          catch (AvpDataException e) {
            throw new InternalException(e);
          }
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
          JCreditControlAnswer answer=(JCreditControlAnswer) localEvent.getAnswer();
          try {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32()))
            {
              startTcc(answer.getValidityTimeAvp());
            }
            else
            {
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
          try
          {
            if(isSuccess(answer.getResultCodeAvp().getUnsigned32()))
            {
              stopTcc(false);
            }
            else
            {
              //its a failure, we wait for Tcc to fire
            }
          }
          catch (AvpDataException e) {
            throw new InternalException(e);
          }
          finally
          {
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
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally
    {
      if(newState != null)
      {
        setState(newState);
      }
      
      sendAndStateLock.unlock();
    }

    return false;
  }

  public Answer processRequest(Request request)
  {
    try
    {
      if(isValidToServerMessage(request))
      {
        JCreditControlRequest jRequest=new JCreditControlRequestImpl(request);
        if(!jRequest.isRequestTypeAVPPresent())
        {

          return this.terminateSessionOnError(request,"Avp Request-Type MUST be present", 5005);
        }

        this.handleEvent(new Event(true,new JCreditControlRequestImpl(request),null));
      }
      else
      {
        listener.doOtherEvent(this, new AppRequestEventImpl(request), null);
      }
    }
    catch(Exception e)
    {
      logger.error("Failure processing Request.", e);
    }

    return null;
  }

  public void receivedSuccessMessage(Request request, Answer answer)
  {
    //Here is propably part for RAX
    if(!isValidResponseToServer(request,answer))
    {
      //FIXME: drop?
    }
    else
    {
      //RAA is the only response we receive, its the only one valid
      try
      {
        handleEvent(new Event(EventType.RECEIVED_RAA,factory.createReAuthRequest(request ),factory.createReAuthAnswer(answer)));
      }
      catch(Exception e)
      {
        logger.error("Error handling success message.", e);
      }
    }
  }

  public void timeoutExpired(Request request)
  {
    context.timeoutExpired(request);
    //FIXME: do more? Only timeout is for RAR
    //App should decide whats next
  }

  //for now we accept only CCR (272 request) 
  private boolean isValidToServerMessage(Request request)
  {
    return request.getCommandCode()== JCreditControlRequest.code;
  }

  private Answer terminateSessionOnError(Request r,String msg, long responseCode)
  {
    logger.error("Terminating session due to: "+msg);

    return r.createAnswer(responseCode);
  }

  private boolean isValidResponseToServer(Request request, Answer answer)
  {
    //FIXME: server CCA awaits only for RAA
    return answer.getCommandCode()==ReAuthAnswerImpl.code;
  }

  private void startTcc(Avp validityAvp)
  {
    long defaultValue = 2*context.getDefaultValidityTime();
    
    if(validityAvp!=null)
    {
      try
      {
        defaultValue = 2*validityAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        logger.error( "Error setting timer value, using default.", e );
      }
    }
    if(tccFuture != null)
    {
      stopTcc(true);
      tccFuture=super.scheduler.schedule(new TccScheduledTask(this),defaultValue,TimeUnit.SECONDS);
      context.sessionSupervisionTimerReStarted(this, tccFuture);
    }
    else
    {
      tccFuture=super.scheduler.schedule(new TccScheduledTask(this),defaultValue,TimeUnit.SECONDS);
    }
  }
  
  private void stopTcc(boolean willRestart)
  {
    if(tccFuture!=null)
    {
      tccFuture.cancel(true);
      ScheduledFuture f = tccFuture;
      tccFuture = null;
      
      if(!willRestart)
      {
        context.sessionSupervisionTimerStopped(this, f);
      }
    }
  }

  protected boolean isSuccess(long code)
  {
    return (code < 3000 && code >= 2000);
  }

  protected void setState(ServerCCASessionState newState)
  {
    setState(newState, true);
  }

  protected void setState(ServerCCASessionState newState, boolean release)
  {
    try
    {
      IAppSessionState oldState = state;
      state = newState;
      
      for (StateChangeListener i : stateListeners)
      {
        i.stateChanged((Enum) oldState, (Enum) newState);
      }
      
      if (newState == ServerCCASessionState.IDLE)
      {
        if (release)
        {
          this.release();
        }
        
        stopTcc(false);
      }
    }
    catch(Exception e)
    {
      logger.error("Error while setting CCA Server Session state: " + newState, e);
    }
  }

  private class TccScheduledTask implements Runnable
  {
    ServerCCASession session=null;

    private TccScheduledTask(ServerCCASession session)
    {
      super();
      this.session = session;
    }

    public void run()
    {
      try
      {
        context.sessionSupervisionTimerExpired(session);
      }
      catch(Exception e)
      {
        logger.error( "Error while setting supervision timer expired.", e );
      }
      
      try
      {
        sendAndStateLock.lock();
        tccFuture=null;
        setState(ServerCCASessionState.IDLE);
      }
      finally
      {
        sendAndStateLock.unlock();
      }
    }
  }

  protected void send(EventType type, AppRequestEvent request, AppAnswerEvent answer) throws InternalException
  {
    try {

      //FIXME: isnt this bad? Shouldnt send be before state change?
      sendAndStateLock.lock();
      
      if (type != null)
      {
        handleEvent(new Event(type, request, answer));
      }
      
      //AppEvent event = null;
      //if (request != null) {
      //	event = request;
      //} else {
      //	event = answer;
      //}
      //session.send(event.getMessage(), this);
      // Store last destinmation information
      //destRealm = event.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
      //destHost = event.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString();
    }
    catch (Exception exc)
    {
      throw new InternalException(exc);
    }
    finally
    {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException
  {
    try
    {
      session.send(event.getMessage(), this);
      // Store last destinmation information
      destRealm = event.getMessage().getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
      destHost = event.getMessage().getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString();
    }
    catch(Exception e)
    {
      throw new InternalException(e);
    }
  }
}
