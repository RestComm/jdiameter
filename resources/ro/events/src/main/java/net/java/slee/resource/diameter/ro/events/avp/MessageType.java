package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * Java class to represent the MessageType enumerated type.
 * Author: baranowb
 */
public class MessageType implements Enumerated, java.io.Serializable{
    public static final int _M_ACKNOWLEDGE_IND=6;

    public static final int _M_DELIVERY_IND=7;

    public static final int _M_FORWARD_CONF=11;

    public static final int _M_FORWARD_REQ=20;

    public static final int _M_MBOX_DELETE_CONF=15;

    public static final int _M_MBOX_STORE_CONF=12;

    public static final int _M_MBOX_UPLOAD_CONF=14;

    public static final int _M_MBOX_VIEW_CONF=13;

    public static final int _M_NOTIFICATION_IND=3;

    public static final int _M_NOTIFYRESP_IND=4;

    public static final int _M_READ_ORIG_IND=9;

    public static final int _M_READ_REC_IND=8;

    public static final int _M_RETRIEVE_CONF=5;

    public static final int _M_SEND_CONF=2;

    public static final int _M_SEND_REQ=1;

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_ACKNOWLEDGE_IND=new MessageType(_M_ACKNOWLEDGE_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_DELIVERY_IND=new MessageType(_M_DELIVERY_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_FORWARD_CONF=new MessageType(_M_FORWARD_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_FORWARD_REQ=new MessageType(_M_FORWARD_REQ);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_MBOX_DELETE_CONF=new MessageType(_M_MBOX_DELETE_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_MBOX_STORE_CONF=new MessageType(_M_MBOX_STORE_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_MBOX_UPLOAD_CONF=new MessageType(_M_MBOX_UPLOAD_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_MBOX_VIEW_CONF=new MessageType(_M_MBOX_VIEW_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_NOTIFICATION_IND=new MessageType(_M_NOTIFICATION_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_NOTIFYRESP_IND=new MessageType(_M_NOTIFYRESP_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_READ_ORIG_IND=new MessageType(_M_READ_ORIG_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_READ_REC_IND=new MessageType(_M_READ_REC_IND);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_RETRIEVE_CONF=new MessageType(_M_RETRIEVE_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_SEND_CONF=new MessageType(_M_SEND_CONF);

    public static final net.java.slee.resource.diameter.ro.events.avp.MessageType M_SEND_REQ=new MessageType(_M_SEND_REQ);

    private MessageType(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static MessageType fromInt(int type) {
        switch(type) {
        case _M_ACKNOWLEDGE_IND: return M_ACKNOWLEDGE_IND;

        case _M_DELIVERY_IND: return M_DELIVERY_IND;

        case _M_FORWARD_CONF: return M_FORWARD_REQ;

        case _M_FORWARD_REQ: return M_FORWARD_REQ;

        case _M_MBOX_DELETE_CONF: return M_MBOX_DELETE_CONF;

        case _M_MBOX_STORE_CONF: return M_MBOX_STORE_CONF;

        case _M_MBOX_UPLOAD_CONF: return M_MBOX_UPLOAD_CONF;

        case _M_MBOX_VIEW_CONF: return M_MBOX_VIEW_CONF;

        case _M_NOTIFICATION_IND: return M_NOTIFICATION_IND;

        case _M_NOTIFYRESP_IND: return M_NOTIFYRESP_IND;

        case _M_READ_ORIG_IND: return M_READ_ORIG_IND;

        case _M_READ_REC_IND: return M_READ_REC_IND;

        case _M_RETRIEVE_CONF: return M_RETRIEVE_CONF;

        case _M_SEND_CONF: return M_SEND_CONF;

        case _M_SEND_REQ: return M_SEND_REQ;
            default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _M_ACKNOWLEDGE_IND: return "M_ACKNOWLEDGE_IND";

        case _M_DELIVERY_IND: return "M_DELIVERY_IND";

        case _M_FORWARD_CONF: return "M_FORWARD_REQ";

        case _M_FORWARD_REQ: return "M_FORWARD_REQ";

        case _M_MBOX_DELETE_CONF: return "M_MBOX_DELETE_CONF";

        case _M_MBOX_STORE_CONF: return "M_MBOX_STORE_CONF";

        case _M_MBOX_UPLOAD_CONF: return "M_MBOX_UPLOAD_CONF";

        case _M_MBOX_VIEW_CONF: return "M_MBOX_VIEW_CONF";

        case _M_NOTIFICATION_IND: return "M_NOTIFICATION_IND";

        case _M_NOTIFYRESP_IND: return "M_NOTIFYRESP_IND";

        case _M_READ_ORIG_IND: return "M_READ_ORIG_IND";

        case _M_READ_REC_IND: return "M_READ_REC_IND";

        case _M_RETRIEVE_CONF: return "M_RETRIEVE_CONF";

        case _M_SEND_CONF: return "M_SEND_CONF";

        case _M_SEND_REQ: return "M_SEND_REQ";
            default: return "<Invalid Value>";
        }
    }

    private Object readResolve() throws StreamCorruptedException {
        try {
            return fromInt(value);
        }
        catch (IllegalArgumentException iae) {
            throw new StreamCorruptedException("Invalid internal state found: " + value);
        }
    }

    private int value=0;

}
