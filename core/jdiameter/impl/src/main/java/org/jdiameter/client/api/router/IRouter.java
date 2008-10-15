/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.router;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.RouteException;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;

/**
 * This class describe Router functionality
 */
public interface IRouter  {

    /**
     * Return peer from inner peer table by predefined pameters
     * @param message message with routed avps
     * @param manager instance of peer manager
     * @return peer instance
     * @throws RouteException
     * @throws AvpDataException
     */
    IPeer getPeer(IMessage message, IPeerTable manager) throws RouteException, AvpDataException;

    /**
     * Return realm of peer by fqdn
     * @param fqdn host name
     * @return realm of peer
     */
    String getRealmForPeer(String fqdn);

    /**
     * Register route information by received request. This information will be used
     * during answer routing.
     * @param request request
     */
    void registerRequestRouteInfo(IMessage request);

    /**
     * Return Request route info
     * @param hopByHopIndentifier hop By Hop Indentifier
     * @return Array (host and realm)
     */
    String[] getRequestRouteInfo(long hopByHopIndentifier);

    /**
     * Update redirect information
     * @param answer redirect answer message
     * @throws InternalException
     * @throws RouteException
     */
    void updateRedirectInformation(IMessage answer) throws InternalException, RouteException;

    /**
     * Start inner time facilities
     */
    void start();

    /**
     * Stop inner time facilities
     */
    void stop();

    /**
     * Release all resources
     */
    void destroy();

}
