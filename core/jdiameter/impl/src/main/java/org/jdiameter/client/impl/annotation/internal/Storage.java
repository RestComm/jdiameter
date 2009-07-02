package org.jdiameter.client.impl.annotation.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

  private Map<Class<?>, ClassInfo> annotations = new ConcurrentHashMap<Class<?>, ClassInfo>();

  public synchronized final ClassInfo getClassInfo(Class<?> _class){
    ClassInfo info = annotations.get(_class);
    if (info == null) {
      info = new ClassInfo(this, _class);
      annotations.put(_class, info);
    }
    return info;
  }

  public synchronized  final void clear() {
    annotations.clear();
  }

}