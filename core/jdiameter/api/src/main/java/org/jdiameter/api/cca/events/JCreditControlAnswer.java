package org.jdiameter.api.cca.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * 
 * JCreditControlAnswer.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 3:45:53 PM Dec 2, 2008 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public interface JCreditControlAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "CCA";
  public static final String _LONG_NAME = "Credit-Control-Answer";

  public static final int code = 272;

  boolean hasAcctMultiSessionId();

  String getAcctMultiSessionId() throws AvpDataException;

  void setAcctMultiSessionId(String val);

  long getAuthApplicationId() throws AvpDataException;

  boolean hasAuthApplicationId();

  void setAuthApplicationId(long val);

  long getCCRequestNumber() throws AvpDataException;

  boolean hasCCRequestNumber();

  void setCCRequestNumber(long val);

  int getCCRequestType() throws AvpDataException;

  boolean hasCCRequestType();

  void setCCRequestType(int val);

  long getCCSubSessionId() throws AvpDataException;

  boolean hasCCSubSessionId();

  void setCCSubSessionId(long val);

  int getCheckBalanceResult() throws AvpDataException;

  boolean hasCheckBalanceResult();

  void setCheckBalanceResult(int val);

  int getCCFailureHandling() throws AvpDataException;

  boolean hasCCFailureHandling();

  void setCCFailureHandling(int val);

  int getDDFailureHandling() throws AvpDataException;

  boolean hasDDFailureHandling();

  void setDDFailureHandling(int val);

  boolean hasEventTimestamp();

  String getEventTimestamp() throws AvpDataException;

  void setEventTimestamp(String val);

  long getOriginStateId() throws AvpDataException;

  boolean hasOriginStateId();

  void setOriginStateId(long val);

  boolean hasUserName();

  String getUserName() throws AvpDataException;

  void setUserName(String val);

  //unsigned32
  boolean hasValidityTime();

  long getValidityTime() throws AvpDataException;

  void setValidityTime(long val);

}
