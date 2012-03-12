/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
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

package org.jdiameter.api.app;

/**
 * Interface used to inform about changes in the state for a FSM.
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface StateChangeListener<T> {

  /**
   * @deprecated
   * A change of state has occurred for a FSM.
   * @param oldState Old state of FSM
   * @param newState New state of FSM
   */
  @SuppressWarnings("unchecked")
  void stateChanged(Enum oldState, Enum newState);

  /**
   * A change of state has occurred for a FSM.
   * 
   * @param source the App Session that generated the change. 
   * @param oldState Old state of FSM
   * @param newState New state of FSM
   */
  @SuppressWarnings("unchecked")
  void stateChanged(T source, Enum oldState, Enum newState);
}
