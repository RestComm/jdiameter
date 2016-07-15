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

package org.jdiameter.server.impl.agent;

import java.io.IOException;
import java.util.Properties;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.RouteException;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.server.api.agent.IAgentConfiguration;
import org.jdiameter.server.api.agent.IRedirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RedirectAgentImpl extends AgentImpl implements IRedirect {

  private static final Logger logger = LoggerFactory.getLogger(RedirectAgentImpl.class);

  /**
   * @param container
   * @param peerTable
   * @param realmTable
   */
  public RedirectAgentImpl(IContainer container, IRealmTable realmTable) {
    super(container, realmTable);
  }

  public static final int RESULT_REDIRECT_INDICATION = ResultCode.REDIRECT_INDICATION;
  public static final int RESULT_INVALID_AVP_VALUE = ResultCode.INVALID_AVP_VALUE;

  /*
   * (non-Javadoc)
   * @see org.jdiameter.server.api.agent.IAgent#processRequest(org.jdiameter.client.api.IRequest, org.jdiameter.client.api.controller.IRealm)
   */
  @Override
  public Answer processRequest(IRequest request, IRealm matchedRealm) {
    try {
      Answer ans = request.createAnswer(RESULT_REDIRECT_INDICATION);
      AvpSet set = ans.getAvps();
      String[] destHosts = matchedRealm.getPeerNames();
      for (String host:destHosts) {
        set.addAvp(Avp.REDIRECT_HOST, host, false);
      }

      IAgentConfiguration agentConfiguration = matchedRealm.getAgentConfiguration();
      //default
      int rhuValue = RHU_REALM_AND_APPLICATION;
      if (agentConfiguration != null) {
        Properties p = agentConfiguration.getProperties();
        try {
          rhuValue = Integer.parseInt(p.getProperty(RHU_PROPERTY, "" + rhuValue));
        }
        catch (Exception e) {
          if (logger.isWarnEnabled()) {
            logger.warn("Failed to parse configuration value. ", e);
          }
        }
      }

      set.addAvp(Avp.REDIRECT_HOST_USAGE, rhuValue);
      ans.setError(true);
      super.container.sendMessage((IMessage) ans);
    }
    catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug("Failure when trying to send Answer", e);
      }
      Answer ans = request.createAnswer(RESULT_INVALID_AVP_VALUE);
      // now lets do the magic.
      if (e instanceof AvpDataException && ((AvpDataException) e).getAvp() != null) {
        // lets add failed if its present
        AvpSet failedAvp = ans.getAvps().addGroupedAvp(Avp.FAILED_AVP);
        failedAvp.addAvp(((AvpDataException) e).getAvp());
      }
      try {
        container.sendMessage((IMessage) ans);
      }
      catch (RouteException re) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failure when trying to send failure answer", re);
        }
      }
      catch (AvpDataException ade) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failure when trying to send failure answer", ade);
        }
      }
      catch (IllegalDiameterStateException idse) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failure when trying to send failure answer", idse);
        }
      }
      catch (IOException ioe) {
        if (logger.isDebugEnabled()) {
          logger.debug("Failure when trying to send failure answer", ioe);
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.EventListener#receivedSuccessMessage(org.jdiameter.api.Message, org.jdiameter.api.Message)
   */
  @Override
  public void receivedSuccessMessage(Request request, Answer answer) {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.EventListener#timeoutExpired(org.jdiameter.api.Message)
   */
  @Override
  public void timeoutExpired(Request request) {
    // TODO Auto-generated method stub
  }

}
