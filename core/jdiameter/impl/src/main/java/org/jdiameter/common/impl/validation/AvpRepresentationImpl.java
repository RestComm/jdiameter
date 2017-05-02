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

package org.jdiameter.common.impl.validation;

import java.util.ArrayList;
import java.util.List;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.AvpRepresentation;

/**
 * Implementation of {@link AvpRepresentation} interface.
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 *
 */
public class AvpRepresentationImpl implements AvpRepresentation {

  protected String description;
  protected boolean mayEncrypt;
  protected boolean _protected;
  protected boolean _mandatory;
  protected String ruleMandatory;
  protected String ruleProtected;
  protected String ruleVendorBit;
  protected String originalType;
  protected String type; // String, in case user defines his own type

  // Usually this will be -1, as only SessionId has fixed position
  private int positionIndex = _FIX_POSITION_INDEX;
  protected int code = -1;
  protected long vendor = 0;
  protected boolean allowed = true;
  protected String multiplicityIndicator = "0";
  protected String name = "Some-AVP";
  protected boolean grouped = false;
  protected List<AvpRepresentation> children = new ArrayList<AvpRepresentation>();
  protected boolean weak = false;

  public AvpRepresentationImpl(AvpRepresentationImpl clone) {
    this(-1, clone.code, clone.getVendorId(), clone.getMultiplicityIndicator(), clone.getName());

    this.allowed = clone.allowed;
    this.code = clone.code;
    this.grouped = clone.grouped;
    this.multiplicityIndicator = clone.multiplicityIndicator;
    this.name = clone.name;
    this.positionIndex = clone.positionIndex;
    this.vendor = clone.vendor;
    this.weak = clone.weak;

    this._mandatory = clone._mandatory;
    this._protected = clone._protected;
    this.description = clone.description;
    this.mayEncrypt = clone.mayEncrypt;
    this.ruleMandatory = clone.ruleMandatory;
    this.ruleProtected = clone.ruleProtected;
    this.ruleVendorBit = clone.ruleVendorBit;
    this.originalType = clone.originalType;
    this.type = clone.type;
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  public AvpRepresentationImpl() {
    super();
    this.weak = true;
  }

  /**
   * Constructor used to represent weak children. - weak means its only
   * defined by name in some other AVP. After configuration parse procedure is
   * complete weak children are resolved. Weak children should not be stored
   * in Set or any other has structure, its due to nature of hashing, which is
   * done on vendor and code, which for weak children is always different than
   * fully defined AVP representation. <br>
   * This constructor should be generally used by extending classes, as well as
   * no argument constructor.
   *
   * @param name
   * @param vendor
   */
  public AvpRepresentationImpl(String name, long vendor) {
    super();
    this.name = name;
    this.vendor = vendor;
    this.weak = true;
  }

  /**
   * This constructor is used my validator to lookup correct representation.
   * Its hash and equals methods will match to fully populated avp
   * representation in any data structure
   *
   * @param code
   * @param vendor
   */
  public AvpRepresentationImpl(int code, long vendor) {
    super();

    this.code = code;
    this.vendor = vendor;
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  public AvpRepresentationImpl(int positionIndex, int code, long vendor, String multiplicityIndicator, String name) {
    super();
    this.positionIndex = positionIndex;

    this.code = code;
    this.vendor = vendor;
    this.multiplicityIndicator = multiplicityIndicator;
    this.name = name;
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  public AvpRepresentationImpl(int code, long vendor, String name) {
    super();
    this.positionIndex = -1;
    this.code = code;

    this.vendor = vendor;
    this.multiplicityIndicator = _MP_ZERO_OR_MORE;
    this.name = name;
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  public AvpRepresentationImpl(String name, String description, int code, boolean mayEncrypt, String ruleMandatory, String ruleProtected,
      String ruleVendorBit, long vendorId, String originalType, String type) {

    // zero and more, since its definition.
    this(-1, code, vendorId, _MP_ZERO_OR_MORE, name);

    this.description = description;

    this.mayEncrypt = mayEncrypt;
    this.ruleMandatory = ruleMandatory;
    this.ruleProtected = ruleProtected;
    this.ruleVendorBit = ruleVendorBit;

    if (this.ruleMandatory == null || this.ruleMandatory.equals("")) {
      this.ruleMandatory = _DEFAULT_MANDATORY;
    }
    if (this.ruleProtected == null || this.ruleProtected.equals("")) {
      this.ruleProtected = _DEFAULT_PROTECTED;
    }
    if (this.ruleVendorBit == null || this.ruleVendorBit.equals("")) {
      this.ruleVendorBit = _DEFAULT_VENDOR;
    }

    this.originalType = originalType;
    this.type = type;
    this._mandatory = this.ruleMandatory.equals("must");
    this._protected = this.ruleProtected.equals("must");
    if (type.equals(Type.Grouped.toString())) {
      this.setGrouped(true);
    }
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  @Override
  public boolean isPositionFixed() {
    return this.positionIndex == _FIX_POSITION_INDEX;
  }

  public void markFixPosition(int index) {
    this.positionIndex = index;
  }

  @Override
  public boolean isCountValidForMultiplicity(AvpSet destination, int numberToAdd) {
    AvpSet innerSet = destination.getAvps(getCode(), getVendorId());

    int count = numberToAdd;
    if (innerSet != null) {
      count += innerSet.size();
    }
    return this.isCountValidForMultiplicity(count);
  }

  @Override
  public boolean isCountValidForMultiplicity(int avpCount) {
    // This covers not_allowed
    if (!allowed) {
      if (avpCount == 0) {
        return true;
      }
    }
    else {
      if (this.multiplicityIndicator.equals(_MP_ZERO_OR_MORE)) {
        if (avpCount >= 0) {
          return true;
        }
      }
      else if (this.multiplicityIndicator.equals(_MP_ZERO_OR_ONE)) {
        if ((avpCount == 0) || (avpCount == 1)) {
          return true;
        }
      }
      else if (this.multiplicityIndicator.equals(_MP_ONE)) {
        if (avpCount == 1) {
          return true;
        }
      }
      else if (this.multiplicityIndicator.equals(_MP_ONE_AND_MORE)) {
        if (avpCount >= 1) {
          return true;
        }
      }
    }

    // if we did not return, we are screwed.
    return false;
  }

  public static int get_FIX_POSITION_INDEX() {
    return _FIX_POSITION_INDEX;
  }

  @Override
  public int getPositionIndex() {
    return positionIndex;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public long getVendorId() {
    return vendor;
  }

  @Override
  public boolean isAllowed() {
    return allowed;
  }

  @Override
  public boolean isAllowed(int avpCode, long vendorId) {
    if (this.isGrouped()) {
      // make better get ?
      for (AvpRepresentation rep : this.children) {
        if (rep.getCode() == avpCode && rep.getVendorId() == vendorId) {
          return rep.isAllowed();
        }
        else {
          continue;
        }
      }

      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public boolean isAllowed(int avpCode) {
    return this.isAllowed(avpCode, 0L);
  }

  @Override
  public String getMultiplicityIndicator() {
    return multiplicityIndicator;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isGrouped() {
    return grouped;
  }

  public void setGrouped(boolean grouped) {
    this.grouped = grouped;
  }

  @Override
  public List<AvpRepresentation> getChildren() {
    return children;
  }

  public void setChildren(List<AvpRepresentation> children) {
    this.children = children;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setVendorId(long vendor) {
    this.vendor = vendor;
  }

  public void setMultiplicityIndicator(String multiplicityIndicator) {
    this.multiplicityIndicator = multiplicityIndicator;
    if (this.multiplicityIndicator.equals(_MP_NOT_ALLOWED)) {
      this.allowed = false;
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isWeak() {
    return weak;
  }

  public void markWeak(boolean isWeak) {
    this.weak = isWeak;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isMayEncrypt() {
    return mayEncrypt;
  }

  @Override
  public String getRuleMandatory() {
    return ruleMandatory;
  }

  @Override
  public int getRuleMandatoryAsInt() {
    return Rule.valueOf(ruleMandatory).ordinal();
  }

  @Override
  public String getRuleProtected() {
    return ruleProtected;
  }

  @Override
  public int getRuleProtectedAsInt() {
    return Rule.valueOf(ruleProtected).ordinal();
  }

  @Override
  public String getRuleVendorBit() {
    return ruleVendorBit;
  }

  @Override
  public int getRuleVendorBitAsInt() {
    return Rule.valueOf(ruleVendorBit).ordinal();
  }

  @Override
  public String getOriginalType() {
    return originalType;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public boolean isProtected() {
    return _protected;
  }

  @Override
  public boolean isMandatory() {
    return _mandatory;
  }

  @Override
  public void validate(Avp avp) throws AvpNotAllowedException {
    if (isGrouped()) {
      try {
        AvpSet avpAsGrouped = avp.getGrouped();
        validate(avpAsGrouped);
      }
      catch (AvpDataException e) {
        throw new AvpNotAllowedException("Failed to parse AVP to grouped!", e, code, vendor);
      }
    }
    else {
      // dont care
    }
  }

  @Override
  public void validate(AvpSet avpSet) throws AvpNotAllowedException { //this is used in RAs, cause ... AvpSet is asexual AVP, no code, no vendor
    // let it rip
    for (AvpRepresentation childrenVAvp : getChildren()) {
      AvpSet childSset = avpSet.getAvps(childrenVAvp.getCode(), childrenVAvp.getVendorId());
      int count = childSset.size();

      if (!childrenVAvp.isCountValidForMultiplicity(count)) {
        throw new AvpNotAllowedException("AVP: " + childrenVAvp + " has wrong count, in grouped parent avp - " + (count) + ", allowed: "
            + childrenVAvp.getMultiplicityIndicator(), getCode(), getVendorId());
      }
      if (childrenVAvp.isGrouped()) {
        for (int index = 0; index < childSset.size(); index++) {

          Avp presumablyGrouped = childSset.getAvpByIndex(index);
          childrenVAvp.validate(presumablyGrouped);
        }

      }
      // else we are good ?
    }
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();

    sb.append("name: ").append(this.getName()).append(", code: ").append(this.getCode()).append(", vendor: ").append(this.getVendorId()).append(", weak: ")
    .append(this.isWeak()).append(", grouped: ").append(this.isGrouped()).append(", type: ").append(this.getType()).append(", multiplicity: ")
    .append(this.getMultiplicityIndicator());
    if (this.isGrouped()) {
      for (AvpRepresentation child : this.getChildren()) {
        String childStr = child.toString().replace("\n", "\n---");
        sb.append("\n---" + childStr);
      }
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    // code+vendor is enough by AVP def
    final int prime = 31;
    int result = 1;
    result = prime * result + code;
    result = prime * result + (int) (vendor ^ (vendor >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    // code+vendor is enough by AVP def
    AvpRepresentationImpl other = (AvpRepresentationImpl) obj;
    if (code != other.code) {
      return false;
    }
    if (vendor != other.vendor) {
      return false;
    }
    return true;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    AvpRepresentationImpl clone = new AvpRepresentationImpl();
    clone.allowed = this.allowed;
    clone.code = this.code;

    clone.grouped = this.grouped;
    clone.multiplicityIndicator = this.multiplicityIndicator;
    clone.name = this.name;
    clone.positionIndex = this.positionIndex;
    clone.vendor = this.vendor;
    clone.weak = this.weak;

    clone._mandatory = this._mandatory;
    clone._protected = this._protected;
    clone.description = this.description;
    clone.mayEncrypt = this.mayEncrypt;
    clone.ruleMandatory = this.ruleMandatory;
    clone.ruleProtected = this.ruleProtected;
    clone.ruleVendorBit = this.ruleVendorBit;
    clone.originalType = this.originalType;
    clone.type = this.type;

    List<AvpRepresentation> cloneChildren = new ArrayList<AvpRepresentation>();
    clone.children = cloneChildren;
    for (AvpRepresentation c : this.children) {
      cloneChildren.add((AvpRepresentation) c.clone());
    }
    return clone;
  }
}
