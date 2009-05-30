/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution statements
 * applied by the authors. All third-party contributions are distributed under
 * license by Red Hat Middleware LLC.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.mobicents.diameter.dictionary;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.jboss.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * AvpDictionary.java
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @version 1.0 
 */
public class AvpDictionary
{
  private static transient Logger logger = Logger.getLogger( AvpDictionary.class );

  public final static AvpDictionary INSTANCE = new AvpDictionary();

  private static HashMap<AvpKey, AvpRepresentation> avpMap = new HashMap<AvpKey, AvpRepresentation>();

  private static HashMap<String, String> vendorMap = new HashMap<String, String>();

  private static HashMap<String, String> commandMap = new HashMap<String, String>();

  private static HashMap<String, String> typedefMap = new HashMap<String, String>();

  private static HashMap<String, AvpKey> nameToCodeMap = new HashMap<String, AvpKey>();

  private AvpDictionary() {
    // Exists only to defeat instantiation.
  }

  public void parseDictionart(String filename) throws Exception
  {
    parseDictionary( new FileInputStream( filename ) );  
  }

  public void parseDictionary(InputStream is) throws Exception
  {
    long startTime = System.currentTimeMillis();

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating( false );
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document doc = db.parse( is );

    doc.getDocumentElement().normalize();

    /**************************************************************************
     *  VENDORS 
     */

    /*
     * <!-- ************************* Vendors **************************** -->
     * <vendor vendor-id="None" code="0" name="None"/>
     * <vendor vendor-id="HP"    code="11"    name="Hewlett Packard"/>
     * <vendor vendor-id="Merit" code="61" name="Merit Networks"/>
     * <vendor vendor-id="Sun" code="42" name="Sun Microsystems, Inc."/>
     * <vendor vendor-id="USR" code="429" name="US Robotics Corp."/>
     * <vendor vendor-id="3GPP2" code="5535" name="3GPP2"/>
     * <vendor vendor-id="TGPP"  code="10415" name="3GPP"/>
     * <vendor vendor-id="TGPPCX" code="16777216" name="3GPP CX/DX"/>
     * <vendor vendor-id="Ericsson"  code="193" name="Ericsson"/>
     * <vendor vendor-id="ETSI"  code="13019" name="ETSI"/>
     * <vendor vendor-id="Vodafone" code="12645" name="Vodafone"/>
     * <!-- *********************** End Vendors ************************** -->
     */

    NodeList vendorNodes = doc.getElementsByTagName("vendor");

    for (int v = 0; v < vendorNodes.getLength(); v++) {

      Node vendorNode = vendorNodes.item(v);

      if (vendorNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element vendorElement = (Element) vendorNode;

        String vendorCode = vendorElement.getAttribute( "code" );
        String vendorId = vendorElement.getAttribute( "vendor-id" );

        vendorMap.put( vendorId, vendorCode );
      }
    }

    /**************************************************************************
     * COMMAND-CODES
     */

    /*
     * <!-- *********************** Commands ***************************** -->
     * <!-- Diameter Base Protocol Command Codes -->
     * <command name="Capabilities-Exchange" code="257" vendor-id="None"/>
     * <command name="Re-Auth" code="258" vendor-id="None"/>
     * <command name="Accounting" code="271" vendor-id="None"/>
     * <command name="Abort-Session" code="274" vendor-id="None">
     * </command>
     * <command name="Session-Termination" code="275" vendor-id="None"/>
     * <command name="Device-Watchdog" code="280" vendor-id="None"/>
     * <command name="Disconnect-Peer" code="282" vendor-id="None"/>
     * <!-- ********************** End Commands ************************** -->
     */

    NodeList commandNodes = doc.getElementsByTagName("command");

    for (int c = 0; c < commandNodes.getLength(); c++)
    {
      Node vendorNode = commandNodes.item(c);

      if (vendorNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element vendorElement = (Element) vendorNode;

        String commandName = vendorElement.getAttribute( "name" );
        String commandCode = vendorElement.getAttribute( "code" );
        String commandVendorId = vendorElement.getAttribute( "vendor-id" );

        String commandVendorCode = vendorMap.get( commandVendorId );

        commandMap.put( commandVendorCode + "-" + commandCode, commandName );
      }
    }


    /**************************************************************************
     * TYPE DEFINITIONS
     */

    /*
     * <typedefn type-name="OctetString"/>
     * <typedefn type-name="UTF8String" type-parent="OctetString"/>
     * <typedefn type-name="VendorId" type-parent="Unsigned32"/>
     */

    NodeList typedefNodes = doc.getElementsByTagName("typedefn");

    for (int td = 0; td < typedefNodes.getLength(); td++)
    {
      Node typedefNode = typedefNodes.item(td);

      if (typedefNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element typedefElement = (Element) typedefNode;

        String typeName = typedefElement.getAttribute( "type-name" );
        String typeParent = typedefElement.getAttribute( "type-parent" );

        if(typeParent.equals("") || typeName.equals("UTF8String"))
          typeParent = typeName;

        typedefMap.put(typeName, typeParent);
      }
    }

    /**************************************************************************
     *  AVPs 
     */

    NodeList avpNodes = doc.getElementsByTagName("avp");

    for (int s = 0; s < avpNodes.getLength(); s++) {

      Node avpNode = avpNodes.item(s);

      /*
       * <!ELEMENT avp ((type | grouped), (enum*))>
       * <!ATTLIST avp
       *   name ID #REQUIRED
       *   description CDATA #IMPLIED
       *   code CDATA #REQUIRED
       *   may-encrypt (yes | no) "yes"
       *   mandatory (must | may | mustnot | shouldnot) "may"
       *   protected (must | may | mustnot | shouldnot) "may"
       *   vendor-bit (must | may | mustnot | shouldnot) "mustnot"
       *   vendor-id IDREF #IMPLIED
       *   constrained (true | false) "false"
       * >
       */
      if (avpNode.getNodeType() == Node.ELEMENT_NODE)
      {
        Element avpElement = (Element) avpNode;

        String avpName = avpElement.getAttribute("name");

        String avpDescription = avpElement.getAttribute("description");

        String avpCode = avpElement.getAttribute("code");

        String avpMayEncrypt = avpElement.getAttribute("may-encrypt");

        String avpMandatory = avpElement.getAttribute("mandatory");

        String avpProtected = avpElement.getAttribute("protected").equals("") ? "may" : avpElement.getAttribute("protected");

        String avpVendorBit = avpElement.getAttribute("vendor-bit");

        String avpVendorId = avpElement.getAttribute("vendor-id");

        String avpConstrained = avpElement.getAttribute("constrained");

        String avpType = "";

        // Now either we have type or grouped
        NodeList avpChildNodes = avpNode.getChildNodes();

        for (int j = 0; j < avpChildNodes.getLength(); j++)
        {
          Node avpChildNode = avpChildNodes.item(j);

          if (avpChildNode.getNodeType() == Node.ELEMENT_NODE)
          {
            Element avpChildElement = (Element) avpChildNode;

            if(avpChildElement.getNodeName().equals("grouped"))
            {
              // All we need to know is that's a grouped AVP.
              avpType = "Grouped";
            }
            else if(avpChildElement.getNodeName().equals("type"))
            {
              avpType = avpChildElement.getAttribute("type-name");
              avpType = typedefMap.get( avpType );
            }
          }
        }

        if(logger.isDebugEnabled())
        {
          logger.debug("Parsed AVP: Name[" + avpName + "] Description[" + avpDescription + "] Code[" + avpCode + "] May-Encrypt[" + avpMayEncrypt + "] Mandatory[" + avpMandatory +
              "] Protected [" + avpProtected + "] Vendor-Bit [" + avpVendorBit + "] Vendor-Id [" + avpVendorId + "] Constrained[" + avpConstrained + "] Type [" + avpType + "]");
        }
        try{
          AvpRepresentation avp=null;

          avp = new AvpRepresentation(avpName, avpDescription, Integer.valueOf(avpCode), 
              avpMayEncrypt.equals("yes"), avpMandatory, avpProtected, avpVendorBit, getVendorCode(avpVendorId), 
              avpConstrained.equals("true"), avpType);

          AvpKey mapKey = getMapKey(avp.getCode(), avp.getVendorId());
          
          avpMap.put( mapKey, avp );

          nameToCodeMap.put( avp.getName(), mapKey );
        }catch(Exception e)
        {
          logger.error("Parsed AVP: Name[" + avpName + "] Description[" + avpDescription + "] Code[" + avpCode + "] May-Encrypt[" + avpMayEncrypt + "] Mandatory[" + avpMandatory +
              "] Protected [" + avpProtected + "] Vendor-Bit [" + avpVendorBit + "] Vendor-Id [" + avpVendorId + "] Constrained[" + avpConstrained + "] Type [" + avpType + "]");
          e.printStackTrace();
        }
      }
    }

    long endTime = System.currentTimeMillis();

    logger.info( "AVP Dictionary :: Loaded in " + (endTime-startTime) + "ms == Vendors[" + vendorMap.size() + "] Commands[" + commandMap.size() + "] Types[" + typedefMap.size() + 
        "] AVPs[" + avpMap.size() + "]" );
  }

  public AvpRepresentation getAvp(int code)
  {
    return getAvp( code, 0 );
  }

  public AvpRepresentation getAvp(int code, long vendorId)
  {
    AvpRepresentation avp = avpMap.get(getMapKey(code, vendorId));
    
    if(avp == null) {
      logger.warn("AVP with code " + code + " and Vendor-Id "  + vendorId + " not present in dictionary!");
    }
    
    return avp;
  }

  public AvpRepresentation getAvp(String avpName)
  {
    AvpKey avpKey = nameToCodeMap.get(avpName);

    return avpKey != null ? avpMap.get(avpKey) : null;
  }

  private long getVendorCode(String vendorId)
  {
    long value = -1;
    
    if(vendorId == null)
    {
      value = 0;
    }
    else
    {
      String vendorCode = vendorMap.get(vendorId);
      
      value = vendorCode == null ? 0 : Long.parseLong(vendorCode); 
    }


    return value;
  }

  private AvpKey getMapKey(int avpCode, long vendorId)
  {
    return new AvpKey(avpCode, vendorId); 
  }
}
