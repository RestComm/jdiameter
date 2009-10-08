/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.client.impl.helpers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provide uid generator functionality
 */
public class UIDGenerator {

    private static long value;
    private static final Lock mutex = new ReentrantLock();
    private static final ThreadLocal<Delta> ranges = new ThreadLocal<Delta>() {
        protected synchronized Delta initialValue() {
            return new Delta();
        }
    };

    private static class Delta {
        long start;
        long stop;

        public long update(long value) {
            start = value;
            stop  = value + 1;
            return stop;
        }
    }

    /**
     * Create instance of class
     */
    public UIDGenerator() {
        value = System.currentTimeMillis();
    }

    /**
     * Create instance of class with predefined start value
     * 
     * @param startValue start value of counter
     */
    public UIDGenerator(long startValue) {
        value = startValue;
    }

    /**
     * Return next uid as int
     * 
     * @return  uid
     */
    public int nextInt() {
        return (int) (0x7FFFFFFF & nextLong());
    }

    /**
     * Return next uid as long
     * 
     * @return uid as long
     */
    public long nextLong() {
        Delta d = ranges.get();
        if (d.start <= d.stop) {
            mutex.lock();
            value = d.update(value);
            mutex.unlock();
        }
        return d.start++;
    }
}
