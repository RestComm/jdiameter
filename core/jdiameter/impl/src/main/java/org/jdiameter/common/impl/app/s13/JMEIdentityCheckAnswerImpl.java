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

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Request;
import org.jdiameter.api.s13.events.JMEIdentityCheckAnswer;
import org.jdiameter.common.impl.app.AppAnswerEventImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMEIdentityCheckAnswerImpl extends AppAnswerEventImpl implements JMEIdentityCheckAnswer {
  private static final long serialVersionUID = 1L;

  protected static final Logger logger = LoggerFactory.getLogger(JMEIdentityCheckAnswerImpl.class);

  public JMEIdentityCheckAnswerImpl(Answer answer) {
    super(answer);
  }

  public JMEIdentityCheckAnswerImpl(Request request, long resultCode) {
    super(request.createAnswer(resultCode));
  }

  @Override
  public boolean isEquipmentStatusAVPPresent() {
    return super.message.getAvps().getAvp(Avp.EQUIPMENT_STATUS) != null;
  }

  @Override
  public int getEquipmentStatus() {

    Avp equipmentStatusAvp = super.message.getAvps().getAvp(Avp.EQUIPMENT_STATUS);
    if (equipmentStatusAvp != null) {
      try {
        return equipmentStatusAvp.getInteger32();
      } catch (AvpDataException e) {
        logger.debug("Failure trying to obtain Equipment-Status AVP value", e);
      }
    }
    return -1;
  }
}
