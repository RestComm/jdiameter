package org.mobicents.slee.resource.diameter.base.events.avp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;


import org.jdiameter.api.Avp;

public class IPFilterRuleAvpImpl extends DiameterAvpImpl implements
		IPFilterRuleAvp {
	//TODO: baranowb; Where is code for this AVP?
	private static int code=-1;
	
	public IPFilterRuleAvpImpl(String rule, long vendorId, boolean mandatory, boolean prt) {
		super(code, vendorId, mandatory?1:0, prt?1:0, rule.getBytes(), DiameterAvpType.OCTET_STRING);
		
        parseRule(rule);
    }

    public String toString() {
        return getRuleString();
    }

    public String getRuleString() {
        //StringBuffer ruleBuf = new StringBuffer();
        //ruleBuf.append(action == ACTION_PERMIT ? "permit ":"deny ");
        //ruleBuf.append(direction == DIR_IN ? "in ":"out ");
        //ruleBuf.append(isAnyProtocol() ? "ip" : String.valueOf(protocol));
        //ruleBuf.append(" from ");
       // sourceAddressSet.appendAddressSet(ruleBuf);
        //ruleBuf.append(" to ");
        //destAddressSet.appendAddressSet(ruleBuf);
        //ruleBuf.append(' ');
        //ruleBuf.append(fragment ? "frag ":"");
        //if(null != ipoptions) ruleBuf.append("ipoptions ").append(ipoptions).append(' ');
        //if(null != tcpoptions) ruleBuf.append("tcpoptions ").append(tcpoptions).append(' ');
        //ruleBuf.append(established ? "established ":"");
        //ruleBuf.append(setup ? "setup ":"");
        //if(null != tcpflags) ruleBuf.append("tcpflags ").append(tcpflags).append(' ');;
        //if(null != icmptypes) ruleBuf.append("icmptypes ").append(icmptypes);
       // return ruleBuf.toString();
    	return new String(super.stringValue());
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

    public static final int ACTION_PERMIT = 0;
    public static final int ACTION_DENY = 1;

    public static final int DIR_IN = 0;
    public static final int DIR_OUT = 1;

    private int action;
    private int direction;
    private boolean anyProtocol;
    private int protocol;

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
    
    private AddressSet sourceAddressSet;
    private AddressSet destAddressSet;

    private boolean fragment = false;
    private String ipoptions = null;
    private String tcpoptions = null;
    private boolean established = false;
    private boolean setup = false;
    private String tcpflags = null;
    private String icmptypes = null;


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
                //System.out.println(rule + " -> " + new IPFilterRuleAvpImpl(rule).toString());
            }
            catch (IllegalArgumentException iae) {
//                iae.printStackTrace();
                System.out.println(iae.getMessage());
            }
        }
    }
}
