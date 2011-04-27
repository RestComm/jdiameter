/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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
