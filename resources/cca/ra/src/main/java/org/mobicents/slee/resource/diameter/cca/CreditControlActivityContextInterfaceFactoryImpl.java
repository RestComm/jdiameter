package org.mobicents.slee.resource.diameter.cca;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cca.CreditControlClientSession;
import net.java.slee.resource.diameter.cca.CreditControlServerSession;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

public class CreditControlActivityContextInterfaceFactoryImpl implements CreditControlActivityContextInterfaceFactory {

  private static final Logger logger = Logger.getLogger(CreditControlActivityContextInterfaceFactoryImpl.class);

  private SleeContainer serviceContainer = null;

  private String jndiName = null;

  private ActivityContextFactory factory = null;

  private String raEntityName = null;

  public CreditControlActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String jndiName)
  {
    super();
    if (logger.isInfoEnabled())
    logger.info("Diameter CCA RA :: CreditControlActivityContextInterfaceFactoryImpl :: serviceContainer["+ serviceContainer + "], jndiName[" + jndiName + "].");

    this.serviceContainer = serviceContainer;
    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-cca-ra-acif";
    this.factory = serviceContainer.getActivityContextFactory();
    this.raEntityName = jndiName;
  }

  public String getJndiName()
  {
    return this.jndiName;
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory#getActivityContextInterface(net.java.slee.resource.diameter.cca.CreditControlClientSession)
   */
  public ActivityContextInterface getActivityContextInterface(CreditControlClientSession cccs)
  {
	  if (logger.isInfoEnabled())
    logger.info("Diameter CCA RA :: getActivityContextInterface :: activity[" + cccs + "].");

    return this.getActivityContextInterface( (DiameterActivityImpl)cccs );
  }

  /*
   * (non-Javadoc)
   * @see net.java.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactory#getActivityContextInterface(net.java.slee.resource.diameter.cca.CreditControlServerSession)
   */
  public ActivityContextInterface getActivityContextInterface(CreditControlServerSession ccss)
  {
	  if (logger.isInfoEnabled())
    logger.info("Diameter CCA RA :: getActivityContextInterface :: activity[" + ccss + "].");

    return this.getActivityContextInterface( (DiameterActivityImpl)ccss );
  }

  /**
   * Creates a ACI from a given Diameter Activity (either CC Server/Client Session)
   * @param session the Credit-Control Server/Client Session
   * @return an ActivityContextInterface wrapping the session
   */
  private ActivityContextInterface getActivityContextInterface(DiameterActivityImpl session)
  {
    SleeActivityHandle sah = new SleeActivityHandle(raEntityName, session.getActivityHandle(), serviceContainer);

    ActivityContext ac = this.factory.getActivityContext(sah);

    return new ActivityContextInterfaceImpl(this.serviceContainer, ac.getActivityContextId());
  }
}
