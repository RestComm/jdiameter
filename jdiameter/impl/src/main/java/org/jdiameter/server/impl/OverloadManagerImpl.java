package org.jdiameter.server.impl;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.OverloadListener;
import org.jdiameter.api.URI;
import org.jdiameter.server.api.IOverloadManager;
import static org.jdiameter.server.impl.helpers.Parameters.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OverloadManagerImpl implements IOverloadManager {

    private ConcurrentLinkedQueue<OverloadInfo> listeners = new ConcurrentLinkedQueue<OverloadInfo>();    
    private ConcurrentHashMap<Object, AppOverloadInfo> appInfo = new ConcurrentHashMap<Object, AppOverloadInfo>();

    public OverloadManagerImpl(Configuration config) {
        Configuration[] entries = config.getChildren(OverloadMonitor.ordinal());
        if (entries == null) return;
        for (Configuration e : entries) {
            ApplicationId appId = null;
            Configuration[] cAppId = e.getChildren(ApplicationId.ordinal());
            for (Configuration i : cAppId) {
                if ( i.getLongValue(AuthApplId.ordinal(), 0) != 0 )
                    appId = org.jdiameter.api.ApplicationId.createByAuthAppId(
                        i.getLongValue(VendorId.ordinal(), 0),
                        i.getLongValue(AuthApplId.ordinal(), 0)
                    );
                else
                    appId = org.jdiameter.api.ApplicationId.createByAccAppId(
                        i.getLongValue(VendorId.ordinal(), 0),
                        i.getLongValue(AcctApplId.ordinal(), 0)
                    );
                break;
            }
            if (appId == null) continue;
            AppOverloadInfo info = new AppOverloadInfo(appId);
            info.appendEntry(
                e.getIntValue(OverloadEntryIndex.ordinal(), 0),
                e.getDoubleValue(OverloadEntrylowThreshold.ordinal(), 0),
                e.getDoubleValue(OverloadEntryhighThreshold.ordinal(), 0)
            );
            appInfo.put(appId, info);
        }
    }

    public void parentAppOverloadDetected(ApplicationId applicationId, int type, double value) {
        AppOverloadInfo app = appInfo.get(createKey(applicationId));
        if (app != null) {
            app.updateInformation(type, value);
        }
    }

    public void parentAppOverloadCeased(ApplicationId applicationId, int type) {
        AppOverloadInfo app = appInfo.get(createKey(applicationId));
        if (app != null) {
            app.updateInformation(type, 0);
        }
    }

    private Object createKey(final ApplicationId appId) {
        return new Object() {
            public int hashCode() {
                return appId.hashCode();
            }
            public boolean equals(Object obj) {
                return appId.equals(obj);
            }
        };
    }

    public boolean isParenAppOverload(final ApplicationId appId) {
        if (appId == null) return false;
        AppOverloadInfo app = appInfo.get( createKey(appId) );
        return app != null && app.isOverload();
    }

    public boolean isParenAppOverload(final ApplicationId appId, final int type) {
        AppOverloadInfo app = appInfo.get( createKey(appId) );
        return app != null && app.isOverload(type);
    }

    public void addOverloadListener(OverloadListener overloadListener, double lowThreshold, double highThreshold, int qIndex) {
        listeners.add(new OverloadInfo(overloadListener, lowThreshold, highThreshold, qIndex));
    }

    public void removeOverloadListener(OverloadListener overloadListener, int qIndex) {
         listeners.remove(new OverloadInfo(overloadListener, qIndex));
    }

    public void changeNotification(int index, URI uri, double value) {
        for (OverloadInfo e : listeners)
            if (e.getCode() == index) e.changeNotification(uri, value);
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
                if (e.isOverload()) return true;
            }
            return false;
        }

        public boolean isOverload(int type) {
            for (AppOverloadInfoEntry e : entries) {
                if (e.getType() == type)
                    synchronized(lock) {
                        if (e.isOverload()) return true;
                    }
            }
            return false;
        }

        public void updateInformation(int type, double threshold) {
            for (AppOverloadInfoEntry e : entries) {
                    if (e.getType() == type)
                        synchronized(lock) {
                            e.updateInformation(threshold);
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
            synchronized(lock) {
                this.currentValue = threshold;
            }
        }

        public boolean isOverload() {
            synchronized(lock) {
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

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OverloadInfo that = (OverloadInfo) o;

            if (qIndex != that.qIndex) return false;
            if (overloadListener != null ? !overloadListener.equals(that.overloadListener) : that.overloadListener != null)
                return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (overloadListener != null ? overloadListener.hashCode() : 0);
            result = 31 * result + qIndex;
            return result;
        }

    }
}
