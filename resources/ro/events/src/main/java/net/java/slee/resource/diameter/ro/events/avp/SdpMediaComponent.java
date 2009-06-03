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

/**
 * 
 * Defines an interface representing the SDP-Media-Component grouped AVP type. 
 * 
 * From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 
 *  7.2.95 SDP-Media-Component AVP The SDP-Media-Component AVP (AVP code 843) is of type Grouped and contains information about media used for a IMS session. 
 *  
 *  It has the following ABNF grammar: 
 *   SDP-Media-Component ::= AVP Header: 843 
 *     [ SDP-Media-Name ] 
 *   * [ SDP-Media-Description ] 
 *     [ Media-Initiator-Flag] 
 *     [ Authorized-QoS ] 
 *     [ TGPP-Charging-Id ]
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface SdpMediaComponent extends net.java.slee.resource.diameter.base.events.avp.GroupedAvp{
  /**
   * Returns the value of the Authorized-QoS AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getAuthorizedQos();

  /**
   * Returns the value of the Media-Initiator-Flag AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
   */
  abstract net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag getMediaInitiatorFlag();

  /**
   * Returns the set of SDP-Media-Description AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no SDP-Media-Description AVPs have been set. The elements in the given array are String objects.
   */
  abstract java.lang.String[] getSdpMediaDescriptions();

  /**
   * Returns the value of the SDP-Media-Name AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
   */
  abstract java.lang.String getSdpMediaName();

  /**
   * Returns the value of the TGPP-Charging-Id AVP, of type OctetString. A return value of null implies that the AVP has not been set.
   */
  abstract String getTgppChargingId();

  /**
   * Returns true if the Authorized-QoS AVP is present in the message.
   */
  abstract boolean hasAuthorizedQos();

  /**
   * Returns true if the Media-Initiator-Flag AVP is present in the message.
   */
  abstract boolean hasMediaInitiatorFlag();

  /**
   * Returns true if the SDP-Media-Name AVP is present in the message.
   */
  abstract boolean hasSdpMediaName();

  /**
   * Returns true if the TGPP-Charging-Id AVP is present in the message.
   */
  abstract boolean hasTgppChargingId();

  /**
   * Sets the value of the Authorized-QoS AVP, of type UTF8String.
   */
  abstract void setAuthorizedQos(java.lang.String authorizedQos);

  /**
   * Sets the value of the Media-Initiator-Flag AVP, of type Enumerated.
   */
  abstract void setMediaInitiatorFlag(net.java.slee.resource.diameter.ro.events.avp.MediaInitiatorFlag mediaInitiatorFlag);

  /**
   * Sets a single SDP-Media-Description AVP in the message, of type UTF8String.
   */
  abstract void setSdpMediaDescription(java.lang.String sdpMediaDescription);

  /**
   * Sets the set of SDP-Media-Description AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getSdpMediaDescriptions() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
   */
  abstract void setSdpMediaDescriptions(java.lang.String[] sdpMediaDescriptions);

  /**
   * Sets the value of the SDP-Media-Name AVP, of type UTF8String.
   */
  abstract void setSdpMediaName(java.lang.String sdpMediaName);

  /**
   * Sets the value of the TGPP-Charging-Id AVP, of type OctetString.
   */
  abstract void setTgppChargingId(String tgppChargingId);

}
