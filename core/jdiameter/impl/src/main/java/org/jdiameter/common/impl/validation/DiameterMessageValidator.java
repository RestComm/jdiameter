package org.jdiameter.common.impl.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.client.impl.parser.MessageImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Start time:11:42:36 2009-05-26<br>
 * Project: diameter-parent<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class DiameterMessageValidator {

	private static final DiameterMessageValidator instance = new DiameterMessageValidator();
	private static final String fileName = "validator.xml";
	private Map<MessageRepresentation, MessageRepresentation> configuredMessageTypes = new TreeMap<MessageRepresentation, MessageRepresentation>();

	/**
	 * 	
	 */
	private DiameterMessageValidator() {
		parseConfiguration();
	}

	/**
	 * 
	 */
	private void parseConfiguration() {
		InputStream is = DiameterMessageValidator.class.getClassLoader().getResourceAsStream(fileName);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			doc.getDocumentElement().normalize();

			NodeList commandNodes = doc.getElementsByTagName("command");

			for (int v = 0; v < commandNodes.getLength(); v++) {
				try {
					Node cmdNode = commandNodes.item(v);

					if (cmdNode.getNodeType() == Node.ELEMENT_NODE) {
						Element cmdElement = (Element) cmdNode;

						String cmdCode = cmdElement.getAttribute("code");
						String applicationID = cmdElement.getAttribute("application");
						String request = cmdElement.getAttribute("request");

						MessageRepresentation msgRepresentation = new MessageRepresentation(Integer.valueOf(cmdCode), applicationID != null ? Long.valueOf(applicationID) : 0, Boolean.valueOf(request));
						Map<AvpRepresentation,AvpRepresentation> avpList = new HashMap<AvpRepresentation,AvpRepresentation>();
						msgRepresentation.setMessageAvps(avpList);
						configuredMessageTypes.put(msgRepresentation, msgRepresentation);

						NodeList cmdAvpNodes = cmdNode.getChildNodes();
						for (int index = 0; index < cmdAvpNodes.getLength(); index++) {
							try {
								Node avpNode = cmdAvpNodes.item(index);
								if (avpNode.getNodeType() == Node.ELEMENT_NODE) {

									Element avpElement = (Element) avpNode;
									String name = avpElement.getAttribute("name");
									String avpCode = avpElement.getAttribute("code");
									String avpVendor = avpElement.getAttribute("vendor");
									String multiPlicity = avpElement.getAttribute("multiplicity");
									String indexIndicator = avpElement.getAttribute("index");

									AvpRepresentation ap = new AvpRepresentation(Integer.valueOf(indexIndicator), Integer.valueOf(avpCode), avpVendor != null ? Long.valueOf(avpVendor) : 0,
											multiPlicity, name);

									avpList.put(ap,ap);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static final DiameterMessageValidator getInstance() {
		return DiameterMessageValidator.instance;
	}


	public void validate(MessageImpl msg) throws AvpNotAllowedException {
		MessageRepresentation rep = new MessageRepresentation(msg.getCommandCode(), msg.getApplicationId(), msg.isRequest());
		rep = this.configuredMessageTypes.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return;
		}

		rep.validate(msg);
	}
	public void validate(int commandCode, long appId, boolean isRequest,AvpSet destination, Avp avp)
	{
		MessageRepresentation rep = new MessageRepresentation(commandCode,appId,isRequest);
		rep = this.configuredMessageTypes.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return;
		}

		rep.validate(destination, avp);
	}
	
	public boolean isCountValidForMultiplicity(int commandCode, long appId, boolean isRequest,AvpSet destination, int avpCode,long avpVendor)
	{
		MessageRepresentation rep = new MessageRepresentation(commandCode,appId,isRequest);
		rep = this.configuredMessageTypes.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return true;
		}
		AvpSet innerSet = destination.getAvps(avpCode, avpVendor);
		int count = 0;
		if(innerSet!=null)
		{
			count+=innerSet.size();
		}
		
		return rep.isCountValidForMultiplicity(avpCode, avpVendor, count);
	}
	
	public boolean isAllowed(int commandCode, long appId, boolean isRequest, int avpCode,long avpVendor)
	{
		MessageRepresentation rep = new MessageRepresentation(commandCode,appId,isRequest);
		rep = this.configuredMessageTypes.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return true;
		}
		
		return rep.isAllowed(avpCode,avpVendor);
	}
}
