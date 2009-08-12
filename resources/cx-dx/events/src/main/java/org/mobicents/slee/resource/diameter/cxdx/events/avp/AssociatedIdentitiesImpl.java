package org.mobicents.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 *
 * AssociatedIdentitiesImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class AssociatedIdentitiesImpl extends GroupedAvpImpl implements AssociatedIdentities {

  /**
   * 
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public AssociatedIdentitiesImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities#getUserNames()
   */
  public String[] getUserNames() {
    return (String[]) getAvpsAsUTF8String(DiameterAvpCodes.USER_NAME);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities#setUserName(java.lang.String)
   */
  public void setUserName(String userName) {
    addAvp(DiameterAvpCodes.USER_NAME, userName);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.events.avp.AssociatedIdentities#setUserNames(java.lang.String[])
   */
  public void setUserNames(String[] userNames) {
    for(String userName : userNames) {
      setUserName(userName);
    }
  }

}
