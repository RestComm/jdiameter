/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.statistic;

/**
 * This interface describe extends methods of base class
 */
public interface IStatisticRecord {

  /**
   * Increment counter
   */
  void inc();

  /**
   * Increment counter
   */
  void inc(long value);

  /**
   * Decrement counter
   */
  void dec();

  /**
   * Set value of statistic
   *
   * @param value new value of record
   */
  void setLongValue(long value);

  /**
   * Set value of statistic
   *
   * @param value new value of record
   */
  void setDoubleValue(double value);

  /**
   * Enable/Disable counter
   *
   * @param e on/off parameter
   */
  public void enable(boolean e);

  /**
   * ValueHolder for external statistics
   */
  public static interface ValueHolder {
    String getValueAsString();
  }

  public static interface IntegerValueHolder extends ValueHolder {
    /**
     * Return value of counter as integer
     *
     * @return value of counter
     */
    int getValueAsInt();
  }

  public static interface LongValueHolder extends ValueHolder {
    /**
     * Return value of counter as long
     *
     * @return value of counter
     */
    long getValueAsLong();
  }

  public static interface DoubleValueHolder extends ValueHolder {

    /**
     * Return value of counter as double
     *
     * @return value of counter
     */
    double getValueAsDouble();
  }

  //===========================

  /**
   * Return name of counter
   *
   * @return name of counter
   */
  String getName();

  /**
   * Retrurn description of counter
   *
   * @return description of counter
   */
  String getDescription();

  /**
   * Return value of counter as integer
   *
   * @return value of counter
   */
  int getValueAsInt();

  /**
   * Return value of counter as double
   *
   * @return value of counter
   */
  double getValueAsDouble();

  /**
   * Return value of counter as long
   *
   * @return value of counter
   */
  long getValueAsLong();

  /**
   * Return code of counter
   *
   * @return code of counter
   */
  int getType();

  /**
   * Return childs counters
   *
   * @return array of childs countres
   */
  IStatisticRecord[] getChilds();

  /**
   * Reset counter and all child counters
   */
  void reset();
}
