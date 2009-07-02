/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

import java.util.List;

/**
 * This interface introduces a capability to work with a network.
 * You can get instance of this interface over stack instance:
 * <code>
 * if (stack.isWrapperFor(Network.class)) {
 *       Network netWork = stack.unwrap(Network.class);
 * .....
 * }
 * </code>
 * @version 1.5.1 Final
 */

public interface Network extends Wrapper {

    /**
     * Return local peer network statistics
     * @return network statistics
     */
    Statistic getStatistic();

    /**
     * Register listener for processing network requests
     * @param applicationId application Id
     * @param listener request listener
     * @throws ApplicationAlreadyUseException  if listener with predefined appId already append to network
     */
    void addNetworkReqListener(NetworkReqListener listener, ApplicationId... applicationId) throws ApplicationAlreadyUseException;

    /**
     * Register listener for processing network requests
     * @param selector application selector
     * @param listener request listener
     */
    void addNetworkReqListener(NetworkReqListener listener, Selector<Message, ApplicationId>... selector);
    
    /**
     * Remove request listener
     * @param applicationId application id of listener
     */
    void removeNetworkReqListener(ApplicationId... applicationId);

    /**
     * Remove request listener
     * @param selector selector of application
     */
    void removeNetworkReqListener(Selector<Message, ApplicationId>... selector);

}

