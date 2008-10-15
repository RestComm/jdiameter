package org.jdiameter.server.impl;

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.ResultCode;
import org.jdiameter.client.api.IMessage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides check incoming/outgoing diameter messages.
 * Check's rules consist into xml file.
 */
public class MessageValidator {

    public static final Result SUCCESS = new Result(null, ResultCode.SUCCESS);

    private ConcurrentHashMap<Integer, MessageValidator> mv = new ConcurrentHashMap<Integer, MessageValidator>();

    private boolean enable = true;

    public MessageValidator() {
        // todo load validator rules
        
    }

    /**
     * Enable validation functions
     */
    public void enable() {
        enable = true;
    }

    /**
     * Disable validation functions
     */
    public void disable() {
        enable = false;
    }

    /**
     * Return true if validation function is on
     * @return true if validation function is on
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Validate message
     * @param message message instance
     * @return result of validation procedure
     */
    public Result check(IMessage message) {
        if (message == null)
            throw new IllegalArgumentException("Message is null");
        if (!enable)
            return SUCCESS;
        // todo
        return null;
    }

    public static class Result {

        private IMessage errorMessage;
        private long code = ResultCode.SUCCESS;

        Result(IMessage errorMessage, long code) {
            this.errorMessage = errorMessage;
            this.code = code;
        }

        /**
         * Return true if message is correct
         * @return true if message is correct
         */
        public boolean isOK() {
            return code == ResultCode.SUCCESS || code == ResultCode.LIMITED_SUCCESS;
        }

        /**
         * Return long value of result code
         * @return long value of result code
         */
        public long toLong() {
            return code;
        }

        /**
         * Create error answer message with Result-Code Avp
         * @return error answer message
         */
        public IMessage toMessage() {
            if ( errorMessage != null && errorMessage.getAvps().getAvp(Avp.RESULT_CODE) == null )
                errorMessage.getAvps().addAvp(Avp.RESULT_CODE, code);
            return errorMessage;
        }

        /**
         * Create error answer message with Experemental-Result-Code Avp
         * @param vendorId vendor id
         * @return error answer message with Experemental-Result-Code Avp
         */
        public IMessage toMessage(int vendorId) {
            if ( errorMessage != null && errorMessage.getAvps().getAvp(297) == null ) { // EXPERIMENTAL_RESULT = 297
                AvpSet er = errorMessage.getAvps().addGroupedAvp(297);
                er.addAvp(Avp.VENDOR_ID, vendorId);
                er.addAvp(Avp.EXPERIMENTAL_RESULT_CODE, code);
            }
            return errorMessage;
        }
    }
}
