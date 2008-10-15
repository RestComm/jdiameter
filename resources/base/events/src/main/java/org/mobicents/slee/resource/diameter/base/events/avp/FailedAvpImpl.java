package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.FailedAvp;


public class FailedAvpImpl extends GroupedAvpImpl implements FailedAvp {
    
    public FailedAvpImpl(int code, long l, int mnd, int prt, byte[] value) {
        super(code, l, mnd, prt, value);
        name = "Failed-Avp";
    }

   
}
