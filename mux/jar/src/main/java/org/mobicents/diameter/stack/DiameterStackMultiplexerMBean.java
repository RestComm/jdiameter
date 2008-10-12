package org.mobicents.diameter.stack;

import org.jboss.system.ServiceMBean;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Stack;
import org.mobicents.diameter.api.DiameterMessageFactory;
import org.mobicents.diameter.api.DiameterProvider;

public interface DiameterStackMultiplexerMBean extends ServiceMBean
{

  public static final String MBEAN_NAME_PREFIX = "diameter:Service=DiameterStackMultiplexer,Name=";
  
  public void registerListener(DiameterListener listener, ApplicationId[] appIds) throws IllegalStateException;
  
  public void unregisterListener(DiameterListener listener);

  //For sake of simplicity in the pre Gamma :)
  public Stack getStack();
  
  public DiameterProvider getProvider();
  
  public DiameterMessageFactory getMessageFactory();
  
  public DiameterStackMultiplexerMBean getMultiplexerMBean();
}
