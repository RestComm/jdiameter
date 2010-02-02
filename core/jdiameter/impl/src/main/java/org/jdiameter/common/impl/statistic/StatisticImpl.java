package org.jdiameter.common.impl.statistic;

import org.jdiameter.api.InternalException;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.concurrent.ConcurrentLinkedQueue;

class StatisticImpl implements IStatistic {

  protected boolean enable = true;
  protected ConcurrentLinkedQueue<IStatisticRecord> records = new ConcurrentLinkedQueue<IStatisticRecord>();

  private String name;
  private String description;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public StatisticImpl(String name, String desctiprion, IStatisticRecord... rec) {
    this.name = name;
    this.description = desctiprion;
    for (IStatisticRecord r : rec) {
      records.add((IStatisticRecord) r);
    }
  }

  public IStatistic appendCounter(IStatisticRecord... rec) {
    for (IStatisticRecord r : rec) {
      records.add(r);
    }
    return this;
  }

  public IStatisticRecord getRecordByName(String name) {
    for (IStatisticRecord r : records) {
      if (r.getName().equals(name)) {
        return r;
      }
    }
    return null;
  }

  public void enable(boolean e) {
    for (IStatisticRecord r : records) {
      r.enable(e);
    }
    enable = e;
  }

  public boolean isEnable() {
    return enable;
  }

  public void reset() {
    for (IStatisticRecord r : records) {
      r.reset();
    }
  }

  public IStatisticRecord[] getRecords() {
    return records.toArray(new IStatisticRecord[0]);
  }

  public String toString() {
    return "Statistic{" + " records=" + records + " }";
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return false;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    return null;
  }
}
