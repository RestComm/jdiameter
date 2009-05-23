/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
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
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

/**
 * 
 * Start time:16:54:53 2009-05-23<br>
 * Project: diameter-parent<br>
 * Implmentation of Sh Servier ACIF
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @see ShServerActivityContextInterfaceFactory
 */
public class ShServerActivityContextInterfaceFactoryImpl implements ShServerActivityContextInterfaceFactory {

	private static Logger logger = Logger.getLogger(ShServerActivityContextInterfaceFactoryImpl.class);

	private SleeContainer serviceContainer = null;

	private String jndiName = null;

	private ActivityContextFactory factory = null;

	private String raEntityName = null;

	public ShServerActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String jndiName) {
		super();

		if (logger.isInfoEnabled()) {
			logger.info("Diameter ShClient RA :: ShServerActivityContextInterfaceFactory :: serviceContainer[" + serviceContainer + "], jndiName[" + jndiName + "].");
		}

		this.serviceContainer = serviceContainer;
		this.jndiName = "java:slee/resources/" + jndiName + "/diameter-shserver-ra-acif";
		this.factory = serviceContainer.getActivityContextFactory();
		this.raEntityName = jndiName;
	}

	public ActivityContextInterface getActivityContextInterface(ShServerActivity activity) throws UnrecognizedActivityException {
		if (logger.isInfoEnabled()) {
			logger.info("Diameter ShServer RA :: getActivityContextInterface :: activity[" + activity + "].");
		}

		if (activity == null) {
			throw new NullPointerException("Received null in ACIF");
		}

		ActivityContext activityContext = this.factory.getActivityContext(new SleeActivityHandle(raEntityName, ((DiameterActivityImpl) activity).getActivityHandle(), serviceContainer));

		return new ActivityContextInterfaceImpl(this.serviceContainer, activityContext.getActivityContextId());
	}

	public ActivityContextInterface getActivityContextInterface(ShServerSubscriptionActivity activity) throws UnrecognizedActivityException {
		if (logger.isInfoEnabled()) {
			logger.info("Diameter ShServer RA :: getActivityContextInterface :: activity[" + activity + "].");
		}

		if (activity == null) {
			throw new NullPointerException("Received null in ACIF");
		}

		ActivityContext activityContext = this.factory.getActivityContext(new SleeActivityHandle(raEntityName, ((DiameterActivityImpl) activity).getActivityHandle(), serviceContainer));

		return new ActivityContextInterfaceImpl(this.serviceContainer, activityContext.getActivityContextId());
	}

	public String getJndiName() {
		return this.jndiName;
	}

}
