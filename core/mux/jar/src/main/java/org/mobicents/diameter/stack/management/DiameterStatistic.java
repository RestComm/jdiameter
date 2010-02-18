package org.mobicents.diameter.stack.management;

import java.io.Serializable;

public class DiameterStatistic implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private String value;

  public DiameterStatistic(String name, String description, String value) {
    super();
    this.name = name;
    this.description = description;
    this.value = value;
  }

  public String getName() {
    return name;
  }
  
  public String getDescription() {
    return description;
  }
  
  public Object getValue() {
    return this.value.indexOf('.') != -1 ? Double.valueOf(value) : Long.valueOf(value);
  }
  
  public String getValueAsString() {
    return value;
  }
  
  @Override
  public String toString() {
    return name + "=" + value;
  }
}
