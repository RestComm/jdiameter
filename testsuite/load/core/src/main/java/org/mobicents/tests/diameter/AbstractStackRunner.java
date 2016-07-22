/**
 *
 */
package org.mobicents.tests.diameter;

import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.api.StackType;
import org.jdiameter.client.impl.parser.MessageParser;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;

/**
 * Base class for all soak/cps tests
 *
 * @author baranowb
 *
 */
public abstract class AbstractStackRunner implements NetworkReqListener, EventListener<Request, Answer> {

  protected final Logger log = Logger.getLogger(getClass());
  // protected static String clientHost = "uac.mobicents.org";
  // protected static String clientPort = "13868";
  // protected static String clientURI = "aaa://" + clientHost + ":" +
  // clientPort;

  // protected static String serverHost = "127.0.0.1";
  protected static String serverHost = "uas.mobicents.org";
  protected static String serverPort = "3868";
  protected static String serverURI = "aaa://" + serverHost + ":" + serverPort;

  protected static String realmName = "mobicents.org";

  protected final int[] prefilledAVPs = new int[] { Avp.DESTINATION_HOST, Avp.DESTINATION_REALM, Avp.ORIGIN_HOST,
      Avp.ORIGIN_REALM, Avp.SESSION_ID, Avp.VENDOR_SPECIFIC_APPLICATION_ID };

  protected MessageParser parser = new MessageParser();
  protected Stack stack;
  protected SessionFactory factory;
  protected InputStream configFile;
  protected AvpDictionary dictionary = AvpDictionary.INSTANCE;
  private boolean run = true;

  /**
   *
   */
  public AbstractStackRunner() {
    // TODO Auto-generated constructor stub
  }

  public void configure(InputStream f) throws Exception {
    this.configFile = f;
    // add more
    try {
      dictionary.parseDictionary(this.getClass().getClassLoader().getResourceAsStream("dictionary.xml"));
      log.info("AVP Dictionary successfully parsed.");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    initStack();
  }

  private void initStack() {
    if (log.isInfoEnabled()) {
      log.info("Initializing Stack...");
    }

    try {

      this.stack = new StackImpl();
    }
    catch (Exception e) {
      e.printStackTrace();
      stack.destroy();
      return;
    }

    try {
      InputStream is;
      if (configFile != null) {
        is = this.configFile;
      }
      else {
        String configFile = "jdiameter-config.xml";
        is = this.getClass().getClassLoader().getResourceAsStream(configFile);
      }
      Configuration config = new XMLConfiguration(is);
      factory = stack.init(config);
      if (log.isInfoEnabled()) {
        log.info("Stack Configuration successfully loaded.");
      }
      Network network = stack.unwrap(Network.class);

      Set<org.jdiameter.api.ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

      log.info("Diameter Stack  :: Supporting " + appIds.size() + " applications.");

      // network.addNetworkReqListener(this,
      // ApplicationId.createByAccAppId( 193, 19302 ));

      for (org.jdiameter.api.ApplicationId appId : appIds) {
        log.info("Diameter Stack Mux :: Adding Listener for [" + appId + "].");
        network.addNetworkReqListener(this, appId);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
      stack.destroy();
      return;
    }

    MetaData metaData = stack.getMetaData();
    if (metaData.getStackType() != StackType.TYPE_SERVER || metaData.getMinorVersion() <= 0) {
      stack.destroy();
      if (log.isEnabledFor(org.apache.log4j.Level.ERROR)) {
        log.error("Incorrect driver");
      }
      return;
    }

    try {
      if (log.isInfoEnabled()) {
        log.info("Starting stack");
      }
      stack.start();
      if (log.isInfoEnabled()) {
        log.info("Stack is running.");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      stack.destroy();
      return;
    }
    if (log.isInfoEnabled()) {
      log.info("Stack initialization successfully completed.");
    }
  }

  /**
   * @return the run
   */
  public boolean isRun() {
    return run;
  }

  /**
   * @param run
   *          the run to set
   */
  public void setRun(boolean run) {
    this.run = run;
  }

  public void performTestRun() {

    log.info("Press 'q' to stop execution");
    while (run) {
      try {
        Thread.sleep(1000);

        if (System.in.available() > 0) {

          int r = System.in.read();
          if (r == 'q') {
            run = false;
            break;
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();

      }
    }

    clean();
  }

  protected void clean() {

    if (stack != null) {
      try {
        stack.stop(10, TimeUnit.SECONDS, DisconnectCause.REBOOTING);

        stack = null;
        factory = null;
      }
      catch (IllegalDiameterStateException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (InternalException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  protected void dumpMessage(Message message, boolean sending) {
    if (log.isInfoEnabled()) {
      log.info((sending ? "Sending " : "Received ") + (message.isRequest() ? "Request: " : "Answer: ")
          + message.getCommandCode() + "\nE2E:" + message.getEndToEndIdentifier() + "\nHBH:"
          + message.getHopByHopIdentifier() + "\nAppID:" + message.getApplicationId());
      log.info("Request AVPS: \n");
      try {
        printAvps(message.getAvps());
      }
      catch (AvpDataException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  protected Message createAnswer(Request request, int answerCode, ApplicationId appId)
      throws InternalException, IllegalDiameterStateException {

    int commandCode = 0;
    long endToEndId = 0;
    long hopByHopId = 0;
    commandCode = request.getCommandCode();
    endToEndId = request.getEndToEndIdentifier();
    hopByHopId = request.getHopByHopIdentifier();

    Message raw = stack.getSessionFactory().getNewRawSession().createMessage(commandCode, appId, hopByHopId,
        endToEndId);
    AvpSet avps = raw.getAvps();

    // inser session iD
    avps.insertAvp(0, 263, request.getSessionId(), false);
    // add result //asUnsignedInt32
    avps.addAvp(268, 2001L, true);
    // origin host
    avps.addAvp(264, serverHost, true);
    // origin realm
    avps.addAvp(296, realmName, true);
    raw.setProxiable(true);
    raw.setRequest(false);
    // ((MessageImpl) raw).setPeer(((MessageImpl) request).getPeer());
    return raw;

  }

  protected void printAvps(AvpSet avpSet) throws AvpDataException {
    printAvpsAux(avpSet, 0);
  }

  /**
   * Prints the AVPs present in an AvpSet with a specified 'tab' level
   *
   * @param avpSet
   *          the AvpSet containing the AVPs to be printed
   * @param level
   *          an int representing the number of 'tabs' to make a pretty
   *          print
   * @throws AvpDataException
   */
  private void printAvpsAux(AvpSet avpSet, int level) throws AvpDataException {
    String prefix = "                      ".substring(0, level * 2);

    for (Avp avp : avpSet) {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(avp.getCode(), avp.getVendorId());

      if (avpRep != null && avpRep.getType().equals("Grouped")) {
        log.info(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\""
            + avp.getVendorId() + "\">");
        printAvpsAux(avp.getGrouped(), level + 1);
        log.info(prefix + "</avp>");
      }
      else if (avpRep != null) {
        String value = "";

        if (avpRep.getType().equals("Integer32")) {
          value = String.valueOf(avp.getInteger32());
        }
        else if (avpRep.getType().equals("Integer64") || avpRep.getType().equals("Unsigned64")) {
          value = String.valueOf(avp.getInteger64());
        }
        else if (avpRep.getType().equals("Unsigned32")) {
          value = String.valueOf(avp.getUnsigned32());
        }
        else if (avpRep.getType().equals("Float32")) {
          value = String.valueOf(avp.getFloat32());
        }
        else {
          value = new String(avp.getOctetString());
        }

        log.info(prefix + "<avp name=\"" + avpRep.getName() + "\" code=\"" + avp.getCode() + "\" vendor=\""
            + avp.getVendorId() + "\" value=\"" + value + "\" />");
      }
    }
  }

}
