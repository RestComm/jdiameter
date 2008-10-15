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
import org.jdiameter.client.impl.helpers.Loggers;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

class AvpImpl implements Avp {

    final MessageParser parser;

    int avpCode;
    long vendorID;

    boolean isMandatory = false;
    boolean isEncrypted = false;
    boolean isVendorSpecific = false;

    byte [] rawData = new byte[0];
    AvpSet groupedData;

    protected Logger logger = Logger.getLogger(Loggers.AVP.fullName());

    AvpImpl(MessageParser messageParser, int code, int flags, long vnd, byte[] data) {
        parser   = messageParser;
        avpCode  = code;
        //
        isMandatory = (flags & 0x40) != 0;
        isEncrypted = (flags & 0x20) != 0;
        isVendorSpecific = (flags & 0x80) != 0;
        //
        vendorID = vnd;
        rawData  = data;
    }

    AvpImpl(MessageParser messageParser, Avp avp) {
        parser      = messageParser;
        avpCode     = avp.getCode();
        vendorID    = avp.getVendorId();
        isMandatory = avp.isMandatory();
        isEncrypted = avp.isEncrypted();
        isVendorSpecific = avp.isVendorId();
        try {
            rawData = avp.getRaw();
            if (rawData == null || rawData.length == 0)
                groupedData = avp.getGrouped();
        } catch (AvpDataException e) {
            logger.log(Level.FINEST, "Can not create Avp", e);
        }

    }

    public AvpImpl(MessageParser messageParser, int newCode, Avp avp) {
        this(messageParser, avp);
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

    public String getOctetString() throws AvpDataException {
        return parser.bytesToOctetString(rawData);
    }

    public String getUTF8String() throws AvpDataException {
        return parser.bytesToUtf8String(rawData);
    }    

     public int getInteger32() throws AvpDataException {
        return parser.bytesToInt(rawData);
    }

    public long getInteger64() throws AvpDataException {
        return parser.bytesToLong(rawData);
    }

    public long getUnsigned32() throws AvpDataException {
        return parser.bytesToInt(rawData);
    }

    public long getUnsigned64() throws AvpDataException {
       return parser.bytesToLong(rawData);
    }

    public float getFloat32() throws AvpDataException {
        return parser.bytesToFloat(rawData);
    }

    public double getFloat64() throws AvpDataException {
        return parser.bytesToDouble(rawData);
    }

    public InetAddress getAddress() throws AvpDataException {
        return parser.bytesToAddress(rawData);
    }

    public Date getTime() throws AvpDataException {
        return parser.bytesToDate(rawData);   
    }

    public String getDiameterIdentity() throws AvpDataException {
        return getOctetString();
    }

    public URI getDiameterURI() throws AvpDataException {
        try {
            return new URI(getOctetString());
        } catch (URISyntaxException e) {
            throw new AvpDataException(e);
        } catch (UnknownServiceException e) {
            throw new AvpDataException(e);
        }
    }

    public AvpSet getGrouped() throws AvpDataException {
        try {
            if (groupedData == null) {
                groupedData = parser.decodeAvpSet(rawData);
                rawData = new byte[0];
            }
            return groupedData;
        } catch (Exception e) {
            throw new AvpDataException(e);
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
}
