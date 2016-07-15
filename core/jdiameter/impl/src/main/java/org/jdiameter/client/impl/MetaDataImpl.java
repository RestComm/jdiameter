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

package org.jdiameter.client.impl;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;
import static org.jdiameter.client.impl.helpers.Parameters.OwnFirmwareRevision;
import static org.jdiameter.client.impl.helpers.Parameters.OwnIPAddress;
import static org.jdiameter.client.impl.helpers.Parameters.OwnProductName;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.OwnVendorID;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerStateListener;
import org.jdiameter.api.StackType;
import org.jdiameter.api.URI;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.fsm.EventTypes;
import org.jdiameter.client.api.io.IConnectionListener;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.impl.helpers.IPConverter;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.common.impl.controller.AbstractPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use stack extension point
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MetaDataImpl implements IMetaData {

  private static final Logger logger = LoggerFactory.getLogger(MetaDataImpl.class);
  protected List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();

  protected IContainer stack;
  protected long state;
  protected IPeer peer;
  protected Set<ApplicationId> appIds = new LinkedHashSet<ApplicationId>();
  protected final ISessionDatasource sessionDataSource;

  public MetaDataImpl(IContainer s) {
    this.stack = s;
    this.sessionDataSource = s.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  public MetaDataImpl(IContainer s, IStatisticManager statisticFactory) {
    this(s);
    this.peer = newLocalPeer(statisticFactory);

    IStatisticRecord heapMemory = statisticFactory.newCounterRecord(IStatisticRecord.Counters.HeapMemory,
        new IStatisticRecord.LongValueHolder() {
          @Override
          public long getValueAsLong() {
            for (MemoryPoolMXBean bean : beans) {
              MemoryType memoryType = bean.getType();
              MemoryUsage memoryUsage = bean.getUsage();
              if (memoryType == MemoryType.HEAP) {
                return memoryUsage.getUsed();
              }
            }
            return 0;
          }

          @Override
          public String getValueAsString() {
            return String.valueOf(getValueAsLong());
          }
        });

    IStatisticRecord noHeapMemory = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NoHeapMemory,
        new IStatisticRecord.LongValueHolder() {
          @Override
          public long getValueAsLong() {
            for (MemoryPoolMXBean bean : beans) {
              MemoryType memoryType = bean.getType();
              MemoryUsage memoryUsage = bean.getUsage();
              if (memoryType != MemoryType.HEAP) {
                return memoryUsage.getUsed();
              }
            }
            return 0;
          }

          @Override
          public String getValueAsString() {
            return String.valueOf(getValueAsLong());
          }
        });
    peer.getStatistic().appendCounter(heapMemory, noHeapMemory);
  }

  protected IPeer newLocalPeer(IStatisticManager statisticFactory) {
    return new ClientLocalPeer(statisticFactory);
  }

  @Override
  public Peer getLocalPeer() {
    return peer;
  }

  @Override
  public int getMajorVersion() {
    return 2;
  }

  @Override
  public int getMinorVersion() {
    return 1;
  }

  @Override
  public StackType getStackType() {
    return StackType.TYPE_CLIENT;
  }

  @Override
  public Configuration getConfiguration() {
    return stack.getConfiguration();
  }

  @Override
  public void updateLocalHostStateId() {
    state = System.currentTimeMillis();
  }

  @Override
  public long getLocalHostStateId() {
    return state;
  }

  @Override
  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    return aClass == IMetaData.class;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> aClass) throws InternalException {
    if (aClass == IMetaData.class) {
      return (T) this;
    }

    return null;
  }

  protected class ClientLocalPeer extends AbstractPeer implements IPeer {

    protected AtomicLong hopByHopId = new AtomicLong(0);
    protected InetAddress[] addresses = new InetAddress[0];

    public void resetAddresses() {
      addresses = new InetAddress[0];
    }

    @Override
    public void connect() throws IllegalDiameterStateException {
      throw new IllegalDiameterStateException("Illegal operation");
    }

    @Override
    public void disconnect(int disconnectCause) throws IllegalDiameterStateException {
      throw new IllegalDiameterStateException("Illegal operation");
    }

    public ClientLocalPeer(IStatisticManager statisticFactory) {
      //FIXME: remove NULL?
      super(null, statisticFactory);
      createPeerStatistics();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getState(Class<E> anEnum) {
      switch (stack.getState()) {
        case IDLE:
          return (E) PeerState.DOWN;
        case CONFIGURED:
          return (E) PeerState.INITIAL;
        case STARTED:
          return (E) PeerState.OKAY;
        case STOPPED:
          return (E) PeerState.SUSPECT;
      }
      return (E) PeerState.DOWN;
    }

    @Override
    public URI getUri() {
      try {
        return new URI(stack.getConfiguration().getStringValue(OwnDiameterURI.ordinal(), (String) OwnDiameterURI.defValue()));
      }
      catch (URISyntaxException e) {
        throw new IllegalArgumentException(e);
      }
      catch (UnknownServiceException e) {
        throw new IllegalArgumentException(e);
      }
    }

    @Override
    public String getRealmName() {
      return stack.getConfiguration().getStringValue(OwnRealm.ordinal(), (String) OwnRealm.defValue());
    }

    @Override
    public long getVendorId() {
      return stack.getConfiguration().getLongValue(OwnVendorID.ordinal(), (Long) OwnVendorID.defValue());
    }

    @Override
    public String getProductName() {
      return stack.getConfiguration().getStringValue(OwnProductName.ordinal(), (String) OwnProductName.defValue());
    }

    @Override
    public long getFirmware() {
      return stack.getConfiguration().getLongValue(OwnFirmwareRevision.ordinal(), -1L);
    }

    @Override
    public Set<ApplicationId> getCommonApplications() {
      if (logger.isDebugEnabled()) {
        logger.debug("In getCommonApplications appIds size is [{}]", appIds.size());
      }
      if (appIds.isEmpty()) {
        Configuration[] apps = stack.getConfiguration().getChildren(ApplicationId.ordinal());
        if (apps != null) {
          if (logger.isDebugEnabled()) {
            logger.debug("Stack configuration has apps list size of  [{}]. Looping through them", apps.length);
          }
          for (Configuration a : apps) {
            long vnd = a.getLongValue(VendorId.ordinal(), 0L);
            long auth = a.getLongValue(AuthApplId.ordinal(), 0L);
            long acc = a.getLongValue(AcctApplId.ordinal(), 0L);
            if (logger.isDebugEnabled()) {
              logger.debug("Adding app id vendor [{}] auth [{}] acc [{}]", new Object[]{vnd, auth, acc});
            }
            if (auth != 0) {
              appIds.add(org.jdiameter.api.ApplicationId.createByAuthAppId(vnd, auth));
            }
            if (acc != 0) {
              appIds.add(org.jdiameter.api.ApplicationId.createByAccAppId(vnd, acc));
            }
          }
        }
        else {
          logger.debug("Apps is null - we have no apps in the stack configuration.");
        }
      }
      return appIds;
    }

    @Override
    public InetAddress[] getIPAddresses() {
      if (addresses.length == 0) {
        String address = stack.getConfiguration().getStringValue(OwnIPAddress.ordinal(), null);
        if (address == null || address.length() == 0) {
          try {
            addresses = new InetAddress[]{InetAddress.getByName(getUri().getFQDN())};
          }
          catch (UnknownHostException e) {
            logger.debug("Can not get IP by URI {}", e);
            try {
              addresses = new InetAddress[]{InetAddress.getLocalHost()};
            }
            catch (UnknownHostException e1) {
              addresses = new InetAddress[0];
            }
          }
        }
        else {
          InetAddress ia = IPConverter.InetAddressByIPv4(address);
          if (ia == null) {
            ia = IPConverter.InetAddressByIPv6(address);
          }
          if (ia == null) {
            try {
              addresses = new InetAddress[]{InetAddress.getLocalHost()};
            }
            catch (UnknownHostException e) {
              addresses = new InetAddress[0];
            }
          }
          else {
            addresses = new InetAddress[]{ia};
          }
        }
      }
      return addresses;
    }

    @Override
    public IStatistic getStatistic() {
      return statistic;
    }

    @Override
    public String toString() {
      return "Peer{" +
          "\n\tUri=" + getUri() + "; RealmName=" + getRealmName() + "; VendorId=" + getVendorId() +
          ";\n\tProductName=" + getProductName() + "; FirmWare=" + getFirmware() +
          ";\n\tAppIds=" + getCommonApplications() +
          ";\n\tIPAddresses=" + Arrays.asList(getIPAddresses()).toString() + ";" + "\n}";
    }

    @Override
    public int getRating() {
      return 0;
    }

    @Override
    public void addPeerStateListener(PeerStateListener peerStateListener) {
    }

    @Override
    public void removePeerStateListener(PeerStateListener peerStateListener) {
    }

    @Override
    public long getHopByHopIdentifier() {
      return hopByHopId.incrementAndGet();
    }

    @Override
    public void addMessage(IMessage message) {
    }

    @Override
    public void remMessage(IMessage message) {
    }

    @Override
    public IMessage[] remAllMessage() {
      return new IMessage[0];
    }

    @Override
    public boolean handleMessage(EventTypes type, IMessage message, String key) throws TransportException, OverloadException, InternalException {
      return false;
    }

    @Override
    public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
      return false;
    }

    @Override
    public boolean hasValidConnection() {
      return false;
    }

    @Override
    public void setRealm(String realm) {
    }

    @Override
    public void addStateChangeListener(StateChangeListener listener) {
    }

    @Override
    public void remStateChangeListener(StateChangeListener listener) {
    }

    @Override
    public void addConnectionListener(IConnectionListener listener) {
    }

    @Override
    public void remConnectionListener(IConnectionListener listener) {
    }

    /* (non-Javadoc)
     * @see org.jdiameter.client.api.controller.IPeer#isConnected()
     */
    @Override
    public boolean isConnected() {
      return true; // it's own peer
    }
  }
}
