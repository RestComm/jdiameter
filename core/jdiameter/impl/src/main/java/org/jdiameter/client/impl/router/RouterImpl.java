package org.jdiameter.client.impl.router;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.*;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.helpers.Loggers;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouterImpl implements IRouter {

    public static final int DONT_CACHE = 0;
    public static final int ALL_SESSION = 1;
    public static final int ALL_REALM = 2;
    public static final int REALM_AND_APPLICATION = 3;
    public static final int ALL_APPLICATION = 4;
    public static final int ALL_HOST = 5;
    public static final int ALL_USER = 6;
    //
    protected Logger logger = Logger.getLogger(Loggers.Router.fullName());
    protected MetaData metaData;
    //
    private ConcurrentHashMap<String, String[]> network = new ConcurrentHashMap<String, String[]>();
    // Redirection feature
    public final int REDIRECT_TABLE_SIZE = 1024;
    protected ConcurrentHashMap<RedirectEntry, RedirectEntry> redirectTable = new ConcurrentHashMap<RedirectEntry, RedirectEntry>(REDIRECT_TABLE_SIZE);
    protected ScheduledExecutorService redirectScheduler = Executors.newScheduledThreadPool(1);
    protected Runnable redirectTask = new Runnable() {
        public void run() {
            for (RedirectEntry entry : redirectTable.values()) {
                if (entry.getExpiredTime() <= System.currentTimeMillis())
                    redirectTable.remove(entry);
            }
        }
    };
    protected ScheduledFuture redirectEntryHandler = redirectScheduler.scheduleAtFixedRate(redirectTask, 1, 1, TimeUnit.SECONDS);
    // Answer routing feature
    public static final int REQUEST_TABLE_SIZE = 10 * 1024;
    public static final int REQUEST_TABLE_CLEAR_SIZE = 5 * 1024;
    protected ReadWriteLock requestLock = new ReentrantReadWriteLock();
    protected Map<Long, AnswerEntry> requestEntryTable = new ConcurrentHashMap<Long, AnswerEntry>(REQUEST_TABLE_SIZE);
    protected List<Long> requestSortedEntryTable = new java.util.concurrent.CopyOnWriteArrayList<Long>();

    public RouterImpl(Configuration config, MetaData aMetaData) {
        metaData = aMetaData;
        init();
        loadConfiguration(config);
    }

    protected void init() {
    }

    protected void loadConfiguration(Configuration config) {
        // load realm entry
        Configuration[] items = config.getChildren(RealmTable.ordinal());
        if (items != null & items.length > 0) {
            String entry;
            for (Configuration c:items) {
                entry = c.getStringValue(RealmEntry.ordinal(), null);
                if (entry != null)
                    try {
                        int pos = entry.indexOf(':');
                        String realm = entry.substring(0, pos).trim();
                        String[] hosts = entry.substring(pos + 1).split(",");
                        network.put(realm, hosts);
                    } catch (Exception e) {
                       logger.log(Level.WARNING, "Can not append realm entry", e);
                    }
            }
        }
    }

    public void registerRequestRouteInfo(IMessage request) {
        try {
            requestLock.readLock().lock();
            long hopByHopId = request.getHopByHopIdentifier();
            Avp hostAvp = request.getAvps().getAvp(Avp.ORIGIN_HOST);
            Avp realmAvp = request.getAvps().getAvp(Avp.ORIGIN_REALM);
            AnswerEntry entry = new AnswerEntry(
                            hopByHopId, hostAvp != null ? hostAvp.getOctetString() : null,
                            realmAvp != null ? realmAvp.getOctetString() : null
            );
            requestEntryTable.put(hopByHopId, entry);
            requestSortedEntryTable.add(hopByHopId);
            if ( requestEntryTable.size() > REQUEST_TABLE_SIZE) {
              for (int i = 0; i < REQUEST_TABLE_CLEAR_SIZE ; i++) {
                Long lastKey = requestSortedEntryTable.remove(0);
                requestEntryTable.remove( lastKey );
              }
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "Can not store route info", e);
        } finally{
            requestLock.readLock().unlock();
        }
    }

    public String[] getRequestRouteInfo(long hopByHopIdentifier) {
        requestLock.writeLock().lock();
        AnswerEntry ans = requestEntryTable.get( hopByHopIdentifier );
        requestLock.writeLock().unlock();
        if (ans != null)
        {
        	return new String[] {ans.getHost(), ans.getRealm()};
        }
        else
        {
            return null;
        }
    }

    public void updateRedirectInformation(IMessage answer) throws InternalException, RouteException {
        try {
            String[] redirectHosts = null;
            if (answer.getAvps().getAvps(Avp.REDIRECT_HOST) != null) {
                AvpSet avps = answer.getAvps().getAvps(Avp.REDIRECT_HOST);
                redirectHosts = new String[avps.size()];
                int i = 0;
                // loop detected
                for (Avp avp : avps) {
                    String r =  avp.getOctetString();
                    if (r.equals(metaData.getLocalPeer().getUri().getFQDN()))
                        throw new RouteException("Loop detected");
                    redirectHosts[i++] = r;
                }
            }
            //
            int redirectUsage = DONT_CACHE;
            if (answer.getAvps().getAvp(Avp.REDIRECT_HOST_USAGE) != null)
                redirectUsage = answer.getAvps().getAvp(Avp.REDIRECT_HOST_USAGE).getInteger32();
            //
            if (redirectUsage != DONT_CACHE) {
                long redirectCacheTime = 0;
                if (answer.getAvps().getAvp(Avp.REDIRECT_MAX_CACHE_TIME) != null)
                    redirectCacheTime = answer.getAvps().getAvp(Avp.REDIRECT_MAX_CACHE_TIME).getUnsigned32();
                String primaryKey = null;
                ApplicationId secondaryKey = null;
                switch (redirectUsage) {
                    case ALL_SESSION:
                        if (answer.getSessionId() != null)
                            primaryKey = answer.getSessionId();
                        break;
                    case ALL_REALM:
                        if (answer.getAvps().getAvp(Avp.DESTINATION_REALM) != null)
                            primaryKey = answer.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
                        break;
                    case REALM_AND_APPLICATION:
                        if (answer.getAvps().getAvp(Avp.DESTINATION_REALM) != null)
                            primaryKey = answer.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString();
                        secondaryKey = answer.getSingleApplicationId();
                        break;
                    case ALL_APPLICATION:
                        secondaryKey = answer.getSingleApplicationId();
                        break;
                    case ALL_HOST:
                        if (answer.getAvps().getAvp(Avp.DESTINATION_HOST) != null)
                            primaryKey = answer.getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString();
                        break;
                    case ALL_USER:
                        if (answer.getAvps().getAvp(Avp.USER_NAME) != null)
                            primaryKey = answer.getAvps().getAvp(Avp.USER_NAME).getUTF8String();
                        break;
                }
                //
                if (REDIRECT_TABLE_SIZE > redirectTable.size()) {
                    RedirectEntry e = new RedirectEntry(
                            primaryKey, secondaryKey, redirectCacheTime, redirectUsage, redirectHosts
                    );
                    redirectTable.put(e, e);
                    redirectProcessing(
                            answer,
                            answer.getAvps().getAvp(Avp.DESTINATION_REALM).getOctetString(),
                            answer.getAvps().getAvp(Avp.DESTINATION_HOST).getOctetString()
                    );
                } else {
                     if (redirectHosts != null && redirectHosts.length > 0) {
                        String destHost = redirectHosts[0];
                        setRouteInfo(answer, getRealmForPeer(destHost), destHost);                                                                                
                    }
                }
            } else {
                if (redirectHosts != null && redirectHosts.length > 0) {
                    String destHost = redirectHosts[0];
                    setRouteInfo(answer, getRealmForPeer(destHost), destHost);
                }
            }
        } catch (AvpDataException exc) {
            throw new InternalException(exc);
        }
    }

    public IPeer getPeer(IMessage message, IPeerTable manager) throws RouteException, AvpDataException {

        String destRealm = null;
        String destHost = null;
        // Get destination information
        String[] info = null;
        if ( !message.isRequest() )
        {
            info = getRequestRouteInfo(message.getHopByHopIdentifier());
            if (info != null) {
            	destHost = info[0];
            	destRealm = info[1];
        	}
        }else {
            Avp avpRealm = message.getAvps().getAvp(Avp.DESTINATION_REALM);
            if (avpRealm == null)
                throw new RouteException("Destination realm avp is empty");
            destRealm = avpRealm.getOctetString();

            Avp avpHost = message.getAvps().getAvp(Avp.DESTINATION_HOST);
            if (avpHost != null)
                destHost = avpHost.getOctetString();
        }
        
        IPeer peer = getPeerPredProcessing(message, destRealm, destHost);

        if (peer != null) return peer;


        // Check realm name
        if ( !checkRealm(destRealm) )
            throw new RouteException("Unknown realm name [" + destRealm + "]");

        // Redirect processing
        redirectProcessing(message, destRealm, destHost);
        // Check previous context information
        if (message.getPeer() != null && destHost != null && destHost.equals(message.getPeer().getUri()) &&
                message.getPeer().hasValidConnection()) {

            return message.getPeer();
        }

        // Balansing procedure
        IPeer c = destHost != null ? manager.getPeerByName(destHost) : null;
        if (c != null && c.hasValidConnection() ) {
            return c;
        } else {
            String peers[] = getRealmPeers(destRealm);
            if (peers == null || peers.length == 0)
                throw new RouteException("Can not find context by route information [" + destRealm + " ," + destHost + "]");
            // Collect peers
            ArrayList<IPeer> avaliblePeers = new ArrayList<IPeer>(5);
            for (String peerName : peers) {
                IPeer localPeer = manager.getPeerByName(peerName);
                if (localPeer != null && localPeer.hasValidConnection())
                    avaliblePeers.add(localPeer);
            }
            // Balansing
            peer = selectPeer(avaliblePeers);
            if (peer == null)
                throw new RouteException("Can not find valid connection to peer[" + destHost + "] in realm[" + destRealm + "]");
            return peer;
        }
    }

    protected IPeer getPeerPredProcessing(IMessage message, String destRealm, String destHost) {
        return null;
    }

    public void start() {
        redirectEntryHandler = redirectScheduler.scheduleAtFixedRate(redirectTask, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        if (redirectEntryHandler != null)
            redirectEntryHandler.cancel(true);
        if (redirectTable != null)
            redirectTable.clear();
        if (requestEntryTable != null)
            requestEntryTable.clear();
        if (requestSortedEntryTable != null)
            requestSortedEntryTable.clear();
    }

    public void destroy() {
        if (redirectEntryHandler != null)
            redirectEntryHandler.cancel(true);
        redirectEntryHandler = null;

        if (redirectScheduler != null) {
            redirectScheduler.shutdownNow();
            Executors.unconfigurableScheduledExecutorService(redirectScheduler);
        }
        redirectScheduler = null;

        if (redirectTable != null)
            redirectTable.clear();
        redirectTable = null;

        if (requestEntryTable != null)
            requestEntryTable.clear();
        requestEntryTable = null;
        if (requestSortedEntryTable != null)
            requestSortedEntryTable.clear();
        requestEntryTable = null;
    }

    protected IPeer selectPeer(List<IPeer> avaliblePeers) {
        IPeer p = null;
        for (IPeer c : avaliblePeers) {
            if (p == null || c.getRaiting() >= p.getRaiting())
                p = c;
        }
        return p;
    }

    protected void redirectProcessing(IMessage message, String destRealm, String destHost) throws AvpDataException {
        String userName = null;
        // get Session id
        String sessionId = message.getSessionId();
        //
        Avp avpUserName = message.getAvps().getAvp(Avp.USER_NAME);
        // Get application id
        ApplicationId appId = message.getSingleApplicationId();
        // User name
        if (avpUserName != null)
            userName = avpUserName.getUTF8String();
        // Processing table
        for (RedirectEntry e : redirectTable.values()) {
            boolean isContinue = false;
            switch (e.getUsageType()) {
                case 1: // Usage type: ALL SESSION
                    isContinue = sessionId != null && e.primaryKey != null &
                            sessionId.equals(e.primaryKey);
                    break;
                case 2: // Usage type: ALL REALM
                    isContinue = destRealm != null && e.primaryKey != null &
                            destRealm.equals(e.primaryKey);
                    break;
                case 3: // Usage type: REALM AND APPLICATION
                    isContinue = destRealm != null & appId != null & e.primaryKey != null & e.secondaryKey != null &
                            destRealm.equals(e.primaryKey) & appId.equals(e.secondaryKey);
                    break;
                case 4: // Usage type: ALL APPLICATION
                    isContinue = appId != null & e.secondaryKey != null &
                            appId.equals(e.secondaryKey);
                    break;
                case 5: // Usage type: ALL HOST
                    isContinue = destHost != null & e.primaryKey != null &
                            destHost.equals(e.primaryKey);
                    break;
                case 6: // Usage type: ALL USER
                    isContinue = userName != null & e.primaryKey != null &
                            userName.equals(e.primaryKey);
                    break;
            }
            // Update message redirect information
            if (isContinue) {
                destHost  = e.getRedirectHost();
                destRealm = getRealmForPeer(destHost);
                setRouteInfo(message, destRealm, destHost);
            }
        }
    }

    private void setRouteInfo(IMessage message, String destRealm, String destHost) {
        message.getAvps().removeAvp(Avp.DESTINATION_REALM);
        message.getAvps().removeAvp(Avp.DESTINATION_HOST);
        if (destRealm != null)
            message.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
        if (destHost != null)
            message.getAvps().addAvp(Avp.DESTINATION_HOST, destHost, true, false,  true);
    }

    public String  getRealmForPeer(String destHost) {
        for (String key : getRealmsName()) {
            for (String h : getRealmPeers(key)) {
                if (h.trim().equals(destHost.trim()))
                    return key;
            }
        }
        return null;
    }

    protected boolean checkRealm(String name) {
      return name == null ? false : network.containsKey(name);
    }

    protected Set<String> getRealmsName() {
        return network.keySet();
    }

    protected String[] getRealmPeers(String key) {
        return network.get(key);
    }

    protected class RedirectEntry {

        final long createTime = System.currentTimeMillis();

        String primaryKey;
        ApplicationId secondaryKey;
        long liveTime;
        int usageType;
        String[] hosts;

        public RedirectEntry(String key1, ApplicationId key2, long time, int usage, String[] aHosts) throws InternalError {
            // Check arguments
            if (key1 == null && key2 == null)
                throw new InternalError("Incorrect redirection key.");
            if (aHosts == null || aHosts.length == 0)
                throw new InternalError("Incorrect redirection hosts.");
            // Set values
            primaryKey = key1;
            secondaryKey = key2;
            liveTime = time * 1000;
            usageType = usage;
            hosts = aHosts;
        }

        public int getUsageType() {
            return usageType;
        }

        public String[] getRedirectHosts() {
            return hosts;
        }

        public String getRedirectHost() {
            return hosts[hosts.length - 1];
        }

        public long getExpiredTime() {
            return createTime + liveTime;
        }

        public int hashCode() {
            int result = (primaryKey != null ? primaryKey.hashCode() : 0);
            result = 31 * result + (secondaryKey != null ? secondaryKey.hashCode() : 0);
            result = 31 * result + (int) (liveTime ^ (liveTime >>> 32));
            result = 31 * result + usageType;
            result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
            return result;
        }

        public boolean equals(Object other) {

            if (other == this)
                return true;

            if (other instanceof RedirectEntry) {
                RedirectEntry that = (RedirectEntry) other;
                return liveTime == that.liveTime && usageType == that.usageType &&
                        Arrays.equals(hosts, that.hosts) && !(primaryKey != null ? !primaryKey.equals(that.primaryKey) : that.primaryKey != null) &&
                        !(secondaryKey != null ? !secondaryKey.equals(that.secondaryKey) : that.secondaryKey != null);
            } else {
                return false;
            }
        }
    }

    protected class AnswerEntry {

        final long createTime = System.nanoTime();

        Long hopByHopId;
        String host, realm;


        public AnswerEntry(Long hopByHopId) {
            this.hopByHopId = hopByHopId;
        }

        public AnswerEntry(Long hopByHopId, String host, String realm) throws InternalError {
            this.hopByHopId = hopByHopId;
            this.host = host;
            this.realm = realm;
        }

        public long getCreateTime() {
            return createTime;
        }

        public Long getHopByHopId() {
            return hopByHopId;
        }

        public String getHost() {
            return host;
        }

        public String getRealm() {
            return realm;
        }


        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AnswerEntry that = (AnswerEntry) o;
            return hopByHopId == that.hopByHopId;
        }


        public String toString() {
            return "AnswerEntry{" +
                    "createTime=" + createTime +
                    ", hopByHopId=" + hopByHopId +
                    '}';
        }
    }
}
