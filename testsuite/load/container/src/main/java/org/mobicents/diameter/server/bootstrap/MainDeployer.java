/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.diameter.server.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jboss.kernel.Kernel;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;


/**
 * Simplified deployement framework designed for hot deployement of endpoints and media components.
 *
 * Deployement is represented by tree of folders. Each folder may contains one or more deployement descriptors. The most
 * top deployment directory is referenced as root. Maindeployer creates recursively HDScanner for root and each nested
 * directoty. The HDScanner corresponding to the root directory is triggered periodicaly by local timer and in it order
 * starts nested scanners recursively.
 *
 * @author kulikov
 * @author amit bhayani
 */
public class MainDeployer implements ContainerOperations{

  /** JBoss microconatiner kernel */
  private Kernel kernel;
  /** Basic deployer */
  private BasicXMLDeployer kernelDeployer;
  /** Interval for scanning root deployment directory */
  private int scanPeriod;
  /** Filter for selecting descriptors */
  private FileFilter fileFilter;
  /** Root deployment directory as string */
  private Set<String> path;
  /** Root deployment directory as file object */
  private File[] root;
  /** Scanner trigger */
  private ScheduledExecutorService executor = null;
  /** Trigger's controller */
  private ScheduledFuture activeScan;


  /** Logger instance */
  private Logger logger = Logger.getLogger(MainDeployer.class);

  /**
   * Creates new instance of deployer.
   */
  public MainDeployer() {
    executor = Executors.newSingleThreadScheduledExecutor(new ScannerThreadFactory());
  }

  /**
   * Gets the current value of the period used for scanning deployement directory.
   *
   * @return the value of the period in milliseconds.
   */
  public int getScanPeriod() {
    return scanPeriod;
  }

  /**
   * Modifies value of the period used to scan deployement directory.
   *
   * @param scanPeriod
   *            the value of the period in milliseconds.
   */
  public void setScanPeriod(int scanPeriod) {
    this.scanPeriod = scanPeriod;
  }

  /**
   * Gets the path to the to the root deployment directory.
   *
   * @return path to deployment directory.
   */
  public Set<String> getPath() {
    return path;
  }

  /**
   * Modify the path to the root deployment directory
   *
   * @param path
   */
  public void setPath(Set<String> path) {
    this.path = path;
    root = new File[path.size()];
    int count = 0;
    for (String s : path) {
      root[count++] = new File(s);
    }
  }

  /**
   * Gets the filter used by Deployer to select files for deployement.
   *
   * @return the file filter object.
   */
  public FileFilter getFileFilter() {
    return fileFilter;
  }

  /**
   * Assigns file filter used for selection files for deploy.
   *
   * @param fileFilter
   *            the file filter object.
   */
  public void setFileFilter(FileFilter fileFilter) {
    this.fileFilter = fileFilter;
  }

  /**
   * Starts main deployer.
   *
   * @param kernel
   *            the jboss microntainer kernel instance.
   * @param kernelDeployer
   *            the jboss basic deployer.
   */
  public void start(Kernel kernel, BasicXMLDeployer kernelDeployer) {

    this.kernel = kernel;
    this.kernelDeployer = kernelDeployer;

    // If scanPeriod is set to -ve, reset to 0
    if (scanPeriod < 0) {
      this.scanPeriod = 0;
    }

    // If scanPeriod is set to less than 1 sec but greater than 0 millisec, re-set to 1 sec
    if (scanPeriod < 1000 && scanPeriod > 0) {
      this.scanPeriod = 1000;
    }

    for (File f : root) {
      // create deployement scanner and do first deployement
      HDScanner scanner = new HDScanner(f);
      scanner.run();

      // hot-deployment disabled for scan period set to 0
      if (!(scanPeriod == 0)) {
        activeScan = executor.scheduleAtFixedRate(scanner, scanPeriod, scanPeriod, TimeUnit.MILLISECONDS);
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug("scanPeriod is set to 0. Hot deployment disabled");
        }
      }
    }
    logger.info("[[[[[[[[[  Started " + "]]]]]]]]]");
  }

  /**
   * Shuts down deployer.
   */
  public void stop() {
    if (activeScan != null) {
      activeScan.cancel(true);
    }
    logger.info("Stopped");
  }

  /**
   * Deploys components using specified deployement descriptor.
   *
   * @param file
   *            the reference to the deployment descriptor
   * @throws java.lang.Throwable
   */
  private void deploy(File file) {
    logger.info("Deploying " + file);

    String filePath = file.getPath();
    if (filePath.endsWith(".xml")) {
      try {
        kernelDeployer.deploy(file.toURI().toURL());
        kernelDeployer.validate();
        logger.info("Deployed " + file);
      } catch (Throwable t) {
        logger.info("Could not deploy " + file, t);
      }
    }
  }

  /**
   * Undeploys components specified in deployment descriptor.
   *
   * @param file
   *            the reference to the deployment descriptor.
   */
  private void undeploy(File file) {
    logger.info("Undeploying " + file);
    String filePath = file.getPath();
    if (filePath.endsWith(".xml")) {
      try {
        kernelDeployer.undeploy(file.toURI().toURL());
      } catch (MalformedURLException e) {
      }
      logger.info("Undeployed " + file);
    }
  }

  /**
   * Redeploys components specified in deployment descriptor.
   *
   * This method subsequently performs undeployment and deployment procedures.
   *
   * @param file
   *            the reference to the deployment descriptor.
   */
  private void redeploy(File file) {
    undeploy(file);
    deploy(file);
  }

  /**
   * Deployment scanner.
   *
   * The scanner relates to a directory in the deployement structure. It is responsible for processing all descriptors
   * in this directotry and for triggering nested scanners.
   */
  private class HDScanner implements Runnable {

    /** directory to which scanner relates */
    private File dir;
    /** nested scanners */
    private HashMap<File, HDScanner> scanners = new HashMap();
    /** map between descriptor and last deployed time */
    private HashMap<File, Deployment> deployments = new HashMap();

    /**
     * Creates new instance of the scanner.
     *
     * @param dir
     *            the directory releated to this scanner.
     */
    public HDScanner(File dir) {
      // we do not any check for file (is it directory) because we know that
      // always directory
      this.dir = dir;
    }

    /**
     * This methods is called by local timer.
     *
     * It shoudl find changes in the deployement structure and execute corresponding method:
     *
     * -deploy for new descriptors; -undeploy for removed descriptors; -redeploy for updated descriptors; -create
     * nested scanner for new nested directories; -remove nested scanner for deleted nested directories; -run nested
     * scanners recursively.
     */
    public void run() {
      // get the fresh list of nested files
      File[] files = dir.listFiles();

      // select list of new files and process this list
      Collection<File> list = getNew(files);
      if (!list.isEmpty()) {
        for (File file : list) {
          // again, for directories we are creating nested scanners
          // and deploying desciptors
          if (file.isDirectory()) {
            HDScanner childScanner = new HDScanner(file);
            scanners.put(file, childScanner);
            // keep reference to nested directory because we need to track
            // the case when directory will be deleted
          } else if (file.isFile() && fileFilter.accept(file)) {
            deploy(file);
          }
          deployments.put(file, new Deployment(file));
        }
      }

      // now same for removed.
      // determine list of removed files
      list = getRemoved(files);
      for (File file : list) {
        Deployment d = deployments.remove(file);
        if (d == null) {
          continue;
        }
        if (d.isDirectory()) {
          HDScanner scanner = scanners.remove(file);
          if (scanner != null) {
            scanner.undeployAll();
          }
        } else {
          undeploy(file);
        }
      }

      // redeploying
      list = getUpdates(files);
      for (File file : list) {
        // ignore directories
        if (file.isFile() && fileFilter.accept(file)) {
          redeploy(file);
        }
      }

      // Processing nested deployments
      Collection<HDScanner> nested = scanners.values();
      for (HDScanner scanner : nested) {
        scanner.run();
      }
    }

    private void undeployAll() {
      // undeploy descriptors
      Set<File> list = deployments.keySet();
      for (File file : list) {
        if (!deployments.get(file).isDirectory()) {
          undeploy(file);
        }
      }
      deployments.clear();

      // recursively undeploy nested directories
      Collection<HDScanner> nested = scanners.values();
      for (HDScanner scanner : nested) {
        scanner.undeployAll();
      }
    }

    /**
     * Collects added descriptors from the specified list.
     *
     * @param files
     *            the list of descriptors.
     * @return the list of new new descriptors.
     */
    private Collection<File> getNew(File[] files) {
      ArrayList<File> list = new ArrayList();
      for (File f : files) {
        if (!deployments.containsKey(f)) {
          list.add(f);
        }
      }

      return list;
    }

    /**
     * Collects descriptors that were deleted.
     *
     * @param files
     *            the list of currently available descriptors
     * @return the list of deleted descriptors.
     */
    private Collection<File> getRemoved(File[] files) {
      List<File> removed = new ArrayList();
      Set<File> list = deployments.keySet();

      for (File descriptor : list) {
        boolean found = false;
        for (File file : files) {
          if (descriptor.equals(file)) {
            found = true;
            break;
          }
        }

        if (!found) {
          removed.add(descriptor);
        }
      }

      return removed;
    }

    /**
     * Collects descriptors that were updates.
     *
     * @param files
     *            the fresh list of descriptors.
     * @return the list of updated descriptors.
     */
    private Collection<File> getUpdates(File[] files) {
      ArrayList<File> list = new ArrayList();
      for (File f : files) {
        if (deployments.containsKey(f)) {
          long lastModified = deployments.get(f).lastModified();
          if (lastModified < f.lastModified()) {
            deployments.get(f).update(f.lastModified());
            list.add(f);
          }
        }
      }
      return list;
    }
  }

  /**
   * Scanner thread factory.
   */
  private class ScannerThreadFactory implements ThreadFactory {

    /**
     * Creates new thread.
     *
     * @param r
     *            main deployer.
     * @return thread object.
     */
    public Thread newThread(Runnable r) {
      return new Thread(r, "MainDeployer");
    }
  }


  //hook jmx method to terminate container
  public void terminateContainer()
  {
    //this will trigger shoot down hook, which nicely kills container.
    System.exit(0);
  }
}
