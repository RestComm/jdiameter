package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.base.events.avp.AddressType;

public class AddressAvpImpl implements AddressAvp{
	 public AddressAvpImpl(AddressType addressType, byte[] address) {
	        this.addressType = addressType;
	        this.address = address;
	    }

	    public AddressType getAddressType() {
	        return addressType;
	    }

	    public byte[] getAddress() {
	        return address;
	    }

	    public static AddressAvp decode(byte[] encodedAddress) {
	        // "The first two octets of the Address AVP represents the AddressType"
	        // And in 8bits at a time
	        int addressTypeInt = (encodedAddress[0] << 8);
	        addressTypeInt |= (encodedAddress[1]) & 0x000000ff;

	        // decode the address bytes
	        byte[] addressBytes = new byte[encodedAddress.length - 2];
	        System.arraycopy(encodedAddress, 2, addressBytes, 0, addressBytes.length);

	        return new AddressAvpImpl(AddressType.fromInt(addressTypeInt),addressBytes);
	    }

	    public byte[] encode() {
	        // "The first two octets of the Address AVP represents the AddressType"
	        int addressTypeInt = addressType.getValue();
	        byte[] encodedAddress = new byte[address.length + 2];
	        encodedAddress[0] = (byte)(addressTypeInt>>8); //get bits 15-8
	        encodedAddress[1] = (byte)addressTypeInt; //get bits 7-0

	        // encode the address bytes
	        System.arraycopy(address, 0, encodedAddress, 2, address.length);

	        return encodedAddress;
	    }

	    private final AddressType addressType;
	    private final byte[] address;
}
