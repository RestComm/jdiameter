package net.java.slee.resource.diameter.cxdx;

import java.io.IOException;

import net.java.slee.resource.diameter.cxdx.events.LocationInfoAnswer;
import net.java.slee.resource.diameter.cxdx.events.MultimediaAuthenticationAnswer;
import net.java.slee.resource.diameter.cxdx.events.PushProfileRequest;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationAnswer;
import net.java.slee.resource.diameter.cxdx.events.RegistrationTerminationRequest;
import net.java.slee.resource.diameter.cxdx.events.ServerAssignmentAnswer;
import net.java.slee.resource.diameter.cxdx.events.UserAuthorizationAnswer;

/**
 *
 * Represents a CxDxClientSession session for Cx/Dx servers.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface CxDxServerSession extends CxDxSession {

  /**
   * Create a User-Authorization-Answer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new UserAuthorizationAnswer
   */
  UserAuthorizationAnswer createUserAuthorizationAnswer();

  /**
   * Send an event User-Authorization-Answer in response to a User-Authorization-Request received on this activity.
   * 
   * @param userAuthorizationAnswer the User-Authorization-Answer message to send
   * @throws IOException
   */
  void sendUserAuthorizationAnswer(UserAuthorizationAnswer userAuthorizationAnswer) throws IOException;

  /**
   * Create a ServerAssignmentAnswer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new ServerAssignmentAnswer
   */
  ServerAssignmentAnswer createServerAssignmentAnswer();

  /**
   * Send an event Registration-Termination-Answer in response to a Registration-Termination-Request received on this activity.
   * 
   * @param serverAssignmentAnswer the Registration-Termination-Answer message to send
   * @throws IOException
   */
  void sendServerAssignmentAnswer(ServerAssignmentAnswer serverAssignmentAnswer) throws IOException;

  /**
   * Create a LocationInfoAnswer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new LocationInfoAnswer
   */
  LocationInfoAnswer createLocationInfoAnswer();

  /**
   * Send an event Location-Info-Answer in response to a Location-Info-Request received on this activity.
   * 
   * @param locationInfoAnswer the Location-Info-Answer message to send
   * @throws IOException
   */
  void sendLocationInfoAnswer(LocationInfoAnswer locationInfoAnswer) throws IOException;

  /**
   * Create a MultimediaAuthenticationAnswer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new MultimediaAuthenticationAnswer
   */
  MultimediaAuthenticationAnswer createMultimediaAuthenticationAnswer();

  /**
   * Send an event Multimedia-Authentication-Answer in response to a Multimedia-Authentication-Request received on this activity.
   * 
   * @param multimediaAuthenticationAnswer the Multimedia-Authentication-Answer message to send
   * @throws IOException
   */
  void sendMultimediaAuthenticationAnswer(MultimediaAuthenticationAnswer multimediaAuthenticationAnswer) throws IOException;

  /**
   * Create a RegistrationTerminationAnswer message pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new RegistrationTerminationAnswer
   */
  RegistrationTerminationRequest createRegistrationTerminationRequest();

  /**
   * Send an event Registration-Termination-Answer in response to a Registration-Termination-Request received on this activity.
   * 
   * @param registrationTerminationAnswer the Registration-Termination-Answer message to send
   * @throws IOException
   */
  void sendRegistrationTerminationRequest(RegistrationTerminationRequest registrationTerminationRequest) throws IOException;

  /**
   * Create a Push-Profile-Request pre-populated with the AVPs appropriate for this session.
   * 
   * @return a new PushProfileRequest
   */
  PushProfileRequest createPushProfileRequest();

  /**
   * Send an event Push-Profile-Request. An event containing the answer will be fired on this activity.
   * 
   * @param PushProfileRequest the Push-Profile-Request message to send
   * @throws IOException
   */
  void sendPushProfileRequest(PushProfileRequest pushProfileRequest) throws IOException;

}
