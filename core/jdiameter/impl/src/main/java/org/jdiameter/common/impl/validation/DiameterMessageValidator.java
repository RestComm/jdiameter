/*
 * Mobicents, Communications Middleware
 *
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package org.jdiameter.common.impl.validation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @since 1.5.189
 */
public class DiameterMessageValidator {

	private static final transient Logger log = LoggerFactory.getLogger(DiameterMessageValidator.class);

	public static final String _AVP_ATTRIBUTE_NAME = "name";
	public static final String _AVP_ATTRIBUTE_CODE = "code";
	public static final String _AVP_ATTRIBUTE_VENDOR = "vendor";
	public static final String _AVP_ATTRIBUTE_MULTIPLICITY = "multiplicity";
	public static final String _AVP_ATTRIBUTE_INDEX = "index";
	public static final String _VALIDATOR_NODE_NAME = "validator";
	public static final String _VALIDATOR_NODE_ENABLED_ATTR = "enabled";

	private static final DiameterMessageValidator instance = new DiameterMessageValidator();
	private static final String fileName = "dictionary.xml";

	private boolean on = true;
	private boolean configured = false;
	protected Map<VMessageRepresentation, VMessageRepresentation> commandMap = new HashMap<VMessageRepresentation, VMessageRepresentation>();
	protected Map<VAvpRepresentation, VAvpRepresentation> avpMap = new HashMap<VAvpRepresentation, VAvpRepresentation>();
	protected Map<String, String> vendorMap = new HashMap<String, String>();
	protected Map<String, String> typedefMap = new HashMap<String, String>();
	protected Map<String, VAvpRepresentation> nameToCodeMap = new TreeMap<String, VAvpRepresentation>(new Comparator<String>() {

		public int compare(String o1, String o2) {
			if (o1 == null) {
				return 1;
			} else if (o2 == null) {
				return -1;
			}
			return o1.compareTo(o2);
		}
	});

	/**
   * 	
   */
	protected DiameterMessageValidator() {
		try {
			InputStream is = DiameterMessageValidator.class.getClassLoader().getResourceAsStream(fileName);
			if (is != null) {
				parseConfiguration(is, false);
			} else {
				log.error("Failed to init validator dictionary resources. No resource in CP: {}", fileName);
			}
		} catch (Exception e) {
			log.error("Failed to init validator dictionary resources.", e);
		}
	}


	public void parseConfiguration(InputStream is, boolean reset) {
		long startTime = System.currentTimeMillis();

		if (reset) {
			this.configured = false;
			this.commandMap.clear();
			this.avpMap.clear();
			this.vendorMap.clear();
			this.typedefMap.clear();
			this.nameToCodeMap.clear();
		}

		if (this.configured) {
			return;
		}

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			doc.getDocumentElement().normalize();

			this.vendorMap = parseVendors(doc);
			this.typedefMap = parseTypDefs(doc);
			this.avpMap = parseAvps(doc, this.vendorMap);
			this.resolveWeakGroupedChildren(this.avpMap, this.nameToCodeMap);
			this.commandMap = parseCommands(doc, this.avpMap, this.nameToCodeMap);
			this.configured = true;

			NodeList validatorNodeList = doc.getElementsByTagName(_VALIDATOR_NODE_NAME);
			if (validatorNodeList == null || validatorNodeList.getLength() == 0) {
				this.on = false;
			} else {
				boolean found = false;
				for (int index = 0; index < validatorNodeList.getLength(); index++) {
					if (validatorNodeList.item(index).getNodeType() == Node.ELEMENT_NODE) {
						found = true;
						Element validatorElement = (Element) validatorNodeList.item(index);
						if (!validatorElement.hasAttribute(_VALIDATOR_NODE_ENABLED_ATTR)) {
							this.on = false;
						} else {
							this.on = Boolean.parseBoolean(validatorElement.getAttribute(_VALIDATOR_NODE_ENABLED_ATTR));
						}
						break;
					}
				}
				if (!found) {
					this.on = false;
				}
			}
			long endTime = System.currentTimeMillis();

			log.info("AVP Validator :: Loaded in {}ms == Vendors[{}] Commands[{}] Types[{}] AVPs[{}]", new Object[] { (endTime - startTime), vendorMap.size(), commandMap.size(), typedefMap.size(),
					avpMap.size() });

			if (log.isInfoEnabled()) {
				StringBuffer sb = new StringBuffer();
				int c = 0;
				for (VAvpRepresentation key : this.avpMap.keySet()) {
					if (this.avpMap.get(key).isWeak()) {
						c++;
						sb.append("---------------------------------\n").append("Found incomplete AVP definition:\n").append(this.avpMap.get(key)).append("\n");
					}
				}
				sb.append("------- TOTAL INCOMPLETE AVPS COUNT: " + c + " -------");
				
				
				

				log.info(sb.toString());
				
				
				
				
			}
		} catch (Exception e) {
			on = false;
			log.error("Failed to parse validator configuration. Validator disabled.", e);
		}
	}

	/**
	 * @param doc
	 * @param nameToCode
	 * @param avpMap
	 * @return
	 */
	private Map<VMessageRepresentation, VMessageRepresentation> parseCommands(Document doc, Map<VAvpRepresentation, VAvpRepresentation> avpMap, Map<String, VAvpRepresentation> nameToCodeMap) {
		// here all grouped avps should have proper filling.
		// now lets go through message definition, we have to respect
		// application nodes
		NodeList applicationNodes = doc.getElementsByTagName("application");
		Map<VMessageRepresentation, VMessageRepresentation> commandMap = new HashMap<VMessageRepresentation, VMessageRepresentation>();
		for (int applicationIndex = 0; applicationIndex < applicationNodes.getLength(); applicationIndex++) {
			if (applicationNodes.item(applicationIndex).getNodeType() == Node.ELEMENT_NODE) {
				Element applicationElement = (Element) applicationNodes.item(applicationIndex);

				if (!applicationElement.hasAttribute("id")) {
					log.error("Application definition does not have ID, skipping message");
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
							if (log.isDebugEnabled()) {
								log.debug("Command for application: {} does not define if its request or answer, skipping.", applicationCode);
							}
							continue;
						}
						String commandName = commandElement.getAttribute("name");
						String commandCode = commandElement.getAttribute("code");
						// String commandVendorId =
						// commandElement.getAttribute("vendor-id");
						String isRequest = commandElement.getAttribute("request");
						// FIXME: should commandVendorId should be used
						// somewhere?
						// String commandVendorCode =
						// vendorMap.get(commandVendorId);
						VMessageRepresentation msg = new VMessageRepresentation(Integer.valueOf(commandCode), applicationCode, Boolean.parseBoolean(isRequest), commandName);

						Map<VAvpRepresentation, VAvpRepresentation> commandAvpList = new HashMap<VAvpRepresentation, VAvpRepresentation>();
						msg.setMessageAvps(commandAvpList);
						commandMap.put(msg, msg);

						// now we have to process avp defs for this message :)
						NodeList commandAvpsList = commandElement.getElementsByTagName("avp");
						{
							for (int commandAvpIndex = 0; commandAvpIndex < commandAvpsList.getLength(); commandAvpIndex++) {
								if (commandAvpsList.item(commandAvpIndex).getNodeType() == Node.ELEMENT_NODE) {
									Element commandAvpElement = (Element) commandAvpsList.item(commandAvpIndex);
									String multiplicity = null;
									String name = null;
									String index = null;
									if (!commandAvpElement.hasAttribute("name")) {
										log.error("Command defines avp without name! Command: {}, Code: {}, ApplicationID: {}", new Object[] { msg.getName(), msg.getCommandCode(),
												msg.getApplicationId() });
										continue;
									} else {
										name = commandAvpElement.getAttribute("name").trim();
									}

									if (!commandAvpElement.hasAttribute("multiplicity")) {
										log.warn("Command defines avp without multiplicity.");
										multiplicity = VAvpRepresentation._MP_ZERO_OR_MORE;
									} else {
										multiplicity = commandAvpElement.getAttribute("multiplicity");
									}
									if (!commandAvpElement.hasAttribute("index")) {
										index = "-1";
									} else {
										index = commandAvpElement.getAttribute("index");
									}

									// here we have name and multiplicity. we
									// have to get avp def from name, clone and
									// set multiplicity.
									VAvpRepresentation strongRepresentation = null;
									VAvpRepresentation strongKey = nameToCodeMap.get(name.trim());
									if (strongKey == null) {
										log.error("No strong avp key representation for msg, name: {}", name.trim());
										continue;
									}
									strongRepresentation = this.avpMap.get(strongKey);
									if (strongRepresentation != null && !strongRepresentation.isWeak()) {
										VAvpRepresentation clone;
										try {
											clone = (VAvpRepresentation) strongRepresentation.clone();
											clone.setMultiplicityIndicator(multiplicity);
											clone.markFixPosition(Integer.valueOf(index));
											commandAvpList.put(clone, clone);
										} catch (CloneNotSupportedException e) {
											log.error("Unable to clone VAvpRepresentation", e);
										}
									} else {
										log.error("No strong avp representation for msg: {}", strongKey);
									}
								}
							}
						}
					}
				}
			}
		}
		return commandMap;
	}

	/**
	 * @param doc
	 * @param vendorMap
	 * @return
	 */
	public Map<VAvpRepresentation, VAvpRepresentation> parseAvps(Document doc, Map<String, String> vendorMap) {
		// now, lets process avps, we ignore <application> boundries, since avps
		// are unique by: name, code, vendor-name/mapped to id here
		// once we have this set, we will resolve weak avps, than we can process
		// messages, and based on avp name populate message with proper
		// representations.

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
					long vendorCode = getVendorCode(avpVendorId, vendorMap);
					String avpConstrained = avpElement.getAttribute("constrained");

					// So it shows, clearly we mess up some where.
					String avpType = "NOT-SET";
					List<VAvpRepresentation> weakGroupedAvpChildren = new ArrayList<VAvpRepresentation>();
					// Now either we have type or grouped
					NodeList avpChildNodes = avpNode.getChildNodes();

					for (int j = 0; j < avpChildNodes.getLength(); j++) {
						Node avpChildNode = avpChildNodes.item(j);

						if (avpChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element avpChildElement = (Element) avpChildNode;

							if (avpChildElement.getNodeName().equals("grouped")) {
								// All we need to know is that's a grouped AVP.
								avpType = "Grouped";
								// we ceate a bunch on weak avp reps.

								NodeList groupedAvpMembers = avpChildElement.getChildNodes();
								for (int gChildIndex = 0; gChildIndex < groupedAvpMembers.getLength(); gChildIndex++) {
									Node groupedAvpChildNode = groupedAvpMembers.item(gChildIndex);
									if (groupedAvpChildNode.getNodeType() == Node.ELEMENT_NODE) {
										// we have our member
										Element groupedChildWeakElement = (Element) groupedAvpChildNode;
										String name = null;
										String multiplicity = VAvpRepresentation._MP_ZERO_OR_MORE;
										String indexIndicator = "-1";
										if (!groupedChildWeakElement.hasAttribute("name")) {
											log.error("Grouped child does not have name, grouped avp:  Name[" + avpName + "] Description[" + avpDescription + "] Code[" + avpCode + "] May-Encrypt["
													+ avpMayEncrypt + "] Mandatory[" + avpMandatory + "] Protected [" + avpProtected + "] Vendor-Bit [" + avpVendorBit + "] Vendor-Id [" + avpVendorId
													+ "] Constrained[" + avpConstrained + "] Type [" + avpType + "]");
											continue;
										} else {
											name = groupedChildWeakElement.getAttribute("name").trim();
										}

										if (!groupedChildWeakElement.hasAttribute("multiplicity")) {
											multiplicity = VAvpRepresentation._MP_ZERO_OR_MORE;
										} else {
											multiplicity = groupedChildWeakElement.getAttribute("multiplicity");
										}
										if (!groupedChildWeakElement.hasAttribute("index")) {
											indexIndicator = "-1";
										} else {
											indexIndicator = groupedChildWeakElement.getAttribute("index");
										}

										VAvpRepresentation weakChild = new VAvpRepresentation(name, vendorCode);
										weakChild.setMultiplicityIndicator(multiplicity);
										weakChild.markFixPosition(Integer.valueOf(indexIndicator));
										// just to be sure
										weakChild.markWeak(true);
										weakGroupedAvpChildren.add(weakChild);
									}
								}
							} else if (avpChildElement.getNodeName().equals("type")) {
								avpType = avpChildElement.getAttribute("type-name");
								avpType = typedefMap.get(avpType);
							} else if (avpChildElement.getNodeName().equals("enum")) {
								// NOP?
							}
						}
					}

					if (log.isDebugEnabled()) {
						log.debug("Parsed AVP: Name[" + avpName + "] Description[" + avpDescription + "] Code[" + avpCode + "] May-Encrypt[" + avpMayEncrypt + "] Mandatory[" + avpMandatory
								+ "] Protected [" + avpProtected + "] Vendor-Bit [" + avpVendorBit + "] Vendor-Id [" + avpVendorId + "] Constrained[" + avpConstrained + "] Type [" + avpType + "]");
					}
					try {
						VAvpRepresentation avp = null;

						avp = new VAvpRepresentation(avpName.trim(), avpDescription, Integer.valueOf(avpCode), avpMayEncrypt.equals("yes"), avpMandatory, avpProtected, avpVendorBit, vendorCode,
								avpConstrained.equals("true"), avpType);

						if (avp.isGrouped()) {
							avp.setChildren(weakGroupedAvpChildren);
							// we are not strong enough., children are
							// referenced ONLY by name, so we are
							// weak until all children can be resolved to strong
							// representation
							avp.markWeak(true);
						}
						VAvpRepresentation mapKey = new VAvpRepresentation(avp.getCode(), avp.getVendorId());

						avpMap.put(mapKey, avp);
						if(nameToCodeMap.containsKey(avp.getName().trim()) && log.isErrorEnabled())
						{
							log.error("Overwriting definition of avp(same name) , present: {}, new one: {}",new Object[]{nameToCodeMap.get(avp.getName().trim()),mapKey});
						}
						nameToCodeMap.put(avp.getName().trim(), mapKey);
					} catch (Exception e) {
						if (log.isErrorEnabled()) {
							log.error("Failed Parsing AVP: Name[" + avpName + "] Description[" + avpDescription + "] Code[" + avpCode + "] May-Encrypt[" + avpMayEncrypt + "] Mandatory["
									+ avpMandatory + "] Protected [" + avpProtected + "] Vendor-Bit [" + avpVendorBit + "] Vendor-Id [" + avpVendorId + "] Constrained[" + avpConstrained + "] Type ["
									+ avpType + "]", e);
						}
					}
				}
			}

		}
		return avpMap;
	}

	/**
	 * @param doc
	 * @return
	 */
	public Map<String, String> parseTypDefs(Document doc) {
		// here we have full base of vendor-name --> vendorId(long) map.
		// Now lets parse type defs, dunno why, but we do that, so we can std
		// out it :)
		/*
		 * <typedefn type-name="OctetString"/> <typedefn type-name="UTF8String"
		 * type-parent="OctetString"/> <typedefn type-name="VendorId"
		 * type-parent="Unsigned32"/>
		 */

		NodeList typedefNodes = doc.getElementsByTagName("typedefn");
		HashMap<String, String> typedefMap = new HashMap<String, String>();
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

		return typedefMap;
	}

	/**
	 * @param doc
	 * @return
	 */
	public Map<String, String> parseVendors(Document doc) {
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
		HashMap<String, String> vendorMap = new HashMap<String, String>();
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

		return vendorMap;
	}

	public void resolveWeakGroupedChildren(Map<VAvpRepresentation, VAvpRepresentation> avpMap, Map<String, VAvpRepresentation> nameToCodeMap) {
		// FIXME: we have maximum 50 runs, this does not take much time, limits
		// number of iterations over collection to fill all data.
		// this is due uncertanity - that data might have not been initialized
		// yet - but its somewhere in collections
		int runCount = 20;
		boolean haveWeaklings = true;
		while (haveWeaklings && runCount > 0) {
			boolean passed = true;
			
			for (VAvpRepresentation groupedAvp : avpMap.values()) {
				if (!groupedAvp.isGrouped() || !groupedAvp.isWeak()) {
					continue;
				}
				if (resolveWeaklings(groupedAvp, avpMap, nameToCodeMap)) {
					passed = false;
				} else {
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
	protected boolean resolveWeaklings(VAvpRepresentation groupedAvp, Map<VAvpRepresentation, VAvpRepresentation> avpMap, Map<String, VAvpRepresentation> nameToCodeMap) {

		// if we are here it means this avp rep is weak. its grouped for sure.
		boolean hasWeaklings = false;
		List<VAvpRepresentation> children = groupedAvp.getChildren();
		for (int index = 0; index < children.size(); index++) {
			VAvpRepresentation local = (VAvpRepresentation) children.get(index);

			if (local.isWeak()) {
				// we shoud have strong rep somewhere.
				// AvpKey strongKey = new AvpKey(local.getCode(),
				// local.getVendorId());
				VAvpRepresentation strongRep = null;
				VAvpRepresentation strongKey = nameToCodeMap.get(local.getName().trim());
				if (strongKey == null) {
					log.debug("No avp key representation for avp name: {}", local.getName().trim());
					hasWeaklings = true;
					continue;
				}

				strongRep = avpMap.get(strongKey);

				if (strongRep == null || strongRep.isWeak()) {
					log.debug("Resolving weak link for: {}; Strong representation for name: {} does not exist V:[{}]!", new Object[] { groupedAvp, local.getName(), strongRep });
					hasWeaklings = true;
				} else {
					try {
						strongRep = (VAvpRepresentation) strongRep.clone();
					} catch (CloneNotSupportedException e) {
						log.error("Unable to clone VAvpRepresentation", e);
					}
					strongRep.setMultiplicityIndicator(local.getMultiplicityIndicator());
					children.remove(index);
					children.add(index, strongRep);
				}
			} else {
				continue;
			}
		}
		if (!hasWeaklings) {
			groupedAvp.markWeak(false);
		}

		return hasWeaklings;
	}

	public long getVendorCode(String vendorId, Map<String, String> vendorMap) {
		long value = -1;

		if (vendorId == null) {
			value = 0;
		} else {
			String vendorCode = vendorMap.get(vendorId);

			value = vendorCode == null ? 0 : Long.parseLong(vendorCode);
		}

		return value;
	}

	/**
	 * Retrieves singleton instance of DiameterMessageValidator
	 * 
	 * @return
	 */
	public static final DiameterMessageValidator getInstance() {
		return DiameterMessageValidator.instance;
	}

	/**
	 * Determines if validator is enabled.
	 * 
	 * @return <ul>
	 *         <li><b>true</b> if validator is enabled</li>
	 *         <li><b>false</b> if validator is disabled</li>
	 *         </ul>
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * Valdiates message against XML configuration file. If there is no
	 * representation it does nothing. If
	 * {@link DiameterMessageValidator#hasRepresentation(int, long, boolean, int, long)}
	 * returns false this method returns always without exception.
	 * 
	 * @param msg
	 * @throws JAvpNotAllowedException
	 *             - thrown when validation fails.
	 */
	public void validate(Message msg) throws JAvpNotAllowedException {
		if (!on) {
			return; // throw new IllegalStateException("validation is of.");
		}

		VMessageRepresentation rep = new VMessageRepresentation(msg.getCommandCode(), msg.getApplicationId(), msg.isRequest());
		rep = this.commandMap.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			log.warn("Validation could not be performed for Command. Code={}, Application-Id={}, Req={}", new Object[] { msg.getCommandCode(), msg.getApplicationId(), msg.isRequest() });
			return;
		}

		rep.validate(msg);
	}

	/**
	 * Validate if avp can be added/present in message - meaning it checks if
	 * there is place for passed avp. If
	 * {@link DiameterMessageValidator#hasRepresentation(int, long, boolean, int, long)}
	 * returns false this method returns always without exception.
	 * 
	 * @param commandCode
	 *            - message command code
	 * @param appId
	 *            - application id of message
	 * @param isRequest
	 *            - true if message is request.
	 * @param destination
	 *            - AvpSet of message
	 * @param avp
	 *            - avp to be checked.
	 */
	public void validate(int commandCode, long appId, boolean isRequest, AvpSet destination, Avp avp) {
		if (!on)
			throw new IllegalStateException("validation is of.");
		VMessageRepresentation rep = new VMessageRepresentation(commandCode, appId, isRequest);
		rep = this.commandMap.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return;
		}

		rep.validate(destination, avp);
	}

	/**
	 * Determines if avp identified by code and vendor has correct multiplicity
	 * in passed set. If
	 * {@link DiameterMessageValidator#hasRepresentation(int, long, boolean, int, long)}
	 * returns false this method returns always true.
	 * 
	 * @param commandCode
	 *            - message code
	 * @param appId
	 *            - message application id.
	 * @param isRequest
	 *            - true if message is request.
	 * @param destination
	 *            - message AvpSet
	 * @param avpCode
	 *            - avp code
	 * @param avpVendor
	 *            - avp vendor - zero if there is none
	 * @return<ul> <li><b>true</b> if multiplicity is correct</li> <li>
	 *             <b>false</b> if multiplicity is incorrect</li> </ul>
	 */
	public boolean isCountValidForMultiplicity(int commandCode, long appId, boolean isRequest, AvpSet destination, int avpCode, long avpVendor) {
		if (!on)
			throw new IllegalStateException("validation is of.");
		VMessageRepresentation rep = new VMessageRepresentation(commandCode, appId, isRequest);
		rep = this.commandMap.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return true;
		}
		AvpSet innerSet = destination.getAvps(avpCode, avpVendor);
		// FIXME: 1 is for avp beeing added
		int count = 1;
		if (innerSet != null) {
			count += innerSet.size();
		}

		return rep.isCountValidForMultiplicity(avpCode, avpVendor, count);
	}

	/**
	 * Determines if avp is allowed in message. If
	 * {@link DiameterMessageValidator#hasRepresentation(int, long, boolean, int, long)}
	 * returns false this method returns always true.
	 * 
	 * @param commandCode
	 *            - message command code
	 * @param appId
	 *            - message application id
	 * @param isRequest
	 *            - true if message is request.
	 * @param avpCode
	 *            - avp code.
	 * @param avpVendor
	 *            - avp vendor, zero if none.
	 * @return
	 */
	public boolean isAllowed(int commandCode, long appId, boolean isRequest, int avpCode, long avpVendor) {
		if (!on)
			throw new IllegalStateException("Message validation is disabled.");
		VMessageRepresentation rep = new VMessageRepresentation(commandCode, appId, isRequest);
		rep = this.commandMap.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return true;
		}

		return rep.isAllowed(avpCode, avpVendor);
	}

	/**
	 * Return values is computed as follows:<br>
	 * return messages.get(commandCode, appId, isRequest)!=null &&
	 * messages.get(commandCode , appId, isRequest).getAvp(avpCode,
	 * avpVendor)!=null;
	 * 
	 * @param commandCode
	 *            - message command code
	 * @param appId
	 *            - message application id
	 * @param isRequest
	 *            - true if message is request.
	 * @param avpCode
	 *            - avp code.
	 * @param avpVendor
	 *            - avp vendor, zero if none.
	 * @return
	 */
	public boolean hasRepresentation(int commandCode, long appId, boolean isRequest, int avpCode, long avpVendor) {
		VMessageRepresentation rep = new VMessageRepresentation(commandCode, appId, isRequest);
		rep = this.commandMap.get(rep);
		if (rep == null) {
			// no notion, lets leave it.
			return false;
		}

		return rep.hasRepresentation(avpCode, avpVendor);
	}

	// SETTER GETTER SECTION -- allows to change configuration

	// FIXME: add clone ops
	public Map<VMessageRepresentation, VMessageRepresentation> getCommandMap() {
		return commandMap;
	}

	public void setCommandMap(Map<VMessageRepresentation, VMessageRepresentation> commandMap) {
		this.commandMap = commandMap;
	}

	public Map<VAvpRepresentation, VAvpRepresentation> getAvpMap() {
		return avpMap;
	}

	public void setAvpMap(Map<VAvpRepresentation, VAvpRepresentation> avpMap) {
		this.avpMap = avpMap;
	}

	public Map<String, String> getVendorMap() {
		return vendorMap;
	}

	public void setVendorMap(Map<String, String> vendorMap) {
		this.vendorMap = vendorMap;
	}

	public Map<String, String> getTypedefMap() {
		return typedefMap;
	}

	public void setTypedefMap(Map<String, String> typedefMap) {
		this.typedefMap = typedefMap;
	}

	public Map<String, VAvpRepresentation> getNameToCodeMap() {
		return nameToCodeMap;
	}

	public void setNameToCodeMap(Map<String, VAvpRepresentation> nameToCodeMap) {
		this.nameToCodeMap = nameToCodeMap;
	}

	public boolean isConfigured() {
		return configured;
	}
}
