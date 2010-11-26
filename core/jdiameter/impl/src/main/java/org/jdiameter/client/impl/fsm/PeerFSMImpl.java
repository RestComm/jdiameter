package org.jdiameter.client.impl.fsm;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import static org.jdiameter.client.impl.fsm.FsmState.DOWN;
import static org.jdiameter.client.impl.fsm.FsmState.REOPEN;
import static org.jdiameter.client.impl.helpers.Parameters.CeaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.DpaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.DwaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.IacTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.QueueSize;
import static org.jdiameter.client.impl.helpers.Parameters.RecTimeOut;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.FsmEvent;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeerFSMImpl implements IStateMachine {

  private static final Logger logger = LoggerFactory.getLogger(PeerFSMImpl.class);
  
  //TODO: set me
  protected final Dictionary dictionary = DictionarySingleton.getDictionary();
  
  protected ConcurrentLinkedQueue<StateChangeListener> listeners;
  protected LinkedBlockingQueue<StateEvent> eventQueue;

  protected FsmState state = FsmState.DOWN;
  protected boolean watchdogSent;
  protected long timer;
  protected long CEA_TIMEOUT = 0, IAC_TIMEOUT = 0, REC_TIMEOUT = 0, DWA_TIMEOUT = 0, DPA_TIMEOUT = 0;
  protected final StateEvent timeOutEvent = new FsmEvent(EventTypes.TIMEOUT_EVENT);
  protected Random random = new Random();

  protected IConcurrentFactory concurrentFactory;
  protected Thread executor;
  
  protected IContext context;
  protected State[] states;
  protected int predefSize;
  private Lock lock = new ReentrantLock();

  protected IStatistic queueStat;
  protected IStatisticRecord timeSumm;
  protected IStatisticRecord timeCount;
  
  public PeerFSMImpl(IContext aContext, IConcurrentFactory concurrentFactory, Configuration config, IStatisticFactory statisticFactory) {
    this.context = aContext;
    
    this.predefSize = config.getIntValue(QueueSize.ordinal(), (Integer) QueueSize.defValue());
    this.eventQueue = new LinkedBlockingQueue<StateEvent>(predefSize);
    this.listeners = new ConcurrentLinkedQueue<StateChangeListener>();
    loadTimeOuts(config);
    this.concurrentFactory = concurrentFactory;
    runQueueProcessing();
    IStatisticRecord queueSize = statisticFactory.newCounterRecord(IStatistic.Counters.QueueSize,
      new IStatisticRecord.IntegerValueHolder() {
        public int getValueAsInt() {
          return eventQueue.size();
        }

        public String getValueAsString() {
          return String.valueOf(getValueAsInt());
        }
      });
    
    this.timeSumm = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);
    this.timeCount = statisticFactory.newCounterRecord("TimeSumm", "TimeSumm", 0);
    final IStatisticRecord messagePrcAverageTime = statisticFactory.newCounterRecord(
        IStatistic.Counters.MessageProcessingTime,
        new IStatisticRecord.DoubleValueHolder() {
          public double getValueAsDouble() {
            IStatisticRecord mpta = queueStat.getRecordByName(IStatistic.Counters.MessageProcessingTime.name());
            if (mpta.getChilds().length == 2 || mpta.getChilds()[1].getValueAsLong() != 0) {
              long count = mpta.getChilds()[1].getValueAsLong();
              return ((float) mpta.getChilds()[0].getValueAsLong()) / ((float) (count != 0 ? count : 1));
            }
            else {
              return 0;
            }
          }

          public String getValueAsString() {
            return String.valueOf(getValueAsDouble());
          }
        }, timeSumm, timeCount
        );          
   
    queueStat = statisticFactory.newStatistic(IStatistic.Groups.PeerFSM, queueSize, messagePrcAverageTime);
  }
  
  public IStatistic getStatistic() {
    return queueStat;
  }

  public void removeStateChangeNotification(StateChangeListener stateChangeListener) {
    listeners.remove(stateChangeListener);
  }

  private void runQueueProcessing() {
    executor = concurrentFactory.getThread(
    "FSM-" + context.getPeerDescription(),
        new Runnable() {
          public void run() {
            while (executor != null) {
              StateEvent event;
              try {
                event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
              }
              catch (InterruptedException e) {
                logger.debug("Peer fsm stopped");
                break;
              }
              //FIXME: baranowb: why this lock is here?
              lock.lock();
              try {
                if (event != null) {
                  if (event instanceof FsmEvent) {
                    timeSumm.inc(System.currentTimeMillis() - ((FsmEvent) event).getCreatedTime());
                    timeCount.inc();
                  }
                  logger.debug("Process event {}", event);
                  getStates()[state.ordinal()].processEvent(event);
                }
                if (timer != 0 && timer < System.currentTimeMillis()) {
                  timer  = 0;
                  handleEvent(timeOutEvent);
                }
              }
              catch (Exception e) {
                logger.debug("Error during processing fsm event", e);
              }
              finally {
                lock.unlock();
              }
            }
          }
        }
    );
    executor.start();
  }

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

  public void addStateChangeNotification(StateChangeListener stateChangeListener) {
    if (listeners.contains(stateChangeListener)) return;
    listeners.add(stateChangeListener);
  }

  public void remStateChangeNotification(StateChangeListener stateChangeListener) {
    listeners.remove(stateChangeListener);
  }

  protected void switchToNextState(FsmState newState) {
    if (!newState.isInternal()) {
      for (StateChangeListener l : listeners) {
        l.stateChanged(state.getPublicState(), newState.getPublicState());
      }
    }
    getStates()[state.ordinal()].exitAction();
    logger.debug("{} fsm switch state {} -> {}", new Object[] {context.getPeerDescription(), state, newState});
    state = newState;
    getStates()[state.ordinal()].entryAction();
  }

  public boolean handleEvent(StateEvent event) throws InternalError, OverloadException {
    //if (state.getPublicState() == PeerState.DOWN && event.encodeType(EventTypes.class) == EventTypes.START_EVENT) {
    if(executor == null) {
      runQueueProcessing();
    }
    
    if (event.getData() != null && dictionary!= null && dictionary.isEnabled()) {
      boolean incoming = event.getType() == EventTypes.RECEIVE_MSG_EVENT;
      if(incoming) {
        // outgoing are done elsewhere: see BaseSessionImpl
        try{
        	dictionary.validate((Message) event.getData(), incoming);
        }
        catch(AvpNotAllowedException e) {
          logger.error("Failed to validate incoming message.", e);
          return false;
        }
      }
    }

    boolean rc;
    try {
      rc = eventQueue.offer(event, IAC_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException e) {
      throw new InternalError("Can not put event to fsm " + this.toString());
    }
    if (!rc) {
      throw new OverloadException("FSM overloaded");
    }
    return true;
  }

  protected void setInActiveTimer() {
    timer = IAC_TIMEOUT - 2 * 1000 + random.nextInt(5) * 1000 + System.currentTimeMillis();
  }

  public String toString() {
    return "PeerFSM{" + "context=" + context + ", state=" + state + '}';
  }

  public <E> E getState(Class<E> a) {
    if (a == PeerState.class) {
      return (E) state.getPublicState();
    }
    else {
      return null;
    }
  }

  protected abstract class MyState implements org.jdiameter.api.app.State {

    public void entryAction() {
    }

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
          new MyState() // OKEY
          {
            public void entryAction() {
              setInActiveTimer();
              watchdogSent = false;
            }

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
                  if (watchdogSent)
                    switchToNextState(FsmState.SUSPECT);
                  else
                    watchdogSent = true;
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
                  context.sendDprMessage(ResultCode.SUCCESS);
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
          new MyState() // SUSPECT
          {
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
                  context.sendDprMessage(ResultCode.SUCCESS);
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
          new MyState() // DOWN
          {
            public void entryAction() {
              clearTimer();
            }

            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
              case START_EVENT:
                try {
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
          new MyState() // REOPEN
          {
            public boolean processEvent(StateEvent event) {
              switch (event.encodeType(EventTypes.class)) {
              case CONNECT_EVENT:
                try {
                  context.sendCerMessage();
                  setTimer(CEA_TIMEOUT);
                  switchToNextState(FsmState.INITIAL);
                }
                catch(Throwable e) {
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
          new MyState() // INITIAL
          {

            public void entryAction() {
              setTimer(CEA_TIMEOUT);
            }

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
          new MyState() // STOPPING
          {
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
