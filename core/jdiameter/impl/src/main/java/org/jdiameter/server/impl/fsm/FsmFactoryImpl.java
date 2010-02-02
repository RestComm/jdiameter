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
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.server.api.IFsmFactory;
import org.jdiameter.server.api.IStateMachine;

public class FsmFactoryImpl extends org.jdiameter.client.impl.fsm.FsmFactoryImpl implements IFsmFactory {

  public FsmFactoryImpl(IStatisticFactory statisticFactory) {
    super(statisticFactory);
  }

  public IStateMachine createInstanceFsm(IContext context, IConcurrentFactory concurrentFactory, Configuration config) throws InternalException {
    return new org.jdiameter.server.impl.fsm.PeerFSMImpl(context, concurrentFactory, config, statisticFactory);
  }
}
