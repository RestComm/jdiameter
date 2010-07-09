/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.jdiameter.common.impl.timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.common.api.data.ISessionDatasource;
import org.jdiameter.common.api.timer.ITimerFacility;
import org.jdiameter.common.impl.app.AppSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local implementation of timer facility for {@link ITimerFacility}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class LocalTimerFacilityImpl implements ITimerFacility {

  private static final Logger logger = LoggerFactory.getLogger(LocalTimerFacilityImpl.class);

  private ScheduledExecutorService executor;

  @SuppressWarnings("unchecked")
  private HashMap<String, ScheduledFuture> idToFutureMapping;

  private ISessionDatasource sessionDataSource;

  @SuppressWarnings("unchecked")
  public LocalTimerFacilityImpl(IContainer container) {
    super();
    this.executor = container.getConcurrentFactory().getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
    this.idToFutureMapping = new HashMap<String, ScheduledFuture>();
    this.sessionDataSource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
   */
  @SuppressWarnings("unchecked")
  public void cancel(Serializable id) {
    logger.debug("Cancelling timer with id {}", id);
    ScheduledFuture f = this.idToFutureMapping.remove(id);
    if (f != null) {
      f.cancel(false);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.timer.ITimerFacility#schedule(java.lang.String, java.lang.String, long)
   */
  @SuppressWarnings("unchecked")
  public Serializable schedule(String sessionId, String timerName, long milliseconds) throws IllegalArgumentException {
    String id = sessionId + "/" + timerName;
    logger.debug("Scheduling timer with id {}", id);
    if (this.idToFutureMapping.containsKey(id)) {
      throw new IllegalArgumentException("Timer already running: " + id);
    }
    InternalRunner ir = new InternalRunner();
    ir.id = id;
    ir.sessionId = sessionId;
    ir.timerName = timerName;
    ScheduledFuture future = this.executor.schedule(ir, milliseconds, TimeUnit.MILLISECONDS);
    this.idToFutureMapping.put(id, future);
    return id;
  }

  private final class InternalRunner implements Runnable {
    private String sessionId;
    private String timerName;
    private String id;

    public void run() {
      idToFutureMapping.remove(id);
      try {
        BaseSession bSession = sessionDataSource.getSession(sessionId);
        if (bSession == null || !bSession.isAppSession()) {
          // FIXME: error ?
          return;
        }
        else {
          AppSessionImpl impl = (AppSessionImpl) bSession;
          impl.onTimer(timerName);
        }
      }
      catch (Exception e) {
        logger.error("Failure executing timer task", e);
      }
    }
  }

}
