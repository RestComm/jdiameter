package org.mobicents.slee.resources.diameter.tests.factories;

import java.util.ArrayList;
import java.util.Arrays;

public class TestGenerator {

  static String[] shortNames = {"ULR","ULA",
    "AIR","AIA",
    "CLR","CLA",
    "IDR","IDA",
    "DSR","DSA",
    "PUR","PUA",
    "RSR","RSA",
    "NOR","NOA"
  };

  static String[] longNames = {"Update-Location-Request","Update-Location-Answer",
    "Authentication-Information-Request","Authentication-Information-Answer",
    "Cancel-Location-Request","Cancel-Location-Answer",
    "Insert-Subscriber-Data-Request","Insert-Subscriber-Data-Answer",
    "Delete-Subscriber-Data-Request","Delete-Subscriber-Data-Answer",
    "Purge-UE-Request","Purge-UE-Answer",
    "Reset-Request","Reset-Answer",
    "Notify-Request","Notify-Answer"
  };

  static String[] clientSessionAnswers = {"CLA","IDA","DSA","RSA"};
  static String[] serverSessionAnswers = {"ULA","AIA","PUA","NOA"};

  static String msgFact = "s6aMessageFactory";

  static String avpFact = "s6aAvpFactory";

  public static void main(String[] args) {
    //generateMessagesTests();
    generateAVPTests();
  }
  
  static String[] avpClassesNames = {
    "ActiveAPNAvp",
    "AMBRAvp",
    "APNConfigurationAvp",
    "APNConfigurationProfileAvp",
    "AllocationRetentionPriorityAvp",
    "AuthenticationInfoAvp",
    "EPSLocationInformationAvp",
    "EPSSubscribedQoSProfileAvp",
    "EPSUserStateAvp",
    "EUTRANVectorAvp",
    "MIP6AgentInfoAvp",
    "MIPHomeAgentHostAvp",
    "MMELocationInformationAvp",
    "MMEUserStateAvp",
    "RequestedEUTRANAuthenticationInfoAvp",
    "RequestedUTRANGERANAuthenticationInfoAvp",
    "SGSNLocationInformationAvp",
    "SGSNUserStateAvp",
    "SpecificAPNInfoAvp",
    "SubscriptionDataAvp",
    "SupportedFeaturesAvp",
    "TerminalInformationAvp"    
  }; 
  
  public static void generateAVPTests() {
    for(String avpClass : avpClassesNames) {
      String avpName = avpClass.replace("Avp", "");
      System.out.println("  @Test");
      System.out.println("  public void testGettersAndSetters" + avpName + "() throws Exception {");
      System.out.println("    " + avpClass + " avp = s6aAvpFactory.create" + avpName + "();");
      System.out.println("");
      System.out.println("    int nFailures = AvpAssistant.testMethods(avp, " + avpClass + "Impl.class);");
      System.out.println("");
      System.out.println("    assertTrue(\"Some methods have failed. See logs for more details.\", nFailures == 0);");
      System.out.println("  }");
      System.out.println("");
    }
  }
  
  public static void generateMessagesTests() {
    for(int i = 0; i < shortNames.length; i++) {
      String longName = longNames[i];
      String longNameReq = longName.replaceFirst("Answer", "Request");
      String longNameClass = longNames[i].replaceAll("-", "");
      String longNameReqClass = longNameClass.replaceFirst("Answer", "Request");
      String shortName = shortNames[i];
      String shortNameSmall = shortNames[i].toLowerCase();
      char[] tmp = shortNameSmall.toCharArray();
      tmp[2] = 'r';
      String shortNameReqSmall = new String(tmp);

      // For answers only...
      String sessionInstance = Arrays.asList(serverSessionAnswers).contains(shortName) ? "serverSession" : "clientSession";

      boolean useReqInAnsCreation = false;
      
      boolean req = (i % 2 == 0);
      if(req) {
        // @Test
        // public void isRequestULR() throws Exception {
        //   UpdateLocationRequest ulr = s6aMessageFactory.createUpdateLocationRequest();
        //   assertTrue("Request Flag in Update-Location-Request is not set.", ulr.getHeader().isRequest());
        // }

        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void isRequest" + shortName + "() throws Exception {");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + msgFact + ".create" + longNameClass + "();");
        System.out.println("");
        System.out.println("  assertTrue(\"Request Flag in " + longName + " is not set.\", " + shortNameSmall + ".getHeader().isRequest());");
        System.out.println("}");

        // @Test
        // public void isProxiableLIR() throws Exception {
        //   LocationInfoRequest lir = s6aMessageFactory.createLocationInfoRequest();
        //   assertTrue("The 'P' bit is not set by default in Location-Info-Request, it should.", lir.getHeader().isProxiable());
        // }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void isProxiable" + shortName + "() throws Exception {");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + msgFact + ".create" + longNameClass + "();");
        System.out.println("  assertTrue(\"The 'P' bit is not set by default in " + longName + " it should.\", " + shortNameSmall + ".getHeader().isProxiable());");
        System.out.println("}");

        // @Test
        // public void testGettersAndSettersLIR() throws Exception {
        //   LocationInfoRequest lir = s6aMessageFactory.createLocationInfoRequest();
        //
        //   int nFailures = AvpAssistant.testMethods(lir, LocationInfoRequest.class);
        //
        //   assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
        // }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void testGettersAndSetters" + shortName + "() throws Exception {");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + msgFact + ".create" + longNameClass + "();");
        System.out.println("");
        System.out.println("  int nFailures = AvpAssistant.testMethods(" + shortNameSmall + ", " + longNameClass + ".class);");
        System.out.println("");
        System.out.println("  assertTrue(\"Some methods have failed. See logs for more details.\", nFailures == 0);");
        System.out.println("}");
      }
      else {
        // @Test
        // public void isAnswerLIA() throws Exception {
        //   serverSession.fetchSessionData(s6aMessageFactory.createLocationInfoRequest());
        //   LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
        //
        //   assertFalse("Request Flag in Location-Info-Answer is set.", lia.getHeader().isRequest());
        // }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void isAnswer" + shortName + "() throws Exception {");
        System.out.println("  " + longNameReqClass + " " + shortNameReqSmall + " = " + msgFact + ".create" + longNameReqClass + "();");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "(" + (useReqInAnsCreation ? shortNameReqSmall : "") + ");");
        System.out.println("");
        System.out.println("  assertFalse(\"Request Flag in " + longName + " is set.\", " + shortNameSmall + ".getHeader().isRequest());");
        System.out.println("}");

        // @Test
        // public void testGettersAndSettersLIA() throws Exception {
        //   serverSession.fetchSessionData(s6aMessageFactory.createLocationInfoRequest());
        //   LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
        //
        //   int nFailures = AvpAssistant.testMethods(lia, LocationInfoAnswer.class);
        //
        //   assertTrue("Some methods have failed. See logs for more details.", nFailures == 0);
        // }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void testGettersAndSetters" + shortName + "() throws Exception {");
        System.out.println("  " + longNameReqClass + " " + shortNameReqSmall + " = " + msgFact + ".create" + longNameReqClass + "();");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "(" + (useReqInAnsCreation ? shortNameReqSmall : "") + ");");
        System.out.println("");
        System.out.println("  int nFailures = AvpAssistant.testMethods(" + shortNameSmall + ", " + longNameClass + ".class);");
        System.out.println("");
        System.out.println("  assertTrue(\"Some methods have failed. See logs for more details.\", nFailures == 0);");
        System.out.println("}");

        //      @Test
        //      public void hasDestinationHostLIA() throws Exception {
        //        serverSession.fetchSessionData(s6aMessageFactory.createLocationInfoRequest());
        //        LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
        //
        //        assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", lia.getDestinationHost());
        //      }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void hasDestinationHost" + shortName + "() throws Exception {");
        System.out.println("  " + longNameReqClass + " " + shortNameReqSmall + " = " + msgFact + ".create" + longNameReqClass + "();");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "(" + (useReqInAnsCreation ? shortNameReqSmall : "") + ");");
        System.out.println("");
        System.out.println("  assertNull(\"The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]\", " + shortNameSmall + ".getDestinationHost());");
        System.out.println("}");

        //      @Test
        //      public void hasDestinationRealmLIA() throws Exception {
        //        serverSession.fetchSessionData(s6aMessageFactory.createLocationInfoRequest());
        //        LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
        //
        //        assertNull("The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]", lia.getDestinationRealm());
        //      }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void hasDestinationRealm" + shortName + "() throws Exception {");
        System.out.println("  " + longNameReqClass + " " + shortNameReqSmall + " = " + msgFact + ".create" + longNameReqClass + "();");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "(" + (useReqInAnsCreation ? shortNameReqSmall : "") + ");");
        System.out.println("");
        System.out.println("  assertNull(\"The Destination-Host and Destination-Realm AVPs MUST NOT be present in the answer message. [RFC3588/6.2]\", " + shortNameSmall + ".getDestinationRealm());");
        System.out.println("}");

        //      @Test
        //      public void isProxiableCopiedLIA() throws Exception {
        //        LocationInfoRequest lir = s6aMessageFactory.createLocationInfoRequest();
        //        serverSession.fetchSessionData(lir);
        //        LocationInfoAnswer lia = serverSession.createLocationInfoAnswer();
        //        assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", lir.getHeader().isProxiable(), lia.getHeader().isProxiable());
        //
        //        // Reverse 'P' bit ...
        //        ((DiameterMessageImpl) lir).getGenericData().setProxiable(!lir.getHeader().isProxiable());
        //        assertTrue("The 'P' bit was not modified in Location-Info-Request, it should.", lir.getHeader().isProxiable() != lia.getHeader().isProxiable());
        //        serverSession.fetchSessionData(lir);
        //
        //        lia = serverSession.createLocationInfoAnswer();
        //        assertEquals("The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]", lir.getHeader().isProxiable(), lia.getHeader().isProxiable());
        //      }
        System.out.println("");
        System.out.println("@Test");
        System.out.println("public void isProxiableCopied" + shortName + "() throws Exception {");
        System.out.println("  " + longNameReqClass + " " + shortNameReqSmall + " = " + msgFact + ".create" + longNameReqClass + "();");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("  " + longNameClass + " " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "(" + (useReqInAnsCreation ? shortNameReqSmall : "") + ");");
        System.out.println("  assertEquals(\"The 'P' bit is not copied from request in Location-Info-Answer, it should. [RFC3588/6.2]\", " + shortNameReqSmall + ".getHeader().isProxiable(), " + shortNameSmall + ".getHeader().isProxiable());");
        System.out.println("");
        System.out.println("  // Reverse 'P' bit ...");
        System.out.println("  ((DiameterMessageImpl) " + shortNameReqSmall + ").getGenericData().setProxiable(!" + shortNameReqSmall + ".getHeader().isProxiable());");
        System.out.println("  assertTrue(\"The 'P' bit was not modified in " + longNameReq + ", it should.\",  " + shortNameReqSmall + ".getHeader().isProxiable() != " + shortNameSmall + ".getHeader().isProxiable());");
        System.out.println("  " + sessionInstance + ".fetchSessionData(" + shortNameReqSmall + ");");
        System.out.println("");
        System.out.println("  " + shortNameSmall + " = " + sessionInstance + ".create" + longNameClass + "();");
        System.out.println("  assertEquals(\"The 'P' bit is not copied from request in " + longName + ", it should. [RFC3588/6.2]\", " + shortNameReqSmall + ".getHeader().isProxiable(), " + shortNameSmall + ".getHeader().isProxiable());");
        System.out.println("}");
      }
    }
  }

}
