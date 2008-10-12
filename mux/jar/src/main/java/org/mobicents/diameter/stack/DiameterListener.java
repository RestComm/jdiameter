package org.mobicents.diameter.stack;

import java.io.Serializable;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;

public interface DiameterListener extends NetworkReqListener, Serializable, EventListener<Request, Answer>
{

}
