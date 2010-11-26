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
    if (s.toUpperCase().equals(_OFF))
      return OFF;
    if (s.toUpperCase().equals(_MESSAGE))
      return MESSAGE;
    if (s.toUpperCase().equals(_ALL))
      return ALL;
    throw new IllegalArgumentException("No level for such value: " + s);
  }

  public String toString() {
    return "ValidatorLevel [name=" + name + "]";
  }

}
