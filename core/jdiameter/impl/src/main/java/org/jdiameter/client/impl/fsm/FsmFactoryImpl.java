package org.jdiameter.client.impl.fsm;

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
import org.jdiameter.client.api.fsm.ExecutorFactory;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.fsm.IStateMachine;

public class FsmFactoryImpl implements IFsmFactory { // TODO: please redesign this code "duplicate"

  public IStateMachine createInstanceFsm(IContext context, ExecutorFactory executor, Configuration config) throws InternalException {
    return new PeerFSMImpl(context, executor, config);
  }
}
