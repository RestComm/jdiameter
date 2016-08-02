/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
package org.mobicents.diameter.stack.functional.sh.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;

/**
 * Base implementation of Server
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ServerSNR extends org.mobicents.diameter.stack.functional.sh.AbstractServer {

  protected boolean sentSubscribeNotifications;
  protected boolean sentProfileUpdate;
  protected boolean sentUserData;
  protected boolean sentPushNotification;
  protected boolean receiveSubscribeNotifications;
  protected boolean receiveProfileUpdate;
  protected boolean receiveUserData;
  protected boolean receivePushNotification;

  protected SubscribeNotificationsRequest subscribeNotificationsRequest;

  // ------- send methods to trigger answer
  public void sendSubscribeNotifications() throws Exception {
    if (!this.receiveSubscribeNotifications || this.subscribeNotificationsRequest == null) {
      fail("Did not receive SUBSCRIBE or answer already sent.", null);
      throw new Exception("Did not receive SUBSCRIBE or answer already sent. Request: " + this.subscribeNotificationsRequest);
    }
    SubscribeNotificationsAnswer answer = new SubscribeNotificationsAnswerImpl((Request) this.subscribeNotificationsRequest.getMessage(), 2001);

    AvpSet reqSet = subscribeNotificationsRequest.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));
    this.serverShSession.sendSubscribeNotificationsAnswer(answer);
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);

    this.subscribeNotificationsRequest = null;
  }

  // ------------- specific, app session listener.

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != SubscribeNotificationsRequest.code) {
      fail("Received Request with code not used by Sh!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverShSession == null) {
      try {
        super.serverShSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerShSession.class, (Object) null);
        ((NetworkReqListener) this.serverShSession).processRequest(request);
      }
      catch (Exception e) {
        fail(null, e);
      }
    }
    else {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cca.ServerRoSessionListener#doOtherEvent(org.jdiameter.api.app.AppSession,
   * org.jdiameter.api.app.AppRequestEvent, org.jdiameter.api.app.AppAnswerEvent)
   */
  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  @Override
  public void doSubscribeNotificationsRequestEvent(ServerShSession session, SubscribeNotificationsRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receiveSubscribeNotifications) {
      fail("Received SUBSCRIBE more than once!", null);
    }
    this.receiveSubscribeNotifications = true;
    this.subscribeNotificationsRequest = request;

  }

  @Override
  public void doProfileUpdateRequestEvent(ServerShSession session, ProfileUpdateRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"PUR\" event, request[" + request + "], on session[" + session + "]", null);
    this.receiveProfileUpdate = true;
  }

  @Override
  public void doPushNotificationAnswerEvent(ServerShSession session, PushNotificationRequest request, PushNotificationAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"PUA\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
    this.receivePushNotification = true;
  }

  @Override
  public void doUserDataRequestEvent(ServerShSession session, UserDataRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"UDR\" event, request[" + request + "], on session[" + session + "]", null);
    this.receiveUserData = true;
  }

  public boolean isSentSubscribeNotifications() {
    return sentSubscribeNotifications;
  }

  public boolean isSentProfileUpdate() {
    return sentProfileUpdate;
  }

  public boolean isSentUserData() {
    return sentUserData;
  }

  public boolean isSentPushNotification() {
    return sentPushNotification;
  }

  public boolean isReceiveSubscribeNotifications() {
    return receiveSubscribeNotifications;
  }

  public boolean isReceiveProfileUpdate() {
    return receiveProfileUpdate;
  }

  public boolean isReceiveUserData() {
    return receiveUserData;
  }

  public boolean isReceivePushNotification() {
    return receivePushNotification;
  }

}
