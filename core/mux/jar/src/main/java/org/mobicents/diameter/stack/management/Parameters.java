package org.mobicents.diameter.stack.management;

import java.io.Serializable;
import java.util.HashMap;

public interface Parameters extends Serializable {

  public boolean getAcceptUndefinedPeer();

  public void setAcceptUndefinedPeer(boolean acceptUndefinedPeer);

  public boolean getDuplicateProtection();

  public void setDuplicateProtection(boolean duplicateProtection);

  public long getDuplicateTimer();

  public void setDuplicateTimer(long duplicateTimer);

  public boolean getUseUriAsFqdn();

  public void setUseUriAsFqdn(boolean useUriAsFqdn);

  public int getQueueSize();

  public void setQueueSize(int queueSize);

  public long getMessageTimeout();

  public void setMessageTimeout(long messageTimeout);

  public long getStopTimeout();

  public void setStopTimeout(long stopTimeout);

  public long getCeaTimeout();

  public void setCeaTimeout(long ceaTimeout);

  public long getIacTimeout();

  public void setIacTimeout(long iacTimeout);

  public long getDwaTimeout();

  public void setDwaTimeout(long dwaTimeout);

  public long getDpaTimeout();

  public void setDpaTimeout(long dpaTimeout);

  public long getRecTimeout();

  public void setRecTimeout(long recTimeout);

  /* Gone since merge with build-350
  public String getThreadPool_Priority();

  public void setThreadPool_Priority(String threadPoolPriority);

  public Integer getThreadPool_Size();

  public void setThreadPool_Size(Integer threadPoolSize);
   */

  public HashMap<String, ConcurrentEntity> getConcurrentEntities();

  public void setConcurrentEntity(ConcurrentEntity concurrentEntity);

  public Long getStatisticLogger_Delay();

  public void setStatisticLogger_Delay(Long statisticLoggerDelay);

  public Long getStatisticLogger_Pause();

  public void setStatisticLogger_Pause(Long statisticLoggerPause);
}
