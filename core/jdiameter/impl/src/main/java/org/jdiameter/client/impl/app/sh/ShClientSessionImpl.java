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
package org.jdiameter.client.impl.app.sh;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
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
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.api.app.sh.IShSessionFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.ShSession;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of ShClientSession - can be one time - for UDR,PUR and
 * constant for SNR-PNR pair, in case when SNA contains response code from range
 * different than 2001-2004(success codes) user is responsible for maintaing
 * state - releasing etc, same goes if result code is contained
 * Experimental-Result AVP <br>
 * If ShSession moves to ShSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShClientSessionImpl extends ShSession implements ClientShSession, EventListener<Request, Answer>, NetworkReqListener {

  private static final long serialVersionUID = 1L;

  private Logger logger = LoggerFactory.getLogger(ShClientSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient IShMessageFactory factory = null;
  protected transient ClientShSessionListener listener;

  protected long appId = -1;
  protected String destHost, destRealm;

  public ShClientSessionImpl(IShMessageFactory fct, SessionFactory sf, ClientShSessionListener lst) {
    this(null, fct, sf, lst);
  }

  public ShClientSessionImpl(String sessionId, IShMessageFactory fct, SessionFactory sf, ClientShSessionListener lst) {
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

      // Do the delivery
      switch ((Event.Type) localEvent.getType()) {
      case RECEIVE_PUSH_NOTIFICATION_REQUEST:
        listener.doPushNotificationRequestEvent(this, new PushNotificationRequestImpl( (Request) localEvent.getRequest().getMessage() ));
        break;

      case RECEIVE_PROFILE_UPDATE_ANSWER:
        listener.doProfileUpdateAnswerEvent(this, null, new ProfileUpdateAnswerImpl( (Answer) localEvent.getAnswer().getMessage()));
        break;

      case RECEIVE_USER_DATA_ANSWER:
        listener.doUserDataAnswerEvent(this, null, new UserDataAnswerImpl((Answer) localEvent.getAnswer().getMessage()));
        break;

      case RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER:
        listener.doSubscribeNotificationsAnswerEvent(this, null, new SubscribeNotificationsAnswerImpl( (Answer) localEvent.getAnswer().getMessage()));
        break;

      case SEND_PROFILE_UPDATE_REQUEST:
      case SEND_PUSH_NOTIFICATION_ANSWER:
      case SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST:
      case SEND_USER_DATA_REQUEST:
        Message m = null;
        Object data = event.getData();
        m = data instanceof AppEvent ? ((AppEvent)data).getMessage() : (Message) event.getData();
        session.send(m, this);
        break;

      case TIMEOUT_EXPIRES:
        // TODO Anything here?
        break;

      default:
        logger.error("Wrong message type={} req={} ans={}", new Object[]{localEvent.getType(), localEvent.getRequest(), localEvent.getAnswer()});
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

  public void sendProfileUpdateRequest(ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PROFILE_UPDATE_REQUEST, request, null);
  }

  public void sendPushNotificationAnswer(PushNotificationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PUSH_NOTIFICATION_ANSWER, null, answer);
  }

  public void sendSubscribeNotificationsRequest(SubscribeNotificationsRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_SUBSCRIBE_NOTIFICATIONS_REQUEST, request, null);
  }

  public void sendUserDataRequest(UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_USER_DATA_REQUEST, request, null);
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
    try {
      sendAndStateLock.lock();
      if (type != null) {
        handleEvent(new Event(type, request, answer));
      }

      if(request != null) {
        AvpSet avps = request.getMessage().getAvps();
        Avp a = null;
        // Store last destination information
        if((a = avps.getAvp(Avp.DESTINATION_REALM)) != null) {
          destRealm = a.getOctetString();         
        }
        if((a = avps.getAvp(Avp.DESTINATION_HOST)) != null) {
          destHost = a.getOctetString();         
        }
      }
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
    finally {
      sendAndStateLock.unlock();
    }
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
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == ProfileUpdateRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createProfileUpdateRequest(request), null));
          return;
        }
        else if (request.getCommandCode() == UserDataRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createUserDataRequest(request), null));
          return;
        }
        else if (request.getCommandCode() == SubscribeNotificationsRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createSubscribeNotificationsRequest(request), null));
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
  
  public boolean isStateless() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.common.impl.app.AppSessionImpl#isReplicable()
   */
  @Override
  public boolean isReplicable() {
    return true;
  }
  @Override
  public void relink(IContainer stack) {
    if (super.sf == null) {
      super.relink(stack);
      IShSessionFactory fct = (IShSessionFactory) ((ISessionFactory) super.sf).getAppSessionFactory(ClientShSession.class);
      this.listener = fct.getClientShSessionListener();
      this.factory = fct.getMessageFactory();
    }
  }

  private class RequestDelivery implements Runnable {
    ClientShSession session;
    Request request;

    public void run() {
      try {
        if (request.getApplicationId() == factory.getApplicationId()) {
          if (request.getCommandCode() == PushNotificationRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PUSH_NOTIFICATION_REQUEST, factory.createPushNotificationRequest(request), null));
            return;
          }
        }
        listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
      }
      catch (Exception e) {
        logger.debug("Failed to process request {}", request, e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientShSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getApplicationId() == factory.getApplicationId()) {
          if (request.getCommandCode() == ProfileUpdateRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PROFILE_UPDATE_ANSWER, factory.createProfileUpdateRequest(request), factory.createProfileUpdateAnswer(answer)));
            return;
          }
          else if (request.getCommandCode() == UserDataRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_USER_DATA_ANSWER, factory.createUserDataRequest(request), factory.createUserDataAnswer(answer)));
            return;
          }
          else if (request.getCommandCode() == SubscribeNotificationsRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_SUBSCRIBE_NOTIFICATIONS_ANSWER, factory.createSubscribeNotificationsRequest(request), factory
                .createSubscribeNotificationsAnswer(answer)));
            return;
          }
        }
        listener.doOtherEvent(session, new AppRequestEventImpl(request), new AppAnswerEventImpl(answer));
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
