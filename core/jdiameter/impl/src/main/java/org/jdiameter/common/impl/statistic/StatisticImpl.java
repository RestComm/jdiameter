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

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jdiameter.api.StatisticRecord;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.common.api.statistic.IStatisticRecord.Counters;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
class StatisticImpl implements IStatistic {

  protected boolean enable = true;
  protected ConcurrentLinkedQueue<StatisticRecord> records = new ConcurrentLinkedQueue<StatisticRecord>();
  protected String name;
  protected String description;
  protected IStatistic.Groups group;
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  StatisticImpl(String name) {
    this.name = name;
  }

  StatisticImpl(String name, IStatistic.Groups group) {
    this(group.name() + "." + name);
    this.group = group;
    this.description = group.getDescription();

  }
  //  public StatisticImpl(String name, String desctiprion, IStatisticRecord... rec) {
  //    this.name = name;
  //    this.description = desctiprion;
  //    for (IStatisticRecord r : rec) {
  //      records.add((IStatisticRecord) r);
  //    }
  //  }
  StatisticImpl(String name, IStatistic.Groups group, String desctiprion, IStatisticRecord... rec) {
    this(name, group);
    this.description = desctiprion;
    for (IStatisticRecord r : rec) {
      records.add(r);
    }
  }
  @Override
  public void appendCounter(IStatisticRecord... rec) {
    for (IStatisticRecord r : rec) {
      r.enable(this.enable);
      records.add(r);
    }
  }

  @Override
  public IStatisticRecord getRecordByName(String name) {
    for (StatisticRecord r : records) {
      if (r.getName().equals(name)) {
        return (IStatisticRecord) r;
      }
    }
    return null;
  }

  @Override
  public IStatisticRecord getRecordByName(Counters name) {
    for (StatisticRecord r : records) {
      if (r.getName().equals(name.toString())) {
        return (IStatisticRecord) r;
      }
    }
    return null;
  }

  @Override
  public void enable(boolean e) {
    for (StatisticRecord r : records) {
      r.enable(e);
    }
    enable = e;
  }

  @Override
  public boolean isEnabled() {
    return enable;
  }

  @Override
  public void reset() {
    for (StatisticRecord r : records) {
      r.reset();
    }
  }

  @Override
  public StatisticRecord[] getRecords() {
    return records.toArray(new StatisticRecord[0]);
  }

  @Override
  public String toString() {
    return "Statistic{" + " records=" + records + " }";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((group == null) ? 0 : group.hashCode());
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
    StatisticImpl other = (StatisticImpl) obj;
    if (group != other.group) {
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
