package org.rhq.plugins.diameter;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.DiameterStatistic;
import org.mobicents.diameter.stack.management.NetworkPeer;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.configuration.ConfigurationFacet;
import org.rhq.core.pluginapi.configuration.ConfigurationUpdateReport;
import org.rhq.core.pluginapi.inventory.DeleteResourceFacet;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.plugins.diameter.utils.MBeanServerUtils;

public class DiameterNetworkPeerComponent implements ResourceComponent<DiameterServerComponent>, ConfigurationFacet, MeasurementFacet, OperationFacet, DeleteResourceFacet {

  private final Log logger = LogFactory.getLog(DiameterNetworkPeerComponent.class);
  
  private ResourceContext<DiameterServerComponent> resourceContext;

  @Override
  public void start(ResourceContext<DiameterServerComponent> resourceContext) throws InvalidPluginConfigurationException, Exception {
    this.resourceContext = resourceContext;
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }

  @Override
  public AvailabilityType getAvailability() {
    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
      boolean isConnected = mbeanUtils.getDiameterMBean()._Network_Peers_isPeerConnected(resourceContext.getResourceKey());
      
      return isConnected ? AvailabilityType.UP : AvailabilityType.DOWN;
    }
    catch (Exception e) {
      logger.error("", e);
      return AvailabilityType.DOWN;
    }
  }

  @Override
  public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception {
    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
      DiameterConfiguration diameterConfig = mbeanUtils.getDiameterMBean().getDiameterConfiguration();
      
      HashMap<String,DiameterStatistic> stats = diameterConfig.getNetwork().getPeer(resourceContext.getPluginConfiguration().getSimpleValue("name", "")).getStatistics();
      
      for (MeasurementScheduleRequest request : metrics) {
        try {
          report.addData(new MeasurementDataNumeric(request, (Double)stats.get(request.getName()).getValue()));
        }
        catch (Exception e) {
          logger.error("", e);
          report.addData(new MeasurementDataNumeric(request, -1.0));
        }
      }
    }
    catch (Exception e) {
      logger.error("", e);
    }
  }

  @Override
  public OperationResult invokeOperation(String arg0, Configuration arg1) throws InterruptedException, Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Configuration loadResourceConfiguration() throws Exception {
    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
    DiameterConfiguration diameterConfig = mbeanUtils.getDiameterMBean().getDiameterConfiguration();

    Configuration config = new Configuration();

    NetworkPeer peer = diameterConfig.getNetwork().getPeer(resourceContext.getResourceKey());

    config.put(new PropertySimple("name", peer.getName()));
    config.put(new PropertySimple("rating", peer.getRating()));
    config.put(new PropertySimple("attemptConnect", peer.getAttemptConnect()));
    PropertyMap portRange = new PropertyMap("portRange");
    portRange.put(new PropertySimple("portRangeLow", peer.getPortRangeLow()));
    portRange.put(new PropertySimple("portRangeHigh", peer.getPortRangeHigh()));
    config.put(portRange);
    config.put(new PropertySimple("ip", peer.getIp()));

    return config;
  }

  @Override
  public void updateResourceConfiguration(ConfigurationUpdateReport cur) {
    try {
      throw new UnsupportedOperationException("Network Peer Configuration not supported.");
    }
    catch (Exception e) {
      logger.error("", e);
      cur.setStatus(ConfigurationUpdateStatus.FAILURE);
      cur.setErrorMessageFromThrowable(e);
    }
    
    cur.setStatus(ConfigurationUpdateStatus.SUCCESS);
  }

  @Override
  public void deleteResource() throws Exception {
    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
    DiameterStackMultiplexerMBean diameterMBean = mbeanUtils.getDiameterMBean();

    diameterMBean._Network_Peers_removePeer(resourceContext.getResourceKey());
  }

}
