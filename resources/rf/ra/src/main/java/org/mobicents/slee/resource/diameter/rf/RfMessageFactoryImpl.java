package org.mobicents.slee.resource.diameter.rf;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.events.AccountingRequest;
import net.java.slee.resource.diameter.base.events.avp.AccountingRecordType;
import net.java.slee.resource.diameter.rf.RfMessageFactory;

import org.apache.log4j.Logger;
import org.jdiameter.api.Stack;
import org.mobicents.slee.resource.diameter.base.DiameterMessageFactoryImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvpImpl;

/**
 * 
 * RfMessageFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>1:59:52 AM Apr 14, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfMessageFactoryImpl extends DiameterMessageFactoryImpl implements RfMessageFactory {

  private static Logger logger = Logger.getLogger( RfMessageFactoryImpl.class );

  private DiameterMessageFactoryImpl baseMessageFactory;
  
  public RfMessageFactoryImpl(DiameterMessageFactoryImpl baseMessageFactory, Stack stack)
  {
    super(stack);

    this.baseMessageFactory = baseMessageFactory;
  }

  public AccountingRequest createRfAccountingRequest( AccountingRecordType accountingrecordtype )
  {
    AccountingRequest acr = super.createAccountingRequest();
    acr.setAcctApplicationId( _RF_ACC_APP_ID );
    if(logger.isDebugEnabled())
    {
      logger.debug( "Created Rf ACR with Accounting-Record-Type[" + accountingrecordtype + "]" );
    }
    
    acr.setAccountingRecordType( accountingrecordtype );

    return acr;
  }

  public DiameterMessageFactory getBaseMessageFactory()
  {
    return this.baseMessageFactory;
  }


}
