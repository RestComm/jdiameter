/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
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
