package org.mobicents.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.ro.events.avp.TalkBurstExchange;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;


/**
 * TalkBurstExchangeImpl.java
 *
 * <br>Project:  mobicents
 * <br>12:30:32 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class TalkBurstExchangeImpl extends GroupedAvpImpl implements TalkBurstExchange {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public TalkBurstExchangeImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

}
