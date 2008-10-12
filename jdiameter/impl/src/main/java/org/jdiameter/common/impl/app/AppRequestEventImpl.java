package org.jdiameter.common.impl.app;

import static org.jdiameter.api.Avp.DESTINATION_HOST;
import static org.jdiameter.api.Avp.DESTINATION_REALM;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppRequestEvent;

public class AppRequestEventImpl extends AppEventImpl implements AppRequestEvent {

    public AppRequestEventImpl(Message message) {
        super(message);
    }

    public String getDestinationHost() throws AvpDataException {
        if ( message.getAvps().getAvp(DESTINATION_HOST) != null )
            return message.getAvps().getAvp(DESTINATION_HOST).getOctetString();
        else
            throw new AvpDataException("Avp DESTINATION_HOST not found");
    }

    public String getDestinationRealm() throws AvpDataException {
        if ( message.getAvps().getAvp(DESTINATION_REALM) != null )
            return message.getAvps().getAvp(DESTINATION_REALM).getOctetString();
        else
            throw new AvpDataException("Avp DESTINATION_REALM not found");
    }
}
