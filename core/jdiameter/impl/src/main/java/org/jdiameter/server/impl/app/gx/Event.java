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

package org.jdiameter.server.impl.app.gx;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.gx.events.GxCreditControlAnswer;
import org.jdiameter.api.gx.events.GxCreditControlRequest;

/**
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 */
public class Event implements StateEvent {

  public enum Type {

    RECEIVED_EVENT,
    SENT_EVENT_RESPONSE,
    RECEIVED_INITIAL,
    SENT_INITIAL_RESPONSE,
    RECEIVED_UPDATE,
    SENT_UPDATE_RESPONSE,
    RECEIVED_TERMINATE,
    SENT_TERMINATE_RESPONSE,
    // These have no transition, no state resources, timers
    SENT_RAR,
    RECEIVED_RAA;
  }
  Type type;
  AppRequestEvent request;
  AppAnswerEvent answer;

  Event(Type type) {
    this.type = type;
  }

  Event(Type type, AppRequestEvent request, AppAnswerEvent answer) {
    this.type = type;
    this.answer = answer;
    this.request = request;
  }

  Event(boolean isRequest, GxCreditControlRequest request, GxCreditControlAnswer answer) {

    this.answer = answer;
    this.request = request;
    /**
     * <pre>
     * 8.3.  CC-Request-Type AVP
     *
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
    if (isRequest) {
      switch (request.getRequestTypeAVPValue()) {
        case 1:
          type = Type.RECEIVED_INITIAL;
          break;
        case 2:
          type = Type.RECEIVED_UPDATE;
          break;
        case 3:
          type = Type.RECEIVED_TERMINATE;
          break;
        case 4:
          type = Type.RECEIVED_EVENT;
          break;
        default:
          throw new IllegalArgumentException("Invalid value or Request-Type AVP not present in CC Request.");
      }
    } else {
      switch (answer.getRequestTypeAVPValue()) {
        case 1:
          type = Type.SENT_INITIAL_RESPONSE;
          break;
        case 2:
          type = Type.SENT_UPDATE_RESPONSE;
          break;
        case 3:
          type = Type.SENT_TERMINATE_RESPONSE;
          break;
        case 4:
          type = Type.SENT_EVENT_RESPONSE;
          break;
        default:
          throw new IllegalArgumentException("Invalid value or Request-Type AVP not present in CC Answer.");
      }
    }
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Event.Type.class ? (E) type : null;
  }

  @Override
  public Enum getType() {
    return type;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }

  @Override
  public Object getData() {
    return this.request != null ? this.request : this.answer;
  }

  @Override
  public void setData(Object data) {
    // data = (AppEvent) o;
    // FIXME: What should we do here?! Is it request or answer?
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }
}
