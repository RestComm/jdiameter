package org.mobicents.slee.resource.diameter.rf;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
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

import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.rf.RfActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.rf.RfClientSession;
import net.java.slee.resource.diameter.rf.RfMessageFactory;
import net.java.slee.resource.diameter.rf.RfProvider;
import net.java.slee.resource.diameter.ro.RoAvpFactory;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.acc.ClientAccSessionImpl;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import org.mobicents.diameter.stack.DiameterListener;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.mobicents.slee.resource.diameter.base.DiameterActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.ErrorAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.handlers.AccountingSessionFactory;
import org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener;
import org.mobicents.slee.resource.diameter.ro.RoAvpFactoryImpl;

/**
 * 
 * RfResourceAdaptor.java
 *
 * <br>Project:  mobicents
 * <br>9:50:28 AM Apr 8, 2009 
 * <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class RfResourceAdaptor implements ResourceAdaptor, DiameterListener, BaseSessionCreationListener {

  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(RfResourceAdaptor.class);

  @SuppressWarnings("unused")
  private ResourceAdaptorState state;

  private Stack stack;
  private SessionFactory sessionFactory = null;

  private DiameterStackMultiplexerMBean diameterMux = null;

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

  // Diameter Base Factories
  private DiameterMessageFactoryImpl baseMessageFactory;
  private DiameterAvpFactoryImpl baseAvpFactory;

  // Rf Specific Factories
  private RfMessageFactoryImpl rfMessageFactory;
  private RoAvpFactoryImpl rfAvpFactory;

  // ACI Factory
  protected RfActivityContextInterfaceFactory acif = null;

  // Provisioning
  private long messageTimeout = 5000;

  private AccountingSessionFactory accSessionFactory;

  private RfProviderImpl raProvider;

  public RfResourceAdaptor()
  {
    logger.info("Diameter Rf RA :: Constructor called.");
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
    logger.info("Diameter Rf RA :: activityEnded :: handle[" + handle + ".");

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
    logger.info("Diameter Rf RA :: activityUnreferenced :: handle[" + handle + "].");

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
    logger.info("Diameter Rf RA :: entityActivated.");

    try
    {
      logger.info("Activating Diameter Rf RA Entity");

      Object[] params = new Object[]{};

      String[] signature = new String[]{};

      String operation = "getMultiplexerMBean";

      ObjectName diameterMultiplexerObjectName = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");

      Object object = SleeContainer.lookupFromJndi().getMBeanServer().invoke( diameterMultiplexerObjectName, operation, params, signature );

      if(object instanceof DiameterStackMultiplexerMBean)
      {
        this.diameterMux = (DiameterStackMultiplexerMBean) object;
      }

      this.raProvider = new RfProviderImpl(this);

      initializeNamingContext();

      this.activities = new ConcurrentHashMap();

      this.state = ResourceAdaptorState.CONFIGURED;

      // Initialize the protocol stack
      initStack();

      // Resource Adaptor ready to rumble!
      this.state = ResourceAdaptorState.ACTIVE;
      this.sessionFactory = this.stack.getSessionFactory();

      this.baseMessageFactory = new DiameterMessageFactoryImpl(stack);
      this.rfMessageFactory = new RfMessageFactoryImpl(this.baseMessageFactory, stack);

      this.baseAvpFactory = new DiameterAvpFactoryImpl();
      this.rfAvpFactory = new RoAvpFactoryImpl(this.baseAvpFactory);

      //      this.proxySessionFactory = new SessionFactory() {
      //
      //        public <T extends AppSession> T getNewAppSession(ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException
      //        {
      //          return (T)sessionFactory.getNewAppSession(applicationId, userSession);
      //        }
      //
      //        public <T extends AppSession> T getNewAppSession(String sessionId, ApplicationId applicationId, Class<? extends AppSession> userSession) throws InternalException
      //        {
      //          return (T)sessionFactory.getNewAppSession(sessionId, applicationId, userSession);
      //        }
      //
      //        public RawSession getNewRawSession() throws InternalException
      //        {
      //          try
      //          {
      //            return stack.getSessionFactory().getNewRawSession();
      //          }
      //          catch ( IllegalDiameterStateException e )
      //          {
      //            logger.error( "Failure while obtaining Session Factory for new Raw Session.", e );
      //            return null;
      //          }
      //        }
      //
      //        public Session getNewSession() throws InternalException
      //        {
      //          Session session = sessionFactory.getNewSession();
      //          sessionCreated(session);
      //          return session;
      //        }
      //
      //        public Session getNewSession(String sessionId) throws InternalException
      //        {
      //          Session session = sessionFactory.getNewSession(sessionId);
      //          sessionCreated(session);
      //          return session;
      //        }
      //      };

      // Register Accounting App Session Factories
      this.sessionFactory = this.stack.getSessionFactory();

      this.accSessionFactory = AccountingSessionFactory.INSTANCE;
      this.accSessionFactory.registerListener(this,messageTimeout,sessionFactory);

      ((ISessionFactory) sessionFactory).registerAppFacory(ServerAccSession.class, accSessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ClientAccSession.class, accSessionFactory);
    }
    catch (Exception e) {
      logger.error("Error Activating Diameter Rf RA Entity", e);
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
    logger.info("Diameter Rf RA :: entityCreated :: bootstrapContext[" + bootstrapContext + "].");

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
    logger.info("Diameter Rf RA :: entityDeactivated.");

    logger.info("Diameter Rf RA :: Cleaning RA Activities.");

    synchronized (this.activities)
    {
      activities.clear();
    }
    activities = null;

    logger.info("Diameter Rf RA :: Cleaning naming context.");

    try
    {
      cleanNamingContext();
    }
    catch (NamingException e)
    {
      logger.error("Diameter Rf RA :: Cannot unbind naming context.");
    }

    logger.info("Diameter Rf RA :: RA Stopped.");
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
    logger.info("Diameter Rf RA :: entityDeactivating.");

    this.state = ResourceAdaptorState.STOPPING;

    try
    {
      diameterMux.unregisterListener(this);
    }
    catch (Exception e) {
      logger.error("Failure while unregistering Rf Resource Adaptor from Mux.", e);
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

    logger.info("Diameter Rf RA :: entityDeactivating completed.");
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
    // Clean up!
    this.acif = null;
    this.activities = null;
    this.bootstrapContext = null;
    this.eventLookup = null;
    this.raProvider = null;
    this.sleeEndpoint = null;
    this.stack = null;

    logger.info("Diameter Rf RA :: entityRemoved.");
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
    logger.info("Diameter Rf RA :: eventProcessingFailed :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + 
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
    logger.info("Diameter Rf RA :: eventProcessingSuccessful :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + 
        flags + "].");

    // FIXME: Alexandre: Check if needed
    //DiameterActivity activity = activities.get(handle);
    //
    //if(activity instanceof RfClientSessionImpl)
    //{
    //  RfClientSessionImpl rfClientActivity = (RfClientSessionImpl) activity;
    //
    //  FIXME: Alexandre: Check if needed to implement this method
    //  if(rfClientActivity..getTerminateAfterAnswer())
    //  {
    //    rfClientActivity.endActivity();
    //  }
    //}
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
    logger.info("Diameter Rf RA :: getActivity :: handle[" + handle + "].");

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
    logger.info("Diameter Rf RA :: getActivityHandle :: activity[" + activity + "].");

    if (!(activity instanceof DiameterActivity))
    {
      logger.warn( "Trying to get activity handle for non-Diameter Activity. Returning null." );
      return null;
    }

    DiameterActivity inActivity = (DiameterActivity) activity;

    for (Map.Entry<ActivityHandle, DiameterActivity> activityInfo : this.activities.entrySet())
    {
      Object curActivity = activityInfo.getValue();

      if (curActivity.equals(inActivity))
      {
        return activityInfo.getKey();
      }
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
    logger.info("Diameter Rf RA :: getMarshaler");

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
    logger.info("Diameter Rf RA :: getSBBResourceAdaptorInterface :: className[" + className + "].");

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
    logger.info("Diameter Rf RA :: queryLiveness :: handle[" + handle + "].");

    DiameterActivityImpl activity = (DiameterActivityImpl) activities.get(handle);

    if (activity != null && !activity.isValid())
    {
      try
      {
        sleeEndpoint.activityEnding(handle);
      }
      catch (Exception e) {
        logger.error("Failure while ending non-live activity.", e);
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
    logger.info("Diameter Rf RA :: serviceActivated :: serviceKey[" + serviceKey + "].");
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
    logger.info("Diameter Rf RA :: serviceDeactivated :: serviceKey[" + serviceKey + "].");
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
    logger.info("Diameter Rf RA :: serviceInstalled :: serviceKey[" + serviceKey + "], eventIDs[" + eventIDs + "], resourceOptions[" + resourceOptions + "].");
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
    logger.info("Diameter Rf RA :: serviceUninstalled :: serviceKey[" + serviceKey + "].");
  }

  // ###################
  // # PRIVATE METHODS #
  // ###################

  /**
   * Set up the JNDI naming context.
   * 
   * @throws NamingException
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
    catch (UnrecognizedResourceAdaptorEntityException uraee) {
      throw new NamingException("Failure setting up Naming Context. RA Entity not found.");
    }

    ResourceAdaptorTypeID raTypeId = resourceAdaptorEntity.getInstalledResourceAdaptor().getRaType().getResourceAdaptorTypeID();

    // create the ActivityContextInterfaceFactory
    acif = new RfActivityContextInterfaceFactoryImpl(resourceAdaptorEntity.getServiceContainer(), entityName);

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

        logger.info("Diameter Rf RA :: Registering in JNDI :: Prefix[" + prefix + "], Name[" + name + "].");

        SleeContainer.registerWithJndi(prefix, name, this.acif);

        logger.info("Diameter Rf RA :: Registered in JNDI successfully.");
      }
    }
    catch (IndexOutOfBoundsException iobe) {
      logger.info("Failure initializing name context.", iobe);
    }
  }

  /**
   * Clean the JNDI naming context.
   * 
   * @throws NamingException
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

        logger.info("Diameter Rf RA :: Unregistering from JNDI :: Name[" + javaJNDIName + "].");

        SleeContainer.unregisterWithJndi(javaJNDIName);

        logger.info("Diameter Rf RA :: Unregistered from JNDI successfully.");
      }
    }
    catch (IndexOutOfBoundsException iobe) {
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
    // Register the RA as a listener to Rf Application (Vendor-Id(0), Acct-App-Id(3))
    this.diameterMux.registerListener(this, new ApplicationId[]{ApplicationId.createByAccAppId(0L, 3L)});
    this.stack = this.diameterMux.getStack();
    this.messageTimeout = this.stack.getMetaData().getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());

    logger.info("Diameter Rf RA :: Successfully initialized stack.");
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
    logger.info( "Diameter Rf RA :: activityCreated :: activity[" + ac + "]" );

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

  /**
   * Create Event object from request/answer
   * 
   * @param request
   *            the request to create the event from, if any.
   * @param answer
   *            the answer to create the event from, if any.
   * @return a DiameterMessage object wrapping the request/answer
   * @throws OperationNotSupportedException
   */
  private DiameterMessage createEvent(Request request, Answer answer) throws OperationNotSupportedException {
		if (request == null && answer == null)
			return null;

		int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());
		if (answer != null && answer.isError()) {
			return new ErrorAnswerImpl(answer);
		}

		// FIXME: baranowb: we might need here more.
		switch (commandCode) {
		case AccountingAnswer.commandCode: // ACR/ACA
			return request != null ? new AccountingRequestImpl(request) : new AccountingAnswerImpl(answer);

		default:
			// throw new
			// OperationNotSupportedException("Not supported message code:" +
			// commandCode + "\n" + (request != null ? request : answer));
			return new ExtensionDiameterMessageImpl(request != null ? request : answer);
		}
	}

  /**
   * Method for firing event to SLEE
   * 
   * @param handle
   *            the handle for the activity where event will be fired on
   * @param name
   *            the unqualified Event name
   * @param request
   *            the request that will be wrapped in the event, if any
   * @param answer
   *            the answer that will be wrapped in the event, if any
   */
  private void fireEvent(ActivityHandle handle, String name, Request request, Answer answer)
  {
    try
    {
      int eventID = eventLookup.getEventID(name, "java.net", "0.8");

      DiameterMessage event = (DiameterMessage) createEvent(request, answer);

      sleeEndpoint.fireEvent(handle, event, eventID, null);
    }
    catch (Exception e)
    {
      logger.warn("Can not send event", e);
    }
  }

  // ######################################
  // ## DIAMETER LISTENER IMPLEMENTATION ##
  // ######################################

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  public Answer processRequest(Request request)
  {
    DiameterActivityImpl activity;

    try
    {
      activity = (DiameterActivityImpl) raProvider.createActivity(request);

      if(activity instanceof RfServerSessionImpl)
      {
        RfServerSessionImpl assai = (RfServerSessionImpl)activity;

        ((ServerAccSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof RfClientSessionImpl)
      {
        RfClientSessionImpl assai = (RfClientSessionImpl)activity;

        ((ClientAccSessionImpl)assai.getSession()).processRequest(request);
      }
    }
    catch (CreateActivityException e) {
      logger.error("", e);
    }

    // returning null so we can answer later
    return null;
  }

  // ####################
  // ## EVENT LISTENER ##
  // ####################

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.api.Message,
   *      org.jdiameter.api.Message)
   */
  public void receivedSuccessMessage(Request req, Answer ans)
  {
    logger.info("Diameter Rf RA :: receivedSuccessMessage :: " + "Request[" + req + "], Answer[" + ans + "].");

    try
    {
      logger.info( "Received Message Result-Code: " + ans.getResultCode().getUnsigned32() );
    }
    catch ( AvpDataException ignore )
    {
      // ignore, this was just for informational purposes...
    }
    // FIXME: alexandre: what should we do here? end activity?
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  public void timeoutExpired(Request req)
  {
    logger.info("Diameter Base RA :: timeoutExpired :: " + "Request[" + req + "].");

    // Message delivery timed out - we have to remove activity
    DiameterActivityHandle ah = new DiameterActivityHandle(req.getSessionId());

    try
    {
      activities.get(ah).endActivity();
    }
    catch (Exception e)
    {
      logger.error("Failure processing timeout message.", e);
    }
  }


  // #############################
  // ## PROVIDER IMPLEMENTATION ##
  // #############################

  private class RfProviderImpl implements RfProvider {

    protected final Logger logger = Logger.getLogger(RfProviderImpl.class);

    protected RfResourceAdaptor ra;
    protected RoAvpFactory avpFactory = null;
    protected DiameterMessageFactory messageFactory = null;

    /**
     * Constructor.
     * 
     * @param rfResourceAdaptor The resource adaptor for this Provider.
     */
    public RfProviderImpl(RfResourceAdaptor rfResourceAdaptor) 
    {
      this.ra = rfResourceAdaptor;
    }

    private DiameterActivity createActivity(Message message) throws CreateActivityException
    {
      String sessionId = message.getSessionId();
      DiameterActivityHandle handle = new DiameterActivityHandle(sessionId);

      if (activities.keySet().contains(handle))
      {
        return activities.get(handle);
      }
      else 
      {
        if (message.isRequest())
        {
          return createRfServerSessionActivity((Request) message);
        }
        else
        {
          AvpSet avps = message.getAvps();
          Avp avp = null;

          DiameterIdentity destinationHost = null;
          DiameterIdentity destinationRealm = null;

          if ((avp = avps.getAvp(Avp.DESTINATION_HOST)) != null)
          {
            try
            {
              destinationHost = new DiameterIdentity(avp.getDiameterIdentity());
            }
            catch (AvpDataException e)
            {
              logger.error("Failed to extract Destination-Host from Message.", e);
            }
          }

          if ((avp = avps.getAvp(Avp.DESTINATION_REALM)) != null)
          {
            try
            {
              destinationRealm = new DiameterIdentity(avp.getDiameterIdentity());
            }
            catch (AvpDataException e)
            {
              logger.error("Failed to extract Destination-Realm from Message.", e);
            }
          }

          return createRfClientSessionActivity( destinationHost, destinationRealm );
        }
      }
    }

    private DiameterActivity createRfServerSessionActivity(Request request) throws CreateActivityException
    {
      ServerAccSession session = null;

      try
      {
        ApplicationId appId = request.getApplicationIdAvps().isEmpty() ? null : request.getApplicationIdAvps().iterator().next(); 
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(request.getSessionId(), appId, ServerAccSession.class, request);

        if (session == null)
        {
          throw new CreateActivityException("Got NULL Session while creating Server Accounting Activity");
        }
      }
      catch (InternalException e) {
        throw new CreateActivityException("Internal exception while creating Server Accounting Activity", e);
      }
      catch (IllegalDiameterStateException e) {
        throw new CreateActivityException("Illegal Diameter State exception while creating Server Accounting Activity", e);
      }

      return (RfServerSessionImpl) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
    }

    // Actual Provider Methods 

    public RfClientSession createRfClientSessionActivity() throws CreateActivityException
    {
      return createRfClientSessionActivity( null, null );
    }

    public RfClientSession createRfClientSessionActivity( DiameterIdentity destinationHost, DiameterIdentity destinationRealm ) throws CreateActivityException
    {
      try
      {
        ClientAccSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAccAppId(0L, 3L), ClientAccSession.class);

        return new RfClientSessionImpl((DiameterMessageFactoryImpl)ra.rfMessageFactory, (DiameterAvpFactoryImpl) rfAvpFactory.getBaseFactory(), session, ra.messageTimeout, destinationHost, destinationRealm, ra.sleeEndpoint, stack);
      }
      catch (Exception e) {
        throw new CreateActivityException("Internal exception while creating Client Accounting Activity", e);
      }
    }

    public RfMessageFactory getRfMessageFactory()
    {
      return ra.rfMessageFactory;
    }

    public AccountingAnswer sendAccountingRequest( AccountingRequest accountingRequest )
    {
      try
      {
        String sessionId = accountingRequest.getSessionId();
        DiameterActivityHandle handle = new DiameterActivityHandle(sessionId);

        if (!activities.keySet().contains(handle))
        {
          createActivity(((DiameterMessageImpl)accountingRequest).getGenericData());
        }

        DiameterActivityImpl activity = (DiameterActivityImpl) getActivity(handle);

        return (AccountingAnswer) activity.sendSyncMessage(accountingRequest);
      }
      catch (Exception e)
      {
        logger.error("Failure sending sync request.", e);
      }

      // FIXME Throw unknown message exception?
      return null;
    }
    
    public DiameterIdentity[] getConnectedPeers()
    {
      return ra.getConnectedPeers();
    }

    public int getPeerCount()
    {
      return ra.getConnectedPeers().length;
    }
    
  }

  /**
   * @return
   */
  	public DiameterIdentity[] getConnectedPeers() {
  		if (this.stack != null) {
  			try {
  				// Get the list of peers from the stack
  				List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();

  				DiameterIdentity[] result = new DiameterIdentity[peers.size()];

  				int i = 0;

  				// Get each peer from the list and make a DiameterIdentity
  				for (Peer peer : peers) {
  					DiameterIdentity identity = new DiameterIdentity(peer.getUri().toString());

  					result[i++] = identity;
  				}

  				return result;
  			} catch (Exception e) {
  				logger.error("Failure getting peer list.", e);
  			}
  		}

  		return new DiameterIdentity[0];
  	}
  // #############################
  // ## BASE SESSION MANAGEMENT ##
  // #############################

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#fireEvent(java.lang.String, java.lang.String, org.jdiameter.api.Request, org.jdiameter.api.Answer)
   */
  public void fireEvent(String sessionId, String name, Request request, Answer answer)
  {
    this.fireEvent(getActivityHandle(sessionId), name, request, answer);
  }  

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.acc.ClientAccSession)
   */
  public void sessionCreated(ClientAccSession session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(stack);

    RfClientSessionImpl activity = new RfClientSessionImpl(msgFactory, baseAvpFactory, session, messageTimeout, null, null, sleeEndpoint, stack);

    activity.setSessionListener(this);
    session.addStateChangeNotification(activity);
    activityCreated(activity);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.acc.ServerAccSession)
   */
  public void sessionCreated(ServerAccSession session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(stack);

    RfServerSessionImpl activity = new RfServerSessionImpl(msgFactory, baseAvpFactory, session, messageTimeout, null, null, sleeEndpoint, stack);

    session.addStateChangeNotification(activity);
    activity.setSessionListener(this);
    activityCreated(activity);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.auth.ServerAuthSession)
   */
  public void sessionCreated(ServerAuthSession session)
  {
    logger.error( "Unexpected Auth Session at Rf Resource Adaptor." );
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.auth.ClientAuthSession)
   */
  public void sessionCreated(ClientAuthSession session)
  {
    logger.error( "Unexpected Auth Session at Rf Resource Adaptor." );
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.Session)
   */
  public void sessionCreated(Session session)
  {
    if(session instanceof ServerAccSession)
      sessionCreated((ServerAccSession)session);
    else if(session instanceof ClientAccSession)
      sessionCreated((ClientAccSession)session);
    else
      logger.error( "Diameter Rf RA :: Unexpected Session [" + session + "]" );
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionDestroyed(java.lang.String, java.lang.Object)
   */
  public void sessionDestroyed(String sessionId, Object appSession)
  {
    try
    {
      this.sleeEndpoint.activityEnding(getActivityHandle(sessionId));
    }
    catch (Exception e) {
      logger.error( "Failure Ending Activity with Session-Id[" + sessionId + "]", e );
    }
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionExists(java.lang.String)
   */
  public boolean sessionExists(String sessionId)
  {
    return this.activities.containsKey(getActivityHandle(sessionId));
  }


  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#getSupportedApplications()
   */
  public ApplicationId[] getSupportedApplications()
  {
    return new ApplicationId[]{ApplicationId.createByAccAppId(0L, 3L)};
  }

}
