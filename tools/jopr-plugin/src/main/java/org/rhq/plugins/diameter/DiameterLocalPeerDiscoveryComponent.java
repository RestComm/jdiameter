package org.rhq.plugins.diameter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.management.ApplicationIdJMX;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.LocalPeer;
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

public class DiameterLocalPeerDiscoveryComponent implements ResourceDiscoveryComponent<DiameterServerComponent> {

  private final Log logger = LogFactory.getLog(DiameterLocalPeerDiscoveryComponent.class);

  @Override
  public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<DiameterServerComponent> context) throws InvalidPluginConfigurationException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("discoverResources() called");
    }

    // Initialize the discovered resources
    Set<DiscoveredResourceDetails> discoveredLocalPeers = new HashSet<DiscoveredResourceDetails>();

    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = context.getParentResourceComponent().getMBeanServerUtils();
    DiameterConfiguration config = mbeanUtils.getDiameterMBean().getDiameterConfiguration();

    // Add each resource to discovered list
    addLocalPeer(config.getLocalPeer(), discoveredLocalPeers, context.getResourceType());

    return discoveredLocalPeers;
  }

  private void addLocalPeer(LocalPeer localPeer, Set<DiscoveredResourceDetails> discoveredLocalPeers, ResourceType resourceType) {
    String key = localPeer.getUri();
    String description = localPeer.getUri();

    // Create new Peer resource
    DiscoveredResourceDetails discoveredLocalPeer = new DiscoveredResourceDetails(resourceType, key, "[LP] "+key, localPeer.getFirmwareRev().toString(), description, null, null);

    if(logger.isInfoEnabled()) {
      logger.info("Created new Local Peer. key=" + key);
    }

    // Add properties to Peer resource
    Configuration config = discoveredLocalPeer.getPluginConfiguration();
    config.put(new PropertySimple("uri", localPeer.getUri()));
    PropertyList ipAddressesList = new PropertyList("ipAddressesList");
    for(String ipAddress : localPeer.getIpAddresses()) {
      PropertyMap entry = new PropertyMap("ipAddressesDefinition");
      entry.put(new PropertySimple("ipAddress", ipAddress));
      ipAddressesList.add(entry);
    }
    config.put(ipAddressesList);
    config.put(new PropertySimple("realm", localPeer.getRealm()));
    config.put(new PropertySimple("vendorId", localPeer.getVendorId()));
    config.put(new PropertySimple("productName", localPeer.getProductName()));
    config.put(new PropertySimple("firmwareRev", localPeer.getFirmwareRev()));
    PropertyList appIdList = new PropertyList("ApplicationIdList");
    for(ApplicationIdJMX appId : localPeer.getDefaultApplications()) {
      PropertyMap entry = new PropertyMap("ApplicationIdDefinition");
      entry.put(new PropertySimple("VendorId", appId.getVendorId()));
      entry.put(new PropertySimple("AuthAppId", appId.getAuthApplicationId()));
      entry.put(new PropertySimple("AcctAppId", appId.getAcctApplicationId()));

      appIdList.add(entry);
    }
    config.put(appIdList);
    // TODO: config.put(new PropertySimple("overloadMonitor", peer.getOverloadMonitor()));
    
    // Add it to our resources list
    discoveredLocalPeers.add(discoveredLocalPeer);
  }

}
