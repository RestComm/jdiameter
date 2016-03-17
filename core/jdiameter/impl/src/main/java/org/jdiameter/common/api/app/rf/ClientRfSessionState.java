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

package org.jdiameter.common.api.app.rf;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public enum ClientRfSessionState implements IAppSessionState<ClientRfSessionState> {

    IDLE(0),
    OPEN(1),
    PENDING_EVENT(2),
    PENDING_START (3) ,
    PENDING_INTERIM(4),
    PENDING_CLOSE(5),
    PENDING_BUFFERED(6);

    private final int value;

    private ClientRfSessionState(int val) {
        value = val;
    }

    public final int getValue() {
        return value;
    }

    public final ClientRfSessionState fromInt(int val) throws IllegalArgumentException {
        switch (val) {
            case 0:
                return IDLE;
            case 1:
                return OPEN;
            case 2:
                return PENDING_EVENT;
            case 3:
                return PENDING_START;
            case 4:
                return PENDING_INTERIM;
            case 5:
                return PENDING_CLOSE;
            case 6:
                return PENDING_BUFFERED;
            default:
                throw new IllegalArgumentException();
        }
    }
}