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

package org.jdiameter.api;

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
