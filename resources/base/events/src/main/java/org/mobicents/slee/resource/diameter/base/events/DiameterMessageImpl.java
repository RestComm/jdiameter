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
package org.mobicents.slee.resource.diameter.base.events;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.AvpUtilities;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.base.events.avp.DiameterURI;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.RedirectHostUsageType;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.FailedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.ProxyInfoAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

/**
 * Super class for all diameter messages <br>
 * <br>
 * Super project: mobicents <br>
 * 13:25:46 2008-05-08 <br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public abstract class DiameterMessageImpl implements DiameterMessage {

  private Logger log = Logger.getLogger(DiameterMessageImpl.class);

  protected Message message = null;

  /**
   * Constructor taking a jDiameter {@link Message} as argument.
   * 
   * @param message the jDiameter Message object to create the DiameterMessage from
   */
  public DiameterMessageImpl(Message message)
  {
    this.message = message;
  }

  // Begin of DiameterMessage Implementation

  /**
   * This method returns long name of this message type - Like
   * Device-Watchdog-Request
   * 
   * @return
   */
  public abstract String getLongName();

  /**
   * This method return short name of this message type - for instance DWR,DWA
   * for DeviceWatchdog message
   * 
   * @return
   */
  public abstract String getShortName();

  public DiameterHeader getHeader()
  {
    return new DiameterHeaderImpl(this.message);
  }

  public DiameterCommand getCommand()
  {
    return new DiameterCommandImpl(this.message.getCommandCode(), this.message.getApplicationId(), getShortName(), getLongName(), this.message.isRequest(), this.message.isProxiable());
  }

  public DiameterAvp[] getAvps()
  {
    DiameterAvp[] avps = new DiameterAvp[0];

    try {
      avps = getAvpsInternal(message.getAvps());
    }
    catch (Exception e) {
      log.error("Failed to obtain/decode AVP/data.", e);
    }

    return avps;
  }

  public DiameterAvp[] getExtensionAvps() {
    return getAvps();
  }

  public void setExtensionAvps(DiameterAvp... avps) throws AvpNotAllowedException {
    for (DiameterAvp a : avps) {
      this.addAvp(a);
    }
  }

  public Object clone() {
    // TODO
    return null;
  }

  // AVP Getters and Setters

  public boolean hasSessionId() {
    return hasAvp(Avp.SESSION_ID);
  }

  public String getSessionId() {
    return getAvpAsUTF8String(Avp.SESSION_ID);
  }

  public void setSessionId(String sessionId) {
    addAvp(Avp.SESSION_ID, sessionId);
  }

  public boolean hasOriginHost() {
    return hasAvp(Avp.ORIGIN_HOST);
  }

  public DiameterIdentity getOriginHost() {
    return getAvpAsDiameterIdentity(Avp.ORIGIN_HOST);
  }

  public void setOriginHost(DiameterIdentity originHost) {
    addAvp(Avp.ORIGIN_HOST, originHost);
  }

  public boolean hasOriginRealm() {
    return hasAvp(Avp.ORIGIN_REALM);
  }

  public DiameterIdentity getOriginRealm() {
    return getAvpAsDiameterIdentity(Avp.ORIGIN_REALM);
  }

  public void setOriginRealm(DiameterIdentity originRealm) {
    addAvp(Avp.ORIGIN_REALM, originRealm);
  }

  public boolean hasDestinationHost() {
    return hasAvp(Avp.DESTINATION_HOST);
  }

  public DiameterIdentity getDestinationHost() {
    return getAvpAsDiameterIdentity(Avp.DESTINATION_HOST);
  }

  public void setDestinationHost(DiameterIdentity destinationHost) {
    addAvp(Avp.DESTINATION_HOST, destinationHost);
  }

  public boolean hasDestinationRealm() {
    return hasAvp(Avp.DESTINATION_REALM);
  }

  public DiameterIdentity getDestinationRealm() {
    return getAvpAsDiameterIdentity(Avp.DESTINATION_REALM);
  }

  public void setDestinationRealm(DiameterIdentity destinationRealm) {
    addAvp(Avp.DESTINATION_REALM, destinationRealm);
  }

  public boolean hasAcctApplicationId() {
    return hasAvp(Avp.ACCT_APPLICATION_ID);
  }

  public long getAcctApplicationId() {
    return getAvpAsUnsigned32(Avp.ACCT_APPLICATION_ID);
  }

  public void setAcctApplicationId(long acctApplicationId) {
    addAvp(Avp.ACCT_APPLICATION_ID, acctApplicationId);
  }

  public boolean hasAuthApplicationId() {
    return hasAvp(Avp.AUTH_APPLICATION_ID);
  }

  public long getAuthApplicationId() {
    return getAvpAsUnsigned32(Avp.AUTH_APPLICATION_ID);
  }

  public void setAuthApplicationId(long authApplicationId) {
    addAvp(Avp.AUTH_APPLICATION_ID, authApplicationId);
  }

  public boolean hasErrorMessage() {
    return hasAvp(Avp.ERROR_MESSAGE);
  }

  public String getErrorMessage() {
    return getAvpAsUTF8String(Avp.ERROR_MESSAGE);
  }

  public void setErrorMessage(String errorMessage) {
    addAvp(Avp.ERROR_MESSAGE, errorMessage);
  }

  public boolean hasErrorReportingHost() {
    return hasAvp(Avp.ERROR_REPORTING_HOST);
  }

  public DiameterIdentity getErrorReportingHost() {
    return getAvpAsDiameterIdentity(Avp.ERROR_REPORTING_HOST);
  }

  public void setErrorReportingHost(DiameterIdentity errorReportingHost) {
    addAvp(Avp.ERROR_REPORTING_HOST, errorReportingHost);
  }

  public boolean hasEventTimestamp() {
    return hasAvp(Avp.EVENT_TIMESTAMP);
  }

  public Date getEventTimestamp() {
    return getAvpAsTime(Avp.EVENT_TIMESTAMP);
  }

  public void setEventTimestamp(Date eventTimestamp) {
    addAvp(Avp.EVENT_TIMESTAMP, eventTimestamp);
  }

  public boolean hasOriginStateId() {
    return hasAvp(Avp.ORIGIN_STATE_ID);
  }

  public long getOriginStateId() {
    return getAvpAsUnsigned32(Avp.ORIGIN_STATE_ID);
  }

  public void setOriginStateId(long originStateId) {
    addAvp(Avp.ORIGIN_STATE_ID, originStateId);
  }

  public boolean hasResultCode() {
    return hasAvp(Avp.RESULT_CODE);
  }

  public long getResultCode() {
    return getAvpAsUnsigned32(Avp.RESULT_CODE);
  }

  public void setResultCode(long resultCode) {
    addAvp(Avp.RESULT_CODE, resultCode);
  }

  public boolean hasFailedAvp() {
    return hasAvp(Avp.FAILED_AVP);
  }

  public FailedAvp[] getFailedAvps() {
    return (FailedAvp[]) getAvpsAsCustom(Avp.FAILED_AVP, FailedAvpImpl.class);
  }

  public FailedAvp getFailedAvp() {
    return (FailedAvp) getAvpAsCustom(Avp.FAILED_AVP, FailedAvpImpl.class);
  }

  public void setFailedAvp(FailedAvp failedAvp) {
    addAvp(Avp.FAILED_AVP, failedAvp.getExtensionAvps());
  }

  public void setFailedAvps(FailedAvp[] failedAvps) {
    for (FailedAvp f : failedAvps) {
      setFailedAvp(f);
    }
  }

  public boolean hasUserName() {
    return hasAvp(Avp.USER_NAME);
  }

  public String getUserName() {
    return getAvpAsUTF8String(Avp.USER_NAME);
  }

  public void setUserName(String userName) {
    addAvp(Avp.USER_NAME, userName);
  }

  public boolean hasProxyInfo() {
    return hasAvp(Avp.PROXY_INFO);
  }

  public void setProxyInfo(ProxyInfoAvp proxyInfo)
  {
    addAvp(Avp.PROXY_INFO, proxyInfo.byteArrayValue());
  }

  public ProxyInfoAvp[] getProxyInfos() {
    return (ProxyInfoAvp[]) getAvpsAsCustom(Avp.PROXY_INFO, ProxyInfoAvpImpl.class);
  }

  public void setProxyInfos(ProxyInfoAvp[] proxyInfos) {
    for (ProxyInfoAvp p : proxyInfos) {
      setProxyInfo(p);
    }
  }

  public boolean hasRedirectHostUsage() {
    return hasAvp(Avp.REDIRECT_HOST_USAGE);
  }

  public void setRedirectHostUsage(RedirectHostUsageType redirectHostUsage) {
    addAvp(Avp.REDIRECT_HOST_USAGE, (long)redirectHostUsage.getValue());
  }

  public RedirectHostUsageType getRedirectHostUsage() {
    return (RedirectHostUsageType) getAvpAsEnumerated(Avp.REDIRECT_HOST_USAGE, RedirectHostUsageType.class);
  }

  public boolean hasRedirectMaxCacheTime() {
    return hasAvp(Avp.REDIRECT_MAX_CACHE_TIME);
  }

  public void setRedirectMaxCacheTime(long redirectMaxCacheTime) {
    addAvp(Avp.REDIRECT_MAX_CACHE_TIME, redirectMaxCacheTime);
  }

  public long getRedirectMaxCacheTime() {
    return getAvpAsUnsigned32(Avp.REDIRECT_MAX_CACHE_TIME);
  }

  public boolean hasRedirectHosts() {
    return hasAvp(Avp.REDIRECT_HOST);
  }

  public DiameterURI[] getRedirectHosts() {
    return getAvpsAsDiameterURI(Avp.REDIRECT_HOST);
  }

  public void setRedirectHost(DiameterURI redirectHost) {
    addAvp(Avp.REDIRECT_HOST, redirectHost);
  }

  public void setRedirectHosts(DiameterURI[] redirectHosts) {
    for (DiameterURI uri : redirectHosts) {
      setRedirectHost(uri);
    }
  }

  public boolean hasRouteRecords() {
    return hasAvp(Avp.ROUTE_RECORD);
  }

  public DiameterIdentity[] getRouteRecords() {
    return getAvpsAsDiameterIdentity(Avp.ROUTE_RECORD);
  }

  public void setRouteRecord(DiameterIdentity routeRecord) {
    addAvp(Avp.ROUTE_RECORD, routeRecord);
  }

  public void setRouteRecords(DiameterIdentity[] routeRecords) {
    for (DiameterIdentity routeRecord : routeRecords) {
      setRouteRecord(routeRecord);
    }
  }

  public boolean hasVendorSpecificApplicationId() {
    return hasAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
  }

  public VendorSpecificApplicationIdAvp getVendorSpecificApplicationId() {
    return (VendorSpecificApplicationIdAvp) getAvpAsCustom( Avp.VENDOR_SPECIFIC_APPLICATION_ID, VendorSpecificApplicationIdAvpImpl.class );
  }

  public void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp vsaid)
  {
    addAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, vsaid.byteArrayValue());
  }

  public Message getGenericData() {
    return message;
  }

  @Override
  public String toString() {
    DiameterHeader header = this.getHeader();

    String toString = "\r\n" + "+----------------------------------- HEADER ----------------------------------+\r\n" + "| Version................." + header.getVersion() + "\r\n"
    + "| Message-Length.........." + header.getMessageLength() + "\r\n" + "| Command-Flags..........." + "R[" + header.isRequest() + "] P[" + header.isProxiable() + "] " + "E["
    + header.isError() + "] T[" + header.isPotentiallyRetransmitted() + "]" + "\r\n" + "| Command-Code............" + this.getHeader().getCommandCode() + "\r\n"
    + "| Application-Id.........." + this.getHeader().getApplicationId() + "\r\n" + "| Hop-By-Hop Identifier..." + this.getHeader().getHopByHopId() + "\r\n" + "| End-To-End Identifier..."
    + this.getHeader().getEndToEndId() + "\r\n" + "+------------------------------------ AVPs -----------------------------------+\r\n";

    for (Avp avp : this.getGenericData().getAvps()) {
      toString += printAvp(avp, "");
    }

    toString += "+-----------------------------------------------------------------------------+\r\n";

    return toString;
  }

  // ===== AVP Management =====

  private DiameterAvp[] getAvpsInternal(AvpSet set) throws Exception {
    List<DiameterAvp> acc = new ArrayList<DiameterAvp>();

    for (Avp a : set) {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(a.getCode(), a.getVendorId());

      if (avpRep == null) {
        //log.error("Avp with code: " + a.getCode() + " VendorId: " + a.getVendorId() + " is not listed in dictionary, skipping!");
        continue;
      } else if (avpRep.getType().equals("Grouped")) {
        GroupedAvpImpl gAVP = new GroupedAvpImpl(a.getCode(), a.getVendorId(), a.isMandatory() ? 1 : 0, a.isEncrypted() ? 1 : 0, a.getRaw());

        gAVP.setExtensionAvps(getAvpsInternal(a.getGrouped()));

        // This is a grouped AVP... let's make it like that.
        acc.add(gAVP);
      } else {
        acc.add(new DiameterAvpImpl(a.getCode(), a.getVendorId(), a.isMandatory() ? 1 : 0, a.isEncrypted() ? 1 : 0, a.getRaw(), null));
      }
    }

    return acc.toArray(new DiameterAvp[0]);
  }

  private String printAvp(Avp avp, String indent) {
    Object avpValue = null;
    String avpString = "";
    boolean isGrouped = false;

    try {
      String avpType = AvpDictionary.INSTANCE.getAvp(avp.getCode(), avp.getVendorId()).getType();

      if ("Integer32".equals(avpType) || "AppId".equals(avpType)) {
        avpValue = avp.getInteger32();
      } else if ("Unsigned32".equals(avpType) || "VendorId".equals(avpType)) {
        avpValue = avp.getUnsigned32();
      } else if ("Float64".equals(avpType)) {
        avpValue = avp.getFloat64();
      } else if ("Integer64".equals(avpType)) {
        avpValue = avp.getInteger64();
      } else if ("Time".equals(avpType)) {
        avpValue = avp.getTime();
      } else if ("Unsigned64".equals(avpType)) {
        avpValue = avp.getUnsigned64();
      } else if ("Grouped".equals(avpType)) {
        avpValue = "<Grouped>";
        isGrouped = true;
      } else {
        avpValue = avp.getOctetString().replaceAll("\r", "").replaceAll("\n", "");
      }
    } catch (Exception ignore) {
      try {
        avpValue = avp.getOctetString().replaceAll("\r", "").replaceAll("\n", "");
      } catch (AvpDataException e) {
        avpValue = avp.toString();
      }
    }

    avpString += "| " + indent + "AVP: Code[" + avp.getCode() + "] VendorID[" + avp.getVendorId() + "] Value[" + avpValue + "] Flags[M=" + avp.isMandatory() + ";E=" + avp.isEncrypted() + ";V="
    + avp.isVendorId() + "]\r\n";

    if (isGrouped) {
      try {
        for (Avp subAvp : avp.getGrouped()) {
          avpString += printAvp(subAvp, indent + "  ");
        }
      } catch (AvpDataException e) {
        // Failed to ungroup... ignore then...
      }
    }

    return avpString;
  }

  protected void reportAvpFetchError(String msg, long code) {
    log.error("Failed to fetch avp, code: " + code + ". Message: " + msg);
  }

  // AVP Utilities Proxy Methods

  protected Date getAvpAsTime(int code)
  {
    return AvpUtilities.getAvpAsTime(code, message.getAvps());
  }

  protected Date getAvpAsTime(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsTime(code, vendorId, message.getAvps());
  }

  protected Date[] getAvpsAsTime(int code)
  {
    return AvpUtilities.getAvpsAsTime(code, message.getAvps());
  }

  protected Date[] getAvpsAsTime(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsTime(code, vendorId, message.getAvps());
  }

  protected void setAvpAsTime(int code, long vendorId, Date value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsTime(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected float getAvpAsFloat32(int code)
  {
    return AvpUtilities.getAvpAsFloat32(code, message.getAvps());
  }

  protected float getAvpAsFloat32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsFloat32(code, vendorId, message.getAvps());
  }

  protected float[] getAvpsAsFloat32(int code)
  {
    return AvpUtilities.getAvpsAsFloat32(code, message.getAvps());
  }

  protected float[] getAvpsAsFloat32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsFloat32(code, vendorId, message.getAvps());
  }

  protected void setAvpAsFloat32(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat32(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected double getAvpAsFloat64(int code)
  {
    return AvpUtilities.getAvpAsFloat64(code, message.getAvps());
  }

  protected double getAvpAsFloat64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsFloat64(code, vendorId, message.getAvps());
  }

  protected double[] getAvpsAsFloat64(int code)
  {
    return AvpUtilities.getAvpsAsFloat64(code, message.getAvps());
  }

  protected double[] getAvpsAsFloat64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsFloat64(code, vendorId, message.getAvps());
  }

  protected void setAvpAsFloat64(int code, long vendorId, float value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsFloat64(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected byte[] getAvpAsGrouped(int code)
  {
    return AvpUtilities.getAvpAsGrouped(code, message.getAvps());
  }

  protected byte[] getAvpAsGrouped(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsGrouped(code, vendorId, message.getAvps());
  }

  protected byte[][] getAvpsAsGrouped(int code)
  {
    return AvpUtilities.getAvpsAsGrouped(code, message.getAvps());
  }

  protected byte[][] getAvpsAsGrouped(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsGrouped(code, vendorId, message.getAvps());
  }

  protected AvpSet setAvpAsGrouped(int code, long vendorId, DiameterAvp[] childs, boolean isMandatory, boolean isProtected)
  {
    return AvpUtilities.setAvpAsGrouped(message, code, vendorId, message.getAvps(), isMandatory, isProtected, childs);
  }

  protected int getAvpAsInteger32(int code)
  {
    return AvpUtilities.getAvpAsInteger32(code, message.getAvps());
  }

  protected int getAvpAsInteger32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsInteger32(code, vendorId, message.getAvps());
  }

  protected int[] getAvpsAsInteger32(int code)
  {
    return AvpUtilities.getAvpsAsInteger32(code, message.getAvps());
  }

  protected int[] getAvpsAsInteger32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsInteger32(code, vendorId, message.getAvps());
  }

  protected void setAvpAsInteger32(int code, long vendorId, int value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger32(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected long getAvpAsInteger64(int code)
  {
    return AvpUtilities.getAvpAsInteger64(code, message.getAvps());
  }

  protected long getAvpAsInteger64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsInteger64(code, vendorId, message.getAvps());
  }

  protected long[] getAvpsAsInteger64(int code)
  {
    return AvpUtilities.getAvpsAsInteger64(code, message.getAvps());
  }

  protected long[] getAvpsAsInteger64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsInteger64(code, vendorId, message.getAvps());
  }

  protected void setAvpAsInteger64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsInteger64(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected long getAvpAsUnsigned32(int code)
  {
    return AvpUtilities.getAvpAsUnsigned32(code, message.getAvps());
  }

  protected long getAvpAsUnsigned32(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUnsigned32(code, vendorId, message.getAvps());
  }

  protected long[] getAvpsAsUnsigned32(int code)
  {
    return AvpUtilities.getAvpsAsUnsigned32(code, message.getAvps());
  }

  protected long[] getAvpsAsUnsigned32(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUnsigned32(code, vendorId, message.getAvps());
  }

  protected void setAvpAsUnsigned32(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned32(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected long getAvpAsUnsigned64(int code)
  {
    return AvpUtilities.getAvpAsUnsigned64(code, message.getAvps());
  }

  protected long getAvpAsUnsigned64(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUnsigned64(code, vendorId, message.getAvps());
  }

  protected long[] getAvpsAsUnsigned64(int code)
  {
    return AvpUtilities.getAvpsAsUnsigned64(code, message.getAvps());
  }

  protected long[] getAvpsAsUnsigned64(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUnsigned64(code, vendorId, message.getAvps());
  }

  protected void setAvpAsUnsigned64(int code, long vendorId, long value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUnsigned64(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected String getAvpAsUTF8String(int code)
  {
    return AvpUtilities.getAvpAsUTF8String(code, message.getAvps());
  }

  protected String getAvpAsUTF8String(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsUTF8String(code, vendorId, message.getAvps());
  }

  protected String[] getAvpsAsUTF8String(int code)
  {
    return AvpUtilities.getAvpsAsUTF8String(code, message.getAvps());
  }

  protected String[] getAvpsAsUTF8String(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsUTF8String(code, vendorId, message.getAvps());
  }

  protected void setAvpAsUTF8String(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsUTF8String(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected String getAvpAsOctetString(int code)
  {
    return AvpUtilities.getAvpAsOctetString(code, message.getAvps());
  }

  protected String getAvpAsOctetString(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsOctetString(code, vendorId, message.getAvps());
  }

  protected String[] getAvpsAsOctetString(int code)
  {
    return AvpUtilities.getAvpsAsOctetString(code, message.getAvps());
  }

  protected String[] getAvpsAsOctetString(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsOctetString(code, vendorId, message.getAvps());
  }

  protected void setAvpAsOctetString(int code, long vendorId, String value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsOctetString(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected byte[] getAvpAsRaw(int code)
  {
    return AvpUtilities.getAvpAsRaw(code, message.getAvps());
  }

  protected byte[] getAvpAsRaw(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsRaw(code, vendorId, message.getAvps());
  }

  protected byte[][] getAvpsAsRaw(int code)
  {
    return AvpUtilities.getAvpsAsRaw(code, message.getAvps());
  }

  protected byte[][] getAvpsAsRaw(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsRaw(code, vendorId, message.getAvps());
  }

  protected void setAvpAsRaw(int code, long vendorId, byte[] value, boolean isMandatory, boolean isProtected)
  {
    AvpUtilities.setAvpAsRaw(message, code, vendorId, message.getAvps(), isMandatory, isProtected, value);
  }

  protected Object getAvpAsCustom(int code, Class clazz)
  {
    return AvpUtilities.getAvpAsCustom(code, message.getAvps(), clazz);
  }

  protected Object getAvpAsCustom(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpAsCustom(code, vendorId, message.getAvps(), clazz);
  }

  protected Object[] getAvpsAsCustom(int code, Class clazz)
  {
    return AvpUtilities.getAvpsAsCustom(code, message.getAvps(), clazz);
  }

  protected Object[] getAvpsAsCustom(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpsAsCustom(code, vendorId, message.getAvps(), clazz);
  }

  protected DiameterIdentity getAvpAsDiameterIdentity(int code)
  {
    return AvpUtilities.getAvpAsDiameterIdentity(code, message.getAvps());
  }

  protected DiameterIdentity getAvpAsDiameterIdentity(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsDiameterIdentity(code, vendorId, message.getAvps());
  }

  protected DiameterIdentity[] getAvpsAsDiameterIdentity(int code)
  {
    return AvpUtilities.getAvpsAsDiameterIdentity(code, message.getAvps());
  }

  protected DiameterIdentity[] getAvpsAsDiameterIdentity(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsDiameterIdentity(code, vendorId, message.getAvps());
  }

  protected DiameterURI getAvpAsDiameterURI(int code)
  {
    return AvpUtilities.getAvpAsDiameterURI(code, message.getAvps());
  }

  protected DiameterURI getAvpAsDiameterURI(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsDiameterURI(code, vendorId, message.getAvps());
  }

  protected DiameterURI[] getAvpsAsDiameterURI(int code)
  {
    return AvpUtilities.getAvpsAsDiameterURI(code, message.getAvps());
  }

  protected DiameterURI[] getAvpsAsDiameterURI(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsDiameterURI(code, vendorId, message.getAvps());
  }

  protected Address getAvpAsAddress(int code)
  {
    return AvpUtilities.getAvpAsAddress(code, message.getAvps());
  }

  protected Address getAvpAsAddress(int code, long vendorId)
  {
    return AvpUtilities.getAvpAsAddress(code, vendorId, message.getAvps());
  }

  protected Address[] getAvpsAsAddress(int code)
  {
    return AvpUtilities.getAvpsAsAddress(code, message.getAvps());
  }

  protected Address[] getAvpsAsAddress(int code, long vendorId)
  {
    return AvpUtilities.getAvpsAsAddress(code, vendorId, message.getAvps());
  }

  protected Object getAvpAsEnumerated(int code, Class clazz)
  {
    return AvpUtilities.getAvpAsEnumerated(code, message.getAvps(), clazz);
  }

  protected Object getAvpAsEnumerated(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpAsEnumerated(code, vendorId, message.getAvps(), clazz);
  }

  protected Object[] getAvpsAsEnumerated(int code, Class clazz)
  {
    return AvpUtilities.getAvpsAsEnumerated(code, message.getAvps(), clazz);
  }

  protected Object[] getAvpsAsEnumerated(int code, long vendorId, Class clazz)
  {
    return AvpUtilities.getAvpsAsEnumerated(code, vendorId, message.getAvps(), clazz);
  }

  protected void addAvp(String avpName, Object avp)
  {
    AvpUtilities.addAvp(message, avpName, message.getAvps(), avp);
  }

  protected void addAvp(int avpCode, Object avp)
  {
    AvpUtilities.addAvp(message, avpCode, 0L, message.getAvps(), avp);
  }

  protected void addAvp(int avpCode, long vendorId, Object avp)
  {
    AvpUtilities.addAvp(message, avpCode, vendorId, message.getAvps(), avp);
  }

  protected boolean hasAvp(int code)
  {
    return AvpUtilities.hasAvp(code, 0L, message.getAvps());
  }

  protected boolean hasAvp(int code, long vendorId)
  {
    return AvpUtilities.hasAvp(code, vendorId, message.getAvps());
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
    AvpUtilities.addAvp(avp, message.getAvps());
  }
  
  
  //some hack
	private transient Object data = null;

	public void setData(Object d) {
		this.data = d;
	}

	public Object removeData() {
		Object o = this.data;
		this.data = null;
		return o;

	}

	public Object getData() {

		return this.data;

	}
  
}
