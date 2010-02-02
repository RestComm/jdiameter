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
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.fsm.IFsmFactory;
import org.jdiameter.client.api.fsm.IStateMachine;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.statistic.IStatisticFactory;

public class FsmFactoryImpl implements IFsmFactory { // TODO: please redesign this code "duplicate"

  protected IStatisticFactory statisticFactory;

  public FsmFactoryImpl(IStatisticFactory statisticFactory) {
    this.statisticFactory = statisticFactory;
  }

  public IStateMachine createInstanceFsm(IContext context, IConcurrentFactory concurrentFactory, Configuration config) throws InternalException {
    return new org.jdiameter.client.impl.fsm.PeerFSMImpl(context, concurrentFactory, config, statisticFactory);
  }
}
