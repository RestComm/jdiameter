package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import static net.java.slee.resource.diameter.cxdx.events.avp.DiameterCxDxAvpCodes.*;

import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * ChargingInformationImpl.java
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ChargingInformationImpl extends GroupedAvpImpl implements ChargingInformation {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public ChargingInformationImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#getPrimaryChargingCollectionFunctionName()
   */
  public DiameterURI getPrimaryChargingCollectionFunctionName() {
    return getAvpAsDiameterURI(PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#getPrimaryEventChargingFunctionName()
   */
  public DiameterURI getPrimaryEventChargingFunctionName() {
    return getAvpAsDiameterURI(PRIMARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#getSecondaryChargingCollectionFunctionName()
   */
  public DiameterURI getSecondaryChargingCollectionFunctionName() {
    return getAvpAsDiameterURI(SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#getSecondaryEventChargingFunctionName()
   */
  public DiameterURI getSecondaryEventChargingFunctionName() {
    return getAvpAsDiameterURI(SECONDARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#hasPrimaryChargingCollectionFunctionName()
   */
  public boolean hasPrimaryChargingCollectionFunctionName() {
    return hasAvp(PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#hasPrimaryEventChargingFunctionName()
   */
  public boolean hasPrimaryEventChargingFunctionName() {
    return hasAvp(PRIMARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#hasSecondaryChargingCollectionFunctionName()
   */
  public boolean hasSecondaryChargingCollectionFunctionName() {
    return hasAvp(SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#hasSecondaryEventChargingFunctionName()
   */
  public boolean hasSecondaryEventChargingFunctionName() {
    return hasAvp(SECONDARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#setPrimaryChargingCollectionFunctionName(net.java.slee.resource.diameter.base.events.avp.DiameterURI)
   */
  public void setPrimaryChargingCollectionFunctionName(DiameterURI primaryChargingCollectionFunctionName) {
    addAvp(PRIMARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID, primaryChargingCollectionFunctionName.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#setPrimaryEventChargingFunctionName(net.java.slee.resource.diameter.base.events.avp.DiameterURI)
   */
  public void setPrimaryEventChargingFunctionName(DiameterURI primaryEventChargingFunctionName) {
    addAvp(PRIMARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID, primaryEventChargingFunctionName.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#setSecondaryChargingCollectionFunctionName(net.java.slee.resource.diameter.base.events.avp.DiameterURI)
   */
  public void setSecondaryChargingCollectionFunctionName(DiameterURI secondaryChargingCollectionFunctionName) {
    addAvp(SECONDARY_CHARGING_COLLECTION_FUNCTION_NAME, CXDX_VENDOR_ID, secondaryChargingCollectionFunctionName.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.ChargingInformation#setSecondaryEventChargingFunctionName(net.java.slee.resource.diameter.base.events.avp.DiameterURI)
   */
  public void setSecondaryEventChargingFunctionName(DiameterURI secondaryEventChargingFunctionName) {
    addAvp(SECONDARY_EVENT_CHARGING_FUNCTION_NAME, CXDX_VENDOR_ID, secondaryEventChargingFunctionName.toString());
  }

}
