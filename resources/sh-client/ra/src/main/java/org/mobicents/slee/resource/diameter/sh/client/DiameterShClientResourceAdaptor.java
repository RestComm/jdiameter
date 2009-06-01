/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.sh.client;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

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
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ClientShSessionListener;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.sh.ShClientSessionImpl;
import org.jdiameter.common.api.app.IAppSessionFactory;
import org.jdiameter.common.api.app.sh.IShMessageFactory;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.jdiameter.common.impl.app.AppRequestEventImpl;
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
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.ErrorAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.ProfileUpdateAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.PushNotificationRequestImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.UserDataAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.client.handlers.ShClientSessionListener;
import org.mobicents.slee.resource.diameter.sh.server.events.ProfileUpdateRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.PushNotificationAnswerImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.UserDataRequestImpl;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>11:08:09 AM May 26, 2009 
 * <br>
 *
 * Mobicents Diameter Sh (Client-side) Resource Adaptor
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DiameterShClientResourceAdaptor implements ResourceAdaptor, DiameterListener , ShClientSessionListener{

  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(DiameterShClientResourceAdaptor.class);

  private Stack stack;
  private SessionFactory sessionFactory = null;
  private long messageTimeout = 5000;
  //private DiameterStackMultiplexerProxyMBeanImpl proxy = null;
  private DiameterStackMultiplexerMBean diameterMux = null;
  private ResourceAdaptorState state;

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

  private static final Map<Integer, String> events;

  static
  {
    Map<Integer, String> eventsTemp = new HashMap<Integer, String>();

    eventsTemp.put(ProfileUpdateRequest.commandCode, "ProfileUpdate");
    eventsTemp.put(PushNotificationAnswer.commandCode, "PushNotification");
    eventsTemp.put(UserDataAnswer.commandCode, "UserData");
    eventsTemp.put(SubscribeNotificationsAnswer.commandCode, "SubscribeNotifications");

    events = Collections.unmodifiableMap(eventsTemp);
  }

  /**
   * A link to the DiameterProvider which then will be exposed to Sbbs
   */
  //private transient DiameterProvider raProvider = null;
  private transient ShClientProviderImpl clientProvider = null;

  /**
   * The list of activites stored in this resource adaptor. If this resource
   * adaptor were a distributed and highly available solution, this storage
   * were one of the candidates for distribution.
   */
  private transient ConcurrentHashMap<ActivityHandle, DiameterActivity> activities = null;

  /**
   * The activity context interface factory defined in
   * DiameterRAActivityContextInterfaceFactoryImpl
   */
  private transient ShClientActivityContextInterfaceFactory acif = null;

  private DiameterAvpFactoryImpl diameterAvpFactory = new DiameterAvpFactoryImpl();

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
    logger.info("Diameter ShClient RA :: activityEnded :: handle[" + handle + ".");

    if (this.activities != null)
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
   * activity’s Activity Context object is no longer attached to any SBB
   * entities and is no longer referenced by any SLEE Facilities. This enables
   * the resource adaptor to implicitly end the Activity object.
   */
  public void activityUnreferenced(ActivityHandle handle)
  {
    logger.info("Diameter ShClient RA :: activityUnreferenced :: handle[" + handle + "].");

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
    logger.info("Diameter ShClient RA :: entityActivated.");

    try {
      logger.info("Activating Diameter ShClient RA Entity");

      initializeNamingContext();

      this.activities = new ConcurrentHashMap();

      this.state = ResourceAdaptorState.CONFIGURED;
    }
    catch (Exception e) {
      logger.error("Error Configuring Diameter ShClient RA Entity", e);
    }

    try
    {
      // Initialize the protocol stack
      ObjectName diameterMultiplexerObjectName = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");
      Object[] params = new Object[]{};
      String[] signature = new String[]{};
      String operation = "getMultiplexerMBean";

      Object object = SleeContainer.lookupFromJndi().getMBeanServer().invoke( diameterMultiplexerObjectName, operation, params, signature );

      if(object instanceof DiameterStackMultiplexerMBean)
      {
        this.diameterMux = (DiameterStackMultiplexerMBean) object;
      }

      initStack();

      // Resource Adaptor ready to rumble!
      this.state = ResourceAdaptorState.ACTIVE;
      this.sessionFactory = this.stack.getSessionFactory();

      ((ISessionFactory) sessionFactory).registerAppFacory(ClientShSession.class, new ShClientSessionFactory(this));
    }
    catch (Exception e) {
      logger.error("Error Activating Diameter ShClient RA Entity", e);
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
    logger.info("Diameter ShClient RA :: entityCreated :: bootstrapContext[" + bootstrapContext + "].");

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
    logger.info("Diameter ShClient RA :: entityDeactivated.");

    logger.info("Diameter ShClient RA :: Cleaning RA Activities.");

    synchronized (this.activities)
    {
      activities.clear();
    }
    activities = null;

    logger.info("Diameter ShClient RA :: Cleaning naming context.");

    try
    {
      cleanNamingContext();
    }
    catch (NamingException e) {
      logger.error("Diameter ShClient RA :: Cannot unbind naming context.", e);
    }

    logger.info("Diameter ShClient RA :: RA Stopped.");
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
    logger.info("Diameter ShClient RA :: entityDeactivating.");

    this.state = ResourceAdaptorState.STOPPING;

    try
    {
      diameterMux.unregisterListener(this);
    }
    catch (Exception e) {
      logger.error("Failed to unregister ShClient RA from Diameter Mux.", e);
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
        catch (Exception e) {
          logger.error("Error Deactivating Activity", e);
        }
      }
    }

    logger.info("Diameter ShClient RA :: entityDeactivating completed.");
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
    //this.raProvider = null;
    this.sleeEndpoint = null;
    this.stack = null;

    logger.info("Diameter ShClient RA :: entityRemoved.");
  }

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
    acif = new ShClientActivityContextInterfaceFactoryImpl(resourceAdaptorEntity.getServiceContainer(), entityName);

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

        logger.info("Diameter ShClient RA :: Registering in JNDI :: Prefix[" + prefix + "], Name[" + name + "].");

        SleeContainer.registerWithJndi(prefix, name, this.acif);

        logger.info("Diameter ShClient RA :: Registered in JNDI successfully.");
      }
    }
    catch (IndexOutOfBoundsException iobe) {
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

        logger.info("Diameter ShClient RA :: Unregistering from JNDI :: Name[" + javaJNDIName + "].");

        SleeContainer.unregisterWithJndi(javaJNDIName);

        logger.info("Diameter ShClient RA :: Unregistered from JNDI successfully.");
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
    // Regsiter in the Mux as a Sh app listener.
    this.diameterMux.registerListener(this, new ApplicationId[]{ApplicationId.createByAuthAppId(10415L, 16777217L)});

    // Get the stack (should not mess with)
    this.stack = this.diameterMux.getStack();
    this.messageTimeout = stack.getMetaData().getConfiguration().getLongValue(MessageTimeOut.ordinal(), (Long) MessageTimeOut.defValue());
    this.clientProvider=new ShClientProviderImpl(this);

    logger.info("Diameter ShClient RA :: Successfully initialized stack.");
  }

  /**
   * implements javax.slee.resource.ResourceAdaptor Please refer to JSLEE v1.1
   * Specification Page 300 for further information. <br>
   * The SLEE calls this method to inform the resource adaptor object that the
   * specified event was processed unsuccessfully by the SLEE. Event
   * processing can fail if, for example, the SLEE doesn’t have enough
   * resource to process the event, a SLEE node fails during event processing
   * or a system level failure prevents the SLEE from committing transactions.
   */
  public void eventProcessingFailed(ActivityHandle handle, Object event, int eventID, Address address, int flags, FailureReason reason)
  {
    logger.info("Diameter ShClient RA :: eventProcessingFailed :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags["
        + flags + "], reason[" + reason + "].");
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
    logger.info("Diameter ShClient RA :: eventProcessingSuccessful :: handle[" + handle + "], event[" + event + "], eventID[" + eventID + "], address[" + address + "], flags["
        + flags + "].");
  }

  public Object getActivity(ActivityHandle activityHandle)
  {
    return this.activities.get(activityHandle);
  }

  public ActivityHandle getActivityHandle(Object activity)
  {
    logger.info("Diameter ShClient RA :: getActivityHandle :: activity[" + activity + "].");

    if (!(activity instanceof DiameterActivity))
    {
      logger.warn( "Diameter ShClient RA :: getActivityHandle :: Invalid Activity passed (not instanceof DiameterActivity)." );
      return null;
    }

    DiameterActivity inActivity = (DiameterActivity) activity;

    for (Entry<ActivityHandle, DiameterActivity> activityInfo : this.activities.entrySet())
    {
      Object curActivity = activityInfo.getValue();

      if (curActivity.equals(inActivity))
        return activityInfo.getKey();
    }

    return null;
  }

  public Marshaler getMarshaler()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Object getSBBResourceAdaptorInterface(String className)
  {
    return this.clientProvider;
  }

  public Answer processRequest(Request request)
  {
    try
    {
      clientProvider.createActivity(request);

      //baranowb: do nothing here, if its valid it should be processed, f not we will get exception
    }
    catch (CreateActivityException e) {
      logger.error("Failed creating activity to process request.", e);
    }

    // returning null so we can answer later
    return null;
  }

  public void receivedSuccessMessage(Request req, Answer ans)
  {
    logger.info("Diameter ShClient RA :: receivedSuccessMessage :: " + "Request[" + req + "], Answer[" + ans + "].");

    try
    {
      logger.info("Received Message Result-Code: " + ans.getResultCode().getUnsigned32());
    }
    catch (AvpDataException ignore) {
      // ignore, this was just for informational purposes...
    }
  }

  public void timeoutExpired(Request req)
  {
    logger.info("Diameter Base RA :: timeoutExpired :: " + "Request[" + req + "].");

    // Message delivery timed out - we have to remove activity
    DiameterActivityHandle ah = new DiameterActivityHandle(req.getSessionId());

    try
    {
      activities.get(ah).endActivity();
    }
    catch (Exception e) {
      logger.error("Failure processing timeout message.", e);
    }
  }

  /**
   * Method for obtaining the Peers the RA is currently conneceted to.
   * 
   * @return an array of DiameterIdentity AVPs representing the peers.
   */
  public DiameterIdentity[] getConnectedPeers()
  {
    if (this.stack != null)
    {
      try
      {
        // Get the list of peers from the stack
        List<Peer> peers = stack.unwrap(PeerTable.class).getPeerTable();

        DiameterIdentity[] result = new DiameterIdentity[peers.size()];

        int i = 0;

        // Get each peer from the list and make a DiameterIdentity
        for (Peer peer : peers)
        {
          DiameterIdentity identity = new DiameterIdentity(peer.getUri().toString());

          result[i++] = identity;
        }

        return result;
      }
      catch (Exception e)
      {
        logger.error("Failure getting peer list.", e);
      }
    }

    return null;
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
  public void fireEvent(ActivityHandle handle, String name, Request request, Answer answer)
  {
    try
    {
      int eventID = eventLookup.getEventID(name, "java.net", "0.8");

      DiameterMessage event = (DiameterMessage) createEvent(request, answer);
      sleeEndpoint.fireEvent(handle, event, eventID, null);
    }
    catch (Exception e) {
      logger.error("Can not send event", e);
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
  public DiameterMessage createEvent(Request request, Answer answer) throws OperationNotSupportedException {
		if (request == null && answer == null) {
			return null;
		}

		int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());
		if (answer != null && answer.isError()) {
			return new ErrorAnswerImpl(answer);
		}

		switch (commandCode) {
		case PushNotificationRequestImpl.commandCode: // PNR/PNA
			return request != null ? new PushNotificationRequestImpl(request) : new PushNotificationAnswerImpl(answer);
		case ProfileUpdateRequestImpl.commandCode: // PUR/PUA
			return request != null ? new ProfileUpdateRequestImpl(request) : new ProfileUpdateAnswerImpl(answer);
		case SubscribeNotificationsRequestImpl.commandCode: // SNR/SNA
			return request != null ? new SubscribeNotificationsRequestImpl(request) : new SubscribeNotificationsAnswerImpl(answer);
		case net.java.slee.resource.diameter.sh.server.events.UserDataRequest.commandCode: // UDR/UDA
			return request != null ? new UserDataRequestImpl(request) : new UserDataAnswerImpl(answer);

		default:
			return new ExtensionDiameterMessageImpl(request != null ? request : answer);
		}
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
    catch (Exception e) {
      logger.error("Error creating/starting activity.", e);
    }
  }

  private class ShClientSessionFactory implements IAppSessionFactory, ClientShSessionListener, StateChangeListener, IShMessageFactory
  {
    DiameterShClientResourceAdaptor ra = null;

    public ShClientSessionFactory(DiameterShClientResourceAdaptor ra)
    {
      super();
      this.ra = ra;
    }

    public AppSession getNewSession(String sessionId, Class<? extends AppSession> aClass, ApplicationId applicationId, Object[] args)
    {
      try
      {
        if (aClass == ClientShSession.class)
        {

          ShClientSessionImpl clientSession=null;
          if(args!=null && args.length>1 && args[0] instanceof Request)
          {
            Request request = (Request) args[0];
            clientSession=new ShClientSessionImpl(request.getSessionId(),this,sessionFactory,this);

          }
          else
          {
            clientSession=new ShClientSessionImpl(null,this,sessionFactory,this);
          }

          clientSession.addStateChangeNotification(this);
          return clientSession;
        }
        else
        {
          throw new IllegalArgumentException("Wrong session class!!["+aClass+"]. Supported["+ClientShSession.class+"]");
        }

      }
      catch (Exception e) {
        logger.error("Failure to obtain new Accounting Session.", e);
      }

      return null;
    }

    public void doOtherEvent(AppSession appSession, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
    {
      logger.info("Diameter ShClient RA :: doOtherEvent :: appSession[" + appSession + "], Request[" + request + "], Answer[" + answer + "]");

      DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

      if (answer != null)
      {
    	  if(answer.getMessage().isError())
    		  this.ra.fireEvent(handle, _ErrorAnswer, null, (Answer) answer.getMessage());
    	  else
    		  this.ra.fireEvent(handle, _ExtensionDiameterMessage, null, (Answer) answer.getMessage());
      }
      else
      {
    	  this.ra.fireEvent(handle, _ExtensionDiameterMessage, (Request) request.getMessage(), null);
      }
    }

    public void doProfileUpdateAnswerEvent(ClientShSession appSession, org.jdiameter.api.sh.events.ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
    {
      logger.info("doProfileUpdateAnswerEvent :: appSession[" + appSession + "], request[" + request + "], answer[" + answer + "]");

      DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

      if(answer.getMessage().isError())
		  this.ra.fireEvent(handle, _ErrorAnswer, null, (Answer) answer.getMessage());
	  else
		  this.ra.fireEvent(handle, _ProfileUpdateAnswer, null, (Answer) answer.getMessage());
    }

    public void doPushNotificationRequestEvent(ClientShSession appSession, PushNotificationRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
    {
      logger.info("doPushNotificationRequestEvent :: appSession[" + appSession + "], request[" + request + "], answer[" + null + "]");

      DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

      this.ra.fireEvent(handle, _PushNotificationRequest, (Request) request.getMessage(), null);
    }

    public void doSubscribeNotificationsAnswerEvent(ClientShSession appSession, SubscribeNotificationsRequest request, org.jdiameter.api.sh.events.SubscribeNotificationsAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
    {
      logger.info("doSubscribeNotificationsAnswerEvent :: appSession[" + appSession + "], request[" + request + "], answer[" + answer + "]");

      DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

      if(answer.getMessage().isError())
		  this.ra.fireEvent(handle, _ErrorAnswer, null, (Answer) answer.getMessage());
	  else
		  this.ra.fireEvent(handle, _SubscribeNotificationsAnswer, null, (Answer) answer.getMessage());
    }

    public void doUserDataAnswerEvent(ClientShSession appSession, UserDataRequest request, org.jdiameter.api.sh.events.UserDataAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException
    {
      logger.info("doUserDataAnswerEvent :: appSession[" + appSession + "], request[" + request + "], answer[" + answer + "]");

      DiameterActivityHandle handle = new DiameterActivityHandle(appSession.getSessions().get(0).getSessionId());

      if(answer.getMessage().isError())
		  this.ra.fireEvent(handle, _ErrorAnswer, null, (Answer) answer.getMessage());
	  else
		  this.ra.fireEvent(handle, _UserDataAnswer, null, (Answer) answer.getMessage());
    }

    public void stateChanged(Enum oldState, Enum newState)
    {
      logger.info("Diameter Sh ClientSessionFactory :: stateChanged :: oldState[" + oldState + "], newState[" + newState + "]");
    }

    public AppAnswerEvent createProfileUpdateAnswer(Answer answer)
    {
      return new AppAnswerEventImpl(answer);
    }

    public AppRequestEvent createProfileUpdateRequest(Request request)
    {
      return new AppRequestEventImpl(request);
    }

    public AppAnswerEvent createPushNotificationAnswer(Answer answer)
    {
      return new AppAnswerEventImpl(answer);
    }

    public AppRequestEvent createPushNotificationRequest(Request request)
    {
      return new AppRequestEventImpl(request);
    }

    public AppAnswerEvent createSubscribeNotificationsAnswer(Answer answer)
    {
      return new AppAnswerEventImpl(answer);
    }

    public AppRequestEvent createSubscribeNotificationsRequest(Request request)
    {
      return new AppRequestEventImpl(request);
    }

    public AppAnswerEvent createUserDataAnswer(Answer answer)
    {
      return new AppAnswerEventImpl(answer);
    }

    public AppRequestEvent createUserDataRequest(Request request)
    {
      return new AppRequestEventImpl(request);
    }

    public long getApplicationId()
    {
      return ShClientMessageFactory._SH_APP_ID;
    }

    public long getMessageTimeout()
    {
      return this.ra.messageTimeout;
    }
  }

  private class ShClientProviderImpl implements ShClientProvider
  {
    DiameterShClientResourceAdaptor ra = null;

    public ShClientProviderImpl(DiameterShClientResourceAdaptor ra)
    {
      super();
      this.ra = ra;
    }

    /**
     * This method is for internal use only, it creates activities for
     * requests that do not fall in certain app range or no activitis were
     * found <br>
     * It should distinguish between initial requests, requests with
     * diferent domains etc. - respo for createing XXXServerSession or basic
     * diameter activity lies in this method
     * 
     * @param message
     * @return
     */
    DiameterActivity createActivity(Message message) throws CreateActivityException
    {
      String sessionId = message.getSessionId();
      DiameterActivityHandle handle = new DiameterActivityHandle(sessionId);

      if (activities.keySet().contains(handle))
      {
        return activities.get(handle);
      }
      else
      {
        //FIXME: baranowb: here we can receive only (valid) PNR, other message are errors?
        if(message.getCommandCode() != PushNotificationRequestImpl.commandCode)
        {
          throw new CreateActivityException("Cant create activity for unexpected message:\r\n" + message);
        }

        ShClientSubscriptionActivityImpl activity = (ShClientSubscriptionActivityImpl) this.createShClientSubscriptionActivity(new PushNotificationRequestImpl( message));

        return activity;
      }
    }

    private ShClientSubscriptionActivity createShClientSubscriptionActivity(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest pushNotificationRequest)
    {

      ClientShSession session = null;

      try
      {
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, null, ClientShSession.class, null);

        if (session == null)
        {
          logger.error("Failure creating Sh-Client Subscription Session (null).");
          return null;
        }
      }
      catch (Exception e)
      {
        logger.error("Failure creating Sh-Client Subscription Session.", e);
        return null;
      }

      DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);

      ShClientSubscriptionActivityImpl activity = new ShClientSubscriptionActivityImpl(msgFactory, new ShClientMessageFactoryImpl(session.getSessions().get(0), stack), diameterAvpFactory, new DiameterShAvpFactoryImpl(diameterAvpFactory,stack), session, messageTimeout, null, null, sleeEndpoint);
      activity.fetchSubscriptionData(pushNotificationRequest);
      activity.setSessionListener(ra);
      activityCreated(activity);

      //FIXME: baranowb: this is akward, jdiam has weird api
      //This is trick to trigger fire and state machine
      ((ShClientSessionImpl)session).processRequest((Request) ((DiameterMessageImpl)pushNotificationRequest).getGenericData());

      return activity;
    }

    public ShClientActivity createShClientActivity() throws CreateActivityException
    {
      ClientShSession session = null;

      try
      {
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, null, ClientShSession.class, null);

        if (session == null)
        {
          logger.error("Failure creating Sh-Client Session (null).");
          return null;
        }
      }
      catch (Exception e)
      {
        logger.error("Failure creating Sh-Client Session.", e);
        return null;
      }

      DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);

      ShClientActivityImpl activity = new ShClientActivityImpl(msgFactory, new ShClientMessageFactoryImpl(session.getSessions().get(0), stack), diameterAvpFactory, new DiameterShAvpFactoryImpl(diameterAvpFactory,stack), session, messageTimeout, null, null, sleeEndpoint);;

      activity.setSessionListener(ra);
      activityCreated(activity);

      return activity;
    }

    public ShClientMessageFactory getClientMessageFactory()
    {
      return new ShClientMessageFactoryImpl(stack);
    }

    public net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer profileUpdateRequest(ProfileUpdateRequest message) throws IOException {
			// This is sync, we dont care about activities or FSM, someone else
			// should care...
			if (message == null)
				throw new IOException("Cant send null message");
			try {

				String sessionID = message.getSessionId();
				if (sessionID == null) {
					throw new IllegalArgumentException("Session Id must not be null.");
				}
				Session session = stack.getSessionFactory().getNewSession(sessionID);
				Future<Message> f = session.send(((DiameterMessageImpl) message).getGenericData());
				return new ProfileUpdateAnswerImpl(f.get());
			} catch (AvpNotAllowedException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Failed to send due to: " + e);
			}

		}

		public SubscribeNotificationsAnswer subscribeNotificationsRequest(net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest message) throws IOException {
			// This is sync, we dont care about activities or FSM, someone else
			// should care...
			if (message == null)
				throw new IOException("Cant send null message");
			try {

				String sessionID = message.getSessionId();
				if (sessionID == null) {
					throw new IllegalArgumentException("Session Id must not be null.");
				}
				Session session = stack.getSessionFactory().getNewSession(sessionID);
				Future<Message> f = session.send(((DiameterMessageImpl) message).getGenericData());
				return new SubscribeNotificationsAnswerImpl(f.get());
			} catch (AvpNotAllowedException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Failed to send due to: " + e);
			}
		}

		public UserDataAnswer userDataRequest(net.java.slee.resource.diameter.sh.server.events.UserDataRequest message) throws IOException {
			// This is sync, we dont care about activities or FSM, someone else
			// should care...
			if (message == null)
				throw new IOException("Cant send null message");
			try {

				String sessionID = message.getSessionId();
				if (sessionID == null) {
					throw new IllegalArgumentException("Session Id must not be null.");
				}
				Session session = stack.getSessionFactory().getNewSession(sessionID);
				Future<Message> f = session.send(((DiameterMessageImpl) message).getGenericData());

				return new UserDataAnswerImpl(f.get());
			} catch (AvpNotAllowedException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Failed to send due to: " + e);
			}
		}

    public ShClientSubscriptionActivity createShClientSubscriptionActivity() throws CreateActivityException
    {
      ClientShSession session = null;

      try
      {
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, null, ClientShSession.class, null);

        if (session == null)
        {
          logger.error("Failure creating Sh-Client Subscription Session (null).");
          return null;
        }
      }
      catch (Exception e)
      {
        logger.error("Failure creating Sh-Client Subscription Session.", e);
        return null;
      }

      DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);

      ShClientSubscriptionActivityImpl activity = new ShClientSubscriptionActivityImpl(msgFactory, new ShClientMessageFactoryImpl(session.getSessions().get(0), stack), diameterAvpFactory, new DiameterShAvpFactoryImpl(diameterAvpFactory,stack), session, messageTimeout, null, null, sleeEndpoint);
      activity.setSessionListener(ra);
      activityCreated(activity);

      return activity;
    }

    public DiameterShAvpFactory getClientAvpFactory()
    {
      return new DiameterShAvpFactoryImpl(diameterAvpFactory,stack);
    }

    public DiameterIdentity[] getConnectedPeers()
    {
      return this.ra.getConnectedPeers();
    }

    public int getPeerCount()
    {
      return getConnectedPeers().length;
    }
  }

  // ################################
  // # SERVICE FILTERING            #
  // ################################

  /*
   * (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#queryLiveness(javax.slee.resource.ActivityHandle)
   */
  public void queryLiveness(ActivityHandle handle)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceActivated(java.lang.String)
   */
  public void serviceActivated(String serviceKey)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceDeactivated(java.lang.String)
   */
  public void serviceDeactivated(String serviceKey)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceInstalled(java.lang.String, int[], java.lang.String[])
   */
  public void serviceInstalled(String serviceKey, int[] eventIDs, String[] resourceOptions)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see javax.slee.resource.ResourceAdaptor#serviceUninstalled(java.lang.String)
   */
  public void serviceUninstalled(String serviceKey)
  {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.sh.client.handlers.ShClientSessionListener#sessionDestroyed(java.lang.String, org.jdiameter.api.sh.ClientShSession)
   */
  public void sessionDestroyed(String sessionId,ClientShSession session)
  {
    try
    {
      this.sleeEndpoint.activityEnding(getActivityHandle(sessionId));
    }
    catch (Exception e) {
      logger.error( "Failed to end activity with handle[" + getActivityHandle(sessionId) );
    }
  }

  protected DiameterActivityHandle getActivityHandle(String sessionId)
  {
    return new DiameterActivityHandle(sessionId);
  }

}
