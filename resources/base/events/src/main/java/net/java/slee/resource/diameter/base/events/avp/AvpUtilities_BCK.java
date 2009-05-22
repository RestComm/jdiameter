/**
 * Start time:13:11:26 2008-11-12<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package net.java.slee.resource.diameter.base.events.avp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Start time:13:11:26 2008-11-12<br>
 * Project: mobicents-diameter-parent<br>
 * This class contains some handy methods. It requires avp dictionary to be
 * loaded
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpUtilities_BCK {

	private static boolean _AVP_REMOVAL_ALLOWED = false;
	static {
		// addd init

	}

	public static boolean isAvpRemoveAllowed() {
		return _AVP_REMOVAL_ALLOWED;
	}

	public static boolean hasAvp(int avpCode, long vendorId, AvpSet set) {
		AvpSet inner = set.getAvps(avpCode, vendorId);
		if (inner.getAvp(avpCode, vendorId) != null) {
			return true;
		} else if (set.getAvp(avpCode, vendorId) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param avpCode
	 * @param vendorId
	 * @param set
	 */
	private static void performPreAddOperations(int avpCode, long vendorId, AvpSet set) {
		if (hasAvp(avpCode, vendorId, set) && !isAvpRemoveAllowed()) {
			throw new IllegalStateException("AVP is already present in message and cannot be overwritten.");
		} else {
			set.removeAvp(avpCode);
		}

	}

	public static void setAvpAsString(int avpCode, boolean isOctetString, AvpSet set, String... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsString(avpCode, rep.getVendorId(), isOctetString, set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsString(avpCode, 0, isOctetString, set, true, false, values);
		}
	}

	public static void setAvpAsString(int avpCode, long vendorId, boolean isOctetString, AvpSet set, String... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsString(avpCode, rep.getVendorId(), isOctetString, set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsString(avpCode, vendorId, isOctetString, set, true, false, values);
		}
	}

	public static void setAvpAsString(int avpCode, long vendorId, boolean isOctetString, AvpSet set, boolean isMandatory, boolean isProtected, String... values) {

		performPreAddOperations(avpCode, vendorId, set);
		for (String s : values)
			set.addAvp(avpCode, s, vendorId, isMandatory, isProtected, isOctetString);
	}

	public static void setAvpAsUInt32(int avpCode, AvpSet set, boolean remove, long... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsUInt32(avpCode, rep.getVendorId(), set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsUInt32(avpCode, 0, set, remove, true, false, values);
		}

	}

	public static void setAvpAsUInt32(int avpCode, long vendorId, AvpSet set, boolean remove, long... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsUInt32(avpCode, vendorId, set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsUInt32(avpCode, vendorId, set, remove, true, false, values);
		}
	}

	public static void setAvpAsUInt32(int avpCode, long vendorId, AvpSet set, boolean remove, boolean isMandatory, boolean isProtected, long... values) {
		performPreAddOperations(avpCode, vendorId, set);
		for (long l : values)
			set.addAvp(avpCode, l, vendorId, isMandatory, isProtected, true);

	}

	public static void setAvpAsUInt64(int avpCode, AvpSet set, boolean remove, long... values) {

		// FIXME: whats the diff with UInt32 setter
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsUInt64(avpCode, rep.getVendorId(), set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsUInt64(avpCode, 0, set, remove, true, false, values);
		}
	}

	public static void setAvpAsUInt64(int avpCode, long vendorId, AvpSet set, boolean remove, long... values) {

		// FIXME: whats the diff with UInt32 setter
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsUInt64(avpCode, vendorId, set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsUInt64(avpCode, vendorId, set, remove, true, false, values);
		}

	}

	public static void setAvpAsUInt64(int avpCode, long vendorId, AvpSet set, boolean remove, boolean isMandatory, boolean isProtected, long... values) {
		performPreAddOperations(avpCode, vendorId, set);
		for (long l : values)
			set.addAvp(avpCode, l, vendorId, isMandatory, isProtected, true);

	}

	public static void setAvpAsInt32(int avpCode, int value, AvpSet set, boolean remove, int... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsInt32(avpCode, rep.getVendorId(), set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsInt32(avpCode, 0, set, remove, true, false, values);
		}

	}

	public static void setAvpAsInt32(int avpCode, long vendorId, AvpSet set, boolean remove, int... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsInt32(avpCode, vendorId, set, remove, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsInt32(avpCode, vendorId, set, remove, true, false, values);
		}

	}

	public static void setAvpAsInt32(int avpCode, long vendorId, AvpSet set, boolean remove, boolean isMandatory, boolean isProtected, int... values) {
		performPreAddOperations(avpCode, vendorId, set);
		for (int l : values)
			set.addAvp(avpCode, l, vendorId, isMandatory, isProtected, true);

	}

	public static void setAvpAsInt64(int avpCode, AvpSet set, long... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsInt64(avpCode, rep.getVendorId(), set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsInt64(avpCode, 0, set, true, false, values);
		}
	}

	public static void setAvpAsInt64(int avpCode, long vendorId, AvpSet set, long... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsInt64(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsInt64(avpCode, vendorId, set, true, false, values);
		}
	}

	public static void setAvpAsInt64(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, long... values) {

		performPreAddOperations(avpCode, vendorId, set);
		for (long l : values)
			set.addAvp(avpCode, l, vendorId, isMandatory, isProtected, true);
	}

	public static void setAvpAsFloat32(int avpCode, AvpSet set, float... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsFloat32(avpCode, rep.getVendorId(), set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsFloat32(avpCode, 0, set, true, false, values);
		}

	}

	public static void setAvpAsFloat32(int avpCode, long vendorId, AvpSet set, float... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsFloat32(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsFloat32(avpCode, vendorId, set, true, false, values);
		}
	}

	public static void setAvpAsFloat32(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, float... values) {
		performPreAddOperations(avpCode, vendorId, set);
		for (float f : values)
			set.addAvp(avpCode, f, vendorId, isMandatory, isProtected);

	}

	public static void setAvpAsFloat64(int avpCode, AvpSet set, double... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsFloat64(avpCode, rep.getVendorId(), set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsFloat64(avpCode, 0, set, true, false, values);
		}

	}

	public static void setAvpAsFloat64(int avpCode, long vendorId, AvpSet set, double... values) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsFloat64(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), values);
		} else {
			setAvpAsFloat64(avpCode, vendorId, set, true, false, values);
		}

	}

	public static void setAvpAsFloat64(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, double... values) {

		performPreAddOperations(avpCode, vendorId, set);
		for (double d : values)
			set.addAvp(avpCode, d, vendorId, isMandatory, isProtected);
	}

	public static void setAvpAsRaw(int avpCode, AvpSet set, byte[]... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsRaw(avpCode, rep.getVendorId(),  set,  rep.isMandatory(), rep.isProtected(),values);
		} else {
			setAvpAsRaw(avpCode, 0,  set,  true, false,values);
		}
	}

	public static void setAvpAsRaw(int avpCode, long vendorId,  AvpSet set,byte[]... values) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsRaw(avpCode, vendorId, set,  rep.isMandatory(), rep.isProtected(),values);
		} else {

			setAvpAsRaw(avpCode, vendorId,  set,  true, false,values);
		}
	}

	public static void setAvpAsRaw(int avpCode, long vendorId,  AvpSet set,  boolean isMandatory, boolean isProtected,byte[]... values) {
		performPreAddOperations(avpCode, vendorId, set);
		for(byte[] b:values)
			set.addAvp(avpCode, b, vendorId, isMandatory, isProtected);

	}

	public static void setAvpAsDate(int avpCode,  AvpSet set,Date... dates) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			setAvpAsDate(avpCode, rep.getVendorId(),  set,  rep.isMandatory(), rep.isProtected(),dates);
		} else {
			setAvpAsDate(avpCode, 0,  set,  true, false,dates);
		}
	}

	public static void setAvpAsDate(int avpCode, long vendorId, AvpSet set,Date... dates) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			setAvpAsDate(avpCode, vendorId,  set,  rep.isMandatory(), rep.isProtected(),dates);
		} else {

			setAvpAsDate(avpCode, vendorId,  set,  true, false,dates);
		}
	}

	public static void setAvpAsDate(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected,Date... dates) {
		performPreAddOperations(avpCode, vendorId, set);
		for(Date d:dates)
			set.addAvp(avpCode, d, vendorId, isMandatory, isProtected);

	}

	public static AvpSet setAvpAsGrouped(int avpCode, DiameterAvp[] childs, AvpSet set, boolean remove) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			return setAvpAsGrouped(avpCode, rep.getVendorId(), childs, set, remove, rep.isMandatory(), rep.isProtected());
		} else {

			return setAvpAsGrouped(avpCode, 0, childs, set, remove, true, false);
		}
	}

	public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, DiameterAvp[] childs, AvpSet set, boolean remove) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			return setAvpAsGrouped(avpCode, vendorId, childs, set, remove, rep.isMandatory(), rep.isProtected());
		} else {

			return setAvpAsGrouped(avpCode, vendorId, childs, set, remove, true, false);
		}
	}

	
	//this has removeFlag, causeit can be used from grouped avps?
	public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, DiameterAvp[] childs, AvpSet set, boolean remove, boolean isMandatory, boolean isProtected) {
		if (remove) {
			set.removeAvp(avpCode);
		}

		AvpSet g = set.addGroupedAvp(avpCode, vendorId, isMandatory, isProtected);
		for (DiameterAvp a : childs) {
			g.addAvp(a.getCode(), a.byteArrayValue(), a.getVendorId(), a.getMandatoryRule() == 1, a.getProtectedRule() == 1);
		}

		return g;
	}

	
	
	public static AvpSet[] setAvpsAsGrouped(int avpCode,  AvpSet set, DiameterAvp[]... childs) {

		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null) {
			return setAvpsAsGrouped(avpCode, rep.getVendorId(),  set,  rep.isMandatory(), rep.isProtected(),childs);
		} else {

			return setAvpsAsGrouped(avpCode, 0,  set,  true, false,childs);
		}
	}

	public static AvpSet[] setAvpsAsGrouped(int avpCode, long vendorId, AvpSet set, DiameterAvp[]... childs) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
		if (rep != null) {
			return setAvpsAsGrouped(avpCode, vendorId,  set,  rep.isMandatory(), rep.isProtected(),childs);
		} else {

			return setAvpsAsGrouped(avpCode, vendorId,  set,  true, false,childs);
		}
	}

	
	//this has removeFlag, causeit can be used from grouped avps?
	public static AvpSet[] setAvpsAsGrouped(int avpCode, long vendorId,  AvpSet set, boolean isMandatory, boolean isProtected,DiameterAvp[]... childs) {
		

		List<AvpSet> create = new ArrayList<AvpSet>();
		
		for (DiameterAvp[] a : childs) {
			create.add(setAvpAsGrouped(avpCode, a, set, false));
		}

		return create.toArray(new AvpSet[create.size()]);
	}
	public static Avp getAvp(int avpCode, AvpSet set) {
		AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode);
		if (rep != null)
			return set.getAvp(avpCode, rep.getVendorId());
		else
			return set.getAvp(avpCode, 0);
	}

	public static Avp getAvp(int avpCode, long vendorId, AvpSet set) {

		return set.getAvp(avpCode, vendorId);
	}

	public static void removeAvp(int avpCode, AvpSet set) {

	}

}
