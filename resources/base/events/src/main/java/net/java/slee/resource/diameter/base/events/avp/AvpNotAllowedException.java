/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base.events.avp;

/**
 * Thrown when an AVP is not allowed to be added to a Diameter command or grouped AVP type.
 * 
 * @author Open Cloud
 */
public class AvpNotAllowedException extends Exception {
    public AvpNotAllowedException(String message) {
        super(message);
    }

    public AvpNotAllowedException(Throwable cause) {
        super(cause);
    }

    public AvpNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
