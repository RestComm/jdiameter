package org.mobicents.diameter.stack.management;

import java.io.Serializable;

public interface ConcurrentEntity extends Serializable {

  public static enum ConcurrentEntityNames {
    ThreadGroup, ProcessingMessageTimer, DuplicationMessageTimer,
    RedirectMessageTimer, PeerOverloadTimer, ConnectionTimer, StatisticTimer, ApplicationSession;
  }

  public String getName();

  public String getDescription();

  public Integer getSize();
}
