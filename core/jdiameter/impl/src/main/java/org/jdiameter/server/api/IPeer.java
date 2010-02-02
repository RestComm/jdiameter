/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api;

import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.io.IConnection;

/**
 * This interface describe extends methods of base class
 */
public interface IPeer extends org.jdiameter.client.api.controller.IPeer {

  /**
   * Return true if peer must start reconnect procedure
   * 
   * @return true if peer must start reconnect procedure
   */
  boolean isAttemptConnection();

  /**
   * Return action context
   * 
   * @return action context
   */
  IContext getContext();

  /**
   * Return peer connection
   * 
   * @return peer connection
   */
  IConnection getConnection();

  /**
   * Add new network connection (wait CER/CEA)
   * 
   * @param conn new network connection
   */
  void addIncomingConnection(IConnection conn);

  /**
   * Set result of election
   * 
   * @param isElection result of election
   */
  void setElection(boolean isElection);

  /**
   * Set overload manager
   * 
   * @param ovrManager overload manager
   */
  void notifyOvrManager(IOverloadManager ovrManager);
}
