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

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Java class to represent the AddressType enumerated type.
 *<P/>
 * Documentation from the relevant specification:
 *<P/>
 * Address type defined in http://www.iana.org/assignments/address-family-numbers, referenced by the Diameter specification. 
 *
 * @author Open Cloud
 */
public class AddressType implements Serializable, Enumerated {

  private static final long serialVersionUID = 1L;

  public static final int _ADDRESS_RESERVED = 0;
  public static final int _ADDRESS_IP = 1;
  public static final int _ADDRESS_IPV6 = 2;
  public static final int _ADDRESS_NSAP = 3;
  public static final int _ADDRESS_HDLC = 4;
  public static final int _ADDRESS_BBN1822 = 5;
  public static final int _ADDRESS_802 = 6;
  public static final int _ADDRESS_E163 = 7;
  public static final int _ADDRESS_E164 = 8;
  public static final int _ADDRESS_F69 = 9;
  public static final int _ADDRESS_X121 = 10;
  public static final int _ADDRESS_IPX = 11;
  public static final int _ADDRESS_APPLETALK = 12;
  public static final int _ADDRESS_DECNET_IV = 13;
  public static final int _ADDRESS_BANYAN_VINES = 14;
  public static final int _ADDRESS_E164NSAP = 15;
  public static final int _ADDRESS_DNS = 16;
  public static final int _ADDRESS_DN = 17;
  public static final int _ADDRESS_ASN = 18;
  public static final int _ADDRESS_XTPV4 = 19;
  public static final int _ADDRESS_XTPV6 = 20;
  public static final int _ADDRESS_XTPN = 21;
  public static final int _ADDRESS_FCP = 22;
  public static final int _ADDRESS_FCN = 23;
  public static final int _ADDRESS_GWID = 24;
  public static final int _ADDRESS_RESERVED2 = 65535;

  /**
   * Reserved 
   */
   public static final AddressType ADDRESS_RESERVED = new AddressType(_ADDRESS_RESERVED);

   /**
    * IP (IP version 4) 
    */
   public static final AddressType ADDRESS_IP = new AddressType(_ADDRESS_IP);

   /**
    * IP6 (IP version 6) 
    */
   public static final AddressType ADDRESS_IPV6 = new AddressType(_ADDRESS_IPV6);

   /**
    * NSAP 
    */
   public static final AddressType ADDRESS_NSAP = new AddressType(_ADDRESS_NSAP);

   /**
    * HDLC (8-bit multidrop) 
    */
   public static final AddressType ADDRESS_HDLC = new AddressType(_ADDRESS_HDLC);

   /**
    * BBN 1822 
    */
   public static final AddressType ADDRESS_BBN1822 = new AddressType(_ADDRESS_BBN1822);

   /**
    * 802 (includes all 802 media plus Ethernet "canonical format") 
    */
   public static final AddressType ADDRESS_802 = new AddressType(_ADDRESS_802);

   /**
    * E.163 
    */
   public static final AddressType ADDRESS_E163 = new AddressType(_ADDRESS_E163);

   /**
    * E.164 (SMDS, Frame Relay, ATM) 
    */
   public static final AddressType ADDRESS_E164 = new AddressType(_ADDRESS_E164);

   /**
    * F.69 (Telex) 
    */
   public static final AddressType ADDRESS_F69 = new AddressType(_ADDRESS_F69);

   /**
    * X.121 (X.25, Frame Relay) 
    */
   public static final AddressType ADDRESS_X121 = new AddressType(_ADDRESS_X121);

   /**
    * IPX 
    */
   public static final AddressType ADDRESS_IPX = new AddressType(_ADDRESS_IPX);

   /**
    * Appletalk 
    */
   public static final AddressType ADDRESS_APPLETALK = new AddressType(_ADDRESS_APPLETALK);

   /**
    * Decnet IV 
    */
   public static final AddressType ADDRESS_DECNET_IV = new AddressType(_ADDRESS_DECNET_IV);

   /**
    * Banyan Vines 
    */
   public static final AddressType ADDRESS_BANYAN_VINES = new AddressType(_ADDRESS_BANYAN_VINES);

   /**
    * E.164 with NSAP format subaddress 
    */
   public static final AddressType ADDRESS_E164NSAP = new AddressType(_ADDRESS_E164NSAP);

   /**
    * DNS (Domain Name System) 
    */
   public static final AddressType ADDRESS_DNS = new AddressType(_ADDRESS_DNS);

   /**
    * Distinguished Name 
    */
   public static final AddressType ADDRESS_DN = new AddressType(_ADDRESS_DN);

   /**
    * AS Number 
    */
   public static final AddressType ADDRESS_ASN = new AddressType(_ADDRESS_ASN);

   /**
    * XTP over IP version 4 
    */
   public static final AddressType ADDRESS_XTPV4 = new AddressType(_ADDRESS_XTPV4);

   /**
    * XTP over IP version 6 
    */
   public static final AddressType ADDRESS_XTPV6 = new AddressType(_ADDRESS_XTPV6);

   /**
    * XTP native mode XTP 
    */
   public static final AddressType ADDRESS_XTPN = new AddressType(_ADDRESS_XTPN);

   /**
    * Fibre Channel World-Wide Port Name 
    */
   public static final AddressType ADDRESS_FCP = new AddressType(_ADDRESS_FCP);

   /**
    * Fibre Channel World-Wide Node Name 
    */
   public static final AddressType ADDRESS_FCN = new AddressType(_ADDRESS_FCN);

   /**
    * GWID 
    */
   public static final AddressType ADDRESS_GWID = new AddressType(_ADDRESS_GWID);

   /**
    * Reserved 
    */
   public static final AddressType ADDRESS_RESERVED2 = new AddressType(_ADDRESS_RESERVED2);

   private AddressType(int value) {
     this.value = value;
   }

   public static AddressType fromInt(int type) {
     switch(type) {
     case _ADDRESS_RESERVED: return ADDRESS_RESERVED;
     case _ADDRESS_IP: return ADDRESS_IP;
     case _ADDRESS_IPV6: return ADDRESS_IPV6;
     case _ADDRESS_NSAP: return ADDRESS_NSAP;
     case _ADDRESS_HDLC: return ADDRESS_HDLC;
     case _ADDRESS_BBN1822: return ADDRESS_BBN1822;
     case _ADDRESS_802: return ADDRESS_802;
     case _ADDRESS_E163: return ADDRESS_E163;
     case _ADDRESS_E164: return ADDRESS_E164;
     case _ADDRESS_F69: return ADDRESS_F69;
     case _ADDRESS_X121: return ADDRESS_X121;
     case _ADDRESS_IPX: return ADDRESS_IPX;
     case _ADDRESS_APPLETALK: return ADDRESS_APPLETALK;
     case _ADDRESS_DECNET_IV: return ADDRESS_DECNET_IV;
     case _ADDRESS_BANYAN_VINES: return ADDRESS_BANYAN_VINES;
     case _ADDRESS_E164NSAP: return ADDRESS_E164NSAP;
     case _ADDRESS_DNS: return ADDRESS_DNS;
     case _ADDRESS_DN: return ADDRESS_DN;
     case _ADDRESS_ASN: return ADDRESS_ASN;
     case _ADDRESS_XTPV4: return ADDRESS_XTPV4;
     case _ADDRESS_XTPV6: return ADDRESS_XTPV6;
     case _ADDRESS_XTPN: return ADDRESS_XTPN;
     case _ADDRESS_FCP: return ADDRESS_FCP;
     case _ADDRESS_FCN: return ADDRESS_FCN;
     case _ADDRESS_GWID: return ADDRESS_GWID;
     case _ADDRESS_RESERVED2: return ADDRESS_RESERVED2;
     default: throw new IllegalArgumentException("Invalid AddressType value: " + type);
     }
   }

   public int getValue() {
     return value;
   }

   public String toString() {
     switch(value) {
     case _ADDRESS_RESERVED: return "ADDRESS_RESERVED";
     case _ADDRESS_IP: return "ADDRESS_IP";
     case _ADDRESS_IPV6: return "ADDRESS_IPV6";
     case _ADDRESS_NSAP: return "ADDRESS_NSAP";
     case _ADDRESS_HDLC: return "ADDRESS_HDLC";
     case _ADDRESS_BBN1822: return "ADDRESS_BBN1822";
     case _ADDRESS_802: return "ADDRESS_802";
     case _ADDRESS_E163: return "ADDRESS_E163";
     case _ADDRESS_E164: return "ADDRESS_E164";
     case _ADDRESS_F69: return "ADDRESS_F69";
     case _ADDRESS_X121: return "ADDRESS_X121";
     case _ADDRESS_IPX: return "ADDRESS_IPX";
     case _ADDRESS_APPLETALK: return "ADDRESS_APPLETALK";
     case _ADDRESS_DECNET_IV: return "ADDRESS_DECNET_IV";
     case _ADDRESS_BANYAN_VINES: return "ADDRESS_BANYAN_VINES";
     case _ADDRESS_E164NSAP: return "ADDRESS_E164NSAP";
     case _ADDRESS_DNS: return "ADDRESS_DNS";
     case _ADDRESS_DN: return "ADDRESS_DN";
     case _ADDRESS_ASN: return "ADDRESS_ASN";
     case _ADDRESS_XTPV4: return "ADDRESS_XTPV4";
     case _ADDRESS_XTPV6: return "ADDRESS_XTPV6";
     case _ADDRESS_XTPN: return "ADDRESS_XTPN";
     case _ADDRESS_FCP: return "ADDRESS_FCP";
     case _ADDRESS_FCN: return "ADDRESS_FCN";
     case _ADDRESS_GWID: return "ADDRESS_GWID";
     case _ADDRESS_RESERVED2: return "ADDRESS_RESERVED2";
     default: return "<Invalid Value>";
     }
   }

   private Object readResolve() throws StreamCorruptedException {
     try {
       return fromInt(value);
     }
     catch (IllegalArgumentException iae) {
       throw new StreamCorruptedException("Invalid internal state found: " + value);
     }
   }

   private int value;
}
