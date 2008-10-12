/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

/**
 * A Diameter Request is a request from a client to a server (or server to client - network request).
 * @version 1.5.1 Final
 */

public interface Request extends Message {

    /**
     * @return true if it is network request
     */
    boolean isNetworkRequest();

    /**
     * Creates a response for this request with the specifies result code.
     * Header and system avps from request has copy to answer.
     * @param resultCode result code of answer
     * @return answer object instance
     */
    Answer createAnswer(long resultCode);

    /**
     * Creates a response for this request with the specifies experement result code.
     * Header and system avps from request has copy to answer.
     * @param vendorId vendorId
     * @param experementalResultCode experement result code of answer
     * @return answer object instance
     */
    Answer createAnswer(long vendorId, long experementalResultCode);
}
