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
package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>3:09:25 PM May 25, 2009 
 * <br>
 *
 * Implementation of {@link ExperimentalResultAvp}
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ExperimentalResultAvpImpl extends GroupedAvpImpl implements ExperimentalResultAvp {

  private static transient Logger logger = Logger.getLogger(ExperimentalResultAvpImpl.class);

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
	public ExperimentalResultAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value)
	{
		super(code, vendorId, mnd, prt, value);
	}

	/*
	 * (non-Javadoc)
	 * @see net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp#getExperimentalResultCode()
	 */
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
	
	/*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp#hasExperimentalResultCode()
   */
  public boolean hasExperimentalResultCode()
  {
  	return super.avpSet.getAvp(Avp.EXPERIMENTAL_RESULT_CODE) != null;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp#setExperimentalResultCode(long)
   */
  public void setExperimentalResultCode(long experimentalResultCode)
  {
    addAvp(Avp.EXPERIMENTAL_RESULT_CODE, experimentalResultCode);
  }

  /*
	 * (non-Javadoc)
	 * @see org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl#getVendorId()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp#hasVendorId()
	 */
	public boolean hasVendorId()
	{
		return super.avpSet.getAvp(Avp.VENDOR_ID) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp#setVendorId(long)
	 */
	public void setVendorId(long vendorId)
	{
	  addAvp(Avp.VENDOR_ID, vendorId,true);
	}

}
