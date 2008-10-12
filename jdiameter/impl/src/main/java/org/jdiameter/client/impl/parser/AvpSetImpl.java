package org.jdiameter.client.impl.parser;

/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */

import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.parser.DecodeException;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class AvpSetImpl implements AvpSet {

    MessageParser parser;
    CopyOnWriteArrayList<Avp> avps = new CopyOnWriteArrayList<Avp>();

    AvpSetImpl(MessageParser parser) {
        this.parser = parser;
    }

    public Avp getAvp(int avpCode) {
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                return avp;
            }
        }
        return null;
    }

    public Avp getAvpByIndex(int avpCode) {
        return this.avps.get(avpCode);
    }

    public Avp getAvp(int avpCode, long vendorId) {
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                if (avp.getVendorId() == vendorId)
                    return avp;
            }
        }
        return null;
    }

    public AvpSet getAvps(int avpCode) {
        AvpSet result = new AvpSetImpl(parser);
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                result.addAvp(avp);
            }
        }
        return result;
    }



    public AvpSet getAvps(int avpCode, long vendorId) {
        AvpSet result = new AvpSetImpl(parser);
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                if (avp.getVendorId() == vendorId)
                    result.addAvp(avp);
            }
        }
        return result;
    }

    public AvpSet removeAvp(int avpCode) {
        AvpSet result = new AvpSetImpl(parser);
        for (Avp avp : this.avps) {
            if (avp.getCode() == avpCode) {
                result.addAvp(avp);
                this.avps.remove(avp);
            }
        }
        return result;
    }

    public Avp removeAvpByIndex(int i) {
        if (i >= this.avps.size()) return null;
        return this.avps.remove(i);
    }

    public Avp[] asArray() {
        return this.avps.toArray(new Avp[avps.size()]);
    }

    public Avp addAvp(int avpCode, long value, boolean asUsigned) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, asUsigned ? parser.intU32ToBytes(value):parser.int64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUsigned) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags , 0, asUsigned ? parser.intU32ToBytes(value):parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, long vndId, boolean mFlag, boolean pFlag, boolean asUsigned) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, asUsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public void insertAvp(int index, Avp... avps) {
        this.avps.addAll(index, Arrays.asList(avps));
    }

    public void insertAvp(int index, AvpSet avpSet) {
        this.avps.addAll(index, Arrays.asList(avpSet.asArray()));
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean asUsigned) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, asUsigned ? parser.intU32ToBytes(value):parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag, boolean asUsigned) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags , 0, asUsigned ? parser.intU32ToBytes(value):parser.int64ToBytes(value) );
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, long vndId, boolean mFlag, boolean pFlag, boolean asUsigned) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, asUsigned ? parser.intU32ToBytes(value) : parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode) {
        AvpImpl res = new AvpImpl(parser, avpCode, 0, 0, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(index, res);
        return res.groupedData;
    }

    public int size() {
        return this.avps.size();
    }

    public void addAvp(AvpSet avpSet) {
        for (Avp a:avpSet) avps.add(a);
    }

    public void addAvp(Avp... avps) {
        for (Avp a : avps) {
            Avp res = new AvpImpl(parser, a);
            this.avps.add(res);
        }
    }

    public Avp addAvp(int avpCode, byte[] rawData) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, byte[] rawData, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags , 0, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, byte[] rawData, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, rawData);
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, int value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.int32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, long value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.int64ToBytes(value) );
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, float value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
         Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.float32ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, double value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;

    }

    public Avp addAvp(int avpCode, double value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, double value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.float64ToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, String value, boolean asOctetString) {
        try {
            Avp res = new AvpImpl(
                parser, avpCode, 0, 0, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, String value, boolean mFlag, boolean pFlag,  boolean asOctetString) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(
                parser, avpCode, flags, 0, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, String value, long vndId, boolean mFlag, boolean pFlag,  boolean asOctetString) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(
                parser, avpCode, flags, vndId, asOctetString ? parser.octetStringToBytes(value) : parser.utf8StringToBytes(value)
            );
            this.avps.add(res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value) {
        try {
            Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (DecodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (DecodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, URI value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.octetStringToBytes(value.toString()));
            this.avps.add(res);
            return res;
        } catch (DecodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp addAvp(int avpCode, InetAddress value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, InetAddress value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, InetAddress value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.addressToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, Date value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.dateToBytes(value));
        this.avps.add(res);
        return res;

    }

    public Avp addAvp(int avpCode, Date value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.dateToBytes(value));
        this.avps.add(res);
        return res;
    }

    public Avp addAvp(int avpCode, Date value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.dateToBytes(value));
        this.avps.add(res);
        return res;
    }

    public AvpSet addGroupedAvp(int avpCode) {
        AvpImpl res = new AvpImpl(parser, avpCode, 0, 0, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(res);
        return res.groupedData;
    }

    public AvpSet addGroupedAvp(int avpCode, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(parser, avpCode, flags, 0, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(res);
        return res.groupedData;
    }

    public AvpSet addGroupedAvp(int avpCode, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(parser, avpCode, flags, vndId, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(res);
        return res.groupedData;
    }

    public Avp insertAvp(int index, int avpCode, byte[] value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, value);
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, byte[] value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, value);
        this.avps.add(index, res);
        return res;

    }

    public Avp insertAvp(int index, int avpCode, byte[] value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, value);
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, int value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.int32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, long value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.int64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, float value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.float32ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, double value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.float64ToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, String value, boolean asOctetString) {
        try {
            Avp res = new AvpImpl(parser, avpCode, 0, 0, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, String value, boolean mFlag, boolean pFlag, boolean asOctetString) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, 0, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    public Avp insertAvp(int index, int avpCode, String value, long vndId, boolean mFlag, boolean pFlag, boolean asOctetString) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, vndId, asOctetString ? parser.octetStringToBytes(value) :
            parser.utf8StringToBytes(value));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value) {
        try {
            Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, URI value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        try {
            Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.octetStringToBytes(value.toString()));
            this.avps.add(index, res);
            return res;
        } catch(Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, InetAddress value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.addressToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value) {
        Avp res = new AvpImpl(parser, avpCode, 0, 0, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, 0, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public Avp insertAvp(int index, int avpCode, Date value, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        Avp res = new AvpImpl(parser, avpCode, flags, vndId, parser.dateToBytes(value));
        this.avps.add(index, res);
        return res;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode, boolean mFlag, boolean pFlag) {
        int flags = ((mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(parser, avpCode, flags, 0, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(index, res);
        return res.groupedData;
    }

    public AvpSet insertGroupedAvp(int index, int avpCode, long vndId, boolean mFlag, boolean pFlag) {
        int flags = ((vndId !=0 ? 0x80:0) | (mFlag ? 0x40:0) | (pFlag ? 0x20:0));
        AvpImpl res = new AvpImpl(parser, avpCode, flags, vndId, new byte[0] );
        res.groupedData = new AvpSetImpl(parser);
        this.avps.add(index, res);
        return res.groupedData;
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        return false;
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        return null;
    }

    public Iterator<Avp> iterator() {
        return Collections.unmodifiableList(this.avps).iterator();
    }

}
