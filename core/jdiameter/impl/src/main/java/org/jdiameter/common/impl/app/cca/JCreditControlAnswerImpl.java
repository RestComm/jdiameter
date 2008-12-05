package org.jdiameter.common.impl.app.cca;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.cca.events.JCreditControlAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;

/**
 * 
 * JCreditControlAnswerImpl.java
 *
 * <br>Super project:  mobicents
 * <br>5:04:44 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class JCreditControlAnswerImpl extends AppAnswerEventImpl implements JCreditControlAnswer {

  public JCreditControlAnswerImpl(Request message, long resultCode)
  {
    super(message.createAnswer(resultCode));
    // TODO Auto-generated constructor stub
  }

  //public JCreditControlAnswerImpl(Request message, long resultCode, long vendorId) {
  //	super(message.createAnswer(vendorId,resultCode));
  //
  //}

  public JCreditControlAnswerImpl(Answer message )
  {
    super(message);
  }

  public int getCredidControlFailureHandlingAVPValue()
  {
    if(isCreditControlFailureHandlingAVPPresent())
    {
      try
      {
        return super.message.getAvps().getAvp(427).getInteger32();
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return -1;
  }

  public int getDirectDebitingFailureHandlingAVPValue()
  {
    if(isDirectDebitingFailureHandlingAVPPresent())
    {
      try
      {
        return super.message.getAvps().getAvp(428).getInteger32();
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return -1;
  }

  public boolean isCreditControlFailureHandlingAVPPresent()
  {
    return super.message.getAvps().getAvp(427)!=null;
  }

  public boolean isDirectDebitingFailureHandlingAVPPresent()
  {
    return super.message.getAvps().getAvp(428)!=null;
  }

  public int getRequestedActionAVPValue()
  {
    if(isRequestActionAVPPresent())
    {
      try
      {
        return super.message.getAvps().getAvp(436).getInteger32();
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return -1;
  }

  public boolean isRequestActionAVPPresent()
  {
    return super.message.getAvps().getAvp(436)!=null;
  }

  public int getRequestTypeAVPValue()
  {
    if(isRequestTypeAVPPresent())
    {
      try
      {
        return super.message.getAvps().getAvp(416).getInteger32();
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return -1;

  }

  public Avp getValidityTimeAvp()
  {
    return super.message.getAvps().getAvp(448);
  }

  public boolean isRequestTypeAVPPresent()
  {
    return super.message.getAvps().getAvp(416)!=null;
  }

}
