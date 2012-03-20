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

package org.jdiameter.common.api.concurrent;

import org.jdiameter.common.api.statistic.IStatistic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DummyConcurrentFactory implements IConcurrentFactory {

  public Thread getThread(Runnable runnuble) {
    return new Thread(runnuble);
  }

  public Thread getThread(String namePrefix, Runnable runnuble) {
    return new Thread(runnuble, namePrefix);
  }

  public List<Thread> getThreads() {
    return new ArrayList<Thread>();
  }

  public ThreadGroup getThreadGroup() {
    return null;
  }

  public ScheduledExecutorService getScheduledExecutorService(String name) {
    return Executors.newScheduledThreadPool(4);
  }

  public Collection<ScheduledExecutorService> getScheduledExecutorServices() {
    return new ArrayList<ScheduledExecutorService>();
  }

  public void shutdownNow(ScheduledExecutorService service) {
  }

  public IStatistic getStatistic() {
    return null;
  }

  @Override
   public List<IStatistic> getStatistics() {
	// TODO Auto-generated method stub
	return new ArrayList<IStatistic>();
}

public void shutdownAllNow() {
  }
}
