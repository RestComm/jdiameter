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

import static org.jdiameter.client.impl.helpers.Parameters.PeerIp;
import static org.jdiameter.client.impl.helpers.Parameters.PeerLocalPortRange;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.jdiameter.api.Configuration;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

public class NetworkPeerImpl implements NetworkPeer {

  private static final long serialVersionUID = 1L;

  // Mandatory
  private String name;
  private Boolean attemptConnect;
  private Integer rating;

  // Optional
  private String ip;
  private Integer portRangeLow;
  private Integer portRangeHigh;
  private String securityRef;

  // Helpers
  private static final String DEFAULT_STRING = "default_string";

  private HashMap<String, DiameterStatistic> statistics;

  public NetworkPeerImpl(String name, Boolean attemptConnect, Integer rating) {
    this.name = name;
    this.attemptConnect = attemptConnect;
    this.rating = rating;
  }

  public NetworkPeerImpl(String name, Boolean attemptConnect, Integer rating, String ip, Integer portRangeLow, Integer portRangeHigh, String securityRef) {
    this(name, attemptConnect, rating);
    this.ip = ip;
    this.portRangeLow = portRangeLow;
    this.portRangeHigh = portRangeHigh;
    this.securityRef = securityRef;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    if (!this.name.equals(name)) {
      EmptyConfiguration config = getPeerConfiguration(name);
      if (config != null) {
        config.add(PeerName, name);
        this.name = name;
      }
    }
  }

  @Override
  public Boolean getAttemptConnect() {
    return attemptConnect;
  }

  @Override
  public void setAttemptConnect(Boolean attemptConnect) {
    this.attemptConnect = attemptConnect;
  }

  @Override
  public Integer getRating() {
    return rating;
  }

  @Override
  public void setRating(Integer rating) {
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerRating, rating);
      this.rating = rating;
    }
  }

  @Override
  public String getIp() {
    return ip;
  }

  @Override
  public void setIp(String ip) {
    // TODO: Verify IP Address
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerIp, ip);
      this.ip = ip;
    }
  }

  @Override
  public Integer getPortRangeLow() {
    return portRangeLow;
  }

  @Override
  public Integer getPortRangeHigh() {
    return portRangeHigh;
  }

  @Override
  public void setPortRange(Integer portRangeLow, Integer portRangeHigh) {
    EmptyConfiguration config = getPeerConfiguration(name);
    if (config != null) {
      config.add(PeerLocalPortRange, portRangeLow + "-" + portRangeHigh);
      this.portRangeLow = portRangeLow;
      this.portRangeHigh = portRangeHigh;
    }
  }

  @Override
  public String getSecurityRef() {
    return securityRef;
  }

  @Override
  public void setSecurityRef(String securityRef) {
    this.securityRef = securityRef;
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

  private EmptyConfiguration getPeerConfiguration(String name) {
    XMLConfiguration configuration = (XMLConfiguration) DiameterConfiguration.stack.getMetaData().getConfiguration();
    Configuration[] peerTable = configuration.getChildren(PeerTable.ordinal());

    for (Configuration curPeer : peerTable) {
      if (curPeer.getStringValue(PeerName.ordinal(), DEFAULT_STRING).equals(name)) {
        return (EmptyConfiguration) curPeer;
      }
    }

    return null;
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
