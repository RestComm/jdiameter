package net.java.slee.resource.diameter.rf;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.base.DiameterActivity;

public interface RfActivityContextInterfaceFactory {

	public ActivityContextInterface getActivityContextInterface(RfClientSession cSession);

	public ActivityContextInterface getActivityContextInterface(RfServerSession sSession);
	
	public ActivityContextInterface getActivityContextInterface(DiameterActivity activity);
}
