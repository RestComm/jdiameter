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

package org.mobicents.diameter.stack;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

import javax.management.MBeanException;

import org.jboss.system.ServiceMBean;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Peer;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;
import org.mobicents.diameter.stack.management.DiameterConfiguration;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface DiameterStackMultiplexerMBean extends ServiceMBean {

  String MBEAN_NAME_PREFIX = "diameter:Service=DiameterStackMultiplexer,Name=";

  void registerListener(DiameterListener listener, ApplicationId[] appIds) throws IllegalStateException;

  void unregisterListener(DiameterListener listener);

  //For sake of simplicity in the pre Gamma :)
  Stack getStack();

  DiameterProvider getProvider();

  DiameterMessageFactory getMessageFactory();

  DiameterStackMultiplexerMBean getMultiplexerMBean();

  // MANAGEMENT OPERATIONS

  // Get a Serializable Configuration

  DiameterConfiguration getDiameterConfiguration() throws MBeanException;

  // Local Peer ----------------------------------------------------------

  /**
   * Changes the URI of the Local Peer.
   *
   * @param uri the new URI to be used by the Local Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _LocalPeer_setURI(String uri) throws MBeanException;

  /**
   * Adds an IP Address to the Local Peer.
   * @param ipAddress the IP Address to be added, if not present
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _LocalPeer_addIPAddress(String ipAddress) throws MBeanException;

  /**
   * Removes an IP Address from the Local Peer.
   *
   * @param ipAddress the IP Address to be removed, if present
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _LocalPeer_removeIPAddress(String ipAddress) throws MBeanException;

  /**
   * Changes the Realm of the Local Peer.
   *
   * @param realm the new Realm to be used by the Local Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _LocalPeer_setRealm(String realm) throws MBeanException;

  /**
   * Sets the Local Peer Vendor-Id.
   *
   * @param vendorId the new Vendor-Id for the Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _LocalPeer_setVendorId(long vendorId) throws MBeanException;

  // Parameters ----------------------------------------------------------

  /**
   * Sets whether the stack will accept connections from unknown peers or not (default: true)
   *
   * @param acceptUndefinedPeer indicates if the stack will accept unknown connections
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setAcceptUndefinedPeer(boolean acceptUndefinedPeer) throws MBeanException;

  /**
   * Sets whether the stack will use URI (aaa://IP_ADDRESS:PORT) as FQDN. Some Peers require it.
   *
   * @param useUriAsFqdn indicates if the stack will use URI as FQDN
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setUseUriAsFqdn(boolean useUriAsFqdn) throws MBeanException;

  /**
   * Sets the value to consider a message as a duplicate, in ms. (default: 240000, 4 minutes).
   *
   * @param duplicateTimer the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setDuplicateTimer(long duplicateTimer) throws MBeanException;

  // Parameters : Timeouts -----------------------------------------------

  /**
   * Sets the timeout for general Diameter messages, in ms. (default: 60000, 1 minute).
   *
   * @param messageTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setMessageTimeout(long messageTimeout) throws MBeanException;

  /**
   * Sets the timeout for stopping the stack. (default: 10000, 10 seconds).
   *
   * @param stopTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setStopTimeout(long stopTimeout) throws MBeanException;

  /**
   * Sets the timeout for CEA messages. (default: 10000, 10 seconds).
   *
   * @param ceaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setCeaTimeout(long ceaTimeout) throws MBeanException;

  /**
   * Sets the timeout for inactiveness. (default: 20000, 20 seconds).
   *
   * @param iacTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setIacTimeout(long iacTimeout) throws MBeanException;

  /**
   * Sets the timeout for DWA messages. (default: 10000, 10 seconds).
   *
   * @param dwaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setDwaTimeout(long dwaTimeout) throws MBeanException;

  /**
   * Sets the timeout for DPA messages. (default: 5000, 5 seconds).
   *
   * @param dpaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setDpaTimeout(long dpaTimeout) throws MBeanException;

  /**
   * Sets the timeout for reconnecting. (default: 10000, 10 seconds).
   *
   * @param recTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Parameters_setRecTimeout(long recTimeout) throws MBeanException;

  void _Parameters_setConcurrentEntity(String name, String desc, Integer size) throws MBeanException;

  void _Parameters_setStatisticLoggerDelay(long delay) throws MBeanException;

  void _Parameters_setStatisticLoggerPause(long pause) throws MBeanException;

  // Network : Peers -----------------------------------------------------

  /**
   * Adds a peer definition to the stack. Same as <peer/> element in XML Configuration.
   *
   * @param name the name/uri of the peer
   * @param attemptConnect indicates if the stack should try to connect to this peer or wait for incoming connection
   * @param rating the peer rating for decision on message routing
   * @param realm name of the realm
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Peers_addPeer(String name, boolean attemptConnect, int rating, String realm) throws MBeanException;

  /**
   * Removes a peer definition from stack.
   *
   * @param name the name/uri of the peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Peers_removePeer(String name) throws MBeanException;

  // Network : Realms ----------------------------------------------------

  /**
   * Adds a new Realm to the stack. Same as <realm/> element in XML Configuration.
   *
   * @param name the name of the Realm
   * @param peers the Realm peer hosts, separated by comma
   * @param appVendorId the vendor-id of the application supported by this realm
   * @param appAcctId the accounting-id of the application supported by this realm
   * @param appAuthId the authorization-id of the application supported by this realm
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId) throws MBeanException;

  void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId, String localAction, String agentConfiguration,
      boolean isDynamic, int expTime) throws MBeanException;

  /**
   * Removes a Realm from the stack.
   *
   * @param name the name of the Realm
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Realms_removeRealm(String name) throws MBeanException;

  /**
   * Adds a new Peer host to the Realm
   *
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be added
   * @param attemptConnecting either try or not to connect the peer (client/server)
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Realms_addPeerToRealm(String realmName, String peerName, boolean attemptConnecting) throws MBeanException;

  /**
   * Removes a Peer host from the Realm
   *
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be removed
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Network_Realms_removePeerFromRealm(String realmName, String peerName) throws MBeanException;

  // Stack Operation -----------------------------------------------------

  /**
   * Operation to stop the stack.
   *
   * @throws MBeanException
   */
  void stopStack(int disconnectCause) throws MBeanException;

  /**
   * Operation to start the stack.
   *
   * @throws MBeanException
   */
  void startStack() throws MBeanException;

  // Validation ----------------------------------------------------------

  /**
   * Sets whether validation on Diameter messages/AVPs should be performed or not.
   *
   * @param enableValidation flag indicating if validation should be performed
   * @throws MBeanException if the operation is unable to perform correctly
   */
  void _Validation_setEnabled(boolean enableValidation) throws MBeanException;

  // Configuration Dump --------------------------------------------------

  /**
   * Dumps full stack configuration.
   *
   * @return a String with stack configuration
   * @throws MBeanException if the operation is unable to perform correctly
   */
  String dumpStackConfiguration() throws MBeanException;

  // Information dump methods --------------------------------------------

  String _LocalPeer_getProductName() throws MBeanException;

  Long _LocalPeer_getVendorId() throws MBeanException;

  Long _LocalPeer_getFirmware() throws MBeanException;

  String _LocalPeer_getURI() throws MBeanException;

  String _LocalPeer_getRealmName() throws MBeanException;

  InetAddress[] _LocalPeer_getIPAddresses() throws MBeanException;

  Set<ApplicationId> _LocalPeer_getCommonApplicationIds() throws MBeanException;

  String[] _Network_Realms_getRealms() throws MBeanException;

  String[] _Network_Realms_getRealmPeers(String realmName) throws MBeanException;

  boolean _LocalPeer_isActive() throws MBeanException;

  boolean _Network_Peers_isPeerConnected(String name) throws MBeanException;
  List<Peer> _Network_Peers_retrievePeer() throws MBeanException;
}
