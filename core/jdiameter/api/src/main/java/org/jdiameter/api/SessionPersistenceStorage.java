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
