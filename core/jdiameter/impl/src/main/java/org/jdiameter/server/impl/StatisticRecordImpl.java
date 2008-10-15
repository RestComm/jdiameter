package org.jdiameter.server.impl;

import org.jdiameter.api.StatisticRecord;
import org.jdiameter.server.api.IStatisticRecord;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticRecordImpl implements IStatisticRecord {

    public static interface Counter {
        int getValueAsInt();
    }

    protected boolean enable = true;
    protected String name;
    protected String description;
    protected int type;
    protected AtomicLong value;
    protected ConcurrentLinkedQueue<IStatisticRecord> childs = new ConcurrentLinkedQueue<IStatisticRecord>();
    protected Counter counter;

    public StatisticRecordImpl(String name, int type) {
        this.name = name;
        this.type = type;
        this.value = new AtomicLong(0);
    }

    public StatisticRecordImpl(String name, String description, int type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = new AtomicLong(0);
    }

    public StatisticRecordImpl(String name, String description, int type, Counter counter) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = new AtomicLong(0);
        this.counter = counter;
    }

    public StatisticRecordImpl(String name, String description, int type, long value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = new AtomicLong(value);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValueAsInt() {
        return counter != null ? counter.getValueAsInt() : (int) value.get();
    }

    public double getValueAsDouble() {
        return value.get();
    }

    public long getValueAsLong() {
        return value.get();
    }

    public int getType() {
        return type;
    }

    public void inc() {
        if (enable)
            value.incrementAndGet();
    }

    public void dec() {
        if (enable)
            value.decrementAndGet();
    }

    public StatisticRecord[] getChilds() {
        return childs.toArray(new StatisticRecord[0]);
    }

    public void reset() {
        value.set(0);
    }

    public void enable(boolean e) {
        for (IStatisticRecord r : childs) r.enable(e);
        enable = e;
    }


    public String toString() {
        return "\nStatisticRecord {" +
                "enable=" + enable +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", value=" + getValueAsInt() +
                ", childs=" + childs +
                "}";
    }
}
