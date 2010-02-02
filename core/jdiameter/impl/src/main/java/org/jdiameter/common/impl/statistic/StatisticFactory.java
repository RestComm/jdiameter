package org.jdiameter.common.impl.statistic;

import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatisticFactory implements IStatisticFactory {

  List<IStatistic> allStatistic = new CopyOnWriteArrayList<IStatistic>();
  List<IStatisticRecord> allPSStatisticRecord = new CopyOnWriteArrayList<IStatisticRecord>();

  public IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription) {
    return new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), recordDescription.getId());
  }

  public IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord.ValueHolder counters) {
    return new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), recordDescription.getId(), counters);
  }

  public IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord.ValueHolder counter, IStatisticRecord... rec) {
    return new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), recordDescription.getId(), counter, rec);

  }

  public IStatisticRecord newCounterRecord(String name, String description, int id) {
    return new StatisticRecordImpl(name, description, id);
  }

  public IStatisticRecord newCounterRecord(String name, String description, int id, IStatisticRecord.ValueHolder counters) {
    return new StatisticRecordImpl(name, description, id, counters);
  }

  public IStatisticRecord newPerSecondCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord child) {
    IStatisticRecord prevValue = new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), recordDescription.getId());
    IStatisticRecord psStatistic = new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), recordDescription.getId(), child, prevValue);
    allPSStatisticRecord.add(psStatistic);
    return prevValue;
  }

  public IStatistic newStatistic(IStatistic.Groups group, IStatisticRecord... rec) {
    IStatistic statistic = new StatisticImpl(group.name(), group.getDescription(), rec);
    allStatistic.add(statistic);
    return statistic;
  }

  public IStatistic newStatistic(String name, String description, IStatisticRecord... rec) {
    IStatistic statistic = new StatisticImpl(name, description, rec);
    allStatistic.add(statistic);
    return statistic;
  }
}
