/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.common.api.app.rf;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.Request;
import org.jdiameter.api.rf.ClientRfSession;

/**
 * Diameter Accounting Client Additional listener
 * Actions for FSM
 * 
 *  
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IClientRfActionContext {

  /**
   * Filling nested avp into interim message
   * @param interimRequest instance of interim message which will be sent to server
   */
  void interimIntervalElapses(ClientRfSession appSession,Request interimRequest) throws InternalException;

  /**
   * Call back for failed_send_record event
   * @param accRequest accounting request record
   * @return true if you want put message to buffer and false if you want to stop processing
   */
  boolean failedSendRecord(ClientRfSession appSession,Request accRequest) throws InternalException;

  /**
   * Filling nested avp into STR
   * @param sessionTermRequest instance of STR which will be sent to server
   */
  void disconnectUserOrDev(ClientRfSession appSession,Request sessionTermRequest) throws InternalException;
}
