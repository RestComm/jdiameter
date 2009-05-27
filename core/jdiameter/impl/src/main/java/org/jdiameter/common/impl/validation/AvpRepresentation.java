/**
 * Start time:10:50:39 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
package org.jdiameter.common.impl.validation;

/**
 * Start time:10:50:39 2009-05-26<br>
 * Project: diameter-parent<br>
 * Represents command avp, it stores info about presence, multiplicity, avp code, vendor.
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpRepresentation {

	/**
	 * <pre>
	 * 0     The AVP MUST NOT be present in the message.
     * 0+    Zero or more instances of the AVP MAY be present in the
     *       message.
     * 0-1   Zero or one instance of the AVP MAY be present in the
     *       message.  It is considered an error if there are more than
     *       one instance of the AVP.
     * 1     One instance of the AVP MUST be present in the message.
     * 1+    At least one instance of the AVP MUST be present in the
     *       message.
     * </pre>
	 */
	public final static String _MP_NOT_ALLOWED = "0";
	public final static String _MP_ZERO_AND_MORE = "0+";
	public final static String _MP_ZERO_OR_ONE = "0-1";
	public final static String _MP_ONE = "1";
	public final static String _MP_ONE_AND_MORE = "1+";
	
	private final transient static int _FIX_POSITION_INDEX = -1;
	
	//ususally this will be -1, as only SessionId has fixed position
	private int positionIndex = _FIX_POSITION_INDEX;
	private int code = -1;
	private long vendor = 0;
	private boolean allowed = true;
	private String multiplicityIndicator = "0";
	private String name = "Some-AVP";
	
	
	
	

	public AvpRepresentation(int code, long vendor) {
		super();
		this.code = code;
		this.vendor = vendor;
	}

	public AvpRepresentation(int positionIndex, int code, long vendor, String multiplicityIndicator, String name) {
		super();
		this.positionIndex = positionIndex;
		this.code = code;
		this.vendor = vendor;
		this.multiplicityIndicator = multiplicityIndicator;
		this.name = name;
		if(this.multiplicityIndicator.equals(_MP_NOT_ALLOWED))
			this.allowed = false;
	}

	public boolean isPositionFixed()
	{
		return this.positionIndex==_FIX_POSITION_INDEX;
	}
	
	public void markFixPosition(int index)
	{
		this.positionIndex = index;
	}
	
	public boolean isCountValidForMultiplicity(int avpCount)
	{
		
		//This covver nto allowed
		if(!allowed)
		{
			if( avpCount == 0)
			{
				return true;
			}
		}else
		{
			if(this.multiplicityIndicator.equals(_MP_ZERO_AND_MORE)  )
			{
				if(avpCount>=0)
					return true;
			}else if(this.multiplicityIndicator.equals(_MP_ZERO_OR_ONE))
			{
				if((avpCount == 0)||(avpCount == 1))
					return true;
			}else if(this.multiplicityIndicator.equals(_MP_ONE))
			{
				if(avpCount == 1)
				{
					return true;
				}
			}else if(this.multiplicityIndicator.equals(_MP_ONE_AND_MORE))
			{
				if(avpCount >= 1)
				{
					return true;
				}
			}
			
				
		}
		
		//if we did nto return, we are screwed.
		return false;
	}

	public static int get_FIX_POSITION_INDEX() {
		return _FIX_POSITION_INDEX;
	}

	public int getPositionIndex() {
		return positionIndex;
	}

	public int getCode() {
		return code;
	}

	public long getVendor() {
		return vendor;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public String getMultiplicityIndicator() {
		return multiplicityIndicator;
	}

	public String getName() {
		return name;
	}
	
	public String toString()
	{
		return this.getName()+"@"+hashCode()+" Name["+getName()+"] Code["+getCode()+"] Vendor["+getVendor()+"] MLP["+getMultiplicityIndicator()+"] Allowed["+isAllowed()+"] ";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + (int) (vendor ^ (vendor >>> 32));
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
		AvpRepresentation other = (AvpRepresentation) obj;
		if (code != other.code)
			return false;
		if (vendor != other.vendor)
			return false;
		return true;
	}
	
	
	
}
