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

import java.util.List;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;

/**
 * Represents avp, it stores info about presence, multiplicity, avp
 * code, vendor.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public interface AvpRepresentation {

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0     The AVP MUST NOT be present in the message.
   * </pre>
   */
  String _MP_NOT_ALLOWED = "0";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0+    Zero or more instances of the AVP MAY be present in the message.
   * </pre>
   */
  String _MP_ZERO_OR_MORE = "0+";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0-1   Zero or one instance of the AVP MAY be present in the message.
   *       It is considered an error if there are more than one instance of the AVP.
   * </pre>
   */
  String _MP_ZERO_OR_ONE = "0-1";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 1     One instance of the AVP MUST be present in the message.
   *       message.
   * </pre>
   */
  String _MP_ONE = "1";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 1+    At least one instance of the AVP MUST be present in the
   *       message.
   * </pre>
   */
  String _MP_ONE_AND_MORE = "1+";

  String _DEFAULT_MANDATORY = "may";
  String _DEFAULT_PROTECTED = "may";
  String _DEFAULT_VENDOR = "mustnot";

  int _FIX_POSITION_INDEX = -1;

  enum Rule {
    must, may, mustnot, shouldnot
  };

  enum Type {
    OctetString, Integer32, Integer64, Unsigned32, Unsigned64, Float32, Float64, Grouped, Address,
    Time, UTF8String, DiameterIdentity, DiameterURI, Enumerated, IPFilterRule, QoSFilterRule
  };

  boolean isPositionFixed();

  //public void markFixPosition(int index);

  boolean isCountValidForMultiplicity(int avpCount);

  boolean isCountValidForMultiplicity(AvpSet destination, int numberToAdd);

  boolean isAllowed(int avpCode, long vendorId);

  boolean isAllowed(int avpCode);

  int getPositionIndex();

  int getCode();

  long getVendorId();

  boolean isAllowed();

  String getMultiplicityIndicator();

  String getName();

  boolean isGrouped();

  //public void setGrouped(boolean grouped);

  List<AvpRepresentation> getChildren();

  //public void setChildren(List<AvpRepresentation> children);

  //public void setCode(int code);

  //public void setVendorId(long vendor);

  //public void setMultiplicityIndicator(String multiplicityIndicator);

  //public void setName(String name);

  boolean isWeak();

  //public void markWeak(boolean isWeak);

  String getDescription();

  boolean isMayEncrypt();

  String getRuleMandatory();

  int getRuleMandatoryAsInt();

  String getRuleProtected();

  int getRuleProtectedAsInt();

  String getRuleVendorBit();

  int getRuleVendorBitAsInt();

  String getOriginalType();

  String getType();

  boolean isProtected();

  boolean isMandatory();

  /**
   * Validates passed avp.
   * @param avp - simply avp which should be confronted vs definition
   */
  void validate(Avp avp) throws AvpNotAllowedException;

  /**
   * Validates passed avp.
   * @param avpSet - AvpSet which represents internal content of this avp
   */
  void validate(AvpSet avpSet) throws AvpNotAllowedException;

  @Override
  String toString();

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

  Object clone() throws CloneNotSupportedException;

}
