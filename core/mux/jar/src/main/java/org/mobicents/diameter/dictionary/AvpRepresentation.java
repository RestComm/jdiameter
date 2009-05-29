/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution statements
 * applied by the authors. All third-party contributions are distributed under
 * license by Red Hat Middleware LLC.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.diameter.dictionary;

/**
 * 
 * AvpRepresentation.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @version 1.0 
 */
public class AvpRepresentation
{
  private String name;
  private String description;
  private int code;
  private boolean mayEncrypt;
  private boolean _protected;
  private boolean _mandatory;
  private String ruleMandatory;
  private String ruleProtected;
  private String ruleVendorBit;
  private long vendorId;
  private boolean constrained;
  private String type;

  private final static String _DEFAULT_MANDATORY = "may";
  private final static String _DEFAULT_PROTECTED = "may";
  private final static String _DEFAULT_VENDOR = "mustnot";

  enum Rule { must, may, mustnot, shouldnot };

  public enum Type { OctetString, Integer32, Integer64, Unsigned32, Unsigned64, Float32, Float64, Grouped, Address, Time, UTF8String, DiameterIdentity, DiameterURI, Enumerated, IPFilterRule, QoSFilterRule };

  public AvpRepresentation( String name, String description, int code, boolean mayEncrypt, String ruleMandatory, String ruleProtected, String ruleVendorBit, long vendorId, boolean constrained, String type )
  {
    this.name = name;
    this.description = description;
    this.code = code;
    this.mayEncrypt = mayEncrypt;
    this.ruleMandatory = ruleMandatory;
    this.ruleProtected = ruleProtected;
    this.ruleVendorBit = ruleVendorBit;
    
    if(this.ruleMandatory==null|| this.ruleMandatory.equals(""))
      this.ruleMandatory=_DEFAULT_MANDATORY;
    if(this.ruleProtected==null|| this.ruleProtected.equals(""))
      this.ruleProtected=_DEFAULT_PROTECTED;
    if(this.ruleVendorBit==null|| this.ruleVendorBit.equals(""))
      this.ruleVendorBit=_DEFAULT_VENDOR;

    this.vendorId = vendorId;
    this.constrained = constrained;
    this.type = type;
    this._mandatory=this.ruleMandatory.equals("must");
    this._protected=this.ruleProtected.equals("must");
  }

  public String getName()
  {
    return name;
  }

  public String getDescription()
  {
    return description;
  }

  public int getCode()
  {
    return code;
  }

  public boolean isMayEncrypt()
  {
    return mayEncrypt;
  }

  public String getRuleMandatory()
  {
    return ruleMandatory;
  }

  public int getRuleMandatoryAsInt()
  {
    return Rule.valueOf(ruleMandatory).ordinal();
  }
  
  public String getRuleProtected()
  {
    return ruleProtected;
  }

  public int getRuleProtectedAsInt()
  {
    return Rule.valueOf(ruleProtected).ordinal();
  }
  
  public String getRuleVendorBit()
  {
    return ruleVendorBit;
  }

  public int getRuleVendorBitAsInt()
  {
    return Rule.valueOf(ruleVendorBit).ordinal();
  }
  
  public long getVendorId()
  {
    return vendorId;
  }

  public boolean isConstrained()
  {
    return constrained;
  }

  public String getType()
  {
    return type;
  }

  public boolean isProtected()
  {
    return _protected;
  }

  public boolean isMandatory()
  {
    return _mandatory;
  }

  public String toString()
  {
    String value="AVP: "+name+", CODE: "+code+", VID: "+vendorId+", RM: "+ruleMandatory+", RP: "+ruleProtected+", RV: "+ruleVendorBit+", DESC: "+description;
    return value;
  }

}