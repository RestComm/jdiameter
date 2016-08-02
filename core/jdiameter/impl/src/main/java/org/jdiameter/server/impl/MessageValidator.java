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

package org.jdiameter.server.impl;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.ResultCode;
import org.jdiameter.client.api.IMessage;

/**
 * This class provides check incoming/outgoing diameter messages.
 * Check's rules consist into xml file.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class MessageValidator {

  public static final Result SUCCESS = new Result(null, ResultCode.SUCCESS);

  private boolean enable = true;

  public MessageValidator() {
    // todo load validator rules
  }

  /**
   * Enable validation functions
   */
  public void enable() {
    enable = true;
  }

  /**
   * Disable validation functions
   */
  public void disable() {
    enable = false;
  }

  /**
   * Return true if validation function is on
   * @return true if validation function is on
   */
  public boolean isEnable() {
    return enable;
  }

  /**
   * Validate message
   * @param message message instance
   * @return result of validation procedure
   */
  public Result check(IMessage message) {
    if (message == null) {
      throw new IllegalArgumentException("Message is null");
    }
    if (!enable) {
      return SUCCESS;
    }
    // todo
    return null;
  }

  public static class Result {

    private IMessage errorMessage;
    private long code = ResultCode.SUCCESS;

    Result(IMessage errorMessage, long code) {
      this.errorMessage = errorMessage;
      this.code = code;
    }

    /**
     * Return true if message is correct
     * @return true if message is correct
     */
    public boolean isOK() {
      return code == ResultCode.SUCCESS || code == ResultCode.LIMITED_SUCCESS;
    }

    /**
     * Return long value of result code
     * @return long value of result code
     */
    public long toLong() {
      return code;
    }

    /**
     * Create error answer message with Result-Code Avp
     * @return error answer message
     */
    public IMessage toMessage() {
      if ( errorMessage != null && errorMessage.getAvps().getAvp(Avp.RESULT_CODE) == null ) {
        errorMessage.getAvps().addAvp(Avp.RESULT_CODE, code);
      }
      return errorMessage;
    }

    /**
     * Create error answer message with Experemental-Result-Code Avp
     * @param vendorId vendor id
     * @return error answer message with Experemental-Result-Code Avp
     */
    public IMessage toMessage(int vendorId) {
      if ( errorMessage != null && errorMessage.getAvps().getAvp(297) == null ) { // EXPERIMENTAL_RESULT = 297
        AvpSet er = errorMessage.getAvps().addGroupedAvp(297);
        er.addAvp(Avp.VENDOR_ID, vendorId);
        er.addAvp(Avp.EXPERIMENTAL_RESULT_CODE, code);
      }
      return errorMessage;
    }
  }
}
