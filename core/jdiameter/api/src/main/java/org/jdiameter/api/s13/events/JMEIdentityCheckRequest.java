package org.jdiameter.api.s13.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppRequestEvent;

/**
 * The ECR command, indicated by the Command-Code field set to 324 and the 'R'
 * bit set in the Command Flags field, is sent by MME or SGSN to EIR to check
 * the Mobile Equipment's identity status (e.g. to check that it has not been
 * stolen, or, to verify that it does not have faults).
 *
 */
public interface JMEIdentityCheckRequest extends AppRequestEvent {

	public static final String _SHORT_NAME = "ECR";
	public static final String _LONG_NAME = "ME-Identity-Check-Request";
	public static final int code = 324;

	public Avp getTerminalInformationAvp();

	public boolean hasIMEI();
	public String getIMEI();

	public boolean hasTgpp2MEID();
	public byte[] getTgpp2MEID();

	public boolean hasSoftwareVersion();
	public String getSoftwareVersion();

	public boolean isUserNameAVPPresent();

	public String getUserName();
}
