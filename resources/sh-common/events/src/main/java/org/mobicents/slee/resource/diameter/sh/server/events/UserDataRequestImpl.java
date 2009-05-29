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
package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.CurrentLocationType;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.IdentitySetType;
import net.java.slee.resource.diameter.sh.client.events.avp.RequestedDomainType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 * 
 * Implementation of {@link UserDataRequest} interface.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class UserDataRequestImpl extends DiameterShMessageImpl implements UserDataRequest {

  public UserDataRequestImpl(Message msg)
  {
    super(msg);

    msg.setRequest(true);

    super.longMessageName = "User-Data-Request";
    super.shortMessageName = "UDR";
  }

  public CurrentLocationType getCurrentLocation()
  {
    return (CurrentLocationType) getAvpAsEnumerated(DiameterShAvpCodes.CURRENT_LOCATION, DiameterShAvpCodes.SH_VENDOR_ID, CurrentLocationType.class);
  }

  public DataReferenceType[] getDataReferences()
  {
    return (DataReferenceType[]) getAvpsAsEnumerated(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID, DataReferenceType.class);
  }

  public IdentitySetType getIdentitySet()
  {
    return IdentitySetType.fromInt(getAvpAsInteger32(DiameterShAvpCodes.IDENTITY_SET, DiameterShAvpCodes.SH_VENDOR_ID));    
  }

  public RequestedDomainType getRequestedDomain()
  {
    return RequestedDomainType.fromInt(getAvpAsInteger32(DiameterShAvpCodes.REQUESTED_DOMAIN, DiameterShAvpCodes.SH_VENDOR_ID));    
  }

  public String getServerName()
  {
    return getAvpAsUTF8String(DiameterShAvpCodes.SERVER_NAME, DiameterShAvpCodes.SH_VENDOR_ID);    
  }

  public byte[][] getServiceIndications()
  {
    return getAvpsAsRaw(DiameterShAvpCodes.SERVICE_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public UserIdentityAvp getUserIdentity()
  {
    return (UserIdentityAvp) getAvpAsCustom(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, UserIdentityAvpImpl.class);
  }

  public boolean hasCurrentLocation()
  {
    return hasAvp(DiameterShAvpCodes.CURRENT_LOCATION, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasIdentitySet()
  {
    return hasAvp(DiameterShAvpCodes.IDENTITY_SET, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasRequestedDomain()
  {
    return hasAvp(DiameterShAvpCodes.REQUESTED_DOMAIN, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasServerName()
  {
    return hasAvp(DiameterShAvpCodes.SERVER_NAME, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasUserIdentity()
  {
    return hasAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public void setCurrentLocation(CurrentLocationType currentLocation)
  {
    addAvp(DiameterShAvpCodes.CURRENT_LOCATION, DiameterShAvpCodes.SH_VENDOR_ID, (long)currentLocation.getValue());
  }

  public void setDataReference(DataReferenceType dataReference)
  {
    addAvp(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID, (long)dataReference.getValue());
  }

  public void setDataReferences(DataReferenceType[] dataReferences)
  {
    for(DataReferenceType dataReference : dataReferences) {
      setDataReference(dataReference);
    }
  }

  public void setIdentitySet(IdentitySetType identitySet)
  {
    addAvp(DiameterShAvpCodes.IDENTITY_SET, DiameterShAvpCodes.SH_VENDOR_ID, (long)identitySet.getValue());
  }

  public void setRequestedDomain(RequestedDomainType requestedDomain)
  {
    addAvp(DiameterShAvpCodes.REQUESTED_DOMAIN, DiameterShAvpCodes.SH_VENDOR_ID, (long)requestedDomain.getValue());
  }

  public void setServerName(String serverName)
  {
    addAvp(DiameterShAvpCodes.SERVER_NAME, DiameterShAvpCodes.SH_VENDOR_ID, serverName);
  }

  public void setServiceIndication(byte[] serviceIndication)
  {
    addAvp(DiameterShAvpCodes.SERVICE_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID, serviceIndication);
  }

  public void setServiceIndications(byte[][] serviceIndications)
  {
    for(byte[] serviceIndication : serviceIndications) {
      setServiceIndication(serviceIndication);
    }
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    addAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, userIdentity.byteArrayValue());
  }

}
