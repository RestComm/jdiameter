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

package org.jdiameter.client.impl.app.slg;

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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.slg.LocationReportRequestImpl;
import org.jdiameter.common.impl.app.slg.ProvideLocationAnswerImpl;
import org.jdiameter.common.impl.app.slg.SLgSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of SLgClientSession - can be one time - for PLR
 * If SLgSession moves to SLgSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class SLgClientSessionImpl extends SLgSession implements ClientSLgSession, EventListener<Request, Answer>, NetworkReqListener {

  private Logger logger = LoggerFactory.getLogger(SLgClientSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient ISLgMessageFactory factory = null;
  protected transient ClientSLgSessionListener listener;

  protected ISLgClientSessionData sessionData;

  public SLgClientSessionImpl(ISLgClientSessionData sessionData, ISLgMessageFactory fct, ISessionFactory sf, ClientSLgSessionListener lst) {
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
      case RECEIVE_LOCATION_REPORT_REQUEST:
    	listener.doLocationReportRequestEvent(this, new LocationReportRequestImpl((Request) localEvent.getRequest().getMessage()));
      case RECEIVE_PROVIDE_LOCATION_ANSWER:
    	listener.doProvideLocationAnswerEvent(this, null, new ProvideLocationAnswerImpl((Answer) localEvent.getAnswer().getMessage()));
        break;
      case SEND_LOCATION_REPORT_ANSWER: 
      case SEND_PROVIDE_LOCATION_REQUEST:
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

  public void sendProvideLocationRequest(ProvideLocationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    send(Event.Type.SEND_PROVIDE_LOCATION_REQUEST, request, null);
  }
  
  public void sendLocationReportAnswer(LocationReportAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	send(Event.Type.SEND_LOCATION_REPORT_ANSWER, null, answer);
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
        if (request.getCommandCode() == ProvideLocationRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createProvideLocationRequest(request), null));
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
    ClientSLgSession session;
    Request request;

    public void run() {
      try {
        if (request.getApplicationId() == factory.getApplicationId()) {
        	if (request.getCommandCode() == LocationReportRequest.code) {
        		handleEvent(new Event(Event.Type.RECEIVE_LOCATION_REPORT_REQUEST, factory.createLocationReportRequest(request), null));
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
    ClientSLgSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getApplicationId() == factory.getApplicationId()) {
          if (request.getCommandCode() == LCSRoutingInfoRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PROVIDE_LOCATION_ANSWER, factory.createProvideLocationRequest(request), factory.createProvideLocationAnswer(answer)));
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
