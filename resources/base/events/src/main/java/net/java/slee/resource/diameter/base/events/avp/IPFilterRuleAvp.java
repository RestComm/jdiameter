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
public class IPFilterRuleAvp {

  public static final int ACTION_PERMIT = 0;
  public static final int ACTION_DENY = 1;

  public static final int DIR_IN = 0;
  public static final int DIR_OUT = 1;

  private int action;
  private int direction;
  private boolean anyProtocol;
  private int protocol;

  private AddressSet sourceAddressSet;
  private AddressSet destAddressSet;

  private boolean fragment = false;
  private String ipoptions = null;
  private String tcpoptions = null;
  private boolean established = false;
  private boolean setup = false;
  private String tcpflags = null;
  private String icmptypes = null;

  public IPFilterRuleAvp(String rule) {
    parseRule(rule);
  }

  public String toString() {
    return getRuleString();
  }

  public String getRuleString() {
    StringBuffer ruleBuf = new StringBuffer();
    ruleBuf.append(action == ACTION_PERMIT ? "permit ":"deny ");
    ruleBuf.append(direction == DIR_IN ? "in ":"out ");
    ruleBuf.append(isAnyProtocol() ? "ip" : String.valueOf(protocol));
    ruleBuf.append(" from ");
    sourceAddressSet.appendAddressSet(ruleBuf);
    
    ruleBuf.append(" to ");
    destAddressSet.appendAddressSet(ruleBuf);
    
    ruleBuf.append(' ');
    ruleBuf.append(fragment ? "frag ":"");
    if(ipoptions != null) {
      ruleBuf.append("ipoptions ").append(ipoptions).append(' ');
    }
    if(tcpoptions != null) {
      ruleBuf.append("tcpoptions ").append(tcpoptions).append(' ');
    }
    ruleBuf.append(established ? "established ":"");
    ruleBuf.append(setup ? "setup ":"");
    if(tcpflags != null) {
      ruleBuf.append("tcpflags ").append(tcpflags).append(' ');;
    }
    if(icmptypes != null) {
      ruleBuf.append("icmptypes ").append(icmptypes);
    }
    return ruleBuf.toString();
  }

  public int getAction() {
    return action;
  }

  public int getDirection() {
    return direction;
  }

  public boolean isAnyProtocol() {
    return anyProtocol;
  }

  public int getProtocol() {
    return protocol;
  }

  public String getSourceIp() {
    return sourceAddressSet.ip;
  }

  public int getSourceBits() {
    return sourceAddressSet.bits;
  }
  public boolean isSourceAssignedIps() {
    return sourceAddressSet.assignedIps;
  }

  public int[][] getSourcePorts() {
    return sourceAddressSet.ports;
  }

  public String getDestIp() {
    return destAddressSet.ip;
  }

  public int getDestBits() {
    return destAddressSet.bits;
  }

  public boolean isDestAssignedIps() {
    return destAddressSet.assignedIps;
  }

  public int[][] getDestPorts() {
    return destAddressSet.ports;
  }

  public boolean isFragment() {
    return fragment;
  }

  public String[] getIpOptions() {
    return ipoptions.split(",");
  }

  public String[] getTcpOptions() {
    return tcpoptions.split(",");
  }

  public boolean isEstablised() {
    return established;
  }

  public boolean isSetup() {
    return setup;
  }

  public String[] getTcpFlags() {
    return tcpflags.split(",");
  }

  public String[] getIcmpTypes() {
    return icmptypes.split(",");
  }

  public int[] getNumericIcmpTypes() {
    return new int[0];
  }


  private void parseRule(String rule) {

    // TODO: ipoptions, tcpoptions, tcpflags, icmpflags
    // TODO: ipv6 addresses

    Pattern ruleParser = Pattern.compile("(.+)\\s+(.+)\\s+(.+)\\s+from\\s+(.+)\\s+to\\s+([^a-z]+)\\s+(.+)");

    Matcher matcher = ruleParser.matcher(rule.trim());

    if(matcher.matches()) {
      parseAction(matcher.group(1), rule);
      parseDirection(matcher.group(2), rule); 
      parseProtocol(matcher.group(3), rule);
      parseFrom(matcher.group(4), rule);
      parseTo(matcher.group(5), rule);
      parseOptions(matcher.group(6), rule);
    }
    else { 
      fail(rule);
    }        
  }

  private void parseProtocol(String proto, String rule) {
    if("ip".equals(proto)) {
      anyProtocol = true;
    }
    else {
      try {
        protocol = Integer.parseInt(proto);
      }
      catch (NumberFormatException nfe) {
        fail(rule, proto);
      }
    }
  }

  private void parseDirection(String dir, String rule) {
    if("in".equals(dir)) {
      direction = DIR_IN;
    }
    else if("out".equals(dir)) {
      direction = DIR_OUT;
    }
    else fail(rule, dir);
  }

  private void parseAction(String action, String rule) {
    if("permit".equals(action)) {
      this.action = ACTION_PERMIT;
    }
    else if("deny".equals(action)) {
      this.action  = ACTION_DENY;
    }
    else fail(rule, action);
  }

  private void parseFrom(String from, String rule) {
    sourceAddressSet = parseAddressSet(from, rule);
  }

  private void parseTo(String to, String rule) {
    destAddressSet = parseAddressSet(to, rule);
  }

  private AddressSet parseAddressSet(String addressSetString, String rule) {
    AddressSet addressSet = new AddressSet();
    Pattern ipv4Pattern = Pattern.compile("(!?)(assigned|[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})(/[0-9]{1,3})?( [0-9,-]*)?");
    Matcher matcher = ipv4Pattern.matcher(addressSetString);
    if(matcher.matches()) {
      addressSet.notMatch = "!".equals(matcher.group(1));
      if("assigned".equals(matcher.group(2))) {
        addressSet.assignedIps = true;
      }
      else {
        addressSet.ip = matcher.group(2);
        if(null != matcher.group(3)) {
          try {
            addressSet.bits = Integer.parseInt(matcher.group(3).substring(1));
          }
          catch (NumberFormatException nfe) {
            fail(rule, matcher.group(3));
          }
        }
      }
      // {port | port-port}[,ports[,...]]
      if(null != matcher.group(4)) {
        String portsString = matcher.group(4).trim();
        String ports[] = portsString.split(",");
        addressSet.ports = new int[ports.length][2];
        for (int i = 0; i < ports.length; i++) {
          String port = ports[i];
          String[] ranges = port.split("-");
          addressSet.ports[i][0] = Integer.parseInt(ranges[0]);
          try {
            if(ranges.length == 1) {
              addressSet.ports[i][1] = addressSet.ports[i][0];
            }
            else if(ranges.length == 2) {
              addressSet.ports[i][1] = Integer.parseInt(ranges[1]);
            }
            else fail(rule, portsString);
          }
          catch (NumberFormatException e) {
            fail(rule, portsString);
          }
        }
      }
    }
    else fail(rule, addressSetString);
    return addressSet;
  }


  private void parseOptions(String options, String rule) {
    if(options.length() > 0) {
      Pattern optionsSplitter = Pattern.compile("\\s+");
      String[] optionsArray = optionsSplitter.split(options);
      for (int i = 0; i < optionsArray.length; i++) {
        String option = optionsArray[i];
        if("frag".equals(option)) fragment = true;
        else if("ipoptions".equals(option)) ipoptions = optionsArray[++i];
        else if("tcpoptions".equals(option)) tcpoptions = optionsArray[++i];
        else if("established".equals(option)) established = true;
        else if("setup".equals(option)) setup = true;
        else if("tcpflags".equals(option)) tcpflags = optionsArray[++i];
        else if("icmptypes".equals(option)) icmptypes = optionsArray[++i];
        else fail(rule, option);
      }
    }
  }

  private void fail(String rule, String error) {
    throw new IllegalArgumentException("Could not parse rule \"" + rule + "\", failed at: \"" + error + "\"");
  }

  private void fail(String rule) {
    throw new IllegalArgumentException("Could not parse rule \"" + rule + "\", failed to match.");
  }

  private class AddressSet {
    private void appendAddressSet(StringBuffer asBuf) {
      if(notMatch) asBuf.append('!');
      if(assignedIps) {
        asBuf.append("assigned");
      }
      else {
        asBuf.append(ip);
        if(0 <= bits) asBuf.append('/').append(bits);
      }
      if(null != ports) { 
        asBuf.append(' ');
        for (int i = 0; i < ports.length; i++) {
          int[] sourcePort = ports[i];
          asBuf.append(sourcePort[0]);
          if(sourcePort[1] != sourcePort[0]) asBuf.append('-').append(sourcePort[1]);
          if(i != ports.length-1) asBuf.append(',');
        }
      }
    }
    private String ip;
    private int bits = -1;
    private int[][] ports;
    private boolean assignedIps = false;
    private boolean notMatch = false;
  }

  // tests
  public static void main(String[] args) {
    String rules[] = {
        "permit in ip from 192.168.0.0/24 10,11,12,20-30 to 192.168.1.1 99 frag established",
        "permit out 2 from 192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss",
        "permit out 2 from !192.1.0.0/24 to 192.1.1.1/0 frag established setup tcpoptions mrss",
        "permit out 2 from assigned 34 to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss ipoptions !rr,!ts",
        "deny in ip from !assigned to 192.1.1.1/0 6,3 frag established setup tcpoptions mrss",
        "deny out udp from 192.168.0.0 to 192.168.1.1 established",
        "permit in 9999 from 192.168.0.0/24 to 192.168.1.1 frag foo"
    };
    for (int i = 0; i < rules.length; i++) {
      String rule = rules[i];
      try {
        System.out.println(rule + " -> " + new IPFilterRuleAvp(rule).toString());
      }
      catch (IllegalArgumentException iae) {
        System.out.println(iae.getMessage());
      }
    }
  }

}
