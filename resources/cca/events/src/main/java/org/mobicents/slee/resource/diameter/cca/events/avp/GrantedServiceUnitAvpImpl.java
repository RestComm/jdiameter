package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.cca.events.avp.GrantedServiceUnitAvp;

/**
 * Start time:15:04:05 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link GrantedServiceUnitAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class GrantedServiceUnitAvpImpl extends ServiceUnitAvpTypeImpl implements GrantedServiceUnitAvp {

	public GrantedServiceUnitAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
		super(code, vendorId, mnd, prt, value);
	}

}
