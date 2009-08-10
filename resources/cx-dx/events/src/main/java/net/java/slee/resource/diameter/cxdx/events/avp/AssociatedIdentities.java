package net.java.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 * <b>6.3.33  Associated-Identities AVP</b>
 * The Associated-Identities AVP is of type Grouped and it contains the private user identities
 * associated to an IMS subscription.
 * 
 * AVP format
 * Associated-Identities ::= < AVP header: 632, 10415 >
 *                       *[ User-Name ]
 *                       *[ AVP ]
 * 
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface AssociatedIdentities extends GroupedAvp {

  /**
   * Returns the value of the User-Name AVP, of type UTF8String.
   * @return the value of the User-Name AVP or null if it has not been set on this message
   */
  String[] getUserNames();

  /**
   * Sets the value of the User-Name AVP, of type UTF8String.
   * @throws IllegalStateException if setUserName has already been called
   */
  void setUserName(String userName);

  /**
   * Sets the value of the User-Name AVP, of type UTF8String.
   * @throws IllegalStateException if setUserName has already been called
   */
  void setUserNames(String[] userNames);

}
