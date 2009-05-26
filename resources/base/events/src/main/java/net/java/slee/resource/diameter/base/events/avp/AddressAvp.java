/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
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
public class AddressAvp {

  private final AddressType addressType;
  private final byte[] address;

  public AddressAvp(AddressType addressType, byte[] address) {
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

    return new AddressAvp(AddressType.fromInt(addressTypeInt), addressBytes);
  }

  public byte[] encode() {
    // "The first two octets of the Address AVP represents the AddressType"
    int addressTypeInt = addressType.getValue();
    byte[] encodedAddress = new byte[address.length + 2];
    encodedAddress[0] = (byte) (addressTypeInt >> 8); // get bits 15-8
    encodedAddress[1] = (byte) addressTypeInt; // get bits 7-0

    // encode the address bytes
    System.arraycopy(address, 0, encodedAddress, 2, address.length);

    return encodedAddress;
  }

}
