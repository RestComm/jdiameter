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

import org.jdiameter.client.api.io.IConnection;

/**
 * This interface allow notifies consumers about created connections
 */
public interface INetworkConnectionListener {

  /**
   * Invoked when an new connection created.
   * 
   * @param connection created connections
   */
  public void newNetworkConnection(IConnection connection);

}