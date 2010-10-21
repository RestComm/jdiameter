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

import static org.jdiameter.api.Avp.ACCT_APPLICATION_ID;
import static org.jdiameter.api.Avp.AUTH_APPLICATION_ID;
import static org.jdiameter.api.Avp.SESSION_ID;
import static org.jdiameter.api.Avp.VENDOR_SPECIFIC_APPLICATION_ID;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageParser extends ElementParser implements IMessageParser {

  private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

  protected UIDGenerator endToEndGen = new UIDGenerator(
      (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) & 0xFFF) << 20
  );

 

  public MessageParser() {

  }

  public IMessage createMessage(ByteBuffer data) throws AvpDataException {
    // Read header
    try {
      byte[] message = data.array();
      long tmp;
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
      tmp = in.readInt();
      short version = (short) (tmp >> 24);
      if (version != 1) {
        throw new Exception("Illegal value of version " + version);
      }
      
      if (message.length != (tmp & 0x00FFFFFF)) {
        //throw new ParseException("Wrong length of data: " + (tmp & 0x00FFFFFF));
        throw new Exception("Wrong length of data: " + (tmp & 0x00FFFFFF));
      }
      
      tmp = in.readInt();
      short flags        = (short) ((tmp >> 24) & 0xFF);
      int commandCode    = (int) (tmp & 0xFFFFFF);
      long applicationId = ((long) in.readInt() << 32) >>> 32;
      long hopByHopId    = ((long) in.readInt() << 32) >>> 32;
      long endToEndId    = ((long) in.readInt() << 32) >>> 32;
      // Read body
      // byte[] body = new byte[message.length - 20];
      // System.arraycopy(message, 20, body, 0, body.length);
      // AvpSetImpl avpSet = decodeAvpSet(body);
      AvpSetImpl avpSet = decodeAvpSet(message, 20);

      return new MessageImpl(commandCode, applicationId, flags, hopByHopId, endToEndId, avpSet);
    }
    catch (Exception exc) {
      throw new AvpDataException(exc);
    }
  }

  public <T> T createMessage(Class<?> iface, ByteBuffer data) throws AvpDataException {
    if (iface == IMessage.class) {
      return (T) createMessage(data);
    }
    return null;
  }

  public <T> T createEmptyMessage(Class<?> iface, IMessage parentMessage) {
    if (iface == Request.class) {
      return (T) createEmptyMessage(parentMessage, parentMessage.getCommandCode());   
    }
    else {
      return null;
    }
  }

  public IMessage createEmptyMessage(IMessage prnMessage) {
    return createEmptyMessage(prnMessage, prnMessage.getCommandCode());
  }

  public IMessage createEmptyMessage(IMessage prnMessage, int commandCode) {
    //
    MessageImpl newMessage = new MessageImpl(
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
	  //left it here, but 
    Avp avp;
    // Copy session id's information
    {
      avp = prnMessage.getAvps().getAvp(SESSION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
      avp = prnMessage.getAvps().getAvp(Avp.ACC_SESSION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
      avp = prnMessage.getAvps().getAvp(Avp.ACC_SUB_SESSION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
      avp = prnMessage.getAvps().getAvp(Avp.ACC_MULTI_SESSION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
    }
    // Copy Applicatio id's information
    {
      avp = prnMessage.getAvps().getAvp(VENDOR_SPECIFIC_APPLICATION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
      avp = prnMessage.getAvps().getAvp(ACCT_APPLICATION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
      avp = prnMessage.getAvps().getAvp(AUTH_APPLICATION_ID);
      if (avp != null) {
        newMessage.getAvps().addAvp(new AvpImpl(avp));
      }
    }
    // Copy proxy information
    {
      avp = prnMessage.getAvps().getAvp(Avp.PROXY_INFO);
      if (avp != null) {
        AvpSet avps;
        try {
          avps = avp.getGrouped();
          for (Avp avpp : avps) {
            newMessage.getAvps().addAvp(new AvpImpl(avpp));
          }
        }
        catch (AvpDataException e) {
          logger.debug("Error copying Proxy-Info AVP", e);
        }
      }
    }
    // Copy route information
    {
      if (newMessage.isRequest()) {
        if (invertPoints) {
          // set Dest host
          avp = prnMessage.getAvps().getAvp(Avp.ORIGIN_HOST);
          if (avp != null) {
            newMessage.getAvps().addAvp(new AvpImpl(Avp.DESTINATION_HOST, avp));
          }
          // set Dest realm
          avp = prnMessage.getAvps().getAvp(Avp.ORIGIN_REALM);
          if (avp != null) {
            newMessage.getAvps().addAvp(new AvpImpl(Avp.DESTINATION_REALM, avp));
          }
        }
        else {
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
      }
//      // set Orig host and realm
//      try {
//        newMessage.getAvps().addAvp(Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
//        newMessage.getAvps().addAvp(Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
//      }
//      catch (Exception e) {
//        logger.debug("Error copying Origin-Host/Realm AVPs", e);
//      }
    }
  }

  public ByteBuffer encodeMessage(IMessage message) throws ParseException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      byte[] rawData = encodeAvpSet(message.getAvps());
      DataOutputStream data = new DataOutputStream(out);
      // Wasting processor time, are we ?
      // int tmp = (1 << 24) & 0xFF000000;
      int tmp = (1 << 24);
      tmp += 20 + rawData.length;
      data.writeInt(tmp);
      // Again, unneeded operation ?
      // tmp = (message.getFlags() << 24) & 0xFF000000;
      tmp = (message.getFlags() << 24);
      tmp += message.getCommandCode();
      data.writeInt(tmp);
      data.write(toBytes(message.getHeaderApplicationId()));
      data.write(toBytes(message.getHopByHopIdentifier()));
      data.write(toBytes(message.getEndToEndIdentifier())); 
      data.write(rawData);
    }
    catch (Exception e) {
      //logger.debug("Error during encode message", e);
      throw new ParseException("Failed to encode message.", e);
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

  public IMessage createEmptyMessage(int commandCode, long headerAppId) {
    return new MessageImpl(commandCode, headerAppId);
  }

  public <T> T createEmptyMessage(Class<?> iface, int commandCode, long headerAppId) {
    if (iface == IRequest.class) {
      return (T) new MessageImpl(commandCode, headerAppId);
    }
    return null;
  }


  public int getNextEndToEndId() {
    return endToEndGen.nextInt();
  }
}
