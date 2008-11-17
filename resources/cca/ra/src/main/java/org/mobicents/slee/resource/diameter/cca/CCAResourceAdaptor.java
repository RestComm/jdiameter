package org.mobicents.slee.resource.diameter.cca;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.slee.Address;
import javax.slee.facilities.EventLookupFacility;
import javax.slee.management.UnrecognizedResourceAdaptorEntityException;
import javax.slee.resource.ActivityHandle;
import javax.slee.resource.BootstrapContext;
import javax.slee.resource.FailureReason;
import javax.slee.resource.Marshaler;
import javax.slee.resource.ResourceAdaptor;
import javax.slee.resource.ResourceAdaptorTypeID;
import javax.slee.resource.ResourceException;
import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;

import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlProvider;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RawSession;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.client.api.ISessionFactory;
import org.mobicents.diameter.stack.DiameterListener;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.mobicents.slee.resource.diameter.base.DiameterActivityContextInterfaceFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterBaseResourceAdaptor;

import org.mobicents.slee.resource.diameter.base.handlers.AccountingSessionFactory;
import org.mobicents.slee.resource.diameter.base.handlers.AuthorizationSessionFactory;

public class CCAResourceAdaptor implements ResourceAdaptor, DiameterListener, CCASessionCreationListener{

	private static final long serialVersionUID = 1L;

	  private static transient Logger logger = Logger.getLogger(DiameterBaseResourceAdaptor.class);

	  private ResourceAdaptorState state;

	  private Stack stack;
	  private SessionFactory sessionFactory = null;
	  private long messageTimeout = 5000;
	  //private DiameterStackMultiplexerProxyMBeanImpl proxy=new DiameterStackMultiplexerProxyMBeanImpl();
	  private ObjectName diameterMultiplexerObjectName = null;
	  private DiameterStackMultiplexerMBean diameterMux=null;
	
	  /**
	   * The BootstrapContext provides the resource adaptor with the required
	   * capabilities in the SLEE to execute its work. The bootstrap context is
	   * implemented by the SLEE. The BootstrapContext object holds references to
	   * a number of objects that are of interest to many resource adaptors. For
	   * further information see JSLEE v1.1 Specification Page 305. The
	   * bootstrapContext will be set in entityCreated() method.
	   */
	  private transient BootstrapContext bootstrapContext = null;

	  /**
	   * The SLEE endpoint defines the contract between the SLEE and the resource
	   * adaptor that enables the resource adaptor to deliver events
	   * asynchronously to SLEE endpoints residing in the SLEE. This contract
	   * serves as a generic contract that allows a wide range of resources to be
	   * plugged into a SLEE environment via the resource adaptor architecture.
	   * For further information see JSLEE v1.1 Specification Page 307 The
	   * sleeEndpoint will be initialized in entityCreated() method.
	   */
	  private transient SleeEndpoint sleeEndpoint = null;

	  /**
	   * the EventLookupFacility is used to look up the event id of incoming
	   * events
	   */
	  private transient EventLookupFacility eventLookup = null;

	  /**
	   * The list of activites stored in this resource adaptor. If this resource
	   * adaptor were a distributed and highly available solution, this storage
	   * were one of the candidates for distribution.
	   */
	  private transient ConcurrentHashMap<ActivityHandle, DiameterActivity> activities = null;
	  
	  protected transient SessionFactory proxySessionFactory=null;
	  protected transient CreditControlAVPFactory localFactory=null;
	  protected transient CreditControlProvider raProvider=null;
	  private static final Map<Integer, String> events;
	 
	  
	  static
	  {
	    Map<Integer, String> eventsTemp = new HashMap<Integer, String>();

	    eventsTemp.put(CreditControlAnswer.commandCode, "CreditControl");
	   
	    
	    // FIXME: baranowb - make sure its compilant with xml
	    //eventsTemp.put(ExtensionDiameterMessage.commandCode, "ExtensionDiameter");
	    
	    events = Collections.unmodifiableMap(eventsTemp);
	    
	   
	  }
	  
	  public CCAResourceAdaptor()
	  {
	    logger.info("Diameter Base RA :: DiameterBaseResourceAdaptor.");
	    
	   
	  }
	  
	  
	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 301 for further information. <br>
	   * The SLEE calls this method to inform the resource adaptor that the SLEE
	   * has completed activity end processing for the activity represented by the
	   * activity handle. The resource adaptor should release any resource related
	   * to this activity as the SLEE will not ask for it again.
	   */
	  public void activityEnded(ActivityHandle handle)
	  {
	    logger.info("Diameter Base RA :: activityEnded :: handle[" + handle + ".");
	    
	    if(this.activities != null)
	    {
	      synchronized (this.activities)
	      {
	        this.activities.remove(handle);
	      }
	    }
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 301 for further information. <br>
	   * The SLEE calls this method to inform the resource adaptor that the
	   * activitys Activity Context object is no longer attached to any SBB
	   * entities and is no longer referenced by any SLEE Facilities. This enables
	   * the resource adaptor to implicitly end the Activity object.
	   */
	  public void activityUnreferenced(ActivityHandle handle)
	  {
	    logger.info("Diameter Base RA :: activityUnreferenced :: handle[" + handle + "].");

	    this.activityEnded(handle);
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor The JSLEE v1.1
	   * Specification does not include entityActivated(). However, the API
	   * description of JSLEE v1.1 does include this method already. So, the
	   * documentation follows the code. <br>
	   * This method is called in context of project Mobicents in context of
	   * resource adaptor activation. More precisely,
	   * org.mobicents.slee.resource.ResourceAdaptorEntity.activate() calls this
	   * method entityActivated(). This method signals the resource adaptor the
	   * transition from state "INACTIVE" to state "ACTIVE".
	   */
	  public void entityActivated() throws ResourceException
	  {
	    logger.info("Diameter Base RA :: entityActivated.");

	    try
	    {
	      logger.info("Activating Diameter Base RA Entity");


	        this.diameterMultiplexerObjectName=new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");
	        
	        Object[] params = new Object[]{};

	          String[] signature = new String[]{};
	          
	          String operation = "getMultiplexerMBean";
	          
	          Object object = SleeContainer.lookupFromJndi().getMBeanServer().invoke( this.diameterMultiplexerObjectName, operation, params, signature );
	          
	          if(object instanceof DiameterStackMultiplexerMBean)
	            this.diameterMux = (DiameterStackMultiplexerMBean) object;
	      
	      
	      
	      this.raProvider = new CCADiameterProvider();

	      initializeNamingContext();

	      this.activities = new ConcurrentHashMap();

	      this.state = ResourceAdaptorState.CONFIGURED;
	    
	      // Initialize the protocol stack
	      initStack();

	      // Resource Adaptor ready to rumble!
	      this.state = ResourceAdaptorState.ACTIVE;
	      this.sessionFactory = this.stack.getSessionFactory();
	      this.accSessionFactory=new AccountingSessionFactory(this,messageTimeout,sessionFactory);
	      this.authSessionFactory=new AuthorizationSessionFactory(this,messageTimeout,sessionFactory);
	      //this.proxySessionFactory=this.sessionFactory;
	      
	      this.proxySessionFactory = new SessionFactory() {

	        public <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException
	        {
	          return (T)sessionFactory.getNewAppSession(applicationId, userSession);
	        }

	        public <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException
	        {
	          return (T)sessionFactory.getNewAppSession(sessionId, applicationId, userSession);
	        }

	        public RawSession getNewRawSession() throws InternalException
	        {
	          try
	          {
	            return stack.getSessionFactory().getNewRawSession();
	          }
	          catch ( IllegalDiameterStateException e )
	          {
	            logger.error( "Failure while obtaining Session Factory for new Raw Session.", e );
	            return null;
	          }
	        }

	        public Session getNewSession() throws InternalException
	        {
	          Session session = sessionFactory.getNewSession();
	          sessionCreated(session);
	          return session;
	        }

	        public Session getNewSession(String sessionId) throws InternalException
	        {
	          Session session=sessionFactory.getNewSession(sessionId);
	          sessionCreated(session);
	          return session;
	        }
	      };
	      
	      // Register Accounting App Session Factories
	      ((ISessionFactory) sessionFactory).registerAppFacory(ServerAccSession.class, accSessionFactory);
	      ((ISessionFactory) sessionFactory).registerAppFacory(ClientAccSession.class, accSessionFactory);

	      // Register Authorization App Session Factories
	      ((ISessionFactory) sessionFactory).registerAppFacory(ServerAuthSession.class, authSessionFactory);
	      ((ISessionFactory) sessionFactory).registerAppFacory(ClientAuthSession.class, authSessionFactory);
	    }
	    catch (Exception e)
	    {
	      logger.error("Error Activating Diameter Base RA Entity", e);
	    }
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 298 for further information. <br>
	   * This method is called by the SLEE when a resource adaptor object instance
	   * is bootstrapped, either when a resource adaptor entity is created or
	   * during SLEE startup. The SLEE implementation will construct the resource
	   * adaptor object and then invoke the entityCreated method before any other
	   * operations can be invoked on the resource adaptor object.
	   */
	  public void entityCreated(BootstrapContext bootstrapContext) throws ResourceException
	  {
	    logger.info("Diameter Base RA :: entityCreated :: bootstrapContext[" + bootstrapContext + "].");

	    this.bootstrapContext = bootstrapContext;
	    this.sleeEndpoint = bootstrapContext.getSleeEndpoint();
	    this.eventLookup = bootstrapContext.getEventLookupFacility();

	    this.state = ResourceAdaptorState.UNCONFIGURED;
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor The JSLEE v1.1
	   * Specification does not include entityDeactivated(). However, the API
	   * description of JSLEE v1.1 does include this method already. So, the
	   * documentation follows the code. <br>
	   * This method is called in context of project Mobicents in context of
	   * resource adaptor deactivation. More precisely,
	   * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate() calls this
	   * method entityDeactivated(). The method call is done AFTER the call to
	   * entityDeactivating(). This method signals the resource adaptor the
	   * transition from state "STOPPING" to state "INACTIVE".
	   */
	  public void entityDeactivated()
	  {
	    logger.info("Diameter Base RA :: entityDeactivated.");

	    logger.info("Diameter Base RA :: Cleaning RA Activities.");

	    synchronized (this.activities)
	    {
	      activities.clear();
	    }
	    activities = null;

	    logger.info("Diameter Base RA :: Cleaning naming context.");

	    try
	    {
	      cleanNamingContext();
	    }
	    catch (NamingException e)
	    {
	      logger.error("Diameter Base RA :: Cannot unbind naming context.");
	    }

	    // Stop the stack
	    //try
	    //{
	    //  stack.stop(5, TimeUnit.SECONDS);
	    //}
	    //catch (Exception e)
	    //{
	    //  logger.error("Diameter Base RA :: Failure while stopping ");
	    //}

	    //proxy.stopService(this.bootstrapContext.getEntityName());
	    
	    logger.info("Diameter Base RA :: RA Stopped.");
	  }

	  /**
	   * This method is called in context of project Mobicents in context of
	   * resource adaptor deactivation. More precisely,
	   * org.mobicents.slee.resource.ResourceAdaptorEntity.deactivate() calls this
	   * method entityDeactivating() PRIOR to invoking entityDeactivated(). This
	   * method signals the resource adaptor the transition from state "ACTIVE" to
	   * state "STOPPING".
	   */
	  public void entityDeactivating()
	  {
	    logger.info("Diameter Base RA :: entityDeactivating.");
	    
	    this.state = ResourceAdaptorState.STOPPING;
	    
	    //try 
	    //{
	    //  Network network = stack.unwrap(Network.class);

	    //  Iterator<ApplicationId> appIdsIt = stack.getMetaData().getLocalPeer().getCommonApplications().iterator();
	    //  
	    //  while(appIdsIt.hasNext())
	    //  {
	    //    network.removeNetworkReqListener(appIdsIt.next());
	        
	        // Update the iterator (avoid ConcurrentModificationException)
	    //    appIdsIt = stack.getMetaData().getLocalPeer().getCommonApplications().iterator();
	    //  }
	    //}
	    //catch (InternalException e) 
	    //{
	    //  logger.error("", e);
	    //}

	    try{
	      diameterMux.unregisterListener(this);
	    }catch (Exception e) 
	    {
	      logger.error("", e);
	    }
	    
	    synchronized (this.activities)
	    {
	      for (ActivityHandle activityHandle : activities.keySet())
	      {
	        try
	        {
	          logger.info("Ending activity [" + activityHandle + "]");

	          activities.get(activityHandle).endActivity();

	        }
	        catch (Exception e)
	        {
	          logger.error("Error Deactivating Activity", e);
	        }
	      }

	    }
	    
	    logger.info("Diameter Base RA :: entityDeactivating completed.");
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 299 for further information. <br>
	   * This method is called by the SLEE when a resource adaptor object instance
	   * is being removed, either when a resource adaptor entity is deleted or
	   * during SLEE shutdown. When receiving this invocation the resource adaptor
	   * object is expected to close any system resources it has allocated.
	   */
	  public void entityRemoved()
	  {
	    // Stop the stack
	    //this.stack.destroy();

	    // Clean up!
	    this.acif = null;
	    this.activities = null;
	    this.bootstrapContext = null;
	    this.eventLookup = null;
	    this.raProvider = null;
	    this.sleeEndpoint = null;
	    this.stack = null;

	    logger.info("Diameter Base RA :: entityRemoved.");
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 300 for further information. <br>
	   * The SLEE calls this method to inform the resource adaptor object that the
	   * specified event was processed unsuccessfully by the SLEE. Event
	   * processing can fail if, for example, the SLEE doesnt have enough
	   * resource to process the event, a SLEE node fails during event processing
	   * or a system level failure prevents the SLEE from committing transactions.
	   */
	  public void eventProcessingFailed(ActivityHandle handle, Object event, int eventID, Address address, int flags, FailureReason reason)
	  {
	    logger.info("Diameter Base RA :: eventProcessingFailed :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + 
	        "], reason[" + reason + "].");
	    

	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 300 for further information. <br>
	   * The SLEE calls this method to inform the resource adaptor object that the
	   * specified event was processed successfully by the SLEE. An event is
	   * considered to be processed successfully if the SLEE has attempted to
	   * deliver the event to all interested SBBs.
	   */
	  public void eventProcessingSuccessful(ActivityHandle handle, Object event, int eventID, Address address, int flags)
	  {
	    logger.info("Diameter Base RA :: eventProcessingSuccessful :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + 
	        flags + "].");


	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 301 for further information. <br>
	   * The SLEE calls this method to get access to the underlying activity for
	   * an activity handle. The resource adaptor is expected to pass back a
	   * non-null object.
	   */
	  public Object getActivity(ActivityHandle handle)
	  {
	    logger.info("Diameter Base RA :: getActivity :: handle[" + handle + "].");

	    return this.activities.get(handle);
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 301 for further information. <br>
	   * The SLEE calls this method to get an activity handle for an activity
	   * created by the underlying resource. This method is invoked by the SLEE
	   * when it needs to construct an activity context for an activity via an
	   * activity context interface factory method invoked by an SBB.
	   */
	  public ActivityHandle getActivityHandle(Object activity)
	  {
	    logger.info("Diameter Base RA :: getActivityHandle :: activity[" + activity + "].");

	    if (!(activity instanceof DiameterActivity))
	      return null;

	    DiameterActivity inActivity = (DiameterActivity) activity;
	    
	    for (Map.Entry<ActivityHandle, DiameterActivity> activityInfo : this.activities.entrySet())
	    {
	      Object curActivity = activityInfo.getValue();
	      
	      if (curActivity.equals(inActivity))
	        return activityInfo.getKey();
	    }

	    return null;
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 302 for further information. <br>
	   * The SLEE calls this method to get reference to the Marshaler object. The
	   * resource adaptor implements the Marshaler interface. The Marshaler is
	   * used by the SLEE to convert between object and distributable forms of
	   * events and event handles.
	   */
	  public Marshaler getMarshaler()
	  {
	    logger.info("Diameter Base RA :: getMarshaler");

	    return null;
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 302 for further information. <br>
	   * The SLEE calls this method to get access to the underlying resource
	   * adaptor interface that enables the SBB to invoke the resource adaptor, to
	   * send messages for example.
	   */
	  public Object getSBBResourceAdaptorInterface(String className)
	  {
	    logger.info("Diameter Base RA :: getSBBResourceAdaptorInterface :: className[" + className + "].");

	    return this.raProvider;
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 301 for further information. <br>
	   * The SLEE calls this method to query if a specific activity belonging to
	   * this resource adaptor object is alive.
	   */
	  public void queryLiveness(ActivityHandle handle)
	  {
	    logger.info("Diameter Base RA :: queryLiveness :: handle[" + handle + "].");

	    DiameterActivityImpl activity = (DiameterActivityImpl) activities.get(handle);

	    if (activity != null && !activity.isValid())
	    {
	      try
	      {
	        sleeEndpoint.activityEnding(handle);
	      }
	      catch (Exception e)
	      {
	        logger.error("", e);
	      }
	    }
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 303 for further information. <br>
	   * The SLEE calls this method to inform the resource adaptor that a service
	   * has been activated and is interested in the event types associated to the
	   * service key. The service must be installed with the resource adaptor via
	   * the serviceInstalled method before it can be activated.
	   */
	  public void serviceActivated(String serviceKey)
	  {
	    logger.info("Diameter Base RA :: serviceActivated :: serviceKey[" + serviceKey + "].");
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 304 for further information. <br>
	   * The SLEE calls this method to inform the SLEE that a service has been
	   * deactivated and is no longer interested in the event types associated to
	   * the service key.
	   */
	  public void serviceDeactivated(String serviceKey)
	  {
	    logger.info("Diameter Base RA :: serviceDeactivated :: serviceKey[" + serviceKey + "].");
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 302 for further information. <br>
	   * The SLEE calls this method to signify to the resource adaptor that a
	   * service has been installed and is interested in a specific set of events.
	   * The SLEE passes an event filter which identifies a set of event types
	   * that services in the SLEE are interested in. The SLEE calls this method
	   * once a service is installed.
	   */
	  public void serviceInstalled(String serviceKey, int[] eventIDs, String[] resourceOptions)
	  {
	    logger.info("Diameter Base RA :: serviceInstalled :: serviceKey[" + serviceKey + "], eventIDs[" + eventIDs + "], resourceOptions[" + resourceOptions + "].");
	  }

	  /**
	   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
	   * Specification Page 303 for further information. <br>
	   * The SLEE calls this method to signify that a service has been
	   * un-installed in the SLEE. The event types associated to the service key
	   * are no longer of interest to a particular application.
	   */
	  public void serviceUninstalled(String serviceKey)
	  {
	    logger.info("Diameter Base RA :: serviceUninstalled :: serviceKey[" + serviceKey + "].");
	  }

	  /**
	   * Set up the JNDI naming context
	   */
	  private void initializeNamingContext() throws NamingException
	  {
	    // get the reference to the SLEE container from JNDI
	    SleeContainer container = SleeContainer.lookupFromJndi();

	    // get the entities name
	    String entityName = bootstrapContext.getEntityName();

	    ResourceAdaptorEntity resourceAdaptorEntity;

	    try
	    {
	      resourceAdaptorEntity = ((ResourceAdaptorEntity) container.getResourceAdaptorEntity(entityName));
	    }
	    catch (UnrecognizedResourceAdaptorEntityException uraee)
	    {
	      throw new NamingException("Failure setting up Naming Context. RA Entity not found.");
	    }

	    ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity.getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();

	    // create the ActivityContextInterfaceFactory
	    acif = new DiameterActivityContextInterfaceFactoryImpl(resourceAdaptorEntity.getServiceContainer(), entityName);

	    // set the ActivityContextInterfaceFactory
	    resourceAdaptorEntity.getServiceContainer().getActivityContextInterfaceFactories().put(raTypeId, acif);

	    try
	    {
	      if (this.acif != null)
	      {
	        // parse the string = java:slee/resources/RAFrameRA/raframeacif
	        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) acif).getJndiName();
	        
	        int begind = jndiName.indexOf(':');
	        int toind = jndiName.lastIndexOf('/');
	        
	        String prefix = jndiName.substring(begind + 1, toind);
	        String name = jndiName.substring(toind + 1);

	        logger.info("Diameter Base RA :: Registering in JNDI :: Prefix[" + prefix + "], Name[" + name + "].");

	        SleeContainer.registerWithJndi(prefix, name, this.acif);
	        
	        logger.info("Diameter Base RA :: Registered in JNDI successfully.");
	      }
	    }
	    catch (IndexOutOfBoundsException iobe)
	    {
	      logger.info("Failure initializing name context.", iobe);
	    }
	  }

	  /**
	   * Clean the JNDI naming context
	   */
	  private void cleanNamingContext() throws NamingException
	  {
	    try
	    {
	      if (this.acif != null)
	      {
	        // parse the string = java:slee/resources/RAFrameRA/raframeacif
	        String jndiName = ((ResourceAdaptorActivityContextInterfaceFactory) this.acif).getJndiName();

	        // remove "java:" prefix
	        int begind = jndiName.indexOf(':');
	        String javaJNDIName = jndiName.substring(begind + 1);

	        logger.info("Diameter Base RA :: Unregistering from JNDI :: Name[" + javaJNDIName + "].");

	        SleeContainer.unregisterWithJndi(javaJNDIName);
	        
	        logger.info("Diameter Base RA :: Unregistered from JNDI successfully.");
	      }
	    }
	    catch (IndexOutOfBoundsException iobe)
	    {
	      logger.error("Failure cleaning name context.", iobe);
	    }
	  }

	  /**
	   * Initializes the RA Diameter Stack.
	   * 
	   * @throws Exception
	   */
	  private synchronized void initStack() throws Exception
	  {
	    //FIXME: Fetch stack
	    // Set message timeout accordingly to stack definition
	    
	    // FIXME: This should come from config.. adding manually
	    // <ApplicationID>
	    // <VendorId value="193"/>
	    // <AuthApplId value="0"/>
	    // <AcctApplId value="19302"/>
	    // </ApplicationID>
	    //appIds.add(ApplicationId.createByAccAppId(193L, 19302L));

	    // <ApplicationID>
	    // <VendorId value="193"/>
	    // <AuthApplId value="19301"/>
	    // <AcctApplId value="0"/>
	    // </ApplicationID>
	    //appIds.add(ApplicationId.createByAuthAppId(193L, 19301L));
	    //DiameterStackMultiplexerProxyMBeanImpl proxy=new DiameterStackMultiplexerProxyMBeanImpl();
	    //proxy.startService(this.bootstrapContext.getEntityName());
	    Set<Integer> codes=events.keySet();
	    long[] command=new long[codes.size()];
	    Iterator<Integer> it=codes.iterator();
	    for(int i=0;i<codes.size();i++)
	    {
	      Integer ii=it.next();
	      command[i]=ii.longValue();
	    }
	    this.diameterMux.registerListener( this, new ApplicationId[]{ApplicationId.createByAccAppId(193L, 19302L),ApplicationId.createByAuthAppId(193L, 19301L)});
	    this.stack=this.diameterMux.getStack();
	    this.messageTimeout = stack.getMetaData().getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());
	    logger.info("Diameter Base RA :: Successfully initialized stack.");
	  }


	  /**
	   * Create the Diameter Activity Handle for an given session id
	   * 
	   * @param sessionId the session identifier to create the activity handle from
	   * @return a DiameterActivityHandle for the provided sessionId
	   */
	  protected DiameterActivityHandle getActivityHandle(String sessionId)
	  {
	    return new DiameterActivityHandle(sessionId);
	  }

	  /**
	   * Method for performing tasks when activity is created, such as informing
	   * SLEE about it and storing into internal map.
	   * 
	   * @param ac
	   *            the activity that has been created
	   */
	  private void activityCreated(DiameterActivity ac)
	  {
	    try
	    {
	      // Inform SLEE that Activity Started
	      DiameterActivityImpl activity = (DiameterActivityImpl) ac;
	      sleeEndpoint.activityStarted(activity.getActivityHandle());

	      // Put it into our activites map
	      activities.put(activity.getActivityHandle(), activity);

	      logger.info("Activity started [" + activity.getActivityHandle() + "]");
	    }
	    catch (Exception e)
	    {
	      logger.error("Error creating activity", e);
	      
	      throw new RuntimeException("Error creating activity", e);
	    }
	  }

	  
	  private class CCADiameterProvider implements CreditControlProvider
	  {

		public CreditControlClientSession createClientSession() {
			// TODO Auto-generated method stub
			return null;
		}

		public CreditControlClientSession createClientSession(
				DiameterIdentityAvp destinationHost,
				DiameterIdentityAvp destinationRealm) {
			// TODO Auto-generated method stub
			return null;
		}

		public CreditControlAVPFactory getCreditControlAVPFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		public CreditControlMessageFactory getCreditControlMessageFactory() {
			// TODO Auto-generated method stub
			return null;
		}
		  
	  }
	  
}
