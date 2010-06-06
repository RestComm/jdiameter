package org.jdiameter.common.impl.app;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppAnswerEvent;

public class AppAnswerEventImpl extends AppEventImpl implements AppAnswerEvent {

  private static final long serialVersionUID = 1L;

  public AppAnswerEventImpl(Message message) {
    super(message);
  }

  public Avp getResultCodeAvp() throws AvpDataException {
    Avp resultCodeAvp = message.getAvps().getAvp(Avp.RESULT_CODE);
    return resultCodeAvp != null ? resultCodeAvp : message.getAvps().getAvp(Avp.EXPERIMENTAL_RESULT_CODE);
  }
}
