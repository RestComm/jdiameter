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

package org.jdiameter.client.impl.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * This class allows to convert string to IPv4/IPv6 object instance
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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
