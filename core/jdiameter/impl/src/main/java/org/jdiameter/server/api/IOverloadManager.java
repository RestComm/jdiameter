/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
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

package org.jdiameter.server.api;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.OverloadManager;
import org.jdiameter.api.URI;

/**
 * This interface describe extends methods of base class
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IOverloadManager extends OverloadManager {

    /**
     * Return true if application has overload
     * @param appId application id
     * @return true if application has overload
     */
    public boolean isParenAppOverload(final ApplicationId appId);

    /**
     * eturn true if application has overload by predefined type
     * @param appId application id
     * @param type type of overload (CPU, Memory... )
     * @return true if application has overload
     */
    public boolean isParenAppOverload(final ApplicationId appId, int type);

    /**
     * Notification about overload
     * @param index overload entry index
     * @param uri peer uri
     * @param value overload value
     */
    public void changeNotification(int index, URI uri, double value);
}
