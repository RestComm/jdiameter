package org.rhq.plugins.diameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.util.file.FilenamePrefixFilter;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertyList;
import org.rhq.core.domain.configuration.PropertyMap;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.pluginapi.event.log.LogFileEventResourceComponentHelper;
import org.rhq.core.pluginapi.inventory.ClassLoaderFacet;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ManualAddFacet;
import org.rhq.core.pluginapi.inventory.ProcessScanResult;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;
import org.rhq.core.pluginapi.util.FileUtils;
import org.rhq.core.system.ProcessInfo;
import org.rhq.plugins.diameter.jbossas5.ApplicationServerPluginConfigurationProperties;
import org.rhq.plugins.diameter.jbossas5.helper.JBossInstallationInfo;
import org.rhq.plugins.diameter.jbossas5.helper.JBossInstanceInfo;
import org.rhq.plugins.diameter.jbossas5.helper.JBossProperties;
import org.rhq.plugins.diameter.jbossas5.helper.MobicentsDiameterProperties;
import org.rhq.plugins.diameter.jbossas5.util.JBossASDiscoveryUtils;
import org.rhq.plugins.diameter.jbossas5.util.JnpConfig;

public class DiameterServerDiscoveryComponent implements ResourceDiscoveryComponent<DiameterServerComponent>, ManualAddFacet<DiameterServerComponent>, ClassLoaderFacet<DiameterServerComponent> {

  private final Log logger = LogFactory.getLog(DiameterServerDiscoveryComponent.class);

  private static final String CHANGE_ME = "***CHANGE_ME***";
  private static final String ANY_ADDRESS = "0.0.0.0";
  private static final String LOCALHOST = "127.0.0.1";

  private static final String JBOSS_SERVICE_XML = "conf" + File.separator + "jboss-service.xml";
  private static final String JBOSS_NAMING_SERVICE_XML = "deploy" + File.separator + "naming-service.xml";
  private static final String JAVA_HOME_ENV_VAR = "JAVA_HOME";

  private static final String[] CLIENT_JARS = new String[] { "client/jbossall-client.jar",
    "common/lib/jboss-security-aspects.jar", "lib/jboss-managed.jar", "lib/jboss-metatype.jar",
    "lib/jboss-dependency.jar" };

  @Override
  public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext<DiameterServerComponent> discoveryContext) throws InvalidPluginConfigurationException, Exception {
    if(logger.isInfoEnabled()) {
      logger.info("Discovering Mobicents Diameter Resources...");
    }

    Set<DiscoveredResourceDetails> resources = new HashSet<DiscoveredResourceDetails>();
    DiscoveredResourceDetails inProcessJBossAS = discoverInProcessJBossAS(discoveryContext);
    if (inProcessJBossAS != null) {
      // If we're running inside a JBoss AS JVM, that's the only AS instance we want to discover.
      resources.add(inProcessJBossAS);
    }
    else {
      // Otherwise, scan the process table for external AS instances.
      resources.addAll(discoverExternalJBossAsProcesses(discoveryContext));
    }
    if(logger.isInfoEnabled()) {
      logger.info("Discovered " + resources.size() + " Mobicents Diameter Resources.");
    }

    return resources;
  }

  @Override
  public DiscoveredResourceDetails discoverResource(Configuration pluginConfig, ResourceDiscoveryContext<DiameterServerComponent> context) throws InvalidPluginConfigurationException {
    ProcessInfo processInfo = null;
    String jbossHomeDir = pluginConfig.getSimple(ApplicationServerPluginConfigurationProperties.SERVER_HOME_DIR).getStringValue();

    String jbossDeploy = jbossHomeDir + File.separator + "deploy" + File.separator;
    File f = new File(jbossDeploy);
    String[] diameterList = f.list(new FilenamePrefixFilter("mobicents-diameter-mux"));
    if(diameterList.length > 1) {
      logger.warn("More than one Mobicents Diameter MUX were found.");
    }
    else if(diameterList.length < 1) {
      logger.error("Skipping discovery for Mobicents Diameter " + jbossDeploy + ", because Mobicents Diameter dir could not be found.");
      throw new InvalidPluginConfigurationException("");
    }
    
    String mobicentsDiameterHome = jbossDeploy + diameterList[0];
    String diameterVersion = diameterList[0].replaceFirst("mobicents-diameter-mux-", "").replaceAll(".sar", "");
    pluginConfig.setNotes(diameterVersion);
    
    File mobicentsDiameter = new File(mobicentsDiameterHome);

    try {
      if (!mobicentsDiameter.getCanonicalFile().isDirectory()) {
        logger.warn("Skipping manual add for Mobicents Diameter " + mobicentsDiameterHome + ", because Mobicents Diameter dir 'mobicents-diameter.sar' does not exist or is not a directory.");
      }
    }
    catch (IOException e) {
      logger.error("Skipping discovery for Mobicents Diameter " + mobicentsDiameterHome + ", because Mobicents Diameter dir could not be canonicalized.", e);
      throw new InvalidPluginConfigurationException(e);
    }

    pluginConfig.put(new PropertySimple(MobicentsDiameterProperties.DIAMETER_HOME_DIR, mobicentsDiameterHome));

    JBossInstallationInfo installInfo;
    try {
      installInfo = new JBossInstallationInfo(new File(jbossHomeDir));
    }
    catch (IOException e) {
      throw new InvalidPluginConfigurationException(e);
    }

    DiscoveredResourceDetails resourceDetails;
    try {
      resourceDetails = createResourceDetails(context, pluginConfig, processInfo, installInfo);
    }
    catch (Exception e) {
      throw new InvalidPluginConfigurationException(e);
    }
    return resourceDetails;
  }

  @Override
  public List<URL> getAdditionalClasspathUrls(ResourceDiscoveryContext<DiameterServerComponent> context, DiscoveredResourceDetails details) throws Exception {
    if(logger.isInfoEnabled()) {
      logger.info("Diameter Server Discovery Component > getAdditionalClasspathUrls called.");
    }
    
    Configuration pluginConfig = details.getPluginConfiguration();
    String homeDir = pluginConfig.getSimple(ApplicationServerPluginConfigurationProperties.HOME_DIR).getStringValue();

    List<URL> clientJars = new ArrayList<URL>();

    for (String jarFileName : CLIENT_JARS) {
        File clientJar = new File(homeDir, jarFileName);
        if (!clientJar.exists()) {
            throw new FileNotFoundException("Cannot find [" + clientJar + "] - unable to manage server.");
        }
        if (!clientJar.canRead()) {
            throw new IOException("Cannot read [" + clientJar + "] - unable to manage server.");
        }
        clientJars.add(clientJar.toURI().toURL());
    }

    return clientJars;
  }

  // Private Methods -----------------------------------------------------

  private DiscoveredResourceDetails discoverInProcessJBossAS(ResourceDiscoveryContext discoveryContext) {
    try {
      return null;// new InProcessJBossASDiscovery().discoverInProcessJBossAS(discoveryContext);
    }
    catch (Throwable t) {
      if(logger.isDebugEnabled()) {
        logger.debug("In-process JBoss AS discovery failed - we are probably not running embedded within JBoss AS.", t);
      }
      return null;
    }
  }

  private Set<DiscoveredResourceDetails> discoverExternalJBossAsProcesses(ResourceDiscoveryContext discoveryContext) throws Exception {
    Set<DiscoveredResourceDetails> resources = new HashSet<DiscoveredResourceDetails>();
    List<ProcessScanResult> autoDiscoveryResults = discoveryContext.getAutoDiscoveredProcesses();

    for (ProcessScanResult autoDiscoveryResult : autoDiscoveryResults) {
      ProcessInfo processInfo = autoDiscoveryResult.getProcessInfo();
      if (logger.isDebugEnabled()) {
        logger.debug("Discovered JBoss AS process: " + processInfo);
      }

      JBossInstanceInfo cmdLine;
      try {
        cmdLine = new JBossInstanceInfo(processInfo);
      }
      catch (Exception e) {
        logger.error("Failed to process JBoss AS command line: " + Arrays.asList(processInfo.getCommandLine()), e);
        continue;
      }

      // Skip it if it's an AS/EAP/SOA-P version we don't support.
      JBossInstallationInfo installInfo = cmdLine.getInstallInfo();
      // if (!isSupportedProduct(installInfo)) {
      // // continue;
      // }

      File installHome = new File(cmdLine.getSystemProperties().getProperty(JBossProperties.HOME_DIR));
      File configDir = new File(cmdLine.getSystemProperties().getProperty(JBossProperties.SERVER_HOME_DIR));

      // The config dir might be a symlink - call getCanonicalFile() to resolve it if so, before calling 
      // isDirectory() (isDirectory() returns false for a symlink, even if it points at a directory).
      try {
        if (!configDir.getCanonicalFile().isDirectory()) {
          logger.warn("Skipping discovery for JBoss AS process " + processInfo + ", because configuration dir '" + configDir + "' does not exist or is not a directory.");
          continue;
        }
      }
      catch (IOException e) {
        logger.error("Skipping discovery for JBoss AS process " + processInfo + ", because configuration dir '" + configDir + "' could not be canonicalized.", e);
        continue;
      }

      // Find Mobicents Diameter folder
      File mobicentsDiameter = new File(cmdLine.getSystemProperties().getProperty(MobicentsDiameterProperties.DIAMETER_HOME_DIR));
      try {
        if (!mobicentsDiameter.getCanonicalFile().isDirectory()) {
          logger.warn("Skipping discovery for Mobicents Diameter process " + processInfo + ", because Mobicents Diameter dir " + mobicentsDiameter.getCanonicalFile().getAbsolutePath() + " does not exist or is not a directory.");
          continue;
        }
      }
      catch (IOException e) {
        logger.error("Skipping discovery for Mobicents Diameter process " + processInfo + ", because Mobicents Diameter dir could not be canonicalized.", e);
        continue;
      }

      Configuration pluginConfiguration = discoveryContext.getDefaultPluginConfiguration();

      String jnpURL = getJnpURL(cmdLine, installHome, configDir);

      // TODO? Set the connection type - local or remote

      // Set the required props...
      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.NAMING_URL, jnpURL));
      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.HOME_DIR, installHome.getAbsolutePath()));
      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.SERVER_HOME_DIR, configDir));

      pluginConfiguration.put(new PropertySimple(MobicentsDiameterProperties.DIAMETER_HOME_DIR, cmdLine.getSystemProperties().getProperty(MobicentsDiameterProperties.DIAMETER_HOME_DIR)));
      pluginConfiguration.put(new PropertySimple(MobicentsDiameterProperties.DIAMETER_VERSION, cmdLine.getSystemProperties().getProperty(MobicentsDiameterProperties.DIAMETER_VERSION)));

      // Set the optional props...
      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.SERVER_NAME, cmdLine.getSystemProperties().getProperty(JBossProperties.SERVER_NAME)));
      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.BIND_ADDRESS, cmdLine.getSystemProperties().getProperty(JBossProperties.BIND_ADDRESS)));

      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.SERVER_TMP_DIR, cmdLine.getSystemProperties().getProperty(JBossProperties.SERVER_TEMP_DIR)));
      JBossASDiscoveryUtils.UserInfo userInfo = JBossASDiscoveryUtils.getJmxInvokerUserInfo(configDir);
      if (userInfo != null) {
        pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.PRINCIPAL, userInfo.getUsername()));
        pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.CREDENTIALS, userInfo.getPassword()));
      }

      String javaHome = processInfo.getEnvironmentVariable(JAVA_HOME_ENV_VAR);
      if (javaHome == null && logger.isDebugEnabled()) {
        logger.debug("JAVA_HOME environment variable not set in JBoss AS process - defaulting " + ApplicationServerPluginConfigurationProperties.JAVA_HOME + " connection property to the plugin container JRE dir.");
        javaHome = System.getenv(JAVA_HOME_ENV_VAR);
      }

      pluginConfiguration.put(new PropertySimple(ApplicationServerPluginConfigurationProperties.JAVA_HOME, javaHome));

      initLogEventSourcesConfigProp(configDir, pluginConfiguration);

      // TODO: Init props that have static defaults.
      // setPluginConfigurationDefaults(pluginConfiguration);

      DiscoveredResourceDetails resourceDetails = createResourceDetails(discoveryContext, pluginConfiguration, processInfo, installInfo);
      resources.add(resourceDetails);
    }
    return resources;
  }

  private DiscoveredResourceDetails createResourceDetails(ResourceDiscoveryContext discoveryContext,
      Configuration pluginConfig, ProcessInfo processInfo, JBossInstallationInfo installInfo) throws Exception {
    String serverHomeDir = pluginConfig.getSimple(MobicentsDiameterProperties.DIAMETER_HOME_DIR).getStringValue();
    File absoluteConfigPath = resolvePathRelativeToHomeDir(pluginConfig, serverHomeDir);

    // Canonicalize the config path, so it's consistent no matter how it's entered.
    // This prevents two servers with different forms of the same config path, but
    // that are actually the same server, from ending up in inventory.
    // JON: fix for JBNADM-2634 - do not resolve symlinks (ips, 12/18/07)
    String key = FileUtils.getCanonicalPath(absoluteConfigPath.getPath());

    String bindAddress = pluginConfig.getSimple(ApplicationServerPluginConfigurationProperties.BIND_ADDRESS)
    .getStringValue();
    String namingUrl = pluginConfig.getSimple(ApplicationServerPluginConfigurationProperties.NAMING_URL)
    .getStringValue();

    // Only include the JNP port in the Resource name if its value is not "***CHANGE_ME***".
    String namingPort = getNamingPort(namingUrl);

    // TODO: Obtain Diameter Name/Version/Vendor from MBean
    /*
    ObjectName sleemanagement = new ObjectName(SleeManagementMBean.OBJECT_NAME);

    MBeanServerUtils mbeanUtils = new MBeanServerUtils(namingUrl);
    MBeanServerConnection connection = mbeanUtils.getConnection();

    String sleeName = (String) connection.getAttribute(sleemanagement, "SleeName");
    String sleeVersion = (String) connection.getAttribute(sleemanagement, "SleeVersion");
    String sleeVendor = (String) connection.getAttribute(sleemanagement, "SleeVendor");
     */
    String diameterName = "Mobicents Diameter";
    String diameterVersion = pluginConfig.getSimple(MobicentsDiameterProperties.DIAMETER_VERSION).getStringValue(); //"1.1.1.GA";
    String diameterVendor = "JBoss/Red Hat";

    String description = diameterName + " v" + diameterVersion + " by " + diameterVendor;

    String name = formatServerName(diameterName + " " + diameterVersion, bindAddress, namingPort);

    return new DiscoveredResourceDetails(discoveryContext.getResourceType(), key, name, diameterVersion, description, pluginConfig, processInfo);
  }

  private String formatServerName(String baseName, String bindingAddress, String jnpPort) {
    String details = null;
    if ((bindingAddress != null) && (jnpPort != null && !jnpPort.equals(CHANGE_ME))) {
      details = bindingAddress + ":" + jnpPort;
    }
    else if ((bindingAddress == null) && (jnpPort != null && !jnpPort.equals(CHANGE_ME))) {
      details = jnpPort;
    }
    else if (bindingAddress != null) {
      details = bindingAddress;
    }

    return baseName + ((details != null) ? (" (" + details + ")") : "");
  }

  // Get JNP related info ------------------------------------------------

  private String getJnpURL(JBossInstanceInfo cmdLine, File installHome, File configDir) {
    File jnpServiceUrlFile = new File(configDir, "data/jnp-service.url");
    if (jnpServiceUrlFile.exists() && jnpServiceUrlFile.canRead()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(jnpServiceUrlFile));
        String jnpUrl = br.readLine();
        if (jnpUrl != null) {
          if (logger.isDebugEnabled()) {
            logger.debug("Read JNP URL from jnp-service.url file: " + jnpUrl);
          }
          return jnpUrl;
        }
      } catch (IOException ioe) {
        // Nothing to do
      }
    }

    logger.warn("Failed to read JNP URL from '" + jnpServiceUrlFile + "'.");

    // Above did not work, so fall back to our previous scheme
    JnpConfig jnpConfig = getJnpConfig(installHome, configDir, cmdLine.getSystemProperties());
    String jnpAddress = (jnpConfig.getJnpAddress() != null) ? jnpConfig.getJnpAddress() : CHANGE_ME;
    if (ANY_ADDRESS.equals(jnpAddress)) {
      jnpAddress = LOCALHOST;
    }
    String jnpPort = (jnpConfig.getJnpPort() != null) ? String.valueOf(jnpConfig.getJnpPort()) : CHANGE_ME;
    return "jnp://" + jnpAddress + ":" + jnpPort;
  }

  private static JnpConfig getJnpConfig(File installHome, File configDir, Properties props) {
    File serviceXML = new File(configDir, JBOSS_SERVICE_XML);
    JnpConfig config = JnpConfig.getConfig(installHome, serviceXML, props);
    if ((config == null) || (config.getJnpPort() == null)) {
      File namingServiceFile = new File(configDir, JBOSS_NAMING_SERVICE_XML);
      if (namingServiceFile.exists()) {
        config = JnpConfig.getConfig(installHome, namingServiceFile, props);
      }
    }
    return config;
  }

  private String getNamingPort(String namingUrl) {
    // Only include the JNP port in the Resource name if its value is not "***CHANGE_ME***".
    String namingPort = null;
    // noinspection ConstantConditions
    int colonIndex = namingUrl.lastIndexOf(':');
    if ((colonIndex != -1) && (colonIndex != (namingUrl.length() - 1))) {
      // NOTE: We assume the JNP URL does not have a trailing slash.
      String port = namingUrl.substring(colonIndex + 1);
      if (!port.equals(CHANGE_ME)) {
        namingPort = port;
      }
    }
    return namingPort;
  }

  // Utils ---------------------------------------------------------------

  private void initLogEventSourcesConfigProp(File configDir, Configuration pluginConfig) {
    File logDir = new File(configDir, "log");
    File serverLogFile = new File(logDir, "server.log");
    if (serverLogFile.exists() && !serverLogFile.isDirectory()) {
      PropertyMap serverLogEventSource = new PropertyMap("serverLog");
      serverLogEventSource.put(new PropertySimple(LogFileEventResourceComponentHelper.LogEventSourcePropertyNames.LOG_FILE_PATH, serverLogFile));
      serverLogEventSource.put(new PropertySimple(LogFileEventResourceComponentHelper.LogEventSourcePropertyNames.ENABLED, Boolean.FALSE));
      PropertyList logEventSources = pluginConfig.getList(LogFileEventResourceComponentHelper.LOG_EVENT_SOURCES_CONFIG_PROP);

      // TODO : Not sure why I get this null
      if (logEventSources != null) {
        logEventSources.add(serverLogEventSource);
      }
    }
  }

  private static File resolvePathRelativeToHomeDir(Configuration pluginConfig, String path) {
    File configDir = new File(path);
    if (!configDir.isAbsolute()) {
      String homeDir = pluginConfig.getSimple(ApplicationServerPluginConfigurationProperties.HOME_DIR).getStringValue();
      configDir = new File(homeDir, path);
    }
    return configDir;
  }
}
