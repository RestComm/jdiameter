/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.fsm;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;

import java.util.concurrent.Executor;

/**
 * Peer FSM factory
 */
public interface IFsmFactory {

    /**
     * Create instance of Peer FSM
     * @param context fsm context object
     * @param executor executor facility
     * @param config configuration
     * @return State machine instance
     * @throws InternalException
     */
    IStateMachine createInstanceFsm(IContext context, ExecutorFactory executor, Configuration config) throws InternalException;
    
}
