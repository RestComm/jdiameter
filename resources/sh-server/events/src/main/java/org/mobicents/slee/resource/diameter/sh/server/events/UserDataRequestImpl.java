package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.CurrentLocationType;
import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.IdentitySetType;
import net.java.slee.resource.diameter.sh.client.events.avp.RequestedDomainType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;

import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

public class UserDataRequestImpl extends SubscribeNotificationsRequestImpl implements UserDataRequest {

	public UserDataRequestImpl(Message msg) {
		super(msg);
	
		super.longMessageName="User-Data-Request";
		super.shortMessageName="UDR";
	}

	public CurrentLocationType getCurrentLocation() {
		if (hasCurrentLocation()) {
			try {
				return CurrentLocationType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.CURRENT_LOCATION).getInteger32());
			} catch (AvpDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public IdentitySetType getIdentitySet() {
		if (hasIdentitySet()) {
			try {
				return IdentitySetType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.IDENTITY_SET).getInteger32());
			} catch (AvpDataException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	public RequestedDomainType getRequestedDomain() {
		if (hasRequestedDomain()) {
			try {
				return RequestedDomainType.fromInt(super.message.getAvps().getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN).getInteger32());
			} catch (AvpDataException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasCurrentLocation() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.CURRENT_LOCATION) != null;
	}

	public boolean hasIdentitySet() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.IDENTITY_SET) != null;
	}

	public boolean hasRequestedDomain() {
		return super.message.getAvps().getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN) != null;
	}

	public void setCurrentLocation(CurrentLocationType currentLocation) {

		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.CURRENT_LOCATION);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

		super.setAvpAsInt32(DiameterShAvpCodes.CURRENT_LOCATION, currentLocation.getValue(), mandatoryAvp == 1, true);

	}

	public void setIdentitySet(IdentitySetType identitySet) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.IDENTITY_SET);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

		super.setAvpAsInt32(DiameterShAvpCodes.IDENTITY_SET, identitySet.getValue(), mandatoryAvp == 1, true);

	}

	public void setRequestedDomain(RequestedDomainType requestedDomain) {
		AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterShAvpCodes.REQUESTED_DOMAIN);

		int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;

		super.setAvpAsInt32(DiameterShAvpCodes.REQUESTED_DOMAIN, requestedDomain.getValue(), mandatoryAvp == 1, true);

	}

}
