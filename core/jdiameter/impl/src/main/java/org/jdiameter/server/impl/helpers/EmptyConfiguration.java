/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.server.impl.helpers;

import org.jdiameter.api.ConfigurationListener;
import org.jdiameter.api.MutableConfiguration;
import org.jdiameter.client.impl.helpers.ExtensionPoint;
import static org.jdiameter.client.impl.helpers.Parameters.ExtensioinName;
import static org.jdiameter.server.impl.helpers.ExtensionPoint.*;
import static org.jdiameter.server.impl.helpers.Parameters.Extensions;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class allow create configuration class for stack
 */
public class EmptyConfiguration extends org.jdiameter.client.impl.helpers.EmptyConfiguration implements MutableConfiguration {

    private final ConcurrentHashMap<Integer, List> listeners = new ConcurrentHashMap<Integer, List>();

    protected EmptyConfiguration() {
        this(true);
    }

    /**
     * Create instance of class
     *
     * @param callInit true if need append default parameters
     */
    public EmptyConfiguration(boolean callInit) {
        if (callInit)
            add(Extensions, getInstance(). // Internal extension point
                    add(ExtensioinName, ExtensionPoint.Internal.name()).
                    add(InternalMetaData, "org.jdiameter.server.impl.MetaDataImpl").
                    add(InternalMessageParser, InternalMessageParser.defValue()).
                    add(InternalElementParser, InternalElementParser.defValue()).
                    add(InternalTransportFactory, "org.jdiameter.server.impl.io.TransportLayerFactory").
                    add(InternalPeerFsmFactory, "org.jdiameter.server.impl.fsm.FsmFactoryImpl").
                    add(InternalSessionFactory, InternalSessionFactory.defValue()).
                    add(InternalRouterEngine, "org.jdiameter.server.impl.RouterImpl").
                    add(InternalNetWork, "org.jdiameter.server.impl.NetworkImpl").
                    add(InternalStatisticFactory, InternalStatisticFactory.defValue()).
                    add(InternalOverloadManager, "org.jdiameter.server.impl.OverloadManagerImpl").
                    add(InternalPeerController, "org.jdiameter.server.impl.MutablePeerTableImpl"),
                    getInstance().  // StackLayer extension point
                            add(ExtensioinName, ExtensionPoint.StackLayer.name()),
                    getInstance().  // ControllerLayer extension point
                            add(ExtensioinName, ExtensionPoint.ControllerLayer.name()),
                    getInstance().  // TransportLayer extension point
                            add(ExtensioinName, ExtensionPoint.TransportLayer.name())
            );
    }
                                                              //

    public void setByteValue(int key, byte value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setIntValue(int key, int value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setLongValue(int key, long value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setDoubleValue(int key, double value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setByteArrayValue(int key, byte[] value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setBooleanValue(int key, boolean value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setStringValue(int key, java.lang.String value) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, value);
            if (commit)
                putValue(key, value);
        } else {
            putValue(key, value);
        }
    }

    public void setChildren(int key, org.jdiameter.api.Configuration... values) {
        List<ConfigurationListener> list = listeners.get(key);
        if (list != null)  {
            boolean commit = true;
            for (ConfigurationListener l : list)
                commit &= l.elementChanged(key, values);
            if (commit)
                putValue(key, values);
                // Removed due to issue #1009 (http://code.google.com/p/mobicents/issues/detail?id=1009)
                // putValue(key, new EmptyConfiguration(false).add(key, values));
        } else {
            putValue(key, values);
            // Removed due to issue #1009 (http://code.google.com/p/mobicents/issues/detail?id=1009)
            // putValue(key, new EmptyConfiguration(false).add(key, values));
        }
    }

    public void removeValue(int... keys) {
        for (int i:keys) {
            List<ConfigurationListener> list = listeners.get(i);
            if (list != null) {
                boolean rem = true;
                for (ConfigurationListener l : list) {
                    rem &= l.elementChanged(i, null);
                }
                if (rem) {
                  removeValue(i);
                }
            }
        }
    }

    /**
     * @see org.jdiameter.api.MutableConfiguration class
     */
    public void addChangeListener(ConfigurationListener listener, int... ints) {
        for (int i:ints) {
            List list = listeners.get(i);
            if (list == null) {
                list = new java.util.concurrent.CopyOnWriteArrayList<ConfigurationListener>();
                list.add(listener);
            }
            listeners.put(i, list);
        }
    }

    /**
     * @see org.jdiameter.api.MutableConfiguration class
     */
    public void removeChangeListener(ConfigurationListener listener,int... ints) {
        for (int i:ints) {
            List list = listeners.get(i);
            if (list != null) {
                list.remove(listener);
                if (list.size() == 0) listeners.remove(i);

            }
        }
    }
}
