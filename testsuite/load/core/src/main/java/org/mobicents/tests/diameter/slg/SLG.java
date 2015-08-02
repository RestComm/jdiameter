package org.mobicents.tests.diameter.slg;

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
import org.jdiameter.api.slg.ClientSLgSession;
import org.jdiameter.api.slg.ClientSLgSessionListener;
import org.jdiameter.api.slg.ServerSLgSession;
import org.jdiameter.api.slg.ServerSLgSessionListener;
import org.jdiameter.api.slg.events.LocationReportAnswer;
import org.jdiameter.api.slg.events.LocationReportRequest;
import org.jdiameter.api.slg.events.ProvideLocationAnswer;
import org.jdiameter.api.slg.events.ProvideLocationRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.api.app.slg.ISLgMessageFactory;
import org.jdiameter.common.impl.app.slg.LocationReportAnswerImpl;
import org.jdiameter.common.impl.app.slg.LocationReportRequestImpl;
import org.jdiameter.common.impl.app.slg.ProvideLocationAnswerImpl;
import org.jdiameter.common.impl.app.slg.ProvideLocationRequestImpl;
import org.jdiameter.common.impl.app.slg.SLgSessionFactoryImpl;
import org.jdiameter.server.impl.app.slg.SLgServerSessionImpl;
import org.mobicents.tests.diameter.AbstractStackRunner;

public class SLG extends AbstractStackRunner implements ServerSLgSessionListener, ClientSLgSessionListener,
		StateChangeListener<AppSession>, ISLgMessageFactory {

	private ApplicationId slgAuthApplicationId = ApplicationId.createByAuthAppId(10415, 16777255);
	private SLgSessionFactoryImpl slgSessionFactory;

	public SLG() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(InputStream f) throws Exception {
		// TODO Auto-generated method stub
		super.configure(f);

		this.slgSessionFactory = new SLgSessionFactoryImpl(super.factory);
		this.slgSessionFactory.setClientSLgSessionListener(this);
		this.slgSessionFactory.setServerSLgSessionListener(this);

		Network network = stack.unwrap(Network.class);
		network.addNetworkReqListener(this, slgAuthApplicationId);
		((ISessionFactory) super.factory).registerAppFacory(ServerSLgSession.class, this.slgSessionFactory);
		((ISessionFactory) super.factory).registerAppFacory(ClientSLgSession.class, this.slgSessionFactory);
	}

	public Answer processRequest(Request request) {

		int commandCode = request.getCommandCode();
		if (commandCode != 8388620 || commandCode != 8388621) {
			if (log.isEnabledFor(Level.ERROR)) {
				log.error("Received command with wrong code: " + commandCode);
				super.dumpMessage(request, false);
			}
			return null;
		}

		if (commandCode == 8388620 || commandCode == 8388621) {
			try {
				SLgServerSessionImpl session = ((ISessionFactory) super.factory).getNewAppSession(request.getSessionId(),
						slgAuthApplicationId, ServerSLgSession.class, null);
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
		return this.slgAuthApplicationId.getAuthAppId();
	}

	public long getMessageTimeout() {
		return 5000;
	}

	@Override
	public AppRequestEvent createProvideLocationRequest(Request request) {
		return new ProvideLocationRequestImpl(request);
	}

	@Override
	public AppRequestEvent createLocationReportRequest(Request request) {
		return new LocationReportRequestImpl(request);
	}

	@Override
	public AppAnswerEvent createProvideLocationAnswer(Answer answer) {
		return new ProvideLocationAnswerImpl(answer);
	}

	@Override
	public AppAnswerEvent createLocationReportAnswer(Answer answer) {
		return new LocationReportAnswerImpl(answer);
	}

	@Override
	public void doProvideLocationAnswerEvent(ClientSLgSession session,
			ProvideLocationRequest request, ProvideLocationAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		if (log.isEnabledFor(Level.DEBUG)) {
			log.error("Received PLA");
			super.dumpMessage(request.getMessage(), false);
		}
	}

	@Override
	public void doLocationReportRequestEvent(ClientSLgSession session,
			LocationReportRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		try {
			// create answer, we will do that always
			Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.slgAuthApplicationId);
			AvpSet set = answer.getAvps();
	
			// Auth-Session-State
			set.addAvp(277, 0);
	
			if (log.isDebugEnabled()) {
				log.info("Recievend LRR in App Session.");
				super.dumpMessage(request.getMessage(), false);
				log.info("Sending LRA in App Session.");
				super.dumpMessage(answer, true);
			}
	
			session.sendLocationReportAnswer((LocationReportAnswer) this.createLocationReportAnswer(answer));
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doProvideLocationRequestEvent(ServerSLgSession session,
			ProvideLocationRequest request) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
		try {

			// create answer, we will do that always
			Answer answer = (Answer) super.createAnswer((Request) request.getMessage(), 2001, this.slgAuthApplicationId);
			AvpSet set = answer.getAvps();

			// Auth-Session-State
			set.addAvp(277, 0);

			if (log.isDebugEnabled()) {
				log.info("Recievend PLR in App Session.");
				super.dumpMessage(request.getMessage(), false);
				log.info("Sending PLA in App Session.");
				super.dumpMessage(answer, true);
			}

			session.sendProvideLocationAnswer((ProvideLocationAnswer) this.createProvideLocationAnswer(answer));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doLocationReportAnswerEvent(ServerSLgSession session,
			LocationReportRequest request, LocationReportAnswer answer)
			throws InternalException, IllegalDiameterStateException,
			RouteException, OverloadException {
		if (log.isEnabledFor(Level.DEBUG)) {
			log.error("Received LRA");
			super.dumpMessage(request.getMessage(), false);
		}
	}

	@Override
	public void doOtherEvent(AppSession session, AppRequestEvent request,
			AppAnswerEvent answer) throws InternalException,
			IllegalDiameterStateException, RouteException, OverloadException {
	}
}
