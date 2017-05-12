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

package org.jdiameter.client.impl.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Realm;
import org.jdiameter.api.Statistic;
import org.jdiameter.client.api.IAnswer;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.server.api.agent.IAgent;
import org.jdiameter.server.api.agent.IAgentConfiguration;
import org.jdiameter.server.api.agent.IProxy;
import org.jdiameter.server.api.agent.IRedirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RealmTableImpl implements IRealmTable {

  private static final Logger logger = LoggerFactory.getLogger(RealmTableImpl.class);

  // maps name->realms (cause there might be more than one realm defined, with different app id.
  protected Map<String, RealmSet> realmNameToRealmSet = new HashMap<String, RealmSet>();

  // "cache" so we don't have to combine all realms
  protected List<String> allRealmsSet = new ArrayList<String>();

  protected String localRealmName;
  protected String localHost;

  protected IAssembler assembler;

  public RealmTableImpl(IContainer con) {
    this.assembler = con.getAssemblerFacility();
  }

  @Override
  public boolean realmExists(String realmName) {
    // NOTE: this is still valid for local realm
    return (this.realmNameToRealmSet.containsKey(realmName) &&  this.realmNameToRealmSet.get(realmName).size() > 0);
  }

  @Override
  public Realm addRealm(String realmName, ApplicationId applicationId, LocalAction action, String agentConfiguration, boolean dynamic, long expirationTime,
      String[] hosts) throws InternalException {
    logger.debug("Adding realm [{}] into network map", realmName);

    IAgentConfiguration agentConf = this.assembler.getComponentInstance(IAgentConfiguration.class);

    if (agentConf != null) {
      agentConf = agentConf.parse(agentConfiguration);
    }

    return addRealm(realmName, applicationId, action, agentConf, dynamic, expirationTime, hosts);
  }

  @Override
  public Realm addRealm(String realmName, ApplicationId applicationId, LocalAction action, IAgentConfiguration agentConf, boolean dynamic, long expirationTime,
      String[] hosts) throws InternalException {
    IAgent agent = null;
    switch (action) {
      case LOCAL:
      case RELAY:
        break;
      case PROXY:
        agent = this.assembler.getComponentInstance(IProxy.class);
        break;
      case REDIRECT:
        agent = this.assembler.getComponentInstance(IRedirect.class);
        break;
    }

    RealmImpl realmImpl = new RealmImpl(realmName, applicationId, action, agent, agentConf, dynamic, expirationTime, hosts);
    addRealm(realmImpl);

    return realmImpl;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#getRealm(java.lang.String, org.jdiameter.api.ApplicationId)
   */
  @Override
  public Realm getRealm(String realmName, ApplicationId applicationId) {
    RealmSet rs = this.realmNameToRealmSet.get(realmName);
    return rs == null ? null : rs.getRealm(applicationId);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#removeRealmApplicationId(java.lang.String, org.jdiameter.api.ApplicationId)
   */
  @Override
  public Realm removeRealmApplicationId(String realmName, ApplicationId appId) {
    RealmSet set = this.realmNameToRealmSet.get(realmName);

    if (set != null) {
      Realm r = set.getRealm(appId);
      set.removeRealm(appId);
      if (set.size() == 0 && !realmName.equals(this.localRealmName)) {
        this.realmNameToRealmSet.remove(realmName);
        this.allRealmsSet.remove(realmName);
      }
      return r;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#removeRealms(java.lang.String)
   */
  @Override
  public Collection<Realm> removeRealm(String realmName) {
    RealmSet set = null;
    if (realmName.equals(this.localRealmName)) {
      set = this.realmNameToRealmSet.get(realmName);
    }
    else {
      set = this.realmNameToRealmSet.remove(realmName);
      if (set != null) {
        Collection<Realm> present = set.values();
        allRealmsSet.remove(realmName);
        return new ArrayList<Realm>(present);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#getRealms(java.lang.String)
   */
  @Override
  public Collection<Realm> getRealms(String realmName) {
    RealmSet set = this.realmNameToRealmSet.get(realmName);
    if (set != null) {
      Collection<Realm> present = set.values();
      return new ArrayList<Realm>(present);
    }
    return new ArrayList<Realm>(0);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#getRealms()
   */
  @Override
  public Collection<Realm> getRealms() {
    ArrayList<Realm> rss = new ArrayList<Realm>();

    Set<String> keys = new HashSet<String>(this.realmNameToRealmSet.keySet());
    for (String key : keys) {
      RealmSet rs = this.realmNameToRealmSet.get(key);
      rss.addAll(rs.values());
    }
    return rss;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#matchRealm(org.jdiameter.client.api.IRequest)
   */
  @Override
  public Realm matchRealm(IRequest request) {
    try {
      // once again casting...
      IMessage req = (IMessage) request;
      String destinationRealm = req.getAvps().getAvp(Avp.DESTINATION_REALM).getDiameterIdentity();
      // we have req, we need match, not dummy longest from right BS match.
      return this.matchRealm(req, destinationRealm);
    }
    catch (Exception e) {
      logger.error("Unable to read Destination-Realm AVP to match realm to request", e);
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#matchRealm(org.jdiameter.client.api.IAnswer, java.lang.String)
   */
  @Override
  public Realm matchRealm(IAnswer message, String destRealm) {
    return this.matchRealm((IMessage) message, destRealm);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.client.api.controller.IRealmTable#getRealmForPeer(java.lang.String)
   */
  @Override
  public String getRealmForPeer(String fqdn) {
    //
    Collection<Realm> realms = getRealms();
    for (Realm r : realms) {
      IRealm ir = (IRealm) r;
      if (ir.hasPeerName(fqdn)) {
        return ir.getName();
      }
    }
    return null;
  }

  /**
   * @param appId
   */
  @Override
  public void addLocalApplicationId(ApplicationId appId) {
    RealmSet rs = getRealmSet(localRealmName, false);
    rs.addRealm(new RealmImpl(localRealmName, appId, LocalAction.LOCAL, null, null, true, -1, this.localHost) {
      @Override
      public boolean isLocal() {
        return true;
      }
    });
  }

  /**
   * @param appId
   */
  @Override
  public void removeLocalApplicationId(ApplicationId appId) {
    RealmSet rs = getRealmSet(localRealmName, false);
    Realm realm = rs.getRealm(appId);
    if (realm.isDynamic()) {
      rs.removeRealm(appId);
    }
  }

  /**
   * @param localRealm
   * @param fqdn
   */
  @Override
  public void addLocalRealm(String localRealm, String fqdn) {
    this.localRealmName = localRealm;
    this.localHost = fqdn;
    getRealmSet(localRealm, true /* adds realm if not present  */);
  }

  // -------------------- helper methods --------------------

  protected Realm matchRealm(IMessage message, String realm) {
    if (realmExists(realm)) {
      ApplicationId singleId = message.getSingleApplicationId();
      // check on single app id, than we iterate.
      Realm r = getRealm(realm, singleId);
      if (r == null) {
        List<ApplicationId> appIds = message.getApplicationIdAvps();
        for (int index = 0; index < appIds.size(); index++) {
          r = getRealm(realm, appIds.get(index));
          if (r != null) {
            break;
          }
        }
      }

      return r;
    }
    return null;
  }

  protected void addRealm(Realm realm) throws InternalException {
    RealmSet rs = getRealmSet(realm.getName(), true);
    rs.addRealm(realm);
    allRealmsSet.add(realm.getName());
  }

  protected RealmSet getRealmSet(String pKey, boolean create) {
    RealmSet rs = realmNameToRealmSet.get(pKey);
    if (rs == null && create) {
      rs = new RealmSet();
      realmNameToRealmSet.put(pKey, rs);
    }
    return rs;
  }

  private class RealmSet {

    // TODO: use two lists and iterate over index?
    protected Map<ApplicationId, Realm> appIdToRealm = new HashMap<ApplicationId, Realm>();

    /**
     * @param realm
     * @throws InternalException
     */
    public void addRealm(Realm realm) {
      if (this.appIdToRealm.containsKey(realm.getApplicationId())) {
        Realm presentRealm = this.appIdToRealm.get(realm.getApplicationId());

        if (realm.getName().equals(localRealmName)) {
          // we need to merge - its a local realm, possibly definition
          // of hosts that can handle requests for us.
          RealmImpl realmImpl = (RealmImpl) presentRealm;
          realmImpl.dynamic = false; // ensure its static
          for (String peerName : ((RealmImpl) realm).getPeerNames()) {
            realmImpl.addPeerName(peerName);
          }
        }
        else if (!presentRealm.isDynamic() && realm.isDynamic()) {
          // its a dynamic realm, present is static, we dont have to
          // do a thing?
        }
        else if (presentRealm.isDynamic() && !realm.isDynamic()) {
          // we need to merge?
          RealmImpl realmImpl = (RealmImpl) presentRealm;
          realmImpl.dynamic = false; // make it static :)
          for (String peerName : ((RealmImpl) realm).getPeerNames()) {
            realmImpl.addPeerName(peerName);
          }
        }
        else {
          if (logger.isDebugEnabled()) {
            logger.debug("Entry for realm '{}', already exists: {}", realm, this);
          }
        }
      }
      else {
        this.appIdToRealm.put(realm.getApplicationId(), realm);
      }
    }

    /**
     * @return
     */
    public Collection<Realm> values() {
      return this.appIdToRealm.values();
    }

    /**
     * @return
     */
    public int size() {
      return this.appIdToRealm.size();
    }

    /**
     * @param appId
     * @return
     */
    public Realm getRealm(ApplicationId appId) {
      return this.appIdToRealm.get(appId);
    }

    /**
     * @param appId
     * @return
     */
    public Realm removeRealm(ApplicationId appId) {
      return this.appIdToRealm.remove(appId);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.RealmTable#getStatistic(java.lang.String)
   */
  @Override
  public Statistic getStatistic(String realmName) {
    // FIXME: ...
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.Wrapper#isWrapperFor(java.lang.Class)
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws InternalException {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.Wrapper#unwrap(java.lang.Class)
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws InternalException {
    return null;
  }

  @Override
  public List<String> getAllRealmSet() {
    return allRealmsSet;
  }

}
