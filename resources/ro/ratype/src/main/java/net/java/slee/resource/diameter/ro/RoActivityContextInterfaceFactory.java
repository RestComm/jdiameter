package net.java.slee.resource.diameter.ro;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.base.DiameterActivity;

public interface RoActivityContextInterfaceFactory {

	public ActivityContextInterface getActivityContextInterface(
			RoClientSession cSession);

	public ActivityContextInterface getActivityContextInterface(
			RoServerSession sSession);
	public ActivityContextInterface getActivityContextInterface(
			DiameterActivity activity);
}
