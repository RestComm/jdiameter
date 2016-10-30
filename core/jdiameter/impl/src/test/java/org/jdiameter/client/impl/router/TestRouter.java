/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
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
 */

package org.jdiameter.client.impl.router;

import org.jdiameter.api.*;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IAnswer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.impl.helpers.XMLConfiguration;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.common.impl.controller.AbstractPeer;
import org.jdiameter.common.impl.statistic.StatisticManagerImpl;
import org.jdiameter.server.api.agent.IAgentConfiguration;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Various testcases for Router implementations
 *
 * @author <a href="mailto:n.sowen@2scale.net">Nils Sowen</a>
 */
public class TestRouter {

    @Test
    public void testWeightedRoundRobin() throws Exception {

        Configuration config = new XMLConfiguration("src/test/resources/jdiameter-weightedroundrobin-config.xml");
        WeightedRoundRobinRouter router = new WeightedRoundRobinRouter(new RealmTableTest(), config);

        IStatisticManager manager = new StatisticManagerImpl(config);
        PeerTest p1 = new PeerTest(1, 1, true, manager);
        PeerTest p2 = new PeerTest(2, 1, true, manager);
        PeerTest p3 = new PeerTest(3, 1, true, manager);
        PeerTest p4 = new PeerTest(4, 1, true, manager);

        List<IPeer> peers = new ArrayList<IPeer>(3);
        peers.add(p1);
        peers.add(p2);
        peers.add(p3);

        // Test simple round robin (all weight = 1)
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());

        // Test weighted round robin (p1=2, p2=1, p3=1)
        p1.setRating(2);
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());

        // Test weighted round robin (p1=2, p2=2, p3=1)
        p2.setRating(2);
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());

        // Test equally weighted round robin (p1=2, p2=2, p3=2)
        p3.setRating(2);
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());

        // Add Peer-4 with weight 1 to list
        peers.add(p4);
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        // expected glitch here: due to the sudden availibity of Peer-4, the algorithm is disturbed
        assertEquals(p4.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p4.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());
        assertEquals(p4.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p3.toString(), router.selectPeer(peers).toString());

        // Next cycle would produce Peer-4, but reduce peer list now
        peers = peers.subList(0,2); // now: Peer-1, Peer-2
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

    }

    @Test
    public void testWeightedLeastConnections() throws Exception {

        Configuration config = new XMLConfiguration("src/test/resources/jdiameter-weightedleastconnections-config.xml");
        WeightedLeastConnectionsRouter router = new WeightedLeastConnectionsRouter(new RealmTableTest(), config);

        IStatisticManager manager = new StatisticManagerImpl(config);
        PeerTest p1 = new PeerTest(1, 1, true, manager);
        PeerTest p2 = new PeerTest(2, 1, true, manager);
        PeerTest p3 = new PeerTest(3, 1, true, manager);

        List<IPeer> peers = new ArrayList<IPeer>(2);
        peers.add(p1);
        peers.add(p2);

        // Test simple round robin (all weight = 1)
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

        // increase p1 requests/s by 1
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());

        // increase p2 requests/s by 1
        p2.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+p2.getUri()).inc();
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

        // decrease p1 requests/s by 1
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenResponsePerSecond.name()+'.'+p1.getUri()).dec();
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

        // increase p1 requests/s by 3
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());

        // increase p2 requests/s by 1
        p2.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+p2.getUri()).inc();
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());

        // increase weight of p1
        p1.setRating(2);
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

        // decrease p1 requests/s by 1
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).dec();
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

        // increase p1 requests/s by 2
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        p1.getStatistic().getRecordByName(IStatisticRecord.Counters.AppGenRequestPerSecond.name()+'.'+p1.getUri()).inc();
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());

        // increase weight and requests/s of p2
        p2.setRating(2);
        p2.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+p2.getUri()).inc();
        p2.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+p2.getUri()).inc();
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());
        assertEquals(p2.toString(), router.selectPeer(peers).toString());

        // increase p2 requests/s by 1
        p2.getStatistic().getRecordByName(IStatisticRecord.Counters.NetGenRequestPerSecond.name()+'.'+p2.getUri()).inc();
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());
        assertEquals(p1.toString(), router.selectPeer(peers).toString());

    }

    private static class RealmTableTest implements IRealmTable {

        public Realm matchRealm(IRequest request) {
            return null;
        }

        public Realm matchRealm(IAnswer message, String destRealm) {
            return null;
        }

        public Realm getRealm(String realmName, ApplicationId applicationId) {
            return null;
        }

        public Realm removeRealmApplicationId(String realmName, ApplicationId appId) {
            return null;
        }

        public Collection<Realm> removeRealm(String realmName) {
            return null;
        }

        public Collection<Realm> getRealms(String realm) {
            return null;
        }

        public Collection<Realm> getRealms() {
            return null;
        }

        public String getRealmForPeer(String fqdn) {
            return null;
        }

        public void addLocalApplicationId(ApplicationId ap) {

        }

        public void removeLocalApplicationId(ApplicationId a) {

        }

        public void addLocalRealm(String localRealm, String fqdn) {

        }

        public Realm addRealm(String name, ApplicationId appId, LocalAction locAction, IAgentConfiguration agentConfImpl, boolean isDynamic, long expirationTime, String[] hosts) throws InternalException {
            return null;
        }

        public Statistic getStatistic(String realmName) {
            return null;
        }

        public Realm addRealm(String realmName, ApplicationId applicationId, LocalAction action, String agentConfiguration, boolean dynamic, long expirationTime, String[] hosts) throws InternalException {
            return null;
        }

        public boolean realmExists(String realmName) {
            return false;
        }

        public boolean isWrapperFor(Class<?> iface) throws InternalException {
            return false;
        }

        public <T> T unwrap(Class<T> iface) throws InternalException {
            return null;
        }
        
        public List<String> getAllRealmSet(){
          return null;
        }
    }

    private static class PeerTest extends AbstractPeer implements IPeer {

        private int id;
        private int rating;
        private boolean connected;

        public PeerTest(int id, int rating, boolean connected, IStatisticManager manager) throws URISyntaxException, UnknownServiceException {
            super(new URI("aaa://"+id), manager);
            this.id = id;
            this.rating = rating;
            this.connected = connected;
            createPeerStatistics();
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public int getRating() {
            return rating;
        }

        public long getHopByHopIdentifier() {
            return 0;
        }

        public void addMessage(IMessage message) {

        }

        public void remMessage(IMessage message) {

        }

        public IMessage[] remAllMessage() {
            return new IMessage[0];
        }

        public boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException {
            return false;
        }

        public boolean sendMessage(IMessage message) throws TransportException, OverloadException, InternalException {
            return false;
        }

        public boolean hasValidConnection() {
            return connected;
        }

        public void setRealm(String realm) {

        }

        public void addStateChangeListener(StateChangeListener listener) {

        }

        public void remStateChangeListener(StateChangeListener listener) {

        }

        public void addConnectionListener(IConnectionListener listener) {

        }

        public void remConnectionListener(IConnectionListener listener) {

        }

        public IStatistic getStatistic() {
            return statistic;
        }

        public boolean isConnected() {
            return connected;
        }

        public void connect() throws InternalException, IOException, IllegalDiameterStateException {

        }

        @Override
        public void disconnect(int disconnectCause) throws InternalException, IllegalDiameterStateException {

        }

        public <E> E getState(Class<E> enumc) {
            return null;
        }

        public URI getUri() {
            return uri;
        }

        public InetAddress[] getIPAddresses() {
            return new InetAddress[0];
        }

        public String getRealmName() {
            return null;
        }

        public long getVendorId() {
            return 0;
        }

        public String getProductName() {
            return null;
        }

        public long getFirmware() {
            return 0;
        }

        public Set<ApplicationId> getCommonApplications() {
            return null;
        }

        public void addPeerStateListener(PeerStateListener listener) {

        }

        public void removePeerStateListener(PeerStateListener listener) {

        }

        @Override
        public String toString() {
            return "Peer-"+id;
        }
    }

}