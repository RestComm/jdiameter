package org.mobicents.tests.diameter.slh;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Network;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.api.slh.ClientSLhSession;
import org.jdiameter.api.slh.ClientSLhSessionListener;
import org.jdiameter.api.slh.ServerSLhSession;
import org.jdiameter.api.slh.ServerSLhSessionListener;
import org.jdiameter.api.slh.events.LCSRoutingInfoAnswer;
import org.jdiameter.api.slh.events.LCSRoutingInfoRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slh.ISLhMessageFactory;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoAnswerImpl;
import org.jdiameter.common.impl.app.slh.LCSRoutingInfoRequestImpl;
import org.jdiameter.common.impl.app.slh.SLhSessionFactoryImpl;
import org.jdiameter.server.impl.app.slh.SLhServerSessionImpl;
import org.mobicents.tests.diameter.AbstractStackRunner;

public class SLH extends AbstractStackRunner implements ServerSLhSessionListener, ClientSLhSessionListener,
		StateChangeListener<AppSession>, ISLhMessageFactory {

	private ApplicationId slhAuthApplicationId = ApplicationId.createByAuthAppId(10415, 16777291);
	private SLhSessionFactoryImpl slhSessionFactory;

	public SLH() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(InputStream f) throws Exception {
		// TODO Auto-generated method stub
		super.configure(f);

		this.slhSessionFactory = new SLhSessionFactoryImpl(super.factory);
		this.slhSessionFactory.setClientSLhSessionListener(this);
		this.slhSessionFactory.setServerSLhSessionListener(this);

		Network network = stack.unwrap(Network.class);
		network.addNetworkReqListener(this, slhAuthApplicationId);
		((ISessionFactory) super.factory).registerAppFacory(ServerSLhSession.class, this.slhSessionFactory);
		((ISessionFactory) super.factory).registerAppFacory(ClientSLhSession.class, this.slhSessionFactory);
	}

	public Answer processRequest(Request request) {

		int commandCode = request.getCommandCode();
		if (commandCode != 8388622) {
			if (log.isEnabledFor(Level.ERROR)) {
				log.error("Received command with wrong code: " + commandCode);
				super.dumpMessage(request, false);
			}
			return null;
		}

		if (commandCode == 8388622) {
			try {
				SLhServerSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
						slhAuthApplicationId, ServerSLhSession.class, null);
				// session.
				session.addStateChangeNotification(this);
				session.processRequest(request);
			} catch (InternalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void receivedSuccessMessage(Request arg0, Answer arg1) {
		if (super.log.isEnabledFor(Level.ERROR)) {
			super.log.error("Received answer");
			dumpMessage(arg1, false);
			new Exception().printStackTrace();
		}

	}

	public void timeoutExpired(Request arg0) {
		if (super.log.isInfoEnabled()) {
			super.log.info("Timeout expired");
			dumpMessage(arg0, true);
		}

	}

	public void stateChanged(Enum arg0, Enum arg1) {
		if (log.isDebugEnabled()) {
			log.debug("State changed from[" + arg0 + "] to[" + arg1 + "]");

		}
	}

	public void stateChanged(AppSession source, Enum arg0, Enum arg1) {
		this.stateChanged(arg0, arg1);
	}

	public long getApplicationId() {
		return this.slhAuthApplicationId.getAuthAppId();
	}

	public long getMessageTimeout() {
		return 5000;
	}

	@Override
	public AppRequestEvent createLCSRoutingInfoRequest(Request request) {
		return new LCSRoutingInfoRequestImpl(request);
	}

	@Override
	public AppAnswerEvent createLCSRoutingInfoAnswer(Answer answer) {
		return new LCSRoutingInfoAnswerImpl(answer);
	}

	@Override
	public void doLCSRoutingInfoAnswerEvent(ClientSLhSession session,
			LCSRoutingInfoRequest request, LCSRoutingInfoAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		if (log.isEnabledFor(Level.DEBUG)) {
			log.error("Received RIA");
			super.dumpMessage(request.getMessage(), false);
		}
	}

	@Override
	public void doLCSRoutingInfoRequestEvent(ServerSLhSession session,
			LCSRoutingInfoRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		try {

			// create answer, we will do that always
			Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.slhAuthApplicationId);
			AvpSet set = answer.getAvps();

			// Auth-Session-State
			set.addAvp(277, 0);

			if (log.isDebugEnabled()) {
				log.info("Recievend RIR in App Session.");
				super.dumpMessage(request.getMessage(), false);
				log.info("Sending RIA in App Session.");
				super.dumpMessage(answer, true);
			}

			session.sendLCSRoutingInfoAnswer((LCSRoutingInfoAnswer) this.createLCSRoutingInfoAnswer(answer));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
	}

}
