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
package org.mobicents.slee.resource.diameter.base;

import static org.jdiameter.client.impl.helpers.Parameters.MessageTimeOut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import net.java.slee.resource.diameter.base.AccountingClientSessionActivity;
import net.java.slee.resource.diameter.base.AccountingServerSessionActivity;
import net.java.slee.resource.diameter.base.AuthClientSessionActivity;
import net.java.slee.resource.diameter.base.AuthServerSessionActivity;
import net.java.slee.resource.diameter.base.CreateActivityException;
import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.DiameterActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.DiameterProvider;
import net.java.slee.resource.diameter.base.events.AbortSessionAnswer;
import net.java.slee.resource.diameter.base.events.AccountingAnswer;
import net.java.slee.resource.diameter.base.events.CapabilitiesExchangeAnswer;
import net.java.slee.resource.diameter.base.events.DeviceWatchdogAnswer;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.DisconnectPeerAnswer;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.ReAuthAnswer;
import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;

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
import org.jdiameter.api.RawSession;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.acc.ClientAccSession;
import org.jdiameter.api.acc.ServerAccSession;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.ClientAuthSession;
import org.jdiameter.api.auth.ServerAuthSession;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.impl.app.acc.ClientAccSessionImpl;
import org.jdiameter.client.impl.app.auth.ClientAuthSessionImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.jdiameter.server.impl.app.acc.ServerAccSessionImpl;
import org.jdiameter.server.impl.app.auth.ServerAuthSessionImpl;
import org.mobicents.diameter.stack.DiameterListener;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;
import org.mobicents.slee.resource.ResourceAdaptorEntity;
import org.mobicents.slee.resource.ResourceAdaptorState;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AbortSessionRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.AccountingRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.CapabilitiesExchangeRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DeviceWatchdogRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.DisconnectPeerRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.ErrorAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.ReAuthRequestImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationAnswerImpl;
import org.mobicents.slee.resource.diameter.base.events.SessionTerminationRequestImpl;
import org.mobicents.slee.resource.diameter.base.handlers.AccountingSessionFactory;
import org.mobicents.slee.resource.diameter.base.handlers.AuthorizationSessionFactory;
import org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener;

/**
 * Diameter Base Resource Adaptor
 * 
 * <br>
 * Super project: mobicents <br>
 * 1:20:00 AM May 9, 2008 <br>
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Erick Svenson
 */
public class DiameterBaseResourceAdaptor implements ResourceAdaptor, DiameterListener, BaseSessionCreationListener {
  
  private static final long serialVersionUID = 1L;

  private static transient Logger logger = Logger.getLogger(DiameterBaseResourceAdaptor.class);

  private ResourceAdaptorState state;

  private Stack stack;
  private SessionFactory sessionFactory = null;
  private long messageTimeout = 5000;
  //private DiameterStackMultiplexerProxyMBeanImpl proxy=new DiameterStackMultiplexerProxyMBeanImpl();
  private ObjectName diameterMultiplexerObjectName = null;
  private DiameterStackMultiplexerMBean diameterMux=null;
  private DiameterAvpFactoryImpl diameterAvpFactory = new DiameterAvpFactoryImpl();
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

  /**
   * The activity context interface factory defined in
   * DiameterRAActivityContextInterfaceFactoryImpl
   */
  private transient DiameterActivityContextInterfaceFactory acif = null;

  /**
   * A link to the DiameterProvider which then will be exposed to Sbbs
   */
  private transient DiameterProviderImpl raProvider = null;

  protected transient AuthorizationSessionFactory authSessionFactory=null;
  protected transient AccountingSessionFactory accSessionFactory=null;
  protected transient SessionFactory proxySessionFactory=null;
  
  /**
   * Holds code to name
   */
  private static final Map<Integer, String> events;
  private static HashSet<Integer> accEventCodes = new HashSet<Integer>();
  private static HashSet<Integer> authEventCodes = new HashSet<Integer>();
  
  //private static final Map<String,Integer> evenName2EventID = new HashMap<String, Integer>();
  
  static
  {
    Map<Integer, String> eventsTemp = new HashMap<Integer, String>();

    eventsTemp.put(AbortSessionAnswer.commandCode, "AbortSession");
    eventsTemp.put(AccountingAnswer.commandCode, "Accounting");
    eventsTemp.put(CapabilitiesExchangeAnswer.commandCode, "CapabilitiesExchange");
    eventsTemp.put(DeviceWatchdogAnswer.commandCode, "DeviceWatchdog");
    eventsTemp.put(DisconnectPeerAnswer.commandCode, "DisconnectPeer");
    eventsTemp.put(ReAuthAnswer.commandCode, "ReAuth");
    eventsTemp.put(SessionTerminationAnswer.commandCode, "SessionTermination");
    eventsTemp.put(ErrorAnswer.commandCode, "Error");
    
    // FIXME: baranowb - make sure its compilant with xml
    //eventsTemp.put(ExtensionDiameterMessage.commandCode, "ExtensionDiameter");
    
    events = Collections.unmodifiableMap(eventsTemp);
    
    authEventCodes.add(AbortSessionAnswer.commandCode);
    authEventCodes.add(ReAuthAnswer.commandCode);
    authEventCodes.add(SessionTerminationAnswer.commandCode);
    
    accEventCodes.add(AccountingAnswer.commandCode);
  }

  
  
  public DiameterBaseResourceAdaptor()
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
      
      
      
      this.raProvider = new DiameterProviderImpl(this);

      initializeNamingContext();

      this.activities = new ConcurrentHashMap();

      this.state = ResourceAdaptorState.CONFIGURED;
    
      // Initialize the protocol stack
      initStack();

      // Resource Adaptor ready to rumble!
      this.state = ResourceAdaptorState.ACTIVE;
      this.sessionFactory = this.stack.getSessionFactory();
      this.accSessionFactory = AccountingSessionFactory.INSTANCE;
      this.accSessionFactory.registerListener(this, messageTimeout, sessionFactory);
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
    
    
    //this.diameterMux.registerListener( this, new ApplicationId[]{ApplicationId.createByAccAppId(193L, 19302L),ApplicationId.createByAuthAppId(193L, 19301L)});
    List<ApplicationId> appIds = new ArrayList<ApplicationId>();
    for(int index = 0;index<acctAppIds.length;index++)
    {
    	appIds.add(ApplicationId.createByAccAppId(acctVendorIds[index], acctAppIds[index]));
    	
    }
    for(int index = 0;index<authAppIds.length;index++)
    {
    	appIds.add(ApplicationId.createByAuthAppId(authVendorIds[index], authAppIds[index]));
    	
    }
    
    
    this.diameterMux.registerListener( this, appIds.toArray(new ApplicationId[appIds.size()]));
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
  public DiameterMessage createEvent(Request request, Answer answer) throws OperationNotSupportedException
  {
    if (request == null && answer == null)
      return null;

    int commandCode = (request != null ? request.getCommandCode() : answer.getCommandCode());

	if (answer != null && answer.isError()) {
		return new ErrorAnswerImpl(answer);
	}
    
    switch (commandCode) {
		case AbortSessionAnswer.commandCode: // ASR/ASA
			return request != null ? new AbortSessionRequestImpl(request) : new AbortSessionAnswerImpl(answer);
		case AccountingAnswer.commandCode: // ACR/ACA
			return request != null ? new AccountingRequestImpl(request) : new AccountingAnswerImpl(answer);
		case CapabilitiesExchangeAnswer.commandCode: // CER/CEA
			return request != null ? new CapabilitiesExchangeRequestImpl(request) : new CapabilitiesExchangeAnswerImpl(answer);
		case DeviceWatchdogAnswer.commandCode: // DWR/DWA
			return request != null ? new DeviceWatchdogRequestImpl(request) : new DeviceWatchdogAnswerImpl(answer);
		case DisconnectPeerAnswer.commandCode: // DPR/DPA
			return request != null ? new DisconnectPeerRequestImpl(request) : new DisconnectPeerAnswerImpl(answer);
		case ReAuthAnswer.commandCode: // RAR/RAA
			return request != null ? new ReAuthRequestImpl(request) : new ReAuthAnswerImpl(answer);
		case SessionTerminationAnswer.commandCode: // STR/STA
			return request != null ? new SessionTerminationRequestImpl(request) : new SessionTerminationAnswerImpl(answer);
		default:
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
	  //FIXME: add even lookup cache.
    try
    {
    	int eventID = -1;

      DiameterMessage event = (DiameterMessage) createEvent(request, answer);
      if(event instanceof ErrorAnswer)
      {
    	  eventID = eventLookup.getEventID("net.java.slee.resource.diameter.base.events.ErrorAnswer", "java.net", "0.8");
      }
      else
      {
    	  eventID = eventLookup.getEventID(name, "java.net", "0.8");
      }
      sleeEndpoint.fireEvent(handle, event, eventID, null);
    }
    catch (Exception e)
    {
      logger.warn("Can not send event", e);
    }
  }

  
  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#fireEvent(java.lang.String, java.lang.String, org.jdiameter.api.Request, org.jdiameter.api.Answer)
   */
  public void fireEvent(String sessionId, String name, Request request, Answer answer)
  {
    this.fireEvent(getActivityHandle(sessionId), name, request, answer);
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

  // ##########################
  // ## NETWORK REQ LISTENER ##
  // ##########################
  
  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  public Answer processRequest(Request request)
  {
    
    //String sessionId = request.getSessionId();
    //DiameterActivityHandle handle = getActivityHandle(sessionId);
    
    DiameterActivityImpl activity;

    try
    {
      activity = (DiameterActivityImpl) raProvider.createActivity(request);
      
      // Here we have either created activity or got old one, In cass of app activities, 
      // if we are here it means its initial, or something is wrong - stack is not firing
      // events into correct listener?
      //
      // If its a base - we have to fire manually
      
      if(activity instanceof AuthServerSessionActivityImpl)
      {
        AuthServerSessionActivityImpl assai = (AuthServerSessionActivityImpl)activity;
        
        ((ServerAuthSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof AuthClientSessionActivityImpl)
      {
        AuthClientSessionActivityImpl assai=(AuthClientSessionActivityImpl)activity;
        
        ((ClientAuthSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof AccountingServerSessionActivityImpl)
      {
        AccountingServerSessionActivityImpl assai=(AccountingServerSessionActivityImpl)activity;
        
        ((ServerAccSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof AccountingClientSessionActivity)
      {
        AccountingClientSessionActivityImpl assai=(AccountingClientSessionActivityImpl)activity;
        
        ((ClientAccSessionImpl)assai.getSession()).processRequest(request);
      }
      else if(activity instanceof DiameterActivityImpl)
      {
        fireEvent(activity.getActivityHandle(),_ExtensionDiameterMessage, request, null);
      }
      else
      {
        //FIXME: Error?
      }
      
    }
    catch (CreateActivityException e)
    {
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
    logger.info("Diameter Base RA :: receivedSuccessMessage :: " + "Request[" + req + "], Answer[" + ans + "].");

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

  private class DiameterProviderImpl implements DiameterProvider {

    protected final Logger logger = Logger.getLogger(DiameterProviderImpl.class);

    protected DiameterBaseResourceAdaptor ra;
    protected DiameterAvpFactory avpFactory = null;
    protected DiameterMessageFactory messageFactory = null;

    /**
     * Constructor.
     * 
     * @param ra The resource adaptor for this Provider.
     */
    public DiameterProviderImpl(DiameterBaseResourceAdaptor ra) 
    {
      this.ra = ra;
    }

    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createActivity()
     */
    public DiameterActivity createActivity() throws CreateActivityException
    {
      logger.info("Diameter Base RA :: createActivity");

      return this.createActivity(null, null);
    }

    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createActivity(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity)
     */
    public DiameterActivity createActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException
    {
      logger.info("Diameter Base RA :: createActivity :: destinationHost[" + destinationHost + "], destinationRealm[" + destinationRealm + "]");

      return createActivity(destinationHost, destinationRealm, null);
    }

    public DiameterActivity createActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm, String sessionId) throws CreateActivityException
    {
      Session session = null;

      try
      {
        if (sessionId != null)
        {
          session = proxySessionFactory.getNewSession(sessionId);
        }
        else
        {
          session = proxySessionFactory.getNewSession();
        }
      }
      catch (InternalException e)
      {
        logger.error("Failure obtaining session for creating activity.", e);
        return null;
      }

      return activities.get(getActivityHandle(session.getSessionId()));
    }

    // ###############################
    // ## AUTHENTICATION ACTIVITIES ##
    // ###############################
    
    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createAuthenticationActivity()
     */
    public AuthClientSessionActivity createAuthenticationActivity() throws CreateActivityException
    {
      return this.createAuthenticationActivity(null, null);
    }

    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createAuthenticationActivity(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity)
     */
    public AuthClientSessionActivity createAuthenticationActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException
    {
      try 
      {

    	  //we take first
        ClientAuthSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(authVendorIds[0], authAppIds[0]), ClientAuthSession.class);

        return (AuthClientSessionActivity) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
      }
      catch (InternalException e) {
        throw new CreateActivityException("Internal exception while creating Authentication Activity", e);
      }
      catch (IllegalDiameterStateException e) {
        throw new CreateActivityException("Illegal Diameter State exception while creating Authentication Activity", e);
      }
    }

    public AuthServerSessionActivity createAuthenticationServerActivity(Request request) throws CreateActivityException
    {
      ServerAuthSession session = null;

      try
      {
    	  //we take first
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(authVendorIds[0], authAppIds[0]), ServerAuthSession.class, new Object[]{request});
         
        return (AuthServerSessionActivity) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
      }
      catch (InternalException e) {
        throw new CreateActivityException("Internal exception while creating Server Authentication Activity", e);
      }
      catch (IllegalDiameterStateException e) {
        throw new CreateActivityException("Illegal Diameter State exception while creating Server Authentication Activity", e);
      }
    }
    
    // ###########################
    // ## ACCOUNTING ACTIVITIES ##
    // ###########################
    
    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createAccountingActivity()
     */
    public AccountingClientSessionActivity createAccountingActivity() throws CreateActivityException
    {
      return this.createAccountingActivity(null, null);
    }

    /*
     * (non-Javadoc)
     * @see net.java.slee.resource.diameter.base.DiameterProvider#createAccountingActivity(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity)
     */
    public AccountingClientSessionActivity createAccountingActivity(DiameterIdentity destinationHost, DiameterIdentity destinationRealm) throws CreateActivityException
    {
      try
      {
        // FIXME: alexandre: This must be fixed, we need way to get Application-Id!
        ClientAccSession session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAccAppId(acctVendorIds[0], acctAppIds[0]), ClientAccSession.class);

        return (AccountingClientSessionActivity) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
      }
      catch (InternalException e) {
        throw new CreateActivityException("Internal exception while creating Client Accounting Activity", e);
      }
      catch (IllegalDiameterStateException e) {
        throw new CreateActivityException("Illegal Diameter State exception while creating Client Accounting Activity", e);
      }
    }
    
    public AccountingServerSessionActivity createAccountingServerActivity(Request req) throws CreateActivityException
    {
      ServerAccSession session = null;

      try
      {
        ApplicationId appId = req.getApplicationIdAvps().isEmpty() ? null : req.getApplicationIdAvps().iterator().next(); 
        session = ((ISessionFactory) stack.getSessionFactory()).getNewAppSession(req.getSessionId(), appId, ServerAccSession.class, req);
        
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
      
      return (AccountingServerSessionActivity) activities.get(getActivityHandle(session.getSessions().get(0).getSessionId()));
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
        DiameterIdentity destinationHost = null;
        DiameterIdentity destinationRealm = null;

        AvpSet avps = message.getAvps();

        Avp raw = null;

        if((raw = avps.getAvp(Avp.DESTINATION_HOST)) != null) 
        {
          try 
          {
            destinationHost = new DiameterIdentity(raw.getDiameterIdentity());
          }
          catch (AvpDataException e) 
          {
            logger.error("", e);
          }
        }

        if((raw = avps.getAvp(Avp.DESTINATION_REALM)) != null)
        {
          try 
          {
            destinationRealm = new DiameterIdentity(raw.getDiameterIdentity());
          }
          catch (AvpDataException e)
          {
            logger.error("", e);
          }
        }

        if (isMessageOfType(message, DiameterAvpCodes.AUTH_APPLICATION_ID))
        {
          return createAuthenticationServerActivity((Request) message);
        }
        else if (isMessageOfType(message, DiameterAvpCodes.ACCT_APPLICATION_ID))
        {
          return createAccountingServerActivity((Request) message);
        }
        else // Base Activity
        {
          return this.createActivity(destinationHost, destinationRealm, message.getSessionId());
        }
      }
    }


	private boolean isMessageOfType(Message message, int type)
    {
      try
      {
        Avp vendorSpecificAvp = null;  
        
        if( message.getAvps().getAvp(type) != null )
        {
          return true;
        }
        else if( (vendorSpecificAvp = message.getAvps().getAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID)) != null )
        {
          return vendorSpecificAvp.getGrouped().getAvp(type) != null;
        }
      }
      catch (Exception ignore) {
        // ignore this... say it isn't
      }
      
      return false;
    }

    public DiameterMessageFactory getDiameterMessageFactory()
    {
      if (this.messageFactory == null)
      {
        this.messageFactory = new DiameterMessageFactoryImpl(this.ra.stack);
      }

      return this.messageFactory;
    }

    public DiameterAvpFactory getDiameterAvpFactory()
    {
      if (this.avpFactory == null)
      {
        this.avpFactory = new DiameterAvpFactoryImpl();
      }
      
      return this.avpFactory;
    }

    public DiameterMessage sendSyncRequest(DiameterMessage message) throws IOException
    {
      try
      {
        if (message instanceof DiameterMessageImpl) 
        {
          DiameterMessageImpl msg = (DiameterMessageImpl) message;
          
          // FIXME: baranowb - get session from RA! THIS WILL BE COMPLICATED IN CASE WE HAVE RELLY AGENT!
          // here we can have leaks ;[
          
          String sessionId = message.getSessionId();
          DiameterActivityHandle handle = new DiameterActivityHandle(sessionId);

          if (!activities.keySet().contains(handle))
          {
            createActivity(msg.getGenericData());
          }

          DiameterActivityImpl activity = (DiameterActivityImpl) getActivity(handle);
          
          return activity.sendSyncMessage(message);
        }
      } catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}

      // FIXME Throw unknown message exception?
      return null;
    }

    public DiameterIdentity[] getConnectedPeers() {
      return this.ra.getConnectedPeers();
    }

    public int getPeerCount() {
      return getConnectedPeers().length;
    }
  }

  // #############################
  // ## BASE SESSION MANAGEMENT ##
  // #############################

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.acc.ServerAccSession)
   */
  public void sessionCreated(ServerAccSession session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(stack);
    
    AccountingServerSessionActivityImpl activity = new AccountingServerSessionActivityImpl(msgFactory, diameterAvpFactory, session, messageTimeout,
        null, null, sleeEndpoint, stack);
    
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
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);
    
    AuthServerSessionActivityImpl activity = new AuthServerSessionActivityImpl(msgFactory, diameterAvpFactory, session, messageTimeout,
        null, null, sleeEndpoint);
    
    session.addStateChangeNotification(activity);
    activity.setSessionListener(this);
    activityCreated(activity);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.auth.ClientAuthSession)
   */
  public void sessionCreated(ClientAuthSession session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session.getSessions().get(0), stack, null, null);
    
    AuthClientSessionActivityImpl activity = new AuthClientSessionActivityImpl(msgFactory, diameterAvpFactory, session, messageTimeout, 
        null, null, sleeEndpoint);
    
    session.addStateChangeNotification(activity);
    activity.setSessionListener(this);
    activityCreated(activity);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.acc.ClientAccSession)
   */
  public void sessionCreated(ClientAccSession session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(stack);
    
    AccountingClientSessionActivityImpl activity = new AccountingClientSessionActivityImpl(msgFactory, diameterAvpFactory, session, messageTimeout, 
        null, null, sleeEndpoint);
    
    activity.setSessionListener(this);
    session.addStateChangeNotification(activity);
    activityCreated(activity);
  }

  /*
   * (non-Javadoc)
   * @see org.mobicents.slee.resource.diameter.base.handlers.BaseSessionCreationListener#sessionCreated(org.jdiameter.api.Session)
   */
  public void sessionCreated(Session session)
  {
    DiameterMessageFactoryImpl msgFactory = new DiameterMessageFactoryImpl(session, stack, null, null);

    DiameterActivityImpl activity = new DiameterActivityImpl(msgFactory, diameterAvpFactory, session, this, messageTimeout, null, null, sleeEndpoint);
    
    //FIXME: baranowb: add basic session mgmt for base? or do we relly on responses?
    //session.addStateChangeNotification(activity);
    activity.setSessionListener(this);
    activityCreated(activity);
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
  public ApplicationId[] getSupportedApplications() {

		List<ApplicationId> appIds = new ArrayList<ApplicationId>();
		for (int index = 0; index < acctAppIds.length; index++) {
			appIds.add(ApplicationId.createByAccAppId(acctVendorIds[index], acctAppIds[index]));

		}
		for (int index = 0; index < authAppIds.length; index++) {
			appIds.add(ApplicationId.createByAuthAppId(authVendorIds[index], authAppIds[index]));

		}

		return appIds.toArray(new ApplicationId[appIds.size()]);
	}

  
  
  private long[] acctVendorIds;
	private long[] authVendorIds;
	private long[] acctAppIds;
	private long[] authAppIds;

	public String getAcctVendorIds() {

		return convertToString(acctVendorIds);
	}

	public void setAcctVendorIds(String vendorIds) {
		this.acctVendorIds = convertToLong(vendorIds);
	}

	public String getAuthVendorIds() {

		return convertToString(authVendorIds);
	}

	public void setAuthVendorIds(String vendorIds) {
		this.authVendorIds = convertToLong(vendorIds);
	}

	public String getAcctAppIds() {
		return convertToString(acctAppIds);
	}

	public void setAcctAppIds(String acctAppId) {
		this.acctAppIds = convertToLong(acctAppId);
	}

	public String getAuthAppIds() {
		return convertToString(authAppIds);
	}

	public void setAuthAppIds(String authAppId) {
		this.authAppIds = convertToLong(authAppId);
	}

	protected String convertToString(long[] l) {
		String s = "";
		for (int index = 0; index < l.length; index++) {
			s += l[index];
			if (l.length - 1 != index) {
				s += ",";
			}
		}
		return s;

	}

	protected long[] convertToLong(String s) {
		String[] ss = s.split(",");
		long[] l = new long[ss.length];
		for (int index = 0; index < ss.length; index++) {
			l[index] = Long.valueOf(ss[index]);
		}
		return l;
	}
}
