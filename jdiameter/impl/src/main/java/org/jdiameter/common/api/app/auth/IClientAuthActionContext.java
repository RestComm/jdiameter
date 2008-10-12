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
import org.jdiameter.api.Message;
import org.jdiameter.api.auth.ClientAuthSession;

public interface IClientAuthActionContext {

    long createAccessTimer() throws InternalException;

    void accessTimeoutElapses() throws InternalException;

    void disconnectUserOrDev(ClientAuthSession session, Message request) throws InternalException;
}
