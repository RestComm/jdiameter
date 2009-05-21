package net.java.slee.resource.diameter.cca.events.avp;

import java.io.StreamCorruptedException;

import net.java.slee.resource.diameter.base.events.avp.Enumerated;

/**
 * <pre><b>8.3. CC-Request-Type AVP</b>
 *
 *
 *   The CC-Request-Type AVP (AVP Code 416) is of type Enumerated and
 *   contains the reason for sending the credit-control request message.
 *   It MUST be present in all Credit-Control-Request messages.  The
 *   following values are defined for the CC-Request-Type AVP:
 *
 *   <b>INITIAL_REQUEST                 1</b>
 *      An Initial request is used to initiate a credit-control session,
 *      and contains credit control information that is relevant to the
 *      initiation.
 *
 *   <b>UPDATE_REQUEST                  2</b>
 *      An Update request contains credit-control information for an
 *      existing credit-control session.  Update credit-control requests
 *      SHOULD be sent every time a credit-control re-authorization is
 *      needed at the expiry of the allocated quota or validity time.
 *      Further, additional service-specific events MAY trigger a
 *      spontaneous Update request.
 *
 *   <b>TERMINATION_REQUEST             3</b>
 *      A Termination request is sent to terminate a credit-control
 *      session and contains credit-control information relevant to the
 *      existing session.
 *
 *   <b>EVENT_REQUEST                   4</b>
 *      An Event request is used when there is no need to maintain any
 *      credit-control session state in the credit-control server.  This
 *      request contains all information relevant to the service, and is
 *      the only request of the service.  The reason for the Event request
 *      is further detailed in the Requested-Action AVP.  The Requested-
 *      Action AVP MUST be included in the Credit-Control-Request message
 *      when CC-Request-Type is set to EVENT_REQUEST.
 *      <pre>
 * @author baranowb
 *
 */
public enum CcRequestType implements Enumerated {
	EVENT_REQUEST(4), INITIAL_REQUEST(1), TERMINATION_REQUEST(3), UPDATE_REQUEST(2); 
	
	public static final int _UPDATE_REQUEST = UPDATE_REQUEST.getValue();
	public static final int _TERMINATION_REQUEST = TERMINATION_REQUEST.getValue();
	public static final int _INITIAL_REQUEST = INITIAL_REQUEST.getValue();
	public static final int _EVENT_REQUEST = EVENT_REQUEST.getValue();

	private int value=-1;
	
	
	private CcRequestType(int value)
	{
		this.value=value;
	}
	
	
	
	
	private Object readResolve() throws StreamCorruptedException {
		try {
			return fromInt(value);
		} catch (IllegalArgumentException iae) {
			throw new StreamCorruptedException("Invalid internal state found: "
					+ value);
		}
	}

	public CcRequestType fromInt(int presumableValue)
			throws IllegalArgumentException {

		switch (presumableValue) {
		case 1:
			return INITIAL_REQUEST;
		case 2:
			return UPDATE_REQUEST;
		case 3:
			return TERMINATION_REQUEST;
		case 4:
			return EVENT_REQUEST;
		

		default:
			throw new IllegalArgumentException();

		}

	}
	
	
	

	public int getValue() {
		
		return this.value;
	}

}
