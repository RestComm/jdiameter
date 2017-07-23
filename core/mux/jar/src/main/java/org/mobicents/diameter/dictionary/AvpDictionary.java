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

package org.mobicents.diameter.dictionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.jdiameter.client.impl.DictionarySingleton;
import org.jdiameter.common.impl.validation.AvpRepresentationImpl;
import org.jdiameter.common.impl.validation.DictionaryImpl;

/**
 *
 * AvpDictionary.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @version 1.1
 */
public class AvpDictionary {

  private static transient Logger logger = Logger.getLogger(AvpDictionary.class);

  public static final AvpDictionary INSTANCE = new AvpDictionary();

  //dont like that, this is not the same instance as in AvpUtils... ech.
  private DictionaryImpl stackDictionary;

  private HashMap<AvpRepresentation, AvpRepresentation> avpMap = new HashMap<AvpRepresentation, AvpRepresentation>();

  private Map<String, AvpRepresentation> nameToCodeMap = new HashMap<String, AvpRepresentation>();

  private AvpDictionary() {
    // Exists only to defeat instantiation.
  }

  public void parseDictionary(String filename) throws Exception {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filename);
      parseDictionary(fis);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException e) {
          logger.error("Failed to close FileInputStream", e);
        }
        fis = null;
      }
    }
  }

  public void parseDictionary(InputStream is) throws Exception {
    // we override default conf here.
    this.stackDictionary = (DictionaryImpl) DictionarySingleton.getDictionary(is);
    this.avpMap.clear();
    this.nameToCodeMap.clear();

    //dont like that....
    // fill AVP Map
    for (org.jdiameter.api.validation.AvpRepresentation value:this.stackDictionary.getAvpMap().values()) {
      AvpRepresentation avp = new AvpRepresentation((AvpRepresentationImpl) value);
      this.avpMap.put(avp, avp);
      this.nameToCodeMap.put(avp.getName(), avp);
    }
  }

  public AvpRepresentation getAvp(int code) {
    return getAvp(code, 0);
  }

  public AvpRepresentation getAvp(int code, long vendorId) {
    AvpRepresentation avp = avpMap.get(getMapKey(code, vendorId));

    if (avp == null) {
      logger.warn("AVP with code " + code + " and Vendor-Id " + vendorId + " not present in dictionary!");
    }

    return avp;
  }

  public AvpRepresentation getAvp(String avpName) {
    AvpRepresentation avpKey = nameToCodeMap.get(avpName);

    return avpKey != null ? avpMap.get(avpKey) : null;
  }

  /**
   * @param code
   * @param vendorId
   * @return
   */
  private AvpRepresentation getMapKey(int code, long vendorId) {
    return new AvpRepresentation(code, vendorId);
  }

}
