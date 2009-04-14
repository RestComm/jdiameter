package net.java.slee.resource.diameter.ro;

import javax.slee.ActivityContextInterface;
import javax.slee.UnrecognizedActivityException;

/**
 * Declares the methods to obtain an ActivityContextInterface for Ro activities.
 *  
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface RoActivityContextInterfaceFactory {

  /**
   * Method for obtaining ActivityContextInterface for a Ro client activity.
   * 
   * @param cSession the Ro client activity
   * @return the ActivityContextInterface
   * @throws UnrecognizedActivityException
   */
	public ActivityContextInterface getActivityContextInterface(RoClientSession cSession) throws UnrecognizedActivityException;

	/**
   * Method for obtaining ActivityContextInterface for a Ro server activity.
	 * 
	 * @param sSession the Ro server activity
	 * @return the ActivityContextInterface
   * @throws UnrecognizedActivityException
	 */
	public ActivityContextInterface getActivityContextInterface(RoServerSession sSession) throws UnrecognizedActivityException;
	
}
