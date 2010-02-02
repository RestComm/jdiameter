package org.jdiameter.common.impl.statistic;

import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

class StatisticRecordImpl implements IStatisticRecord {

  protected boolean enable = true;
  protected String name;
  protected String description;
  protected int type;
  protected AtomicLong value;
  protected ConcurrentLinkedQueue<IStatisticRecord> childs = new ConcurrentLinkedQueue<IStatisticRecord>();
  protected ValueHolder valueHolder;

  public StatisticRecordImpl(String name, int type) {
    this.name = name;
    this.type = type;
    this.value = new AtomicLong(0);
  }

  public StatisticRecordImpl(String name, String description, int type) {
    this(name, type);
    this.description = description;
  }

  public StatisticRecordImpl(String name, String description, int type, IStatisticRecord... childs) {
    this(name, description, type);
    this.childs.addAll(Arrays.asList(childs));
  }

  public StatisticRecordImpl(String name, String description, int type, ValueHolder valueHolder) {
    this(name, description, type);
    this.valueHolder = valueHolder;
  }

  public StatisticRecordImpl(String name, String description, int type, ValueHolder valueHolder, IStatisticRecord... childs) {
    this(name, description, type, valueHolder);
    this.childs.addAll(Arrays.asList(childs));
  }

  public StatisticRecordImpl(String name, String description, int type, long value) {
    this(name, description, type);
    this.value = new AtomicLong(value);
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getValueAsInt() {
    return valueHolder != null ? ((IntegerValueHolder) valueHolder).getValueAsInt() : (int) value.get();
  }

  public double getValueAsDouble() {
    return valueHolder != null ? ((DoubleValueHolder) valueHolder).getValueAsDouble() :
      Double.longBitsToDouble(value.get());
  }

  public long getValueAsLong() {
    return valueHolder != null ? ((LongValueHolder) valueHolder).getValueAsLong() : value.get();
  }

  public int getType() {
    return type;
  }

  public void inc() {
    if (enable) {
      this.value.incrementAndGet();
    }
  }

  public void inc(long delta) {
    if (enable) {
      this.value.addAndGet(delta);
    }
  }

  public void setDoubleValue(double value) {
    if (enable) {
      this.value.set(Double.doubleToLongBits(value));
    }
  }

  public void setLongValue(long value) {
    if (enable) {
      this.value.set(value);
    }
  }

  public void dec() {
    if (enable) {
      value.decrementAndGet();
    }
  }

  public IStatisticRecord[] getChilds() {
    return childs.toArray(new IStatisticRecord[childs.size()]);
  }

  public void reset() {
    value.set(0);
  }

  public void enable(boolean e) {
    for (IStatisticRecord r : childs) {
      r.enable(e);
    }
    enable = e;
  }

  public String toString() {
    return String.valueOf(valueHolder != null ? valueHolder.getValueAsString() : value.get());
  }
}
