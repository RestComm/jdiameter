package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * ApplicationServerInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:55:05 AM Apr 11, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ApplicationServerInformationImpl extends GroupedAvpImpl implements ApplicationServerInformation {

  private static final Logger logger = Logger.getLogger( ApplicationServerInformationImpl.class );

  public ApplicationServerInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#getApplicationProvidedCalledPartyAddresses()
   */
  public String[] getApplicationProvidedCalledPartyAddresses()
  {
    String[] applicationProvidedCalledPartyAddresses = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      applicationProvidedCalledPartyAddresses = new String[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          applicationProvidedCalledPartyAddresses[i] = rawAvps.getAvpByIndex(i).getUTF8String();
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.APPLICATION_PROVIDED_CALLED_PARTY_ADDRESS);
          logger.error( "Failure while trying to obtain Application-Provided-Called-Party-Address AVP (index:" + i + ").", e );
        }
      }
    }

    return applicationProvidedCalledPartyAddresses;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#getApplicationServer()
   */
  public String getApplicationServer()
  {
    if(hasApplicationServer())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.APPLICATION_SERVER, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.APPLICATION_SERVER);
        logger.error( "Failure while trying to obtain Application-Server AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#hasApplicationServer()
   */
  public boolean hasApplicationServer()
  {
    return hasAvp(DiameterRoAvpCodes.APPLICATION_SERVER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#setApplicationProvidedCalledPartyAddress(java.lang.String)
   */
  public void setApplicationProvidedCalledPartyAddress( String applicationProvidedCalledPartyAddress )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, applicationProvidedCalledPartyAddress, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#setApplicationProvidedCalledPartyAddresses(java.lang.String[])
   */
  public void setApplicationProvidedCalledPartyAddresses( String[] applicationProvidedCalledPartyAddresses )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    for(String applicationProvidedCalledPartyAddress : applicationProvidedCalledPartyAddresses)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.ADDITIONAL_TYPE_INFORMATION, applicationProvidedCalledPartyAddress, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.ApplicationServerInformation#setApplicationServer(java.lang.String)
   */
  public void setApplicationServer( String applicationServer )
  {
    if(hasApplicationServer())
    {
      throw new IllegalStateException("AVP Application-Server is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.APPLICATION_SERVER, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.DOMAIN_NAME);
      super.avpSet.addAvp(DiameterRoAvpCodes.APPLICATION_SERVER, applicationServer, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }
}
