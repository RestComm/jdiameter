/**
 * Start time:13:11:26 2008-11-12<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package net.java.slee.resource.diameter.base.events.avp;

import java.util.Date;


import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Start time:13:11:26 2008-11-12<br>
 * Project: mobicents-diameter-parent<br>
 * This class contains some handy methods. It requires avp dictionary to be loaded
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpUtilities {

	public static void setAvpAsString(int avpCode,boolean isOctetString, String value, AvpSet set,boolean remove)
	{
	
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsString(avpCode,rep.getVendorId(),isOctetString,value,set,remove,rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsString(avpCode,0,isOctetString,value,set,remove,true,false);
		}
	}
	
	public static void setAvpAsString(int avpCode,long vendorId,boolean isOctetString, String value, AvpSet set, boolean remove)
	{
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsString(avpCode,rep.getVendorId(),isOctetString,value,set,remove,rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsString(avpCode,vendorId,isOctetString,value,set,remove,true,false);
		}
	}
	
	public static void setAvpAsString(int avpCode,long vendorId,boolean isOctetString, String value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected,isOctetString);
	}
	
	public static void setAvpAsUInt32(int avpCode,long value, AvpSet set,boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsUInt32(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsUInt32(avpCode, 0, value, set, remove,true,false);
		}
		
	}
	
	public static void setAvpAsUInt32(int avpCode,long vendorId,long value, AvpSet set, boolean remove)
	{
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsUInt32(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsUInt32(avpCode, vendorId, value, set, remove,true,false);
		}
	}
	
	public static void setAvpAsUInt32(int avpCode,long vendorId,long value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected,true);
	
	}
	
	public static void setAvpAsUInt64(int avpCode,long value, AvpSet set,boolean remove)
	{
	
		//FIXME: whats the diff with UInt32 setter
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsUInt64(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsUInt64(avpCode, 0, value, set, remove,true,false);
		}
	}
	
	public static void setAvpAsUInt64(int avpCode,long vendorId,long value, AvpSet set, boolean remove)
	{
		
		//FIXME: whats the diff with UInt32 setter
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsUInt64(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsUInt64(avpCode, vendorId, value, set, remove,true,false);
		}
		
	}
	
	public static void setAvpAsUInt64(int avpCode,long vendorId,long value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected,true);
	
	}
	
	public static void setAvpAsInt32(int avpCode,int value, AvpSet set,boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsInt32(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsInt32(avpCode, 0, value, set, remove,true,false);
		}
		
	}
	
	public static void setAvpAsInt32(int avpCode,long vendorId,int value, AvpSet set, boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsInt32(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsInt32(avpCode, vendorId, value, set, remove,true,false);
		}
		
	}
	
	public static void setAvpAsInt32(int avpCode,long vendorId,int value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected);
	
	}
	
	public static void setAvpAsInt64(int avpCode,long value, AvpSet set,boolean remove)
	{
	
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsInt64(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsInt64(avpCode, 0, value, set, remove,true,false);
		}
	}
	
	public static void setAvpAsInt64(int avpCode,long vendorId,long value, AvpSet set, boolean remove)
	{
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsInt64(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsInt64(avpCode, vendorId, value, set, remove,true,false);
		}
	}
	
	public static void setAvpAsInt64(int avpCode,long vendorId,long value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected);
	}
	
	public static void setAvpAsFloat32(int avpCode,float value, AvpSet set,boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsFloat32(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsFloat32(avpCode, 0, value, set, remove, true,false);		}
		
	}
	
	public static void setAvpAsFloat32(int avpCode,long vendorId,float value, AvpSet set, boolean remove)
	{
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsFloat32(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsFloat32(avpCode, vendorId, value, set, remove, true,false);
		}
	}
	
	public static void setAvpAsFloat32(int avpCode,long vendorId,float value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected);
	
	}
	
	public static void setAvpAsFloat64(int avpCode,double value, AvpSet set,boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsFloat64(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsFloat64(avpCode, 0, value, set, remove, true,false);
		}
		
	}
	
	public static void setAvpAsFloat64(int avpCode,long vendorId,double value, AvpSet set, boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsFloat64(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsFloat64(avpCode, vendorId, value, set, remove, true,false);
		}
		
	}
	
	public static void setAvpAsFloat64(int avpCode,long vendorId,double value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected);
	}
	
	
	public static void setAvpAsRaw(int avpCode, byte[] value, AvpSet set,boolean remove)
	{
		
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsRaw(avpCode, rep.getVendorId(), value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsRaw(avpCode, 0, value, set, remove, true,false);
		}
	}
	
	public static void setAvpAsRaw(int avpCode,long vendorId,byte[] value, AvpSet set, boolean remove)
	{
		
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsRaw(avpCode, vendorId, value, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			
			setAvpAsRaw(avpCode, vendorId, value, set, remove, true,false);
		}
	}
	
	public static void setAvpAsRaw(int avpCode,long vendorId,byte[] value, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,value,vendorId,isMandatory,isProtected);
		
	}
	
	public static void setAvpAsDate(int avpCode, Date date, AvpSet set,boolean remove)
	{
		
	
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			setAvpAsDate(avpCode, rep.getVendorId(), date, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			setAvpAsDate(avpCode, 0, date, set, remove, true,false);
		}
	}
	
	public static void setAvpAsDate(int avpCode,long vendorId,Date date, AvpSet set, boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			setAvpAsDate(avpCode, vendorId, date, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
		
			setAvpAsDate(avpCode, vendorId, date, set, remove, true,false);
		}
	}
	
	public static void setAvpAsDate(int avpCode,long vendorId,Date date, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		set.addAvp(avpCode,date,vendorId,isMandatory,isProtected);
		
	}
	
	
	public static AvpSet setAvpAsGrouped(int avpCode, DiameterAvp[] childs, AvpSet set,boolean remove)
	{
	
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
		{
			return setAvpAsGrouped(avpCode, rep.getVendorId(), childs, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			
			return setAvpAsGrouped(avpCode, 0, childs, set, remove, true,false);
		}
	}
	
	public static AvpSet setAvpAsGrouped(int avpCode,long vendorId,DiameterAvp[] childs, AvpSet set, boolean remove)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode,vendorId);
		if(rep!=null)
		{
			return setAvpAsGrouped(avpCode, vendorId, childs, set, remove, rep.isMandatory(),rep.isProtected());
		}
		else
		{
			
			return setAvpAsGrouped(avpCode, vendorId, childs, set, remove, true,false);
		}
	}
	
	public static AvpSet setAvpAsGrouped(int avpCode,long vendorId,DiameterAvp[] childs, AvpSet set, boolean remove, boolean isMandatory,boolean isProtected)
	{
		if(remove)
		{
			set.removeAvp(avpCode);
		}

		AvpSet g = set.addGroupedAvp(avpCode, vendorId,isMandatory, isProtected);
		for (DiameterAvp a : childs)
		{
			g.addAvp(a.getCode(), a.byteArrayValue(), a.getVendorId(), a.getMandatoryRule() == 1, a.getProtectedRule() == 1);
		}
		
		return g;
	}
	
	
	public static Avp getAvp(int avpCode, AvpSet set)
	{
		AvpRepresentation rep=AvpDictionary.INSTANCE.getAvp(avpCode);
		if(rep!=null)
			return set.getAvp(avpCode, rep.getVendorId());
		else
			return set.getAvp(avpCode,0);
	}
	
	public static Avp getAvp(int avpCode,long vendorId,AvpSet set)
	{
		
		return set.getAvp(avpCode, vendorId);
	}
	
	public static void removeAvp(int avpCode, AvpSet set)
	{
		
	}
	
	
	
	
}
