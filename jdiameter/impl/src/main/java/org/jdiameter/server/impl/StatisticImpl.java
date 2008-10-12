package org.jdiameter.server.impl;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.server.api.IStatistic;
import org.jdiameter.server.api.IStatisticRecord;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StatisticImpl implements IStatistic {

    protected boolean enable = true;
    protected ConcurrentLinkedQueue<IStatisticRecord> records = new ConcurrentLinkedQueue<IStatisticRecord>();

    private String name;
    private String desctiprion;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desctiprion;
    }

    public StatisticImpl(String name, String desctiprion, StatisticRecord... rec) {
        this.name = name;
        this.desctiprion = desctiprion;
        for (StatisticRecord r : rec) records.add((IStatisticRecord)r);
    }

    public StatisticImpl appendCounter(StatisticRecord... rec) {
        for (StatisticRecord r : rec) records.add((IStatisticRecord)r);
        return this;
    }

    public void enable(boolean e) {
        for (IStatisticRecord r : records) r.enable(e);
        enable = e;
    }

    public boolean isEnable() {
        return enable;
    }

    public void reset() {
        for (IStatisticRecord r : records) r.reset();
    }

    public Set<StatisticRecord> getRecords() {
        return Collections.unmodifiableSet(new LinkedHashSet<StatisticRecord>(records));
    }

    public void appendCounter(Set<StatisticRecord> e) {
       for (StatisticRecord r : e) records.add((IStatisticRecord)r);
    }

    public String toString() {
        return "Statistic{" +
                " records=" + records +
                " }";
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;
    }
}
