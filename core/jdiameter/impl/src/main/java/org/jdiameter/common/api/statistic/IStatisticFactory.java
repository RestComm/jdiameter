package org.jdiameter.common.api.statistic;

public interface IStatisticFactory {

  IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription);

  IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord.ValueHolder counters);

  IStatisticRecord newCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord.ValueHolder counters, IStatisticRecord... rec);

  IStatisticRecord newCounterRecord(String name, String description, int id);

  IStatisticRecord newPerSecondCounterRecord(IStatistic.Counters recordDescription, IStatisticRecord record);

  IStatistic newStatistic(IStatistic.Groups group, IStatisticRecord... rec);

  IStatistic newStatistic(String name, String description, IStatisticRecord... rec);

  IStatisticRecord newCounterRecord(String name, String description, int id, IStatisticRecord.ValueHolder counter);
}
