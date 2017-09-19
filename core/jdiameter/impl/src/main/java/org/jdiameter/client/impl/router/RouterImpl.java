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

package org.jdiameter.client.impl.router;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.Agent;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;
import static org.jdiameter.server.impl.helpers.Parameters.RequestTable;
import static org.jdiameter.server.impl.helpers.Parameters.RequestTableClearSize;
import static org.jdiameter.server.impl.helpers.Parameters.RequestTableSize;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
//PCB added for thread safe
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.IAnswer;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.client.api.router.IRouter;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.agent.IAgentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Diameter Routing Core
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RouterImpl implements IRouter {

  public static final int DONT_CACHE = 0;
  public static final int ALL_SESSION = 1;
  public static final int ALL_REALM = 2;
  public static final int REALM_AND_APPLICATION = 3;
  public static final int ALL_APPLICATION = 4;
  public static final int ALL_HOST = 5;
  public static final int ALL_USER = 6;
  //
  private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);
  protected MetaData metaData;
  //
  //private ConcurrentHashMap<String, String[]> network = new ConcurrentHashMap<String, String[]>();
  protected IRealmTable realmTable;
  // Redirection feature
  public final int REDIRECT_TABLE_SIZE = 1024;
  //TODO: index it differently.
  protected List<RedirectEntry> redirectTable = new ArrayList<RedirectEntry>(REDIRECT_TABLE_SIZE);
  protected IConcurrentFactory concurrentFactory;

  protected IContainer container;

  // Answer routing feature
  public static int REQUEST_TABLE_SIZE = 10 * 1024;
  public static int REQUEST_TABLE_CLEAR_SIZE = 2 * 1024;

  protected Lock requestEntryTableLock = new ReentrantLock();
  protected ReadWriteLock redirectTableLock = new ReentrantReadWriteLock();
  //PCB added
  protected Map<String, AnswerEntry> requestEntryMap;
  //protected List<Long> requestSortedEntryTable = new ArrayList<Long>();
  protected boolean isStopped = true;

  public RouterImpl(IContainer container, IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData aMetaData) {
    this.concurrentFactory = concurrentFactory;
    this.metaData = aMetaData;
    this.realmTable = realmTable;
    this.container = container;
    logger.debug("Constructor for RouterImpl: Calling loadConfiguration");
    loadConfiguration(config);
  }

  protected void loadConfiguration(Configuration config) {
    logger.debug("Loading Router Configuration. Populating Realms, Application IDs, etc");
    //add local realm : this might not be good
    String localRealm = config.getStringValue(OwnRealm.ordinal(), null);
    String localHost = config.getStringValue(Parameters.OwnDiameterURI.ordinal(), null);
    try {
      this.realmTable.addLocalRealm(localRealm, new URI(localHost).getFQDN());
    }
    catch (UnknownServiceException use) {
      throw new RuntimeException("Unable to create URI from Own URI config value:" + localHost, use);
    }
    catch (URISyntaxException use) {
      throw new RuntimeException("Unable to create URI from Own URI config value:" + localHost, use);
    }
    if (config.getChildren(RequestTable.ordinal()) != null) {
      AppConfiguration requestTableConfig = (AppConfiguration) config.getChildren(org.jdiameter.server.impl.helpers.Parameters.RequestTable.ordinal())[0];
      int tSize = requestTableConfig.getIntValue(RequestTableSize.ordinal(),(Integer) RequestTableSize.defValue());
      int tClearSize = requestTableConfig.getIntValue(RequestTableClearSize.ordinal(),(Integer) RequestTableClearSize.defValue());
      if (tSize > 0 && tClearSize >= tSize) {
        logger.warn("Configuration entry RequestTable, attribute 'clear_size' [{}] should not be greater than 'size' [{}]. Adjusting.", tSize, tClearSize);
        while (tClearSize >= tSize) {
          tSize *= 10;
        }
      }
      REQUEST_TABLE_SIZE = tSize;
      REQUEST_TABLE_CLEAR_SIZE = tClearSize;
    }
    //PCB added thread safety
    this.requestEntryMap = new ConcurrentHashMap<String, AnswerEntry>(REQUEST_TABLE_SIZE);
    logger.debug("Configured Request Table with size[{}] and clear size[{}].", REQUEST_TABLE_SIZE, REQUEST_TABLE_CLEAR_SIZE);

    //add realms based on realm table.
    if (config.getChildren(RealmTable.ordinal()) != null) {
      logger.debug("Going to loop through configured realms and add them into a network map");
      for (Configuration items : config.getChildren(RealmTable.ordinal())) {
        if (items != null) {
          Configuration[] m = items.getChildren(RealmEntry.ordinal());
          for (Configuration c : m) {
            try {
              String name = c.getStringValue(RealmName.ordinal(), "");
              logger.debug("Getting config for realm [{}]", name);
              ApplicationId appId = null;
              {
                Configuration[] apps = c.getChildren(ApplicationId.ordinal());
                if (apps != null) {
                  for (Configuration a : apps) {
                    if (a != null) {
                      long vnd = a.getLongValue(VendorId.ordinal(), 0);
                      long auth = a.getLongValue(AuthApplId.ordinal(), 0);
                      long acc = a.getLongValue(AcctApplId.ordinal(), 0);
                      if (auth != 0) {
                        appId = org.jdiameter.api.ApplicationId.createByAuthAppId(vnd, auth);
                      }
                      else {
                        appId = org.jdiameter.api.ApplicationId.createByAccAppId(vnd, acc);
                      }
                      if (logger.isDebugEnabled()) {
                        logger.debug("Realm [{}] has application Acct [{}] Auth [{}] Vendor [{}]",
                            new Object[]{name, appId.getAcctAppId(), appId.getAuthAppId(), appId.getVendorId()});
                      }
                      break;
                    }
                  }
                }
              }
              String[] hosts = c.getStringValue(RealmHosts.ordinal(), (String) RealmHosts.defValue()).split(",");
              logger.debug("Adding realm [{}] with hosts [{}] to network map", name, hosts);
              LocalAction locAction = LocalAction.valueOf(c.getStringValue(RealmLocalAction.ordinal(), "0"));
              boolean isDynamic = c.getBooleanValue(RealmEntryIsDynamic.ordinal(), false);
              long expirationTime = c.getLongValue(RealmEntryExpTime.ordinal(), 0);
              //check if there is Agent, ATM we support only props there.
              IAgentConfiguration agentConfImpl = null;
              Configuration[] confs = c.getChildren(Agent.ordinal());
              if (confs != null && confs.length > 0) {
                Configuration agentConfiguration = confs[0]; //only one!
                agentConfImpl = this.container.getAssemblerFacility().getComponentInstance(IAgentConfiguration.class);

                if (agentConfImpl != null) {
                  agentConfImpl = agentConfImpl.parse(agentConfiguration);
                }
              }
              this.realmTable.addRealm(name, appId, locAction, agentConfImpl, isDynamic, expirationTime, hosts);
            }
            catch (Exception e) {
              logger.warn("Unable to append realm entry", e);
            }
          }
        }
      }
    }
  }

  @Override
  public void registerRequestRouteInfo(IRequest request) {
    logger.debug("Entering registerRequestRouteInfo");
    if (REQUEST_TABLE_SIZE == 0) {
      return; // we don't have anything to do as we are storing routing info at answer message
    }

    try {
      // PCB removed lock
      // requestEntryTableLock.writeLock().lock();
      long hopByHopId = request.getHopByHopIdentifier();
      Avp hostAvp = request.getAvps().getAvp(Avp.ORIGIN_HOST);
      // we store the peer FQDN instead of Origin-Host as we want to route back to it, in case of proxied requests this
      // should be the FQDN of the proxy, otherwise it's (should be) the same as Origin-Host
      String host = ((IMessage)request).getPeer() != null ? ((IMessage)request).getPeer().getUri().getFQDN() : hostAvp.getDiameterIdentity();
      Avp realmAvp = request.getAvps().getAvp(Avp.ORIGIN_REALM);

      AnswerEntry entry;
      AvpSet rrAvps = request.getAvps().getAvps(Avp.ROUTE_RECORD);
      if (rrAvps.size() > 0) {
        logger.debug("Found [{}] Route-Record AVP(s) in Request with HbH [{}], storing them for copying and routing.",
            rrAvps.size(), request.getHopByHopIdentifier());
        ArrayList<String> rrStrings = new ArrayList<String>();
        for(Avp rrAvp : rrAvps) {
          String rrAvpHost = rrAvp.getDiameterIdentity();
          logger.trace("Route-Record in Request with HbH [{}]: [{}]", request.getHopByHopIdentifier(), rrAvpHost);
          rrStrings.add(rrAvpHost);
        }
        entry = new AnswerEntry(hopByHopId, host, realmAvp != null ? realmAvp.getDiameterIdentity() : null, rrStrings);
      }
      else {
        entry = new AnswerEntry(hopByHopId, host, realmAvp != null ? realmAvp.getDiameterIdentity() : null);
      }

      int s = requestEntryMap.size();
      // PCB added logging
      logger.debug("RequestRoute map size is [{}]", s);

      //PCB added
      if (s > REQUEST_TABLE_CLEAR_SIZE) {
        try {
          requestEntryTableLock.lock();
          s = requestEntryMap.size();
          logger.debug("After 'lock', RequestRoute map size is [{}]", s);
          // The double-check with a gap is in case while about to clear the map, it drops below REQUEST_TABLE_SIZE due to a
          // response being sent, and then the lock might not be in effect for another thread
          // Hence lock at REQUEST_TABLE_CLEAR_SIZE and clear at REQUEST_TABLE_SIZE so that all threads would be locked not
          // just the first to reach REQUEST_TABLE_SIZE
          if (s > REQUEST_TABLE_SIZE) {
            // Going to clear it out and suffer the consequences of some messages possibly not being routed back the clients..
            logger.warn("RequestRoute map size is [{}]. There's probably a leak. Cleaning up after a short wait...", s);
            // Lets do our best to avoid lost messages by locking table from writes (as this is the only code writing to it),
            // sleeping for a while for responses to be sent and then clearing it
            Thread.sleep(5000);
            logger.warn("RequestRoute map size is now [{}] after sleeping. Clearing it!", requestEntryMap.size());
            requestEntryMap.clear();
          }
        }
        catch (Exception e) {
          logger.warn("Failure trying to clear RequestRoute map", e);
        }
        finally {
          requestEntryTableLock.unlock();
        }
      }

      String messageKey = makeRoutingKey(request);
      logger.debug("Adding request key [{}] to RequestRoute map with entry [{}] for routing answers back to the requesting peer", messageKey, entry);
      requestEntryMap.put(messageKey, entry);
      // requestSortedEntryTable.add(hopByHopId);
    }
    catch (Exception e) {
      logger.warn("Unable to store route info", e);
    }
    finally {
      // requestEntryTableLock.writeLock().unlock();
    }
  }

  // PCB - Made better routing algorithm that should not grow all the time
  private String makeRoutingKey(Message message) {
    String sessionId = message.getSessionId();
    return new StringBuilder(sessionId != null ? sessionId : "null").append(message.getEndToEndIdentifier())
        .append(message.getHopByHopIdentifier()).toString();
  }

  private String[] getRequestRouteInfoAndCopyProxyAvps(IMessage message, boolean copy) {
    if (REQUEST_TABLE_SIZE == 0) {
      return ((MessageImpl) message).getRoutingInfo(); // using answer stored routing info
      // TODO: Handle copy Proxy AVPs in this case...
    }

    // using request table
    String messageKey = makeRoutingKey(message);
    AnswerEntry ans = requestEntryMap.get(messageKey);
    if (ans != null) {
      if (logger.isDebugEnabled()) {
        logger.debug("getRequestRouteInfo found host [{}] and realm [{}] for Message key Id [{}]", new Object[]{ans.getHost(), ans.getRealm(), messageKey});
      }
      if (ans.getRouteRecords() != null && ans.getRouteRecords().size() > 0) {
        AvpSet msgRouteRecords = message.getAvps().getAvps(Avp.ROUTE_RECORD);
        if (msgRouteRecords.size() > 0) {
          logger.debug("We had Route-Records to insert but the message already has some... not doing anything");
        }
        else {
          for (String rr : ans.getRouteRecords()) {
            message.getAvps().addAvp(Avp.ROUTE_RECORD, rr, true);
          }
        }
      }
      return new String[] { ans.getHost(), ans.getRealm() };
    }
    else {
      if (logger.isWarnEnabled()) {
        logger.warn("Could not find route info for message key [{}]. Table size is [{}]", messageKey, requestEntryMap.size());
      }
      return null;
    }
  }

  @Override
  public String[] getRequestRouteInfo(IMessage message) {
    return getRequestRouteInfoAndCopyProxyAvps(message, false);
  }

  //PCB added
  @Override
  public void garbageCollectRequestRouteInfo(IMessage message) {
    if (REQUEST_TABLE_SIZE == 0) {
      return; // we don't have anything to do as we are storing routing info at answer message
    }

    String messageKey = makeRoutingKey(message);
    requestEntryMap.remove(messageKey);
  }

  @Override
  public IPeer getPeer(IMessage message, IPeerTable manager) throws RouteException, AvpDataException {
    logger.debug("Getting a peer for message [{}]", message);
    //FIXME: add ability to send without matching realm+peer pair?, that is , route based on peer table entries?
    //that is, if msg.destHost != null > getPeer(msg.destHost).sendMessage(msg);
    String destRealm = null;
    String destHost = null;
    IRealm matchedRealm = null;
    String[] info = null;
    // Get destination information
    if (message.isRequest()) {
      Avp avpRealm = message.getAvps().getAvp(Avp.DESTINATION_REALM);
      if (avpRealm == null) {
        throw new RouteException("Destination realm avp is empty");
      }
      destRealm = avpRealm.getDiameterIdentity();

      Avp avpHost = message.getAvps().getAvp(Avp.DESTINATION_HOST);
      if (avpHost != null) {
        destHost = avpHost.getDiameterIdentity();
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Looking up peer for request: [{}], DestHost=[{}], DestRealm=[{}]", new Object[] {message, destHost, destRealm});
      }

      matchedRealm = (IRealm) this.realmTable.matchRealm(message);
    }
    else {
      //answer, search
      info = getRequestRouteInfoAndCopyProxyAvps(message, true);

      if (info != null) {
        destHost = info[0];
        destRealm = info[1];
        logger.debug("Message is an answer. Host is [{}] and Realm is [{}] as per hopbyhop info from request", destHost, destRealm);
        if (destRealm == null) {
          logger.warn("Destination-Realm was null for hopbyhop id " + message.getHopByHopIdentifier());
        }
      }
      else {
        logger.debug("No Host and realm found based on hopbyhop id of the answer associated request");
      }
      //FIXME: if no info, should not send it ?
      //FIXME: add strict deff in route back table so stack does not have to lookup?
      if (logger.isDebugEnabled()) {
        logger.debug("Looking up peer for answer: [{}], DestHost=[{}], DestRealm=[{}]", new Object[] {message, destHost, destRealm});
      }
      matchedRealm = (IRealm) this.realmTable.matchRealm((IAnswer) message, destRealm);
    }

    //  IPeer peer = getPeerPredProcessing(message, destRealm, destHost);
    //
    //  if (peer != null) {
    //    logger.debug("Found during preprocessing...[{}]", peer);
    //    return peer;
    //  }

    // Check realm name
    if (matchedRealm == null) {
      if (message.getAvps().getAvp(Avp.ROUTE_RECORD) == null) {
        // if it doesn't come through a proxy, we fail with unknown realm...
        throw new RouteException("Unknown realm name [" + destRealm + "]");
      }
      else {
        logger.debug("Realm [{}] not found, but message has Route-Record AVP so it came from proxy peer [{}]. Proceeding...", destRealm, destHost);
      }
    }

    // THIS IS GET PEER, NOT ROUTE!!!!!!!
    // Redirect processing
    //redirectProcessing(message, destRealm, destHost);
    // Check previous context information, this takes care of most answers.
    if (message.getPeer() != null && destHost != null && destHost.equals(message.getPeer().getUri().getFQDN()) && message.getPeer().hasValidConnection()) {
      if (logger.isDebugEnabled()) {
        logger.debug("Select previous message usage peer [{}]", message.getPeer());
      }
      return message.getPeer();
    }

    // Balancing procedure

    IPeer c = destHost != null ? manager.getPeer(destHost) : null;

    if (c != null && c.hasValidConnection()) {
      logger.debug("Found a peer using destination host avp [{}] peer is [{}] with a valid connection.", destHost, c);
      //here matchedRealm MAY
      return c;
    }
    else {
      logger.debug("Finding peer by destination host avp [host={}] did not find anything. Now going to try finding one by destination realm [{}]",
          destHost, destRealm);
      String[] peers = matchedRealm.getPeerNames();
      if (peers == null || peers.length == 0) {
        throw new RouteException("Unable to find context by route information [" + destRealm + " ," + destHost + "]");
      }

      // Collect peers
      ArrayList<IPeer> availablePeers = new ArrayList<IPeer>(5);
      logger.debug("Looping through peers in realm [{}]", destRealm);
      for (String peerName : peers) {
        IPeer localPeer = manager.getPeer(peerName);
        if (logger.isDebugEnabled()) {
          logger.debug("Checking peer [{}] for name [{}]", new Object[]{localPeer, peerName});
        }
        // ammendonca: added peer state check.. should not be needed but
        // hasValidConnection is returning true for disconnected peers in *FTFlowTests
        if (localPeer != null && localPeer.getState(PeerState.class) == PeerState.OKAY) {
          if (localPeer.hasValidConnection()) {
            if (logger.isDebugEnabled()) {
              logger.debug("Found available peer to add to available peer list with uri [{}] with a valid connection", localPeer.getUri().toString());
            }
            availablePeers.add(localPeer);
          }
          else {
            if (logger.isDebugEnabled()) {
              logger.debug("Found a peer with uri [{}] with no valid connection", localPeer.getUri());
            }
          }
        }
      }

      if (logger.isDebugEnabled()) {
        logger.debug("Performing Realm routing. Realm [{}] has the following peers available [{}] from list [{}]",
            new Object[] {destRealm, availablePeers, Arrays.asList(peers)});
      }

      // Balancing
      IPeer peer = selectPeer(availablePeers);
      if (peer == null) {
        throw new RouteException("Unable to find valid connection to peer[" + destHost + "] in realm[" + destRealm + "]");
      }
      else {
        if (logger.isDebugEnabled()) {
          logger.debug("Load balancing selected peer with uri [{}]", peer.getUri());
        }
      }

      return peer;
    }
  }

  @Override
  public IRealmTable getRealmTable() {
    return this.realmTable;
  }

  @Override
  public void processRedirectAnswer(IRequest request, IAnswer answer, IPeerTable table) throws InternalException, RouteException {
    try {
      Avp destinationRealmAvp = request.getAvps().getAvp(Avp.DESTINATION_REALM);
      if (destinationRealmAvp == null) {
        throw new RouteException("Request to be routed has no Destination-Realm AVP!"); // sanity check... if user messes with us
      }
      String destinationRealm = destinationRealmAvp.getDiameterIdentity();
      String[] redirectHosts = null;
      if (answer.getAvps().getAvps(Avp.REDIRECT_HOST) != null) {
        AvpSet avps = answer.getAvps().getAvps(Avp.REDIRECT_HOST);
        redirectHosts = new String[avps.size()];
        int i = 0;
        // loop detected
        for (Avp avp : avps) {
          String r =  avp.getDiameterIdentity();
          if (r.equals(metaData.getLocalPeer().getUri().getFQDN())) {
            throw new RouteException("Loop detected");
          }
          redirectHosts[i++] = r;
        }
      }
      //
      int redirectUsage = DONT_CACHE;
      Avp redirectHostUsageAvp = answer.getAvps().getAvp(Avp.REDIRECT_HOST_USAGE);
      if (redirectHostUsageAvp != null) {
        redirectUsage = redirectHostUsageAvp.getInteger32();
      }

      if (redirectUsage != DONT_CACHE) {
        long redirectCacheTime = 0;
        Avp redirectCacheMaxTimeAvp = answer.getAvps().getAvp(Avp.REDIRECT_MAX_CACHE_TIME);
        if (redirectCacheMaxTimeAvp != null) {
          redirectCacheTime = redirectCacheMaxTimeAvp.getUnsigned32();
        }
        String primaryKey = null;
        ApplicationId secondaryKey = null;
        switch (redirectUsage) {
          case ALL_SESSION:
            primaryKey = request.getSessionId();
            break;
          case ALL_REALM:
            primaryKey = destinationRealm;
            break;
          case REALM_AND_APPLICATION:
            primaryKey = destinationRealm;
            secondaryKey = ((IMessage) request).getSingleApplicationId();
            break;
          case ALL_APPLICATION:
            secondaryKey = ((IMessage) request).getSingleApplicationId();
            break;
          case ALL_HOST:
            Avp destinationHostAvp = request.getAvps().getAvp(Avp.DESTINATION_HOST);
            if (destinationHostAvp == null) {
              throw new RouteException("Request to be routed has no Destination-Host AVP!"); // sanity check... if user messes with us
            }
            primaryKey = destinationHostAvp.getDiameterIdentity();
            break;
          case ALL_USER:
            Avp userNameAvp = answer.getAvps().getAvp(Avp.USER_NAME);
            if (userNameAvp == null) {
              throw new RouteException("Request to be routed has no User-Name AVP!"); // sanity check... if user messes with us
            }
            primaryKey = userNameAvp.getUTF8String();
            break;
        }
        //
        if (redirectTable.size() > REDIRECT_TABLE_SIZE) {
          try {
            //yes, possible that this will trigger this procedure twice, but thats worst than locking always.
            redirectTableLock.writeLock().lock();
            trimRedirectTable();
          }
          finally {
            redirectTableLock.writeLock().unlock();
          }
        }
        if (REDIRECT_TABLE_SIZE > redirectTable.size()) {
          RedirectEntry e = new RedirectEntry(primaryKey, secondaryKey, redirectCacheTime, redirectUsage, redirectHosts, destinationRealm);
          redirectTable.add(e);
          //redirectProcessing(answer, destRealm.getOctetString(), destHost !=null ? destHost.getOctetString():null);
          //we dont have to elect?
          updateRoute(request, e.getRedirectHost());
        }
        else {
          if (redirectHosts != null && redirectHosts.length > 0) {
            String destHost = redirectHosts[0];
            //setRouteInfo(answer, getRealmForPeer(destHost), destHost);
            updateRoute(request, destHost);
          }
        }
      }
      else {
        if (redirectHosts != null && redirectHosts.length > 0) {
          String destHost = redirectHosts[0];
          //setRouteInfo(answer, getRealmForPeer(destHost), destHost);
          updateRoute(request, destHost);
        }
      }
      //now send
      table.sendMessage((IMessage) request);
    }
    catch (AvpDataException exc) {
      throw new InternalException(exc);
    }
    catch (IllegalDiameterStateException e) {
      throw new InternalException(e);
    }
    catch (IOException e) {
      throw new InternalException(e);
    }
  }

  /**
   *
   */
  private void trimRedirectTable() {
    for (int index = 0; index < redirectTable.size(); index++) {
      try {
        if (redirectTable.get(index).getExpiredTime() <= System.currentTimeMillis()) {
          redirectTable.remove(index);
          index--; //a trick :)
        }
      }
      catch (Exception e) {
        logger.debug("Error in redirect task cleanup.", e);
        break;
      }
    }
  }

  /**
   * @param request
   * @param destHost
   */
  private void updateRoute(IRequest request, String destHost) {
    // Realm does not change I think... :)
    request.getAvps().removeAvp(Avp.DESTINATION_HOST);
    request.getAvps().addAvp(Avp.DESTINATION_HOST, destHost, true, false,  true);
  }

  @Override
  public boolean updateRoute(IRequest message) throws RouteException, AvpDataException {
    AvpSet set = message.getAvps();
    Avp destRealmAvp = set.getAvp(Avp.DESTINATION_REALM);
    Avp destHostAvp = set.getAvp(Avp.DESTINATION_HOST);

    if (destRealmAvp == null) {
      throw new RouteException("Request does not have Destination-Realm AVP!");
    }

    String destRealm = destRealmAvp.getDiameterIdentity();
    String destHost = destHostAvp != null ? destHostAvp.getDiameterIdentity() : null;

    boolean matchedEntry = false;
    String userName = null;
    // get Session id
    String sessionId = message.getSessionId();
    //
    Avp avpUserName = message.getAvps().getAvp(Avp.USER_NAME);
    // Get application id
    ApplicationId appId = ((IMessage) message).getSingleApplicationId();
    // User name
    if (avpUserName != null) {
      userName = avpUserName.getUTF8String();
    }
    // Processing table
    try {
      redirectTableLock.readLock().lock();

      for (int index = 0; index < redirectTable.size(); index++) {
        RedirectEntry e = redirectTable.get(index);
        switch (e.getUsageType()) {
          case ALL_SESSION: // Usage type: ALL SESSION
            matchedEntry = sessionId != null && e.primaryKey != null & sessionId.equals(e.primaryKey);
            break;
          case ALL_REALM: // Usage type: ALL REALM
            matchedEntry = destRealm != null && e.primaryKey != null & destRealm.equals(e.primaryKey);
            break;
          case REALM_AND_APPLICATION: // Usage type: REALM AND APPLICATION
            matchedEntry = destRealm != null & appId != null & e.primaryKey != null & e.secondaryKey != null & destRealm.equals(e.primaryKey)
              & appId.equals(e.secondaryKey);
            break;
          case ALL_APPLICATION: // Usage type: ALL APPLICATION
            matchedEntry = appId != null & e.secondaryKey != null & appId.equals(e.secondaryKey);
            break;
          case ALL_HOST: // Usage type: ALL HOST
            matchedEntry = destHost != null & e.primaryKey != null & destHost.equals(e.primaryKey);
            break;
          case ALL_USER: // Usage type: ALL USER
            matchedEntry = userName != null & e.primaryKey != null & userName.equals(e.primaryKey);
            break;
        }
        // Update message redirect information
        if (matchedEntry) {
          String newDestHost = e.getRedirectHost();
          //String newDestRealm = getRealmForPeer(destHost);
          //setRouteInfo(message, destRealm, newDestHost);
          updateRoute(message, newDestHost);
          logger.debug("Redirect message from host={}; to new-host={}, realm={} ", new Object[] { destHost, newDestHost, destRealm});
          return true;
        }
      }
    }
    finally {
      redirectTableLock.readLock().unlock();
    }
    return false;
  }

  protected IPeer getPeerPredProcessing(IMessage message, String destRealm, String destHost) {
    return null;
  }

  @Override
  public void start() {
    if (isStopped) {
      //redirectScheduler = concurrentFactory.getScheduledExecutorService(RedirectMessageTimer.name());
      //redirectEntryHandler = redirectScheduler.scheduleAtFixedRate(redirectTask, 1, 1, TimeUnit.SECONDS);
      isStopped = false;
    }
  }

  @Override
  public void stop() {
    isStopped = true;
    // if (redirectEntryHandler != null) {
    //  redirectEntryHandler.cancel(true);
    //}
    if (redirectTable != null) {
      redirectTable.clear();
    }
    if (requestEntryMap != null) {
      requestEntryMap.clear();
    }
    //PCB removed
    //if (requestSortedEntryTable != null) {
    //  requestSortedEntryTable.clear();
    //}
    //if (redirectScheduler != null) {
    //  concurrentFactory.shutdownNow(redirectScheduler);
    //}
  }

  @Override
  public void destroy() {
    try {
      if (!isStopped) {
        stop();
      }
    }
    catch (Exception exc) {
      logger.error("Unable to stop router", exc);
    }

    //redirectEntryHandler = null;
    //redirectScheduler = null;
    redirectTable = null;
    requestEntryMap = null;
  }

  protected IPeer selectPeer(List<IPeer> availablePeers) {
    IPeer p = null;
    for (IPeer c : availablePeers) {
      if (p == null || c.getRating() >= p.getRating()) {
        p = c;
      }
    }
    return p;
  }

  //    protected void redirectProcessing(IMessage message, final String destRealm, final String destHost) throws AvpDataException {
  //        String userName = null;
  //        // get Session id
  //        String sessionId = message.getSessionId();
  //        //
  //        Avp avpUserName = message.getAvps().getAvp(Avp.USER_NAME);
  //        // Get application id
  //        ApplicationId appId = message.getSingleApplicationId();
  //        // User name
  //        if (avpUserName != null)
  //            userName = avpUserName.getUTF8String();
  //        // Processing table
  //        for (RedirectEntry e : redirectTable.values()) {
  //            boolean matchedEntry = false;
  //            switch (e.getUsageType()) {
  //                case ALL_SESSION: // Usage type: ALL SESSION
  //                  matchedEntry = sessionId != null && e.primaryKey != null &
  //                  sessionId.equals(e.primaryKey);
  //                  break;
  //                case ALL_REALM: // Usage type: ALL REALM
  //                  matchedEntry = destRealm != null && e.primaryKey != null &
  //                  destRealm.equals(e.primaryKey);
  //                  break;
  //                case REALM_AND_APPLICATION: // Usage type: REALM AND APPLICATION
  //                  matchedEntry = destRealm != null & appId != null & e.primaryKey != null & e.secondaryKey != null &
  //                  destRealm.equals(e.primaryKey) & appId.equals(e.secondaryKey);
  //                  break;
  //                case ALL_APPLICATION: // Usage type: ALL APPLICATION
  //                  matchedEntry = appId != null & e.secondaryKey != null &
  //                  appId.equals(e.secondaryKey);
  //                  break;
  //                case ALL_HOST: // Usage type: ALL HOST
  //                  matchedEntry = destHost != null & e.primaryKey != null &
  //                  destHost.equals(e.primaryKey);
  //                  break;
  //                case ALL_USER: // Usage type: ALL USER
  //                  matchedEntry = userName != null & e.primaryKey != null &
  //                  userName.equals(e.primaryKey);
  //                  break;
  //            }
  //            // Update message redirect information
  //            if (matchedEntry) {
  //              String newDestHost  = e.getRedirectHost();
  //              // FIXME: Alexandre: Should use newDestHost?
  //              String newDestRealm = getRealmForPeer(destHost);
  //              setRouteInfo(message, destRealm, newDestHost);
  //              logger.debug("Redirect message from host={}; realm={} to new-host={}; new-realm={}",
  //              new Object[] {destHost, destRealm, newDestHost, newDestRealm});
  //              return;
  //            }
  //        }
  //    }
  //
  //    private void setRouteInfo(IMessage message, String destRealm, String destHost) {
  //        message.getAvps().removeAvp(Avp.DESTINATION_REALM);
  //        message.getAvps().removeAvp(Avp.DESTINATION_HOST);
  //        if (destRealm != null)
  //            message.getAvps().addAvp(Avp.DESTINATION_REALM, destRealm, true, false, true);
  //        if (destHost != null)
  //            message.getAvps().addAvp(Avp.DESTINATION_HOST, destHost, true, false,  true);
  //    }
  //does not make sense, there can be multple realms :/
  //    public String  getRealmForPeer(String destHost) {
  //        for (String key : getRealmsName()) {
  //            for (String h : getRealmPeers(key)) {
  //                if (h.trim().equals(destHost.trim()))
  //                    return key;
  //            }
  //        }
  //        return null;
  //    }

  protected class RedirectEntry {

    final long createTime = System.currentTimeMillis();

    String primaryKey;
    ApplicationId secondaryKey;
    long liveTime;
    int usageType;
    String[] hosts;
    String destinationRealm;

    public RedirectEntry(String key1, ApplicationId key2, long time, int usage, String[] aHosts, String destinationRealm) throws InternalError {
      // Check arguments
      if (key1 == null && key2 == null) {
        throw new InternalError("Incorrect redirection key.");
      }
      if (aHosts == null || aHosts.length == 0) {
        throw new InternalError("Incorrect redirection hosts.");
      }
      // Set values
      this.primaryKey = key1;
      this.secondaryKey = key2;
      this.liveTime = time * 1000;
      this.usageType = usage;
      this.hosts = aHosts;
      this.destinationRealm = destinationRealm;
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

    @Override
    public int hashCode() {
      int result = (primaryKey != null ? primaryKey.hashCode() : 0);
      result = 31 * result + (secondaryKey != null ? secondaryKey.hashCode() : 0);
      result = 31 * result + (int) (liveTime ^ (liveTime >>> 32));
      result = 31 * result + usageType;
      result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
      return result;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) {
        return true;
      }

      if (other instanceof RedirectEntry) {
        RedirectEntry that = (RedirectEntry) other;
        return liveTime == that.liveTime && usageType == that.usageType &&
            Arrays.equals(hosts, that.hosts) && !(primaryKey != null ? !primaryKey.equals(that.primaryKey) : that.primaryKey != null) &&
            !(secondaryKey != null ? !secondaryKey.equals(that.secondaryKey) : that.secondaryKey != null);
      }
      else {
        return false;
      }
    }
  }

  protected class AnswerEntry {

    final long createTime = System.nanoTime();
    Long hopByHopId;
    String host, realm;
    ArrayList<String> routeRecords;

    public AnswerEntry(Long hopByHopId) {
      this.hopByHopId = hopByHopId;
    }

    public AnswerEntry(Long hopByHopId, String host, String realm) throws InternalError {
      this.hopByHopId = hopByHopId;
      this.host = host;
      this.realm = realm;
    }

    public AnswerEntry(Long hopByHopId, String host, String realm, ArrayList<String> routeRecords) throws InternalError {
      this.hopByHopId = hopByHopId;
      this.host = host;
      this.realm = realm;
      this.routeRecords = routeRecords;
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

    public ArrayList<String> getRouteRecords() {
      return routeRecords;
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      AnswerEntry that = (AnswerEntry) o;
      return hopByHopId == that.hopByHopId;
    }

    @Override
    public String toString() {
      return "AnswerEntry {" + "createTime=" + createTime + ", hopByHopId=" + hopByHopId + ", host=" + host + ", realm=" + realm+ "}";
    }
  }
}
