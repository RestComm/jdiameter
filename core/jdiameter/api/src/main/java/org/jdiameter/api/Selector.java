package org.jdiameter.api;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
public interface Selector<T, A> {

  /**
   * Return true if rule is true
   * @param object check object
   * @return true if rule is true
   */
  boolean checkRule(T object);

  /**
   * Return metainformation object
   * @return  metainformation object
   */
  A getMetaData();
}