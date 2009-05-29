/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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

import java.util.Date;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SendDataIndicationType;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
/**
 * 
 * Start time:12:38:45 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link SubscribeNotificationsRequest} interface.
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class SubscribeNotificationsRequestImpl extends DiameterShMessageImpl implements SubscribeNotificationsRequest {

  private static transient Logger logger = Logger.getLogger(SubscribeNotificationsRequestImpl.class);

  public SubscribeNotificationsRequestImpl(Message msg)
  {
    super(msg);

    msg.setRequest(true);

    super.longMessageName = "Subscribe-Notification-Request";
    super.shortMessageName = "SNR";
  }

  public DataReferenceType[] getDataReferences()
  {
    AvpSet set = super.message.getAvps().getAvps(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID);
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

  public SendDataIndicationType getSendDataIndication()
  {
    try
    {
      return hasSendDataIndication() ? SendDataIndicationType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID).getInteger32()) : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode Send-Data-Indication AVP contents.", e );
    }
    
    return null;
  }

  public String getServerName()
  {
    try
    {
      return hasServerName() ? super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME, DiameterShAvpCodes.SH_VENDOR_ID).getUTF8String() : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode Server-Name AVP contents.", e );
    }
    
    return null;
  }

  public byte[][] getServiceIndications()
  {
    AvpSet set = super.message.getAvps().getAvps(DiameterShAvpCodes.SERVICE_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID);
    
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

  public SubsReqType getSubsReqType()
  {
    if(hasSubsReqType())
    {
      try
      {
        return SubsReqType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, DiameterShAvpCodes.SH_VENDOR_ID).getInteger32());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode Subs-Req-Type AVP contents.", e );
      }
    }
    
    return null;
  }

  public boolean hasSendDataIndication()
  {
    return hasAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasServerName()
  {
    return hasAvp(DiameterShAvpCodes.SERVER_NAME, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasSubsReqType()
  {
    return hasAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public void setDataReference(DataReferenceType dataReference)
  {
    addAvp(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID, (long)dataReference.getValue());
  }

  public void setDataReferences(DataReferenceType[] dataReferences)
  {
    super.message.getAvps().removeAvp(DiameterShAvpCodes.DATA_REFERENCE);
    
    for (DataReferenceType drt : dataReferences)
    {
      super.message.getAvps().addAvp(DiameterShAvpCodes.DATA_REFERENCE, drt.getValue(), DiameterShAvpCodes.SH_VENDOR_ID, true, false);
    }
  }

  public void setSendDataIndication(SendDataIndicationType sendDataIndication)
  {
    addAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, DiameterShAvpCodes.SH_VENDOR_ID, sendDataIndication.getValue());
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
    super.message.getAvps().removeAvp(DiameterShAvpCodes.SERVICE_INDICATION);
    
    for (byte[] b : serviceIndications)
    {
      super.message.getAvps().addAvp(DiameterShAvpCodes.SERVICE_INDICATION, b, DiameterShAvpCodes.SH_VENDOR_ID, true, false);
    }
  }

  public void setSubsReqType(SubsReqType subsReqType)
  {
    addAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, DiameterShAvpCodes.SH_VENDOR_ID, (long)subsReqType.getValue());
  }

  public UserIdentityAvp getUserIdentity() {
    return (UserIdentityAvp) getAvpAsCustom(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, UserIdentityAvpImpl.class);
	}

  public boolean hasUserData()
  {
    return hasAvp(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasUserIdentity()
  {
    return hasAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public Date getExpiryTime()
  {
    return getAvpAsTime(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public boolean hasExpiryTime()
  {
    return hasAvp(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public void setExpiryTime(Date expiryTime)
  {
    addAvp(DiameterShAvpCodes.EXPIRY_TIME, DiameterShAvpCodes.SH_VENDOR_ID, expiryTime);
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    addAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, userIdentity.byteArrayValue() );
  }

}
