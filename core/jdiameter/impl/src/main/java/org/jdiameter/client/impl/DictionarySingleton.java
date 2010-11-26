/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.client.impl;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.api.validation.ValidatorLevel;

/**
 * Util class. Makes it easier to access Dictionary instance as singleton.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public class DictionarySingleton {

  private static Dictionary SINGLETON;

  private DictionarySingleton() {
    // defeat instantiation
  }

  public static Dictionary getDictionary() {
    return SINGLETON;
  }

  static void init(String clazz, boolean validatorEnabled, ValidatorLevel validatorSendLevel, ValidatorLevel validatorReceiveLevel) throws InternalException {
    try {
      SINGLETON = (Dictionary) Class.forName(clazz).getField("INSTANCE").get(null);
      SINGLETON.setEnabled(validatorEnabled);
      SINGLETON.setSendLevel(validatorSendLevel);
      SINGLETON.setReceiveLevel(validatorReceiveLevel);
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }
}
