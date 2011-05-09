/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.server.impl;

import static org.jdiameter.api.PeerState.DOWN;
import static org.jdiameter.api.PeerState.INITIAL;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.Message;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.Realm;
import org.jdiameter.api.Request;
import org.jdiameter.api.ResultCode;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.IRequest;
import org.jdiameter.client.api.ISessionFactory;
import org.jdiameter.client.api.controller.IRealm;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.client.api.fsm.IContext;
import org.jdiameter.client.api.io.IConnection;
import org.jdiameter.client.api.io.ITransportLayerFactory;
import org.jdiameter.client.api.io.TransportException;
import org.jdiameter.client.api.parser.IMessageParser;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.jdiameter.server.api.IFsmFactory;
import org.jdiameter.server.api.INetwork;
import org.jdiameter.server.api.IOverloadManager;
import org.jdiameter.server.api.IPeer;
import org.jdiameter.server.api.IStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public class PeerImpl extends org.jdiameter.client.impl.controller.PeerImpl implements IPeer {

  private static final Logger logger = LoggerFactory.getLogger(org.jdiameter.server.impl.PeerImpl.class);

  // External references
  private MutablePeerTableImpl peerTable;
  protected Set<URI> predefinedPeerTable;
  protected INetwork network;
  protected IOverloadManager ovrManager;
  protected ISessionFactory sessionFactory;
  // Internal parameters and members
  protected boolean isDuplicateProtection;
  protected boolean isAttemptConnection;
  protected boolean isElection = true;
  protected Map<String, IConnection> incConnections;

  /**
   *  Create instance of class
   */
  public PeerImpl(int rating, URI remotePeer, String ip, String portRange, boolean attCnn, IConnection connection,
      MutablePeerTableImpl peerTable, IMetaData metaData, Configuration config, Configuration peerConfig,
      ISessionFactory sessionFactory, IFsmFactory fsmFactory, ITransportLayerFactory trFactory,
      IStatisticManager statisticFactory, IConcurrentFactory concurrentFactory,
      IMessageParser parser, INetwork nWork, IOverloadManager oManager, final ISessionDatasource sessionDataSource)
  throws InternalException, TransportException {
    super(peerTable, rating, remotePeer, ip, portRange, metaData, config, peerConfig, fsmFactory, trFactory, parser,
        statisticFactory, concurrentFactory, connection, sessionDataSource);
    // Create specific action context
    this.peerTable = peerTable;
    this.isDuplicateProtection = this.peerTable.isDuplicateProtection();
    this.sessionFactory = sessionFactory;
    this.isAttemptConnection = attCnn;
    this.incConnections = this.peerTable.getIncConnections();
    this.predefinedPeerTable = this.peerTable.getPredefinedPeerTable();
    this.network = nWork;
    this.ovrManager = oManager;
  }

  protected void createPeerStatistics() {
    super.createPeerStatistics();

    // Append fsm statistic
    if (this.fsm instanceof IStateMachine) {
    	StatisticRecord[] records = ((IStateMachine) fsm).getStatistic().getRecords(); 
    	IStatisticRecord[] recordsArray = new IStatisticRecord[records.length];
    	int count = 0;
    	for(StatisticRecord st: records) {
    		recordsArray[count++] = (IStatisticRecord) st;
    	}
    	this.statistic.appendCounter(recordsArray);
    }
  }

  protected void preProcessRequest(IMessage message) {
    // ammendonca: this is non-sense, we don't want to save requests
    //if (isDuplicateProtection && message.isRequest()) {
    //  peerTable.saveToDuplicate(message.getDuplicationKey(), message);
    //}
  }

  public boolean isAttemptConnection() {
    return isAttemptConnection; 
  }

  public IContext getContext() {
    return new LocalActionConext(); 
  }

  public IConnection getConnection() {
    return connection;
  }

  public void addIncomingConnection(IConnection conn) {
    PeerState state = fsm.getState(PeerState.class);
    if (DOWN  ==  state || INITIAL == state) {
      conn.addConnectionListener(connListener);
      logger.debug("Append external connection {}", conn.getKey());
    }
    else {
      logger.debug("Releasing connection {}", conn.getKey());
      incConnections.remove(conn.getKey());
      try {
        conn.release();
      }
      catch (IOException e) {
        logger.debug("Can not close external connection", e);
      }
      finally {
        logger.debug("Close external connection");
      }
    }
  }

  public void setElection(boolean isElection) {
    this.isElection = isElection;
  }

  public void notifyOvrManager(IOverloadManager ovrManager) {
    ovrManager.changeNotification(0, getUri(), fsm.getQueueInfo());
  }

  public String toString() {
    return uri.toString();
  }

  protected class LocalActionConext extends ActionContext {
  
    @Override
	public String toString() {
    	try{
		return "LocalActionConext [isRestoreConnection()=" + isRestoreConnection() + ", getPeerDescription()=" + getPeerDescription() + ", isConnected()="
				+ isConnected() + ", LocalPeer="+metaData.getLocalPeer().getUri()+" ]";
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return "LocalActionContext: No data available...";
    	}
	}

	public void sendCeaMessage(int resultCode, Message cer,  String errMessage) throws TransportException, OverloadException {
      logger.debug("Send CEA message");

      IMessage message = parser.createEmptyMessage(Message.CAPABILITIES_EXCHANGE_ANSWER, 0);
      message.setRequest(false);
      message.setHopByHopIdentifier(cer.getHopByHopIdentifier());
      message.setEndToEndIdentifier(cer.getEndToEndIdentifier());
      message.getAvps().addAvp(Avp.ORIGIN_HOST, metaData.getLocalPeer().getUri().getFQDN(), true, false, true);
      message.getAvps().addAvp(Avp.ORIGIN_REALM, metaData.getLocalPeer().getRealmName(), true, false, true);
      for (InetAddress ia : metaData.getLocalPeer().getIPAddresses()) {
        message.getAvps().addAvp(Avp.HOST_IP_ADDRESS, ia, true, false);
      }
      message.getAvps().addAvp(Avp.VENDOR_ID, metaData.getLocalPeer().getVendorId(), true, false, true);

      for (ApplicationId appId: metaData.getLocalPeer().getCommonApplications()) {
        addAppId(appId, message);
      }

      message.getAvps().addAvp(Avp.PRODUCT_NAME,  metaData.getLocalPeer().getProductName(), false);
      message.getAvps().addAvp(Avp.RESULT_CODE, resultCode, true, false, true);
      message.getAvps().addAvp(Avp.FIRMWARE_REVISION, metaData.getLocalPeer().getFirmware(), true);
      if (errMessage != null) {
        message.getAvps().addAvp(Avp.ERROR_MESSAGE, errMessage, false);
      }
      sendMessage(message);
    }

    public int processCerMessage(String key, IMessage message) {
      logger.debug("Processing CER");

      int resultCode = ResultCode.SUCCESS;
      try {
        if (connection == null || !connection.isConnected()) {
          connection = incConnections.get(key);
        }
        // Process cer
        Set<ApplicationId> newAppId = getCommonApplicationIds(message);
        if (newAppId.isEmpty()) {
          logger.debug("Processing CER failed... no common application, message AppIps {}", message.getApplicationIdAvps());
          return ResultCode.NO_COMMON_APPLICATION;
        }
        // Handshake
        if (!connection.getKey().equals(key)) { // received cer by other connection
          logger.debug("CER received by other connection {}", key);

          switch(fsm.getState(PeerState.class)) {
          case DOWN:
            resultCode = ResultCode.SUCCESS;
            break;
          case INITIAL:
            boolean isLocalWin = false;
            if (isElection)
              try {
                // ammendonca: can't understand why it checks for <= 0 ... using equals
                //isLocalWin = metaData.getLocalPeer().getUri().getFQDN().compareTo(
                //    message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString()) <= 0;
                isLocalWin = metaData.getLocalPeer().getUri().getFQDN().equals(
                    message.getAvps().getAvp(Avp.ORIGIN_HOST).getOctetString());
              }
            catch(Exception exc) {
              isLocalWin = true;
            }

            logger.debug("local peer is win - {}", isLocalWin);

            resultCode = 0;
            if (isLocalWin) {
              IConnection c = incConnections.get(key);
              c.remConnectionListener(connListener);
              c.disconnect();
              incConnections.remove(key);
            }
            else {
              connection.disconnect();  // close current connection and work with other connection
              connection.remConnectionListener(connListener);
              connection = incConnections.remove(key);
              resultCode = ResultCode.SUCCESS;
            }
            break;
          }
        }
        else {
          logger.debug("CER received by current connection, key: "+key+", PeerState: "+fsm.getState(PeerState.class));
          if (fsm.getState(PeerState.class).equals(INITIAL)) // received cer by current connection
            resultCode = 0; // NOP

          incConnections.remove(key);
        }
        if (resultCode == ResultCode.SUCCESS) {
          commonApplications.clear();
          commonApplications.addAll(newAppId);
          fillIPAddressTable(message);
        }
      }
      catch (Exception exc) {
        logger.debug("Can not process CER", exc);
      }
      logger.debug("CER result {}", resultCode);

      return resultCode;
    }

    public boolean isRestoreConnection() {
      return isAttemptConnection;
    }

    public String getPeerDescription() {
      return PeerImpl.this.toString();
    }

		public boolean receiveMessage(IMessage message) {
			boolean isProcessed = false;

			if (message.isRequest()) {
				IRequest req = message;
				Avp destRealmAvp = req.getAvps().getAvp(Avp.DESTINATION_REALM);
				String destRealm = null;
				if (destRealmAvp == null) {
					// TODO: add that missing avp in "Failed-AVP" avp...
					sendErrorAnswer(message, "No destination REALM!!!", ResultCode.MISSING_AVP);
					return true;
				} else {
					try{
						destRealm = destRealmAvp.getDiameterIdentity();
					}catch(AvpDataException ade)
					{
						sendErrorAnswer(message, "Failed to parse Destination-Realm avp!", ResultCode.INVALID_AVP_VALUE,destRealmAvp);
						return true;
					}
					
				}
				IRealmTable realmTable = router.getRealmTable();
	
				if (!realmTable.realmExists(destRealm)) {
					// send no such realm answer.
					sendErrorAnswer(message, null, ResultCode.REALM_NOT_SERVED);
					return true;
				}
				ApplicationId appId = message.getSingleApplicationId();
				if (appId == null) {
					sendErrorAnswer(message, "No ApplicationId AVP!!!", ResultCode.MISSING_AVP,null); 
					//TODO: add Auth-Application-Id,Acc-Application-Id and Vendor-Specific-Application-Id, can be empty
					return true;
				}

				// check condition for local processing.
				Avp destHostAvp = req.getAvps().getAvp(Avp.DESTINATION_HOST);
				
				//messages for local stack:
//				-  The Destination-Host AVP contains the local host's identity,
//  			    -  The Destination-Host AVP is not present, the Destination-Realm AVP
//				   contains a realm the server is configured to process locally, and
//				   the Diameter application is locally supported, or
				//check conditions for immediate consumption.
				//this includes
				if (destHostAvp != null) {
					try{
						String destHost = destHostAvp.getDiameterIdentity();
						//FIXME: add check with DNS/names to check 127 vs localhost
						if (destHost.equals(metaData.getLocalPeer().getUri().getFQDN())) {
				
							// this is for handling possible REDIRECT, destRealm!=local.realm
							LocalAction action = null;
							IRealm matched = null;
					
								matched = (IRealm) realmTable.matchRealm(req);
								if(matched!=null)
								{
									action = matched.getLocalAction();
								}else{
									// hmm, we dont support it localy, its not defined as
									// remote... soooooo
									// send no such realm answer.
									sendErrorAnswer(message, null, ResultCode.APPLICATION_UNSUPPORTED); 
									// or REALM_NOT_SERVED ?
									return true;
								}	
							

							
							
							switch (action) {
							case LOCAL: // always call listener - this covers realms
										// configured as localy processed and
										// LocalPeer.realm
							case PROXY: // might be complicated, lets make it listener
										// now
							case RELAY: // might be complicated, lets make it listener
										// now
								isProcessed = consumeMessage(message); //if its not redirected its 
								break;
							case REDIRECT:
								//TODO: change this its almost the same as above, make it sync, so no router code involved
								if (ovrManager != null && ovrManager.isParenAppOverload(message.getSingleApplicationId())) {
									logger.debug("Request {} skipped, because server application has overload", message);
									sendErrorAnswer(message, "Too Busy!!!", ResultCode.TOO_BUSY);
									return true;
								} else {
									try {
										
										router.registerRequestRouteInfo(message);
										IMessage answer = (IMessage)matched.getAgent().processRequest(req,matched);
										if (isDuplicateProtection && answer != null) {
											peerTable.saveToDuplicate(message.getDuplicationKey(), answer);
										}
										isProcessed = true;
										if(answer!=null)
											sendMessage(answer);
										if (statistic.isEnabled())
											statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
									} catch (Exception exc) {
										// TODO: check this!!
										logger.warn("Error during processing message by redirect agent", exc);
										sendErrorAnswer(message, "Cant process!!!", ResultCode.UNABLE_TO_COMPLY);
										return true;

									}
								}
								if (isProcessed) {
									// NOTE: done to inc stat whcih informs on net work request consumption :)... 
									if (statistic.isEnabled())
										statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();

								}

								break;

							}
						} else {
							//NOTE: this check should be improved, it checks if there is connection to peer, otherwise we cant serve it.
							//possibly also match realm.
							IPeer p = (IPeer) peerTable.getPeer(destHost);
							if(p!=null && p.hasValidConnection())
							{	
								isProcessed = consumeMessage(message);
							}else
							{
								sendErrorAnswer(req, "No connection to peer", ResultCode.UNABLE_TO_DELIVER);
								isProcessed = true;
							}
						}
					}catch(AvpDataException ade)
					{
						sendErrorAnswer(message, "Failed to parse Destination-Host avp!", ResultCode.INVALID_AVP_VALUE,destHostAvp);
						return true;
					}
					
				} else {
					// we have to match realms :) this MUST include local realm
					LocalAction action = null;
					IRealm matched = null;
			
						matched = (IRealm) realmTable.matchRealm(req);
						if(matched!=null)
						{
							action = matched.getLocalAction();
						}else{
							// hmm, we dont support it localy, its not defined as
							// remote... soooooo
							// send no such realm answer.
							sendErrorAnswer(message, null, ResultCode.APPLICATION_UNSUPPORTED); 
							// or REALM_NOT_SERVED ?
							return true;
						}	
					

					
					
					switch (action) {
					case LOCAL: // always call listener - this covers realms
								// configured as localy processed and
								// LocalPeer.realm
					case PROXY: // might be complicated, lets make it listener
								// now
					case RELAY: // might be complicated, lets make it listener
								// now
						isProcessed = consumeMessage(message);
						break;
					case REDIRECT:
						//TODO: change this its almost the same as above, make it sync, so no router code involved
						if (ovrManager != null && ovrManager.isParenAppOverload(message.getSingleApplicationId())) {
							logger.debug("Request {} skipped, because server application has overload", message);
							sendErrorAnswer(message, "Too Busy!!!", ResultCode.TOO_BUSY);
							return true;
						} else {
							try {
								
								router.registerRequestRouteInfo(message);
								IMessage answer = (IMessage)matched.getAgent().processRequest(req,matched);
								if (isDuplicateProtection && answer != null) {
									peerTable.saveToDuplicate(message.getDuplicationKey(), answer);
								}
								isProcessed = true;
								if(answer!=null)
									sendMessage(answer);
								if (statistic.isEnabled())
									statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
							} catch (Exception exc) {
								// TODO: check this!!
								logger.warn("Error during processing message by redirect agent", exc);
								sendErrorAnswer(message, "Cant process!!!", ResultCode.UNABLE_TO_COMPLY);
								return true;

							}
						}
						if (isProcessed) {
							// NOTE: done to inc stat whcih informs on net work request consumption :)... 
							if (statistic.isEnabled())
								statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();

						}

						break;

					}
				

				}

			} else {
				// answer, let client ... pfff, do it work
				isProcessed = super.receiveMessage(message);
			}

			return isProcessed;
		}


		/**
		 * @param message
		 * @return
		 */
		private boolean consumeMessage(IMessage message) {
			// now its safe to call stupid client code....
			boolean isProcessed = super.receiveMessage(message);
			IMessage answer = null;
			// this will process if session exists.
			if (!isProcessed) {
				if (statistic.isEnabled())
					statistic.getRecordByName(IStatisticRecord.Counters.NetGenRejectedRequest.name()).dec(); 
				// why its net gen rejected :/

				NetworkReqListener listener = network.getListener(message);
				if (listener != null) {
					
					if (isDuplicateProtection) {
						answer = peerTable.isDuplicate(message);
					}
					if (answer != null) {
						answer.setProxiable(message.isProxiable());
						answer.getAvps().removeAvp(Avp.PROXY_INFO);
						for (Avp avp : message.getAvps().getAvps(Avp.PROXY_INFO)) {
							answer.getAvps().addAvp(avp);
						}
						answer.setHopByHopIdentifier(message.getHopByHopIdentifier());
						
						isProcessed = true;
						try{
							sendMessage(answer);
						if (statistic.isEnabled())
							statistic.getRecordByName(IStatisticRecord.Counters.SysGenResponse.name()).inc();
						}catch(Exception e)
						{
							// TODO: check this!!
							logger.warn("Error during processing message by duplicate protection", e);
							sendErrorAnswer(message, "Cant process!!!", ResultCode.UNABLE_TO_COMPLY);
							return true;
						}
					} else {
						if (ovrManager != null && ovrManager.isParenAppOverload(message.getSingleApplicationId())) {
							logger.debug("Request {} skipped, because server application has overload", message);
							sendErrorAnswer(message, "Too Busy!!!", ResultCode.TOO_BUSY);
							return true;
						} else {
							try {
								router.registerRequestRouteInfo(message);
								answer = (IMessage) listener.processRequest(message);
								if (isDuplicateProtection && answer != null) {
									peerTable.saveToDuplicate(message.getDuplicationKey(), answer);
								}
								isProcessed = true;
								if(isProcessed && answer != null)
									sendMessage(answer);
								if (statistic.isEnabled())
									statistic.getRecordByName(IStatisticRecord.Counters.AppGenResponse.name()).inc();
							} catch (Exception exc) {
								// TODO: check this!!
								logger.warn("Error during processing message by listener", exc);
								sendErrorAnswer(message, "Cant process!!!", ResultCode.UNABLE_TO_COMPLY);
								return true;

							}
						}
					}

					
				} else {
					// NOTE: no listener defined for messages apps,
					// response with "bad peer" stuff.
					logger.warn("Received message for unsupported Application-Id {}", message.getSingleApplicationId());
					sendErrorAnswer(message, "Cant process!!!", ResultCode.APPLICATION_UNSUPPORTED);
					return true;
				}

			}
			
			if (isProcessed) {
				// NOTE: done to inc stat whcih informs on net work request consumption :)... 
				if (statistic.isEnabled())
					statistic.getRecordByName(IStatisticRecord.Counters.NetGenRequest.name()).inc();

			}
			return isProcessed;
		}
  }

}
