/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.jdiameter.server.api.agent;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IRedirect extends IAgent {

  /**
   * Default property name for redirect host usage.
   */
  public static final String RHU_PROPERTY = "rdr.host.usage";

  public static final int RHU_DONT_CACHE = 0;
  public static final int RHU_ALL_SESSION = 1;
  public static final int RHU_ALL_REALM = 2;
  public static final int RHU_REALM_AND_APPLICATION = 3;
  public static final int RHU_ALL_APPLICATION = 4;
  public static final int RHU_ALL_HOST = 5;
  public static final int RHU_ALL_USER = 6;
}
