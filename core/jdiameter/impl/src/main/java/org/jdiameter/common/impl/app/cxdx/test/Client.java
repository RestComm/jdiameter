/**
 * Start time:14:34:26 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.common.impl.app.cxdx.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationAlreadyUseException;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.ServerCxDxSession;
import org.jdiameter.api.cxdx.events.JLocationInfoAnswer;
import org.jdiameter.api.cxdx.events.JLocationInfoRequest;
import org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer;
import org.jdiameter.api.cxdx.events.JMultimediaAuthRequest;
import org.jdiameter.api.cxdx.events.JPushProfileAnswer;
import org.jdiameter.api.cxdx.events.JPushProfileRequest;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer;
import org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer;
import org.jdiameter.api.cxdx.events.JUserAuthorizationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.cxdx.JPushProfileRequestImpl;

/**
 * Start time:14:34:26 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
public class Client extends SessionFactoryCXDX implements NetworkReqListener, EventListener<Request, Answer> {

	private StackCreator sc;

	/**
	 * 
	 * @param sessionFactory
	 * @throws IOException
	 */
	public Client() throws IOException {
		super(null);

		StringBuffer sb = new StringBuffer();
		File f = new File("D:\\java\\jprojects\\diameterrelease\\diameter\\core\\jdiameter\\impl\\src\\main\\java\\org\\jdiameter\\common\\impl\\app\\cxdx\\test\\conf.xml");
		BufferedReader br = new BufferedReader(new FileReader(f));

		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		this.sc = new StackCreator(sb.toString(), this, this, "Client");
		try {
			super.sessionFactory = this.sc.getSessionFactory();
		} catch (IllegalDiameterStateException e) {

			e.printStackTrace();
		}

		((ISessionFactory) sessionFactory).registerAppFacory(ServerCxDxSession.class, this);
		((ISessionFactory) sessionFactory).registerAppFacory(ClientCxDxSession.class, this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api
	 * .Request)
	 */
	public Answer processRequest(Request request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doLocationInformationAnswer
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JLocationInfoRequest,
	 * org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
	 */
	public void doLocationInformationAnswer(ClientCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doLocationInformationAnswer");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doMultimediaAuthAnswer
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JMultimediaAuthRequest,
	 * org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
	 */
	public void doMultimediaAuthAnswer(ClientCxDxSession session, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doMultimediaAuthAnswer");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doOtherEvent(org.jdiameter
	 * .api.app.AppSession, org.jdiameter.api.app.AppRequestEvent,
	 * org.jdiameter.api.app.AppAnswerEvent)
	 */
	public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doOtherEvent");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doPushProfileRequest
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JPushProfileRequest,
	 * org.jdiameter.api.cxdx.events.JPushProfileAnswer)
	 */
	public void doPushProfileRequest(ClientCxDxSession session, JPushProfileRequest request, JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doPushProfileRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.api.cxdx.ClientCxDxSessionListener#
	 * doRegistrationTerminationRequest
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest,
	 * org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
	 */
	public void doRegistrationTerminationRequest(ClientCxDxSession session, JRegistrationTerminationRequest request, JRegistrationTerminationAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doRegistrationTerminationRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doServerAssignmentAnswer
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JServerAssignmentRequest,
	 * org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
	 */
	public void doServerAssignmentAnswer(ClientCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doServerAssignmentAnswer");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ClientCxDxSessionListener#doUserAuthorizationAnswer
	 * (org.jdiameter.api.cxdx.ClientCxDxSession,
	 * org.jdiameter.api.cxdx.events.JUserAuthorizationRequest,
	 * org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
	 */
	public void doUserAuthorizationAnswer(ClientCxDxSession session, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doUserAuthorizationAnswer");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doLocationInformationRequest
	 * (org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JLocationInfoRequest,
	 * org.jdiameter.api.cxdx.events.JLocationInfoAnswer)
	 */
	public void doLocationInformationRequest(ServerCxDxSession session, JLocationInfoRequest request, JLocationInfoAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doLocationInformationRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doMultimediaAuthRequest
	 * (org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JMultimediaAuthRequest,
	 * org.jdiameter.api.cxdx.events.JMultimediaAuthAnswer)
	 */
	public void doMultimediaAuthRequest(ServerCxDxSession session, JMultimediaAuthRequest request, JMultimediaAuthAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doMultimediaAuthRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doPushProfileAnswer(
	 * org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JPushProfileRequest,
	 * org.jdiameter.api.cxdx.events.JPushProfileAnswer)
	 */
	public void doPushProfileAnswer(ServerCxDxSession session, JPushProfileRequest request, JPushProfileAnswer answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doPushProfileAnswer");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jdiameter.api.cxdx.ServerCxDxSessionListener#
	 * doRegistrationTerminationAnswer(org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JRegistrationTerminationRequest,
	 * org.jdiameter.api.cxdx.events.JRegistrationTerminationAnswer)
	 */
	public void doRegistrationTerminationAnswer(ServerCxDxSession session, JRegistrationTerminationRequest request, JRegistrationTerminationAnswer answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doRegistrationTerminationAnswer");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doServerAssignmentRequest
	 * (org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JServerAssignmentRequest,
	 * org.jdiameter.api.cxdx.events.JServerAssignmentAnswer)
	 */
	public void doServerAssignmentRequest(ServerCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doServerAssignmentRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.cxdx.ServerCxDxSessionListener#doUserAuthorizationRequest
	 * (org.jdiameter.api.cxdx.ServerCxDxSession,
	 * org.jdiameter.api.cxdx.events.JUserAuthorizationRequest,
	 * org.jdiameter.api.cxdx.events.JUserAuthorizationAnswer)
	 */
	public void doUserAuthorizationRequest(ServerCxDxSession session, JUserAuthorizationRequest request, JUserAuthorizationAnswer answer) throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT doUserAuthorizationRequest");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.
	 * api.Message, org.jdiameter.api.Message)
	 */
	public void receivedSuccessMessage(Request request, Answer answer) {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT receivedSuccessMessage");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
	 */
	public void timeoutExpired(Request request) {
		System.out.println("--[" + this.getClass().getSimpleName() + "]-- GOT timeoutExpired");

	}

	public static void main(String args[]) {

		Client c = null;
		try {
			c = new Client();

			Network network = c.sc.unwrap(Network.class);
			// s.sc.start();
			network.addNetworkReqListener(c, ApplicationId.createByAuthAppId(10415, 16777216));
			Thread.sleep(5000);
			List<Peer> peers = c.sc.unwrap(PeerTable.class).getPeerTable();
			System.err.println("ooooo "+peers.size());
			for(Peer p:peers)
			{
				System.err.println(p.getUri()+" "+p.getRealmName()+" "+Arrays.toString(p.getIPAddresses()));
			}
			//c.sendRTR();
			c.sendPPR();
			Thread.currentThread().sleep(10000);

		} catch (InternalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ApplicationAlreadyUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (c != null) {
				try {
					System.err.println("--- KILL ---");
					c.sc.stop(1000, TimeUnit.MILLISECONDS);
				} catch (IllegalDiameterStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InternalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 
	 */
	private void sendPPR() {
		// TODO Auto-generated method stub
		try {
			ServerCxDxSession session = ((ISessionFactory) sc.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(10415L, 16777216L), ServerCxDxSession.class, null);
			JPushProfileRequestImpl rtr = (JPushProfileRequestImpl) this.createPushProfileRequest(session.getSessions().get(0).createRequest(JPushProfileRequestImpl.code, ApplicationId.createByAuthAppId(10415, 16777216),"mobicents.org", "aaa://127.0.0.1:1812"));
			AvpSet avpSet = rtr.getMessage().getAvps();
			
			//Auth-Session-State
			avpSet.addAvp(277,0);
			
			//User-Name 
			avpSet.addAvp(1,"KillerUserName",false);
			session.sendPushProfileRequest(rtr);

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

	

	/**
	 * 
	 */
	private void sendRTR() {
		try {
			ServerCxDxSession session = ((ISessionFactory) sc.getSessionFactory()).getNewAppSession(null, ApplicationId.createByAuthAppId(10415L, 16777216L), ServerCxDxSession.class, null);
			JRegistrationTerminationRequest rtr = (JRegistrationTerminationRequest) this.createRegistrationTerminationRequest(session.getSessions().get(0).createRequest(JRegistrationTerminationRequest.code, ApplicationId.createByAuthAppId(10415, 16777216),"mobicents.org", "aaa://127.0.0.1:1812"));
			AvpSet avpSet = rtr.getMessage().getAvps();
			
			//Auth-Session-State
			avpSet.addAvp(277,0);
			
			//User-Name 
			avpSet.addAvp(1,"KillerUserName",false);
			session.sendRegistrationTerminationRequest(rtr);

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
