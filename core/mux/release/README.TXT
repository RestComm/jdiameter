==========================
 Mobicents Diameter : Mux
==========================
              v@MUX_VERSION@

 Welcome to the Mobicents Diameter Mux v@MUX_VERSION@ release!

--------------
 Introduction
--------------

 This is the Multiplexer release of Mobicents Diameter solution.

 This multiplexer (Mux) is intended to provide aplications with ease of development, and in the future to work as a
 wrapper to the stack in order to abstract the stack being used.

 At the moment, it enables the application to register itself on the Mux for the desired Diameter Application and
 receive and send messages for that same application. With the multiplexer you can use the same Diameter implementation
 in Mobicents JAIN SLEE or Mobicents SIP Servlets.

 It also provides a Diameter dictionary which application can lookup at in order to correctly create their Diameter
 Messages and AVPs.

------------------
 Release Contents
------------------

  mobicents-diameter-mux-jar-@MUX_VERSION@.jar (contains the compiled classes for the Mux)

  mobicents-diameter-mux-sar-@MUX_VERSION@.sar (deployable SAR for JBoss AS 4.x, with the jar as a lib)

----------------
 How to Use it?
----------------

 In order to use it, drop the SAR in the deploy folder of JBoss AS.

 You can configure your Diameter settings by editing the jdiameter-config.xml file in the Mux JAR located in the lib
 folder (org\mobicents\diameter\stack\jdiameter-config.xml). For more information on how to configure it please refer
 to the Mobicents Diameter Page or jDiameter Homepage.

------------------
 More Information
------------------

 For more detailed information, please visit the following sites:

  » Mobicents Diameter Mux Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter-core)

  » Mobicents Diameter Page (http://groups.google.com/group/mobicents-public/web/mobicents-diameter)

  » jDiameter Homepage (http://jdiameter.dev.java.net)

  » Mobicents Google Group, for posting questions (http://groups.google.com/group/mobicents-public)

  » Mobicents Homepage (http://www.mobicents.org)