package net.java.slee.resource.diameter.rf;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;

/**
 * 
 * Used by applications to create Diameter Rf request messages.
 * 
 * Rf answer messages can be created using the RfServerSessionActivity.createRfAccountingAnswer() methods. 
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RfMessageFactory {

  public static final long _RF_TGPP_VENDOR_ID = 10415L;
  public static final int  _RF_ACC_APP_ID = 3;

  /**
   * Get a factory to create AVPs and messages defined by Diameter Base. 
   * 
   * @return
   */
  public DiameterMessageFactory getBaseMessageFactory();

  /**
   * Creates an Accounting Request message with the Accounting-Record-Type AVP set. 
   * 
   * @param accountingrecordtype
   * @return
   */
  public AccountingRequest createRfAccountingRequest(AccountingRecordType accountingrecordtype);

}
