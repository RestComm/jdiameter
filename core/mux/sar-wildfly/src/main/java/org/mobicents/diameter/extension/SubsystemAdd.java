package org.mobicents.diameter.extension;

import java.util.List;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.jmx.MBeanServerService;
import org.jboss.dmr.ModelNode;
//import org.jboss.logging.Logger;
import org.jboss.msc.service.ServiceController;

import org.jboss.msc.service.ServiceName;

import javax.management.MBeanServer;

/**
 * Handler responsible for adding the subsystem resource to the model
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
class SubsystemAdd extends AbstractBoottimeAddStepHandler {

    static final SubsystemAdd INSTANCE = new SubsystemAdd();

    //private final Logger log = Logger.getLogger(SubsystemAdd.class);

    private SubsystemAdd() {
    }

    /** {@inheritDoc} */
    @Override
    protected void populateModel(ModelNode operation, ModelNode model) throws OperationFailedException {
        //log.info("Populating the model");
        model.setEmptyObject();
    }

    /** {@inheritDoc} */
    @Override
    public void performBoottime(OperationContext context, ModelNode operation, ModelNode model,
            ServiceVerificationHandler verificationHandler, List<ServiceController<?>> newControllers)
            throws OperationFailedException {

        // Install service with MBean SleeConnectionTest

        DiameterMuxService service = new DiameterMuxService();
        ServiceName name = DiameterMuxService.getServiceName();
        ServiceController<DiameterMuxService> controller = context.getServiceTarget()
                .addService(name, service)
                .addDependency(MBeanServerService.SERVICE_NAME, MBeanServer.class, service.getMbeanServer())
                .addListener(verificationHandler)
                .setInitialMode(ServiceController.Mode.ACTIVE)
                .install();
        newControllers.add(controller);

    }
}
