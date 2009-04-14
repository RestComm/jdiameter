package org.mobicents.slee.resource.diameter.ro;

import javax.slee.ActivityContextInterface;

import net.java.slee.resource.diameter.base.DiameterActivity;
import net.java.slee.resource.diameter.ro.RoActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.ro.RoClientSession;
import net.java.slee.resource.diameter.ro.RoServerSession;

import org.apache.log4j.Logger;
import org.mobicents.slee.container.SleeContainer;
import org.mobicents.slee.resource.SleeActivityHandle;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.cca.CreditControlActivityContextInterfaceFactoryImpl;
import org.mobicents.slee.runtime.ActivityContext;
import org.mobicents.slee.runtime.ActivityContextFactory;
import org.mobicents.slee.runtime.ActivityContextInterfaceImpl;


/**
 * 
 * RoActivityContextInterfaceFactoryImpl.java
 *
 * <br>Project:  mobicents
 * <br>11:54:34 PM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class RoActivityContextInterfaceFactoryImpl extends CreditControlActivityContextInterfaceFactoryImpl implements RoActivityContextInterfaceFactory {

  private static final Logger logger = Logger.getLogger(RoActivityContextInterfaceFactoryImpl.class);

  private SleeContainer sleeContainer = null;
  private ActivityContextFactory acFactory;
  private String raEntityName;

  private String jndiName;

  public RoActivityContextInterfaceFactoryImpl(SleeContainer sleeContainer, String jndiName)
  {
    super( sleeContainer, jndiName );

    logger.info("Diameter Ro RA :: RoActivityContextInterfaceFactoryImpl :: sleeContainer["+ sleeContainer + "], jndiName[" + jndiName + "].");

    this.jndiName = "java:slee/resources/" + jndiName + "/diameter-ro-ra-acif";

    this.sleeContainer = sleeContainer;
    this.acFactory = sleeContainer.getActivityContextFactory();
    this.raEntityName = jndiName;
  }

  public ActivityContextInterface getActivityContextInterface(RoClientSession session)
  {
    logger.info("Diameter Ro RA :: getActivityContextInterface :: activity[" + session + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)session );
  }

  public ActivityContextInterface getActivityContextInterface(RoServerSession session)
  {
    logger.info("Diameter Ro RA :: getActivityContextInterface :: activity[" + session + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)session );
  }

  public ActivityContextInterface getActivityContextInterface(DiameterActivity activity)
  {
    logger.info("Diameter Ro RA :: getActivityContextInterface :: activity[" + activity + "].");
    
    return this.getActivityContextInterface( (DiameterActivityImpl)activity );
  }

  private ActivityContextInterface getActivityContextInterface(DiameterActivityImpl session)
  {
    if(session == null)
    {
      throw new NullPointerException("Diameter Ro RA :: getActivityContextInterface :: Activity cannot be null.");
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
