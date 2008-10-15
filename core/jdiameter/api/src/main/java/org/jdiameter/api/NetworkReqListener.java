package org.jdiameter.api;

/**
 * The NetworkReqListener interface is used to process
 * network requests. This listener can be attach to session or
 * to network class instances
 *
 * @version 1.5.1 Final
 */
public interface NetworkReqListener {
    /**
     * This method use for process new network requests.
     * @param request request message
     * @return answer immediate answer messsage. Method may return null and an
     * Answer will be sent later on
     */
    Answer processRequest(Request request);
}
