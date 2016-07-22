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

package org.jdiameter.common.impl.statistic;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.jdiameter.api.StatisticRecord;
import org.jdiameter.common.api.statistic.IStatisticRecord;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class StatisticRecordImpl implements IStatisticRecord {

  protected boolean enable = true;
  protected String name;
  protected String description;
  protected Counters counter;

  protected AtomicLong value;
  protected ConcurrentLinkedQueue<StatisticRecord> childs = new ConcurrentLinkedQueue<StatisticRecord>();
  protected ValueHolder valueHolder;

  StatisticRecordImpl(String name) {
    this.name = name;
    this.value = new AtomicLong(0);
  }

  StatisticRecordImpl(String name, String description) {
    this(name);
    this.description = description;
  }

  StatisticRecordImpl(String name, Counters counter) {
    this(counter.name() + "." + name);
    this.counter = counter;
    this.description = counter.getDescription();
  }

  StatisticRecordImpl(String name, String description,  IStatisticRecord... childs) {
    this(name, description);
    this.childs.addAll(Arrays.asList(childs));
  }

  StatisticRecordImpl(String name, String description,  ValueHolder valueHolder) {
    this(name, description);
    this.valueHolder = valueHolder;
  }

  StatisticRecordImpl(String name, String description,  ValueHolder valueHolder, IStatisticRecord... childs) {
    this(name, description, valueHolder);
    this.childs.addAll(Arrays.asList(childs));
  }

  StatisticRecordImpl(String name, String description,  long value) {
    this(name, description);
    this.value = new AtomicLong(value);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public int getValueAsInt() {
    return valueHolder != null ? ((IntegerValueHolder) valueHolder).getValueAsInt() : (int) value.get();
  }

  @Override
  public double getValueAsDouble() {
    return valueHolder != null ? ((DoubleValueHolder) valueHolder).getValueAsDouble() :
      Double.longBitsToDouble(value.get());
  }

  @Override
  public long getValueAsLong() {
    return valueHolder != null ? ((LongValueHolder) valueHolder).getValueAsLong() : value.get();
  }


  @Override
  public void inc() {
    if (enable) {
      this.value.incrementAndGet();
    }
  }

  @Override
  public void inc(long delta) {
    if (enable) {
      this.value.addAndGet(delta);
    }
  }

  @Override
  public void setDoubleValue(double value) {
    if (enable) {
      this.value.set(Double.doubleToLongBits(value));
    }
  }

  @Override
  public void setLongValue(long value) {
    if (enable) {
      this.value.set(value);
    }
  }

  @Override
  public void dec() {
    if (enable) {
      value.decrementAndGet();
    }
  }

  @Override
  public  StatisticRecord[] getChilds() {
    return childs.toArray(new StatisticRecord[0]);
  }

  @Override
  public void reset() {
    value.set(0);
  }

  @Override
  public void enable(boolean e) {
    for (StatisticRecord r : childs) {
      r.enable(e);
    }
    enable = e;
  }

  @Override
  public boolean isEnabled() {
    return this.enable;
  }

  @Override
  public String toString() {
    return String.valueOf(valueHolder != null ? valueHolder.getValueAsString() : value.get());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((counter == null) ? 0 : counter.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StatisticRecordImpl other = (StatisticRecordImpl) obj;
    if (counter != other.counter) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

}
