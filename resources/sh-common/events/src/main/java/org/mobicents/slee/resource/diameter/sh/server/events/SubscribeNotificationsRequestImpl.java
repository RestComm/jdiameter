package org.mobicents.slee.resource.diameter.sh.server.events;

import java.util.Date;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SendDataIndicationType;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

public class SubscribeNotificationsRequestImpl extends DiameterShMessageImpl implements SubscribeNotificationsRequest {

	public SubscribeNotificationsRequestImpl(Message msg) {
		super(msg);
		super.longMessageName="Subscribe-Notification-Request";
		super.shortMessageName="SNR";
	}

	public DataReferenceType[] getDataReferences() {

		AvpSet set=super.message.getAvps().getAvps(DiameterShAvpCodes.DATA_REFERENCE);
		if(set==null )
		{
			return null;
			
		}else
		{
			DataReferenceType[] returnValue=new DataReferenceType[set.size()];
			int counter=0;
			for(Avp raw:set)
			{
				try {
					returnValue[counter++]=DataReferenceType.fromInt(raw.getInteger32());
				} catch (AvpDataException e) {
					
					e.printStackTrace();
					return null;
				}
			}
			
			return returnValue;
		}
	}

	public SendDataIndicationType getSendDataIndication() {
		if(hasSendDataIndication())
		{
			try {
				return SendDataIndicationType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SEND_DATA_INDICATION).getInteger32());
			} catch (AvpDataException e) {

				e.printStackTrace();
			}
		}
		
		
		return null;
	}

	public String getServerName() {

		if(hasServerName())
		{
			try {
				return super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME).getUTF8String();
			} catch (AvpDataException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public byte[][] getServiceIndications() {
		
		AvpSet set=super.message.getAvps().getAvps(DiameterShAvpCodes.SERVICE_INDICATION);
		if(set==null)
		{
		return null;
		}else
		{
			byte[][] returnValue=new byte[set.size()][];
			int counter=0;
			for(Avp raw:set)
			{
				try {
					returnValue[counter++]=raw.getRaw();
				} catch (AvpDataException e) {
					e.printStackTrace();
					return null;
				}
			}
			return returnValue;
		}
		
	}

	public SubsReqType getSubsReqType() {

		if(hasSubsReqType())
		{
			try {
				return SubsReqType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.SUBS_REQ_TYPE).getInteger32());
			} catch (AvpDataException e) {
								e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasSendDataIndication() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.SEND_DATA_INDICATION) != null;
	}

	public boolean hasServerName() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.SERVER_NAME) != null;
	}

	public boolean hasSubsReqType() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.SUBS_REQ_TYPE) != null;
	}

	public void setDataReference(DataReferenceType dataReference) {
		super.setAvpAsInt32(DiameterShAvpCodes.DATA_REFERENCE, dataReference.getValue(), true, true);

	}

	public void setDataReferences(DataReferenceType[] dataReferences) {

		super.message.getAvps().removeAvp(DiameterShAvpCodes.DATA_REFERENCE);
		for (DataReferenceType drt : dataReferences)
			super.setAvpAsInt32(DiameterShAvpCodes.DATA_REFERENCE, drt.getValue(), true, false);

	}

	public void setSendDataIndication(SendDataIndicationType sendDataIndication) {

		super.setAvpAsInt32(DiameterShAvpCodes.SEND_DATA_INDICATION, sendDataIndication.getValue(), true, true);

	}

	public void setServerName(String serverName) {

		super.setAvpAsUtf8(DiameterShAvpCodes.SERVER_NAME, serverName, true, true);

	}

	public void setServiceIndication(byte[] serviceIndication) {

		super.message.getAvps().removeAvp(DiameterShAvpCodes.SERVICE_INDICATION);
		super.addAvpAsByteArray(DiameterShAvpCodes.SERVICE_INDICATION, serviceIndication, true);

	}

	public void setServiceIndications(byte[][] serviceIndications) {

		super.message.getAvps().removeAvp(DiameterShAvpCodes.SERVICE_INDICATION);
		for (byte[] b : serviceIndications) {
			super.addAvpAsByteArray(DiameterShAvpCodes.SERVICE_INDICATION, b, true);
		}

	}

	public void setSubsReqType(SubsReqType subsReqType) {

		super.setAvpAsInt32(DiameterShAvpCodes.SUBS_REQ_TYPE, subsReqType.getValue(), true, true);

	}

	public UserIdentityAvp getUserIdentity() {

		if (!hasUserIdentity())
			return null;
		Avp rawAvp = super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY);

		UserIdentityAvp returnValue;
		try {
			returnValue = new UserIdentityAvpImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
		} catch (AvpDataException e) {

			e.printStackTrace();
			return null;
		}

		return returnValue;
	}

	public boolean hasUserData() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_DATA) != null;
	}

	public boolean hasUserIdentity() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.USER_IDENTITY) != null;
	}

	public Date getExpiryTime() {
		if (hasExpiryTime()) {

			return super.getAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME);

		}
		return null;
	}

	public boolean hasExpiryTime() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.EXPIRY_TIME) != null;
	}

	public void setExpiryTime(Date expiryTime) {
		super.setAvpAsDate(DiameterShAvpCodes.EXPIRY_TIME, expiryTime, true, true);

	}

	public void setUserIdentity(UserIdentityAvp userIdentity) {

		super.setAvpAsGroup(userIdentity.getCode(), new UserIdentityAvp[] { userIdentity }, true, true);
	}

}
