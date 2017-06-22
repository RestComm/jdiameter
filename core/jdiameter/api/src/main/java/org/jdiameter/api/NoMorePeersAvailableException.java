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

package org.jdiameter.api;

/**
 * Signals that no peer is available for routing.
 *
 * @author <a href="mailto:info@pro-ids.com"> ProIDS sp. z o.o.</a>
 */
public class NoMorePeersAvailableException extends RouteException {

  private static final long serialVersionUID = 1L;

  private boolean sessionPersistentRoutingEnabled = false;
  private int lastSelectedPeerRating = -1;
  private String roundRobinContextDescription = null;

  /**
   * Constructor with reason string and routing details
   *
   * @param message reason string
   */
  public NoMorePeersAvailableException(String message, boolean spre, String rrcd, int lspr) {
    super(message);
    this.setSessionPersistentRoutingEnabled(spre);
    this.setRoundRobinContextDescription(rrcd);
    this.setLastSelectedPeerRating(lspr);
  }

  /**
   * Constructor with reason string and parent exception
   *
   * @param message message reason string
   * @param cause   parent exception
   */
  public NoMorePeersAvailableException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor with reason string
   *
   * @param message reason string
   */
  public NoMorePeersAvailableException(String message) {
    super(message);
  }

  public boolean isSessionPersistentRoutingEnabled() {
    return sessionPersistentRoutingEnabled;
  }

  public void setSessionPersistentRoutingEnabled(boolean sessionPersistentRoutingEnabled) {
    this.sessionPersistentRoutingEnabled = sessionPersistentRoutingEnabled;
  }

  public String getRoundRobinContextDescription() {
    return roundRobinContextDescription;
  }

  public void setRoundRobinContextDescription(String roundRobinContextDescription) {
    this.roundRobinContextDescription = roundRobinContextDescription;
  }

  public int getLastSelectedPeerRating() {
    return lastSelectedPeerRating;
  }

  public void setLastSelectedPeerRating(int lastSelectedPeerRating) {
    this.lastSelectedPeerRating = lastSelectedPeerRating;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("NoMorePeersAvailableException [sessionPersistentRoutingEnabled=")
        .append(sessionPersistentRoutingEnabled)
        .append(", lastSelectedPeerRating=").append(lastSelectedPeerRating)
        .append(", roundRobinContextDescription=")
        .append(roundRobinContextDescription)
        .append(", message=").append(getMessage())
        .append("]");
    return builder.toString();
  }
}
