package org.mobicents.diameter.stack.base;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;
import static org.jdiameter.client.impl.helpers.Parameters.OwnIPAddress;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.OwnVendorID;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;

import org.jdiameter.client.impl.helpers.EmptyConfiguration;

/**
 * Class representing the Diameter Test Framework Configuration  
 */
public class StackConfig extends EmptyConfiguration {

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
  
  private static String serverHost = "127.0.0.1";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
  
  private static String realmName = "mobicents.org";

  public StackConfig() {
    super();
    
    add(Assembler, Assembler.defValue());
    add(OwnDiameterURI, clientURI);
    add(OwnIPAddress, "127.0.0.1");
    add(OwnRealm, realmName);
    add(OwnVendorID, 193L);
    // Set Ericsson SDK feature
    //add(UseUriAsFqdn, true);
    // Set Common Applications
    add(ApplicationId,
        // AppId 1
        getInstance().
        add(VendorId,   193L).
        add(AuthApplId, 0L).
        add(AcctApplId, 19302L)
    );
    // Set peer table
    add(PeerTable,
        // Peer 1
        getInstance().
        add(PeerRating, 1).
        add(PeerName, serverURI));
    // Set realm table
    add(RealmTable,
        // Realm 1
        getInstance().
        add(RealmEntry, realmName + ":" + clientHost + "," + serverHost)
    );
  }
}
