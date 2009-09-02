=============================================
 Mobicents Diameter : Cx/Dx Resource Adaptor
=============================================
                              v@cx-dx_VERSION@

 Welcome to the Mobicents Diameter Cx/Dx Resource Adaptor v@cx-dx_VERSION@ release!

--------------
 Introduction
--------------

 This is the Cx/Dx RA release of Mobicents Diameter solution.

 This RA provides Cx/Dx interface application implementation of Diameter for JAIN SLEE and depends on 
 the Mobicents Diameter Mux and Base RA to be deployed before using it.

 This interface allows the communication between CSCF and HSS (Cx) and CSCF and SLF (Dx) and it is defined
 in 3GPP TS 29.228 & TS 29.229.

------------------
 Release Contents
------------------

  diameter-cxdx-ra-DU-@cx-dx_VERSION@.jar (the JAIN SLEE Deployable Unit to be installed in Mobicents)

  du-management.xml (the auxiliar build file for managing deployment/undeployment)

  build.xml (the ant build file for deploying/undeploying the RA)

----------------
 How to Use it?
----------------

 In order to deploy/undeploy and correctly activate/deactivate the Resource Adaptor, you can:

 1) Simply copy the DU jar to the deploy folder of the JBoss running Mobicents or delete it from the deploy folder to
    uninstall it;

 2) Use the provided build.xml file and call the desired targets:
    - deploy/deploy-jmx for hard deploying or deploying via JMX interface;
    - undeploy/undeploy-jmx for hard undeploying or undeploying via JMX interface;

------------------
 More Information
------------------

 For more detailed information, please visit the following sites:

  » Mobicents Diameter Cx/Dx (RA & Examples) Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter-cxdx)

  » Mobicents Diameter Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter)

  » jDiameter Homepage (http://jdiameter.dev.java.net)

  » Mobicents Google Group, for posting questions (http://groups.google.com/group/mobicents-public)

  » Mobicents Homepage (http://www.mobicents.org)