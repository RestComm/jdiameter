/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors by the
 * @authors tag. See the copyright.txt in the distribution for a
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
package org.jdiameter.common.impl.app;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AppAnswerEventImpl extends AppEventImpl implements AppAnswerEvent {

  private static final long serialVersionUID = 1L;

  public AppAnswerEventImpl(Request request, long resultCode) {
    this(request.createAnswer(resultCode));
  }

  public AppAnswerEventImpl(Request request, long vendorId, long resultCode) {
    this(request.createAnswer(vendorId, resultCode));
  }

  public AppAnswerEventImpl(Request request) {
    this(request.createAnswer());
  }

  public AppAnswerEventImpl(Answer answer) {
    super(answer);
  }

  public Avp getResultCodeAvp() throws AvpDataException {
    Avp resultCodeAvp = message.getAvps().getAvp(Avp.RESULT_CODE);
    return resultCodeAvp != null ? resultCodeAvp : message.getAvps().getAvp(Avp.EXPERIMENTAL_RESULT_CODE);
  }
}
