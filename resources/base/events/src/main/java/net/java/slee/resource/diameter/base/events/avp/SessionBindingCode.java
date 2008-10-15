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
 * The Session-Binding AVP (AVP Code 270) is of type Unsigned32, and MAY
 * be present in application-specific authorization answer messages.  If
 * present, this AVP MAY inform the Diameter client that all future
 * application-specific re-auth messages for this session MUST be sent
 * to the same authorization server.  This AVP MAY also specify that a
 * Session-Termination-Request message for this session MUST be sent to
 * the same authorizing server.
 * <p/>
 * This field is a bit mask, this class defines the meaning of the bits.
 *
 * @author Open Cloud
 */
public class SessionBindingCode {

    /**
     * When set, future re-auth messages for this session MUST NOT
     * include the Destination-Host AVP.  When cleared, the default
     * value, the Destination-Host AVP MUST be present in all re-auth
     * messages for this session.
     */
    public static final int RE_AUTH = 1;

    /**
     * When set, the STR message for this session MUST NOT include the
     * Destination-Host AVP.  When cleared, the default value, the
     * Destination-Host AVP MUST be present in the STR message for this
     * session.
     */
    public static final int STR = 2;

    /**
     * When set, all accounting messages for this session MUST NOT
     * include the Destination-Host AVP.  When cleared, the default
     * value, the Destination-Host AVP, if known, MUST be present in all
     * accounting messages for this session.
     */
    public static final int ACCOUNTING = 4;
}
