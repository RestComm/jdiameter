/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
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

package org.mobicents.diameter.impl.ha.common.slg;

import java.io.Serializable;
import java.nio.ByteBuffer;

import org.jboss.cache.Fqn;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.common.api.app.slg.SLgSessionState;
import org.jdiameter.common.api.app.slg.ISLgSessionData;
import org.mobicents.cluster.MobicentsCluster;
import org.mobicents.diameter.impl.ha.common.AppSessionDataReplicatedImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fernando Mendioroz (fernando.mendioroz@telestax.com)
 *
 */
public abstract class SLgSessionDataReplicatedImpl extends AppSessionDataReplicatedImpl implements ISLgSessionData {

  private static final Logger logger = LoggerFactory.getLogger(SLgSessionDataReplicatedImpl.class);

  private static final String STATE = "STATE";
  private static final String BUFFER = "BUFFER";
  private static final String TS_TIMERID = "TS_TIMERID";

  private IMessageParser messageParser;

  /**
   * @param nodeFqn
   * @param mobicentsCluster
   * @param iface
   */
  public SLgSessionDataReplicatedImpl(Fqn<?> nodeFqn, MobicentsCluster mobicentsCluster, IContainer container) {
    super(nodeFqn, mobicentsCluster);
    this.messageParser = container.getAssemblerFacility().getComponentInstance(IMessageParser.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.slg.ISLgSessionData#setSLgSessionState(org.jdiameter.common.api.app.slg.SLgSessionState)
   */
  public void setSLgSessionState(SLgSessionState state) {
    if (exists()) {
      getNode().put(STATE, state);
    } else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.slg.ISLgSessionData#getSLgSessionState()
   */
  public SLgSessionState getSLgSessionState() {
    if (exists()) {
      return (SLgSessionState) getNode().get(STATE);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.common.api.app.slg.ISLgSessionData#getTsTimerId()
   */
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
   * @see org.jdiameter.common.api.app.slg.ISLgSessionData#setTsTimerId(java.io.Serializable)
   */
  public void setTsTimerId(Serializable tid) {
    if (exists()) {
      getNode().put(TS_TIMERID, tid);
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
      } catch (AvpDataException e) {
        logger.error("Unable to recreate message from buffer.");
        return null;
      }
    } else {
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
}
