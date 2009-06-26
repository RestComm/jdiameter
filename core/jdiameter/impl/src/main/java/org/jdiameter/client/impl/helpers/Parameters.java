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

/**
 * This enumeration defined all parameters of diameter stack implementation
 */
public class Parameters extends Ordinal {

    protected static int index;

    private static ArrayList<Parameters> value = new ArrayList<Parameters>();

    /**
     * Class name of IOC  property
     */
    public static final Parameters Assembler = new Parameters("Assembler", String.class, "org.jdiameter.client.impl.helpers.AssemblerImpl");
    /**
     * Ckass name of connection interface implementation property
     */
    public static final Parameters ConnectionImplClass = new Parameters("ConnectionImplClas",String.class, "org.jdiameter.client.impl.transport.tcp.TCPClientConnection");

    /**
     * Local peer URI property
     */
    public static final Parameters OwnDiameterURI = new Parameters("OwnDiameterURI",String.class, "aaa://localhost:1812");
    /**
     * Local peer ip address property
     */
    public static final Parameters OwnIPAddress = new Parameters("OwnIPAddress",String.class, "");
    /**
     * Local peer realm name property
     */
    public static final Parameters OwnRealm = new Parameters("OwnRealm",String.class, "local");
    /**
     * Local peer vendor id  property
     */
    public static final Parameters OwnVendorID = new Parameters("OwnVendorID",Long.class, 0L);
    /**
     * Local peer stack product name property
     */
    public static final Parameters OwnProductName = new Parameters("OwnProductName",String.class, "jDiameter");
    /**
     * Local peer stack firmware version property
     */
    public static final Parameters OwnFirmwareRevision = new Parameters("OwnFirmwareRevision",Long.class, 0L);

    /**
     * Task executor task queue size  property 
     */
    public static final Parameters QueueSize = new Parameters("QueueSize",Integer.class,   10000);

    /**
     * Message time out  property
      */
    public static final Parameters MessageTimeOut = new Parameters("MessageTimeOut",Long.class, 60000L);
    /**
     * Stop stack time out  property
     */
    public static final Parameters StopTimeOut = new Parameters("StopTimeOut",Long.class,    10000L);

    /**
     * CEA command time out property
     */
    public static final Parameters CeaTimeOut = new Parameters("CeaTimeOut",Long.class, 10000L);
    /**
     * Peer inactive time out property
     */
    public static final Parameters IacTimeOut = new Parameters("IacTimeOut",Long.class, 20000L);
    /**
     * DWA command time out property
     */
    public static final Parameters DwaTimeOut = new Parameters("DwaTimeOut",Long.class, 10000L);
    /**
     * DPA command time out property
     */
    public static final Parameters DpaTimeOut = new Parameters("DpaTimeOut",Long.class, 5000L);
    /**
     * Reconnect time out property
     */
    public static final Parameters RecTimeOut = new Parameters("RecTimeOut",Long.class, 10000L);

    /**
     * Orig_host avp set as URI into CER message
     */
    public static final Parameters UseUriAsFqdn = new Parameters("UseUriAsFqdn",Boolean.class, false);    

    /**
     * Peer name property
     */
    public static final Parameters PeerName = new Parameters("PeerName",String.class, "");

    /**
     * Peer ip property
     */
    public static final Parameters PeerIp = new Parameters("PeerIp",String.class, "");

    /**
     * Peer local peer port range (format: 1345-1346) property
     */
    public static final Parameters PeerLocalPortRange = new Parameters("PeerLocalPortRange",String.class, "");
    
    /**
     * Peer rating property
     */
    public static final Parameters PeerRating = new Parameters("PeerRating",Integer.class, 0);
    /**
     *  Peer ptoperty
     */
    public static final Parameters Peer = new Parameters("Peer",Object.class);

    /**
     * Real entry property
      */
    public static final Parameters RealmEntry = new Parameters("RealmEntry",String.class, "");
    /**
     * Realm property
     */
    public static final Parameters Realm = new Parameters("Realm",Object.class);

    /**
     * Vendor id property
      */
    public static final Parameters VendorId = new Parameters("VendorId", Long.class);
    /**
     * Authentication application id property
     */
    public static final Parameters AuthApplId = new Parameters("AuthApplId", Long.class);
    /**
     * Accounting application id property
     */
    public static final Parameters AcctApplId = new Parameters("AcctApplId",Long.class);

    /**
     * Application Id property
     */
    public static final Parameters ApplicationId = new Parameters("ApplicationId",Object.class);

    /**
     * Extendion point property
     */
    public static final Parameters Extensions = new Parameters("Extensions",Object.class);
    /**
     * Extension point name property
     */
    public static final Parameters ExtensioinName = new Parameters("ExtensioinName",String.class);

    /**
     * Peer list property
     */
    public static final Parameters PeerTable = new Parameters("PeerTable",Object.class);

    /**
     * Realm list property
     */
    public static final Parameters RealmTable = new Parameters("RealmTable",Object.class);

    /**
     * Security list property
     */
    public static final Parameters Security = new Parameters("Security",Object.class);

    /**
     * Security entry
     */
    public static final Parameters SecurityData = new Parameters("SecurityData",Object.class);

    /**
     * Security data name
     */
    public static final Parameters SDName = new Parameters("SDName",String.class);    

    /**
     * Security protocol
     */
    public static final Parameters SDProtocol = new Parameters("SDProtocol",String.class,"TLS");

    /**
     * Security session creation flag
     */
    public static final Parameters SDEnableSessionCreation = new Parameters("SDEnableSessionCreation",Boolean.class, false);

    /**
     * Security client mode flag
     */
    public static final Parameters SDUseClientMode= new Parameters("SDUseClientMode",Boolean.class, false);

    /**
     * Cipher suites separated by ','
     */
    public static final Parameters CipherSuites = new Parameters("CipherSuites",String.class);

    /**
     * Key data
     */
    public static final Parameters KeyData = new Parameters("KeyData",String.class);

    /**
     * Key manager
     */
    public static final Parameters KDManager = new Parameters("KDManager",String.class);

    /**
     * Key store
     */
    public static final Parameters KDStore = new Parameters("KDStore",String.class);

    /**
     * Key file
     */
    public static final Parameters KDFile = new Parameters("KDFile",String.class);

    /**
     * Key password
     */
    public static final Parameters KDPwd = new Parameters("KDPwd",String.class);


    /**
     * Trust data
     */
    public static final Parameters TrustData = new Parameters("TrustData",String.class);    

    /**
     * Key manager
     */
    public static final Parameters TDManager = new Parameters("TDManager",String.class);

    /**
     * Key store
     */
    public static final Parameters TDStore = new Parameters("TDStore",String.class);

    /**
     * Key file
     */
    public static final Parameters TDFile = new Parameters("TDFile",String.class);

    /**
     * Key password
     */
    public static final Parameters TDPwd = new Parameters("TDPwd",String.class);

    /**
     * Reference to security information
     */
    public static final Parameters SecurityRef = new Parameters("SecurityRef",String.class);

    /**
     * Return all parameters as iterator
     * @return all parameters as iterator
     */
    public static Iterable<Parameters> values(){
        return value;
    }

    private Class type;
    private Object defValue;

    protected Parameters(String name, Class type) {
        this.name = name;
        this.type = type;
        ordinal = index++;
        value.add(this);
    }

    protected Parameters(String name, Class type, Object defValue) {
        this.name = name;
        this.type = type;
        this.defValue = defValue;
        ordinal = index++;
        value.add(this);
    }

    /**
     * Return default value of property
     * @return default value of property
     */
    public Object defValue() {
        return defValue;
    }

    /**
     * Return type of property
     * @return type of property
     */
    public Class type() {
        return type;
    }
}
