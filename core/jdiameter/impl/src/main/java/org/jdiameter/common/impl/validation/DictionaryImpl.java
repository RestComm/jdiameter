 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

  public static Dictionary INSTANCE = getInstance("dictionary.xml");

  private static final String UNDEFINED_AVP_TYPE = "UNDEFINED";

  private static final String AVP_DEFAULT_INDEX = "-1";
  private static final String AVP_DEFAULT_MULTIPLICITY = AvpRepresentation._MP_ZERO_OR_MORE;

  public static final String _AVP_ATTRIBUTE_NAME = "name";
  public static final String _AVP_ATTRIBUTE_CODE = "code";
  public static final String _AVP_ATTRIBUTE_VENDOR = "vendor";
  public static final String _AVP_ATTRIBUTE_MULTIPLICITY = "multiplicity";
  public static final String _AVP_ATTRIBUTE_INDEX = "index";

  private Map<AvpRepresentation, AvpRepresentation> avpMap = new HashMap<AvpRepresentation, AvpRepresentation>();
  private Map<String, AvpRepresentation> avpByNameMap = new HashMap<String, AvpRepresentation>();

  private Map<String, String> vendorMap = new HashMap<String, String>();

  private Map<MessageRepresentation, MessageRepresentation> commandMap = new HashMap<MessageRepresentation, MessageRepresentation>();

  private Map<String, String> typedefMap = new HashMap<String, String>();

  private boolean configured = false;

  private DictionaryImpl(InputStream is) {
    init(is);
  }

  public static Dictionary getInstance(InputStream is) {
    if (is == null) {
      if (INSTANCE != null) {
        return INSTANCE;
      }
      // Maintaining 1.7.0 behaviour
      String confFile = "dictionary.xml";
      is = getInputStream(confFile);
    }
    if (INSTANCE != null) {
      ((DictionaryImpl) INSTANCE).init(is);
    }
    else {
      INSTANCE = new DictionaryImpl(is);
    }
    return INSTANCE;
  }

  public static Dictionary getInstance(String confFile) {
    if (confFile == null) {
      if (INSTANCE != null) {
        return INSTANCE;
      }
      confFile = "dictionary.xml";
    }
    InputStream is = getInputStream(confFile);
    return getInstance((InputStream) is);
  }

  private static InputStream getInputStream(String confFile) {
    InputStream is = null;

    try {
      is = DictionarySingleton.class.getResourceAsStream(confFile);
      if (is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in class classloader. Trying thread context class loader.", confFile);
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(confFile);
      }

      if (is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in thread context class loader. Trying using 'config/' prefix.", confFile);
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/" + confFile);
      }

      if (is == null) {
        logger.debug("Failed to locate dictionary configuration file: {}, in thread context class loader. Trying regular file.", confFile);
        File fDict = new File(confFile);
        if (fDict.exists()) {
          is = new FileInputStream(fDict);
        }
        else {
          logger.debug("Failed to locate dictionary configuration file: {}, from regular file. Trying using 'config/' prefix.", confFile);
          fDict = new File("config/" + confFile);
          if (fDict.exists()) {
            is = new FileInputStream(fDict);
          }
        }
      }
    }
    catch (FileNotFoundException fnfe) {
      logger.debug("Could not load configuration file: {}, from any known location.", confFile);
    }
    return is;
  }

  private void init(InputStream is) {

    try {
      if (is != null) {
        this.configure(is);
      }
      else {
        this.setEnabled(false);
        logger.warn("Failed to initialize and configure Diameter Dictionary since configuration file was not found. Validator is disabled.");
      }
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (IOException e) {
          logger.error("", e);
        }
      }
    }
  }

  // Parser functions ---------------------------------------------------------

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#configure(java.io.InputStream)
   */
  @Override
  public void configure(InputStream is) {
    if (is == null) {
      logger.error("No input stream to configure dictionary from?");
      return;
    }
    try {
      long startTime = System.currentTimeMillis();
      this.avpByNameMap = new TreeMap<String, AvpRepresentation>(new Comparator<String>() {
        @Override
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
      this.parseTypeDefs(doc);
      this.parseAvps(doc);
      this.parseCommands(doc);

      this.configured = true;

      long endTime = System.currentTimeMillis();

      if (logger.isInfoEnabled()) {
        logger.info("Mobicents Diameter Dictionary loaded in {}ms -- Vendors[{}] Commands[{}] Types[{}] AVPs[{}]",
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
   * Parses the <vendor /> attributes from a Dictionary XML Document
   *
   * @param doc the DOM object representing the XML Document with the Dictionary definitions
   */
  protected void parseVendors(Document doc) {
    // Parse vendors, we will need those.
    // Format: <vendor vendor-id="TGPP" code="10415" name="3GPP" />
    NodeList vendorNodes = doc.getElementsByTagName("vendor");

    for (int v = 0; v < vendorNodes.getLength(); v++) {
      Node vendorNode = vendorNodes.item(v);
      if (vendorNode.getNodeType() == Node.ELEMENT_NODE) {
        Element vendorElement = (Element) vendorNode;

        // Get the Code (number) and ID (string)
        String vendorCode = vendorElement.getAttribute("code");
        String vendorId = vendorElement.getAttribute("vendor-id");

        vendorMap.put(vendorId, vendorCode);
      }
    }
  }

  /**
   * Parses the <typedefn /> attributes from a Dictionary XML Document
   *
   * @param doc the DOM object representing the XML Document with the Dictionary definitions
   */
  protected void parseTypeDefs(Document doc) {
    // Parse type definitions. Handy to match against defined AVP types
    // and to fill AVPs with generic function.
    // Format: <typedefn type-name="Integer32"  />
    //         <typedefn type-name="Enumerated" type-parent="Integer32" />
    NodeList typedefNodes = doc.getElementsByTagName("typedefn");

    for (int td = 0; td < typedefNodes.getLength(); td++) {
      Node typedefNode = typedefNodes.item(td);
      if (typedefNode.getNodeType() == Node.ELEMENT_NODE) {
        Element typedefElement = (Element) typedefNode;

        String typeName = typedefElement.getAttribute("type-name");
        String typeParent = typedefElement.getAttribute("type-parent");

        // UTF8String and Time are special situations, we don't want to convert these.
        if (typeParent == null || typeParent.equals("") || typeName.equals("UTF8String") || typeName.equals("Time")) {
          typeParent = typeName;
        }

        typedefMap.put(typeName, typeParent);
      }
    }
  }

  /**
   * Parses the <typedefn /> attributes from a Dictionary XML Document
   *
   * @param doc the DOM object representing the XML Document with the Dictionary definitions
   */
  protected void parseAvps(Document doc) {
    // Format:  <avpdefn name="Talk-Burst-Volume" code="1256" vendor-id="TGPP" mandatory="must" protected="may" may-encrypt="true" vendor-bit="must" >
    //            <type type-name="Unsigned32" />
    //          </avpdefn>

    NodeList avpDefnNodes = doc.getElementsByTagName("avpdefn");

    for (int i = 0; i < avpDefnNodes.getLength(); i++) {
      Node avpNode = avpDefnNodes.item(i);
      Element avpDefnElement = (Element) avpNode;

      String avpName = avpDefnElement.getAttribute("name");
      String avpCode = avpDefnElement.getAttribute("code");
      String avpVendorId = avpDefnElement.getAttribute("vendor-id");

      String avpMandatory = avpDefnElement.getAttribute("mandatory");
      String avpProtected = avpDefnElement.getAttribute("protected").equals("") ? "may" : avpDefnElement.getAttribute("protected");
      String avpMayEncrypt = avpDefnElement.getAttribute("may-encrypt");
      String avpVendorBit = avpDefnElement.getAttribute("vendor-bit");

      long vendorCode = getVendorCode(avpVendorId);

      // Let's figure out the type
      // It can be:
      // <type type-name="UTF8String" />
      //  OR
      // <grouped> <avp name="PoC-Change-Time" multiplicity="1" /> ... </grouped>
      //  OR
      // <type type-name="Enumerated"> <enum code="0" name="MULTICAST" /> ... </enumerated>
      String avpOriginalType = UNDEFINED_AVP_TYPE;
      String avpType = avpOriginalType;
      List<AvpRepresentation> groupedAvpChilds = new ArrayList<AvpRepresentation>();

      NodeList avpDefnChildNodes = avpNode.getChildNodes();
      for (int j = 0; j < avpDefnChildNodes.getLength(); j++) {
        Node avpDefnChildNode = avpDefnChildNodes.item(j);

        if (avpDefnChildNode.getNodeType() == Node.ELEMENT_NODE) {
          Element avpDefnChildElement = (Element) avpDefnChildNode;

          if (avpDefnChildElement.getNodeName().equals("grouped")) {
            avpOriginalType = "Grouped";
            avpType = avpOriginalType;

            // Let's fetch the childs
            // Format: <avp name="PoC-Change-Time" multiplicity="1" />
            NodeList groupedAvpMembers = avpDefnChildElement.getChildNodes();

            for (int gChildIndex = 0; gChildIndex < groupedAvpMembers.getLength(); gChildIndex++) {
              Node groupedAvpChildNode = groupedAvpMembers.item(gChildIndex);

              if (groupedAvpChildNode.getNodeType() == Node.ELEMENT_NODE) {
                Element groupedAvpChildElement = (Element) groupedAvpChildNode;

                String childName = null;
                String childMultiplicity = AVP_DEFAULT_MULTIPLICITY;
                String childIndexIndicator = AVP_DEFAULT_INDEX;

                if (!groupedAvpChildElement.hasAttribute("name")) {
                  if (logger.isDebugEnabled()) {
                    logger.debug(new StringBuffer("[ERROR] Grouped child does not have name, grouped avp:  Name[").append(avpName).append("] Description[")
                        .append("").append("] Code[").append(avpCode).append("] May-Encrypt[").append(avpMayEncrypt).append("] Mandatory[")
                        .append(avpMandatory).append("] Protected [").append(avpProtected).append("] Vendor-Bit [").append(avpVendorBit).append("] Vendor-Id [")
                        .append(avpVendorId).append("] Constrained[").append("").append("] Type [").append(avpType).append("]").toString());
                  }
                  continue;
                }
                else {
                  childName = groupedAvpChildElement.getAttribute("name");
                }

                childMultiplicity = groupedAvpChildElement.hasAttribute("multiplicity") ?
                    groupedAvpChildElement.getAttribute("multiplicity") : AvpRepresentation._MP_ZERO_OR_MORE;

                childIndexIndicator = groupedAvpChildElement.hasAttribute("index") ?
                        groupedAvpChildElement.getAttribute("index") : "-1";

                // have we parsed this child definition already?
                AvpRepresentation childRep = this.avpByNameMap.get(childName);
                AvpRepresentationImpl child = null;
                if (childRep != null) {
                  try {
                    child = (AvpRepresentationImpl) childRep.clone();
                  }
                  catch (CloneNotSupportedException cnse) {
                    // It should not happen, but anyway
                    if (logger.isWarnEnabled()) {
                      logger.warn("Unable to clone AVP " + childRep, cnse);
                    }
                  }
                }
                else {
                  child = new AvpRepresentationImpl(childName, vendorCode);
                  child.markWeak(true);
                }
                child.setMultiplicityIndicator(childMultiplicity);
                child.markFixPosition(Integer.valueOf(childIndexIndicator));

                groupedAvpChilds.add(child);
              }
            }
          }
          else if (avpDefnChildElement.getNodeName().equals("type")) {
            avpOriginalType = avpDefnChildElement.getAttribute("type-name");
            avpType = avpOriginalType;
            //FIXME: baranowb: why this is like that? This changes type of AVP to primitive ONE..? Checks against type dont make sense, ie to check for Address type...
            avpType = typedefMap.get(avpType);

            if (avpType == null) {
              logger.warn("Unknown AVP Type ({}) for AVP with code {} and vendor-id {} ",
                  new Object[] { avpDefnChildElement.getAttribute("type-name"), avpCode, avpVendorId});
            }
          }
          else {
            logger.warn("Unknown AVP Definition child element for AVP with code {} and vendor-id {} ", avpCode, avpVendorId);
          }
        }
      }

      try {
        AvpRepresentationImpl avp = null;

        avp = new AvpRepresentationImpl(avpName, "N/A", Integer.valueOf(avpCode), avpMayEncrypt.equals("yes"), avpMandatory,
            avpProtected, avpVendorBit, vendorCode, avpOriginalType, avpType);

        if (avp.isGrouped()) {
          avp.setChildren(groupedAvpChilds);

          // we are not strong enough, children are referenced ONLY by name, so we are
          // weak until all children can be resolved to strong representation
          avp.markWeak(true);
        }

        resolveWeakLinks(avp);

        AvpRepresentation existingAvp = null;
        if ((existingAvp = avpMap.get(avp)) != null) {
          logger.warn("Duplicated AVP Definition for AVP Code: {}, Vendor-Id: {}. See TRACE logs for definitions.", avp.getCode(), avp.getVendorId());
          logger.trace("Existing AVP:\r\n {}\r\n New AVP:\r\n {}", existingAvp, avp);
        }
        else {
          avpMap.put(avp, avp);
        }

        AvpRepresentation oldAvp = avpByNameMap.put(avp.getName(), avp);

        if (oldAvp != null) {
          logger.debug("[WARN] Overwrited definition of AVP with the same name: Old: {}, New: {}", new Object[] { oldAvp, avp });
        }
      }
      catch (Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug(new StringBuffer("[ERROR] Failed Parsing AVP: Name[").append(avpName).append("] Description[").append("N/A").
              append("] Code[").append(avpCode).append("] May-Encrypt[").append(avpMayEncrypt).append("] Mandatory[").append(avpMandatory).
              append("] Protected [").append(avpProtected).append("] Vendor-Bit [").append(avpVendorBit).append("] Vendor-Id [").append(avpVendorId).
              append("] Constrained[").append("N/A").append("] OriginalType [").append(avpOriginalType).
              append("] Type [").append(avpType).append("]").toString(), e);
        }
      }
    }

    for (AvpRepresentation rep : avpMap.values()) {
      markWeaks((AvpRepresentationImpl) rep);
    }
  }

  private boolean markWeaks(AvpRepresentationImpl rep) {
    if (rep.isGrouped()) {
      boolean isWeak = false;
      for (AvpRepresentation repC : rep.getChildren()) {
        if (markWeaks((AvpRepresentationImpl) repC)) {
          isWeak = true;
        }
      }
      rep.markWeak(isWeak);
    }
    else {
      rep.markWeak(rep.getCode() == -1);
    }

    return rep.isWeak();
  }

  /**
   * For a given AVP resolves the weak links (where AVP definition in grouped
   * AVPs is not yet known, and only added by Name)
   *
   * @param newAvp the AVP which was just defined
   */
  private void resolveWeakLinks(AvpRepresentation newAvp) {
    for (AvpRepresentation avp : avpMap.values()) {
      if (avp.isGrouped()) {
        if (avp.getName().equals(newAvp.getName())) {
          continue;
        }
        List<AvpRepresentation> avpChilds = avp.getChildren();
        for (int n = 0; n < avpChilds.size(); n++) {
          AvpRepresentation avpChild = avpChilds.get(n);
          if (avpChild.getName().equals(newAvp.getName())) {
            try {
              AvpRepresentationImpl strongAvp = (AvpRepresentationImpl) newAvp.clone();
              strongAvp.setMultiplicityIndicator(avpChild.getMultiplicityIndicator());
              strongAvp.markFixPosition(avpChild.getPositionIndex());
              strongAvp.markWeak(false);

              avpChilds.set(n, strongAvp);

              resolveWeakLinks(avp);
            }
            catch (CloneNotSupportedException cnse) {
              // It should not happen, but anyway
              if (logger.isWarnEnabled()) {
                logger.warn("Unable to clone AVP " + newAvp, cnse);
              }
            }
          }
        }
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

            if (!commandElement.hasAttribute("request")) {
              logger.debug("[ERROR] Command for application: {} does not define if its request or answer, skipping.", applicationCode);
              continue;
            }
            String commandName = commandElement.getAttribute("name");
            String commandCode = commandElement.getAttribute("code");

            String isRequest = commandElement.getAttribute("request");

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
                  name = commandAvpElement.getAttribute("name");
                }

                if (!commandAvpElement.hasAttribute("multiplicity")) {
                  logger.debug("[WARN] Command defines avp without multiplicity.");
                  multiplicity = AvpRepresentation._MP_ZERO_OR_MORE;
                }
                else {
                  multiplicity = commandAvpElement.getAttribute("multiplicity");
                }

                index = commandAvpElement.hasAttribute("index") ? commandAvpElement.getAttribute("index") : "-1";

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
                  catch (CloneNotSupportedException cnse) {
                    // It should not happen, but anyway
                    if (logger.isWarnEnabled()) {
                      logger.warn("Unable to clone AVP " + strongRepresentation, cnse);
                    }
                  }
                }
                else {
                  logger.debug("[WARN] No strong avp for key {}, in name: {}", new Object[] {strongKey, name});
                  continue;
                }
              }
            }

            msg.setMessageAvps(commandAvpList);
          }
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#isConfigured()
   */
  @Override
  public boolean isConfigured() {
    return this.configured;
  }

  @Override
  public AvpRepresentation getAvp(int code) {
    return getAvp(code, 0);
  }

  @Override
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

  @Override
  public AvpRepresentation getAvp(String avpName) {
    return this.configured ? avpByNameMap.get(avpName) : null;
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
  @Override
  public MessageRepresentation getMessage(int commandCode, boolean isRequest) {
    return this.getMessage(commandCode, 0, isRequest);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#getMessage(int, long, boolean)
   */
  @Override
  public MessageRepresentation getMessage(int commandCode, long applicationId, boolean isRequest) {
    if (!this.configured) {
      return null;
    }
    MessageRepresentation key = new MessageRepresentationImpl(commandCode, applicationId, isRequest);
    return this.commandMap.get(key);
  }

  // Validation ---------------------------------------------------------------

  private boolean enabled = true;
  private ValidatorLevel sendValidationLevel = ValidatorLevel.ALL;
  private ValidatorLevel receiveValidationLevel = ValidatorLevel.OFF;

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#isValidate()
   */
  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#getSendLevel()
   */
  @Override
  public ValidatorLevel getSendLevel() {
    return this.sendValidationLevel;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.validation.Dictionary#getReceiveLevel()
   */
  @Override
  public ValidatorLevel getReceiveLevel() {
    return this.receiveValidationLevel;
  }

  @Override
  public void setSendLevel(ValidatorLevel level) {
    this.sendValidationLevel = level;
  }

  @Override
  public void setReceiveLevel(ValidatorLevel level) {
    this.receiveValidationLevel = level;
  }

  @Override
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setConfigured(boolean configured) {
    this.configured = configured;
  }

  @Override
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
    return avpByNameMap;
  }

  protected void printAvpTree(AvpRepresentation rep, String tab) {
    String x = tab + "+-- " + rep.getCode() + "/" + rep.getVendorId();
    while (x.length() < 25) {
      x += ".";
    }
    System.out.println(x + rep.getName() + " > " + rep.getType());
    if (rep.isGrouped()) {
      for (AvpRepresentation repC : rep.getChildren()) {
        printAvpTree(repC, "  " + tab);
      }
    }
  }

}
