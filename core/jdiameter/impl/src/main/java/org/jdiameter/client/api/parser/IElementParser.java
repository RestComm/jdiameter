/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.parser;

import org.jdiameter.api.AvpDataException;

import java.net.InetAddress;
import java.util.Date;

/**
 * Basic interface for diameter basic elements parsers.
 */

public interface IElementParser {

    /**
     * Convert byte array to int
     * @param rawData byte representation of int value
     * @return int value
     * @throws AvpDataException
     */
    int bytesToInt(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to long
     * @param rawData byte representation of long value
     * @return long value
     * @throws AvpDataException
     */
    long bytesToLong(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to float
     * @param rawData byte representation of float value
     * @return float value
     * @throws AvpDataException
     */
    float bytesToFloat(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to double
     * @param rawData byte representation of double value
     * @return double value
     * @throws AvpDataException
     */
    double bytesToDouble(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to octet string
     * @param rawData byte representation of octet string value
     * @return octet string value
     * @throws AvpDataException
     */
    String bytesToOctetString(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to utf8 string
     * @param rawData byte representation of utf8 string value
     * @return utf8 string value
     * @throws AvpDataException
     */
    String bytesToUtf8String(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to date
     * @param rawData byte representation of date value
     * @return date value
     * @throws AvpDataException
     */
    Date bytesToDate(byte[] rawData) throws AvpDataException;

    /**
     * Convert byte array to InetAddress
     * @param rawData byte representation of InetAddress value
     * @return InetAddress value
     * @throws AvpDataException
     */
    InetAddress bytesToAddress(byte[] rawData) throws AvpDataException;

    /**
     * Convert int to byte array representation
     * @param value int value
     * @return byte array
     */
    byte[] int32ToBytes(int value);

    /**
     * Convert long to 4-byte  array representation
     * @param value long value
     * @return byte array
     */
    byte [] intU32ToBytes(long value);

    /**
     * Convert long to byte array representation
     * @param value long value
     * @return byte array
     */
    byte[] int64ToBytes(long value);

    /**
     * Convert float to byte array representation
     * @param value float value
     * @return byte array
     */
    byte[] float32ToBytes(float value);

    /**
     * Convert double to byte array representation
     * @param value double value
     * @return byte array
     */
    byte[] float64ToBytes(double value);

    /**
     * Convert octet string to byte array representation
     * @param value octet string value
     * @return byte array
     * @throws ParseException
     */
    byte[] octetStringToBytes(String value) throws ParseException;

    /**
     * Convert utf8 string to byte array representation
     * @param value utf8 string value
     * @return byte array
     * @throws ParseException
     */
    byte[] utf8StringToBytes(String value) throws ParseException;

    /**
     * Convert InetAddress to byte array representation
     * @param value InetAddress value
     * @return byte array
     */
    byte[] addressToBytes(InetAddress value);

    /**
     * Convert Date to byte array representation
     * @param value Date value
     * @return byte array
     */
    byte[] dateToBytes(Date value);

    /**
     * Convert byte array to specefied object
     * @param rawData byte representation of InetAddress value
     * @param iface type of object
     * @return object instance
     * @throws AvpDataException
     */
    <T> T bytesToObject(java.lang.Class<?> iface, byte[] rawData) throws AvpDataException;

    /**
     * Convert specified object to byte array representation
     * @param value object
     * @return byte array
     * @throws ParseException
     */
    byte[] objectToBytes(Object value) throws ParseException;
}
