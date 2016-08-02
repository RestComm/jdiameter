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

import org.jdiameter.api.StatisticRecord;

/**
 * This interface describe extends methods of base class
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IStatisticRecord extends StatisticRecord {

  enum Counters {

    AppGenRequest("Count of app generated requests"),
    AppGenRejectedRequest("Count of rejected app generated requests"),
    AppGenResponse("Count of app generated responses"),
    AppGenRejectedResponse("Count of rejected app generated responses"),
    NetGenRequest("Count of network generated processed requests"),
    NetGenRejectedRequest("Count of network generated rejected requests"),
    NetGenResponse("Count of network generated processed responses"),
    NetGenRejectedResponse("Count of network generated rejected responses"),
    SysGenResponse("Count of platform generated responses"),

    AppGenRequestPerSecond("Count of app generated request per second"),
    AppGenResponsePerSecond("Count of app generated responses per second"),
    NetGenResponsePerSecond("Count of network generated responses per second"),
    NetGenRequestPerSecond("Count of network generated request per second"),

    RequestListenerCount("Count of network request appIdToNetListener"),
    SelectorCount("Count of network request selectorToNetListener"),

    HeapMemory("Heap memory usage"),
    NoHeapMemory("No-heap memory usage"),
    MessageProcessingTime("Average time of processing message"),

    ConcurrentThread("Count thread in default thread group"),
    ConcurrentScheduledExecutedServices("Count of ScheduledExecutorServices"),

    WorkingThread("Count of working thread"),
    CanceledTasks("Count of canceled thread"),
    ExecTimeTask("Average execution time of task"),
    WaitTimeTask("Average waiting time for execution task"),
    BrokenTasks("Count of broken thread"),
    RejectedTasks("Count of rejected tasks"),
    QueueSize("Peer FSM queue size");

    private String description;

    Counters(String description) {
      this.description = description;
    }

    public String getDescription() {
      return description;
    }
  }

  /**
   * Increment counter
   */
  void inc();

  /**
   * Increment counter
   */
  void inc(long value);

  /**
   * Decrement counter
   */
  void dec();

  /**
   * Set value of statistic
   *
   * @param value new value of record
   */
  void setLongValue(long value);


  /**
   * Set value of statistic
   *
   * @param value new value of record
   */
  void setDoubleValue(double value);
  /**
   * ValueHolder for external statistics
   */
  public interface ValueHolder {
    String getValueAsString();
  }

  public interface IntegerValueHolder extends ValueHolder {
    /**
     * Return value of counter as integer
     *
     * @return value of counter
     */
    int getValueAsInt();
  }

  public interface LongValueHolder extends ValueHolder {
    /**
     * Return value of counter as long
     *
     * @return value of counter
     */
    long getValueAsLong();
  }

  public interface DoubleValueHolder extends ValueHolder {

    /**
     * Return value of counter as double
     *
     * @return value of counter
     */
    double getValueAsDouble();
  }

  //===========================

}
