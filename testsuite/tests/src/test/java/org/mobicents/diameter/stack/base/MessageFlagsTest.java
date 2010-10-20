package org.mobicents.diameter.stack.base;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MessageFlagsTest {

  private static StackCreator stackCreator = new StackCreator(new StackConfig(), null, null, "Server", true);

  private boolean expected_R;
  private boolean expected_P;
  private boolean expected_E;
  private boolean expected_T;

  private boolean initFlags;

  private Message createEmptyMessage() throws InternalException, IllegalDiameterStateException {
    Message m = stackCreator.getSessionFactory().getNewRawSession().createMessage(123, ApplicationId.createByAccAppId(3));

    // Initialize flags with the default value
    m.setRequest(initFlags);
    m.setProxiable(initFlags);
    m.setError(initFlags);
    m.setReTransmitted(initFlags);

    return m;
  }

  private Message fillMessageFlags(Message m, boolean r, boolean p, boolean e, boolean t) {
    m.setRequest(r);
    m.setProxiable(p);
    m.setError(e);
    m.setReTransmitted(t);

    return m;
  }

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
        {true, true, true, true, true},
        {true, true, true, true, false},
        {true, true, true, false, true},
        {true, true, true, false, false},
        {true, true, false, true, true},
        {true, true, false, true, false},
        {true, true, false, false, true},
        {true, true, false, false, false},
        {true, false, true, true, true},
        {true, false, true, true, false},
        {true, false, true, false, true},
        {true, false, true, false, false},
        {true, false, false, true, true},
        {true, false, false, true, false},
        {true, false, false, false, true},
        {true, false, false, false, false},
        {false, true, true, true, true},
        {false, true, true, true, false},
        {false, true, true, false, true},
        {false, true, true, false, false},
        {false, true, false, true, true},
        {false, true, false, true, false},
        {false, true, false, false, true},
        {false, true, false, false, false},
        {false, false, true, true, true},
        {false, false, true, true, false},
        {false, false, true, false, true},
        {false, false, true, false, false},
        {false, false, false, true, true},
        {false, false, false, true, false},
        {false, false, false, false, true},
        {false, false, false, false, false}
    });
  }

  public MessageFlagsTest(boolean init, boolean r, boolean p, boolean e, boolean t) {
    this.initFlags = init;

    this.expected_R = r;
    this.expected_P = p;
    this.expected_E = e;
    this.expected_T = t;

    System.out.println("Testing with flags: Init[" + initFlags + "] - R[" + expected_R + "] P[" + expected_P + "] E[" + expected_E + "] T[" + expected_T + "]");
  }

  @Test
  public void testFlags() {
    try {
      // Create the message
      Message m = createEmptyMessage();

      // Set the flags
      fillMessageFlags(m, expected_R, expected_P, expected_E, expected_T);

      // Get the flags
      boolean received_R = m.isRequest();
      boolean received_P = m.isProxiable();
      boolean received_E = m.isError();
      boolean received_T = m.isReTransmitted();

      // Confirm it's OK
      assertTrue("Wrong flags: R[rcv " + received_R + "; exp " + expected_R + "], P[rcv " + received_P + "; exp " + expected_P + "]" +
          "E[rcv " + received_E + "; exp " + expected_E + "], T[rcv " + received_T + "; exp " + expected_T + "]", 
          (expected_R == received_R) && (expected_P == received_P) && (expected_E == received_E) && (expected_T == received_T));
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
