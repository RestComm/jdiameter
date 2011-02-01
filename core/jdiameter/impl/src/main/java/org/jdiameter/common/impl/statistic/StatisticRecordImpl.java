package org.jdiameter.common.impl.statistic;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.jdiameter.api.StatisticRecord;
import org.jdiameter.common.api.statistic.IStatisticRecord;

class StatisticRecordImpl implements IStatisticRecord {

  protected boolean enable = true;
  protected String name;
  protected String description;
  protected Counters counter;
  
  protected AtomicLong value;
  protected ConcurrentLinkedQueue<StatisticRecord> childs = new ConcurrentLinkedQueue<StatisticRecord>();
  protected ValueHolder valueHolder;

  public StatisticRecordImpl(String name) {
    this.name = name;
    this.value = new AtomicLong(0);
  }

  public StatisticRecordImpl(String name, String description) {
	    this(name);
	    this.description = description;
	  }
  
  public StatisticRecordImpl(String name, Counters counter) {
	    this(counter.name()+"."+name);
	    this.counter = counter;
	    this.description = counter.getDescription();
	  }

  public StatisticRecordImpl(String name, String description,  IStatisticRecord... childs) {
    this(name, description);
    this.childs.addAll(Arrays.asList(childs));
  }

  public StatisticRecordImpl(String name, String description,  ValueHolder valueHolder) {
    this(name, description);
    this.valueHolder = valueHolder;
  }

  public StatisticRecordImpl(String name, String description,  ValueHolder valueHolder, IStatisticRecord... childs) {
    this(name, description, valueHolder);
    this.childs.addAll(Arrays.asList(childs));
  }

  public StatisticRecordImpl(String name, String description,  long value) {
    this(name, description);
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

  public  StatisticRecord[] getChilds() {
    return childs.toArray(new StatisticRecord[0]);
  }

  public void reset() {
    value.set(0);
  }

  public void enable(boolean e) {
    for (StatisticRecord r : childs) {
      r.enable(e);
    }
    enable = e;
  }

  public boolean isEnabled()
  {
	  return this.enable;
  }
  
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatisticRecordImpl other = (StatisticRecordImpl) obj;
		if (counter != other.counter)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
  
  
}
