package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.CurrentLocationType;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.IdentitySetType;
import net.java.slee.resource.diameter.sh.client.events.avp.RequestedDomainType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class UserDataRequestImpl extends DiameterShMessageImpl implements UserDataRequest {

  private static transient Logger logger = Logger.getLogger(UserDataRequestImpl.class);

  public UserDataRequestImpl(Message msg)
  {
    super(msg);

    msg.setRequest(true);

    super.longMessageName = "User-Data-Request";
    super.shortMessageName = "UDR";
  }

  public CurrentLocationType getCurrentLocation()
  {
    if (hasCurrentLocation())
    {
      try
      {
        return CurrentLocationType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.CURRENT_LOCATION).getInteger32());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode Current-Location AVP contents.", e );
      }
    }

    return null;
  }

  public IdentitySetType getIdentitySet()
  {
    if (hasIdentitySet())
    {
      try
      {
        return IdentitySetType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.IDENTITY_SET).getInteger32());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode Identity-Set AVP contents.", e );
      }
    }

    return null;
  }

  public RequestedDomainType getRequestedDomain()
  {
    if (hasRequestedDomain())
    {
      try
      {
        return RequestedDomainType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN).getInteger32());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode Requested-Domain AVP contents.", e );
      }
    }

    return null;
  }

  public boolean hasCurrentLocation()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.CURRENT_LOCATION) != null;
  }

  public boolean hasIdentitySet()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.IDENTITY_SET) != null;
  }

  public boolean hasRequestedDomain()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN) != null;
  }

  public void setCurrentLocation(CurrentLocationType currentLocation)
  {

    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.CURRENT_LOCATION);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

    super.setAvpAsInt32(DiameterShAvpCodes.CURRENT_LOCATION, currentLocation.getValue(), mandatoryAvp == 1, true);
  }

  public void setIdentitySet(IdentitySetType identitySet)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.IDENTITY_SET);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

    super.setAvpAsInt32(DiameterShAvpCodes.IDENTITY_SET, identitySet.getValue(), mandatoryAvp == 1, true);
  }

  public void setRequestedDomain(RequestedDomainType requestedDomain)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN);

    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

    super.setAvpAsInt32(DiameterShAvpCodes.REQUESTED_DOMAIN, requestedDomain.getValue(), mandatoryAvp == 1, true);
  }

  public DataReferenceType[] getDataReferences()
  {
    AvpSet set = super.message.getAvps().getAvps(DiameterShAvpCodes.DATA_REFERENCE, 10415L);
    if(set == null )
    {
      return null;
    }
    else
    {
      DataReferenceType[] returnValue = new DataReferenceType[set.size()];
      int counter = 0;

      for(Avp raw:set)
      {
        try
        {
          returnValue[counter++] = DataReferenceType.fromInt(raw.getInteger32());
        }
        catch (AvpDataException e) {
          logger.error( "Unable to decode Experimental-Result AVP contents.", e );

          return null;
        }
      }

      return returnValue;
    }
  }

  public String getServerName()
  {
    try
    {
      return hasServerName() ? super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME, 10415L).getUTF8String() : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode Server-Name AVP contents.", e );
    }

    return null;
  }

  public byte[][] getServiceIndications()
  {
    AvpSet set = super.message.getAvps().getAvps(DiameterShAvpCodes.SERVICE_INDICATION, 10415L);

    if(set == null)
    {
      return null;
    }
    else
    {
      byte[][] returnValue = new byte[set.size()][];
      int counter = 0;

      for(Avp raw:set)
      {
        try
        {
          returnValue[counter++] = raw.getRaw();
        }
        catch (AvpDataException e) {
          logger.error( "Unable to decode Service-Indications AVP contents.", e );

          return null;
        }
      }

      return returnValue;
    }
  }

  public UserIdentityAvp getUserIdentity()
  {
    if (!hasUserIdentity())
    {
      return null;
    }

    try
    {
      Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);

      return new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
    }
    catch (AvpDataException e) {
      logger.error( "Unable to decode User-Identity AVP contents.", e );
    }

    return null;
  }

  public boolean hasServerName()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME, 10415L) != null;
  }

  public boolean hasUserIdentity()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L) != null;
  }

  public void setDataReference(DataReferenceType dataReference)
  {
    super.message.getAvps().addAvp(DiameterShAvpCodes.DATA_REFERENCE, dataReference.getValue(), 10415L, true, true);
  }

  public void setDataReferences(DataReferenceType[] dataReferences)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.DATA_REFERENCE);

    for (DataReferenceType drt : dataReferences)
    {
      super.message.getAvps().addAvp(DiameterShAvpCodes.DATA_REFERENCE, drt.getValue(), 10415L, true, false);
    }
  }

  public void setServerName(String serverName)
  {
    // FIXME: alexandre: is this OctetString?
    super.message.getAvps().addAvp(DiameterShAvpCodes.SERVER_NAME, serverName, 10415L, true, true, true);
  }

  public void setServiceIndication(byte[] serviceIndication)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.SERVICE_INDICATION);
    super.message.getAvps().addAvp(DiameterShAvpCodes.SERVICE_INDICATION, serviceIndication, 10415L, true, false);
  }

  public void setServiceIndications(byte[][] serviceIndications)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.SERVICE_INDICATION);

    for (byte[] b : serviceIndications)
    {
      super.message.getAvps().addAvp(DiameterShAvpCodes.SERVICE_INDICATION, b, 10415L, true, false);
    }
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
  }

}
