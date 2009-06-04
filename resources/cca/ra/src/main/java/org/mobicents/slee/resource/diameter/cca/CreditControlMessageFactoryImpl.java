package org.mobicents.slee.resource.diameter.cca;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlAnswer;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.Stack;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.DiameterHeaderImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlAnswerImpl;
import org.mobicents.slee.resource.diameter.cca.events.CreditControlRequestImpl;

/**
 * Start time:11:16:00 2008-12-09<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlMessageFactoryImpl implements CreditControlMessageFactory {

  protected DiameterMessageFactoryImpl baseFactory = null;

  protected Session session;
  protected Stack stack;
  protected Logger logger = Logger.getLogger(this.getClass());
  protected CreditControlAVPFactoryImpl localFactory = null;

  protected ArrayList<DiameterAvp> avpList = new ArrayList<DiameterAvp>();

  public CreditControlMessageFactoryImpl(DiameterMessageFactoryImpl baseFactory, Session session, Stack stack, CreditControlAVPFactory localFactory)
  {
    super();
    this.baseFactory = baseFactory;
    this.session = session;
    this.stack = stack;
    this.localFactory = (CreditControlAVPFactoryImpl) localFactory;
  }

  protected final static Set<Integer> ids;

  static
  {
    Set<Integer> _ids = new HashSet<Integer>();

    //SessionId
    //_ids.add(Avp.SESSION_ID);
    //Sub-Session-Id
    _ids.add(CreditControlAVPCodes.CC_Sub_Session_Id);
    //{ Origin-Host }
    //_ids.add(Avp.ORIGIN_HOST);
    //{ Origin-Realm }
   // _ids.add(Avp.ORIGIN_REALM);
    //{ Destination-Realm }
   // _ids.add(Avp.DESTINATION_REALM);
    //_ids.add(Avp.DESTINATION_HOST);
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

    ids = Collections.unmodifiableSet(_ids);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlMessageFactory#createCreditControlAnswer
   * (net.java.slee.resource.diameter.cca.events.CreditControlRequest)
   */
  public CreditControlAnswer createCreditControlAnswer(CreditControlRequest request) {

		// Create the answer from the request
		CreditControlRequestImpl ccr = (CreditControlRequestImpl) request;
		Request req = (Request) ccr.getGenericData();

		// Message msg = session.createRequest(req);

		DiameterAvp sessionIdAvp = null;
		try {
			sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, req.getSessionId());
		} catch (NoSuchAvpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CreditControlAnswerImpl msg = (CreditControlAnswerImpl) createCreditControlMessage(ccr.getHeader(), new DiameterAvp[] { sessionIdAvp });
		// msg.setRequest(false);
		// ((MessageImpl)msg.getGenericData()).setEndToEndIdentifier(req.getEndToEndIdentifier());
		// ((MessageImpl)msg.getGenericData()).setHopByHopIdentifier(req.getHopByHopIdentifier());

		msg.getGenericData().getAvps().removeAvp(DiameterAvpCodes.DESTINATION_HOST);
		msg.getGenericData().getAvps().removeAvp(DiameterAvpCodes.DESTINATION_REALM);
		msg.getGenericData().getAvps().removeAvp(DiameterAvpCodes.ORIGIN_HOST);
		msg.getGenericData().getAvps().removeAvp(DiameterAvpCodes.ORIGIN_REALM);

		// Now copy the needed AVPs

		DiameterAvp[] messageAvps = request.getAvps();
		if (messageAvps != null) {
			for (DiameterAvp a : messageAvps) {
				try {
					if (ids.contains(a.getCode())) {

						msg.addAvp(a);

					}
				} catch (Exception e) {
					logger.error("Failed to add AVP to answer. Code[" + a.getCode() + "]", e);
				}
			}
		}
		msg.setOriginRealm(request.getDestinationRealm());
		msg.setOriginHost(new DiameterIdentity(stack.getMetaData().getLocalPeer().getUri().getFQDN().toString()));
		return msg;
	}


  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlMessageFactory#createCreditControlRequest()
   */
  public CreditControlRequest createCreditControlRequest()
  {
	  
	  CreditControlRequest req = (CreditControlRequest) createCreditControlMessage( null, null );
	  req.setOriginRealm(new DiameterIdentity(stack.getMetaData().getLocalPeer().getRealmName()));
	  req.setOriginHost(new DiameterIdentity(stack.getMetaData().getLocalPeer().getUri().getFQDN().toString()));
    return req;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlMessageFactory#createCreditControlRequest(java.lang.String)
   */
  public CreditControlRequest createCreditControlRequest(String sessionId) throws IllegalArgumentException {

		try {
			DiameterAvp sessionIdAvp;
			sessionIdAvp = localFactory.getBaseFactory().createAvp(0, DiameterAvpCodes.SESSION_ID, sessionId);
			CreditControlRequest req = (CreditControlRequest) (CreditControlRequest) createCreditControlMessage(null, new DiameterAvp[] { sessionIdAvp });
			req.setOriginRealm(new DiameterIdentity(stack.getMetaData().getLocalPeer().getRealmName()));
			req.setOriginHost(new DiameterIdentity(stack.getMetaData().getLocalPeer().getUri().getFQDN().toString()));
			return req;
		} catch (NoSuchAvpException e) {
			throw new IllegalArgumentException(e);
		}

	}

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.CreditControlMessageFactory#
   * getBaseMessageFactory()
   */
  public DiameterMessageFactory getBaseMessageFactory()
  {
    return this.baseFactory;
  }

  public List<DiameterAvp> getInnerAvps()
  {
    return this.avpList;
  }

  public  void addAvpToInnerList(DiameterAvp avp)
  {
    // Remove existing occurences...
    removeAvpFromInnerList( avp.getCode() );

    this.avpList.add(avp);
  }

  public  void removeAvpFromInnerList(int code)
  {
    Iterator<DiameterAvp> it = this.avpList.iterator();

    while(it.hasNext())
    {
      if(it.next().getCode() == code)
      {
        it.remove();
      }
    }
  }
  
  // »»»»»»»»»»»»»»»»»»»»»
  // »» PRIVATE METHODS ««
  // «««««««««««««««««««««




  private CreditControlMessage createCreditControlMessage(DiameterHeader diameterHeader, DiameterAvp[] avps) throws IllegalArgumentException
  {
    
	

    //List<DiameterAvp> list = (List<DiameterAvp>) this.avpList.clone();
    boolean isRequest = diameterHeader == null;
    CreditControlMessage msg = null;
    if(!isRequest)
    {
    	Message raw=createMessage(diameterHeader,avps);
    	raw.setProxiable(true);
    	raw.setRequest(false);
    	msg = new CreditControlAnswerImpl(raw);
    	
    }else
    {

    	Message raw=createMessage(null,avps);
    	raw.setProxiable(true);
    	raw.setRequest(true);
    	msg = new CreditControlAnswerImpl(raw);
    }
    
    
    return msg;
  }
  
  public Message createMessage(DiameterHeader header, DiameterAvp[] avps) throws AvpNotAllowedException {
		
	

		try {
			Message msg =createRawMessage(header);

			AvpSet set = msg.getAvps();
			for (DiameterAvp avp : avps)
				addAvp(avp, set);
			
			return msg;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
  }
  
  protected Message createRawMessage(DiameterHeader header)
	{
	  
	  	int commandCode = 0;
		long endToEndId = 0;
		long hopByHopId = 0;
		ApplicationId aid = null;
	  	if(header !=null)
	  	{
	  		 commandCode = header.getCommandCode();
			 endToEndId = header.getEndToEndId();
			 hopByHopId = header.getHopByHopId();
			 aid = ApplicationId.createByAccAppId(header.getApplicationId());
	  	}else
	  	{
	  		commandCode = AccountingRequest.commandCode;
	  		endToEndId = (long) (Math.random()*1000000);
			hopByHopId = (long) (Math.random()*1000000)+1;
	  		aid = ApplicationId.createByAccAppId(_CCA_VENDOR, _CCA_AUTH_APP_ID);
	  	}
		try {
			Message msg = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, aid, hopByHopId, endToEndId);
			return msg;
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
  protected void addAvp(DiameterAvp avp, AvpSet set) {
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
}
