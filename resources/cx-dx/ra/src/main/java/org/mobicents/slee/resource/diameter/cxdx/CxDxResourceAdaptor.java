package org.mobicents.slee.resource.diameter.cxdx;

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
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cxdx.CxDxAVPFactory;
import net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cxdx.CxDxClientSession;
import net.java.slee.resource.diameter.cxdx.CxDxMessageFactory;
import net.java.slee.resource.diameter.cxdx.CxDxProvider;
import net.java.slee.resource.diameter.cxdx.CxDxServerSession;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
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
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;
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
import org.mobicents.slee.resource.diameter.base.events.ErrorAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.handlers.AccountingSessionFactory;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.LocationInfoRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.PushProfileRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.RegistrationTerminationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.ServerAssignmentRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.UserAuthorizationAnswerImpl;
import org.mobicents.slee.resource.diameter.cxdx.events.UserAuthorizationRequestImpl;
import org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener;
import org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionFactory;

/**
 *
 * CxDxResourceAdaptor.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxResourceAdaptor implements ResourceAdaptor, DiameterListener , CxDxSessionCreationListener{

  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(CxDxResourceAdaptor.class);

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

  // Cx/Dx Specific Factories
  private CxDxMessageFactory cxdxMessageFactory;
  private CxDxAVPFactory cxdxAvpFactory;

  // ACI Factory
  protected CxDxActivityContextInterfaceFactory acif = null;

  // Provisioning
  private long messageTimeout = 5000;

  private CxDxSessionFactory cxdxSessionFactory;

  private CxDxProviderImpl raProvider;
  
  /**
   * 
   */
  public CxDxResourceAdaptor() {
    logger.debug("Diameter Cx/Dx RA :: Constructor called.");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#activityEnded(javax.slee.resource.ActivityHandle)
   */
  public void activityEnded(ActivityHandle handle) {
    logger.info("Diameter Cx/Dx RA :: activityEnded :: handle[" + handle + ".");

    if(this.activities != null)
    {
      synchronized (this.activities)
      {
        this.activities.remove(handle);
      }
    }
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#activityUnreferenced(javax.slee.resource.ActivityHandle)
   */
  public void activityUnreferenced(ActivityHandle handle) {
    logger.info("Diameter Cx/Dx RA :: activityUnreferenced :: handle[" + handle + "].");

    this.activityEnded(handle);
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#entityActivated()
   */
  public void entityActivated() throws ResourceException {
    logger.info("Diameter Cx/Dx RA :: entityActivated.");

    try
    {
      logger.info("Activating Diameter Cx/Dx RA Entity");

      Object[] params = new Object[]{};

      String[] signature = new String[]{};

      String operation = "getMultiplexerMBean";

      ObjectName diameterMultiplexerObjectName = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");

      Object object = SleeContainer.lookupFromJndi().getMBeanServer().invoke( diameterMultiplexerObjectName, operation, params, signature );

      if(object instanceof DiameterStackMultiplexerMBean) {
        this.diameterMux = (DiameterStackMultiplexerMBean) object;
      }

      this.raProvider = new CxDxProviderImpl(this);

      initializeNamingContext();

      this.activities = new ConcurrentHashMap();

      this.state = ResourceAdaptorState.CONFIGURED;

      // Initialize the protocol stack
      initStack();

      // Resource Adaptor ready to rumble!
      this.state = ResourceAdaptorState.ACTIVE;
      this.sessionFactory = this.stack.getSessionFactory();

      this.baseMessageFactory = new DiameterMessageFactoryImpl(stack);
      this.cxdxMessageFactory = new CxDxMessageFactoryImpl(stack);

      this.baseAvpFactory = new DiameterAvpFactoryImpl();
      this.cxdxAvpFactory = new CxDxAVPFactoryImpl();



      //this.accSessionFactory = AccountingSessionFactory.INSTANCE;
      //this.accSessionFactory.registerListener(this,messageTimeout,sessionFactory);
      
      this.cxdxSessionFactory = new CxDxSessionFactory(this,messageTimeout,sessionFactory);
      //this.cxdxSessionFactory.registerListener(this,messageTimeout,sessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ServerCxDxSession.class, cxdxSessionFactory);
      ((ISessionFactory) sessionFactory).registerAppFacory(ClientCxDxSession.class, cxdxSessionFactory);
    }
    catch (Exception e) {
      logger.error("Error Activating Diameter Cx/Dx RA Entity", e);
    }
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#entityCreated(javax.slee.resource.BootstrapContext)
   */
  public void entityCreated(BootstrapContext bootstrapContext) throws ResourceException {
    logger.info("Diameter Cx/Dx RA :: entityCreated :: bootstrapContext[" + bootstrapContext + "].");

    this.bootstrapContext = bootstrapContext;
    this.sleeEndpoint = bootstrapContext.getSleeEndpoint();
    this.eventLookup = bootstrapContext.getEventLookupFacility();

    this.state = ResourceAdaptorState.UNCONFIGURED;
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#entityDeactivated()
   */
  public void entityDeactivated() {
    logger.info("Diameter Cx/Dx RA :: entityDeactivated.");

    logger.info("Diameter Cx/Dx RA :: Cleaning RA Activities.");

    synchronized (this.activities) {
      activities.clear();
    }
    ((ISessionFactory) sessionFactory).unRegisterAppFacory(ServerCxDxSession.class);
    ((ISessionFactory) sessionFactory).unRegisterAppFacory(ClientCxDxSession.class);
    activities = null;

    logger.info("Diameter Cx/Dx RA :: Cleaning naming context.");

    try {
      cleanNamingContext();
    }
    catch (NamingException e) {
      logger.error("Diameter Cx/Dx RA :: Cannot unbind naming context.");
    }

    logger.info("Diameter Cx/Dx RA :: RA Stopped.");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#entityDeactivating()
   */
  public void entityDeactivating() {
    logger.info("Diameter Cx/Dx RA :: entityDeactivating.");

    this.state = ResourceAdaptorState.STOPPING;

    try {
      diameterMux.unregisterListener(this);
    }
    catch (Exception e) {
      logger.error("Failure while unregistering Cx/Dx Resource Adaptor from Mux.", e);
    }

    synchronized (this.activities) {
      for (ActivityHandle activityHandle : activities.keySet()) {
        try {
          logger.info("Ending activity [" + activityHandle + "]");

          activities.get(activityHandle).endActivity();
        }
        catch (Exception e) {
          logger.error("Error Deactivating Activity", e);
        }
      }
    }

    logger.info("Diameter Cx/Dx RA :: entityDeactivating completed.");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#entityRemoved()
   */
  public void entityRemoved() {
    // Clean up!
    this.acif = null;
    this.activities = null;
    this.bootstrapContext = null;
    this.eventLookup = null;
    this.raProvider = null;
    this.sleeEndpoint = null;
    this.stack = null;

    logger.info("Diameter Cx/Dx RA :: entityRemoved.");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#eventProcessingFailed(javax.slee.resource.ActivityHandle, java.lang.Object, int, javax.slee.Address, int, javax.slee.resource.FailureReason)
   */
  public void eventProcessingFailed(ActivityHandle handle, Object event, int eventID, Address address, int flags, FailureReason reason) {
    logger.info("Diameter Cx/Dx RA :: eventProcessingFailed :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "], reason[" + reason + "].");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#eventProcessingSuccessful(javax.slee.resource.ActivityHandle, java.lang.Object, int, javax.slee.Address, int)
   */
  public void eventProcessingSuccessful(ActivityHandle handle, Object event, int eventID, Address address, int flags) {
    logger.info("Diameter Cx/Dx RA :: eventProcessingSuccessful :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags[" + flags + "].");

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

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#getActivity(javax.slee.resource.ActivityHandle)
   */
  public Object getActivity(ActivityHandle handle) {
    logger.info("Diameter Cx/Dx RA :: getActivity :: handle[" + handle + "].");

    return this.activities.get(handle);
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#getActivityHandle(java.lang.Object)
   */
  public ActivityHandle getActivityHandle(Object activity) {
    logger.info("Diameter Cx/Dx RA :: getActivityHandle :: activity[" + activity + "].");

    if (!(activity instanceof DiameterActivity)) {
      logger.warn( "Trying to get activity handle for non-Diameter Activity. Returning null." );
      return null;
    }

    DiameterActivity inActivity = (DiameterActivity) activity;

    for (Map.Entry<ActivityHandle, DiameterActivity> activityInfo : this.activities.entrySet()) {
      Object curActivity = activityInfo.getValue();

      if (curActivity.equals(inActivity)) {
        return activityInfo.getKey();
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#getMarshaler()
   */
  public Marshaler getMarshaler() {
    logger.info("Diameter Cx/Dx RA :: getMarshaler");

    return null;
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#getSBBResourceAdaptorInterface(java.lang.String)
   */
  public Object getSBBResourceAdaptorInterface(String className) {
    logger.info("Diameter Cx/Dx RA :: getSBBResourceAdaptorInterface :: className[" + className + "].");

    return this.raProvider;
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#queryLiveness(javax.slee.resource.ActivityHandle)
   */
  public void queryLiveness(ActivityHandle handle) {
    logger.info("Diameter Cx/Dx RA :: queryLiveness :: handle[" + handle + "].");

    DiameterActivityImpl activity = (DiameterActivityImpl) activities.get(handle);

    if (activity != null && !activity.isValid()) {
      try {
        sleeEndpoint.activityEnding(handle);
      }
      catch (Exception e) {
        logger.error("Failure while ending non-live activity.", e);
      }
    }
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceActivated(java.lang.String)
   */
  public void serviceActivated(String serviceKey) {
    logger.info("Diameter Cx/Dx RA :: serviceActivated :: serviceKey[" + serviceKey + "].");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceDeactivated(java.lang.String)
   */
  public void serviceDeactivated(String serviceKey) {
    logger.info("Diameter Cx/Dx RA :: serviceDeactivated :: serviceKey[" + serviceKey + "].");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceInstalled(java.lang.String, int[], java.lang.String[])
   */
  public void serviceInstalled(String serviceKey, int[] eventIDs, String[] resourceOptions) {
    logger.info("Diameter Cx/Dx RA :: serviceInstalled :: serviceKey[" + serviceKey + "], eventIDs[" + eventIDs + "], resourceOptions[" + resourceOptions + "].");
  }

  /* (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceUninstalled(java.lang.String)
   */
  public void serviceUninstalled(String serviceKey) {
    logger.info("Diameter Cx/Dx RA :: serviceUninstalled :: serviceKey[" + serviceKey + "].");
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
    acif = new CxDxActivityContextInterfaceFactoryImpl(resourceAdaptorEntity.getServiceContainer(), entityName);

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

        logger.info("Diameter Cx/Dx RA :: Registering in JNDI :: Prefix[" + prefix + "], Name[" + name + "].");

        SleeContainer.registerWithJndi(prefix, name, this.acif);

        logger.info("Diameter Cx/Dx RA :: Registered in JNDI successfully.");
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

        logger.info("Diameter Cx/Dx RA :: Unregistering from JNDI :: Name[" + javaJNDIName + "].");

        SleeContainer.unregisterWithJndi(javaJNDIName);

        logger.info("Diameter Cx/Dx RA :: Unregistered from JNDI successfully.");
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
    // Register the RA as a listener to Cx/Dx Application (Vendor-Id(10415), Acct-App-Id(16777216))
    this.diameterMux.registerListener(this, new ApplicationId[]{ApplicationId.createByAuthAppId(10415L, 16777216L)});
    this.stack = this.diameterMux.getStack();
    this.messageTimeout = this.stack.getMetaData().getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());

    logger.info("Diameter Cx/Dx RA :: Successfully initialized stack.");
  }

  /**
   * Create the Diameter Activity Handle for an given session id
   * 
   * @param sessionId the session identifier to create the activity handle from
   * @return a DiameterActivityHandle for the provided sessionId
   */
  protected DiameterActivityHandle getActivityHandle(String sessionId) {
    return new DiameterActivityHandle(sessionId);
  }

  /**
   * Method for performing tasks when activity is created, such as informing
   * SLEE about it and storing into internal map.
   * 
   * @param ac
   *            the activity that has been created
   */
  private void activityCreated(DiameterActivity ac) {
    logger.info( "Diameter Cx/Dx RA :: activityCreated :: activity[" + ac + "]" );

    try {
      // Inform SLEE that Activity Started
      DiameterActivityImpl activity = (DiameterActivityImpl) ac;
      sleeEndpoint.activityStarted(activity.getActivityHandle());

      // Put it into our activites map
      activities.put(activity.getActivityHandle(), activity);

      logger.info("Activity started [" + activity.getActivityHandle() + "]");
    }
    catch (Exception e) {
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
    if (request == null && answer == null) {
      return null;
    }

    int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());
    if (answer != null && answer.isError()) {
      return new ErrorAnswerImpl(answer);
    }

    // FIXME: baranowb: we might need here more.
    switch (commandCode) {
    case UserAuthorizationRequest.COMMAND_CODE: // UAR/UAA
      return request != null ? new UserAuthorizationRequestImpl(request) : new UserAuthorizationAnswerImpl(answer);
    case ServerAssignmentRequest.COMMAND_CODE: // SAR/SAA
      return request != null ? new ServerAssignmentRequestImpl(request) : new ServerAssignmentAnswerImpl(answer);
    case LocationInfoRequest.COMMAND_CODE: // LIR/LIA
      return request != null ? new LocationInfoRequestImpl(request) : new LocationInfoAnswerImpl(answer);
    case MultimediaAuthenticationRequest.COMMAND_CODE: // MAR/MAA
      return request != null ? new MultimediaAuthenticationRequestImpl(request) : new MultimediaAuthenticationAnswerImpl(answer);
    case RegistrationTerminationRequest.COMMAND_CODE: // RTR/RTA
      return request != null ? new RegistrationTerminationRequestImpl(request) : new RegistrationTerminationAnswerImpl(answer);
    case PushProfileRequest.COMMAND_CODE: // PPR/PPA
      return request != null ? new PushProfileRequestImpl(request) : new PushProfileAnswerImpl(answer);

    default:
      // throw new OperationNotSupportedException("Not supported message code:" + commandCode + "\n" + (request != null ? request : answer));
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
    try {
      int eventID = eventLookup.getEventID(name, "java.net", "0.8");

      DiameterMessage event = (DiameterMessage) createEvent(request, answer);
      //perf looser, bu we need it.
      Object _activity = this.activities.get(handle);
      
      if(_activity != null && _activity instanceof CxDxSessionImpl) {
        CxDxSessionImpl cxdxSession = (CxDxSessionImpl)_activity;
        cxdxSession.fetchSessionData(event);
      }
      sleeEndpoint.fireEvent(handle, event, eventID, null);
    }
    catch (Exception e) {
      logger.warn("Can not send event", e);
    }
  }

  // ######################################
  // ## DIAMETER LISTENER IMPLEMENTATION ##
  // ######################################

  /* (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  public Answer processRequest(Request request) {
    DiameterActivityImpl activity;

    try {
      activity = (DiameterActivityImpl) raProvider.createActivity(request);

      /*if(activity instanceof RfServerSessionImpl) {
        RfServerSessionImpl assai = (RfServerSessionImpl)activity;

        ((ServerAccSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof RfClientSessionImpl) {
        RfClientSessionImpl assai = (RfClientSessionImpl)activity;

        ((ClientAccSession)assai.getSession()).processRequest(request);
      }*/
    }
    catch (CreateActivityException e) {
      logger.error("", e);
    }

    // returning null so we can answer later
    return null;
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.api.Message, org.jdiameter.api.Message)
   */
  public void receivedSuccessMessage(Request request, Answer answer) {
    logger.info("Diameter Cx/Dx RA :: receivedSuccessMessage :: " + "Request[" + request + "], Answer[" + answer + "].");

    try {
      logger.info( "Received Message Result-Code: " + answer.getResultCode().getUnsigned32() );
    }
    catch ( AvpDataException ignore ) {
      // ignore, this was just for informational purposes...
    }
  }

  /* (non-Javadoc)
   * @see org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  public void timeoutExpired(Request request) {
    logger.info("Diameter Cx/Dx RA :: timeoutExpired :: " + "Request[" + request + "].");

    // Message delivery timed out - we have to remove activity
    DiameterActivityHandle ah = new DiameterActivityHandle(request.getSessionId());

    try {
      activities.get(ah).endActivity();
    }
    catch (Exception e) {
      logger.error("Failure processing timeout message.", e);
    }
  }

  // #############################
  // ## PROVIDER IMPLEMENTATION ##
  // #############################

  private class CxDxProviderImpl implements CxDxProvider {

    protected final Logger logger = Logger.getLogger(CxDxProviderImpl.class);

    protected CxDxResourceAdaptor ra;
    protected CxDxAVPFactory avpFactory = null;
    protected DiameterMessageFactory messageFactory = null;

    /**
     * Constructor.
     * 
     * @param cxdxResourceAdaptor The resource adaptor for this Provider.
     */
    public CxDxProviderImpl(CxDxResourceAdaptor cxdxResourceAdaptor) {
      this.ra = cxdxResourceAdaptor;
    }

    private DiameterActivity createActivity(Message message) throws CreateActivityException {
      String sessionId = message.getSessionId();
      DiameterActivityHandle handle = new DiameterActivityHandle(sessionId);

      if (activities.keySet().contains(handle)) {
        return activities.get(handle);
      }
      else {
        if (message.isRequest()) {
          if(message.getCommandCode() == PushProfileRequest.COMMAND_CODE || message.getCommandCode() == RegistrationTerminationRequest.COMMAND_CODE) {
            return createCxDxClientSessionActivity((Request) message);
          }
          else {
            return createCxDxServerSessionActivity((Request) message);
          }
        }
        else {
        	throw new IllegalStateException("Got answer, there should already be activity.");
//          AvpSet avps = message.getAvps();
//          Avp avp = null;
//
//          DiameterIdentity destinationHost = null;
//          DiameterIdentity destinationRealm = null;
//
//          if ((avp = avps.getAvp(Avp.DESTINATION_HOST)) != null) {
//            try {
//              destinationHost = new DiameterIdentity(avp.getDiameterIdentity());
//            }
//            catch (AvpDataException e) {
//              logger.error("Failed to extract Destination-Host from Message.", e);
//            }
//          }
//
//          if ((avp = avps.getAvp(Avp.DESTINATION_REALM)) != null) {
//            try {
//              destinationRealm = new DiameterIdentity(avp.getDiameterIdentity());
//            }
//            catch (AvpDataException e) {
//              logger.error("Failed to extract Destination-Realm from Message.", e);
//            }
//          }
//
//          return createCxDxClientSessionActivity( destinationHost, destinationRealm );
        }
      }
    }

    private DiameterActivity createCxDxServerSessionActivity(Request request) throws CreateActivityException
    {
      ServerCxDxSession session = null;

      try {
        ApplicationId appId = request.getApplicationIdAvps().isEmpty() ? null : request.getApplicationIdAvps().iterator().next(); 
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(request.getSessionId(), appId, ServerCxDxSession.class, request);

        if (session == null) {
          throw new CreateActivityException("Got NULL Session while creating Server Accounting Activity");
        }
      }
      catch (InternalException e) {
        throw new CreateActivityException("Internal exception while creating Server Accounting Activity", e);
      }
      catch (IllegalDiameterStateException e) {
        throw new CreateActivityException("Illegal Diameter State exception while creating Server Accounting Activity", e);
      }

      return (CxDxServerSessionImpl) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
    }

    // Actual Provider Methods 

    public CxDxClientSession createCxDxClientSessionActivity() throws CreateActivityException {
      //return createCxDxClientSessionActivity( null, null );
    	//FIXME: I dony know why YOu push those methods :)
    	return createCxDxClientSessionActivity( null);
    }

    //public CxDxClientSession createCxDxClientSessionActivity( DiameterIdentity destinationHost, DiameterIdentity destinationRealm ) throws CreateActivityException
    public CxDxClientSession createCxDxClientSessionActivity( Request req ) throws CreateActivityException
    {
      try {
    	  String sessionId = req == null? null: req.getSessionId();
        ClientCxDxSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(sessionId, ApplicationId.createByAccAppId(0L, 3L), ClientAccSession.class);

        //return new org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl(ra.cxdxMessageFactory, ra.cxdxAvpFactory, session, (EventListener<Request, Answer>) session, ra.messageTimeout, destinationHost, destinationRealm, ra.sleeEndpoint);
         
        org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl activity = new org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl(ra.cxdxMessageFactory, ra.cxdxAvpFactory, session, (EventListener<Request, Answer>) session, ra.messageTimeout, null, null, ra.sleeEndpoint);
        activityCreated(activity);
        return activity;
      }
      catch (Exception e) {
        throw new CreateActivityException("Internal exception while creating Client Accounting Activity", e);
      }
    }
    /* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cxdx.CxDxProvider#createCxDxClientSessionActivity(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity)
	 */
	public CxDxClientSession createCxDxClientSessionActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException {
		 try {
	    	 
	        ClientCxDxSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAccAppId(0L, 3L), ClientAccSession.class);

	        //return new org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl(ra.cxdxMessageFactory, ra.cxdxAvpFactory, session, (EventListener<Request, Answer>) session, ra.messageTimeout, destinationHost, destinationRealm, ra.sleeEndpoint);
	        org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl activity = new org.mobicents.slee.resource.diameter.cxdx.CxDxClientSessionImpl(ra.cxdxMessageFactory, ra.cxdxAvpFactory, session, (EventListener<Request, Answer>) session, ra.messageTimeout, destinationHost, destinationRealm, ra.sleeEndpoint);
	        activityCreated(activity);
	        return activity;
	      }
	      catch (Exception e) {
	        throw new CreateActivityException("Internal exception while creating Client Accounting Activity", e);
	      }
	}
    public CxDxMessageFactory getCxDxMessageFactory() {
      return ra.cxdxMessageFactory;
    }

    public CxDxAVPFactory getCxDxAVPFactory() {
      return ra.cxdxAvpFactory;
    }

    public DiameterIdentity[] getConnectedPeers() {
      return ra.getConnectedPeers();
    }

    public int getPeerCount() {
      return ra.getConnectedPeers().length;
    }

    public CxDxServerSession createCxDxServerSessionActivity() throws CreateActivityException {
      return createCxDxServerSessionActivity(null, null);
    }

    public CxDxServerSession createCxDxServerSessionActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException {
			try {
				ServerCxDxSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAccAppId(0L, 3L), ClientAccSession.class);
				org.mobicents.slee.resource.diameter.cxdx.CxDxServerSessionImpl activity = new org.mobicents.slee.resource.diameter.cxdx.CxDxServerSessionImpl(ra.cxdxMessageFactory,
						ra.cxdxAvpFactory, session, (EventListener<Request, Answer>) session, ra.messageTimeout, destinationHost, destinationRealm, ra.sleeEndpoint, stack);

				activityCreated(activity);
				return activity;
			} catch (Exception e) {
				throw new CreateActivityException("Internal exception while creating Client Accounting Activity", e);
			}
		}

	}

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
      }
      catch (Exception e) {
        logger.error("Failure getting peer list.", e);
      }
    }

    return new DiameterIdentity[0];
  }

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#fireEvent(java.lang.String, java.lang.String, org.jdiameter.api.Request, org.jdiameter.api.Answer)
 */
public void fireEvent(String sessionId, String name, Request request, Answer answer) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#getSupportedApplications()
 */
public ApplicationId[] getSupportedApplications() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#sessionCreated(org.jdiameter.api.cxdx.ServerCxDxSession)
 */
public void sessionCreated(ServerCxDxSession session) {
	//FIXME: here we should create Activity!!!!!!!!!!!!!
	
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#sessionCreated(org.jdiameter.api.cxdx.ClientCxDxSession)
 */
public void sessionCreated(ClientCxDxSession session) {
	//FIXME: here we should create Activity!!!!!!!!!!!!!
	
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#sessionCreated(org.jdiameter.api.Session)
 */
public void sessionCreated(Session session) {
	//FIXME: here we should create Activity!!!!!!!!!!!!!
	
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#sessionDestroyed(java.lang.String, java.lang.Object)
 */
public void sessionDestroyed(String sessionId, Object appSession) {
	  try
	    {
	      
	      this.sleeEndpoint.activityEnding(getActivityHandle(sessionId));
	    }
	    catch (Exception e) {
	      logger.error( "Failure Ending Activity with Session-Id[" + sessionId + "]", e );
	    }
	
}

/* (non-Javadoc)
 * @see org.mobicents.slee.resource.diameter.cxdx.handlers.CxDxSessionCreationListener#sessionExists(java.lang.String)
 */
public boolean sessionExists(String sessionId) {
	return this.activities.contains(new DiameterActivityHandle(sessionId));
}
  
}
