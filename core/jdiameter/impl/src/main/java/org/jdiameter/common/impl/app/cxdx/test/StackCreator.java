/**
 * Start time:14:20:55 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 */
package org.jdiameter.common.impl.app.cxdx.test;

import java.io.ByteArrayInputStream;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


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

/**
 * Start time:14:20:55 2009-08-19<br>
 * Project: diameter-parent-release<br>
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski
 *         </a>
 */
public class StackCreator implements Stack{
	private Stack stack=null;
		//private Logger logger=Logger.getLogger(name)
	private String dooer=null;
	public StackCreator(String stringConfig,NetworkReqListener eventListener, EventListener<Request, Answer>eventListener2,String dooer) {
		super();
				this.stack = new StackImpl();
				this.dooer=dooer;
				
			
				try {
					Configuration config = new XMLConfiguration(new ByteArrayInputStream(stringConfig.getBytes()));
					this.stack.init(config);
					this.stack.start();
				      Network network = stack.unwrap(Network.class);
		
				      Set<ApplicationId> appIds = stack.getMetaData().getLocalPeer().getCommonApplications();
		
				      System.out.println("Diameter "+dooer+" :: Supporting " + appIds.size() + " applications.");
				      //network.addNetworkReqListener(this, ApplicationId.createByAccAppId( 193, 19302 ));
		
				      for (ApplicationId appId : appIds)
				      {
				        System.out.println("Diameter "+dooer+" :: Adding Listener for [" + appId + "].");
				        network.addNetworkReqListener(eventListener, appId);
				      }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public void destroy() {
		stack.destroy();
	}

	public Logger getLogger() {
		return stack.getLogger();
	}

	public MetaData getMetaData() {
		return stack.getMetaData();
	}

	public SessionFactory getSessionFactory()
			throws IllegalDiameterStateException {
		return stack.getSessionFactory();
	}

	public SessionFactory init(Configuration config)
			throws IllegalDiameterStateException, InternalException {
		return stack.init(config);
	}

	public boolean isActive() {
		return stack.isActive();
	}

	public boolean isWrapperFor(Class<?> iface) throws InternalException {
		return stack.isWrapperFor(iface);
	}

	public void start() throws IllegalDiameterStateException, InternalException {
		stack.start();
	}

	public void start(Mode mode, long timeout, TimeUnit unit)
			throws IllegalDiameterStateException, InternalException {
		stack.start(mode, timeout, unit);
	}

	public void stop(long timeout, TimeUnit unit)
			throws IllegalDiameterStateException, InternalException {
		stack.stop(timeout, unit);
	}

	public <T> T unwrap(Class<T> iface) throws InternalException {
		return stack.unwrap(iface);
	}
}
