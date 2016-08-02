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

package org.jdiameter.common.api.statistic;

import java.util.List;
import java.util.Set;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IStatisticManager {

  IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription);

  IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counters);

  IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counters, IStatisticRecord... rec);

  IStatisticRecord newCounterRecord(String name, String description);

  IStatisticRecord newCounterRecord(String name, String description, IStatisticRecord.ValueHolder counter);

  IStatisticRecord newPerSecondCounterRecord(String name, IStatisticRecord.Counters recordDescription, IStatisticRecord record);

  IStatistic newStatistic(String name, IStatistic.Groups group, IStatisticRecord... rec);

  //IStatistic newStatistic(String name, String description, IStatisticRecord... rec);

  //void removePerSecondCounterRecord(String name, IStatisticRecord.Counters recordDescription);

  void removePerSecondCounterRecord(IStatisticRecord rec);

  //void removeStatistic(String name);

  //void removeStatistic(String name, IStatistic.Groups group);

  void removeStatistic(IStatistic stat);

  // --- non factory methods, metadata access
  boolean isOn();

  long getPause();

  long getDelay();

  Set<String> getEnabled();

  // --- access method

  List<IStatisticRecord> getPSStatisticRecord();

  List<IStatistic> getStatistic();

}
