package org.jdiameter.client.impl.annotation;

import org.jdiameter.api.annotation.Getter;
import org.jdiameter.api.annotation.Setter;

public abstract class Value<T> {

  protected T value;

  @Setter
  public Value(T value) {
    this.value = value;
  }

  @Getter
  public T get() {
    return value;
  }
}