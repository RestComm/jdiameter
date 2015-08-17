/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.impl.parser;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.InternalException;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.api.parser.IElementParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * 
 * erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ElementParser implements IElementParser {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ElementParser.class);
    /**
     * This is seconds shift (70 years in seconds) applied to date, 
     * since NTP date starts since 1900, not 1970.
     */
    private static final long SECOND_SHIFT = 2208988800L;
    
    private static final int INT_INET4 = 1;
    private static final int INT_INET6 = 2;
    
    private static final int INT32_SIZE = 4;
    private static final int INT64_SIZE = 8;
    private static final int FLOAT32_SIZE = 4;
    private static final int FLOAT64_SIZE = 8;

    public int bytesToInt(byte[] rawData) throws AvpDataException {
      return prepareBuffer(rawData, INT32_SIZE).getInt();
    }

    public long bytesToLong(byte[] rawData) throws AvpDataException {
      return prepareBuffer(rawData, INT64_SIZE).getLong();
    }

    public float bytesToFloat(byte[] rawData) throws AvpDataException {
       return prepareBuffer(rawData, INT32_SIZE).getFloat();
    }

    public double bytesToDouble(byte[] rawData) throws AvpDataException {
         return prepareBuffer(rawData, FLOAT64_SIZE).getDouble();
    }

    protected ByteBuffer prepareBuffer(byte [] bytes, int len) throws AvpDataException  {
        if (bytes.length != len)
            throw new AvpDataException("Incorrect data length");
        //ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        //buffer.put(bytes);
        //buffer.flip();
        return ByteBuffer.wrap(bytes);
    }

    public String bytesToOctetString(byte[] rawData) throws AvpDataException {
        try {
        	//TODO: veirfy ISO-8859-1 is correct here, according to google results its only ... western EU..
        	//TODO: verify this, it octet sting we can not discard some chars, we have no idea whats there....
        	// issue: http://code.google.com/p/mobicents/issues/detail?id=2757
//            char[] ca = new String(rawData, "iso-8859-1").toCharArray();
//            StringBuffer rc = new StringBuffer(ca.length);
//            
//            for (char c:ca)
//                if (c != (char)0x0) 
//                {	//easier to debug...
//                	rc.append(c);
//                }
//          
//            return rc.toString();
        	return new String(rawData, "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new AvpDataException("Invalid data type", e);
        }
    }

    public String bytesToUtf8String(byte[] rawData) throws AvpDataException {
        try {
            char[] ca = new String(rawData, "utf8").toCharArray();
            StringBuffer rc = new StringBuffer(ca.length);
            for (char c:ca)
                if (c != (char)0x0) rc.append(c);
            return rc.toString();
        } catch (Exception e) {
             throw new AvpDataException("Invalid data type", e);
        }
    }

    public Date bytesToDate(byte[] rawData) throws AvpDataException {
        try {
            byte[] tmp = new byte[8];
            System.arraycopy(rawData, 0 , tmp, 4, 4);
            return new Date(((bytesToLong(tmp) - SECOND_SHIFT) * 1000L));
        } catch (Exception e) {
            throw new AvpDataException(e);
        }
    }

    public InetAddress bytesToAddress(byte[] rawData) throws AvpDataException {
        InetAddress inetAddress;
        byte[] address;
        try {
            if (rawData[INT_INET4] == INT_INET4) {
                address = new byte[4];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet4Address.getByAddress(address);
            } else {
                address = new byte[16];
                System.arraycopy(rawData, 2, address, 0, address.length);
                inetAddress = Inet6Address.getByAddress(address);
            }
        } catch (Exception e) {
            throw new AvpDataException(e);
        }
        return inetAddress;
    }

    public byte [] int32ToBytes(int value){
        byte [] bytes = new byte[INT32_SIZE];
        //ByteBuffer buffer = ByteBuffer.allocate(INT32_SIZE);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putInt(value);
        //buffer.flip();
        //buffer.get(bytes);
        return bytes;
    }

    public byte [] intU32ToBytes(long value){
        // FIXME: this needs to reworked!
        byte [] bytes = new byte[INT32_SIZE];
        ByteBuffer buffer = ByteBuffer.allocate(INT64_SIZE);
        buffer.putLong(value);
        buffer.flip();
        buffer.get(bytes);
        buffer.get(bytes);
        return bytes;
    }

    public byte [] int64ToBytes(long value){
        byte [] bytes = new byte[INT64_SIZE];
        //ByteBuffer buffer = ByteBuffer.allocate(INT64_SIZE);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putLong(value);
        //buffer.flip();
        //buffer.get(bytes);
        return bytes;
    }

    public byte [] float32ToBytes(float value){
        byte [] bytes = new byte[FLOAT32_SIZE];
        //ByteBuffer buffer = ByteBuffer.allocate(FLOAT32_SIZE);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putFloat(value);
        //buffer.flip();
        //buffer.get(bytes);
        return bytes;
    }

    public byte [] float64ToBytes(double value){
        byte [] bytes = new byte[FLOAT64_SIZE];
        //ByteBuffer buffer = ByteBuffer.allocate(FLOAT64_SIZE);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.putDouble(value);
        //buffer.flip();
        //buffer.get(bytes);
        return bytes;
    }

    public byte[] octetStringToBytes(String value) throws ParseException{
        try {
            return value.getBytes("iso-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            throw new ParseException(e);
        }
    }

    public byte[] utf8StringToBytes(String value) throws ParseException {
        try {
            return value.getBytes("utf8");
        }
        catch (Exception e) {
            throw new ParseException(e);
        }
    }

    public byte[] addressToBytes(InetAddress address) {
        byte byteAddrOrig[] = address.getAddress();

        byte[] data = new byte[byteAddrOrig.length + 2];

        int addrType = address instanceof Inet4Address ? INT_INET4 : INT_INET6;
        data[0] = (byte) ((addrType >> 8) & 0xFF);
        data[INT_INET4] = (byte) ((addrType >> 0) & 0xFF);

        System.arraycopy(byteAddrOrig, 0, data, 2, byteAddrOrig.length);
        return data;
    }

    public byte[] dateToBytes(Date date) {
        byte[] data = new byte[4];
        System.arraycopy(int64ToBytes((date.getTime()/1000L) + SECOND_SHIFT), 4, data, 0, 4);
        return data;
    }

    public <T> T bytesToObject(java.lang.Class<?> iface, byte[] rawdata) throws AvpDataException {
        return null;
    }

    public byte [] objectToBytes(Object data) throws ParseException {
        return null;
    }

    public AvpSetImpl decodeAvpSet(byte[] buffer) throws IOException, AvpDataException {
      return this.decodeAvpSet(buffer, 0);
    }
    
    private String fullDecode(byte[] buffer, int shift) throws IOException {
    	DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, shift, buffer.length));
    	StringBuilder sb = new StringBuilder();
    	
    	int read = shift;
    	while (read < buffer.length) {
    		int code = in.readInt();
    		int flags = in.readInt();
    		int consumed = 8;
    		
    		int vendor = ((flags & 0x80) != 0) ? in.readInt() : -1;
    		if (vendor != -1) {
    			consumed += 4;
    		}
    		
    		int length = (int)(flags & 0xFFFFFF) - consumed;
    		int padding = ((length % 4) != 0) ? (4 - (length % 4)) : 0;
    		length += padding;
    		
    		int num_read = ((read + consumed + length) > buffer.length) ? (buffer.length-consumed-read) : length;
    		int skip = num_read;
    		
    		while (skip > 0) {
    			skip -= in.skipBytes(skip);
    		}
    		
    		read += (consumed + num_read);
    		
    		sb.append(" code: ").append(code).append(" flags: ").append(flags)
    		  .append(" len: ").append(length).append(" pad: ").append(padding)
    		  .append(" skip: ").append(skip).append(" read: ").append(read).append("\n");
    	}
    	
    	return sb.toString();
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
      
      if (buffer == null) {
    	  return avps; // empty
      }
      
      int tmp, counter = shift;
      DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer, shift, buffer.length /* - shift ? */));

      while (counter < buffer.length) {
        int code = in.readInt();
        tmp = in.readInt();
        int flags = (tmp >> 24) & 0xFF;
        int length  = tmp & 0xFFFFFF;
        
        if(length < 0 || counter + length > buffer.length) {
        	logger.error("unable to decode code: {}, flags: {}, length: {}, counter: {}, shift:{}, buf_size: {}\n{}\n{}",
        			new Object[]{code, (short)flags, length, counter, shift, buffer.length, MessageParser.byteArrayToHexString(buffer), fullDecode(buffer,shift)});
        	throw new AvpDataException("Not enough data in buffer!");
        }
        long vendor = 0;
        if ((flags & 0x80) != 0) {
          vendor = in.readInt();
        }
        // Determine body L = length - 4(code) -1(flags) -3(length) [-4(vendor)]
        byte[] rawData = new byte[length - (8 + (vendor == 0 ? 0 : 4))];
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
    	return encodeAvpSet(avps, null, "");
    }
    
    public byte[] encodeAvpSet(AvpSet avps, StringBuilder sb, String prefix) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
          DataOutputStream data = new DataOutputStream(out);
          for (Avp a : avps) {
        	  byte[] raw = a.getRaw();
              byte[] enc = encodeAvp(a);
              
              if (sb != null) {
            	  sb.append(prefix).append(a).append(", len: ").append((raw != null) ? raw.length : null)
            	  	.append(", enc: ").append(MessageParser.byteArrayToHexStringLine(enc)).append("\n");
              }
              
              data.write(enc);
          }
        } catch (Exception e) {
          logger.error("Error during encode avps", e);
        }
        return out.toByteArray();
    }
    
    public byte[] encodeAvp(Avp avp) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
          DataOutputStream data = new DataOutputStream(out);
          data.writeInt(avp.getCode());
          int flags = (byte) ((avp.getVendorId() != 0 ? 0x80 : 0) |
              (avp.isMandatory() ? 0x40 : 0) | (avp.isEncrypted() ? 0x20 : 0));
          
          byte[] raw = avp.getRaw();
          if (raw == null) {
        	  raw = encodeAvpSet(avp.getGrouped());
          }
          
          int origLength = raw.length + 8 + (avp.getVendorId() != 0 ? 4 : 0);
          // newLength is never used. Should it?
          //int newLength  = origLength;
          //if (newLength % 4 != 0) {
          //  newLength += 4 - (newLength % 4);
          //}
          data.writeInt(((flags << 24) & 0xFF000000) + origLength);
          if (avp.getVendorId() != 0) {
            data.writeInt((int) avp.getVendorId());
          }
          data.write(raw);
          if (raw.length % 4 != 0) {
            for(int i = 0; i < 4 - raw.length % 4; i++) {
              data.write(0);
            }
          }
        }
        catch (Exception e) {
          logger.debug("Error during encode avp", e);
        }
        return out.toByteArray();
      }
}
