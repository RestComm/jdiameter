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
package org.mobicents.diameter.stack.functional.ro.base;

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
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.ro.ServerRoSession;
import org.jdiameter.api.ro.events.RoCreditControlAnswer;
import org.jdiameter.api.ro.events.RoCreditControlRequest;
import org.jdiameter.common.impl.app.ro.RoCreditControlAnswerImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.ro.AbstractServer;

/**
 * Base implementation of Server
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Server extends AbstractServer {

  protected boolean sentINITIAL;
  protected boolean sentINTERIM;
  protected boolean sentTERMINATE;
  protected boolean sentEVENT;
  protected boolean receiveINITIAL;
  protected boolean receiveINTERIM;
  protected boolean receiveTERMINATE;
  protected boolean receiveEVENT;

  protected RoCreditControlRequest request;

  // ------- send methods to trigger answer

  public void sendInitial() throws Exception {
    if (!this.receiveINITIAL || this.request == null) {
      fail("Did not receive INITIAL or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }
    RoCreditControlAnswer answer = new RoCreditControlAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER));

    super.serverRoSession.sendCreditControlAnswer(answer);

    sentINITIAL = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  public void sendInterim() throws Exception {
    if (!this.receiveINTERIM || this.request == null) {
      fail("Did not receive INTERIM or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }

    RoCreditControlAnswerImpl answer = new RoCreditControlAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER));
    super.serverRoSession.sendCreditControlAnswer(answer);
    sentINTERIM = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  public void sendTermination() throws Exception {
    if (!this.receiveTERMINATE || this.request == null) {
      fail("Did not receive TERMINATE or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }

    RoCreditControlAnswerImpl answer = new RoCreditControlAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER));

    super.serverRoSession.sendCreditControlAnswer(answer);
    sentTERMINATE = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  public void sendEvent() throws Exception {
    if (!this.receiveEVENT || this.request == null) {
      fail("Did not receive EVENT or answer already sent.", null);
      throw new Exception("Request: " + this.request);
    }
    RoCreditControlAnswerImpl answer = new RoCreditControlAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER));

    super.serverRoSession.sendCreditControlAnswer(answer);
    sentEVENT = true;
    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
  }

  // ------- initial, this will be triggered for first msg.

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    if (request.getCommandCode() != 272) {
      fail("Received Request with code not equal 272!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.serverRoSession == null) {
      try {
        super.serverRoSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ServerRoSession.class, (Object) null);
        ((NetworkReqListener) this.serverRoSession).processRequest(request);
      }
      catch (Exception e) {
        fail(null, e);
      }
    }
    else {
      // do fail?
      fail("Received Request in base listener, not in app specific!", null);
    }
    return null;
  }

  // ------------- specific, app session listener.

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cca.ServerRoSessionListener#doCreditControlRequest(org.jdiameter.api.cca.ServerRoSession,
   * org.jdiameter.api.cca.events.RoCreditControlRequest)
   */
  @Override
  public void doCreditControlRequest(ServerRoSession session, RoCreditControlRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    try {
      Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), false);
      // INITIAL_REQUEST 1,
      // UPDATE_REQUEST 2,
      // TERMINATION_REQUEST 3,
      // EVENT_REQUEST 4

      switch (request.getRequestTypeAVPValue()) {
        case CC_REQUEST_TYPE_INITIAL:
          if (receiveINITIAL) {
            fail("Received INITIAL more than once!", null);
          }
          receiveINITIAL = true;
          this.request = request;
          break;

        case CC_REQUEST_TYPE_INTERIM:
          if (receiveINTERIM) {
            fail("Received INTERIM more than once!", null);
          }
          receiveINTERIM = true;
          this.request = request;
          break;

        case CC_REQUEST_TYPE_TERMINATE:
          if (receiveTERMINATE) {
            fail("Received TERMINATE more than once!", null);
          }
          receiveTERMINATE = true;
          this.request = request;
          break;

        case CC_REQUEST_TYPE_EVENT:
          if (receiveEVENT) {
            fail("Received EVENT more than once!", null);
          }
          receiveEVENT = true;
          this.request = request;
          break;

        default:
          fail("No REQ type present?: " + request.getRequestTypeAVPValue(), null);
      }
    }
    catch (Exception e) {
      fail(null, e);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.cca.ServerRoSessionListener#doReAuthAnswer(org.jdiameter.api.cca.ServerRoSession,
   * org.jdiameter.api.auth.events.ReAuthRequest, org.jdiameter.api.auth.events.ReAuthAnswer)
   */
  @Override
  public void doReAuthAnswer(ServerRoSession session, ReAuthRequest request, ReAuthAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"ReAuthAnswer\" event, request[" + request + "], on session[" + session + "]", null);
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

  public boolean isSentINITIAL() {
    return sentINITIAL;
  }

  public boolean isSentINTERIM() {
    return sentINTERIM;
  }

  public boolean isSentTERMINATE() {
    return sentTERMINATE;
  }

  public boolean isReceiveINITIAL() {
    return receiveINITIAL;
  }

  public boolean isReceiveINTERIM() {
    return receiveINTERIM;
  }

  public boolean isReceiveTERMINATE() {
    return receiveTERMINATE;
  }

  public boolean isSentEVENT() {
    return sentEVENT;
  }

  public boolean isReceiveEVENT() {
    return receiveEVENT;
  }

  public RoCreditControlRequest getRequest() {
    return request;
  }

}
