package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * 
 * AssociatedRegisteredIdentitiesImpl.java
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AssociatedRegisteredIdentitiesImpl extends GroupedAvpImpl implements AssociatedRegisteredIdentities {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public AssociatedRegisteredIdentitiesImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities#getUserNames()
   */
  public String[] getUserNames() {
    return (String[]) getAvpsAsUTF8String(DiameterAvpCodes.USER_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities#setUserName(java.lang.String)
   */
  public void setUserName(String userName) {
    addAvp(DiameterAvpCodes.USER_NAME, userName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedRegisteredIdentities#setUserNames(java.lang.String[])
   */
  public void setUserNames(String[] userNames) {
    for(String userName : userNames) {
      setUserName(userName);
    }
  }

}
