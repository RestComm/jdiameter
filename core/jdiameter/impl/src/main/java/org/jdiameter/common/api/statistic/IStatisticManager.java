package org.jdiameter.common.api.statistic;

import java.util.List;
import java.util.Set;

public interface IStatisticManager {

	IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription);

	IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counters);

	IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counters, IStatisticRecord... rec);

	IStatisticRecord newCounterRecord(String name, String description);

	IStatisticRecord newCounterRecord(String name, String description, IStatisticRecord.ValueHolder counter);
	
	
	
	IStatisticRecord newPerSecondCounterRecord(String name,IStatisticRecord.Counters recordDescription, IStatisticRecord record);

	IStatistic newStatistic(String name, IStatistic.Groups group, IStatisticRecord... rec);

	//IStatistic newStatistic(String name, String description, IStatisticRecord... rec);

	//void removePerSecondCounterRecord(String name,IStatisticRecord.Counters recordDescription);
	
	void removePerSecondCounterRecord(IStatisticRecord rec);
	
	//void removeStatistic(String name);
	
	//void removeStatistic(String name, IStatistic.Groups group);
	
	void removeStatistic(IStatistic stat);
	
	// --- non factory methods, metadata access
	public boolean isOn();

	public long getPause();

	public long getDelay();

	public Set<String> getEnabled();
	
	// --- access method
	
	public List<IStatisticRecord> getPSStatisticRecord();
	
	public List<IStatistic> getStatistic();
	
}
