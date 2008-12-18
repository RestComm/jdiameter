package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import org.jdiameter.api.Avp;

public class VendorSpecificApplicationIdAvpImpl extends GroupedAvpImpl implements VendorSpecificApplicationIdAvp {

    public VendorSpecificApplicationIdAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
        super(code, vendorId, mnd, prt, value);
    }

    public long[] getVendorIds() {
        return getAllAvpAsUInt32(Avp.VENDOR_ID);
    }

    public void setVendorId(long vendorId) {
        setAvpAsUInt32(Avp.VENDOR_ID, vendorId,  false);
    }

    public void setVendorIds(long[] vendorIds) {
        for (long i : vendorIds)
            setAvpAsUInt32(Avp.VENDOR_ID, i,  false);
    }

    public boolean hasAuthApplicationId() {
        return avpSet.getAvp(Avp.AUTH_APPLICATION_ID) != null;
    }

    public long getAuthApplicationId() {
        return getAvpAsUInt32(Avp.AUTH_APPLICATION_ID);
    }

    public void setAuthApplicationId(long authApplicationId) {
        setAvpAsUInt32(Avp.AUTH_APPLICATION_ID, authApplicationId, true);
    }

    public boolean hasAcctApplicationId() {
        return avpSet.getAvp(Avp.ACCT_APPLICATION_ID) != null;
    }

    public long getAcctApplicationId() {
        return getAvpAsUInt32(Avp.ACCT_APPLICATION_ID);
    }

    public void setAcctApplicationId(long acctApplicationId) {
        setAvpAsUInt32(Avp.ACCT_APPLICATION_ID, acctApplicationId, true);
    }
}
