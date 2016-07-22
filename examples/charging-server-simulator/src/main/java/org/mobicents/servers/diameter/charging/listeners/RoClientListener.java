package org.mobicents.servers.diameter.charging.listeners;

public interface RoClientListener {

  /**
   * Callback method for successful request for credit units.
   *
   * @param amount the amount of granted units
   * @param finalUnits true if these are the last units
   * @throws Exception
   */
  void creditGranted(long amount, boolean finalUnits) throws Exception;

  /**
   * Callback method for unsuccessful request for credit units.
   *
   * @param failureCode the code specifying why it failed
   * @throws Exception
   */
  void creditDenied(int failureCode) throws Exception;

  /**
   * Callback method for end of credit. Service should be terminated.
   *
   * @throws Exception
   */
  void creditTerminated() throws Exception;

}
