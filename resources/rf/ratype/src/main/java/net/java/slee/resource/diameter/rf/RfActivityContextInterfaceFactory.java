package net.java.slee.resource.diameter.rf;

import javax.slee.ActivityContextInterface;

import org.mobicents.slee.resource.ResourceAdaptorActivityContextInterfaceFactory;

/**
 * 
 * RfActivityContextInterfaceFactory.java
 *
 * <br>Project:  mobicents
 * <br>6:09:35 PM Apr 10, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RfActivityContextInterfaceFactory extends ResourceAdaptorActivityContextInterfaceFactory {

  /**
   * 
   * @param cSession
   * @return
   */
	public ActivityContextInterface getActivityContextInterface(RfClientSession cSession);

	/**
	 * 
	 * @param sSession
	 * @return
	 */
	public ActivityContextInterface getActivityContextInterface(RfServerSession sSession);
	
}
