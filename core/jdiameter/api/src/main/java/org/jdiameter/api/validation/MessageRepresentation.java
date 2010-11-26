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
package org.jdiameter.api.validation;

import java.util.Map;

import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;

/**
 * This class represents message/command in validation framework. It contains
 * basic info about command along with avp list - their multiplicity and
 * allowance.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public interface MessageRepresentation {

	public Map<AvpRepresentation, AvpRepresentation> getMessageAvps();

	public int getCommandCode();

	public long getApplicationId();

	public boolean isRequest();

	public String getName();

	/**
	 * Fetches Avp representation for given code. If no such AVP is found,
	 * <b>null</b> value is returned. AVP must be top level AVP to be fetched by
	 * this operation.
	 * 
	 * @param code
	 *            - positive integer, equal to AVP code.
	 * @return AvpRepresentation for given code or <b>null</b>
	 */
	public AvpRepresentation getAvp(int code);

	/**
	 * Fetches Avp representation for given code and vendorId. If no such AVP is
	 * found, <b>null</b> value is returned. AVP must be top level AVP to be
	 * fetched by this operation.
	 * 
	 * @param code
	 *            - positive integer, equal to AVP code.
	 * @param vendorId
	 *            - positive long representing vendor code.
	 * @return AvpRepresentation for given code/vendor pair or <b>null</b>
	 */
	public AvpRepresentation getAvp(int code, long vendorId);

	public boolean isCountValidForMultiplicity(int code, int avpCount);

	public boolean isCountValidForMultiplicity(int code, long vendorId, int avpCount);

	public boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId, int numberToAdd);

	public boolean isCountValidForMultiplicity(AvpSet destination, int code, int numberToAdd);

	public boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId);

	public boolean isCountValidForMultiplicity(AvpSet destination, int code);

	public boolean isAllowed(int avpCode, long vendorId);

	public boolean isAllowed(int avpCode);

	public void validate(Message msg, ValidatorLevel validatorLevel) throws AvpNotAllowedException;

}
