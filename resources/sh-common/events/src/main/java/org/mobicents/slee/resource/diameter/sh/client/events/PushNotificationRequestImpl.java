/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.slee.resource.diameter.sh.client.events;

import net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 * 
 * Start time:12:40:46 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link PushNotificationRequest} interface.
 * 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class PushNotificationRequestImpl extends DiameterShMessageImpl implements PushNotificationRequest {

  /**
   * 
   * @param msg
   */
  public PushNotificationRequestImpl(Message msg) {
    super(msg);
    msg.setRequest(true);
    super.longMessageName = "Push-Notification-Request";
    super.shortMessageName = "PNR";
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#getUserIdentity()
   */
  public UserIdentityAvp getUserIdentity() {
    return (UserIdentityAvp) getAvpAsCustom(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, UserIdentityAvpImpl.class);
  }
  
  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#hasUserIdentity()
   */
  public boolean hasUserIdentity() {
    return hasAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#setUserIdentity(net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp)
   */
  public void setUserIdentity(UserIdentityAvp userIdentity) {
    addAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, userIdentity.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#hasUserData()
   */
  public boolean hasUserData() {
    return hasAvp(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#getUserData()
   */
  public String getUserData() {
    return getAvpAsOctetString(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.sh.client.events.PushNotificationRequest#setUserData(java.lang.String)
   */
  public void setUserData(String userData) {
    addAvp(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID, userData);
  }

}
