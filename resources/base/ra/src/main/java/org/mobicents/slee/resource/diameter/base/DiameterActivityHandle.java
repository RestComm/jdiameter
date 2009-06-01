package org.mobicents.slee.resource.diameter.base;

import javax.slee.resource.ActivityHandle;

import org.apache.log4j.Logger;

/**
 * Activity handle wrapper for DiameterSession activity with given unique session ID
 * 
 * <br>Super project:  mobicents
 * <br>12:11:29 AM May 21, 2008 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Erick Svenson
 */
public class DiameterActivityHandle implements ActivityHandle
{
  private static Logger logger = Logger.getLogger(DiameterActivityHandle.class);
  
  private String handle;
  
  public DiameterActivityHandle(String id)
  {
    //logger.info("Diameter Base RA :: DiameterActivityHandle :: id[" + id + "].");
    
    this.handle = id;
  }
  
  public boolean equals(Object o)
  {
    if (o != null && o.getClass() == this.getClass())
    {
      return ((DiameterActivityHandle)o).handle.equals(this.handle);
    }
    else
    {
      return false;
    }
  }
  
  public String toString()
  {
      return "Diameter Session ID[" + handle + "]";
  }
  
  public int hashCode()
  {
    return handle.hashCode();
  }
  
}
