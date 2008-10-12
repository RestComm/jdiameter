/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: Lesser General Public License (LGPL)
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.api;

/**
 * An exception that provides information on a stack has application request overload.
 * @version 1.5.1 Final
 */

public class OverloadException extends Exception {

    double lowThreshold, highThreshold, value;

    /**
     * Default constructor
     */
    public OverloadException() {
        super();
    }

    /**
     * Constructor with overload data
     * @param lowThreshold low threshold overload value
     * @param highThreshold  high threshold overload value
     * @param value current overload value 
     */
    public OverloadException(double lowThreshold, double highThreshold, double value) {
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.value = value;
    }

    /**
     * Constructor with reason string
     * @param message reason string
     */
    public OverloadException(String message) {
        super(message);
    }

    /**
     * Constructor with reason string and parent exception
     * @param message message reason string
     * @param cause parent exception
     */
    public OverloadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with parent exception
     * @param cause  parent exception
     */
    public OverloadException(Throwable cause) {
        super(cause);
    }

    /**
     * @return current low threshold overload value
     */
    public double getLowThreshold() {
        return lowThreshold;
    }

    /**
     * @return current high threshold overload value
     */
    public double getHighThreshold() {
        return highThreshold;
    }

    /**
     * @return current overload value
     */
    public double getValue() {
        return value;
    }
}
