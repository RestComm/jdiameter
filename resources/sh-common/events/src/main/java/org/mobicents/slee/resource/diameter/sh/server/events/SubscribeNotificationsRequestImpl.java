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

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
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
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;
/**
 * 
 * Start time:12:38:45 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link SubscribeNotificationsRequest} interface.
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
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

  public SendDataIndicationType getSendDataIndication()
  {
    try
    {
      return hasSendDataIndication() ? SendDataIndicationType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, 10415L).getInteger32()) : null;
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

  public SubsReqType getSubsReqType()
  {
    if(hasSubsReqType())
    {
      try
      {
        return SubsReqType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, 10415L).getInteger32());
      }
      catch (AvpDataException e) {
        logger.error( "Unable to decode Subs-Req-Type AVP contents.", e );
      }
    }
    
    return null;
  }

  public boolean hasSendDataIndication()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, 10415L) != null;
  }

  public boolean hasServerName()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME, 10415L) != null;
  }

  public boolean hasSubsReqType()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, 10415L) != null;
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

  public void setSendDataIndication(SendDataIndicationType sendDataIndication)
  {
    super.message.getAvps().addAvp(DiameterShAvpCodes.SEND_DATA_INDICATION, sendDataIndication.getValue(), 10415L, true, true);
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

  public void setSubsReqType(SubsReqType subsReqType)
  {
    super.message.getAvps().addAvp(DiameterShAvpCodes.SUBS_REQ_TYPE, subsReqType.getValue(), 10415L, true, true);
  }

  public UserIdentityAvp getUserIdentity() {
		if (hasUserIdentity()) {
			try {
				Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L);

				UserIdentityAvpImpl a = new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, new byte[] {});

				for (Avp subAvp : rawAvp.getGrouped()) {
					try {
						a.setExtensionAvps(new DiameterAvp[] { new DiameterAvpImpl(subAvp.getCode(), subAvp.getVendorId(), subAvp.isMandatory() ? 1 : 0, subAvp.isEncrypted() ? 1 : 0, subAvp.getRaw(),
								null) });
					} catch (AvpNotAllowedException e) {
						logger.error("Unable to add child AVPs to User-Identity AVP.", e);
					}
				}

				return a;
			} catch (AvpDataException e) {
				logger.error("Unable to decode User-Identity AVP contents.", e);
			}
		}

		return null;
	}

  public boolean hasUserData()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA, 10415L) != null;
  }

  public boolean hasUserIdentity()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L) != null;
  }

  public Date getExpiryTime()
  {
    try
    {
      return hasExpiryTime() ? super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME, 10415L).getTime() : null;
    }
    catch ( AvpDataException e ) {
      logger.error( "Unable to decode Expiry-Time AVP contents.", e );
    }
    
    return null;
  }

  public boolean hasExpiryTime()
  {
    return super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME) != null;
  }

  public void setExpiryTime(Date expiryTime)
  {
    addAvp(DiameterShAvpCodes.EXPIRY_TIME, expiryTime);
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    // FIXME: Alexandre: Make it use addAvp(...)
    if(hasUserIdentity())
    {
      throw new IllegalStateException("AVP User-Identity is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.USER_IDENTITY, 10415L);
      boolean mandatoryAvp = !(avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot"));
      boolean protectedAvp = avpRep.getRuleProtected().equals("must");

      // FIXME: Alexandre: Need to specify protected!
      super.setAvpAsGrouped(avpRep.getCode(), avpRep.getVendorId(), userIdentity.getExtensionAvps(), mandatoryAvp, protectedAvp);
    }
  }

}
