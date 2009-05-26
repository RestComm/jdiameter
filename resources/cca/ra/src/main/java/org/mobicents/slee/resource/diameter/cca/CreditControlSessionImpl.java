/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
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
package org.mobicents.slee.resource.diameter.cca;

import javax.slee.resource.SleeEndpoint;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvpType;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.cca.CreditControlAVPFactory;
import net.java.slee.resource.diameter.cca.CreditControlMessageFactory;
import net.java.slee.resource.diameter.cca.CreditControlSession;
import net.java.slee.resource.diameter.cca.CreditControlSessionState;
import net.java.slee.resource.diameter.cca.handlers.CCASessionCreationListener;

import org.jdiameter.api.Answer;
import org.jdiameter.api.EventListener;
import org.jdiameter.api.Request;
import org.jdiameter.api.Session;
import org.jdiameter.api.app.StateChangeListener;
import org.mobicents.slee.resource.diameter.base.DiameterActivityImpl;
import org.mobicents.slee.resource.diameter.base.events.avp.DiameterAvpImpl;

/**
 * 
 * CreditControlSessionImpl.java
 *
 * <br>Super project:  mobicents
 * <br>8:34:55 PM Dec 29, 2008 
 * <br>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 */
public abstract class CreditControlSessionImpl extends DiameterActivityImpl implements CreditControlSession,StateChangeListener{

  protected CreditControlMessageFactory ccaMessageFactory = null;
  protected CreditControlAVPFactory ccaAvpFactory = null;
  protected CreditControlSessionState state = CreditControlSessionState.IDLE;
  protected CCASessionCreationListener listener = null;

  public CreditControlSessionImpl(CreditControlMessageFactory messageFactory, CreditControlAVPFactory avpFactory, Session session, EventListener<Request, Answer> raEventListener, long timeout,
      DiameterIdentity destinationHost, DiameterIdentity destinationRealm, SleeEndpoint endpoint)
  {
    super(null, null, session, raEventListener, timeout, destinationHost, destinationRealm, endpoint);

    this.ccaMessageFactory=messageFactory;
    this.ccaAvpFactory=avpFactory;
  }

  public CreditControlSessionState getState()
  {
    return state;
  }

  @Override
  public Object getDiameterAvpFactory()
  {
    return this.ccaAvpFactory;
  }

  @Override
  public Object getDiameterMessageFactory()
  {
    return this.ccaMessageFactory;
  }

  @Override
  public Object getSessionListener()
  {
    return this.listener;
  }

  @Override
  public void setSessionListener(Object ra)
  {
    this.listener = (CCASessionCreationListener) ra;
  }

  public void setDestinationHost(DiameterIdentity destinationHost)
  {
    super.destinationHost = destinationHost;

    DiameterAvp avp = new DiameterAvpImpl(DiameterAvpCodes.DESTINATION_HOST, 0L, 0, 0, destinationHost.toString().getBytes(), DiameterAvpType.DIAMETER_IDENTITY);
    
    //((CreditControlMessageFactoryImpl)ccaMessageFactory).removeAvpFromInnerList(avp);
    ((CreditControlMessageFactoryImpl)ccaMessageFactory).addAvpToInnerList(avp);
  }

  public void setDestinationRealm(DiameterIdentity destinationRealm)
  {
    super.destinationRealm=destinationRealm;

    DiameterAvp avp = new DiameterAvpImpl(DiameterAvpCodes.DESTINATION_REALM, 0L, 0, 0, destinationHost.toString().getBytes(), DiameterAvpType.DIAMETER_IDENTITY);

    //((CreditControlMessageFactoryImpl)ccaMessageFactory).removeAvpFromInnerList(avp);
    ((CreditControlMessageFactoryImpl)ccaMessageFactory).addAvpToInnerList(avp);
  }
}
