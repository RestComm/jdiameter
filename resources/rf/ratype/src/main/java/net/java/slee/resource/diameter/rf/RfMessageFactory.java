package net.java.slee.resource.diameter.rf;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;

public interface RfMessageFactory {

  public DiameterMessageFactory getBaseMessageFactory();

  public AccountingRequest createRfAccountingRequest(AccountingRecordType accountingrecordtype);
}
