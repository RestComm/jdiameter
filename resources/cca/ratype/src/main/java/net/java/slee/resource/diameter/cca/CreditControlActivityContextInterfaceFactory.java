package net.java.slee.resource.diameter.cca;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.base.DiameterActivity;

public interface CreditControlActivityContextInterfaceFactory {

	public ActivityContextInterface getActivityContextInterface(CreditControlClientSession cccs);
	public ActivityContextInterface getActivityContextInterface(CreditControlServerSession ccss);
	
	//FIXME: baranowb : this is not required, is it?
	//public ActivityContextInterface getActivityContextInterface(
	//		DiameterActivity activity);
}
