/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl.helpers;

import org.jdiameter.api.Configuration;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensioinName;
import static org.jdiameter.client.impl.helpers.Parameters.Extensions;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class allow create configuration class for stack
 */

public class EmptyConfiguration implements AppConfiguration {

    protected final Configuration[] EMPTY_ARRAY = new Configuration[0];
    private final ConcurrentHashMap<Integer, Object> p = new ConcurrentHashMap<Integer, Object>();

    /**
     * Create instance of class with system default parameters
     *
     * @return instance of class with system default parameters
     */
    public static AppConfiguration getInstance() {
        return new EmptyConfiguration(false);
    }

    /**
     * Create instance of class. Internal parameters will be appends
     */
    protected EmptyConfiguration() {
        this(true);
    }

    /**
     * Create instance of class
     *
     * @param callInit if value is true then this constructor appends internal configuration parameters
     */
    private EmptyConfiguration(boolean callInit) {
        if (callInit)
            add(Extensions, getInstance(). // Internal extension point
                                add(ExtensioinName, ExtensionPoint.Internal.name()).
                                add(InternalMetaData, InternalMetaData.defValue()).
                                add(InternalRouterEngine, InternalRouterEngine.defValue()).
                                add(InternalMessageParser, InternalMessageParser.defValue()).
                                add(InternalElementParser, InternalElementParser.defValue()).
                                add(InternalTransportFactory, InternalTransportFactory.defValue()).
                                add(InternalPeerFsmFactory, InternalPeerFsmFactory.defValue()).
                                add(InternalSessionFactory, InternalSessionFactory.defValue()).
                                add(InternalPeerController, InternalPeerController.defValue()
                            ),
                            getInstance().  // StackLayer extension point
                                add(ExtensioinName, ExtensionPoint.StackLayer.name()),
                            getInstance().  // ControllerLayer extension point
                                add(ExtensioinName, ExtensionPoint.ControllerLayer.name()),
                            getInstance().  // TransportLayer extension point
                                add(ExtensioinName, ExtensionPoint.TransportLayer.name())
            );
    }

    /**
     * @see AppConfiguration class
     */
    public AppConfiguration add(Ordinal e, Configuration... value) {
        p.put(e.ordinal(), value);
        return this;
    }

    /**
     * @see AppConfiguration class
     */
    public AppConfiguration add(Ordinal e, Object value) {
        if (value instanceof Configuration) {
            p.put(e.ordinal(), new Configuration[]{(Configuration) value});
        } else {
            p.put(e.ordinal(), value);
        }
        return this;
    }

    protected void putValue(int key, Object value) {
        p.put(key, value);
    }

    protected Object getValue(int key) {
        return p.get(key);
    }

    protected void removeVelue(int... keys) {
        for (int i:keys) p.remove(i);
    }

    protected AppConfiguration add(int e, Configuration... value) {
        p.put(e, value);
        return this;
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public byte getByteValue(int i, byte b) {
        return (Byte) (isAttributeExist(i) ? p.get(i) : b);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public int getIntValue(int i, int i1) {
        return (Integer) (isAttributeExist(i) ? p.get(i) : i1);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public long getLongValue(int i, long l) {
        return (Long) (isAttributeExist(i) ? p.get(i) : l);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public double getDoubleValue(int i, double v) {
        return (Double) (isAttributeExist(i) ? p.get(i) : v);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public byte[] getByteArrayValue(int i, byte[] bytes) {
        return (byte[]) (isAttributeExist(i) ? p.get(i) : bytes);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public boolean getBooleanValue(int i, boolean b) {
        return (Boolean) (isAttributeExist(i) ? p.get(i) : b);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public String getStringValue(int i, String defValue) {
        String result = (String) p.get(i);
        return result != null ? result : defValue;
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public boolean isAttributeExist(int i) {
        return p.containsKey(i);
    }

    /**
     * @see org.jdiameter.api.Configuration class
     */
    public Configuration[] getChildren(int i) {
        return (Configuration[]) p.get(i);
    }

    /**
     * Return string representation of configuration
     *
     * @return string representation of configuration
     */
    public String toString() {
        StringBuffer buf = new StringBuffer("Configuration");
        buf.append("{");

        for (Integer key : p.keySet()) {
            Object value = p.get(key);
            Parameters pr = getParameterByIndex(key);
            if (pr == null) continue;
            if (pr.name().equals(Extensions.name())) continue;
            if (value instanceof Configuration[]) {
                buf.append('\n');
            }
            buf.append(pr.name());
            buf.append("=");
            if (value instanceof Configuration[])
                for (Configuration i : ((Configuration[]) value))
                    buf.append(i.toString()).append('\n');
            else
                buf.append(value);
            buf.append(", ");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.deleteCharAt(buf.length() - 1);
        buf.append("}");
        return buf.toString();
    }

    private Parameters getParameterByIndex(int index) {
        for (Parameters p : Parameters.values()) {
            if (p.ordinal() == index) return p;
        }
        return null;
    }
}
