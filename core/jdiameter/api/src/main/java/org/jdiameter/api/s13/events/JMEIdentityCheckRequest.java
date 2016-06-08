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

package org.jdiameter.api.s13.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * The ECR command, indicated by the Command-Code field set to 324 and the 'R'
 * bit set in the Command Flags field, is sent by MME or SGSN to EIR to check
 * the Mobile Equipment's identity status (e.g. to check that it has not been
 * stolen, or, to verify that it does not have faults).
 *
 */
public interface JMEIdentityCheckRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "ECR";
  public static final String _LONG_NAME = "ME-Identity-Check-Request";
  public static final int code = 324;

  public Avp getTerminalInformationAvp();

  public boolean hasIMEI();
  public String getIMEI();

  public boolean hasTgpp2MEID();
  public byte[] getTgpp2MEID();

  public boolean hasSoftwareVersion();
  public String getSoftwareVersion();

  public boolean isUserNameAVPPresent();

  public String getUserName();
}
