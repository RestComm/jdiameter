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
package org.mobicents.slee.resource.diameter.base.events.avp;

import static org.jdiameter.api.Avp.PROXY_HOST;
import static org.jdiameter.api.Avp.PROXY_STATE;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>12:52:27 PM May 08, 2008 
 * <br>
 *
 * Implementation of AVP: {@link ProxyInfoAvp}
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ProxyInfoAvpImpl extends GroupedAvpImpl implements ProxyInfoAvp {

  public ProxyInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
    name = "Proxy-Info-Avp";
  }

  public boolean hasProxyHost() {
    return hasAvp(PROXY_HOST);
  }

  public DiameterIdentityAvp getProxyHost() {
    return getAvpAsIdentity(PROXY_HOST);
  }

  public void setProxyHost(DiameterIdentityAvp proxyHost) {
    addAvp(PROXY_HOST, proxyHost.toString());
  }

  public boolean hasProxyState() {
    return hasAvp(PROXY_STATE);
  }

  public byte[] getProxyState() {
    return getAvpAsRaw(PROXY_STATE);
  }

  public void setProxyState(byte[] proxyState) {
    addAvp(PROXY_STATE, byteArrayValue());
  }
}
