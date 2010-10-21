/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors 
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.client.impl;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;

/**
 * Small util class to separate AVP manipulation code from MessageParser class.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * 
 */
public class MessageUtility {

  private MessageUtility() {
  }

  /**
   * Used to set origin, previously done in MessageParser.
   * 
   * @param m
   * @param md
   */
  public static void addOriginAvps(Message m, MetaData md) {
    // FIXME: check for "userFqnAsUri" ?
    AvpSet set = m.getAvps();
    if (set.getAvp(Avp.ORIGIN_HOST) == null) {
      m.getAvps().addAvp(Avp.ORIGIN_HOST, md.getLocalPeer().getUri().getFQDN(), true, false, true);
    }
    if (set.getAvp(Avp.ORIGIN_REALM) == null) {
      m.getAvps().addAvp(Avp.ORIGIN_REALM, md.getLocalPeer().getRealmName(), true, false, true);
    }
  }

}
