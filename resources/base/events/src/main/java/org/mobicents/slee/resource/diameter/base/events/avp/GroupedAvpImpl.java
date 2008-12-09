package org.mobicents.slee.resource.diameter.base.events.avp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpUtilities;

/**
 * 
 * GroupedAvpImpl.java
 *
 * <br>Super project:  mobicents
 * <br>12:05:02 PM Jul 8, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class GroupedAvpImpl extends DiameterAvpImpl implements GroupedAvp {

	protected AvpSet avpSet;

	public GroupedAvpImpl(int code, long vendorId, int mnd, int prt,
			byte[] value) {
		super(code, vendorId, mnd, prt, null, DiameterAvpType.GROUPED);
		try
    {
      avpSet = parser.decodeAvpSet(value);
    }
    catch ( IOException e )
    {
      log.error("", e);
    }

	}

	public void setExtensionAvps(DiameterAvp[] extensions) throws AvpNotAllowedException
	{
		try
		{
			for (DiameterAvp avp : extensions)
			{
			  addAvp( avp, avpSet );
			}
		}
		catch (Exception e)
		{
			log.error("", e);
		}
	}

	public DiameterAvp[] getExtensionAvps()
	{
	  DiameterAvp[] acc = new DiameterAvp[0];
	  
		try
    {
		  acc = getExtensionAvpsInternal(avpSet);
    }
    catch ( Exception e )
    {
      log.error("", e);
    }
    
    return acc;
	}

	public double doubleValue() {
		throw new IllegalArgumentException();
	}

	public float floatValue() {
		throw new IllegalArgumentException();
	}

	public int intValue() {
		throw new IllegalArgumentException();
	}

	public long longValue() {
		throw new IllegalArgumentException();
	}

	public String stringValue() {
		throw new IllegalArgumentException();
	}

	public boolean hasExtensionAvps() {
		return getExtensionAvps().length > 0;
	}

	public byte[] byteArrayValue() {
		return parser.encodeAvpSet(avpSet);
	}

	public Object clone() {
		return new GroupedAvpImpl(code, vendorId, mnd, prt, byteArrayValue());
	}

	// todo set/get Avp methods is duplicate @see DiameterMessageImpl

	protected void setAvpAsIdentity(int code, String value, boolean octet,
			boolean mandatory, boolean remove) {
		AvpUtilities.setAvpAsString(code, 0, octet, value, avpSet, remove, mandatory, false);
	}

	protected void setAvpAsIdentity(int code, String value, boolean octet,
			 boolean remove) {
		AvpUtilities.setAvpAsString(code, octet, value, avpSet, remove);
	}
	
	protected void setAvpAsIdentity(int code, String value, long vendorId, boolean octet,
			boolean mandatory,boolean isProtected , boolean remove) {
		AvpUtilities.setAvpAsString(code, vendorId, octet, value, avpSet, remove, mandatory, false);
	}
	//FIXME: baranowb - those methods are the same!!!
	protected void setAvpAsString(int code, String value, boolean octet,
			boolean mandatory, boolean remove) {
		AvpUtilities.setAvpAsString(code, 0, octet, value, avpSet, remove, mandatory, false);
	}

	protected void setAvpAsString(int code, String value, long vendorId, boolean octet,
			boolean mandatory,boolean isProtected , boolean remove) {
		AvpUtilities.setAvpAsString(code, vendorId, octet, value, avpSet, remove, mandatory, false);
	}
	
	protected void setAvpAsString(int code, String value,  boolean octet,
			  boolean remove) {
		AvpUtilities.setAvpAsString(code, octet, value, avpSet, remove);
	}
	protected DiameterIdentityAvp getAvpAsIdentity(int code) {
		try {
			Avp rawAvp = avpSet.getAvp(code);
			if (rawAvp != null) {
				int mndr = rawAvp.isMandatory() ? 1 : 0;
				// FIXME: baranowb; how to set prt here?
				return new DiameterIdentityAvpImpl(rawAvp.getCode(), rawAvp
						.getVendorId(), mndr, 1, rawAvp.getRaw());
			}
			return null;
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}

	protected void setAvpAsByteArray(int code, byte[] value,boolean remove) {
		AvpUtilities.setAvpAsRaw(code, value, avpSet, remove);
	}
	protected void setAvpAsByteArray(int code,long vendorId, byte[] value, boolean mandatory,boolean isProtected, boolean remove) {
		AvpUtilities.setAvpAsRaw(code, vendorId,value, avpSet, remove,mandatory,isProtected);
	}
	
	

	protected void setAvpAsByteArray(int code, byte[] value, boolean mandatory,boolean isProtected, boolean remove) {
		AvpUtilities.setAvpAsRaw(code, 0,value, avpSet, remove,mandatory,isProtected);
	}
	protected void setAvpAsUInt32(int code, long value,
			boolean remove) {
		
		AvpUtilities.setAvpAsUInt32(code, value, avpSet, remove);
	}
	
	protected void setAvpAsUInt32(int code,long vendorId, long value, boolean mandatory, boolean isProtected,
			boolean remove) {
		AvpUtilities.setAvpAsUInt32(code, vendorId, value, avpSet, remove,mandatory,isProtected);
		
	}

	//FIXME: again those are the same methods
	
	protected void setAvpAsUInt64(int code, long value, boolean mandatory,
			boolean remove) {
		AvpUtilities.setAvpAsUInt64(code, value, avpSet, remove);
	}
	
	protected void setAvpAsUInt64(int code,long vendorId, long value, boolean mandatory, boolean isProtected,
			boolean remove) {
		AvpUtilities.setAvpAsUInt64(code, vendorId, value, avpSet, remove,mandatory,isProtected);
	}
	
	
	protected long getAvpAsUInt32(int code) {
		try {
			return avpSet.getAvp(code).getUnsigned32();
		} catch (Exception e) {
			log.warn(e);
			return -1;
		}
	}

	protected int getAvpAsInt32(int code)
	{
		try {
			return avpSet.getAvp(code).getInteger32();
		} catch (Exception e) {
			log.warn(e);
			return Integer.MIN_VALUE;
		}
	}
	protected long getAvpAsInt64(int code)
	{
		try {
			return avpSet.getAvp(code).getInteger64();
		} catch (Exception e) {
			log.warn(e);
			return Long.MIN_VALUE;
		}
	}
	protected long[] getAllAvpAsUInt32(int code) {
		AvpSet all = avpSet.getAvps(code);
		long[] acc = new long[all.size()];
		for (int i = 0; i < acc.length; i++)
			try {
				acc[i] = all.getAvpByIndex(i).getUnsigned32();
			} catch (Exception e) {
				log.warn(e);
			}
		return acc;
	}

  private void addAvp(DiameterAvp avp, AvpSet set)
  {
    // FIXME: alexandre: Should we look at the types and add them with proper function?
    if(avp instanceof GroupedAvp)
    {
      AvpSet avpSet = set.addGroupedAvp(avp.getCode(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
      
      DiameterAvp[] groupedAVPs = ((GroupedAvp)avp).getExtensionAvps();
      for(DiameterAvp avpFromGroup : groupedAVPs)
      {
        addAvp(avpFromGroup, avpSet);
      }
    }
    else if(avp != null)
      set.addAvp(avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() == 1, avp.getProtectedRule() == 1);
  }

  private DiameterAvp[] getExtensionAvpsInternal(AvpSet set) throws Exception
  {
    List<DiameterAvp> acc = new ArrayList<DiameterAvp>();

    for (Avp a : set) 
    {
      // FIXME: alexandre: This is how I can check if it's a Grouped AVP... 
      // should use dictionary (again). a.getGrouped() get's into deadlock.
      if(a.getRaw().length == 0)
      {
        GroupedAvpImpl gAVP = new GroupedAvpImpl(a.getCode(), a.getVendorId(), 
            a.isMandatory() ? 1 : 0, a.isEncrypted() ? 1: 0, a.getRaw());
        
        gAVP.setExtensionAvps( getExtensionAvpsInternal(a.getGrouped()) );
        
        // This is a grouped AVP... let's make it like that.
        acc.add( gAVP );
      }
      else
      {
        acc.add(new DiameterAvpImpl(a.getCode(), a.getVendorId(),
            a.isMandatory() ? 1 : 0, a.isEncrypted() ? 1 : 0, a.getRaw(), null));
      }
    }
    
    return acc.toArray(new DiameterAvp[0]);
  }  
  
  protected boolean hasAvp(int code)
  {
	  Avp rawAvp=avpSet.getAvp(code);
		if(rawAvp!=null)
		{	
				return true;
		}
		
		return false;
  }
  
  //public AvpSet getRaw()
  //{
  //	  return this.avpSet;
  //}

  public byte[] getAvpAsByteArray(int code) {
	  Avp rawAvp=this.avpSet.getAvp(code);
	  if(rawAvp!=null)
		  try {
			  return rawAvp.getRaw();
		  } catch (AvpDataException e) {
			  reportAvpFetchError(""+e, code);
			  e.printStackTrace();
		  }
		
		  return null;
	
  	}
}
