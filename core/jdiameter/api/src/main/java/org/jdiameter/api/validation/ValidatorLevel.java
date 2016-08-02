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

package org.jdiameter.api.validation;

/**
 * Represents possible levels for Diameter Validator
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public class ValidatorLevel {

  public static final String _OFF = "OFF";
  public static final String _MESSAGE = "MESSAGE";
  public static final String _ALL = "ALL";

  public static final ValidatorLevel OFF = new ValidatorLevel(_OFF);
  public static final ValidatorLevel MESSAGE = new ValidatorLevel(_MESSAGE);
  public static final ValidatorLevel ALL = new ValidatorLevel(_ALL);

  private String name = null;

  private ValidatorLevel(String name) {
    super();
    this.name = name;
  }

  public static ValidatorLevel fromString(String s) throws IllegalArgumentException {
    if (s.toUpperCase().equals(_OFF)) {
      return OFF;
    }
    if (s.toUpperCase().equals(_MESSAGE)) {
      return MESSAGE;
    }
    if (s.toUpperCase().equals(_ALL)) {
      return ALL;
    }
    throw new IllegalArgumentException("No level for such value: " + s);
  }

  @Override
  public String toString() {
    return "ValidatorLevel [name=" + name + "]";
  }

}
