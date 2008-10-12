/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app;


public interface IAppSessionState<T> {

    int getValue();

    T fromInt(int val) throws IllegalArgumentException;
}
