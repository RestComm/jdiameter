package net.java.slee.resource.diameter.cxdx;

import java.io.IOException;

import net.java.slee.resource.diameter.cxdx.events.LocationInfoRequest;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationRequest;
import net.java.slee.resource.diameter.cxdx.events.PushProfileAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentRequest;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationRequest;

/**
 * 
 * Represents a CxDxClientSession session for Cx/Dx clients.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface CxDxClientSession extends CxDxSession {

  /**
   * Create a User-Authorization-Request message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new UserAuthorizationRequest
   */
  UserAuthorizationRequest createUserAuthorizationRequest();

  /**
   * Send an event User-Authorization-Request. An event containing the answer will be fired on this activity.
   * 
   * @param userAuthorizationRequest the User-Authorization-Request message to send
   * @throws IOException
   */
  void sendUserAuthorizationRequest(UserAuthorizationRequest userAuthorizationRequest) throws IOException;

  /**
   * Create a ServerAssignmentRequest message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new ServerAssignmentRequest
   */
  ServerAssignmentRequest createServerAssignmentRequest();

  /**
   * Send an event Registration-Termination-Request. An event containing the answer will be fired on this activity.
   * 
   * @param serverAssignmentRequest the Registration-Termination-Request message to send
   * @throws IOException
   */
  void sendServerAssignmentRequest(ServerAssignmentRequest serverAssignmentRequest) throws IOException;

  /**
   * Create a LocationInfoRequest message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new LocationInfoRequest
   */
  LocationInfoRequest createLocationInfoRequest();

  /**
   * Send an event Location-Info-Request. An event containing the answer will be fired on this activity.
   * 
   * @param locationInfoRequest the Location-Info-Request message to send
   * @throws IOException
   */
  void sendLocationInfoRequest(LocationInfoRequest locationInfoRequest) throws IOException;

  /**
   * Create a MultimediaAuthenticationRequest message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new MultimediaAuthenticationRequest
   */
  MultimediaAuthenticationRequest createMultimediaAuthenticationRequest();

  /**
   * Send an event Multimedia-Authentication-Request. An event containing the answer will be fired on this activity.
   * 
   * @param multimediaAuthenticationRequest the Multimedia-Authentication-Request message to send
   * @throws IOException
   */
  void sendMultimediaAuthenticationRequest(MultimediaAuthenticationRequest multimediaAuthenticationRequest) throws IOException;

  /**
   * Create a RegistrationTerminationRequest message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new RegistrationTerminationRequest
   */
  RegistrationTerminationRequest createRegistrationTerminationRequest();

  /**
   * Send an event Registration-Termination-Request. An event containing the answer will be fired on this activity.
   * 
   * @param registrationTerminationRequest the Registration-Termination-Request message to send
   * @throws IOException
   */
  void sendRegistrationTerminationRequest(RegistrationTerminationRequest registrationTerminationRequest) throws IOException;

  /**
   * Create a Push-Profile-Answer populated with the AVPs appropriate for this session.
   * 
   * @return a new PushProfileAnswer
   */
  PushProfileAnswer createPushProfileAnswer();

  /**
   * Send an event Push-Profile-Answer in response to a Push-Profile-Request received on this activity.
   * 
   * @param PushProfileAnswer the Push-Profile-Answer message to send
   * @throws IOException
   */
  void sendPushProfileAnswer(PushProfileAnswer pushProfileAnswer) throws IOException;

}
