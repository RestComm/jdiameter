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
import java.util.Date;
import java.util.List;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
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

  protected long getAvpAsUInt32(int code) {
    try {
      return avpSet.getAvp(code).getUnsigned32();
    } catch (Exception e) {
      log.warn(e);
      return -1;
    }
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

  protected boolean hasAvp(int code)
  {
    return hasAvp(code, 0L);
  }

  protected boolean hasAvp(int code, long vendorId)
  {
    return avpSet.getAvp(code, vendorId)  !=  null;
  }

  public byte[] getAvpAsByteArray(int code) {
    Avp rawAvp = this.avpSet.getAvp(code);
    if(rawAvp != null)
      try {
        return rawAvp.getRaw();
      } catch (AvpDataException e) {
        reportAvpFetchError(""+e, code);
        e.printStackTrace();
      }

      return null;

  }
  
  
  ////// Copied from DiameterMessageImpl ...
  protected void setAvpAsTime(int code, long vendorId, Date value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsTime(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsFloat32(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat32(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsFloat64(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat64(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected AvpSet setAvpAsGrouped(int code, long vendorId, DiameterAvp[] childs, boolean isMandatory, boolean isProtected)
  {
    return AvpUtilities.setAvpAsGrouped(code, vendorId, childs, avpSet, isMandatory, isProtected);
  }

  protected void setAvpAsInteger32(int code, long vendorId, int value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger32(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsInteger64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger64(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsUnsigned32(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned32(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsUnsigned64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned64(code, vendorId, avpSet, isMandatory, isProtected, value);
  }

  protected void setAvpAsUTF8String(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUTF8String(code, vendorId, avpSet, isMandatory, isProtected, value);
  }
  
  protected void setAvpAsOctetString(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsOctetString(code, vendorId, avpSet, isMandatory, isProtected, value);
  }
  
  protected void setAvpAsRaw(int code, long vendorId, byte[] value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsRaw(code, vendorId, avpSet, isMandatory, isProtected, value);
  }
  
  public void addAvp(String avpName, Object avp)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpName);
    
    if(rep != null)
    {
      addAvp(rep.getCode(), rep.getVendorId(), avp);
    }
  }

  public void addAvp(int avpCode, Object avp)
  {
    addAvp(avpCode, 0, avp );
  }
  
  public void addAvp(int avpCode, long vendorId, Object avp)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
    
    if(avpRep != null)
    {
      DiameterAvpType avpType = DiameterAvpType.fromString(avpRep.getType());
      
      boolean isMandatoryAvp = !(avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot"));
      boolean isProtectedAvp = avpRep.getRuleProtected().equals("must");
      
      switch (avpType.getType())
      {
      case DiameterAvpType._ADDRESS:
      case DiameterAvpType._DIAMETER_IDENTITY:
      case DiameterAvpType._DIAMETER_URI:
      case DiameterAvpType._IP_FILTER_RULE:
      case DiameterAvpType._OCTET_STRING:
      case DiameterAvpType._QOS_FILTER_RULE:
      {
        setAvpAsOctetString(avpCode, vendorId, (String) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      case DiameterAvpType._ENUMERATED:
      case DiameterAvpType._INTEGER_32:
      {
        setAvpAsInteger32(avpCode, vendorId, (Integer) avp, isMandatoryAvp, isProtectedAvp);        
        break;
      }
      case DiameterAvpType._FLOAT_32:
      {
        setAvpAsFloat32(avpCode, vendorId, (Float) avp, isMandatoryAvp, isProtectedAvp);        
        break;
      }
      case DiameterAvpType._FLOAT_64:
      {
        setAvpAsFloat64(avpCode, vendorId, (Float) avp, isMandatoryAvp, isProtectedAvp);        
        break;
      }
      case DiameterAvpType._GROUPED:
      {
        setAvpAsGrouped(avpCode, vendorId, (DiameterAvp[]) avp, isMandatoryAvp, isProtectedAvp);        
        break;
      }
      case DiameterAvpType._INTEGER_64:
      {
        setAvpAsInteger64(avpCode, vendorId, (Integer) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      case DiameterAvpType._TIME:
      {
        setAvpAsTime(avpCode, vendorId, (Date) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      case DiameterAvpType._UNSIGNED_32:
      {
        setAvpAsUnsigned32(avpCode, vendorId, (Long) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      case DiameterAvpType._UNSIGNED_64:
      {
        setAvpAsUnsigned64(avpCode, vendorId, (Long) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      case DiameterAvpType._UTF8_STRING:
      {
        setAvpAsUTF8String(avpCode, vendorId, (String) avp, isMandatoryAvp, isProtectedAvp);
        break;
      }
      }
    }
  }

}
