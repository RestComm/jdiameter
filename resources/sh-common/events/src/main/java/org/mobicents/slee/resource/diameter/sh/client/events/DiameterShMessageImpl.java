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

import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.DiameterShMessage;
import net.java.slee.resource.diameter.sh.client.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvpImpl;
/**
 * 
 * Start time:15:44:42 2009-05-23<br>
 * Project: diameter-parent<br>
 * Implementation of common methods for Sh messages.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class DiameterShMessageImpl extends DiameterMessageImpl implements DiameterShMessage {

  protected String longMessageName = null;
  protected String shortMessageName = null;

  public DiameterShMessageImpl(Message msg)
  {
    super(msg);
  }

  @Override
  public String getLongName()
  {
    return this.longMessageName;
  }

  @Override
  public String getShortName()
  {
    return this.shortMessageName;
  }

  public AuthSessionStateType getAuthSessionState()
  {
    return (AuthSessionStateType) getAvpAsEnumerated(Avp.AUTH_SESSION_STATE, AuthSessionStateType.class);
  }

  public SupportedFeaturesAvp[] getSupportedFeatureses()
  {
    return (SupportedFeaturesAvp[]) getAvpsAsCustom(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  public boolean hasAuthSessionState()
  {
    return hasAvp(DiameterAvpCodes.AUTH_SESSION_STATE);
  }

  public void setAuthSessionState(AuthSessionStateType authSessionState)
  {
    addAvp(DiameterAvpCodes.AUTH_SESSION_STATE, (long)authSessionState.getValue());
  }

  public void setSupportedFeatures(SupportedFeaturesAvp supportedFeatures)
  {
    addAvp(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, supportedFeatures.byteArrayValue());
  }

  public void setSupportedFeatureses(SupportedFeaturesAvp[] supportedFeatureses)
  {
    for(SupportedFeaturesAvp supportedFeatures : supportedFeatureses) {
      setSupportedFeatures(supportedFeatures);
    }
  }

  public SupportedFeaturesAvp getSupportedFeatures()
  {
    return (SupportedFeaturesAvp) getAvpAsCustom(DiameterShAvpCodes.SUPPORTED_FEATURES, DiameterShAvpCodes.SH_VENDOR_ID, SupportedFeaturesAvpImpl.class);
  }

  
  //some hack
	private transient Object data = null;

	public void setData(Object d) {
		this.data = d;
	}

	public Object removeData() {
		Object o = this.data;
		this.data = null;
		return o;

	}

	public Object getData() {

		return this.data;

	}

  
}
