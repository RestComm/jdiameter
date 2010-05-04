package org.mobicents.diameter.stack;

import static org.jdiameter.server.impl.helpers.Parameters.*;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.management.MBeanException;

import org.jboss.system.ServiceMBeanSupport;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationAlreadyUseException;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Message;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.MutablePeerTable;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.RealmTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.controller.PeerImpl;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.server.impl.NetworkImpl;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiameterStackMultiplexer extends ServiceMBeanSupport implements DiameterStackMultiplexerMBean, DiameterProvider, NetworkReqListener, EventListener<Request, Answer>, DiameterMessageFactory
{
  private static final Logger logger = LoggerFactory.getLogger(DiameterStackMultiplexer.class);

  protected Stack stack = null;

  protected HashMap<DiameterListener, Collection<org.jdiameter.api.ApplicationId>> listenerToAppId = new HashMap<DiameterListener, Collection<org.jdiameter.api.ApplicationId>>(3);
  protected HashMap<Long, DiameterListener> appIdToListener = new HashMap<Long, DiameterListener>(3);

  // This is for synch
  protected ReentrantLock lock = new ReentrantLock();

  protected DiameterProvider provider;

  // ===== STACK MANAGEMENT =====

  private void initStack() throws Exception {
    initStack(this.getClass().getClassLoader().getResourceAsStream("config/jdiameter-config.xml"));
  }

  private void initStack(InputStream is) throws Exception {
    try {
      // Create and configure stack
      this.stack = new StackImpl();

      // Load the configuration
      Configuration config = new XMLConfiguration(is);

      this.stack.init(config);

      Network network = stack.unwrap(Network.class);

      Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

      if(logger.isInfoEnabled()) {
        logger.info("Diameter Stack Mux :: Supporting {} applications.", appIds.size());
      }
      //network.addNetworkReqListener(this, ApplicationId.createByAccAppId(193, 19302));

      for (org.jdiameter.api.ApplicationId appId : appIds) {
        if(logger.isInfoEnabled()) {
          logger.info("Diameter Stack Mux :: Adding Listener for [{}].", appId);
        }
        network.addNetworkReqListener(this, appId);

        if(appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE) {
          this.appIdToListener.put(appId.getAcctAppId(), null);
        }
        else if(appId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE) {
          this.appIdToListener.put(appId.getAuthAppId(), null);
        }
      }

      try {
        if(logger.isInfoEnabled()) {
          logger.info("Parsing AVP Dictionary file...");
        }
        AvpDictionary.INSTANCE.parseDictionary(AvpDictionary.class.getResourceAsStream("dictionary.xml"));
        if(logger.isInfoEnabled()) {
          logger.info("AVP Dictionary file successfuly parsed!");
        }
      }
      catch (Exception e) {
        logger.error("Error while parsing dictionary file.", e);
      }

      this.stack.start();
    }
    finally {
      if (is != null)
        is.close();

      is = null;
    }

    if(logger.isInfoEnabled()) {
      logger.info("Diameter Stack Mux :: Successfully initialized stack.");
    }
  }

  private void doStopStack() throws Exception {
    try {
      if(logger.isInfoEnabled()) {
        logger.info("Stopping Diameter Mux Stack...");
      }

      stack.stop(10, TimeUnit.SECONDS);

      if(logger.isInfoEnabled()) {
        logger.info("Diameter Mux Stack Stopped Successfully.");
      }
    }
    catch (Exception e) {
      logger.error("Failure while stopping stack", e);
    }

    stack.destroy();
  }

  private DiameterListener findListener(Message message) {
    Set<org.jdiameter.api.ApplicationId> appIds = message.getApplicationIdAvps();

    if(appIds.size() > 0) {
      for(org.jdiameter.api.ApplicationId appId : appIds) {
        if(logger.isDebugEnabled()) {
          logger.debug("Diameter Stack Mux :: findListener :: AVP AppId [" + appId + "]");
        }

        DiameterListener listener;

        Long appIdValue = appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE ? appId.getAcctAppId() : appId.getAuthAppId(); 

        if((listener = this.appIdToListener.get(appIdValue)) != null) {
          if(logger.isDebugEnabled()) {
            logger.debug("Diameter Stack Mux :: findListener :: Found Listener [" + listener + "]");
          }

          return listener;
        }
      }
    }
    else {
      Long appId = message.getApplicationId();

      if(logger.isDebugEnabled()) {
        logger.debug("Diameter Stack Mux :: findListener :: Header AppId [" + appId + "]");
      }

      DiameterListener listener;

      if((listener = this.appIdToListener.get(appId)) != null) {
        if(logger.isDebugEnabled()) {
          logger.debug("Diameter Stack Mux :: findListener :: Found Listener [" + listener + "]");
        }

        return listener;
      }
    }

    if(logger.isInfoEnabled()) {
      logger.info("Diameter Stack Mux :: findListener :: No Listener Found.");
    }

    return null;
  }

  // ===== NetworkReqListener IMPLEMENTATION ===== 

  public Answer processRequest(Request request) {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Stack Mux :: processRequest :: Command-Code [" + request.getCommandCode() + "]");
    }

    DiameterListener listener = findListener(request);

    if(listener != null) {
      return listener.processRequest(request);
    }
    else {
      try {
        Answer answer = request.createAnswer(ResultCode.APPLICATION_UNSUPPORTED);
        //this.stack.getSessionFactory().getNewRawSession().send(answer);
        return answer;
      }
      catch (Exception e) {
        logger.error("Failed to create APPLICATION UNSUPPORTED answer.", e);
      }
    }
    return null;
  }

  // ===== EventListener<Request, Answer> IMPLEMENTATION ===== 

  public void receivedSuccessMessage(Request request, Answer answer) {
    DiameterListener listener = findListener(request);

    if(listener != null) {
      listener.receivedSuccessMessage(request, answer);
    }
  }

  public void timeoutExpired(Request request) {
    DiameterListener listener = findListener(request);

    if(listener != null) {
      listener.timeoutExpired(request);
    }
  }

  // ===== SERVICE LIFECYCLE MANAGEMENT =====

  @Override
  protected void startService() throws Exception {
    super.startService();
    initStack();
  }

  @Override
  protected void stopService() throws Exception {
    super.stopService();
    doStopStack();
  }

  public String sendMessage(Message message) {
    try {
      Avp sessionId = null;
      Session session = null;

      if((sessionId = message.getAvps().getAvp(Avp.SESSION_ID)) == null) {
        session = stack.getSessionFactory().getNewSession();
      }
      else {
        session = stack.getSessionFactory().getNewSession(sessionId.getUTF8String());
      }

      session.send(message);

      return session.getSessionId();
    }
    catch (Exception e) {
      logger.error("", e);
    }

    return null;
  }

  public Message sendMessageSync(Message message) {
    try {
      Avp sessionId = null;
      Session session = null;

      if((sessionId = message.getAvps().getAvp(Avp.SESSION_ID)) == null) {
        session = stack.getSessionFactory().getNewSession();
      }
      else {
        session = stack.getSessionFactory().getNewSession(sessionId.getUTF8String());
      }

      Future<Message> answer = session.send(message);

      return answer.get();
    }
    catch (Exception e) {
      logger.error("", e);
    }

    return null;
  }

  public Message createMessage(boolean isRequest, int commandCode, long applicationId) {
    try {
      Message message = this.stack.getSessionFactory().getNewRawSession().createMessage(commandCode, org.jdiameter.api.ApplicationId.createByAccAppId(applicationId), new Avp[]{});
      message.setRequest(isRequest);

      return  message;
    }
    catch (Exception e) {
      logger.error("Failure while creating message.", e);
    }

    return null;
  }

  public Message createRequest(int commandCode, long applicationId) {
    return createMessage(true, commandCode, applicationId);
  }

  public Message createAnswer(int commandCode, long applicationId) {
    return createMessage(false, commandCode, applicationId);
  }

  // ===== MBEAN OPERATIONS =====

  public DiameterStackMultiplexerMBean getMultiplexerMBean() {
    return this;
  }

  public DiameterMessageFactory getMessageFactory() {
    return this;
  }

  public DiameterProvider getProvider() {
    return this;
  }

  public Stack getStack() {
    return new DiameterStackProxy(this.stack);
  }

  public void registerListener(DiameterListener listener, org.jdiameter.api.ApplicationId[] appIds) throws IllegalStateException {
    if(listener == null) {
      logger.warn("Trying to register a null Listener. Give up...");
      return;
    }

    int curAppIdIndex = 0;

    try {
      lock.lock();

      // Register the selected appIds in the stack
      Network network = stack.unwrap(Network.class);

      if(logger.isInfoEnabled()) {
        logger.info("Diameter Stack Mux :: Registering  " + appIds.length + " applications.");
      }

      for (; curAppIdIndex < appIds.length; curAppIdIndex++) {
        org.jdiameter.api.ApplicationId appId = appIds[curAppIdIndex];
        if(logger.isInfoEnabled()) {
          logger.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        }
        network.addNetworkReqListener(this, appId);

        if(appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE) {
          this.appIdToListener.put(appId.getAcctAppId(), listener);
        }
        else if(appId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE) {
          this.appIdToListener.put(appId.getAuthAppId(), listener);
        }
      }

      // And add the listener and it's holder
      Collection<org.jdiameter.api.ApplicationId> registeredAppIds = this.listenerToAppId.get(listener);

      // Merge the existing (if any) with new.
      if(registeredAppIds != null) {
        registeredAppIds.addAll(Arrays.asList(appIds));
      }
      else {
        this.listenerToAppId.put(listener, Arrays.asList(appIds));
      }
    }
    catch (ApplicationAlreadyUseException aaue) {
      // Let's remove what we've done so far...
      try {
        Network network = stack.unwrap(Network.class);

        for (; curAppIdIndex >= 0; curAppIdIndex--) {
          // Remove the app id from map
          this.appIdToListener.remove(appIds[curAppIdIndex]);

          // Unregister it from stack listener
          network.removeNetworkReqListener(appIds[curAppIdIndex]);
        }
      }
      catch (Exception e) {
        logger.error("", e);
      }
    }
    catch (Exception e) {
      logger.error("", e);
    }
    finally {
      lock.unlock();
    }
  }

  public void unregisterListener(DiameterListener listener) {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Stack Mux :: unregisterListener :: Listener [" + listener + "]");
    }
    
    if(listener == null) {
      logger.warn("Diameter Stack Mux :: unregisterListener :: Trying to unregister a null Listener. Give up...");
      return;
    }

    try
    {
      lock.lock();

      Collection<org.jdiameter.api.ApplicationId> appIds = this.listenerToAppId.remove(listener);

      if(appIds == null) {
        logger.warn("Diameter Stack Mux :: unregisterListener :: Listener has no App-Ids registered. Give up...");
        return;
      }

      Network network = stack.unwrap(Network.class);

      for (org.jdiameter.api.ApplicationId appId : appIds) {
        try {
          if(logger.isInfoEnabled()) {
            logger.info("Diameter Stack Mux :: unregisterListener :: Unregistering AppId [" + appId + "]");
          }

          // Remove the appid from map
          this.appIdToListener.remove(appId);

          // and unregister the listener from stack
          network.removeNetworkReqListener(appId);
        }
        catch (Exception e) {
          logger.error("", e);
        }
      }
    }
    catch (InternalException ie) {
      logger.error("", ie);
    }
    finally {
      lock.unlock();
    }
  }

  //  management operations ----------------------------------------------

  /*
   *  -- MutableConfiguration Parameters --
   * Levels  Parameters name
   * Runtime 
   *  y DuplicateTimer
   *  y AcceptUndefinedPeer
   *  y MessageTimeOut
   *  y StopTimeOut
   *  y CeaTimeOut
   *  y IacTimeOut
   *  y DwaTimeOut
   *  y DpaTimeOut
   *  y RecTimeOut
   *  y PeerTable, Peer, PeerName, PeerRating, PeerAttemptConnection (by NetWork interface)
   *  y RealmTable, Realm, RealmEntry RealmName, RealmHosts, RealmLocalAction, RealmEntryIsDynamic, RealmEntryExpTime (by NetWork interface)
   * Restart stack
   *  y OwnDiameterURI
   *  y OwnIPAddresses, OwnIPAddress
   *  y OwnRealm
   *  y OwnVendorID
   *  n OwnProductName
   *  n OwnFirmwareRevision
   *  n ApplicationId, VendorId, AuthApplId, AcctApplId
   * Not changeable
   *  n OverloadMonitor, OverloadMonitorEntry, OverloadMonitorData, OverloadEntryIndex, OverloadEntryhighThreshold, OverloadEntrylowThreshold
   *  n DuplicateProtection
   *  n QueueSize
   */

  private final String DEFAULT_STRING = "default_string";

  private MutableConfiguration getMutableConfiguration() throws MBeanException {
    return (MutableConfiguration) stack.getMetaData().getConfiguration();
  }

  private AppConfiguration getClientConfiguration() {
    return org.jdiameter.client.impl.helpers.EmptyConfiguration.getInstance();
  }

  final Pattern IP_PATTERN = Pattern.compile("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");

  public void _LocalPeer_addIPAddress(String ipAddress) throws MBeanException {
    // validate ip address
    if(IP_PATTERN.matcher(ipAddress).matches()) {
      Configuration[] oldIPAddressesConfig = getMutableConfiguration().getChildren(OwnIPAddresses.ordinal());

      List<Configuration> newIPAddressesConfig  = Arrays.asList(oldIPAddressesConfig);
      AppConfiguration newIPAddress = getClientConfiguration().add(OwnIPAddress, ipAddress);
      newIPAddressesConfig.add(newIPAddress);

      getMutableConfiguration().setChildren(OwnIPAddresses.ordinal(), (Configuration[]) newIPAddressesConfig.toArray());

      if(logger.isInfoEnabled()) {
        logger.info("Local Peer IP Address successfully changed to " + ipAddress + ". Restart to Diameter stack is needed to apply changes.");
      }
    }
    else {
      throw new MBeanException(new IllegalArgumentException("Invalid IP address entered (" + ipAddress + ")"));
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_LocalPeer_removeIPAddress(java.lang.String)
   */
  public void _LocalPeer_removeIPAddress(String ipAddress) throws MBeanException {
    Configuration[] oldIPAddressesConfig = getMutableConfiguration().getChildren(OwnIPAddresses.ordinal());

    AppConfiguration ipAddressToRemove = null;

    List<Configuration> newIPAddressesConfig  = Arrays.asList(oldIPAddressesConfig);
    for(Configuration curIPAddress : newIPAddressesConfig) {
      if(curIPAddress.getStringValue(OwnIPAddress.ordinal(), DEFAULT_STRING).equals(ipAddress)) {
        break;
      }
    }

    if(ipAddressToRemove != null) {
      newIPAddressesConfig.remove(ipAddressToRemove);

      getMutableConfiguration().setChildren(OwnIPAddresses.ordinal(), (Configuration[]) newIPAddressesConfig.toArray());

      if(logger.isInfoEnabled()) {
        logger.info("Local Peer IP Address " + ipAddress + " successfully added. Restart to Diameter stack is needed to apply changes.");
      }
    }
    else {
      if(logger.isInfoEnabled()) {
        logger.info("Local Peer IP Address " + ipAddress + " not found. No changes were made.");
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_LocalPeer_setRealm(java.lang.String)
   */
  public void _LocalPeer_setRealm(String realm) throws MBeanException {
    getMutableConfiguration().setStringValue(OwnRealm.ordinal(), realm);

    if(logger.isInfoEnabled()) {
      logger.info("Local Peer Realm successfully changed to '" + realm + "'. Restart to Diameter stack is needed to apply changes.");
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_LocalPeer_setURI(java.lang.String)
   */
  public void _LocalPeer_setURI(String uri) throws MBeanException {
    // validate uri
    try {
      new URI(uri);

      getMutableConfiguration().setStringValue(OwnDiameterURI.ordinal(), uri);

      if(logger.isInfoEnabled()) {
        logger.info("Local Peer URI successfully changed to '" + uri + "'. Restart to Diameter stack is needed to apply changes.");
      }
    }
    catch (URISyntaxException use) {
      throw new MBeanException(use);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_LocalPeer_setVendorId(java.lang.String)
   */
  public void _LocalPeer_setVendorId(long vendorId) throws MBeanException {
    // validate vendor-id
    try {
      getMutableConfiguration().setLongValue(OwnVendorID.ordinal(), vendorId);
      if(logger.isInfoEnabled()) {
        logger.info("Local Peer Vendor-Id successfully changed to '" + vendorId + "'. Restart to Diameter stack is needed to apply changes.");
      }
    }
    catch (NumberFormatException nfe) {
      throw new MBeanException(nfe);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_Network_Peers_addPeer(java.lang.String, boolean, int)
   */
  public void _Network_Peers_addPeer(String name, boolean attemptConnect, int rating) throws MBeanException {
    try {
      NetworkImpl n = (NetworkImpl) stack.unwrap(Network.class);
      /*Peer p =*/ n.addPeer(name, "", attemptConnect); // FIXME: This requires realm...
    }
    catch (IllegalArgumentException e) {
      logger.warn(e.getMessage());
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to add peer with name '" + name + "'");
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_Network_Peers_removePeer(java.lang.String)
   */
  public void _Network_Peers_removePeer(String name) throws MBeanException {
    try {
      MutablePeerTable n = (MutablePeerTable) stack.unwrap(PeerTable.class);
      n.removePeer(name);
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to remove peer with name '" + name + "'");
    }
  }

  public void _Network_Realms_addPeerToRealm(String realmName, String peerName, boolean attemptConnect) throws MBeanException {
    try {
      NetworkImpl n = (NetworkImpl) stack.unwrap(Network.class);
      /*Peer p =*/ n.addPeer(peerName, realmName, attemptConnect);
    }
    catch (IllegalArgumentException e) {
      logger.warn(e.getMessage());
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to add peer with name '" + peerName + "' to realm '" + realmName + "'");
    }
  }

  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId, String localAction, boolean isDynamic, int expTime) throws MBeanException {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) stack.unwrap(org.jdiameter.api.Network.class);
      ApplicationId appId = appAcctId == 0 ? org.jdiameter.api.ApplicationId.createByAuthAppId(appVendorId, appAuthId) : org.jdiameter.api.ApplicationId.createByAccAppId(appVendorId, appAcctId);
      org.jdiameter.api.Realm r = n.addRealm(name, appId, LocalAction.valueOf(localAction), isDynamic, expTime);
      for(String peer : peers.split(",")) {
        r.addPeerName(peer);
      }
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to add realm with name '" + name + "'.");
    }
  }
  
  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId) throws MBeanException {
    _Network_Realms_addRealm(name, peers, appVendorId, appAcctId, appAuthId, "LOCAL", false, 1);
  }

  public void _Network_Realms_removePeerFromRealm(String realmName, String peerName) throws MBeanException {
    try {
      RealmTable rt = (RealmTable) stack.unwrap(RealmTable.class);
      for(org.jdiameter.api.Realm r : rt.getAllRealms()) {
        if(r.getName().equals(realmName)) {
          r.removePeerName(peerName);
        }
      }
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to add peer '" + peerName + "' from realm with '" + realmName + "'.");
    }
  }

  public void _Network_Realms_removeRealm(String name) throws MBeanException {
    try {
      org.jdiameter.server.impl.NetworkImpl n = (org.jdiameter.server.impl.NetworkImpl) stack.unwrap(org.jdiameter.api.Network.class);
      n.remRealm(name);
    }
    catch (InternalException e) {
      throw new MBeanException(e, "Failed to remove realm '" + name + "'.");
    }
  }

  public void _Parameters_setAcceptUndefinedPeer(boolean acceptUndefinedPeer) throws MBeanException {
    getMutableConfiguration().setBooleanValue(AcceptUndefinedPeer.ordinal(), acceptUndefinedPeer);
  }

  public void _Parameters_setUseUriAsFqdn(boolean useUriAsFqdn) throws MBeanException {
    getMutableConfiguration().setBooleanValue(UseUriAsFqdn.ordinal(), useUriAsFqdn);
  }

  public void _Parameters_setDuplicateTimer(long duplicateTimer) throws MBeanException {
    getMutableConfiguration().setLongValue(DuplicateTimer.ordinal(), duplicateTimer);
  }

  public void _Parameters_setMessageTimeout(long messageTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(MessageTimeOut.ordinal(), messageTimeout);
  }

  public void _Parameters_setStopTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(StopTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setCeaTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(CeaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setIacTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(IacTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setDwaTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(DwaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setDpaTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(DpaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setRecTimeout(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(RecTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setConcurrentEntity(String name, String desc, Integer size) throws MBeanException {
    for(Configuration c : getMutableConfiguration().getChildren(Concurrent.ordinal())) {
      if(name.equals(c.getStringValue(ConcurrentEntityName.ordinal(), null))) {
        ((AppConfiguration)c).add(ConcurrentEntityPoolSize, size);
        if(desc != null) {
          ((AppConfiguration)c).add(ConcurrentEntityDescription, desc);
        }
      }
    }
  }
  public void _Parameters_setStatisticLoggerDelay(long delay)  throws MBeanException {
    getMutableConfiguration().setLongValue(StatisticLoggerDelay.ordinal(), delay);
  }

  public void _Parameters_setStatisticLoggerPause(long pause) throws MBeanException {
    getMutableConfiguration().setLongValue(StatisticLoggerPause.ordinal(), pause);
  }

  public void _Validation_setEnabled(boolean enableValidation) throws MBeanException {
    DiameterMessageValidator.setOn(enableValidation);
  }

  public String dumpStackConfiguration() throws MBeanException {
    return getMutableConfiguration().toString();
  }

  public void startStack() throws MBeanException {
    try {
      this.stack.start();
    }
    catch (Exception e) {
      throw new MBeanException(e);
    }
  }

  public void stopStack() throws MBeanException {
    try {
      this.stack.stop(getMutableConfiguration().getLongValue(StopTimeOut.ordinal(), 10000L), TimeUnit.MILLISECONDS);
    }
    catch (Exception e) {
      throw new MBeanException(e);
    }
  }  

  // Getters ------------------------------------------------------------- //
  
  public String _LocalPeer_getProductName() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getProductName();
  }

  public Long _LocalPeer_getVendorId() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getVendorId();
  }

  public Long _LocalPeer_getFirmware() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getFirmware();
  }

  public String _LocalPeer_getURI() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getUri().toString();
  }

  public String _LocalPeer_getRealmName() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getRealmName();
  }

  public InetAddress[] _LocalPeer_getIPAddresses() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getIPAddresses();
  }

  public Set<ApplicationId> _LocalPeer_getCommonApplicationIds() throws MBeanException {
    return this.stack.getMetaData().getLocalPeer().getCommonApplications();
  }

  public String[] _Network_Realms_getRealms() throws MBeanException {
    Configuration[] realmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    String[] realmNames = new String[realmEntries.length];

    for(int i = 0; i < realmEntries.length; i++) {
      realmNames[i] = realmEntries[i].getStringValue(RealmName.ordinal(), DEFAULT_STRING);
    }

    return realmNames;
  }

  public String[] _Network_Realms_getRealmPeers(String realmName) throws MBeanException {
    Configuration[] realmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    String[] realmHosts = new String[realmEntries.length];

    for(Configuration realmEntry : realmEntries) {
      if(realmEntry.getStringValue(RealmName.ordinal(), DEFAULT_STRING).equals(realmName)) {

        String realmHostsString = realmEntry.getStringValue(RealmHosts.ordinal(), DEFAULT_STRING);
        if(!realmHostsString.equals(DEFAULT_STRING)) {
          realmHosts = realmHostsString.replaceAll(" ", "").split(",");
        }
      }
    }

    return realmHosts;
  }

  public DiameterConfiguration getDiameterConfiguration() throws MBeanException {
    return new DiameterConfiguration(stack);
  }
  
  public boolean _LocalPeer_isActive() throws MBeanException {
    return this.stack.isActive();
  }

  public boolean _Network_Peers_isPeerConnected(String name) throws MBeanException {
    try {
      MutablePeerTable n = (MutablePeerTable) stack.unwrap(PeerTable.class);
      PeerImpl p = ((PeerImpl)n.getPeer(name));
      return p != null ? p.getContext().isConnected() : false;
    }
    catch (Exception e) {
      throw new MBeanException(e, "Failed to get connection availability for peer with name '" + "'.");
    }
  }
  
  
}
