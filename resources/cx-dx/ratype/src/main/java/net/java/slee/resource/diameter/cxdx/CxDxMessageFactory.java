package net.java.slee.resource.diameter.cxdx;

import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

/**
 *
 * Factory to support the creation of Diameter Cx/Dx messages.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface CxDxMessageFactory {

  /**
   * The Cx interface protocol is defined as an IETF vendor specific Diameter application, where
   * the vendor is 3GPP. The vendor identifier assigned by IANA to 3GPP 
   * (http://www.iana.org/assignments/enterprise-numbers) is 10415.
   */
  public static final long _CXDX_VENDOR = 10415L;

  /**
   * The Diameter application identifier assigned to the Cx/Dx interface application is 16777216 
   * (allocated by IANA).
   */
  public static final long _CXFX_AUTH_APP_ID = 16777216L;

  /**
   * Create a UserAuthorizationRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new UserAuthorizationRequest
   */
  UserAuthorizationRequest createUserAuthorizationRequest();

  /**
   * Create a UserAuthorizationRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new UserAuthorizationRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  UserAuthorizationRequest createUserAuthorizationRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Create a ServerAssignmentRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new ServerAssignmentRequest
   */
  ServerAssignmentRequest createServerAssignmentRequest();

  /**
   * Create a ServerAssignmentRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new ServerAssignmentRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  ServerAssignmentRequest createServerAssignmentRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Create a LocationInfoRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new LocationInfoRequest
   */
  LocationInfoRequest createLocationInfoRequest();

  /**
   * Create a LocationInfoRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new LocationInfoRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  LocationInfoRequest createLocationInfoRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Create a MultimediaAuthenticationRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new MultimediaAuthenticationRequest
   */
  MultimediaAuthenticationRequest createMultimediaAuthenticationRequest();

  /**
   * Create a MultimediaAuthenticationRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new MultimediaAuthenticationRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  MultimediaAuthenticationRequest createMultimediaAuthenticationRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Create a RegistrationTerminationRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new RegistrationTerminationRequest
   */
  RegistrationTerminationRequest createRegistrationTerminationRequest();

  /**
   * Create a RegistrationTerminationRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new RegistrationTerminationRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  RegistrationTerminationRequest createRegistrationTerminationRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Create a PushProfileRequest instance, populating it with the internal
   * AVPs not known or needed by the application.
   * 
   * @return a new PushProfileRequest
   */
  PushProfileRequest createPushProfileRequest();

  /**
   * Create a PushProfileRequest instance, populating it with the internal AVPs not known or
   * needed by the application. Use the session ID provided to find the Diameter session. This
   * should be used when the requests are being made synchronously and there is no 
   * CxDxClientSession available. 
   * 
   * @param sessionId the Session-Id
   * @return a new PushProfileRequest
   * @throws IllegalArgumentException if sessionId is not a valid SessionID
   */
  PushProfileRequest createPushProfileRequest(String sessionId) throws IllegalArgumentException;

  /**
   * Get the Diameter Base protocol message factory.
   * 
   * @return
   */
  DiameterMessageFactory getBaseMessageFactory();

}
