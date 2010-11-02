/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api.app;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;

/**
 * Basic class for application specific answer event (Sx, Rx, Gx)
 * @version 1.5.1 Final
 */
public interface AppAnswerEvent extends AppEvent {

  /**
   * Return result code (or experimental if present) AVP of answer message
   * @return result code (or experimental if present) AVP of answer message
   * @throws AvpDataException if result code avp absent
   */
  Avp getResultCodeAvp() throws AvpDataException;

  /**
   * Return result code value of answer message
   * @return result code of answer message
   * @throws AvpDataException if result code avp absent
   */
  int getResultCode() throws AvpDataException;

  public void setResultCode(int code);

  public boolean hasResultCode();

  /**
   * Return experimental result grouped avp of answer message
   * @return experimental result grouped avp of answer message
   * @throws AvpDataException if experimental result avp absent
   */
  public AvpSet getExperimentalResult() throws AvpDataException;

  public boolean hasExperimentalResult();

  public void setExperimentalResult(long vendor, int code);

}
