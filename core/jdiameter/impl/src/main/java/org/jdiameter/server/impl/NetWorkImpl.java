package org.jdiameter.server.impl;

import static org.jdiameter.server.impl.helpers.StatisticTypes.NET_APPID_LIST_COUNTER;
import static org.jdiameter.server.impl.helpers.StatisticTypes.NET_SELECTOR_LIST_COUNTER;

import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.ApplicationAlreadyUseException;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Peer;
import org.jdiameter.api.Realm;
import org.jdiameter.api.Selector;
import org.jdiameter.api.Statistic;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetWorkImpl implements INetwork {

  protected Logger logger = LoggerFactory.getLogger(NetWorkImpl.class);

    protected IMutablePeerTable manager;
    protected IRouter router;
    protected IMetaData metaData;
    private final ApplicationId commonAuthAppId = ApplicationId.createByAuthAppId(0, 0xffffffff);
    private final ApplicationId commonAccAppId = ApplicationId.createByAccAppId(0, 0xffffffff);
    private final ConcurrentHashMap<ApplicationId, NetworkReqListener> appIdToNetListener = new ConcurrentHashMap<ApplicationId, NetworkReqListener>();
    private final ConcurrentHashMap<Selector, NetworkReqListener> selectorToNetListener = new ConcurrentHashMap<Selector, NetworkReqListener>();
    
    protected StatisticRecord nrlStat = new StatisticRecordImpl("ReqLisCount", "Count of network request appIdToNetListener", NET_APPID_LIST_COUNTER,
            new StatisticRecordImpl.Counter() {
                public int getValueAsInt() {
                    return appIdToNetListener.size();
                }
            });

    protected StatisticRecord nslStat = new StatisticRecordImpl("SekectorLisCount", "Count of network request selectorToNetListener", NET_SELECTOR_LIST_COUNTER,
        new StatisticRecordImpl.Counter() {
            public int getValueAsInt() {
                return selectorToNetListener.size();
            }
    });

    protected Statistic statistic = new StatisticImpl("Network", "Network statistic", nrlStat, nslStat);

    public NetWorkImpl(IMetaData metaData, IRouter router) {
        this.router = router;
        this.metaData = metaData;
        this.router.setNetWork(this);
    }

    public void addNetworkReqListener(NetworkReqListener networkReqListener, ApplicationId... applicationId) throws ApplicationAlreadyUseException {
        for (ApplicationId a : applicationId) {
            if ( appIdToNetListener.containsKey(commonAuthAppId) || appIdToNetListener.containsKey(commonAccAppId) )
                throw new ApplicationAlreadyUseException(a + " already use by common application id");

            if (appIdToNetListener.containsKey(applicationId))
                throw new ApplicationAlreadyUseException(a + " already use");

            appIdToNetListener.put(a, networkReqListener);
            metaData.addApplicationId(a);
        }
    }

    public void addNetworkReqListener(NetworkReqListener listener, Selector<Message, ApplicationId>... selectors) {
        for (Selector<Message, ApplicationId> s : selectors) {
            selectorToNetListener.put(s, listener);
            ApplicationId ap = s.getMetaData();
            metaData.addApplicationId(ap);
        }
    }

    public void removeNetworkReqListener(ApplicationId... applicationId) {
        for (ApplicationId a : applicationId) {
          appIdToNetListener.remove(a);
          for (Selector<Message, ApplicationId> s : selectorToNetListener.keySet()) {
            if (s.getMetaData().equals(a)) return;
          }
          metaData.remApplicationId(a);
        }
    }

    public void removeNetworkReqListener(Selector<Message, ApplicationId>... selectors) {
      for (Selector<Message, ApplicationId> s : selectors) {
        selectorToNetListener.remove(s);
        if (appIdToNetListener.containsKey(s.getMetaData())) {
          return;
        }
        
        for (Selector<Message, ApplicationId> i : selectorToNetListener.keySet()) {
          if (i.getMetaData().equals(s.getMetaData())) {
            return;
          }
        }
        metaData.remApplicationId(s.getMetaData());
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

    public NetworkReqListener getListener(IMessage message) {
      if (message == null) return null;
      for (Selector<Message, ApplicationId> s : selectorToNetListener.keySet()) {
          boolean r = s.checkRule(message);
          if (r) return selectorToNetListener.get(s);
      }

      ApplicationId appId = message.getSingleApplicationId();
      if (appId == null) return null;
      if (appIdToNetListener.containsKey(commonAuthAppId))
          return appIdToNetListener.get(commonAuthAppId);
      else
      if (appIdToNetListener.containsKey(commonAccAppId))
          return appIdToNetListener.get(commonAccAppId);
      else
          return appIdToNetListener.get(appId);
    }

    public void setPeerManager(IMutablePeerTable manager) {
        this.manager = manager;
    }
    
}
