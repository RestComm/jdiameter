package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IRouter;
import org.jdiameter.server.impl.helpers.Loggers;
import static org.jdiameter.server.impl.helpers.StatisticTypes.NET_LIST_COUNTER;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class NetWorkImpl implements INetwork {

    protected Logger logger = Logger.getLogger(Loggers.NetWork.fullName());

    protected IMutablePeerTable manager;
    protected IRouter router;
    protected IMetaData metaData;
    private final ApplicationId commonAuthAppId = ApplicationId.createByAuthAppId(0, 0xffffffff);
    private final ApplicationId commonAccAppId = ApplicationId.createByAccAppId(0, 0xffffffff);
    private final ConcurrentHashMap<ApplicationId, NetworkReqListener> listeners = new ConcurrentHashMap<ApplicationId, NetworkReqListener>();

    protected StatisticRecord nrlStat = new StatisticRecordImpl("ReqLisCount", "Count of network request listeners", NET_LIST_COUNTER,
            new StatisticRecordImpl.Counter() {
                public int getValueAsInt() {
                    return listeners.size();
                }
            });

    protected Statistic statistic = new StatisticImpl("Network", "Network statistic", nrlStat);

    public NetWorkImpl(IMetaData metaData, IRouter router) {
        this.router = router;
        this.metaData = metaData;
        this.router.setNetWork(this);
    }

    public void addNetworkReqListener(NetworkReqListener networkReqListener, ApplicationId... applicationId) throws ApplicationAlreadyUseException {
        for (ApplicationId a : applicationId) {
            if ( listeners.containsKey(commonAuthAppId) || listeners.containsKey(commonAccAppId) )
                throw new ApplicationAlreadyUseException(a + " already use by common application id");

            if (listeners.containsKey(applicationId))
                throw new ApplicationAlreadyUseException(a + " already use");

            listeners.put(a, networkReqListener);
            metaData.addApplicationId(a);
        }
    }

    public void removeNetworkReqListener(ApplicationId... applicationId) {
        for (ApplicationId a : applicationId) {
            listeners.remove(a);
            metaData.remApplicationId(a);
        }
    }


    public Peer addPeer(String name, String realm, boolean connecting) {
        if (manager != null)
            try {
                return manager.addPeer(new URI(name), realm, connecting);
            } catch (Exception e) {
                return null;
            }
        else
            return null;
    }


    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;
    }

    public Realm addRealm(String name, ApplicationId applicationId, LocalAction localAction,  boolean dynamic, long expirationTime) {
        return router.addRealm(name,applicationId, localAction,dynamic,expirationTime);
    }

    public Realm remRealm(String name) {
        return router.remRealm(name);
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public NetworkReqListener getListener(ApplicationId appId) {
        if (appId == null) return null;
        if (listeners.containsKey(commonAuthAppId))
            return listeners.get(commonAuthAppId);
        else
        if (listeners.containsKey(commonAccAppId))
            return listeners.get(commonAccAppId);
        else
            return listeners.get(appId);
    }

    public void setPeerManager(IMutablePeerTable manager) {
        this.manager = manager;
    }
    
}
