package org.jdiameter.common.api.app.sh;

import org.jdiameter.common.api.app.IAppSessionState;

/**
 * This is pseudo state - it is used only to determine wheather Sh Session should be kept alive
 * <br><br>Super project:  mobicents-jainslee-server
 * <br>13:52:51 2008-09-04	
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author Erick Svenson
 */
public enum ShSessionState implements IAppSessionState<ShSessionState>{

	//Possible Transition chains:
	//NOTSUBSCRIBED --> SUBSCRIBED --> TERMINATED : on SubscribeNotificationsSuccess sequence
	//NOTSUBSCRIBED --> TERMINATED : on any other
	NOTSUBSCRIBED,SUBSCRIBED,TERMINATED;

	public ShSessionState fromInt(int val) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}
}
