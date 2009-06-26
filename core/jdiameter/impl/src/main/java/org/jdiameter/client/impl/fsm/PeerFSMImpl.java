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

import org.jdiameter.api.Configuration;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.app.State;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.fsm.FsmEvent;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.client.impl.helpers.Loggers;
import static org.jdiameter.client.impl.helpers.Parameters.*;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeerFSMImpl implements IStateMachine {

    protected Logger logger = Logger.getLogger(Loggers.FSM.fullName());
    protected ConcurrentLinkedQueue<StateChangeListener> listeners;
    protected LinkedBlockingQueue<StateEvent> eventQueue;

    protected CIntState state = CIntState.DOWN;
    protected boolean watchdogSent;
    protected long timer;
    protected long CEA_TIMEOUT = 0, IAC_TIMEOUT = 0, REC_TIMEOUT = 0, DWA_TIMEOUT = 0, DPA_TIMEOUT = 0;
    protected final StateEvent timeOutEvent = new FsmEvent(EventTypes.TIMEOUT_EVENT);
    protected Random random = new Random();
    
    protected IContext context;
    private State[] states;
    private int predefSize;


    public static class CIntState {

        protected static int index;

        public static CIntState OKAY     = new CIntState("OKAY", PeerState.OKAY);
        public static CIntState SUSPECT  = new CIntState("SUSPECT",PeerState.SUSPECT);
        public static CIntState DOWN     = new CIntState("DOWN",PeerState.DOWN);
        public static CIntState REOPEN   = new CIntState("REOPEN",PeerState.REOPEN);
        public static CIntState INITIAL  = new CIntState("INITIAL",PeerState.INITIAL);
        public static CIntState STOPPING = new CIntState("STOPPING",PeerState.DOWN, true);

        private String name;
        private int ordinal;

        private Enum publicState;
        private boolean isInternal;

        public CIntState(String name, Enum publicState) {
            this.name = name;
            this.publicState = publicState;
            this.ordinal = index++;
        }

        public CIntState(String name,Enum publicState, boolean isInternal) {
            this.name = name;
            this.publicState = publicState;
            this.isInternal  = isInternal;
            this.ordinal = index++;
        }

        public int ordinal() {
            return ordinal;
        }

        public String name() {
            return name;
        }

        public Enum getPublicState() {
            return publicState;
        }

        public boolean isInternal() {
            return isInternal;
        }


        public String toString() {
            return name;
        }
    }

    public void removeStateChangeNotification(StateChangeListener stateChangeListener) {
        listeners.remove(stateChangeListener);
    }

    public PeerFSMImpl(IContext aContext, Executor executor, Configuration config) {
        context = aContext;
        predefSize = config.getIntValue( QueueSize.ordinal(), (Integer) QueueSize.defValue() );
        eventQueue = new LinkedBlockingQueue<StateEvent>(predefSize);
        listeners = new ConcurrentLinkedQueue<StateChangeListener>();
        loadTimeOuts(config);
        executor.execute(
            new Runnable() {
                public void run() {
                    while (true) {
                        StateEvent event;
                        try {
                            event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            logger.finest("Peer fsm stopped");
                            break;
                        }
                        try {
                            if (event != null)
                                getStates()[state.ordinal()].processEvent(event);
                            if (timer != 0 && timer < System.currentTimeMillis()) {
                                timer  = 0;
                                handleEvent( timeOutEvent );
                            }
                        } catch (Exception e) {
                            logger.log(Level.INFO, "Error during processing fsm event", e);
                        }
                    }
                }
            }
        );
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

    protected void swithToNextState(CIntState newState) {
        if (!newState.isInternal())
            for (StateChangeListener l : listeners)
                l.stateChanged(state.getPublicState(), newState.getPublicState());
        getStates()[newState.ordinal()].exitAction();
        logger.log(Level.FINEST, context.getPeerDescription() + " fsm swith state " + state + " -> " + newState);
        state = newState;
        getStates()[state.ordinal()].entryAction();
    }

    public boolean handleEvent(StateEvent event) throws InternalError, OverloadException {
        boolean rc;
        try {
             rc = eventQueue.offer(event, IAC_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new InternalError("Can not put event to fsm " + this.toString());
        }
        if ( !rc )
            throw new OverloadException("FSM overloaded");
        return true;
    }

    private long setInActiveTimer() {
      return IAC_TIMEOUT - 2 * 1000 + random.nextInt(5) * 1000 + System.currentTimeMillis();
    }

    public String toString() {
        return "PeerFSM{" +
                "context=" + context +
                ", state=" + state +
                '}';
    }

    public <E> E getState(Class<E> a) {
        if (a == PeerState.class)
            return (E) state.getPublicState();
        else
            return null;
    }

    protected State[] getStates() {
        if (states == null)
            states = new State[] {
                new State() // OKEY
                {
                    public void entryAction() {
                        timer = setInActiveTimer();
                        watchdogSent = false;
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case DISCONNECT_EVENT:
                                timer = REC_TIMEOUT + System.currentTimeMillis();
                                swithToNextState(CIntState.REOPEN);
                                break;
                            case TIMEOUT_EVENT:
                                try {
                                    context.sendDwrMessage();
                                    timer = DWA_TIMEOUT + System.currentTimeMillis();
                                    if (watchdogSent)
                                        swithToNextState(CIntState.SUSPECT);
                                    else
                                        watchdogSent = true;
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send DWR", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.REOPEN);
                                }
                                break;
                            case STOP_EVENT:
                                try {
                                    context.sendDprMessage(ResultCode.SUCCESS);
                                    timer = DPA_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.STOPPING);
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send DPR", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    swithToNextState(CIntState.DOWN);
                                }
                                break;
                            case RECEIVE_MSG_EVENT:
                                timer = setInActiveTimer();
                                context.receiveMessage((IMessage) event.getData());
                                break;
                            case DPR_EVENT:
                                try {
                                    context.sendDpaMessage((IMessage) event.getData(),ResultCode.SUCCESS, null );
                                    context.disconnect();
                                } catch (Throwable e) {
                                    logger.log(Level.INFO, "Can not send DWR", e);
                                }
                                swithToNextState(CIntState.DOWN);
                                break;
                            case DWR_EVENT:
                                timer = setInActiveTimer();
                                try {
                                    context.sendDwaMessage((IMessage) event.getData(), ResultCode.SUCCESS, null);
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send DWA", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    swithToNextState(CIntState.DOWN);
                                }
                                break;
                            case DWA_EVENT:
                                timer = setInActiveTimer();
                                watchdogSent = false;
                                break;
                            case SEND_MSG_EVENT:
                                try {
                                    context.sendMessage((IMessage) event.getData());
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send message", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.REOPEN);
                                }
                                break;
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;

                    }
                },
                new State() // SUSPECT
                {
                    public void entryAction() {
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case DISCONNECT_EVENT:
                                timer = REC_TIMEOUT + System.currentTimeMillis();
                                swithToNextState(CIntState.REOPEN);
                                break;
                            case TIMEOUT_EVENT:
                                try {
                                    context.disconnect();
                                } catch (Throwable e) {
                                }
                                timer = REC_TIMEOUT + System.currentTimeMillis();
                                swithToNextState(CIntState.REOPEN);
                                break;
                            case STOP_EVENT:
                                try {
                                    context.sendDprMessage(ResultCode.SUCCESS);
                                    timer = setInActiveTimer();
                                    swithToNextState(CIntState.STOPPING);
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send DPR", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    swithToNextState(CIntState.DOWN);
                                }
                                break;
                            case DPR_EVENT:
                                try {
                                    context.sendDpaMessage( (IMessage) event.getData(), ResultCode.SUCCESS, null);
                                    context.disconnect();
                                } catch (Throwable e) {
                                    logger.log(Level.INFO, "Can not send DPA", e);
                                }
                                swithToNextState(CIntState.DOWN);
                                break;
                            case DWA_EVENT:
                                swithToNextState(CIntState.OKAY);
                                break;
                            case DWR_EVENT:
                                try {
                                    context.sendDwaMessage((IMessage) event.getData(), ResultCode.SUCCESS, null);
                                    swithToNextState(CIntState.OKAY);
                                } catch (Throwable exc) {
                                    logger.log(Level.INFO, "Can not send DWR", exc);
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {
                                    }
                                    swithToNextState(CIntState.DOWN);
                                }
                                break;
                            case RECEIVE_MSG_EVENT:
                                context.receiveMessage((IMessage) event.getData());
                                swithToNextState(CIntState.OKAY);
                                break;
                            case SEND_MSG_EVENT:
                                throw new RuntimeException("Connection is down");
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;
                    }
                },
                new State() // DOWN
                {
                    public void entryAction() {
                        timer = 0;
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case START_EVENT:
                                try {
                                    context.connect();
                                    context.sendCerMessage();
                                    timer = CEA_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.INITIAL);
                                } catch (Throwable exc) {
                                    logger.log(Level.FINEST, "Connect error", exc);
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.REOPEN);
                                }
                                break;
                            case SEND_MSG_EVENT:
                                throw new RuntimeException("Connection is down");
                            case STOP_EVENT:
                            case DISCONNECT_EVENT:
                                break;
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;
                    }
                },
                new State() // REOPEN
                {
                    public void entryAction() {
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case CONNECT_EVENT:
                                try {
                                    context.sendCerMessage();
                                    timer = CEA_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.INITIAL);
                                } catch(Throwable exc) {
                                    logger.log(Level.INFO, "Can not send CER", exc);
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                }
                                break;
                            case TIMEOUT_EVENT:
                                try {
                                    context.connect();
                                } catch (Exception exc) {
                                    logger.log(Level.FINE, "Can not create connection", exc);
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                }
                                break;
                            case STOP_EVENT:
                                timer = 0;
                                try {
                                    context.disconnect();
                                } catch (Throwable e) {
                                }
                                swithToNextState(CIntState.DOWN);
                                break;
                            case DISCONNECT_EVENT:
                                break;
                            case SEND_MSG_EVENT:
                                throw new RuntimeException("Connection is down");
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;
                    }
                },
                new State() // INITIAL
                {

                    public void entryAction() {
                        timer = CEA_TIMEOUT + System.currentTimeMillis();
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case DISCONNECT_EVENT:
                                timer = REC_TIMEOUT + System.currentTimeMillis();
                                swithToNextState(CIntState.REOPEN);
                                break;
                            case TIMEOUT_EVENT:
                                try {
                                    context.disconnect();
                                } catch (Throwable e) {}
                                timer = REC_TIMEOUT + System.currentTimeMillis();
                                swithToNextState(CIntState.REOPEN);
                                break;
                            case STOP_EVENT:
                                timer = 0;
                                try {
                                    context.disconnect();
                                } catch (Throwable e) {}
                                swithToNextState(CIntState.DOWN);
                                break;
                            case CEA_EVENT:
                                timer = 0;
                                if ( context.processCeaMessage( ((FsmEvent)event).getKey(), ((FsmEvent)event).getMessage() ) ) {
                                    swithToNextState(CIntState.OKAY);
                                } else {
                                    try {
                                        context.disconnect();
                                    } catch (Throwable e) {}
                                    timer = REC_TIMEOUT + System.currentTimeMillis();
                                    swithToNextState(CIntState.REOPEN);
                                }
                                break;
                            case SEND_MSG_EVENT:
                                throw new RuntimeException("Connection is down");
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;
                    }
                },
                new State() // STOPPING
                {
                    public void entryAction() {
                    }

                    public void exitAction() {
                    }

                    public boolean processEvent(StateEvent event) {
                        switch (event.encodeType(EventTypes.class)) {
                            case TIMEOUT_EVENT:
                            case DPA_EVENT:
                                try {
                                  context.disconnect();
                                } catch (Exception e) {
                                  logger.log(Level.FINE, "Can not stop network client", e);
                                }
                                swithToNextState(CIntState.DOWN);
                                break;
                            case RECEIVE_MSG_EVENT:
                                context.receiveMessage((IMessage) event.getData());
                                break;
                            case SEND_MSG_EVENT:
                                throw new RuntimeException("Stack now is stopping");
                            case STOP_EVENT:
                            case DISCONNECT_EVENT:
                                try {
                                  context.disconnect();
                                } catch (Exception e) {
                                  logger.log(Level.FINE, "Can not stop network client", e);
                                }
                                break;
                            default:
                                logger.finest("Unknown event type:" + event.encodeType(EventTypes.class) + " in state " + state);
                                return false;
                        }
                        return true;
                    }
                },

        };
        return states;
    }

}
