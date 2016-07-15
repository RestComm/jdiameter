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

package org.jdiameter.client.impl.app.rf;

import org.jdiameter.api.Avp;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.app.AppEvent;
import org.jdiameter.api.app.StateEvent;
import org.jdiameter.api.rf.events.RfAccountingAnswer;
import org.jdiameter.api.rf.events.RfAccountingRequest;

/**
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
class Event implements StateEvent {

  enum Type {
    SEND_EVENT_RECORD,
    SEND_START_RECORD,
    SEND_INTERIM_RECORD,
    SEND_STOP_RECORD,
    FAILED_SEND_RECORD,
    RECEIVED_RECORD,
    FAILED_RECEIVE_RECORD
  }

  Type type;
  AppEvent data;

  Event(Type type) {
    this.type = type;
  }

  Event(RfAccountingAnswer accountAnswer) throws Exception {
    int resCode = ResultCode.SUCCESS;
    try {
      resCode = accountAnswer.getMessage().getAvps().getAvp(Avp.RESULT_CODE).getInteger32();
    }
    catch (Exception exc) {
    }
    type =  (resCode == ResultCode.SUCCESS || (resCode / 1000 == 4)) ? Type.RECEIVED_RECORD : Type.FAILED_RECEIVE_RECORD;
    data = accountAnswer;
  }

  Event(RfAccountingRequest accountRequest) throws Exception {
    data = accountRequest;
    int type = accountRequest.getAccountingRecordType(); //legal, its int size...
    switch (type) {
      case 1:
        this.type = Type.SEND_EVENT_RECORD;
        break;
      case 2:
        this.type = Type.SEND_START_RECORD;
        break;
      case 3:
        this.type = Type.SEND_INTERIM_RECORD;
        break;
      case 4:
        this.type = Type.SEND_STOP_RECORD;
        break;
      default:
        throw new Exception("Unknown type " + type);
    }
  }

  Event(Type type, RfAccountingRequest accountRequest) throws Exception {
    this.type = type;
    this.data = accountRequest;
  }

  @Override
  public <E> E encodeType(Class<E> eClass) {
    return eClass == Type.class ? (E) type : null;
  }

  @Override
  public Enum getType() {
    return type;
  }

  @Override
  public void setData(Object o) {
    data = (AppEvent) o;
  }

  @Override
  public Object getData() {
    return data;
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}