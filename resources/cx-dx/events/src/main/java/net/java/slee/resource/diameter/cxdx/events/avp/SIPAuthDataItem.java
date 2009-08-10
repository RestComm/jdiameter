package net.java.slee.resource.diameter.cxdx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * <pre>
 * <b>6.3.13 SIP-Auth-Data-Item AVP</b>
 * The SIP-Auth-Data-Item is of type Grouped, and contains the authentication and/or authorization
 * information for the Diameter client.
 * 
 * AVP format
 * SIP-Auth-Data-Item :: = < AVP Header : 612 10415 >
 *                    [ SIP-Item-Number ]
 *                    [ SIP-Authentication-Scheme ]
 *                    [ SIP-Authenticate ]
 *                    [ SIP-Authorization ]
 *                    [ SIP-Authentication-Context ]
 *                    [ Confidentiality-Key ]
 *                    [ Integrity-Key ]
 *                    [ SIP-Digest-Authenticate ]
 *                    [ Framed-IP-Address ]
 *                    [ Framed-IPv6-Prefix ]
 *                    [ Framed-Interface-Id ]
 *                  * [ Line-Identifier ]
 *                  * [AVP]
 * 
 * </pre>
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface SIPAuthDataItem extends GroupedAvp {

  /**
   * Returns true if the SIP-Item-Number AVP is present in the message.
   */
  public boolean hasSIPItemNumber();

  /**
   * Returns the value of the SIP-Item-Number AVP, of type Unsigned32.
   * A return value of Long.MIN_VALUE implies that the AVP has not been set or some error has been encountered.
   */
  public long getSIPItemNumber();

  /**
   * Sets the value of the SIP-Item-Number AVP, of type Unsigned32.
   * @throws IllegalStateException if setSIPItemNumber has already been called
   */
  public void setSIPItemNumber(long sipItemNumber);

  /**
   * Returns true if the SIP-Authentication-Scheme AVP is present in the message.
   */
  public boolean hasSIPAuthenticationScheme();

  /**
   * Returns the value of the SIP-Authentication-Scheme AVP, of type UTF8String.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getSIPAuthenticationScheme();

  /**
   * Sets the value of the SIP-Authentication-Scheme AVP, of type UTF8String.
   * @throws IllegalStateException if setSIPAuthenticationScheme has already been called
   */
  public void setSIPAuthenticationScheme(String sipAuthenticationScheme);

  /**
   * Returns true if the SIP-Authenticate AVP is present in the message.
   */
  public boolean hasSIPAuthenticate();

  /**
   * Returns the value of the SIP-Authenticate AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getSIPAuthenticate();

  /**
   * Sets the value of the SIP-Authenticate AVP, of type OctetString.
   * @throws IllegalStateException if setSIPAuthenticate has already been called
   */
  public void setSIPAuthenticate(String sipAuthenticate);

  /**
   * Returns true if the SIP-Authorization AVP is present in the message.
   */
  public boolean hasSIPAuthorization();

  /**
   * Returns the value of the SIP-Authorization AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getSIPAuthorization();

  /**
   * Sets the value of the SIP-Authorization AVP, of type OctetString.
   * @throws IllegalStateException if setSIPAuthorization has already been called
   */
  public void setSIPAuthorization(String sipAuthorization);

  /**
   * Returns true if the SIP-Authentication-Context AVP is present in the message.
   */
  public boolean hasSIPAuthenticationContext();

  /**
   * Returns the value of the SIP-Authentication-Context AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getSIPAuthenticationContext();

  /**
   * Sets the value of the SIP-Authentication-Context AVP, of type OctetString.
   * @throws IllegalStateException if setSIPAuthenticationContext has already been called
   */
  public void setSIPAuthenticationContext(String sipAuthenticationContext);

  /**
   * Returns true if the Confidentiality-Key AVP is present in the message.
   */
  public boolean hasConfidentialityKey();

  /**
   * Returns the value of the Confidentiality-Key AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getConfidentialityKey();

  /**
   * Sets the value of the Confidentiality-Key AVP, of type OctetString.
   * @throws IllegalStateException if setConfidentialityKey has already been called
   */
  public void setConfidentialityKey(String confidentialityKey);

  /**
   * Returns true if the Integrity-Key AVP is present in the message.
   */
  public boolean hasIntegrityKey();

  /**
   * Returns the value of the Integrity-Key AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getIntegrityKey();

  /**
   * Sets the value of the Integrity-Key AVP, of type OctetString.
   * @throws IllegalStateException if setIntegrityKey has already been called
   */
  public void setIntegrityKey(String integrityKey);

  /**
   * Returns true if the SIP-Digest-Authenticate AVP is present in the message.
   */
  public boolean hasSIPDigestAuthenticate();

  /**
   * Returns the value of the SIP-Digest-Authenticate AVP, of type Grouped.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public SIPDigestAuthenticate getSIPDigestAuthenticate();

  /**
   * Sets the value of the SIP-Digest-Authenticate AVP, of type Grouped.
   * @throws IllegalStateException if setSIPDigestAuthenticate has already been called
   */
  public void setSIPDigestAuthenticate(SIPDigestAuthenticate sipDigestAuthenticate);

  /**
   * Returns true if the Framed-IP-Address AVP is present in the message.
   */
  public boolean hasFramedIPAddress();

  /**
   * Returns the value of the Framed-IP-Address AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getFramedIPAddress();

  /**
   * Sets the value of the Framed-IP-Address AVP, of type OctetString.
   * @throws IllegalStateException if setFramedIPAddress has already been called
   */
  public void setFramedIPAddress(String framedIPAddress);

  /**
   * Returns true if the Framed-IPv6-Prefix AVP is present in the message.
   */
  public boolean hasFramedIPv6Prefix();

  /**
   * Returns the value of the Framed-IPv6-Prefix AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getFramedIPv6Prefix();

  /**
   * Sets the value of the Framed-IPv6-Prefix AVP, of type OctetString.
   * @throws IllegalStateException if setFramedIPv6Prefix has already been called
   */
  public void setFramedIPv6Prefix(String framedIPv6Prefix);

  /**
   * Returns true if the Framed-Interface-Id AVP is present in the message.
   */
  public boolean hasFramedInterfaceId();

  /**
   * Returns the value of the Framed-Interface-Id AVP, of type Unsigned64.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public long getFramedInterfaceId();

  /**
   * Sets the value of the Framed-Interface-Id AVP, of type Unsigned64.
   * @throws IllegalStateException if setFramedInterfaceId has already been called
   */
  public void setFramedInterfaceId(long framedInterfaceId);

  /**
   * Returns the value of the Line-Identifier AVP, of type OctetString.
   * A return value of null implies that the AVP has not been set or some error has been encountered.
   */
  public String getLineIdentifiers();

  /**
   * Sets the value of the Line-Identifier AVP, of type OctetString.
   * @throws IllegalStateException if setLineIdentifier has already been called
   */
  public void setLineIdentifier(String lineIdentifier);

  /**
   * Sets the value of the Line-Identifier AVP, of type OctetString.
   * @throws IllegalStateException if setLineIdentifier has already been called
   */
  public void setLineIdentifiers(String[] lineIdentifiers);

}
