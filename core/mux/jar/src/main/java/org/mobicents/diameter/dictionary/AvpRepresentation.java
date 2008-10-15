package org.mobicents.diameter.dictionary;

public class AvpRepresentation
{
  private String name;
  private String description;
  private int code;
  private boolean mayEncrypt;
  private String ruleMandatory;
  private String ruleProtected;
  private String ruleVendorBit;
  private String vendorId;
  private boolean constrained;
  private String type;

  enum Rule { MUST, MAY, MUSTNOT, SHOULDNOT };
  
  public AvpRepresentation( String name, String description, int code, boolean mayEncrypt, String ruleMandatory, String ruleProtected, String ruleVendorBit, String vendorId, boolean constrained, String type )
  {
    this.name = name;
    this.description = description;
    this.code = code;
    this.mayEncrypt = mayEncrypt;
    this.ruleMandatory = ruleMandatory;
    this.ruleProtected = ruleProtected;
    this.ruleVendorBit = ruleVendorBit;
    this.vendorId = vendorId;
    this.constrained = constrained;
    this.type = type;
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

  public String getRuleProtected()
  {
    return ruleProtected;
  }

  public String getRuleVendorBit()
  {
    return ruleVendorBit;
  }

  public String getVendorId()
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


public String toString() {
	String value="AVP: "+name+", CODE: "+code+", VID: "+vendorId+", RM: "+ruleMandatory+", RP: "+ruleProtected+", RV: "+ruleVendorBit+", DESC: "+description;
	return value;
}
  
  
  
}