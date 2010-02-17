package org.rhq.plugins.diameter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.NetworkPeer;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.resource.ResourceType;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.plugins.diameter.utils.MBeanServerUtils;

public class DiameterNetworkPeerDiscoveryComponent implements ResourceDiscoveryComponent<DiameterServerComponent> {

  private final Log logger = LogFactory.getLog(DiameterNetworkPeerDiscoveryComponent.class);

  @Override
  public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<DiameterServerComponent> context) throws InvalidPluginConfigurationException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("discoverResources() called");
    }

    // Initialize the discovered resources
    Set<DiscoveredResourceDetails> discoveredNetworkPeers = new HashSet<DiscoveredResourceDetails>();

    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = context.getParentResourceComponent().getMBeanServerUtils();
    DiameterConfiguration config = mbeanUtils.getDiameterMBean().getDiameterConfiguration();

    // Add each resource to discovered list
    for(NetworkPeer p : config.getNetwork().getPeers().values()) {
      addPeer(p, discoveredNetworkPeers, context.getResourceType());
    }

    return discoveredNetworkPeers;
  }

  private void addPeer(NetworkPeer peer, Set<DiscoveredResourceDetails> discoveredNetworkPeers, ResourceType resourceType) {
    String key = peer.getName();
    String description = peer.getName();

    // Create new Peer resource
    DiscoveredResourceDetails discoveredNetworkPeer = new DiscoveredResourceDetails(resourceType, key, "[NP] "+ peer.getName(), "", description, null, null);

    if(logger.isInfoEnabled()) {
      logger.info("Created new Peer. name=" + peer.getName());
    }

    // Add properties to Peer resource
    Configuration config = discoveredNetworkPeer.getPluginConfiguration();
    config.put(new PropertySimple("name", peer.getName()));
    config.put(new PropertySimple("rating", peer.getRating()));
    config.put(new PropertySimple("attemptConnect", peer.getAttemptConnect()));
    PropertyMap portRange = new PropertyMap("portRange");
    portRange.put(new PropertySimple("portRangeLow", peer.getPortRangeLow()));
    portRange.put(new PropertySimple("portRangeHigh", peer.getPortRangeHigh()));
    config.put(portRange);
    config.put(new PropertySimple("ip", peer.getIp()));
    // TODO: config.put(new PropertySimple("securityRef", peer.getSecurityRef()));
    
    // Add it to our resources list
    discoveredNetworkPeers.add(discoveredNetworkPeer);
  }

}
