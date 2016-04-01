/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional.s13;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.s13.ClientS13Session;
import org.jdiameter.api.s13.ServerS13Session;
import org.jdiameter.api.s13.ServerS13SessionListener;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.api.s13.events.JMEIdentityCheckRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.common.impl.app.s13.JMEIdentityCheckAnswerImpl;
import org.jdiameter.common.impl.app.s13.S13SessionFactoryImpl;
import org.mobicents.diameter.stack.functional.TBase;

/**
 * @author baranowb
 *
 */
public abstract class AbstractServer extends TBase implements ServerS13SessionListener {

	// NOTE: implementing NetworkReqListener since its required for stack to
	// know we support it... ech.

	protected ServerS13Session serverS13Session;

	public void init(InputStream configStream, String clientID) throws Exception {
		try {
			super.init(configStream, clientID, ApplicationId.createByAuthAppId(10415, 16777252));
			S13SessionFactoryImpl s13SessionFactory = new S13SessionFactoryImpl(this.sessionFactory);
			((ISessionFactory) sessionFactory).registerAppFacory(ServerS13Session.class, s13SessionFactory);
			((ISessionFactory) sessionFactory).registerAppFacory(ClientS13Session.class, s13SessionFactory);
			s13SessionFactory.setServerSessionListener(this);
		}
		finally {
			try {
				configStream.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// ----------- delegate methods so

	public void start() throws IllegalDiameterStateException, InternalException {
		stack.start();
	}

	public void start(Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
		stack.start(mode, timeOut, timeUnit);
	}

	public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
		stack.stop(timeOut, timeUnit, disconnectCause);
	}

	public void stop(int disconnectCause) {
		stack.stop(disconnectCause);
	}

	public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer) throws InternalException, IllegalDiameterStateException, RouteException,
			OverloadException {
		fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
	}

	public void doMEIdentityCheckRequestEvent(ServerS13Session session, JMEIdentityCheckRequest request) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
		fail("Received \"ECR\" event, request[" + request + "], on session[" + session + "]", null);
	}

	// -------- conf

	public String getSessionId() {
		return this.serverS13Session.getSessionId();
	}

	public void fetchSession(String sessionId) throws InternalException {
		this.serverS13Session = stack.getSession(sessionId, ServerS13Session.class);
	}


	public ServerS13Session getSession() {
		return this.serverS13Session;
	}

	protected abstract int getEquipmentStatus();

	// ----------- helper

	public JMEIdentityCheckAnswer createECA(JMEIdentityCheckRequest ecr, long resultCode) throws Exception {
	/*
	 <ME-Identity-Check-Answer>::=<Diameter Header:324,PXY,16777252>
					< Session-Id >
					[ Vendor-Specific-Application-Id ]
					[ Result-Code ]
					[ Experimental-Result ]
					{ Auth-Session-State }
					{ Origin-Host }
					{ Origin-Realm }
					[ Equipment-Status ]
				 *[ AVP ]
				 *[ Failed-AVP ]
				 *[ Proxy-Info ]
				 *[ Route-Record ]
	 */
		JMEIdentityCheckAnswer eca = new JMEIdentityCheckAnswerImpl((Request) ecr.getMessage(), resultCode);

		AvpSet reqSet = ecr.getMessage().getAvps();
		AvpSet set = eca.getMessage().getAvps();
		set.removeAvp(Avp.DESTINATION_HOST);
		set.removeAvp(Avp.DESTINATION_REALM);
			set.addAvp(reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

			// { Vendor-Specific-Application-Id }
			if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
				AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
				// 1* [ Vendor-Id ]
				vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
				// 0*1{ Auth-Application-Id }
				vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
			}
			// [ Result-Code ]
			// [ Experimental-Result ]
			// { Auth-Session-State }
			if (set.getAvp(Avp.AUTH_SESSION_STATE) == null) {
				set.addAvp(Avp.AUTH_SESSION_STATE, 1);
			}

		// Equipment-Status
		if (getEquipmentStatus() >= 0) {
			// Avp addAvp(int avpCode, long value, long vendorId, boolean mFlag, boolean pFlag);
			set.addAvp(Avp.EQUIPMENT_STATUS, getEquipmentStatus(), 10415, false, false);
		}
		return eca;
	}
}
