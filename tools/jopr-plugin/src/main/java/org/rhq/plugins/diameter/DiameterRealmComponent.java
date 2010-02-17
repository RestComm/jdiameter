package org.rhq.plugins.diameter;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.diameter.stack.management.ApplicationIdJMX;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.Realm;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.Property;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
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

public class DiameterRealmComponent implements ResourceComponent<DiameterServerComponent>, ConfigurationFacet, MeasurementFacet, OperationFacet, DeleteResourceFacet {

  private final Log logger = LogFactory.getLog(DiameterRealmComponent.class);

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
      DiameterConfiguration diameterConfig = mbeanUtils.getDiameterMBean().getDiameterConfiguration();
      
      return diameterConfig.getNetwork().getRealm(resourceContext.getResourceKey()) != null ? AvailabilityType.UP : AvailabilityType.DOWN;
    }
    catch (Exception e) {
      logger.error("", e);
      return AvailabilityType.DOWN;
    }
  }

  @Override
  public void getValues(MeasurementReport arg0, Set<MeasurementScheduleRequest> arg1) throws Exception {
    // TODO Auto-generated method stub
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

    Realm realm = diameterConfig.getNetwork().getRealm(resourceContext.getResourceKey());

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
    
    return config;
  }

  @Override
  public void updateResourceConfiguration(ConfigurationUpdateReport cur) {
    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
      DiameterStackMultiplexerMBean diameterMBean = mbeanUtils.getDiameterMBean();
  
      Configuration configuration = cur.getConfiguration();

      String name = configuration.getSimple("name").getStringValue();
      String peers = "";
      for(Property peer : configuration.getList("peersList").getList()) {
        peers += (peers.length() != 0 ? "," : "") + ((PropertyMap)peer).getSimple("ipAddress").getStringValue();
      }
      
      String localAction = configuration.getSimple("localAction").getStringValue();
      Boolean dynamic = configuration.getSimple("dynamic").getBooleanValue();
      Integer expTime = configuration.getSimple("expTime").getIntegerValue();

      Long appVendorId = null;
      Long appAuthId = null;
      Long appAcctId = null;
      for(Property appId : configuration.getList("applicationIdList").getList()) {
        PropertyMap mAppId = (PropertyMap)appId;
        appVendorId = mAppId.getSimple("vendorId").getLongValue();
        appAuthId = mAppId.getSimple("authAppId").getLongValue();
        appAcctId = mAppId.getSimple("acctAppId").getLongValue();
      }
      
      diameterMBean._Network_Realms_addRealm(name, peers, appVendorId, appAcctId == null ? 0 : appAcctId, appAuthId == null ? 0 : appAuthId, localAction, dynamic, expTime);
      cur.setStatus(ConfigurationUpdateStatus.SUCCESS);
    }
    catch (Exception e) {
      logger.error("", e);
      cur.setErrorMessageFromThrowable(e);
      cur.setStatus(ConfigurationUpdateStatus.FAILURE);
    }
  }

  @Override
  public void deleteResource() throws Exception {
    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = resourceContext.getParentResourceComponent().getMBeanServerUtils();
    DiameterStackMultiplexerMBean diameterMBean = mbeanUtils.getDiameterMBean();

    diameterMBean._Network_Realms_removeRealm(resourceContext.getResourceKey());
  }

}
