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

package org.jdiameter.client.impl.app.cca;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.api.cca.events.JCreditControlRequest;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_INITIAL_REQUEST, RECEIVED_INITIAL_ANSWER,
    SEND_UPDATE_REQUEST, RECEIVED_UPDATE_ANSWER,
    SEND_TERMINATE_REQUEST, RECEIVED_TERMINATED_ANSWER,
    RECEIVED_RAR, SEND_RAA, Tx_TIMER_FIRED,
    SEND_EVENT_REQUEST, RECEIVE_EVENT_ANSWER;
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

  Event(boolean isRequest, JCreditControlRequest request, JCreditControlAnswer answer) {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getRequestTypeAVPValue()) {
        case 1:
          type = Type.SEND_INITIAL_REQUEST;
          break;
        case 2:
          type = Type.SEND_UPDATE_REQUEST;
          break;
        case 3:
          type = Type.SEND_TERMINATE_REQUEST;
          break;
        case 4:
          type = Type.SEND_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + request.getRequestTypeAVPValue());

      }

    }
    else {
      switch (answer.getRequestTypeAVPValue()) {
        case 1:
          type = Type.RECEIVED_INITIAL_ANSWER;
          break;
        case 2:
          type = Type.RECEIVED_UPDATE_ANSWER;
          break;
        case 3:
          type = Type.RECEIVED_TERMINATED_ANSWER;
          break;
        case 4:
          type = Type.RECEIVE_EVENT_ANSWER;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getRequestTypeAVPValue());
      }
    }
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
    return request != null ? request : answer;
  }

  @Override
  public void setData(Object data) {
    // FIXME: What should we do here?! Is it request or answer?
  }

  public AppEvent getRequest() {
    return request;
  }

  public AppEvent getAnswer() {
    return answer;
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Event.Type.class ? (E) type : null;
  }
}
