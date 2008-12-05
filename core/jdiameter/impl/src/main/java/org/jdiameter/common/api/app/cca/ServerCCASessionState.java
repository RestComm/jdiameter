package org.jdiameter.common.api.app.cca;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * 
 * ServerCCASessionState.java
 *
 * <br>Super project:  mobicents
 * <br>4:23:45 PM Dec 2, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public enum ServerCCASessionState implements IAppSessionState<ServerCCASessionState> {

	IDLE(0),
	OPEN(1);

	private int stateRepresentation=-1;

	ServerCCASessionState(int v)
	{
		this.stateRepresentation=v;
	}

	public ServerCCASessionState fromInt(int v) throws IllegalArgumentException
	{
		switch(v)
		{
		
		case 0:
			return IDLE;

		case 1:
			return OPEN;
		
		default:
			throw new IllegalArgumentException("Illegal value of int representation!!!!");
		
		}
	}

	public int getValue()
	{
		return stateRepresentation;
	}
	
}
