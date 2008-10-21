package org.mobicents.diameter.api;

import org.jdiameter.api.Message;

public interface DiameterProvider
{
  /**
   * Sends a Diameter Message through the provider. Creates a new session if the 
   * Session-Id AVP is not present and returns the id of the session.
   * 
   * @param message
   * @return 
   */
  public String sendMessage(Message message);
  
  public Message sendMessageSync(Message message);
}
