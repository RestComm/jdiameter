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

package org.jdiameter.api;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

/**
 * A set of data representing a message avp set. AvpSet is simple container of avps allows direct access to Avp by Avp code
 * or index of Avp. The AvpSet interface provides add/rem methods for appending new Avp and remove Avp from AvpSet.
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * Serializable interface allows use this class in SLEE Event objects
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:grzegorz.figiel@pro-ids.com"> Grzegorz Figiel [ProIDS] </a>
 *
 * @version 1.5.1  Final
 */
public interface AvpSet extends Iterable<Avp>, Serializable, Wrapper {

  /**
   * get AVP by code
   * @param avpCode code of Avp
   * @return Avp instance
   */
  Avp getAvp(int avpCode);

  /**
   * get AVP by position
   * @param index position of Avp
   * @return Avp instance
   */
  Avp getAvpByIndex(int index);

  /**
   * get AVP by code
   * @param avpCode code of Avp
   * @param vendorId vendor of Avp
   * @return Avp instance
   */
  Avp getAvp(int avpCode, long vendorId);

  /**
   * get AVP by code
   * @param avpCode code of Avp
   * @return array Avp instance
   */
  AvpSet getAvps(int avpCode);

  /**
   * get AVP by code
   * @param avpCode code of Avp
   * @param vendorId vendor of Avp
   * @return array Avp instance
   */
  AvpSet getAvps(int avpCode, long vendorId);

  /**
   * Get position of the first instance of the AVP
   * @param avpCode code of the Avp
   * @return index (position) of the first occurrence of the Avp. -1 in case Avp is not found
   */
  int getAvpIndex(int avpCode);

  /**
   * Get position of the first instance of the AVP
   * @param avpCode code of the Avp
   * @param vendorId vendorId of the Avp
   * @return index (position) of the first occurrence of the Avp. -1 in case Avp is not found
   */
  int getAvpIndex(int avpCode, long vendorId);

  /**
   * Remove AVPs with avpCode
   * @param avpCode code of Avp
   * @return array of removed Avps instance
   */
  AvpSet removeAvp(int avpCode);

  /**
   * Remove AVPs with avpCode
   * @param avpCode code of Avp
   * @param vendorId code of Vendor
   * @return array of removed Avps instance
   */
  AvpSet removeAvp(int avpCode, long vendorId);

  /**
   * Remove Avp by Avp index
   * @param index Avp position
   * @return Avp instance
   */
  Avp removeAvpByIndex(int index);

  /**
   * Return count of top Avps
   * @return size of top Avps
   */
  int size();

  /**
   * Return array of avp
   * @return array of avp
   */
  Avp[] asArray();

  /**
   * Append avp array as value
   * @param value avp array
   */
  void addAvp(Avp... value);

  /**
   * Append array of avps
   * @param value avp array from AvpSet container
   */
  void addAvp(AvpSet value);

  /**
   * Append byte[] AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, byte[] value);

  /**
   * Append byte[] AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, byte[] value, boolean mFlag, boolean pFlag);

  /**
   * Append byte[] AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, byte[] value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append int AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, int value);

  /**
   * Append int AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, int value, boolean mFlag, boolean pFlag);

  /**
   * Append int AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, int value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param asUnsignedInt32 true if value is unsigned integer 32 type
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value, boolean asUnsignedInt32);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asUnsignedInt32 true if value is unsigned integer 32 type
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUnsignedInt32);

  /**
   * Append long(integer64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asUnsignedInt32 true if value is unsigned integer 32 type
   * @return Avp instance
   */
  Avp addAvp(int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag, boolean asUnsignedInt32);

  /**
   * Append float AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, float value);

  /**
   * Append float AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, float value, boolean mFlag, boolean pFlag);

  /**
   * Append float AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, float value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append double(float64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, double value);

  /**
   * Append double(float64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, double value, boolean mFlag, boolean pFlag);

  /**
   * Append double(float64) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, double value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append String(UTF-8 or Octet) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param asOctetString true if it octet string
   * @return Avp instance
   */
  Avp addAvp(int avpCode, String value, boolean asOctetString);

  /**
   * Append String(UTF-8 or Octet) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asOctetString  true if it octet string
   * @return Avp instance
   */
  Avp addAvp(int avpCode, String value, boolean mFlag, boolean pFlag, boolean asOctetString);

  /**
   * Append String(UTF-8 or Octet) AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asOctetString true if it octet string
   * @return Avp instance
   */
  Avp addAvp(int avpCode, String value, long vendorId, boolean mFlag, boolean pFlag, boolean asOctetString);

  /**
   * Append URI AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, URI value);

  /**
   * Append URI AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, URI value, boolean mFlag, boolean pFlag);

  /**
   * Append URI AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, URI value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append Address AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp addAvp(int avpCode, InetAddress value);

  /**
   * Append Address AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, InetAddress value, boolean mFlag, boolean pFlag);

  /**
   * Append Address AVP
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, InetAddress value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append Diameter Time AVP
   * @param avpCode code of Avp
   * @param date value
   * @return Avp instance
   */
  Avp addAvp(int avpCode, Date date);

  /**
   * Append Diameter Time AVP
   * @param avpCode code of Avp
   * @param date value
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, Date date, boolean mFlag, boolean pFlag);

  /**
   * Append Diameter Time AVP
   * @param avpCode code of Avp
   * @param date value
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp addAvp(int avpCode, Date date, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Append Grouped AVP
   * @param avpCode code of Avp
   * @return AvpSet instance
   */
  AvpSet addGroupedAvp(int avpCode);

  /**
   * Append Grouped AVP
   * @param avpCode code of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return AvpSet instance
   */
  AvpSet addGroupedAvp(int avpCode, boolean mFlag, boolean pFlag);

  /**
   * Append Grouped AVP
   * @param avpCode code of Avp
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return AvpSet instance
   */
  AvpSet addGroupedAvp(int avpCode, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert AvpSet
   * @param index index of inserted avp
   * @param value avp array
   */
  void insertAvp(int index, Avp... value);

  /**
   * Insert AvpSet
   * @param index index of inserted avp
   * @param value avp array from AvpSet container
   */
  void insertAvp(int index, AvpSet value);

  /**
   * Insert byte[] AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, byte[] value);

  /**
   * Insert byte[] AVP
   * @param avpCode code of Avp
   * @param index index of inserted avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, byte[] value, boolean mFlag, boolean pFlag);

  /**
   * Insert byte[] AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, byte[] value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert int AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, int value);

  /**
   * Insert int AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, int value, boolean mFlag, boolean pFlag);

  /**
   * Insert int AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, int value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param asUnsignedInt32 true if value is unisignet integer 32 type
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value, boolean asUnsignedInt32);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asUnsignedInt32 true if value is unisignet integer 32 type
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUnsignedInt32);

  /**
   * Insert long(integer64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asUnsignedInt32 true if value is unisignet integer 32 type
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag, boolean asUnsignedInt32);

  /**
   * Insert float AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, float value);

  /**
   * Insert float AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, float value, boolean mFlag, boolean pFlag);

  /**
   * Insert float AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, float value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert double(float64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, double value);

  /**
   * Insert double(float64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, double value, boolean mFlag, boolean pFlag);

  /**
   * Insert double(float64) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, double value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert String(UTF-8 or Octet) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param asOctetString true if it octet string
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, String value, boolean asOctetString);

  /**
   * Insert String(UTF-8 or Octet) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asOctetString  true if it octet string
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, String value, boolean mFlag, boolean pFlag, boolean asOctetString);

  /**
   * Insert String(UTF-8 or Octet) AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @param asOctetString true if it octet string
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, String value, long vendorId, boolean mFlag, boolean pFlag, boolean asOctetString);

  /**
   * Insert URI AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, URI value);

  /**
   * Insert URI AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, URI value, boolean mFlag, boolean pFlag);

  /**
   * Insert URI AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, URI value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert Address AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, InetAddress value);

  /**
   * Insert Address AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, InetAddress value, boolean mFlag, boolean pFlag);

  /**
   * Insert Address AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param value Avp data
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, InetAddress value, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert Diameter Time AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param date value
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, Date date);

  /**
   * Insert Diameter Time AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param date value
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, Date date, boolean mFlag, boolean pFlag);

  /**
   * Insert Diameter Time AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param date value
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return Avp instance
   */
  Avp insertAvp(int index, int avpCode, Date date, long vendorId, boolean mFlag, boolean pFlag);

  /**
   * Insert Grouped AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @return AvpSet instance
   */
  AvpSet insertGroupedAvp(int index, int avpCode);

  /**
   * Insert Grouped AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return AvpSet instance
   */
  AvpSet insertGroupedAvp(int index, int avpCode, boolean mFlag, boolean pFlag);

  /**
   * Insert Grouped AVP
   * @param index index of inserted avp
   * @param avpCode code of Avp
   * @param vendorId vendor of Avp
   * @param mFlag true set M flag/false clear M flag in header Avp
   * @param pFlag true set P flag/false clear P flag in header Avp
   * @return AvpSet instance
   */
  AvpSet insertGroupedAvp(int index, int avpCode, long vendorId, boolean mFlag, boolean pFlag);

}