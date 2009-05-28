package org.jdiameter.client.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IEventListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

public abstract class BaseSessionImpl implements BaseSession {

    protected final long creationTime = System.currentTimeMillis();
    protected long lastAccessedTime = creationTime;
    protected boolean isValid = true;
    protected transient IContainer container;
    protected transient IMessageParser parser;
    protected NetworkReqListener reqListener;
    protected static final DiameterMessageValidator messageValidator = DiameterMessageValidator.getInstance();
    
    
    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public boolean isValid() {
        return isValid;
    }

    protected void genericSend(Message message, EventListener listener) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        if (isValid) {
        	if(BaseSessionImpl.messageValidator.isOn())
        	{
        		BaseSessionImpl.messageValidator.validate(message);
        	}
            long timeOut = container.getConfiguration().getLongValue(
                    MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue()
            );
            genericSend(message, listener, timeOut, TimeUnit.MILLISECONDS);
        } else {
            throw new IllegalDiameterStateException("Session already released");
        }
    }

    protected void genericSend(Message aMessage, EventListener listener, long timeout, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        if ( isValid ) {
            lastAccessedTime = System.currentTimeMillis();

            if(BaseSessionImpl.messageValidator.isOn())
        	{
            	
        		BaseSessionImpl.messageValidator.validate(aMessage);
        	}
            IMessage message = (IMessage) aMessage;
            IEventListener localListener = createListenerWrapper(listener);
            if (message.isRequest())
                message.setListener( localListener );

            if (message.getState() != IMessage.STATE_NOT_SENT && message.getState() != IMessage.STATE_ANSWERED)
                throw new IllegalDiameterStateException("Illegal state");

            message.createTimer(container.getScheduledFacility(), timeout, timeUnit);
            try {
                container.sendMessage(message);
            }catch(RouteException e)
            {
            	throw e;
            }
            catch (Exception e) {
                e.printStackTrace();
                message.clearTimer();
                throw new InternalException(e);
            }
        } else {
            throw new IllegalDiameterStateException("Session already released");
        }
    }

    protected IEventListener createListenerWrapper(final EventListener listener) {
        if (listener == null)
            return null;
        else
            return new MyEventListener(this, listener);
    }

    public Future<Message> send(final Message message) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        MyFuture future = new MyFuture();
        future.send(message);
        return future;
    }

    public Future<Message> send(Message message, long timeOut, TimeUnit timeUnit) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
        MyFuture future = new MyFuture();
        future.send(message, timeOut, timeUnit);
        return future;
    }

    private class MyFuture implements Future<Message> {

        private boolean canceled;
        private boolean done;
        private boolean timeOut;
        private Lock lock = new ReentrantLock();
        private CountDownLatch block = new CountDownLatch(1);
        private Message result;

        public boolean cancel(boolean mayInterruptIfRunning) {
            lock.lock();
            try {
                canceled = true;
                done = false;
                block.countDown();
            } finally {
                lock.unlock();
            }
            return true;
        }

        public boolean isCancelled() {
            return canceled;
        }

        public boolean isDone() {
            return done;
        }

        public Message get() throws InterruptedException, ExecutionException {
            try {
                block.await();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }

            Message rc = canceled ? null : result;
            result = null;
            return rc;
        }

        public Message get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            try {                
                block.await();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
            if (timeOut)
                throw new TimeoutException();
            Message rc = canceled ? null : result;
            result = null;
            return rc;
        }

        private IEventListener createListener() {
            return new IEventListener() {

                public void setValid(boolean value) {
                }

                public boolean isValid() {
                    return !canceled;
                }

                public void receivedSuccessMessage(Message r, Message a) {
                    lock.lock();
                    try {
                        if (!canceled) {
                            result = a;
                            canceled = false;
                            done = true;
                        }
                        block.countDown();
                    } finally {
                        lock.unlock();
                    }
                }

                public void timeoutExpired(Message message) {
                    lock.lock();
                    try {
                        if (!canceled) {
                            done = true;
                            timeOut = true;
                        }
                        block.countDown();
                    } finally {
                        lock.unlock();
                    }
                }
            };
        }

        public void send(Message message) throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
            genericSend(message, createListener());
        }

        public void send(Message message, long timeOut, TimeUnit timeUnit) throws RouteException, OverloadException, IllegalDiameterStateException, InternalException {
            genericSend(message, createListener(), timeOut, timeUnit);
        }
    }

    protected void appendAppId(ApplicationId appId, Message m) { // todo duplicate code look peerimpl 601 line
        if (appId == null) return;
        if (appId.getVendorId() == 0) {
            if (appId.getAcctAppId() != 0)
                m.getAvps().addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
            if (appId.getAuthAppId() != 0)
                m.getAvps().addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
        } else {
            AvpSet avp = m.getAvps().addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, true, false);
            avp.addAvp(Avp.VENDOR_ID, appId.getVendorId(), true, false, true);
            if (appId.getAuthAppId() != 0)
                avp.addAvp(Avp.AUTH_APPLICATION_ID, appId.getAuthAppId(), true, false, true);
            if (appId.getAcctAppId() != 0)
                avp.addAvp(Avp.ACCT_APPLICATION_ID, appId.getAcctAppId(), true, false, true);
        }
    }

    protected long getAppId(ApplicationId appId) {
        if (appId == null) return 0;
       // if (appId.getVendorId() == 0) {
        if (appId.getAcctAppId() != 0)
             return appId.getAcctAppId();
        if (appId.getAuthAppId() != 0)
             return appId.getAuthAppId();
       // }
        return appId.getVendorId();
    }    
}

class MyEventListener implements IEventListener {

    BaseSessionImpl session;
    EventListener listener;
    boolean isValid = true;

    public MyEventListener(BaseSessionImpl session, EventListener listener) {
        this.session = session;
        this.listener = listener;
    }

    public void setValid(boolean value) {
        isValid = value;
        if ( !isValid ) {
            session = null;
            listener = null;
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public void receivedSuccessMessage(Message request, Message answer) {
        if (isValid) {
            session.lastAccessedTime = System.currentTimeMillis();
            listener.receivedSuccessMessage(request, answer);
        }
    }

    public void timeoutExpired(Message message) {
        if (isValid) {
            session.lastAccessedTime = System.currentTimeMillis();
            listener.timeoutExpired(message);
        }
    }

    public int hashCode() {
        return listener == null ? 0 :listener.hashCode();
    }

    public boolean equals(Object obj) {
        return listener != null && listener.equals(obj);
    }

    public String toString() {
        return listener == null ? "null" : listener.toString();
    }
}
