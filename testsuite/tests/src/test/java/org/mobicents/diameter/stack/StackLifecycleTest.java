package org.mobicents.diameter.stack;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.jdiameter.api.Answer;
import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.DisconnectCause;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.Mode;
import org.jdiameter.api.Network;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.Request;
import org.jdiameter.client.api.StackState;
import org.jdiameter.server.impl.StackImpl;
import org.junit.Assert;
import org.junit.Test;

public class StackLifecycleTest {

  // Stack Lifecycle
  // +------+           +------------+            +---------+
  // | Idle | = init => | Configured | = start => | Started |
  // +------+           +------------+            +---------+
  //   /\ /\                  ||                    ||   /\
  //   || ||                  ||                   stop  ||
  //   || ++==== destroy =====++                    ||   ||
  //   ||                +---------+ <==============++   ||
  //   ++== destroy ==== | Stopped |                     ||
  //                     +---------+ === start ==========++

  @Test
  public void testStartWithoutInit() {
    StackImpl stack = new StackImpl();

    try {
      stack.start();

      fail("Should have failed calling start() when stack is in state IDLE");
    }
    catch (IllegalDiameterStateException idse) {
      // We are OK. This is what was expected.
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Expected different exception (IllegalDiameterStateException), got " + e.getClass().getName());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testDoubleInit() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      // We should fail here, it's already initialized
      stack.init(config);

      fail("Should have failed calling init() when stack is in state CONFIGURED");
    }
    catch (IllegalDiameterStateException idse) {
      // We are OK. This is what was expected.
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Expected different exception (IllegalDiameterStateException), got " + e.getClass().getName());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testInitDestroyInit() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.destroy();

      stack.init(config);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful switching states IDLE -> CONFIGURED -> IDLE -> CONFIGURED. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testStopWithoutInit() {
    StackImpl stack = new StackImpl();

    try {
      stack.stop(DisconnectCause.REBOOTING);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful calling stop(DisconnectCause.REBOOTING) with stack in IDLE state. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testDestroyWithoutInit() {
    StackImpl stack = new StackImpl();

    try {
      stack.destroy();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful calling destroy() with stack in IDLE state. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testStopWithoutStart() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.stop(DisconnectCause.REBOOTING);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful calling stop(DisconnectCause.REBOOTING) with stack in CONFIGURED state. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testDestroyWithoutStop() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.start();

      stack.destroy();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful calling destroy() with stack in STARTED state. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testInitAfterStart() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.start();

      stack.init(config);

      fail("Should have failed calling init() with stack in STARTED state.");
    }
    catch (IllegalDiameterStateException idse) {
      // We are OK. This is what was expected.
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Expected different exception (IllegalDiameterStateException), got " + e.getClass().getName());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testDoubleStart() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.start();

      // We should fail here, it's already started
      stack.start();

      fail("Should have failed calling start() when stack is in STARTED state.");
    }
    catch (IllegalDiameterStateException idse) {
      // We are OK. This is what was expected.
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Expected different exception (IllegalDiameterStateException), got " + e.getClass().getName());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testInitWhenStopped() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.start();

      stack.stop(DisconnectCause.REBOOTING);

      // We should fail here, it's already configured, just stopped
      stack.init(config);

      fail("Should have failed calling init() when stack is in STOPPED state.");
    }
    catch (IllegalDiameterStateException idse) {
      // We are OK. This is what was expected.
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Expected different exception (IllegalDiameterStateException), got " + e.getClass().getName());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testDoubleStop() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      stack.start();

      stack.stop(DisconnectCause.REBOOTING);

      // We should fail here, it's already configured, just stopped
      stack.stop(DisconnectCause.REBOOTING);
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Should have been successful calling stop(DisconnectCause.REBOOTING) with stack in STOPPED state. Failed with: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  @Test
  public void testStartStopStart() {
    StackImpl stack = new StackImpl();

    try {
      InputStream is;
      String configFile = "jdiameter-server-two.xml";

      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Assert.assertNotNull("InputStream for configuration file should not be null", is);

      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      stack.init(config);

      Network network = stack.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          //this wont be called.
          return null;
        }
      }, ApplicationId.createByAuthAppId(193, 19302));

      stack.start();
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");
      //System.err.println(" // 1. stack.start() called.");
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");

      /** can connect to the stack here **/
      boolean canConnect = tryToConnect();
      Assert.assertTrue("Should be able to connect to server after 1st 'start'.", canConnect);

      stack.stop(DisconnectCause.REBOOTING);
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");
      //System.err.println(" // 2. stack.stop(DisconnectCause.REBOOTING) called.");
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");

      /** cannot connect to the stack here **/
      canConnect = tryToConnect();
      Assert.assertFalse("Should NOT be able to connect to server after 'stop'.", canConnect);

      stack.start();
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");
      //System.err.println(" // 3. stack.start() called.");
      //System.err.println(" ///////////////////////////////////////////////////////////////////// ");

      /** should be able to connect to the stack here again **/
      canConnect = tryToConnect();
      Assert.assertTrue("Should be able to connect to server after 2nd 'start'.", canConnect);

      stack.stop(DisconnectCause.REBOOTING);

      stack.destroy();
    }
    catch (Exception e) {
      e.printStackTrace();
      fail("Failure while performing test: " + e.getMessage());
    }
    finally {
      shutdownStack(stack);
    }
  }

  /**
   * Creates a client which tries to connect to stack under test.
   *
   * @return
   */
  private boolean tryToConnect() {
    org.jdiameter.server.impl.StackImpl clientStack = null;
    try {
      clientStack = new org.jdiameter.server.impl.StackImpl();
      InputStream is;
      String configFile = "jdiameter-client-two.xml";
      is = StackLifecycleTest.class.getClassLoader().getResourceAsStream("configurations/" + configFile);
      Configuration config;
      config = new org.jdiameter.server.impl.helpers.XMLConfiguration(is);
      clientStack.init(config);

      Network network = clientStack.unwrap(Network.class);
      network.addNetworkReqListener(new NetworkReqListener() {

        @Override
        public Answer processRequest(Request request) {
          // this wont be called.
          return null;
        }
      }, ApplicationId.createByAuthAppId(193, 19302));

      clientStack.start(Mode.ALL_PEERS, 5000, TimeUnit.MILLISECONDS);
    }
    catch (Exception e) {
      return false;
    }
    finally {
      shutdownStack(clientStack);
    }

    return true;
  }

  private void shutdownStack(StackImpl stack) {
    if (stack != null) {
      try {
        Thread.sleep(2000);
      }
      catch (InterruptedException e) {
        // ignore
      }

      StackState curState = stack.getState();
      if (curState == StackState.IDLE) {
        // We are good. let's return
        stack = null;
        return;
      }
      else if (curState == StackState.CONFIGURED) {
        // It's configured, just destroy it
        try {
          stack.destroy();
          Thread.sleep(500);
        }
        catch (InterruptedException e) {
          // ignore
        }
        finally {
          stack = null;
        }
      }
      else if (curState == StackState.STARTED) {
        // It's started, stop and destroy it
        try {
          stack.stop(DisconnectCause.REBOOTING);
          Thread.sleep(500);
        }
        catch (InterruptedException e) {
          // ignore
        }
        finally {
          try {
            stack.destroy();
            Thread.sleep(500);
          }
          catch (InterruptedException e) {
            // ignore
          }
          finally {
            stack = null;
          }
        }
      }
      else if (curState == StackState.STOPPED) {
        // It's stopped, just destroy it
        try {
          stack.destroy();
          Thread.sleep(500);
        }
        catch (InterruptedException e) {
          // ignore
        }
        finally {
          stack = null;
        }
      }
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        // ignore
      }
    }
  }
}
