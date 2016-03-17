/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jdiameter.client.api.parser;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.client.api.IMessage;

import java.nio.ByteBuffer;

/**
 * Basic interface for diameter message parsers.
 * 
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface IMessageParser {

    /**
     * Create message from bytebuffer
     * @param data message bytebuffer
     * @return instance of message
     * @throws AvpDataException
     */
    IMessage createMessage(ByteBuffer data) throws AvpDataException;

    /**
     * Created specified type of message
     * @param iface type of message
     * @param data message bytebuffer
     * @return instance of message
     * @throws AvpDataException
     */
    <T> T createMessage(java.lang.Class<?> iface, ByteBuffer data) throws AvpDataException;

    /**
     * Created empty message
     * @param commandCode message command code
     * @param headerAppId header applicatio id
     * @return instance of message
     */
    IMessage createEmptyMessage(int commandCode, long headerAppId);

    /**
     * Created specified type of message
     * @param iface type of message
     * @param commandCode message command code
     * @param headerAppId header applicatio id
     * @return instance of message
     */
    <T> T createEmptyMessage(Class<?> iface, int commandCode, long headerAppId);

    /**
     * Created new message with copied of header of parent message
     * @param parentMessage parent message
     * @return instance of message
     */
    IMessage createEmptyMessage(IMessage parentMessage);

    /**
     * Created new message with copied of header of parent message
     * @param parentMessage parent message
     * @param commandCode new command code value
     * @return instance of message
     */
    IMessage createEmptyMessage(IMessage parentMessage, int commandCode);

    /**
     * Created new message with copied of header of parent message
     * @param iface type of message
     * @param parentMessage parent message
     * @return  instance of message
     */
    <T> T createEmptyMessage(Class<?> iface, IMessage parentMessage);

    /**
     * Encode message to ByteBuffer
     * @param message diameter message
     * @return instance of message
     * @throws ParseException
     */
    ByteBuffer encodeMessage(IMessage message) throws ParseException;

}
