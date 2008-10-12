package org.mobicents.diameter.stack;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Mode;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;

public class DiameterStackProxy implements Stack
{

  protected Stack realStack = null;

  
  public DiameterStackProxy(Stack realStack)
  {
    super();
    this.realStack = realStack;
  }
  
  public void destroy()
  {
    this.realStack.destroy();
  }

  public Logger getLogger()
  {
    return this.realStack.getLogger();
  }

  public MetaData getMetaData()
  {
    return this.realStack.getMetaData();
  }

  public SessionFactory getSessionFactory() throws IllegalDiameterStateException
  {
    return this.realStack.getSessionFactory();
  }

  public SessionFactory init( Configuration config ) throws IllegalDiameterStateException, InternalException
  {
    return this.realStack.init( config );
  }

  public boolean isActive()
  {
    return this.realStack.isActive();
  }

  public void start() throws IllegalDiameterStateException, InternalException
  {
    this.realStack.start();    
  }

  public void start(Mode mode, long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException
  {
    this.realStack.start( mode, timeout, unit );
  }

  public void stop( long timeout, TimeUnit unit ) throws IllegalDiameterStateException, InternalException
  {
    this.realStack.stop( timeout, unit );    
  }

  public boolean isWrapperFor( Class<?> iface ) throws InternalException
  {
    return this.realStack.isWrapperFor( iface );
  }

  public <T> T unwrap( Class<T> iface ) throws InternalException
  {
    return this.realStack.unwrap( iface );
  }

}
