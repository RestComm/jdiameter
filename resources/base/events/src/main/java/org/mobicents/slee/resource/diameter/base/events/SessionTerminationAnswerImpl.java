package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.SessionTerminationAnswer;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;

public class SessionTerminationAnswerImpl extends ExtensionDiameterMessageImpl implements SessionTerminationAnswer
{

  private Logger logger = Logger.getLogger(SessionTerminationAnswerImpl.class);

  @Override
  public String getLongName()
  {
    return "Session-Termination-Answer";
  }

  @Override
  public String getShortName()
  {
    return "STA";
  }

  public SessionTerminationAnswerImpl(Message message)
  {
    super(message);
  }

  public byte[][] getClassAvps()
  {
    AvpSet s = message.getAvps().getAvps(Avp.CLASS);

    byte[][] rc = new byte[s.size()][];

    for (int i = 0; i < s.size(); i++)
    {
      try 
      {
        rc[i] = s.getAvpByIndex(i).getRaw();
      }
      catch (Exception e) {
        logger.error("Unable to obtain/decode AVP (code:" + Avp.CLASS + ")", e);
      }
    }

    return rc;
  }

  public void setClassAvp(byte[] classAvp)
  {
    message.getAvps().addAvp(25, classAvp, true, false);
  }

  public void setClassAvps(byte[][] classAvps)
  {
    for (byte[] i : classAvps)
    {
      setClassAvp(i);
    }
  }

}
