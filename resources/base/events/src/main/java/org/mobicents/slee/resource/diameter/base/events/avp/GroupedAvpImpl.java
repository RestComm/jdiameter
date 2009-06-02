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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * 
 * GroupedAvpImpl.java
 *
 * <br>Super project:  mobicents
 * <br>12:05:02 PM Jul 8, 2008 
 * <br>
 * @author <a href = "mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href = "mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public class GroupedAvpImpl extends DiameterAvpImpl implements GroupedAvp {

  protected AvpSet avpSet;

  public GroupedAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value)
  {
    super(code, vendorId, mnd, prt, value, DiameterAvpType.GROUPED);
    
    try {
      avpSet = parser.decodeAvpSet(value);
    }
    catch ( IOException e ) {
      log.error("Failure creating Grouped AVP.", e);
    }
  }

  public DiameterAvp[] getExtensionAvps()
  {
    DiameterAvp[] acc = new DiameterAvp[0];

    try {
      acc = getExtensionAvpsInternal(avpSet);
    }
    catch ( Exception e ) {
      log.error("Failure getting Extension AVPs.", e);
    }

    return acc;
  }

  public boolean hasExtensionAvps() {
    return getExtensionAvps().length > 0;
  }

  public void setExtensionAvps(DiameterAvp[] extensions) throws AvpNotAllowedException
  {
    if(extensions == null) {
      return;
    }
    
    try
    {
      for (DiameterAvp avp : extensions) {
        addAvp( avp, avpSet );
      }
    }
    catch (Exception e) {
      log.error("Failure setting Extension AVPs.", e);
    }
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

  public byte[] byteArrayValue() {
    return parser.encodeAvpSet(avpSet);
  }

  public Object clone() {
    return new GroupedAvpImpl(code, vendorId, mnd, prt, byteArrayValue());
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
    else if(avp  !=  null)
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

  // AVP Utilities Proxy Methods
  
  protected Date getAvpAsTime(int code)
  {
    return AvpUtilities.getAvpAsTime(code, avpSet);
  }

  protected Date getAvpAsTime(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsTime(code, vendorId, avpSet);
  }

  protected Date[] getAvpsAsTime(int code)
  {
    return AvpUtilities.getAvpsAsTime(code, avpSet);
  }

  protected Date[] getAvpsAsTime(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsTime(code, vendorId, avpSet);
  }

  protected void setAvpAsTime(int code, long vendorId, Date value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsTime(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected float getAvpAsFloat32(int code)
  {
    return AvpUtilities.getAvpAsFloat32(code, avpSet);
  }

  protected float getAvpAsFloat32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsFloat32(code, vendorId, avpSet);
  }

  protected float[] getAvpsAsFloat32(int code)
  {
    return AvpUtilities.getAvpsAsFloat32(code, avpSet);
  }

  protected float[] getAvpsAsFloat32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsFloat32(code, vendorId, avpSet);
  }

  protected void setAvpAsFloat32(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat32(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected double getAvpAsFloat64(int code)
  {
    return AvpUtilities.getAvpAsFloat64(code, avpSet);
  }

  protected double getAvpAsFloat64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsFloat64(code, vendorId, avpSet);
  }

  protected double[] getAvpsAsFloat64(int code)
  {
    return AvpUtilities.getAvpsAsFloat64(code, avpSet);
  }

  protected double[] getAvpsAsFloat64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsFloat64(code, vendorId, avpSet);
  }

  protected void setAvpAsFloat64(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat64(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected byte[] getAvpAsGrouped(int code)
  {
    return AvpUtilities.getAvpAsGrouped(code, avpSet);
  }

  protected byte[] getAvpAsGrouped(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsGrouped(code, vendorId, avpSet);
  }

  protected byte[][] getAvpsAsGrouped(int code)
  {
    return AvpUtilities.getAvpsAsGrouped(code, avpSet);
  }

  protected byte[][] getAvpsAsGrouped(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsGrouped(code, vendorId, avpSet);
  }

  protected AvpSet setAvpAsGrouped(int code, long vendorId, DiameterAvp[] childs, boolean isMandatory, boolean isProtected)
  {
    return AvpUtilities.setAvpAsGrouped(null, code, vendorId, avpSet, isMandatory, isProtected, childs);
  }

  protected int getAvpAsInteger32(int code)
  {
    return AvpUtilities.getAvpAsInteger32(code, avpSet);
  }

  protected int getAvpAsInteger32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsInteger32(code, vendorId, avpSet);
  }

  protected int[] getAvpsAsInteger32(int code)
  {
    return AvpUtilities.getAvpsAsInteger32(code, avpSet);
  }

  protected int[] getAvpsAsInteger32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsInteger32(code, vendorId, avpSet);
  }

  protected void setAvpAsInteger32(int code, long vendorId, int value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger32(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected long getAvpAsInteger64(int code)
  {
    return AvpUtilities.getAvpAsInteger64(code, avpSet);
  }

  protected long getAvpAsInteger64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsInteger64(code, vendorId, avpSet);
  }

  protected long[] getAvpsAsInteger64(int code)
  {
    return AvpUtilities.getAvpsAsInteger64(code, avpSet);
  }

  protected long[] getAvpsAsInteger64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsInteger64(code, vendorId, avpSet);
  }

  protected void setAvpAsInteger64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger64(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected long getAvpAsUnsigned32(int code)
  {
    return AvpUtilities.getAvpAsUnsigned32(code, avpSet);
  }

  protected long getAvpAsUnsigned32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUnsigned32(code, vendorId, avpSet);
  }

  protected long[] getAvpsAsUnsigned32(int code)
  {
    return AvpUtilities.getAvpsAsUnsigned32(code, avpSet);
  }

  protected long[] getAvpsAsUnsigned32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUnsigned32(code, vendorId, avpSet);
  }

  protected void setAvpAsUnsigned32(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned32(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected long getAvpAsUnsigned64(int code)
  {
    return AvpUtilities.getAvpAsUnsigned64(code, avpSet);
  }

  protected long getAvpAsUnsigned64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUnsigned64(code, vendorId, avpSet);
  }

  protected long[] getAvpsAsUnsigned64(int code)
  {
    return AvpUtilities.getAvpsAsUnsigned64(code, avpSet);
  }

  protected long[] getAvpsAsUnsigned64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUnsigned64(code, vendorId, avpSet);
  }

  protected void setAvpAsUnsigned64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned64(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected String getAvpAsUTF8String(int code)
  {
    return AvpUtilities.getAvpAsUTF8String(code, avpSet);
  }

  protected String getAvpAsUTF8String(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUTF8String(code, vendorId, avpSet);
  }

  protected String[] getAvpsAsUTF8String(int code)
  {
    return AvpUtilities.getAvpsAsUTF8String(code, avpSet);
  }

  protected String[] getAvpsAsUTF8String(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUTF8String(code, vendorId, avpSet);
  }

  protected void setAvpAsUTF8String(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUTF8String(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected String getAvpAsOctetString(int code)
  {
    return AvpUtilities.getAvpAsOctetString(code, avpSet);
  }

  protected String getAvpAsOctetString(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsOctetString(code, vendorId, avpSet);
  }

  protected String[] getAvpsAsOctetString(int code)
  {
    return AvpUtilities.getAvpsAsOctetString(code, avpSet);
  }

  protected String[] getAvpsAsOctetString(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsOctetString(code, vendorId, avpSet);
  }

  protected void setAvpAsOctetString(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsOctetString(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected byte[] getAvpAsRaw(int code)
  {
    return AvpUtilities.getAvpAsRaw(code, avpSet);
  }

  protected byte[] getAvpAsRaw(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsRaw(code, vendorId, avpSet);
  }

  protected byte[][] getAvpsAsRaw(int code)
  {
    return AvpUtilities.getAvpsAsRaw(code, avpSet);
  }

  protected byte[][] getAvpsAsRaw(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsRaw(code, vendorId, avpSet);
  }

  protected void setAvpAsRaw(int code, long vendorId, byte[] value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsRaw(null, code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected Object getAvpAsCustom(int code, Class clazz)
  {
    return AvpUtilities.getAvpAsCustom(code, avpSet, clazz);
  }

  protected Object getAvpAsCustom(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpAsCustom(code, vendorId, avpSet, clazz);
  }

  protected Object[] getAvpsAsCustom(int code, Class clazz)
  {
    return AvpUtilities.getAvpsAsCustom(code, avpSet, clazz);
  }

  protected Object[] getAvpsAsCustom(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpsAsCustom(code, vendorId, avpSet, clazz);
  }

  protected DiameterIdentity getAvpAsDiameterIdentity(int code)
  {
    return AvpUtilities.getAvpAsDiameterIdentity(code, avpSet);
  }

  protected DiameterIdentity getAvpAsDiameterIdentity(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsDiameterIdentity(code, vendorId, avpSet);
  }

  protected DiameterIdentity[] getAvpsAsDiameterIdentity(int code)
  {
    return AvpUtilities.getAvpsAsDiameterIdentity(code, avpSet);
  }

  protected DiameterIdentity[] getAvpsAsDiameterIdentity(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsDiameterIdentity(code, vendorId, avpSet);
  }

  protected DiameterURI getAvpAsDiameterURI(int code)
  {
    return AvpUtilities.getAvpAsDiameterURI(code, avpSet);
  }

  protected DiameterURI getAvpAsDiameterURI(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsDiameterURI(code, vendorId, avpSet);
  }

  protected DiameterURI[] getAvpsAsDiameterURI(int code)
  {
    return AvpUtilities.getAvpsAsDiameterURI(code, avpSet);
  }

  protected DiameterURI[] getAvpsAsDiameterURI(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsDiameterURI(code, vendorId, avpSet);
  }

  protected Address getAvpAsAddress(int code)
  {
    return AvpUtilities.getAvpAsAddress(code, avpSet);
  }

  protected Address getAvpAsAddress(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsAddress(code, vendorId, avpSet);
  }

  protected Address[] getAvpsAsAddress(int code)
  {
    return AvpUtilities.getAvpsAsAddress(code, avpSet);
  }

  protected Address[] getAvpsAsAddress(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsAddress(code, vendorId, avpSet);
  }

  protected Object getAvpAsEnumerated(int code, Class clazz)
  {
    return AvpUtilities.getAvpAsEnumerated(code, avpSet, clazz);
  }

  protected Object getAvpAsEnumerated(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpAsEnumerated(code, vendorId, avpSet, clazz);
  }

  protected Object[] getAvpsAsEnumerated(int code, Class clazz)
  {
    return AvpUtilities.getAvpsAsEnumerated(code, avpSet, clazz);
  }

  protected Object[] getAvpsAsEnumerated(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpsAsEnumerated(code, vendorId, avpSet, clazz);
  }

  protected void addAvp(String avpName, Object avp)
  {
    AvpUtilities.addAvp(null, avpName, avpSet, avp);
  }

  protected void addAvp(int avpCode, Object avp)
  {
    AvpUtilities.addAvp(null, avpCode, 0L, avpSet, avp);
  }

  protected void addAvp(int avpCode, long vendorId, Object avp)
  {
    AvpUtilities.addAvp(null, avpCode, vendorId, avpSet, avp);
  }

  protected boolean hasAvp(int code)
  {
    return AvpUtilities.hasAvp(code, 0L, avpSet);
  }

  protected boolean hasAvp(int code, long vendorId)
  {
    return AvpUtilities.hasAvp(code, vendorId, avpSet);
  }

  protected Object getAvp(int avpCode)
  {
    return getAvp(avpCode, 0L);
  }

  protected Object getAvp(String avpName)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpName);

    if(avpRep != null) {
      return getAvp(avpRep.getCode(), avpRep.getVendorId());
    }

    return null;
  }

  protected Object getAvp(int avpCode, long vendorId)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if(avpRep != null)
    {
      int avpType = AvpRepresentation.Type.valueOf(avpRep.getType()).ordinal();

      switch (avpType)
      {
      case DiameterAvpType._ADDRESS:
      case DiameterAvpType._DIAMETER_IDENTITY:
      case DiameterAvpType._DIAMETER_URI:
      case DiameterAvpType._IP_FILTER_RULE:
      case DiameterAvpType._OCTET_STRING:
      case DiameterAvpType._QOS_FILTER_RULE:
      {
        return getAvpAsOctetString(avpCode, vendorId);
      }
      case DiameterAvpType._ENUMERATED:
      case DiameterAvpType._INTEGER_32:
      {
        return getAvpAsInteger32(avpCode, vendorId);        
      }
      case DiameterAvpType._FLOAT_32:
      {
        return getAvpAsFloat32(avpCode, vendorId);        
      }
      case DiameterAvpType._FLOAT_64:
      {
        return getAvpAsFloat64(avpCode, vendorId);        
      }
      case DiameterAvpType._GROUPED:
      {
        return getAvpAsGrouped(avpCode, vendorId);
      }
      case DiameterAvpType._INTEGER_64:
      {
        return getAvpAsInteger64(avpCode, vendorId);
      }
      case DiameterAvpType._TIME:
      {
        return getAvpAsTime(avpCode, vendorId);
      }
      case DiameterAvpType._UNSIGNED_32:
      {
        return getAvpAsUnsigned32(avpCode, vendorId);
      }
      case DiameterAvpType._UNSIGNED_64:
      {
        return getAvpAsUnsigned64(avpCode, vendorId);
      }
      case DiameterAvpType._UTF8_STRING:
      {
        return getAvpAsUTF8String(avpCode, vendorId);
      }
      default:
      {
        return getAvpAsRaw(avpCode, vendorId);
      }
      }
    }

    return null;
  }

  public void addAvp(DiameterAvp avp) {
    AvpUtilities.addAvp(avp, avpSet);
  }

  @Override
  public boolean equals( Object other )
  {
    if(!(other instanceof GroupedAvpImpl))
      return false;
    GroupedAvpImpl that = (GroupedAvpImpl) other;
    
    if(this.code != that.getCode() || this.vendorId != that.getVendorId())
      return false;
      
    List<DiameterAvp> thisArray = Arrays.asList(this.getExtensionAvps());
    List<DiameterAvp> thatArray = Arrays.asList(that.getExtensionAvps());

    if(thisArray.size() != thatArray.size())
      return false;
    
    for(DiameterAvp avp : thisArray)
    {
      if(!thatArray.contains(avp))
        return false;
    }
    
    return true;
  }
}
