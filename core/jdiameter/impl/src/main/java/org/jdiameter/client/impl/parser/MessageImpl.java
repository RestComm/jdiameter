package org.jdiameter.client.impl.parser;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.*;
import org.jdiameter.client.api.IEventListener;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.impl.helpers.Loggers;
import org.jdiameter.client.api.parser.DecodeException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageImpl implements IMessage {

    protected static Logger logger = Logger.getLogger(Loggers.Message.fullName());

    int state = STATE_NOT_SENT;

    short version = 1, flags;
    int commandCode;
    long applicationId;
    long hopByHopId;
    boolean notMutableHopByHop;
    long endToEndId;

    AvpSetImpl avpSet;

    boolean isNetworkRequest = false;

    transient IPeer peer;
    transient MessageParser parser;
    transient TimerTask timerTask;
    transient IEventListener listener;

    /**
     * Create empry message
     * @param parser
     * @param commandCode
     * @param appId
     */
    MessageImpl(MessageParser parser, int commandCode, long appId) {
        this.commandCode = commandCode;
        this.applicationId = appId;
        this.parser = parser;
        this.avpSet = new AvpSetImpl(parser);
        this.endToEndId = parser.getNextEndToEndId();
    }

    /**
     * Create empty message
     * @param parser
     * @param commandCode
     * @param applicationId
     * @param flags
     * @param hopByHopId
     * @param endToEndId
     * @param avpSet
     */
    MessageImpl(MessageParser parser, int commandCode, long applicationId, short flags,
                long hopByHopId, long endToEndId, AvpSetImpl avpSet) {
        this(parser, commandCode, applicationId);
        this.flags = flags;
        this.hopByHopId = hopByHopId;
        this.endToEndId = endToEndId;
        if (avpSet != null)
            this.avpSet = avpSet;
    }

    /**
     * Create empry message
     * @param metaData
     * @param parser
     * @param commandCode       getApplicationId
     * @param appId
     */
    MessageImpl(MetaData metaData, MessageParser parser, int commandCode, long appId) {
        this(parser, commandCode, appId);
        try {
            getAvps().addAvp(
               Avp.ORIGIN_HOST,
                metaData.getLocalPeer().getUri().getFQDN(),
                true, false, true
            );
        	
            getAvps().addAvp(
                Avp.ORIGIN_REALM,
                metaData.getLocalPeer().getRealmName(),
                true, false, true
            );
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not create message", e);
        }
    }

    /**
     * Create Answer
     * @param request parent request
     */
    private MessageImpl(MessageImpl request) {
        this(request.parser, request.getCommandCode(), request.getHeaderApplicationId());
        copyHeader(request);
        //
        setRequest(false);
        parser.copyBasicAvps(this, request, true);
    }

    public byte getVersion() {
        return (byte) version;
    }

    public boolean isRequest() {
        return (flags & 0x80) != 0;
    }

    public void setRequest(boolean b) {
        if (b)
            flags |= 0x80;
        else
            flags &= 0x7F;
    }

    public boolean isProxiable() {
        return (flags & 0x40) != 0;
    }

    public void setProxiable(boolean b) {
        if (b)
            flags |= 0x40;
        else
            flags &= 0xBF;
    }

    public boolean isError() {
        return (flags & 0x20) != 0;
    }

    public void setError(boolean b) {
        if (b)
            flags |= 0x20;
        else
            flags &= 0xDF;
    }

    public boolean isReTransmitted() {
        return (flags & 0x10) != 0;
    }

    public void setReTransmitted(boolean b) {
        if (b)
            flags |= 0x10;
        else
            flags &= 0xEF;
    }

    public int getCommandCode() {
        return this.commandCode;
    }

    public String getSessionId() {
        try {
            Avp avpSessionId = avpSet.getAvp(Avp.SESSION_ID);
            return avpSessionId != null ? avpSessionId.getUTF8String() : null;
        } catch(AvpDataException exc) {
            return null;
        }
    }

    public Answer createAnswer(long resultCode) {
        MessageImpl answer = new MessageImpl(this);
        try {
            answer.getAvps().addAvp(Avp.RESULT_CODE, resultCode, true, false, true);
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not create answer message", e);
        }
        answer.setRequest(false);
        return answer;
    }

    public Answer createAnswer(long vendorId, long experementalResultCode) {
        MessageImpl answer = new MessageImpl(this);
        try {
            AvpSet exp_code = answer.getAvps().addGroupedAvp(297, true, false);
            exp_code.addAvp(Avp.VENDOR_ID, vendorId, true, false, true);
            exp_code.addAvp(Avp.EXPERIMENTAL_RESULT_CODE, experementalResultCode, true, false, true);
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not create answer message", e);
        }
        answer.setRequest(false);
        return answer;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public ApplicationId getSingleApplicationId() {
        Set<ApplicationId> appIds = getApplicationIdAvps();
        ApplicationId first = null;
        for (ApplicationId id: appIds) {
            if (first == null)
                first = id;
            if (applicationId != 0 && id.getVendorId() == 0 && applicationId == id.getAuthAppId())
                return id;
            if (applicationId != 0 && id.getVendorId() == 0 && applicationId == id.getAcctAppId())
                return id;
            if (applicationId != 0 && ( applicationId == id.getAuthAppId() || applicationId == id.getAcctAppId()))
                return id;

        }
        return first;
    }

    public Set<ApplicationId> getApplicationIdAvps() {
        Set<ApplicationId> rc = new LinkedHashSet<ApplicationId>();
        try {
            AvpSet authAppId = avpSet.getAvps(Avp.AUTH_APPLICATION_ID);
            for (Avp anAuthAppId : authAppId) {
                rc.add(ApplicationId.createByAuthAppId(( anAuthAppId).getInteger32()));
            }
            AvpSet accAppId = avpSet.getAvps(Avp.ACCT_APPLICATION_ID);
            for (Avp anAccAppId : accAppId) {
                rc.add(ApplicationId.createByAccAppId(( anAccAppId).getInteger32()));
            }
            AvpSet specAppId = avpSet.getAvps(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
            for (Avp aSpecAppId : specAppId) {
                long vendorId = 0, acctApplicationId = 0, authApplicationId = 0;
                AvpSet avps = ( aSpecAppId).getGrouped();
                for (Avp localAvp : avps) {
                    if (localAvp.getCode() == Avp.VENDOR_ID)
                        vendorId = localAvp.getUnsigned32();
                    if (localAvp.getCode() == Avp.AUTH_APPLICATION_ID)
                        authApplicationId = localAvp.getUnsigned32();
                    if (localAvp.getCode() == Avp.ACCT_APPLICATION_ID)
                        acctApplicationId = localAvp.getUnsigned32();
                }
                if ( authApplicationId != 0 )
                    rc.add(ApplicationId.createByAuthAppId(vendorId, authApplicationId));
                if ( acctApplicationId != 0 )
                    rc.add(ApplicationId.createByAccAppId(vendorId, acctApplicationId));
            }
        } catch (Exception exception) {
            return new LinkedHashSet<ApplicationId>();
        }
        return rc;
    }


    public long getHopByHopIdentifier() {
        return hopByHopId;
    }

    public long getEndToEndIdentifier() {
        return endToEndId;
    }

    public AvpSet getAvps() {
        return avpSet;
    }

    protected void copyHeader(MessageImpl request) {
        endToEndId = request.endToEndId;
        hopByHopId = request.hopByHopId;
        version    = request.version;
        flags      = request.flags;
        peer       = request.peer;
    }

    public Avp getResultCode() {
        return getAvps().getAvp(Avp.RESULT_CODE);
    }

    public void setNetworkRequest(boolean isNetworkRequest) {
        this.isNetworkRequest = isNetworkRequest;
    }

    public boolean isNetworkRequest() {
        return isNetworkRequest;
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;
    }

    // Inner API
    public void setHopByHopIdentifier(long hopByHopId) {
        if (hopByHopId < 0 ) {
            this.hopByHopId = -hopByHopId;
            this.notMutableHopByHop = true;
        } else {
            if (!this.notMutableHopByHop)
                this.hopByHopId = hopByHopId;
        }
    }

    public void setEndToEndIdentifier(long endByEndId) {
        this.endToEndId = endByEndId;
    }

    public IPeer getPeer() {
        return peer;
    }

    public void setPeer(IPeer peer) {
        this.peer = peer; 
    }

    public int getState() {
        return state;
    }

    public long getHeaderApplicationId() {
        return applicationId;
    }

    public int getFlags() {
        return flags;
    }

    public void setState(int newState) {
        state = newState;
    }

    public void createTimer(ScheduledExecutorService scheduledFacility, long timeOut, TimeUnit timeUnit) {
        timerTask = new TimerTask(this);
        timerTask.setTimerHandler(
            scheduledFacility.schedule(timerTask, timeOut, timeUnit)
        );
    }

    public void runTimer() {
        if (timerTask != null && !timerTask.isDone() && !timerTask.isCancelled())
            timerTask.run();
    }

    public boolean isTimeOut() {
        return timerTask != null && timerTask.isDone() && !timerTask.isCancelled();
    }

    public void setListener(IEventListener listener) {
        this.listener = listener;
    }

    public IEventListener getEventListener() {
        return listener;
    }

    public void clearTimer() {
        if (timerTask != null)
            timerTask.cancel();
    }

    public String toString() {
        return "MessageImpl{" +
                "commandCode=" + commandCode +
                ", flags=" + flags +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageImpl message = (MessageImpl) o;

        if (applicationId != message.applicationId) return false;
        if (commandCode != message.commandCode) return false;
        if (endToEndId != message.endToEndId) return false;
        return hopByHopId == message.hopByHopId;
    }

    public int hashCode() {
        long result;
        result = commandCode;
        result = 31 * result + applicationId;
        result = 31 * result + hopByHopId;
        result = 31 * result + endToEndId;
        return new Long(result).hashCode();
    }

    public String getDuplicationKey() {
        try {
            return getDuplicationKey(
                    getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString(), getEndToEndIdentifier()
            );
        } catch (AvpDataException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public String getDuplicationKey(String host, long endToEndId) {
        return host + endToEndId;
    }

    public Object clone() {
        try {
            return parser.createMessage( parser.encodeMessage(this));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }    

    protected static class TimerTask implements Runnable{

        ScheduledFuture timerHandler;
        MessageImpl message;

        public TimerTask(MessageImpl message) {
            this.message = message;
        }

        public void setTimerHandler(ScheduledFuture timerHandler) {
            this.timerHandler = timerHandler;
        }

        public void run() {
            try {
                if (message != null && message.state != STATE_ANSWERED) {
                    IEventListener listener = null;
                    if ( message.listener  instanceof IEventListener)
                        listener = message.listener;
                    if (listener != null && listener.isValid()) {
                        if (message.peer != null) message.peer.remMessage(message);
                        message.listener.timeoutExpired(message);
                    }
                }
            } catch(Throwable e) {
                logger.log(Level.INFO, "Can not process timeout", e);
            }
        }

        public void cancel() {
            if (timerHandler != null) timerHandler.cancel(true);
            message = null;
        }

        public boolean isDone() {
            return timerHandler != null && timerHandler.isDone();
        }


        @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
        public boolean isCancelled() {
            return timerHandler == null || timerHandler.isCancelled();
        }
    }
}
