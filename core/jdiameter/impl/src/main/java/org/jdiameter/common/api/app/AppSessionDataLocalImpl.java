/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.common.api.app;

import org.jdiameter.api.ApplicationId;

/**
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public abstract class AppSessionDataLocalImpl implements IAppSessionData {

  private String sessionId;
  private ApplicationId applicationId;

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public ApplicationId getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(ApplicationId applicationId) {
    this.applicationId = applicationId;
  }

  public boolean remove() {
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((applicationId == null) ? 0 : applicationId.hashCode());
    result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AppSessionDataLocalImpl other = (AppSessionDataLocalImpl) obj;
    if (applicationId == null) {
      if (other.applicationId != null)
        return false;
    } else if (!applicationId.equals(other.applicationId))
      return false;
    if (sessionId == null) {
      if (other.sessionId != null)
        return false;
    }
    else if (!sessionId.equals(other.sessionId))
      return false;

    return true;
  }

}
