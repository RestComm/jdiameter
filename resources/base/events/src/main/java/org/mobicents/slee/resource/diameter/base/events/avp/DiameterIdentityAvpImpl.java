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
