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

package org.jdiameter.client.api.parser;

/**
 * Signals that an parser exception has occurred in a during decoding message
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ParseException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Create instance of class
   */
  public ParseException() {
  }

  /**
   * Create instance of class with predefined parameters
   * @param message error message
   */
  public ParseException(String message) {
    super(message);
  }

  /**
   * Create instance of class with predefined parameters
   * @param message error message
   * @param cause error cause
   */
  public ParseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Create instance of class with predefined parameters
   * @param cause error cause
   */
  public ParseException(Throwable cause) {
    super(cause);
  }
}
