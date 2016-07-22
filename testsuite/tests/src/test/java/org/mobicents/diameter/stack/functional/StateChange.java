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

/**
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class StateChange<T> {

  private T oldState;
  private T newState;

  /**
   * @param oldState
   * @param newState
   */
  public StateChange(T oldState, T newState) {
    super();
    this.oldState = oldState;
    this.newState = newState;
  }

  public T getOldState() {
    return oldState;
  }

  public T getNewState() {
    return newState;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((newState == null) ? 0 : newState.hashCode());
    result = prime * result + ((oldState == null) ? 0 : oldState.hashCode());
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
    StateChange other = (StateChange) obj;
    if (newState == null) {
      if (other.newState != null) {
        return false;
      }
    }
    else if (!newState.equals(other.newState)) {
      return false;
    }
    if (oldState == null) {
      if (other.oldState != null) {
        return false;
      }
    }
    else if (!oldState.equals(other.oldState)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "StateChange [oldState=" + oldState + ", newState=" + newState + "]";
  }

}
