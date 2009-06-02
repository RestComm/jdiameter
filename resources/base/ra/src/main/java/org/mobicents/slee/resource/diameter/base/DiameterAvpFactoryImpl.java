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
package org.mobicents.slee.resource.diameter.base;

import java.net.InetAddress;
import java.util.Date;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.mobicents.slee.resource.diameter.base.events.DiameterCommandImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.FailedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ProxyInfoAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;


/**
 * 
 * <br>Super project:  mobicents
 * <br>7:52:06 PM May 13, 2008 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Erick Svenson
 */
public class DiameterAvpFactoryImpl implements DiameterAvpFactory
{
  // RFC3588 - Page 40
  // Unless otherwise noted, AVPs will have the following default AVP
  // Flags field settings:
  //   The ’M’ bit MUST be set.  The ’V’ bit MUST NOT be set.
  
  private final static long BASE_VENDOR_ID = 0L;
  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createCommand(int, int, java.lang.String, java.lang.String, boolean, boolean)
   */
  public DiameterCommand createCommand( int commandCode, int applicationId, String shortName, String longName, boolean isRequest, boolean isProxiable )
  {
    return new DiameterCommandImpl( commandCode, applicationId, shortName, longName, isRequest, isProxiable );
  }

  // Generic AVP Creators
  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public DiameterAvp createAvp( int avpCode, DiameterAvp[] avps ) throws NoSuchAvpException, AvpNotAllowedException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, avps);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, DiameterAvp[] avps ) throws NoSuchAvpException, AvpNotAllowedException
  {
    GroupedAvpImpl avp = (GroupedAvpImpl) AvpUtilities.createAvp( avpCode, vendorId, GroupedAvpImpl.class );
    
    avp.setExtensionAvps( avps );
    
    return avp;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, byte[])
   */
  public DiameterAvp createAvp( int avpCode, byte[] value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, byte[])
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, byte[] value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int)
   */
  public DiameterAvp createAvp( int avpCode, int value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, int)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, int value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, long)
   */
  public DiameterAvp createAvp( int avpCode, long value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, long)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, long value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, float)
   */
  public DiameterAvp createAvp( int avpCode, float value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, float)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, float value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, double)
   */
  public DiameterAvp createAvp( int avpCode, double value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, double)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, double value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, java.net.InetAddress)
   */
  public DiameterAvp createAvp( int avpCode, InetAddress value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, java.net.InetAddress)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, InetAddress value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, java.util.Date)
   */
  public DiameterAvp createAvp( int avpCode, Date value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, java.util.Date)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, Date value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, java.lang.String)
   */
  public DiameterAvp createAvp( int avpCode, String value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, java.lang.String)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, String value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, net.java.slee.resource.diameter.base.events.avp.Enumerated)
   */
  public DiameterAvp createAvp( int avpCode, Enumerated value ) throws NoSuchAvpException
  {
    return createAvp((int)BASE_VENDOR_ID, avpCode, value);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createAvp(int, int, net.java.slee.resource.diameter.base.events.avp.Enumerated)
   */
  public DiameterAvp createAvp( int vendorId, int avpCode, Enumerated value ) throws NoSuchAvpException
  {
    return AvpUtilities.createAvp(avpCode, vendorId, value);
  }
  
  // Custom Grouped AVP Creators
  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createExperimentalResult(long, long)
   */
  public ExperimentalResultAvp createExperimentalResult( long vendorId, long experimentalResultCode )
  {
    ExperimentalResultAvp experimentalResultAvp = createExperimentalResult();
    
    experimentalResultAvp.setVendorIdAVP(vendorId);
    experimentalResultAvp.setExperimentalResultCode(experimentalResultCode);
    
    return experimentalResultAvp;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createExperimentalResult()
   */
  public ExperimentalResultAvp createExperimentalResult()
  {
    return createExperimentalResult( new DiameterAvp[0] );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createExperimentalResult(net.java.slee.resource.diameter.base.events.avp.DiameterAvp)
   */
  public ExperimentalResultAvp createExperimentalResult( DiameterAvp avp ) throws AvpNotAllowedException
  {
    return createExperimentalResult( new DiameterAvp[]{ avp } );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createExperimentalResult(net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public ExperimentalResultAvp createExperimentalResult( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    return (ExperimentalResultAvp) AvpUtilities.createAvp( DiameterAvpCodes.EXPERIMENTAL_RESULT, avps, ExperimentalResultAvpImpl.class );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createFailedAvp()
   */
  public FailedAvp createFailedAvp()
  {
    return createFailedAvp( new DiameterAvp[0] );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createFailedAvp(net.java.slee.resource.diameter.base.events.avp.DiameterAvp)
   */
  public FailedAvp createFailedAvp( DiameterAvp avp )
  {
    return createFailedAvp( new DiameterAvp[]{ avp } );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createFailedAvp(net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public FailedAvp createFailedAvp( DiameterAvp[] avps )
  {
    return (FailedAvp) AvpUtilities.createAvp( DiameterAvpCodes.FAILED_AVP, avps, FailedAvpImpl.class );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createProxyInfo(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity, byte[])
   */
  public ProxyInfoAvp createProxyInfo( DiameterIdentity proxyHost, byte[] proxyState )
  {
    ProxyInfoAvp proxyInfo = createProxyInfo();
    
    proxyInfo.setProxyHost( proxyHost );
    proxyInfo.setProxyState( proxyState );
    
    return proxyInfo;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createProxyInfo()
   */
  public ProxyInfoAvp createProxyInfo()
  {
    return createProxyInfo(new DiameterAvp[0]);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createProxyInfo(net.java.slee.resource.diameter.base.events.avp.DiameterAvp)
   */
  public ProxyInfoAvp createProxyInfo( DiameterAvp avp )
  {
    return createProxyInfo( new DiameterAvp[]{avp} );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createProxyInfo(net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public ProxyInfoAvp createProxyInfo( DiameterAvp[] avps )
  {
    return (ProxyInfoAvp) AvpUtilities.createAvp( DiameterAvpCodes.PROXY_INFO, avps, ProxyInfoAvpImpl.class );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createVendorSpecificApplicationId(long)
   */
  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( long vendorId )
  {
    VendorSpecificApplicationIdAvp vsaidAvp = createVendorSpecificApplicationId();
    
    vsaidAvp.setVendorIdAvp(vendorId);
    
    return vsaidAvp;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createVendorSpecificApplicationId()
   */
  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId()
  {
    return createVendorSpecificApplicationId( new DiameterAvp[0] );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createVendorSpecificApplicationId(net.java.slee.resource.diameter.base.events.avp.DiameterAvp)
   */
  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( DiameterAvp avp ) throws AvpNotAllowedException
  {
    return createVendorSpecificApplicationId( new DiameterAvp[]{avp} );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.base.DiameterAvpFactory#createVendorSpecificApplicationId(net.java.slee.resource.diameter.base.events.avp.DiameterAvp[])
   */
  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    return (VendorSpecificApplicationIdAvp) AvpUtilities.createAvp( DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID, avps, VendorSpecificApplicationIdAvpImpl.class );
  }

}
