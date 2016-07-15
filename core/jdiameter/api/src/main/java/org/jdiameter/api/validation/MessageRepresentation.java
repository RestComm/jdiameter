 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

  Map<AvpRepresentation, AvpRepresentation> getMessageAvps();

  int getCommandCode();

  long getApplicationId();

  boolean isRequest();

  String getName();

  /**
   * Fetches Avp representation for given code. If no such AVP is found,
   * <b>null</b> value is returned. AVP must be top level AVP to be fetched by
   * this operation.
   *
   * @param code
   *            - positive integer, equal to AVP code.
   * @return AvpRepresentation for given code or <b>null</b>
   */
  AvpRepresentation getAvp(int code);

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
  AvpRepresentation getAvp(int code, long vendorId);

  boolean isCountValidForMultiplicity(int code, int avpCount);

  boolean isCountValidForMultiplicity(int code, long vendorId, int avpCount);

  boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId, int numberToAdd);

  boolean isCountValidForMultiplicity(AvpSet destination, int code, int numberToAdd);

  boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId);

  boolean isCountValidForMultiplicity(AvpSet destination, int code);

  boolean isAllowed(int avpCode, long vendorId);

  boolean isAllowed(int avpCode);

  void validate(Message msg, ValidatorLevel validatorLevel) throws AvpNotAllowedException;

}
