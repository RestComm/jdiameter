package org.mobicents.diameter.stack.management;

import static org.jdiameter.client.impl.helpers.Parameters.CeaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.Concurrent;
import static org.jdiameter.client.impl.helpers.Parameters.DpaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.DwaTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.IacTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.QueueSize;
import static org.jdiameter.client.impl.helpers.Parameters.RecTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.StatisticLoggerDelay;
import static org.jdiameter.client.impl.helpers.Parameters.StatisticLoggerPause;
import static org.jdiameter.client.impl.helpers.Parameters.StopTimeOut;
import static org.jdiameter.client.impl.helpers.Parameters.UseUriAsFqdn;
import static org.jdiameter.server.impl.helpers.Parameters.AcceptUndefinedPeer;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateProtection;
import static org.jdiameter.server.impl.helpers.Parameters.DuplicateTimer;
import static org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityName;
import static org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityDescription;
import static org.jdiameter.client.impl.helpers.Parameters.ConcurrentEntityPoolSize;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MutableConfiguration;
import org.mobicents.diameter.stack.management.ConcurrentEntity.ConcurrentEntityNames;

public class ParametersImpl implements Parameters {

  private static final long serialVersionUID = 1L;

  // The Singleton
  // public final static Parameters INSTANCE = new ParametersImpl();

  private boolean acceptUndefinedPeer;

  private boolean duplicateProtection;
  private long duplicateTimer;

  private boolean useUriAsFqdn;

  private int queueSize;

  private long messageTimeout;

  private long stopTimeout;
  private long ceaTimeout;
  private long iacTimeout;
  private long dwaTimeout;
  private long dpaTimeout;
  private long recTimeout;

  // Gone since merge with build-350
  // private String threadPool_Priority;
  // private Integer threadPool_Size;

  private Long statisticLogger_Pause;
  private Long statisticLogger_Delay;

  private HashMap<String, ConcurrentEntity> concurrentEntities = new HashMap<String, ConcurrentEntity>();

  public ParametersImpl(MutableConfiguration config) {
    // Generic Stack Configuration
    this.acceptUndefinedPeer = config.getBooleanValue(AcceptUndefinedPeer.ordinal(), true);
    this.duplicateProtection = config.getBooleanValue(DuplicateProtection.ordinal(), true);
    this.duplicateTimer = config.getLongValue(DuplicateTimer.ordinal(), 4 * 60 * 1000L);
    this.useUriAsFqdn = config.getBooleanValue(UseUriAsFqdn.ordinal(), false);
    this.queueSize = config.getIntValue(QueueSize.ordinal(), 10000);

    // Timeouts
    this.messageTimeout = config.getLongValue(MessageTimeOut.ordinal(), 60000L);
    this.stopTimeout = config.getLongValue(StopTimeOut.ordinal(), 10000L);
    this.ceaTimeout = config.getLongValue(CeaTimeOut.ordinal(), 10000L);
    this.iacTimeout = config.getLongValue(IacTimeOut.ordinal(), 20000L);
    this.dwaTimeout = config.getLongValue(DwaTimeOut.ordinal(), 10000L);
    this.dpaTimeout = config.getLongValue(DpaTimeOut.ordinal(), 5000L);
    this.recTimeout = config.getLongValue(RecTimeOut.ordinal(), 10000L);

    // Concurrent Entities
    for(Configuration concurrentEntity : config.getChildren(Concurrent.ordinal())) {
      String name = concurrentEntity.getStringValue(ConcurrentEntityName.ordinal(), null);
      String desc = concurrentEntity.getStringValue(ConcurrentEntityDescription.ordinal(), "");
      int size = concurrentEntity.getIntValue(ConcurrentEntityPoolSize.ordinal(), 1);
      this.concurrentEntities.put(name, ConcurrentEntityImpl.createEntity(ConcurrentEntityNames.valueOf(name), desc, size));
    }

    // Statistic Logger
    this.statisticLogger_Delay = config.getLongValue(StatisticLoggerDelay.ordinal(), 5000L);
    this.statisticLogger_Pause = config.getLongValue(StatisticLoggerPause.ordinal(), 5000L);
  }

  public boolean getAcceptUndefinedPeer() {
    return acceptUndefinedPeer;
  }

  public void setAcceptUndefinedPeer(boolean acceptUndefinedPeer) {
    DiameterConfiguration.getMutableConfiguration().setBooleanValue(AcceptUndefinedPeer.ordinal(), acceptUndefinedPeer);
  }

  public boolean getDuplicateProtection() {
    return duplicateProtection;
  }

  public void setDuplicateProtection(boolean duplicateProtection) {
    throw new UnsupportedOperationException();
  }

  public long getDuplicateTimer() {
    return duplicateTimer;
  }

  public void setDuplicateTimer(long duplicateTimer) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(DuplicateTimer.ordinal(), duplicateTimer);
  }

  public boolean getUseUriAsFqdn() {
    return useUriAsFqdn;
  }

  public void setUseUriAsFqdn(boolean useUriAsFqdn) {
    DiameterConfiguration.getMutableConfiguration().setBooleanValue(UseUriAsFqdn.ordinal(), useUriAsFqdn);
  }

  public int getQueueSize() {
    return queueSize;
  }

  public void setQueueSize(int queueSize) {
    throw new UnsupportedOperationException();
  }

  public long getMessageTimeout() {
    return messageTimeout;
  }

  public void setMessageTimeout(long messageTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(MessageTimeOut.ordinal(), messageTimeout);
  }

  public long getStopTimeout() {
    return stopTimeout;
  }

  public void setStopTimeout(long stopTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(StopTimeOut.ordinal(), stopTimeout);
  }

  public long getCeaTimeout() {
    return ceaTimeout;
  }

  public void setCeaTimeout(long ceaTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(CeaTimeOut.ordinal(), ceaTimeout);
  }

  public long getIacTimeout() {
    return iacTimeout;
  }

  public void setIacTimeout(long iacTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(IacTimeOut.ordinal(), iacTimeout);
  }

  public long getDwaTimeout() {
    return dwaTimeout;
  }

  public void setDwaTimeout(long dwaTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(DwaTimeOut.ordinal(), dwaTimeout);
  }

  public long getDpaTimeout() {
    return dpaTimeout;
  }

  public void setDpaTimeout(long dpaTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(DpaTimeOut.ordinal(), dpaTimeout);
  }

  public long getRecTimeout() {
    return recTimeout;
  }

  public void setRecTimeout(long recTimeout) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(RecTimeOut.ordinal(), recTimeout);
  }

  /* Gone since merge with build-350
  public String getThreadPool_Priority() {
    return threadPool_Priority;
  }

  public void setThreadPool_Priority(String threadPoolPriority) {
    threadPool_Priority = threadPoolPriority;
  }

  public Integer getThreadPool_Size() {
    return threadPool_Size;
  }

  public void setThreadPool_Size(Integer threadPoolSize) {
    threadPool_Size = threadPoolSize;
  }
  */
  
  public HashMap<String, ConcurrentEntity> getConcurrentEntities() {
    return concurrentEntities;
  }

  public void setConcurrentEntity(ConcurrentEntity concurrentEntity) {
    this.concurrentEntities.put(concurrentEntity.getName(), concurrentEntity);
  }
  
  public Long getStatisticLogger_Delay() {
    return statisticLogger_Delay;
  }
  
  public void setStatisticLogger_Delay(Long statisticLoggerDelay) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(StatisticLoggerDelay.ordinal(), statisticLoggerDelay);
  }
  
  public Long getStatisticLogger_Pause() {
    return statisticLogger_Pause;
  }
  
  public void setStatisticLogger_Pause(Long statisticLoggerPause) {
    DiameterConfiguration.getMutableConfiguration().setLongValue(StatisticLoggerPause.ordinal(), statisticLoggerPause);
  }
  
  @Override
  public String toString() {
    String dotsString = " .............................................................";
    Class<?> cls;
    StringBuffer toStringBuffer = new StringBuffer();
    try {
      cls = Class.forName(this.getClass().getName());
      Field fieldlist[] = cls.getDeclaredFields();
      for (int i = 0; i < fieldlist.length; i++) {
        Field fld = fieldlist[i];
        if(!Modifier.isStatic(fld.getModifiers())) {
          toStringBuffer.append(fld.getName());
          int dots = 60 - fld.getName().length();
          toStringBuffer.append(dotsString, 0, dots);
          toStringBuffer.append(" ").append(fld.get(this)).append("\r\n");
        }
        //System.out.println("decl class = " + fld.getDeclaringClass());
        //System.out.println("type = " + fld.getType());
        //int mod = fld.getModifiers();
        //System.out.println("modifiers = " + Modifier.toString(mod));
        //System.out.println("-----");
      }
    }
    catch (Exception e) {
      // ignore
    }

    return toStringBuffer.toString();
  }
}
