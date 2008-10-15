/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Sun Industry Standards Source License (SISSL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

package org.jdiameter.api;

/**
 * This interface is the extended version of the interface configuration and
 * allows to receive of a notification about reconfiguratings
 * @version 1.5.1 Final
 */

public interface MutableConfiguration extends Configuration {

    /**
     * Set byte value to configuration
     * @param key key of value
     * @param value byte value
     */
    void setByteValue(int key, byte value);

    /**
     * Set int value to configuration
     * @param key key of value
     * @param value int value
     */
    void setIntValue(int key, int value);

    /**
     * Set long value to configuration
     * @param key key of value
     * @param value long value
     */
    void setLongValue(int key, long value);

    /**
     * Set double value to configuration
     * @param key key of value
     * @param value double value
     */
    void setDoubleValue(int key, double value);

    /**
     * Set byte array value to configuration
     * @param key key of value
     * @param value byte array value
     */
    void setByteArrayValue(int key, byte[] value);

    /**
     * Set boolean value to configuration
     * @param key key of value
     * @param value boolean value
     */
    void setBooleanValue(int key, boolean value);

    /**
     * Set string value to configuration
     * @param key key of value
     * @param value string value
     */
    void setStringValue(int key, String value);

    /**
     * Set children to configuration
     * @param key key of children
     * @param value children value
     */
    void setChildren(int key, Configuration... value);

    /**
     * Remove defined key
     * @param key array keys of removed entry
     */
    void removeValue(int... key);

    /**
     * Add change configuration listener
     * @param listener instance of listener
     * @param keys array of observed propertie's keys
     * if keys.length == 0 then observed all properties of configuration node
     */
    void addChangeListener(ConfigurationListener listener, int... keys);

    /**
     * Remove change configuration listener
     * @param listener instance of listener
     * @param keys array of removed listener's keys
     */
    void removeChangeListener(ConfigurationListener listener, int... keys);
}
