package org.mobicents.diameter.framework;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnDiameterURI;
import static org.jdiameter.client.impl.helpers.Parameters.OwnIPAddress;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.OwnVendorID;
import static org.jdiameter.client.impl.helpers.Parameters.PeerName;
import static org.jdiameter.client.impl.helpers.Parameters.PeerRating;
import static org.jdiameter.client.impl.helpers.Parameters.PeerTable;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Mode;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.Session;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.StackType;
import org.jdiameter.client.api.parser.ParseException;
import org.jdiameter.client.impl.helpers.EmptyConfiguration;
import org.jdiameter.client.impl.helpers.Loggers;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.jdiameter.client.impl.parser.MessageParser;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestingFramework
{
  
  private static int timeout = 30 * 1000;

  private static String clientHost = "127.0.0.1";
  private static String clientPort = "13868";
  private static String clientURI  = "aaa://" + clientHost + ":" + clientPort;
  
  private static String serverHost = "127.0.0.1";
  private static String serverPort = "3868";
  private static String serverURI = "aaa://" + serverHost + ":" + serverPort;
  
  private static String realmName = "mobicents.org";
  
  private static AvpDictionary avpDictionary = AvpDictionary.INSTANCE;
  
  private ArrayList<Message> receivedMessages = new ArrayList<Message>();
  
  private static Stack stack;
  private static SessionFactory factory;
  
  // The AVPs that may be pre-filled in message.
  private final int[] prefilledAVPs = new int[]{Avp.DESTINATION_HOST, Avp.DESTINATION_REALM, Avp.ORIGIN_HOST, Avp.ORIGIN_REALM, Avp.SESSION_ID, Avp.VENDOR_SPECIFIC_APPLICATION_ID};
  
  protected MessageParser parser = new MessageParser();
  
  private boolean printAVPs = true;
  
  static 
  {
//    String name = "localhost";
//    
//    try
//    {
//      name = InetAddress.getLocalHost().getHostName();
//    }
//    catch (UnknownHostException ignore)
//    {
//      // ignore this... use localhost
//    }
//    
//    serverHost = name;
//    serverURI = "aaa://" + serverHost + ":" + serverPort;
  } 

  private void initStack()
  {
    log("Initializing Stack...");
    try
    {
      avpDictionary.parseDictionary(this.getClass().getClassLoader().getResourceAsStream("dictionary.xml"));
      log("AVP Dictionary successfully parsed.");
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    try
    {
      stack = new org.jdiameter.client.impl.StackImpl();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      stack.destroy();
      return;
    }

    try
    {
      factory = stack.init(new MyConfiguration());
      log("Stack Configuration successfully loaded.");
    }
    catch (Exception e) 
    {
      e.printStackTrace();
      stack.destroy();
      return;
    }

    MetaData metaData = stack.getMetaData();
    if (metaData.getStackType() != StackType.TYPE_CLIENT || metaData.getMinorVersion() <= 0)
    {
      stack.destroy();
      logError("Incorrect driver");
      return;
    }

    // Set logger levels
    Loggers.Stack.logger().setLevel(Level.OFF);
    ConsoleHandler fh = new ConsoleHandler();
    fh.setLevel(Level.ALL);
    stack.getLogger().addHandler(fh);
    stack.getLogger().setUseParentHandlers(false);
    log("Stack Logging successfully initialized.");

    try
    {
      log("Connecting to Peers...");
      stack.start(Mode.ALL_PEERS, 20, TimeUnit.SECONDS);
      log("Connection to Peers successfully completed.");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      stack.destroy();
      return;
    }
    
    log("Stack initialization successfully completed.");
  }

  public void executeTest(InputStream is)
  {
    try
    {
      assertNotNull("InputStream must exist.", is);
      
      initStack();
  
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(is);
      
      doc.getDocumentElement().normalize();    
  
      NodeList scenarioNodes = doc.getChildNodes();
      
      for(int i = 0; i < scenarioNodes.getLength(); i++)
      {
        Node scenarioNode = scenarioNodes.item(i);
        
        if (scenarioNode.getNodeType() == Node.ELEMENT_NODE)
        {
          ArrayList<Element> actions = parseScenario( (Element) scenarioNode );
          
          for(Element action : actions)
          {
            ArrayList<AvpSet> messagesAVPs = parseAction(action);
            
            // Add AVPs
            executeAction(action, (Element) action.getElementsByTagName("command").item(0), messagesAVPs);
          }
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
    finally
    {
      if(stack != null)
        stopStack();
    }
  }
  
  private void stopStack()
  {
    try
    {
      log("Stopping Stack...");
      stack.stop(10, TimeUnit.SECONDS, DisconnectCause.REBOOTING);
      log("Stack Stopped Successfully.");
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    stack.destroy();
    
    try
    {
      // Let it stop... useful for JUnit tests
      Thread.sleep( 1000 );
    }
    catch ( InterruptedException e )
    {
      e.printStackTrace();
    }
  }

  /**
   * Method for executing the scenario actions
   * @param action the action Element to be executed (send or receive)
   * @param command the message Element to send or receive (eg. ACR)
   * @param avpSet an ArrayList of AvpSet defining the AVPs to be sent in message
   * @throws InternalException
   * @throws IllegalDiameterStateException
   * @throws RouteException
   * @throws OverloadException
   * @throws AvpDataException
   */
  private void executeAction(Element action, Element command, ArrayList<AvpSet> avpSet) throws InternalException, IllegalDiameterStateException, RouteException, OverloadException, AvpDataException
  {
    MySessionEventListener listener = new MySessionEventListener();
    
    if(action.getNodeName().equals("send"))
    {
      Session session = null;
      
      for(AvpSet avps : avpSet)
      {
        log("Sending Message...");

        int commandCode = command.getAttribute("code").equals("") ? getCommandFromString(command.getAttribute("name")) : Integer.valueOf(command.getAttribute("code"));
        
        if(session == null)
        {
          if(avps.getAvp(Avp.SESSION_ID) != null)
            session = factory.getNewSession(avps.getAvp(Avp.SESSION_ID).getUTF8String());
          else
            session = factory.getNewSession();
        }
        else if(avps.getAvp(Avp.SESSION_ID) != null && !session.getSessionId().equals(avps.getAvp(Avp.SESSION_ID).getUTF8String()))
        {
          session = factory.getNewSession(avps.getAvp(Avp.SESSION_ID).getUTF8String());
        }
        
        Request msg = session.createRequest(
            commandCode,
            org.jdiameter.api.ApplicationId.createByAccAppId(193, 19302),
            realmName,
            serverHost
        );
        
        String messageFlags = command.getAttribute("flags");
        
        if(messageFlags != null && !messageFlags.equals(""))
        {
          msg.setRequest( messageFlags.contains("request") );
          msg.setProxiable( messageFlags.contains("proxiable") );
          msg.setError( messageFlags.contains("error") );
          msg.setReTransmitted( messageFlags.contains("retransmitted") );
        }
        
        // Set hop-by-hop and end-to-end if present
        String h2h = command.getAttribute("hop-by-hop");
        
        if(h2h != null && h2h.length() > 0) {
          // Adding as negative so it becomes immutable...
          ((MessageImpl) msg).setHopByHopIdentifier(-Long.valueOf(h2h));
        }
        
        String e2e = command.getAttribute("end-to-end");
        
        if(e2e != null && e2e.length() > 0) {
          ((MessageImpl) msg).setEndToEndIdentifier(Long.valueOf(e2e));
        }
        
        AvpSet msgAVPs = msg.getAvps();
        
        // In case we want to override pre-filled AVPs
        for(int prefilledAVP : prefilledAVPs)
        {
          if(avps.getAvp(prefilledAVP) != null) {
            msgAVPs.removeAvp(prefilledAVP);
          }
        }
        
        msgAVPs.addAvp( avps );
        
        session.send(msg, listener);
        
        if(printAVPs)
          printAvps( msg.getAvps() );
        
        log("Message sent.");
        
        long startTime = System.currentTimeMillis();
        try
        {
          synchronized(TestingFramework.this)
          {
            if(!listener.hasAnswer()) {
              TestingFramework.this.wait(timeout + 1000);
            }
          }
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        long stopTime = System.currentTimeMillis();
        
        if (listener.hasAnswer())
        {
          log("Processing time: " + (stopTime - startTime) + "ms");
        }
      }
    }
    else if(action.getNodeName().equals("receive"))
    {
      for(AvpSet avps : avpSet)
      {
        if(receivedMessages.size() > 0)
        {
          log("Checking received Message...");
          Message receivedMessage = receivedMessages.remove(0);
          
          if(printAVPs)
            printAvps( receivedMessage.getAvps() );
          
          AvpSet receivedAvps = receivedMessage.getAvps();
          
          int expectedCommandCode = command.getAttribute("code").equals("") ? getCommandFromString(command.getAttribute("name")) : Integer.valueOf(command.getAttribute("code")); 
          
          assertTrue( "Unexpected type of Message: Received[" + receivedMessage.getCommandCode() + "] Expected[" + expectedCommandCode + "]",
              receivedMessage.getCommandCode() == expectedCommandCode );
          
          // Verify hop-by-hop and end-to-end if present
          String h2h = command.getAttribute("hop-by-hop");
          
          if(h2h != null && h2h.length() > 0) {
            assertTrue( "Unexpected Hop-By-Hop Identifier: Received[" + receivedMessage.getHopByHopIdentifier() + "] Expected[" + Long.valueOf(h2h) + "]",
                receivedMessage.getHopByHopIdentifier() == Long.valueOf(h2h));
          }
          
          String e2e = command.getAttribute("end-to-end");
          
          if(e2e != null && e2e.length() > 0) {
            assertTrue( "Unexpected End-To-End Identifier: Received[" + receivedMessage.getEndToEndIdentifier() + "] Expected[" + Long.valueOf(e2e) + "]",
                receivedMessage.getEndToEndIdentifier() == Long.valueOf(e2e));
          }
          
          for(Avp expectedAvp : avps)
          {
            Avp receivedAvp = null;
            
            assertTrue( "Missing expected AVP: Vendor-Id[" + expectedAvp.getVendorId() + "] Code[" + expectedAvp.getCode() + "]",
                (receivedAvp = receivedAvps.getAvp( expectedAvp.getCode(), expectedAvp.getVendorId()) ) != null );
              
            compareAvps(expectedAvp, receivedAvp);
          }
          
          log("Finished checking message. All Good!");
        }
        else
        {
          log("No message was received... FAILED!");
          assertTrue( "No Message was received. Failing.", false);
        }
      }
    }
  }

  /**
   * Compares two AVPs for their value. In case of Grouped AVPs, works recursively.
   * @param expectedAvp the expected AVP
   * @param receivedAvp the received AVP
   * @throws AvpDataException
   */
  private void compareAvps(Avp expectedAvp, Avp receivedAvp) throws AvpDataException
  {
    if( avpDictionary.getAvp( expectedAvp.getCode(), expectedAvp.getVendorId()).getType().equals( "Grouped" ) )
    {
      for(Avp subExpectedAvp : expectedAvp.getGrouped())
      {
        Avp subReceivedAvp = receivedAvp.getGrouped().getAvp( subExpectedAvp.getCode(), subExpectedAvp.getVendorId() );
        
        compareAvps( subExpectedAvp, subReceivedAvp );
      }
    }
    else
    {
      assertTrue("Expected AVP Value differs: Vendor-Id[" + expectedAvp.getVendorId() + "] Code[" + expectedAvp.getCode() + "] " +
          "Received[" + getAvpValue(receivedAvp) + "] Expected[" + getAvpValue(expectedAvp) + "]",
          Arrays.equals(receivedAvp.getRaw(), expectedAvp.getRaw()));
    }
  }    
  
  /**
   * Method for parsing an action element and get the corresponding AVPs to be used
   * @param action the Element with the action
   * @return an ArrayList of AvpSet to be used in the action
   * @throws InternalException
   */
  private ArrayList<AvpSet> parseAction(Element action) throws InternalException
  {
    log("Parsing Action '" + action.getNodeName() + "'...");
    
    ArrayList<AvpSet> messages = new ArrayList<AvpSet>();
    
    NodeList commandNodes = action.getElementsByTagName("command");
    
    for(int i = 0; i < commandNodes.getLength(); i++)
    {
      Node commandNode = commandNodes.item(i);
      
      if(commandNode.getNodeType() == Node.ELEMENT_NODE)
      {
        // Now we add the AVPs
        Message msg = parser.createEmptyMessage( Integer.valueOf(0), 0L );
        
        addAvps( msg.getAvps(), commandNode.getChildNodes());

        messages.add(msg.getAvps());
      }
    }
    
    log("Finished Parsing Action '" + action.getNodeName() + "'. It contains " + messages.size() + " messages.");
    
    return messages;
  }

  /**
   * Adds the AVPs present in the NodeList element to the passed AvpSet
   * @param avpSet the original set of AVPs, which will be modified and returned
   * @param avpNodes the NodeList containing the AVP elements
   * @return an AvpSet (the one passed as argument) with the newly added AVPs
   */
  private AvpSet addAvps(AvpSet avpSet, NodeList avpNodes)
  {
    for(int a = 0; a < avpNodes.getLength(); a++)
    {
      Node avpNode = avpNodes.item(a);
      
      if(avpNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element avpElem = (Element) avpNode;
        
        String avpCode = avpElem.getAttribute( "code" );
        String avpVendor = avpElem.getAttribute( "vendor" );
        String avpName = avpElem.getAttribute( "name" );
        String avpValue = avpElem.getAttribute( "value" );
        
        AvpRepresentation avpRep = null; 
        
        if( !avpCode.equals("") ) {
          int code = Integer.valueOf(avpCode);
          long vendorId = avpVendor.equals("") ? 0 : Long.valueOf(avpVendor);
          
          avpRep = AvpDictionary.INSTANCE.getAvp( code, vendorId );
        }
        else if( !avpName.equals( "" ) ) {
          avpRep = AvpDictionary.INSTANCE.getAvp(avpName);
        }
        else {
          throw new IllegalArgumentException("AVP Definition missing 'code' and 'name' attribute. At least one must be present.");
        }

        if(avpRep == null) {
          logError("Unable to find AVP with code [" + avpCode + "] and/or name [" + avpName + "] in dictionary. Can't add.");
        }
        else if(avpRep.getType().equals("Grouped")) {
          boolean isMandatory = !(avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot"));
          boolean isProtected = avpRep.getRuleProtected().equals("must");

          AvpSet gAvp = avpSet.addGroupedAvp(avpRep.getCode(), Long.valueOf(avpRep.getVendorId()), isMandatory, isProtected);
          addAvps( gAvp, avpNode.getChildNodes() );
        }
        else {
          avpSet.addAvp( avpRep.getCode(), valueToBytes( avpRep, avpValue ), Long.valueOf(avpRep.getVendorId()), true, false );
        }
      }
    }
    
    return avpSet;
  }
  
  /**
   * Parses the scenario returning an ArrayList of the actions to perform.
   * @param e the scenario Element
   * @return an ArrayList of Elements representing the actions
   * @throws Exception 
   */
  private ArrayList<Element> parseScenario(Element e) throws Exception
  {
    log("Parsing Scenario '" + e.getAttribute( "name" ) + "'...");
    
    ArrayList<Element> actions = new ArrayList<Element>();
    
    NodeList actionNodes = e.getChildNodes();
    
    for(int i = 0; i < actionNodes.getLength(); i++)
    {
      Node actionNode = actionNodes.item(i);
      
      if (actionNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element actionElem = (Element) actionNode;

        String actionName = actionElem.getNodeName();
        
        if(actionName.equals("send") || actionName.equals("receive"))
        {
          actions.add( actionElem );
        }
        else if(actionName.equals("configuration"))
        {
          log("Found scenario configuration. Loading it...");
          configureScenario(actionElem);
        }
        else
        {
          throw new Exception("Invalid scenario action (" + actionName + ")");
        }
      }
    }
    
    log("Successfully parsed scenario '" + e.getAttribute( "name" ) + "'. It contains " + actions.size() + " actions.");
    
    return actions;
  }
  
  private void configureScenario(Element configElem)
  {
    NodeList propertyNode = null;
    if( (propertyNode = configElem.getElementsByTagName("local-host")).getLength() > 0)
    {
      clientHost = propertyNode.item(0).getTextContent();
      log("Setting local-host to " + clientHost );
    }
    if( (propertyNode = configElem.getElementsByTagName("local-port")).getLength() > 0)
    {
      clientPort = propertyNode.item(0).getTextContent();
      log("Setting local-port to " + clientPort );
    }
    if( (propertyNode = configElem.getElementsByTagName("remote-host")).getLength() > 0)
    {
      serverHost = propertyNode.item(0).getTextContent();
      log("Setting remote-host to " + serverHost );
    }
    if( (propertyNode = configElem.getElementsByTagName("remote-port")).getLength() > 0)
    {
      serverPort = propertyNode.item(0).getTextContent();
      log("Setting remote-port to " + serverPort );
    }
    if( (propertyNode = configElem.getElementsByTagName("realm-name")).getLength() > 0)
    {
      realmName = propertyNode.item(0).getTextContent();
      log("Setting realm-name to " + realmName );
    }
    if( (propertyNode = configElem.getElementsByTagName("message-timeout")).getLength() > 0)
    {
      timeout = Integer.valueOf(propertyNode.item(0).getTextContent());
      log("Setting message-timeout to " + timeout );
    }
    
    log("Finished configuring scenario.");
  }

  private  class MySessionEventListener implements EventListener<Request, Answer> {

    private Answer answer = null;

    public void receivedSuccessMessage(Request request, Answer answer)
    {
      this.answer =  answer;
      receivedMessages.add( answer );
      continueProcessing();
    }

    public void timeoutExpired(Request request)
    {
      continueProcessing();
    }

    public boolean hasAnswer()
    {
      return getAnswer() != null;
    }

    public Answer getAnswer()
    {
      return answer;
    }

    private void continueProcessing()
    {
      synchronized(TestingFramework.this)
      {
        TestingFramework.this.notifyAll();
      }
    }
  }
  
  /**
   * Class representing the Diameter Test Framework Configuration  
   */
  public static class MyConfiguration extends EmptyConfiguration 
  {
    public MyConfiguration() 
    {
      super();
      
      add(Assembler, Assembler.defValue());
      add(OwnDiameterURI, clientURI);
      add(OwnIPAddress, "127.0.0.1");
      add(OwnRealm, realmName);
      add(OwnVendorID, 193L);
      // Set Ericsson SDK feature
      //add(UseUriAsFqdn, true);
      // Set Common Applications
      add(ApplicationId,
          // AppId 1
          getInstance().
          add(VendorId,   193L).
          add(AuthApplId, 0L).
          add(AcctApplId, 19302L)
      );
      // Set peer table
      add(PeerTable,
          // Peer 1
          getInstance().
          add(PeerRating, 1).
          add(PeerName, serverURI));
      // Set realm table
      add(RealmTable, 
          // Realm 1
          getInstance().add(RealmEntry, getInstance().
              add(RealmName, realmName).
              add(ApplicationId, getInstance().add(VendorId, 193L).add(AuthApplId, 0L).add(AcctApplId, 19302L)).
              add(RealmHosts, clientHost + ", " + serverHost).
              add(RealmLocalAction, "LOCAL").
              add(RealmEntryIsDynamic, false).
              add(RealmEntryExpTime, 1000L))
      );
    }
  }
  
  private Object getAvpValue(Avp avp) throws AvpDataException
  {
    String avpType = avpDictionary.getAvp( avp.getCode(), avp.getVendorId() ).getType();
    
    if(avpType.equals("Integer32"))
    {
      return avp.getInteger32();
    }
    else if(avpType.equals( "Unsigned32" ))
    {
      return avp.getUnsigned32();
    }
    else if(avpType.equals( "Integer64" ))
    {
      return avp.getInteger64(); 
    }
    else if(avpType.equals( "Unsigned64" ))
    {
      return avp.getUnsigned64();
    }
    else if(avpType.equals( "Float32" ))
    {
      return avp.getFloat32(); 
    }
    else if(avpType.equals( "Float64" ))
    {
      return avp.getFloat64(); 
    }
    else if(avpType.equals( "OctetString" ))
    {
      return avp.getOctetString();
    }
    else if(avpType.equals( "UTF8String" ))
    {
      return avp.getUTF8String();
    }
    else if(avpType.equals("Time"))
    {
      return avp.getTime();
    }
    
    // Default
    return avp.getOctetString();
  }
    
  /**
   * Converts a string value into a byte value
   * @param avpRep the AvpRepresentation of the AVP
   * @param valueStr the String value of the AVP
   * @return a byte[] containing the bytes of the correct format value
   */
  private byte[] valueToBytes(AvpRepresentation avpRep, String valueStr)
  {
    String avpType = avpRep.getType();
    
    if(avpType.equals("Integer32"))
    {
      return parser.int32ToBytes( Integer.valueOf(valueStr) );
    }
    else if(avpType.equals( "Unsigned32" ))
    {
      return parser.intU32ToBytes( Long.valueOf(valueStr) );
    }
    else if(avpType.equals( "Integer64" ))
    {
      return parser.int64ToBytes( Long.valueOf(valueStr) ); 
    }
    else if(avpType.equals( "Unsigned64" ))
    {
      return parser.int64ToBytes( Long.valueOf(valueStr) );
    }
    else if(avpType.equals( "Float32" ))
    {
      return parser.float32ToBytes( Float.valueOf(valueStr) ); 
    }
    else if(avpType.equals( "Float64" ))
    {
      return parser.float64ToBytes( Double.valueOf(valueStr) ); 
    }
    else if(avpType.equals( "OctetString" ))
    {
      try
      {
        return parser.octetStringToBytes( (valueStr) );
      }
      catch ( ParseException pe )
      {
        pe.printStackTrace();
      }
    }
    else if(avpType.equals("Time"))
    {
      return parser.dateToBytes( new Date(Long.valueOf(valueStr)) );
    }
    
    // Default
    return valueStr.getBytes();
  }
  
  /**
   * Prints the AVPs present in an AvpSet
   * @param avpSet the AvpSet containing the AVPs to be printed
   * @throws AvpDataException
   */
  private void printAvps(AvpSet avpSet) throws AvpDataException
  {
    printAvpsAux( avpSet, 0 );
  }

  /**
   * Prints the AVPs present in an AvpSet with a specified 'tab' level
   * @param avpSet the AvpSet containing the AVPs to be printed
   * @param level an int representing the number of 'tabs' to make a pretty print
   * @throws AvpDataException
   */
  private void printAvpsAux(AvpSet avpSet, int level) throws AvpDataException
  {
    String prefix = "                      ".substring( 0, level*2 );
    
    for(Avp avp : avpSet) {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp( avp.getCode(), avp.getVendorId() );

      if(avpRep != null) {
        if(avpRep.getType().equals("Grouped")) {
          log(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\">");
          printAvpsAux( avp.getGrouped(), level+1 );
          log(prefix + "</avp>");
        }
        else {
          String value = "";
          
          if(avpRep.getType().equals("Integer32"))
            value = String.valueOf(avp.getInteger32());
          else if(avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64"))
            value = String.valueOf(avp.getInteger64());
          else if(avpRep.getType().equals("Unsigned32"))
            value = String.valueOf(avp.getUnsigned32());
          else if(avpRep.getType().equals("Float32"))
            value = String.valueOf(avp.getFloat32());
          else
            value = avp.getUTF8String();
          
          log(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\" value=\"" + value + "\" />");
        }
      }
      else {
        log(prefix + "<avp name=\"?\" code=\"" + avp.getCode() + "\" vendor=\"" + avp.getVendorId() + "\" value=\"" + avp.getOctetString() + "\" />");
      }
    }
  }

  /**
   * Returns the Command-Code from Short or Long Command Name
   * @param name the command name
   * @return an int identifying the command
   */
  private int getCommandFromString(String name)
  {
    if( name.equals("Capabilities-Exchange-Request") || name.equals("CER") )
      return Message.CAPABILITIES_EXCHANGE_REQUEST;
    else if( name.equals("Capabilities-Exchange-Answer") || name.equals("CEA") )
      return Message.CAPABILITIES_EXCHANGE_ANSWER;
    else if( name.equals("Disconnect-Peer-Request") || name.equals("DPR") )
      return Message.DISCONNECT_PEER_REQUEST;
    else if( name.equals("Disconnect-Peer-Answer") || name.equals("DPA") )
      return Message.DISCONNECT_PEER_ANSWER;
    else if( name.equals("Device-Watchdog-Request") || name.equals("DWR") )
      return Message.DEVICE_WATCHDOG_REQUEST;
    else if( name.equals("Device-Watchdog-Answer") || name.equals("DWA") )
      return Message.DEVICE_WATCHDOG_ANSWER;
    else if( name.equals("Re-Auth-Request") || name.equals("RAR") )
      return Message.RE_AUTH_REQUEST;
    else if( name.equals("Re-Auth-Answer") || name.equals("RAA") )
      return Message.RE_AUTH_ANSWER;
    else if( name.equals("Abort-Session-Request") || name.equals("ASR") )
      return Message.ABORT_SESSION_REQUEST;
    else if( name.equals("Abort-Session-Answer") || name.equals("ASA") )
      return Message.ABORT_SESSION_ANSWER;
    else if( name.equals("Session-Termination-Request") || name.equals("STR") )
      return Message.SESSION_TERMINATION_REQUEST;
    else if( name.equals("Session-Termination-Answer") || name.equals("STA") )
      return Message.SESSION_TERMINATION_ANSWER;
    else if( name.equals("Accounting-Request") || name.equals("ACR") )
      return Message.ACCOUNTING_REQUEST;
    else if( name.equals("Accounting-Answer") || name.equals("ACA") )
      return Message.ACCOUNTING_ANSWER;

    return -1;
  }

  /**
   * Logs a message into stdout
   * @param message the message to be printed
   */
  private static void log(String message)
  {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
    String log = "[" + format.format(new Date()) + "] " + message;
    
    System.out.println(log);
  }
  
  /**
   * Logs a message into stderr
   * @param message the message to be printed
   */
  private static void logError(String message)
  {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
    String log = "[" + format.format(new Date()) + "] " + message;

    System.err.println(log);
  }  
}
