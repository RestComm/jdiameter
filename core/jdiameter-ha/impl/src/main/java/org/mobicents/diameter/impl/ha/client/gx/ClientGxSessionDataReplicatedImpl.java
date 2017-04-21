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

package org.mobicents.diameter.impl.ha.client.gx;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.restcomm.cache.FqnWrapper;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.gx.ClientGxSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.impl.app.gx.IClientGxSessionData;
import org.jdiameter.common.api.app.gx.ClientGxSessionState;
import org.restcomm.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientGxSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientGxSessionData {

  private static final Logger logger = LoggerFactory.getLogger(ClientGxSessionDataReplicatedImpl.class);

  private static final String EVENT_BASED = "EVENT_BASED";
  private static final String REQUEST_TYPE = "REQUEST_TYPE";
  private static final String STATE = "STATE";
  private static final String TXTIMER_ID = "TXTIMER_ID";
  private static final String TXTIMER_REQUEST = "TXTIMER_REQUEST";
  private static final String BUFFER = "BUFFER";
  private static final String GRA = "GRA";
  private static final String GDDFH = "GDDFH";
  private static final String GCCFH = "GCCFH";

  private IMessageParser messageParser;

  /**
   * @param nodeFqnWrapper
   * @param mobicentsCluster
   * @param container
   */
  public ClientGxSessionDataReplicatedImpl(FqnWrapper nodeFqnWrapper, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqnWrapper, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ClientGxSession.class);
      setClientGxSessionState(ClientGxSessionState.IDLE);
    }

    this.messageParser = container.getAssemblerFacility().getComponentInstance(IMessageParser.class);
  }

  /**
   * @param sessionId
   * @param mobicentsCluster
   * @param container
   */
  public ClientGxSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(
      FqnWrapper.fromRelativeElementsWrapper(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId),
      mobicentsCluster, container
    );
  }

  @Override
  public boolean isEventBased() {
    if (exists()) {
      return toPrimitive((Boolean) getNodeValue(EVENT_BASED), true);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setEventBased(boolean isEventBased) {
    if (exists()) {
      putNodeValue(EVENT_BASED, isEventBased);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public boolean isRequestTypeSet() {
    if (exists()) {
      return toPrimitive((Boolean) getNodeValue(REQUEST_TYPE), false);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setRequestTypeSet(boolean requestTypeSet) {
    if (exists()) {
      putNodeValue(REQUEST_TYPE, requestTypeSet);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public ClientGxSessionState getClientGxSessionState() {
    if (exists()) {
      return (ClientGxSessionState) getNodeValue(STATE);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setClientGxSessionState(ClientGxSessionState state) {
    if (exists()) {
      putNodeValue(STATE, state);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public Serializable getTxTimerId() {
    if (exists()) {
      return (Serializable) getNodeValue(TXTIMER_ID);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setTxTimerId(Serializable txTimerId) {
    if (exists()) {
      putNodeValue(TXTIMER_ID, txTimerId);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public Request getTxTimerRequest() {
    if (exists()) {

      byte[] data = (byte[]) getNodeValue(TXTIMER_REQUEST);
      if (data != null) {
        try {
          return this.messageParser.createMessage(ByteBuffer.wrap(data));
        }
        catch (AvpDataException e) {
          logger.error("Unable to recreate Tx Timer Request from buffer.");
          return null;
        }
      }
      else {
        return null;
      }

    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setTxTimerRequest(Request txTimerRequest) {
    if (exists()) {
      if (txTimerRequest != null) {

        try {
          byte[] data = this.messageParser.encodeMessage((IMessage) txTimerRequest).array();
          putNodeValue(TXTIMER_REQUEST, data);
        }
        catch (ParseException e) {
          logger.error("Unable to encode Tx Timer Request to buffer.");
        }
      }
      else {
        removeNodeValue(TXTIMER_REQUEST);
      }
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public Request getBuffer() {
    byte[] data = (byte[]) getNodeValue(BUFFER);
    if (data != null) {
      try {
        return this.messageParser.createMessage(ByteBuffer.wrap(data));
      }
      catch (AvpDataException e) {
        logger.error("Unable to recreate message from buffer.");
        return null;
      }
    }
    else {
      return null;
    }
  }

  @Override
  public void setBuffer(Request buffer) {
    if (buffer != null) {
      try {
        byte[] data = this.messageParser.encodeMessage((IMessage) buffer).array();
        putNodeValue(BUFFER, data);
      }
      catch (ParseException e) {
        logger.error("Unable to encode message to buffer.");
      }
    }
    else {
      removeNodeValue(BUFFER);
    }
  }

  @Override
  public int getGatheredRequestedAction() {
    if (exists()) {
      return toPrimitive((Integer) getNodeValue(GRA));
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setGatheredRequestedAction(int gatheredRequestedAction) {
    if (exists()) {
      putNodeValue(GRA, gatheredRequestedAction);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public int getGatheredCCFH() {
    if (exists()) {
      return toPrimitive((Integer) getNodeValue(GCCFH));
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setGatheredCCFH(int gatheredCCFH) {
    if (exists()) {
      putNodeValue(GCCFH, gatheredCCFH);
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public int getGatheredDDFH() {
    if (exists()) {
      return toPrimitive((Integer) getNodeValue(GDDFH));
    }
    else {
      throw new IllegalStateException();
    }
  }

  @Override
  public void setGatheredDDFH(int gatheredDDFH) {
    if (exists()) {
      putNodeValue(GDDFH, gatheredDDFH);
    }
    else {
      throw new IllegalStateException();
    }
  }

}
