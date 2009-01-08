package org.mobicents.slee.resource.diameter.sh.server;

import javax.slee.ActivityContextInterface;
import javax.slee.UnrecognizedActivityException;

import net.java.slee.resource.diameter.sh.server.ShServerActivity;
import net.java.slee.resource.diameter.sh.server.ShServerActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.server.ShServerSubscriptionActivity;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

public class ShServerActivityContextInterfaceFactoryImpl implements
		ShServerActivityContextInterfaceFactory {

	private static Logger logger = Logger
			.getLogger(ShServerActivityContextInterfaceFactoryImpl.class);

	private SleeContainer serviceContainer = null;

	private String jndiName = null;

	private ActivityContextFactory factory = null;

	private String raEntityName = null;

	public ShServerActivityContextInterfaceFactoryImpl(
			SleeContainer serviceContainer, String jndiName) {
		super();

		if (logger.isInfoEnabled())
			logger
					.info("Diameter ShClient RA :: ShServerActivityContextInterfaceFactory :: serviceContainer["
							+ serviceContainer
							+ "], jndiName["
							+ jndiName
							+ "].");

		this.serviceContainer = serviceContainer;
		this.jndiName = "java:slee/resources/" + jndiName
				+ "/diameter-shserver-ra-acif";
		this.factory = serviceContainer.getActivityContextFactory();
		this.raEntityName = jndiName;
	}

	public ActivityContextInterface getActivityContextInterface(
			ShServerActivity activity) throws UnrecognizedActivityException {
		if (logger.isInfoEnabled())
			logger
					.info("Diameter ShServer RA :: getActivityContextInterface :: activity["
							+ activity + "].");

		if (activity == null)
			throw new NullPointerException("Received null in ACIF");

		return new ActivityContextInterfaceImpl(this.serviceContainer,
				this.factory
						.getActivityContext(
								new SleeActivityHandle(raEntityName,
										((DiameterActivityImpl) activity)
												.getActivityHandle(),
										serviceContainer))
						.getActivityContextId());
	}

	public ActivityContextInterface getActivityContextInterface(
			ShServerSubscriptionActivity activity)
			throws UnrecognizedActivityException {
		if (logger.isInfoEnabled())
			logger
					.info("Diameter ShServer RA :: getActivityContextInterface :: activity["
							+ activity + "].");

		if (activity == null)
			throw new NullPointerException("Received null in ACIF");

		return new ActivityContextInterfaceImpl(this.serviceContainer,
				this.factory
						.getActivityContext(
								new SleeActivityHandle(raEntityName,
										((DiameterActivityImpl) activity)
												.getActivityHandle(),
										serviceContainer))
						.getActivityContextId());
	}

	public String getJndiName() {
		return this.jndiName;
	}

}
