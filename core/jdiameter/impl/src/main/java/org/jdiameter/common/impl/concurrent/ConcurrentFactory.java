package org.jdiameter.common.impl.concurrent;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ConcurrentFactory implements IConcurrentFactory {

  private BaseThreadFactory threadFactory;

  //private CopyOnWriteArrayList<CommonScheduledExecutorService> scheduledExecutorServices;
  private Map<String, CommonScheduledExecutorService> scheduledExecutorServices;
  private Configuration[] config;
  private IStatisticFactory statisticFactory;
  private IStatistic statistic;

  public ConcurrentFactory(Configuration config, IStatisticFactory statisticFactory) {

    this.config = config.getChildren(Parameters.Concurrent.ordinal());
    Configuration dgConfig = getConfigByName(BaseThreadFactory.ENTITY_NAME);
    String defThreadGroupName = dgConfig != null ?
        dgConfig.getStringValue(Parameters.ConcurrentEntityDescription.ordinal(), (String) Parameters.ConcurrentEntityDescription.defValue()) :
          (String) Parameters.ConcurrentEntityDescription.defValue();

        int size = dgConfig != null ? dgConfig.getIntValue(Parameters.ConcurrentEntityPoolSize.ordinal(), Integer.MAX_VALUE) : Integer.MAX_VALUE;

        threadFactory = new BaseThreadFactory(defThreadGroupName, size);
        //scheduledExecutorServices = new CopyOnWriteArrayList<CommonScheduledExecutorService>();
        scheduledExecutorServices = new ConcurrentHashMap<String, CommonScheduledExecutorService>();
        IStatisticRecord threadCount = statisticFactory.newCounterRecord(
            IStatistic.Counters.ConcurrentThread,
            new IStatisticRecord.IntegerValueHolder() {
              public String getValueAsString() {
                return getValueAsInt() + "";
              }

              public int getValueAsInt() {
                return getThreadGroup().activeCount();
              }
            });
        IStatisticRecord schedExeServiceCount = statisticFactory.newCounterRecord(
            IStatistic.Counters.ConcurrentScheduledExecutedServices,
            new IStatisticRecord.IntegerValueHolder() {
              public String getValueAsString() {
                return getValueAsInt() + "";
              }

              public int getValueAsInt() {
                return scheduledExecutorServices.size();
              }
            });
        
        statistic = statisticFactory.newStatistic(IStatistic.Groups.Concurrent, threadCount, schedExeServiceCount);
        this.statisticFactory = statisticFactory;
  }

  private Configuration getConfigByName(String name) {
    if (config != null) {
      for (Configuration c : config) {
        if (c != null && c.getStringValue(Parameters.ConcurrentEntityName.ordinal(), "").equals(name)) {
          return c;
        }
      }
    }
    return null;
  }

  public Thread getThread(Runnable runnable) {
    return threadFactory.newThread(runnable);
  }

  public Thread getThread(String namePrefix, Runnable runnuble) {
    return threadFactory.newThread(namePrefix, runnuble);
  }

  public List<Thread> getThreads() {
    Thread[] threads = new Thread[threadFactory.getThreadGroup().activeCount()];
    threadFactory.getThreadGroup().enumerate(threads);
    return Arrays.asList(threads);
  }

  public ThreadGroup getThreadGroup() {
    return threadFactory.getThreadGroup();
  }

  public ScheduledExecutorService getScheduledExecutorService(String name) {
	  CommonScheduledExecutorService service = null;
	  if(!scheduledExecutorServices.containsKey(name))
	  { 
		  service = new CommonScheduledExecutorService(name, getConfigByName(name), new EntityFactory(statistic), statisticFactory);
		  scheduledExecutorServices.put(name,service);
	  }else
	  {
		  service = scheduledExecutorServices.get(name);
	  }
	   
    return service;
  }

  public Collection<ScheduledExecutorService> getScheduledExecutorServices() {
    List<ScheduledExecutorService> external = new ArrayList<ScheduledExecutorService>(scheduledExecutorServices.values());
    
    return external;
  }

  public void shutdownNow(ScheduledExecutorService service) {
    
	  for (String name : scheduledExecutorServices.keySet()) {
	    	ExecutorService e = scheduledExecutorServices.get(name);
	    	 if (e == service) {
	    	        e.shutdownNow();
	    	        scheduledExecutorServices.remove(name);
	    	        break;
	    	      }
	      
	    }
  }

  public IStatistic getStatistic() {
    return statistic;
  }

  public IStatistic[] getStatistics() {
    List<IStatistic> statistics = new ArrayList<IStatistic>();

    for (CommonScheduledExecutorService e : scheduledExecutorServices.values()) {
      statistics.add(e.getStatistic());
    }
    return statistics.toArray(new IStatistic[statistics.size()]);
  }

  public void shutdownAllNow() {
    for (String name : scheduledExecutorServices.keySet()) {
    	ExecutorService e = scheduledExecutorServices.remove(name);
        e.shutdownNow();
      
    }

  }
}
