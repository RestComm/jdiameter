package org.mobicents.diameter.extension;

//import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.mobicents.diameter.stack.DiameterStackMultiplexerAS7;

import javax.management.MBeanServer;
import javax.management.ObjectName;

public class DiameterMuxService implements Service<DiameterMuxService> {

    //private final Logger log = Logger.getLogger(DiameterMuxService.class);

    public static ServiceName getServiceName() {
        return ServiceName.of("restcomm","diameter-mux");
    }

    private final InjectedValue<MBeanServer> mbeanServer = new InjectedValue<MBeanServer>();
    public InjectedValue<MBeanServer> getMbeanServer() {
        return mbeanServer;
    }

    private DiameterStackMultiplexerAS7 diameterMuxBean;

    @Override
    public DiameterMuxService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
        //log.info("Starting DiameterMuxService");

        diameterMuxBean = new DiameterStackMultiplexerAS7();
        //try {
            //StandardMBean standardMBean =
            //        new StandardMBean(diameterMuxBean, DiameterStackMultiplexerAS7MBean.class);
            registerMBean(diameterMuxBean, DiameterStackMultiplexerAS7.OBJECT_NAME);
        //} catch (NotCompliantMBeanException e) {
        //    e.printStackTrace();
        //} finally {
            diameterMuxBean.startService();
        //}
    }

    @Override
    public void stop(StopContext context) {
        //log.info("Stopping DiameterMuxService");

        diameterMuxBean.stopService();
        unregisterMBean(DiameterStackMultiplexerAS7.OBJECT_NAME);
    }

    private void registerMBean(Object mBean, String name) throws StartException {
        try {
            getMbeanServer().getValue().registerMBean(mBean, new ObjectName(name));
        } catch (Throwable e) {
            throw new StartException(e);
        }
    }

    private void unregisterMBean(String name) {
        try {
            getMbeanServer().getValue().unregisterMBean(new ObjectName(name));
        } catch (Throwable e) {
            //log.error("failed to unregister mbean", e);
        }
    }
}
