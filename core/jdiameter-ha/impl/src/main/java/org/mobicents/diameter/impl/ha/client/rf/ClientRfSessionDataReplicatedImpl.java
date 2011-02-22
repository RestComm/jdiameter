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
package org.mobicents.diameter.impl.ha.client.rf;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jboss.cache.Fqn;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.rf.ClientRfSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.impl.app.rf.IClientRfSessionData;
import org.jdiameter.common.api.app.rf.ClientRfSessionState;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.mobicents.diameter.impl.ha.data.ReplicatedSessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ClientRfSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements IClientRfSessionData {

  private static final Logger logger = LoggerFactory.getLogger(ClientRfSessionDataReplicatedImpl.class);

  private static final String STATE = "STATE";
  private static final String BUFFER = "BUFFER";
  private static final String TS_TIMERID = "TS_TIMERID";
  private static final String DESTINATION_HOST = "DESTINATION_HOST";
  private static final String DESTINATION_REALM = "DESTINATION_REALM";

  private IMessageParser messageParser;

  /**
   * @param nodeFqn
   * @param mobicentsCluster
   * @param iface
   */
  public ClientRfSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster);

    if (super.create()) {
      setAppSessionIface(this, ClientRfSession.class);
      setClientRfSessionState(ClientRfSessionState.IDLE);
    }

    this.messageParser = container.getAssemblerFacility().getComponentInstance(IMessageParser.class);
  }

  /**
   * @param sessionId
   * @param mobicentsCluster
   * @param iface
   */
  public ClientRfSessionDataReplicatedImpl(String sessionId, MobicentsCluster mobicentsCluster, IContainer container) {
    this(Fqn.fromRelativeElements(ReplicatedSessionDatasource.SESSIONS_FQN, sessionId), mobicentsCluster, container);
  }

  public ClientRfSessionState getClientRfSessionState() {
    if (exists()) {
      return (ClientRfSessionState) getNode().get(STATE);
    }
    else {
      throw new IllegalStateException();
    }
  }

  public void setClientRfSessionState(ClientRfSessionState state) {
    if (exists()) {
      getNode().put(STATE, state);
    }
    else {
      throw new IllegalStateException();
    }
  }

  public Request getBuffer() {
    byte[] data = (byte[]) getNode().get(BUFFER);
    if (data != null) {
      try {
        return (Request) this.messageParser.createMessage(ByteBuffer.wrap(data));
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

  public void setBuffer(Request buffer) {
    if (buffer != null) {

      try {
        byte[] data = this.messageParser.encodeMessage((IMessage) buffer).array();
        getNode().put(BUFFER, data);
      }
      catch (ParseException e) {
        logger.error("Unable to encode message to buffer.");
      }
    }
    else {
      getNode().remove(BUFFER);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getTsTimerId()
   */
  @Override
  public Serializable getTsTimerId() {
    if (exists()) {
      return (Serializable) getNode().get(TS_TIMERID);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setTsTimerId(java.io.Serializable)
   */
  @Override
  public void setTsTimerId(Serializable tid) {
    if (exists()) {
      getNode().put(TS_TIMERID, tid);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getDestinationHost()
   */
  @Override
  public String getDestinationHost() {
    if (exists()) {
      return (String) getNode().get(DESTINATION_HOST);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setDestinationHost(java.lang.String)
   */
  @Override
  public void setDestinationHost(String destinationHost) {
    if (exists()) {
      getNode().put(DESTINATION_HOST, destinationHost);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#getDestinationRealm()
   */
  @Override
  public String getDestinationRealm() {
    if (exists()) {
      return (String) getNode().get(DESTINATION_REALM);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.client.impl.app.rf.IClientRfSessionData#setDestinationRealm(java.lang.String)
   */
  @Override
  public void setDestinationRealm(String destinationRealm) {
    if (exists()) {
      getNode().put(DESTINATION_REALM, destinationRealm);
    }
    else {
      throw new IllegalStateException();
    }
  }
}
