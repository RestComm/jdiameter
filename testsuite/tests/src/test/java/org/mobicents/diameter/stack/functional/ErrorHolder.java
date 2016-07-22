/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
package org.mobicents.diameter.stack.functional;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ErrorHolder {

  private String errorMessage;
  private Throwable error;

  /**
   * @param error
   */
  public ErrorHolder(Throwable error) {
    super();
    this.error = error;
  }

  /**
   * @param errorMessage
   */
  public ErrorHolder(String errorMessage) {
    super();
    this.errorMessage = errorMessage;
  }

  /**
   * @param errorMessage
   * @param error
   */
  public ErrorHolder(String errorMessage, Throwable error) {
    super();
    this.errorMessage = errorMessage;
    this.error = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Throwable getError() {
    return error;
  }

  @Override
  public String toString() {
    // for now. add StackTrace gen
    StringBuilder sb = new StringBuilder(" Msg: ");
    if (errorMessage != null) {
      sb.append(errorMessage).append(", stack trace: \n");
    }
    else {
      sb.append("EMPTY, stack trace: \n");
    }

    if (error != null) {
      error.fillInStackTrace();
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      error.printStackTrace(pw);
      sb.append(sw.getBuffer().toString());
    }

    return sb.toString();
  }

}
