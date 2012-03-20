/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.common.impl.statistic;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class StatisticManagerImpl implements IStatisticManager {

	//TODO: remove CopyOnWrite....
	private List<IStatistic> allStatistic = new CopyOnWriteArrayList<IStatistic>();
	private List<IStatisticRecord> allPSStatisticRecord = new CopyOnWriteArrayList<IStatisticRecord>();

	private List<IStatistic> frozenAllStatistic = Collections.unmodifiableList(allStatistic);
	private List<IStatisticRecord> frozenAllPSStatisticRecord = Collections.unmodifiableList(allPSStatisticRecord);
	
	private boolean enabled;
	private long pause, delay;
	private Set<String> activeRecords; //list of stats enabled on start

	
	public StatisticManagerImpl(Configuration config) {
		long pause = (Long) Parameters.StatisticsLoggerPause.defValue();
		long delay = (Long) Parameters.StatisticsLoggerDelay.defValue();
		boolean enabled = (Boolean) Parameters.StatisticsEnabled.defValue();
		String activeRecords = (String) Parameters.Statistics.defValue();
		Configuration[] loggerParams = config.getChildren(Parameters.Statistics.ordinal());
		if (loggerParams != null && loggerParams.length > 0) {
			pause = loggerParams[0].getLongValue(Parameters.StatisticsLoggerPause.ordinal(), pause);
			delay = loggerParams[0].getLongValue(Parameters.StatisticsLoggerDelay.ordinal(), delay);
			enabled = loggerParams[0].getBooleanValue(Parameters.StatisticsEnabled.ordinal(), enabled);
			activeRecords = loggerParams[0].getStringValue(Parameters.StatisticsActiveList.ordinal(), activeRecords);
		}
		this.pause = pause;
		this.delay = delay;
		this.enabled = enabled;
		Set<String> enabledSet = new HashSet<String>();
		if(activeRecords != null && activeRecords.length()>0)
		{
			for(String s:activeRecords.split(","))
			{
				enabledSet.add(s);
			}
		}
		this.activeRecords = Collections.unmodifiableSet(enabledSet);
			
	}

	public IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription) {
		StatisticRecordImpl statisticRecord = new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription());
		statisticRecord.enable(this.isEnabled(recordDescription.name()));
		
		return statisticRecord;
	}

	public IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counters) {
		StatisticRecordImpl statisticRecord = new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), counters);
		statisticRecord.enable(this.isEnabled(recordDescription.name()));
		return statisticRecord;
	}

	public IStatisticRecord newCounterRecord(IStatisticRecord.Counters recordDescription, IStatisticRecord.ValueHolder counter, IStatisticRecord... rec) {
		StatisticRecordImpl statisticRecord = new StatisticRecordImpl(recordDescription.name(), recordDescription.getDescription(), counter, rec);
		statisticRecord.enable(this.isEnabled(recordDescription.name()));
		return statisticRecord;
	}

	public IStatisticRecord newCounterRecord(String name, String description) {
		StatisticRecordImpl statisticRecord = new StatisticRecordImpl(name, description);
		statisticRecord.enable(this.isEnabled(name));
		return statisticRecord;
	}

	public IStatisticRecord newCounterRecord(String name, String description, IStatisticRecord.ValueHolder counters) {
		StatisticRecordImpl statisticRecord = new StatisticRecordImpl(name, description, counters);
		statisticRecord.enable(this.isEnabled(name));
		return statisticRecord;
	}

	public IStatisticRecord newPerSecondCounterRecord(String name,IStatisticRecord.Counters recordDescription, IStatisticRecord child) {
		IStatisticRecord prevValue = new StatisticRecordImpl(name, recordDescription.getDescription());
		IStatisticRecord psStatistic = new StatisticRecordImpl(recordDescription.name()+"."+name, recordDescription.getDescription(), child, prevValue);
		if(allPSStatisticRecord.contains(psStatistic))
		{
			throw new IllegalArgumentException("Statistic already defined: "+psStatistic);
		}
		allPSStatisticRecord.add(psStatistic);
		return psStatistic;
	}

	public IStatistic newStatistic(String name, IStatistic.Groups group, IStatisticRecord... rec) {
		IStatistic statistic = new StatisticImpl(name,group, group.getDescription(), rec);
		statistic.enable(this.isEnabled(statistic.getName()));
		if(allStatistic.contains(statistic))
		{
			throw new IllegalArgumentException("Statistic already defined: "+statistic);
		}
		allStatistic.add(statistic);
		return statistic;
	}

//	public IStatistic newStatistic(String name, String description, IStatisticRecord... rec) {
//		//FIXME: remove this?
//		IStatistic statistic = new StatisticImpl(name, description, rec);
//		statistic.enable(this.isEnabled(statistic.getName()));
//		if(allStatistic.contains(statistic))
//		{
//			throw new IllegalArgumentException("Statistic already defined: "+statistic);
//		}
//		allStatistic.add(statistic);
//		return statistic;
//	}
//	
	
//    public void removePerSecondCounterRecord(String name,IStatisticRecord.Counters recordDescription)
//    {
//    	IStatisticRecord record = new StatisticRecordImpl(recordDescription+"."+name,recordDescription.getDescription());
//    	this.allPSStatisticRecord.remove(record);
//    }
//	
//    public void removeStatistic(String name)
//    {
//    	IStatistic statistic = new StatisticImpl(name);
//    	this.allStatistic.remove(statistic);
//    }
//	
//    public void removeStatistic(String name, IStatistic.Groups group)
//    {
//    	IStatistic statistic = new StatisticImpl(name,group);
//    	this.allStatistic.remove(statistic);
//    }

	public void removePerSecondCounterRecord(IStatisticRecord rec) {
		this.allPSStatisticRecord.remove(rec);		
	}

	public void removeStatistic(IStatistic stat) {
		this.allStatistic.remove(stat);
	}

	private boolean isEnabled(String name)
	{

		if(this.activeRecords.contains(name))
		{
			return true;
		}
		
		//else lets check prefixes.
		while(name.indexOf(".")>0)
		{
			name = name.substring(0,name.lastIndexOf("."));
			if(this.activeRecords.contains(name))
			{

				return true;
			}
		}
		return this.activeRecords.contains(name);
	}
	
	
	public boolean isOn() {
		return enabled;
	}

	public long getPause() {
		return pause;
	}

	public long getDelay() {
		return delay;
	}

	public Set<String> getEnabled() {
		return activeRecords;
	}

	public List<IStatisticRecord> getPSStatisticRecord(){
		return this.frozenAllPSStatisticRecord;
	}
	
	public List<IStatistic> getStatistic(){
		return this.frozenAllStatistic;
	}
		
	
//	Exception in thread "Timer-0" java.lang.NullPointerException
//	at org.jdiameter.client.impl.fsm.PeerFSMImpl$2.getValueAsDouble(PeerFSMImpl.java:107)
//	at org.jdiameter.client.impl.fsm.PeerFSMImpl$2.getValueAsString(PeerFSMImpl.java:119)
//	at org.jdiameter.common.impl.statistic.StatisticRecordImpl.toString(StatisticRecordImpl.java:125)
//	at java.lang.String.valueOf(String.java:2826)
//	at java.lang.StringBuilder.append(StringBuilder.java:115)
//	at java.util.AbstractCollection.toString(AbstractCollection.java:422)
//	at java.lang.String.valueOf(String.java:2826)
//	at java.lang.StringBuilder.append(StringBuilder.java:115)
//	at org.jdiameter.common.impl.statistic.StatisticImpl.toString(StatisticImpl.java:89)
//	at java.lang.String.valueOf(String.java:2826)
//	at java.lang.StringBuilder.append(StringBuilder.java:115)
//	at org.jdiameter.common.impl.statistic.StatisticManagerImpl.print(StatisticManagerImpl.java:209)
//	at org.jdiameter.common.impl.statistic.StatisticManagerImpl.access$0(StatisticManagerImpl.java:204)
//	at org.jdiameter.common.impl.statistic.StatisticManagerImpl$1.run(StatisticManagerImpl.java:48)
//	at java.util.TimerThread.mainLoop(Timer.java:512)
//	at java.util.TimerThread.run(Timer.java:462)
}
