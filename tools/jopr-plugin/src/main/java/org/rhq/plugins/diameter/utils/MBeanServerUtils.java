package org.rhq.plugins.diameter.utils;

import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.mobicents.diameter.stack.DiameterStackMultiplexerMBean;

public class MBeanServerUtils {

  private Properties prop = null;

  public MBeanServerUtils(String namingURL) {
    this.prop = new Properties();
    prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
    prop.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
    prop.put(Context.PROVIDER_URL, namingURL);
  }

  public MBeanServerConnection getConnection() throws NamingException {
    InitialContext ctx = new InitialContext(this.prop);
    MBeanServerConnection server = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");
    return server;
  }

  public DiameterStackMultiplexerMBean getDiameterMBean() throws NamingException, MalformedObjectNameException, NullPointerException {
    MBeanServerConnection connection = this.getConnection();

    ObjectName diameterMuxON = new ObjectName("diameter.mobicents:service=DiameterStackMultiplexer");

    DiameterStackMultiplexerMBean diameterMBean = (DiameterStackMultiplexerMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
        diameterMuxON, DiameterStackMultiplexerMBean.class, false);

    return diameterMBean;
  }

}
