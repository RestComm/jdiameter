/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2017, TeleStax Inc. and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.restcomm.diameter.base.accounting;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.acc.ServerAccSessionListener;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.acc.IAccSessionFactory;
import org.jdiameter.common.impl.app.acc.AccSessionFactoryImpl;
import org.jdiameter.common.impl.app.acc.AccountAnswerImpl;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base Accounting Server implementation
 *
 * @author ammendonca
 */
public class BaseAccountingServer {

  private static final Logger logger = LoggerFactory.getLogger(BaseAccountingServer.class);

  // Stack configuration file -------------------------------------------------
  private static final String configFile = "jdiameter-config.xml";

  // Application Id -----------------------------------------------------------
  private static final ApplicationId DIAM_APP_ID = ApplicationId.createByAccAppId(0, 3);

  private static IAccSessionFactory accSessionFactory;

  private static Stack stack;

  public static void main(String[] args) throws Exception {
    stack = new StackImpl();

    XMLConfiguration configuration = null;
    configuration = new XMLConfiguration(BaseAccountingServer.class.getClassLoader().getResourceAsStream(configFile));

    System.out.println("\n" +
        "             ___  _______   __  ____________________            \n" +
        "            / _ \\/  _/ _ | /  |/  / __/_  __/ __/ _ \\           \n" +
        "           / // _/ // __ |/ /|_/ / _/  / / / _// , _/           \n" +
        "          /____/___/_/ |_/_/  /_/___/ /_/ /___/_/|_|            \n" +
        "----------------------------------------------------------------\n" +
        "                  LOAD TEST :: BASE ACCOUNTING                  \n"+
        "----------------------------------------------------------------");

    SessionFactory factory = stack.init(configuration);

    accSessionFactory = new AccSessionFactoryImpl(factory);

    // register app id with network req listener
    Network network = stack.unwrap(Network.class);
    network.addNetworkReqListener(new MyNetworkListener(), DIAM_APP_ID);

    // set server session listener
    accSessionFactory.setServerSessionListener(new BaseAcctServerSessionListener());

    ((ISessionFactory)factory).registerAppFacory(ServerAccSession.class, accSessionFactory);

    stack.start();
  }

  /**
   * The NetworkReqListener implementation
   * Responsible for receiving the requests from the network and deliver it to the Session
   */
  private static class MyNetworkListener implements NetworkReqListener {
    @Override
    public Answer processRequest(final Request req) {
      if (req.getCommandCode() != AccountRequest.code) {
        logger.warn("Received unexpected message with command-code " + req.getCommandCode() +". Answering with COMMAND_UNSUPPORTED.");
        return req.createAnswer(ResultCode.COMMAND_UNSUPPORTED);
      }

      try {
        ApplicationId appId = req.getApplicationIdAvps().isEmpty() ? null : req.getApplicationIdAvps().iterator().next();
        ServerAccSessionImpl session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(req.getSessionId(),
            appId, ServerAccSession.class, req);
        session.processRequest(req);
      }
      catch (Exception e) {
        logger.error("Failure trying to obtain new session with ID '" + req.getSessionId() + "'", e);
      }
      // return null so that the listener can send it's answer
      return null;
    }
  }

  /**
   * The ServerAccSessionListener
   * Receives session events and act upon them
   */
  private static class BaseAcctServerSessionListener implements ServerAccSessionListener {
    @Override
    public void doAccRequestEvent(ServerAccSession session, AccountRequest acr)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
      try {
        if (logger.isInfoEnabled()) {
          logger.info("<< [SID:{}] Received ACR [T={},N={}]",
              new Object[] { acr.getMessage().getSessionId(), acr.getAccountingRecordType(), acr.getAccountingRecordNumber() });
        }

        AccountAnswerImpl aca = new AccountAnswerImpl((Request) acr.getMessage(), 2001L);
        aca.getMessage().getAvps().addAvp(Avp.ACC_RECORD_TYPE, acr.getAccountingRecordType());
        aca.getMessage().getAvps().addAvp(Avp.ACC_RECORD_NUMBER, acr.getAccountingRecordNumber(), true);
        if (logger.isInfoEnabled()) {
          logger.info("== [SID:{}]  Created ACA [T={},N={}]",
              new Object[] { aca.getMessage().getSessionId(), aca.getAccountingRecordType(), acr.getAccountingRecordNumber() });
        }

        session.sendAccountAnswer(aca);
        if (logger.isInfoEnabled()) {
          logger.info(">> [SID:{}]     Sent ACA [T={},N={}]",
              new Object[] { aca.getMessage().getSessionId(), aca.getAccountingRecordType(), acr.getAccountingRecordNumber() });
        }
      }
      catch (Exception e) {
        logger.error("Failure trying to create/send ACA", e);
      }
    }

    @Override
    public void doOtherEvent(AppSession session, AppRequestEvent req, AppAnswerEvent ans)
        throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
      logger.warn("Unexpected event at Accountin Server Session with ID '{}'. Request:{}, Answer:{}",
          new Object[] { session.getSessionId(), req, ans });
    }
  }
}