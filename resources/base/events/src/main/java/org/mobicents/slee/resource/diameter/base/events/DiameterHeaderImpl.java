package org.mobicents.slee.resource.diameter.base.events;

import net.java.slee.resource.diameter.base.events.DiameterHeader;

/**
 * 
 * DiameterHeaderImpl.java
 *
 * <br>Super project:  mobicents
 * <br>3:05:20 PM Jun 20, 2008 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a> 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a> 
 * @author Erick Svenson
 */
public class DiameterHeaderImpl implements DiameterHeader {

  private long applicationId;
  private long hopByHopId;
  private long endToEndId;
  private int messageLength;
  private int commandCode;
  private boolean request;
  private boolean proxiable;
  private boolean error;
  private boolean potentiallyRetransmitted;

  public DiameterHeaderImpl(long applicationId, long hopByHopId, long endToEndId, int messageLength, int commandCode, boolean request, boolean proxiable, boolean error, boolean potentiallyRetransmitted) {
    this.applicationId = applicationId;
    this.hopByHopId = hopByHopId;
    this.endToEndId = endToEndId;
    this.messageLength = messageLength;
    this.commandCode = commandCode;
    this.request = request;
    this.proxiable = proxiable;
    this.error = error;
    this.potentiallyRetransmitted = potentiallyRetransmitted;
  }

  public long getApplicationId() {
    return applicationId;
  }

  public long getHopByHopId() {
    return hopByHopId;
  }

  public long getEndToEndId() {
    return endToEndId;
  }

  public int getMessageLength() {
    return messageLength;
  }

  public int getCommandCode() {
    return commandCode;
  }

  public boolean isRequest() {
    return request;
  }

  public boolean isProxiable() {
    return proxiable;
  }

  public boolean isError() {
    return error;
  }

  public boolean isPotentiallyRetransmitted() {
    return potentiallyRetransmitted;
  }

  public short getVersion() {
    return 1;
  }

  public Object clone() {
    return new DiameterHeaderImpl(applicationId, hopByHopId, endToEndId,
        messageLength, commandCode, request, proxiable, error, 
        potentiallyRetransmitted);
  }

}
