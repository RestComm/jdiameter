package org.mobicents.slee.resource.diameter.sh.client.events.avp;

import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * SupportedFeaturesAvpImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:34:33 PM Dec 18, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class SupportedFeaturesAvpImpl extends GroupedAvpImpl implements SupportedFeaturesAvp {

  public SupportedFeaturesAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value)
  {
    super(code, vendorId, mnd, prt, value);
  }

  public long getFeatureList()
  {
    return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST);
  }

  public long getFeatureListId()
  {
    return super.getAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID);
  }

  public boolean hasFeatureList()
  {
    return super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST) != null;
  }

  public boolean hasFeatureListId()
  {
    return super.avpSet.getAvp(DiameterShAvpCodes.FEATURE_LIST_ID) != null;
  }

  public void setFeatureList(long featureList)
  {
    super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST, featureList, true);
  }

  public void setFeatureListId(long featureListId)
  {
    super.setAvpAsUInt32(DiameterShAvpCodes.FEATURE_LIST_ID, featureListId, true);
  }

  public boolean hasVendorId()
  {
    return super.avpSet.getAvp(Avp.VENDOR_ID) != null;
  }

  public void setVendorId(long vendorId)
  {
    if (hasVendorId())
    {
      throw new IllegalStateException("Unable to set Vendor-Id AVP, it is already present in this message.");
    }

    super.setAvpAsUInt32(Avp.VENDOR_ID, vendorId, true);
  }
}
