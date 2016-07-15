 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.api;

import static java.security.AccessController.doPrivileged;

import java.io.PrintWriter;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <P>The basic service for managing a set of Diameter stacks.<br>
 *
 * <P>As part of its initialization, the <code>DriverManager</code> class will
 * attempt to load the stacks classes referenced in the "diameter.drivers"
 * system property. This allows a user to customize the Diameter Drivers
 * used by their applications. For example in your
 * ~/.hotjava/properties file you might specify:
 * <pre>
 * <CODE>diameter.drivers=foo.bah.Stack:wombat.diameter.Stack</CODE>
 * </pre>
 *
 * A program can also explicitly load Diameter stacks at any time. For
 * example, the my.diameter.Stack is loaded with the following statement:
 * <pre>
 * <CODE>Class.forName("my.diameter.Stack");</CODE>
 * </pre>
 *
 * <P>When the method <code>getSession</code> is called,
 * the <code>StackManager</code> will attempt to
 * locate a suitable stack from amongst those loaded at
 * initialization and those loaded explicitly using the same classloader
 * as the current applet or application.
 *
 * @author erick.svenson@yahoo.com
 * @version 1.5.1 Final
 */
public final class StackManager {

  private static final Object logSync = new Object();

  private static List<StackInfo> stacks = new CopyOnWriteArrayList<StackInfo>();
  private static PrintWriter logWriter = null;
  private static boolean initialized = false;

  static void initialize() {
    if (initialized) {
      return;
    }
    initialized = true;
    loadInitialStacks();
    println("Diameter StackManager initialized");
  }

  private StackManager() {
  }

  /**
   * Retrieves the log writer.
   *
   * The <code>getLogWriter</code> and <code>setLogWriter</code>
   * methods should be used instead
   * of the <code>get/setlogStream</code> methods, which are deprecated.
   * @return a <code>java.io.PrintWriter</code> object
   * @see #setLogWriter
   */
  public static PrintWriter getLogWriter() {
    synchronized (logSync) {
      return logWriter;
    }
  }
  /**
   * Sets the logging/tracing <code>PrintWriter</code> object
   * that is used by the <code>StackManager</code> and all drivers.
   * <P>
   * There is a minor versioning problem created by the introduction
   * of the method <code>setLogWriter</code>.  The
   * method <code>setLogWriter</code> cannot create a <code>PrintStream</code> object
   * that will be returned by <code>getLogStream</code>
   *
   * @param out the new logging/tracing <code>PrintStream</code> object;
   *      <code>null</code> to disable logging and tracing
   * @throws SecurityException
   *    if a security manager exists and its
   *    <code>checkPermission</code> method denies
   *    setting the log writer
   */
  public static void setLogWriter(java.io.PrintWriter out) {
    synchronized (logSync) {
      logWriter = out;
    }
  }

  /**
   * Attempts to locate a stack.
   * The <code>StackManager</code> attempts to select an appropriate stack from
   * the set of registered Diameter stacks.
   * @param className class name of stack
   * @return stack instance
   * @exception InternalException if a manager has internal error
   */
  public static synchronized Stack getStack(String className) throws InternalException {
    println(new StringBuilder().append("StackManager.getStack(\"").append(className).append("\")").toString());
    if (!initialized) {
      initialize();
    }
    // Gets the classloader of the code that called this method, may be null.
    ClassLoader callerCL = ClassLoader.getSystemClassLoader();
    // Walk through the loaded stacks attempting to locate someone who understands the given URL.
    for (StackInfo di : stacks) {
      // If the caller does not have permission to load the stack then skip it.
      if (getCallerClass(callerCL, di.stackClassName) != di.stackClass) {
        println(new StringBuilder().append("    skipping: ").append(di).toString());
        continue;
      }
      println(new StringBuilder().append("    trying ").append(di).toString());
      if (di.stackClassName.equals(className)) {
        // Success!
        println("geStack returning " + di);
        return (di.stack);
      }
    }

    println("getStack: no suitable stack");
    throw new InternalException("No suitable stack");
  }

  /**
   * Registers the given stack with the <code>ScoketManager</code>.
   * A newly-loaded stack class should call
   * the method <code>registerStack</code> to make itself
   * known to the <code>StackManager</code>.
   *
   * @param stack the new Diameter Stack that is to be registered with the
   *               <code>StackManager</code>
   * @exception InternalException if a manager has internal error
   */
  public static synchronized void registerStack(Stack stack) throws InternalException {
    if (!initialized) {
      initialize();
    }
    StackInfo stackInfo = new StackInfo();
    stackInfo.stack = stack;
    stackInfo.stackClass = stack.getClass();
    stackInfo.stackClassName = stackInfo.stackClass.getName();
    stacks.add(stackInfo);
    println(new StringBuilder().append("registerStack: ").append(stackInfo).toString());
  }

  /**
   * Drops a driver from the <code>DiameterManager</code>'s list.  Applets can only
   * deregister stacks from their own classloaders.
   *
   * @param stack the Diameter stack to drop
   * @exception InternalException if a manager has internal error
   */
  public static synchronized void deregisterStack(Stack stack) throws InternalException {
    // Gets the classloader of the code that called this method, may be null.
    ClassLoader callerCL = ClassLoader.getSystemClassLoader();
    println(new StringBuilder().append("StackManager.deregisterStack: ").append(stack).toString());
    // Walk through the loaded stacks.
    int i;
    StackInfo stackInfo = null;
    for (i = 0; i < stacks.size(); i++) {
      stackInfo = stacks.get(i);
      if (stackInfo.stack == stack) {
        break;
      }
    }
    // If we can't find the stack just return.
    if (i >= stacks.size()) {
      println("    couldn't find stack to unload");
      return;
    }
    // If the caller does not have permission to load the stack then throw a security exception.
    if (stackInfo == null || getCallerClass(callerCL, stackInfo.stackClassName) != stackInfo.stackClass) {
      throw new SecurityException();
    }
    // Remove the stack.  Other entries in stacks get shuffled down.
    stacks.remove(i);
  }

  /**
   * Retrieves an Enumeration with all of the currently loaded Diameter stacks
   * to which the current caller has access.
   *
   * <P><B>Note:</B> The classname of a stack can be found using
   * <CODE>d.getClass().getName()</CODE>
   *
   * @return the list of Diameter stacks loaded by the caller's class loader
   */
  public static synchronized Enumeration<Stack> getStacks() {
    List<Stack> result = new CopyOnWriteArrayList<Stack>();
    if (!initialized) {
      initialize();
    }
    // Gets the classloader of the code that called this method, may be null.
    ClassLoader callerCL = ClassLoader.getSystemClassLoader();
    // Walk through the loaded stacks.
    for (StackInfo di : stacks) {
      // If the caller does not have permission to load the stack then skip it.
      if (getCallerClass(callerCL, di.stackClassName) != di.stackClass) {
        println(new StringBuilder().append("    skipping: ").append(di).toString());
        continue;
      }
      result.add(di.stack);
    }

    return Collections.enumeration(result);
  }

  public static void println(String message) {
    synchronized (logSync) {
      if (logWriter != null) {
        logWriter.println(message);
        // automatic flushing is never enabled, so we must do it ourselves
        logWriter.flush();
      }
    }
  }

  private static Class getCallerClass(ClassLoader callerClassLoader, String stackClassName) {
    Class callerC;
    try {
      callerC = Class.forName(stackClassName, true, callerClassLoader);
    } catch (Exception ex) {
      callerC = null;
    }
    return callerC;
  }

  private static void loadInitialStacks() {
    String stacks;
    try {
      stacks = doPrivileged( new GetPropertyAction("diameter.stacks") );
    } catch (Exception ex) {
      stacks = null;
    }
    println(new StringBuilder().append("StackManager.initialize: diameter.stacks = ").append(stacks).toString());
    if (stacks == null) {
      return;
    }
    while (stacks.length() != 0) {
      int x = stacks.indexOf(':');
      String stack;
      if (x < 0) {
        stack = stacks;
        stacks = "";
      } else {
        stack = stacks.substring(0, x);
        stacks = stacks.substring(x + 1);
      }
      if (stack.length() == 0) {
        continue;
      }
      try {
        println(new StringBuilder().append("StackManager.Initialize: loading ").append(stack).toString());
        Class.forName(stack, true, ClassLoader.getSystemClassLoader());
      } catch (Exception ex) {
        println(new StringBuilder().append("StackManager.Initialize: load failed: ").append(ex).toString());
      }
    }
  }
}

class GetPropertyAction  implements PrivilegedAction<String> {

  private String theProp;
  private String defaultVal;
  GetPropertyAction(String s) {
    theProp = s;
  }

  GetPropertyAction(String s, String s1) {
    theProp = s;
    defaultVal = s1;
  }

  @Override
  public String run() {
    String s = System.getProperty(theProp);
    return s != null ? s : defaultVal;
  }
}

class StackInfo {
  Stack stack;
  Class stackClass;
  String stackClassName;

  @Override
  public String toString() {
    return (new StringBuilder().append("stack[className=").append(stackClassName).append(",").
        append(stack).append("]").toString());
  }
}


