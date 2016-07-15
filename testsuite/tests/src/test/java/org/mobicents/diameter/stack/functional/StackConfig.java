/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional;

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
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;

import org.jdiameter.client.impl.helpers.EmptyConfiguration;

/**
 * Class representing the Diameter Test Framework Configuration
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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
    //    add(RealmTable,
    //        // Realm 1
    //        getInstance().
    //        add(RealmEntry, realmName + ":" + clientHost + "," + serverHost)
    //    );
  }
}
