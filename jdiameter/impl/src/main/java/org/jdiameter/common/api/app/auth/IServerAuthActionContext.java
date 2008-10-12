/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.app.auth;

import org.jdiameter.api.InternalException;
import org.jdiameter.api.auth.ServerAuthSession;

public interface IServerAuthActionContext {

    long createAccessTimer() throws InternalException;

    void accessTimeoutElapses(ServerAuthSession session) throws InternalException;
}