 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
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
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.server.impl.fsm;

import static org.jdiameter.client.impl.fsm.FsmState.DOWN;
import static org.jdiameter.client.impl.fsm.FsmState.INITIAL;
import static org.jdiameter.client.impl.fsm.FsmState.OKAY;
import static org.jdiameter.client.impl.fsm.FsmState.STOPPING;
import static org.jdiameter.client.impl.fsm.FsmState.SUSPECT;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.ConfigurationListener;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.server.api.IStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class PeerFSMImpl extends org.jdiameter.client.impl.fsm.PeerFSMImpl implements IStateMachine, ConfigurationListener {

  private static final Logger logger = LoggerFactory.getLogger(org.jdiameter.server.impl.fsm.PeerFSMImpl.class);

  public PeerFSMImpl(IContext context, IConcurrentFactory concurrentFactory, Configuration config, IStatisticManager statisticFactory) {
    super(context, concurrentFactory, config, statisticFactory);
  }

  @Override
  protected void loadTimeOuts(Configuration config) {
    super.loadTimeOuts(config);
    if (config instanceof MutableConfiguration) {
      ((MutableConfiguration) config).addChangeListener(this, 0);
    }
  }

  @Override
  public boolean elementChanged(int i, Object data) {
    Configuration newConfig = (Configuration) data;
    super.loadTimeOuts(newConfig);
    return true;
  }

  @Override
  protected State[] getStates() {
    if (states == null) {
      states = new State[] {
          new MyState() { // OKEY
            @Override
            public void entryAction() { // todo send buffered messages
              setInActiveTimer();
              watchdogSent = false;
            }

            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case DISCONNECT_EVENT:
                  doEndConnection();
                  break;
                case TIMEOUT_EVENT:
                  try {
                    context.sendDwrMessage();
                    setTimer(DWA_TIMEOUT);
                    if (watchdogSent) {
                      switchToNextState(SUSPECT);
                    }
                    else {
                      watchdogSent = true;
                    }
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWR", e);
                    doDisconnect();
                    doEndConnection();
                  }
                  break;
                case STOP_EVENT:
                  try {
                    if (event.getData() == null) {
                      context.sendDprMessage(DisconnectCause.BUSY);
                    }
                    else {
                      Integer disconnectCause = (Integer) event.getData();
                      context.sendDprMessage(disconnectCause);
                    }
                    setTimer(DPA_TIMEOUT);
                    switchToNextState(STOPPING);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPR", e);
                    doDisconnect();
                    switchToNextState(DOWN);
                  }
                  break;
                case RECEIVE_MSG_EVENT:
                  setInActiveTimer();
                  context.receiveMessage(message(event));
                  break;
                case CEA_EVENT:
                  setInActiveTimer();
                  if (context.processCeaMessage(key(event), message(event))) {
                    doDisconnect(); // !
                    doEndConnection();
                  }
                  break;
                case CER_EVENT:
                  // setInActiveTimer();
                  logger.debug("Rejecting CER in OKAY state. Answering with UNABLE_TO_COMPLY (5012)");
                  try {
                    context.sendCeaMessage(ResultCode.UNABLE_TO_COMPLY, message(event), "Unable to receive CER in OPEN state.");
                  }
                  catch (Exception e) {
                    logger.debug("Failed to send CEA.", e);
                    doDisconnect();  // !
                    doEndConnection();
                  }
                  break;
                case DPR_EVENT:
                  try {
                    int code = context.processDprMessage((IMessage) event.getData());
                    context.sendDpaMessage(message(event), code, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPA", e);
                  }
                  IMessage message = (IMessage) event.getData();
                  try {
                    Avp discCause = message.getAvps().getAvp(Avp.DISCONNECT_CAUSE);
                    boolean willReconnect = (discCause != null) ? (discCause.getInteger32() == DisconnectCause.REBOOTING) : false;
                    if (willReconnect) {
                      doDisconnect();
                      doEndConnection();
                    }
                    else {
                      doDisconnect();
                      switchToNextState(DOWN);
                    }
                  }
                  catch (AvpDataException ade) {
                    logger.warn("Disconnect cause is bad.", ade);
                    doDisconnect();
                    switchToNextState(DOWN);
                  }

                  break;
                case DWR_EVENT:
                  setInActiveTimer();
                  try {
                    context.sendDwaMessage(message(event), ResultCode.SUCCESS, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWA, reconnecting", e);
                    doDisconnect();
                    doEndConnection();
                  }
                  break;
                case DWA_EVENT:
                  setInActiveTimer();
                  watchdogSent = false;
                  break;
                case SEND_MSG_EVENT:
                  try {
                    context.sendMessage(message(event));
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send message", e);
                    doDisconnect();
                    doEndConnection();
                  }
                  break;
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // SUSPECT
            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case DISCONNECT_EVENT:
                  doEndConnection();
                  break;
                case TIMEOUT_EVENT:
                  doDisconnect();
                  doEndConnection();
                  break;
                case STOP_EVENT:
                  try {
                    if (event.getData() == null) {
                      context.sendDprMessage(DisconnectCause.REBOOTING);
                    }
                    else {
                      Integer disconnectCause = (Integer) event.getData();
                      context.sendDprMessage(disconnectCause);
                    }
                    setInActiveTimer();
                    switchToNextState(STOPPING);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPR", e);
                    doDisconnect();
                    switchToNextState(DOWN);
                  }
                  break;
                case CER_EVENT:
                case CEA_EVENT:
                case DWA_EVENT:
                  clearTimer();
                  switchToNextState(OKAY);
                  break;
                case DPR_EVENT:
                  try {
                    int code = context.processDprMessage((IMessage) event.getData());
                    context.sendDpaMessage(message(event), code, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPA", e);
                  }
                  IMessage message = (IMessage) event.getData();
                  try {
                    if (message.getAvps().getAvp(Avp.DISCONNECT_CAUSE) != null &&
                        message.getAvps().getAvp(Avp.DISCONNECT_CAUSE).getInteger32() == DisconnectCause.REBOOTING) {
                      doDisconnect();
                      doEndConnection();
                    }
                    else {
                      doDisconnect();
                      switchToNextState(DOWN);
                    }
                  } catch (AvpDataException e1) {
                    logger.warn("Disconnect cause is bad.", e1);
                    doDisconnect();
                    switchToNextState(DOWN);
                  }
                  break;
                case DWR_EVENT:
                  try {
                    int code = context.processDwrMessage((IMessage) event.getData());
                    context.sendDwaMessage(message(event), code, null);
                    switchToNextState(OKAY);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWA", e);
                    doDisconnect();
                    switchToNextState(DOWN);
                  }
                  break;
                case RECEIVE_MSG_EVENT:
                  clearTimer();
                  context.receiveMessage(message(event));
                  switchToNextState(OKAY);
                  break;
                case SEND_MSG_EVENT: // todo buffering
                  throw new IllegalStateException("Connection is down");
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // DOWN
            @Override
            public void entryAction() {
              setTimer(0);
              //FIXME: baranowb: removed this, cause this breaks peers as
              //       it seems, if peer is not removed, it will linger
              //       without any way to process messages
              // if (context.isRestoreConnection()) {
              //PCB added FSM multithread
              mustRun = false;
              // }
              context.removeStatistics();
            }

            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case START_EVENT:
                  try {
                    context.createStatistics();
                    if (!context.isConnected()) {
                      context.connect();
                    }
                    context.sendCerMessage();
                    setTimer(CEA_TIMEOUT);
                    switchToNextState(INITIAL);
                  }
                  catch (Throwable e) {
                    logger.debug("Connect error", e);
                    doEndConnection();
                  }
                  break;
                case CER_EVENT:
                  context.createStatistics();
                  int resultCode = context.processCerMessage(key(event), message(event));
                  if (resultCode == ResultCode.SUCCESS) {
                    try {
                      context.sendCeaMessage(resultCode, message(event), null);
                      switchToNextState(OKAY);
                    }
                    catch (Exception e) {
                      logger.debug("Failed to send CEA.", e);
                      doDisconnect();  // !
                      doEndConnection();
                    }
                  }
                  else {
                    try {
                      context.sendCeaMessage(resultCode, message(event),  null);
                    }
                    catch (Exception e) {
                      logger.debug("Failed to send CEA.", e);
                    }
                    doDisconnect(); // !
                    doEndConnection();
                  }
                  break;
                case SEND_MSG_EVENT:
                  // todo buffering
                  throw new IllegalStateException("Connection is down");
                case STOP_EVENT:
                case TIMEOUT_EVENT:
                case DISCONNECT_EVENT:
                  // those are ~legal, ie. DISCONNECT_EVENT is sent back from connection
                  break;
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // REOPEN
            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case CONNECT_EVENT:
                  try {
                    context.sendCerMessage();
                    setTimer(CEA_TIMEOUT);
                    switchToNextState(INITIAL);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send CER", e);
                    setTimer(REC_TIMEOUT);
                  }
                  break;
                case TIMEOUT_EVENT:
                  try {
                    context.connect();
                  }
                  catch (Exception e) {
                    logger.debug("Can not connect to remote peer", e);
                    setTimer(REC_TIMEOUT);
                  }
                  break;
                case STOP_EVENT:
                  setTimer(0);
                  doDisconnect();
                  switchToNextState(DOWN);
                  break;
                case DISCONNECT_EVENT:
                  break;
                case SEND_MSG_EVENT:
                  // todo buffering
                  throw new IllegalStateException("Connection is down");
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // INITIAL
            @Override
            public void entryAction() {
              setTimer(CEA_TIMEOUT);
            }

            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case DISCONNECT_EVENT:
                  setTimer(0);
                  doEndConnection();
                  break;
                case TIMEOUT_EVENT:
                  doDisconnect();
                  doEndConnection();
                  break;
                case STOP_EVENT:
                  setTimer(0);
                  doDisconnect();
                  switchToNextState(DOWN);
                  break;
                case CEA_EVENT:
                  setTimer(0);
                  if (context.processCeaMessage(key(event), message(event))) {
                    switchToNextState(OKAY);
                  }
                  else {
                    doDisconnect(); // !
                    doEndConnection();
                  }
                  break;
                case CER_EVENT:
                  int resultCode = context.processCerMessage(key(event), message(event));
                  if (resultCode == ResultCode.SUCCESS) {
                    try {
                      context.sendCeaMessage(resultCode, message(event), null);
                      switchToNextState(OKAY); // if other connection is win
                    }
                    catch (Exception e) {
                      logger.debug("Can not send CEA", e);
                      doDisconnect();
                      doEndConnection();
                    }
                  }
                  else if (resultCode == -1 || resultCode == ResultCode.NO_COMMON_APPLICATION) {
                    doDisconnect();
                    doEndConnection();
                  }
                  break;
                case SEND_MSG_EVENT:
                  // todo buffering
                  throw new IllegalStateException("Connection is down");
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // STOPPING
            @Override
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
                case TIMEOUT_EVENT:
                case DPA_EVENT:
                  switchToNextState(DOWN);
                  break;
                case RECEIVE_MSG_EVENT:
                  context.receiveMessage(message(event));
                  break;
                case SEND_MSG_EVENT:
                  throw new IllegalStateException("Stack now is stopping");
                case STOP_EVENT:
                case DISCONNECT_EVENT:
                  break;
                default:
                  logger.debug("Unknown event type {} in state {}", type(event), state);
                  return false;
              }
              return true;
            }
          }
      };
    }

    return states;
  }
}
