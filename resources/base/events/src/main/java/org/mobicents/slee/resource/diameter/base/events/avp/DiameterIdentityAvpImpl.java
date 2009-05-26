/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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


import net.java.slee.resource.diameter.base.events.avp.*;

/**
 * 
 * Super project:  mobicents
 * 12:53:13 2008-05-08	
 * @author <a href="mailto:bbaranow@redhat.com">baranowb - Bartosz Baranowski </a>
 */
public class DiameterIdentityAvpImpl extends DiameterAvpImpl implements DiameterIdentityAvp {
	
	public DiameterIdentityAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, value, DiameterAvpType.OCTET_STRING);
		super.type=DiameterAvpType.OCTET_STRING;
		//FIXME:baranowb; how can we get name?
		//super.name=NameDecoder.decode(code);
		
	}

	public byte[] byteArrayValue() {
		//FIXME: baranowb; 1. is this operation allowable, 2. if so should we return opy of array?
		//throw new UnsupportedOperationException ("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
		return super.byteArrayValue();
	}

	public double doubleValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public float floatValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}


	public int intValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public long longValue() {
		throw new UnsupportedOperationException("Diameter Identity AVP of type "+type.toString()+" doesnt allow this operation!!!");
	}

	public String stringValue() {
		return super.stringValue();
	}

	public Object clone()
	{
		byte[] b=new byte[super.byteArrayValue().length];
		System.arraycopy(super.byteArrayValue(), 0, b, 0, b.length);
		DiameterIdentityAvpImpl clone=new DiameterIdentityAvpImpl(code,vendorId,mnd,prt,b);
		return clone;
	}
}
