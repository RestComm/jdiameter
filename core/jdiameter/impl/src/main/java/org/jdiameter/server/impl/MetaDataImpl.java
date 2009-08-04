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
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.impl.helpers.IPConverter;
import org.jdiameter.server.api.IMetaData;
import org.jdiameter.server.api.IMutablePeerTable;
import org.jdiameter.server.api.INetwork;

public class MetaDataImpl extends org.jdiameter.client.impl.MetaDataImpl implements IMetaData {

  private final Object lock = new Object();

  public MetaDataImpl(IContainer s) {
    super(s);
  }

  public LocalPeer getLocalPeer() {
    return new MyLocalPeer();        
  }

  public StackType getStackType() {
    return StackType.TYPE_SERVER;
  }

  public void addApplicationId(ApplicationId applicationId) {
    synchronized (lock) {
      if (appIds.contains(applicationId)) {
        return;
      }
      appIds.add(applicationId);
    }
  }

  public void remApplicationId(ApplicationId applicationId) {
    synchronized (lock) {
      appIds.remove(applicationId);
    }
  }

  public void reload() {
    // Reload common application ids from configuration
    synchronized (lock) {
      appIds.clear();
      getLocalPeerInfo().getCommonApplications();
      // Reload ip addresses from configuration
      peer.resetAddresses();
      peer.getIPAddresses();
    }
  }

  protected class MyLocalPeer extends LocalPeer {

    protected INetwork net = null;
    protected IMutablePeerTable manager = null;
    protected ISessionFactory factory = null;
    protected Map<String, NetworkReqListener> slc = null;
    Map<Long, IMessage> peerRequests = new ConcurrentHashMap<Long, IMessage>();

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
          logger.debug("Can not get ip by address {}", address, e);
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
        return InetAddress.getByName( getLocalPeer().getUri().getFQDN() );
      }
      catch (Exception e1) {
        logger.debug("Can not get ip by uri", e1);
        try {
          return InetAddress.getLocalHost();
        }
        catch (Exception e2) {
          logger.debug("Can not get ip localhost", e2);
        }
      }
      return null;
    }

    // Local processing message
    public boolean sendMessage(IMessage message) throws TransportException, OverloadException {
      try {
        if (net == null || manager == null) {
          try {
            net = (INetwork) stack.unwrap(Network.class);
            manager = (IMutablePeerTable) stack.unwrap(PeerTable.class);
            factory = manager.getSessionFactory();
            slc = manager.getSessionReqListeners();
          }
          catch (Exception e) {
            logger.debug("Error initialising for message send", e);
          }
        }

        IMessage answer = null;
        if (message.isRequest()) {
          message.setHopByHopIdentifier( peer.getHopByHopIdentifier() );
          peerRequests.put(message.getHopByHopIdentifier(), message);
          NetworkReqListener listener = net.getListener(message);
          if (listener != null) {
            // This is duplicate code from PeerImpl
            answer = manager.isDuplicate(message);
            if (answer != null) {
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
                NetworkReqListener sessionListener = slc.get(avpSessionId);
                if (sessionListener != null) {
                  answer = (IMessage) sessionListener.processRequest(message);
                }
                else {
                  try {
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
            logger.debug("Can not find handler {} for message {}", message.getSingleApplicationId(), message);
          }
          if (answer != null) {
            peerRequests.remove( message.getHopByHopIdentifier() );
          }
        }
        else {
          answer = message;
          message = peerRequests.get(answer.getHopByHopIdentifier());
        }
        // Process answer
        if ( message != null && !message.isTimeOut() && answer != null ) {
          message.clearTimer();
          message.setState(IMessage.STATE_ANSWERED);
          message.getEventListener().receivedSuccessMessage(message, answer);
        }
        return true;
      }
      catch (Exception e) {
        logger.debug("Can not processed message {}", message, e);
      }
      return false;
    }
  }
}
