package org.jdiameter.server.impl;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Realm;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RealmImpl extends Realm {

    Collection<String> hosts = new ConcurrentLinkedQueue<String>();

    public RealmImpl(String name, ApplicationId applicationId, LocalAction localAction, boolean dynamic, long expirationTime, String... hs) {
        super(name, applicationId, localAction, dynamic, expirationTime);
        hosts.addAll(Arrays.asList(hs));
    }

    public String[] getPeerHosts() {
        return hosts.toArray(new String[0]);
    }

    public void addPeerName(String name) {
        hosts.add(name);
    }

    public void removePeerName(String s) {
         hosts.remove(name);
    }
}

