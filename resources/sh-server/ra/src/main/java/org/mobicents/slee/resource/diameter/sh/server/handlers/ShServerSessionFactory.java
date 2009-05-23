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
package org.mobicents.slee.resource.diameter.sh.server.handlers;

import net.java.slee.resource.diameter.sh.client.MessageFactory;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
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
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.jdiameter.server.impl.app.sh.ShServerSessionImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityHandle;
import org.mobicents.slee.resource.diameter.sh.server.DiameterShServerResourceAdaptor;

/**
 * Start time:18:16:01 2009-01-06<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShServerSessionFactory implements IAppSessionFactory, ServerShSessionListener, ClientShSessionListener, StateChangeListener, IShMessageFactory {

  protected SessionFactory sessionFactory = null;
  protected DiameterShServerResourceAdaptor resourceAdaptor = null;
  protected static final Logger logger = Logger.getLogger(ShServerSessionFactory.class);

  // Message timeout value (in milliseconds)
  protected long messageTimeout = 5000;

  public ShServerSessionFactory(SessionFactory sessionFactory, DiameterShServerResourceAdaptor diameterShServerResourceAdaptor, long messageTimeout)
  {
    super();

    this.sessionFactory = sessionFactory;
    this.resourceAdaptor = diameterShServerResourceAdaptor;
    this.messageTimeout = messageTimeout;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.app.IAppSessionFactory#getNewSession(java.lang.String, java.lang.Class, org.jdiameter.api.ApplicationId, java.lang.Object[])
   */
  public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args)
  {
    if (aClass == ServerShSession.class)
    {
      ShServerSessionImpl serverSession = null;

      if (args != null && args.length > 0 && args[0] instanceof Request)
      {
        // This shouldnt happen but just in case
        Request request = (Request) args[0];
        serverSession = new ShServerSessionImpl(sessionId, this, sessionFactory, this);
        serverSession.addStateChangeNotification(this);

        //Notify SLEE
        this.resourceAdaptor.sessionCreated(serverSession, request.getCommandCode() == SubscribeNotificationsRequest.code);
      }
      else
      {
        throw new IllegalArgumentException("Can't create Sh-Server Session: Unknown request type.");
      }

      return serverSession;
    }
    else
    {
      throw new IllegalArgumentException("Wrong session class. Class[" + aClass + "]. Supported[" + ServerShSession.class + "]");
    }
  }

  //////////////////////
  // Message Handlers //
  //////////////////////

  private void doMessage(AppSession appSession, AppEvent message, boolean isRequest) throws InternalException
  {
    DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

    if(isRequest)
    {
      this.resourceAdaptor.fireEvent(handle, DiameterShServerResourceAdaptor.events.get(message.getCommandCode()) + "Request", (Request) message.getMessage(), null);      
    }
    else
    {
      this.resourceAdaptor.fireEvent(handle, DiameterShServerResourceAdaptor.events.get(message.getCommandCode()) + "Answer", null, (Answer) message.getMessage());     
    }
  }

  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    DiameterActivityHandle handle = new DiameterActivityHandle(session.getSessions().get(0).getSessionId());

    logger.info("Diameter ShServer RA :: doOtherEvent :: appSession[" + session + "], Request[" + request + "], Answer[" + answer + "]");

    if (answer != null)
    {
      this.resourceAdaptor.fireEvent(handle, "net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage", null, (Answer) answer.getMessage());
    }
    else
    {
      this.resourceAdaptor.fireEvent(handle, "net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage", (Request) request.getMessage(), null);
    }
  }

  public void doProfileUpdateRequestEvent(ServerShSession session, ProfileUpdateRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, request, true);
  }

  public void doPushNotificationAnswerEvent(ServerShSession session, PushNotificationRequest request, PushNotificationAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, answer, false);
  }

  public void doSubscribeNotificationsRequestEvent(ServerShSession session, SubscribeNotificationsRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, request, true);
  }

  public void doUserDataRequestEvent(ServerShSession session, UserDataRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, request, true);
  }

  public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, answer, false);
  }

  public void doPushNotificationRequestEvent(ClientShSession session, PushNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, request, true);
  }

  public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request, SubscribeNotificationsAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, answer, false);
  }

  public void doUserDataAnswerEvent(ClientShSession session, UserDataRequest request, UserDataAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
  {
    doMessage(session, answer, false);
  }

  public void stateChanged(Enum oldState, Enum newState)
  {
    // TODO Auto-generated method stub
  }

  public AppAnswerEvent createProfileUpdateAnswer(Answer answer)
  {
    return new ProfileUpdateAnswerImpl(answer);
  }

  public AppRequestEvent createProfileUpdateRequest(Request request)
  {
    return new ProfileUpdateRequestImpl(request);
  }

  public AppAnswerEvent createPushNotificationAnswer(Answer answer)
  {
    return new PushNotificationAnswerImpl(answer);
  }

  public AppRequestEvent createPushNotificationRequest(Request request)
  {
    return new PushNotificationRequestImpl(request);
  }

  public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer)
  {
    return new SubscribeNotificationsAnswerImpl(answer);
  }

  public AppRequestEvent createSubscribeNotificationsRequest(Request request)
  {
    return new SubscribeNotificationsRequestImpl(request);
  }

  public AppAnswerEvent createUserDataAnswer(Answer answer)
  {
    return new UserDataAnswerImpl(answer);
  }

  public AppRequestEvent createUserDataRequest(Request request)
  {
    return new UserDataRequestImpl(request);
  }

  public long getApplicationId()
  {
    return MessageFactory._SH_APP_ID;
  }

  public long getMessageTimeout()
  {
    return this.messageTimeout;
  }

}
