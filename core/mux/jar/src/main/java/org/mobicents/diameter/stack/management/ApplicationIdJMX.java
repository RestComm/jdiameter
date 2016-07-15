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

package org.mobicents.diameter.stack.management;

import java.io.Serializable;

import org.jdiameter.api.ApplicationId;

public class ApplicationIdJMX implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long vendorId = null;
  private Long authApplicationId = null;
  private Long acctApplicationId = null;

  private ApplicationIdJMX(Long vendorId, Long authApplicationId, Long acctApplicationId) {
    this.vendorId = vendorId;
    this.authApplicationId = authApplicationId;
    this.acctApplicationId = acctApplicationId;
  }

  public static ApplicationIdJMX createAcctApplicationId(long vendorId, long applicationId) {
    return new ApplicationIdJMX(vendorId, null, applicationId);
  }

  public static ApplicationIdJMX createAuthApplicationId(long vendorId, long applicationId) {
    return new ApplicationIdJMX(vendorId, applicationId, null);
  }

  public static ApplicationIdJMX createAcctApplicationId(long applicationId) {
    return createAcctApplicationId(0L, applicationId);
  }

  public static ApplicationIdJMX createAuthApplicationId(long applicationId) {
    return createAuthApplicationId(0L, applicationId);
  }

  public Long getAcctApplicationId() {
    return acctApplicationId;
  }

  public Long getAuthApplicationId() {
    return authApplicationId;
  }

  public Long getVendorId() {
    return vendorId;
  }

  public ApplicationId asApplicationId() {
    return authApplicationId != null ? ApplicationId.createByAuthAppId(vendorId, authApplicationId) :
      ApplicationId.createByAccAppId(vendorId, acctApplicationId);
  }

  public static ApplicationIdJMX fromApplicationId(ApplicationId appId) {
    if (appId.getAuthAppId() != 0) {
      return new ApplicationIdJMX(appId.getVendorId(), appId.getAuthAppId(), null);
    }
    else {
      return new ApplicationIdJMX(appId.getVendorId(), null, appId.getAcctAppId());
    }
  }

  @Override
  public String toString() {
    return "ApplicationID[vendor=" + vendorId + "; Auth=" + authApplicationId + "; Acct=" + acctApplicationId + "]";
  }
}
