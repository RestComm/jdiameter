/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.api.fsm;

import org.jdiameter.api.app.StateEvent;
import org.jdiameter.client.api.IMessage;

/**
 * This class extends behaviour of FSM StateEvent
 */
public class FsmEvent implements StateEvent {

    private String key;
    private EventTypes type;
    private Object value;

    /**
     * Create instance of class
     * @param type type of event
     */
    public FsmEvent(EventTypes type) {
        this.type = type;
    }

    /**
     * Create instance of class with predefined parameters
     * @param type type of event
     * @param key event key
     */
    public FsmEvent(EventTypes type, String key) {
        this(type);
        this.key = key;
    }

    /**
     * Create instance of class with predefined parameters
     * @param type type of event
     * @param value attached message
     */
    public FsmEvent(EventTypes type, IMessage value) {
        this(type);
        this.value = value;
    }

    /**
     * Create instance of class with predefined parameters
     * @param type type of event
     * @param value  attached message
     * @param key event key
     */
    public FsmEvent(EventTypes type, IMessage value, String key) {
        this(type, value);
        this.key = key;
    }

    /**
     * Return key value
     * @return key value
     */
    public String getKey() {
        return key;
    }

    /**
     * Return attached message
     * @return diameter message
     */
    public IMessage getMessage() {
        return (IMessage) getData();
    }

    public <E> E encodeType(Class<E> eClass) {
        return (E) type;
    }

    public Enum getType() {
        return type;
    }

    public void setData(Object o) {
        value = o;
    }

    public Object getData() {
        return value;
    }

    public int compareTo(Object o) {
        return 0;
    }

    /**
     * Return string representation of instance
     * @return string representation of instance
     */
    public String toString() {
      return "Event{name:" + type.name() + ", key:" + key + ", object:" + value + "}";
    }
}
