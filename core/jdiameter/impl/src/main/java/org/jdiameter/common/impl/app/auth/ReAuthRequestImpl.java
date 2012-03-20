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

package org.jdiameter.common.impl.app.auth;

import static org.jdiameter.api.Avp.AUTH_APPLICATION_ID;
import static org.jdiameter.api.Avp.RE_AUTH_REQUEST_TYPE;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.auth.events.ReAuthRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ReAuthRequestImpl extends AppRequestEventImpl implements ReAuthRequest {

  private static final long serialVersionUID = 1L;

  public ReAuthRequestImpl(Message message) {
    super(message);
  }

  public long getAuthApplicationId() throws AvpDataException {
    Avp authApplicationIdAvp = message.getAvps().getAvp(AUTH_APPLICATION_ID);
    if (authApplicationIdAvp != null) {
      return authApplicationIdAvp.getUnsigned32();
    }
    else {
      throw new AvpDataException("Avp AUTH_APPLICATION_ID not found");
    }
  }

  public int getReAuthRequestType() throws AvpDataException {
    Avp reAuthRequestTypeAvp = message.getAvps().getAvp(RE_AUTH_REQUEST_TYPE);
    if (reAuthRequestTypeAvp != null) {
      return reAuthRequestTypeAvp.getInteger32();
    }
    else {
      throw new AvpDataException("Avp RE_AUTH_REQUEST_TYPE not found");
    }
  }
}
