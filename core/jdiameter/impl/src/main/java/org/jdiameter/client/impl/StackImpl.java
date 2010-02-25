/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.ControllerLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.StackLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.TransportLayer;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.Mode;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Peer;
import org.jdiameter.api.PeerState;
import org.jdiameter.api.PeerTable;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.SessionFactory;
import org.jdiameter.api.app.StateChangeListener;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.StackState;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.common.impl.validation.DiameterMessageValidator;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.ProcessingMessageTimer;
import org.jdiameter.common.api.statistic.IStatisticProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use stack extension point
 */
public class StackImpl implements IContainer, StackImplMBean {

  private static final Logger log = LoggerFactory.getLogger(StackImpl.class);

  protected IAssembler assembler;
  protected IConcurrentFactory concurrentFactory;
  protected Configuration config;
  protected IPeerTable peerManager;
  protected StackState state = StackState.IDLE;
  protected Lock lock = new ReentrantLock();
  //This will create validator with basic validator xml
  protected DiameterMessageValidator validator = DiameterMessageValidator.getInstance();
  /**
   * Use for processing request time-out tasks (for all active peers)
   */
  protected ScheduledExecutorService scheduledFacility;

  public SessionFactory init(Configuration config) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.IDLE) {
        throw new IllegalDiameterStateException();
      }

      try {
        Class assemblerClass = Class.forName(config.getStringValue(Assembler.ordinal(), (String) Assembler.defValue()));
        assembler = (IAssembler) assemblerClass.getConstructor(Configuration.class).newInstance(config);
        // register common instances
        assembler.registerComponentInstance(this);
        assembler.registerComponentInstance(config);
      }
      catch (Exception e) {
        throw new InternalException(e);
      }
      this.config = config;
      // created manager
      this.peerManager = (IPeerTable) assembler.getComponentInstance(IPeerTable.class);
      this.concurrentFactory = (IConcurrentFactory) assembler.getComponentInstance(IConcurrentFactory.class);
      this.peerManager.setAssembler(assembler);
      this.state = StackState.CONFIGURED;
    }
    finally {
      lock.unlock();
    }
    return (SessionFactory) assembler.getComponentInstance(SessionFactory.class);
  }

  public SessionFactory getSessionFactory() throws IllegalDiameterStateException {
    if (state == StackState.CONFIGURED || state == StackState.STARTED) {
      return (SessionFactory) assembler.getComponentInstance(SessionFactory.class);
    }
    else {
      throw new IllegalDiameterStateException();
    }
  }

  public void start() throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.STOPPED && state != StackState.CONFIGURED) {
        throw new IllegalDiameterStateException();
      }
      scheduledFacility = concurrentFactory.getScheduledExecutorService(ProcessingMessageTimer.name());
      assembler.getComponentInstance(IStatisticProcessor.class).start();
      startPeerManager();
      state = StackState.STARTED;
    }
    finally {
      lock.unlock();
    }
  }

  public void start(final Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.STOPPED && state != StackState.CONFIGURED) {
        throw new IllegalDiameterStateException();
      }
      scheduledFacility = concurrentFactory.getScheduledExecutorService(ProcessingMessageTimer.name());
      assembler.getComponentInstance(IStatisticProcessor.class).start();
      List<Peer> peerTable = peerManager.getPeerTable();
      final CountDownLatch barrier = new CountDownLatch(Mode.ANY_PEER.equals(mode) ? 1 : peerTable.size());
      StateChangeListener listener = new StateChangeListener() {
        public void stateChanged(Enum oldState, Enum newState) {
          if (PeerState.OKAY.equals(newState)) {
            barrier.countDown();
          }
        }
      };
      for (Peer p : peerTable) {
        ((IPeer) p).addStateChangeListener(listener);
      }
      startPeerManager();
      try {
        barrier.await(timeOut, timeUnit);
        if (barrier.getCount() != 0) {
          throw new InternalException("TimeOut");
        }
        state = StackState.STARTED;
      }
      catch (InterruptedException e) {
        throw new InternalException("TimeOut");
      }
      finally {
        for (Peer p : peerTable) {
          ((IPeer) p).remStateChangeListener(listener);
        }
      }
    }
    finally {
      lock.unlock();
    }
  }

  private void startPeerManager() throws InternalException {
    try {
      if (peerManager != null) {
        peerManager.start();
      }
      getMetaData().unwrap(IMetaData.class).updateLocalHostStateId();
    }
    catch (Exception e) {
      throw new InternalException(e);
    }
  }

  public void stop(long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state == StackState.STARTED || state == StackState.CONFIGURED) {
        List<Peer> peerTable = peerManager.getPeerTable();
        final CountDownLatch barrier = new CountDownLatch(peerTable.size());
        StateChangeListener listener = new StateChangeListener() {
          public void stateChanged(Enum oldState, Enum newState) {
            if (PeerState.DOWN.equals(newState)) {
              barrier.countDown();
            }
          }
        };
        for (Peer p : peerTable) {
          if (p.getState(PeerState.class).equals(PeerState.DOWN)) {
            barrier.countDown();
          }
          else {
            ((IPeer) p).addStateChangeListener(listener);
          }
        }
        if (peerManager != null) {
          try {
            peerManager.stopping();
          }
          catch (Exception e) {
            log.warn("Stopping error", e);
          }
        }
        try {
          barrier.await(timeOut, timeUnit);
          if (barrier.getCount() != 0) {
            throw new InternalException("TimeOut");
          }
        }
        catch (InterruptedException e) {
          throw new InternalException("TimeOut");
        }
        finally {
          state = StackState.STOPPED;
          for (Peer p : peerTable) {
            ((IPeer) p).remStateChangeListener(listener);
          }
        }
        assembler.getComponentInstance(IStatisticProcessor.class).stop();
        try {
          if (peerManager != null) {
            peerManager.stopped();
          }
          // Clear all timeout tasks
          if (scheduledFacility != null) {
            concurrentFactory.shutdownNow(scheduledFacility);
          }
        }
        catch (Exception e) {
          log.warn("Stopped error", e);
        }
        state = StackState.STOPPED;
      }
    }
    finally {
      lock.unlock();
    }
  }

  public void destroy() {
    lock.lock();
    try {
      if (peerManager != null) {
        peerManager.destroy();
      }
      if (assembler != null) {
        assembler.destroy();
      }
      if (scheduledFacility != null) {
        concurrentFactory.shutdownNow(scheduledFacility);
      }
    }
    catch (Exception e) {
      log.warn("Destroy error", e);
    }
    finally {
      state = StackState.IDLE;
      lock.unlock();
    }
  }

  public boolean isActive() {
    return state == StackState.STARTED;
  }

  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getAnonymousLogger();
  }

  public MetaData getMetaData() {
    if (state == StackState.IDLE) {
      throw new IllegalAccessError("Meta data not defined");
    }
    return (MetaData) assembler.getComponentInstance(IMetaData.class);
  }

  public boolean isWrapperFor(Class<?> aClass) throws InternalException {
    boolean isWrap = aClass == PeerTable.class;
    if (!isWrap) {
      isWrap = assembler.getChilds()[StackLayer.id()].getComponentInstance(aClass) != null;
    }
    if (!isWrap) {
      isWrap = assembler.getChilds()[ControllerLayer.id()].getComponentInstance(aClass) != null;
    }
    if (!isWrap) {
      isWrap = assembler.getChilds()[TransportLayer.id()].getComponentInstance(aClass) != null;
    }
    return isWrap;
  }

  public <T> T unwrap(Class<T> aClass) throws InternalException {
    Object unwrapObject = null;
    if (aClass == PeerTable.class) {
      unwrapObject = assembler.getComponentInstance(aClass);
    }
    if (unwrapObject == null) {
      unwrapObject = assembler.getChilds()[StackLayer.id()].getComponentInstance(aClass);
    }
    if (unwrapObject == null) {
      unwrapObject = assembler.getChilds()[ControllerLayer.id()].getComponentInstance(aClass);
    }
    if (unwrapObject == null) {
      unwrapObject = assembler.getChilds()[TransportLayer.id()].getComponentInstance(aClass);
    }
    return (T) unwrapObject;
  }

  // Extended methods

  public StackState getState() {
    return state;
  }

  public Configuration getConfiguration() {
    return config;
  }

  public IAssembler getAssemblerFacility() {
    return assembler;
  }

  public void sendMessage(IMessage message) throws RouteException, AvpDataException, IllegalDiameterStateException, IOException {
    peerManager.sendMessage(message);
  }

  public void addSessionListener(String sessionId, NetworkReqListener listener) {
    peerManager.addSessionReqListener(sessionId, listener);
  }

  public void removeSessionListener(String sessionId) {
    peerManager.removeSessionListener(sessionId);
  }

  public ScheduledExecutorService getScheduledFacility() {
    return scheduledFacility;
  }

  public IConcurrentFactory getConcurrentFactory() {
		
		return this.concurrentFactory;
	} 
  
  public String configuration() {
    return config != null ? config.toString() : "not set";
  }

  public String metaData() {
    try {
      return getMetaData().toString();
    }
    catch(Exception exc) {
      return "not set";
    }
  }

  public String peerDescription(String name) {
    try {
      for (Peer p : unwrap(PeerTable.class).getPeerTable()) {
        if (p.getUri().getFQDN().equals(name)) {
          return p.toString();
        }
      }
    }
    catch (InternalException e) {
      log.debug("InternalException", e);
    }

    return "not set";
  }

  public String peerList() {
    try {
      return unwrap(PeerTable.class).getPeerTable().toString();
    }
    catch (InternalException e) {
      return "not set";
    }
  }

  public void stop() {
    try {
      stop(10, TimeUnit.SECONDS);
    }
    catch (Exception e) {
      log.debug("Exception", e);
    }
  }

   
}
