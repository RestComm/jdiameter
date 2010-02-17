package org.rhq.plugins.diameter.utils;

import org.rhq.core.pluginapi.inventory.ResourceComponent;

public interface DiameterPluginUtils<T extends ResourceComponent> extends ResourceComponent<T> {
  public MBeanServerUtils getMBeanServerUtils();
}
