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

package org.jdiameter.client.api.parser;

import java.net.InetAddress;
import java.util.Date;

import org.jdiameter.api.AvpDataException;

/**
 * Basic interface for diameter basic elements parsers.
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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
