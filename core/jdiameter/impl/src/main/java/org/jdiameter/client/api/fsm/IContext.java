 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.client.api.fsm;

import java.io.IOException;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.OverloadException;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.io.TransportException;

/**
 * This interface describe operations of FSM context object
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IContext {

  /**
   * Start connection procedure to remote peer
   * @throws InternalException
   * @throws IOException
   * @throws org.jdiameter.api.IllegalDiameterStateException
   */
  void connect() throws InternalException, IOException, IllegalDiameterStateException;

  /**
   * Start disconnect procedure from remote peer
   * @throws InternalException
   * @throws org.jdiameter.api.IllegalDiameterStateException
   */
  void disconnect() throws InternalException, IllegalDiameterStateException;

  /**
   * This method allow sent message to remote peer
   * @param message message which one should be sent to remote peer
   * @throws TransportException
   * @throws OverloadException
   */
  boolean sendMessage(IMessage message) throws TransportException, OverloadException;

  /**
   * This method allow sent CER command to remote peer
   * @throws TransportException
   * @throws OverloadException
   */
  void sendCerMessage() throws TransportException, OverloadException;

  /**
   * This method allow sent CEA command to remote peer
   * @param resultCode value for result-code Avp
   * @param errMessage value for error-message Avp
   * @throws TransportException
   * @throws OverloadException
   */
  void sendCeaMessage(int resultCode, Message cer, String errMessage) throws TransportException, OverloadException;

  /**
   * This method allow sent DWR command to remote peer
   * @throws TransportException
   * @throws OverloadException
   */
  void sendDwrMessage() throws TransportException, OverloadException;

  /**
   * This method allow sent DWA command to remote peer
   * @param dwr parent DWR command receved from remote peer
   * @param resultCode value for result-code avp
   * @param errorMessage value for error-message avp
   * @throws TransportException
   * @throws OverloadException
   */
  void sendDwaMessage(IMessage dwr, int resultCode, String errorMessage) throws TransportException, OverloadException;

  /**
   * This method allow sent DPR command to remote peer
   * @param disconnectCause value for disconnect-cause avp
   * @throws TransportException
   * @throws OverloadException
   */
  void sendDprMessage(int disconnectCause) throws TransportException, OverloadException;

  /**
   * This method allow sent DPA command to remote peer
   * @param dpr  parent DPR command receved from remote peer
   * @param resultCode value for result-code avp
   * @param errorMessage value for error-message avp
   * @throws TransportException
   * @throws OverloadException
   */
  void sendDpaMessage(IMessage dpr, int resultCode, String errorMessage) throws TransportException, OverloadException;

  /**
   * This method allow processed message from to remote peer
   * @param iMessage message from  remote peer
   * @return true if message correct processed
   */
  boolean receiveMessage(IMessage iMessage);

  /**
   * This method call when peer instance receive DWR event
   * @param iMessage message
   * @return result code With this code stack will be send DWA message
   */
  int processDwrMessage(IMessage iMessage);

  /**
   * This method call when peer instance receive DPR event
   * @param iMessage message
   * @return result code With this code stack will be send DPA message
   */
  int processDprMessage(IMessage iMessage);

  /**
   * This method allow sent CEA command to remote peer
   * @param key connection key (host + ":" + port)
   * @param message
   * @return true if the message is sent to remote peer
   */
  boolean processCeaMessage(String key, IMessage message);

  /**
   *  This method allow processed CER command from remote peer
   * @param key connection key (host + ":" + port)
   * @param message received from remote host
   * @return result-code for CEA message or -1 if message can not be processed
   */
  int processCerMessage(String key, IMessage message);

  /**
   * Return true if connection should be restored
   * Look AttemptToConnect property of peer
   * @return true if connection should be restored
   */
  boolean isRestoreConnection();

  /**
   * Reeturn true if connection already created and connected
   * @return  true if connection  already created and connected
   */
  boolean isConnected();

  /**
   * Return parent peer description
   * @return parent peer description
   */
  String getPeerDescription();

  /**
   * Clears statistics for context
   */
  void removeStatistics();

  /**
   * Creates statistics for context
   */
  void createStatistics();
}
