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

import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
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

  private static transient final Logger logger = Logger.getLogger(AvpUtilities.class);

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

  public static String getAvpAsOctetString(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getOctetString() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type OctetString.", e);
      return null;
    }
  }

  public static String getAvpAsOctetString(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getOctetString() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type OctetString.", e);
      return null;
    }
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

  public static String getAvpAsUTF8String(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getUTF8String() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type UTF8String.", e);
      return null;
    }
  }

  public static String getAvpAsUTF8String(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getUTF8String() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type UTF8String.", e);
      return null;
    }
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

  public static long getAvpAsUnsigned32(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getUnsigned32() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Unsigned32.", e);
      return Long.MIN_VALUE;
    }
  }

  public static long getAvpAsUnsigned32(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getUnsigned32() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Unsigned32.", e);
      return Long.MIN_VALUE;
    }
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

  public static long getAvpAsUnsigned64(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getUnsigned64() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Unsigned64.", e);
      return Long.MIN_VALUE;
    }
  }

  public static long getAvpAsUnsigned64(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getUnsigned64() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Unsigned64.", e);
      return Long.MIN_VALUE;
    }
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

  public static int getAvpAsInteger32(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getInteger32() : Integer.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Integer32.", e);
      return Integer.MIN_VALUE;
    }
  }

  public static int getAvpAsInteger32(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getInteger32() : Integer.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Integer32.", e);
      return Integer.MIN_VALUE;
    }
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

  public static long getAvpAsInteger64(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getInteger64() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Integer64.", e);
      return Long.MIN_VALUE;
    }
  }

  public static long getAvpAsInteger64(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getInteger64() : Long.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Integer64.", e);
      return Long.MIN_VALUE;
    }
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

  public static float getAvpAsFloat32(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getFloat32() : Float.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Float32.", e);
      return Float.MIN_VALUE;
    }
  }

  public static float getAvpAsFloat32(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getFloat32() : Float.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Float32.", e);
      return Float.MIN_VALUE;
    }
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

  public static double getAvpAsFloat64(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getFloat64() : Double.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Float64.", e);
      return Double.MIN_VALUE;
    }
  }

  public static double getAvpAsFloat64(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getFloat64() : Double.MIN_VALUE;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Float64.", e);
      return Double.MIN_VALUE;
    }
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

  public static byte[] getAvpAsRaw(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getRaw() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Raw.", e);
      return  null;
    }
  }

  public static byte[] getAvpAsRaw(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getRaw() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Raw.", e);
      return null;
    }
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

  public static Date getAvpAsTime(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getTime() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Time.", e);
      return  null;
    }
  }

  public static Date getAvpAsTime(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getTime() : null;
    }
    catch (AvpDataException e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Time.", e);
      return null;
    }
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

  public static byte[] getAvpAsGrouped(int avpCode, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode);
      return avp != null ? avp.getRawData() : null;
    }
    catch (Exception e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " as type Grouped.", e);
      return  null;
    }
  }

  public static byte[] getAvpAsGrouped(int avpCode, long vendorId, AvpSet set)
  {
    try {
      Avp avp = set.getAvp(avpCode, vendorId);
      return avp != null ? avp.getRawData() : null;
    }
    catch (Exception e) {
      logger.debug("Failed to obtain AVP with code " + avpCode + " and Vendor-Id " + vendorId + " as type Grouped.", e);
      return null;
    }
  }

  /**
   * Adds AVP to {@link AvpSet} as Grouped with the given code and Base Vendor-Id (0).
   * 
   * @param avpCode the code of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static AvpSet setAvpAsGrouped(int avpCode, AvpSet set, DiameterAvp[] childs)
  {
    return setAvpAsGrouped(avpCode, _DEFAULT_VENDOR_ID, set, childs);
  }

  /**
   * Adds AVP to {@link AvpSet} as Grouped with the given code and given Vendor-Id.
   * 
   * @param avpCode the code of the AVP
   * @param vendorId the Vendor-Id of the AVP
   * @param set the Vendor-Id of the AVP
   * @param value the value of the AVP to add
   */
  public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, AvpSet set, DiameterAvp[] childs)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if (rep != null) {
      return setAvpAsGrouped(avpCode, vendorId, set, rep.isMandatory(), rep.isProtected(), childs);
    }
    else {
      return setAvpAsGrouped(avpCode, vendorId, set, _DEFAULT_MANDATORY, _DEFAULT_PROTECTED, childs);
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
  public static AvpSet setAvpAsGrouped(int avpCode, long vendorId, AvpSet set, boolean isMandatory, boolean isProtected, DiameterAvp[] childs)
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
  public static Avp getJDAvp(int avpCode, AvpSet set)
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
  public static Avp getJDAvp(int avpCode, long vendorId, AvpSet set)
  {
    return set.getAvp(avpCode, vendorId);
  }

  public static void addAvp(String avpName, AvpSet set, Object avp)
  {
    AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpName);

    if(rep != null)
    {
      addAvp(rep.getCode(), rep.getVendorId(), set, avp);
    }
  }

  public static void addAvp(int avpCode, AvpSet set, Object avp)
  {
    addAvp(avpCode, 0L, set, avp);
  }

  /**
   * Method for adding AVP with given code and Vendor-Id to the given set.
   * 
   * @param avpCode the code of the AVP to look for
   * @param vendorId the Vendor-Id of the AVP to be added
   * @param avp the AVP object
   * @param set the AvpSet where to add the AVP
   */
  public static void addAvp(int avpCode, long vendorId, AvpSet set, Object avp)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if(avpRep != null)
    {
      DiameterAvpType avpType = DiameterAvpType.fromString(avpRep.getType());

      boolean isMandatoryAvp = !(avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot"));
      boolean isProtectedAvp = avpRep.getRuleProtected().equals("must");

      if(avp instanceof byte[])
      {
        setAvpAsRaw(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (byte[]) avp);
      }
      else
      {
        switch (avpType.getType())
        {
        case DiameterAvpType._ADDRESS:
        case DiameterAvpType._DIAMETER_IDENTITY:
        case DiameterAvpType._DIAMETER_URI:
        case DiameterAvpType._IP_FILTER_RULE:
        case DiameterAvpType._OCTET_STRING:
        case DiameterAvpType._QOS_FILTER_RULE:
        {
          setAvpAsOctetString(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (String) avp);
          break;
        }
        case DiameterAvpType._ENUMERATED:
        case DiameterAvpType._INTEGER_32:
        {
          setAvpAsInteger32(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Integer) avp);        
          break;
        }
        case DiameterAvpType._FLOAT_32:
        {
          setAvpAsFloat32(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Float) avp);        
          break;
        }
        case DiameterAvpType._FLOAT_64:
        {
          setAvpAsFloat64(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Float) avp);        
          break;
        }
        case DiameterAvpType._GROUPED:
        {
          setAvpAsGrouped(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (DiameterAvp[]) avp);
          break;
        }
        case DiameterAvpType._INTEGER_64:
        {
          setAvpAsInteger64(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Integer) avp);
          break;
        }
        case DiameterAvpType._TIME:
        {
          setAvpAsTime(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Date) avp);
          break;
        }
        case DiameterAvpType._UNSIGNED_32:
        {
          setAvpAsUnsigned32(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Long) avp);
          break;
        }
        case DiameterAvpType._UNSIGNED_64:
        {
          setAvpAsUnsigned64(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (Long) avp);
          break;
        }
        case DiameterAvpType._UTF8_STRING:
        {
          setAvpAsUTF8String(avpCode, vendorId, set, isMandatoryAvp, isProtectedAvp, (String) avp);
          break;
        }
        }
      }
    }
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

  public static Object getAvp(int avpCode, AvpSet set)
  {
    Avp avp = set.getAvp(avpCode);
    
    if(avp != null) {
      return getAvp(avp.getCode(), avp.getVendorId(), set);
    }
    
    return null;
  }

  public static Object getAvp(String avpName, AvpSet set)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpName);
    
    if(avpRep != null) {
      return getAvp(avpRep.getCode(), avpRep.getVendorId(), set);
    }
    
    return null;
  }
  
  public static Object getAvp(int avpCode, long vendorId, AvpSet set)
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);

    if(avpRep != null)
    {
      DiameterAvpType avpType = DiameterAvpType.fromString(avpRep.getType());

      switch (avpType.getType())
      {
      case DiameterAvpType._ADDRESS:
      {
        return AddressAvp.decode( getAvpAsRaw(avpCode, vendorId, set) );
      }
      case DiameterAvpType._DIAMETER_IDENTITY:
      {
        return new DiameterIdentity(getAvpAsOctetString(avpCode, vendorId, set));
      }
      case DiameterAvpType._DIAMETER_URI:
      {
        try
        {
          return new DiameterURI(getAvpAsOctetString(avpCode, vendorId, set));
        }
        catch ( URISyntaxException e )
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      case DiameterAvpType._IP_FILTER_RULE:
      {
        return new IPFilterRule(getAvpAsOctetString(avpCode, vendorId, set));
      }
      case DiameterAvpType._OCTET_STRING:
      {
        return getAvpAsOctetString(avpCode, vendorId, set);
      }
      case DiameterAvpType._QOS_FILTER_RULE:
      {
        return getAvpAsOctetString(avpCode, vendorId, set);
      }
      case DiameterAvpType._ENUMERATED:
      case DiameterAvpType._INTEGER_32:
      {
        return getAvpAsInteger32(avpCode, vendorId, set);        
      }
      case DiameterAvpType._FLOAT_32:
      {
        return getAvpAsFloat32(avpCode, vendorId, set);        
      }
      case DiameterAvpType._FLOAT_64:
      {
        return getAvpAsFloat64(avpCode, vendorId, set);        
      }
      case DiameterAvpType._GROUPED:
      {
        return getAvpAsGrouped(avpCode, vendorId, set);
      }
      case DiameterAvpType._INTEGER_64:
      {
        return getAvpAsInteger64(avpCode, vendorId, set);
      }
      case DiameterAvpType._TIME:
      {
        return getAvpAsTime(avpCode, vendorId, set);
      }
      case DiameterAvpType._UNSIGNED_32:
      {
        return getAvpAsUnsigned32(avpCode, vendorId, set);
      }
      case DiameterAvpType._UNSIGNED_64:
      {
        return getAvpAsUnsigned64(avpCode, vendorId, set);
      }
      case DiameterAvpType._UTF8_STRING:
      {
        return getAvpAsUTF8String(avpCode, vendorId, set);
      }
      default:
      {
        return getAvpAsRaw(avpCode, vendorId, set);
      }
      }
    }
    
    return null;
  }
  
  public static Object getAvpAsCustom(int avpCode, AvpSet set, Class clazz)
  {
    return getAvpAsCustom(avpCode, 0L, set, clazz);
  }
  
  public static Object getAvpAsCustom(int avpCode, long vendorId, AvpSet set, Class clazz)
  {
    Avp avp = set.getAvp(avpCode, vendorId);

    if (avp != null)
    {
      AvpRepresentation rep = AvpDictionary.INSTANCE.getAvp(avpCode, vendorId);
      
      Constructor c = null;

      try {
        c = clazz.getConstructor(int.class, long.class, int.class, int.class, byte[].class);
        return c.newInstance(rep.getCode(), rep.getVendorId(), rep.getRuleMandatoryAsInt(), rep.getRuleProtectedAsInt(), avp.getRawData());
      }
      catch (Exception e) {
        logger.error( "Failed to obtain AVP constructor.", e );
        return null;
      }
    }
    
    return null;
  }
}
