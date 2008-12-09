package org.mobicents.slee.resource.diameter.cca;

import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;

public class CreditControlActivityContextInterfaceFactoryImpl implements
		CreditControlActivityContextInterfaceFactory {

	private static final Logger logger = Logger
			.getLogger(CreditControlActivityContextInterfaceFactory.class);

	private SleeContainer serviceContainer = null;

	private String jndiName = null;

	private ActivityContextFactory factory = null;

	private String raEntityName = null;

	public CreditControlActivityContextInterfaceFactoryImpl(
			SleeContainer serviceContainer, String jndiName) {
		super();

		logger
				.info("Diameter Base RA :: DiameterActivityContextInterfaceFactoryImpl :: serviceContainer["
						+ serviceContainer + "], jndiName[" + jndiName + "].");

		this.serviceContainer = serviceContainer;
		this.jndiName = "java:slee/resources/" + jndiName
				+ "/diameter-base-ra-acif";
		this.factory = serviceContainer.getActivityContextFactory();
		this.raEntityName = jndiName;
	}

	public String getJndiName() {
		return this.jndiName;
	}

	public ActivityContextInterface getActivityContextInterface(
			CreditControlClientSession cccs) {
		logger.info("Diameter CCA RA :: getActivityContextInterface :: activity[" + cccs + "].");
		
		
		SleeActivityHandle sah=new SleeActivityHandle(raEntityName,
                ((DiameterActivityImpl)cccs ).getActivityHandle(), serviceContainer);
		ActivityContext ac=this.factory.getActivityContext(sah);
		
		return new ActivityContextInterfaceImpl(this.serviceContainer, ac.getActivityContextId());
		
		
	}

	public ActivityContextInterface getActivityContextInterface(
			CreditControlServerSession ccss) {
		SleeActivityHandle sah=new SleeActivityHandle(raEntityName,
                ((DiameterActivityImpl)ccss ).getActivityHandle(), serviceContainer);
		ActivityContext ac=this.factory.getActivityContext(sah);
		
		return new ActivityContextInterfaceImpl(this.serviceContainer, ac.getActivityContextId());
		
	}

	

}
