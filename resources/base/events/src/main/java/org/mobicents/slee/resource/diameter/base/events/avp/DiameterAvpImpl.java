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

import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;

import org.apache.log4j.Logger;
import org.jdiameter.client.impl.parser.MessageParser;

/**
 * 
 * Super project: mobicents 12:51:53 <br>
 * 2008-05-08 Base class for all AVP classes.<br>
 * Implementation of {@link DiameterAvp}
 * 
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public class DiameterAvpImpl implements DiameterAvp {

	protected final Logger log = Logger.getLogger(DiameterAvpImpl.class);

	protected long vendorId;
	protected int code, mnd, prt;
	protected String name = "undefined";
	// FIXME: baranowb; isnt this wrong?
	protected DiameterAvpType type = DiameterAvpType.DIAMETER_IDENTITY;
	protected MessageParser parser = new MessageParser(null);

	private byte[] value;

	public DiameterAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value, DiameterAvpType type) {
		this.code = code;
		this.vendorId = vendorId;
		this.mnd = mnd;
		this.prt = prt;
		this.value = value;
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public long getVendorId() {
		return vendorId;
	}

	public String getName() {
		return name;
	}

	public DiameterAvpType getType() {
		return type;
	}

	public int getMandatoryRule() {
		return mnd;
	}

	public int getProtectedRule() {
		return prt;
	}

	public double doubleValue() {
		try {
			return parser.bytesToDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public float floatValue() {
		try {
			return parser.bytesToFloat(value);
		} catch (Exception e) {
			log.debug(e);
			return 0;
		}
	}

	public int intValue() {
		try {
			return parser.bytesToInt(value);
		} catch (Exception e) {
			log.debug(e);
			return 0;
		}
	}

	public long longValue() {
		try {
			return parser.bytesToLong(value);
		} catch (Exception e) {
			log.debug(e);
			return 0;
		}
	}

	public String stringValue() {
		try {
			return parser.bytesToUtf8String(value);
		} catch (Exception e) {
			log.debug(e);
			return null;
		}
	}

	public byte[] byteArrayValue() {
		return value;
	}

	public Object clone() {
		return new DiameterAvpImpl(code, vendorId, mnd, prt, value, type);
	}

	@Override
	public String toString() {
		return "DiameterAVP[Vendor[" + this.vendorId + "], Code[" + this.code + "], " + "Name[" + this.name + "], Type[" + this.type + "], Mandatory[" + this.mnd + "], " + "Protected[" + this.prt
				+ "], Value[" + this.value + "]]";
	}

	protected void reportAvpFetchError(String msg, long code) {
		log.error("Failed to fetch avp, code: " + code + ". Message: " + msg);
	}

	public String octetStringValue() {
		try {
			return parser.bytesToOctetString(value);
		} catch (Exception e) {
			log.debug(e);
			return null;
		}
	}

}
