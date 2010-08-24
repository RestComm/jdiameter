/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.server.impl.app.sh;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
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
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.IShSessionFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.sh.ShSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of ShServerSession - can be one time - for UDR, PUR and
 * constant for SNR-PNR pair, in case when SNA contains response code from range
 * different than 2001-2004(success codes) user is responsible for maintaing
 * state - releasing etc, same goes if result code is contained
 * Experimental-Result AVP <br>
 * If ShSession moves to ShSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * 
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShServerSessionImpl extends ShSession implements ServerShSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  private Logger logger = LoggerFactory.getLogger(ShServerSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();
  protected boolean receivedSubTerm = false;

  // Factories and Listeners --------------------------------------------------
  protected transient IShMessageFactory factory = null;
  protected transient ServerShSessionListener listener;

  protected String destHost, destRealm;
  protected long appId = -1;

  public ShServerSessionImpl(IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public ShServerSessionImpl(String sessionId, IShMessageFactory fct, SessionFactory sf, ServerShSessionListener lst) {
    super(sf,sessionId);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    appId = fct.getApplicationId();
    listener = lst;
    factory = fct;
    //    try {
    //      if (sessionId == null) {
    //        session = sf.getNewSession();
    //      }
    //      else {
    //        session = sf.getNewSession(sessionId);
    //      }
    //      session.setRequestListener(this);
    //    }
    //    catch (InternalException e) {
    //      throw new IllegalArgumentException(e);
    //    }
  }

  public void sendProfileUpdateAnswer(ProfileUpdateAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PROFILE_UPDATE_ANSWER, null, answer);
  }

  public void sendPushNotificationRequest(PushNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PUSH_NOTIFICATION_REQUEST, request, null);
  }

  public void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER, null, answer);
  }

  public void sendUserDataAnswer(UserDataAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_USER_DATA_ANSWER, null, answer);
  }

  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  public void timeoutExpired(Request request) {
    try {
      sendAndStateLock.lock();
      if (request.getApplicationId() == appId) {
        if (request.getCommandCode() == PushNotificationRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createPushNotificationRequest(request), null));
          return;
        } 
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  public Answer processRequest(Request request) {
    RequestDelivery rd = new RequestDelivery();
    rd.session = this;
    rd.request = request;
    super.scheduler.execute(rd);
    return null;
  }

  @SuppressWarnings("unchecked")
  public <E> E getState(Class<E> stateType) {
    return null;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      Event localEvent = (Event) event;
      switch ((Event.Type) localEvent.getType()) {
      case RECEIVE_PROFILE_UPDATE_REQUEST:
        listener.doProfileUpdateRequestEvent(this, (ProfileUpdateRequest) localEvent.getRequest());
        break;
      case RECEIVE_PUSH_NOTIFICATION_ANSWER:
        listener.doPushNotificationAnswerEvent(this, (PushNotificationRequest) localEvent.getRequest(), (PushNotificationAnswer) localEvent.getAnswer());
        break;
      case RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST:
        listener.doSubscribeNotificationsRequestEvent(this, (SubscribeNotificationsRequest) localEvent.getRequest());
        break;
      case RECEIVE_USER_DATA_REQUEST:
        listener.doUserDataRequestEvent(this, (UserDataRequest) localEvent.getRequest());
        break;
      case SEND_PROFILE_UPDATE_ANSWER:
        dispatchEvent(localEvent.getAnswer());
        break;
      case SEND_PUSH_NOTIFICATION_REQUEST:
        dispatchEvent(localEvent.getRequest());
        break;
      case SEND_SUBSCRIBE_NOTIFICATIONS_ANSWER:
        dispatchEvent(localEvent.getAnswer());
        break;
      case SEND_USER_DATA_ANSWER:
        dispatchEvent(localEvent.getAnswer());
        break;
      case TIMEOUT_EXPIRES:
        break;
      default:
        logger.error("Wrong message type = {} req = {} ans = {}", new Object[]{localEvent.getType(), localEvent.getRequest(), localEvent.getAnswer()});
      }
    }
    catch (IllegalDiameterStateException idse) {
      throw new InternalException(idse);
    }
    catch (RouteException re) {
      throw new InternalException(re);
    }
    finally {
      sendAndStateLock.unlock();
    }

    return true;
  }

  public boolean isStateless() {
    return true;
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      //FIXME: isnt this bad? Shouldnt send be before state change?
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }
    }
    catch (Exception exc) {
      throw new InternalException(exc);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected void dispatchEvent(AppEvent event) throws InternalException {
    try{
      session.send(event.getMessage(), this);
      // FIXME: add differentiation on server/client request
    }
    catch(Exception e) {
      logger.debug("Failed to dispatch event", e);
    }
  }

  @SuppressWarnings("unchecked")
  public void release() {
    try {
      sendAndStateLock.lock();
      
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
    catch (Exception e) {
      logger.debug("Failed to release session", e);
    }
    finally {
      sendAndStateLock.unlock();
    }
  }

  protected long extractExpiryTime(Message answer) {
    try {
      // FIXME: Replace 709 by Avp.EXPIRY_TIME
      Avp expiryTimeAvp = answer.getAvps().getAvp(709);
      return expiryTimeAvp != null ? expiryTimeAvp.getTime().getTime() : -1;
    }
    catch (AvpDataException ade) {
      logger.debug("Failure trying to extract Expiry-Time AVP value", ade);
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.sh.ShSessionImpl#relink(org.jdiameter.client.api.IContainer)
   */
  @Override
  public void relink(IContainer stack) {
    if (super.sf == null) {
      super.relink(stack);
      IShSessionFactory fct = (IShSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(ServerShSession.class);
      this.listener = fct.getServerShSessionListener();
      this.factory = fct.getMessageFactory();
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (appId ^ (appId >>> 32));
    result = prime * result + ((destHost == null) ? 0 : destHost.hashCode());
    result = prime * result + ((destRealm == null) ? 0 : destRealm.hashCode());
    result = prime * result + (receivedSubTerm ? 1231 : 1237);
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    ShServerSessionImpl other = (ShServerSessionImpl) obj;
    if (appId != other.appId) {
      return false;
    }
    if (destHost == null) {
      if (other.destHost != null)
        return false;
    }
    else if (!destHost.equals(other.destHost)) {
      return false;
    }
    if (destRealm == null) {
      if (other.destRealm != null) {
        return false;
      }
    }
    else if (!destRealm.equals(other.destRealm)) {
      return false;
    }
    if (receivedSubTerm != other.receivedSubTerm) {
      return false;
    }

    return true;
  }

  private class RequestDelivery implements Runnable {
    ServerShSession session;
    Request request;

    public void run() {
      try {
        if (request.getApplicationId() == appId) {
          if (request.getCommandCode() == SubscribeNotificationsRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_REQUEST, factory.createSubscribeNotificationsRequest(request), null));
          }
          else if(request.getCommandCode() == UserDataRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_USER_DATA_REQUEST, factory.createUserDataRequest(request), null));
          }
          else if(request.getCommandCode() == ProfileUpdateRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PROFILE_UPDATE_REQUEST, factory.createProfileUpdateRequest(request), null));
          }
          else {
            listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
          }
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process request message", e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ServerShSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getApplicationId() == appId) {
          if (request.getCommandCode() == PushNotificationRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PUSH_NOTIFICATION_ANSWER, factory.createPushNotificationRequest(request), factory.createPushNotificationAnswer(answer)));
            return;
          }
          else {
            listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
          }
        }
        else {
          logger.warn("Message with Application-Id {} reached Application Session with Application-Id {}. Skipping.", request.getApplicationId(), appId);
        }
      }
      catch (Exception e) {
        logger.debug("Failed to process success message", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
  }

}
