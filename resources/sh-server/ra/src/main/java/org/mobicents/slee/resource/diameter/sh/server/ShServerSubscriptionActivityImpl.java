/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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
package org.mobicents.slee.resource.diameter.sh.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequestImpl;
import org.mobicents.slee.resource.diameter.sh.server.handlers.ShServerSessionListener;

/**
 * Start time:16:43:18 2009-01-06<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of statful activity.
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShServerSubscriptionActivity
 */
public class ShServerSubscriptionActivityImpl extends DiameterActivityImpl implements ShServerSubscriptionActivity, StateChangeListener {

  protected ServerShSession serverSession = null;
  protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
  protected ShServerSessionListener listener = null;
  protected DiameterShAvpFactory shAvpFactory = null;
  protected ShServerMessageFactoryImpl messageFactory = null;

//FIXME: add more
	protected UserIdentityAvp userIdentity;
	protected DataReferenceType[] dataReferenceType;
	protected AuthSessionStateType authSessionState;
	protected DiameterIdentity remoteRealm;
	protected DiameterIdentity remoteHost;
	/**
   * Should contina requests, so we can create answer.
   */
  protected ArrayList<DiameterMessageImpl> stateMessages = new ArrayList<DiameterMessageImpl>();
	
  public ShServerSubscriptionActivityImpl(ShServerMessageFactory shServerMessageFactory, DiameterShAvpFactory diameterShAvpFactory, ServerShSession session,
      long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint)
  {
    super(null, null, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);

    this.serverSession = session;
    this.serverSession.addStateChangeNotification(this);
    super.setCurrentWorkingSession(this.serverSession.getSessions().get(0));
    this.shAvpFactory = diameterShAvpFactory;
    this.messageFactory = (ShServerMessageFactoryImpl) shServerMessageFactory;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createPushNotificationRequest()
   */
  public PushNotificationRequest createPushNotificationRequest()
  {
    PushNotificationRequest request = this.messageFactory.createPushNotificationRequest();

    if(request.getDestinationHost() == null && remoteHost != null)
    {
      request.setDestinationHost(remoteHost);
    }
    if(request.getDestinationRealm() == null && remoteRealm != null)
    {
      request.setDestinationRealm(remoteRealm);
    }

    if(request.getUserIdentity() == null && this.userIdentity!=null)
    {
    	request.setExtensionAvps(this.userIdentity);
    }
    if(request.getAuthSessionState() == null && this.authSessionState!=null)
    {
    	request.setAuthSessionState(this.authSessionState);
    }
    return request; 
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createSubscribeNotificationsAnswer(long, boolean)
   */
  public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult)
  {
	  
	  SubscribeNotificationsAnswer answer = null;
	  for(int index =0 ;index<stateMessages.size();index++)
	    {
	    	if(stateMessages.get(index).getCommand().getCode() == SubscribeNotificationsRequest.commandCode)
	    	{
	    		SubscribeNotificationsRequest msg = (SubscribeNotificationsRequest) stateMessages.get(index);
	    	
	    		answer = this.messageFactory.createSubscribeNotificationsAnswer(msg, resultCode, isExperimentalResult);
	    		//FIXME:
	    		 //if(answer.getDestinationRealm() == null && remoteRealm != null)
	    		 //   {
	    		//	 answer.setDestinationRealm(remoteRealm);
	    		 //   }
	    		    if(answer.getAuthSessionState() == null && this.authSessionState!=null)
	    		    {
	    		    	answer.setAuthSessionState(this.authSessionState);
	    		    }
	    		 ((DiameterShMessageImpl)answer).setData(msg);
	    		 break;
	    	}
	    }
	  
	 
	   
	    //answer.setSessionId(super.session.getSessionId());
	    return answer; 
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createSubscribeNotificationsAnswer()
   */
  public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer()
  {
	  SubscribeNotificationsAnswer answer = null;
	  for(int index =0 ;index<stateMessages.size();index++)
	    {
	    	if(stateMessages.get(index).getCommand().getCode() == SubscribeNotificationsRequest.commandCode)
	    	{
	    		SubscribeNotificationsRequest msg = (SubscribeNotificationsRequest) stateMessages.get(index);
	    	
	    		answer = this.messageFactory.createSubscribeNotificationsAnswer(msg);
	    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
	    		    {
	    		    	answer.setAuthSessionState(this.authSessionState);
	    		    }
	    		 
	    		 ((DiameterShMessageImpl)answer).setData(msg);
	    		 break;
	    	}
	    }
	  
	 
	   
	    //answer.setSessionId(super.session.getSessionId());
	    return answer; 
	  
	  
     
  }

  public ProfileUpdateAnswer createProfileUpdateAnswer(long resultCode, boolean isExperimentalResult)
  {
	  
	  ProfileUpdateAnswer answer = null;
	  for(int index =0 ;index<stateMessages.size();index++)
	    {
	    	if(stateMessages.get(index).getCommand().getCode() == ProfileUpdateRequest.commandCode)
	    	{
	    		ProfileUpdateRequest msg = (ProfileUpdateRequest) stateMessages.get(index);
	    	
	    		answer = this.messageFactory.createProfileUpdateAnswer(msg, resultCode, isExperimentalResult);
	    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
	    		    {
	    		    	answer.setAuthSessionState(this.authSessionState);
	    		    }
	    		 ((DiameterShMessageImpl)answer).setData(msg);
	    		 break;
	    	}
	    }
	  
	 
	   
	    //answer.setSessionId(super.session.getSessionId());
	    return answer; 
  }

  public UserDataAnswer createUserDataAnswer(byte[] userData)
	{
		UserDataAnswer answer = null;
		  for(int index =0 ;index<stateMessages.size();index++)
		    {
		    	if(stateMessages.get(index).getCommand().getCode() == UserDataRequest.commandCode)
		    	{
		    		UserDataRequest msg = (UserDataRequest) stateMessages.get(index);
		    	
		    		answer = this.messageFactory.createUserDataAnswer(msg,userData);
		    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
		    		    {
		    		    	answer.setAuthSessionState(this.authSessionState);
		    		    }
		    		 
		    		 ((DiameterShMessageImpl)answer).setData(msg);
		    		 break;
		    	}
		    }
		  
		  
		  //answer.setSessionId(super.session.getSessionId());
		    return answer; 
}

	public UserDataAnswer createUserDataAnswer(long resultCode, boolean isExperimentalResult)
	{
		UserDataAnswer answer = null;
		  for(int index =0 ;index<stateMessages.size();index++)
		    {
		    	if(stateMessages.get(index).getCommand().getCode() == UserDataRequest.commandCode)
		    	{
		    		UserDataRequest msg = (UserDataRequest) stateMessages.get(index);
		    	
		    		answer = this.messageFactory.createUserDataAnswer(msg,resultCode,isExperimentalResult);
		    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
		    		    {
		    		    	answer.setAuthSessionState(this.authSessionState);
		    		    }
		    		 ((DiameterShMessageImpl)answer).setData(msg);
		    		 break;
		    	}
		    }
		  
		  
		  //answer.setSessionId(super.session.getSessionId());
		    return answer; 
	}

	public UserDataAnswer createUserDataAnswer()
	{
		UserDataAnswer answer = null;
		  for(int index =0 ;index<stateMessages.size();index++)
		    {
		    	if(stateMessages.get(index).getCommand().getCode() == UserDataRequest.commandCode)
		    	{
		    		UserDataRequest msg = (UserDataRequest) stateMessages.get(index);
		    	
		    		answer = this.messageFactory.createUserDataAnswer(msg);
		    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
		    		    {
		    		    	answer.setAuthSessionState(this.authSessionState);
		    		    }
		    		 ((DiameterShMessageImpl)answer).setData(msg);
		    		 break;
		    	}
		    }
		  
		  
		  //answer.setSessionId(super.session.getSessionId());
		    return answer; 
	}
  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#createSubscribeNotificationsAnswer()
   */
  public ProfileUpdateAnswer createProfileUpdateAnswer()
  {
	  ProfileUpdateAnswer answer = null;
	  for(int index =0 ;index<stateMessages.size();index++)
	    {
	    	if(stateMessages.get(index).getCommand().getCode() == ProfileUpdateRequest.commandCode)
	    	{
	    		ProfileUpdateRequest msg = (ProfileUpdateRequest) stateMessages.get(index);
	    	
	    		answer = this.messageFactory.createProfileUpdateAnswer(msg);
	    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
	    		    {
	    		    	answer.setAuthSessionState(this.authSessionState);
	    		    }
	    		 ((DiameterShMessageImpl)answer).setData(msg);
	    		 break;
	    	}
	    }
	  
	  
	  //answer.setSessionId(super.session.getSessionId());
	    return answer; 
  }

  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity#sendPushNotificationRequest(net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest)
   */
  public void sendPushNotificationRequest(PushNotificationRequest message) throws IOException {
	  DiameterShMessageImpl msg = (DiameterShMessageImpl) message;
		fetchSessionData(msg, false);
		try {
			this.serverSession.sendPushNotificationRequest(new PushNotificationRequestImpl((Request) msg.getGenericData()));
			clean(msg);
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}
  
  public void sendUserDataAnswer(UserDataAnswer message) throws IOException {
		try {
			DiameterShMessageImpl msg = (DiameterShMessageImpl) message;

			this.serverSession.sendUserDataAnswer(new UserDataAnswerImpl((Answer) msg.getGenericData()));
			clean(msg);
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}
  
  public void sendProfileUpdateAnswer(ProfileUpdateAnswer message) throws IOException {
	  DiameterShMessageImpl msg = (DiameterShMessageImpl) message;
		fetchSessionData(msg, false);
		try {
			this.serverSession.sendProfileUpdateAnswer(new ProfileUpdateAnswerImpl((Answer) msg.getGenericData()));
			clean(msg);
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity
	 * #sendSubscribeNotificationsAnswer
	 * (net.java.slee.resource.diameter.sh.client
	 * .events.SubscribeNotificationsAnswer)
	 */
	public void sendSubscribeNotificationsAnswer(SubscribeNotificationsAnswer message) throws IOException {
		DiameterShMessageImpl msg = (DiameterShMessageImpl) message;
		fetchSessionData(msg, false);
		try {
			this.serverSession.sendSubscribeNotificationsAnswer(new SubscribeNotificationsAnswerImpl((Answer) msg.getGenericData()));
			clean(msg);
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}

  public void stateChanged(Enum oldState, Enum newState)
  {
    org.jdiameter.common.api.app.sh.ShSessionState _state = (org.jdiameter.common.api.app.sh.ShSessionState) newState;

    switch (_state)
    {
    case NOTSUBSCRIBED:
      break;
    case SUBSCRIBED:
      state = ShSessionState.SUBSCRIBED;
      // FIXME: error?

      break;
    case TERMINATED:
      state = ShSessionState.TERMINATED;
      
      listener.sessionDestroyed(getSessionId(), serverSession);
      this.serverSession.removeStateChangeNotification(this);
      this.messageFactory.clean();
      this.serverSession = null;
      super.session =  null;
      this.messageFactory = null;
	  this.shAvpFactory = null;
	  super.session = null;
	  super.clean();
      break;
    }
  }

  public void fetchSessionData(DiameterMessage msg, boolean incoming)
  {
    if(msg.getHeader().isRequest())
    {
      //Well it should always be getting this on request and only once ?
      if(incoming)
      {
       //FIXME: add more ?
        if(this.remoteRealm == null)
        {
          this.remoteRealm = msg.getOriginRealm();
        }
        if(this.remoteHost == null)
        {
        	this.remoteHost = msg.getOriginHost();
        }
        if(msg instanceof SubscribeNotificationsRequest)
        {
        	SubscribeNotificationsRequestImpl msgImpl = (SubscribeNotificationsRequestImpl) msg;
        	if(authSessionState == null && msgImpl.hasAuthSessionState())
        	{
        		this.authSessionState = msgImpl.getAuthSessionState();
        	}
        	if(dataReferenceType == null && msgImpl.hasDataReferenceType())
        	{
        		this.dataReferenceType = msgImpl.getDataReferences();
        	}
        }
        
        stateMessages.add((DiameterMessageImpl) msg);
        
      }
      else
      {
        //FIXME, do more :)
      }
    }
  }

  private void clean(DiameterShMessageImpl msg)
  {
	  if(msg.getData()!=null)
	  {
		  this.stateMessages.remove(msg.removeData());
	  }
  }
  
  @Override
  public Object getDiameterAvpFactory()
  {
    return this.avpFactory;
  }

  @Override
  public Object getDiameterMessageFactory()
  {
    return this.messageFactory;
  }

  @Override
  public Object getSessionListener()
  {
    return this.listener;
  }

  @Override
  public void setSessionListener(Object ra)
  {
    this.listener = (ShServerSessionListener) ra;
  }
}
