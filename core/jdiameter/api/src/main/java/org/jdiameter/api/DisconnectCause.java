/*
 * JBoss, Home of Professional Open Source
 *
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
