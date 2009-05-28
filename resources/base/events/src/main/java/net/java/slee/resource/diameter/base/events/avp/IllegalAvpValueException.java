/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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
package net.java.slee.resource.diameter.base.events.avp;

/**
 * Start time:14:44:47 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * This exception is thrown when wrong fetch type is used on avp/message, it
 * indicates that underlying avp is of wrong type or it has no value set yet.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class IllegalAvpValueException extends RuntimeException {

	public IllegalAvpValueException() {

	}

	public IllegalAvpValueException(String message) {
		super(message);

	}

	public IllegalAvpValueException(Throwable cause) {
		super(cause);

	}

	public IllegalAvpValueException(String message, Throwable cause) {
		super(message, cause);

	}

}
