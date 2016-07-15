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

package org.jdiameter.server.impl;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntryIndex;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntryhighThreshold;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadEntrylowThreshold;
import static org.jdiameter.server.impl.helpers.Parameters.OverloadMonitor;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.OverloadListener;
import org.jdiameter.api.URI;
import org.jdiameter.server.api.IOverloadManager;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class OverloadManagerImpl implements IOverloadManager {

  private ConcurrentLinkedQueue<OverloadInfo> listeners = new ConcurrentLinkedQueue<OverloadInfo>();
  private ConcurrentHashMap<Object, AppOverloadInfo> appInfo = new ConcurrentHashMap<Object, AppOverloadInfo>();

  public OverloadManagerImpl(Configuration config) {
    Configuration[] entries = config.getChildren(OverloadMonitor.ordinal());
    if (entries == null) {
      return;
    }
    for (Configuration e : entries) {
      ApplicationId appId = null;
      Configuration[] cAppId = e.getChildren(ApplicationId.ordinal());
      for (Configuration i : cAppId) {
        if ( i.getLongValue(AuthApplId.ordinal(), 0) != 0 ) {
          appId = org.jdiameter.api.ApplicationId.createByAuthAppId(
              i.getLongValue(VendorId.ordinal(), 0),
              i.getLongValue(AuthApplId.ordinal(), 0)
              );
        }
        else {
          appId = org.jdiameter.api.ApplicationId.createByAccAppId(
              i.getLongValue(VendorId.ordinal(), 0),
              i.getLongValue(AcctApplId.ordinal(), 0)
              );
        }
        break;
      }
      if (appId == null) {
        continue;
      }
      AppOverloadInfo info = new AppOverloadInfo(appId);
      info.appendEntry(
          e.getIntValue(OverloadEntryIndex.ordinal(), 0),
          e.getDoubleValue(OverloadEntrylowThreshold.ordinal(), 0),
          e.getDoubleValue(OverloadEntryhighThreshold.ordinal(), 0)
      );
      appInfo.put(appId, info);
    }
  }

  @Override
  public void parentAppOverloadDetected(ApplicationId applicationId, int type, double value) {
    AppOverloadInfo app = appInfo.get(createKey(applicationId));
    if (app != null) {
      app.updateInformation(type, value);
    }
  }

  @Override
  public void parentAppOverloadCeased(ApplicationId applicationId, int type) {
    AppOverloadInfo app = appInfo.get(createKey(applicationId));
    if (app != null) {
      app.updateInformation(type, 0);
    }
  }

  private Object createKey(final ApplicationId appId) {
    return new Object() {
      @Override
      public int hashCode() {
        return appId.hashCode();
      }
      @Override
      public boolean equals(Object obj) {
        return appId.equals(obj);
      }
    };
  }

  @Override
  public boolean isParenAppOverload(final ApplicationId appId) {
    if (appId == null) {
      return false;
    }
    AppOverloadInfo app = appInfo.get( createKey(appId) );
    return app != null && app.isOverload();
  }

  @Override
  public boolean isParenAppOverload(final ApplicationId appId, final int type) {
    AppOverloadInfo app = appInfo.get( createKey(appId) );
    return app != null && app.isOverload(type);
  }

  @Override
  public void addOverloadListener(OverloadListener overloadListener, double lowThreshold, double highThreshold, int qIndex) {
    listeners.add(new OverloadInfo(overloadListener, lowThreshold, highThreshold, qIndex));
  }

  @Override
  public void removeOverloadListener(OverloadListener overloadListener, int qIndex) {
    listeners.remove(new OverloadInfo(overloadListener, qIndex));
  }

  @Override
  public void changeNotification(int index, URI uri, double value) {
    for (OverloadInfo e : listeners) {
      if (e.getCode() == index) {
        e.changeNotification(uri, value);
      }
    }
  }

  public static class AppOverloadInfo {
    private ApplicationId appId;
    private ArrayList <AppOverloadInfoEntry> entries = new ArrayList<AppOverloadInfoEntry>();
    private final Object lock = new Object();

    public ApplicationId getAppId() {
      return appId;
    }

    public AppOverloadInfo(ApplicationId appId) {
      this.appId = appId;
    }

    public void appendEntry(int type, double lowThreshold, double highThreshold) {
      entries.add(new AppOverloadInfoEntry(type, lowThreshold, highThreshold));
    }

    public boolean isOverload() {
      for (AppOverloadInfoEntry e : entries) {
        if (e.isOverload()) {
          return true;
        }
      }
      return false;
    }

    public boolean isOverload(int type) {
      for (AppOverloadInfoEntry e : entries) {
        if (e.getType() == type) {
          synchronized (lock) {
            if (e.isOverload()) {
              return true;
            }
          }
        }
      }
      return false;
    }

    public void updateInformation(int type, double threshold) {
      for (AppOverloadInfoEntry e : entries) {
        if (e.getType() == type) {
          synchronized (lock) {
            e.updateInformation(threshold);
          }
        }
      }
    }
  }

  public static class AppOverloadInfoEntry {
    private int type;
    private double lowThreshold, highThreshold;
    private double currentValue;
    private final Object lock = new Object();

    public AppOverloadInfoEntry(int type, double lowThreshold, double highThreshold) {
      this.type = type;
      this.lowThreshold = lowThreshold;
      this.highThreshold = highThreshold;
    }


    public int getType() {
      return type;
    }

    public double getLowThreshold() {
      return lowThreshold;
    }

    public double getHighThreshold() {
      return highThreshold;
    }

    public double getCurrentValue() {
      return currentValue;
    }

    public void updateInformation(double threshold) {
      synchronized (lock) {
        this.currentValue = threshold;
      }
    }

    public boolean isOverload() {
      synchronized (lock) {
        return  (currentValue >= lowThreshold && currentValue <= highThreshold);
      }
    }
  }

  public static class OverloadInfo {

    private OverloadListener overloadListener;
    private double lowThreshold, highThreshold;
    private int qIndex;
    private boolean isOverload;
    private Lock lock = new ReentrantLock();

    public OverloadInfo(OverloadListener overloadListener, int qIndex) {
      this.overloadListener = overloadListener;
      this.qIndex = qIndex;
    }

    public OverloadInfo(OverloadListener overloadListener, double lowThreshold, double highThreshold, int qIndex) {
      this.overloadListener = overloadListener;
      this.lowThreshold = lowThreshold;
      this.highThreshold = highThreshold;
      this.qIndex = qIndex;
    }

    public void changeNotification(URI uri, double value) {
      if ( value >= lowThreshold && value <= highThreshold ) {
        overloadListener.overloadDetected(uri, value);
        lock.lock();
        isOverload = true;
        lock.unlock();
      } else {
        lock.lock();
        if (isOverload) {
          overloadListener.overloadCeased(uri);
          isOverload = false;
        }
        lock.unlock();
      }
    }

    public int getCode() {
      return qIndex;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      OverloadInfo that = (OverloadInfo) o;

      if (qIndex != that.qIndex) {
        return false;
      }
      if (overloadListener != null ? !overloadListener.equals(that.overloadListener) : that.overloadListener != null) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result;
      result = (overloadListener != null ? overloadListener.hashCode() : 0);
      result = 31 * result + qIndex;
      return result;
    }

  }
}
