package org.jdiameter.api.annotation;

/**
 * This enumerated class describe base type of avp.
 * For more information look RFC 3588
 */
public enum AvpType {
  OctetString,
  Integer32,
  Integer64,
  Unsigned32,
  Unsigned64,
  Float32,
  Float64,
  Grouped,
  Address,
  Time,
  UTF8String,
  DiameterIdentity,
  DiameterURI,
  Enumerated,
  IPFilterRule,
  QoSFilterRule
}