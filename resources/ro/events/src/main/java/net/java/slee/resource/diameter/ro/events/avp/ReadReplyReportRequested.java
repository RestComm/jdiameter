/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * 
 * Java class to represent the ReadReplyReportRequested enumerated type.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReadReplyReportRequested implements Enumerated {
  public static final int _NO = 0;

  public static final int _YES = 1;

  public static final net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested NO = new ReadReplyReportRequested(_NO);

  public static final net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested YES = new ReadReplyReportRequested(_YES);

  private ReadReplyReportRequested(int v) {
    value = v;
  }

  /**
   * Return the value of this instance of this enumerated type.
   */
  public static ReadReplyReportRequested fromInt(int type) {
    switch (type) {
    case _NO:
      return NO;
    case _YES:
      return YES;

    default:
      throw new IllegalArgumentException(
          "Invalid ReadReplyReportRequested value: " + type);
    }
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    switch (value) {
    case _NO:
      return "NO";
    case _YES:
      return "YES";
    default:
      return "<Invalid Value>";
    }
  }

  private Object readResolve() throws StreamCorruptedException {
    try {
      return fromInt(value);
    } catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found: "
          + value);
    }
  }

  private int value = 0;

}
