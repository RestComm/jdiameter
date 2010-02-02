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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class provide pluggable features
 */
public class ExtensionPoint extends Ordinal {

  private static final long serialVersionUID = 1L;

    protected static int index;

    private static ArrayList<Parameters> value = new ArrayList<Parameters>();

    /**
     * MetaData implementation class name
     */
    public static final ExtensionPoint InternalMetaData = new ExtensionPoint("InternalMetaData", "org.jdiameter.client.impl.MetaDataImpl");

    /**
     * Message parser implementation class name
     */
    public static final ExtensionPoint InternalMessageParser = new ExtensionPoint("InternalMessageParser", "org.jdiameter.client.impl.parser.MessageParser");

    /**
     * Element message implementation class name
     */
    public static final ExtensionPoint InternalElementParser = new ExtensionPoint("InternalElementParser", "org.jdiameter.client.impl.parser.ElementParser");

    /**
     * Router enginr implementation class name
     */
    public static final ExtensionPoint InternalRouterEngine = new ExtensionPoint("InternalRouterEngine", "org.jdiameter.client.impl.router.RouterImpl");

    /**
     * Peer controller implementation class name
     */
    public static final ExtensionPoint InternalPeerController = new ExtensionPoint("InternalPeerController", "org.jdiameter.client.impl.controller.PeerTableImpl");

    /**
     * Session factiry implementation class name
     */
    public static final ExtensionPoint InternalSessionFactory = new ExtensionPoint("InternalSessionFactory", "org.jdiameter.client.impl.SessionFactoryImpl");

    /**
     * Transport factory implementation class name
     */
    public static final ExtensionPoint InternalTransportFactory = new ExtensionPoint("InternalTransportFactory", "org.jdiameter.client.impl.transport.TransportLayerFactory");

    /**
     * Peer fsm factory implementation class name
     */
    public static final ExtensionPoint InternalPeerFsmFactory = new ExtensionPoint("InternalPeerFsmFactory", "org.jdiameter.client.impl.fsm.FsmFactoryImpl");

    /**
     * Statistic factory implementation class name
     */
    public static final ExtensionPoint InternalStatisticFactory = new ExtensionPoint("InternalStatisticFactory", "org.jdiameter.common.impl.statistic.StatisticFactory");

    /**
     * Statistic factory implementation class name
     */
    public static final ExtensionPoint InternalStatisticProcessor = new ExtensionPoint("InternalStatisticProcessor", "org.jdiameter.common.impl.statistic.StatisticProcessor");

    /**
     * Concurrent factory implementation class name
     */
    public static final ExtensionPoint InternalConcurrentFactory = new ExtensionPoint("InternalConcurrentFactory", "org.jdiameter.common.impl.concurrent.ConcurrentFactory");

    /**
     * List of internal extension point
     */
    public static final ExtensionPoint Internal = new ExtensionPoint(
            "Internal", 0,
            InternalMetaData,
            InternalMessageParser,
            InternalElementParser,
            InternalRouterEngine,
            InternalPeerController,
            InternalSessionFactory,
            InternalTransportFactory,
            InternalPeerFsmFactory,
            InternalStatisticFactory,
            InternalConcurrentFactory,
            InternalStatisticProcessor
    );

    /**
     * Stack layer
     */
    public static final ExtensionPoint StackLayer = new ExtensionPoint("StackLayer", 0);

    /**
     * Controller layer
     */
    public static final ExtensionPoint ControllerLayer = new ExtensionPoint("ControllerLayer", 1);

    /**
     * Transport layer
     */
    public static final ExtensionPoint TransportLayer = new ExtensionPoint("TransportLayer", 2);

    /**
     * Return Iterator of all entries
     * 
     * @return  Iterator of all entries
     */
    public static Iterable<Parameters> values() {
        return value;
    }

    private ExtensionPoint[] elements = new ExtensionPoint[0];
    private String defaultValue = "";
    private int id = -1;

    /**
     * Type's count of extension point
     */
    public static final int COUNT = 3;

    /**
     * Create instance of class
     */
    public ExtensionPoint() {
        this.ordinal = index++;
    }

    protected ExtensionPoint(String name, String defaultValue) {
        this();
        this.name = name;
        this.defaultValue = defaultValue;
    }

    protected ExtensionPoint(String name, ExtensionPoint... elements) {
        this();
        this.name = name;
        this.elements = elements;
    }

    protected ExtensionPoint(String name, int id, ExtensionPoint... elements) {
        this();
        this.name = name;
        this.id = id;
        this.elements = elements;
    }

    /**
     * Append extension point entries
     * 
     * @param elements array of append extension point entries
     */
    public void appendElements(ExtensionPoint... elements) {
        List<ExtensionPoint> rc = new ArrayList<ExtensionPoint>();
        rc.addAll(Arrays.asList(this.elements));
        rc.addAll(Arrays.asList(elements));
        this.elements = rc.toArray(new ExtensionPoint[0]);
    }

    /**
     * Return parameters of extension point
     * 
     * @return array parameters of extension point
     */
    public ExtensionPoint[] getArrayOfParameters() {
        return elements;
    }

    /**
     * Return default value of extension point
     * 
     * @return default value of extension point
     */
    public String defValue() {
        return defaultValue;
    }

    /**
     * Return id of extension point
     * 
     * @return id of extension point
     */
    public int id() {
        return id;
    }
}
