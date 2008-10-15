package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

/**
 * Java class to represent the TriggerType enumerated type.
 * Author: baranowb
 */
public class TriggerType implements net.java.slee.resource.diameter.base.events.avp.Enumerated, java.io.Serializable{
    public static final int _CHANGE_IN_LOCATION=3;

    public static final int _CHANGE_IN_QOS=2;

    public static final int _CHANGE_IN_RAT=4;

    public static final int _CHANGE_IN_SGSN_IP_ADDRESS=1;

    public static final int _CHANGEINLOCATION_CellId=34;

    public static final int _CHANGEINLOCATION_LAC=33;

    public static final int _CHANGEINLOCATION_MCC=30;

    public static final int _CHANGEINLOCATION_MNC=31;

    public static final int _CHANGEINLOCATION_RAC=32;

    public static final int _CHANGEINPARTICIPANTS_Number=50;

    public static final int _CHANGEINQOS_DELAY_CLASS=12;

    public static final int _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK=23;

    public static final int _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK=22;

    public static final int _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK=17;

    public static final int _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK=16;

    public static final int _CHANGEINQOS_MEAN_THROUGHPUT=15;

    public static final int _CHANGEINQOS_PEAK_THROUGHPUT=13;

    public static final int _CHANGEINQOS_PRECEDENCE_CLASS=14;

    public static final int _CHANGEINQOS_RELIABILITY_CLASS=11;

    public static final int _CHANGEINQOS_RESIDUAL_BER=18;

    public static final int _CHANGEINQOS_SDU_ERROR_RATIO=19;

    public static final int _CHANGEINQOS_TRAFFIC_CLASS=10;

    public static final int _CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY=21;

    public static final int _CHANGEINQOS_TRANSFER_DELAY=20;

    /**
     * This value is used to indicate that a change in the end user location shall cause the credit control client to ask for a re- authorisation of the associated quota. This should not be used in conjunction with enumerated values 30 to 34.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGE_IN_LOCATION=new TriggerType(_CHANGE_IN_LOCATION);

    /**
     * This value is used to indicate that a change in the end user negotiated QoS shall cause the credit control client to ask for a re- authorisation of the associated quota. This should not be used in conjunction with enumerated values 10 to 23.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGE_IN_QOS=new TriggerType(_CHANGE_IN_QOS);

    /**
     * This value is used to indicate that a change in the radio access technology shall cause the credit control client to ask for a re- authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGE_IN_RAT=new TriggerType(_CHANGE_IN_RAT);

    /**
     * This value is used to indicate that a change in the SGSN IP address shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGE_IN_SGSN_IP_ADDRESS=new TriggerType(_CHANGE_IN_SGSN_IP_ADDRESS);

    /**
     * This value is used to indicate that a change in the Cell Identity where the end user is located shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINLOCATION_CellId=new TriggerType(_CHANGEINLOCATION_CellId);

    /**
     * This value is used to indicate that a change in the LAC where the end user is located shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINLOCATION_LAC=new TriggerType(_CHANGEINLOCATION_LAC);

    /**
     * This value is used to indicate that a change in the MCC of the serving network shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINLOCATION_MCC=new TriggerType(_CHANGEINLOCATION_MCC);

    /**
     * This value is used to indicate that a change in the MNC of the serving network shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINLOCATION_MNC=new TriggerType(_CHANGEINLOCATION_MNC);

    /**
     * This value is used to indicate that a change in the RAC where the end user is located shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINLOCATION_RAC=new TriggerType(_CHANGEINLOCATION_RAC);

    /**
     * This value is used specifically for PoC to indicate that a change in the number of active participants within a PoC session shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINPARTICIPANTS_Number=new TriggerType(_CHANGEINPARTICIPANTS_Number);

    /**
     * This value is used to indicate that a change in the end user negotiated delay class shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_DELAY_CLASS=new TriggerType(_CHANGEINQOS_DELAY_CLASS);

    /**
     * This value is used to indicate that a change in the end user negotiated downlink guaranteed bit rate shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK=new TriggerType(_CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK);

    /**
     * This value is used to indicate that a change in the end user negotiated uplink guaranteed bit rate shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK=new TriggerType(_CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK);

    /**
     * This value is used to indicate that a change in the end user negotiated downlink maximum bit rate shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK=new TriggerType(_CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK);

    /**
     * This value is used to indicate that a change in the end user negotiated uplink maximum bit rate shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK=new TriggerType(_CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK);

    /**
     * This value is used to indicate that a change in the end user negotiated mean throughput shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_MEAN_THROUGHPUT=new TriggerType(_CHANGEINQOS_MEAN_THROUGHPUT);

    /**
     * This value is used to indicate that a change in the end user negotiated peak throughput shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_PEAK_THROUGHPUT=new TriggerType(_CHANGEINQOS_PEAK_THROUGHPUT);

    /**
     * This value is used to indicate that a change in the end user negotiated precedence class shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_PRECEDENCE_CLASS=new TriggerType(_CHANGEINQOS_PRECEDENCE_CLASS);

    /**
     * This value is used to indicate that a change in the end user negotiated reliability class shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_RELIABILITY_CLASS=new TriggerType(_CHANGEINQOS_RELIABILITY_CLASS);

    /**
     * This value is used to indicate that a change in the end user negotiated residual BER shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_RESIDUAL_BER=new TriggerType(_CHANGEINQOS_RESIDUAL_BER);

    /**
     * This value is used to indicate that a change in the end user negotiated SDU error ratio shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_SDU_ERROR_RATIO=new TriggerType(_CHANGEINQOS_SDU_ERROR_RATIO);

    /**
     * This value is used to indicate that a change in the end user negotiated traffic class shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_TRAFFIC_CLASS=new TriggerType(_CHANGEINQOS_TRAFFIC_CLASS);

    /**
     * This value is used to indicate that a change in the end user negotiated traffic handling priority shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY=new TriggerType(_CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY);

    /**
     * This value is used to indicate that a change in the end user negotiated transfer delay shall cause the credit control client to ask for a re-authorisation of the associated quota.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.TriggerType CHANGEINQOS_TRANSFER_DELAY=new TriggerType(_CHANGEINQOS_TRANSFER_DELAY);

    private TriggerType(int v)
    {
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static TriggerType fromInt(int type) {
        switch(type) {
        case _CHANGE_IN_LOCATION: return CHANGE_IN_LOCATION;

        case _CHANGE_IN_QOS: return CHANGE_IN_QOS;

        case _CHANGE_IN_RAT: return CHANGE_IN_RAT;

        case _CHANGE_IN_SGSN_IP_ADDRESS: return CHANGE_IN_SGSN_IP_ADDRESS;

        case _CHANGEINLOCATION_CellId: return CHANGEINLOCATION_CellId;

        case _CHANGEINLOCATION_LAC: return CHANGEINLOCATION_LAC;

        case _CHANGEINLOCATION_MCC: return CHANGEINLOCATION_MCC;

        case _CHANGEINLOCATION_MNC: return CHANGEINLOCATION_MNC;

        case _CHANGEINLOCATION_RAC: return CHANGEINLOCATION_RAC;

        case _CHANGEINPARTICIPANTS_Number: return CHANGEINPARTICIPANTS_Number;

        case _CHANGEINQOS_DELAY_CLASS: return CHANGEINQOS_DELAY_CLASS;

        case _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK: return CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK;

        case _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK: return CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK;

        case _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK: return CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK;

        case _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK: return CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK;

        case _CHANGEINQOS_MEAN_THROUGHPUT: return CHANGEINQOS_MEAN_THROUGHPUT; 

        case _CHANGEINQOS_PEAK_THROUGHPUT: return CHANGEINQOS_PEAK_THROUGHPUT;

        case _CHANGEINQOS_PRECEDENCE_CLASS: return CHANGEINQOS_PRECEDENCE_CLASS;

        case _CHANGEINQOS_RELIABILITY_CLASS: return CHANGEINQOS_RELIABILITY_CLASS;

        case _CHANGEINQOS_RESIDUAL_BER: return CHANGEINQOS_RESIDUAL_BER;

        case _CHANGEINQOS_SDU_ERROR_RATIO: return CHANGEINQOS_SDU_ERROR_RATIO;

        case _CHANGEINQOS_TRAFFIC_CLASS: return CHANGEINQOS_TRAFFIC_CLASS;

        case _CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY: return CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY;

        case _CHANGEINQOS_TRANSFER_DELAY: return CHANGEINQOS_TRANSFER_DELAY;
            default: throw new IllegalArgumentException("Invalid TriggerType value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _CHANGE_IN_LOCATION: return "CHANGE_IN_LOCATION";

        case _CHANGE_IN_QOS: return "CHANGE_IN_QOS";

        case _CHANGE_IN_RAT: return "CHANGE_IN_RAT";

        case _CHANGE_IN_SGSN_IP_ADDRESS: return "CHANGE_IN_SGSN_IP_ADDRESS";

        case _CHANGEINLOCATION_CellId: return "CHANGEINLOCATION_CellId";

        case _CHANGEINLOCATION_LAC: return "CHANGEINLOCATION_LAC";

        case _CHANGEINLOCATION_MCC: return "CHANGEINLOCATION_MCC";

        case _CHANGEINLOCATION_MNC: return "CHANGEINLOCATION_MNC";

        case _CHANGEINLOCATION_RAC: return "CHANGEINLOCATION_RAC";

        case _CHANGEINPARTICIPANTS_Number: return "CHANGEINPARTICIPANTS_Number";

        case _CHANGEINQOS_DELAY_CLASS: return "CHANGEINQOS_DELAY_CLASS";

        case _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK: return "CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_DOWNLINK";

        case _CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK: return "CHANGEINQOS_GUARANTEED_BIT_RATE_FOR_UPLINK";

        case _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK: return "CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_DOWNLINK";

        case _CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK: return "CHANGEINQOS_MAXIMUM_BIT_RATE_FOR_UPLINK";

        case _CHANGEINQOS_MEAN_THROUGHPUT: return "CHANGEINQOS_MEAN_THROUGHPUT"; 

        case _CHANGEINQOS_PEAK_THROUGHPUT: return "CHANGEINQOS_PEAK_THROUGHPUT";

        case _CHANGEINQOS_PRECEDENCE_CLASS: return "CHANGEINQOS_PRECEDENCE_CLASS";

        case _CHANGEINQOS_RELIABILITY_CLASS: return "CHANGEINQOS_RELIABILITY_CLASS";

        case _CHANGEINQOS_RESIDUAL_BER: return "CHANGEINQOS_RESIDUAL_BER";

        case _CHANGEINQOS_SDU_ERROR_RATIO: return "CHANGEINQOS_SDU_ERROR_RATIO";

        case _CHANGEINQOS_TRAFFIC_CLASS: return "CHANGEINQOS_TRAFFIC_CLASS";

        case _CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY: return "CHANGEINQOS_TRAFFIC_HANDLING_PRIORITY";

        case _CHANGEINQOS_TRANSFER_DELAY: return "CHANGEINQOS_TRANSFER_DELAY";
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
