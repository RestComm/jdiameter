/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, TeleStax Inc. and individual contributors
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

package org.jdiameter.client.impl.parser;

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

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MessageParser extends ElementParser implements IMessageParser {

  private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

  protected UIDGenerator endToEndGen = new UIDGenerator(
      (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) & 0xFFF) << 20
      );



  public MessageParser() {

  }

  @Override
  public IMessage createMessage(byte[] message) throws AvpDataException {
    // Read header
    try {
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

  @Override
  public IMessage createMessage(ByteBuffer data) throws AvpDataException {
    byte[] message = data.array();
    return createMessage(message);
  }

  @Override
  public <T> T createMessage(Class<?> iface, ByteBuffer data) throws AvpDataException {
    if (iface == IMessage.class) {
      return (T) createMessage(data);
    }
    return null;
  }

  @Override
  public <T> T createEmptyMessage(Class<?> iface, IMessage parentMessage) {
    if (iface == Request.class) {
      return (T) createEmptyMessage(parentMessage, parentMessage.getCommandCode());
    }
    else {
      return null;
    }
  }

  @Override
  public IMessage createEmptyMessage(IMessage prnMessage) {
    return createEmptyMessage(prnMessage, prnMessage.getCommandCode());
  }

  @Override
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
      AvpSet avps = prnMessage.getAvps().getAvps(Avp.PROXY_INFO);
      for (Avp piAvp : avps) {
        newMessage.getAvps().addAvp(new AvpImpl(piAvp));
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

  public static String byteArrayToHexString(byte[] in, boolean columnize) {
    if (in == null || in.length <= 0) {
      return "";
    }
    String pseudo = "0123456789ABCDEF";

    StringBuffer out = new StringBuffer(in.length * 3);

    for (int i = 0; i < in.length; i++) {
      byte ch = in[i];
      out.append(pseudo.charAt((int) ((ch & 0xF0) >> 4)));
      out.append(pseudo.charAt((int) (ch & 0x0F)));

      if (columnize) {
        if ((i + 1) % 16 == 0) {
          out.append("\n");
        }
        else if ((i + 1) % 4 == 0) {
          out.append(" ");
        }
      }
    }

    return out.toString();
  }

  public static String byteArrayToHexStringLine(byte[] in) {
    return byteArrayToHexString(in, false);
  }

  public static String byteArrayToHexString(byte[] in) {
    return byteArrayToHexString(in, true);
  }

  @Override
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
    try {
      return prepareBuffer(out.toByteArray(), out.size());
    }
    catch (AvpDataException ade) {
      throw new ParseException(ade);
    }
  }

  private byte[] toBytes(long value) {
    byte[] data = new byte[4];
    data[0] = (byte) ((value >> 24) & 0xFF);
    data[1] = (byte) ((value >> 16) & 0xFF);
    data[2] = (byte) ((value >> 8) & 0xFF);
    data[3] = (byte) ((value) & 0xFF);
    return data;
  }

  @Override
  public IMessage createEmptyMessage(int commandCode, long headerAppId) {
    return new MessageImpl(commandCode, headerAppId);
  }

  @Override
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
