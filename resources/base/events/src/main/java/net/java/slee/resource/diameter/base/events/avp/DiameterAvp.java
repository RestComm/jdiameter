/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
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

import javax.naming.OperationNotSupportedException;


/**
 * Diameter Attribute Value Pair (AVP).  Applications can use this interface to retrieve the
 * AVP's code, name, type and value.  Concrete implementations of this interface are created via the
 * {@link DiameterMessageFactory}.
 *
 * @author Open Cloud
 */
public interface DiameterAvp extends Cloneable {

    /**
     * Return the AVP code, e.g., 263 for Session-Id
     * @return the AVP code
     */
    int getCode();

    /**
     * Return the Vendor-ID value for proprietary (non-IETF) AVPs.<p>
     * The value should be the IANA-assigned
     * <a href="http://www.iana.org/assignments/enterprise-numbers">"SMI Network Management Private Enterprise Codes"</a>
     * value of the organisation that defined the AVP, for example 3GPP is 10415.<p>
     * A Vendor-ID value of zero means the Vendor-ID is not specified. Standard AVPs defined
     * by IETF will have a Vendor-ID of zero.
     * @return the Vendor-ID, or zero if it is not specified.
     */
    long getVendorId();

    /**
     * Return the AVP name, e.g., "Session-Id"
     * @return the AVP name
     */
    String getName();

    /**
     * Return the AVP type (one of the String constants from {@link org.mobicents.slee.resource.diameter.base.DiameterAvpType}.
     * @return the AVP type
     */
    DiameterAvpType getType();

    /**
     * Return the rule for the mandatory (M) flag of this AVP.<P>
     * 0 - MUST<BR>
     * 1 - MAY<BR>
     * 2 - MUSTNOT<BR>
     */
    int getMandatoryRule();

    /**
     * Return the rule for the protected (P) flag of this AVP.<P>
     * 0 - MUST<BR>
     * 1 - MAY<BR>
     * 2 - MUSTNOT<BR>
     */
    int getProtectedRule();

    /**
     * The value of this AVP if the Diameter type can be represented in
     * a Java double value (e.g., Float64, Float32)
     * @return the AVP value as a double
     * @throws OperationNotSupportedException if the AVP does not contain a double equivalent
     */
    double doubleValue();

    /**
     * The value of this AVP if the Diameter type can be represented in
     * a Java float value (e.g., Float32)
     * @return the AVP value as a float
     * @throws OperationNotSupportedException if the AVP does not contain a float equivalent
     */
    float floatValue();

    /**
     * The value of this AVP if the Diameter type can be represented in
     * a Java int value (e.g., Integer32)
     * @return the AVP value as an int
     * @throws OperationNotSupportedException if the AVP does not contain an int equivalent
     */
    int intValue();

    /**
     * The value of this AVP if the Diameter type can be represented in
     * a Java long value (e.g., Integer64, Unsigned32, Integer32)
     * @return the AVP value as a long
     * @throws OperationNotSupportedException if the AVP does not contain a long equivalent
     */
    long longValue();

    /**
     * The value of this AVP if the Diameter type is equivalent to a Java String
     * value (e.g., UTF8String)
     * @return the AVP value as a String
     * @throws 3588 if the AVP does not contain a String equivalent
     */
    String stringValue();
    
    /**
     * The value of this AVP if the Diameter type is equivalent to a Java String
     * value (e.g., OctetString)
     * @return the AVP value as a String
     * @throws 3588 if the AVP does not contain a String equivalent
     */
    String octetStringValue();

    /**
     * Return the raw contents of this AVP
     * @return the AVP value as a byte array
     */
    byte[] byteArrayValue();


    /**
     * Creates and returns a deep copy of this AVP instance.
     * @return a deep copy of this AVP.
     */
    Object clone();

    int FLAG_RULE_MUST = 0;
    int FLAG_RULE_MAY = 1;
    int FLAG_RULE_MUSTNOT = 2;
}
