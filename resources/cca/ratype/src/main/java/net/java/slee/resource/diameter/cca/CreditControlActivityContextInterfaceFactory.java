package net.java.slee.resource.diameter.cca;

import javax.slee.ActivityContextInterface;

import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;

/**
 * 
 * CreditControlActivityContextInterfaceFactory.java
 *
 * <br>Super project:  mobicents
 * <br>10:51:37 AM Dec 30, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public interface CreditControlActivityContextInterfaceFactory extends ResourceAdaptorActivityContextInterfaceFactory {

  /**
   * Method for obtaining a ACI wrapping the given Credit-Control Client Session.
   * @param cccs the Credit-Control Client Session
   * @return an ActivityContextInterface
   */
  public ActivityContextInterface getActivityContextInterface(CreditControlClientSession cccs);

  /**
   * Method for obtaining a ACI wrapping the given Credit-Control Server Session.
   * @param ccss the Credit-Control Client Session
   * @return an ActivityContextInterface
   */
  public ActivityContextInterface getActivityContextInterface(CreditControlServerSession ccss);

}
