package org.rhq.plugins.diameter;

import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;
import org.mobicents.diameter.stack.management.ConcurrentEntity;
import org.mobicents.diameter.stack.management.DiameterConfiguration;
import org.mobicents.diameter.stack.management.Parameters;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.ConfigurationUpdateStatus;
import org.rhq.core.domain.configuration.Property;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.domain.resource.CreateResourceStatus;
import org.rhq.core.pluginapi.configuration.ConfigurationFacet;
import org.rhq.core.pluginapi.configuration.ConfigurationUpdateReport;
import org.rhq.core.pluginapi.inventory.CreateChildResourceFacet;
import org.rhq.core.pluginapi.inventory.CreateResourceReport;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;
import org.rhq.core.pluginapi.operation.OperationFacet;
import org.rhq.core.pluginapi.operation.OperationResult;
import org.rhq.plugins.diameter.utils.DiameterPluginUtils;
import org.rhq.plugins.diameter.utils.MBeanServerUtils;

public class DiameterServerComponent implements DiameterPluginUtils, MeasurementFacet, OperationFacet, ConfigurationFacet, CreateChildResourceFacet {

  private final Log logger = LogFactory.getLog(DiameterServerComponent.class);

  volatile MBeanServerUtils mBeanServerUtils = null;

  private ResourceContext resourceContext;

  private String diameterState;

  @Override
  public MBeanServerUtils getMBeanServerUtils() {
    return this.mBeanServerUtils;
  }

  @Override
  public void start(ResourceContext resourceContext) throws InvalidPluginConfigurationException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Component > Start Called.");
    }

    this.resourceContext = resourceContext;

    // XXX: Remove this, not needed.
    // this.deployFolder = resourceContext.getPluginConfiguration().getSimple(ApplicationServerPluginConfigurationProperties.SERVER_HOME_DIR).getStringValue() + File.separator  + "deploy";
    // Connect to the JBAS instance's Profile Service and JMX MBeanServer.

    Configuration pluginConfig = resourceContext.getPluginConfiguration();

    String namingURL = pluginConfig.getSimple("namingURL").getStringValue();
    String principal = pluginConfig.getSimple("principal").getStringValue();
    String credentials = pluginConfig.getSimple("credentials").getStringValue();

    this.mBeanServerUtils = new MBeanServerUtils(namingURL);
  }

  @Override
  public void stop() {
    // TODO Auto-generated method stub
  }

  @Override
  public AvailabilityType getAvailability() {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Component > getAvailability Called.");
    }

    if (this.mBeanServerUtils != null) {
      try {
        ObjectName diameterObjectName = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");
        MBeanServerConnection connection = this.mBeanServerUtils.getConnection();
        this.diameterState = (String) connection.getAttribute(diameterObjectName, "StateString");
        if(logger.isInfoEnabled()) {
          logger.info("Diameter Server Component > State is '" + this.diameterState + ".");
        }
        return AvailabilityType.UP;

      }
      catch (Exception e) {
        logger.error("Failed to obtain Mobicents Diameter state.", e);
        return AvailabilityType.DOWN;
      }
    }
    else {
      logger.error("Returning availability as DOWN");
      return AvailabilityType.DOWN;
    }
  }

  @Override
  public void getValues(MeasurementReport mr, Set<MeasurementScheduleRequest> msrs) throws Exception {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Component > getValues Called.");
    }
  }

  @Override
  public CreateResourceReport createResource(CreateResourceReport crr) {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Component > createResource Called.");
    }

    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = getMBeanServerUtils();
      DiameterStackMultiplexerMBean diameterMBean = mbeanUtils.getDiameterMBean();
  
      Configuration configuration = crr.getResourceConfiguration();

      if(crr.getResourceType().getName().equals("Realm")) {
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
        
        crr.setResourceKey(name);
        crr.setResourceName(name);
        
        diameterMBean._Network_Realms_addRealm(name, peers, appVendorId, appAcctId, appAuthId, localAction, dynamic, expTime);
      }
      else if(crr.getResourceType().getName().equals("NetworkPeer")) {
        String name = configuration.getSimple("name").getStringValue();
        Integer rating = configuration.getSimple("rating").getIntegerValue();
        Boolean attemptConnect = configuration.getSimple("attemptConnect").getBooleanValue();
        //PropertyMap portRangeMap = configuration.getMap("portRange");
        //Integer portRangeLow = portRangeMap.getSimple("portRangeLow").getIntegerValue();
        //Integer portRangeHigh = portRangeMap.getSimple("portRangeHigh").getIntegerValue();
        //String ip = configuration.getSimple("ip").getStringValue();
        
        crr.setResourceKey(name);
        crr.setResourceName(name);

        diameterMBean._Network_Peers_addPeer(name, attemptConnect, rating);
      }

      crr.setStatus(CreateResourceStatus.SUCCESS);
    }
    catch (Exception e) {
      // TODO: handle exception
      logger.error("", e);
      crr.setStatus(CreateResourceStatus.FAILURE);
      crr.setException(e);
    }

    return crr;
  }

  @Override
  public OperationResult invokeOperation(String name, Configuration parameters) throws InterruptedException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Component > invokeOperation Called.");
    }

    ObjectName diameterObjectName = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");
    MBeanServerConnection connection = this.mBeanServerUtils.getConnection();

    OperationResult result = new OperationResult();

    if(name.equals("dumpStackConfiguration")) {
      String stackConfig = (String) connection.invoke(diameterObjectName, "dumpStackConfiguration", new Object[]{}, new String[]{});
      result.getComplexResults().put(new PropertySimple("result", stackConfig));
    }
    else if(name.equals("stopStack")) {
      connection.invoke(diameterObjectName, "stopStack", new Object[]{}, new String[]{});
      result.getComplexResults().put(new PropertySimple("result", "Operation completed successfully."));
    }
    else if(name.equals("startStack")) {
      connection.invoke(diameterObjectName, "startStack", new Object[]{}, new String[]{});
      result.getComplexResults().put(new PropertySimple("result", "Operation completed successfully."));
    }
    else if(name.equals("setValidation")) {
      connection.invoke(diameterObjectName, "_Validation_setEnabled", new Object[]{parameters.getSimple("validation").getBooleanValue()}, new String[]{boolean.class.getName()});
      result.getComplexResults().put(new PropertySimple("result", "Operation completed successfully."));
    }
    
    return result;
  }

  @Override
  public Configuration loadResourceConfiguration() throws Exception {
    // Fetch the configuration object
    MBeanServerUtils mbeanUtils = getMBeanServerUtils();
    DiameterConfiguration diameterConfig = mbeanUtils.getDiameterMBean().getDiameterConfiguration();

    Configuration config = new Configuration();

    Parameters parameters = diameterConfig.getParameters();

    config.put(new PropertySimple("AcceptUndefinedPeer", parameters.getAcceptUndefinedPeer()));
    config.put(new PropertySimple("DuplicateProtection", parameters.getDuplicateProtection()));
    config.put(new PropertySimple("DuplicateTimer", parameters.getDuplicateTimer()));
    config.put(new PropertySimple("UseUriAsFqdn", parameters.getUseUriAsFqdn()));
    config.put(new PropertySimple("QueueSize", parameters.getQueueSize()));
    config.put(new PropertySimple("MessageTimeout", parameters.getMessageTimeout()));
    config.put(new PropertySimple("StopTimeout", parameters.getStopTimeout()));
    config.put(new PropertySimple("CeaTimeout", parameters.getCeaTimeout()));
    config.put(new PropertySimple("IacTimeout", parameters.getIacTimeout()));
    config.put(new PropertySimple("DwaTimeout", parameters.getDwaTimeout()));
    config.put(new PropertySimple("DpaTimeout", parameters.getDpaTimeout()));
    config.put(new PropertySimple("RecTimeout", parameters.getRecTimeout()));
    
    // Concurrent Entities
    PropertyList concurrentList = new PropertyList("ConcurrentEntitiesList");
    for(ConcurrentEntity concurrentEntity : parameters.getConcurrentEntities().values()) {
      PropertyMap entry = new PropertyMap("ConcurrentEntitiesDefinition");
      entry.put(new PropertySimple("name", concurrentEntity.getName()));
      entry.put(new PropertySimple("description", concurrentEntity.getDescription()));
      entry.put(new PropertySimple("size", concurrentEntity.getSize()));

      concurrentList.add(entry);
    }
    config.put(concurrentList);
    
    config.put(new PropertySimple("StatisticLoggerDelay", parameters.getStatisticLogger_Delay()));
    config.put(new PropertySimple("StatisticLoggerPause", parameters.getStatisticLogger_Pause()));

    return config;
  }

  @Override
  public void updateResourceConfiguration(ConfigurationUpdateReport cur) {
    try {
      // Fetch the configuration object
      MBeanServerUtils mbeanUtils = getMBeanServerUtils();
      DiameterStackMultiplexerMBean diameterMBean = mbeanUtils.getDiameterMBean();
      
      diameterMBean._Parameters_setAcceptUndefinedPeer(((PropertySimple)cur.getConfiguration().get("AcceptUndefinedPeer")).getBooleanValue());
      //diameterMBean._Parameters_setDuplicateProtection(((PropertySimple)cur.getConfiguration().get("DuplicateProtection")).getBooleanValue());
      diameterMBean._Parameters_setDuplicateTimer(((PropertySimple)cur.getConfiguration().get("DuplicateTimer")).getLongValue());
      diameterMBean._Parameters_setUseUriAsFqdn(((PropertySimple)cur.getConfiguration().get("UseUriAsFqdn")).getBooleanValue());
      //diameterMBean._Parameters_setQueueSize(((PropertySimple)cur.getConfiguration().get("QueueSize")).getIntegerValue());
      diameterMBean._Parameters_setMessageTimeout(((PropertySimple)cur.getConfiguration().get("MessageTimeout")).getLongValue());
      diameterMBean._Parameters_setStopTimeout(((PropertySimple)cur.getConfiguration().get("StopTimeout")).getLongValue());
      diameterMBean._Parameters_setCeaTimeout(((PropertySimple)cur.getConfiguration().get("CeaTimeout")).getLongValue());
      diameterMBean._Parameters_setIacTimeout(((PropertySimple)cur.getConfiguration().get("IacTimeout")).getLongValue());
      diameterMBean._Parameters_setDwaTimeout(((PropertySimple)cur.getConfiguration().get("DwaTimeout")).getLongValue());
      diameterMBean._Parameters_setDpaTimeout(((PropertySimple)cur.getConfiguration().get("DpaTimeout")).getLongValue());
      diameterMBean._Parameters_setRecTimeout(((PropertySimple)cur.getConfiguration().get("RecTimeout")).getLongValue());
      
      for(Property p : cur.getConfiguration().getList("ConcurrentEntitiesList").getList()) {
        PropertyMap pMap = (PropertyMap)p;
        String name = ((PropertySimple)pMap.get("name")).getStringValue();
        String desc = ((PropertySimple)pMap.get("description")).getStringValue();
        Integer size = ((PropertySimple)pMap.get("size")).getIntegerValue();
        diameterMBean._Parameters_setConcurrentEntity(name, desc, size);
      }
      
      diameterMBean._Parameters_setStatisticLoggerDelay(((PropertySimple)cur.getConfiguration().get("StatisticLoggerDelay")).getLongValue());
      diameterMBean._Parameters_setStatisticLoggerPause(((PropertySimple)cur.getConfiguration().get("StatisticLoggerPause")).getLongValue());
    }
    catch (Exception e) {
      // TODO: handle exception
      logger.error("", e);
      cur.setStatus(ConfigurationUpdateStatus.FAILURE);
      cur.setErrorMessageFromThrowable(e);
    }
    //diameterConfig.getParameters().setAcceptUndefinedPeer(arg0.getConfiguration().getProperties().)
    cur.setStatus(ConfigurationUpdateStatus.SUCCESS);
  }

}
