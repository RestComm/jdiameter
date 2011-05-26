/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors by the
 * @authors tag. See the copyright.txt in the distribution for a
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
package org.jdiameter.api;

/**
 * This interface introduces a capability to work with a network.
 * You can get instance of this interface over stack instance:
 * <code>
 * if (stack.isWrapperFor(RealmTable.class)) {
 *       RealmTable realmTabke = stack.unwrap(RealmTable.class);
 *       .....
 * }
 * </code>
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface RealmTable extends Wrapper {

  /**
   * Return different network statistics
   * @param realmName realmName
   * @return network statistics
   */
  Statistic getStatistic(String realmName);

  /**
   * Add new realm to realm table
   * @param realmName name of realm
   * @param applicationId application id of realm
   * @param action action of realm
   * @param dynamic commCode of realm
   * @param expirationTime expiration time of realm
   * @param extraConf - additional configuration which may be used by implementation
   * @return instance of created realm
   * @throws InternalException - when realm definition under pKey and sKey exist
   */
  public Realm addRealm(String realmName, ApplicationId applicationId, LocalAction action, boolean dynamic, long expirationTime, String[] hosts) throws InternalException;

  /**
   * Checks if there is such realm entry.
   * @param realmName
   * @return
   */
  public boolean realmExists(String realmName);

}
