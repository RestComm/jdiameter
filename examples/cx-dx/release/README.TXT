===================================
 Mobicents Diameter : Cx/Dx Example
===================================
                      v@cx-dx_EXAMPLE_VERSION@

 Welcome to the Mobicents Diameter Cx/Dx Example v@cx-dx_EXAMPLE_VERSION@ release!

--------------
 Introduction
--------------

 This is the Cx/Dx Example release of Mobicents Diameter solution.

 This example demonstrates the usage of basic Cx/Dx messages in server and client mode, using two different Diameter instances.
 It depends on Mobicents Diameter Mux, Base RA and Cx/Dx RA to be deployed before using it.

 For more detailed information on the examples, please visit the Mobicents Diameter Page.

------------------
 Release Contents
------------------

  diameter-cx-dx-example-DU-@cx-dx_EXAMPLE_VERSION@.jar (the JAIN SLEE Deployable Unit to be installed in Mobicents)

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

  » Mobicents Diameter User Guide (http://hudson.jboss.org/hudson/view/Mobicents/job/MobicentsBooks/lastSuccessfulBuild/artifact/diameter/index.html)

  » Mobicents Diameter Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter)

  » jDiameter Homepage (http://jdiameter.dev.java.net)

  » Mobicents Google Group, for posting questions (http://groups.google.com/group/mobicents-public)

  » Mobicents Homepage (http://www.mobicents.org)