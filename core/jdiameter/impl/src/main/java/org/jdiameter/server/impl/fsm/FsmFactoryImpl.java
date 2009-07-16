package org.jdiameter.server.impl.fsm;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.client.api.fsm.ExecutorFactory;

import java.util.concurrent.Executor;

public class FsmFactoryImpl implements IFsmFactory {

    public IStateMachine createInstanceFsm(IContext context, ExecutorFactory executor, Configuration config) throws InternalException {
        return new PeerFSMImpl(context, executor, config);
    }
}
