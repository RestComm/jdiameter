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

package org.jdiameter.common.impl.app;

import static org.jdiameter.api.Avp.ORIGIN_HOST;
import static org.jdiameter.api.Avp.ORIGIN_REALM;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.app.AppEvent;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AppEventImpl implements AppEvent {

  private static final long serialVersionUID = 1L;
  protected Message message;

  public AppEventImpl(Message message) {
    this.message = message;
  }

  @Override
  public int getCommandCode() {
    return message.getCommandCode();
  }

  @Override
  public Message getMessage() throws InternalException {
    return message;
  }

  @Override
  public String getOriginHost() throws AvpDataException {
    Avp originHostAvp = message.getAvps().getAvp(ORIGIN_HOST);
    if (originHostAvp != null) {
      return originHostAvp.getDiameterIdentity();
    }
    else {
      throw new AvpDataException("Avp ORIGIN_HOST not found");
    }
  }

  @Override
  public String getOriginRealm() throws AvpDataException {
    Avp originRealmAvp = message.getAvps().getAvp(ORIGIN_REALM);
    if (originRealmAvp != null) {
      return originRealmAvp.getDiameterIdentity();
    }
    else {
      throw new AvpDataException("Avp ORIGIN_REALM not found");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AppEventImpl that = (AppEventImpl) o;

    return message.equals(that.message);
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }

  @Override
  public String toString() {
    return message != null ? message.toString() : "empty";
  }
}
