package org.mobicents.servers.diameter.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.Stack;
import org.jdiameter.server.impl.StackImpl;
import org.jdiameter.server.impl.helpers.XMLConfiguration;

public class StackCreator extends StackImpl implements Stack {

  private static Logger logger = Logger.getLogger(StackCreator.class);

  private Stack stack = null;

  public StackCreator(Configuration config, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String identifier,
      Boolean isServer) {
    super();
    this.stack = new org.jdiameter.server.impl.StackImpl();

    try {
      this.stack.init(config);

      // Let it stabilize...
      Thread.sleep(500);

      Network network = stack.unwrap(Network.class);

      Set<ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();

      for (ApplicationId appId : appIds) {
        if (logger.isInfoEnabled()) {
          logger.info("Diameter " + identifier + " :: Adding Listener for [" + appId + "].");
        }
        network.addNetworkReqListener(networkReqListener, appId);
      }

      if (logger.isInfoEnabled()) {
        logger.info("Diameter " + identifier + " :: Supporting " + appIds.size() + " applications.");
      }
    }
    catch (Exception e) {
      logger.error("Failure creating stack '" + identifier + "'", e);
    }
  }

  public StackCreator(InputStream streamConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer,
      Boolean isServer) throws Exception {
    this(isServer ? new XMLConfiguration(streamConfig) : new org.jdiameter.client.impl.helpers.XMLConfiguration(streamConfig), networkReqListener,
        eventListener, dooer, isServer);
  }

  public StackCreator(String stringConfig, NetworkReqListener networkReqListener, EventListener<Request, Answer> eventListener, String dooer, Boolean isServer)
      throws Exception {
    this(isServer ? new XMLConfiguration(new ByteArrayInputStream(stringConfig.getBytes())) : new org.jdiameter.client.impl.helpers.XMLConfiguration(
        new ByteArrayInputStream(stringConfig.getBytes())), networkReqListener, eventListener, dooer, isServer);
  }

  @Override
  public void destroy() {
    stack.destroy();
  }

  @Override
  public java.util.logging.Logger getLogger() {
    return stack.getLogger();
  }

  @Override
  public MetaData getMetaData() {
    return stack.getMetaData();
  }

  @Override
  public SessionFactory getSessionFactory() throws IllegalDiameterStateException {
    return stack.getSessionFactory();
  }

  @Override
  public SessionFactory init(Configuration config) throws IllegalDiameterStateException, InternalException {
    return stack.init(config);
  }

  @Override
  public boolean isActive() {
    return stack.isActive();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws InternalException {
    return stack.isWrapperFor(iface);
  }

  @Override
  public void start() throws IllegalDiameterStateException, InternalException {
    stack.start();
  }

  @Override
  public void start(Mode mode, long timeout, TimeUnit unit) throws IllegalDiameterStateException, InternalException {
    stack.start(mode, timeout, unit);
  }

  @Override
  public void stop(long timeout, TimeUnit unit, int disconnectReason) throws IllegalDiameterStateException, InternalException {
    stack.stop(timeout, unit, disconnectReason);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws InternalException {
    return stack.unwrap(iface);
  }

}
