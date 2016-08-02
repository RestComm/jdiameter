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

import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;

/**
 * This class handles Diameter URIs.
 * It only implements parts that are needed for the Diameter URI
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author Yulian Oifa
 * @version 1.5.1  Final
 */
public final class URI implements Comparable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final String FIELD_PROTOCOL = "protocol=";
  private static final String FIELD_TRANSPORT = "transport=";
  private static final String SCHEME_SEPARATOR = "://";
  private static final String PARAMS_SEPARATOR = ";";

  private static final String DEFAULT_SCHEME = "aaa";
  private static final int DEFAULT_PORT = 3868;

  private String scheme;
  private String host;
  private int port = -1;
  private String path = "";

  /**
   * Constructor with string parameter
   * @param uri String representation of URI
   * @throws URISyntaxException which signals that URI has syntax error
   * @throws UnknownServiceException which signals that URI has incorrect scheme
   */
  public URI(String uri) throws URISyntaxException, UnknownServiceException {
    parse(uri);
    if (getFQDN() == null || getFQDN().trim().length() == 0) {
      throw new URISyntaxException(uri, "Host not found");
    }
    if (!getScheme().equals("aaa") && !getScheme().equals("aaas")) {
      throw new UnknownServiceException(new StringBuilder().append("Unsupported service: ").append(getScheme()).toString());
    }
  }

  /**
   * @return scheme for URI
   */
  public String getScheme() {
    return scheme;
  }

  /**
   * @return host name of URI
   */
  public String getFQDN() {
    return host;
  }

  /**
   * Returns the port number of this URI, or -1 if this is not set.
   * @return the port number of this URI
   */
  public int getPort() {
    return port == -1 ? DEFAULT_PORT : port;
  }

  /**
   * @return  true if this URI is secure
   */
  public boolean isSecure() {
    return getScheme().endsWith("s");
  }

  /**
   * @return path of this URI
   */
  public String getPath() {
    return path;
  }

  /**
   * @return protocol parameter of this URI
   */
  public String getProtocolParam() {
    String[] args = getPath().split(PARAMS_SEPARATOR);
    for (String arg : args) {
      if (arg.startsWith(FIELD_PROTOCOL)) {
        return arg.substring(FIELD_PROTOCOL.length());
      }
    }

    return null;
  }

  /**
   * @return transport parameter of this URI
   */
  public String getTransportParam() {
    String[] args = getPath().split(PARAMS_SEPARATOR);
    for (String arg : args) {
      if (arg.startsWith(FIELD_TRANSPORT)) {
        return arg.substring(FIELD_TRANSPORT.length());
      }
    }

    return null;
  }


  /**
   * @return String representation of this URI in RFC 3588 format
   */
  @Override
  public String toString() {
    StringBuffer rc = new StringBuffer(scheme).append(SCHEME_SEPARATOR).append(host);
    if (port != -1) {
      rc.append(":").append(port);
    }
    if (path != null && path.length() > 0) {
      rc.append(PARAMS_SEPARATOR).append(path);
    }

    return rc.toString();
  }

  private void parse(String uri) throws URISyntaxException {
    try {
      int schemeStartIndex = uri.indexOf(SCHEME_SEPARATOR);
      int schemeEndIndex = 0;
      if (schemeStartIndex == -1) {
        scheme = DEFAULT_SCHEME;
      }
      else {
        scheme = uri.substring(0, schemeStartIndex);
        schemeEndIndex = schemeStartIndex + 3;
        schemeStartIndex = uri.indexOf(';', schemeEndIndex);
      }

      if (schemeStartIndex == -1) {
        host = uri.substring(schemeEndIndex);
      }
      else {
        host = uri.substring(schemeEndIndex, schemeStartIndex);
      }
      int sepIndex = host.indexOf(':');
      if (sepIndex != -1) {
        port = Integer.parseInt(host.substring(sepIndex + 1));
        host = host.substring(0, sepIndex);
      }
      if (schemeStartIndex != -1) {
        path = uri.substring(schemeStartIndex + 1);
      }
    }
    catch (Exception e) {
      throw new URISyntaxException(uri, "URI has incorrect format");
    }
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * <p>
   * The <code>equals</code> method implements an equivalence relation
   * on non-null object references:
   * <ul>
   * <li>It is <i>reflexive</i>: for any non-null reference value
   *     <code>x</code>, <code>x.equals(x)</code> should return
   *     <code>true</code>.
   * <li>It is <i>symmetric</i>: for any non-null reference values
   *     <code>x</code> and <code>y</code>, <code>x.equals(y)</code>
   *     should return <code>true</code> if and only if
   *     <code>y.equals(x)</code> returns <code>true</code>.
   * <li>It is <i>transitive</i>: for any non-null reference values
   *     <code>x</code>, <code>y</code>, and <code>z</code>, if
   *     <code>x.equals(y)</code> returns <code>true</code> and
   *     <code>y.equals(z)</code> returns <code>true</code>, then
   *     <code>x.equals(z)</code> should return <code>true</code>.
   * <li>It is <i>consistent</i>: for any non-null reference values
   *     <code>x</code> and <code>y</code>, multiple invocations of
   *     <tt>x.equals(y)</tt> consistently return <code>true</code>
   *     or consistently return <code>false</code>, provided no
   *     information used in <code>equals</code> comparisons on the
   *     objects is modified.
   * <li>For any non-null reference value <code>x</code>,
   *     <code>x.equals(null)</code> should return <code>false</code>.
   * </ul>
   * <p>
   * The <tt>equals</tt> method for class <code>Object</code> implements
   * the most discriminating possible equivalence relation on objects;
   * that is, for any non-null reference values <code>x</code> and
   * <code>y</code>, this method returns <code>true</code> if and only
   * if <code>x</code> and <code>y</code> refer to the same object
   * (<code>x == y</code> has the value <code>true</code>).
   * <p>
   * Note that it is generally necessary to override the <tt>hashCode</tt>
   * method whenever this method is overridden, so as to maintain the
   * general contract for the <tt>hashCode</tt> method, which states
   * that equal objects must have equal hash codes.
   *
   * @param   obj   the reference object with which to compare.
   * @return  <code>true</code> if this object is the same as the obj
   *          argument; <code>false</code> otherwise.
   * @see     #hashCode()
   * @see     java.util.Hashtable
   */

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    URI that = (URI) obj;

    return getPort() == that.getPort() && !(host != null ?
        !host.equals(that.host) : that.host != null) &&
        !(path != null ? !path.equals(that.path) : that.path != null) &&
        !(scheme != null ? !scheme.equals(that.scheme) : that.scheme != null);

  }
  /**
   * Returns a hash code value for the object. This method is
   * supported for the benefit of hashtables such as those provided by
   * <code>java.util.Hashtable</code>.
   * <p>
   * The general contract of <code>hashCode</code> is:
   * <ul>
   * <li>Whenever it is invoked on the same object more than once during
   *     an execution of a Java application, the <tt>hashCode</tt> method
   *     must consistently return the same integer, provided no information
   *     used in <tt>equals</tt> comparisons on the object is modified.
   *     This integer need not remain consistent from one execution of an
   *     application to another execution of the same application.
   * <li>If two objects are equal according to the <tt>equals(Object)</tt>
   *     method, then calling the <code>hashCode</code> method on each of
   *     the two objects must produce the same integer result.
   * <li>It is <em>not</em> required that if two objects are unequal
   *     according to the {@link java.lang.Object#equals(java.lang.Object)}
   *     method, then calling the <tt>hashCode</tt> method on each of the
   *     two objects must produce distinct integer results.  However, the
   *     programmer should be aware that producing distinct integer results
   *     for unequal objects may improve the performance of hashtables.
   * </ul>
   * <p>
   * As much as is reasonably practical, the hashCode method defined by
   * class <tt>Object</tt> does return distinct integers for distinct
   * objects. (This is typically implemented by converting the internal
   * address of the object into an integer, but this implementation
   * technique is not required by the
   * Java<font size="-2"><sup>TM</sup></font> programming language.)
   *
   * @return  a hash code value for this object.
   * @see     java.lang.Object#equals(java.lang.Object)
   * @see     java.util.Hashtable
   */
  @Override
  public int hashCode() {
    int result;
    result = (scheme != null ? scheme.hashCode() : 0);
    result = 31 * result + (host != null ? host.hashCode() : 0);
    result = 31 * result + getPort();
    result = 31 * result + (path != null ? path.hashCode() : 0);
    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.<p>
   *
   * In the foregoing description, the notation
   * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
   * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
   * <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i>
   * is negative, zero or positive.
   *
   * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
   * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
   * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
   * <tt>y.compareTo(x)</tt> throws an exception.)<p>
   *
   * The implementor must also ensure that the relation is transitive:
   * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
   * <tt>x.compareTo(z)&gt;0</tt>.<p>
   *
   * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
   * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
   * all <tt>z</tt>.<p>
   *
   * It is strongly recommended, but <i>not</i> strictly required that
   * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
   * class that implements the <tt>Comparable</tt> interface and violates
   * this condition should clearly indicate this fact.  The recommended
   * language is "Note: this class has a natural ordering that is
   * inconsistent with equals."
   *
   * @param   obj the Object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *          is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this Object.
   */
  @Override
  public int compareTo(Object obj) {
    if (obj instanceof URI) {
      return this.toString().compareTo(obj.toString());
    }
    else {
      return -1;
    }
  }
}
