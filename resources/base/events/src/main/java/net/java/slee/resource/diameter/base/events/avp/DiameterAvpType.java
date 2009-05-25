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
package net.java.slee.resource.diameter.base.events.avp;

import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * Enumerated type class to identify AVP value types.
 *
 * <p/>
 * From the spec:
 * <p/>
 * The Data field is zero or more octets and contains information
 * specific to the Attribute.  The format and length of the Data field
 * is determined by the AVP Code and AVP Length fields.  The format of
 * the Data field MUST be one of the following base data types or a data
 * type derived from the base data types.  In the event that a new Basic
 * AVP Data Format is needed, a new version of this RFC must be created.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class DiameterAvpType implements Serializable {

  private static final long serialVersionUID = 1L;

  private DiameterAvpType(int type)
  {
    this.type = type;
  }

  public String toString()
  {
    return names[type];
  }

  public static DiameterAvpType fromString(String s)
  {
    for (int i = 0; i < names.length; i++) {
      if(s.equals(names[i])) {
        return fromInt(i);
      }
    }
    
    throw new IllegalArgumentException("Unknown type: " + s);
  }

  public int getType()
  {
    return type;
  }

  public static DiameterAvpType fromInt(int i)
  {
    switch (i)
    {
    case _OCTET_STRING: return OCTET_STRING;
    case _INTEGER_32: return INTEGER_32;
    case _INTEGER_64: return INTEGER_64;
    case _UNSIGNED_32: return UNSIGNED_32;
    case _UNSIGNED_64: return UNSIGNED_64;
    case _FLOAT_32: return FLOAT_32;
    case _FLOAT_64: return FLOAT_64;
    case _GROUPED: return GROUPED;
    case _ADDRESS: return ADDRESS;
    case _TIME: return TIME;
    case _UTF8_STRING: return UTF8_STRING;
    case _DIAMETER_IDENTITY: return DIAMETER_IDENTITY;
    case _DIAMETER_URI: return DIAMETER_URI;
    case _ENUMERATED: return ENUMERATED;
    case _IP_FILTER_RULE: return IP_FILTER_RULE;
    case _QOS_FILTER_RULE: return QOS_FILTER_RULE;
    default: throw new IllegalArgumentException("Unknown type: " + i);
    }
  }

  public static final int _OCTET_STRING = 0;
  public static final int _INTEGER_32 = 1;
  public static final int _INTEGER_64 = 2;
  public static final int _UNSIGNED_32 = 3;
  public static final int _UNSIGNED_64 = 4;
  public static final int _FLOAT_32 = 5;
  public static final int _FLOAT_64 = 6;
  public static final int _GROUPED = 7;
  public static final int _ADDRESS = 8;
  public static final int _TIME = 9;
  public static final int _UTF8_STRING = 10;
  public static final int _DIAMETER_IDENTITY = 11;
  public static final int _DIAMETER_URI = 12;
  public static final int _ENUMERATED = 13;
  public static final int _IP_FILTER_RULE = 14;
  public static final int _QOS_FILTER_RULE = 15;

  private static final String[] names = {
    "OctetString",
    "Integer32",
    "Integer64",
    "Unsigned32",
    "Unsigned64",
    "Float32",
    "Float64",
    "Grouped",
    "Address",
    "Time",
    "UTF8String",
    "DiameterIdentity",
    "DiameterURI",
    "Enumerated",
    "IPFilterRule",
    "QoSFilterRule"
  };

  /**
   * The data contains arbitrary data of variable length.  Unless
   * otherwise noted, the AVP Length field MUST be set to at least 8
   * (12 if the 'V' bit is enabled).  AVP Values of this type that are
   * not a multiple of four-octets in length is followed by the
   * necessary padding so that the next AVP (if any) will start on a
   * 32-bit boundary.
   */
  public static final DiameterAvpType OCTET_STRING = new DiameterAvpType(_OCTET_STRING);
  
  /**
   * 32 bit signed value, in network byte order.  The AVP Length field
   * MUST be set to 12 (16 if the 'V' bit is enabled).
   */
  public static final DiameterAvpType INTEGER_32 = new DiameterAvpType(_INTEGER_32);
  
  /**
   * 64 bit signed value, in network byte order.  The AVP Length field
   * MUST be set to 16 (20 if the 'V' bit is enabled).
   */
  public static final DiameterAvpType INTEGER_64 = new DiameterAvpType(_INTEGER_64);
  
  /**
   * 32 bit unsigned value, in network byte order.  The AVP Length
   * field MUST be set to 12 (16 if the 'V' bit is enabled).
   */
  public static final DiameterAvpType UNSIGNED_32 = new DiameterAvpType(_UNSIGNED_32);
  
  /**
   * 64 bit unsigned value, in network byte order.  The AVP Length
   * field MUST be set to 16 (20 if the 'V' bit is enabled).
   */
  public static final DiameterAvpType UNSIGNED_64 = new DiameterAvpType(_UNSIGNED_64);
  
  /**
   * This represents floating point values of single precision as
   * described by [FLOATPOINT].  The 32-bit value is transmitted in
   * network byte order.  The AVP Length field MUST be set to 12 (16 if
   * the 'V' bit is enabled).
   */
  public static final DiameterAvpType FLOAT_32 = new DiameterAvpType(_FLOAT_32);
  
  /**
   * This represents floating point values of double precision as
   * described by [FLOATPOINT].  The 64-bit value is transmitted in
   * network byte order.  The AVP Length field MUST be set to 16 (20 if
   * the 'V' bit is enabled).
   */
  public static final DiameterAvpType FLOAT_64 = new DiameterAvpType(_FLOAT_64);
  
  /**
   * The Data field is specified as a sequence of AVPs.  Each of these
   * AVPs follows - in the order in which they are specified -
   * including their headers and padding.  The AVP Length field is set
   * to 8 (12 if the 'V' bit is enabled) plus the total length of all
   * included AVPs, including their headers and padding.  Thus the AVP
   * length field of an AVP of type Grouped is always a multiple of 4.
   */
  public static final DiameterAvpType GROUPED = new DiameterAvpType(_GROUPED);
  
  /**
   * The Address format is derived from the OctetString AVP Base
   * Format.  It is a discriminated union, representing, for example a
   * 32-bit (IPv4) [IPV4] or 128-bit (IPv6) [IPV6] address, most
   * significant octet first.  The first two octets of the Address
   * AVP represents the AddressType, which contains an Address Family
   * defined in [IANAADFAM].  The AddressType is used to discriminate
   * the content and format of the remaining octets.
   */
  public static final DiameterAvpType ADDRESS = new DiameterAvpType(_ADDRESS);
  
  /**
   * The Time format is derived from the OctetString AVP Base Format.
   * The string MUST contain four octets, in the same format as the
   * first four bytes are in the NTP timestamp format.  The NTP
   * Timestamp format is defined in chapter 3 of [SNTP].
   * <p/>
   * This represents the number of seconds since 0h on 1 January 1900
   * with respect to the Coordinated Universal Time (UTC).
   * <p/>
   * On 6h 28m 16s UTC, 7 February 2036 the time value will overflow.
   * SNTP [SNTP] describes a procedure to extend the time to 2104.
   * This procedure MUST be supported by all DIAMETER nodes.
   */
  public static final DiameterAvpType TIME = new DiameterAvpType(_TIME);
  
  /**
   * The UTF8String format is derived from the OctetString AVP Base
   * Format.  This is a human readable string represented using the
   * ISO/IEC IS 10646-1 character set, encoded as an OctetString using
   * the UTF-8 [UFT8] transformation format described in RFC 2279.
   * <p/>
   * Since additional code points are added by amendments to the 10646
   * standard from time to time, implementations MUST be prepared to
   * encounter any code point from 0x00000001 to 0x7fffffff.  Byte
   * sequences that do not correspond to the valid encoding of a code
   * point into UTF-8 charset or are outside this range are prohibited.
   * <p/>
   * The use of control codes SHOULD be avoided.  When it is necessary
   * to represent a new line, the control code sequence CR LF SHOULD be
   * used.
   * <p/>
   * The use of leading or trailing white space SHOULD be avoided.
   * <p/>
   * For code points not directly supported by user interface hardware
   * or software, an alternative means of entry and display, such as
   * hexadecimal, MAY be provided.
   * <p/>
   * For information encoded in 7-bit US-ASCII, the UTF-8 charset is
   * identical to the US-ASCII charset.
   * <p/>
   * UTF-8 may require multiple bytes to represent a single character /
   * code point; thus the length of an UTF8String in octets may be
   * different from the number of characters encoded.
   * <p/>
   * Note that the AVP Length field of an UTF8String is measured in
   * octets, not characters.
   */
  public static final DiameterAvpType UTF8_STRING = new DiameterAvpType(_UTF8_STRING);
  
  /**
          The DiameterIdentity format is derived from the OctetString AVP
          Base Format.
    <pre>

             DiameterIdentity  = FQDN
    </pre>

          DiameterIdentity value is used to uniquely identify a Diameter
          node for purposes of duplicate connection and routing loop
          detection.
    <p/>
          The contents of the string MUST be the FQDN of the Diameter node.
          If multiple Diameter nodes run on the same host, each Diameter
          node MUST be assigned a unique DiameterIdentity.  If a Diameter
          node can be identified by several FQDNs, a single FQDN should be
          picked at startup, and used as the only DiameterIdentity for that
          node, whatever the connection it is sent on.
   */
  public static final DiameterAvpType DIAMETER_IDENTITY = new DiameterAvpType(_DIAMETER_IDENTITY);
  
  /**
          The DiameterURI MUST follow the Uniform Resource Identifiers (URI)
          syntax [URI] rules specified below.

    <pre>
          "aaa://" FQDN [ port ] [ transport ] [ protocol ]

                          ; No transport security

          "aaas://" FQDN [ port ] [ transport ] [ protocol ]

                          ; Transport security used

          FQDN               = Fully Qualified Host Name

          port               = ":" 1*DIGIT

                          ; One of the ports used to listen for
                          ; incoming connections.
                          ; If absent,
                          ; the default Diameter port (3868) is
                          ; assumed.

          transport          = ";transport=" transport-protocol

                          ; One of the transports used to listen
                          ; for incoming connections.  If absent,
                          ; the default SCTP [SCTP] protocol is
                          ; assumed.  UDP MUST NOT be used when
                          ; the aaa-protocol field is set to
                          ; diameter.

          transport-protocol = ( "tcp" / "sctp" / "udp" )

          protocol           = ";protocol=" aaa-protocol

                          ; If absent, the default AAA protocol
                          ; is diameter.

          aaa-protocol       = ( "diameter" / "radius" / "tacacs+" )

          The following are examples of valid Diameter host identities:

          aaa://host.example.com;transport=tcp
          aaa://host.example.com:6666;transport=tcp
          aaa://host.example.com;protocol=diameter
          aaa://host.example.com:6666;protocol=diameter
          aaa://host.example.com:6666;transport=tcp;protocol=diameter
          aaa://host.example.com:1813;transport=udp;protocol=radius
    </pre>

   */
  public static final DiameterAvpType DIAMETER_URI = new DiameterAvpType(_DIAMETER_URI);
  
  /**
   * Enumerated is derived from the Integer32 AVP Base Format.  The
   * definition contains a list of valid values and their
   * interpretation and is described in the Diameter application
   * introducing the AVP.
   */
  public static final DiameterAvpType ENUMERATED = new DiameterAvpType(_ENUMERATED);
  
  /**
          The IPFilterRule format is derived from the OctetString AVP Base
          Format.  It uses the ASCII charset.  Packets may be filtered based
          on the following information that is associated with it.

    <pre>
             Direction                          (in or out)
             Source and destination IP address  (possibly masked)
             Protocol
             Source and destination port        (lists or ranges)
             TCP flags
             IP fragment flag
             IP options
             ICMP types
    </pre>

          Rules for the appropriate direction are evaluated in order, with
          the first matched rule terminating the evaluation.  Each packet is
          evaluated once.  If no rule matches, the packet is dropped if the
          last rule evaluated was a permit, and passed if the last rule was
          a deny.

          IPFilterRule filters MUST follow the format:
    <pre>
             action dir proto from src to dst [options]

             action       permit - Allow packets that match the rule.
                          deny   - Drop packets that match the rule.

             dir          "in" is from the terminal, "out" is to the
                          terminal.

             proto        An IP protocol specified by number.  The "ip"
                          keyword means any protocol will match.

             src and dst  &lt;address/mask&gt; [ports]

                          The &lt;address/mask&gt; may be specified as:
                          ipno       An IPv4 or IPv6 number in dotted-
                                     quad or canonical IPv6 form.  Only
                                     this exact IP number will match the
                                     rule.
                          ipno/bits  An IP number as above with a mask
                                     width of the form 1.2.3.4/24.  In
                                     this case, all IP numbers from
                                     1.2.3.0 to 1.2.3.255 will match.
                                     The bit width MUST be valid for the
                                     IP version and the IP number MUST
                                     NOT have bits set beyond the mask.
                                     For a match to occur, the same IP
                                     version must be present in the
                                     packet that was used in describing
                                     the IP address.  To test for a
                                     particular IP version, the bits part
                                     can be set to zero.  The keyword
                                     "any" is 0.0.0.0/0 or the IPv6
                                     equivalent.  The keyword "assigned"
                                     is the address or set of addresses
                                     assigned to the terminal.  For IPv4,
                                     a typical first rule is often "deny
                                     in ip! assigned"

                          The sense of the match can be inverted by
                          preceding an address with the not modifier (!),
                          causing all other addresses to be matched
                          instead.  This does not affect the selection of
                          port numbers.

                          With the TCP, UDP and SCTP protocols, optional
                          ports may be specified as:

                             {port/port-port}[,ports[,...]]

                          The '-' notation specifies a range of ports
                          (including boundaries).

                          Fragmented packets that have a non-zero offset
                          (i.e., not the first fragment) will never match
                          a rule that has one or more port
                          specifications.  See the frag option for
                          details on matching fragmented packets.

             options:
                frag    Match if the packet is a fragment and this is not
                        the first fragment of the datagram.  frag may not
                        be used in conjunction with either tcpflags or
                        TCP/UDP port specifications.

                ipoptions spec
                        Match if the IP header contains the comma
                        separated list of options specified in spec.  The
                        supported IP options are:

                        ssrr (strict source route), lsrr (loose source
                        route), rr (record packet route) and ts
                        (timestamp).  The absence of a particular option
                        may be denoted with a '!'.

                tcpoptions spec
                        Match if the TCP header contains the comma
                        separated list of options specified in spec.  The
                        supported TCP options are:

                        mss (maximum segment size), window (tcp window
                        advertisement), sack (selective ack), ts (rfc1323
                        timestamp) and cc (rfc1644 t/tcp connection
                        count).  The absence of a particular option may
                        be denoted with a '!'.

                established
                        TCP packets only.  Match packets that have the RST
                        or ACK bits set.

                setup   TCP packets only.  Match packets that have the SYN
                        bit set but no ACK bit.

                tcpflags spec
                        TCP packets only.  Match if the TCP header
                        contains the comma separated list of flags
                        specified in spec.  The supported TCP flags are:

                        fin, syn, rst, psh, ack and urg.  The absence of a
                        particular flag may be denoted with a '!'.  A rule
                        that contains a tcpflags specification can never
                        match a fragmented packet that has a non-zero
                        offset.  See the frag option for details on
                        matching fragmented packets.

                icmptypes types
                        ICMP packets only.  Match if the ICMP type is in
                        the list types.  The list may be specified as any
                        combination of ranges or individual types
                        separated by commas.  Both the numeric values and
                        the symbolic values listed below can be used.  The
                        supported ICMP types are:

                        echo reply (0), destination unreachable (3),
                        source quench (4), redirect (5), echo request
                        (8), router advertisement (9), router
                        solicitation (10), time-to-live exceeded (11), IP
                        header bad (12), timestamp request (13),
                        timestamp reply (14), information request (15),
                        information reply (16), address mask request (17)
                        and address mask reply (18).
    </pre>

       There is one kind of packet that the access device MUST always
       discard, that is an IP fragment with a fragment offset of one. This
       is a valid packet, but it only has one use, to try to circumvent
       firewalls.

          An access device that is unable to interpret or apply a deny rule
          MUST terminate the session.  An access device that is unable to
          interpret or apply a permit rule MAY apply a more restrictive
          rule.  An access device MAY apply deny rules of its own before the
          supplied rules, for example to protect the access device owner's
          infrastructure.

       The rule syntax is a modified subset of ipfw(8) from FreeBSD, and the
       ipfw.c code may provide a useful base for implementations.
   */
  public static final DiameterAvpType IP_FILTER_RULE = new DiameterAvpType(_IP_FILTER_RULE);
  
  /**
          The QosFilterRule format is derived from the OctetString AVP Base
          Format.  It uses the ASCII charset.  Packets may be marked or
          metered based on the following information that is associated with
          it:
    <pre>
             Direction                          (in or out)
             Source and destination IP address  (possibly masked)
             Protocol
             Source and destination port        (lists or ranges)
             DSCP values                        (no mask or range)
    </pre>

          Rules for the appropriate direction are evaluated in order, with
          the first matched rule terminating the evaluation.  Each packet is
          evaluated once.  If no rule matches, the packet is treated as best
          effort.  An access device that is unable to interpret or apply a
          QoS rule SHOULD NOT terminate the session.

       QoSFilterRule filters MUST follow the format:
    <pre>
       action dir proto from src to dst [options]

                    tag    - Mark packet with a specific DSCP
                             [DIFFSERV].  The DSCP option MUST be
                             included.
                    meter  - Meter traffic.  The metering options
                             MUST be included.

       dir          The format is as described under IPFilterRule.

                    proto        The format is as described under
                    IPFilterRule.

                    src and dst  The format is as described under
                    IPFilterRule.
    </pre>
   */
  public static final DiameterAvpType QOS_FILTER_RULE = new DiameterAvpType(_QOS_FILTER_RULE);

  private Object readResolve() throws StreamCorruptedException
  {
    try {
      return fromInt(type);
    }
    catch (IllegalArgumentException iae) {
      throw new StreamCorruptedException("Invalid internal state found:" + type);
    }
  }

  private final int type;
}
