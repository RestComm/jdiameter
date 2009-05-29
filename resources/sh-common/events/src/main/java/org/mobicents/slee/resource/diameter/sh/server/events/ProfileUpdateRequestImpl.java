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
package org.mobicents.slee.resource.diameter.sh.server.events;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.sh.client.events.DiameterShMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.UserIdentityAvpImpl;

/**
 * 
 * Start time:10:23:07 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of {@link ProfileUpdateRequest} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ProfileUpdateRequestImpl extends DiameterShMessageImpl implements ProfileUpdateRequest {

  public ProfileUpdateRequestImpl(Message msg) {
    super(msg);

    msg.setRequest(true);

    super.longMessageName = "Profile-Update-Request";
    super.shortMessageName = "PUR";
  }

  public DataReferenceType getDataReference() {
    return (DataReferenceType) getAvpAsEnumerated(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID, DataReferenceType.class);
  }

  public UserIdentityAvp getUserIdentity() {
    return (UserIdentityAvp) getAvpAsCustom(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, UserIdentityAvpImpl.class);
  }

  public boolean hasDataReference() {
    return hasAvp(DiameterShAvpCodes.DATA_REFERENCE);
  }

  public boolean hasUserData() {
    return hasAvp(DiameterShAvpCodes.USER_DATA);
  }

  public boolean hasUserIdentity() {
    return hasAvp(DiameterShAvpCodes.USER_IDENTITY);
  }

  public void setDataReference(DataReferenceType dataReference) {
    addAvp(DiameterShAvpCodes.DATA_REFERENCE, DiameterShAvpCodes.SH_VENDOR_ID, (long)dataReference.getValue());
  }

  public void setUserIdentity(UserIdentityAvp userIdentity)
  {
    addAvp(DiameterShAvpCodes.USER_IDENTITY, DiameterShAvpCodes.SH_VENDOR_ID, userIdentity.byteArrayValue());
  }

  public String getUserData() {
    return getAvpAsOctetString(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID);
  }

  public void setUserData(String userData) {
    addAvp(DiameterShAvpCodes.USER_DATA, DiameterShAvpCodes.SH_VENDOR_ID, userData);
  }

}
