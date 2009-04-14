package net.java.slee.resource.diameter.ro;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.cca.events.CreditControlRequest;
import net.java.slee.resource.diameter.sh.client.MessageFactory;

/**
 * 
 * Used by applications to create Diameter Ro request messages.
 * Ro answer messages can be created using the RoServerSessionActivity.createRoCreditControlAnswer() method. 
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoMessageFactory extends MessageFactory {

  public static final long _RO_TGPP_VENDOR_ID = 10415L;
  public static final int  _RO_AUTH_APP_ID = 4;

  /**
   * Creates an empty Credit Control Request message.
   * 
   * @return
   */
  public CreditControlRequest createRoCreditControlRequest();
  
  /**
   * Creates a Credit Control Request message with the Session-Id AVP populated with the sessionId parameter.
   * 
   * @param sessionId
   * @return
   */
  public CreditControlRequest createRoCreditControlRequest(String sessionId);
  
  /**
   * Returns a reference to a message factory that can be used to create AVPs defined by the Diameter Base specification.
   * 
   * @return Base Diameter message factory
   */
  public DiameterMessageFactory getBaseMessageFactory();
  
}
