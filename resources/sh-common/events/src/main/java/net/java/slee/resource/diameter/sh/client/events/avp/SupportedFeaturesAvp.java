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

/**
 * Defines an interface representing the Supported-Features grouped AVP type.
 *
 * From the Diameter Sh Reference Point Protocol Details (3GPP TS 29.329 V7.1.0) specification:
 * <pre>
 * 6.3.29        Supported-Features AVP
 * 
 * The Supported-Features AVP is of type Grouped. If this AVP is present it may inform the
 * destination host about the features that the origin host supports. The Feature-List AVP
 * contains a list of supported features of the origin host. The Vendor-ID AVP and the
 * Feature-List AVP shall together identify which feature list is carried in the
 * Supported-Features AVP.
 * 
 * Where a Supported-Features AVP is used to identify features that have been defined by
 * 3GPP, the Vendor-ID AVP shall contain the vendor ID of 3GPP. Vendors may define
 * proprietary features, but it is strongly recommended that the possibility is used only as
 * the last resort. Where the Supported-Features AVP is used to identify features that have
 * been defined by a vendor other than 3GPP, it shall contain the vendor ID of the specific
 * vendor in question. 
 * 
 * If there are multiple feature lists defined by the same vendor, the Feature-List-ID AVP
 * shall differentiate those lists from one another. The destination host shall use the value
 * of the Feature-List-ID AVP to identify the feature list. 
 * 
 * AVP format
 * Supported-Features ::=  < AVP Header: 628 10415 >
 *                         { Vendor-Id }
 *                         { Feature-List-ID }
 *                         { Feature-List }
 *                         *[AVP]
 * </pre>
 */
public interface SupportedFeaturesAvp extends GroupedAvp {

  /**
   * Returns true if the Vendor-Id AVP is present in the message.
   */
  public boolean hasVendorId();

  /**
   * Returns the value of the Vendor-Id AVP, of type Unsigned32.
   * A return value of null implies that the AVP has not been set.
   */
  public long getVendorId();

  /**
   * Sets the value of the Vendor-Id AVP, of type Unsigned32.
   * @throws IllegalStateException if setVendorId has already been called
   */
  public void setVendorId(long vendorId);

  /**
   * Returns true if the Feature-List-ID AVP is present in the message.
   */
  public boolean hasFeatureListId();

  /**
   * Returns the value of the Feature-List-ID AVP, of type Unsigned32.
   * A return value of null implies that the AVP has not been set.
   */
  public long getFeatureListId();

  /**
   * Sets the value of the Feature-List-ID AVP, of type Unsigned32.
   * @throws IllegalStateException if setFeatureListId has already been called
   */
  public void setFeatureListId(long featureListId);

  /**
   * Returns true if the Feature-List AVP is present in the message.
   */
  public boolean hasFeatureList();

  /**
   * Returns the value of the Feature-List AVP, of type Unsigned32.
   * A return value of null implies that the AVP has not been set.
   */
  public long getFeatureList();

  /**
   * Sets the value of the Feature-List AVP, of type Unsigned32.
   * @throws IllegalStateException if setFeatureList has already been called
   */
  public void setFeatureList(long featureList);


}
