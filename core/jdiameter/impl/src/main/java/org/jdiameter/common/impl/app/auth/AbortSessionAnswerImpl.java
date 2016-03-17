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

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.Request;
import org.jdiameter.api.auth.events.AbortSessionAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AbortSessionAnswerImpl extends AppAnswerEventImpl implements AbortSessionAnswer {

	private static final long serialVersionUID = 1L;

	public AbortSessionAnswerImpl(Request request, int authRequestType, long resultCode) {
		super(request.createAnswer(resultCode));
		try {
			getMessage().getAvps().addAvp(Avp.AUTH_REQUEST_TYPE, authRequestType);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * @param answer
	 */
	public AbortSessionAnswerImpl(Answer answer) {
		super(answer);
	}

	/**
	 * @param request
	 * @param vendorId
	 * @param resultCode
	 */
	public AbortSessionAnswerImpl(Request request, long vendorId, long resultCode) {
		super(request, vendorId, resultCode);
	}

	/**
	 * @param request
	 * @param resultCode
	 */
	public AbortSessionAnswerImpl(Request request, long resultCode) {
		super(request, resultCode);
	}

	/**
	 * @param request
	 */
	public AbortSessionAnswerImpl(Request request) {
		super(request);
	}

}
