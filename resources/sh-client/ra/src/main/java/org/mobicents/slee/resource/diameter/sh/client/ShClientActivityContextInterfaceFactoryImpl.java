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
package org.mobicents.slee.resource.diameter.sh.client;

import javax.slee.ActivityContextInterface;
import javax.slee.UnrecognizedActivityException;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientSubscriptionActivity;
/**
 * 
 * Start time:13:22:47 2009-05-22<br>
 * Project: diameter-parent<br>
 * Implementation of Sh Client ACIF
 * @author <a href="mailto:baranowb@gmail.com">baranowb - Bartosz Baranowski
 *         </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class ShClientActivityContextInterfaceFactoryImpl implements ShClientActivityContextInterfaceFactory {

	private static Logger logger = Logger.getLogger(ShClientActivityContextInterfaceFactoryImpl.class);
	  
	  private SleeContainer serviceContainer = null;
	  
	  private String jndiName = null;
	  
	  private ActivityContextFactory factory = null;
	  
	  private String raEntityName = null;
	
	
	public ShClientActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String jndiName)
	  {
	    super();

	    logger.info("Diameter ShClient RA :: ShClientActivityContextInterfaceFactory :: serviceContainer[" + serviceContainer + "], jndiName[" + jndiName + "].");
	    
	    this.serviceContainer = serviceContainer;
	    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-shclient-ra-acif";
	    this.factory = serviceContainer.getActivityContextFactory();
	    this.raEntityName = jndiName;
	  }

	public ActivityContextInterface getActivityContextInterface(ShClientActivity activity) throws UnrecognizedActivityException {
		logger.info("Diameter ShClient RA :: getActivityContextInterface :: activity[" + activity + "].");
	    
	    if (activity == null)
	      throw new NullPointerException("Received null in ACIF");
	    
	    return new ActivityContextInterfaceImpl(this.serviceContainer,
	        this.factory.getActivityContext(
	            new SleeActivityHandle(raEntityName,
	                ((DiameterActivityImpl)activity ).getActivityHandle(), serviceContainer))
	            .getActivityContextId());
	}

	public ActivityContextInterface getActivityContextInterface(ShClientSubscriptionActivity activity) throws UnrecognizedActivityException {
		logger.info("Diameter ShClient RA :: getActivityContextInterface :: activity[" + activity + "].");
	    
	    if (activity == null)
	      throw new NullPointerException("Received null in ACIF");
	    
	    return new ActivityContextInterfaceImpl(this.serviceContainer,
	        this.factory.getActivityContext(
	            new SleeActivityHandle(raEntityName,
	                ((DiameterActivityImpl)activity ).getActivityHandle(), serviceContainer))
	            .getActivityContextId());
	}

	 public String getJndiName()
	  {
	    return this.jndiName;
	  }
	
}
