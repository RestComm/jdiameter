package org.jdiameter.api.annotation;

/**
 * This enumerated class describe all flags of message
 */
public enum CommandFlag {
  /**
   * Request flag
   */
  R,
  /**
   * Proxiable flag
   */
  P,
  /**
   * Error flag
   */
  E,
  /**
   * Re-transmitted flag
   */
  T,
}