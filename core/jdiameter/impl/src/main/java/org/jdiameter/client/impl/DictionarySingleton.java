/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.client.impl;

import java.io.InputStream;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.common.impl.validation.DictionaryImpl;

/**
 * Util class. Makes it easier to access Dictionary instance as singleton.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public class DictionarySingleton {

  private DictionarySingleton() {
    // defeat instantiation
  }

  public static Dictionary getDictionary() {
    return DictionaryImpl.getInstance((String) null);
  }

  public static Dictionary getDictionary(String confFile) {
    return DictionaryImpl.getInstance(confFile);
  }

  public static Dictionary getDictionary(InputStream is) {
    return DictionaryImpl.getInstance(is);
  }

  static void init(String clazz, boolean validatorEnabled, ValidatorLevel validatorSendLevel, ValidatorLevel validatorReceiveLevel) throws InternalException {
    try {
      Class.forName(clazz).getMethod("getInstance", String.class).invoke(null, new Object[] {null});
      DictionaryImpl.INSTANCE.setEnabled(validatorEnabled);
      DictionaryImpl.INSTANCE.setSendLevel(validatorSendLevel);
      DictionaryImpl.INSTANCE.setReceiveLevel(validatorReceiveLevel);
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }
}
