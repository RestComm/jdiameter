package org.jdiameter.common.impl.data;

import org.jdiameter.api.BaseSession;
import org.jdiameter.client.api.IContainer;
import org.jdiameter.client.api.controller.IPeer;
import org.jdiameter.common.api.data.IRoutingAwareSessionDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation of routing aware session datasource for {@link IRoutingAwareSessionDatasource}.
 */
public class RoutingAwareDataSource extends LocalDataSource implements IRoutingAwareSessionDatasource {

  private static final Logger logger = LoggerFactory.getLogger(RoutingAwareDataSource.class);


  /**
   * Default constructor.
   */
  public RoutingAwareDataSource() {
    super();
    logger.debug("Constructor for RoutingAwareDataSource: nothing to do");
  }

  /**
   * Parameterized constructor. Should be called by any subclasses.
   *
   * @param container container object
   */
  public RoutingAwareDataSource(IContainer container) {
    super(container);
    logger.debug("Constructor for RoutingAwareDataSource: nothing to do");
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.impl.data.LocalDataSource#addSession(org.jdiameter.api.BaseSession)
   */
  @Override
  public void addSession(BaseSession session) {
    addSession(session, RoutingAwareSessionEntry.class);
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.data.IRoutingAwareSessionDatasource#setSessionPeer(java.lang.String, org.jdiameter.client.api.controller.IPeer)
   */
  @Override
  public void setSessionPeer(String sessionId, IPeer peer) {
    logger.debug("Assigning routing destination peer [{}] to session [{}]", peer, sessionId);
    SessionEntry se = sessionIdToEntry.get(sessionId);
    if (se == null) {
      throw new IllegalArgumentException("No session entry for id: " + sessionId);
    }
    else if (!(se instanceof RoutingAwareSessionEntry)) {
      throw new IllegalArgumentException("Session entry is of a wrong type for id: " + sessionId);
    }
    else {
      ((RoutingAwareSessionEntry) se).peer = peer.getUri().getFQDN();
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.data.IRoutingAwareSessionDatasource#getSessionPeer(java.lang.String)
   */
  @Override
  public String getSessionPeer(String sessionId) {
    SessionEntry se = sessionIdToEntry.get(sessionId);
    logger.debug("Looking up routing peer for session [{}]: {}", sessionId, se);
    return (se != null && se instanceof RoutingAwareSessionEntry) ? ((RoutingAwareSessionEntry) se).peer : null;
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.common.api.data.IRoutingAwareSessionDatasource#removeSessionPeer(java.lang.String)
   */
  @Override
  public String removeSessionPeer(String sessionId) {
    SessionEntry se = sessionIdToEntry.get(sessionId);
    logger.debug("Looking up routing peer for removal for session [{}]: {}", sessionId, se);
    if (se != null && se instanceof RoutingAwareSessionEntry) {
      String oldPeer = ((RoutingAwareSessionEntry) se).peer;
      ((RoutingAwareSessionEntry) se).peer = null;
      return oldPeer;
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * @see org.jdiameter.api.SessionPersistenceStorage#dumpStickySessions(int)
   */
  @Override
  public List<String> dumpStickySessions(int maxLimit) {
    int counter = 0;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    List<String> sessions = maxLimit > 0 ? new ArrayList<String>(maxLimit) : new ArrayList<String>(sessionIdToEntry.size());

    logger.debug("Reading [{}] sessions out of [{}]", maxLimit > 0 ? String.valueOf(maxLimit) : "unlimited", sessionIdToEntry.size());

    for (Map.Entry<String, SessionEntry> entry : sessionIdToEntry.entrySet()) {
      if (entry.getValue() instanceof RoutingAwareSessionEntry) {
        RoutingAwareSessionEntry tmpEntry = (RoutingAwareSessionEntry) entry.getValue();
        if (tmpEntry.peer != null) {
          sessions.add(tmpEntry.preetyPrint(entry.getKey(), dateFormat));
          if (maxLimit > 0 && ++counter >= maxLimit) {
            break;
          }
        }
      }
    }

    return sessions;
  }

  /**
   * Extends basic session entry, which is used to store records in session storage, with extra info about
   * a specific peer that is bound to a particular session. Extra info is used for session persistent routing.
   */
  protected static class RoutingAwareSessionEntry extends SessionEntry {
    String peer;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("RoutingAwareSessionEntry [peer=").append(peer).append(", toString()=").append(super.toString()).append("]");
      return builder.toString();
    }

    /**
     * Gets a readable and more user friendly format of an entry.
     *
     * @param key        key used to store that entry in a session storage map
     * @param dateFormat format used to print last session activity timestamp
     * @return readable representation of this session entry
     */
    public String preetyPrint(String key, DateFormat dateFormat) {
      StringBuilder builder = new StringBuilder("{id=[");
      builder.append(key)
              .append("], peer=[")
              .append(peer).append("], timestamp=[")
              .append(dateFormat.format(new Date(session.getLastAccessedTime())))
              .append("]}").toString();
      return builder.toString();
    }
  }
}
