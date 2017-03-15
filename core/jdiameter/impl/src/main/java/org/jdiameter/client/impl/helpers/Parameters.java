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

package org.jdiameter.client.impl.helpers;

import java.util.ArrayList;

/**
 * This enumeration defined all parameters of diameter stack implementation
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Parameters extends Ordinal {

  private static final long serialVersionUID = 1L;

  protected static int index;

  private static ArrayList<Parameters> value = new ArrayList<Parameters>();

  /**
   * Class name of IOC  property
   */
  public static final Parameters Assembler = new Parameters("Assembler", String.class, "org.jdiameter.client.impl.helpers.AssemblerImpl");

  /**
   * Local peer URI property
   */
  public static final Parameters OwnDiameterURI = new Parameters("OwnDiameterURI", String.class, "aaa://localhost:3868");
  /**
   * Local peer ip address property
   */
  public static final Parameters OwnIPAddress = new Parameters("OwnIPAddress", String.class, "");
  /**
   * Local peer realm name property
   */
  public static final Parameters OwnRealm = new Parameters("OwnRealm", String.class, "local");
  /**
   * Local peer vendor id  property
   */
  public static final Parameters OwnVendorID = new Parameters("OwnVendorID", Long.class, 0L);
  /**
   * Local peer stack product name property
   */
  public static final Parameters OwnProductName = new Parameters("OwnProductName", String.class, "jDiameter");
  /**
   * Local peer stack firmware version property
   */
  public static final Parameters OwnFirmwareRevision = new Parameters("OwnFirmwareRevision", Long.class, 0L);

  /**
   * Task executor task queue size  property
   */
  public static final Parameters QueueSize = new Parameters("QueueSize", Integer.class,   10000);

  /**
   * Message time out  property
   */
  public static final Parameters MessageTimeOut = new Parameters("MessageTimeOut", Long.class, 60000L);
  /**
   * Stop stack time out  property
   */
  public static final Parameters StopTimeOut = new Parameters("StopTimeOut", Long.class, 10000L);

  /**
   * CEA command time out property
   */
  public static final Parameters CeaTimeOut = new Parameters("CeaTimeOut", Long.class, 10000L);
  /**
   * Peer inactive time out property
   */
  public static final Parameters IacTimeOut = new Parameters("IacTimeOut", Long.class, 20000L);
  /**
   * DWA command time out property
   */
  public static final Parameters DwaTimeOut = new Parameters("DwaTimeOut", Long.class, 10000L);
  /**
   * DPA command time out property
   */
  public static final Parameters DpaTimeOut = new Parameters("DpaTimeOut", Long.class, 5000L);
  /**
   * Reconnect time out property
   */
  public static final Parameters RecTimeOut = new Parameters("RecTimeOut", Long.class, 10000L);
  /**
   * Idle session time out property
   */
  public static final Parameters SessionTimeOut = new Parameters("SessionTimeOut", Long.class, 0L);

  /**
   * Peer FSM Thread Count property
   */
  public static final Parameters PeerFSMThreadCount = new Parameters("PeerFSMThreadCount", Integer.class, 3);

  /**
   * Orig_host avp set as URI into CER message
   */
  public static final Parameters UseUriAsFqdn = new Parameters("UseUriAsFqdn", Boolean.class, false);

  /**
   * Peer name property
   */
  public static final Parameters PeerName = new Parameters("PeerName", String.class, "");

  /**
   * Peer ip property
   */
  public static final Parameters PeerIp = new Parameters("PeerIp", String.class, "");

  /**
   * Peer local peer port range (format: 1345-1346) property
   */
  public static final Parameters PeerLocalPortRange = new Parameters("PeerLocalPortRange", String.class, "");

  /**
   * Peer rating property
   */
  public static final Parameters PeerRating = new Parameters("PeerRating", Integer.class, 0);
  /**
   *  Peer ptoperty
   */
  public static final Parameters Peer = new Parameters("Peer", Object.class);

  /**
   * Real entry property
   */
  public static final Parameters RealmEntry = new Parameters("RealmEntry", String.class, "");
  /**
   * Agent element
   */
  public static final Parameters Agent = new Parameters("Agent", Object.class);
  /**
   * Properties element
   */
  public static final Parameters Properties = new Parameters("Properties", Object.class);
  /**
   * Properties element
   */
  public static final Parameters Property = new Parameters("Property ", Object.class);
  /**
   * Property name
   */
  public static final Parameters PropertyName = new Parameters("name", String.class, "");
  /**
   * Property value
   */
  public static final Parameters PropertyValue = new Parameters("value", String.class, "");
  /**
   * Realm property
   */
  public static final Parameters Realm = new Parameters("Realm", Object.class);

  /**
   * Vendor id property
   */
  public static final Parameters VendorId = new Parameters("VendorId", Long.class);
  /**
   * Authentication application id property
   */
  public static final Parameters AuthApplId = new Parameters("AuthApplId", Long.class);
  /**
   * Accounting application id property
   */
  public static final Parameters AcctApplId = new Parameters("AcctApplId", Long.class);

  /**
   * Application Id property
   */
  public static final Parameters ApplicationId = new Parameters("ApplicationId", Object.class);

  /**
   * Extension point property
   */
  public static final Parameters Extensions = new Parameters("Extensions", Object.class);
  /**
   * Extension point name property
   */
  public static final Parameters ExtensionName = new Parameters("ExtensionName", String.class);

  /**
   * Peer list property
   */
  public static final Parameters PeerTable = new Parameters("PeerTable", Object.class);

  /**
   * Realm list property
   */
  public static final Parameters RealmTable = new Parameters("RealmTable", Object.class);

  /**
   * Security list property
   */
  public static final Parameters Security = new Parameters("Security", Object.class);

  /**
   * Security entry
   */
  public static final Parameters SecurityData = new Parameters("SecurityData", Object.class);

  /**
   * Security data name
   */
  public static final Parameters SDName = new Parameters("SDName", String.class);

  /**
   * Security protocol
   */
  public static final Parameters SDProtocol = new Parameters("SDProtocol", String.class, "TLS");

  /**
   * Security session creation flag
   */
  public static final Parameters SDEnableSessionCreation = new Parameters("SDEnableSessionCreation", Boolean.class, false);

  /**
   * Security client mode flag
   */
  public static final Parameters SDUseClientMode = new Parameters("SDUseClientMode", Boolean.class, false);

  /**
   * Cipher suites separated by ', '
   */
  public static final Parameters CipherSuites = new Parameters("CipherSuites", String.class);

  /**
   * Key data
   */
  public static final Parameters KeyData = new Parameters("KeyData", String.class);

  /**
   * Key manager
   */
  public static final Parameters KDManager = new Parameters("KDManager", String.class);

  /**
   * Key store
   */
  public static final Parameters KDStore = new Parameters("KDStore", String.class);

  /**
   * Key file
   */
  public static final Parameters KDFile = new Parameters("KDFile", String.class);

  /**
   * Key password
   */
  public static final Parameters KDPwd = new Parameters("KDPwd", String.class);


  /**
   * Trust data
   */
  public static final Parameters TrustData = new Parameters("TrustData", String.class);

  /**
   * Key manager
   */
  public static final Parameters TDManager = new Parameters("TDManager", String.class);

  /**
   * Key store
   */
  public static final Parameters TDStore = new Parameters("TDStore", String.class);

  /**
   * Key file
   */
  public static final Parameters TDFile = new Parameters("TDFile", String.class);

  /**
   * Key password
   */
  public static final Parameters TDPwd = new Parameters("TDPwd", String.class);

  /**
   * Reference to security information
   */
  public static final Parameters SecurityRef = new Parameters("SecurityRef", String.class);

  /**
   * XML entry for thread pool
   */
  public static final Parameters ThreadPool = new Parameters("ThreadPool", Object.class);
  /**
   * Thread pool max size
   */
  public static final Parameters ThreadPoolSize = new Parameters("ThreadPoolSize", Integer.class, 10);
  /**
   * Thread pool max size
   */
  public static final Parameters ThreadPoolPriority = new Parameters("ThreadPoolPriority", Integer.class, 5);

  /**
   * Statistic logger properties
   */
  public static final Parameters Statistics = new Parameters("Statistics", Object.class);

  /**
   * Statistic logger start pause
   */
  public static final Parameters StatisticsLoggerPause = new Parameters("StatisticsLoggerPause", Long.class, 30000L);

  /**
   * Statistic logger delay between save statistic information
   */
  public static final Parameters StatisticsLoggerDelay = new Parameters("StatisticsLoggerDelay", Long.class, 30000L);

  /**
   * Statistic flag controlling if statistics are on/off - ie. timer tasks to display are created.
   */
  public static final Parameters StatisticsEnabled = new Parameters("StatisticsEnabled", Boolean.class, false);

  /**
   * List of statistics names which should be enabled.
   */
  public static final Parameters StatisticsActiveList = new Parameters("StatisticsActiveList", String.class, false);

  /**
   * Concurrent configuration root point
   */
  public static final Parameters Concurrent = new Parameters("Concurrent", Object.class);

  /**
   * Concurrent entity name
   */
  public static final Parameters ConcurrentEntityName = new Parameters("ConcurrentEntityName", String.class, "Empty");

  /**
   * Concurrent thread group name
   */
  public static final Parameters ConcurrentEntityDescription = new Parameters("ConcurrentEntityDescription", String.class, "ThreadPool");

  /**
   * Concurrent thread group size
   */
  public static final Parameters ConcurrentEntityPoolSize = new Parameters("ConcurrentEntityPoolSize", Integer.class, 4);

  /**
   * Dictionary root
   */
  public static final Parameters Dictionary = new Parameters("Dictionary", Object.class);

  /**
   * Dictionary Class name
   */
  public static final Parameters DictionaryClass = new Parameters("DictionaryClass", String.class, "org.jdiameter.common.impl.validation.DictionaryImpl");

  /**
   * Dictionary Validation enabled
   */
  public static final Parameters DictionaryEnabled = new Parameters("DictionaryEnabled", Boolean.class, false);

  /**
   * Dictionary Send Level Validation
   */
  public static final Parameters DictionarySendLevel = new Parameters("DictionarySendLevel", String.class, "MESSAGE");

  /**
   * Dictionary Receive Level Validation
   */
  public static final Parameters DictionaryReceiveLevel = new Parameters("DictionaryReceiveLevel", String.class, "OFF");

  /**
   * Return all parameters as iterator
   *
   * @return all parameters as iterator
   */
  public static Iterable<Parameters> values() {
    return value;
  }

  private Class type;
  private Object defValue;

  protected Parameters(String name, Class type) {
    this.name = name;
    this.type = type;
    ordinal = index++;
    value.add(this);
  }

  protected Parameters(String name, Class type, Object defValue) {
    this.name = name;
    this.type = type;
    this.defValue = defValue;
    ordinal = index++;
    value.add(this);
  }

  /**
   * Return default value of property
   *
   * @return default value of property
   */
  public Object defValue() {
    return defValue;
  }

  /**
   * Return type of property
   *
   * @return type of property
   */
  public Class type() {
    return type;
  }
}
