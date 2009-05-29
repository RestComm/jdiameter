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

import net.java.slee.resource.diameter.base.events.AccountingMessage;
import net.java.slee.resource.diameter.base.events.avp.AccountingRealtimeRequiredType;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;

import org.jdiameter.api.Avp;
import org.jdiameter.api.Message;

/**
 * Start time:16:56:38 2009-05-22<br>
 * Project: diameter-parent<br>
 * Super class for ACX messages Super class implementing methods for ACR and
 * ACA. it implements methods from {@link AccountingMessage}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see DiameterMessageImpl
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
    return hasAvp(Avp.ACC_RECORD_TYPE);
  }

  public AccountingRecordType getAccountingRecordType() {
    return AccountingRecordType.fromInt(getAvpAsInteger32(Avp.ACC_RECORD_TYPE));
  }

  public void setAccountingRecordType(AccountingRecordType accountingRecordType) {
    addAvp(Avp.ACC_RECORD_TYPE, accountingRecordType.getValue());
  }

  public boolean hasAccountingRecordNumber() {
    return hasAvp(Avp.ACC_RECORD_NUMBER);
  }

  public long getAccountingRecordNumber() {
    return getAvpAsUnsigned32(Avp.ACC_RECORD_NUMBER);
  }

  public void setAccountingRecordNumber(long accountingRecordNumber) {
    addAvp(Avp.ACC_RECORD_NUMBER, accountingRecordNumber);
  }

  public boolean hasAccountingSubSessionId() {
    return message.getAvps().getAvp(Avp.ACC_SUB_SESSION_ID) != null;
  }

  public long getAccountingSubSessionId() {
    return getAvpAsUnsigned32(Avp.ACC_SUB_SESSION_ID);
  }

  public void setAccountingSubSessionId(long accountingSubSessionId) {
    addAvp(Avp.ACC_SUB_SESSION_ID, accountingSubSessionId);
  }

  public boolean hasAccountingSessionId() {
    return super.hasAvp(Avp.ACC_SESSION_ID);
  }

  public byte[] getAccountingSessionId() {
      return getAvpAsOctetString(Avp.ACC_SESSION_ID).getBytes();
  }

  public void setAccountingSessionId(byte[] accountingSessionId) {
    addAvp(Avp.ACC_SESSION_ID, new String(accountingSessionId));
  }

  public boolean hasAcctMultiSessionId() {
    return message.getAvps().getAvp(Avp.ACC_MULTI_SESSION_ID) != null;
  }

  public String getAcctMultiSessionId() {
    return getAvpAsUTF8String(Avp.ACC_MULTI_SESSION_ID);
  }

  public void setAcctMultiSessionId(String acctMultiSessionId) {
    addAvp(Avp.ACC_MULTI_SESSION_ID, acctMultiSessionId);
  }

  public boolean hasAcctInterimInterval() {
    return message.getAvps().getAvp(Avp.ACCT_INTERIM_INTERVAL) != null;
  }

  public long getAcctInterimInterval() {
    return getAvpAsUnsigned32(Avp.ACCT_INTERIM_INTERVAL);
  }

  public void setAcctInterimInterval(long acctInterimInterval) {
    addAvp(Avp.ACCT_INTERIM_INTERVAL, acctInterimInterval);
  }

  public boolean hasAccountingRealtimeRequired() {
    return message.getAvps().getAvp(Avp.ACCOUNTING_REALTIME_REQUIRED) != null;
  }

  public AccountingRealtimeRequiredType getAccountingRealtimeRequired() {
    return AccountingRealtimeRequiredType.fromInt(getAvpAsInteger32(Avp.ACCOUNTING_REALTIME_REQUIRED));
  }

  public void setAccountingRealtimeRequired(AccountingRealtimeRequiredType accountingRealtimeRequired) {
    addAvp(Avp.ACCOUNTING_REALTIME_REQUIRED, accountingRealtimeRequired.getValue());
  }

}
