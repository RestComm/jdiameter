package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;

public class ExperimentalResultAvpImpl extends GroupedAvpImpl implements ExperimentalResultAvp {

  private static transient Logger logger = Logger.getLogger(ExperimentalResultAvpImpl.class);

	public ExperimentalResultAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value)
	{
		super(code, vendorId, mnd, prt, value);
	}

	public long getExperimentalResultCode()
	{
		if (!hasExperimentalResultCode())
		{
			return -1;
		}
		else
		{
			try {
				Avp rawAvp = super.avpSet.getAvp(Avp.EXPERIMENTAL_RESULT_CODE);
				return rawAvp.getUnsigned32();
			}
			catch (AvpDataException e) {
			  logger.error( "", e );			
				return -1;
			}
		}
	}
	
	public long getVendorId()
	{
    if (!hasVendorId())
    {
      return -1;
    }
    else
    {
      try {
        Avp rawAvp = super.avpSet.getAvp(Avp.VENDOR_ID);
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        logger.error( "", e );      
        return -1;
      }
    }
  }

	public boolean hasExperimentalResultCode()
	{
		return super.avpSet.getAvp(Avp.EXPERIMENTAL_RESULT_CODE) != null;
	}

	public boolean hasVendorId()
	{
		return super.avpSet.getAvp(Avp.VENDOR_ID) != null;
	}

	public void setExperimentalResultCode(long experimentalResultCode)
	{
		if(hasExperimentalResultCode())
		{
			throw new IllegalStateException("Cannot set Experimental-Result-Code AVP again.");
		}
		
		super.setAvpAsUInt32(Avp.EXPERIMENTAL_RESULT_CODE, experimentalResultCode,true);
	}

	public void setVendorId(long vendorId)
	{
		if(hasVendorId())
		{
			throw new IllegalStateException("Cannot set Vendor-Id AVP again.");
		}
		
		super.setAvpAsUInt32(Avp.VENDOR_ID, vendorId,true);
	}

}
