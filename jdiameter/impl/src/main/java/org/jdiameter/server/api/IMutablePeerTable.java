/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api;

import org.jdiameter.api.MutablePeerTable;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.controller.IPeerTable;

/**
 * This interface describe extends methods of base class
 */
public interface IMutablePeerTable extends MutablePeerTable, IPeerTable {


    /**
     * Check message on duplicate
     * @param request checked message
     * @return true if messahe has duplicate into storage
     */
    public IMessage isDuplicate(IMessage request);

    /**
     * Save message to duplicate storage
     * @param key key of message
     * @param answer message
     */
    public void saveToDuplicate(String key, IMessage answer);

    /**
     * Return instance of session factory
     * @return instance of session factory
     */
    ISessionFactory getSessionFactory();
}
