/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base.events;

/**
 * Diameter command.  Applications can use this interface to retrieve the code, short name, long name and request
 * flag of a command. 
 *
 * @author Open Cloud
 */
public interface DiameterCommand {

    /**
     * Return the code for this command, e.g., 257.
     * @return the code for this command
     */
    int getCode();

    /**
     * Return the application ID for this command, e.g., 0
     * @return the application ID for this command
     */
    long getApplicationId();

    /**
     * Return the short name for this command, e.g., "CER".
     * @return the short name for this command
     */
    String getShortName();

    /**
     * Return the long name for this command, e.g., "Capabilities-Exchange-Request".
     * @return the long name for this command
     */
    String getLongName();

   /**
    * Return true if and only if this command is a request.
    * <P>
    * @return true if and only if this command is a request
    */
    boolean isRequest();

    /**
     * Return true if and only if this command may be proxied.
     * <P>
     * @return true if and only if this command may be proxied
     */
    boolean isProxiable();
}
