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
public interface AvpRepresentation{

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0     The AVP MUST NOT be present in the message.
   * </pre>
   */
  public final static String _MP_NOT_ALLOWED = "0";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0+    Zero or more instances of the AVP MAY be present in the message.
   * </pre>
   */
  public final static String _MP_ZERO_OR_MORE = "0+";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 0-1   Zero or one instance of the AVP MAY be present in the message.
   *       It is considered an error if there are more than one instance of the AVP.
   * </pre>
   */
  public final static String _MP_ZERO_OR_ONE = "0-1";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 1     One instance of the AVP MUST be present in the message.
   *       message.
   * </pre>
   */
  public final static String _MP_ONE = "1";

  /**
   * <pre>
   * Represents multiplicity of AVP:
   * 1+    At least one instance of the AVP MUST be present in the
   *       message.
   * </pre>
   */
  public final static String _MP_ONE_AND_MORE = "1+";

  public final static String _DEFAULT_MANDATORY = "may";
  public final static String _DEFAULT_PROTECTED = "may";
  public final static String _DEFAULT_VENDOR = "mustnot";

  public final static int _FIX_POSITION_INDEX = -1;

  public enum Rule {
    must, may, mustnot, shouldnot
  };

  public enum Type {
    OctetString, Integer32, Integer64, Unsigned32, Unsigned64, Float32, Float64, Grouped, Address, Time, UTF8String, DiameterIdentity, DiameterURI, Enumerated, IPFilterRule, QoSFilterRule
  };

  public boolean isPositionFixed();

  //public void markFixPosition(int index);

  public boolean isCountValidForMultiplicity(int avpCount);

  public boolean isCountValidForMultiplicity(AvpSet destination,int numberToAdd);

  public boolean isAllowed(int avpCode,long vendorId);

  public boolean isAllowed(int avpCode);

  public int getPositionIndex();

  public int getCode();

  public long getVendorId();

  public boolean isAllowed();

  public String getMultiplicityIndicator();

  public String getName();

  public boolean isGrouped();

  //public void setGrouped(boolean grouped);

  public List<AvpRepresentation> getChildren();

  //public void setChildren(List<AvpRepresentation> children);

  //public void setCode(int code);

  //public void setVendorId(long vendor);

  //public void setMultiplicityIndicator(String multiplicityIndicator);

  //public void setName(String name);

  public boolean isWeak();

  //public void markWeak(boolean isWeak);

  public String getDescription();

  public boolean isMayEncrypt();

  public String getRuleMandatory();

  public int getRuleMandatoryAsInt();

  public String getRuleProtected();

  public int getRuleProtectedAsInt();

  public String getRuleVendorBit();

  public int getRuleVendorBitAsInt();

  public boolean isConstrained();

  public String getType();

  public boolean isProtected();

  public boolean isMandatory();

  /**
   * Validates passed avp.  
   * @param avp - simply avp which should be confronted vs definition
   */
  public void validate(Avp avp) throws AvpNotAllowedException;

  /**
   * Validates passed avp.  
   * @param avpSet - AvpSet which represents internal content of this avp
   */
  public void validate(AvpSet avpSet) throws AvpNotAllowedException;

  public String toString();

  public int hashCode();

  public boolean equals(Object obj);

  public Object clone() throws CloneNotSupportedException;

}
