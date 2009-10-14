package org.mobicents.diameter.stack;

import javax.management.MBeanException;

import org.jboss.system.ServiceMBean;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;

public interface DiameterStackMultiplexerMBean extends ServiceMBean
{

  public static final String MBEAN_NAME_PREFIX = "diameter:Service=DiameterStackMultiplexer,Name=";
  
  public void registerListener(DiameterListener listener, ApplicationId[] appIds) throws IllegalStateException;
  
  public void unregisterListener(DiameterListener listener);

  //For sake of simplicity in the pre Gamma :)
  public Stack getStack();
  
  public DiameterProvider getProvider();
  
  public DiameterMessageFactory getMessageFactory();
  
  public DiameterStackMultiplexerMBean getMultiplexerMBean();
  
  // MANAGEMENT OPERATIONS
  
  // Local Peer ----------------------------------------------------------

  /**
   * Changes the URI of the Local Peer.
   * 
   * @param uri the new URI to be used by the Local Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _LocalPeer_setURI(String uri) throws MBeanException;
  
  /**
   * Adds an IP Address to the Local Peer.
   * @param ipAddress the IP Address to be added, if not present
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _LocalPeer_addIPAddress(String ipAddress) throws MBeanException;
  
  /**
   * Removes an IP Address from the Local Peer.
   * 
   * @param ipAddress the IP Address to be removed, if present
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _LocalPeer_removeIPAddress(String ipAddress) throws MBeanException;
  
  /**
   * Changes the Realm of the Local Peer.
   * 
   * @param realm the new Realm to be used by the Local Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _LocalPeer_setRealm(String realm) throws MBeanException;
  
  /**
   * Sets the Local Peer Vendor-Id.
   * 
   * @param vendorId the new Vendor-Id for the Peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _LocalPeer_setVendorId(long vendorId) throws MBeanException;
  
  // Parameters ----------------------------------------------------------

  /**
   * Sets whether the stack will accept connections from unknown peers or not (default: true)  
   * 
   * @param acceptUndefinedPeer indicates if the stack will accept unknown connections
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setAcceptUndefinedPeer(boolean acceptUndefinedPeer) throws MBeanException;
  
  /**
   * Sets whether the stack will use URI (aaa://IP_ADDRESS:PORT) as FQDN. Some Peers require it.
   * 
   * @param useUriAsFqdn indicates if the stack will use URI as FQDN
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setUseUriAsFqdn(boolean useUriAsFqdn) throws MBeanException;
  
  /**
   * Sets the value to consider a message as a duplicate, in ms. (default: 240000, 4 minutes). 
   * 
   * @param duplicateTimer the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setDuplicateTimer(long duplicateTimer) throws MBeanException;
  
  // Parameters : Timeouts -----------------------------------------------
  
  /**
   * Sets the timeout for general Diameter messages, in ms. (default: 60000, 1 minute).
   * 
   * @param messageTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setMessageTimeOut(long messageTimeout) throws MBeanException;
  
  /**
   * Sets the timeout for stopping the stack. (default: 10000, 10 seconds).
   * 
   * @param stopTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setStopTimeOut(long stopTimeout) throws MBeanException;

  /**
   * Sets the timeout for CEA messages. (default: 10000, 10 seconds).
   * 
   * @param ceaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setCeaTimeOut(long ceaTimeout) throws MBeanException;

  /**
   * Sets the timeout for inactiveness. (default: 20000, 20 seconds).
   * 
   * @param iacTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setIacTimeOut(long iacTimeout) throws MBeanException;

  /**
   * Sets the timeout for DWA messages. (default: 10000, 10 seconds).
   * 
   * @param dwaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setDwaTimeOut(long dwaTimeout) throws MBeanException;

  /**
   * Sets the timeout for DPA messages. (default: 5000, 5 seconds).
   * 
   * @param dpaTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setDpaTimeOut(long dpaTimeout) throws MBeanException;

  /**
   * Sets the timeout for reconnecting. (default: 10000, 10 seconds).
   * 
   * @param recTimeout the amount of time, in ms.
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Parameters_setRecTimeOut(long recTimeout) throws MBeanException;
  
  // Network : Peers -----------------------------------------------------
  
  /**
   * Adds a peer definition to the stack. Same as <peer/> element in XML Configuration.
   * 
   * @param name the name/uri of the peer
   * @param attemptConnect indicates if the stack should try to connect to this peer or wait for incoming connection
   * @param rating the peer rating for decision on message routing
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Network_Peers_addPeer(String name, boolean attemptConnect, int rating) throws MBeanException;
  
  /**
   * Removes a peer definition from stack.
   * 
   * @param name the name/uri of the peer
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Network_Peers_removePeer(String name) throws MBeanException;

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
  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId) throws MBeanException;
  
  /**
   * Removes a Realm from the stack.
   * 
   * @param name the name of the Realm
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Network_Realms_removeRealm(String name) throws MBeanException;
  
  /**
   * Adds a new Peer host to the Realm
   * 
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be added
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Network_Realms_addPeerToRealm(String realmName, String peerName) throws MBeanException;
  
  /**
   * Removes a Peer host from the Realm
   * 
   * @param realmName the name of the Realm
   * @param peerName the name/host of the Peer to be removed
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Network_Realms_removePeerFromRealm(String realmName, String peerName) throws MBeanException;

  // Stack Operation -----------------------------------------------------
  
  /**
   * Operation to stop the stack.
   * 
   * @throws MBeanException
   */
  public void stopStack() throws MBeanException;

  /**
   * Operation to start the stack.
   * 
   * @throws MBeanException
   */
  public void startStack() throws MBeanException;
  
  // Validation ----------------------------------------------------------
  
  /**
   * Sets whether validation on Diameter messages/AVPs should be performed or not.
   * 
   * @param enableValidation flag indicating if validation should be performed
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public void _Validation_setEnabled(boolean enableValidation) throws MBeanException;
  
  // Configuration Dump --------------------------------------------------
  
  /**
   * Dumps full stack configuration.
   * 
   * @return a String with stack configuration
   * @throws MBeanException if the operation is unable to perform correctly
   */
  public String dumpStackConfiguration() throws MBeanException;
}
