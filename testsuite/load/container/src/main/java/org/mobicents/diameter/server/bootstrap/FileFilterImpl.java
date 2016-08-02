package org.mobicents.diameter.server.bootstrap;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author amit bhayani
 *
 */
public class FileFilterImpl implements FileFilter {

  /** The file suffixes */
  private static Set<String> fileSuffixes = new CopyOnWriteArraySet<String>();

  static {
    fileSuffixes.add("-beans.xml");
    fileSuffixes.add("-conf.xml");
  }

  public FileFilterImpl() {
  }

  public FileFilterImpl(Set<String> suffixes) {
    if (suffixes == null)
      throw new IllegalArgumentException("Null suffixes");
    fileSuffixes.clear();
    fileSuffixes.addAll(suffixes);
  }

  public boolean accept(File pathname) {
    for (String suffix : fileSuffixes) {
      String filePathName = pathname.getName();
      if (filePathName.endsWith(suffix))
        return true;
    }
    return false;
  }

  public Set<String> getSuffixes() {
    return fileSuffixes;
  }

  public static boolean addFileSuffix(String suffix) {
    if (suffix == null)
      throw new IllegalArgumentException("Null suffix");
    return fileSuffixes.add(suffix);
  }

  public static boolean removeFileSuffix(String suffix) {
    if (suffix == null)
      throw new IllegalArgumentException("Null suffix");
    return fileSuffixes.remove(suffix);
  }
}
