package net.java.slee.resource.diameter.cxdx;

import javax.slee.ActivityContextInterface;

/**
 * 
 * CxDxActivityContextInterfaceFactory.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface CxDxActivityContextInterfaceFactory {

  /**
   * Method for obtaining a ACI wrapping the given Cx/Dx Client Session.
   * @param cxdxcs the Cx/Dx Client Session
   * @return an ActivityContextInterface
   */
  public ActivityContextInterface getActivityContextInterface(CxDxClientSession cxdxcs);

  /**
   * Method for obtaining a ACI wrapping the given Cx/Dx Server Session.
   * @param cxdxss the Cx/Dx Client Session
   * @return an ActivityContextInterface
   */
  public ActivityContextInterface getActivityContextInterface(CxDxServerSession cxdxss);
}
