/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.common.impl.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.AvpRepresentation;
import org.jdiameter.api.validation.MessageRepresentation;
import org.jdiameter.api.validation.ValidatorLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents message/command in validation framework. It contains
 * basic info about command along with avp list - their multiplicity and
 * allowance.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 * 
 */
public class MessageRepresentationImpl implements MessageRepresentation {

  private static transient Logger logger = LoggerFactory.getLogger(MessageRepresentationImpl.class);

  private int commandCode = -1;
  private long applicationId = 0;

  private boolean isRequest = false;
  protected Map<AvpRepresentation, AvpRepresentation> unmuttableMessageAvps = new HashMap<AvpRepresentation, AvpRepresentation>();
  private String name = null;

  public MessageRepresentationImpl(int commandCode, long applicationId, boolean isRequest) {
    super();
    this.commandCode = commandCode;
    this.applicationId = applicationId;
    this.isRequest = isRequest;
  }

  public MessageRepresentationImpl(int commandCode, boolean isRequest) {
    this(commandCode, 0, isRequest);
  }

  public MessageRepresentationImpl(int commandCode, long applicationId, boolean isRequest, String name) {
    super();
    this.commandCode = commandCode;
    this.applicationId = applicationId;
    this.isRequest = isRequest;
    this.name = name;
  }

  public MessageRepresentationImpl(MessageRepresentationImpl clone) {
    super();
    this.applicationId = clone.applicationId;
    this.commandCode = clone.commandCode;
    this.isRequest = clone.isRequest;
    this.name = clone.name;
    // TODO: copy avps?
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (applicationId ^ (applicationId >>> 32));
    result = prime * result + commandCode;
    result = prime * result + (isRequest ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    MessageRepresentationImpl other = (MessageRepresentationImpl) obj;
    if (applicationId != other.applicationId) {
      return false;
    }
    if (commandCode != other.commandCode) {
      return false;
    }
    if (isRequest != other.isRequest) {
      return false;
    }
    return true;
  }

  public Map<AvpRepresentation, AvpRepresentation> getMessageAvps() {
    return unmuttableMessageAvps;
  }

  public void setMessageAvps(Map<AvpRepresentation, AvpRepresentation> messageAvps) {
    //this.unmuttableMessageAvps = Collections.unmodifiableMap(messageAvps);
    this.unmuttableMessageAvps = messageAvps;
  }

  public int getCommandCode() {
    return commandCode;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public boolean isRequest() {
    return isRequest;
  }

  public String getName() {
    return name;
  }

  public AvpRepresentation getAvp(int code) {
    return getAvp(code, 0);
  }

  public AvpRepresentation getAvp(int code, long vendorId) {
    AvpRepresentation avp = unmuttableMessageAvps.get(new AvpRepresentationImpl(code, vendorId));

    if (avp == null) {
      logger.warn("AVP with code " + code + " and Vendor-Id " + vendorId + " not present in dictionary!");
    }

    return avp;
  }

  // Convenience methods ------------------------------------------------------

  public boolean isAllowed(int code, long vendorId) {
    AvpRepresentation avpRep = new AvpRepresentationImpl(code, vendorId);
    avpRep = this.unmuttableMessageAvps.get(avpRep);
    if (avpRep == null) {
      return true;
    }
    return avpRep.isAllowed();
  }

  public boolean isAllowed(int code) {
    return this.isAllowed(code, 0);
  }

  public boolean isCountValidForMultiplicity(int code, int avpCount) {
    return this.isCountValidForMultiplicity(code, 0, avpCount);
  }

  public boolean isCountValidForMultiplicity(int code, long vendorId, int avpCount) {
    AvpRepresentation avpRep = getAvp(code, vendorId);
    if (avpRep == null) {
      return true;
    }
    return avpRep.isCountValidForMultiplicity(avpCount);
  }

  public boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId) {
    return this.isCountValidForMultiplicity(destination, code,vendorId,0);
  }

  public boolean isCountValidForMultiplicity(AvpSet destination, int code) {
    return this.isCountValidForMultiplicity(destination, code,0L);
  }

  public boolean isCountValidForMultiplicity(AvpSet destination, int code, long vendorId, int numberToAdd) {
    AvpRepresentation avpRep = getAvp(code, vendorId);
    if (avpRep == null) {
      return true;
    }

    return avpRep.isCountValidForMultiplicity(destination, numberToAdd);
  }

  public boolean isCountValidForMultiplicity(AvpSet destination, int code, int numberToAdd) {
    return this.isCountValidForMultiplicity(destination, code, 0, numberToAdd);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(MessageRepresentation o) {
    if (o == this) {
      return 0;
    }
    if (o == null) {
      return 1;
    }
    return this.hashCode() - o.hashCode();
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    MessageRepresentationImpl clone = (MessageRepresentationImpl) super.clone();
    clone.applicationId = this.applicationId;
    clone.commandCode = this.commandCode;
    clone.isRequest = this.isRequest;
    clone.name = this.name;
    // clone.messageAvps = new HashMap<AvpRepresentation,
    // AvpRepresentation>();
    Map<AvpRepresentation, AvpRepresentation> map = new HashMap<AvpRepresentation, AvpRepresentation>();
    for (Entry<AvpRepresentation, AvpRepresentation> entry : this.unmuttableMessageAvps.entrySet()) {
      map.put((AvpRepresentation) entry.getKey().clone(), (AvpRepresentation) entry.getValue().clone());
    }
    clone.setMessageAvps(map);
    return clone;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(isRequest ? "Request" : "Answer").append(" code: ").append(this.commandCode).append(" applicationId: ").append(this.applicationId)
    .append(" name: ").append(this.name);
    for (AvpRepresentation childAvp : this.getMessageAvps().values()) {
      sb.append("\n").append(childAvp.toString());
    }

    return sb.toString();
  }

  // Validation part ----------------------------------------------------------

  public void validate(Message msg, ValidatorLevel validatorLevel) throws AvpNotAllowedException {
    if (validatorLevel == ValidatorLevel.OFF) {
      return;
    }

    // if its !OFF, we will go down, at least to this section
    for (AvpRepresentation ap : this.unmuttableMessageAvps.values()) {
      AvpSet innerSet = msg.getAvps().getAvps(ap.getCode(), ap.getVendorId());

      int count = 0;
      if (innerSet != null) {
        count = innerSet.size();
      }

      if (!ap.isCountValidForMultiplicity(count)) {
        throw new AvpNotAllowedException("AVP: \n" + ap + "\n,has wrong count in message - " + (count), ap.getCode(), ap.getVendorId());
      }
      // if its ALL, we need to go down deeper in AVPs
      if (validatorLevel != ValidatorLevel.ALL) {
        continue;
      }
      if (count != 0 && ap.isGrouped()) {
        // we are grouped
        validateGrouped(ap, innerSet);
      }
    }
  }

  /**
   * @param ap
   * @param innerSet
   */
  private void validateGrouped(AvpRepresentation ap, AvpSet innerSet) {
    // we have set of grouped avps, and ap is grouped, lets validate
    // NOTE this methods can be called multiple time, until we dont have

    for (int index = 0; index < innerSet.size(); index++) {

      Avp presumablyGrouped = innerSet.getAvpByIndex(index);
      ap.validate(presumablyGrouped);
      // AvpSet groupedPart = null;
      // try {
      //   groupedPart = presumablyGrouped.getGrouped();
      // }
      // catch (AvpDataException e) {
      //   logger.debug("Failed to get grouped AVP.", e);
      // }
      // 
      // if (groupedPart == null) {
      //   logger.error("Avp should be grouped, but its not: " + ap);
      // 
      //   continue;
      // }
      // else {
      //   validateGroupedChildren(ap, groupedPart);
      // }
    }
  }

  //	/**
  //	 * @param ap
  //	 * @param presumablyGrouped
  //	 */
  //	private void validateGroupedChildren(AvpRepresentation ap, AvpSet groupedAvp) {
  //		// we have grouped avp, and its representation, we should validate
  //		// children.
  //		for (AvpRepresentation childrenVAvp : ap.getChildren()) {
  //			AvpSet childSset = groupedAvp.getAvps(childrenVAvp.getCode(), childrenVAvp.getVendorId());
  //			int count = childSset.size();
  //
  //			if (!childrenVAvp.isCountValidForMultiplicity(count)) {
  //				throw new AvpNotAllowedException("AVP: " + childrenVAvp + " has wrong count ,in grouped parent avp - " + (count) + ", allowed: "
  //						+ childrenVAvp.getMultiplicityIndicator(), ap.getCode(), ap.getVendorId());
  //			}
  //			if (childrenVAvp.isGrouped()) {
  //
  //				validateGrouped(childrenVAvp, childSset);
  //			}
  //			// else we are good ?
  //		}
  //
  //	}

}
