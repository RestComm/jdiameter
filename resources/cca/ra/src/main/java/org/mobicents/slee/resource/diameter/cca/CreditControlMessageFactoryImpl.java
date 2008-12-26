/**
 * Start time:11:16:00 2008-12-09<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlAnswerImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlRequestImpl;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

/**
 * Start time:11:16:00 2008-12-09<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlMessageFactoryImpl implements
		CreditControlMessageFactory {

	protected DiameterMessageFactoryImpl baseFactory = null;

	protected Session session;
	protected Stack stack;
	protected Logger logger=Logger.getLogger(this.getClass());
	protected CreditControlAVPFactoryImpl localFactory=null;
	
	protected ArrayList<DiameterAvp> avpList=new ArrayList<DiameterAvp>();
	
	public CreditControlMessageFactoryImpl(
			DiameterMessageFactoryImpl baseFactory, Session session,
			Stack stack, CreditControlAVPFactory localFactory) {
		super();
		this.baseFactory = baseFactory;
		this.session = session;
		this.stack = stack;
		this.localFactory = (CreditControlAVPFactoryImpl) localFactory;
	}

	
	
	protected final static Set<Integer> ids;
	static
	{
		Set<Integer> _ids=new HashSet<Integer>();
		
		//SessionId
		_ids.add(Avp.SESSION_ID);
		//Sub-Session-Id
		_ids.add(CreditControlAVPCodes.CC_Sub_Session_Id);
		//{ Origin-Host }
		_ids.add(Avp.ORIGIN_HOST);
        //{ Origin-Realm }
		_ids.add(Avp.ORIGIN_REALM);
        //{ Destination-Realm }
		_ids.add(Avp.DESTINATION_REALM);
        //{ Auth-Application-Id }
		_ids.add(Avp.AUTH_APPLICATION_ID);
        //{ Service-Context-Id }
		_ids.add(CreditControlAVPCodes.Service_Context_Id);
        //{ CC-Request-Type }
		_ids.add(CreditControlAVPCodes.CC_Request_Type);
        //{ CC-Request-Number }
		_ids.add(CreditControlAVPCodes.CC_Request_Number);
        //[ Acct-Multi-Session-Id ]
		_ids.add(Avp.ACC_MULTI_SESSION_ID);
        //[ Origin-State-Id ]
		_ids.add(Avp.ORIGIN_STATE_ID);
        //[ Event-Timestamp ]
		_ids.add(Avp.EVENT_TIMESTAMP);
        //xx*[ Proxy-Info ]
        //xx*[ Route-Record ]
		
		ids=Collections.unmodifiableSet(_ids);
	}
	
	///**
	// * Should contain headers that have to be included in messages created
	// */
	//protected ArrayList<DiameterAvp> localHeaders=new ArrayList<DiameterAvp>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlMessageFactory#
	 * createCreditControlAnswer
	 * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
	 */
	public CreditControlAnswer createCreditControlAnswer(
			CreditControlRequest request) {
		//What shall we copy?
		//SessionId
		//Sub-Session-Id
		//{ Origin-Host }
        //{ Origin-Realm }
        //{ Destination-Realm }
        //{ Auth-Application-Id }
        //{ Service-Context-Id }
        //{ CC-Request-Type }
        //{ CC-Request-Number }
		//[ CC-Sub-Session-Id ]
        //[ Acct-Multi-Session-Id ]
        //[ Origin-State-Id ]
        //[ Event-Timestamp ]
        //xx*[ Proxy-Info ]
        //xx*[ Route-Record ]

		
		
		ArrayList<DiameterAvp> avps=new ArrayList<DiameterAvp>();
		DiameterAvp[] messageAvps=request.getAvps();
		if(messageAvps!=null)
		{
			for(DiameterAvp a:messageAvps)
			{
				if(ids.contains(a.getCode()))
				{
					avps.add(a);
				}
			}
				
		}
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_CCA_VENDOR, _CCA_AUTH_APP_ID);
		
		Message msg=createMessage(CreditControlAnswer.commandCode, applicationId, avps.toArray(new DiameterAvp[avps.size()]));
		msg.setRequest(false);
		CreditControlAnswerImpl _answer=new CreditControlAnswerImpl(msg);
		//JDIAM HAD BUG, cleans some part of uri
		
		
		
		
		return _answer;
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlMessageFactory#
	 * createCreditControlAnswer(java.lang.String)
	 */
	public CreditControlAnswer createCreditControlAnswer(String sessionId)
			throws IllegalArgumentException {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_CCA_VENDOR, _CCA_AUTH_APP_ID);
		DiameterAvp sessionIdAvp;
		try {
			sessionIdAvp = this.localFactory.getBaseFactory().createAvp(Avp.SESSION_ID, sessionId);
		} catch (NoSuchAvpException e) {
			
			//e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		List<DiameterAvp> list=(List<DiameterAvp>) this.avpList.clone();
		if(list.size()!=0)
		{	
			list.set(0, sessionIdAvp);
		}else
		{
			list.add(sessionIdAvp);
		}
		Message msg=createMessage(CreditControlAnswer.commandCode, applicationId, list.toArray(new DiameterAvp[list.size()]));
		msg.setRequest(false);
		CreditControlAnswerImpl answer=new CreditControlAnswerImpl(msg);
		return answer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlMessageFactory#
	 * createCreditControlRequest()
	 */
	public CreditControlRequest createCreditControlRequest() {
		
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_CCA_VENDOR, _CCA_AUTH_APP_ID);
	
		List<DiameterAvp> list=(List<DiameterAvp>) this.avpList.clone();

		Message msg=createMessage(CreditControlAnswer.commandCode, applicationId, list.toArray(new DiameterAvp[list.size()]));
		CreditControlRequestImpl request=new CreditControlRequestImpl(msg);
		return request;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlMessageFactory#
	 * createCreditControlRequest(java.lang.String)
	 */
	public CreditControlRequest createCreditControlRequest(String sessionId)
			throws IllegalArgumentException {
		ApplicationId applicationId = ApplicationId.createByAuthAppId(_CCA_VENDOR, _CCA_AUTH_APP_ID);
		DiameterAvp sessionIdAvp;
		try {
			sessionIdAvp = this.localFactory.getBaseFactory().createAvp(Avp.SESSION_ID, sessionId);
		} catch (NoSuchAvpException e) {
			
			//e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
		
		List<DiameterAvp> list=(List<DiameterAvp>) this.avpList.clone();
		if(list.size()!=0)
		{	
			list.set(0, sessionIdAvp);
		}else
		{
			list.add(sessionIdAvp);
		}
		Message msg=createMessage(CreditControlAnswer.commandCode, applicationId, list.toArray(new DiameterAvp[list.size()]));
		
		
		CreditControlRequestImpl request=new CreditControlRequestImpl(msg);
		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seenet.java.slee.resource.diameter.cca.CreditControlMessageFactory#
	 * getBaseMessageFactory()
	 */
	public DiameterMessageFactory getBaseMessageFactory() {

		return this.baseFactory;
	}
	
	
	// FIXME: This has been copied from base
	protected Message createMessage(int commandCode, ApplicationId applicationId, DiameterAvp... avps) {
		Message msg = null;

		
		DiameterAvp sessionIdAvp=null;
		for (DiameterAvp avp : avps) {
			if(avp.getCode()==Avp.SESSION_ID)
			{
				sessionIdAvp=avp;
				
				break;
			}
		}
		
		if (session == null) {
			try {
				msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, applicationId);
			} catch (InternalException e) {
				// TODO Auto-generated catch block
				logger.error("", e);
			} catch (IllegalDiameterStateException e) {
				// TODO Auto-generated catch block
				logger.error("", e);
			}
		} else {
			String destRealm = null;
			String destHost = null;
			if(avps!=null)
				for (DiameterAvp avp : avps) {
					if (avp.getCode() == Avp.DESTINATION_REALM)
						destRealm = avp.octetStringValue();
					else if (avp.getCode() == Avp.DESTINATION_HOST)
						destHost = avp.octetStringValue();
					
			}

			msg = destHost == null ? session.createRequest(commandCode, applicationId, destRealm) : session.createRequest(commandCode, applicationId, destRealm, destHost);
		}

		if(sessionIdAvp!=null)
		{
			msg.getAvps().removeAvp(Avp.SESSION_ID);
			addAvp(sessionIdAvp, msg.getAvps());
		}else 	if (msg.getAvps().getAvp(Avp.SESSION_ID) == null)
		{
			// Do we have a session-id already or shall we make one?
			if(this.session!=null)
			{
				msg.getAvps().addAvp(Avp.SESSION_ID, this.session.getSessionId(), true, false, false);
			}else
			{
				msg.getAvps().addAvp(Avp.SESSION_ID, this.baseFactory.generateSessionId(), true, false, false);
			}
		}
		
		if (avps != null) {
			for (DiameterAvp avp : avps) {
				if(avp.getCode()==Avp.SESSION_ID)
				{
					continue;
				}
				addAvp(avp, msg.getAvps());
			}
		}
		
		msg.setProxiable( true );

		return msg;
	}
	
	private void addAvp(DiameterAvp avp, AvpSet set) {
		// FIXME: alexandre: Should we look at the types and add them with
		// proper function?
		if (avp instanceof GroupedAvp) {
			AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);

			DiameterAvp[] groupedAVPs = ((GroupedAvp) avp).getExtensionAvps();
			for (DiameterAvp avpFromGroup : groupedAVPs) {
				addAvp(avpFromGroup, avpSet);
			}
		} else if (avp != null)
			set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
	}


	public List<DiameterAvp> getInnerAvps()
	{
		return this.avpList;
	}
	
	//FIXME:baranowb add removel if present
	public  void addAvpToInnerList(DiameterAvp avp)
	{
		this.avpList.add(avp);
	}
	
	public  void removeAvpFromInnerList(int code)
	{
		  Iterator<DiameterAvp> it=this.avpList.iterator();
		  while(it.hasNext())
		  {
			  if(it.next().getCode()==code)
			  {
				  it.remove();
			  }
		  }
	}
}
