 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.mobicents.diameter.stack.management;

import java.io.Serializable;
import java.util.HashMap;

public interface Parameters extends Serializable {

  boolean getAcceptUndefinedPeer();

  void setAcceptUndefinedPeer(boolean acceptUndefinedPeer);

  boolean getDuplicateProtection();

  void setDuplicateProtection(boolean duplicateProtection);

  long getDuplicateTimer();

  void setDuplicateTimer(long duplicateTimer);

  boolean getUseUriAsFqdn();

  void setUseUriAsFqdn(boolean useUriAsFqdn);

  int getQueueSize();

  void setQueueSize(int queueSize);

  long getMessageTimeout();

  void setMessageTimeout(long messageTimeout);

  long getStopTimeout();

  void setStopTimeout(long stopTimeout);

  long getCeaTimeout();

  void setCeaTimeout(long ceaTimeout);

  long getIacTimeout();

  void setIacTimeout(long iacTimeout);

  long getDwaTimeout();

  void setDwaTimeout(long dwaTimeout);

  long getDpaTimeout();

  void setDpaTimeout(long dpaTimeout);

  long getRecTimeout();

  void setRecTimeout(long recTimeout);

  /* Gone since merge with build-350
  public String getThreadPool_Priority();

  public void setThreadPool_Priority(String threadPoolPriority);

  public Integer getThreadPool_Size();

  public void setThreadPool_Size(Integer threadPoolSize);
   */

  HashMap<String, ConcurrentEntity> getConcurrentEntities();

  void setConcurrentEntity(ConcurrentEntity concurrentEntity);

  Long getStatisticLogger_Delay();

  void setStatisticLogger_Delay(Long statisticLoggerDelay);

  Long getStatisticLogger_Pause();

  void setStatisticLogger_Pause(Long statisticLoggerPause);
}
