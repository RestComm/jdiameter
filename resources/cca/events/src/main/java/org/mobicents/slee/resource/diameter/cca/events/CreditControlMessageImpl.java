/**
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.IllegalAvpValueException;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;

/**
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlMessageImpl extends ExtensionDiameterMessageImpl
		implements CreditControlMessage {

	/**
	 * tttttt	
	 * @param message
	 */
	public CreditControlMessageImpl(Message message) {
		super(message);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getAcctMultiSessionId()
	 */
	public String getAcctMultiSessionId() {
		if(hasAcctMultiSessionId())
		{
			Avp raw=super.message.getAvps().getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			try {
				return raw.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestNumber()
	 */
	public long getCcRequestNumber() {
		if(hasCcRequestNumber())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			try {
				return raw.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_REQUEST_NUMBER);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestType()
	 */
	public CcRequestType getCcRequestType() {
		if(hasCcRequestType())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			try {
				return CcRequestType.EVENT_REQUEST.fromInt(raw.getInteger32());
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_REQUEST_TYPE);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcSubSessionId()
	 */
	public long getCcSubSessionId() {
		if(hasCcSubSessionId())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			try {
				return raw.getUnsigned64();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_SUB_SESSION_ID);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getMultipleServicesCreditControls()
	 */
	public MultipleServicesCreditControlAvp[] getMultipleServicesCreditControls() {
		if(hasMultipleServicesCreditControl())
		{
			AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			MultipleServicesCreditControlAvp[] avps =new MultipleServicesCreditControlAvp[set.size()];
			for(int index=0;index<set.size();index++)
			{
				Avp avp=set.getAvpByIndex(index);
				try {
					avps[index]=new MultipleServicesCreditControlAvpImpl(avp.getCode(),avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				} catch (AvpDataException e) {
					reportAvpFetchError("failed on index: "+index+", "+e, CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
					e.printStackTrace();
				}
			}
			return avps;
		}
		
		return null;
	}

	public boolean hasMultipleServicesCreditControl() {
		return super.hasAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasAcctMultiSessionId()
	 */
	public boolean hasAcctMultiSessionId() {
		return super.hasAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestNumber()
	 */
	public boolean hasCcRequestNumber() {
		return super.hasAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestType()
	 */
	public boolean hasCcRequestType() {
		return super.hasAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcSubSessionId()
	 */
	public boolean hasCcSubSessionId() {
		return super.hasAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setAcctMultiSessionId(java.lang.String)
	 */
	public void setAcctMultiSessionId(String acctMultiSessionId)
			throws IllegalStateException {
		if(hasAcctMultiSessionId())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID,acctMultiSessionId,protectedAvp==1,mandatoryAvp==1,false);
			
			
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestNumber(long)
	 */
	public void setCcRequestNumber(long ccRequestNumber)
			throws IllegalStateException {
		if(hasCcRequestNumber())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_NUMBER,ccRequestNumber,protectedAvp==1,mandatoryAvp==1,true);
			
			
		}


	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestType(net.java.slee.resource.diameter.cca.events.avp.CcRequestType)
	 */
	public void setCcRequestType(CcRequestType ccRequestType)
			throws IllegalStateException {
		if(hasCcRequestType())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_TYPE,ccRequestType.getValue(),protectedAvp==1,mandatoryAvp==1,false);
			
			
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcSubSessionId(long)
	 */
	public void setCcSubSessionId(long ccSubSessionId)
			throws IllegalStateException {
		if(hasCcSubSessionId())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_NUMBER,ccSubSessionId,protectedAvp==1,mandatoryAvp==1,true);
			
			
		}
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControl(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp)
	 */
	public void setMultipleServicesCreditControl(
			MultipleServicesCreditControlAvp multipleServicesCreditControl)
			throws IllegalStateException {
		this.setMultipleServicesCreditControls(new MultipleServicesCreditControlAvp[]{multipleServicesCreditControl});

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControls(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp[])
	 */
	public void setMultipleServicesCreditControls(
			MultipleServicesCreditControlAvp[] multipleServicesCreditControls)
			throws IllegalStateException {
		if(hasMultipleServicesCreditControl())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			
			for(MultipleServicesCreditControlAvp multipleServicesCreditControlAvp: multipleServicesCreditControls)
			{
				super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL, multipleServicesCreditControlAvp.byteArrayValue(),protectedAvp==1,mandatoryAvp==1);
			}
		}

	}

}
/**
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.mobicents.slee.resource.diameter.cca.events;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.IllegalAvpValueException;
import net.java.slee.resource.diameter.cca.events.CreditControlMessage;
import net.java.slee.resource.diameter.cca.events.avp.CcRequestType;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCode;
import net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.mobicents.slee.resource.diameter.base.events.ExtensionDiameterMessageImpl;
import org.mobicents.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvpImpl;

/**
 * Start time:11:38:20 2008-11-11<br>
 * Project: mobicents-diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class CreditControlMessageImpl extends ExtensionDiameterMessageImpl
		implements CreditControlMessage {

	/**
	 * tttttt	
	 * @param message
	 */
	public CreditControlMessageImpl(Message message) {
		super(message);
		
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getAcctMultiSessionId()
	 */
	public String getAcctMultiSessionId() {
		if(hasAcctMultiSessionId())
		{
			Avp raw=super.message.getAvps().getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			try {
				return raw.getUTF8String();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestNumber()
	 */
	public long getCcRequestNumber() {
		if(hasCcRequestNumber())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			try {
				return raw.getUnsigned32();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_REQUEST_NUMBER);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcRequestType()
	 */
	public CcRequestType getCcRequestType() {
		if(hasCcRequestType())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			try {
				return CcRequestType.EVENT_REQUEST.fromInt(raw.getInteger32());
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_REQUEST_TYPE);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getCcSubSessionId()
	 */
	public long getCcSubSessionId() {
		if(hasCcSubSessionId())
		{
			Avp raw=super.message.getAvps().getAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			try {
				return raw.getUnsigned64();
			} catch (AvpDataException e) {
				reportAvpFetchError(""+e, CreditControlAVPCode.CC_SUB_SESSION_ID);
				//e.printStackTrace();
				throw new IllegalAvpValueException(e);
			}
		}
		throw new IllegalAvpValueException("No value set yet.");
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#getMultipleServicesCreditControls()
	 */
	public MultipleServicesCreditControlAvp[] getMultipleServicesCreditControls() {
		if(hasMultipleServicesCreditControl())
		{
			AvpSet set = super.message.getAvps().getAvps(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			MultipleServicesCreditControlAvp[] avps =new MultipleServicesCreditControlAvp[set.size()];
			for(int index=0;index<set.size();index++)
			{
				Avp avp=set.getAvpByIndex(index);
				try {
					avps[index]=new MultipleServicesCreditControlAvpImpl(avp.getCode(),avp.getVendorId(),avp.isMandatory()?1:0,avp.isEncrypted()?1:0,avp.getRaw());
				} catch (AvpDataException e) {
					reportAvpFetchError("failed on index: "+index+", "+e, CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
					e.printStackTrace();
				}
			}
			return avps;
		}
		
		return null;
	}

	public boolean hasMultipleServicesCreditControl() {
		return super.hasAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasAcctMultiSessionId()
	 */
	public boolean hasAcctMultiSessionId() {
		return super.hasAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestNumber()
	 */
	public boolean hasCcRequestNumber() {
		return super.hasAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcRequestType()
	 */
	public boolean hasCcRequestType() {
		return super.hasAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#hasCcSubSessionId()
	 */
	public boolean hasCcSubSessionId() {
		return super.hasAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setAcctMultiSessionId(java.lang.String)
	 */
	public void setAcctMultiSessionId(String acctMultiSessionId)
			throws IllegalStateException {
		if(hasAcctMultiSessionId())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(DiameterAvpCodes.ACCT_MULTI_SESSION_ID,acctMultiSessionId,protectedAvp==1,mandatoryAvp==1,false);
			
			
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestNumber(long)
	 */
	public void setCcRequestNumber(long ccRequestNumber)
			throws IllegalStateException {
		if(hasCcRequestNumber())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_REQUEST_NUMBER);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_NUMBER,ccRequestNumber,protectedAvp==1,mandatoryAvp==1,true);
			
			
		}


	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcRequestType(net.java.slee.resource.diameter.cca.events.avp.CcRequestType)
	 */
	public void setCcRequestType(CcRequestType ccRequestType)
			throws IllegalStateException {
		if(hasCcRequestType())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_REQUEST_TYPE);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_TYPE,ccRequestType.getValue(),protectedAvp==1,mandatoryAvp==1,false);
			
			
		}

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setCcSubSessionId(long)
	 */
	public void setCcSubSessionId(long ccSubSessionId)
			throws IllegalStateException {
		if(hasCcSubSessionId())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.CC_SUB_SESSION_ID);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			super.message.getAvps().addAvp(CreditControlAVPCode.CC_REQUEST_NUMBER,ccSubSessionId,protectedAvp==1,mandatoryAvp==1,true);
			
			
		}
	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControl(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp)
	 */
	public void setMultipleServicesCreditControl(
			MultipleServicesCreditControlAvp multipleServicesCreditControl)
			throws IllegalStateException {
		this.setMultipleServicesCreditControls(new MultipleServicesCreditControlAvp[]{multipleServicesCreditControl});

	}

	/* (non-Javadoc)
	 * @see net.java.slee.resource.diameter.cca.events.CreditControlMessage#setMultipleServicesCreditControls(net.java.slee.resource.diameter.cca.events.avp.MultipleServicesCreditControlAvp[])
	 */
	public void setMultipleServicesCreditControls(
			MultipleServicesCreditControlAvp[] multipleServicesCreditControls)
			throws IllegalStateException {
		if(hasMultipleServicesCreditControl())
		{
			throw new IllegalStateException("Values already set!");
		}else
		{
			super.message.getAvps().removeAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL);
			int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
			int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;
			
			for(MultipleServicesCreditControlAvp multipleServicesCreditControlAvp: multipleServicesCreditControls)
			{
				super.message.getAvps().addAvp(CreditControlAVPCode.MULTIPLE_SERVICES_CREDIT_CONTROL, multipleServicesCreditControlAvp.byteArrayValue(),protectedAvp==1,mandatoryAvp==1);
			}
		}

	}

}
