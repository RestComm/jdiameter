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

package org.jdiameter.client.impl.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.client.api.parser.IElementParser;
import org.jdiameter.client.api.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ElementParser implements IElementParser {

  private static final Logger logger = LoggerFactory.getLogger(ElementParser.class);
  /**
   * This is seconds shift (70 years in seconds) applied to date,
   * since NTP date starts since 1900, not 1970.
   */
  private static final long SECOND_SHIFT = 2208988800L;

  private static final int INT_INET4 = 1;
  private static final int INT_INET6 = 2;

  private static final int INT32_SIZE = 4;
  private static final int INT64_SIZE = 8;

  @Override
  public int bytesToInt(byte[] rawData) throws AvpDataException {
    // http://stackoverflow.com/a/9581858
    return
        (rawData[0] & 0xFF) << 24 |
        (rawData[1] & 0xFF) << 16 |
        (rawData[2] & 0xFF) << 8 |
        (rawData[3] & 0xFF) << 0;
  }

  @Override
  public long bytesToLong(byte[] rawData) throws AvpDataException {
    // http://stackoverflow.com/a/1026804
    return (rawData[0] & 0xFFL) << 56
        | (rawData[1] & 0xFFL) << 48
        | (rawData[2] & 0xFFL) << 40
        | (rawData[3] & 0xFFL) << 32
        | (rawData[4] & 0xFFL) << 24
        | (rawData[5] & 0xFFL) << 16
        | (rawData[6] & 0xFFL) << 8
        | (rawData[7] & 0xFFL) << 0;
  }

  public long bytesToUnsignedInt32(byte[] rawData) throws AvpDataException {
    byte[] u32ext = new byte[INT64_SIZE];
    System.arraycopy(rawData, 0, u32ext, 4, 4);
    return bytesToLong(u32ext);
  }

  @Override
  public float bytesToFloat(byte[] rawData) throws AvpDataException {
    // http://stackoverflow.com/a/14308765
    return Float.intBitsToFloat(bytesToInt(rawData));
  }

  @Override
  public double bytesToDouble(byte[] rawData) throws AvpDataException {
    return Double.longBitsToDouble(bytesToLong(rawData));
  }

  @Override
  public String bytesToOctetString(byte[] rawData) throws AvpDataException {
    try {
      return new String(rawData, "iso-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      throw new AvpDataException("Invalid data type", e);
    }
  }

  @Override
  public String bytesToUtf8String(byte[] rawData) throws AvpDataException {
    try {
      return new String(rawData, "utf8");
    }
    catch (UnsupportedEncodingException e) {
      throw new AvpDataException("Invalid data type", e);
    }
  }

  @Override
  public Date bytesToDate(byte[] rawData) throws AvpDataException {
    try {
      byte[] tmp = new byte[8];
      System.arraycopy(rawData, 0 , tmp, 4, 4);
      return new Date(((bytesToLong(tmp) - SECOND_SHIFT) * 1000L));
    }
    catch (Exception e) {
      throw new AvpDataException(e);
    }
  }

  @Override
  public InetAddress bytesToAddress(byte[] rawData) throws AvpDataException {
    InetAddress inetAddress;
    try {
      boolean isIPv6 = rawData[INT_INET4] != INT_INET4;
      byte[] address = new byte[isIPv6 ? 16 : 4];
      System.arraycopy(rawData, 2, address, 0, address.length);
      inetAddress = isIPv6 ? InetAddress.getByAddress(address) : InetAddress.getByAddress(address);
    }
    catch (Exception e) {
      throw new AvpDataException(e);
    }
    return inetAddress;
  }

  @Override
  public byte [] int32ToBytes(int value) {
    byte[] bytes = new byte[INT32_SIZE];
    bytes[0] = (byte)(value >> 24 & 0xFF);
    bytes[1] = (byte)(value >> 16 & 0xFF);
    bytes[2] = (byte)(value >>  8 & 0xFF);
    bytes[3] = (byte)(value >>  0 & 0xFF);
    return bytes;
  }

  @Override
  public byte [] intU32ToBytes(long value) {
    byte[] bytes = int64ToBytes(value);
    return new byte[]{bytes[4], bytes[5], bytes[6], bytes[7]};
  }

  @Override
  public byte [] int64ToBytes(long value) {
    byte[] bytes = new byte[INT64_SIZE];
    bytes[0] = (byte)(value >> 56 & 0xFF);
    bytes[1] = (byte)(value >> 48 & 0xFF);
    bytes[2] = (byte)(value >> 40 & 0xFF);
    bytes[3] = (byte)(value >> 32 & 0xFF);
    bytes[4] = (byte)(value >> 24 & 0xFF);
    bytes[5] = (byte)(value >> 16 & 0xFF);
    bytes[6] = (byte)(value >>  8 & 0xFF);
    bytes[7] = (byte)(value >>  0 & 0xFF);
    return bytes;
  }

  @Override
  public byte [] float32ToBytes(float value) {
    // http://stackoverflow.com/a/14308774
    return int32ToBytes(Float.floatToIntBits(value));
  }

  @Override
  public byte [] float64ToBytes(double value) {
    return int64ToBytes(Double.doubleToLongBits(value));
  }

  @Override
  public byte[] octetStringToBytes(String value) throws ParseException {
    try {
      return value.getBytes("iso-8859-1");
    }
    catch (UnsupportedEncodingException e) {
      throw new ParseException(e);
    }
  }

  @Override
  public byte[] utf8StringToBytes(String value) throws ParseException {
    try {
      return value.getBytes("utf8");
    }
    catch (Exception e) {
      throw new ParseException(e);
    }
  }

  @Override
  public byte[] addressToBytes(InetAddress address) {
    byte[] byteAddrOrig = address.getAddress();

    byte[] data = new byte[byteAddrOrig.length + 2];

    int addrType = address instanceof Inet4Address ? INT_INET4 : INT_INET6;
    data[0] = (byte) ((addrType >> 8) & 0xFF);
    data[INT_INET4] = (byte) ((addrType >> 0) & 0xFF);

    System.arraycopy(byteAddrOrig, 0, data, 2, byteAddrOrig.length);
    return data;
  }

  @Override
  public byte[] dateToBytes(Date date) {
    byte[] data = new byte[4];
    System.arraycopy(int64ToBytes((date.getTime() / 1000L) + SECOND_SHIFT), 4, data, 0, 4);
    return data;
  }

  @Override
  public <T> T bytesToObject(java.lang.Class<?> iface, byte[] rawdata) throws AvpDataException {
    return null;
  }

  @Override
  public byte [] objectToBytes(Object data) throws ParseException {
    return null;
  }

  public AvpSetImpl decodeAvpSet(byte[] buffer) throws IOException, AvpDataException {
    return this.decodeAvpSet(buffer, 0);
  }

  /**
   *
   * @param buffer
   * @param shift - shift in buffer, for instance for whole message it will have non zero value
   * @return
   * @throws IOException
   * @throws AvpDataException
   */
  public AvpSetImpl decodeAvpSet(byte[] buffer, int shift) throws IOException, AvpDataException {
    AvpSetImpl avps = new AvpSetImpl();
    int tmp, counter = shift;
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, shift, buffer.length /* - shift ? */));

    while (counter < buffer.length) {
      int code = in.readInt();
      tmp = in.readInt();
      int flags = (tmp >> 24) & 0xFF;
      int length  = tmp & 0xFFFFFF;
      if (length < 0 || counter + length > buffer.length) {
        throw new AvpDataException("Not enough data in buffer!");
      }
      long vendor = 0;
      boolean hasVendor = false;
      if ((flags & 0x80) != 0) {
        vendor = in.readInt();
        hasVendor = true;
      }
      // Determine body L = length - 4(code) -1(flags) -3(length) [-4(vendor)]
      byte[] rawData = new byte[length - (8 + (hasVendor ? 4 : 0))];
      in.read(rawData);
      // skip remaining.
      // TODO: Do we need to padd everything? Or on send stack should properly fill byte[] ... ?
      if (length % 4 != 0) {
        for (int i; length % 4 != 0; length += i) {
          i = (int) in.skip((4 - length % 4));
        }
      }
      AvpImpl avp = new AvpImpl(code, (short) flags, (int) vendor, rawData);
      avps.addAvp(avp);
      counter += length;
    }
    return avps;
  }

  public byte[] encodeAvpSet(AvpSet avps) {
    //ByteArrayOutputStream out = new ByteArrayOutputStream();
    DynamicByteArray dba = new DynamicByteArray(0);
    try {
      //DataOutputStream data = new DataOutputStream(out);
      for (Avp a : avps) {
        if (a instanceof AvpImpl) {
          AvpImpl aImpl = (AvpImpl) a;
          if (aImpl.rawData.length == 0 && aImpl.groupedData != null) {
            aImpl.rawData = encodeAvpSet(a.getGrouped());
          }
          //data.write(newEncodeAvp(aImpl));
          dba.add(encodeAvp(aImpl));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      logger.debug("Error during encode avps", e);
    }
    return dba.getResult();
  }

  protected class DynamicByteArray {

    private byte[] array;
    private int size;

    public DynamicByteArray(int cap) {
      array = new byte[cap > 0 ? cap : 256];
      size = 0;
    }

    public int get(int pos) {
      if (pos >= size) {
        throw new ArrayIndexOutOfBoundsException();
      }
      return array[pos];
    }

    public void add(byte[] bytes) {
      if (size + bytes.length > array.length) {
        byte[] newarray = new byte[array.length + bytes.length * 2];
        System.arraycopy(array, 0, newarray, 0, size);
        array = newarray;
      }
      System.arraycopy(bytes, 0, array, size, bytes.length);
      size += bytes.length;
    }

    public byte[] getResult() {
      return Arrays.copyOfRange(array, 0, size);
    }
  }

  public byte[] encodeAvp(AvpImpl avp) {
    try {
      int payloadSize = avp.getRaw().length;
      boolean hasVendorId = avp.getVendorId() != 0;
      int origLength = payloadSize + 8 + (hasVendorId ? 4 : 0);
      int tmp = payloadSize % 4;
      int paddingSize = tmp > 0 ? (4 - tmp) : 0;

      byte[] bCode = this.int32ToBytes(avp.getCode());
      int flags = (byte) ((hasVendorId ? 0x80 : 0) |
          (avp.isMandatory() ? 0x40 : 0) | (avp.isEncrypted() ? 0x20 : 0));
      byte[] bFlags = this.int32ToBytes(((flags << 24) & 0xFF000000) + origLength);
      byte[] bVendor = hasVendorId ? int32ToBytes((int) avp.getVendorId()) : new byte[0];
      return concat(origLength + paddingSize, bCode, bFlags, bVendor, avp.getRaw());
    }
    catch (Exception e) {
      logger.debug("Error during encode avp", e);
      return new byte[0];
    }
  }

  private byte[] concat(int length, byte[]... arrays) {
    if (length == 0) {
      for (byte[] array : arrays) {
        length += array.length;
      }
    }
    byte[] result = new byte[length];
    int pos = 0;
    for (byte[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    }
    return result;
  }

  protected ByteBuffer prepareBuffer(byte [] bytes, int len) throws AvpDataException  {
    if (bytes.length != len) {
      throw new AvpDataException("Incorrect data length");
    }
    return ByteBuffer.wrap(bytes);
  }

}
