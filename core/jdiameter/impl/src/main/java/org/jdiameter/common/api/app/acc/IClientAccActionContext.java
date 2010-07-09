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
package org.jdiameter.common.api.app.acc;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.Request;
import org.jdiameter.api.acc.ClientAccSession;

/**
 * Diameter Accounting Client Additional listener
 * Actions for FSM
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IClientAccActionContext {

  /**
   * Filling nested avp into interim message
   * @param interimRequest instance of interim message which will be sent to server
   */
  void interimIntervalElapses(ClientAccSession appSession,Request interimRequest) throws InternalException;

  /**
   * Call back for failed_send_record event
   * @param accRequest accounting request record
   * @return true if you want put message to buffer and false if you want to stop processing
   */
  boolean failedSendRecord(ClientAccSession appSession,Request accRequest) throws InternalException;

  /**
   * Filling nested avp into STR
   * @param sessionTermRequest instance of STR which will be sent to server
   */
  void disconnectUserOrDev(ClientAccSession appSession,Request sessionTermRequest) throws InternalException;
}
