=======================================================================
 Mobicents Diameter : Credit-Control Application (CCA) Resource Adaptor
=======================================================================
                           v@cca_VERSION@

 Welcome to the Mobicents Diameter CCA Resource Adaptor v@cca_VERSION@ release!

--------------
 Introduction
--------------

 This is the CCA RA release of Mobicents Diameter solution.

 This RA provides CCA interface application (RFC 4006) implementation of Diameter for JAIN SLEE and depends on the Mobicents
 Diameter Mux and Base RA to be deployed before using it.

 The RA provides a framework for real-time charging, primarily meant for the communication between gateways/control-points
 and the back-end account/balance systems.

------------------
 Release Contents
------------------

  diameter-cca-ra-DU-@cca_VERSION@.jar (the JAIN SLEE Deployable Unit to be installed in Mobicents)

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

  » Mobicents Diameter CCA (RA & Examples) Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter-cca)

  » Mobicents Diameter Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter)

  » jDiameter Homepage (http://jdiameter.dev.java.net)

  » Mobicents Google Group, for posting questions (http://groups.google.com/group/mobicents-public)

  » Mobicents Homepage (http://www.mobicents.org)

------------------
 Note
------------------

 This RA must nto be deployed along with Ro