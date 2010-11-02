package org.jdiameter.api.cca.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * 
 * JCreditControlRequest.java
 * 
 * <br>
 * Super project: mobicents <br>
 * 3:46:49 PM Dec 2, 2008 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public interface JCreditControlRequest extends AppRequestEvent {

  public static final String _SHORT_NAME = "CCR";
  public static final String _LONG_NAME = "Credit-Control-Request";

  public static final int code = 272;

  boolean hasAcctMultiSessionId();

  String getAcctMultiSessionId() throws AvpDataException;

  void setAcctMultiSessionId(String val);

  long getAuthApplicationId() throws AvpDataException;

  boolean hasAuthApplicationId();

  void setAuthApplicationId(long val);

  String getCCCorrelationId() throws AvpDataException;

  boolean hasCCCorrelationId();

  void setCCCorrelationId(String val);

  long getCCRequestNumber() throws AvpDataException;

  boolean hasCCRequestNumber();

  void setCCRequestNumber(long val);

  int getCCRequestType() throws AvpDataException;

  boolean hasCCRequestType();

  void setCCRequestType(int val);

  long getCCSubSessionId() throws AvpDataException;

  boolean hasCCSubSessionId();

  void setCCSubSessionId(long val);

  boolean hasEventTimestamp();

  String getEventTimestamp() throws AvpDataException;

  void setEventTimestamp(String val);

  long getOriginStateId() throws AvpDataException;

  boolean hasOriginStateId();

  void setOriginStateId(long val);

  int getMultiServicesIndicator() throws AvpDataException;

  boolean hasMultiServicesIndicator();

  void setMultiServicesIndicator(int val);

  int getRequestedAction() throws AvpDataException;

  boolean hasRequestedAction();

  void setRequestedAction(int val);

  long getServiceId() throws AvpDataException;

  boolean hasServiceId();

  void setServiceId(long val);

  String getServiceContextId() throws AvpDataException;

  boolean hasServiceContextId();

  void setServiceContextId(String val);

  boolean hasTerminationCause();

  int getTerminationCause() throws AvpDataException;

  void setTerminationCause(int val);

  boolean hasUserName();

  String getUserName() throws AvpDataException;

  void setUserName(String val);

}
