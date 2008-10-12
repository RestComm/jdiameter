/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.impl.app;

import static org.jdiameter.api.Avp.ORIGIN_HOST;
import static org.jdiameter.api.Avp.ORIGIN_REALM;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppEvent;


public class AppEventImpl implements AppEvent {

    protected Message message;

    public AppEventImpl(Message message) {
        this.message = message;
    }

    public int getCommandCode() {
        return message.getCommandCode();
    }

    public Message getMessage() throws InternalException {
        return message;
    }

    public String getOriginHost() throws AvpDataException {
        if ( message.getAvps().getAvp(ORIGIN_HOST) != null )
            return message.getAvps().getAvp(ORIGIN_HOST).getOctetString();
        else
            throw new AvpDataException("Avp ORIGIN_HOST not found");
    }

    public String getOriginRealm() throws AvpDataException {
        if ( message.getAvps().getAvp(ORIGIN_REALM) != null )
            return message.getAvps().getAvp(ORIGIN_REALM).getOctetString();
        else
            throw new AvpDataException("Avp ORIGIN_REALM not found");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEventImpl that = (AppEventImpl) o;

        if (!message.equals(that.message)) return false;

        return true;
    }

    public int hashCode() {
        return message.hashCode();
    }

    public String toString() {
        return message != null ? message.toString() : "empty";
    }
}
