/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * The interface that every stack class must implement. The Java Diameter framework allows for multiple database stacks.
 * Each stack should supply a class that implements the Stack interface.
 * The StackManager will try to load as many stacks.
 * It is strongly recommended that each Stack class should be small and standalone.
 * When a Stack class is loaded, it should create an instance of itself and register it with the StackManager.
 * This means that a user can load and register a stack by calling
 * Class.forName("org.jdiameter.impl.Stack")
 *
 * Wrapper interface allows adapt message to any driver vendor specific interface
 * <br>
 * Life cycle state machine for stack
 * <P align="center"><img src="../../../../../../images/stack_fsm.PNG" width="347" height="363"><P>
 *
 * Stack must supported following wrapper classes:
 * - Client API : PeerManager
 * - Server API : PeerManager, OverloadManager (Network, PeerManagerWrapper is optional)
 * @version 1.5.1 Final
 */

public interface Stack extends Wrapper {

    /**
     * Configuration stack and allocation system resources.
     * @param config Object with configuration parameters
     * @return instance of session factory (DataSource equals)
     * @throws IllegalDiameterStateException if a stack already configured or destroed
     * @throws InternalException if a stack can not processing initial procedure
     */
    SessionFactory init(Configuration config) throws IllegalDiameterStateException, InternalException;

    /**
     * Start activity of stack (Thread and Network connections), not
     * waiting swith ANY peer to OKEY state
     * @throws IllegalDiameterStateException if a stack is not confgured or stopped
     * @throws InternalException if a stack can not processing start procedure
     */
    void start() throws IllegalDiameterStateException, InternalException;

    /**
     * Start activity of stack (Thread and Network connections),
     * waiting specified wait time swith peers to OKEY state.
     * @param  mode specified type of wait procedure
     * @param  timeout how long to wait before giving up, in units of unit
     * @param  unit a TimeUnit determining how to interpret the timeout parameter
     * @throws IllegalDiameterStateException if a stack is not confgured or stopped
     * @throws InternalException if a stack can not processing start procedure
     */
    void start(Mode mode, long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException;

    /**
     * Stop any activity of stack (Thread and Network connections),
     * waiting if necessary up to the specified wait time swith peers to DOWN state.
     * @param timeout how long to wait before giving up, in units of unit
     * @param unit a TimeUnit determining how to interpret the timeout parameter
     * @throws IllegalDiameterStateException if a stack is not started
     * @throws InternalException if a stack can not processing start procedure
     */
    void stop(long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException;

    /**
     * Destroy any resource append to this instacne of stack
     */
    void destroy();

    /**
     * @return true is stack is running.
     */
    boolean isActive();

    /**
     * Return logger instance. You can set your logger handler and
     * processing logger alarms in application.
     * @return logger interface
     */
    Logger getLogger();

    /**
     * Return SessionFactory instance
     * @return SessionFactory instance
     * @throws IllegalDiameterStateException if stack is not configure
     */
    SessionFactory getSessionFactory()  throws IllegalDiameterStateException;

    /**
     * @return stack meta information
     */
    MetaData getMetaData();
}
