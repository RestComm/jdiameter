/*
*   TeleStax, Open Source Cloud Communications
*   Copyright 2011-2016, TeleStax Inc. and individual contributors
*   by the @authors tag.
*
*   This program is free software: you can redistribute it and/or modify
*   under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation; either version 3 of
*   the License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <http://www.gnu.org/licenses/>
*
*   This file incorporates work covered by the following copyright and
*   permission notice:
*
*     JBoss, Home of Professional Open Source
*     Copyright 2007-2011, Red Hat, Inc. and individual contributors
*     by the @authors tag. See the copyright.txt in the distribution for a
*     full listing of individual contributors.
*
*     This is free software; you can redistribute it and/or modify it
*     under the terms of the GNU Lesser General Public License as
*     published by the Free Software Foundation; either version 2.1 of
*     the License, or (at your option) any later version.
*
*     This software is distributed in the hope that it will be useful,
*     but WITHOUT ANY WARRANTY; without even the implied warranty of
*     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
*     Lesser General Public License for more details.
*
*     You should have received a copy of the GNU Lesser General Public
*     License along with this software; if not, write to the Free
*     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
*     02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jdiameter.client.impl.parser;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.app.cca.ICCAMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents a Diameter message.
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:grzegorz.figiel@pro-ids.com"> Grzegorz Figiel (ProIDS sp. z o.o.)</a>
 */
public class CCAMessageImpl extends MessageImpl implements ICCAMessage {

  private static final Logger logger = LoggerFactory.getLogger(CCAMessageImpl.class);

  boolean isRetransSupervisionActive = false;
  int numberOfRetransAllowed = Integer.MIN_VALUE;

  /**
   * Create empty message
   *
   * @param commandCode
   * @param appId
   */
  CCAMessageImpl(int commandCode, long appId) {
    super(commandCode, appId);
  }

  /**
   * Create empty message
   *
   * @param commandCode
   * @param applicationId
   * @param flags
   * @param hopByHopId
   * @param endToEndId
   * @param avpSet
   */
  CCAMessageImpl(int commandCode, long applicationId, short flags, long hopByHopId, long endToEndId, AvpSetImpl avpSet) {
    super(commandCode, applicationId, flags, hopByHopId, endToEndId, avpSet);
  }

  /**
   * Create Answer
   *
   * @param request parent request
   */
  private CCAMessageImpl(MessageImpl request) {
    super(request);
  }

  @Override
  public boolean isRetransmissionSupervised() {
    return this.isRetransSupervisionActive;
  }

  public void setRetransmissionSupervised(boolean arg) {
    this.isRetransSupervisionActive = arg;
  }

  public boolean isRetransmissionAllowed() {
    return this.numberOfRetransAllowed > 0;
  }

  public int getCcSessionFailover() {
    try {
      Avp avpCcSessionFailover = avpSet.getAvp(Avp.CC_SESSION_FAILOVER);
      if (avpCcSessionFailover != null) {
        return avpCcSessionFailover.getInteger32();
      }
    }
    catch (AvpDataException ade) {
      logger.error("Failed to fetch CC-Session-Failover", ade);
    }
    return SESSION_FAILOVER_NOT_SUPPORTED_VALUE;
  }

  public void setNumberOfRetransAllowed(int arg) {
    if (this.numberOfRetransAllowed < 0) {
      this.numberOfRetransAllowed = arg;
    }
  }

  public void decrementNumberOfRetransAllowed() {
    this.numberOfRetransAllowed--;
  }

}
