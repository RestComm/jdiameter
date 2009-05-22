package org.mobicents.diameter.stack;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.system.ServiceMBeanSupport;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationAlreadyUseException;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;
import org.mobicents.diameter.dictionary.AvpDictionary;

public class DiameterStackMultiplexer extends ServiceMBeanSupport implements DiameterStackMultiplexerMBean, DiameterProvider, NetworkReqListener, EventListener<Request, Answer>, DiameterMessageFactory
{
  protected Stack stack = null;

  protected HashMap<DiameterListener, Collection<ApplicationId>> listenerToAppId = new HashMap<DiameterListener, Collection<ApplicationId>>(3);
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
      is = this.getClass().getClassLoader().getResourceAsStream(configFile);

      // Load the configuration
      Configuration config = new XMLConfiguration(is);

      this.stack.init(config);

      Network network = stack.unwrap(Network.class);

      Set<ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

      log.info("Diameter Stack Mux :: Supporting " + appIds.size() + " applications.");

      //network.addNetworkReqListener(this, ApplicationId.createByAccAppId( 193, 19302 ));

      for (ApplicationId appId : appIds)
      {
        log.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        network.addNetworkReqListener(this, appId);
        
        if( appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAcctAppId(), null);
        }
        else if( appId.getAuthAppId() != ApplicationId.UNDEFINED_VALUE )
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
  
  private void stopStack() throws Exception
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

    Set<ApplicationId> appIds = message.getApplicationIdAvps();
    
    if( appIds.size() > 0 )
    {
      for(ApplicationId appId : appIds)
      {
        log.info( "Diameter Stack Mux :: findListener :: AVP AppId [" + appId + "]" );
  
        DiameterListener listener;
        
        Long appIdValue = appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE ? appId.getAcctAppId() : appId.getAuthAppId(); 
        
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
    
    stopStack();
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
      Message message = this.stack.getSessionFactory().getNewRawSession().createMessage( commandCode, ApplicationId.createByAccAppId( applicationId ), new Avp[]{} );
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

  public void registerListener( DiameterListener listener, ApplicationId[] appIds) throws IllegalStateException
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
        ApplicationId appId = appIds[curAppIdIndex];
        log.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        network.addNetworkReqListener(this, appId);
        
        if( appId.getAcctAppId() != ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAcctAppId(), listener);
        }
        else if( appId.getAuthAppId() != ApplicationId.UNDEFINED_VALUE )
        {
          this.appIdToListener.put(appId.getAuthAppId(), listener);
        }
      }

      // And add the listener and it's holder
      Collection<ApplicationId> registeredAppIds = this.listenerToAppId.get( listener );

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
      
      Collection<ApplicationId> appIds = this.listenerToAppId.remove(listener);
      
      if(appIds == null)
      {
        log.warn( "Diameter Stack Mux :: unregisterListener :: Listener has no App-Ids registered. Give up..." );
        
        return;
      }

      Network network = stack.unwrap(Network.class);
      
      for (ApplicationId appId : appIds)
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

}
