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

package org.jdiameter.server.impl.app.rx;

import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rx.events.RxAAAnswer;
import org.jdiameter.api.rx.events.RxAARequest;
import org.jdiameter.api.rx.events.RxAbortSessionAnswer;
import org.jdiameter.api.rx.events.RxAbortSessionRequest;
import org.jdiameter.api.rx.events.RxReAuthAnswer;
import org.jdiameter.api.rx.events.RxReAuthRequest;
import org.jdiameter.api.rx.events.RxSessionTermAnswer;
import org.jdiameter.api.rx.events.RxSessionTermRequest;

/**
 *
 * @author <a href="mailto:richard.good@smilecoms.com"> Richard Good </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Event implements StateEvent {

  public enum Type {
    SEND_AAA,               RECEIVE_AAR,
    SEND_STA,               RECEIVE_STR,
    SEND_RAR,               RECEIVE_RAA,
    SEND_ASR,               RECEIVE_ASA,
    SEND_EVENT_ANSWER,      RECEIVE_EVENT_REQUEST;
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

  Event(boolean isRequest, AppRequestEvent request, AppAnswerEvent answer) {

    this.answer = answer;
    this.request = request;

    if (isRequest) {
      switch (request.getCommandCode()) {
        case RxReAuthRequest.code:
          type = Type.SEND_RAR;
          break;
        case RxAbortSessionRequest.code:
          type = Type.SEND_ASR;
          break;
        case RxAARequest.code:
          type = Type.RECEIVE_AAR;
          break;
        case RxSessionTermRequest.code:
          type = Type.RECEIVE_STR;
          break;
        case 5: //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Type.RECEIVE_EVENT_REQUEST;
          break;
        default:
          throw new RuntimeException("Wrong command code value: " + request.getCommandCode());
      }
    }
    else {
      switch (answer.getCommandCode()) {
        case RxAbortSessionAnswer.code:
          type = Type.RECEIVE_ASA;
          break;
        case RxReAuthAnswer.code:
          type = Type.RECEIVE_RAA;
          break;
        case RxAAAnswer.code:
          type = Type.SEND_AAA;
          break;
        case RxSessionTermAnswer.code:
          type = Type.SEND_STA;
          break;
        case 6:  //BUG FIX How do we know this is an event and not a session? Do we need to fix this? Does Rx do event?
          type = Type.SEND_EVENT_ANSWER;
          break;
        default:
          throw new RuntimeException("Wrong CC-Request-Type value: " + answer.getCommandCode());
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
