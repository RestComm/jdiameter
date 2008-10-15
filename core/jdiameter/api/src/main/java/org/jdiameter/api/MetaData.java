/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com, artem.litvinov@gmail.com
 *
 */
package org.jdiameter.api;

/**
 * This interface is implemented by sack vendors to let users know the local properties of a Diameter Stack implementation
 * and current instance.
 * @version 1.5.1 Final
 */

public interface MetaData extends Wrapper {

    /**
     * @return  Retrieves the stack's major version number.
     */
    int getMajorVersion();

    /**
     * @return Retrieves the stack's minor version number.
     */
    int getMinorVersion();

    /**
     * @return stack type
     */
    StackType getStackType();

    /**
     * @return information about local instance of peer
     */
    Peer getLocalPeer();

    /**
     * Return configuration parameters
     * @return configuration 
     */
    Configuration getConfiguration();
}
