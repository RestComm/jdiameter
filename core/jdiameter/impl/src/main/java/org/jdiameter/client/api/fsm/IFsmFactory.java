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
import org.jdiameter.common.api.concurrent.IConcurrentFactory;

/**
 * Peer FSM factory
 */
public interface IFsmFactory {

  /**
   * Create instance of Peer FSM
   * 
   * @param context FSM context object
   * @param concurrentFactory executor facility
   * @param config configuration
   * @return State machine instance
   * @throws InternalException
   */
  IStateMachine createInstanceFsm(IContext context, IConcurrentFactory concurrentFactory, Configuration config) throws InternalException;    
}
