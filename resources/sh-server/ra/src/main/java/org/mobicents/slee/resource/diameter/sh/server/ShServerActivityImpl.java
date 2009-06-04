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
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.sh.client.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.SubscribeNotificationsAnswer;
import net.java.slee.resource.diameter.sh.client.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.server.ShServerActivity;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.common.impl.app.sh.ProfileUpdateAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsAnswerImpl;
import org.jdiameter.common.impl.app.sh.UserDataAnswerImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.server.handlers.ShServerSessionListener;

/**
 * Start time:16:43:18 2009-01-06<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of stateles Sh Server activity whihc recieves. It ends after resposne is sent.
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShServerActivity
 */
public class ShServerActivityImpl extends DiameterActivityImpl implements ShServerActivity, StateChangeListener {

	protected ServerShSession serverSession = null;
	protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
	protected ShServerSessionListener listener = null;
	
	// Factories
	protected DiameterShAvpFactory shAvpFactory = null;
	protected ShServerMessageFactoryImpl messageFactory = null;

	
	protected DiameterIdentity clientOriginRealm = null;
	protected GroupedAvp userIdentity;
	protected AuthSessionStateType authSessionState = null;

	// THIS IS BAD, we need to come up with something.
	/**
	 * Should contina requests, so we can create answer.
	 */
	protected ArrayList<DiameterMessageImpl> stateMessages = new ArrayList<DiameterMessageImpl>();
	
	public ShServerActivityImpl(ShServerMessageFactory shServerMessageFactory, DiameterShAvpFactory diameterShAvpFactory, ServerShSession session, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint)
	{
		super(null, null, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);
		
		this.serverSession = session;
		this.serverSession.addStateChangeNotification(this);
		super.setCurrentWorkingSession(this.serverSession.getSessions().get(0));
		this.shAvpFactory = diameterShAvpFactory;
		this.messageFactory = (ShServerMessageFactoryImpl) shServerMessageFactory;
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

	public SubscribeNotificationsAnswer createSubscribeNotificationsAnswer(long resultCode, boolean isExperimentalResult)
	  {
		  
		  SubscribeNotificationsAnswer answer = null;
		  for(int index =0 ;index<stateMessages.size();index++)
		    {
		    	if(stateMessages.get(index).getCommand().getCode() == SubscribeNotificationsRequest.commandCode)
		    	{
		    		SubscribeNotificationsRequest msg = (SubscribeNotificationsRequest) stateMessages.get(index);
		    	
		    		answer = this.messageFactory.createSubscribeNotificationsAnswer(msg, resultCode, isExperimentalResult);
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

  // #########################
  // # StateChangeListener
  // #########################
  
	public void stateChanged(Enum oldState, Enum newState)
	{
		org.jdiameter.common.api.app.sh.ShSessionState _state = (org.jdiameter.common.api.app.sh.ShSessionState) newState;
		switch(_state)
		{
		case NOTSUBSCRIBED:
			break;
		case SUBSCRIBED:
			//FIXME: error?
			//This should not happen!!!
			break;
		case TERMINATED:
			state = ShSessionState.TERMINATED;
			this.listener.sessionDestroyed(getSessionId(), serverSession);
			this.serverSession.removeStateChangeNotification(this);
			this.messageFactory.clean();
			this.messageFactory = null;
		    this.serverSession = null;
		    super.session =  null;
		    
		    this.shAvpFactory = null;
		   	
		  
		   	this.stateMessages.clear();
		   	this.listener = null;
		   	super.clean();
			break;
		}
	}
	
  // #########################
  // # DiameterActivityImpl
  // #########################
	
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

	public void fetchSessionData(DiameterMessage msg, boolean incoming)
	  {
	    if(msg.getHeader().isRequest())
	    {
	      //Well it should always be getting this on request and only once ?
	      if(incoming)
	      {
	       
	        if(this.userIdentity == null)
	        {
	        	try{
	        		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
	        		this.userIdentity = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID,rep.getRuleMandatoryAsInt(),rep.getRuleProtectedAsInt(),AvpUtilities.getAvpAsGrouped(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, ((DiameterMessageImpl)msg).getGenericData().getAvps()));
	        	}catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        }
	        
	        if(this.authSessionState == null)
	        {
	        	try{

	        		this.authSessionState = AuthSessionStateType.fromInt(AvpUtilities.getAvpAsInteger32(277, ((DiameterMessageImpl)msg).getGenericData().getAvps()));
	        	}catch(Exception e)
	        	{
	        		e.printStackTrace();
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
	
}
