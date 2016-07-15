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
 */

package org.jdiameter.common.impl.app.s13;

import java.io.Serializable;

import org.jdiameter.api.Request;
import org.jdiameter.common.api.app.AppSessionDataLocalImpl;
import org.jdiameter.common.api.app.s13.IS13SessionData;
import org.jdiameter.common.api.app.s13.S13SessionState;

public class S13LocalSessionDataImpl extends AppSessionDataLocalImpl implements IS13SessionData {

  protected S13SessionState state = S13SessionState.IDLE;
  protected Request buffer;
  protected Serializable tsTimerId;

  @Override
  public void setS13SessionState(S13SessionState state) {
    this.state = state;
  }

  @Override
  public S13SessionState getS13SessionState() {
    return this.state;
  }

  @Override
  public Serializable getTsTimerId() {
    return this.tsTimerId;
  }

  @Override
  public void setTsTimerId(Serializable tid) {
    this.tsTimerId = tid;
  }

  @Override
  public void setBuffer(Request buffer) {
    this.buffer = buffer;
  }

  @Override
  public Request getBuffer() {
    return this.buffer;
  }
}
