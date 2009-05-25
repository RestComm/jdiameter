/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
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
package net.java.slee.resource.diameter.base.events.avp;

import java.util.Date;

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
public class AvpUtilities {

  private static long _DEFAULT_VENDOR_ID = 0L;

  private static boolean _DEFAULT_MANDATORY = true;
  private static boolean _DEFAULT_PROTECTED = false;

  private static boolean _AVP_REMOVAL_ALLOWED = false;

  public static boolean isAvpRemoveAllowed()
  {
    return _AVP_REMOVAL_ALLOWED;
  }

  public static boolean hasAvp(int avpCode, long vendorId, AvpSet set)
  {
    AvpSet inner = set.getAvps(avpCode, vendorId);

    if (inner.getAvp(avpCode, vendorId) != null) {
      return true;
    }
    else if (set.getAvp(avpCode, vendorId) != null) {
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * @param avpCode
   * @param vendorId
   * @param set
   */
  private static void performPreAddOperations(int avpCode, long vendorId, AvpSet set)
  {
    if (hasAvp(avpCode, vendorId, set) && !isAvpRemoveAllowed()) {
      throw new IllegalStateException("AVP is already present in message and cannot be overwritten.");
    }
    else {
      set.removeAvp(avpCode);
    }
  }

  /**
   * Returns an {@link AvpRepresentation} of the AVP with the given code and given Vendor-Id, if found.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @return
   */
  private static AvpRepresentation getAvpRepresentation(int avpCode, long vendorId)
  {
    return AvpDictionary.INSTANCE.getAvp(avpCode);
  }

  /**
   * Adds AVP to {@link AvpSet} as String (Octet or UTF-8) with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param isOctetString if true added as OctetString type, otherwise as UTF8String
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsString(int avpCode, boolean isOctetString, AvpSet set, String value)
  {
    setAvpAsString(avpCode, _DEFAULT_VENDOR_ID, isOctetString, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as String (Octet or UTF-8) with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param isOctetString if true added as OctetString type, otherwise as UTF8String
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsString(int avpCode, long vendorId, boolean isOctetString, AvpSet set, String value)
  {
    AvpRepresentation rep = getAvpRepresentation(avpCode, vendorId);

    if (rep != null) {
      setAvpAsString(avpCode, vendorId, isOctetString, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsString(avpCode, vendorId, isOctetString, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as String (Octet or UTF-8) with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param isOctetString if true added as OctetString type, otherwise as UTF8String
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsString(int avpCode, long vendorId, boolean isOctetString, AvpSet set, boolean isMandatory, boolean isProtected, String value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, isOctetString);
  }

  /**
   * Adds AVP to {@link AvpSet} as OctetString with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsOctetString(int avpCode, AvpSet set, String value)
  {
    setAvpAsOctetString(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as OctetString with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsOctetString(int avpCode, long vendorId, AvpSet set, String value)
  {
    AvpRepresentation rep = getAvpRepresentation(avpCode, vendorId);

    if (rep != null) {
      setAvpAsOctetString(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsOctetString(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as OctetString with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsOctetString(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, String value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, true);
  }

  /**
   * Adds AVP to {@link AvpSet} as UTF8String with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUTF8String(int avpCode, boolean isOctetString, AvpSet set, String value)
  {
    setAvpAsUTF8String(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as UTF8String with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUTF8String(int avpCode, long vendorId, AvpSet set, String value)
  {
    AvpRepresentation rep = getAvpRepresentation(avpCode, vendorId);

    if (rep != null) {
      setAvpAsUTF8String(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsUTF8String(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as OctetString with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUTF8String(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, String value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, false);
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned32 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned32(int avpCode, AvpSet set, long value)
  {
    setAvpAsUnsigned32(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned32 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned32(int avpCode, long vendorId, AvpSet set, long value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsUnsigned32(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsUnsigned32(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned32 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned32(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, long value)
  { 
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, true);
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned64 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned64(int avpCode, AvpSet set, long value)
  {
    setAvpAsUnsigned64(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned64 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned64(int avpCode, long vendorId, AvpSet set, long value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsUnsigned64(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsUnsigned64(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as Unsigned64 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsUnsigned64(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, long value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, false);
  }

  /**
   * Adds AVP to {@link AvpSet} as Integer32 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger32(int avpCode, AvpSet set, int value)
  {
    setAvpAsInteger32(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Integer32 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger32(int avpCode, long vendorId, AvpSet set, int value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsInteger32(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsInteger32(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as Integer32 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger32(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, int value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected);
  }

  /**
   * Adds AVP to {@link AvpSet} as Integer64 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger64(int avpCode, AvpSet set, long value)
  {
    setAvpAsInteger64(avpCode, value, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Integer64 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger64(int avpCode, long vendorId, AvpSet set, long value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsInteger64(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsInteger64(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }


  /**
   * Adds AVP to {@link AvpSet} as Integer64 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsInteger64(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, long value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected, true);
  }

  /**
   * Adds AVP to {@link AvpSet} as Float32 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat32(int avpCode, AvpSet set, float value)
  {
    setAvpAsFloat32(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Float32 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat32(int avpCode, long vendorId, AvpSet set, float value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsFloat32(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsFloat32(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }


  /**
   * Adds AVP to {@link AvpSet} as Float32 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat32(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, float value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected);
  }

  /**
   * Adds AVP to {@link AvpSet} as Float64 with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat64(int avpCode, AvpSet set, double value)
  {
    setAvpAsFloat64(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Float64 with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat64(int avpCode, long vendorId, AvpSet set, double value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsFloat64(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsFloat64(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }
  

  /**
   * Adds AVP to {@link AvpSet} as Float64 with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsFloat64(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, double value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected);
  }

  /**
   * Adds AVP to {@link AvpSet} as raw data with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsRaw(int avpCode, AvpSet set, byte[] value)
  {
    setAvpAsRaw(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as raw data with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsRaw(int avpCode, long vendorId, AvpSet set, byte[] value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      setAvpAsRaw(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsRaw(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }


  /**
   * Adds AVP to {@link AvpSet} as raw data with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsRaw(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, byte[] value)
  {
    performPreAddOperations(avpCode, vendorId, set);

    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected);
  }

  /**
   * Adds AVP to {@link AvpSet} as Time with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsTime(int avpCode, AvpSet set, Date value)
  {
    setAvpAsTime(avpCode, _DEFAULT_VENDOR_ID, set, value);
  }

  /**
   * Adds AVP to {@link AvpSet} as Time with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static void setAvpAsTime(int avpCode, long vendorId, AvpSet set, Date value)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
    
    if (rep != null) {
      setAvpAsTime(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), value);
    }
    else {
      setAvpAsTime(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, value);
    }
  }
  

  /**
   * Adds AVP to {@link AvpSet} as Time with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static void setAvpAsTime(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, Date value)
  {
    performPreAddOperations(avpCode, vendorId, set);
    
    set.addAvp(avpCode, value, vendorId, isMandatory, isProtected);
  }

  /**
   * Adds AVP to {@link AvpSet} as Grouped with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static AvpSet setAvpAsGrouped(int avpCode, DiameterAvp[] childs, AvpSet set)
  {
    return setAvpAsGrouped(avpCode, _DEFAULT_VENDOR_ID, childs, set);
  }

  /**
   * Adds AVP to {@link AvpSet} as Grouped with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, DiameterAvp[] childs, AvpSet set)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
    
    if (rep != null) {
      return setAvpAsGrouped(avpCode, vendorId, childs, set, rep.isMandatory(), rep.isProtected());
    }
    else {
      return setAvpAsGrouped(avpCode, vendorId, childs, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED);
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as Grouped with the given code and given Vendor-Id plus defined mandatory and protected flags.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the AvpSet to add AVP
   * @param isMandatory the value for the mandatory bit
   * @param isProtected the value for the protected bit
   * @param value the value of the AVP to add
   */
  public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, DiameterAvp[] childs, AvpSet set, boolean isMandatory, boolean isProtected)
  {
    performPreAddOperations(avpCode, vendorId, set);

    AvpSet grouped = set.addGroupedAvp(avpCode, vendorId, isMandatory, isProtected);
    
    for (DiameterAvp child : childs) {
      grouped.addAvp(child.getCode(), child.byteArrayValue(), child.getVendorId(), child.getMandatoryRule() == 1, child.getProtectedRule() == 1);
    }

    return grouped;
  }

  /**
   * Method for obtaining AVP with given code and any Vendor-Id.
   * 
   * @param avpCode the code of the AVP to look for
   * @param set the set of AVPs where to look
   * 
   * @return an AVP with the given code, or null if none is present.
   */
  public static Avp getAvp(int avpCode, AvpSet set)
  {
    return set.getAvp(avpCode);
  }
  

  /**
   * Method for obtaining AVP with given code and Vendor-Id.
   * 
   * @param avpCode the code of the AVP to look for
   * @param vendorId the Vendor-Id of the AVP to look for
   * @param set the set of AVPs where to look
   * 
   * @return an AVP with the given code and Vendor-Id, or null if none is present.
   */
  public static Avp getAvp(int avpCode, long vendorId, AvpSet set)
  {
    return set.getAvp(avpCode, vendorId);
  }
  
  /**
   * Method for removing AVP with given code.
   * 
   * @param avpCode the code of the AVP to be removed
   * @param set the AvpSet to remove the AVP from
   */
  public static void removeAvp(int avpCode, AvpSet set)
  {
    set.removeAvp(avpCode);
  }

}
