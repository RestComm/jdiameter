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
package org.jdiameter.api.cca.events;

import org.jdiameter.api.app.AppRequestEvent;

/**
 * The Credit-Control-Request message (CCR) is indicated by the command-code field being set to
 * 272 and the 'R' bit being set in the Command Flags field.  It is used between the Diameter
 * credit-control client and the credit-control server to request credit authorization for a given
 * service.
 * 
 * The Auth-Application-Id MUST be set to the value 4, indicating the Diameter credit-control
 * application.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface JCreditControlRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "CCR";
  public static final String _LONG_NAME = "Credit-Control-Request";

  public static final int code = 272;

  boolean isRequestedActionAVPPresent();

  int getRequestedActionAVPValue();

  boolean isRequestTypeAVPPresent();

  int getRequestTypeAVPValue();

}
