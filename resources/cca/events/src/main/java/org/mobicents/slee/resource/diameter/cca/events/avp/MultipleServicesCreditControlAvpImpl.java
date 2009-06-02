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

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp;
import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;
import net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp;
import net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType;
import net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:16:29:27 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link MultipleServicesCreditControlAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MultipleServicesCreditControlAvpImpl extends GroupedAvpImpl implements MultipleServicesCreditControlAvp {

  public MultipleServicesCreditControlAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getFinalUnitIndication()
   */
  public FinalUnitIndicationAvp getFinalUnitIndication() {
    return (FinalUnitIndicationAvp) getAvpAsCustom(CreditControlAVPCodes.Final_Unit_Indication, FinalUnitIndicationAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getGrantedServiceUnit()
   */
  public GrantedServiceUnitAvp getGrantedServiceUnit() {
    return (GrantedServiceUnitAvp) getAvpAsCustom(CreditControlAVPCodes.Granted_Service_Unit, GrantedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getGsuPoolReferences()
   */
  public GSUPoolReferenceAvp[] getGsuPoolReferences() {
    return (GSUPoolReferenceAvp[]) getAvpsAsCustom(CreditControlAVPCodes.G_S_U_Pool_Reference, GrantedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getRatingGroup()
   */
  public long getRatingGroup() {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Rating_Group);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getRequestedServiceUnit()
   */
  public RequestedServiceUnitAvp getRequestedServiceUnit() {
    return (RequestedServiceUnitAvp) getAvpAsCustom(CreditControlAVPCodes.Requested_Service_Unit, RequestedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getResultCode()
   */
  public long getResultCode() {
    return getAvpAsUnsigned32(DiameterAvpCodes.RESULT_CODE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getServiceIdentifiers()
   */
  public long[] getServiceIdentifiers() {
    return getAvpsAsUnsigned32(CreditControlAVPCodes.Service_Identifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getTariffChangeUsage()
   */
  public TariffChangeUsageType getTariffChangeUsage() {
    return (TariffChangeUsageType) getAvpAsEnumerated(CreditControlAVPCodes.Tariff_Change_Usage, TariffChangeUsageType.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getUsedServiceUnits()
   */
  public UsedServiceUnitAvp[] getUsedServiceUnits() {
    return (UsedServiceUnitAvp[]) getAvpsAsCustom(CreditControlAVPCodes.Used_Service_Unit, UsedServiceUnitAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#getValidityTime()
   */
  public long getValidityTime() {
    return getAvpAsUnsigned32(CreditControlAVPCodes.Validity_Time);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasFinalUnitIndication()
   */
  public boolean hasFinalUnitIndication() {
    return hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasGrantedServiceUnit()
   */
  public boolean hasGrantedServiceUnit() {
    return hasAvp(CreditControlAVPCodes.Granted_Service_Unit);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRatingGroup()
   */
  public boolean hasRatingGroup() {
    return hasAvp(CreditControlAVPCodes.Rating_Group);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasRequestedServiceUnit()
   */
  public boolean hasRequestedServiceUnit() {
    return hasAvp(CreditControlAVPCodes.Requested_Service_Unit);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasResultCode()
   */
  public boolean hasResultCode() {
    return hasAvp(DiameterAvpCodes.RESULT_CODE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasTariffChangeUsage()
   */
  public boolean hasTariffChangeUsage() {
    return hasAvp(CreditControlAVPCodes.Tariff_Change_Usage);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#hasValidityTime()
   */
  public boolean hasValidityTime() {
    return hasAvp(CreditControlAVPCodes.Validity_Time);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setFinalUnitIndication(net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp)
   */
  public void setFinalUnitIndication(FinalUnitIndicationAvp finalUnitIndication) {
    addAvp(CreditControlAVPCodes.Final_Unit_Indication, finalUnitIndication.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGrantedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp)
   */
  public void setGrantedServiceUnit(GrantedServiceUnitAvp grantedServiceUnit) {
    addAvp(CreditControlAVPCodes.Granted_Service_Unit, grantedServiceUnit.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGsuPoolReference(net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp)
   */
  public void setGsuPoolReference(GSUPoolReferenceAvp gsuPoolReference) {
    addAvp(CreditControlAVPCodes.G_S_U_Pool_Reference, gsuPoolReference.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setGsuPoolReferences(net.java.slee.resource.diameter.cca.events.avp.GSUPoolReferenceAvp[])
   */
  public void setGsuPoolReferences(GSUPoolReferenceAvp[] gsuPoolReferences) {
    for (GSUPoolReferenceAvp gsuPoolReference : gsuPoolReferences) {
      setGsuPoolReference(gsuPoolReference);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRatingGroup(long)
   */
  public void setRatingGroup(long ratingGroup) {
    addAvp(CreditControlAVPCodes.Rating_Group, ratingGroup);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setRequestedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.RequestedServiceUnitAvp)
   */
  public void setRequestedServiceUnit(RequestedServiceUnitAvp requestedServiceUnit) {
    addAvp(CreditControlAVPCodes.Requested_Service_Unit, requestedServiceUnit.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setResultCode(long)
   */
  public void setResultCode(long resultCode) {
    addAvp(DiameterAvpCodes.RESULT_CODE, resultCode);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setServiceIdentifier(long)
   */
  public void setServiceIdentifier(long serviceIdentifier) {
    addAvp(CreditControlAVPCodes.Service_Identifier, serviceIdentifier);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setServiceIdentifiers(long[])
   */
  public void setServiceIdentifiers(long[] serviceIdentifiers) {
    for (long serviceIdentifier : serviceIdentifiers) {
      setServiceIdentifier(serviceIdentifier);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setTariffChangeUsage(net.java.slee.resource.diameter.cca.events.avp.TariffChangeUsageType)
   */
  public void setTariffChangeUsage(TariffChangeUsageType tariffChangeUsage) {
    addAvp(CreditControlAVPCodes.Tariff_Change_Usage, (long)tariffChangeUsage.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setUsedServiceUnit(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp)
   */
  public void setUsedServiceUnit(UsedServiceUnitAvp usedServiceUnit) {
    addAvp(CreditControlAVPCodes.Used_Service_Unit, usedServiceUnit.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setUsedServiceUnits(net.java.slee.resource.diameter.cca.events.avp.UsedServiceUnitAvp[])
   */
  public void setUsedServiceUnits(UsedServiceUnitAvp[] usedServiceUnits) {
    for(UsedServiceUnitAvp usedServiceUnit : usedServiceUnits) {
      setUsedServiceUnit(usedServiceUnit);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp#setValidityTime(long)
   */
  public void setValidityTime(long validityTime) {
    addAvp(CreditControlAVPCodes.Validity_Time, validityTime);
  }

}
