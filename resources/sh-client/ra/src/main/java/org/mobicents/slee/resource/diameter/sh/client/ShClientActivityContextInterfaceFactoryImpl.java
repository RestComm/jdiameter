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
