/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution statements
 * applied by the authors. All third-party contributions are distributed under
 * license by Red Hat Middleware LLC.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.diameter.api;

import org.jdiameter.api.Message;

/**
 * 
 * DiameterMessageFactory.java
 *
 * @version 1.0 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface DiameterMessageFactory
{
  /**
   * Creates a new Diameter Message (request or answer, depending on boolean flag) 
   * with the specified Command Code and Application Id
   *  
   * @param isRequest
   * @param commandCode
   * @param applicationId
   * @return
   */
  public Message createMessage(boolean isRequest, int commandCode, long applicationId);
  
  /**
   * Creates a new Diameter Message (Request) with the specified Command Code and 
   * Application Id 
   * 
   * @param commandCode
   * @param applicationId
   * @return
   */
  public Message createRequest(int commandCode, long applicationId);
  
  /**
   * Creates a new Diameter Message (Answer) with the specified Command Code and 
   * Application Id 
   * 
   * @param commandCode
   * @param applicationId
   * @return
   */
  public Message createAnswer(int commandCode, long applicationId);

}
