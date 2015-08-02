/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc. and individual contributors
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

package org.jdiameter.server.impl.app.slg;

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
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
import org.jdiameter.common.impl.app.slg.SLgSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of SLgServerSession - can be one time - for PLR
 * If SLgSession moves to SLgSessionState.TERMINATED - it means that no further
 * messages can be received via it and it should be discarded. <br>
 * <br>
 * 
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href = "mailto:ajitlakhwani@gmail.com"> Ajit Lakhwani </a>
 */
public class SLgServerSessionImpl extends SLgSession implements ServerSLgSession, EventListener<Request, Answer>, NetworkReqListener {

  private Logger logger = LoggerFactory.getLogger(SLgServerSessionImpl.class);

  // Session State Handling ---------------------------------------------------
  protected Lock sendAndStateLock = new ReentrantLock();

  // Factories and Listeners --------------------------------------------------
  protected transient ISLgMessageFactory factory = null;
  protected transient ServerSLgSessionListener listener;

  protected ISLgServerSessionData sessionData;
  protected long appId;
  public SLgServerSessionImpl(ISLgServerSessionData sessionData, ISLgMessageFactory fct, ISessionFactory sf, ServerSLgSessionListener lst) {
    super(sf, sessionData);
    if(sessionData == null) {
      throw new NullPointerException("SessionData must not be null");
    }
    if (lst == null) {
      throw new IllegalArgumentException("Listener can not be null");
    }
    if (fct.getApplicationId() < 0) {
      throw new IllegalArgumentException("ApplicationId can not be less than zero");
    }
    this.sessionData = sessionData;
    this.appId = fct.getApplicationId();
    this.listener = lst;
    this.factory = fct;
  }

  @Override
  public void sendLocationReportRequest(LocationReportRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	  send(Event.Type.SEND_LOCATION_REPORT_REQUEST, request, null);
  }
  
  @Override
  public void sendProvideLocationAnswer(ProvideLocationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
	  send(Event.Type.SEND_PROVIDE_LOCATION_ANSWER, null, answer);
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
        if (request.getCommandCode() == LocationReportRequest.code) {
          handleEvent(new Event(Event.Type.TIMEOUT_EXPIRES, factory.createLocationReportRequest(request), null));
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

  public <E> E getState(Class<E> stateType) {
    return null;
  }

  public boolean handleEvent(StateEvent event) throws InternalException, OverloadException {
    try {
      sendAndStateLock.lock();
      Event localEvent = (Event) event;
      switch ((Event.Type) localEvent.getType()) {
      case RECEIVE_PROVIDE_LOCATION_REQUEST:
          listener.doProvideLocationRequestEvent(this, (ProvideLocationRequest) localEvent.getRequest());
          break;
      case RECEIVE_LOCATION_REPORT_ANSWER:
    	  listener.doLocationReportAnswerEvent(this, (LocationReportRequest) localEvent.getRequest(), (LocationReportAnswer) localEvent.getAnswer());
    	  break;
      case SEND_PROVIDE_LOCATION_ANSWER:
      case SEND_LOCATION_REPORT_REQUEST:
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (appId ^ (appId >>> 32));
    result = prime * result + ((sessionData == null) ? 0 : sessionData.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SLgServerSessionImpl other = (SLgServerSessionImpl) obj;
    if (appId != other.appId)
      return false;
    if (sessionData == null) {
      if (other.sessionData != null)
        return false;
    }
    else if (!sessionData.equals(other.sessionData))
      return false;
    return true;
  }


  @Override
  public void onTimer(String timerName) {
    logger.trace("onTimer({})", timerName);
  }

  private class RequestDelivery implements Runnable {
    ServerSLgSession session;
    Request request;

    public void run() {
      try {
        if (request.getApplicationId() == appId) {
          if(request.getCommandCode() == ProvideLocationRequest.code) {
            handleEvent(new Event(Event.Type.RECEIVE_PROVIDE_LOCATION_REQUEST, factory.createProvideLocationRequest(request), null));
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
    ServerSLgSession session;
    Answer answer;
    Request request;

    public void run() {
      try {
        sendAndStateLock.lock();
        if (request.getApplicationId() == appId) {
        	if (request.getCommandCode() == LocationReportRequest.code) {
        		handleEvent(new Event(Event.Type.RECEIVE_LOCATION_REPORT_ANSWER, factory.createLocationReportRequest(request), factory.createLocationReportAnswer(answer)));
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
