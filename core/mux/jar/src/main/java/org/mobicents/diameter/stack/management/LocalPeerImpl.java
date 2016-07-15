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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class LocalPeerImpl implements LocalPeer {

  private static final long serialVersionUID = 1L;

  // The Singleton
  // public final static LocalPeerImpl INSTANCE = new LocalPeerImpl();

  private String uri;
  private Collection<String> ipAddresses = new ArrayList<String>();
  private String realm;

  private Long vendorId;
  private String productName;
  private Long firmwareRev;

  private Collection<ApplicationIdJMX> defaultApplications = new ArrayList<ApplicationIdJMX>();

  // TODO: Implement
  // private OverloadMonitor overloadMonitor;

  private HashMap<String, DiameterStatistic> statistics;

  public LocalPeerImpl() {
  }

  @Override
  public String getUri() {
    return uri;
  }

  @Override
  public Collection<String> getIpAddresses() {
    return ipAddresses;
  }

  @Override
  public String getRealm() {
    return realm;
  }

  @Override
  public Long getVendorId() {
    return vendorId;
  }

  @Override
  public String getProductName() {
    return productName;
  }

  @Override
  public Long getFirmwareRev() {
    return firmwareRev;
  }

  @Override
  public Collection<ApplicationIdJMX> getDefaultApplications() {
    return defaultApplications;
  }

  @Override
  public void setUri(String uri) {
    this.uri = uri;
  }

  @Override
  public void addIpAddress(String ipAddress) {
    this.ipAddresses.add(ipAddress);
  }

  @Override
  public void removeIpAddress(String ipAddress) {
    this.ipAddresses.remove(ipAddress);
  }

  @Override
  public void setRealm(String realm) {
    this.realm = realm;
  }

  @Override
  public void setVendorId(Long vendorId) {
    this.vendorId = vendorId;
  }

  @Override
  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Override
  public void setFirmwareRev(Long firmwareRev) {
    this.firmwareRev = firmwareRev;
  }

  @Override
  public void addDefaultApplication(ApplicationIdJMX defaultApplication) {
    this.defaultApplications.add(defaultApplication);
  }

  @Override
  public void removeDefaultApplication(ApplicationIdJMX defaultApplication) {
    this.defaultApplications.remove(defaultApplication);
  }

  @Override
  public String toString() {
    String dotsString = " .............................................................";
    Class<?> cls;
    StringBuffer toStringBuffer = new StringBuffer();
    try {
      cls = Class.forName(this.getClass().getName());
      Field[] fieldlist = cls.getDeclaredFields();
      for (int i = 0; i < fieldlist.length; i++) {
        Field fld = fieldlist[i];
        if (!Modifier.isStatic(fld.getModifiers())) {
          toStringBuffer.append(fld.getName());
          int dots = 60 - fld.getName().length();
          toStringBuffer.append(dotsString, 0, dots);
          toStringBuffer.append(" ").append(fld.get(this)).append("\r\n");
        }
        //System.out.println("decl class = " + fld.getDeclaringClass());
        //System.out.println("type = " + fld.getType());
        //int mod = fld.getModifiers();
        //System.out.println("modifiers = " + Modifier.toString(mod));
        //System.out.println("-----");
      }
    }
    catch (ClassNotFoundException e) {
      // ignore
    }
    catch (IllegalArgumentException e) {
      // ignore
    }
    catch (IllegalAccessException e) {
      // ignore
    }

    return toStringBuffer.toString();
  }

  @Override
  public HashMap<String, DiameterStatistic> getStatistics() {
    return statistics;
  }

  @Override
  public void setStatistics(HashMap<String, DiameterStatistic> statistics) {
    this.statistics = statistics;
  }
}