package org.jdiameter.common.api.app.cca;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * 
 * ClientCCASessionState.java
 *
 * <br>Super project:  mobicents
 * <br>4:21:53 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public enum ClientCCASessionState implements IAppSessionState<ClientCCASessionState> {

	IDLE(0),
	PENDING_INTERIM(1),
	OPEN(2),
	PENIDNG_UPDATE(3),
	PENDING_TERMINATION(4),
	PENDING_BUFFERED(5),
	PENDING_EVENT(6);

	private int stateValue=-1;

	ClientCCASessionState(int stateV)
	{
		this.stateValue=stateV;
	}
	
	public ClientCCASessionState fromInt(int v) throws IllegalArgumentException
	{
		switch(v)
		{
		
		case 0:
			return IDLE;
		case 1:
			return PENDING_INTERIM;
		case 2:
			return OPEN;
		case 3:
			return PENIDNG_UPDATE;
		case 4:
			return PENDING_TERMINATION;
		case 5:
			return PENDING_BUFFERED;
		case 6:
			return PENDING_EVENT;
		default:
			throw new IllegalArgumentException("Illegal value of int representation!!!!");
		
		}
	}

	public int getValue()
	{
		return stateValue;
	}
	
}
