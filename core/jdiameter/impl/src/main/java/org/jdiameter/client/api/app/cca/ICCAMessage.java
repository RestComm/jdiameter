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
 */

package org.jdiameter.client.api.app.cca;

 import org.jdiameter.client.api.IMessage;

 /**
  * @author <a href="mailto:grzegorz.figiel@pro-ids.com"> Grzegorz Figiel (ProIDS sp. z o.o.)</a>
  */
 public interface ICCAMessage extends IMessage {

   /**
    * Default CC-Session-Failover AVP value - NOT_SUPPORTED(0) according to RFC 4006.
    */
   int SESSION_FAILOVER_NOT_SUPPORTED_VALUE = 0;

   /**
    * CC-Session-Failover AVP value - SUPPORTED(1) according to RFC 4006.
    */
   int SESSION_FAILOVER_SUPPORTED_VALUE = 1;

   /**
    * Tells if there are any timers set to monitor potential retransmissions
    *
    * @return true if potential retransmissions will be handled
    */
   boolean isRetransmissionSupervised();

   /**
    * Marks that message to be under supervision timers guarding retransmissions
    *
    * @param arg true if supervision is active
    */
   void setRetransmissionSupervised(boolean arg);

   /**
    * Tells if the number of allowed retransmissions for this message is
    * already exceeded or not.
    *
    * @return false if no more retransmissions are allowed
    */
   boolean isRetransmissionAllowed();

   /**
    * @return value of CC-Session-Failover AVP.
    */
   int getCcSessionFailover();

   /**
    * Sets the number of allowed retransmissions for this message that can be performed
    * in case of failure detection.
    *
    * @param arg number of allowed retransmissions
    */
   void setNumberOfRetransAllowed(int arg);

   /**
    * Decrements the number of allowed retransmissions for this message.
    */
   void decrementNumberOfRetransAllowed();
 }
