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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
//  private ConcurrentHashMap<String, ScheduledFuture> idToFutureMapping;

  private ISessionDatasource sessionDataSource;

  @SuppressWarnings("unchecked")
  public LocalTimerFacilityImpl(IContainer container) {
    super();
    this.executor = container.getConcurrentFactory().getScheduledExecutorService(IConcurrentFactory.ScheduledExecServices.ApplicationSession.name());
//    this.idToFutureMapping = new ConcurrentHashMap<String, ScheduledFuture>();
    this.sessionDataSource = container.getAssemblerFacility().getComponentInstance(ISessionDatasource.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jdiameter.common.api.timer.ITimerFacility#cancel(java.io.Serializable)
   */
  @SuppressWarnings("unchecked")
  public void cancel(Serializable f) {

	  if( f!= null && f instanceof TimerTaskHandle)
	  {
		  TimerTaskHandle timerTaskHandle = (TimerTaskHandle)f;
		  timerTaskHandle.future.cancel(false);
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
    TimerTaskHandle ir = new TimerTaskHandle();
    ir.id = id;
    ir.sessionId = sessionId;
    ir.timerName = timerName;
    ir.future = this.executor.schedule(ir, milliseconds, TimeUnit.MILLISECONDS);
   return ir;

  }

  private final class TimerTaskHandle implements Runnable, Externalizable {
	  //its not realy serializable;
    private String sessionId;
    private String timerName;
    private String id;
    private transient ScheduledFuture<?> future;
    public void run() {

      try {
        BaseSession bSession = sessionDataSource.getSession(sessionId);
        if (bSession == null || !bSession.isAppSession()) {
          // FIXME: error ?
          return;
        }
        else {
        	try{
        		AppSessionImpl impl = (AppSessionImpl) bSession;
        		impl.onTimer(timerName);
        	}catch(Exception e)
        	{
        		logger.error("Caught exception from app session object!",e);
        	}
        }
      }
      catch (Exception e) {
        logger.error("Failure executing timer task", e);
      }
    }
	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		throw new IOException("Failed to serialize local timer!");
		
	}
	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		throw new IOException("Failed to deserialize local timer!");
		
	}
    
    
  }

}
