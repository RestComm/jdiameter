package org.jdiameter.client.impl.annotation.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassInfo {

  private Storage storage;
  private Class<?> _class;
  private Map<Class<?>, Annotation> annotations;
  private Map<Method, MethodInfo> methods;
  private Map<Constructor, ConstructorInfo> constructors;

  private Collection<Annotation> classCache;
  private Collection<MethodInfo> methodCache;
  private Collection<ConstructorInfo> constructorCache;

  public ClassInfo (Storage storage, Class<?> _class){
    this.storage = storage;
    this._class = _class;
  }

  public Class<?> getAttachedClass() {
    return _class;
  }

  public Collection<Annotation> getAnnotations() {
    if (classCache == null) {
      if (annotations == null) {
        annotations = new ConcurrentHashMap<Class<?>, Annotation>();
        final Class<?> parent = getAttachedClass().getSuperclass();
        if (parent != null) {
          addAnnotations(parent);
        }
        for (Class<?> i : getAttachedClass().getInterfaces()) {
          addAnnotations(i);
        }
        for (Annotation a : getAttachedClass().getDeclaredAnnotations()) {
          annotations.put(a.getClass().getInterfaces()[0], a);
        }
      }
      classCache = annotations.values();
    }
    return classCache;
  }

  public <T> T getAnnotation(Class<?> annotation) {
    for (Annotation a : getAnnotations()) {
      if (a.annotationType() == annotation) {
        return (T) a;
      }
    }

    return null;
  }

  private void addAnnotations(Class<?> _class) {
    for (Annotation annotation : storage.getClassInfo(_class).getAnnotations()) {
      if (annotation != null) {
        for (Class<?> _interface : annotation.getClass().getInterfaces()) {
          annotations.put(_interface, annotation);
        }
      }
    }
  }

  public MethodInfo getMethodInfo(String methodName, Class<?>... args) {
    try {
      return getMethodInfo( getAttachedClass().getMethod(methodName, args) );
    }
    catch (Exception e) {
      return null;
    }
  }

  public ConstructorInfo getConstructorInfo(Class<?>... args) {
    try {
      return getConstructorInfo( getAttachedClass().getConstructor(args) );
    }
    catch (Exception e1) {
      // may be generic
      try {
        return getConstructorInfo( getAttachedClass().getConstructor(Object.class) );
      }
      catch (Exception e2) {}
      return null;
    }
  }

  public MethodInfo getMethodInfo(Method method) {
    return getMethodMap().get(method);
  }

  public ConstructorInfo getConstructorInfo(Constructor constr) {
    return getConstructorMap().get(constr);
  }

  public Collection<MethodInfo> getMethodsInfo() {
    return methodCache == null ? (methodCache = getMethodMap().values()) : methodCache;
  }

  public Collection<ConstructorInfo> getConstructorsInfo() {
    return constructorCache == null ? (constructorCache = getConstructorMap().values()) : constructorCache;
  }

  private Map<Method, MethodInfo> getMethodMap(){
    if (methods == null) {
      methods = new ConcurrentHashMap<Method, MethodInfo>();
      for (Method method : getAttachedClass().getMethods()) {
        methods.put(method, new MethodInfo(storage, this, method));
      }
    }
    return  methods;
  }

  private Map<Constructor, ConstructorInfo> getConstructorMap(){
    if (constructors == null) {
      constructors = new ConcurrentHashMap<Constructor, ConstructorInfo>();
      for (Constructor constr : getAttachedClass().getConstructors()) {
        constructors.put(constr, new ConstructorInfo(storage, this, constr));
      }
    }
    return  constructors;
  }

}
