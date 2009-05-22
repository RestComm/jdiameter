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
package net.java.slee.resource.diameter.sh.client.events.avp;



import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;


/**
 * Defines an interface representing the Supported-Applications grouped AVP type.
 *
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0) specification:
 * <pre>
 * 6.3.32        Supported-Applications AVP
 * 
 * The Supported-Applications AVP is of type Grouped and it contains the supported
 * application identifiers of a Diameter node.
 * 
 * AVP format
 * Supported-Applications ::=    &lt; AVP Header: 631 10415 &gt;
 *                               *{ Auth-Application-Id }
 *                               *{ Acct-Application-Id }
 *                               *{ Vendor-Specific-Application-Id }
 *                               *[ AVP ]
 * </pre>
 */
public interface SupportedApplicationsAvp extends GroupedAvp {

    /**
     * Returns the set of Auth-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Auth-Application-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    public long[] getAuthApplicationIds();

    /**
     * Sets a single Auth-Application-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setAuthApplicationId or setAuthApplicationIds
     *  has already been called
     */
    public void setAuthApplicationId(long authApplicationId);

    /**
     * Sets the set of Auth-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getAuthApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setAuthApplicationId or setAuthApplicationIds
     *  has already been called
     */
    public void setAuthApplicationIds(long[] authApplicationIds);

    /**
     * Returns the set of Acct-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Acct-Application-Id AVPs have been set.
     * The elements in the given array are long objects.
     */
    public long[] getAcctApplicationIds();

    /**
     * Sets a single Acct-Application-Id AVP in the message, of type Unsigned32.
     * @throws IllegalStateException if setAcctApplicationId or setAcctApplicationIds
     *  has already been called
     */
    public void setAcctApplicationId(long acctApplicationId);

    /**
     * Sets the set of Acct-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getAcctApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setAcctApplicationId or setAcctApplicationIds
     *  has already been called
     */
    public void setAcctApplicationIds(long[] acctApplicationIds);

    /**
     * Returns the set of Vendor-Specific-Application-Id AVPs. The returned array contains
     * the AVPs in the order they appear in the message.
     * A return value of null implies that no Vendor-Specific-Application-Id AVPs have been set.
     * The elements in the given array are VendorSpecificApplicationId objects.
     */
    public VendorSpecificApplicationIdAvp[] getVendorSpecificApplicationIds();

    /**
     * Sets a single Vendor-Specific-Application-Id AVP in the message, of type Grouped.
     * @throws IllegalStateException if setVendorSpecificApplicationId or setVendorSpecificApplicationIds
     *  has already been called
     */
    public void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

    /**
     * Sets the set of Vendor-Specific-Application-Id AVPs, with all the values in the given array.
     * The AVPs will be added to message in the order in which they appear in the array.
     *
     * Note: the array must not be altered by the caller following this call, and
     * getVendorSpecificApplicationIds() is not guaranteed to return the same array instance,
     * e.g. an "==" check would fail.
     *
     * @throws IllegalStateException if setVendorSpecificApplicationId or setVendorSpecificApplicationIds
     *  has already been called
     */
    public void setVendorSpecificApplicationIds(VendorSpecificApplicationIdAvp[] vendorSpecificApplicationIds);

    

}
