/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * This class allows to convert string to IPv4/IPv6 object instance
 */
public class IPConverter {

    /**
     * Convert defined string to IPv4 object instance
     * @param address string representation of ip address
     * @return IPv4 object instance
     */
    public static InetAddress InetAddressByIPv4(String address) {
 		StringTokenizer addressTokens = new StringTokenizer(address, ".");
        byte[] bytes;
        if (addressTokens.countTokens() == 4)
            bytes = new byte[]{
                    getByBytes(addressTokens),
                    getByBytes(addressTokens),
                    getByBytes(addressTokens),
                    getByBytes(addressTokens)
            };
        else
           return null;

        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static byte getByBytes(StringTokenizer addressTokens) {
        int word = Integer.parseInt(addressTokens.nextToken());
        return (byte) (word & 0xff);
    }

    /**
     * Convert defined string to IPv6 object instance
     * @param address string representation of ip address
     * @return IPv6 object instance
     */
    public static InetAddress InetAddressByIPv6(String address) {
		StringTokenizer addressTokens = new StringTokenizer(address, ":");
         byte[] bytes = new byte[16];
        if (addressTokens.countTokens() == 8) {
            int count = 0;
            while (addressTokens.hasMoreTokens()) {
                int word = Integer.parseInt(addressTokens.nextToken(), 16);
                bytes[count * 2]     = (byte) ((word >> 8) & 0xff);
                bytes[count * 2 + 1] = (byte) (word & 0xff);
                count++;
            }
        } else
            return null;
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
