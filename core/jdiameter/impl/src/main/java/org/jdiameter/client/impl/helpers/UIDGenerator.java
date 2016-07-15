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

package org.jdiameter.client.impl.helpers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provide uid range generator functionality
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UIDGenerator {  // todo remove or redesign

  private /*static*/ long value; // static causes repetitions
  private static final Lock mutex = new ReentrantLock();
  private static final ThreadLocal<Delta> ranges = new ThreadLocal<Delta>() {
    @Override
    protected synchronized Delta initialValue() {
      return new Delta();
    }
  };

  private static class Delta {
    long start;
    long stop;

    public long update(long value) {
      start = value;
      stop  = value + 1;
      return stop;
    }
  }

  /**
   * Create instance of class
   */
  public UIDGenerator() {
    value = System.currentTimeMillis();
  }

  /**
   * Create instance of class with predefined start value
   *
   * @param startValue start value of counter
   */
  public UIDGenerator(long startValue) {
    value = startValue;
  }

  /**
   * Return next uid as int
   *
   * @return  uid
   */
  public int nextInt() {
    return (int) (0x7FFFFFFF & nextLong());
  }

  /**
   * Return next uid as long
   *
   * @return uid as long
   */
  public long nextLong() {
    Delta d = ranges.get();
    if (d.start <= d.stop) {
      mutex.lock();
      value = d.update(value);
      mutex.unlock();
    }
    return d.start++;
  }
}
