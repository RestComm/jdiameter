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

package org.jdiameter.client.impl.fsm;

import static org.jdiameter.client.impl.fsm.FsmState.DOWN;
import static org.jdiameter.client.impl.fsm.FsmState.REOPEN;
import static org.jdiameter.client.impl.helpers.Parameters.CeaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.DpaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.DwaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.IacTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.PeerFSMThreadCount;
import static org.jdiameter.client.impl.helpers.Parameters.QueueSize;
import static org.jdiameter.client.impl.helpers.Parameters.RecTimeOut;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.FsmEvent;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class PeerFSMImpl implements IStateMachine {

  private static final Logger logger = LoggerFactory.getLogger(PeerFSMImpl.class);

  protected final Dictionary dictionary = DictionarySingleton.getDictionary();

  protected ConcurrentLinkedQueue<StateChangeListener> listeners;
  protected LinkedBlockingQueue<StateEvent> eventQueue;

  protected FsmState state = FsmState.DOWN;
  protected boolean watchdogSent;
  protected long timer;
  protected long CEA_TIMEOUT = 0, IAC_TIMEOUT = 0, REC_TIMEOUT = 0, DWA_TIMEOUT = 0, DPA_TIMEOUT = 0;

  //PCB made FSM queue multi-threaded
  private static int FSM_THREAD_COUNT = 3;

  protected final StateEvent timeOutEvent = new FsmEvent(EventTypes.TIMEOUT_EVENT);
  protected Random random = new Random();

  protected IConcurrentFactory concurrentFactory;
  protected IContext context;
  protected State[] states;
  protected int predefSize;
  private Lock lock = new ReentrantLock();

  protected IStatisticManager statisticFactory;
  protected IStatistic queueStat;
  protected IStatisticRecord timeSumm;
  protected IStatisticRecord timeCount;

  //PCB changed for multi-thread
  protected boolean mustRun = false;
  protected AtomicInteger numberOfThreadsRunning = new AtomicInteger(0);

  public PeerFSMImpl(IContext aContext, IConcurrentFactory concurrentFactory, Configuration config, IStatisticManager statisticFactory) {
    this.context = aContext;
    this.statisticFactory = statisticFactory;
    this.predefSize = config.getIntValue(QueueSize.ordinal(), (Integer) QueueSize.defValue());
    //PCB added logging
    logger.debug("Maximum FSM Queue size is [{}]", predefSize);
    this.eventQueue = new LinkedBlockingQueue<StateEvent>(predefSize);
    this.listeners = new ConcurrentLinkedQueue<StateChangeListener>();
    loadTimeOuts(config);
    this.concurrentFactory = concurrentFactory;
    FSM_THREAD_COUNT = config.getIntValue(PeerFSMThreadCount.ordinal(), (Integer) PeerFSMThreadCount.defValue());
    runQueueProcessing();
  }

  @Override
  public IStatistic getStatistic() {
    //
    return queueStat;
  }

  @Override
  public void removeStateChangeNotification(StateChangeListener stateChangeListener) {
    listeners.remove(stateChangeListener);
  }

  private void runQueueProcessing() {
    try {
      // PCB - changed way it decides if queue processing must happen in order to allow for multithreaded FSM
      lock.lock();
      if (numberOfThreadsRunning.get() > 0) {
        // runQueueProcessing has been called
        return;
      }
      eventQueue.clear();
      mustRun = true;

      IStatisticRecord queueSize = statisticFactory.newCounterRecord(IStatisticRecord.Counters.QueueSize, new IStatisticRecord.IntegerValueHolder() {
        @Override
        public int getValueAsInt() {
          return eventQueue.size();
        }

        @Override
        public String getValueAsString() {
          return String.valueOf(getValueAsInt());
        }
      });

      this.timeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm");
      this.timeCount = statisticFactory.newCounterRecord("TimeCount", "TimeCount");

      final IStatisticRecord messagePrcAverageTime = statisticFactory.newCounterRecord(IStatisticRecord.Counters.MessageProcessingTime,
          new IStatisticRecord.DoubleValueHolder() {
            @Override
            public double getValueAsDouble() {
              if (queueStat == null) {
                return 0;
              }
              IStatisticRecord mpta = queueStat.getRecordByName(IStatisticRecord.Counters.MessageProcessingTime.name());
              org.jdiameter.api.StatisticRecord[] children = mpta.getChilds();
              if (children.length == 2 && children[1].getValueAsLong() != 0) {
                long count = children[1].getValueAsLong();
                return ((float) children[0].getValueAsLong()) / ((float) (count != 0 ? count : 1));
              }
              else {
                return 0;
              }
            }

            @Override
            public String getValueAsString() {
              return String.valueOf(getValueAsDouble());
            }
          }, timeSumm, timeCount);

      logger.debug("Initializing QueueStat @ Thread[{}]", Thread.currentThread().getName());
      queueStat = statisticFactory.newStatistic(context.getPeerDescription(), IStatistic.Groups.PeerFSM, queueSize, messagePrcAverageTime);
      logger.debug("Finished Initializing QueueStat @ Thread[{}]", Thread.currentThread().getName());

      Runnable fsmQueueProcessor = new Runnable() {
        @Override
        public void run() {
          int runningNow = numberOfThreadsRunning.incrementAndGet();
          logger.debug("Starting ... [{}] FSM threads are running", runningNow);
          //PCB changed for multi-thread
          while (mustRun) {
            StateEvent event;
            try {
              event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
              if (logger.isDebugEnabled() && event != null) {
                logger.debug("Got Event [{}] from Queue", event);
              }
            }
            catch (InterruptedException e) {
              logger.debug("Peer FSM stopped", e);
              break;
            }
            //FIXME: baranowb: why this lock is here?
            // PCB removed lock
            // lock.lock();
            try {
              if (event != null) {
                if (event instanceof FsmEvent && queueStat != null && queueStat.isEnabled()) {
                  timeSumm.inc(System.currentTimeMillis() - ((FsmEvent) event).getCreatedTime());
                  timeCount.inc();
                }
                logger.debug("Process event [{}]. Peer State is [{}]", event, state);
                getStates()[state.ordinal()].processEvent(event);
              }
              if (timer != 0 && timer < System.currentTimeMillis()) {
                // ZhixiaoLuo: add lock here to avoid 2 timeout events at the same time if 2 threads get into timer=0
                // ZhixiaoLuo: use double check strategy to avoid locking most normal cases
                lock.lock();
                try {
                  if (timer != 0 && timer < System.currentTimeMillis()) {
                    timer = 0;
                    if (state != DOWN) { //without this check this event is fired in DOWN state.... it should not be.
                      logger.debug("Sending timeout event");
                      handleEvent(timeOutEvent); //FIXME: check why timer is not killed?
                    }
                  }
                }
                finally {
                  lock.unlock();
                }
              }
            }
            catch (Exception e) {
              logger.debug("Error during processing FSM event", e);
            }
            finally {
              // PCB removed lock
              // lock.unlock();
            }
          }
          //PCB added logging
          logger.debug("FSM Thread {} is exiting", Thread.currentThread().getName());
          //this happens when peer FSM is down, lets remove stat
          statisticFactory.removeStatistic(queueStat);
          logger.debug("Setting QueueStat to null @ Thread [{}]", Thread.currentThread().getName());
          queueStat = null;
          logger.debug("Done Setting QueueStat to null @ Thread [{}]", Thread.currentThread().getName());
          int runningNowAfterStop = numberOfThreadsRunning.decrementAndGet();
          logger.debug("Stopping ... [{}] FSM threads are running", runningNowAfterStop);
        }
      };
      //PCB added FSM multithread
      for (int i = 1; i <= FSM_THREAD_COUNT; i++) {
        logger.debug("Starting FSM Thread {} of {}", i, FSM_THREAD_COUNT);
        Thread executor = concurrentFactory.getThread("FSM-" + context.getPeerDescription() + "_" + i, fsmQueueProcessor);
        executor.start();
      }
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public double getQueueInfo() {
    return eventQueue.size() * 1.0 / predefSize;
  }

  protected void loadTimeOuts(Configuration config) {
    CEA_TIMEOUT = config.getLongValue(CeaTimeOut.ordinal(), (Long) CeaTimeOut.defValue());
    IAC_TIMEOUT = config.getLongValue(IacTimeOut.ordinal(), (Long) IacTimeOut.defValue());
    DWA_TIMEOUT = config.getLongValue(DwaTimeOut.ordinal(), (Long) DwaTimeOut.defValue());
    DPA_TIMEOUT = config.getLongValue(DpaTimeOut.ordinal(), (Long) DpaTimeOut.defValue());
    REC_TIMEOUT = config.getLongValue(RecTimeOut.ordinal(), (Long) RecTimeOut.defValue());
  }

  @Override
  public void addStateChangeNotification(StateChangeListener stateChangeListener) {
    if (!listeners.contains(stateChangeListener)) {
      listeners.add(stateChangeListener);
    }
  }

  @Override
  public void remStateChangeNotification(StateChangeListener stateChangeListener) {
    listeners.remove(stateChangeListener);
  }

  protected void switchToNextState(FsmState newState) {
    // Fix for Issue #3026 (http://code.google.com/p/mobicents/issues/detail?id=3026)
    // notify only when it's a new public state
    if (newState.getPublicState() != state.getPublicState()) {
      for (StateChangeListener l : listeners) {
        l.stateChanged(state.getPublicState(), newState.getPublicState());
      }
    }
    getStates()[state.ordinal()].exitAction();
    if (logger.isDebugEnabled()) {
      logger.debug("{} FSM switch state: {} -> {}", new Object[] {context.getPeerDescription(), state, newState});
    }
    state = newState;
    getStates()[state.ordinal()].entryAction();
  }

  @Override
  public boolean handleEvent(StateEvent event) throws InternalError, OverloadException {
    //if (state.getPublicState() == PeerState.DOWN && event.encodeType(EventTypes.class) == EventTypes.START_EVENT) {
    if (logger.isDebugEnabled()) {
      logger.debug("Handling event with type [{}]", event.getType());
    }
    //PCB added FSM multithread
    if (numberOfThreadsRunning.get() == 0) {
      logger.debug("No FSM threads are running so calling runQueueProcessing()");
      runQueueProcessing();
    }

    // check if validation is required
    if (event.getData() != null) {
      boolean incoming = event.getType() == EventTypes.RECEIVE_MSG_EVENT;
      ValidatorLevel incomingLevel = dictionary.getReceiveLevel();
      if (incoming && dictionary != null && dictionary.isEnabled() && incomingLevel != ValidatorLevel.OFF) {
        logger.debug("Performing validation to INCOMING message since validator is ENABLED.");
        // outgoing are done elsewhere: see BaseSessionImpl
        try {
          dictionary.validate((Message) event.getData(), incoming);
        }
        catch (AvpNotAllowedException e) {
          logger.error("Failed to validate incoming message.", e);
          return false;
        }
      }
      else {
        logger.debug("Not performing incoming validation on message. Validator Enabled [{}] Incoming [{}] Incoming Level [{}]",
            new Object[] { (dictionary != null && dictionary.isEnabled()), incoming, incomingLevel });
      }
    }

    boolean rc = false;
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("Placing event [{}] into linked blocking queue with remaining capacity: [{}].", event, eventQueue.remainingCapacity());
        //PCB added logging
        //int queueSize = eventQueue.size();
        //if (System.currentTimeMillis() - lastLogged > 1000) {
        //  lastLogged = System.currentTimeMillis();
        //  if (queueSize >= 5) {
        //    logger.debug("Diameter Event Queue size is [{}]", queueSize);
        //  }
        //}
      }
      rc = eventQueue.offer(event, IAC_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException e) {
      logger.debug("Can not put event '" + event.toString() + "' to FSM " + this.toString(), e);
      throw new InternalError("Can not put event '" + event.toString() + "' to FSM " + this.toString());
    }
    if (!rc) {
      throw new OverloadException("FSM overloaded");
    }
    return true;
  }

  //PCB added logging
  //private static long lastLogged;

  protected void setInActiveTimer() {
    timer = IAC_TIMEOUT - 2 * 1000 + random.nextInt(5) * 1000 + System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return "PeerFSM{" + "context=" + context + ", state=" + state + '}';
  }

  @Override
  public <E> E getState(Class<E> a) {
    if (a == PeerState.class) {
      return (E) state.getPublicState();
    }
    else {
      return null;
    }
  }

  protected abstract class MyState implements org.jdiameter.api.app.State {

    @Override
    public void entryAction() {
    }

    @Override
    public void exitAction() {
    }

    protected void doEndConnection() {
      if (context.isRestoreConnection()) {
        timer = REC_TIMEOUT + System.currentTimeMillis();
        switchToNextState(REOPEN);
      }
      else {
        switchToNextState(DOWN);
      }
    }

    protected void doDisconnect() {
      try {
        context.disconnect();
      }
      catch (Throwable e) {
      }
    }

    protected void setTimer(long value) {
      timer = value + System.currentTimeMillis();
    }

    protected String key(StateEvent event) {
      return ((FsmEvent) event).getKey();
    }

    protected IMessage message(StateEvent event) {
      return ((FsmEvent) event).getMessage();
    }

    protected EventTypes type(StateEvent event) {
      return (EventTypes) event.getType();
    }

    protected void clearTimer() {
      timer = 0;
    }
  }

  protected org.jdiameter.api.app.State[] getStates() {
    if (states == null) {
      states = new org.jdiameter.api.app.State[] { // todo merge and redesign with server fsm
          new MyState() { // OKEY
            @Override
            public void entryAction() {
              setInActiveTimer();
              watchdogSent = false;
            }

            @Override
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
                case DISCONNECT_EVENT:
                  timer = REC_TIMEOUT + System.currentTimeMillis();
                  switchToNextState(FsmState.REOPEN);
                  break;
                case TIMEOUT_EVENT:
                  try {
                    context.sendDwrMessage();
                    setTimer(DWA_TIMEOUT);
                    if (watchdogSent) {
                      switchToNextState(FsmState.SUSPECT);
                    }
                    else {
                      watchdogSent = true;
                    }
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWR", e);
                    doDisconnect();
                    setTimer(REC_TIMEOUT);
                    switchToNextState(FsmState.REOPEN);
                  }
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
                    setTimer(DPA_TIMEOUT);
                    switchToNextState(FsmState.STOPPING);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPR", e);
                    doDisconnect();
                    switchToNextState(FsmState.DOWN);
                  }
                  break;
                case RECEIVE_MSG_EVENT:
                  setInActiveTimer();
                  context.receiveMessage(message(event));
                  break;
                case DPR_EVENT:
                  try {
                    int code = context.processDprMessage(message(event));
                    context.sendDpaMessage(message(event), code, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPA", e);
                  }
                  doDisconnect();
                  switchToNextState(FsmState.DOWN);
                  break;
                case DWR_EVENT:
                  setInActiveTimer();
                  try {
                    int code = context.processDwrMessage(message(event));
                    context.sendDwaMessage(message(event), code, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWA", e);
                    doDisconnect();
                    switchToNextState(FsmState.DOWN);
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
                    setTimer(REC_TIMEOUT);
                    switchToNextState(FsmState.REOPEN);
                  }
                  break;
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // SUSPECT
            @Override
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
                case DISCONNECT_EVENT:
                  setTimer(REC_TIMEOUT);
                  switchToNextState(FsmState.REOPEN);
                  break;
                case TIMEOUT_EVENT:
                  doDisconnect();
                  setTimer(REC_TIMEOUT);
                  switchToNextState(FsmState.REOPEN);
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
                    switchToNextState(FsmState.STOPPING);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPR", e);
                    doDisconnect();
                    switchToNextState(FsmState.DOWN);
                  }
                  break;
                case DPR_EVENT:
                  try {
                    int code = context.processDprMessage(message(event));
                    context.sendDpaMessage(message(event), code, null);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DPA", e);
                  }
                  doDisconnect();
                  switchToNextState(FsmState.DOWN);
                  break;
                case DWA_EVENT:
                  switchToNextState(FsmState.OKAY);
                  break;
                case DWR_EVENT:
                  try {
                    int code = context.processDwrMessage(message(event));
                    context.sendDwaMessage(message(event), code, null);
                    switchToNextState(FsmState.OKAY);
                  }
                  catch (Throwable e) {
                    logger.debug("Can not send DWA", e);
                    doDisconnect();
                    switchToNextState(FsmState.DOWN);
                  }
                  break;
                case RECEIVE_MSG_EVENT:
                  context.receiveMessage(message(event));
                  switchToNextState(FsmState.OKAY);
                  break;
                case SEND_MSG_EVENT:
                  throw new RuntimeException("Connection is down");
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // DOWN
            @Override
            public void entryAction() {
              clearTimer();
              //PCB changed multithread FSM
              logger.debug("Setting mustRun to false @ Thread [{}]", Thread.currentThread().getName());
              mustRun = false;
              logger.debug("Finished Setting mustRun to false @ Thread [{}]", Thread.currentThread().getName());
              context.removeStatistics();
            }

            @Override
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
                case START_EVENT:
                  try {
                    context.createStatistics();
                    context.connect();
                    context.sendCerMessage();
                    setTimer(CEA_TIMEOUT);
                    switchToNextState(FsmState.INITIAL);
                  }
                  catch (Throwable e) {
                    logger.debug("Connect error", e);
                    setTimer(REC_TIMEOUT);
                    switchToNextState(FsmState.REOPEN);
                  }
                  break;
                case SEND_MSG_EVENT:
                  throw new RuntimeException("Connection is down");
                case STOP_EVENT:
                case DISCONNECT_EVENT:
                  break;
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // REOPEN
            @Override
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
                case CONNECT_EVENT:
                  try {
                    context.sendCerMessage();
                    setTimer(CEA_TIMEOUT);
                    switchToNextState(FsmState.INITIAL);
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
                    logger.debug("Timeout processed. Can not connect to {}", context.getPeerDescription());
                    setTimer(REC_TIMEOUT);
                  }
                  break;
                case STOP_EVENT:
                  clearTimer();
                  doDisconnect();
                  switchToNextState(FsmState.DOWN);
                  break;
                case DISCONNECT_EVENT:
                  break;
                case SEND_MSG_EVENT:
                  throw new IllegalStateException("Connection is down");
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
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
              switch (event.encodeType(EventTypes.class)) {
                case DISCONNECT_EVENT:
                  setTimer(REC_TIMEOUT);
                  switchToNextState(FsmState.REOPEN);
                  break;
                case TIMEOUT_EVENT:
                  doDisconnect();
                  setTimer(REC_TIMEOUT);
                  switchToNextState(FsmState.REOPEN);
                  break;
                case STOP_EVENT:
                  clearTimer();
                  doDisconnect();
                  switchToNextState(FsmState.DOWN);
                  break;
                case CEA_EVENT:
                  clearTimer();
                  if (context.processCeaMessage(((FsmEvent) event).getKey(), ((FsmEvent) event).getMessage())) {
                    switchToNextState(FsmState.OKAY);
                  }
                  else {
                    doDisconnect();
                    setTimer(REC_TIMEOUT);
                    switchToNextState(FsmState.REOPEN);
                  }
                  break;
                case SEND_MSG_EVENT:
                  throw new RuntimeException("Connection is down");
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
                  return false;
              }
              return true;
            }
          },
          new MyState() { // STOPPING
            @Override
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
                case TIMEOUT_EVENT:
                case DPA_EVENT:
                  doDisconnect();
                  switchToNextState(FsmState.DOWN);
                  break;
                case RECEIVE_MSG_EVENT:
                  context.receiveMessage(message(event));
                  break;
                case SEND_MSG_EVENT:
                  throw new RuntimeException("Stack now is stopping");
                case STOP_EVENT:
                case DISCONNECT_EVENT:
                  doDisconnect();
                  break;
                default:
                  logger.debug("Unknown event type: {} in state {}", event.encodeType(EventTypes.class), state);
                  return false;
              }
              return true;
            }
          },
      };
    }
    return states;
  }
}
