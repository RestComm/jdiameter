package org.jdiameter.server.impl;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.MetaData;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IRouter;

/**
 * Just a simple counterpart of failure aware router defined for a client role.
 */
public class FailureAwareRouter extends org.jdiameter.client.impl.router.FailureAwareRouter implements IRouter {

  /**
   * Parameterized constructor. Should be called by any subclasses.
   */
  public FailureAwareRouter(IContainer container, IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData aMetaData) {
    super(container, concurrentFactory, realmTable, config, aMetaData);
  }

}
