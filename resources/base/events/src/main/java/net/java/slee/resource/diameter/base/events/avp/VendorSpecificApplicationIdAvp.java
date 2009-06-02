/*
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base.events.avp;

/**
 * Defines an interface representing the Vendor-Specific-Application-Id grouped AVP type.
 *
 * From the Diameter Base Protocol (rfc3588.txt) specification:
 * <pre>
 * 6.11.  Vendor-Specific-Application-Id AVP
 * 
 *    The Vendor-Specific-Application-Id AVP (AVP Code 260) is of type
 *    Grouped and is used to advertise support of a vendor-specific
 *    Diameter Application.  Exactly one of the Auth-Application-Id and
 *    Acct-Application-Id AVPs MAY be present.
 * 
 *    This AVP MUST also be present as the first AVP in all experimental
 *    commands defined in the vendor-specific application.
 * 
 *    This AVP SHOULD be placed as close to the Diameter header as
 *    possible.
 * 
 *    AVP Format
 * 
 *    &lt;Vendor-Specific-Application-Id&gt; ::= &lt; AVP Header: 260 &gt;
 *                                      1* [ Vendor-Id ]
 *                                      0*1{ Auth-Application-Id }
 *                                      0*1{ Acct-Application-Id }
 * </pre>
 */
public interface VendorSpecificApplicationIdAvp extends GroupedAvp {

    /**
     * Returns the set of Vendor-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Vendor-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    public long[] getVendorIdsAvp();

    /**
     * Sets a single Vendor-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setVendorId or setVendorIds
     *  has already been called
     */
    public void setVendorIdAvp(long vendorId);

    /**
     * Sets the set of Vendor-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getVendorIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setVendorId or setVendorIds
     *  has already been called
     */
    public void setVendorIdsAvp(long[] vendorIds);

    /**
     * Returns true if the Auth-Application-Id AVP is present in the message.
     */
    public boolean hasAuthApplicationId();

    /**
     * Returns the value of the Auth-Application-Id AVP, of type Unsigned32.
     * A return value of null implies that the AVP has not been set.
     */
    public long getAuthApplicationId();

    /**
     * Sets the value of the Auth-Application-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setAuthApplicationId has already been called
     */
    public void setAuthApplicationId(long authApplicationId);

    /**
     * Returns true if the Acct-Application-Id AVP is present in the message.
     */
    public boolean hasAcctApplicationId();

    /**
     * Returns the value of the Acct-Application-Id AVP, of type Unsigned32.
     * A return value of null implies that the AVP has not been set.
     */
    public long getAcctApplicationId();

    /**
     * Sets the value of the Acct-Application-Id AVP, of type Unsigned32.
     * @throws IllegalStateException if setAcctApplicationId has already been called
     */
    public void setAcctApplicationId(long acctApplicationId);

}
