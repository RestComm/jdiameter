package net.java.slee.resource.diameter.base.events.avp;

import java.util.concurrent.CopyOnWriteArrayList;

public class AvpList extends CopyOnWriteArrayList<DiameterAvp>
{
  private static final long serialVersionUID = 1L;

  public DiameterAvp getByCode(int code)
  {
    return this.get( code );
  }
  
}
