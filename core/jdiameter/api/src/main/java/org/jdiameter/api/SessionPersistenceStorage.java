/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

import java.util.List;

/**
 * Enables to review and supervise the current state of session persistence map that is used
 * for routing that preserves sticky sessions paradigm. Read only access is given for
 * the sake of safety issues.
 */
public interface SessionPersistenceStorage {

  /**
   * Returns a list of all session persistence records that are currently in operation.
   *
   * @param maxLimit maximum number of records to be listed (0 corresponds to no limit)
   * @return list of active records
   */
  List<String> dumpStickySessions(int maxLimit);
}
