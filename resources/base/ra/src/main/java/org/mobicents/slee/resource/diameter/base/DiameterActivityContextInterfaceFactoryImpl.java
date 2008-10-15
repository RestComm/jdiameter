package org.mobicents.slee.resource.diameter.base;

import javax.slee.ActivityContextInterface;
import javax.slee.UnrecognizedActivityException;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.base.DiameterActivityContextInterfaceFactory;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

public class DiameterActivityContextInterfaceFactoryImpl implements DiameterActivityContextInterfaceFactory
{

  private static Logger logger = Logger.getLogger(DiameterActivityContextInterfaceFactoryImpl.class);
  
  private SleeContainer serviceContainer = null;
  
  private String jndiName = null;
  
  private ActivityContextFactory factory = null;
  
  private String raEntityName = null;
  
  public DiameterActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String jndiName)
  {
    super();

    logger.info("Diameter Base RA :: DiameterActivityContextInterfaceFactoryImpl :: serviceContainer[" + serviceContainer + "], jndiName[" + jndiName + "].");
    
    this.serviceContainer = serviceContainer;
    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-base-ra-acif";
    this.factory = serviceContainer.getActivityContextFactory();
    this.raEntityName = jndiName;
  }
  
  public String getJndiName()
  {
    return this.jndiName;
  }
  
  public ActivityContextInterface getActivityContextInterface( DiameterActivity activity ) throws UnrecognizedActivityException
  {

    logger.info("Diameter Base RA :: getActivityContextInterface :: activity[" + activity + "].");
    
    if (activity == null)
      throw new NullPointerException("Received null in ACIF");
    
    return new ActivityContextInterfaceImpl(this.serviceContainer,
        this.factory.getActivityContext(
            new SleeActivityHandle(raEntityName,
                ((DiameterActivityImpl)activity ).getActivityHandle(), serviceContainer))
            .getActivityContextId());
  }

}
