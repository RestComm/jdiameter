package org.jdiameter.server.impl.helpers;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Message;
import org.jdiameter.api.Selector;
import org.jdiameter.client.api.IMessage;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
public class ApplicationIdSelector implements Selector<Message, ApplicationId> {

  private ApplicationId applicationId;

  public ApplicationIdSelector(ApplicationId applicationId) {
    if (applicationId == null){
      throw new IllegalArgumentException("Please set application id");
    }
    
    this.applicationId = applicationId;
  }

  public boolean checkRule(Message message) {
    return message != null && ((IMessage) message).getSingleApplicationId().equals(applicationId);
  }

  public ApplicationId getMetaData() {
    return applicationId;
  }
}
