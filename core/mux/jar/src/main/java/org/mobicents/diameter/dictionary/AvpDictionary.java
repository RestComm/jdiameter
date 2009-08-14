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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.jboss.logging.Logger;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.common.impl.validation.VAvpRepresentation;
import org.jdiameter.common.impl.validation.VMessageRepresentation;

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

  private HashMap<AvpRepresentation, AvpRepresentation> avpMap = new HashMap<AvpRepresentation, AvpRepresentation>();

  private HashMap<String, String> vendorMap = new HashMap<String, String>();

  private HashMap<MessageRepresentation, MessageRepresentation> commandMap = new HashMap<MessageRepresentation, MessageRepresentation>();

  private HashMap<String, String> typedefMap = new HashMap<String, String>();

  private Map<String, AvpRepresentation> nameToCodeMap = new TreeMap<String, AvpRepresentation>(new Comparator<String>(){

	public int compare(String o1, String o2) {
		if(o1==null)
		{
			return 1;
		}else if(o2 == null)
		{
			return -1;
		}
		return o1.compareTo(o2);
	}});

  private AvpDictionary() {
    // Exists only to defeat instantiation.
  }

  public void parseDictionart(String filename) throws Exception
  {
    parseDictionary( new FileInputStream( filename ) );  
  }

  public void parseDictionary(InputStream is) throws Exception {
		long startTime = System.currentTimeMillis();

		DiameterMessageValidator instance = DiameterMessageValidator.getInstance();
		//we override default conf here.
		instance.parseConfiguration(is,true);
		this.commandMap.clear();
		this.avpMap.clear();
		this.vendorMap.clear();
		this.typedefMap.clear();
		this.nameToCodeMap.clear();
		//now we have to change some things a bit, since validator stores everything as: VxxxRepresentation, our user expect classes from dictionary package
		this.vendorMap.putAll(instance.getVendorMap());
		this.typedefMap.putAll(instance.getTypedefMap());
		Map<VMessageRepresentation,VMessageRepresentation> validatorCommandMap = instance.getCommandMap();
		for(VMessageRepresentation key:validatorCommandMap.keySet())
		{
			VMessageRepresentation value = validatorCommandMap.get(key);
			this.commandMap.put(new MessageRepresentation((VMessageRepresentation) key.clone()), new MessageRepresentation((VMessageRepresentation) value.clone()));
		}
		Map<String, VAvpRepresentation> validatorNameToCodeMap = instance.getNameToCodeMap();
		for(String key:validatorNameToCodeMap.keySet())
		{
			VAvpRepresentation value = validatorNameToCodeMap.get(key);
			this.nameToCodeMap.put(key, new AvpRepresentation((VAvpRepresentation)value.clone()));
		}
		Map<VAvpRepresentation, VAvpRepresentation> validatorAvpMap = instance.getAvpMap();
		for(VAvpRepresentation key:validatorAvpMap.keySet())
		{
			VAvpRepresentation value = validatorAvpMap.get(key);
			this.avpMap.put(new AvpRepresentation((VAvpRepresentation) key.clone()), new AvpRepresentation((VAvpRepresentation) value.clone()));
		}
		long endTime = System.currentTimeMillis();
		
		logger.info("AVP Dictionary :: Loaded in " + (endTime - startTime) + "ms == Vendors[" + vendorMap.size() + "] Commands[" + commandMap.size() + "] Types[" + typedefMap.size() + "] AVPs["
				+ avpMap.size() + "]");

		if (logger.isInfoEnabled()) {
			StringBuffer sb = new StringBuffer();
			int c = 0;
			for (AvpRepresentation key : this.avpMap.keySet()) {
				if (this.avpMap.get(key).isWeak()) {
					c++;
					sb.append("---------------------------------\n").append("Found incomplete AVP definition:\n").append(this.avpMap.get(key)).append("\n");
				}

			}

			sb.append("------- TOTAL INCOMPLETE AVPS COUNT: " + c + " -------");
			logger.info(sb.toString());
		}
	
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
	  AvpRepresentation avpKey = nameToCodeMap.get(avpName);

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

  private AvpRepresentation getMapKey(int avpCode, long vendorId)
  {
    return new AvpRepresentation(avpCode, vendorId); 
  }
}
