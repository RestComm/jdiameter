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
package org.mobicents.diameter.stack.functional.cxdx.base;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.cxdx.ClientCxDxSession;
import org.jdiameter.api.cxdx.events.JServerAssignmentAnswer;
import org.jdiameter.api.cxdx.events.JServerAssignmentRequest;
import org.jdiameter.common.impl.app.cxdx.JServerAssignmentRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.cxdx.AbstractClient;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ClientSAR extends AbstractClient {

  protected boolean receivedServerAssignment;
  protected boolean sentServerAssignment;

  /**
   *
   */
  public ClientSAR() {
  }

  public void sendServerAssignment() throws Exception {
    JServerAssignmentRequest request = new JServerAssignmentRequestImpl(super.createRequest(this.clientCxDxSession, JServerAssignmentRequest.code));
    AvpSet reqSet = request.getMessage().getAvps();
    // <Server-Assignment-Request> ::= < Diameter Header: 301, REQ, PXY, 16777216 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    // { Origin-Host }
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // [ User-Name ]
    // [ OC-Supported-Features ]
    // *[ Supported-Features ]
    // *[ Public-Identity ]
    // [ Wildcarded-Public-Identity ]
    // { Server-Name }
    reqSet.addAvp(Avp.SERVER_NAME, "ala", getApplicationId().getVendorId(), true, false, false);
    // { Server-Assignment-Type }
    reqSet.addAvp(Avp.SERVER_ASSIGNMENT_TYPE, 1, getApplicationId().getVendorId(), true, false, false);
    // { User-Data-Already-Available }
    reqSet.addAvp(Avp.USER_DATA_ALREADY_AVAILABLE, 1, getApplicationId().getVendorId(), true, false, false);
    // [ SCSCF-Restoration-Info ]
    // [ Multiple-Registration-Indication ]
    // [ Session-Priority ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    this.clientCxDxSession.sendServerAssignmentRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentServerAssignment = true;
  }

  @Override
  public void doServerAssignmentAnswer(ClientCxDxSession session, JServerAssignmentRequest request, JServerAssignmentAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    if (this.receivedServerAssignment) {
      fail("Received SAA more than once", null);
      return;
    }

    this.receivedServerAssignment = true;
  }

  public boolean isReceivedServerAssignment() {
    return receivedServerAssignment;
  }

  public boolean isSentServerAssignment() {
    return sentServerAssignment;
  }

}
