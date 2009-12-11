package org.mobicents.tests.diameter;

import java.io.File;
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

	static {

		// This should expand, to get all stack props. for nwo its hardcoded.
		_LONG_OPTS[0] = new LongOpt("usage", LongOpt.NO_ARGUMENT, null, 'h');
		_LONG_OPTS[1] = new LongOpt("testclass", LongOpt.REQUIRED_ARGUMENT, null, 'q');
		_LONG_OPTS[2] = new LongOpt("config", LongOpt.REQUIRED_ARGUMENT, null, 'w');

		configLog4j();
	}

	private boolean configured = true;
	private File configurationFileURL;
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
				// local address
				v = getOpt.getOptarg();
				try {
					Class clazz = Class.forName(v);
					this.runnerClassInstance = (AbstractStackRunner) clazz.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Failed to init test class: " + v);
					this.configured = false;
				}
				break;

			case 'w':
				// local address
				v = getOpt.getOptarg();
				try {
					this.configurationFileURL = new File(v);
					if (!this.configurationFileURL.exists() || !this.configurationFileURL.canRead() || !this.configurationFileURL.isFile()) {
						this.configured = false;
						log.error("File \"" + v + "\" does not exists, is not readable or is not a file.");
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Failed to init test class: " + v);
					this.configured = false;
				}
				break;
			default:
				log.error("Wrong parameter!! ---> " + Character.toString((char) c));

			}
		}

	}

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
		if (this.runnerClassInstance != null)
			try {
				this.runnerClassInstance.configure(this.configurationFileURL);
				this.runnerClassInstance.performTestRun();
			} catch (Exception e) {
				log.error("Failed to run due to exception.", e);
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("log4j configured");

	}
}
