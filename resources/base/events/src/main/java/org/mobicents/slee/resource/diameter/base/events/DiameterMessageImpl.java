package org.mobicents.slee.resource.diameter.base.events;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.java.slee.resource.diameter.base.events.DiameterCommand;
import net.java.slee.resource.diameter.base.events.DiameterHeader;
import net.java.slee.resource.diameter.base.events.DiameterMessage;
import net.java.slee.resource.diameter.base.events.avp.AddressAvp;
import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterURIAvp;
import net.java.slee.resource.diameter.base.events.avp.FailedAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import net.java.slee.resource.diameter.base.events.avp.RedirectHostUsageType;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpUtilities;
import org.mobicents.slee.resource.diameter.base.events.avp.AddressAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterIdentityAvpImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterURIAvpImpl;
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
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author Erick Svenson
 */
public abstract class DiameterMessageImpl implements DiameterMessage {

	protected Message message = null;
	// FIXME:baranowb; is this the way ? Erick?
	protected boolean destinationHostMandatory = false;
	protected boolean destinationRealmMandatory = false;
	protected boolean originHostMandatory = false;
	protected boolean originRealmMandatory = false;

	protected Logger log = Logger.getLogger(DiameterMessageImpl.class);

	public DiameterMessageImpl(Message message) {
		this.message = message;
	}

	protected void addAvpAsByteArray(int code, byte[] value, boolean mandatory) {
		message.getAvps().addAvp(code, value, mandatory, false);
	}

	public Object clone() {
		// TODO
		return null;
	}

	// ======== GETTERS
	protected AddressAvp[] getAvpAsAddress(int code) {

		AvpSet avps = message.getAvps().getAvps(code);
		if (avps == null)
			return null;

		AddressAvp[] r = new AddressAvp[avps.size()];

		for (int i = 0; i < avps.size(); i++) {
			try {
				r[i] = AddressAvpImpl.decode(avps.getAvpByIndex(i).getRaw());
			} catch (AvpDataException e) {
			  log.error( "", e );
				return null;
			}
		}
		return r;

	}

	public long getAcctApplicationId() {
		return getAvpAsUInt32(Avp.ACCT_APPLICATION_ID);
	}

	protected DiameterIdentityAvp[] getAllAvpAsIdentity(int code) {
		List<DiameterIdentityAvp> acc = new ArrayList<DiameterIdentityAvp>();
		// FIXME: baranowb; fix this somehow?
		int proxiable = 1;
		for (Avp a : message.getAvps().getAvps(code)) {
			try {
				acc.add(new DiameterIdentityAvpImpl(a.getCode(), a
						.getVendorId(), a.isMandatory() ? 1 : 0, proxiable, a
						.getRaw()));
			} catch (Exception e) {
				log.warn(e);
				return null;
			}
		}
		return acc.toArray(new DiameterIdentityAvp[0]);
	}

	public long getAuthApplicationId() {
		return getAvpAsUInt32(Avp.AUTH_APPLICATION_ID);
	}

	protected Date getAvpAsDate(int code) {
		try {
			return message.getAvps().getAvp(code).getTime();
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}

	protected DiameterIdentityAvp getAvpAsIdentity(int code) {
		try {
			Avp rawAvp = message.getAvps().getAvp(code);
			// FIXME: baranowb; fix this somehow?
			int proxiable = 1;
			return rawAvp != null ? new DiameterIdentityAvpImpl(rawAvp
					.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1
					: 0, proxiable, rawAvp.getRaw()) : null;
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}

	protected int getAvpAsInt32(int code) {
		try {
			return message.getAvps().getAvp(code).getInteger32();
		} catch (Exception e) {
			log.warn(e);
			return -1;
		}
	}

	protected long getAvpAsUInt32(int code) {
		try {
			return message.getAvps().getAvp(code).getUnsigned32();
		} catch (Exception e) {
			log.warn(e);
			return 0;
		}
	}

	protected String getAvpAsUtf8(int code) {
		try {
			return message.getAvps().getAvp(code).getUTF8String();
		} catch (Exception e) {
			log.warn(e);
			return null;
		}
	}

	protected long[] getAvpsAsUInt32(int code) {
		AvpSet avps = message.getAvps().getAvps(code);
		if (avps != null) {
			long[] r = new long[avps.size()];
			for (int i = 0; i < avps.size(); i++) {
				try {
					r[i] = avps.getAvpByIndex(i).getUnsigned32();
				} catch (AvpDataException e) {

					e.printStackTrace();
					return null;
				}

			}
			return r;
		} else {
			return null;
		}
	}

	protected int[] getAvpsAsInt32(int code) {
		AvpSet avps = message.getAvps().getAvps(code);
		if (avps != null) {
			int[] r = new int[avps.size()];
			for (int i = 0; i < avps.size(); i++) {
				try {
					r[i] = avps.getAvpByIndex(i).getInteger32();
				} catch (AvpDataException e) {

	        log.error( "", e );
					return null;
				}

			}
			return r;
		} else {
			return null;
		}
	}

	public DiameterAvp[] getAvps() {
    DiameterAvp[] acc = new DiameterAvp[0];
    
    try
    {
      acc = getAvpsInternal(message.getAvps());
    }
    catch ( Exception e )
    {
      log.error("", e);
    }
    
    return acc;
	}

	public DiameterCommand getCommand() {

		return new DiameterCommandImpl(this.message.getCommandCode(),
				this.message.getApplicationId(), getShortName(), getLongName(),
				this.message.isRequest(), this.message.isProxiable());
	}

	public DiameterIdentityAvp getDestinationHost() {
		return getAvpAsIdentity(Avp.DESTINATION_HOST);
	}

	public DiameterIdentityAvp getDestinationRealm() {
		return getAvpAsIdentity(Avp.DESTINATION_REALM);
	}

	public String getErrorMessage() {
		return getAvpAsUtf8(Avp.ERROR_MESSAGE);
	}

	public DiameterIdentityAvp getErrorReportingHost() {
		return getAvpAsIdentity(Avp.ERROR_REPORTING_HOST);
	}

	public Date getEventTimestamp() {
		return getAvpAsDate(55);
	}

	public DiameterAvp[] getExtensionAvps() {
	    return getAvps();
	  }

	public FailedAvp[] getFailedAvps() {
		List<FailedAvp> acc = new ArrayList<FailedAvp>();
		for (Avp i : message.getAvps().getAvps(Avp.FAILED_AVP)) {
			try {
				acc.add(new FailedAvpImpl(i.getCode(), i.getVendorId(), i
						.isMandatory() ? 1 : 0, i.isEncrypted() ? 1 : 0, i
						.getRaw()));
			} catch (Exception exc) {
				log.warn(exc);
			}
		}
		return acc.toArray(new FailedAvp[0]);
	}

	public Message getGenericData() {
		return message;
	}

	public DiameterHeader getHeader()
	{
		// FIXME: baranowb; how do we set properly length?
		DiameterHeaderImpl dh = new DiameterHeaderImpl(
		    this.message.getApplicationId(), this.message.getHopByHopIdentifier(),
				this.message.getEndToEndIdentifier(), 0, this.message.getCommandCode(),
				this.message.isRequest(), this.message.isProxiable(), 
				this.message.isError(), this.message.isReTransmitted()
		);

		return dh;
	}

	/**
	 * This method returns long name of this message type - Like
	 * Device-Watchdog-Request
	 * 
	 * @return
	 */
	public abstract String getLongName();

	public DiameterIdentityAvp getOriginHost() {
		return getAvpAsIdentity(Avp.ORIGIN_HOST);
	}

	public DiameterIdentityAvp getOriginRealm() {
		return getAvpAsIdentity(Avp.ORIGIN_REALM);
	}

	public long getOriginStateId() {
		return getAvpAsUInt32(Avp.ORIGIN_STATE_ID);
	}

	public ProxyInfoAvp[] getProxyInfos() {
		List<ProxyInfoAvp> acc = new ArrayList<ProxyInfoAvp>();
		for (Avp i : message.getAvps().getAvps(Avp.PROXY_INFO)) {
			try {
				acc.add(new ProxyInfoAvpImpl(i.getCode(), i.getVendorId(), i
						.isMandatory() ? 1 : 0, i.isEncrypted() ? 1 : 0, i
						.getRaw()));
			} catch (Exception exc) {
				log.warn(exc);
			}
		}
		return acc.toArray(new ProxyInfoAvp[0]);
	}

	public DiameterURIAvp[] getRedirectHosts() {
		List<DiameterURIAvp> acc = new ArrayList<DiameterURIAvp>();
		for (DiameterIdentityAvp i : getAllAvpAsIdentity(Avp.REDIRECT_HOST))
			try {
				acc.add(new DiameterURIAvpImpl(i.toString()));
			} catch (URISyntaxException e) {
				log.warn("", e);
			}
		return acc.toArray(new DiameterURIAvpImpl[0]);
	}

	public RedirectHostUsageType getRedirectHostUsage() {
		return RedirectHostUsageType
				.fromInt(getAvpAsInt32(Avp.REDIRECT_HOST_USAGE));
	}

	public long getRedirectMaxCacheTime() {
		return getAvpAsUInt32(Avp.REDIRECT_MAX_CACHE_TIME);
	}

	public long getResultCode() {
		return getAvpAsUInt32(Avp.RESULT_CODE);
	}

	public DiameterIdentityAvp[] getRouteRecords() {
		return getAllAvpAsIdentity(Avp.ROUTE_RECORD);
	}

	public String getSessionId() {

		return message.getSessionId();
	}

	/**
	 * This method return short name of this message type - for instance DWR,DWA
	 * for DeviceWatchdog message
	 * 
	 * @return
	 */
	public abstract String getShortName();

	public String getUserName() {
		return getAvpAsUtf8(Avp.USER_NAME);
	}

	public VendorSpecificApplicationIdAvp getVendorSpecificApplicationId() {
		try {
			Avp i = message.getAvps()
					.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID);
			return new VendorSpecificApplicationIdAvpImpl(i.getCode(), i
					.getVendorId(), i.isMandatory() ? 1 : 0,
					i.isEncrypted() ? 1 : 0, i.getRaw());
		} catch (Exception e) {
			log.warn("", e);
			return null;
		}
	}

	// -------- HAS SECTION

	public boolean hasAcctApplicationId() {
		return message.getAvps().getAvp(Avp.ACCT_APPLICATION_ID) != null;
	}

	public boolean hasAuthApplicationId() {
		return message.getAvps().getAvp(Avp.AUTH_APPLICATION_ID) != null;
	}

	public boolean hasDestinationHost() {
		return getDestinationHost() != null;
	}

	public boolean hasDestinationRealm() {
		return getDestinationRealm() != null;
	}

	public boolean hasErrorMessage() {
		return getErrorMessage() != null;
	}

	public boolean hasErrorReportingHost() {
		return getErrorReportingHost() != null;
	}

	public boolean hasEventTimestamp() {
		return getEventTimestamp() != null;
	}

	public boolean hasOriginHost() {
		return getOriginHost() != null;
	}

	public boolean hasOriginRealm() {
		return getOriginRealm() != null;
	}

	public boolean hasOriginStateId() {
		return message.getAvps().getAvp(Avp.ORIGIN_STATE_ID) != null;
	}

	public boolean hasRedirectHostUsage() {
		return getRedirectHostUsage() != null;
	}

	public boolean hasRedirectMaxCacheTime() {
		return message.getAvps().getAvp(Avp.REDIRECT_MAX_CACHE_TIME) != null;
	}

	public boolean hasResultCode() {
		return message.getAvps().getAvp(Avp.RESULT_CODE) != null;
	}

	public boolean hasSessionId() {
		return getSessionId() != null;
	}

	public boolean hasUserName() {
		return getUserName() != null;
	}

	public boolean hasVendorSpecificApplicationId() {
		return getVendorSpecificApplicationId() != null;
	}

	// =============== SETTERS

	public void setAvpAsAddress(int code, AddressAvp[] avps, boolean mandatory,
			boolean remove) {
		if (remove)
			message.getAvps().removeAvp(code);
		for (int i = 0; i < avps.length; i++) {
			try {
				message.getAvps().addAvp(code,
						InetAddress.getByAddress(avps[i].encode()), mandatory,
						false);
			} catch (UnknownHostException e) {

				e.printStackTrace();
				return;
			}
		}
	}

	public void setAcctApplicationId(long acctApplicationId) {
		setAvpAsUInt32(Avp.ACCT_APPLICATION_ID, acctApplicationId, true,true);
	}

	public void setAuthApplicationId(long authApplicationId) {
		setAvpAsUInt32(Avp.AUTH_APPLICATION_ID, authApplicationId, true,true);
	}

	protected void setAvpAsDate(int code, Date value, boolean mandatory,
			boolean remove) {
		
		AvpUtilities.setAvpAsDate(code, value, message.getAvps(), remove);
	}

	protected AvpSet setAvpAsGroup(int code, DiameterAvp[] childs,
			boolean mandatory, boolean remove) {
		return AvpUtilities.setAvpAsGrouped(code,0, childs, message.getAvps(), remove,mandatory,false);
	}

	protected void setAvpAsIdentity(int code, String value, boolean octet,
			boolean mandatory, boolean remove) {
		
		AvpUtilities.setAvpAsString(code, 0, octet, value, message.getAvps(), remove, mandatory, false);
	}

	protected void setAvpAsInt32(int code, int value, boolean mandatory,
			boolean remove) {
		AvpUtilities.setAvpAsInt32(code, 0, value, message.getAvps(), remove, mandatory, false);
	}

	protected void setAvpAsUInt32(int code, long value, boolean mandatory,
			boolean remove) {
		AvpUtilities.setAvpAsUInt32(code, 0, value, message.getAvps(), remove, mandatory, false);
	}

	protected void setAvpAsUtf8(int code, String value, boolean mandatory,
			boolean remove) {
		
		AvpUtilities.setAvpAsString(code, 0, false, value, message.getAvps(), remove, mandatory, false);
	}

	protected void setAvpsAsUInt32(int code, long[] values, boolean mandatory,
			boolean remove) {

		if (remove)
		{
			message.getAvps().removeAvp(code);
		}
			
		
		for (long a : values)
			setAvpAsUInt32(code, a, mandatory, false);

	}

	
	

	
	public void setDestinationHost(DiameterIdentityAvp destinationHost) {

		Avp rawAvp = this.message.getAvps().getAvp(Avp.DESTINATION_HOST);
		if (rawAvp == null) {
			// FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.DESTINATION_HOST,
					destinationHost.stringValue(),
					this.destinationHostMandatory, false, true);

		} else {

			throw new IllegalStateException(
					"Cant set Desintation-Host AVP again!!!");
		}
	}

	public void setDestinationRealm(DiameterIdentityAvp destinationRealm) {

		Avp rawAvp = this.message.getAvps().getAvp(Avp.DESTINATION_REALM);
		if (rawAvp == null) {
			// FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.DESTINATION_REALM,
					destinationRealm.stringValue(),
					this.destinationRealmMandatory, false, true);

		} else {

			throw new IllegalStateException(
					"Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setErrorMessage(String errorMessage) {
		setAvpAsUtf8(Avp.USER_NAME, errorMessage, false,true);
	}

	public void setErrorReportingHost(DiameterIdentityAvp errorReportingHost) {
		setAvpAsIdentity(Avp.ERROR_REPORTING_HOST, errorReportingHost
				.toString(), true, false,true);
	}

	public void setEventTimestamp(Date eventTimestamp) {
		setAvpAsDate(55, eventTimestamp, true,true);
	}

	 public void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException {
		    for (DiameterAvp a : avps)
		      addAvp(a);
		  }
	
	public void setFailedAvp(FailedAvp failedAvp) {
		setAvpAsGroup(Avp.FAILED_AVP, failedAvp.getExtensionAvps(), true, false);
	}

	public void setFailedAvps(FailedAvp[] failedAvps) {
		for (FailedAvp f : failedAvps)
			setFailedAvp(f);
	}

	public void setOriginHost(DiameterIdentityAvp originHost) {
		Avp rawAvp = this.message.getAvps().getAvp(Avp.ORIGIN_HOST);
		if (rawAvp == null) {
			// FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.ORIGIN_HOST,
					originHost.stringValue(), this.originHostMandatory, false,
					true);

		} else {

			throw new IllegalStateException(
					"Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setOriginRealm(DiameterIdentityAvp originRealm) {
		Avp rawAvp = this.message.getAvps().getAvp(Avp.ORIGIN_REALM);
		if (rawAvp == null) {
			// FIXME: Ignoring madnatoryRules from avp and pFlag?
			this.message.getAvps().addAvp(Avp.ORIGIN_REALM,
					originRealm.stringValue(), this.originHostMandatory, false,
					true);

		} else {

			throw new IllegalStateException(
					"Cant set Desintation-Realm AVP again!!!");
		}

	}

	public void setOriginStateId(long originStateId) {
		setAvpAsUInt32(Avp.ORIGIN_STATE_ID, originStateId, true,true);
	}

	public void setProxyInfo(ProxyInfoAvp proxyInfo) {
		AvpSet g = setAvpAsGroup(Avp.PROXY_INFO, proxyInfo.getExtensionAvps(),
				true, false);
		if (proxyInfo.hasProxyHost())
			g.addAvp(Avp.PROXY_HOST, proxyInfo.getProxyHost().toString(), true,
					true, false);
		if (proxyInfo.hasProxyState())
			g.addAvp(Avp.PROXY_STATE, proxyInfo.getProxyState(), true, false);
	}

	public void setProxyInfos(ProxyInfoAvp[] proxyInfos) {
		for (ProxyInfoAvp p : proxyInfos)
			setProxyInfo(p);
	}

	public void setRedirectHost(DiameterURIAvp redirectHost) {
		setAvpAsIdentity(Avp.REDIRECT_HOST, redirectHost.toString(), true,
				true, false);
	}

	public void setRedirectHosts(DiameterURIAvp[] redirectHosts) {
		for (DiameterURIAvp uri : redirectHosts)
			setRedirectHost(uri);
	}

	public void setRedirectHostUsage(RedirectHostUsageType redirectHostUsage) {
		setAvpAsInt32(Avp.REDIRECT_HOST_USAGE, redirectHostUsage.getValue(),
				true,true);
	}

	public void setRedirectMaxCacheTime(long redirectMaxCacheTime) {
		setAvpAsUInt32(Avp.REDIRECT_MAX_CACHE_TIME, redirectMaxCacheTime, true,true);
	}

	public void setResultCode(long resultCode) {
		setAvpAsUInt32(Avp.RESULT_CODE, resultCode, true,true);
	}

	public void setRouteRecord(DiameterIdentityAvp routeRecord) {
		setAvpAsIdentity(Avp.ROUTE_RECORD, routeRecord.toString(), true, true,
				false);
	}

	public void setRouteRecords(DiameterIdentityAvp[] routeRecords) {
		for (DiameterIdentityAvp i : routeRecords)
			setAvpAsIdentity(Avp.ROUTE_RECORD, i.toString(), true, true, false);
	}

	public void setSessionId(String sessionId) {

		Avp rawAvp = this.message.getAvps().getAvp(Avp.SESSION_ID);
		if (rawAvp == null) {
			// FIXME: Ignoring madnatoryRules from avp and pFlag?
			rawAvp = this.message.getAvps().addAvp(Avp.SESSION_ID, sessionId,
					true, false, true);

		} else {
		  this.message.getAvps().removeAvp( Avp.SESSION_ID );
		  this.message.getAvps().addAvp(Avp.SESSION_ID, sessionId, true, false, true);
			//throw new IllegalStateException("Cant set Session-Id AVP again!!!");
		}

	}

	public void setUserName(String userName) {
		setAvpAsUtf8(Avp.USER_NAME, userName, true,true);
	}

	public void setVendorSpecificApplicationId(VendorSpecificApplicationIdAvp id) {
		AvpSet g = setAvpAsGroup(Avp.VENDOR_SPECIFIC_APPLICATION_ID, id
				.getExtensionAvps(), true, false);
		g.addAvp(Avp.VENDOR_ID, (int) id.getVendorId(), true, false);
		if (id.hasAcctApplicationId())
			g.addAvp(Avp.ACCT_APPLICATION_ID, (int) id.getAcctApplicationId(),
					true, false);
		if (id.hasAuthApplicationId())
			g.addAvp(Avp.AUTH_APPLICATION_ID, (int) id.getAuthApplicationId(),
					true, false);
	}

	@Override
	public String toString()
	{
	  DiameterHeader header = this.getHeader();
	  
	  String toString = "\r\n" +
	      "+----------------------------------- HEADER ----------------------------------+\r\n" +
        "| Version................." + header.getVersion() + "\r\n" +
        "| Message-Length.........." + header.getMessageLength() + "\r\n" +
        "| Command-Flags..........." + "R[" + header.isRequest() + "] P[" + header.isProxiable() + "] " +
           "E[" + header.isError() + "] T[" + header.isPotentiallyRetransmitted() + "]" + "\r\n" +
        "| Command-Code............" + this.getHeader().getCommandCode() + "\r\n" +
        "| Application-Id.........." + this.getHeader().getApplicationId() + "\r\n" +
        "| Hop-By-Hop Identifier..." + this.getHeader().getHopByHopId() + "\r\n" +
        "| End-To-End Identifier..." + this.getHeader().getEndToEndId() + "\r\n" +
        "+------------------------------------ AVPs -----------------------------------+\r\n";

    for( Avp avp : this.getGenericData().getAvps() )
    {
      toString += printAvp( avp, "" );
    }
    
    toString += "+-----------------------------------------------------------------------------+\r\n";
    
    return toString;
//	  return "";
	}

	// ===== AVP Management =====
	
	public void addAvp(DiameterAvp avp)
	{
	  addAvpInternal( avp, message.getAvps() );
	}
	
	private void addAvpInternal(DiameterAvp avp, AvpSet set)
	{
	  if(avp.getType() == DiameterAvpType.GROUPED)
    {
      GroupedAvp gAvp = (GroupedAvp)avp;
      
      AvpSet groupedAvp = set.addGroupedAvp( gAvp.getCode(), gAvp.getVendorId(), gAvp.getMandatoryRule() != 2, gAvp.getProtectedRule() == 0 );
      
      for(DiameterAvp subAvp : gAvp.getExtensionAvps())
        addAvpInternal( subAvp, groupedAvp );
    }
    else
      set.addAvp( avp.getCode(), avp.byteArrayValue(), avp.getVendorId(), avp.getMandatoryRule() != 2, avp.getProtectedRule() == 0 );
	}
	
  private DiameterAvp[] getAvpsInternal(AvpSet set) throws Exception
  {
    List<DiameterAvp> acc = new ArrayList<DiameterAvp>();

    for (Avp a : set) 
    {
      // FIXME: alexandre: This is how I can check if it's a Grouped AVP... 
      // should use dictionary (again). a.getGrouped() get's into deadlock.
      if(AvpDictionary.INSTANCE.getAvp( a.getCode(), a.getVendorId() ).getType().equals("Grouped"))
      {
        GroupedAvpImpl gAVP = new GroupedAvpImpl(a.getCode(), a.getVendorId(), 
            a.isMandatory() ? 1 : 0, a.isEncrypted() ? 1: 0, a.getRaw());
        
        gAVP.setExtensionAvps( getAvpsInternal(a.getGrouped()) );
        
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
	
  private String printAvp(Avp avp, String indent)
  {
    Object avpValue = null;
    String avpString = "";
    boolean isGrouped = false;
    
    try
    {
      String avpType = AvpDictionary.INSTANCE.getAvp( avp.getCode(), avp.getVendorId() ).getType();
      
      if("Integer32".equals(avpType) || "AppId".equals(avpType))
      {
        avpValue = avp.getInteger32();
      }
      else if("Unsigned32".equals(avpType) || "VendorId".equals(avpType))
      {
        avpValue = avp.getUnsigned32();
      }
      else if("Float64".equals(avpType))
      {
        avpValue = avp.getFloat64();
      }
      else if("Integer64".equals(avpType))
      {
        avpValue = avp.getInteger64();
      }
      else if("Time".equals(avpType))
      {
        avpValue = avp.getTime();
      }
      else if("Unsigned64".equals(avpType))
      {
        avpValue = avp.getUnsigned64();
      }
      else if("Grouped".equals(avpType))
      {
        avpValue = "<Grouped>";
        isGrouped = true;
      }
      else
      {
        avpValue = avp.getOctetString().replaceAll( "\r", "" ).replaceAll( "\n", "" );
      }
    }
    catch (Exception ignore) {
      try
      {
        avpValue = avp.getOctetString().replaceAll( "\r", "" ).replaceAll( "\n", "" );
      }
      catch ( AvpDataException e ) {
        avpValue = avp.toString();
      }
    }
    
    avpString += "| " + indent + "AVP: Code[" + avp.getCode() + "] VendorID[" + avp.getVendorId() + "] Value[" + 
    avpValue + "] Flags[M=" + avp.isMandatory() + ";E=" + avp.isEncrypted() + ";V=" + avp.isVendorId() + "]\r\n";
    
    if(isGrouped)
    {
      try
      {
        for(Avp subAvp : avp.getGrouped())
        {
          avpString += printAvp( subAvp, indent + "  " );          
        }
      }
      catch ( AvpDataException e )
      {
        // Failed to ungroup... ignore then...
      }
    }

    return avpString;
  }
  protected boolean hasAvp(int code)
  {
	  return message.getAvps().getAvps(code)!=null;
  }
  
  protected void reportAvpFetchError(String msg, long code)
  {
	  log.error("Failed to fetch avp, code: "+code+". Message: "+msg);
  }
  
  
}
