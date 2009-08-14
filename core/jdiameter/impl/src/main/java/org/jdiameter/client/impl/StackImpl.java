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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use stack extension point
 */
public class StackImpl implements IContainer, StackImplMBean {

  protected Logger log = LoggerFactory.getLogger(StackImpl.class);

  protected IAssembler assembler;
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
      catch(Exception e) {
        throw new InternalException(e);
      }
      this.config = config;
      // created manager
      this.peerManager = (IPeerTable) assembler.getComponentInstance(IPeerTable.class);
      this.peerManager.setAssempler(assembler);
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
      scheduledFacility = Executors.newScheduledThreadPool(4); // TODO must configured
      startPeerManager();
      state = StackState.STARTED;
    }
    finally {
      lock.unlock();
    }
  }

  // TODO change to barrier
  public void start(final Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.STOPPED && state != StackState.CONFIGURED) {
        throw new IllegalDiameterStateException();
      }
      scheduledFacility = Executors.newScheduledThreadPool(4); // todo must configured
      final Condition notStarted  = lock.newCondition();
      List<Peer> peerTable = peerManager.getPeerTable();
      final int[] count = new int[] {peerTable.size()};
      StateChangeListener listener = new StateChangeListener() {
        public void stateChanged(Enum oldState, Enum newState) {
          PeerState ps = (PeerState) newState;
          if (PeerState.OKAY.equals(ps)) {
            if (Mode.ANY_PEER.equals(mode)) {
              count[0] = 0;
            }
            else {
              if (count[0] > 0) {
                count[0]--;
              }
            }
          }
        }
      };
      for (Peer p : peerTable) {
        ((IPeer)p).addStateChangeListener(listener);
      }
      startPeerManager();
      long tvalue = 0;
      while (count[0] != 0) {
        try {
          notStarted.await(100, TimeUnit.MILLISECONDS);
          tvalue += 100;
          if (tvalue > timeUnit.toMillis(timeOut)) {
            for (Peer p : peerTable) {
              ((IPeer)p).remStateChangeListener(listener);
            }
            throw new InternalException("TimeOut");
          }
        }
        catch (InterruptedException e) {
          log.debug("InterruptedException", e);
        }
      }
      for (Peer p : peerTable) {
        ((IPeer)p).remStateChangeListener(listener);
      }
      state = StackState.STARTED;
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

  // TODO change to barrier
  public void stop(long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state == StackState.STOPPED || state != StackState.STARTED) return;
      final Condition notStopped  = lock.newCondition();
      List<Peer> peerTable = peerManager.getPeerTable();
      final int[] count = new int[1];
      StateChangeListener listener = new StateChangeListener() {
        public void stateChanged(Enum oldState, Enum newState) {
          PeerState ps = (PeerState) newState;
          if (PeerState.DOWN.equals(ps)) {
            if (count[0] > 0) {
              count[0]--;
            }
          }
        }
      };
      for (Peer p:peerTable) {
        if (!p.getState(PeerState.class).equals(PeerState.DOWN)) {
          count[0]++;
        }
        ((IPeer)p).addStateChangeListener(listener);
      }
      try {
        if (peerManager != null) {
          peerManager.stopping();
        }
      }
      catch (Exception e) {
        log.warn("Stopping error", e);
      }
      long tvalue = 0;
      while (count[0] != 0) {
        try {
          notStopped.await(100, TimeUnit.MILLISECONDS);
          tvalue += 100;
          if (tvalue > timeUnit.toMillis(timeOut)) {
            for (Peer p:peerTable) {
              ((IPeer)p).remStateChangeListener(listener);
            }
            state = StackState.STOPPED;
            throw new InternalException("TimeOut");
          }
        }
        catch (InterruptedException e) {
          log.debug("InterruptedException", e);
        }
      }
      for (Peer p : peerTable) {
        ((IPeer)p).remStateChangeListener(listener);
      }
      try {
        if (peerManager != null) {
          peerManager.stopped();
        }
        // Clear all timeout tasks
        if (scheduledFacility != null) {
          scheduledFacility.shutdownNow();
        }
      }
      catch (Exception e) {
        log.warn("Stopped error", e);
      }
      state = StackState.STOPPED;
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
        scheduledFacility.shutdownNow();
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
