package org.rhq.plugins.diameter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.management.ApplicationIdJMX;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.Realm;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.diameter.utils.MBeanServerUtils;

public class DiameterRealmDiscoveryComponent implements ResourceDiscoveryComponent<DiameterServerComponent> {

  private final Log logger = LogFactory.getLog(DiameterRealmDiscoveryComponent.class);

  @Override
  public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<DiameterServerComponent> context) throws InvalidPluginConfigurationException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("discoverResources() called");
    }

    // Initialize the discovered resources
    Set<DiscoveredResourceDetails> discoveredRealms = new HashSet<DiscoveredResourceDetails>();

    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = context.getParentResourceComponent().getMBeanServerUtils();
    DiameterConfiguration config = mbeanUtils.getDiameterMBean().getDiameterConfiguration();

    // Add each resource to discovered list
    for(Realm r : config.getNetwork().getRealms().values()) {
      addRealm(r, discoveredRealms, context.getResourceType());
    }

    return discoveredRealms;
  }

  private void addRealm(Realm realm, Set<DiscoveredResourceDetails> discoveredRealms, ResourceType resourceType) {
    String key = realm.getName();
    String description = realm.getName();

    // Create new Realm resource
    DiscoveredResourceDetails discoveredRealm = new DiscoveredResourceDetails(resourceType, key, realm.getName(), "", description, null, null);

    if(logger.isInfoEnabled()) {
      logger.info("Created new Realm. name=" + realm.getName());
    }

    // Add properties to Peer resource
    Configuration config = discoveredRealm.getPluginConfiguration();
    config.put(new PropertySimple("name", realm.getName()));
    PropertyList peers = new PropertyList("peersList");
    for(String peer : realm.getPeers()) {
      PropertyMap entry = new PropertyMap("peersDefinition");
      entry.put(new PropertySimple("ipAddress", peer));
      peers.add(entry);
    }
    config.put(peers);
    config.put(new PropertySimple("localAction", realm.getLocalAction()));
    config.put(new PropertySimple("dynamic", realm.getDynamic()));
    config.put(new PropertySimple("expTime", realm.getExpTime()));

    // Concurrent Entities
    PropertyList appIdList = new PropertyList("applicationIdList");
    for(ApplicationIdJMX appId : realm.getApplicationIds()) {
      PropertyMap entry = new PropertyMap("applicationIdDefinition");
      entry.put(new PropertySimple("vendorId", appId.getVendorId()));
      entry.put(new PropertySimple("authAppId", appId.getAuthApplicationId()));
      entry.put(new PropertySimple("acctAppId", appId.getAcctApplicationId()));

      appIdList.add(entry);
    }
    config.put(appIdList);
    
    // Add it to our resources list
    discoveredRealms.add(discoveredRealm);
  }

}
