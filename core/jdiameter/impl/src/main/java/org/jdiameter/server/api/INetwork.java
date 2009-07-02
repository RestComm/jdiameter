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

import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.client.api.IMessage;

/**
 * This interface append to base interface some
 * special methods.
 */
public interface INetwork extends Network {


  /**
   * Return NetworkListener instance for specified application-id
   * @param message message
   * @return  NetworkListener instance for specified selector
   * @see org.jdiameter.api.NetworkReqListener
   */
  NetworkReqListener getListener(IMessage message);

  /**
   * This method set peer manager for addPeer/remPeer methods
   * @param manager PeerTable instance
   */
  void setPeerManager(IMutablePeerTable manager);

}
