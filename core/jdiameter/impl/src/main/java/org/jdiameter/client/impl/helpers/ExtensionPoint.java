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
import java.util.Arrays;
import java.util.List;


/**
 * This class provide pluggable features
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ExtensionPoint extends Ordinal {

  private static final long serialVersionUID = 1L;

  protected static int index;

  /**
   * MetaData implementation class name
   */
  public static final ExtensionPoint InternalMetaData = new ExtensionPoint("InternalMetaData", "org.jdiameter.client.impl.MetaDataImpl");

  /**
   * Message parser implementation class name
   */
  public static final ExtensionPoint InternalMessageParser = new ExtensionPoint("InternalMessageParser", "org.jdiameter.client.impl.parser.MessageParser");

  /**
   * Element message implementation class name
   */
  public static final ExtensionPoint InternalElementParser = new ExtensionPoint("InternalElementParser", "org.jdiameter.client.impl.parser.ElementParser");

  /**
   * Router enginr implementation class name
   */
  public static final ExtensionPoint InternalRouterEngine = new ExtensionPoint("InternalRouterEngine", "org.jdiameter.client.impl.router.RouterImpl");

  /**
   * Peer controller implementation class name
   */
  public static final ExtensionPoint InternalPeerController =
      new ExtensionPoint("InternalPeerController", "org.jdiameter.client.impl.controller.PeerTableImpl");

  /**
   * Realm controller implementation class name
   */
  public static final ExtensionPoint InternalRealmController =
      new ExtensionPoint("InternalRealmController", "org.jdiameter.client.impl.controller.RealmTableImpl");

  /**
   * Session factory implementation class name
   */
  public static final ExtensionPoint InternalSessionFactory = new ExtensionPoint("InternalSessionFactory", "org.jdiameter.client.impl.SessionFactoryImpl");

  /**
   * Class name of connection interface implementation
   */
  public static final ExtensionPoint InternalConnectionClass =
      new ExtensionPoint("InternalConnection","org.jdiameter.client.impl.transport.tcp.TCPClientConnection");

  /**
   * Transport factory implementation class name
   */
  public static final ExtensionPoint InternalTransportFactory =
      new ExtensionPoint("InternalTransportFactory", "org.jdiameter.client.impl.transport.TransportLayerFactory");

  /**
   * Peer FSM factory implementation class name
   */
  public static final ExtensionPoint InternalPeerFsmFactory = new ExtensionPoint("InternalPeerFsmFactory", "org.jdiameter.client.impl.fsm.FsmFactoryImpl");

  /**
   * Statistic factory implementation class name
   */
  public static final ExtensionPoint InternalStatisticFactory =
      new ExtensionPoint("InternalStatisticFactory", "org.jdiameter.common.impl.statistic.StatisticManagerImpl");

  /**
   * Statistic factory implementation class name
   */
  public static final ExtensionPoint InternalStatisticProcessor =
      new ExtensionPoint("InternalStatisticProcessor", "org.jdiameter.common.impl.statistic.StatisticProcessorImpl");

  /**
   * Concurrent factory implementation class name
   */
  public static final ExtensionPoint InternalConcurrentFactory =
      new ExtensionPoint("InternalConcurrentFactory", "org.jdiameter.common.impl.concurrent.ConcurrentFactory");

  /**
   * Concurrent entity factory implementation class name
   */
  public static final ExtensionPoint InternalConcurrentEntityFactory =
      new ExtensionPoint("InternalConcurrentEntityFactory", "org.jdiameter.common.impl.concurrent.ConcurrentEntityFactory");

  /**
   * Redirect Agent implementation class name
   */
  public static final ExtensionPoint InternalAgentRedirect = new ExtensionPoint("InternalAgentRedirect", "org.jdiameter.server.impl.agent.RedirectAgentImpl");

  /**
   * Proxy Agent implementation class name
   */
  public static final ExtensionPoint InternalAgentProxy = new ExtensionPoint("InternalAgentProxy", "org.jdiameter.server.impl.agent.ProxyAgentImpl");

  /**
   *  Agent Conf implementation class name
   */
  public static final ExtensionPoint InternalAgentConfiguration =
      new ExtensionPoint("InternalAgentConfiguration", "org.jdiameter.server.impl.agent.AgentConfigurationImpl");

  /**
   * Session Datasource class name
   */
  public static final ExtensionPoint InternalSessionDatasource =
      new ExtensionPoint("InternalSessionDatasource", "org.jdiameter.common.impl.data.LocalDataSource");

  /**
   * Timer Facility class name
   */
  public static final ExtensionPoint InternalTimerFacility =
      new ExtensionPoint("InternalTimerFacility", "org.jdiameter.common.impl.timer.LocalTimerFacilityImpl");

  /**
   * List of internal extension point
   */
  public static final ExtensionPoint Internal = new ExtensionPoint(
      "Internal", 0,
      InternalMetaData,
      InternalMessageParser,
      InternalElementParser,
      InternalRouterEngine,
      InternalPeerController,
      InternalRealmController,
      InternalSessionFactory,
      //DONT add this, this will make assembler to try to create instance and he will fail :)
      //InternalConnectionClass,
      InternalTransportFactory,
      InternalPeerFsmFactory,
      InternalStatisticFactory,
      InternalConcurrentFactory,
      InternalConcurrentEntityFactory,
      InternalTimerFacility,
      InternalSessionDatasource,
      InternalAgentRedirect,
      InternalAgentProxy,
      InternalAgentConfiguration,
      InternalStatisticProcessor
      );

  /**
   * Stack layer
   */
  public static final ExtensionPoint StackLayer = new ExtensionPoint("StackLayer", 1);

  /**
   * Controller layer
   */
  public static final ExtensionPoint ControllerLayer = new ExtensionPoint("ControllerLayer", 2);

  /**
   * Transport layer
   */
  public static final ExtensionPoint TransportLayer = new ExtensionPoint("TransportLayer", 3);

  private ExtensionPoint[] elements = new ExtensionPoint[0];
  private String defaultValue = "";
  private int id = -1;

  /**
   * Type's count of extension point
   */
  public static final int COUNT = 3;

  /**
   * Create instance of class
   */
  public ExtensionPoint() {
    this.ordinal = index++;
  }

  protected ExtensionPoint(String name, String defaultValue) {
    this();
    this.name = name;
    this.defaultValue = defaultValue;
  }

  protected ExtensionPoint(String name, ExtensionPoint... elements) {
    this();
    this.name = name;
    this.elements = elements;
  }

  protected ExtensionPoint(String name, int id, ExtensionPoint... elements) {
    this();
    this.name = name;
    this.id = id;
    this.elements = elements;
  }

  /**
   * Append extension point entries
   *
   * @param elements array of append extension point entries
   */
  public void appendElements(ExtensionPoint... elements) {
    List<ExtensionPoint> rc = new ArrayList<ExtensionPoint>();
    rc.addAll(Arrays.asList(this.elements));
    rc.addAll(Arrays.asList(elements));
    this.elements = rc.toArray(new ExtensionPoint[0]);
  }

  /**
   * Return parameters of extension point
   *
   * @return array parameters of extension point
   */
  public ExtensionPoint[] getExtensionPoints() {
    return elements;
  }

  /**
   * Return default value of extension point
   *
   * @return default value of extension point
   */
  public String defValue() {
    return defaultValue;
  }

  /**
   * Return id of extension point
   *
   * @return id of extension point
   */
  public int id() {
    return id;
  }
}
