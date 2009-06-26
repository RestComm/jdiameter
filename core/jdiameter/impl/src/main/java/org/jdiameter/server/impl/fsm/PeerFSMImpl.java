package org.jdiameter.server.impl.fsm;

import org.jdiameter.api.*;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.FsmEvent;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IStateMachine;
import static org.jdiameter.client.impl.fsm.PeerFSMImpl.CIntState.*;
import org.jdiameter.server.api.IStatistic;
import org.jdiameter.server.api.IStatisticRecord;
import org.jdiameter.server.impl.StatisticImpl;
import org.jdiameter.server.impl.StatisticRecordImpl;
import static org.jdiameter.server.impl.helpers.StatisticTypes.PEER_QUEUE_SIZE;

import java.util.concurrent.Executor;
import java.util.logging.Level;

public class PeerFSMImpl extends org.jdiameter.client.impl.fsm.PeerFSMImpl implements IStateMachine, ConfigurationListener {

    protected IStatisticRecord queueSize = new StatisticRecordImpl("FsmQueue", "Peer FSM queue size", PEER_QUEUE_SIZE,
            new StatisticRecordImpl.Counter() {
                public int getValueAsInt() {
                    return eventQueue.size();
                }
            }
    );
    protected IStatistic queueStat = new StatisticImpl("PeerFSM" , "PeerFSM statistic", queueSize);

    public Statistic getStatistic() {
        return queueStat;
    }

    public PeerFSMImpl(IContext context, Executor executor, Configuration config) {
        super(context, executor, config);
    }

    protected void loadTimeOuts(Configuration config) {
        super.loadTimeOuts(config);
        if (config instanceof MutableConfiguration)
            ((MutableConfiguration)config).addChangeListener(this, 0);
    }

    public boolean elementChanged(int i, Object data) {
         Configuration newConfig = (Configuration) data;
        super.loadTimeOuts(newConfig);
        return true;
    }

    protected abstract class MyState implements State {

        public void entryAction() {}

        public void exitAction() {}

        protected void doEndConnection() {
            if ( context.isRestoreConnection() ) {
                timer = REC_TIMEOUT + System.currentTimeMillis();
                swithToNextState(REOPEN);
            } else {
                swithToNextState(DOWN);
            }
        }

        protected void doDisconnect() {
            try {
                context.disconnect();
            } catch (Throwable e) {}
        }

        protected void setInActiveTimer() {
            timer = IAC_TIMEOUT - 2 + random.nextInt(5) + System.currentTimeMillis();
        }

        protected void setTimer(long value) {
            timer = value + System.currentTimeMillis();
        }

        protected String key(StateEvent event) {
            return ((FsmEvent)event).getKey();
        }

        protected IMessage message(StateEvent event) {
            return ((FsmEvent)event).getMessage();
        }

        protected EventTypes type(StateEvent event) {
            return (EventTypes) event.getType();
        }

        protected void clearTimer() {
            timer = 0;
        }
    }

    private State[] states;
    protected State[] getStates() {
        if (states == null)
            states = new State[] {
            new MyState() // OKEY
            {
                public void entryAction() {
                    // todo send buffered messages
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
                                if (watchdogSent)
                                    swithToNextState(SUSPECT);
                                else
                                    watchdogSent = true;
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DWR", exc);
                            	}
                                doDisconnect();
                                doEndConnection();
                            }
                            break;
                        case STOP_EVENT:
                            try {
                                // todo send user code;
                                context.sendDprMessage(ResultCode.SUCCESS);
                                setTimer(DPA_TIMEOUT);
                                swithToNextState(STOPPING);
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.INFO, "Can not send DPR", exc);
                            	}
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
                                context.sendDpaMessage( message(event),ResultCode.SUCCESS, null );
                            } catch (Throwable e) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DWR", e);
                            	}
                            }
                            doDisconnect();
                            swithToNextState(DOWN);
                            break;
                        case DWR_EVENT:
                            setInActiveTimer();
                            try {
                                context.sendDwaMessage( message(event), ResultCode.SUCCESS, null);
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DWA", exc);
                            	}
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
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send message", exc);
                            	}
                                doDisconnect();
                                doEndConnection();
                            }
                            break;
                        default:
                            logger.finest("Unknown event type:" + type(event) + " in state " + state);
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
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DPR", exc);
                            	}
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
                                context.sendDpaMessage( message(event), ResultCode.SUCCESS, null);
                            } catch (Throwable e) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DPA", e);
                            	}
                            }
                            doDisconnect();
                            swithToNextState(DOWN);
                            break;
                        case DWR_EVENT:
                            try {
                                context.sendDwaMessage( message(event), ResultCode.SUCCESS, null);
                                swithToNextState(OKAY);
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send DWR", exc);
                            	}
                                doDisconnect();
                                swithToNextState(DOWN);
                            }
                            break;
                        case RECEIVE_MSG_EVENT:
                            clearTimer();
                            context.receiveMessage( message(event) );
                            swithToNextState(OKAY);
                            break;
                        case SEND_MSG_EVENT:
                            // todo buffering
                            throw new IllegalStateException("Connection is down");
                        default:
                        	if(logger.isLoggable(Level.FINEST))
                        	{
                        		logger.finest("Unknown event type:" + type(event) + " in state " + state);
                        	}
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
                                if ( !context.isConnected() ) context.connect();                              
                                context.sendCerMessage();
                                setTimer(CEA_TIMEOUT);
                                swithToNextState(INITIAL);
                            } catch (Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Connect error", exc);
                            	}
                                doEndConnection();
                            }
                            break;
                        case CER_EVENT:
                            int resultCode = context.processCerMessage(key(event), message(event));
                            if ( resultCode == ResultCode.SUCCESS ) {
                                try {
                                    context.sendCeaMessage(resultCode, message(event), null);
                                    swithToNextState(OKAY);
                                } catch (Exception e) {
                                    doDisconnect();  // !
                                    doEndConnection();
                                }
                            } else {
                                try {
                                    context.sendCeaMessage(resultCode, message(event),  null);
                                } catch (Exception e) {                                    
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
                        	if(logger.isLoggable(Level.FINEST))
                        	{
                        		logger.finest("Unknown event type:" + type(event) + " in state " + state);
                        	}
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
                            } catch(Throwable exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                            		logger.log(Level.SEVERE, "Can not send CER", exc);
                            	}
                                setTimer(REC_TIMEOUT);
                            }
                            break;
                        case TIMEOUT_EVENT:
                            try {                              
                                context.connect();
                            } catch (Exception exc) {
                            	if(logger.isLoggable(Level.SEVERE))
                            	{
                                	logger.log(Level.SEVERE, "Can not connect", exc);
                                }
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
                        	if(logger.isLoggable(Level.FINEST))
                        	{
                        		logger.finest("Unknown event type:" + type(event) + " in state " + state);
                        	}
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
                            } else {
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
                                } catch (Exception e) {
                                	if(logger.isLoggable(Level.SEVERE))
                                	{
                                		logger.log(Level.SEVERE,"Failed on CER event",e);
                                	}
                                    doDisconnect();
                                    doEndConnection();
                                }
                            } else
                            if (resultCode == -1 || resultCode == ResultCode.NO_COMMON_APPLICATION) {
                                doDisconnect();
                                doEndConnection();
                            }
                            break;
                        case SEND_MSG_EVENT:
                            // todo buffering
                            throw new IllegalStateException("Connection is down");
                        default:
                        	if(logger.isLoggable(Level.FINEST))
                        	{
                        		logger.finest("Unknown event type:" + type(event) + " in state " + state);
                        	}
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
                        	if(logger.isLoggable(Level.FINEST))
                        	{
                        		logger.finest("Unknown event type:" + type(event) + " in state " + state);
                        	}
                            return false;
                    }
                    return true;
                }
            }
        };
        return states;
    }
}
