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
package org.jdiameter.api.ro.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The CCR messages, indicated by the Command-Code field set to 272 is sent by the CTF to the OCF
 * in order to request credits for the request bearer / subsystem / service. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface RoCreditControlRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "CCR";
  public static final String _LONG_NAME = "Credit-Control-Request";

  public static final int code = 272;

  boolean isRequestedActionAVPPresent();

  int getRequestedActionAVPValue();

  boolean isRequestTypeAVPPresent();

  int getRequestTypeAVPValue();

}
