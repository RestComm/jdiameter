/*
 * TeleStax, Open Source Cloud Communications
 *
 * Copyright 2011-2015, Telestax Inc. and individual contributors
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

package org.mobicents.diameter.stack;

import java.net.InetAddress;
import java.util.Set;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;
import org.mobicents.diameter.stack.management.DiameterConfiguration;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */

@javax.ejb.Remote
public interface DiameterStackMultiplexerAS7 {

  public static final String MBEAN_NAME_PREFIX = "diameter:Service=DiameterStackMultiplexer,Name=";

  public void registerListener(DiameterListener listener, ApplicationId[] appIds) throws IllegalStateException;

  public void unregisterListener(DiameterListener listener);

  //For sake of simplicity in the pre Gamma :)
  public Stack getStack();

  public DiameterProvider getProvider();

  public DiameterMessageFactory getMessageFactory();

  public DiameterStackMultiplexerAS7 getMultiplexerMBean();

  // MANAGEMENT OPERATIONS

  // Get a Serializable Configuration

  public DiameterConfiguration getDiameterConfiguration();

  // Local Peer ----------------------------------------------------------

  /**
   * Changes the URI of the Local Peer.
   *
   * @param uri the new URI to be used by the Local Peer
   */
  public void _LocalPeer_setURI(String uri);

  /**
   * Adds an IP Address to the Local Peer.
   * @param ipAddress the IP Address to be added, if not present
   */
  public void _LocalPeer_addIPAddress(String ipAddress);

  /**
   * Removes an IP Address from the Local Peer.
   *
   * @param ipAddress the IP Address to be removed, if present
   */
  public void _LocalPeer_removeIPAddress(String ipAddress);

  /**
   * Changes the Realm of the Local Peer.
   *
   * @param realm the new Realm to be used by the Local Peer
   */
  public void _LocalPeer_setRealm(String realm);

  /**
   * Sets the Local Peer Vendor-Id.
   *
   * @param vendorId the new Vendor-Id for the Peer
   */
  public void _LocalPeer_setVendorId(long vendorId);

  // Parameters ----------------------------------------------------------

  /**
   * Sets whether the stack will accept connections from unknown peers or not (default: true)
   *
   * @param acceptUndefinedPeer indicates if the stack will accept unknown connections
   */
  public void _Parameters_setAcceptUndefinedPeer(boolean acceptUndefinedPeer);

  /**
   * Sets whether the stack will use URI (aaa://IP_ADDRESS:PORT) as FQDN. Some Peers require it.
   *
   * @param useUriAsFqdn indicates if the stack will use URI as FQDN
   */
  public void _Parameters_setUseUriAsFqdn(boolean useUriAsFqdn);

  /**
   * Sets the value to consider a message as a duplicate, in ms. (default: 240000, 4 minutes).
   *
   * @param duplicateTimer the amount of time, in ms.
   */
  public void _Parameters_setDuplicateTimer(long duplicateTimer);

  // Parameters : Timeouts -----------------------------------------------

  /**
   * Sets the timeout for general Diameter messages, in ms. (default: 60000, 1 minute).
   *
   * @param messageTimeout the amount of time, in ms.
   */
  public void _Parameters_setMessageTimeout(long messageTimeout);

  /**
   * Sets the timeout for stopping the stack. (default: 10000, 10 seconds).
   *
   * @param stopTimeout the amount of time, in ms.
   */
  public void _Parameters_setStopTimeout(long stopTimeout);

  /**
   * Sets the timeout for CEA messages. (default: 10000, 10 seconds).
   *
   * @param ceaTimeout the amount of time, in ms.
   */
  public void _Parameters_setCeaTimeout(long ceaTimeout);

  /**
   * Sets the timeout for inactiveness. (default: 20000, 20 seconds).
   *
   * @param iacTimeout the amount of time, in ms.
   */
  public void _Parameters_setIacTimeout(long iacTimeout);

  /**
   * Sets the timeout for DWA messages. (default: 10000, 10 seconds).
   *
   * @param dwaTimeout the amount of time, in ms.
   */
  public void _Parameters_setDwaTimeout(long dwaTimeout);

  /**
   * Sets the timeout for DPA messages. (default: 5000, 5 seconds).
   *
   * @param dpaTimeout the amount of time, in ms.
   */
  public void _Parameters_setDpaTimeout(long dpaTimeout);

  /**
   * Sets the timeout for reconnecting. (default: 10000, 10 seconds).
   *
   * @param recTimeout the amount of time, in ms.
   */
  public void _Parameters_setRecTimeout(long recTimeout);

  public void _Parameters_setConcurrentEntity(String name, String desc, Integer size);

  public void _Parameters_setStatisticLoggerDelay(long delay);

  public void _Parameters_setStatisticLoggerPause(long pause);

  // Network : Peers -----------------------------------------------------

  /**
   * Adds a peer definition to the stack. Same as <peer/> element in XML Configuration.
   *
   * @param name the name/uri of the peer
   * @param attemptConnect indicates if the stack should try to connect to this peer or wait for incoming connection
   * @param rating the peer rating for decision on message routing
   */
  public void _Network_Peers_addPeer(String name, boolean attemptConnect, int rating);

  /**
   * Removes a peer definition from stack.
   *
   * @param name the name/uri of the peer
   */
  public void _Network_Peers_removePeer(String name);

  // Network : Realms ----------------------------------------------------

  /**
   * Adds a new Realm to the stack. Same as <realm/> element in XML Configuration.
   *
   * @param name the name of the Realm
   * @param peers the Realm peer hosts, separated by comma
   * @param appVendorId the vendor-id of the application supported by this realm
   * @param appAcctId the accounting-id of the application supported by this realm
   * @param appAuthId the authorization-id of the application supported by this realm
   */
  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId);

  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId, String localAction, String agentConfiguration, boolean isDynamic, int expTime);

  /**
   * Removes a Realm from the stack.
   *
   * @param name the name of the Realm
   */
  public void _Network_Realms_removeRealm(String name);

  /**
   * Adds a new Peer host to the Realm
   *
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be added
   * @param attemptConnecting either try or not to connect the peer (client/server)
   */
  public void _Network_Realms_addPeerToRealm(String realmName, String peerName, boolean attemptConnecting);

  /**
   * Removes a Peer host from the Realm
   *
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be removed
   */
  public void _Network_Realms_removePeerFromRealm(String realmName, String peerName);

  // Stack Operation -----------------------------------------------------

  /**
   * Operation to stop the stack.
   *
   */
  public void stopStack(int disconnectCause);

  /**
   * Operation to start the stack.
   *
   */
  public void startStack();

  // Validation ----------------------------------------------------------

  /**
   * Sets whether validation on Diameter messages/AVPs should be performed or not.
   *
   * @param enableValidation flag indicating if validation should be performed
   */
  public void _Validation_setEnabled(boolean enableValidation);

  // Configuration Dump --------------------------------------------------

  /**
   * Dumps full stack configuration.
   *
   * @return a String with stack configuration
   */
  public String dumpStackConfiguration();

  // Information dump methods --------------------------------------------

  public String _LocalPeer_getProductName();

  public Long _LocalPeer_getVendorId();

  public Long _LocalPeer_getFirmware();

  public String _LocalPeer_getURI();

  public String _LocalPeer_getRealmName();

  public InetAddress[] _LocalPeer_getIPAddresses();

  public Set<ApplicationId> _LocalPeer_getCommonApplicationIds();

  public String[] _Network_Realms_getRealms();

  public String[] _Network_Realms_getRealmPeers(String realmName);

  public boolean _LocalPeer_isActive();

  public boolean _Network_Peers_isPeerConnected(String name);

}
