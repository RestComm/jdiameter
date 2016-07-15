/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpRepresentation;
import org.jdiameter.api.validation.Dictionary;

/**
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class Utils {

  public static void printMessage(Logger log, Dictionary avpDictionary, Message message, boolean sending) {
    log.info((sending ? "Sending " : "Received ") + (message.isRequest() ? "Request: " : "Answer: ") + message.getCommandCode() + " [E2E:" +
        message.getEndToEndIdentifier() + " -- HBH:" + message.getHopByHopIdentifier() + " -- AppID:" + message.getApplicationId() + "]");
    log.info("Request AVPs:");
    try {
      printAvps(log, avpDictionary, message.getAvps());
    }
    catch (AvpDataException e) {
      e.printStackTrace();
    }
    log.info("\n");
  }

  public static void printAvps(Logger log, Dictionary avpDictionary, AvpSet avpSet) throws AvpDataException {
    printAvpsAux(log, avpDictionary, avpSet, 0);
  }

  /**
   * Prints the AVPs present in an AvpSet with a specified 'tab' level
   *
   * @param avpSet
   *          the AvpSet containing the AVPs to be printed
   * @param level
   *          an int representing the number of 'tabs' to make a pretty print
   * @throws AvpDataException
   */
  private static void printAvpsAux(Logger log, Dictionary avpDictionary, AvpSet avpSet, int level) throws AvpDataException {
    String prefix = "                      ".substring(0, level * 2);

    for (Avp avp : avpSet) {
      AvpRepresentation avpRep = avpDictionary.getAvp(avp.getCode(), avp.getVendorId());

      if (avpRep != null && avpRep.getType().equals("Grouped")) {
        log.info(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\">");
        printAvpsAux(log, avpDictionary, avp.getGrouped(), level + 1);
        log.info(prefix + "</avp>");
      }
      else if (avpRep != null) {
        String value = "";

        if (avpRep.getType().equals("Integer32")) {
          value = String.valueOf(avp.getInteger32());
        }
        else if (avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64")) {
          value = String.valueOf(avp.getInteger64());
        }
        else if (avpRep.getType().equals("Unsigned32")) {
          value = String.valueOf(avp.getUnsigned32());
        }
        else if (avpRep.getType().equals("Float32")) {
          value = String.valueOf(avp.getFloat32());
        }
        else {
          value = avp.getUTF8String();
        }

        log.info(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() +
            "\" vendor=\"" + avp.getVendorId() + "\" value=\"" + value + "\" />");
      }
    }
  }

}
