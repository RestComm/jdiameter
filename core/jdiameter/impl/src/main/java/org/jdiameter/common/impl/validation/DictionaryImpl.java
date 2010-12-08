/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.common.impl.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdiameter.api.Message;
import org.jdiameter.api.validation.AvpNotAllowedException;
import org.jdiameter.api.validation.AvpRepresentation;
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.api.validation.MessageRepresentation;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.client.impl.DictionarySingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of {@link Dictionary} interface.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @since 1.5.4.0-build404
 */
public class DictionaryImpl implements Dictionary {

  private static transient Logger logger = LoggerFactory.getLogger(DictionaryImpl.class);

  public static final Dictionary INSTANCE = new DictionaryImpl();

  public static final String _AVP_ATTRIBUTE_NAME = "name";
  public static final String _AVP_ATTRIBUTE_CODE = "code";
  public static final String _AVP_ATTRIBUTE_VENDOR = "vendor";
  public static final String _AVP_ATTRIBUTE_MULTIPLICITY = "multiplicity";
  public static final String _AVP_ATTRIBUTE_INDEX = "index";
  public static final String _VALIDATOR_NODE_NAME = "validator";
  public static final String _VALIDATOR_NODE_ENABLED_ATTR = "enabled";
  public static final String _VALIDATOR_NODE_SEND_LEVEL_ATTR = "sendLevel";
  public static final String _VALIDATOR_NODE_RECEIVE_LEVEL_ATTR = "receiveLevel";

  private Map<AvpRepresentation, AvpRepresentation> avpMap = new HashMap<AvpRepresentation, AvpRepresentation>();

  private Map<String, String> vendorMap = new HashMap<String, String>();

  private Map<MessageRepresentation, MessageRepresentation> commandMap = new HashMap<MessageRepresentation, MessageRepresentation>();

  private Map<String, String> typedefMap = new HashMap<String, String>();

  private Map<String, AvpRepresentation> nameToCodeMap = new HashMap<String, AvpRepresentation>();

  private boolean configured = false;

  private DictionaryImpl() {
    this.init("dictionary.xml");
  }

  private void init(String confFile) {
    InputStream is = null;

    try {
      is = DictionarySingleton.class.getResourceAsStream(confFile);
      if(is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in class classloader. Trying thread context class loader.", confFile);         
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(confFile);  
      }

      if(is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in thread context class loader. Trying using 'config/' prefix.", confFile);         
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/" + confFile);  
      }

      if(is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in thread context class loader. Trying regular file.", confFile);
        File fDict = new File(confFile);
        if(fDict.exists()) {
          is = new FileInputStream(fDict);
        }
        else {
          logger.debug("Failed to locate dictionary configuration file: {}, from regular file. Trying using 'config/' prefix.", confFile);
          fDict = new File("config/" + confFile);
          if(fDict.exists()) {
            is = new FileInputStream(fDict);
          }
        }
      }

      if(is != null) {
        this.configure(is);        
      }
      else {
        this.setEnabled(false);
        logger.warn("Failed to initialize and configure Diameter Dictionary since configuration file was not found. Validator is disabled.");
      }
    }
    catch(FileNotFoundException fnfe) {
      // normal, maybe its configured elsewhere?
      logger.debug("Could not load configuration file: {}, from any known location.", confFile);
    }
    finally {
      if(is != null) {
        try {
          is.close();
        }
        catch (IOException e) {
          logger.error("", e);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#isConfigured()
   */
  public boolean isConfigured() {
    return this.configured;
  }

  public AvpRepresentation getAvp(int code) {
    return getAvp(code, 0);
  }

  public AvpRepresentation getAvp(int code, long vendorId) {
    if (!this.configured) {
      return null;
    }
    AvpRepresentation avp = avpMap.get(getMapKey(code, vendorId));

    if (avp == null) {
      logger.warn("AVP with code {} and Vendor-Id {} not present in dictionary!", code, vendorId);
    }

    return avp;
  }

  public AvpRepresentation getAvp(String avpName) {
    if (!this.configured) {
      return null;
    }
    AvpRepresentation avpKey = nameToCodeMap.get(avpName);

    return avpKey != null ? avpMap.get(avpKey) : null;
  }

  private long getVendorCode(String vendorId) {

    long value = -1;

    if (vendorId == null) {
      value = 0;
    }
    else {
      String vendorCode = vendorMap.get(vendorId);
      value = vendorCode == null ? 0 : Long.parseLong(vendorCode);
    }

    return value;
  }

  private AvpRepresentation getMapKey(int avpCode, long vendorId) {
    return new AvpRepresentationImpl(avpCode, vendorId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#getMessage(int, boolean)
   */
  public MessageRepresentation getMessage(int commandCode, boolean isRequest) {
    return this.getMessage(commandCode, 0, isRequest);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#getMessage(int, long, boolean)
   */
  public MessageRepresentation getMessage(int commandCode, long applicationId, boolean isRequest) {
    if (!this.configured) {
      return null;
    }
    MessageRepresentation key = new MessageRepresentationImpl(commandCode, applicationId, isRequest);
    return this.commandMap.get(key);
  }

  // Validation ---------------------------------------------------------------

  private boolean enabled = true;
  private ValidatorLevel sendValidationLevel = ValidatorLevel.OFF;
  private ValidatorLevel receiveValidationLevel = ValidatorLevel.OFF;

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#isValidate()
   */
  public boolean isEnabled() {
    return this.enabled;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#getSendLevel()
   */
  public ValidatorLevel getSendLevel() {
    return this.sendValidationLevel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#getReceiveLevel()
   */
  public ValidatorLevel getReceiveLevel() {
    return this.receiveValidationLevel;
  }

  public void setSendLevel(ValidatorLevel level) {
    this.sendValidationLevel = level;
  }

  public void setReceiveLevel(ValidatorLevel level) {
    this.receiveValidationLevel = level;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setConfigured(boolean configured) {
    this.configured = configured;
  }

  public void validate(Message msg, boolean incoming) throws AvpNotAllowedException {
    if (!enabled || !configured) {
      return;
    }

    MessageRepresentationImpl rep = new MessageRepresentationImpl(msg.getCommandCode(), msg.getApplicationId(), msg.isRequest());
    rep = (MessageRepresentationImpl) this.commandMap.get(rep);
    if (rep == null) {
      // no notion, lets leave it.
      logger.warn("Validation could not be performed, command not defined!. Code={}, Application-Id={}, Req={}",
          new Object[] { msg.getCommandCode(), msg.getApplicationId(), msg.isRequest() });
      return;
    }

    rep.validate(msg, (incoming ? receiveValidationLevel : sendValidationLevel));
  }

  // Parsing ------------------------------------------------------------------

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.api.validation.Dictionary#configure(java.io.InputStream)
   */
  public void configure(InputStream is) {
    if (is == null) {
      logger.error("No input stream to configure dictionary from?");
      return;
    }
    try {
      long startTime = System.currentTimeMillis();
      this.nameToCodeMap = new TreeMap<String, AvpRepresentation>(new Comparator<String>() {
        public int compare(String o1, String o2) {
          return (o1 == null) ? 1 : (o2 == null) ? -1 : o1.compareTo(o2);
        }
      });

      this.vendorMap = new HashMap<String, String>();
      this.typedefMap = new HashMap<String, String>();
      this.avpMap = new HashMap<AvpRepresentation, AvpRepresentation>();
      this.commandMap = new HashMap<MessageRepresentation, MessageRepresentation>();

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setValidating(false);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(is);

      doc.getDocumentElement().normalize();

      this.parseVendors(doc);
      this.parseTypDefs(doc);
      this.parseAvps(doc);
      this.resolveWeakGroupedChildren();
      this.parseCommands(doc);
      this.configured = true;

      NodeList validatorNodeList = doc.getElementsByTagName(_VALIDATOR_NODE_NAME);
      if (validatorNodeList == null || validatorNodeList.getLength() == 0) {
        this.enabled = false;
      }
      else {
        boolean found = false;
        for (int index = 0; index < validatorNodeList.getLength(); index++) {
          if (validatorNodeList.item(index).getNodeType() == Node.ELEMENT_NODE) {
            found = true;
            Element validatorElement = (Element) validatorNodeList.item(index);
            if (!validatorElement.hasAttribute(_VALIDATOR_NODE_ENABLED_ATTR)) {
              this.enabled = false;
            }
            else {
              this.enabled = Boolean.parseBoolean(validatorElement.getAttribute(_VALIDATOR_NODE_ENABLED_ATTR));
            }

            if (!validatorElement.hasAttribute(_VALIDATOR_NODE_RECEIVE_LEVEL_ATTR)) {
              this.receiveValidationLevel = ValidatorLevel.OFF;
            }
            else {
              try {
                this.receiveValidationLevel = ValidatorLevel.fromString(validatorElement.getAttribute(_VALIDATOR_NODE_RECEIVE_LEVEL_ATTR));
              }
              catch (IllegalArgumentException e) {
                logger.error("Failed to decode received validation level due to: ", e);
              }
            }
            if (!validatorElement.hasAttribute(_VALIDATOR_NODE_SEND_LEVEL_ATTR)) {
              this.sendValidationLevel = ValidatorLevel.OFF;
            }
            else {
              try {
                this.sendValidationLevel = ValidatorLevel.fromString(validatorElement.getAttribute(_VALIDATOR_NODE_SEND_LEVEL_ATTR));
              }
              catch (IllegalArgumentException e) {
                logger.error("Failed to decode send validation level due to: ", e);
              }
            }

            break;
          }
        }
        if (!found) {
          this.enabled = false;
          this.sendValidationLevel = ValidatorLevel.OFF;
          this.receiveValidationLevel = ValidatorLevel.OFF;
        }
      }

      long endTime = System.currentTimeMillis();

      if(logger.isInfoEnabled()) {
        logger.info("AVP Validator :: Loaded in {}ms == Vendors[{}] Commands[{}] Types[{}] AVPs[{}]",
            new Object[] { (endTime - startTime), vendorMap.size(), commandMap.size(), typedefMap.size(), avpMap.size() });
      }

      if (logger.isInfoEnabled()) {
        StringBuffer sb = new StringBuffer();
        int c = 0;
        for (AvpRepresentation key : this.avpMap.keySet()) {
          if (this.avpMap.get(key).isWeak()) {
            c++;
            sb.append("---------------------------------\n").append("Found incomplete AVP definition:\n").append(this.avpMap.get(key)).append("\n");
          }
        }

        if (c > 0) {
          sb.append("------- TOTAL INCOMPLETE AVPS COUNT: ").append(c).append(" -------");
          logger.info(sb.toString());
        }
      }
    }
    catch (Exception e) {
      this.enabled = false;
      this.configured = false;
      logger.error("Failed to parse validator configuration. Validator disabled.", e);
    }
    finally {
      // close?
      try {
        is.close();
      }
      catch (IOException e) {
        logger.debug("Failed to close InputStream for Dictionary XML.", e);
      }
    }
  }

  /**
   * @param doc
   * @param nameToCode
   * @param avpMap
   * @return
   */
  private void parseCommands(Document doc) {
    // here all grouped AVPs should have proper filling.
    // now lets go through message definition, we have to respect application nodes
    NodeList applicationNodes = doc.getElementsByTagName("application");

    // Map<MessageRepresentation, MessageRepresentation> commandMap = new
    // HashMap<MessageRepresentation, MessageRepresentation>();
    for (int applicationIndex = 0; applicationIndex < applicationNodes.getLength(); applicationIndex++) {
      if (applicationNodes.item(applicationIndex).getNodeType() == Node.ELEMENT_NODE) {
        Element applicationElement = (Element) applicationNodes.item(applicationIndex);

        if (!applicationElement.hasAttribute("id")) {
          logger.debug("[ERROR] Application definition does not have ID, skipping message");
          continue;
        }

        long applicationCode = Long.valueOf(applicationElement.getAttribute("id"));
        NodeList commandNodes = applicationElement.getElementsByTagName("command");

        for (int c = 0; c < commandNodes.getLength(); c++) {
          Node commandNode = commandNodes.item(c);

          if (commandNode.getNodeType() == Node.ELEMENT_NODE) {
            Element commandElement = (Element) commandNode;
            // FIXME: add more
            if (!commandElement.hasAttribute("request")) {
              logger.debug("[ERROR] Command for application: {} does not define if its request or answer, skipping.", applicationCode);
              continue;
            }
            String commandName = commandElement.getAttribute("name");
            String commandCode = commandElement.getAttribute("code");
            // String commandVendorId = commandElement.getAttribute("vendor-id");
            String isRequest = commandElement.getAttribute("request");
            // FIXME: should commandVendorId should be used somewhere?
            // String commandVendorCode = vendorMap.get(commandVendorId);
            MessageRepresentationImpl msg = new MessageRepresentationImpl(Integer.valueOf(commandCode), applicationCode,
                Boolean.parseBoolean(isRequest), commandName);

            Map<AvpRepresentation, AvpRepresentation> commandAvpList = new HashMap<AvpRepresentation, AvpRepresentation>();

            commandMap.put(msg, msg);

            // now we have to process avp defs for this message :)
            NodeList commandAvpsList = commandElement.getElementsByTagName("avp");

            for (int commandAvpIndex = 0; commandAvpIndex < commandAvpsList.getLength(); commandAvpIndex++) {
              if (commandAvpsList.item(commandAvpIndex).getNodeType() == Node.ELEMENT_NODE) {
                Element commandAvpElement = (Element) commandAvpsList.item(commandAvpIndex);
                String multiplicity = null;
                String name = null;
                String index = null;
                if (!commandAvpElement.hasAttribute("name")) {
                  logger.debug("[ERROR] Command defines avp without name! Command: {}, Code: {}, ApplicationID: {}",
                      new Object[] { msg.getName(), msg.getCommandCode(), msg.getApplicationId() });
                  continue;
                }
                else {
                  name = commandAvpElement.getAttribute("name").trim();
                }

                if (!commandAvpElement.hasAttribute("multiplicity")) {
                  logger.debug("[WARN] Command defines avp without multiplicity.");
                  multiplicity = AvpRepresentation._MP_ZERO_OR_MORE;
                }
                else {
                  multiplicity = commandAvpElement.getAttribute("multiplicity");
                }
                if (!commandAvpElement.hasAttribute("index")) {
                  index = "-1";
                }
                else {
                  index = commandAvpElement.getAttribute("index");
                }

                String avpCode = commandAvpElement.getAttribute("code");
                String avpVendor = commandAvpElement.getAttribute("vendor");
                if (avpCode == null) {
                  logger.debug("[ERROR] Command defines avp without code! Command: {}, Code: {}, ApplicationID: {}",
                      new Object[] { msg.getName(), msg.getCommandCode(), msg.getApplicationId() });
                  continue;
                }
                if (avpVendor == null) {
                  logger.debug("[WARN] Command defines avp without vendor, assuming default. Command: {}, Code: {}, ApplicationID: {}",
                      new Object[] { msg.getName(), msg.getCommandCode(), msg.getApplicationId() });
                  avpVendor = "0";
                }

                // here we have name and multiplicity. we have to get avp def from name, clone and set multiplicity.
                AvpRepresentation strongRepresentation = null;
                AvpRepresentation strongKey = getMapKey(Integer.valueOf(avpCode), Long.valueOf(avpVendor));

                strongRepresentation = this.avpMap.get(strongKey);
                if (strongRepresentation != null && !strongRepresentation.isWeak()) {
                  AvpRepresentationImpl clone;
                  try {
                    clone = (AvpRepresentationImpl) strongRepresentation.clone();
                    clone.setMultiplicityIndicator(multiplicity);
                    clone.markFixPosition(Integer.valueOf(index));
                    commandAvpList.put(clone, clone);
                  }
                  catch (CloneNotSupportedException e) {
                    logger.error("Unable to clone AvpRepresentation", e);
                  }
                }
                else {
                  logger.debug("[WARN] No strong avp for key {}, in name: {}", new Object[] { strongKey, name.trim() });
                  continue;
                }
              }
            }

            msg.setMessageAvps(commandAvpList);
          }
        }
      }
    }
    return;
  }

  /**
   * @param doc
   * @param vendorMap
   * @return
   */
  protected void parseAvps(Document doc) {
    // now, lets process AVPs, we ignore <application> boundaries, since AVPs
    // are unique by: name, code, vendor-name/mapped to id here
    // once we have this set, we will resolve weak AVPs, than we can process
    // messages, and based on AVP name populate message with proper representations.

    /**************************************************************************
     * AVPs
     */
    NodeList applicationNodes = doc.getElementsByTagName("application");
    for (int index = 0; index < applicationNodes.getLength(); index++) {
      Node n = applicationNodes.item(index);
      if (n.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      NodeList applicationChildElements = n.getChildNodes();
      for (int applicationChildIndex = 0; applicationChildIndex < applicationChildElements.getLength(); applicationChildIndex++) {
        Node avpNode = applicationChildElements.item(applicationChildIndex);
        if (avpNode.getNodeType() == Node.ELEMENT_NODE && avpNode.getNodeName().trim().equals("avp")) {
          Element avpElement = (Element) avpNode;

          String avpName = avpElement.getAttribute("name").trim();

          String avpDescription = avpElement.getAttribute("description");

          String avpCode = avpElement.getAttribute("code");

          String avpMayEncrypt = avpElement.getAttribute("may-encrypt");

          String avpMandatory = avpElement.getAttribute("mandatory");

          String avpProtected = avpElement.getAttribute("protected").equals("") ? "may" : avpElement.getAttribute("protected");

          String avpVendorBit = avpElement.getAttribute("vendor-bit");

          String avpVendorId = avpElement.getAttribute("vendor-id");
          long vendorCode = getVendorCode(avpVendorId);
          String avpConstrained = avpElement.getAttribute("constrained");

          // So it shows, clearly we mess up some where.
          String avpType = "NOT-SET";
          List<AvpRepresentation> weakGroupedAvpChildren = new ArrayList<AvpRepresentation>();
          // Now either we have type or grouped
          NodeList avpChildNodes = avpNode.getChildNodes();

          for (int j = 0; j < avpChildNodes.getLength(); j++) {
            Node avpChildNode = avpChildNodes.item(j);

            if (avpChildNode.getNodeType() == Node.ELEMENT_NODE) {
              Element avpChildElement = (Element) avpChildNode;

              if (avpChildElement.getNodeName().equals("grouped")) {
                // All we need to know is that's a grouped AVP.
                avpType = "Grouped";
                // we create a bunch on weak avp reps.

                NodeList groupedAvpMembers = avpChildElement.getChildNodes();
                for (int gChildIndex = 0; gChildIndex < groupedAvpMembers.getLength(); gChildIndex++) {
                  Node groupedAvpChildNode = groupedAvpMembers.item(gChildIndex);
                  if (groupedAvpChildNode.getNodeType() == Node.ELEMENT_NODE) {
                    // we have our member
                    Element groupedChildWeakElement = (Element) groupedAvpChildNode;
                    String name = null;
                    String multiplicity = AvpRepresentation._MP_ZERO_OR_MORE;
                    String indexIndicator = "-1";

                    if (!groupedChildWeakElement.hasAttribute("name")) {
                      if (logger.isDebugEnabled()) {
                        logger.debug(new StringBuffer("[ERROR] Grouped child does not have name, grouped avp:  Name[").append(avpName).append("] Description[").
                            append(avpDescription).append("] Code[").append(avpCode).append("] May-Encrypt[").append(avpMayEncrypt).append("] Mandatory[").
                            append(avpMandatory).append("] Protected [").append(avpProtected).append("] Vendor-Bit [").append(avpVendorBit).append("] Vendor-Id [").
                            append(avpVendorId).append("] Constrained[").append(avpConstrained).append("] Type [").append(avpType).append("]").toString());
                      }
                      continue;
                    }
                    else {
                      name = groupedChildWeakElement.getAttribute("name").trim();
                    }

                    if (!groupedChildWeakElement.hasAttribute("multiplicity")) {
                      multiplicity = AvpRepresentation._MP_ZERO_OR_MORE;
                    }
                    else {
                      multiplicity = groupedChildWeakElement.getAttribute("multiplicity");
                    }

                    if (!groupedChildWeakElement.hasAttribute("index")) {
                      indexIndicator = "-1";
                    }
                    else {
                      indexIndicator = groupedChildWeakElement.getAttribute("index");
                    }

                    AvpRepresentationImpl weakChild = new AvpRepresentationImpl(name, vendorCode);
                    weakChild.setMultiplicityIndicator(multiplicity);
                    weakChild.markFixPosition(Integer.valueOf(indexIndicator));
                    // just to be sure
                    weakChild.markWeak(true);
                    weakGroupedAvpChildren.add(weakChild);
                  }
                }
              }
              else if (avpChildElement.getNodeName().equals("type")) {
                avpType = avpChildElement.getAttribute("type-name");
                avpType = typedefMap.get(avpType);
              }
              else if (avpChildElement.getNodeName().equals("enum")) {
                // NOP?
              }
            }
          }

          if (logger.isTraceEnabled()) {
            logger.trace(new StringBuffer("Parsed AVP: Name[").append(avpName).append("] Description[").append(avpDescription).
                append("] Code[").append(avpCode).append("] May-Encrypt[").append(avpMayEncrypt).append("] Mandatory[").
                append(avpMandatory).append("] Protected [").append(avpProtected).append("] Vendor-Bit [").append(avpVendorBit).
                append("] Vendor-Id [").append(avpVendorId).append("] Constrained[").append(avpConstrained).append("] Type [").
                append(avpType).append("]").toString());
          }

          try {
            AvpRepresentationImpl avp = null;

            avp = new AvpRepresentationImpl(avpName.trim(), avpDescription, Integer.valueOf(avpCode), avpMayEncrypt.equals("yes"), avpMandatory,
                avpProtected, avpVendorBit, vendorCode, avpConstrained.equals("true"), avpType);

            if (avp.isGrouped()) {
              avp.setChildren(weakGroupedAvpChildren);
              // we are not strong enough., children are
              // referenced ONLY by name, so we are
              // weak until all children can be resolved to strong
              // representation
              avp.markWeak(true);
            }
            AvpRepresentation mapKey = new AvpRepresentationImpl(avp.getCode(), avp.getVendorId());

            avpMap.put(mapKey, avp);
            if (nameToCodeMap.containsKey(avp.getName().trim())) {
              logger.debug("[ERROR] Overwriting definition of avp(same name) , present: {}, new one: {}",
                  new Object[] { nameToCodeMap.get(avp.getName().trim()), mapKey });
            }
            nameToCodeMap.put(avp.getName().trim(), mapKey);
          }
          catch (Exception e) {
            if (logger.isDebugEnabled()) {
              logger.debug(new StringBuffer("[ERROR] Failed Parsing AVP: Name[").append(avpName).append("] Description[").append(avpDescription).append("] Code[").append(avpCode).append("] May-Encrypt[").append(avpMayEncrypt).append("] Mandatory[").append(avpMandatory).append("] Protected [").append(avpProtected).append("] Vendor-Bit [").append(avpVendorBit).append("] Vendor-Id [").append(avpVendorId).append("] Constrained[").append(avpConstrained).append("] Type [").append(avpType).append("]").toString(), e);
            }
          }
        }
      }
    }
    return;
  }

  /**
   * @param doc
   * @return
   */
  protected void parseTypDefs(Document doc) {
    // here we have full base of vendor-name --> vendorId(long) map.
    // Now lets parse type defs, dunno why, but we do that, so we can std out it :)

    /*
     * <typedefn type-name="OctetString"/> <typedefn type-name="UTF8String"
     * type-parent="OctetString"/> <typedefn type-name="VendorId"
     * type-parent="Unsigned32"/>
     */

    NodeList typedefNodes = doc.getElementsByTagName("typedefn");
    // HashMap<String, String> typedefMap = new HashMap<String, String>();
    for (int td = 0; td < typedefNodes.getLength(); td++) {
      Node typedefNode = typedefNodes.item(td);
      if (typedefNode.getNodeType() == Node.ELEMENT_NODE) {
        Element typedefElement = (Element) typedefNode;

        String typeName = typedefElement.getAttribute("type-name");
        String typeParent = typedefElement.getAttribute("type-parent");

        if (typeParent.equals("") || typeName.equals("UTF8String")) {
          typeParent = typeName;
        }

        typedefMap.put(typeName, typeParent);
      }
    }

    return;
  }

  /**
   * @param doc
   * @return
   */
  protected void parseVendors(Document doc) {
    // Parse vendors, we will need those.
    /*
     * <!-- ************************* Vendors ****************************
     * --> <vendor vendor-id="None" code="0" name="None"/> <vendor
     * vendor-id="HP" code="11" name="Hewlett Packard"/> <vendor
     * vendor-id="Merit" code="61" name="Merit Networks"/> <vendor
     * vendor-id="Sun" code="42" name="Sun Microsystems, Inc."/> <vendor
     * vendor-id="USR" code="429" name="US Robotics Corp."/> <vendor
     * vendor-id="3GPP2" code="5535" name="3GPP2"/> <vendor vendor-id="TGPP"
     * code="10415" name="3GPP"/> <vendor vendor-id="TGPPCX" code="16777216"
     * name="3GPP CX/DX"/> <vendor vendor-id="Ericsson" code="193"
     * name="Ericsson"/> <vendor vendor-id="ETSI" code="13019" name="ETSI"/>
     * <vendor vendor-id="Vodafone" code="12645" name="Vodafone"/> <!--
     * *********************** End Vendors ************************** -->
     */
    // HashMap<String, String> vendorMap = new HashMap<String, String>();
    NodeList vendorNodes = doc.getElementsByTagName("vendor");

    for (int v = 0; v < vendorNodes.getLength(); v++) {

      Node vendorNode = vendorNodes.item(v);

      if (vendorNode.getNodeType() == Node.ELEMENT_NODE) {
        Element vendorElement = (Element) vendorNode;

        String vendorCode = vendorElement.getAttribute("code");
        String vendorId = vendorElement.getAttribute("vendor-id");

        vendorMap.put(vendorId, vendorCode);
      }
    }

    return;
  }

  protected void resolveWeakGroupedChildren() {
    // FIXME: we have maximum 50 runs, this does not take much time, limits
    // number of iterations over collection to fill all data.
    // this is due uncertainty - that data might have not been initialized
    // yet - but its somewhere in collections
    int runCount = 20;
    boolean haveWeaklings = true;
    while (haveWeaklings && runCount > 0) {
      boolean passed = true;

      for (AvpRepresentation groupedAvp : avpMap.values()) {
        if (!groupedAvp.isGrouped() || !groupedAvp.isWeak()) {
          continue;
        }
        if (resolveWeaklings(groupedAvp)) {
          passed = false;
        }
        else {
          // NOP?
        }
      }

      if (passed) {
        haveWeaklings = false;
      }

      runCount--;
    }
  }

  /**
   * @param groupedAvp
   * @return
   */
  protected boolean resolveWeaklings(AvpRepresentation groupedAvp) {

    // if we are here it means this avp rep is weak. its grouped for sure.
    boolean hasWeaklings = false;
    List<AvpRepresentation> children = groupedAvp.getChildren();
    for (int index = 0; index < children.size(); index++) {
      AvpRepresentation local = (AvpRepresentation) children.get(index);

      if (local.isWeak()) {
        // we should have strong representation somewhere.
        // AvpKey strongKey = new AvpKey(local.getCode(), local.getVendorId());
        AvpRepresentationImpl strongRep = null;
        AvpRepresentation strongKey = nameToCodeMap.get(local.getName().trim());
        if (strongKey == null) {
          logger.debug("No avp key representation for avp name: {}", local.getName().trim());
          hasWeaklings = true;
          continue;
        }

        strongRep = (AvpRepresentationImpl) avpMap.get(strongKey);

        if (strongRep == null || strongRep.isWeak()) {
          logger.trace("Resolving weak link for: {}; Strong representation for name: {} does not exist V:[{}]!", new Object[] { groupedAvp, local.getName(), strongRep });
          hasWeaklings = true;
        }
        else {
          try {
            strongRep = (AvpRepresentationImpl) strongRep.clone();
          }
          catch (CloneNotSupportedException e) {
            logger.error("Unable to clone AvpRepresentation", e);
          }
          strongRep.setMultiplicityIndicator(local.getMultiplicityIndicator());
          children.remove(index);
          children.add(index, strongRep);
        }
      }
      else {
        continue;
      }
    }

    if (!hasWeaklings) {
      ((AvpRepresentationImpl) groupedAvp).markWeak(false);
    }

    return hasWeaklings;
  }

  // Helper methods -----------------------------------------------------------

  public Map<AvpRepresentation, AvpRepresentation> getAvpMap() {
    return avpMap;
  }

  public Map<String, String> getVendorMap() {
    return vendorMap;
  }

  public Map<MessageRepresentation, MessageRepresentation> getCommandMap() {
    return commandMap;
  }

  public Map<String, String> getTypedefMap() {
    return typedefMap;
  }

  public Map<String, AvpRepresentation> getNameToCodeMap() {
    return nameToCodeMap;
  }

}
