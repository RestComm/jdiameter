/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.api.io;

/**
 * This interface describe INetWorkConnectionListener consumer
 */
public interface INetworkGuard {

  /**
   * Append new listener
   * 
   * @param listener listener instance
   */
  public void addListener(INetworkConnectionListener listener);

  /**
   * Remove listener
   * 
   * @param listener listener instance
   */
  public void remListener(INetworkConnectionListener listener);

  /**
   * Release all attached resources (socket and etc)
   */
  public void destroy();

}