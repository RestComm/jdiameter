=====================================
 Mobicents Diameter : OpenIMS Example
=====================================
                v@openims_EXAMPLE_VERSION@

 Welcome to the Mobicents Diameter OpenIMS Example v@openims_EXAMPLE_VERSION@ release!

--------------
 Introduction
--------------

 This is the OpenIMS Example release of Mobicents Diameter solution.

 This example demonstrates the integration between Mobicents Diameter and OpenIMS.

 It depends on Mobicents Diameter Mux, Base RA, Sh-Client RA, SIP11 RA and SIP Services Proxy with initial invite to be 
 deployed before using it.

 The idea is to provide a service that will let a user, who has been offline, know who called him while he was offline.
 To do that our service will be a proxy for SIP messages in the AS so he can capture the 404 sent by the CSCF when a call
 is made to him, while the user is offline. It will also register on the HSS for receiving changes to the User-IMS-State,
 using the proper Diameter Sh message, Profile-Notification-Request.

 When a call to a non-registered user happens, data is stored, regarding the person who called and the time of the call.
 When the user becomes online (registers it’s SIP phone), the HSS will fire a notification with the new user state, and
 by that time we will look into our map to find if the user has missed any call while offline. If so, the user will
 receive a message, for each missed call, stating:

  “UserXXX <sip:xxx@open-ims.test> tried to call you on dd/mm/yyyy at hh:mm:ss”.

 For more detailed information on the examples, please visit the Mobicents Diameter Page.

------------------
 Release Contents
------------------

  diameter-openims-example-DU-1.0.0.BETA2.jar (the JAIN SLEE Deployable Unit to be installed in Mobicents)

  du-management.xml (the auxiliar build file for managing deployment/undeployment)

  build.xml (the ant build file for deploying/undeploying the RA)
  
  thirdparty  directory with thir party deps that are required (SIP)

----------------
 How to Use it?
----------------

 In order to deploy/undeploy and correctly activate/deactivate it, you can:

 1) Simply copy the DU jar to the deploy folder of the JBoss running Mobicents or delete it from the deploy folder to
    uninstall it.

 2) Use the provided build.xml file and call the desired targets:
    - deploy-all/deploy-all-jmx for hard deploying or deploying via JMX interface (along with dependencies);
    - undeploy-all/undeploy-all-jmx for hard undeploying or undeploying via JMX interface (along with dependencies);

------------------
 More Information
------------------

 For more detailed information, please visit the following sites:

  » Mobicents Diameter OpenIMS Example Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter-sh-openims)

  » Mobicents Diameter Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter)

  » jDiameter Homepage (http://jdiameter.dev.java.net)

  » Mobicents Google Group, for posting questions (http://groups.google.com/group/mobicents-public)

  » Mobicents Homepage (http://www.mobicents.org)