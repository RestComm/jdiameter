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

import static org.jdiameter.client.impl.helpers.Parameters.PeerIp;
import static org.jdiameter.client.impl.helpers.Parameters.PeerLocalPortRange;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.SecurityRef;
import static org.jdiameter.server.impl.helpers.Parameters.PeerAttemptConnection;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.MutablePeerTable;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Stack;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.server.impl.MutablePeerTableImpl;
import org.jdiameter.server.impl.PeerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DiameterConfiguration implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(DiameterConfiguration.class);

  protected static Stack stack;

  public DiameterConfiguration(Stack stack) {
    DiameterConfiguration.stack = stack;
    updateFromStack(stack);
  }

  // Mandatory and max-occurs = 1
  private LocalPeer localPeer = new LocalPeerImpl();

  private Parameters parameters = null;

  private Network network = new NetworkImpl();

  // Optional
  //<xsi:element ref="Security" minOccurs="0" maxOccurs="1"/>
  //<xsi:element ref="Extensions" minOccurs="0" maxOccurs="1"/>

  private void updateFromStack(Stack stack) {
    long startTime = System.currentTimeMillis();

    // Update LocalPeer
    Peer sLocalPeer = stack.getMetaData().getLocalPeer();

    localPeer.setUri(sLocalPeer.getUri().toString());
    for (InetAddress ipAddress : sLocalPeer.getIPAddresses()) {
      localPeer.addIpAddress(ipAddress.getHostAddress());
    }
    localPeer.setRealm(sLocalPeer.getRealmName());
    localPeer.setVendorId(sLocalPeer.getVendorId());
    localPeer.setProductName(sLocalPeer.getProductName());
    localPeer.setFirmwareRev(sLocalPeer.getFirmware());
    for (org.jdiameter.api.ApplicationId appId : sLocalPeer.getCommonApplications()) {
      if (appId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE) {
        localPeer.addDefaultApplication(ApplicationIdJMX.createAuthApplicationId(appId.getVendorId(), appId.getAuthAppId()));
      }
      else {
        localPeer.addDefaultApplication(ApplicationIdJMX.createAcctApplicationId(appId.getVendorId(), appId.getAcctAppId()));
      }
    }
    HashMap<String, DiameterStatistic> lpStats = new HashMap<String, DiameterStatistic>();
    for (StatisticRecord stat : ((IPeer) sLocalPeer).getStatistic().getRecords()) {
      lpStats.put(stat.getName(), new DiameterStatistic(stat.getName(), stat.getDescription(), stat.toString()));
    }
    localPeer.setStatistics(lpStats);

    MutableConfiguration config = (MutableConfiguration) stack.getMetaData().getConfiguration();

    // Update Parameters
    this.parameters = new ParametersImpl(config);

    // Update Network ...
    // ... Peers (config)
    for (Configuration curPeer : config.getChildren(PeerTable.ordinal())) {
      String name = curPeer.getStringValue(PeerName.ordinal(), "");
      Boolean attemptConnect = curPeer.getBooleanValue(PeerAttemptConnection.ordinal(), false);
      Integer rating = curPeer.getIntValue(PeerRating.ordinal(), 0);
      String ip = curPeer.getStringValue(PeerIp.ordinal(), null);
      String portRange = curPeer.getStringValue(PeerLocalPortRange.ordinal(), "");
      Integer portRangeLow = null;
      Integer portRangeHigh = null;
      if (portRange != null && !portRange.equals("")) {
        String[] rng = portRange.trim().split("-");
        portRangeLow = Integer.parseInt(rng[0]);
        portRangeHigh = Integer.parseInt(rng[1]);
      }
      String securityRef = curPeer.getStringValue(SecurityRef.ordinal(), "");
      network.addPeer(new NetworkPeerImpl(name, attemptConnect, rating, ip, portRangeLow, portRangeHigh, securityRef));
    }

    // ... More Peers (mutable)
    try {
      MutablePeerTable peerTable;
      peerTable = stack.unwrap(MutablePeerTable.class);
      //Peer p = n.addPeer("aaa://127.0.0.1:13868", "mobicents.org", true);
      for (Peer peer : peerTable.getPeerTable()) {
        PeerImpl p = (PeerImpl) peer;
        NetworkPeerImpl nPeer = new NetworkPeerImpl(p.getUri().toString(), p.isAttemptConnection(), p.getRating(), null, null, null, null);
        HashMap<String, DiameterStatistic> npStats = new HashMap<String, DiameterStatistic>();
        for (StatisticRecord stat : p.getStatistic().getRecords()) {
          npStats.put(stat.getName(), new DiameterStatistic(stat.getName(), stat.getDescription(), stat.toString()));
        }
        nPeer.setStatistics(npStats);
        network.addPeer(nPeer);
      }
    }
    catch (InternalException e) {
      logger.error("Failed to update Diameter Configuration from Stack Mutable Peer Table", e);
    }

    // ... Realms (configuration)
    /*for(Configuration realmTable : config.getChildren(RealmTable.ordinal())) {
      for(Configuration curRealm : realmTable.getChildren(RealmEntry.ordinal())) {
        String name = curRealm.getStringValue(RealmName.ordinal(), "");
        String hosts = curRealm.getStringValue(RealmHosts.ordinal(), "localhost");
        ArrayList<String> peers = new ArrayList<String>();
        for(String peer : hosts.split(",")) {
          peers.add(peer.trim());
        }
        String localAction = curRealm.getStringValue(RealmLocalAction.ordinal(), "LOCAL");
        Boolean dynamic = curRealm.getBooleanValue(RealmEntryIsDynamic.ordinal(), false);
        Long expTime = curRealm.getLongValue(RealmEntryExpTime.ordinal(), 0);
        Configuration[] sAppIds = curRealm.getChildren(ApplicationId.ordinal());
        ArrayList<ApplicationIdJMX> appIds = new ArrayList<ApplicationIdJMX>();
        for(Configuration appId : sAppIds) {
          Long acctAppId = appId.getLongValue(AcctApplId.ordinal(), 0);
          Long authAppId = appId.getLongValue(AuthApplId.ordinal(), 0);
          Long vendorId = appId.getLongValue(VendorId.ordinal(), 0);
          if(authAppId != 0) {
            appIds.add(ApplicationIdJMX.createAuthApplicationId(vendorId, authAppId));
          }
          else if (acctAppId != 0){
            appIds.add(ApplicationIdJMX.createAcctApplicationId(vendorId, acctAppId));
          }
        }
        network.addRealm(new RealmImpl(appIds, name, peers, localAction, dynamic, expTime));
      }
    }
     */
    // ... Realms (mutable)
    try {
      MutablePeerTableImpl mpt = (MutablePeerTableImpl) stack.unwrap(PeerTable.class);
      for (org.jdiameter.api.Realm realm : mpt.getAllRealms()) {
        IRealm irealm = null;
        if (realm instanceof IRealm) {
          irealm = (IRealm) realm;
        }
        ArrayList<ApplicationIdJMX> x = new ArrayList<ApplicationIdJMX>();
        x.add(ApplicationIdJMX.fromApplicationId(realm.getApplicationId()));
        network.addRealm(new RealmImpl(x, realm.getName(), new ArrayList<String>(Arrays.asList(((IRealm) realm).getPeerNames())),
            realm.getLocalAction().toString(), irealm != null ? irealm.getAgentConfiguration() : null, realm.isDynamic(), realm.getExpirationTime()));
      }
    }
    catch (Exception e) {
      logger.error("Failed to update Diameter Configuration from Stack Mutable Peer Table", e);
    }

    long endTime = System.currentTimeMillis();

    logger.debug("Info gathered in {}ms", (endTime - startTime));
  }

  public LocalPeer getLocalPeer() {
    return localPeer;
  }

  public Parameters getParameters() {
    return parameters;
  }

  public Network getNetwork() {
    return network;
  }

  protected static MutableConfiguration getMutableConfiguration() {
    return (MutableConfiguration) stack.getMetaData().getConfiguration();
  }

  @Override
  public String toString() {
    String toString = "## LOCAL PEER ##\r\n" + localPeer.toString() + "\r\n";
    toString += "## PARAMETERS ##\r\n" + parameters.toString() + "\r\n";
    toString += "## NETWORK ##\r\n" + network.toString() + "\r\n";
    return toString;
  }

}
