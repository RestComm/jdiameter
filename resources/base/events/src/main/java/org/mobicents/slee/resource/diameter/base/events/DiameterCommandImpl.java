package org.mobicents.slee.resource.diameter.base.events;

import org.jdiameter.api.Message;

import net.java.slee.resource.diameter.base.events.DiameterCommand;

public class DiameterCommandImpl implements DiameterCommand {

    private int code;
    private long applicationId;
    private String shortName = "undefined";
    private String longName = "undefined";
    private boolean request, proxiable;

    public DiameterCommandImpl(int code, int applicationId,  boolean request, boolean proxiable) {
        this.code = code;
        this.applicationId = applicationId;
        this.request = request;
        this.proxiable = proxiable;
    }

    public DiameterCommandImpl(int code, long applicationId, String shortName, String longName, boolean request, boolean proxiable) {
        this.code = code;
        this.applicationId = applicationId;
        this.shortName = shortName;
        this.longName = longName;
        this.request = request;
        this.proxiable = proxiable;
    }

    public int getCode() {
        return code;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public boolean isRequest() {
        return request;
    }

    public boolean isProxiable() {
        return proxiable;
    }

	public String toString()
  {
    return "DiameterCommand : applicationId[" + getApplicationId() + "], " +
        "code[" + getCode() + "], " + 
        "longName[" + longName + "], " + 
        "shortName[" + shortName + "], " +
        "isProxiable[" + isProxiable() + "], " +
        "isRequest[" + isRequest() + "]";
  }

}
