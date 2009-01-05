package org.jdiameter.common.impl.app.sh;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Request;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.common.impl.app.AppRequestEventImpl;

public class ProfileUpdateRequestImpl extends AppRequestEventImpl implements ProfileUpdateRequest {
	


    public ProfileUpdateRequestImpl(Request request) {
        super(request);
    }
}
