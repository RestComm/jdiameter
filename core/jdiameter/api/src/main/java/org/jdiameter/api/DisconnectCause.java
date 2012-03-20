/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package org.jdiameter.api;

/**
 * Interface defining disconnect cause codes
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface DisconnectCause {

  /**
   * A scheduled reboot is imminent.
   */
  public static final int REBOOTING = 0;

  /**
   * The peer's internal resources are constrained, and it has
   * determined that the transport connection needs to be closed.
   */
  public static final int BUSY = 1;

  /**
   * The peer has determined that it does not see a need for the
   * transport connection to exist, since it does not expect any
   * messages to be exchanged in the near future.
   */
  public static final int DO_NOT_WANT_TO_TALK_TO_YOU = 2;
}
