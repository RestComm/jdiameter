package org.mobicents.slee.resource.diameter.base;

import java.net.InetAddress;
import java.util.Date;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.NoSuchAvpException;
import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.Enumerated;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.apache.log4j.Logger;
import org.jdiameter.client.api.parser.DecodeException;
import org.jdiameter.client.impl.parser.MessageParser;
import org.mobicents.slee.resource.diameter.base.events.DiameterCommandImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ExperimentalResultAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.FailedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ProxyInfoAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;


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
  
  private static transient Logger logger = Logger.getLogger(DiameterAvpFactoryImpl.class);

  private final long DEFAULT_VENDOR_ID = 0L;
  
  protected MessageParser parser = new MessageParser(null);
  
  private DiameterAvpType getAvpType(int code, long vendorID) throws NoSuchAvpException
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(code, vendorID);
    
    if(avpRep != null)
      return DiameterAvpType.fromString(avpRep.getType());
    
    return null;
  }
  
  public DiameterAvp createAvp( int avpCode, DiameterAvp[] avps ) throws NoSuchAvpException, AvpNotAllowedException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, avps);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, DiameterAvp[] avps ) throws NoSuchAvpException, AvpNotAllowedException
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorID);
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    GroupedAvpImpl avp = new GroupedAvpImpl(avpCode, vendorID, mandatoryAvp, protectedAvp, new byte[]{});
    
    avp.setExtensionAvps( avps );
    
    return avp;
  }

  public DiameterAvp createAvp( int avpCode, byte[] value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, byte[] value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, value);
  }

  public DiameterAvp createAvp( int avpCode, int value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, int value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, parser.int32ToBytes(value));    
  }

  public DiameterAvp createAvp( int avpCode, long value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, long value ) throws NoSuchAvpException
  {
    DiameterAvpType avpType = getAvpType( avpCode, vendorID );
    
    byte[] byteValue = null;
    
    if( avpType.getType() ==  DiameterAvpType._INTEGER_64  || avpType.getType() ==  DiameterAvpType._UNSIGNED_64 )
      byteValue = parser.int64ToBytes(value);
    else if ( avpType.getType() ==  DiameterAvpType._UNSIGNED_32 )
      byteValue = parser.intU32ToBytes(value);
    else
      throw new NoSuchAvpException("Unrecongnized type");
    
    return createAvpInternal(vendorID, avpCode, byteValue);    
  }

  public DiameterAvp createAvp( int avpCode, float value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, float value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, parser.float32ToBytes(value));
  }

  public DiameterAvp createAvp( int avpCode, double value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, double value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, parser.float64ToBytes(value));
  }

  public DiameterAvp createAvp( int avpCode, InetAddress value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, InetAddress value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, parser.addressToBytes(value));
  }

  public DiameterAvp createAvp( int avpCode, Date value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, Date value ) throws NoSuchAvpException
  {
    return createAvpInternal(vendorID, avpCode, parser.dateToBytes(value));
  }

  public DiameterAvp createAvp( int avpCode, String value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, String value ) throws NoSuchAvpException
  {
    DiameterAvpType avpType = getAvpType( avpCode, vendorID );
    
    byte[] byteValue = null;
      
    try
    {
      if( avpType.getType() ==  DiameterAvpType._OCTET_STRING )
        byteValue = parser.octetStringToBytes(value);
      else if ( avpType.getType() ==  DiameterAvpType._UTF8_STRING )
        byteValue = parser.utf8StringToBytes(value);
      else
        throw new NoSuchAvpException("Unrecongnized type for AVP code " + avpCode);
    }
    catch (Exception e) {
      logger.error( "Failed to create AVP.", e );
      return null;
    }
    
    return createAvpInternal(vendorID, avpCode, byteValue);    
  }

  public DiameterAvp createAvp( int avpCode, Enumerated value ) throws NoSuchAvpException
  {
    return createAvp((int)DEFAULT_VENDOR_ID, avpCode, value);
  }

  public DiameterAvp createAvp( int vendorID, int avpCode, Enumerated value ) throws NoSuchAvpException
  {
    try
    {
      return createAvpInternal(vendorID, avpCode, parser.objectToBytes(value));
    }
    catch ( DecodeException e )
    {
      logger.error("Failed to create AVP.", e);
      return null;
    }
  }

  public DiameterCommand createCommand( int commandCode, int applicationId, String shortName, String longName, boolean isRequest, boolean isProxiable )
  {
    return new DiameterCommandImpl( commandCode, applicationId, shortName, longName, isRequest, isProxiable );
  }

  public ExperimentalResultAvp createExperimentalResult( long vendorId, long experimentalResultCode )
  {
    try
    {
      DiameterAvp resultCodeAvp = createAvp((int)vendorId, DiameterAvpCodes.EXPERIMENTAL_RESULT_CODE, experimentalResultCode );
      return createExperimentalResult(resultCodeAvp);
    }
    catch ( Exception e )
    {
      logger.error( "Failed to create Experimental-Result AVP.", e );
      
      return null;
    }
  }

  public ExperimentalResultAvp createExperimentalResult()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.EXPERIMENTAL_RESULT);
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    return new ExperimentalResultAvpImpl(avpRep.getCode(), DEFAULT_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});
  }

  public ExperimentalResultAvp createExperimentalResult( DiameterAvp avp ) throws AvpNotAllowedException
  {
    return createExperimentalResult( new DiameterAvp[]{ avp } );
  }

  public ExperimentalResultAvp createExperimentalResult( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    ExperimentalResultAvp expResultAvp = createExperimentalResult();
    
    try
    {
      expResultAvp.setExtensionAvps( avps );
    }
    catch ( AvpNotAllowedException e )
    {
      logger.error( "Failed to create Failed-AVP.", e );
      return null;
    }
    
    return expResultAvp;
  }

  public FailedAvp createFailedAvp()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.FAILED_AVP);
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    return new FailedAvpImpl(avpRep.getCode(), DEFAULT_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});
  }

  public FailedAvp createFailedAvp( DiameterAvp avp )
  {
    return createFailedAvp( new DiameterAvp[]{ avp } );
  }

  public FailedAvp createFailedAvp( DiameterAvp[] avps )
  {
    FailedAvp fAvp = createFailedAvp();
    
    try
    {
      fAvp.setExtensionAvps( avps );
    }
    catch ( AvpNotAllowedException e )
    {
      logger.error( "Failed to create Failed-AVP.", e );
      return null;
    }
    
    return fAvp;
  }

  public ProxyInfoAvp createProxyInfo( DiameterIdentity proxyHost, byte[] proxyState )
  {
    ProxyInfoAvp proxyInfo = createProxyInfo();
    
    proxyInfo.setProxyHost( proxyHost );
    proxyInfo.setProxyState( proxyState );
    
    return proxyInfo;
  }

  public ProxyInfoAvp createProxyInfo()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.PROXY_INFO);
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    return new ProxyInfoAvpImpl(DiameterAvpCodes.PROXY_INFO, DEFAULT_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});
  }

  public ProxyInfoAvp createProxyInfo( DiameterAvp avp )
  {
    return createProxyInfo( new DiameterAvp[]{avp} );
  }

  public ProxyInfoAvp createProxyInfo( DiameterAvp[] avps )
  {
    ProxyInfoAvp proxyInfo = createProxyInfo();
    
    try
    {
      proxyInfo.setExtensionAvps( avps );
    }
    catch ( AvpNotAllowedException e )
    {
      logger.error( "Failed to create Proxy-Info AVP.", e );
      
      return null;
    }
    
    return proxyInfo;
  }

  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( long vendorId )
  {
    VendorSpecificApplicationIdAvp vsaidAvp = createVendorSpecificApplicationId();
    
    vsaidAvp.setVendorId(vendorId);
    
    return vsaidAvp;
  }

  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId()
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.VENDOR_SPECIFIC_APPLICATION_ID);
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    return new VendorSpecificApplicationIdAvpImpl(avpRep.getCode(), DEFAULT_VENDOR_ID, mandatoryAvp, protectedAvp, new byte[]{});
  }

  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( DiameterAvp avp ) throws AvpNotAllowedException
  {
    return createVendorSpecificApplicationId( new DiameterAvp[]{avp} );
  }

  public VendorSpecificApplicationIdAvp createVendorSpecificApplicationId( DiameterAvp[] avps ) throws AvpNotAllowedException
  {
    VendorSpecificApplicationIdAvp vsaidAvp = createVendorSpecificApplicationId();
    
    vsaidAvp.setExtensionAvps( avps );
    
    return vsaidAvp;
  }

  private DiameterAvp createAvpInternal(long vendorID, int avpCode, byte[] value)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp( avpCode, vendorID );
    
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
    
    return new DiameterAvpImpl( avpCode, vendorID, mandatoryAvp, protectedAvp, value, DiameterAvpType.fromString(avpRep.getType()) );    
  }
}
