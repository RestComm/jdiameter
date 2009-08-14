package org.mobicents.slee.resource.diameter.cxdx;

import javax.slee.ActivityContextInterface;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactoryImpl;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

import net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.cxdx.CxDxClientSession;
import net.java.slee.resource.diameter.cxdx.CxDxServerSession;

/**
 *
 * CxDxActivityContextInterfaceFactoryImpl.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class CxDxActivityContextInterfaceFactoryImpl implements CxDxActivityContextInterfaceFactory {

  private static final Logger logger = Logger.getLogger(CxDxActivityContextInterfaceFactoryImpl.class);
  private SleeContainer serviceContainer;
  private String jndiName;
  private ActivityContextFactoryImpl factory;
  private String raEntityName;

  /**
   * 
   * @param serviceContainer
   * @param jndiName
   */
  public CxDxActivityContextInterfaceFactoryImpl(SleeContainer serviceContainer, String jndiName)
  {
    super();
    if (logger.isInfoEnabled()) {
      logger.info("Diameter Cx/Dx RA :: CxDxActivityContextInterfaceFactoryImpl :: serviceContainer["+ serviceContainer + "], jndiName[" + jndiName + "].");
    }

    this.serviceContainer = serviceContainer;
    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-cxdx-ra-acif";
    this.factory = serviceContainer.getActivityContextFactory();
    this.raEntityName = jndiName;
  }

  public String getJndiName() {
    return this.jndiName;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory#getActivityContextInterface(net.java.slee.resource.diameter.cxdx.CxDxClientSession)
   */
  public ActivityContextInterface getActivityContextInterface(CxDxClientSession cxdxcs) {
    if (logger.isInfoEnabled()) {
      logger.info("Diameter Cx/Dx RA :: getActivityContextInterface :: activity[" + cxdxcs + "].");
    }

    return this.getActivityContextInterface((DiameterActivityImpl)cxdxcs);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.cxdx.CxDxActivityContextInterfaceFactory#getActivityContextInterface(net.java.slee.resource.diameter.cxdx.CxDxServerSession)
   */
  public ActivityContextInterface getActivityContextInterface(CxDxServerSession cxdxss) {
    if (logger.isInfoEnabled()) {
      logger.info("Diameter Cx/Dx RA :: getActivityContextInterface :: activity[" + cxdxss + "].");
    }

    return this.getActivityContextInterface((DiameterActivityImpl)cxdxss);
  }

  /**
   * Creates a ACI from a given Diameter Activity (either Cx/Dx Server/Client Session)
   * @param session the Cx/Dx Server/Client Session
   * @return an ActivityContextInterface wrapping the session
   */
  private ActivityContextInterface getActivityContextInterface(DiameterActivityImpl session)
  {
    SleeActivityHandle sah = new SleeActivityHandle(raEntityName, session.getActivityHandle(), serviceContainer);

    ActivityContext ac = this.factory.getActivityContext(sah);

    return new ActivityContextInterfaceImpl(this.serviceContainer, ac.getActivityContextId());
  }
}
