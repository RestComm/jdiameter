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

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Arrays;
import java.util.Date;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class AvpImpl implements Avp {

  private static final long serialVersionUID = 1L;
  private static final ElementParser parser = new ElementParser();
  private int avpCode;
  private long vendorID;

  private boolean isMandatory = false;
  private boolean isEncrypted = false;
  private boolean isVendorSpecific = false;

  private byte[] rawData = null;
  private AvpSet groupedData = new AvpSetImpl();

  private static final Logger logger = LoggerFactory.getLogger(AvpImpl.class);

  AvpImpl(int code, int flags, long vnd, byte[] data) {
    avpCode  = code;
    //
    isMandatory = (flags & 0x40) != 0;
    isEncrypted = (flags & 0x20) != 0;
    isVendorSpecific = (flags & 0x80) != 0;
    //
    vendorID = vnd;
    
    if (data != null) { // any data string/int/encoded-grouped
    	rawData = Arrays.copyOf(data, data.length);
    }
  }

  AvpImpl(Avp avp) {
    avpCode     = avp.getCode();
    vendorID    = avp.getVendorId();
    isMandatory = avp.isMandatory();
    isEncrypted = avp.isEncrypted();
    isVendorSpecific = avp.isVendorId();
    try {
    	byte[] data = avp.getRaw();
    	if (data != null) { // simple AVP
    		rawData = Arrays.copyOf(data, data.length);
    	} else {
    		// grouped AVP
    		AvpSet grouped = avp.getGrouped();
    		
    		if (grouped != null) {
    			groupedData = parser.decodeAvpSet(parser.encodeAvpSet(grouped)); // copy all
    		}
    	}
    } catch (Exception e) {
      logger.error("Can not create Avp", e);
    }
  }

  public AvpImpl(int newCode, Avp avp) {
    this(avp);
    avpCode = newCode;
  }

  public int getCode() {
    return avpCode;
  }

  public boolean isVendorId() {
    return isVendorSpecific;
  }

  public boolean isMandatory() {
    return isMandatory;
  }

  public boolean isEncrypted() {
    return isEncrypted;
  }

  public long getVendorId() {
    return vendorID;
  }

  public byte[] getRaw() throws AvpDataException {
    return rawData;
  }

  public byte[] getOctetString() throws AvpDataException {
    return rawData;
  }

  public String getUTF8String() throws AvpDataException {
    try {
      return parser.bytesToUtf8String(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public int getInteger32() throws AvpDataException {
    try {
      return parser.bytesToInt(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getInteger64() throws AvpDataException {
    try {
      return parser.bytesToLong(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getUnsigned32() throws AvpDataException {
    try {
      byte[] u32ext = new byte[8];
      System.arraycopy(rawData, 0, u32ext, 4, 4);
      return parser.bytesToLong(u32ext);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public long getUnsigned64() throws AvpDataException {
    try {
      return parser.bytesToLong(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public float getFloat32() throws AvpDataException {
    try {
      return parser.bytesToFloat(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public double getFloat64() throws AvpDataException {
    try {
      return parser.bytesToDouble(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public InetAddress getAddress() throws AvpDataException {
    try {
      return parser.bytesToAddress(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public Date getTime() throws AvpDataException {
    try {
      return parser.bytesToDate(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public String getDiameterIdentity() throws AvpDataException {
    try {
      return parser.bytesToOctetString(rawData);
    }
    catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public URI getDiameterURI() throws AvpDataException {
    try {
      return new URI(parser.bytesToOctetString(rawData));
    }
    catch (URISyntaxException e) {
      throw new AvpDataException(e, this);
    }
    catch (UnknownServiceException e) {
      throw new AvpDataException(e, this);
    }
  }

  public AvpSet getGrouped() throws AvpDataException {
    try {
    	if (rawData != null) {
    		return parser.decodeAvpSet(rawData);
    	} else {
    		return groupedData;
    	}
    } catch (Exception e) {
      throw new AvpDataException(e, this);
    }
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;  
  }

  public byte[] getRawData() {
    return rawData;
  }

  // Caching toString.. Avp shouldn't be modified once created.
  private String toString;

  @Override
  public String toString() {
    if(toString == null) {
    	this.toString = new StringBuffer("AvpImpl [avpCode=").append(avpCode).append(", vendorID=").append(vendorID)
    		  .append(", len=").append((rawData != null) ? rawData.length : null).append("]@").append(super.hashCode()).toString(); 
    }

    return this.toString;
  }
}
