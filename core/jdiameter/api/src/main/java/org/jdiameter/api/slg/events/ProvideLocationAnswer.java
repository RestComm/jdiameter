/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag.
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

package org.jdiameter.api.slg.events;

import org.jdiameter.api.app.AppAnswerEvent;

/**
 * @author fernando.mendioroz@telestax.com (Fernando Mendioroz)
 *
 */

/*
 * As for 3GPP TS 29.172 v13.0.0, the Provide Subscriber Location operation is used by a GMLC to request the location of a
 * target UE from the MME or SGSN at any time, as part of EPC-MT-LR (Evolved Packet Core Mobile Terminated Location Request) or
 * PS-MT-LR (Packet Switched Mobile Terminated Location Request) positioning procedures. The response contains a location
 * estimate of the target UE and other additional information. This operation is also used by a GMLC to request the location of
 * the target UE from the SGSN at any time, as part of deferred MT-LR procedure. The response contains the acknowledgment of the
 * receipt of the request and other additional information.
 * 
 * The Provide-Location-Answer (PLA) command, indicated by the Command-Code field set to 8388620 and the �R� bit cleared in the
 * Command Flags field, is sent by the MME or SGSN to the GMLC in response to the Provide-Location-Request command (Provide
 * Subscriber Location operation answer)
 */

public interface ProvideLocationAnswer extends AppAnswerEvent{

    public static final String _SHORT_NAME = "PLA";
    public static final String _LONG_NAME = "Provide-Location-Answer";

    public static final int code = 8388620;

}
