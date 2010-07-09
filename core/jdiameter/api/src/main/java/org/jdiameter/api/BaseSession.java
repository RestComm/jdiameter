/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.api;

import java.io.Serializable;

/**
 * The session delivery objects are responsible for delivering all incoming Message to a specific session.
 * It determines the Diameter Session object that the message belongs to by querying the message's session id AVP.
 * The delivery object searches the local session database for a matching session. If no matching session is found,
 * the delivery object will lookup a matching session factory object that has an application id matching the
 * application id of the message. If there is a registered session factory, then the delivery object will ask the
 * factory to create a new session and delivery the message to the newly created session. If non of these lookup's
 * are successful, the session delivery object will silently discard the message.
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * Serializable interface allows use this class in SLEE Event objects
 * 
 * @version 1.5.1 Final
 * 
 * @author erick.svenson@yahoo.com
 * @author artem.litvinov@gmail.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface BaseSession extends Serializable {

  /**
   * Returns the time when this session was created (milliseconds)
   * Start point of time January 1, 1970 GMT.
   * @return long specifying when this session was created
   */
  long getCreationTime();

  /**
   * Returns the last time an event occurred on this session (milliseconds)
   * Start point of time January 1, 1970 GMT.
   * @return long specifying when last time an event occurred on this session
   */
  long getLastAccessedTime();

  /**
   * Return true if session is not released
   * @return true if session is not released
   */
  boolean isValid();

  /**
   * Release all resources append to session
   */
  void release();

  /**
   * Indicates if this is an App Session or a raw/base session
   * @return
   */
  boolean isAppSession();

  /**
   * Indicates if the session is replicable
   * 
   * @return
   */
  boolean isReplicable();

  /**
   * @return session-id as String (Session-Id AVP)
   */
  String getSessionId();
}
