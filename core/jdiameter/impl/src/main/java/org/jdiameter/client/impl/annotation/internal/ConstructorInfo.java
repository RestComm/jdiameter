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

package org.jdiameter.client.impl.annotation.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class ConstructorInfo {

  private Storage storage;
  private Constructor constr;
  private ClassInfo classInfo;
  private Collection<Annotation> annotationsCache;
  private Map<Class<?>, Annotation> annotationStorage;

  public ConstructorInfo(Storage storage, ClassInfo classInfo, Constructor constr) {
    this.storage = storage;
    this.classInfo = classInfo;
    this.constr = constr;
  }

  public Constructor getConstructor() {
    return constr;
  }

  public ClassInfo getClassInfo() {
    return classInfo;
  }

  public Collection<Annotation> getAnnotations() {
    return annotationsCache == null ? (annotationsCache = getAnnotationStorage().values()) : annotationsCache;
  }

  private Map<Class<?>, Annotation> getAnnotationStorage() {
    if (annotationStorage == null) {
      annotationStorage = new ConcurrentHashMap<Class<?>, Annotation>();
      Class<?> parent = getClassInfo().getAttachedClass().getSuperclass();
      if (parent != null) {
        addAnnotations(storage.getClassInfo(parent).getConstructorInfo(getConstructor().getParameterTypes()));
      }
      for (Class<?> i : getClassInfo().getAttachedClass().getInterfaces()) {
        addAnnotations(storage.getClassInfo(i).getConstructorInfo(getConstructor().getParameterTypes()));
      }
      for (Annotation a : getConstructor().getDeclaredAnnotations()) {
        annotationStorage.put(a.getClass().getInterfaces()[0], a);
      }
    }
    return annotationStorage;
  }

  private void addAnnotations(ConstructorInfo constr) {
    if (constr != null) {
      for (Annotation annotation : constr.getAnnotations()) {
        if (annotation != null) {
          for (Class<?> _interface : annotation.getClass().getInterfaces()) {
            annotationStorage.put( _interface, annotation ); // [0]
          }
        }
      }
    }
  }

  public <T> T getAnnotation(Class<?> annotation) {
    for (Annotation a : getAnnotations()) {
      if (a.annotationType() == annotation) {
        return (T) a;
      }
    }
    return null;
  }
}