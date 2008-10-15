package net.java.slee.resource.diameter.base.events.avp;


/**
 * Java class to represent the Address AVP type.
 * <p/>
 * The Address format is derived from the OctetString AVP Base
 * Format.  It is a discriminated union, representing, for example a
 * 32-bit (IPv4) [IPV4] or 128-bit (IPv6) [IPV6] address, most
 * significant octet first.  The first two octets of the Address
 * AVP represents the AddressType, which contains an Address Family
 * defined in [IANAADFAM].  The AddressType is used to discriminate
 * the content and format of the remaining octets.
 */
public interface AddressAvp {

	public AddressType getAddressType() ;

    public byte[] getAddress() ;
    public byte[] encode();
    

    

}
