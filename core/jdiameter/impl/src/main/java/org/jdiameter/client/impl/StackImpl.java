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

package org.jdiameter.client.impl;

import static org.jdiameter.client.impl.helpers.ExtensionPoint.ControllerLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.StackLayer;
import static org.jdiameter.client.impl.helpers.ExtensionPoint.TransportLayer;
import static org.jdiameter.client.impl.helpers.Parameters.Assembler;
import static org.jdiameter.common.api.concurrent.IConcurrentFactory.ScheduledExecServices.ProcessingMessageTimer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.BaseSession;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
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
import org.jdiameter.api.validation.Dictionary;
import org.jdiameter.api.validation.ValidatorLevel;
import org.jdiameter.client.api.IAssembler;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.IMessage;
import org.jdiameter.client.api.IMetaData;
import org.jdiameter.client.api.StackState;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.client.api.controller.IPeerTable;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.statistic.IStatisticProcessor;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use stack extension point
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class StackImpl implements IContainer, StackImplMBean {

  private static final Logger log = LoggerFactory.getLogger(StackImpl.class);

  protected IAssembler assembler;
  protected IConcurrentFactory concurrentFactory;
  protected Configuration config;
  protected IPeerTable peerManager;
  protected StackState state = StackState.IDLE;
  protected Lock lock = new ReentrantLock();

  /**
   * Use for processing request time-out tasks (for all active peers)
   */
  protected ScheduledExecutorService scheduledFacility;

  @Override
  @SuppressWarnings("unchecked")
  public SessionFactory init(Configuration config) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    if (log.isInfoEnabled()) {
      log.info("(-)(-)(-)(-)(-) Starting " + VersionProperties.instance.getProperty("vendor") + " DIAMETER Stack v" +
          VersionProperties.instance.getProperty("version") + " (-)(-)(-)(-)(-)");
    }
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
      this.concurrentFactory = assembler.getComponentInstance(IConcurrentFactory.class);

      try {
        Configuration[] dictionaryConfigs = config.getChildren(Parameters.Dictionary.ordinal());

        // Initialize with default values
        String dictionaryClassName = (String) Parameters.DictionaryClass.defValue();
        Boolean validatorEnabled = (Boolean) Parameters.DictionaryEnabled.defValue();
        ValidatorLevel validatorSendLevel = ValidatorLevel.fromString((String) Parameters.DictionarySendLevel.defValue());
        ValidatorLevel validatorReceiveLevel = ValidatorLevel.fromString((String) Parameters.DictionaryReceiveLevel.defValue());

        if (dictionaryConfigs != null && dictionaryConfigs.length > 0) {
          Configuration dictionaryConfiguration = dictionaryConfigs[0];
          dictionaryClassName = dictionaryConfiguration.getStringValue(Parameters.DictionaryClass.ordinal(), (String) Parameters.DictionaryClass.defValue());
          validatorEnabled = dictionaryConfiguration.getBooleanValue(Parameters.DictionaryEnabled.ordinal(), (Boolean) Parameters.DictionaryEnabled.defValue());
          validatorSendLevel = ValidatorLevel.fromString(dictionaryConfiguration.getStringValue(Parameters.DictionarySendLevel.ordinal(),
              (String) Parameters.DictionarySendLevel.defValue()));
          validatorReceiveLevel = ValidatorLevel.fromString(dictionaryConfiguration.getStringValue(Parameters.DictionaryReceiveLevel.ordinal(),
              (String) Parameters.DictionaryReceiveLevel.defValue()));
        }

        createDictionary(dictionaryClassName, validatorEnabled, validatorSendLevel, validatorReceiveLevel);
      }
      catch (Exception e) {
        throw new InternalException(e);
      }

      // create manager
      this.peerManager = assembler.getComponentInstance(IPeerTable.class);
      this.peerManager.setAssembler(assembler);

      this.state = StackState.CONFIGURED;
    }
    finally {
      lock.unlock();
    }
    if (log.isInfoEnabled()) {
      log.info("(-)(-)(-)(-)(-) Started  " + VersionProperties.instance.getProperty("vendor") + " DIAMETER Stack v" +
          VersionProperties.instance.getProperty("version") + " (-)(-)(-)(-)(-)");
    }
    return assembler.getComponentInstance(SessionFactory.class);
  }

  private void createDictionary(String clazz, boolean validatorEnabled, ValidatorLevel validatorSendLevel, ValidatorLevel validatorReceiveLevel)
      throws InternalException {
    // Defer call to singleton
    DictionarySingleton.init(clazz, validatorEnabled, validatorSendLevel, validatorReceiveLevel);
  }

  @Override
  public SessionFactory getSessionFactory() throws IllegalDiameterStateException {
    if (state == StackState.CONFIGURED || state == StackState.STARTED) {
      // FIXME: When possible, get rid of IoC here.
      return assembler.getComponentInstance(SessionFactory.class);
    }
    else {
      throw new IllegalDiameterStateException();
    }
  }

  @Override
  public Dictionary getDictionary() throws IllegalDiameterStateException {
    return DictionarySingleton.getDictionary();
  }

  @Override
  public void start() throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.STOPPED && state != StackState.CONFIGURED) {
        throw new IllegalDiameterStateException();
      }

      scheduledFacility = concurrentFactory.getScheduledExecutorService(ProcessingMessageTimer.name());
      assembler.getComponentInstance(ISessionDatasource.class).start();
      assembler.getComponentInstance(IStatisticProcessor.class).start();
      assembler.getComponentInstance(ITimerFacility.class);
      startPeerManager();
      state = StackState.STARTED;
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void start(final Mode mode, long timeOut, TimeUnit timeUnit) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state != StackState.STOPPED && state != StackState.CONFIGURED) {
        throw new IllegalDiameterStateException();
      }
      scheduledFacility = concurrentFactory.getScheduledExecutorService(ProcessingMessageTimer.name());
      assembler.getComponentInstance(IStatisticProcessor.class).start();
      assembler.getComponentInstance(ISessionDatasource.class).start();
      assembler.getComponentInstance(ITimerFacility.class);
      List<Peer> peerTable = peerManager.getPeerTable();
      // considering only "to connect" peers are on the table at this time...
      final CountDownLatch barrier = new CountDownLatch(Mode.ANY_PEER.equals(mode) ? Math.min(peerTable.size(), 1) : peerTable.size());
      StateChangeListener listener = new AbstractStateChangeListener() {
        @Override
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

  @Override
  @SuppressWarnings("unchecked")
  public void stop(long timeOut, TimeUnit timeUnit, int disconnectCause) throws IllegalDiameterStateException, InternalException {
    lock.lock();
    try {
      if (state == StackState.STARTED || state == StackState.CONFIGURED) {
        if (log.isInfoEnabled()) {
          log.info("(-)(-)(-)(-)(-) Stopping " + VersionProperties.instance.getProperty("vendor") + " DIAMETER Stack v" +
              VersionProperties.instance.getProperty("version") + " (-)(-)(-)(-)(-)");
        }
        List<Peer> peerTable = peerManager.getPeerTable();
        final CountDownLatch barrier = new CountDownLatch(peerTable.size());
        StateChangeListener listener = new AbstractStateChangeListener() {
          @Override
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
            peerManager.stopping(disconnectCause);
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
        assembler.getComponentInstance(ISessionDatasource.class).stop();
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
        if (log.isInfoEnabled()) {
          log.info("(-)(-)(-)(-)(-) Stopped  " + VersionProperties.instance.getProperty("vendor") + " DIAMETER Stack v" +
              VersionProperties.instance.getProperty("version") + " (-)(-)(-)(-)(-)");
        }
      }
    }
    finally {
      lock.unlock();
    }
  }

  @Override
  public void destroy() {
    // Be friendly
    if (state == StackState.STARTED) {
      log.warn("Calling destroy() with Stack in STARTED state. Calling stop(REBOOTING) before, please do it yourself with the proper cause.");
      stop(DisconnectCause.REBOOTING);
    }

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

  @Override
  public boolean isActive() {
    return state == StackState.STARTED;
  }

  @Override
  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getAnonymousLogger();
  }

  @Override
  public MetaData getMetaData() {
    if (state == StackState.IDLE) {
      throw new IllegalStateException("Meta data not defined");
    }
    return assembler.getComponentInstance(IMetaData.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BaseSession> T getSession(String sessionId, Class<T> clazz) throws InternalException {
    if (getState() == StackState.IDLE) {
      throw new InternalException("Illegal state of stack");
    }
    BaseSession bs = assembler.getComponentInstance(ISessionDatasource.class).getSession(sessionId);
    return bs != null ? (T) bs : null;
  }

  @Override
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

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> aClass) throws InternalException {
    Object unwrapObject = null;
    if (aClass == PeerTable.class) {
      unwrapObject = assembler.getComponentInstance(aClass);
    }
    // TODO: "layers" should be removed....
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

  @Override
  public StackState getState() {
    return state;
  }

  @Override
  public Configuration getConfiguration() {
    return config;
  }

  @Override
  public IAssembler getAssemblerFacility() {
    return assembler;
  }

  @Override
  public void sendMessage(IMessage message) throws RouteException, AvpDataException, IllegalDiameterStateException, IOException {
    peerManager.sendMessage(message);
  }

  @Override
  public void addSessionListener(String sessionId, NetworkReqListener listener) {
    peerManager.addSessionReqListener(sessionId, listener);
  }

  @Override
  public void removeSessionListener(String sessionId) {
    peerManager.removeSessionListener(sessionId);
  }

  @Override
  public ScheduledExecutorService getScheduledFacility() {
    return scheduledFacility;
  }

  @Override
  public IConcurrentFactory getConcurrentFactory() {
    return this.concurrentFactory;
  }

  @Override
  public String configuration() {
    return config != null ? config.toString() : "not set";
  }

  @Override
  public String metaData() {
    try {
      return getMetaData().toString();
    }
    catch (Exception exc) {
      return "not set";
    }
  }

  @Override
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

  @Override
  public String peerList() {
    try {
      return unwrap(PeerTable.class).getPeerTable().toString();
    }
    catch (InternalException e) {
      return "not set";
    }
  }

  @Override
  public void stop(int disconnectCause) {
    try {
      stop(10, TimeUnit.SECONDS, disconnectCause);
    }
    catch (Exception e) {
      log.debug("Exception", e);
    }
  }

}
