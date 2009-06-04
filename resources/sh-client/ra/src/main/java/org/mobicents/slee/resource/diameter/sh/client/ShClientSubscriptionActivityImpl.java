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
package org.mobicents.slee.resource.diameter.sh.client;

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
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
import net.java.slee.resource.diameter.sh.client.ShSessionState;
import net.java.slee.resource.diameter.sh.client.events.ProfileUpdateAnswer;
import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.validation.JAvpNotAllowedException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.DiameterAvpFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.handlers.ShClientSessionListener;

/**
 * 
 * Sh Client activity created for subscription cases
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShClientSubscriptionActivity
 */
public class ShClientSubscriptionActivityImpl extends DiameterActivityImpl implements ShClientSubscriptionActivity, StateChangeListener {

	protected ClientShSession clientSession = null;
	protected ShSessionState state = ShSessionState.NOTSUBSCRIBED;
	protected ShClientSessionListener listener = null;
	protected DiameterShAvpFactory shAvpFactory = null;
	protected ShClientMessageFactory messageFactory = null;

	
	protected UserIdentityAvpImpl userIdentity;
	  protected AuthSessionStateType authSessionState = null;
	 // Last received message
	protected ArrayList<DiameterMessageImpl> stateMessages = new ArrayList<DiameterMessageImpl>();
	
	public ShClientSubscriptionActivityImpl(DiameterMessageFactoryImpl messageFactory, ShClientMessageFactory shClientMessageFactory, DiameterAvpFactoryImpl avpFactory,
			DiameterShAvpFactory diameterShAvpFactory, ClientShSession session, long timeout, DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint)
	{
		super(messageFactory, avpFactory, null, (EventListener<Request, Answer>) session, timeout, destinationHost, destinationRealm, endpoint);
		this.clientSession = session;
		this.clientSession.addStateChangeNotification(this);
		super.setCurrentWorkingSession(this.clientSession.getSessions().get(0));
		this.shAvpFactory = diameterShAvpFactory;
		this.messageFactory = shClientMessageFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity#getSubscribedUserIdendity()
	 */
	public UserIdentityAvp getSubscribedUserIdendity()
	{
		return (UserIdentityAvp) this.userIdentity;
	}

	/*
	 * (non-Javadoc)
	 * @see net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity#sendPushNotificationAnswer(net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer)
	 */
	public void sendPushNotificationAnswer(PushNotificationAnswer answer) throws IOException {
		try {
			DiameterMessageImpl msg = (DiameterMessageImpl) answer;

			this.clientSession.sendPushNotificationAnswer(new PushNotificationAnswerImpl((Answer) msg.getGenericData()));
			clean((DiameterShMessageImpl)answer);
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
	 * net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity
	 * #sendPushNotificationAnswer(long, boolean)
	 */
	public void sendPushNotificationAnswer(long resultCode, boolean isExperimentalResultCode) throws IOException {
		 PushNotificationAnswer answer = null;
		  for(int index =0 ;index<stateMessages.size();index++)
		    {
		    	if(stateMessages.get(index).getCommand().getCode() == ProfileUpdateRequest.commandCode)
		    	{
		    		PushNotificationRequest msg = (PushNotificationRequest) stateMessages.get(index);
		    	
		    		answer = this.messageFactory.createPushNotificationAnswer(msg, resultCode, isExperimentalResultCode);
		    		 if(answer.getAuthSessionState() == null && this.authSessionState!=null)
		    		    {
		    		    	answer.setAuthSessionState(this.authSessionState);
		    		    }
		    		 ((DiameterShMessageImpl)answer).setData(msg);
		    		 break;
		    	}
		    }
		  
		  
		  //answer.setSessionId(super.session.getSessionId());
		   if(answer!=null)
		   {
			   this.sendPushNotificationAnswer(answer);
		   }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity
	 * #sendSubscriptionNotificationRequest
	 * (net.java.slee.resource.diameter.sh.server
	 * .events.SubscribeNotificationsRequest)
	 */
	public void sendSubscriptionNotificationRequest(SubscribeNotificationsRequest request) throws IOException {
		try {
			DiameterMessageImpl msg = (DiameterMessageImpl) request;

			this.clientSession.sendSubscribeNotificationsRequest(new SubscribeNotificationsRequestImpl((Request) msg.getGenericData()));
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
	 * net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity
	 * #sendUnsubscribeRequest()
	 */
	public void sendUnsubscribeRequest() throws IOException {
		try {
			// FIXME: Alexandre: How do we know DataReferenceType?
			SubscribeNotificationsRequest snr = this.messageFactory.createSubscribeNotificationsRequest(getSubscribedUserIdendity(), DataReferenceType.REPOSITORY_DATA, SubsReqType.UNSUBSCRIBE);

			DiameterMessageImpl msg = (DiameterMessageImpl) snr;

			this.clientSession.sendSubscribeNotificationsRequest(new SubscribeNotificationsRequestImpl((Request) msg.getGenericData()));
		} catch (JAvpNotAllowedException e) {
			AvpNotAllowedException anae = new AvpNotAllowedException("Message validation failed.", e, e.getAvpCode(), e.getVendorId());
			throw anae;
		} catch (Exception e) {
			e.printStackTrace();
			IOException ioe = new IOException("Failed to send message, due to: " + e);
			throw ioe;
		}
	}
	private void clean(DiameterShMessageImpl msg)
	  {
		  if(msg.getData()!=null)
		  {
			  this.stateMessages.remove(msg.removeData());
		  }
	  }
	/*
	 * (non-Javadoc)
	 * @see org.jdiameter.api.app.StateChangeListener#stateChanged(java.lang.Enum, java.lang.Enum)
	 */
	public void stateChanged(Enum oldState, Enum newState)
	{
		org.jdiameter.common.api.app.sh.ShSessionState shNewState = (org.jdiameter.common.api.app.sh.ShSessionState) newState;
		
		switch (shNewState)
		{
		case NOTSUBSCRIBED:
			break;
		case SUBSCRIBED:
			state = ShSessionState.SUBSCRIBED;
			// FIXME: error?
			break;
		case TERMINATED:
			state = ShSessionState.TERMINATED;
			listener.sessionDestroyed(getSessionId(), clientSession);
			this.clientSession.removeStateChangeNotification(this);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.DiameterActivityImpl#getSessionListener()
	 */
	public Object getSessionListener()
	{
		return this.listener;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.DiameterActivityImpl#setSessionListener(java.lang.Object)
	 */
	public void setSessionListener(Object ra)
	{
		this.listener = (ShClientSessionListener) ra;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.DiameterActivityImpl#endActivity()
	 */
	public void endActivity()
	{
		this.clientSession.release();
	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.DiameterActivityImpl#getDiameterAvpFactory()
	 */
	public Object getDiameterAvpFactory()
	{
		return this.shAvpFactory;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.DiameterActivityImpl#getDiameterMessageFactory()
	 */
	public Object getDiameterMessageFactory()
	{
		return this.messageFactory;
	}

	/**
	 * 
	 * @return
	 */
	ClientShSession getClientSession()
	{
		return this.clientSession;
	}

	/**
	 * 
	 * @param request
	 */
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

}
