/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl.helpers;

import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * This enumeration containce all logger usage in JDiameter stack implementation
 */
public class Loggers extends Ordinal {

    protected static int index;

    private static ArrayList<Loggers> value = new ArrayList<Loggers>();

    /**
     * Logs the stack lifecycle
     */
    public static final Loggers Stack = new Loggers("Stack", null ,"Logs the stack lifecycle");
    /**
     * Logs the peers
     */
    public static final Loggers Peer = new Loggers("Peer", "peer","Logs the peers");
    /**
     * Logs the peer manager subsystem
     */
    public static final Loggers PeerTable = new Loggers("PeerTable", "peertable","Logs the peer table subsystem");
    /**
     * Logs the peers fsm
     */
    public static final Loggers FSM = new Loggers("FSM", "peer.fsm","Logs the peers fsm");
    /**
     * Logs the message parser
     */
    public static final Loggers Parser = new Loggers("Parser", "parser","Logs the message parser");
    /**
     * Logs the avp opetations processing
     */
    public static final Loggers AVP = new Loggers("AVP", "parser.avp","Logs the avp opetations processing");
    /**
     *  Logs the message opetations/lifecycle processing
     */
    public static final Loggers Message = new Loggers("Message", "parser.message","Logs the message opetations/lifecycle processing");
    /**
     * Logs the message router subsystem
     */
    public static final Loggers Router = new Loggers("Router", "router","Logs the message router subsystem");
    /**
     * Logs the transport(tcp) opetations processing
     */
    public static final Loggers Transport = new Loggers("Transport", "TCPTransport","Logs the transport(tcp) opetations processing");

    /**
     * Return Iterator of all entries
     * @return  Iterator of all entries
     */    
    public static Iterable<Loggers> values(){
        return value;
    }

    private String description;
    private String fullName;

    protected Loggers(String name, String fullName, String desc) {
        this.name = name;
        if (fullName == null)
            this.fullName    = "jDiameter";
        else
            this.fullName    = "jDiameter." + fullName;
        this.description = desc;
        ordinal = index++;
        value.add(this);
    }

    /**
     * Return full name of logger
     *
     * @return full name of logger
     */
    public String fullName() {
        return fullName;
    }

    /**
     * Return description of logger
     *
     * @return description of logger
     */
    public String description() {
        return description;
    }

    /**
     * Return logger instance
     *
     * @return logger instance
     */
    public Logger logger() {
        return Logger.getLogger(fullName);
    }
}
