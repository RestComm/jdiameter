/**
 * Start time:10:40:36 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.jdiameter.common.impl.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.client.impl.parser.MessageImpl;

/**
 * Start time:10:40:36 2009-05-26<br>
 * Project: diameter-parent<br>
 * This class represents message/command in validation framework. It contains
 * basic info about command along with avp list - their multiplicity and
 * allowance. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.189
 */
public class MessageRepresentation implements Comparable<MessageRepresentation>{

	private int commandCode = -1;
	private long applicationId = 0;
	
	private boolean isRequest = false;
	private Map<AvpRepresentation,AvpRepresentation>  messageAvps = new HashMap<AvpRepresentation,AvpRepresentation>();
	
	
	
	
	public MessageRepresentation(int commandCode, long applicationId, boolean isRequest) {
		super();
		this.commandCode = commandCode;
		this.applicationId = applicationId;
		this.isRequest = isRequest;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (applicationId ^ (applicationId >>> 32));
		result = prime * result + commandCode;
		result = prime * result + (isRequest ? 1231 : 1237);
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageRepresentation other = (MessageRepresentation) obj;
		if (applicationId != other.applicationId)
			return false;
		if (commandCode != other.commandCode)
			return false;
		if (isRequest != other.isRequest)
			return false;
		return true;
	}


	public Map<AvpRepresentation,AvpRepresentation> getMessageAvps() {
		return messageAvps;
	}


	public void setMessageAvps(Map<AvpRepresentation,AvpRepresentation> messageAvps) {
		this.messageAvps = messageAvps;
	}


	public void validate(MessageImpl msg) throws AvpNotAllowedException
	{
		for(AvpRepresentation ap: this.messageAvps.values())
		{
			AvpSet innerSet = msg.getAvps().getAvps(ap.getCode(), ap.getVendor());
			int count = 0;
			if(innerSet!=null)
			{
				count+=innerSet.size();
			}
			
			
			if(!ap.isCountValidForMultiplicity(count))
			{
				throw new AvpNotAllowedException("AVP: "+ap+" is not allowed, to many avps present already - "+(count-1));
			}
		}
	}
	public void validate(AvpSet destination, Avp avp)
	{
		AvpRepresentation avpRep= new AvpRepresentation(avp.getCode(),avp.getVendorId());
		avpRep  = this.messageAvps.get(avpRep);
		if(avpRep == null)
			return;
		
		
		if(!avpRep.isAllowed())
		{
			throw new AvpNotAllowedException("AVP: "+avpRep+" is not allowed.");
		}
		//For avp beeing added :)
		int count = 1;
		AvpSet innerSet = destination.getAvps(avpRep.getCode(), avpRep.getVendor());
		if(innerSet!=null)
		{
			count+=innerSet.size();
		}
		
		
		if(!avpRep.isCountValidForMultiplicity(count))
		{
			throw new AvpNotAllowedException("AVP: "+avpRep+" is not allowed, to many avps present already - "+(count-1));
		}
	}



	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(MessageRepresentation o) {
		if(o == this)
			return 0;
		if(o== null)
			return 1;
		return this.hashCode() - o.hashCode();
	}



	/**
	 * @param code
	 * @param vendorId
	 * @param count
	 * @return
	 */
	public boolean isCountValidForMultiplicity(int code, long vendorId, int count) {
		AvpRepresentation avpRep= new AvpRepresentation(code,vendorId);
		avpRep  = this.messageAvps.get(avpRep);
		if(avpRep == null)
			return true;
		return avpRep.isCountValidForMultiplicity(count);
	}



	/**
	 * @param code
	 * @param vendorId
	 * @return
	 */
	public boolean isAllowed(int code, long vendorId) {
		AvpRepresentation avpRep= new AvpRepresentation(code,vendorId);
		avpRep  = this.messageAvps.get(avpRep);
		if(avpRep == null)
			return true;
		return avpRep.isAllowed();
	}
}
