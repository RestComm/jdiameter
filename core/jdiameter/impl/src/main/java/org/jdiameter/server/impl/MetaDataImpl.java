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

import static org.jdiameter.client.impl.helpers.Parameters.OwnIPAddress;
import static org.jdiameter.server.impl.helpers.Parameters.OwnIPAddresses;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.StackType;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.impl.helpers.IPConverter;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MetaDataImpl extends org.jdiameter.client.impl.MetaDataImpl implements IMetaData {

  private static final Logger logger = LoggerFactory.getLogger(MetaDataImpl.class);

  private final Object lock = new Object();

  public MetaDataImpl(IContainer s) {
    super(s);
  }

  public MetaDataImpl(IContainer s, IStatisticManager factory) {
    super(s, factory);
  }

  @Override
  public StackType getStackType() {
    return StackType.TYPE_SERVER;
  }

  @Override
  protected IPeer newLocalPeer(IStatisticManager statisticFactory) {
    return new ServerLocalPeer(statisticFactory);
  }

  @Override
  public void addApplicationId(ApplicationId applicationId) {
    synchronized (lock) {
      if (appIds.contains(applicationId)) {
        return;
      }
      appIds.add(applicationId);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Adding application id of auth [{}] acct [{}] vendor [{}]",
          new Object[]{applicationId.getAuthAppId(), applicationId.getAcctAppId(), applicationId.getVendorId()});
    }
  }

  @Override
  public void remApplicationId(ApplicationId applicationId) {
    synchronized (lock) {
      appIds.remove(applicationId);
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Removing application id of auth [{}] acct [{}] vendor [{}]",
          new Object[]{applicationId.getAuthAppId(), applicationId.getAcctAppId(), applicationId.getVendorId()});
    }
  }

  @Override
  public void reload() {
    // Reload common application ids from configuration
    synchronized (lock) {
      appIds.clear();
      logger.debug("Clearing out application ids");
      getLocalPeer().getCommonApplications();
      // Reload ip addresses from configuration
      ((ServerLocalPeer) peer).resetAddresses();
      peer.getIPAddresses();
    }
  }

  protected class ServerLocalPeer extends ClientLocalPeer {

    protected INetwork net = null;
    protected IMutablePeerTable manager = null;
    protected ISessionFactory factory = null;
    // XXX: FT/HA // protected Map<String, NetworkReqListener> slc = null;
    Map<Long, IMessage> peerRequests = new ConcurrentHashMap<Long, IMessage>();

    public ServerLocalPeer(IStatisticManager statisticFactory) {
      super(statisticFactory);
    }

    @Override
    public Set<ApplicationId> getCommonApplications() {
      Set<ApplicationId> set;
      synchronized (lock) {
        set = super.getCommonApplications();
      }
      return  set;
    }

    @Override
    public InetAddress[] getIPAddresses() {
      if (addresses.length == 0) {
        Configuration[] ipAddresses = stack.getConfiguration().getChildren(OwnIPAddresses.ordinal());
        List<InetAddress> list = new ArrayList<InetAddress>();
        if (ipAddresses != null) {
          for (Configuration address : ipAddresses) {
            if (address != null) {
              InetAddress iaddress = getAddress(address);
              if (iaddress != null) {
                list.add(iaddress);
              }
            }
          }
        }
        else {
          InetAddress address = getDefaultIpAddress();
          if (address != null) {
            list.add(address);
          }
        }
        addresses = list.toArray(new InetAddress[list.size()]);
      }
      return addresses;
    }

    private InetAddress getAddress(Configuration configuration) {
      InetAddress rc;
      String address = configuration.getStringValue(OwnIPAddress.ordinal(), null);
      if (address == null || address.length() == 0) {
        rc = getDefaultIpAddress();
      }
      else {
        try {
          rc = InetAddress.getByName(address);
        }
        catch (UnknownHostException e) {
          logger.debug("Unable to retrieve IP by Address [{}]", address, e);
          rc = IPConverter.InetAddressByIPv4(address);
          if (rc == null) {
            rc = IPConverter.InetAddressByIPv6(address);
          }
          if (rc == null) {
            rc = getDefaultIpAddress();
          }
        }
      }
      return rc;
    }

    private InetAddress getDefaultIpAddress() {
      try {
        return InetAddress.getByName(getLocalPeer().getUri().getFQDN());
      }
      catch (Exception e1) {
        logger.debug("Unable to retrieve IP by URI [{}]", getLocalPeer().getUri().getFQDN(), e1);
        try {
          return InetAddress.getLocalHost();
        }
        catch (Exception e2) {
          logger.debug("Unable to retrieve IP for localhost", e2);
        }
      }
      return null;
    }

    // Local processing message
    @Override
    @SuppressWarnings("unchecked")
    public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
      logger.debug("Sending Message in Server Local Peer");
      try {
        if (net == null || manager == null) {
          try {
            logger.debug("Unwrapping network and manager");
            net = (INetwork) stack.unwrap(Network.class);
            manager = (IMutablePeerTable) stack.unwrap(PeerTable.class);
            factory = manager.getSessionFactory();
            // XXX: FT/HA // slc = manager.getSessionReqListeners();
          }
          catch (Exception e) {
            logger.warn("Error initialising for message send", e);
          }
        }

        IMessage answer = null;
        if (message.isRequest()) {
          logger.debug("Message is a request");
          message.setHopByHopIdentifier(peer.getHopByHopIdentifier());
          peerRequests.put(message.getHopByHopIdentifier(), message);
          NetworkReqListener listener = net.getListener(message);
          if (listener != null) {
            // This is duplicate code from PeerImpl
            answer = manager.isDuplicate(message);
            if (answer != null) {
              logger.debug("Found message in duplicates. No need to invoke listener, will send previous answer.");
              answer.setProxiable(message.isProxiable());
              answer.getAvps().removeAvp(Avp.PROXY_INFO);
              for (Avp avp : message.getAvps().getAvps(Avp.PROXY_INFO)) {
                answer.getAvps().addAvp(avp);
              }
              answer.setHopByHopIdentifier(message.getHopByHopIdentifier());
            }
            else {
              String avpSessionId = message.getSessionId();
              if (avpSessionId != null) {
                // XXX: FT/HA // NetworkReqListener sessionListener = slc.get(avpSessionId);
                NetworkReqListener sessionListener = sessionDataSource.getSessionListener(avpSessionId);
                if (sessionListener != null) {
                  logger.debug("Giving message to sessionListener to process as Session-Id AVP existed in message "
                      + "and was used to get a session from Session DataSource");
                  answer = (IMessage) sessionListener.processRequest(message);
                }
                else {
                  try {
                    logger.debug("Giving message to listener to process. Listener was retrieved from net");
                    answer = (IMessage) listener.processRequest(message);
                    if (answer != null) {
                      manager.saveToDuplicate(message.getDuplicationKey(), answer);
                    }
                  }
                  catch (Exception e) {
                    logger.debug("Error during processing message by listener", e);
                  }
                }
              }
            }
          }
          else {
            if (logger.isDebugEnabled()) {
              logger.debug("Unable to find handler {} for message {}", message.getSingleApplicationId(), message);
            }
          }
          if (answer != null) {
            if (logger.isDebugEnabled()) {
              logger.debug("Removing message with HbH Identifier [{}] from Peer Requests Map", message.getHopByHopIdentifier());
            }
            peerRequests.remove(message.getHopByHopIdentifier());
          }
        }
        else {
          if (logger.isDebugEnabled()) {
            logger.debug("Message is an answer. Setting answer to the message and fetching message from Peer Requests Map using HbH Identifier [{}]",
                message.getHopByHopIdentifier());
          }
          answer = message;
          message = peerRequests.get(answer.getHopByHopIdentifier());
        }
        // Process answer
        if (message != null && !message.isTimeOut() && answer != null) {
          logger.debug("Clearing timer on message and notifying event listeners of receiving success message");
          message.clearTimer();
          message.setState(IMessage.STATE_ANSWERED);
          message.getEventListener().receivedSuccessMessage(message, answer);
        }
        return true;
      }
      catch (Exception e) {
        logger.warn("Unable to process message {}", message, e);
      }
      return false;
    }
  }
}
