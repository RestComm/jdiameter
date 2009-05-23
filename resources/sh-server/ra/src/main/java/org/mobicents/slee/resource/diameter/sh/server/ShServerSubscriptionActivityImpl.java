/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.sh.server;

import java.io.IOException;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity;
import net.java.slee.resource.diameter.sh.server.handlers.ShServerSessionListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;

/**
 * Start time:16:43:18 2009-01-06<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of statful activity.
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShServerSubscriptionActivity
 */
public class ShServerSubscriptionActivityImpl extends DiameterActivityImpl implements ShServerSubscriptionActivity, StateChangeListener {

  protected ServerShSession serverSession = null;
  protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
  protected ShServerSessionListener listener = null;
  protected DiameterShAvpFactory shAvpFactory = null;
  protected ShServerMessageFactoryImpl messageFactory = null;

  protected DiameterIdentityAvp clientOriginHost=null;
  protected DiameterIdentityAvp clientOriginRealm=null;

  public ShServerSubscriptionActivityImpl(ShServerMessageFactory shServerMessageFactory, DiameterShAvpFactory diameterShAvpFactory, ServerShSession session,
      long timeout, DiameterIdentityAvp destinationHost, DiameterIdentityAvp destinationRealm, SleeEndpoint endpoint)
  {
    super(null, null, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);

    this.serverSession = session;
    this.serverSession.addStateChangeNotification(this);
    super.setCurrentWorkingSession(this.serverSession.getSessions().get(0));
    this.shAvpFactory = diameterShAvpFactory;
    this.messageFactory = (ShServerMessageFactoryImpl) shServerMessageFactory;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createPushNotificationRequest()
   */
  public PushNotificationRequest createPushNotificationRequest()
  {
    PushNotificationRequest request = this.messageFactory.createPushNotificationRequest();

    if(request.getDestinationHost() == null && clientOriginHost != null)
    {
      request.setDestinationHost(clientOriginHost);
    }
    if(request.getDestinationRealm() == null && clientOriginRealm != null)
    {
      request.setDestinationRealm(clientOriginRealm);
    }

    return request; 
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createSubscribeNotificationsAnswer(long, boolean)
   */
  public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult)
  {
    return this.messageFactory.createSubscribeNotificationsAnswer(resultCode, isExperimentalResult);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createSubscribeNotificationsAnswer()
   */
  public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer()
  {
    return this.messageFactory.createSubscribeNotificationsAnswer();
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#sendPushNotificationRequest(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest)
   */
  public void sendPushNotificationRequest(PushNotificationRequest message) throws IOException
  {
    DiameterMessageImpl msg = (DiameterMessageImpl) message;
    fetchSessionData(msg, false);
    try
    {
      this.serverSession.sendPushNotificationRequest(new PushNotificationRequestImpl((Request) msg.getGenericData()));
    }
    catch (Exception e) {
      throw new IOException(e.getLocalizedMessage());
    }
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#sendSubscribeNotificationsAnswer(net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer)
   */
  public void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer message) throws IOException
  {
    DiameterMessageImpl msg = (DiameterMessageImpl) message;
    fetchSessionData(msg, false);
    try
    {
      this.serverSession.sendSubscribeNotificationsAnswer(new SubscribeNotificationsAnswerImpl((Answer) msg.getGenericData()));
    }
    catch (Exception e) {
      throw new IOException(e.getLocalizedMessage());
    } 
  }

  public void stateChanged(Enum oldState, Enum newState)
  {
    org.jdiameter.common.api.app.sh.ShSessionState _state = (org.jdiameter.common.api.app.sh.ShSessionState) newState;

    switch (_state)
    {
    case NOTSUBSCRIBED:
      break;
    case SUBSCRIBED:
      state = ShSessionState.SUBSCRIBED;
      // FIXME: error?

      break;
    case TERMINATED:
      state = ShSessionState.TERMINATED;
      listener.sessionDestroyed(getSessionId(), serverSession);
      break;
    }
  }

  public void fetchSessionData(DiameterMessage msg, boolean incoming)
  {
    if(msg.getHeader().isRequest())
    {
      //Well it should always be getting this on request and only once ?
      if(incoming)
      {
        if(this.clientOriginHost == null)
        {
          this.clientOriginHost = msg.getOriginHost();
        }
        if(this.clientOriginRealm == null)
        {
          this.clientOriginRealm = msg.getOriginRealm();
        }
      }
      else
      {
        //FIXME, do more :)
      }
    }
  }

  @Override
  public Object getDiameterAvpFactory()
  {
    return this.avpFactory;
  }

  @Override
  public Object getDiameterMessageFactory()
  {
    return this.messageFactory;
  }

  @Override
  public Object getSessionListener()
  {
    return this.listener;
  }

  @Override
  public void setSessionListener(Object ra)
  {
    this.listener = (ShServerSessionListener) ra;
  }
}
