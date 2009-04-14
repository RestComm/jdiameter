package org.mobicents.slee.resource.diameter.rf;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.rf.RfActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.rf.RfClientSession;
import net.java.slee.resource.diameter.rf.RfServerSession;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityContextInterfaceFactoryImpl;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;

/**
 * 
 * RfActivityContextInterfaceFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:27:47 AM Mar 25, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RfActivityContextInterfaceFactoryImpl extends DiameterActivityContextInterfaceFactoryImpl implements RfActivityContextInterfaceFactory {

  private static final Logger logger = Logger.getLogger(RfActivityContextInterfaceFactoryImpl.class);

  private SleeContainer sleeContainer = null;
  private ActivityContextFactory acFactory;
  private String raEntityName;

  private String jndiName;

  public RfActivityContextInterfaceFactoryImpl(SleeContainer sleeContainer, String jndiName)
  {
    super( sleeContainer, jndiName );

    logger.info("Diameter Rf RA :: RfActivityContextInterfaceFactoryImpl :: sleeContainer["+ sleeContainer + "], jndiName[" + jndiName + "].");

    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-rf-ra-acif";

    this.sleeContainer = sleeContainer;
    this.acFactory = sleeContainer.getActivityContextFactory();
    this.raEntityName = jndiName;
  }

  public ActivityContextInterface getActivityContextInterface(RfClientSession session)
  {
    logger.info("Diameter Rf RA :: getActivityContextInterface :: activity[" + session + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)session );
  }

  public ActivityContextInterface getActivityContextInterface(RfServerSession session)
  {
    logger.info("Diameter Rf RA :: getActivityContextInterface :: activity[" + session + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)session );
  }

  public ActivityContextInterface getActivityContextInterface(DiameterActivity activity)
  {
    logger.info("Diameter Rf RA :: getActivityContextInterface :: activity[" + activity + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)activity );
  }

  private ActivityContextInterface getActivityContextInterface(DiameterActivityImpl session)
  {
    if(session == null)
    {
      throw new NullPointerException("Diameter Rf RA :: getActivityContextInterface :: Activity cannot be null.");
    }
    
    SleeActivityHandle sah = new SleeActivityHandle(raEntityName, session.getActivityHandle(), sleeContainer);

    ActivityContext ac = this.acFactory.getActivityContext(sah);

    return new ActivityContextInterfaceImpl(this.sleeContainer, ac.getActivityContextId());
  }

  public String getJndiName()
  {
    return this.jndiName;
  }

}
