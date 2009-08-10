package net.java.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 * <b>6.3.16  Deregistration-Reason AVP</b>
 * The Deregistration-Reason AVP is of type Grouped, and indicates the reason for a de-registration
 * operation.
 * 
 * AVP format
 * Deregistration-Reason :: = < AVP Header : 615 10415 >
 *                        { Reason-Code }
 *                        [ Reason-Info ]
 *                      * [ AVP ]
 * 
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface DeregistrationReason extends GroupedAvp {

  /**
   * Returns true if the Reason-Code AVP is present in the message.
   */
  boolean hasReasonCode();

  /**
   * Returns the value of the Reason-Code AVP, of type Enumerated.
   * @return the value of the Reason-Code AVP or null if it has not been set on this message
   */
  ReasonCode getReasonCode();

  /**
   * Sets the value of the Reason-Code AVP, of type Enumerated.
   * @throws IllegalStateException if setReasonCode has already been called
   */
  void setReasonCode(ReasonCode reasonCode);

  /**
   * Returns true if the Reason-Info AVP is present in the message.
   */
  boolean hasReasonInfo();

  /**
   * Returns the value of the Reason-Info AVP, of type UTF8String.
   * @return the value of the Reason-Info AVP or null if it has not been set on this message
   */
  String getReasonInfo();

  /**
   * Sets the value of the Reason-Info AVP, of type UTF8String.
   * @throws IllegalStateException if setReasonInfo has already been called
   */
  void setReasonInfo(String reasonInfo);

}
