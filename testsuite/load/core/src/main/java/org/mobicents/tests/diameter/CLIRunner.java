package org.mobicents.tests.diameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * Class which sets up everything for test and waits till test is not
 * terminated.
 *
 * @author baranowb
 *
 */
public class CLIRunner {
  private static final LongOpt[] _LONG_OPTS = new LongOpt[18];
  private static final String _GETOPT_PARAMS_STRING = "h:q:w";
  private static final Logger log = Logger.getLogger(CLIRunner.class);
  private static final String _DIA_HOME_DIR = "dia.home.dir";

  static {

    // This should expand, to get all stack props. for nwo its hardcoded.
    _LONG_OPTS[0] = new LongOpt("usage", LongOpt.NO_ARGUMENT, null, 'h');
    _LONG_OPTS[1] = new LongOpt("testclass", LongOpt.REQUIRED_ARGUMENT, null, 'q');
    _LONG_OPTS[2] = new LongOpt("config", LongOpt.REQUIRED_ARGUMENT, null, 'w');

    // check for dia.home.dir
    if (System.getenv(_DIA_HOME_DIR) == null && System.getProperty(_DIA_HOME_DIR) == null) {
      // this is for cli mode, if we are here, it means we are not run by
      // micocontainer
      configLog4j();
    }
  }

  private boolean configured = false;
  private InputStream configurationFile;
  private AbstractStackRunner runnerClassInstance;

  /**
   * @param args
   */
  public static void main(String[] args) {
    CLIRunner runner = new CLIRunner();

    runner.parseArgs(args);
    if (runner.isConfigured()) {
      runner.performTask();
    }
  }

  private void parseArgs(String[] args) {

    Getopt getOpt = new Getopt("CLIRunner", args, _GETOPT_PARAMS_STRING, _LONG_OPTS);
    getOpt.setOpterr(true);
    int c = -1;
    String v = null;

    while ((c = getOpt.getopt()) != -1) {

      switch (c) {
        case 'h':
          usage();
          System.exit(0);
        case 'q':
          v = getOpt.getOptarg();
          setTestClass(v);
          break;

        case 'w':
          v = getOpt.getOptarg();
          setConfigurationFile(v);
          break;
        default:
          log.error("Wrong parameter!! ---> " + Character.toString((char) c));

      }
    }

  }

  //

  private void usage() {
    StringBuffer sb = new StringBuffer();

    sb.append("java " + CLIRunner.class.getName() + " [OPTIONS] \n");
    sb.append("Where options can be:\n");
    sb.append("--usage           : prints this message.\n");
    sb.append("--testclass       : FQDN of test class to run.\n");
    sb.append("--config          : absolute url to jdiameter config file.\n");

    log.info("Usage: \n" + sb);

  }

  private void performTask() {
    if (this.runnerClassInstance != null) {
      try {
        this.runnerClassInstance.configure(this.configurationFile);
        this.runnerClassInstance.performTestRun();
      }
      catch (Exception e) {
        log.error("Failed to run due to exception.", e);
      }
    }
  }

  private boolean isConfigured() {

    return this.configured;
  }

  private static void configLog4j() {
    InputStream inStreamLog4j = CLIRunner.class.getClassLoader().getResourceAsStream("log4j.properties");
    Properties propertiesLog4j = new Properties();
    try {
      propertiesLog4j.load(inStreamLog4j);
      PropertyConfigurator.configure(propertiesLog4j);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    log.debug("log4j configured");

  }

  // Bean methods and fields for MC runn

  public void setConfigurationFile(String v) {
    try {
      File f = new File(v);

      if (!f.exists() || !f.canRead() || !f.isFile()) {
        if (Thread.currentThread().getContextClassLoader().getResource(v) != null) {
          // this is double check to test runner classes, but we require it to
          // be sure if deployment in mc is ok
          this.configurationFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(v);
        }
        else {
          this.configured = false;
          log.error("File \"" + v + "\" does not exists, is not readable or is not a file.");
          this.configurationFile = null;
        }
      }
      else {
        this.configurationFile = new FileInputStream(f);
      }
      if (this.runnerClassInstance != null) {
        configured = true;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      log.error("Failed to init test class: " + v);
      this.configured = false;
    }
  }

  public String getConfigurationFile() {
    if (this.configurationFile != null) {
      return this.configurationFile.toString();
    }
    else {
      return null;
    }
  }

  public void setTestClass(String v) {
    try {
      Class clazz = Class.forName(v);
      this.runnerClassInstance = (AbstractStackRunner) clazz.newInstance();
      if (this.configurationFile != null) {
        configured = true;
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      log.error("Failed to init test class: " + v);
      this.configured = false;
    }
  }

  public String getTestClass() {
    if (this.runnerClassInstance != null) {
      return this.runnerClassInstance.toString();
    }
    else {
      return null;
    }

  }

  public void start() throws Exception {
    if (isConfigured()) {
      this.runnerClassInstance.configure(this.configurationFile);
      // now here we should be able to receive all data.
    }
    else {
      throw new IllegalStateException("Runner is not configured");
    }
  }

  public void stop() {
    if (isConfigured()) {
      this.runnerClassInstance.clean();
    }
  }

}
