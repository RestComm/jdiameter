package net.java.slee.resource.diameter.base.events.avp;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Java class to represent the Diameter IPFilterRule AVP type.
 *<P>
 * The IPFilterRule format is derived from the OctetString AVP Base Format.  It uses the ASCII charset.
 * Packets may be filtered based on the following information that is associated with it.

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
 <p/>
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
 <p/>
   An access device that is unable to interpret or apply a deny rule
   MUST terminate the session.  An access device that is unable to
   interpret or apply a permit rule MAY apply a more restrictive
   rule.  An access device MAY apply deny rules of its own before the
   supplied rules, for example to protect the access device owner's
   infrastructure.
 <p/>
   The rule syntax is a modified subset of ipfw(8) from FreeBSD.

* @author Open Cloud
*/

public interface IPFilterRuleAvp {

	public int getAction() ;

    public int getDirection() ;

    public boolean isAnyProtocol() ;

    public int getProtocol() ;

    public String getSourceIp() ;

    public int getSourceBits() ;
    public boolean isSourceAssignedIps() ;

    public int[][] getSourcePorts() ;

    public String getDestIp() ;

    public int getDestBits() ;

    public boolean isDestAssignedIps() ;

    public int[][] getDestPorts() ;
    public boolean isFragment() ;

    public String[] getIpOptions() ;

    public String[] getTcpOptions();

    public boolean isEstablised() ;

    public boolean isSetup() ;

    public String[] getTcpFlags() ;

    public String[] getIcmpTypes() ;

    public int[] getNumericIcmpTypes() ;
    
}
