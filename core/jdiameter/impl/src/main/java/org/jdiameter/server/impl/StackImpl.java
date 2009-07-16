package org.jdiameter.server.impl;

import org.jdiameter.api.*;
import org.jdiameter.client.api.StackState;

public class StackImpl extends org.jdiameter.client.impl.StackImpl implements StackImplMBean{

    public MetaData getMetaData() {
        if (state == StackState.IDLE)
            throw new IllegalAccessError("Meta data not defined");
        return (MetaData) assembler.getComponentInstance(MetaDataImpl.class);
    }

    public boolean isWrapperFor(Class<?> aClass) throws InternalException {
        if (aClass == MutablePeerTable.class)
            return true;
        else if (aClass == Network.class)
            return true;
        else if (aClass == OverloadManager.class)
            return true;
        else
            return super.isWrapperFor(aClass);
    }

    public <T> T unwrap(Class<T> aClass) throws InternalException {
        if (aClass == MutablePeerTable.class)
            return (T) assembler.getComponentInstance(aClass);
        if (aClass == Network.class)
            return (T) assembler.getComponentInstance(aClass);
        if (aClass == OverloadManager.class)
            return (T) assembler.getComponentInstance(aClass);
        else
            return (T) super.unwrap(aClass);
    }
}
