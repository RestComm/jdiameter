package org.rhq.plugins.diameter;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.management.ApplicationIdJMX;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.DiameterStatistic;
import org.mobicents.diameter.stack.management.LocalPeer;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.configuration.ConfigurationFacet;
import org.rhq.core.pluginapi.configuration.ConfigurationUpdateReport;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.plugins.diameter.utils.MBeanServerUtils;

public class DiameterLocalPeerComponent implements ResourceComponent<DiameterServerComponent>, ConfigurationFacet, MeasurementFacet, OperationFacet {

  private final Log logger = LogFactory.getLog(DiameterLocalPeerComponent.class);

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
      return mbeanUtils.getDiameterMBean()._LocalPeer_isActive() ? AvailabilityType.UP : AvailabilityType.DOWN;
    }
    catch (Exception e) {
      return AvailabilityType.DOWN;
    }
  }

  @Override
  public void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception {
    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
      DiameterConfiguration diameterConfig = mbeanUtils.getDiameterMBean().getDiameterConfiguration();
      
      HashMap<String,DiameterStatistic> stats = diameterConfig.getLocalPeer().getStatistics();
      
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

    LocalPeer localPeer = diameterConfig.getLocalPeer();

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
    PropertyList appIdList = new PropertyList("applicationIdList");
    for(ApplicationIdJMX appId : localPeer.getDefaultApplications()) {
      PropertyMap entry = new PropertyMap("applicationIdDefinition");
      entry.put(new PropertySimple("vendorId", appId.getVendorId()));
      entry.put(new PropertySimple("authAppId", appId.getAuthApplicationId()));
      entry.put(new PropertySimple("acctAppId", appId.getAcctApplicationId()));

      appIdList.add(entry);
    }
    config.put(appIdList);
    // TODO: config.put(new PropertySimple("overloadMonitor", peer.getOverloadMonitor()));
    
    return config;
  }

  @Override
  public void updateResourceConfiguration(ConfigurationUpdateReport cur) {
    cur.setStatus(ConfigurationUpdateStatus.SUCCESS);
  }

}
