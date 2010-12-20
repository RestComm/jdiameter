/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.server.impl.app.gx;

import org.jdiameter.api.AvpDataException;
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

    Event(boolean isRequest, GxCreditControlRequest request, GxCreditControlAnswer answer) throws AvpDataException {

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

    public <E> E encodeType(Class<E> eClass) {
        return eClass == Event.Type.class ? (E) type : null;
    }

    public Enum getType() {
        return type;
    }

    public int compareTo(Object o) {
        return 0;
    }

    public Object getData() {
        return this.request != null ? this.request : this.answer;
    }

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
