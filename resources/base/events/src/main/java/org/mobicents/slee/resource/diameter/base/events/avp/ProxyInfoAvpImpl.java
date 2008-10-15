package org.mobicents.slee.resource.diameter.base.events.avp;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentityAvp;
import net.java.slee.resource.diameter.base.events.avp.ProxyInfoAvp;
import static org.jdiameter.api.Avp.PROXY_HOST;
import static org.jdiameter.api.Avp.PROXY_STATE;
import org.jdiameter.api.AvpDataException;

/**
 * 
 * Super project:  mobicents
 * 12:56:45 2008-05-08	
 * @author Eric Svenson
 */
public class ProxyInfoAvpImpl extends GroupedAvpImpl implements ProxyInfoAvp {


    public ProxyInfoAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
        super(code, vendorId, mnd, prt, value);
        name = "Proxy-Info-Avp";
    }

    public boolean hasProxyHost() {
        return avpSet.getAvp(PROXY_HOST) != null;
    }

    public DiameterIdentityAvp getProxyHost() {
        return getAvpAsIdentity(PROXY_HOST);
    }

    public void setProxyHost(DiameterIdentityAvp proxyHost) {
        setAvpAsIdentity(PROXY_HOST, proxyHost.toString(), true, true, false);
    }

    public boolean hasProxyState() {
        return avpSet.getAvp(PROXY_STATE) != null;
    }

    public byte[] getProxyState() {
        try {
            return avpSet.getAvp(PROXY_STATE).getRaw();
        } catch (AvpDataException e) {
            log.debug(e);
            return null;
        }
    }

    public void setProxyState(byte[] proxyState) {
        setAvpAsByteArray(PROXY_STATE, byteArrayValue(), true);
    }

    public DiameterAvp[] getExtensionAvps() {
        return getExtensionAvps();
    }

    public void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException {
        setExtensionAvps(avps);
    }
}
