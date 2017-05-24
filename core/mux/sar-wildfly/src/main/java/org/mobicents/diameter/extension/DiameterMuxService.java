package org.mobicents.diameter.extension;

//import org.jboss.logging.Logger;
import org.jboss.msc.service.*;
import org.jboss.msc.value.InjectedValue;
import org.mobicents.diameter.stack.DiameterStackMultiplexer;

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

    private DiameterStackMultiplexer diameterMuxBean;

    @Override
    public DiameterMuxService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }

    @Override
    public void start(StartContext context) throws StartException {
        //log.info("Starting DiameterMuxService");

        diameterMuxBean = new DiameterStackMultiplexer();
        try {
            registerMBean(diameterMuxBean, DiameterStackMultiplexer.OBJECT_NAME);
            diameterMuxBean.create();
            diameterMuxBean.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(StopContext context) {
        //log.info("Stopping DiameterMuxService");

        diameterMuxBean.stop();
        unregisterMBean(DiameterStackMultiplexer.OBJECT_NAME);
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
