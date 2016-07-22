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

package org.jdiameter.common.api.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jdiameter.common.api.statistic.IStatistic;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DummyConcurrentFactory implements IConcurrentFactory {

  @Override
  public Thread getThread(Runnable runnuble) {
    return new Thread(runnuble);
  }

  @Override
  public Thread getThread(String namePrefix, Runnable runnuble) {
    return new Thread(runnuble, namePrefix);
  }

  @Override
  public List<Thread> getThreads() {
    return new ArrayList<Thread>();
  }

  @Override
  public ThreadGroup getThreadGroup() {
    return null;
  }

  @Override
  public ScheduledExecutorService getScheduledExecutorService(String name) {
    return Executors.newScheduledThreadPool(4);
  }

  @Override
  public Collection<ScheduledExecutorService> getScheduledExecutorServices() {
    return new ArrayList<ScheduledExecutorService>();
  }

  @Override
  public void shutdownNow(ScheduledExecutorService service) {
  }

  @Override
  public IStatistic getStatistic() {
    return null;
  }

  @Override
  public List<IStatistic> getStatistics() {
    // TODO Auto-generated method stub
    return new ArrayList<IStatistic>();
  }

  @Override
  public void shutdownAllNow() {
  }
}
