/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
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
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;

import org.jdiameter.api.Message;
import org.mobicents.slee.resource.diameter.base.events.DiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;

/**
 * CCA Credit-Control-Request/Answer common message implementation.<br>
 * <br>
 *  
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class CreditControlMessageImpl extends DiameterMessageImpl implements CreditControlMessage {

  /**
   * Constructor.
   * @param message the Message object to be wrapped
   */
  public CreditControlMessageImpl(Message message) {
    super(message);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getAcctMultiSessionId()
   */
  public String getAcctMultiSessionId()
  {
    return getAvpAsUTF8String(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestNumber()
   */
  public long getCcRequestNumber()
  {
    return getAvpAsUnsigned32(CreditControlAVPCodes.CC_Request_Number);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestType()
   */
  public CcRequestType getCcRequestType()
  {
    return (CcRequestType) getAvpAsEnumerated(CreditControlAVPCodes.CC_Request_Type, CcRequestType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcSubSessionId()
   */
  public long getCcSubSessionId()
  {
    return getAvpAsUnsigned64(CreditControlAVPCodes.CC_Sub_Session_Id);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getMultipleServicesCreditControls()
   */
  public MultipleServicesCreditControlAvp[] getMultipleServicesCreditControls()
  {
    return (MultipleServicesCreditControlAvp[]) getAvpsAsCustom(CreditControlAVPCodes.Multiple_Services_Credit_Control, MultipleServicesCreditControlAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasMultipleServicesCreditControl()
   */
  public boolean hasMultipleServicesCreditControl()
  {
    return hasAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasAcctMultiSessionId()
   */
  public boolean hasAcctMultiSessionId()
  {
    return hasAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestNumber()
   */
  public boolean hasCcRequestNumber()
  {
    return hasAvp(CreditControlAVPCodes.CC_Request_Number);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestType()
   */
  public boolean hasCcRequestType()
  {
    return hasAvp(CreditControlAVPCodes.CC_Request_Type);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcSubSessionId()
   */
  public boolean hasCcSubSessionId()
  {
    return hasAvp(CreditControlAVPCodes.CC_Sub_Session_Id);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setAcctMultiSessionId(java.lang.String)
   */
  public void setAcctMultiSessionId(String acctMultiSessionId) throws IllegalStateException
  {
    addAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID, acctMultiSessionId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestNumber(long)
   */
  public void setCcRequestNumber(long ccRequestNumber) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.CC_Request_Number, ccRequestNumber);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestType(net.java.slee.resource.diameter.cca.events.avp.CcRequestType)
   */
  public void setCcRequestType(CcRequestType ccRequestType) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.CC_Request_Type, (long)ccRequestType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcSubSessionId(long)
   */
  public void setCcSubSessionId(long ccSubSessionId) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.CC_Sub_Session_Id, ccSubSessionId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControl(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp)
   */
  public void setMultipleServicesCreditControl(MultipleServicesCreditControlAvp multipleServicesCreditControl) throws IllegalStateException
  {
    addAvp(CreditControlAVPCodes.Multiple_Services_Credit_Control, multipleServicesCreditControl.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControls(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp[])
   */
  public void setMultipleServicesCreditControls(MultipleServicesCreditControlAvp[] multipleServicesCreditControls) throws IllegalStateException
  {
    for(MultipleServicesCreditControlAvp multipleServicesCreditControl : multipleServicesCreditControls) {
      setMultipleServicesCreditControl(multipleServicesCreditControl);
    }
  }

}
