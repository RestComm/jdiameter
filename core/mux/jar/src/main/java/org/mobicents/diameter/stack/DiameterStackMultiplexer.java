package org.mobicents.diameter.stack;

import static org.jdiameter.server.impl.helpers.Parameters.*;

import java.io.InputStream;
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
import org.jdiameter.api.Avp;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Mode;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.helpers.AppConfiguration;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;
import org.mobicents.diameter.dictionary.AvpDictionary;

public class DiameterStackMultiplexer extends ServiceMBeanSupport implements DiameterStackMultiplexerMBean, DiameterProvider, NetworkReqListener, EventListener<Request, Answer>, DiameterMessageFactory
{
  protected Stack stack = null;

  protected HashMap<DiameterListener, Collection<org.jdiameter.api.ApplicationId>> listenerToAppId = new HashMap<DiameterListener, Collection<org.jdiameter.api.ApplicationId>>(3);
  protected HashMap<Long, DiameterListener> appIdToListener = new HashMap<Long, DiameterListener>(3);

  // This is for synch
  protected ReentrantLock lock = new ReentrantLock();
  
  protected DiameterProvider provider;
  
  // ===== STACK MANAGEMENT =====
  
  private void initStack() throws Exception
  {
    InputStream is = null;

    try 
    {
      // Create and configure stack
      this.stack = new StackImpl();

      // Get configuration
      String configFile = "jdiameter-config.xml";
      is = this.getClass().getClassLoader().getResourceAsStream("config/" + configFile);

      // Load the configuration
      Configuration config = new XMLConfiguration(is);

      this.stack.init(config);

      Network network = stack.unwrap(Network.class);

      Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

      log.info("Diameter Stack Mux :: Supporting " + appIds.size() + " applications.");

      //network.addNetworkReqListener(this, ApplicationId.createByAccAppId( 193, 19302 ));

      for (org.jdiameter.api.ApplicationId appId : appIds)
      {
        log.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        network.addNetworkReqListener(this, appId);
        
        if( appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAcctAppId(), null);
        }
        else if( appId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAuthAppId(), null);
        }
      }

      try
      {
        log.info( "Parsing AVP Dictionary file..." );
        AvpDictionary.INSTANCE.parseDictionary( AvpDictionary.class.getResourceAsStream("dictionary.xml") );
        log.info( "AVP Dictionary file successfuly parsed!" );
      }
      catch ( Exception e )
      {
        log.error( "Error while parsing dictionary file.", e );
      }
      
      this.stack.start();
      
    }
    finally
    {
      if (is != null)
        is.close();

      is = null;
    }

    log.info("Diameter Stack Mux :: Successfully initialized stack.");
  }
  
  private void doStopStack() throws Exception
  {
    try
    {
      log.info("Stopping Diameter Mux Stack...");
      
      stack.stop(10, TimeUnit.SECONDS);
      
      log.info("Diameter Mux Stack Stopped Successfully.");
    }
    catch (Exception e)
    {
      log.error( "Failure while stopping stack", e );
    }

    stack.destroy();
  }
  
  private DiameterListener findListener(Message message)
  {

    Set<org.jdiameter.api.ApplicationId> appIds = message.getApplicationIdAvps();
    
    if( appIds.size() > 0 )
    {
      for(org.jdiameter.api.ApplicationId appId : appIds)
      {
        log.info( "Diameter Stack Mux :: findListener :: AVP AppId [" + appId + "]" );
  
        DiameterListener listener;
        
        Long appIdValue = appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE ? appId.getAcctAppId() : appId.getAuthAppId(); 
        
        if( (listener = this.appIdToListener.get(appIdValue)) != null )
        {
          log.info( "Diameter Stack Mux :: findListener :: Found Listener [" + listener + "]" );
          
          return listener;
        }
      }
    }
    else
    {
      Long appId = message.getApplicationId();
      
      log.info( "Diameter Stack Mux :: findListener :: Header AppId [" + appId + "]" );
      
      DiameterListener listener;
      
      if( (listener = this.appIdToListener.get(appId)) != null )
      {
        log.info( "Diameter Stack Mux :: findListener :: Found Listener [" + listener + "]" );
        
        return listener;
      }
    }
    
    log.info( "Diameter Stack Mux :: findListener :: No Listener Found." );
    
    return null;
  }
  
  // ===== NetworkReqListener IMPLEMENTATION ===== 
  
  public Answer processRequest( Request request )
  {
    log.info( "Diameter Stack Mux :: processRequest :: Command-Code [" + request.getCommandCode() + "]" );
    
    DiameterListener listener = findListener( request );
    
    if( listener != null )
    {
      return listener.processRequest( request );
    }
    else
    {
      try
      {
        Answer answer = request.createAnswer( ResultCode.APPLICATION_UNSUPPORTED );
        
        //this.stack.getSessionFactory().getNewRawSession().send(answer);
        
        return answer;
      }
      catch ( Exception e )
      {
        log.error( "", e );
      }
    }
    
    return null;
  }

  // ===== EventListener<Request, Answer> IMPLEMENTATION ===== 
  
  public void receivedSuccessMessage( Request request, Answer answer )
  {
    DiameterListener listener = findListener( request );
    
    if( listener != null )
    {
      listener.receivedSuccessMessage( request, answer );
    }
  }

  public void timeoutExpired( Request request )
  {
    DiameterListener listener = findListener( request );
    
    if( listener != null )
    {
      listener.timeoutExpired( request );
    }
  }
  
  // ===== SERVICE LIFECYCLE MANAGEMENT =====
  
  @Override
  protected void startService() throws Exception
  {
    super.startService();
    
    initStack();
  }

  @Override
  protected void stopService() throws Exception
  {
    super.stopService();
    
    doStopStack();
  }

  public String sendMessage( Message message )
  {
    try
    {
      Avp sessionId = null;
      Session session = null;
      
      if((sessionId = message.getAvps().getAvp(Avp.SESSION_ID)) == null)
      {
        session = stack.getSessionFactory().getNewSession();
      }
      else
      {
        session = stack.getSessionFactory().getNewSession( sessionId.getUTF8String() );
      }
      
      session.send( message );
      
      return session.getSessionId();
    }
    catch (Exception e) {
      log.error( "", e );
    }
    
    return null;
  }
  
  public Message sendMessageSync( Message message )
  {
    try
    {
      Avp sessionId = null;
      Session session = null;
      
      if((sessionId = message.getAvps().getAvp(Avp.SESSION_ID)) == null)
      {
        session = stack.getSessionFactory().getNewSession();
      }
      else
      {
        session = stack.getSessionFactory().getNewSession( sessionId.getUTF8String() );
      }
      
      Future<Message> answer = session.send( message );
      
      return answer.get();
    }
    catch (Exception e) {
      log.error( "", e );
    }
    
    return null;
  }

  public Message createMessage( boolean isRequest, int commandCode, long applicationId )
  {
    try
    {
      Message message = this.stack.getSessionFactory().getNewRawSession().createMessage( commandCode, org.jdiameter.api.ApplicationId.createByAccAppId( applicationId ), new Avp[]{} );
      message.setRequest( isRequest );
      
      return  message;
    }
    catch ( Exception e )
    {
      log.error( "Failure while creating message.", e );
    }
    
    return null;
  }

  public Message createRequest( int commandCode, long applicationId )
  {
    return createMessage( true, commandCode, applicationId );
  }

  public Message createAnswer( int commandCode, long applicationId )
  {
    return createMessage( false, commandCode, applicationId );
  }

  // ===== MBEAN OPERATIONS =====
  
  public DiameterStackMultiplexerMBean getMultiplexerMBean()
  {
    return this;
  }

  public DiameterMessageFactory getMessageFactory()
  {
    return this;
  }

  public DiameterProvider getProvider()
  {
    return this;
  }

  public Stack getStack()
  {
    return new DiameterStackProxy(this.stack);
  }

  public void registerListener( DiameterListener listener, org.jdiameter.api.ApplicationId[] appIds) throws IllegalStateException
  {
    if(listener == null)
    {
      log.warn( "Trying to register a null Listener. Give up..." );
      
      return;
    }
    
    int curAppIdIndex = 0;
    
    try
    {
      lock.lock();
      
      // Register the selected appIds in the stack
      Network network = stack.unwrap(Network.class);

      log.info("Diameter Stack Mux :: Registering  " + appIds.length + " applications.");
      
      for (; curAppIdIndex < appIds.length; curAppIdIndex++)
      {
        org.jdiameter.api.ApplicationId appId = appIds[curAppIdIndex];
        log.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        network.addNetworkReqListener(this, appId);
        
        if( appId.getAcctAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAcctAppId(), listener);
        }
        else if( appId.getAuthAppId() != org.jdiameter.api.ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAuthAppId(), listener);
        }
      }

      // And add the listener and it's holder
      Collection<org.jdiameter.api.ApplicationId> registeredAppIds = this.listenerToAppId.get( listener );

      // Merge the existing (if any) with new.
      if(registeredAppIds != null)
      {
        registeredAppIds.addAll( Arrays.asList(appIds) );
      }
      else
      {
        this.listenerToAppId.put( listener, Arrays.asList(appIds) );
      }
    }
    catch (ApplicationAlreadyUseException aaue) {

      // Let's remove what we've done so far...
      try
      {
        Network network = stack.unwrap(Network.class);
        
        for (; curAppIdIndex >= 0; curAppIdIndex--)
        {
          // Remove the app id from map
          this.appIdToListener.remove(appIds[curAppIdIndex]);
          
          // Unregister it from stack listener
          network.removeNetworkReqListener(appIds[curAppIdIndex]);
        }
      }
      catch (Exception e) {
        log.error( "", e );
      }
    }
    catch (Exception e) {
      log.error( "", e );
    }
    finally {
      lock.unlock();
    }
  }

  public void unregisterListener( DiameterListener listener )
  {
    log.info( "Diameter Stack Mux :: unregisterListener :: Listener [" + listener + "]" );
    
    if(listener == null)
    {
      log.warn( "Diameter Stack Mux :: unregisterListener :: Trying to unregister a null Listener. Give up..." );
      
      return;
    }
    
    try
    {
      lock.lock();
      
      Collection<org.jdiameter.api.ApplicationId> appIds = this.listenerToAppId.remove(listener);
      
      if(appIds == null)
      {
        log.warn( "Diameter Stack Mux :: unregisterListener :: Listener has no App-Ids registered. Give up..." );
        
        return;
      }

      Network network = stack.unwrap(Network.class);
      
      for (org.jdiameter.api.ApplicationId appId : appIds)
      {
        try
        {
          log.info( "Diameter Stack Mux :: unregisterListener :: Unregistering AppId [" + appId + "]" );
          
          // Remove the appid from map
          this.appIdToListener.remove(appId);
          
          // and unregister the listener from stack
          network.removeNetworkReqListener(appId);
        }
        catch (Exception e)
        {
          log.error( "", e );
        }
      }

    }
    catch (InternalException ie)
    {
      log.error( "", ie );
    }
    finally
    {
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
   *  y PeerTable, Peer, PeerName, PeerRating, PeerAttemptConnection ( by NetWork interface)
   *  y RealmTable, Realm, RealmEntry RealmName, RealmHosts, RealmLocalAction, RealmEntryIsDynamic, RealmEntryExpTime ( by NetWork interface)
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

      log.info("Local Peer IP Address successfully changed to " + ipAddress + ". Restart to Diameter stack is needed to apply changes.");
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

      log.info("Local Peer IP Address " + ipAddress + " successfully added. Restart to Diameter stack is needed to apply changes.");
    }
    else {
      log.info("Local Peer IP Address " + ipAddress + " not found. No changes were made.");
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_LocalPeer_setRealm(java.lang.String)
   */
  public void _LocalPeer_setRealm(String realm) throws MBeanException {
    getMutableConfiguration().setStringValue(OwnRealm.ordinal(), realm);
    
    log.info("Local Peer Realm successfully changed to '" + realm + "'. Restart to Diameter stack is needed to apply changes.");
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
      
      log.info("Local Peer URI successfully changed to '" + uri + "'. Restart to Diameter stack is needed to apply changes.");
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
      log.info("Local Peer Vendor-Id successfully changed to '" + vendorId + "'. Restart to Diameter stack is needed to apply changes.");
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
    Configuration[] oldPeerTable = getMutableConfiguration().getChildren(PeerTable.ordinal());
    
    Configuration[] newPeerTable = Arrays.copyOf(oldPeerTable, oldPeerTable.length + 1);
    
    AppConfiguration newPeer = getClientConfiguration().add(PeerName, name);
    newPeer.add(PeerAttemptConnection, attemptConnect);
    newPeer.add(PeerRating, rating);
    
    newPeerTable[oldPeerTable.length] = newPeer;
    
    getMutableConfiguration().setChildren(PeerTable.ordinal(), newPeerTable);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.diameter.stack.DiameterStackMultiplexerMBean#_Network_Peers_removePeer(java.lang.String)
   */
  public void _Network_Peers_removePeer(String name) throws MBeanException {
    Configuration[] oldPeerTable = getMutableConfiguration().getChildren(PeerTable.ordinal());
    
    AppConfiguration peerToRemove = null;
    
    List<Configuration> newPeerTable  = Arrays.asList(oldPeerTable);
    for(Configuration curPeer : newPeerTable) {
      if(curPeer.getStringValue(PeerName.ordinal(), DEFAULT_STRING).equals(name)) {
        peerToRemove = (AppConfiguration) curPeer;
        break;
      }
    }
    
    if(peerToRemove != null) {
      newPeerTable.remove(peerToRemove);
      getMutableConfiguration().setChildren(PeerTable.ordinal(), (Configuration[]) newPeerTable.toArray());

      log.info("Peer '" + name + "' successfully added. Restart to Diameter stack is needed to apply changes.");
    }
    else {
      log.info("Peer '" + name + "' not found. No changes were made.");
    }
  }

  public void _Network_Realms_addPeerToRealm(String realmName, String peerName) throws MBeanException {
    
    Configuration[] realmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    
    for(Configuration realmEntry : realmEntries) {
      if(realmEntry.getStringValue(RealmName.ordinal(), DEFAULT_STRING).equals(realmName)) {
        
        String realmHosts = realmEntry.getStringValue(RealmHosts.ordinal(), DEFAULT_STRING);
        if(!realmHosts.equals(DEFAULT_STRING)) {
          realmHosts += ", " + peerName;
        }
        else {
          realmHosts = peerName;
        }
        
        ((org.jdiameter.client.impl.helpers.EmptyConfiguration)realmEntry).add(RealmHosts, realmHosts);
        log.info("Added peer '" + peerName + "' to Realm '" + realmName + "'.");
        return;
      }
    }
    
    log.info("No Realm with name '" + realmName + "' was found, no action was performed.");
  }

  public void _Network_Realms_addRealm(String name, String peers, long appVendorId, long appAcctId, long appAuthId) throws MBeanException {
    Configuration[] oldRealmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    
    for(Configuration realmEntry : oldRealmEntries) {
      if(realmEntry.getStringValue(RealmName.ordinal(), DEFAULT_STRING).equals(name)) {
        throw new MBeanException(new IllegalArgumentException("Realm with name '" + name + "' already exists."));
      }
    }

    Configuration[] newRealmEntries = Arrays.copyOf(oldRealmEntries, oldRealmEntries.length + 1);
    
    AppConfiguration newRealm = getClientConfiguration().add(RealmEntry, getClientConfiguration().
        add(ApplicationId, new Configuration[] {getClientConfiguration().add(VendorId, appVendorId).add(AcctApplId, appAcctId).add(AuthApplId, appAuthId)}).
        add(RealmName, name).
        add(RealmHosts, peers).
        add(RealmLocalAction, "LOCAL").
        add(RealmEntryIsDynamic, false).
        add(RealmEntryExpTime, 1));
    
    newRealmEntries[oldRealmEntries.length] = newRealm;
    
    ((org.jdiameter.client.impl.helpers.EmptyConfiguration)getMutableConfiguration().getChildren(RealmTable.ordinal())[0]).add(RealmEntry, newRealmEntries);
    log.info("Realm '" + name + "' added successfuly.");
  }

  public void _Network_Realms_removePeerFromRealm(String realmName, String peerName) throws MBeanException {
    
    Configuration[] realmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    
    for(Configuration realmEntry : realmEntries) {
      if(realmEntry.getStringValue(RealmName.ordinal(), DEFAULT_STRING).equals(realmName)) {
        
        String realmHosts[] = realmEntry.getStringValue(RealmHosts.ordinal(), DEFAULT_STRING).replaceAll(" ", "").split(",");
        
        String newRealmHosts[] = new String[realmHosts.length-1];
        
        int n = 0;
        
        for(String realmHost : realmHosts) {
          if(!peerName.equals(realmHost)) {
            if(n < newRealmHosts.length) {
              newRealmHosts[n++] = realmHost;
            }
            else {
              log.info("No Peer '" + peerName + "' found in Realm '" + realmName + "', no action was performed.");
              return;
            }            
          }
        }
        
        String newRealmHostsString = "";
        for(String realm : newRealmHosts) {
          newRealmHostsString += newRealmHostsString.length() == 0 ? realm : (", " + realm);
        }
        
        ((org.jdiameter.client.impl.helpers.EmptyConfiguration)realmEntry).add(RealmHosts, newRealmHostsString);
        log.info("Removed peer '" + peerName + "' from Realm '" + realmName + "'.");
        return;
      }
    }
    
    log.info("No Realm with name '" + realmName + "' was found, no action was performed.");
  }

  public void _Network_Realms_removeRealm(String name) throws MBeanException {
    Configuration[] oldRealmEntries = getMutableConfiguration().getChildren(RealmTable.ordinal())[0].getChildren(RealmEntry.ordinal());
    
    Configuration[] newRealmEntries = new Configuration[oldRealmEntries.length-1];
    
    int n = 0;
    
    for(Configuration realmEntry : oldRealmEntries) {
      if(!realmEntry.getStringValue(RealmName.ordinal(), DEFAULT_STRING).equals(name)) {
        if(n < newRealmEntries.length) {
          newRealmEntries[n++] = realmEntry;
        }
        else {
          log.info("No Realm with name '" + name + "' was found, no action was performed.");
        }
      }
      else if (log.isDebugEnabled()) {
        log.debug("Found instance of Realm to be removed:" + realmEntry);
      }
    }
    
    ((org.jdiameter.client.impl.helpers.EmptyConfiguration)getMutableConfiguration().getChildren(RealmTable.ordinal())[0]).add(RealmEntry, newRealmEntries);
    log.info("Realm '" + name + "' removed successfuly.");
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

  public void _Parameters_setMessageTimeOut(long messageTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(MessageTimeOut.ordinal(), messageTimeout);
  }

  public void _Parameters_setStopTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(StopTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setCeaTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(CeaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setIacTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(IacTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setDwaTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(DwaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setDpaTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(DpaTimeOut.ordinal(), stopTimeout);
  }

  public void _Parameters_setRecTimeOut(long stopTimeout) throws MBeanException {
    getMutableConfiguration().setLongValue(RecTimeOut.ordinal(), stopTimeout);
  }

  public void _Validation_setEnabled(boolean enableValidation) throws MBeanException {
    DiameterMessageValidator.setOn(enableValidation);
  }

  public String dumpStackConfiguration() throws MBeanException {
    return getMutableConfiguration().toString();
  }

  public void startStack() throws MBeanException {
    try {
      this.stack.start(Mode.ALL_PEERS, 20000L, TimeUnit.MILLISECONDS);
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

}
