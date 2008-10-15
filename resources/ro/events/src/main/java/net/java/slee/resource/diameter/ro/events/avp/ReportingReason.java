package net.java.slee.resource.diameter.ro.events.avp;

import java.io.StreamCorruptedException;

/**
 * Java class to represent the ReportingReason enumerated type.
 * Author: baranowb
 */
public class ReportingReason implements net.java.slee.resource.diameter.base.events.avp.Enumerated, java.io.Serializable{
    public static final int _FINAL=2;

    public static final int _FORCED_REAUTHORISATION=7;

    public static final int _OTHER_QUOTA_TYPE=5;

    public static final int _POOL_EXHAUSTED=8;

    public static final int _QHT=1;

    public static final int _QUOTA_EXHAUSTED=3;

    public static final int _RATING_CONDITION_CHANGE=6;

    public static final int _THRESHOLD=0;

    public static final int _VALIDITY_TIME=4;

    /**
     * This value is used to indicate that the reason for usage reporting of all quota types of the Multiple-Service-Credit-Control AVP where its appears is that a normal PDP context termination has happened.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason FINAL=new ReportingReason(_FINAL);

    /**
     * This value is used to indicate that the reason for usage reporting of all quota types of the Multiple-Service-Credit-Control AVP where its appears is that it is there has been a Server initiated re- authorisation procedure, i.e. receipt of RAR command
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason FORCED_REAUTHORISATION=new ReportingReason(_FORCED_REAUTHORISATION);

    /**
     * This value is used to indicate that the reason for usage reporting of the particular quota type indicated in the Used-Service-Units AVP where it appears is that, for a multi-dimensional quota, one reached a trigger condition and the other quota is being reported.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason OTHER_QUOTA_TYPE=new ReportingReason(_OTHER_QUOTA_TYPE);

    /**
     * This value is used to indicate that the reason for usage reporting of the particular quota type indicated in the User-Service-Units AVP where it appears is that granted units are still available in the pool but are not sufficient for a rating group using the pool.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason POOL_EXHAUSTED=new ReportingReason(_POOL_EXHAUSTED);

    /**
     * This value is used to indicate that the reason for usage reporting of all quota types of the Multiple-Service-Credit-Control AVP where its appears is that the quota holding time specified in a previous CCA command has been hit (i.e. the quota has been unused for that period of time).
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason QHT=new ReportingReason(_QHT);

    /**
     * This value is used to indicate that the reason for usage reporting of the particular quota type indicated in the Used-Service-Units AVP where it appears is that the quota has been exhausted.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason QUOTA_EXHAUSTED=new ReportingReason(_QUOTA_EXHAUSTED);

    /**
     * This value is used to indicate that the reason for usage reporting of all quota types of the Multiple-Service-Credit-Control AVP where its appears is that a change has happened in some of the rating conditions that were previously armed (through the Trigger-Type AVP, e.g. QoS, Radio Access Technology,???). The specific condition that has changed is indicated in an associated Trigger-Type AVP.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason RATING_CONDITION_CHANGE=new ReportingReason(_RATING_CONDITION_CHANGE);

    /**
     * This value is used to indicate that the reason for usage reporting of the particular quota type indicated in the Used-Service-Units AVP where it appears is that the threshold has been reached.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason THRESHOLD=new ReportingReason(_THRESHOLD);

    /**
     * This value is used to indicate that the reason for usage reporting of all quota types of the Multiple-Service-Credit-Control AVP where its appears is that the credit authorization lifetime provided in the Validity-Time AVP has expired.
     */
    public static final net.java.slee.resource.diameter.ro.events.avp.ReportingReason VALIDITY_TIME=new ReportingReason(_VALIDITY_TIME);

    private ReportingReason(int v)
    {
    	
    	value=v;
    }

    /**
     * Return the value of this instance of this enumerated type.
     */
    public static ReportingReason fromInt(int type) {
        switch(type) {
        case _FINAL: return FINAL;

        case _FORCED_REAUTHORISATION: return FORCED_REAUTHORISATION;

        case _OTHER_QUOTA_TYPE: return OTHER_QUOTA_TYPE;

        case _POOL_EXHAUSTED: return POOL_EXHAUSTED;

        case _QHT: return QHT;

        case _QUOTA_EXHAUSTED: return QUOTA_EXHAUSTED;

        case _RATING_CONDITION_CHANGE: return RATING_CONDITION_CHANGE;

        case _THRESHOLD: return THRESHOLD;

        case _VALIDITY_TIME: return VALIDITY_TIME;
            default: throw new IllegalArgumentException("Invalid DisconnectCause value: " + type);
        }
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        switch(value) {
        case _FINAL: return "FINAL";

        case _FORCED_REAUTHORISATION: return "FORCED_REAUTHORISATION";

        case _OTHER_QUOTA_TYPE: return "OTHER_QUOTA_TYPE";

        case _POOL_EXHAUSTED: return "POOL_EXHAUSTED";

        case _QHT: return "QHT";

        case _QUOTA_EXHAUSTED: return "QUOTA_EXHAUSTED";

        case _RATING_CONDITION_CHANGE: return "RATING_CONDITION_CHANGE";

        case _THRESHOLD: return "THRESHOLD";

        case _VALIDITY_TIME: return "VALIDITY_TIME";
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
