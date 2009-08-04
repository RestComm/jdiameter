package org.jdiameter.client.impl;

import org.jdiameter.api.InternalException;

/**
 *  Stack MBean interface.
 */
public interface StackImplMBean {

  /**
   * Return string representation of stack instanceconfiguration
   * @return string representation of stack instance configuration
   */
  String configuration();

  /**
   * Return string representation of stack instance metadata
   * @return string representation of stack instance metadata
   */
  String metaData();

  /**
   * Reurn description (include state) of defined peer
   * @param name peer host name
   * @return description of defined peer
   */
  String peerDescription(String name);

  /**
   * Return list of peer
   * @return list of peer
   */
  String peerList();

  /**
   * Return true if stack is started
   * @return true if stack is started
   */
  boolean isActive();

  /**
   * Run stop procedure
   */
  void stop();

  /**
   * Run startd procedure
   * @throws org.jdiameter.api.IllegalDiameterStateException
   * @throws InternalException
   */
  void start()  throws org.jdiameter.api.IllegalDiameterStateException, InternalException;

}
