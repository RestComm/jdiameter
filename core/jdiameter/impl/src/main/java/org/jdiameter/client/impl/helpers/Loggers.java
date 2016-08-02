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
import java.util.logging.Logger;

/**
 * This enumeration contains all logger usage in JDiameter stack implementation
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Loggers extends Ordinal {

  private static final long serialVersionUID = 1L;

  protected static int index;

  private static ArrayList<Loggers> value = new ArrayList<Loggers>();

  /**
   * Logs the stack lifecycle
   */
  public static final Loggers Stack = new Loggers("Stack", null ,"Logs the stack lifecycle");
  /**
   * Logs the peers
   */
  public static final Loggers Peer = new Loggers("Peer", "peer","Logs the peers");
  /**
   * Logs the peer manager subsystem
   */
  public static final Loggers PeerTable = new Loggers("PeerTable", "peertable","Logs the peer table subsystem");
  /**
   * Logs the peers fsm
   */
  public static final Loggers FSM = new Loggers("FSM", "peer.fsm","Logs the peers fsm");
  /**
   * Logs the message parser
   */
  public static final Loggers Parser = new Loggers("Parser", "parser","Logs the message parser");
  /**
   * Logs the avp opetations processing
   */
  public static final Loggers AVP = new Loggers("AVP", "parser.avp","Logs the avp opetations processing");
  /**
   *  Logs the message opetations/lifecycle processing
   */
  public static final Loggers Message = new Loggers("Message", "parser.message","Logs the message opetations/lifecycle processing");
  /**
   * Logs the message router subsystem
   */
  public static final Loggers Router = new Loggers("Router", "router","Logs the message router subsystem");
  /**
   * Logs the transport(tcp) opetations processing
   */
  public static final Loggers Transport = new Loggers("Transport", "TCPTransport","Logs the transport(tcp) opetations processing");

  /**
   * Return Iterator of all entries
   * @return  Iterator of all entries
   */
  public static Iterable<Loggers> values() {
    return value;
  }

  private String description;
  private String fullName;

  protected Loggers(String name, String fullName, String desc) {
    this.name = name;
    if (fullName == null) {
      this.fullName    = "jDiameter";
    }
    else {
      this.fullName    = "jDiameter." + fullName;
    }
    this.description = desc;
    ordinal = index++;
    value.add(this);
  }

  /**
   * Return full name of logger
   *
   * @return full name of logger
   */
  public String fullName() {
    return fullName;
  }

  /**
   * Return description of logger
   *
   * @return description of logger
   */
  public String description() {
    return description;
  }

  /**
   * Return logger instance
   *
   * @return logger instance
   */
  public Logger logger() {
    return Logger.getLogger(fullName);
  }
}
