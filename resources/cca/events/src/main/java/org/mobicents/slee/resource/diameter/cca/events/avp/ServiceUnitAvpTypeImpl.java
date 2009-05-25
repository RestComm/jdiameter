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

import java.util.Date;

import net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:14:32:32 2009-05-23<br>
 * Project: diameter-parent<br>
 * Super class for avps of structure similar to:
 * <pre>
 *           HDR NAME   ::= < AVP Header: 431 >
 *                          [ Tariff-Time-Change ]
 *                          [ CC-Time ]
 *                          [ CC-Money ]
 *                          [ CC-Total-Octets ]
 *                          [ CC-Input-Octets ]
 *                          [ CC-Output-Octets ]
 *                          [ CC-Service-Specific-Units ]
 *                         *[ AVP ]
 *
 * </pre>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see RequestedServiceUnitAvpImpl
 * @see GrantedServiceUnitAvpImpl
 * @see UsedServiceUnitAvpImpl
 */
public class ServiceUnitAvpTypeImpl extends GroupedAvpImpl {

	private static final Logger logger =Logger.getLogger(ServiceUnitAvpTypeImpl.class);
	/**
	 * 	
	 * @param code
	 * @param vendorId
	 * @param mnd
	 * @param prt
	 * @param value
	 */
	public ServiceUnitAvpTypeImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlInputOctets()
	   */
	  public long getCreditControlInputOctets()
	  {
	    if(hasCreditControlInputOctets())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Input_Octets);
	      try
	      {
	        return rawAvp.getUnsigned64();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Input_Octets);
	        logger.error( "Failure while trying to obtain CC-Input-Octets AVP.", e );
	      }
	    }

	    return -1;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlMoneyAvp()
	   */
	  public CcMoneyAvp getCreditControlMoneyAvp()
	  {
	    if(hasCreditControlMoneyAvp())
	    {
	      Avp rawGroup = super.avpSet.getAvp(CreditControlAVPCodes.CC_Money);
	      try
	      {
	        return new CcMoneyAvpImpl(CreditControlAVPCodes.CC_Money, rawGroup.getVendorId(), rawGroup.isMandatory() ? 1 : 0, rawGroup.isEncrypted() ? 1 : 0, rawGroup.getRaw());
	      }
	      catch (Exception e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Money);
	        logger.error( "Failure while trying to obtain CC-Money AVP.", e );
	      } 
	    }

	    return null;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlOutputOctets()
	   */
	  public long getCreditControlOutputOctets()
	  {
	    if(hasCreditControlOutputOctets())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Output_Octets);
	      try
	      {
	        return rawAvp.getUnsigned64();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Output_Octets);
	        logger.error( "Failure while trying to obtain CC-Output-Octets AVP.", e );
	      }
	    }

	    return -1;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlServiceSpecificUnits()
	   */
	  public long getCreditControlServiceSpecificUnits()
	  {
	    if(hasCreditControlServiceSpecificUnits())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
	      try
	      {
	        return rawAvp.getUnsigned64();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Service_Specific_Units);
	        logger.error( "Failure while trying to obtain CC-Service-Specific-Units-Octets AVP.", e );
	      }
	    }

	    return -1;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTime()
	   */
	  public long getCreditControlTime()
	  {
	    if(hasCreditControlTime())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Time);
	      try
	      {
	        return rawAvp.getUnsigned32();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Time);
	        logger.error( "Failure while trying to obtain CC-Time AVP.", e );
	      }
	    }

	    return -1;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getCreditControlTotalOctets()
	   */
	  public long getCreditControlTotalOctets()
	  {
	    if(hasCreditControlTotalOctets())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.CC_Total_Octets);
	      try
	      {
	        return rawAvp.getUnsigned64();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.CC_Total_Octets);
	        logger.error( "Failure while trying to obtain CC-Total-Octets AVP.", e );
	      }
	    }

	    return -1;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#getTariffTimeChange()
	   */
	  public Date getTariffTimeChange()
	  {
	    if(hasTariffTimeChange())
	    {
	      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Tariff_Time_Change);
	      try
	      {
	        return rawAvp.getTime();
	      }
	      catch (AvpDataException e) {
	        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Tariff_Time_Change);
	        logger.error( "Failure while trying to obtain Tariff-Time-Change AVP.", e );
	      }
	    }

	    return null;
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlInputOctets()
	   */
	  public boolean hasCreditControlInputOctets()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Input_Octets);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlMoneyAvp()
	   */
	  public boolean hasCreditControlMoneyAvp()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Money);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlOutputOctets()
	   */
	  public boolean hasCreditControlOutputOctets()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Output_Octets);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlServiceSpecificUnits()
	   */
	  public boolean hasCreditControlServiceSpecificUnits()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Service_Specific_Units);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTime()
	   */
	  public boolean hasCreditControlTime()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Time);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasCreditControlTotalOctets()
	   */
	  public boolean hasCreditControlTotalOctets()
	  {
	    return super.hasAvp(CreditControlAVPCodes.CC_Total_Octets);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#hasTariffTimeChange()
	   */
	  public boolean hasTariffTimeChange()
	  {
	    return super.hasAvp(CreditControlAVPCodes.Tariff_Time_Change);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlInputOctets(long)
	   */
	  public void setCreditControlInputOctets(long ttc)
	  {
	    addAvp(CreditControlAVPCodes.CC_Input_Octets, ttc);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlMoneyAvp(net.java.slee.resource.diameter.cca.events.avp.CcMoneyAvp)
	   */
	  public void setCreditControlMoneyAvp(CcMoneyAvp ccm)
	  {
	    if(hasAvp(CreditControlAVPCodes.CC_Money))
	    {
	      throw new IllegalStateException("AVP CC-Money is already present in message and cannot be overwritten.");
	    }

	    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCodes.CC_Money);
	    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
	    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

	    //super.avpSet.removeAvp(CreditControlAVPCodes.CC_Money);
	    super.avpSet.addAvp(CreditControlAVPCodes.CC_Money, ccm.byteArrayValue(), mandatoryAvp == 1, protectedAvp == 1);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlOutputOctets(long)
	   */
	  public void setCreditControlOutputOctets(long ccoo)
	  {
	    addAvp(CreditControlAVPCodes.CC_Output_Octets, ccoo);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlServiceSpecificUnits(long)
	   */
	  public void setCreditControlServiceSpecificUnits(long ccssu)
	  {
	    addAvp(CreditControlAVPCodes.CC_Service_Specific_Units, ccssu);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTime(long)
	   */
	  public void setCreditControlTime(long cct)
	  {
	    addAvp(CreditControlAVPCodes.CC_Time, cct);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setCreditControlTotalOctets(long)
	   */
	  public void setCreditControlTotalOctets(long ccto)
	  {
	    addAvp(CreditControlAVPCodes.CC_Total_Octets, ccto);
	  }

	  /* (non-Javadoc)
	   * @see net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp#setTariffTimeChange(java.util.Date)
	   */
	  public void setTariffTimeChange(Date ttc)
	  {
	    addAvp(CreditControlAVPCodes.Tariff_Time_Change	, ttc);
	  }
}
