/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl.app.slh;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ClientSLhSessionListener;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slh.ISLhMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoAnswerImpl;
import org.jdiameter.common.impl.app.slh.SLhSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of SLhClientSession - can be one time - for RIR
 * If SLhSession moves to SLhSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class SLhClientSessionImpl extends SLhSession implements ClientSLhSession, EventListener<Request, Answer>, NetworkReqListener {

  private Logger logger = LoggerFactory.getLogger(SLhClientSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient ISLhMessageFactory factory = null;
  protected transient ClientSLhSessionListener listener;

  protected ISLhClientSessionData sessionData;

  public SLhClientSessionImpl(ISLhClientSessionData sessionData, ISLhMessageFactory fct, ISessionFactory sf, ClientSLhSessionListener lst) {
    super(sf,sessionData);
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    this.listener = lst;
    this.factory = fct;
    this.sessionData = sessionData;
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
      case RECEIVE_LCS_ROUTING_INFO_ANSWER:
    	listener.doLCSRoutingInfoAnswerEvent(this, null, new LCSRoutingInfoAnswerImpl((Answer) localEvent.getAnswer().getMessage()));
        break;

      case SEND_LCS_ROUTING_INFO_REQUEST:
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

  public void sendLCSRoutingInfoRequest(LCSRoutingInfoRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_LCS_ROUTING_INFO_REQUEST, request, null);
  }

  protected void send(Event.Type type, AppEvent request, AppEvent answer) throws InternalException {
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

  public void receivedSuccessMessage(Request request, Answer answer) {
    AnswerDelivery rd = new AnswerDelivery();
    rd.session = this;
    rd.request = request;
    rd.answer = answer;
    super.scheduler.execute(rd);
  }

  public void timeoutExpired(Request request) {
    try {
      if (request.getApplicationId() == factory.getApplicationId()) {
        if (request.getCommandCode() == LCSRoutingInfoRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createLCSRoutingInfoRequest(request), null));
          return;
        }
      }
    }
    catch (Exception e) {
      logger.debug("Failed to process timeout message", e);
    }
  }

  public void release() {
    if (isValid()) {
      try {
        sendAndStateLock.lock();
        super.release();
      }
      catch (Exception e) {
        logger.debug("Failed to release session", e);
      }
      finally {
        sendAndStateLock.unlock();
      }
    }
    else {
      logger.debug("Trying to release an already invalid session, with Session ID '{}'", getSessionId());
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
  public void onTimer(String timerName) {
    // TODO ...
  }

  private class RequestDelivery implements Runnable {
    ClientSLhSession session;
    Request request;

    public void run() {
      try {
    	// We do not except any request on client session
        listener.doOtherEvent(session, new AppRequestEventImpl(request), null);
      }
      catch (Exception e) {
        logger.debug("Failed to process request {}", request, e);
      }
    }
  }

  private class AnswerDelivery implements Runnable {
    ClientSLhSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getApplicationId() == factory.getApplicationId()) {
          if (request.getCommandCode() == LCSRoutingInfoRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_LCS_ROUTING_INFO_ANSWER, factory.createLCSRoutingInfoRequest(request), factory.createLCSRoutingInfoAnswer(answer)));
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
