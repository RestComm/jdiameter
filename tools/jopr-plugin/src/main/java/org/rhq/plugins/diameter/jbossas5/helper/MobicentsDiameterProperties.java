package org.rhq.plugins.diameter.jbossas5.helper;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public abstract class MobicentsDiameterProperties {

  public static final String DIAMETER_HOME_DIR = "diameter.home.dir";

  public static final String DIAMETER_VERSION = "diameter.version";

  public static ObjectName OBJECT_NAME = null;
  
  private MobicentsDiameterProperties() {
    try {
      MobicentsDiameterProperties.OBJECT_NAME = new ObjectName("diameter.home.dir");
    }
    catch (MalformedObjectNameException e) {
    }
    catch (NullPointerException e) {
    }
  }

}
