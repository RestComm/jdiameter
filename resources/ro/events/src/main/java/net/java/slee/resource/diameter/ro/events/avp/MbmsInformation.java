/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * 
 * Defines an interface representing the MBMS-Information grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification:
 * 
 *  7.2.55 MBMS-Information AVP 
 *  The MBMS-Information AVP (AVP code 880) is of type Grouped. Its purpose is to allow the transmission of additional MBMS service specific information elements. 
 *  It has the following ABNF grammar: 
 *    MBMS-Information ::= AVP Header: 880 
 *       { TMGI } 
 *       { MBMS-Service-Type } 
 *       { MBMS-User-Service-Type } 
 *       [ File-Repair-Supported ] 
 *       [ Required-MBMS-Bearer-Capabilities ] 
 *       [ MBMS-2G-3G-Indicator ] 
 *       [ RAI ] 
 *     * [ MBMS-Service-Area ] 
 *       [ MBMS-Session-Identity ]
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface MbmsInformation extends GroupedAvp{
  /**
   * Returns the value of the File-Repair-Supported AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported getFileRepairSupported();

  /**
   * Returns the value of the MBMS-2G-3G-Indicator AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract Mbms2g3gIndicator getMbms2g3gIndicator();

  /**
   * Returns the set of MBMS-Service-Area AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no MBMS-Service-Area AVPs have been set. The elements in the given array are byte[] objects.
   */
  abstract String[] getMbmsServiceAreas();

  /**
   * Returns the value of the MBMS-Service-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.MbmsServiceType getMbmsServiceType();

  /**
   * Returns the value of the MBMS-Session-Identity AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getMbmsSessionIdentity();

  /**
   * Returns the value of the MBMS-User-Service-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType getMbmsUserServiceType();

  /**
   * Returns the value of the RAI AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getRai();

  /**
   * Returns the value of the Required-MBMS-Bearer-Capabilities AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getRequiredMbmsBearerCapabilities();

  /**
   * Returns the value of the TMGI AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTmgi();

  /**
   * Returns true if the File-Repair-Supported AVP is present in the message.
   */
  abstract boolean hasFileRepairSupported();

  /**
   * Returns true if the MBMS-2G-3G-Indicator AVP is present in the message.
   */
  abstract boolean hasMbms2g3gIndicator();

  /**
   * Returns true if the MBMS-Service-Type AVP is present in the message.
   */
  abstract boolean hasMbmsServiceType();

  /**
   * Returns true if the MBMS-Session-Identity AVP is present in the message.
   */
  abstract boolean hasMbmsSessionIdentity();

  /**
   * Returns true if the MBMS-User-Service-Type AVP is present in the message.
   */
  abstract boolean hasMbmsUserServiceType();

  /**
   * Returns true if the RAI AVP is present in the message.
   */
  abstract boolean hasRai();

  /**
   * Returns true if the Required-MBMS-Bearer-Capabilities AVP is present in the message.
   */
  abstract boolean hasRequiredMbmsBearerCapabilities();

  /**
   * Returns true if the TMGI AVP is present in the message.
   */
  abstract boolean hasTmgi();

  /**
   * Sets the value of the File-Repair-Supported AVP, of type Enumerated.
   */
  abstract void setFileRepairSupported(net.java.slee.resource.diameter.ro.events.avp.FileRepairSupported fileRepairSupported);

  /**
   * Sets the value of the MBMS-2G-3G-Indicator AVP, of type OctetString.
   */
  abstract void setMbms2g3gIndicator(Mbms2g3gIndicator mbms2g3gIndicator);

  /**
   * Sets a single MBMS-Service-Area AVP in the message, of type OctetString.
   */
  abstract void setMbmsServiceArea(String mbmsServiceArea);

  /**
   * Sets the set of MBMS-Service-Area AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getMbmsServiceAreas() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setMbmsServiceAreas(String[] mbmsServiceAreas);

  /**
   * Sets the value of the MBMS-Service-Type AVP, of type Enumerated.
   */
  abstract void setMbmsServiceType(net.java.slee.resource.diameter.ro.events.avp.MbmsServiceType mbmsServiceType);

  /**
   * Sets the value of the MBMS-Session-Identity AVP, of type OctetString.
   */
  abstract void setMbmsSessionIdentity(String mbmsSessionIdentity);

  /**
   * Sets the value of the MBMS-User-Service-Type AVP, of type Enumerated.
   */
  abstract void setMbmsUserServiceType(net.java.slee.resource.diameter.ro.events.avp.MbmsUserServiceType mbmsUserServiceType);

  /**
   * Sets the value of the RAI AVP, of type OctetString.
   */
  abstract void setRai(String rai);

  /**
   * Sets the value of the Required-MBMS-Bearer-Capabilities AVP, of type OctetString.
   */
  abstract void setRequiredMbmsBearerCapabilities(String requiredMbmsBearerCapabilities);

  /**
   * Sets the value of the TMGI AVP, of type OctetString.
   */
  abstract void setTmgi(String tmgi);

}
