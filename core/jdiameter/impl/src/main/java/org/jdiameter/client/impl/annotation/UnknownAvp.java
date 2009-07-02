package org.jdiameter.client.impl.annotation;

public class UnknownAvp extends Value<byte[]> {

  private int code;
  boolean m,v,p;
  private long vendorId;

  public UnknownAvp(int code, boolean m, boolean v, boolean p, long vendorId, byte[] value) {
    super(value);
    this.code = code;
    this.m = m;
    this.v = v;
    this.p = p;
    this.vendorId = vendorId;
  }

  public int getCode() {
    return code;
  }

  public boolean isMandatory() {
    return m;
  }

  public boolean isVendorSpecific() {
    return v;
  }

  public long getVendorId() {
    return vendorId;
  }

  public boolean isProxiable() {
    return p;
  }
}