/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base;

import org.jdiameter.api.InternalException;

/**
 * Thrown when an activity cannot be created for any reason.
 * 
 * @author Open Cloud
 */
public class CreateActivityException extends Exception {
    public CreateActivityException(String message) {
        super(message);
    }

    public CreateActivityException(String message, Throwable cause) {
        super(message, cause);
    }

	public CreateActivityException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CreateActivityException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
