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
import static org.jdiameter.api.Avp.*;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.parser.DecodeException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.impl.helpers.Loggers;
import org.jdiameter.client.impl.helpers.UIDGenerator;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageParser extends ElementParser implements IMessageParser {

    protected Logger logger = Logger.getLogger(Loggers.Parser.fullName());
    protected UIDGenerator endToEndGen = new UIDGenerator(
            (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) & 0xFFF) << 20
    );

    MetaData metaData;

    public MessageParser(MetaData metaData) {
        this.metaData = metaData;
    }

    public IMessage createMessage(ByteBuffer data) throws AvpDataException {
        // Read header
        try {
            byte[] message = data.array();
            long tmp;
            DataInputStream in = new DataInputStream( new ByteArrayInputStream(message) );
            tmp = in.readInt();
            short version = (short) (tmp >> 24);
            if (version != 1)
                throw new Exception("Illegal value of version " + version);
            tmp = in.readInt();
            short flags   = (short) ( (tmp >> 24) & 0xFF );
            int commandCode = (int) (tmp & 0xFFFFFF);
            long applicationId = ((long)in.readInt() << 32) >>> 32;
            long hopByHopId = ((long)in.readInt() << 32) >>> 32;
            long endToEndId = ((long)in.readInt() << 32) >>> 32;
            // Read body
            byte[] body = new byte[message.length - 20];
            System.arraycopy(message, 20, body, 0, body.length);
            AvpSetImpl avpSet = decodeAvpSet(body);
            return new MessageImpl(
                this, commandCode, applicationId, flags, hopByHopId, endToEndId, avpSet
            );
        } catch(Exception exc) {
            throw new AvpDataException(exc);
        }
    }

    public AvpSetImpl decodeAvpSet(byte[] message) throws IOException {
        AvpSetImpl avps = new AvpSetImpl(this);
        int tmp, counter = 0;
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        while (counter < message.length) {
            int code = in.readInt();
            tmp = in.readInt();
            int flags = (tmp >> 24) & 0xFF;
            int length  = tmp & 0xFFFFFF;
            long vendor = 0;
            if ( (flags & 0x80) != 0 )
                vendor = in.readInt();
            byte[] rawData = new byte[length - (8 + (vendor == 0 ? 0:4))];
            in.read(rawData);
            if (length % 4 != 0)
                for (int i; length % 4 != 0; length += i) {
                    i = (int) in.skip((4 - length % 4));
                }
            AvpImpl avp = new AvpImpl(this, code, (short)flags, (int)vendor, rawData);
            avps.addAvp(avp);
            counter += length;
        }
        return avps;  
    }

    public <T> T createMessage(Class<?> iface, ByteBuffer data) throws AvpDataException {
        if (iface == IMessage.class) {
            return (T)createMessage(data);
        }
        return null;
    }

    public <T> T createEmptyMessage(Class<?> iface, IMessage parentMessage) {
        if (iface == Request.class) {
            return (T) createEmptyMessage(parentMessage, parentMessage.getCommandCode());   
        } else {
            return null;
        }
    }

    public IMessage createEmptyMessage(IMessage prnMessage) {
        return createEmptyMessage(prnMessage, prnMessage.getCommandCode());
    }

    public IMessage createEmptyMessage(IMessage prnMessage, int commandCode) {
        //
        MessageImpl newMessage = new MessageImpl(
                this,
                commandCode,
                prnMessage.getHeaderApplicationId(),
                (short) prnMessage.getFlags(),
                prnMessage.getHopByHopIdentifier(),
                endToEndGen.nextLong(),
                null
        );
        copyBasicAvps(newMessage, prnMessage, false);

        return newMessage;
    }

    void copyBasicAvps(IMessage newMessage, IMessage prnMessage, boolean invertPoints) {
        Avp avp;
        // Copy session id's information
        {
            avp = prnMessage.getAvps().getAvp(SESSION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));

            avp = prnMessage.getAvps().getAvp(Avp.ACC_SESSION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));

            avp = prnMessage.getAvps().getAvp(Avp.ACC_SUB_SESSION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));

            avp = prnMessage.getAvps().getAvp(Avp.ACC_MULTI_SESSION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));
        }
        // Copy Applicatio id's information
        {
            avp = prnMessage.getAvps().getAvp(VENDOR_SPECIFIC_APPLICATION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));

            avp = prnMessage.getAvps().getAvp(ACCT_APPLICATION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));

            avp = prnMessage.getAvps().getAvp(AUTH_APPLICATION_ID);
            if (avp != null)
                newMessage.getAvps().addAvp(new AvpImpl(this, avp));
        }
       // Copy proxy information
        {
            avp = prnMessage.getAvps().getAvp(Avp.PROXY_INFO);
            if (avp != null) {
                AvpSet avps;
                try {
                    avps = avp.getGrouped();
                    for (Avp avpp : avps)
                        newMessage.getAvps().addAvp(new AvpImpl(this, avpp));
                } catch (AvpDataException e) {
                    logger.log(Level.FINEST, "Error during copy proxy avp", e);
                }
            }
        }
        // Copy route information
        {
            if (invertPoints) {
                // set Dest host
                avp = prnMessage.getAvps().getAvp(Avp.ORIGIN_HOST);
                if (avp != null) {
                    newMessage.getAvps().addAvp(new AvpImpl(this, Avp.DESTINATION_HOST, avp));
                }
                // set Dest realm
                avp = prnMessage.getAvps().getAvp(Avp.ORIGIN_REALM);
                if (avp != null) {
                    newMessage.getAvps().addAvp(new AvpImpl(this, Avp.DESTINATION_REALM, avp));

                }
            } else {
                // set Dest host
                avp = prnMessage.getAvps().getAvp(Avp.DESTINATION_HOST);
                if (avp != null) {
                    newMessage.getAvps().addAvp(avp);
                }
                // set Dest realm
                avp = prnMessage.getAvps().getAvp(Avp.DESTINATION_REALM);
                if (avp != null) {
                    newMessage.getAvps().addAvp(avp);
                }
            }
            // set Orig host and rellm
            try {
                newMessage.getAvps().addAvp(
                        Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true
                );
                newMessage.getAvps().addAvp(
                        Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true
                );
            } catch (Exception e) {
                logger.log(Level.FINEST, "Error during copy orig destination avp", e);
            }
        }
    }

    public ByteBuffer encodeMessage(IMessage message) throws DecodeException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] rawData = encodeAvpSet((AvpSetImpl)message.getAvps());
            DataOutputStream data = new DataOutputStream(out);
            int tmp = (1 << 24) & 0xFF000000;
            tmp += 20 + rawData.length;
            data.writeInt(tmp);
            tmp = (message.getFlags() << 24) & 0xFF000000;
            tmp += message.getCommandCode();
            data.writeInt(tmp);
            data.write( toBytes(message.getHeaderApplicationId()));
            data.write( toBytes(message.getHopByHopIdentifier()));
            data.write( toBytes(message.getEndToEndIdentifier())); 
            data.write(rawData);
        } catch(Exception e) {
            logger.log(Level.INFO, "Error during encode message", e);
        }
        return prepareBuffer(out.toByteArray(), out.size());
    }

    private byte[] toBytes(long value) {
        byte[] data = new byte[4];
        data[0] = (byte) ((value >> 24) & 0xFF);
        data[1] = (byte) ((value >> 16) & 0xFF);
        data[2] = (byte) ((value >> 8) & 0xFF);
        data[3] = (byte) ((value) & 0xFF);
        return data;
    }


    public byte[] encodeAvpSet(AvpSet avps) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            DataOutputStream data = new DataOutputStream(out);
            for(Avp a: avps) {
                if (a instanceof AvpImpl) {
                    AvpImpl aImpl = (AvpImpl) a;
                    if (aImpl.rawData.length == 0 && aImpl.groupedData != null) {
                        aImpl.rawData = encodeAvpSet( a.getGrouped() );
                    }
                    data.write(encodeAvp( aImpl ));
                }
            }
        } catch(Exception e) {
            logger.log(Level.FINEST, "Error during encode avps", e);
        }
        return out.toByteArray();
    }

    public IMessage createEmptyMessage(int commandCode, long headerAppId) {
         return new MessageImpl(this, commandCode, headerAppId);
    }

    public <T> T createEmptyMessage(Class<?> iface, int commandCode, long headerAppId) {
        if (iface == IRequest.class) {
            return (T) new MessageImpl(metaData, this, commandCode, headerAppId);
        }
        return null;
    }

    public byte[] encodeAvp(AvpImpl avp) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            DataOutputStream data = new DataOutputStream(out);
            data.writeInt(avp.getCode());
            int flags = (byte) ( (avp.getVendorId() !=0 ? 0x80:0) |
                                 (avp.isMandatory() ? 0x40:0)     |
                                 (avp.isEncrypted() ? 0x20:0)
                               );
            int origLength = avp.getRaw().length + 8 + (avp.getVendorId() != 0 ? 4:0);
            int newLength  = origLength;
            if (newLength % 4 != 0)
                newLength += 4 - (newLength % 4);
            data.writeInt( ((flags << 24) & 0xFF000000 ) + origLength);
            if (avp.getVendorId() != 0)
                data.writeInt((int)avp.getVendorId());
            data.write(avp.getRaw());
            if(avp.getRaw().length % 4 != 0) {
                for(int i = 0; i < 4 - avp.getRaw().length % 4; i++) data.write(0);
            }
        } catch(Exception e) {
           logger.log(Level.FINEST, "Error during encode avp", e);
        }
        return out.toByteArray();
    }

    public int getNextEndToEndId() {
        return endToEndGen.nextInt();
    }
}
