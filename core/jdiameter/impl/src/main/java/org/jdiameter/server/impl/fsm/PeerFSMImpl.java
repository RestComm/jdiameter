package org.jdiameter.server.impl.fsm;

import static org.jdiameter.client.impl.fsm.PeerFSMImpl.CIntState.*;
import static org.jdiameter.server.impl.helpers.StatisticTypes.PEER_QUEUE_SIZE;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.ConfigurationListener;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Statistic;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.ExecutorFactory;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.server.api.IStatistic;
import org.jdiameter.server.api.IStatisticRecord;
import org.jdiameter.server.impl.StatisticImpl;
import org.jdiameter.server.impl.StatisticRecordImpl;

public class PeerFSMImpl extends org.jdiameter.client.impl.fsm.PeerFSMImpl implements ConfigurationListener {

  protected IStatisticRecord queueSize = new StatisticRecordImpl("FsmQueue", "Peer FSM queue size", PEER_QUEUE_SIZE,
      new StatisticRecordImpl.Counter() { public int getValueAsInt() { return eventQueue.size(); } }
  );

  protected IStatistic queueStat = new StatisticImpl("PeerFSM" , "PeerFSM statistic", queueSize);

  public Statistic getStatistic() {
    return queueStat;
  }

  public PeerFSMImpl(IContext context, ExecutorFactory executor, Configuration config) {
    super(context, executor, config);
  }

  protected void loadTimeOuts(Configuration config) {
    super.loadTimeOuts(config);
    if (config instanceof MutableConfiguration) {
      ((MutableConfiguration)config).addChangeListener(this, 0);
    }
  }

  public boolean elementChanged(int i, Object data) {
    Configuration newConfig = (Configuration) data;
    super.loadTimeOuts(newConfig);
    return true;
  }

  protected State[] getStates() {
    if (states == null) {
      states = new State[] {
          new MyState() // OKEY
          {
            public void entryAction() { // todo send buffered messages
              setInActiveTimer();
              watchdogSent = false;
            }

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
                    swithToNextState(SUSPECT);
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
                try { // TODO: send user code;
                  context.sendDprMessage(ResultCode.SUCCESS);
                  setTimer(DPA_TIMEOUT);
                  swithToNextState(STOPPING);
                }
                catch (Throwable e) {
                  logger.debug("Can not send DPR", e);
                  doDisconnect();
                  swithToNextState(DOWN);
                }
                break;
              case RECEIVE_MSG_EVENT:
                setInActiveTimer();
                context.receiveMessage( message(event) );
                break;
              case CEA_EVENT:
                setInActiveTimer();
                if ( context.processCeaMessage(key(event), message(event)) ) {
                  doDisconnect(); // !
                  doEndConnection();
                }
                break;
              case CER_EVENT:
                setInActiveTimer();
                // Skip
                break;
              case DPR_EVENT:
                try {
                  int code = context.processDprMessage((IMessage) event.getData());
                  context.sendDpaMessage( message(event), code, null );
                }
                catch (Throwable e) {
                  logger.debug("Can not send DPA", e);
                }
                doDisconnect();
                swithToNextState(DOWN);
                break;
              case DWR_EVENT:
                setInActiveTimer();
                try {
                  context.sendDwaMessage( message(event), ResultCode.SUCCESS, null);
                }
                catch (Throwable e) {
                  logger.debug("Can not send DWA", e);
                  doDisconnect();
                  swithToNextState(DOWN);
                }
                break;
              case DWA_EVENT:
                setInActiveTimer();
                watchdogSent = false;
                break;
              case SEND_MSG_EVENT:
                try {
                  context.sendMessage( message(event) );
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
          new MyState() // SUSPECT
          {
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
                  context.sendDprMessage(ResultCode.SUCCESS);
                  setInActiveTimer();
                  swithToNextState(STOPPING);
                }
                catch (Throwable e) {
                  logger.debug("Can not send DPR", e);
                  doDisconnect();
                  swithToNextState(DOWN);
                }
                break;
              case CER_EVENT:
              case CEA_EVENT:
              case DWA_EVENT:
                clearTimer();
                swithToNextState(OKAY);
                break;
              case DPR_EVENT:
                try {
                  int code = context.processDprMessage((IMessage) event.getData());
                  context.sendDpaMessage( message(event), code, null);
                }
                catch (Throwable e) {
                  logger.debug("Can not send DPA", e);
                }
                doDisconnect();
                swithToNextState(DOWN);
                break;
              case DWR_EVENT:
                try {
                  int code = context.processDwrMessage((IMessage) event.getData());
                  context.sendDwaMessage( message(event),code, null);
                  swithToNextState(OKAY);
                }
                catch (Throwable e) {
                  logger.debug("Can not send DWA", e);
                  doDisconnect();
                  swithToNextState(DOWN);
                }
                break;
              case RECEIVE_MSG_EVENT:
                clearTimer();
                context.receiveMessage( message(event) );
                swithToNextState(OKAY);
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
          new MyState() // DOWN
          {
            public void entryAction() {
              setTimer(0);
            }

            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
              case START_EVENT:
                try {
                  if ( !context.isConnected() ) {
                    context.connect();                              
                  }
                  context.sendCerMessage();
                  setTimer(CEA_TIMEOUT);
                  swithToNextState(INITIAL);
                }
                catch (Throwable e) {
                  logger.debug( "Connect error", e);
                  doEndConnection();
                }
                break;
              case CER_EVENT:
                int resultCode = context.processCerMessage(key(event), message(event));
                if ( resultCode == ResultCode.SUCCESS ) {
                  try {
                    context.sendCeaMessage(resultCode, message(event), null);
                    swithToNextState(OKAY);
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
                break;
              default:
                logger.debug("Unknown event type {} in state {}", type(event), state);                          
              return false;
              }
              return true;
            }
          },
          new MyState() // REOPEN
          {
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
              case CONNECT_EVENT:
                try {
                  context.sendCerMessage();
                  setTimer(CEA_TIMEOUT);
                  swithToNextState(INITIAL);
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
                  logger.debug("Can not connect to remote peer", e);
                  setTimer(REC_TIMEOUT);
                }
                break;
              case STOP_EVENT:
                setTimer(0);
                doDisconnect();
                swithToNextState(DOWN);
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
          new MyState() // INITIAL
          {
            public void entryAction() {
              setTimer(CEA_TIMEOUT);
            }

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
                swithToNextState(DOWN);
                break;
              case CEA_EVENT:
                setTimer(0);
                if ( context.processCeaMessage(key(event), message(event)) ) {
                  swithToNextState(OKAY);
                }
                else {
                  doDisconnect(); // !
                  doEndConnection();
                }
                break;
              case CER_EVENT:
                int resultCode = context.processCerMessage(key(event), message(event));
                if ( resultCode == ResultCode.SUCCESS ) {
                  try {
                    context.sendCeaMessage(resultCode, message(event), null);
                    swithToNextState(OKAY); // if other connection is win
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
          new MyState() // STOPPING
          {
            public boolean processEvent(StateEvent event) {
              switch (type(event)) {
              case TIMEOUT_EVENT:
              case DPA_EVENT:
                swithToNextState(DOWN);
                break;
              case RECEIVE_MSG_EVENT:
                context.receiveMessage( message(event) );
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
