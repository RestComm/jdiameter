/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.common.api.app.ro;

import org.jdiameter.api.Message;
import org.jdiameter.api.ro.ClientRoSession;

/**
 * Diameter Credit Control Application Client Additional listener
 * Actions for FSM
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface IClientRoSessionContext {

  long getDefaultTxTimerValue();

  void txTimerExpired(ClientRoSession session);

  int getDefaultCCFHValue();

  int getDefaultDDFHValue();

  void grantAccessOnDeliverFailure(ClientRoSession clientCCASessionImpl,Message request);

  void denyAccessOnDeliverFailure(ClientRoSession clientCCASessionImpl,Message request);

  void grantAccessOnTxExpire(ClientRoSession clientCCASessionImpl);

  void denyAccessOnTxExpire(ClientRoSession clientCCASessionImpl);

  void grantAccessOnFailureMessage(ClientRoSession clientCCASessionImpl);

  void denyAccessOnFailureMessage(ClientRoSession clientCCASessionImpl);

  void indicateServiceError(ClientRoSession clientCCASessionImpl);

}
