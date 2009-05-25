/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.CcUnitType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:03:57 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link GSUPoolReferenceAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GSUPoolReferenceAvpImpl extends GroupedAvpImpl implements GSUPoolReferenceAvp {

  private static transient Logger logger = Logger.getLogger(GSUPoolReferenceAvpImpl.class);

  public GSUPoolReferenceAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getCreditControlUnitType()
   */
  public CcUnitType getCreditControlUnitType() {
    if (hasCreditControlUnitType()) {
      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Unit_Type);
      try {
        return CcUnitType.INPUT_OCTETS.fromInt(rawAvp.getInteger32());
      } catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Unit_Type);
        logger.error("Failure while trying to obtain CC-Unit-Type AVP.", e);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getGSUPoolIdentifier()
   */
  public long getGSUPoolIdentifier() {
    if (hasGSUPoolIdentifier()) {
      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);

      try {
        return rawAvp.getUnsigned32();
      } catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.G_S_U_Pool_Identifier);
        logger.error("Failure while trying to obtain G-S-U-Pool-Identifier AVP.", e);
      }
    }

    return -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#getUnitValue()
   */
  public UnitValueAvp getUnitValue() {
    if (hasUnitValue()) {
      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Unit_Value);
      try {
        return new UnitValueAvpImpl(CreditControlAVPCodes.Unit_Value, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      } catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Unit_Value);
        logger.error("Failure while trying to obtain Unit-Value AVP.", e);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasCreditControlUnitType()
   */
  public boolean hasCreditControlUnitType() {
    return super.hasAvp(CreditControlAVPCodes.CC_Unit_Type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasGSUPoolIdentifier()
   */
  public boolean hasGSUPoolIdentifier() {
    return super.hasAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#hasUnitValue()
   */
  public boolean hasUnitValue() {
    return super.hasAvp(CreditControlAVPCodes.Unit_Value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setCreditControlUnitType
   * (net.java.slee.resource.diameter.cca.events.avp.CcUnitType)
   */
  public void setCreditControlUnitType(CcUnitType ccUnitType) {
    addAvp(CreditControlAVPCodes.CC_Unit_Type, ccUnitType.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setGSUPoolIdentifier(long)
   */
  public void setGSUPoolIdentifier(long gsuPoolIdentifier) {
    addAvp(CreditControlAVPCodes.G_S_U_Pool_Identifier, gsuPoolIdentifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp#setUnitValue
   * (net.java.slee.resource.diameter.cca.events.avp.UnitValueAvp)
   */
  public void setUnitValue(UnitValueAvp unitValue) {
    addAvp(CreditControlAVPCodes.Unit_Value, unitValue.byteArrayValue());
  }

}
