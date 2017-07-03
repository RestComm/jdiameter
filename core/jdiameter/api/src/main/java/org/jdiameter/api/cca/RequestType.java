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
 */

package org.jdiameter.api.cca;

/**
 * This enumerated class defines CC-Request-Type AVP possible values as described in RFC 4006:
 *
 * <pre>
 *     The CC-Request-Type AVP (AVP Code 416) is of type Enumerated and
 *     contains the reason for sending the credit-control request message.
 *     It MUST be present in all Credit-Control-Request messages.  The
 *     following values are defined for the CC-Request-Type AVP:
 *
 *     INITIAL_REQUEST                 1
 *     UPDATE_REQUEST                  2
 *     TERMINATION_REQUEST             3
 *     EVENT_REQUEST                   4
 * </pre>
 */
public enum RequestType {
  INITIAL_REQUEST(1),
  UPDATE_REQUEST(2),
  TERMINATION_REQUEST(3),
  EVENT_REQUEST(4);

  private final int value;

  RequestType(int value) {
    this.value = value;
  }

  /**
   * Gets value of the corresponding constant as defined in RFC
   * @return value of the AVP
   */
  public int value() {
    return this.value;
  }
}
