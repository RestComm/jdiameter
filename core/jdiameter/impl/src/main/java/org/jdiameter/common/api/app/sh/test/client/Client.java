package org.jdiameter.common.api.app.sh.test.client;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationAlreadyUseException;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.acc.events.AccountAnswer;
import org.jdiameter.api.acc.events.AccountRequest;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.api.auth.events.AbortSessionRequest;
import org.jdiameter.api.auth.events.ReAuthAnswer;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.api.auth.events.SessionTermAnswer;
import org.jdiameter.api.auth.events.SessionTermRequest;
import org.jdiameter.api.cca.ClientCCASession;
import org.jdiameter.api.cca.ServerCCASession;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.ServerShSession;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.cca.test.CreditControlSessionFactory;
import org.jdiameter.common.api.app.cca.test.StackCreator;
import org.jdiameter.common.api.app.sh.test.ShSessionFactory;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationRequestImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;

public class Client extends ShSessionFactory implements NetworkReqListener , EventListener<Request, Answer>{

	
	protected StackCreator sc=null;
	protected boolean isSubscribeBased=true;
	
	protected Timer t=new Timer();
	protected boolean sentUserData=false;
	public Client()
	{
		super(null, 5000);
		
		
		
		String config="<?xml version=\"1.0\"?>"+
		"<Configuration xmlns=\"http://www.jdiameter.org/jdiameter-server\">"+
	"<LocalPeer>"+
		"<URI value=\"aaa://localhost:1812\" />"+
		"<IPAddresses>"+
		"	<IPAddress value=\"127.0.0.1\" />"+
		"</IPAddresses>"+
		"<Realm value=\"mobicents.org\" />"+
		"<VendorID value=\"0\" />"+
		"<ProductName value=\"jDiameter\" />"+
		"<FirmwareRevision value=\"1\" />"+
		"<OverloadMonitor>"+
		"	<Entry index=\"1\" lowThreshold=\"0.5\" highThreshold=\"0.6\">"+
		"		<ApplicationID>"+
		"			<VendorId value=\"10415\" />"+
		"			<AuthApplId value=\"16777217\" />"+
		"			<AcctApplId value=\"0\" />"+
		"		</ApplicationID>"+
		"	</Entry>"+
		"</OverloadMonitor>"+
	"</LocalPeer>"+
	"<Parameters>"+
	"	<AcceptUndefinedPeer value=\"true\" />"+
	"	<DuplicateProtection value=\"true\" />"+
	"	<DuplicateTimer value=\"240000\" />"+
	"	<UseUriAsFqdn value=\"true\" /> <!-- Needed for Ericsson Emulator -->"+
	"	<QueueSize value=\"10000\" />"+
	"	<MessageTimeOut value=\"60000\" />"+
	"	<StopTimeOut value=\"10000\" />"+
	"	<CeaTimeOut value=\"10000\" />"+
	"	<IacTimeOut value=\"30000\" />"+
	"	<DwaTimeOut value=\"10000\" />"+
	"	<DpaTimeOut value=\"5000\" />"+
	"	<RecTimeOut value=\"10000\" />"+
	"</Parameters>"+
	"<Network>"+
	"	<Peers>"+
	"		"+
	"		<Peer name=\"aaa://localhost:3868\" attempt_connect=\"true\" rating=\"1\" />"+
	"	</Peers>"+
	"	<Realms>"+
	"		<Realm name=\"mobicents.org\" peers=\"localhost\" local_action=\"LOCAL\""+
	"			dynamic=\"false\" exp_time=\"1\">"+
	"			<ApplicationID>"+
	"				<VendorId value=\"10415\" />"+
	"				<AuthApplId value=\"16777217\" />"+
	"				<AcctApplId value=\"0\" />"+
	"			</ApplicationID>"+
	"		</Realm>"+
	"	</Realms>"+
	"</Network>"+
	""+
	"<Extensions />"+
	"</Configuration>";
		
		this.sc=new StackCreator(config,this,this,"Client");
		try {
			super.sessionFactory=this.sc.getSessionFactory();
		} catch (IllegalDiameterStateException e) {
			
			e.printStackTrace();
		}
		
		((ISessionFactory) sessionFactory).registerAppFacory(ClientShSession.class,this);
		((ISessionFactory) sessionFactory).registerAppFacory(ServerShSession.class,this);

	}
	
	
	
	public void timeoutExpired(Request request) {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT timeoutExpired");
		
	}





	public void doProfileUpdateAnswerEvent(ClientShSession session,
			ProfileUpdateRequest request, ProfileUpdateAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doProfileUpdateAnswerEvent");
		doSubscribeScenario(true,session);
		
	}





	public void doPushNotificationRequestEvent(final ClientShSession session,
			PushNotificationRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doPushNotificationRequestEvent");
		Message msg = ((PushNotificationRequestImpl) request).getMessage();
		AvpSet set = msg.getAvps();
		System.out.println("REQEUST");
		/*
		 * for(Avp a:set) { try {
		 * System.out.println("AVPS1: "+a.getCode()+" --- > "+new
		 * String(a.getRaw())); } catch (AvpDataException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */
		PushNotificationAnswer answer = (PushNotificationAnswer) createPushNotificationAnswer(((Request) msg)
				.createAnswer(2001));
		set = answer.getMessage().getAvps();
		System.out.println("ANSWER");
		/*
		 * for(Avp a:set) { try { if(a.getCode()==293 || a.getCode()==283) {
		 * System
		 * .out.println("AVPS2: "+a.getCode()+" --- > "+a.getOctetString());
		 * }else { System.out.println("AVPS2: "+a.getCode()+" --- > "+new
		 * String(a.getRaw())); } } catch (AvpDataException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */

		session.sendPushNotificationAnswer(answer);
		
		
		t.schedule(new TimerTask(){

			@Override
			public void run() {
				try {
					
					ProfileUpdateRequest request=(ProfileUpdateRequest) createProfileUpdateRequest(session.getSessions().get(0).createRequest(ProfileUpdateRequestImpl.code,ApplicationId.createByAuthAppId(10415, 16777217),"mobicents.org", "aaa://localhost:3868"));
					AvpSet set=request.getMessage().getAvps();
					//System.out.println("REQEUST");
					//for(Avp a:set)
					//{
					//	try {
					//		System.out.println("AVPS1: "+a.getCode()+" --- > "+new String(a.getRaw()));
					//	} catch (AvpDataException e) {
					//		// TODO Auto-generated catch block
					//		e.printStackTrace();
					//	}
					//}
					set.addAvp(461,"service@mobicents.org",false);
					//set.addAvp(296,"mobicents.org",true);
					//set.addAvp(264,"aaa://127.0.0.1:1812",true);
					
					AvpSet vendorSpecificAppId=set.addGroupedAvp(260,0,true,false);
					
					vendorSpecificAppId.addAvp(10415, 16777217, true);
					vendorSpecificAppId.addAvp(258, 16777217, true);
					//vendorSpecificAppId.addAvp(259, 16777217, true);
					
					//We should add UserIdentity and data ref, but who cares now
					session.sendProfileUpdateRequest(request);
				} catch (InternalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalDiameterStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RouteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OverloadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}}, 9000);
	}





	public void doSubscribeNotificationsAnswerEvent(ClientShSession session,
			SubscribeNotificationsRequest request,
			SubscribeNotificationsAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doSubscribeNotificationsAnswerEvent");
		
	}





	public void doUserDataAnswerEvent(ClientShSession session,
			UserDataRequest request, UserDataAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doUserDataAnswerEvent");
		
	}





	public void doProfileUpdateRequestEvent(ServerShSession session,
			ProfileUpdateRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doProfileUpdateRequestEvent");
		
	}





	public void doPushNotificationAnswerEvent(ServerShSession session,
			PushNotificationRequest request, PushNotificationAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doUserDataRequestEvent");
		
	}





	public void doSubscribeNotificationsRequestEvent(ServerShSession session,
			SubscribeNotificationsRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doUserDataRequestEvent");
		
	}





	public void doUserDataRequestEvent(ServerShSession session,
			UserDataRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doUserDataRequestEvent");
		
	}
	
	
	
	


	public void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT doOtherEvent");

	}

	



	public Answer processRequest(Request request) {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT processRequest");
		return null;
	}



	public void receivedSuccessMessage(Request request, Answer answer) {
		System.out.println("--["+this.getClass().getSimpleName()+"]-- GOT receivedSuccessMessage");
		
	}

	
	public static void main(String args[])
	{
		System.out.println("--- START CLIENT");
		Client c=new Client();
		
		System.out.println("--- STARTED CLIENT");
		try {
			//c.sc.start();
			Network network = c.sc.unwrap(Network.class);
			network.addNetworkReqListener(c, ApplicationId.createByAuthAppId(10415, 16777217));
			
			Thread.currentThread().sleep(5000);
			
			
			c.doSubscribeScenario(false,null);
			//c.doUserDataRequestScenario();
		} catch (InternalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ApplicationAlreadyUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
	}


	private void doUserDataRequestScenario()
	{
		isSubscribeBased=false;
		try {
			ClientShSession session=((ISessionFactory) sc.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(10415, 16777217), ClientShSession.class, null);
			UserDataRequest request=(UserDataRequest) createUserDataRequest(session.getSessions().get(0).createRequest(UserDataRequestImpl.code,ApplicationId.createByAuthAppId(10415, 16777217),"mobicents.org", "aaa://localhost:3868"));
			AvpSet set=request.getMessage().getAvps();
			//System.out.println("REQEUST");
			//for(Avp a:set)
			//{
			//	try {
			//		System.out.println("AVPS1: "+a.getCode()+" --- > "+new String(a.getRaw()));
			//	} catch (AvpDataException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
			//}
			set.addAvp(461,"service@mobicents.org",false);
			//set.addAvp(296,"mobicents.org",true);
			//set.addAvp(264,"aaa://127.0.0.1:1812",true);
			
			AvpSet vendorSpecificAppId=set.addGroupedAvp(260,0,true,false);
			
			vendorSpecificAppId.addAvp(10415, 16777217, true);
			vendorSpecificAppId.addAvp(258, 16777217, true);
			//vendorSpecificAppId.addAvp(259, 16777217, true);
			
			//We should add UserIdentity and data ref, but who cares now
			session.sendUserDataRequest(request);
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
	
	private void doSubscribeScenario(boolean term,ClientShSession session)
	{
		isSubscribeBased=true;
		try {
			if(session==null)
				session=((ISessionFactory) sc.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(10415, 16777217), ClientShSession.class, null);
			SubscribeNotificationsRequest request=(SubscribeNotificationsRequest) createSubscribeNotificationsRequest(session.getSessions().get(0).createRequest(SubscribeNotificationsRequestImpl.code,ApplicationId.createByAuthAppId(10415, 16777217),"mobicents.org", "aaa://localhost:3868"));
			AvpSet set=request.getMessage().getAvps();
			//System.out.println("REQEUST");
			//for(Avp a:set)
			//{
			//	try {
			//		System.out.println("AVPS1: "+a.getCode()+" --- > "+new String(a.getRaw()));
			//	} catch (AvpDataException e) {
			//		// TODO Auto-generated catch block
			//		e.printStackTrace();
			//	}
			//}
			set.addAvp(461,"service@mobicents.org",false);
			//set.addAvp(296,"mobicents.org",true);
			//set.addAvp(264,"aaa://127.0.0.1:1812",true);
			
			AvpSet vendorSpecificAppId=set.addGroupedAvp(260,0,true,false);
			
			vendorSpecificAppId.addAvp(10415, 16777217, true);
			vendorSpecificAppId.addAvp(258, 16777217, true);
			if(!term)
				set.addAvp(705,0);
			else
				set.addAvp(705,1);
			//vendorSpecificAppId.addAvp(259, 16777217, true);
			
			//We should add UserIdentity and data ref, but who cares now
			session.sendSubscribeNotificationsRequest(request);
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDiameterStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}	
	

