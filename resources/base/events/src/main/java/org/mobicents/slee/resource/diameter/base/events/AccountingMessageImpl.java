/**
 * Start time:16:56:38 2009-05-22<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.AccountingMessage;
import net.java.slee.resource.diameter.base.events.avp.AccountingRealtimeRequiredType;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * Start time:16:56:38 2009-05-22<br>
 * Project: diameter-parent<br>
 * Super class for CEX messages
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class AccountingMessageImpl extends DiameterMessageImpl implements AccountingMessage {

	/**
	 * 
	 * @param message
	 */
	public AccountingMessageImpl(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public boolean hasAccountingRecordType() {
		return message.getAvps().getAvp(Avp.ACC_RECORD_TYPE) != null;
	}

	public AccountingRecordType getAccountingRecordType() {
		return AccountingRecordType.fromInt(getAvpAsInt32(Avp.ACC_RECORD_TYPE));
	}

	public void setAccountingRecordType(AccountingRecordType accountingRecordType) {
		setAvpAsInt32(Avp.ACC_RECORD_TYPE, accountingRecordType.getValue(), true, true);
	}

	public boolean hasAccountingRecordNumber() {
		return message.getAvps().getAvp(Avp.ACC_RECORD_NUMBER) != null;
	}

	public long getAccountingRecordNumber() {
		return getAvpAsUInt32(Avp.ACC_RECORD_NUMBER);
	}

	public void setAccountingRecordNumber(long accountingRecordNumber) {
		setAvpAsUInt32(Avp.ACC_RECORD_NUMBER, accountingRecordNumber, true, true);
	}

	public boolean hasAccountingSubSessionId() {
		return message.getAvps().getAvp(Avp.ACC_SUB_SESSION_ID) != null;
	}

	public long getAccountingSubSessionId() {
		return getAvpAsUInt32(Avp.ACC_SUB_SESSION_ID);
	}

	public void setAccountingSubSessionId(long accountingSubSessionId) {
		setAvpAsUInt32(Avp.ACC_SUB_SESSION_ID, accountingSubSessionId, true, true);
	}

	public boolean hasAccountingSessionId() {
		return super.hasAvp(Avp.ACC_SESSION_ID);
	}

	public byte[] getAccountingSessionId() {
		if(hasAccountingSessionId())
		{
			return super.getAvpAsOctet(Avp.ACC_SESSION_ID).getBytes();
		}
		
		return null;
		
	}

	public void setAccountingSessionId(byte[] accountingSessionId) {

		super.setAvpAsOctet(Avp.ACC_SESSION_ID, new String(accountingSessionId), true, true);
	}

	public boolean hasAcctMultiSessionId() {
		return message.getAvps().getAvp(Avp.ACC_MULTI_SESSION_ID) != null;
	}

	public String getAcctMultiSessionId() {
		return getAvpAsUtf8(Avp.ACC_MULTI_SESSION_ID);
	}

	public void setAcctMultiSessionId(String acctMultiSessionId) {
		setAvpAsUtf8(Avp.ACC_MULTI_SESSION_ID, acctMultiSessionId, true, true);
	}

	public boolean hasAcctInterimInterval() {
		return message.getAvps().getAvp(Avp.ACCT_INTERIM_INTERVAL) != null;
	}

	public long getAcctInterimInterval() {
		return getAvpAsUInt32(Avp.ACCT_INTERIM_INTERVAL);
	}

	public void setAcctInterimInterval(long acctInterimInterval) {
		setAvpAsUInt32(Avp.ACCT_INTERIM_INTERVAL, acctInterimInterval, true, true);
	}

	public boolean hasAccountingRealtimeRequired() {
		return message.getAvps().getAvp(Avp.ACCOUNTING_REALTIME_REQUIRED) != null;
	}

	public AccountingRealtimeRequiredType getAccountingRealtimeRequired() {
		return AccountingRealtimeRequiredType.fromInt(getAvpAsInt32(Avp.ACCOUNTING_REALTIME_REQUIRED));
	}

	public void setAccountingRealtimeRequired(AccountingRealtimeRequiredType accountingRealtimeRequired) {
		setAvpAsInt32(Avp.ACCOUNTING_REALTIME_REQUIRED, accountingRealtimeRequired.getValue(), true, true);
	}

}
